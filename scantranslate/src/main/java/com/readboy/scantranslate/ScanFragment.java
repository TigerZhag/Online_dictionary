package com.readboy.scantranslate;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.library.quicksearch.QuickSearchWord;
import com.readboy.scantranslate.Ocr.OcrWorker;
import com.readboy.scantranslate.Utils.FileUtil;
import com.readboy.scantranslate.Utils.HardwareUtil;
import com.readboy.scantranslate.Utils.ImageUtil;
import com.readboy.scantranslate.Utils.StringUtil;
import com.readboy.scantranslate.widght.ScannerOverlayView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Zhang shixin
 * @date 16-7-8.
 *
 *   -----------                ----------
 *  |Main Thread|              |I/O Thread|
 *   -----------                ----------
 *       |                          |
 *   init camera                 init Ocr
 *       |                          |
 *       |                          |
 *  start preview                   |
 *       |                          |
 *  ->take picture <----------------
 * |     |                          |
 * |     |                          |
 * |      -------------------->    Ocr
 * |  preview                       |
 * |     |                          |
 * |     |                          |
 * |_____|                          |
 *
 *
 */
public class ScanFragment extends Fragment implements TextureView.SurfaceTextureListener {
    private static final String TAG = "ScanFragment";

    /**
     *
     */
    private View.OnClickListener okListener;
    private View.OnClickListener exitListener;

    private ScannerOverlayView scannerView;
    private FrameLayout previewFrame;
    private TextureView preview;
//    private TextView more;
    private ImageButton back;
    private TextView back_text;
    private CheckBox flash;
    private Button start;

    private Camera camera;

    private String wordAph;
    private String result;
    private OcrWorker ocrWorker = OcrWorker.getInstance();

    private OcrHandler handler;

    private static final int CONTINUE_OCR_DELAY = 50;

    private static final int OCR_TIME_LIMIT =500;

    /**
     * 弹框显示翻译
     */
    private TextView above_toast;
    private LinearLayout dialog;
    private TextView query;
    private TextView trans;
    private TextView more;

    private TextView below_toast;
    private String ocrText;
    /**
     *
     * @param listener
     */

    public void setOkListener(View.OnClickListener listener){
        this.okListener = listener;
        if (more != null){
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.setVisibility(View.GONE);
                    okListener.onClick(v);
                }
            });
        }
    }

    public void setExitListener(View.OnClickListener exitListener){
        this.exitListener = exitListener;
        if (back != null){
            back.setOnClickListener(exitListener);
            back_text.setOnClickListener(exitListener);
        }
    }

//    public String getScanResult(){
//        return result;
//    }

    private long start_time;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_scanner,null,false);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        handler = new OcrHandler(this);
        Log.e(TAG, "onCreateView: ");
        initView(view);

        //检查硬件
        openCamera();
        if (camera == null){
            Toast.makeText(getActivity(), "打开相机失败,请先关闭其他相机或手电筒应用", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return null;
        }
        initOcr();
        initTranslate();

        return view;
    }

    private QuickSearchWord quickSearchWord;

    @Override
    public void onResume() {
        super.onResume();

        if (camera == null){
            openCamera();
        }
        if (camera == null){
            Toast.makeText(getActivity(), "打开相机失败,请先关闭其他相机或手电筒应用", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        preview = new TextureView(getActivity());
        previewFrame.removeAllViews();
        previewFrame.addView(preview);
        preview.setSurfaceTextureListener(this);
        start.setBackgroundResource(R.drawable.press_scan);
        start_time = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopOcr();

        HardwareUtil.turnLightOff(camera);
        releaseCamera();
        if (mWordInfo == null) above_toast.setVisibility(View.GONE);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ocrWorker.release();
        if (quickSearchWord != null) quickSearchWord.destroy();
    }

    private void releaseCamera() {
        if (camera != null) {
            flash.setChecked(false);
            camera.cancelAutoFocus();
            camera.stopPreview();
            camera.release();
            camera = null;
            if (preview.getSurfaceTexture() != null) {
                preview.setSurfaceTextureListener(null);
            }
        }
    }

    private void initOcr() {
        if (!isExist() && !FileUtil.tessDataIsExists(ocrWorker.local_filePath)){
            deepFile("tessdata");
            start.setVisibility(View.GONE);
//            scannerView.setToast("正在初始化字库,请稍候...");
            below_toast.setText("正在初始化字库,请稍候...");
        }else {
            ocrWorker.initOcr(new Action1<String>() {
                @Override
                public void call(String s) {
                    start.setVisibility(View.VISIBLE);
//                    scannerView.setToast("请将框体正对要查询的单词");
//                    scannerView.hideToast();
//                    below_toast.setVisibility(View.VISIBLE);
                    below_toast.setText("请将框体正对要查询的单词");
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Toast.makeText(getActivity(), "OCR初始化失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
        makeOcr();
    }

    public void deepFile(final String path) {
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(String s) {
                initOcr();
            }
        };
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    copyFile(path);
                    subscriber.onNext("init success");
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private void copyFile(String path) throws IOException {
        File tessDir = new File(OcrWorker.local_filePath + path);
        if (!tessDir.exists()) tessDir.mkdirs();
        //将Assets目录下tessdata拷贝至应用目录
        for (String fileName : getActivity().getAssets().list(path)) {
            File file = new File(OcrWorker.local_filePath + path + File.separator + fileName);
            if (!file.exists()) file.createNewFile();
            InputStream is = getActivity().getAssets().open(path + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            is.close();
            fos.flush();
            fos.close();
        }
    }

    private boolean isExist() {
        File file = new File(ocrWorker.sys_filePath + "tessdata" + File.separator + "osd.traineddata");
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private void initView(View view) {
        back = (ImageButton) view.findViewById(R.id.scanner_back);
        back_text = (TextView) view.findViewById(R.id.scanner_backText);
        flash = (CheckBox) view.findViewById(R.id.scanner_flash);
        scannerView = (ScannerOverlayView) view.findViewById(R.id.scanner);
        previewFrame = (FrameLayout) view.findViewById(R.id.scanner_previewframe);
//        more = (TextView) view.findViewById(R.id.scanner_more);
        start = (Button) view.findViewById(R.id.scan_start);

        if (exitListener != null){
            back.setOnClickListener(exitListener);
            back_text.setOnClickListener(exitListener);
        }

        if (!HardwareUtil.checkFlashLight(getActivity())){
            // has not flashlight
            flash.setVisibility(View.GONE);
        }else {
            flash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // flash on
                        HardwareUtil.turnLightOn(camera);
                    } else {
                        // flash off
                        HardwareUtil.turnLightOff(camera);
                    }
                }
            });
        }
        flash.setChecked(false);

        start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    handler.isPaused = false;
                    //press the button
                    start.setBackgroundResource(R.drawable.release_lock);

                    above_toast.setText(R.string.scanner_ing);
                    above_toast.setVisibility(View.VISIBLE);
                    scannerView.hideToast();

                    dialog.setVisibility(View.GONE);
                    mWordInfo = null;
                    wordAph = "";

//                    scannerView.setAboveText(getString(R.string.scanner_ing));
//                    more.setVisibility(View.GONE);
                    startOcr();
                }else if (event.getAction() == MotionEvent.ACTION_UP){
                    // release the button
                    start.setBackgroundResource(R.drawable.press_scan);
                    stopOcr();

                    if (wordAph.isEmpty()) {
                        above_toast.setVisibility(View.GONE);
                        scannerView.setToast("还没有识别出内容");
                        below_toast.setVisibility(View.GONE);
                        above_toast.setVisibility(View.GONE);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scannerView.hideToast();
                            }
                        },1000);
                        return true;
                    }
                    above_toast.setText(R.string.lock_word);
                }
                return false;
            }
        });

        above_toast = (TextView) view.findViewById(R.id.scan_above_toast);
        below_toast = (TextView) view.findViewById(R.id.scan_below_toast);

        dialog = (LinearLayout) view.findViewById(R.id.result_container);
        query = (TextView) dialog.findViewById(R.id.trans_query);
        query.setTypeface(Typeface.DEFAULT_BOLD);

        trans = (TextView) dialog.findViewById(R.id.trans_result);
        //显示音标需要的字体库
        query.setTypeface(Typeface.create("readboy-code",Typeface.NORMAL));
        trans.setTypeface(Typeface.create("readboy-code",Typeface.NORMAL));
        more = (TextView) dialog.findViewById(R.id.trans_more);

        if (okListener != null) {
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.setVisibility(View.GONE);
                    okListener.onClick(v);
                }
            });
        }

        //调整位置
    }

    private void stopOcr() {
        Message msg = handler.obtainMessage();
        msg.what = OcrHandler.PAUSE_OCR;
        handler.sendMessage(msg);
    }

    Subscriber<String> ocrSubcriber;
    Observable<String> ocrObservable;

    public void makeOcr(){
        ocrSubcriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                makeOcr();
                ocrWorker.release();
                ocrIsReady = true;
                handler.sendEmptyMessageDelayed(OcrHandler.CONTINUE_OCR,CONTINUE_OCR_DELAY);
            }

            @Override
            public void onNext(String s) {
                //处理连字字符 ﬁ,ﬂ 等
                String ocrResult = Normalizer.normalize(s, Normalizer.Form.NFKC).toLowerCase().replaceAll("[^(A-Za-z'\\-)]", "");
                if (ocrResult.isEmpty() || ocrResult.equals(ocrText)){
                    ocrIsReady = true;
                    handler.sendEmptyMessageDelayed(OcrHandler.CONTINUE_OCR,CONTINUE_OCR_DELAY);
                }else {
                    translate(ocrResult);
                }
                ocrText = ocrResult;
            }
        };

        ocrObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                long timeMillis = System.currentTimeMillis();
                if (bitmap == null) bitmap = preview.getBitmap();
                else preview.getBitmap(bitmap);
                Log.e(TAG, "getBitmap take times :" + (System.currentTimeMillis() - timeMillis));
//                Bitmap scanedBitmap = scannerView.getScanedImage(bitmap);
//                ImageUtil.saveBitmap(scanedBitmap, System.currentTimeMillis() + "ocr.jpg");
                String result = ocrWorker.doOcr(scannerView.getScanedImage(bitmap));
                if (result == null){
                    subscriber.onNext("");
                }else {
                    subscriber.onNext(result);
                }
            }
        }).distinctUntilChanged()
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.io());
        Log.e(TAG, "makeOcr: 2");
    }

    private Bitmap bitmap;

    int focus_failure_count = 0;
    private Camera.AutoFocusCallback focusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (handler.isPaused) return;
            if (!success && focus_failure_count <= 3){
                focus_failure_count ++;
                startOcr();
                return;
            }
            focus_failure_count = 0;
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    Message msg = handler.obtainMessage();
                    msg.what = OcrHandler.START_OCR;
                    handler.sendMessage(msg);
                }
            }).throttleFirst(2, TimeUnit.SECONDS)
              .subscribe();
        }
    };

    private void startOcr() {
        if (camera == null) handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startOcr();
            }
        },500);
        camera.cancelAutoFocus();
        camera.autoFocus(focusCallback);
    }

    public void lightOff(){
        if (camera != null){
            HardwareUtil.turnLightOff(camera);
        }
    }


    private PlugWorldInfo mWordInfo;

    /**
     * 獲取翻譯結果
     * @return
     */
    public PlugWorldInfo getWordInfo(){
        return mWordInfo;
    }
    private void initTranslate() {
        quickSearchWord = new QuickSearchWord(getContext());
        quickSearchWord.setMaxCount(150);
        quickSearchWord.setListener(new QuickSearchWord.OnQuickSearchListener() {
            @Override
            public void onResult(int status, String keyWord, String expain, PlugWorldInfo wordInfo) {
                ocrIsReady = true;
                if (handler.isPaused) return;
                Log.e(TAG, "translate take times :" + (System.currentTimeMillis() - translate_time));
                if (status == quickSearchWord.STATUS_SUCCESS) {
                    if (expain.length() >= 150) expain = expain + "...";
                    Log.e(TAG, "onResult: " + keyWord + " : " + expain);

                    if (expain.startsWith("\n")) expain = expain.substring(1);

                    /*************************************************************/
                    below_toast.setVisibility(View.GONE);
//                    Log.d(TAG, "onResult: origin keyword" + keyWord);
                    wordAph = StringUtil.filterAlphabet(keyWord);

//                    Log.d(TAG, "onResult: keyword :" + wordAph);
                    result = expain.toString();

                    above_toast.setText(R.string.release_to_lock);
                    above_toast.setVisibility(View.VISIBLE);

                    query.setText(keyWord.replace(".", "").replaceAll("[0-9]", "").replace("-", ""));
                    trans.setText(result);
                    scannerView.hideToast();
                    dialog.setVisibility(View.VISIBLE);

                    if (wordInfo == null){
                        more.setVisibility(View.GONE);
                    }else {
                        mWordInfo = wordInfo;
                        more.setVisibility(View.VISIBLE);
                    }
                }
//                Log.e(TAG, "onResult: ============");
                handler.sendEmptyMessageDelayed(OcrHandler.CONTINUE_OCR, CONTINUE_OCR_DELAY);

            }
        });
    }

    private long translate_time;
    private void translate(final String s) {
//        synchronized (quickSearchWord) {
            translate_time = System.currentTimeMillis();
            quickSearchWord.search(s);
//        }
    }

    private void openCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
            }catch (Exception e){
                getActivity().finish();
                return ;
            }
        }

        if (camera == null){
//            Toast.makeText(getActivity(), "打开相机失败,请先关闭其他相机或手电筒应用", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return ;
        }
        /**
         * 图像传感器默认坐标轴方向与横屏坐标轴方向一致,竖屏需要调整90度
         */
        Camera.Parameters cameraParameters = camera.getParameters();
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            cameraParameters.set("orientation", "portrait"); //
            cameraParameters.set("rotation", 90); // 镜头角度转90度（默认摄像头是横拍）
            camera.setDisplayOrientation(90); // 在2.2以上可以使用
        } else { //如果是横屏
            cameraParameters.set("orientation", "landscape"); //
            camera.setDisplayOrientation(0); // 在2.2以上可以使用
        }
        cameraParameters.setPictureFormat(ImageFormat.JPEG);

        List<Camera.Area> areas = new ArrayList<>();
        areas.add(new Camera.Area(scannerView.getFrame(),cameraParameters.getMaxNumFocusAreas()));
        cameraParameters.setFocusAreas(areas);
        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        camera.setParameters(cameraParameters);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (camera == null) return;
        try {
            camera.setPreviewTexture(surface);
            camera.startPreview();
            if (flash.isChecked()) HardwareUtil.turnLightOn(camera);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (camera != null){
                        camera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {

                            }
                        });
                    }
                }
            },100);
        } catch (IOException e) {
            camera.stopPreview();
            try {
                camera.setPreviewTexture(surface);
                camera.startPreview();
            } catch (IOException e1) {
                e1.printStackTrace();
                Toast.makeText(getActivity(), "无法连接到相机,请重试", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            e.printStackTrace();
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG, "onSurfaceTextureDestroyed: ");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public static boolean ocrIsReady = true;
    private static class OcrHandler extends Handler{

        protected static final int START_OCR = 0x101;
        protected static final int PAUSE_OCR = 0x102;
        protected static final int CONTINUE_OCR = 0x103;

        private boolean isPaused;

        private int failure_count = 0;

        private WeakReference<ScanFragment> fragmentWeakReference;

        protected OcrHandler(ScanFragment fragment){
            this.fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ScanFragment fragment = fragmentWeakReference.get();
            if (fragment == null){
                return;
            }

            //remove other msg
            if (fragment.handler.hasMessages(START_OCR)){
                fragment.handler.removeMessages(START_OCR);
            }
            //handle the ocr message
            switch (msg.what){
                case START_OCR:
                    if (fragment.ocrIsReady) {
//                        fragment.ocrWorker.release();
                        fragment.prepareOcr();
                    }
                    break;
                case PAUSE_OCR:
                    fragment.ocrWorker.release();
                    ocrIsReady = true;
                    isPaused = true;
                    break;
                case CONTINUE_OCR:
                    if (!isPaused){
                        fragment.prepareOcr();
                    }
                    break;

            }
        }
    }

    public void prepareOcr(){
        ocrIsReady = false;
        ocrObservable.timeout(OCR_TIME_LIMIT,TimeUnit.MILLISECONDS)
                .subscribe(ocrSubcriber);
    }
}