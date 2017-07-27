package com.readboy.mobile.dictionary.iflytek.speech;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.iflytek.business.speech.RecognitionListener;
import com.iflytek.business.speech.RecognizerResult;
import com.iflytek.business.speech.SpeechIntent;
import com.iflytek.business.speech.SpeechServiceUtil;
import com.readboy.Dictionary.R;
import com.readboy.mobile.dictionary.iflytek.widget.RefreshHandler;

import java.lang.reflect.Field;

/**
 * Created by adapter on 2014/7/7.
 */
public class RecognizerDialog extends Dialog {

    SpeechServiceUtil mService;
    RecognizerDialogListener listener;
    private RecognizerView mRecognizerView;
    private static final String TAG = "RecognizerDialog";
    private boolean inited = false;

    public RecognizerDialog(Context context) {
        this(context,0);
    }

    public RecognizerDialog(Context context, int theme) {
        this(context, theme ,null);
    }

    public RecognizerDialog(Context context, int theme, RecognizerDialogListener listener) {
        super(context, R.style.common_dialog_check);
        if(listener == null){
            throw new NullPointerException("ISpeechInitListener is null");
        }
        setInited(false);
//        LogHelper.LOGD(TAG,"setInited");
        this.listener = listener;
        initRecognizer();
    }

    private void initRecognizer(){
        Intent intent = new Intent();
        intent.putExtra(SpeechIntent.SERVICE_LOG_ENABLE, false);
        if(mService == null) {
            mService = new SpeechServiceUtil(getContext(), mListener, intent);
        }else{
            release();
            mService = new SpeechServiceUtil(getContext(), mListener, intent);
        }
    }

    private void hookService(SpeechServiceUtil serviceUtil) {
        if (serviceUtil == null) {
            return;
        }

        try {
            Log.i(TAG, "start hook");
            Field handlerField = SpeechServiceUtil.class.getDeclaredField("mHandler");
            handlerField.setAccessible(true);
            Handler mHandler = (Handler)handlerField.get(serviceUtil);
            Log.i(TAG, "base handler: " + mHandler);
            handlerField.set(serviceUtil, new HookHandler(serviceUtil, mHandler));
            Log.i(TAG, "hook successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class HookHandler extends Handler{
        private final int MSG_REBILND = 257;
        private SpeechServiceUtil mService;
        private Handler mBase;

        public HookHandler(SpeechServiceUtil service, Handler base){
            mService = service;
            mBase = base;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mService == null) {
                return;
            }

            if (msg.what == MSG_REBILND) {
                try {
                    Field connectionField = SpeechServiceUtil.class.getDeclaredField("mConnection");
                    connectionField.setAccessible(true);
                    Object connection = connectionField.get(mService);
                    if (connection == null) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mBase.handleMessage(msg);
        }
    }

    //创建服务绑定监听回调
    SpeechServiceUtil.ISpeechInitListener mListener = new SpeechServiceUtil.ISpeechInitListener(){
        Bundle ivwBundle = new Bundle();
        @Override
        public void onSpeechInit() {
//            LogHelper.LOGD(TAG,"onSpeechInit");
            Intent recIntent = new Intent();
            ivwBundle.putInt(SpeechIntent.ARG_RES_TYPE, SpeechIntent.RES_FROM_CLIENT);
            recIntent.putExtra(SpeechIntent.ENGINE_WAKE_DEC, ivwBundle);

            Bundle offlineBundle = new Bundle();
            offlineBundle.putInt(SpeechIntent.ARG_RES_TYPE,
                    SpeechIntent.RES_FROM_CLIENT);
//            offlineBundle.putStringArray(SpeechIntent.EXT_GRAMMARS_FILES,
//                    grammar_file);
            recIntent.putExtra(SpeechIntent.ENGINE_LOCAL_DEC, offlineBundle);
            recIntent.putExtra(SpeechIntent.EXT_GRAMMARS_FLUSH, true);
            mService.initRecognitionEngine(mRecListener, recIntent);

            setInited(true);
        }

        @Override
        public void onSpeechUninit() {
//            LogHelper.LOGD(TAG,"onSpeechUninit");
            setInited(false);
        }
    };

    RecognitionListener.Stub mRecListener = new RecognitionListener.Stub(){

        @Override
        public void onGrammarResult(int i, String s, int i2) throws RemoteException {
//            LogHelper.LOGD(TAG, "onGrammarResult s=" + s);
        }

        @Override
        public void onInit() throws RemoteException {
//            LogHelper.LOGD(TAG, "onInit");
        }

        @Override
        public void onRecordStart() throws RemoteException {
//            LogHelper.LOGD(TAG,"onRecordStart");
        }

        @Override
        public void onSpeechStart() throws RemoteException {
//            LogHelper.LOGD(TAG,"onSpeechStart");
            handler.sendMessage(MSG_SPEED_START);
        }

        @Override
        public void onSpeechEnd() throws RemoteException {
//            LogHelper.LOGD(TAG,"onSpeechEnd");
            handler.sendMessage(MSG_SPEED_END);
        }

        @Override
        public void onRecordEnd() throws RemoteException {
//            LogHelper.LOGD(TAG,"onRecordEnd ");
        }

        @Override
        public void onVolumeGet(int i) throws RemoteException {
//            LogHelper.LOGD(TAG,"onVolumeGet i="  + i);
            handler.sendMessage(MSG_VOLUME_CHANGE,i);
        }

        @Override
        public void onError(int i) throws RemoteException {
//            LogHelper.LOGD(TAG,"onError i="  + i);
            handler.sendMessage(MSG_ERROR,i);
        }

        @Override
        public void onResult(RecognizerResult recognizerResult) throws RemoteException {
//            LogHelper.LOGD(TAG,"onResult context="  + recognizerResult.mContent);
            String temp = recognizerResult.mContent;
            if(temp != null && temp.length() > 0){
            	temp = temp.substring(0,(temp.length()-1));
            }
            handler.sendMessage(MSG_RESULT,temp);
        }

        @Override
        public void onPartialResult(RecognizerResult recognizerResult) throws RemoteException {
//            LogHelper.LOGD(TAG,"onPartialResult context =" + recognizerResult.mContent );
        }

        @Override
        public void onEnd(Intent intent) throws RemoteException {
//            LogHelper.LOGD(TAG,"onEnd s=" + intent.toString());
            handler.sendMessage(MSG_END);
        }
    };//创建识别引擎回调接口

    @Override
    public void cancel() {
        super.cancel();
        stop();
    }

    private boolean startRecognize(/*View view*/){
        if(mService != null) {
            //开始识别
//            mService.startRecognize(new Intent());//intent为Intent对象，根据识别需要传入对应的参数值；
            Intent intent1= new Intent();
            intent1.putExtra(SpeechIntent.EXT_ENGINE_TYPE, SpeechIntent.ENGINE_WEB);
            intent1.putExtra(SpeechIntent.EXT_WEB_SCENE, "all");
            intent1.putExtra(SpeechIntent.EXT_PCM_LOG, true);
            intent1.putExtra(SpeechIntent.EXT_SAMPLERATE, 16000);
//            LogHelper.LOGD(TAG, "startRecognize");
            mService.startRecognize(intent1);
        }
        return mService != null;
    }

    @Override
    public void show() {
        if(isInited()) {
            super.show();
            if (startRecognize()) {

            }
        }else{
            Toast.makeText(getContext(),getContext().getString(R.string.voiceInitFail),Toast.LENGTH_SHORT).show();
        }
        if (mRecognizerView != null) {
            mRecognizerView.reset();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecognizerView = (RecognizerView)View.inflate(getContext(), R.layout.dialog_recognizer,null);
        setContentView(mRecognizerView);
        mRecognizerView.setOnEndRecognizer(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mService != null){
//                    LogHelper.LOGD(TAG,",mService.isSpeaking()=" +mService.isSpeaking());
                    mService.endRecognize();
                    handler.sendMessage(MSG_SPEED_END);
                }
            }
        });
    }

    public void release() {
        stop();
        if(mService != null){
            hookService(mService);
            mService.destroy();
            mService = null;
        }
    }

    private void stop(){
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if(mService != null) {
            mService.stopSpeak();
            mService.stopRecognize();
        }
        if(mRecognizerView != null) {
            mRecognizerView.updateState(RecognizerView.STATE_SPEED_START);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        stop();
    }

    public boolean isInited() {
        return inited;
    }

    public void setInited(boolean isInited) {
        this.inited = isInited;
    }

    public interface RecognizerDialogListener{
        public void onResult(String text);
        public void onError(int error);
    }

    private static final int MSG_SPEED_START = 255;
    private static final int MSG_VOLUME_CHANGE = MSG_SPEED_START + 1;
    private static final int MSG_SPEED_END = MSG_SPEED_START + 2;
    private static final int MSG_ERROR = MSG_SPEED_START + 3;
    private static final int MSG_RESULT = MSG_SPEED_START + 4;
    private static final int MSG_END = MSG_SPEED_START + 5;
    private static final int MSG_DELAY_DISMISS = MSG_SPEED_START + 6;

    RefreshHandler handler = new RefreshHandler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SPEED_START:
//                    mRecognizerView.setVisibility(View.VISIBLE);
                    mRecognizerView.updateState(RecognizerView.STATE_SPEED_START);
                    break;
                case MSG_VOLUME_CHANGE:
                    mRecognizerView.updateLevel(msg.arg1);
                    break;
                case MSG_SPEED_END:
                    mRecognizerView.updateState(RecognizerView.STATE_RECOGNIZER);
                    break;
                case MSG_ERROR:
                    mRecognizerView.updateState(RecognizerView.STATE_ERROR,msg.arg1);
                    if(listener != null){
                        listener.onError(msg.arg1);
                    }
                    this.sendMessageDelayed(MSG_DELAY_DISMISS,null,1000);
                    break;
                case MSG_RESULT:
                    if(listener != null && msg.obj != null && msg.obj instanceof String){
                        listener.onResult((String)msg.obj);
                    }
                    try {
                        if (isShowing()) {
                            dismiss();
                        }
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG,"illegalArgumentException");
                    }
                    break;
                case MSG_END:
                    /*if(listener != null){
                        listener.onResult();
                    }*/
//                    RecognizerDialog.this.dismiss();
                    break;
                case MSG_DELAY_DISMISS:
                    try {
                        if (isShowing()) {
                            dismiss();
                        }
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG,"illegalArgumentException");
                    }
                    break;
            }
        }
    };

}
