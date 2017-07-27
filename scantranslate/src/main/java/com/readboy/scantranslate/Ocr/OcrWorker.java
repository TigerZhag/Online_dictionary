package com.readboy.scantranslate.Ocr;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.readboy.scantranslate.Utils.FileUtil;
import com.readboy.scantranslate.Utils.ImageUtil;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Zhang shixin
 * @date 16-7-6.
 */
public class OcrWorker {
    private static OcrWorker instance = new OcrWorker();

    public static OcrWorker getInstance(){
        return instance;
    }

    private OcrWorker(){

    }

    private static TessBaseAPI baseApi = new TessBaseAPI();

    public static final String OCR_PATH_SYSTEM = "/system/lib/";
    public static final String OCR_PATH_DATA = "/data/data/com.readboy.scantranslation/lib/";

    public static final String sys_filePath = "/system/readboy/";
    public static final String local_filePath =  Environment.getExternalStorageDirectory().toString() + File.separator +
            "Android" + File.separator + "data" + File.separator + "com.readboy.scantranslation" +
            File.separator + "files"  + File.separator;

    public boolean ocrIsInit;

    private static final String TAG = "OcrWorker";
    public void initOcr(Action1<String> action1, Action1<Throwable> errAction){
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //do the init work
                String filePath;
                //判断系统是否内置数据
                if (FileUtil.tessDataIsExists(sys_filePath)){
                    filePath = sys_filePath;
                }else if(FileUtil.tessDataIsExists(local_filePath)){
                    filePath = local_filePath;
                }else {
                    //没有找到数据
                    subscriber.onError(new Exception());
                    return;
                }
                try{
                    ocrIsInit = baseApi.init(filePath,"eng",0);
                    subscriber.onNext("init success");
                    subscriber.onCompleted();
                }catch (IllegalArgumentException exception){
                    subscriber.onError(exception);
                }
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(action1,errAction);
    }

    public boolean needToFocus(Bitmap bitmap){
        return baseApi.NeedToFocus(bitmap,bitmap.getWidth(),bitmap.getHeight(),4);
    }

    public String doOcr(final Bitmap bitmap){
        synchronized (baseApi) {
            if (needToFocus(bitmap)) {
                Log.e(TAG, "doOcr: needFocus");
                return "";
            }
            baseApi.clear();
            baseApi.setImage(bitmap);
            String text;
            long start = System.currentTimeMillis();   //获取开始时间
            // TODO: 16-7-25 here is a synchronize problem
            try {
                text = baseApi.getUTF8Text();
            }catch (Exception e){
                Log.e(TAG, "doOcr: 开动~~~出错拉");
                text = "";
            }
            long end = System.currentTimeMillis(); //获取结束时间
            Log.e(TAG, "getUTF8TextWith() take " + (end - start) + "ms");
            ImageUtil.recycleBmp(bitmap);
//            Log.d(TAG, "doOcr: 5");
            if (text != null) {
                char[] c = text.toCharArray();
                for (int i = 0; i < c.length - 1; i++) {
                    if ((char) (byte) c[i] != c[i] && c[i + 1] == ' ') {
                        for (int j = i + 1; j < c.length - 1; j++) {
                            c[j] = c[j + 1];
                        }
                        if (c[c.length - 1] != ' ') {
                            c[c.length - 1] = ' ';
                        }
                    }
                }
                Log.e(TAG, "do ocr result :" + String.valueOf(c).trim());
                return String.valueOf(c).trim().replace("\n", "");
            } else {
                return null;
            }
        }
    }

    public void release(){
        if (baseApi != null) {
            baseApi.stop();
        }
    }
}
