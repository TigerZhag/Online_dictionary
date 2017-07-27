package com.readboy.mobile.dictionary.iflytek;

import android.annotation.SuppressLint;
import android.content.Context;

import com.readboy.mobile.dictionary.iflytek.speech.RecognizerDialog;


/**
 * Created by adapter on 2014/6/30.
 */
public class VoiceRecognition implements IVoiceRecognition{
    private static final String TAG = "VoiceRecognize";
    // 语音听写对象
    /*private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog iatDialog;
    private RecognizerDialogListener listener ;*/
    private boolean isInited = false;
    private RecognizerDialog dialog;
    private String lag = ZH_CN;

    public VoiceRecognition(Context context,RecognizerDialog.RecognizerDialogListener listener){
    // 初始化识别对象
        /*mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        // 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
        iatDialog = new RecognizerDialog(context,mInitListener);
        this.listener = listener;*/
        if(dialog == null) {
            dialog = new RecognizerDialog(context, 0, listener);
        }

    }

    /**
     * 初始化监听器。
     */
    /*private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            LogHelper.LOGD(TAG, "SpeechRecognizer init() code = " + code);

            isInited = true;
        }
    };*/

    @Override
    public void start() {
        //        mResultText.setText(null);// 清空显示内容
        // 设置参数
        setParam();
        boolean isShowDialog = true;//mSharedPreferences.getBoolean(getString(R.string.pref_key_iat_show), true);
        if (isShowDialog) {
            dialog.show();
            // 显示听写对话框
            /*iatDialog.setListener(listener);
            iatDialog.show();*/
//            showTip(getString(R.string.text_begin));
        } else {
            // 不显示听写对话框
            /*ret = mIat.startListening(recognizerListener);
            if(ret != ErrorCode.SUCCESS){
//                showTip("听写失败,错误码：" + ret);
            }else {
//                showTip(getString(R.string.text_begin));
            }*/
        }
    }

    public void stop(){
        /*mIat.stopListening();*/
        /*dialog.st*/
//        Utils.dismissDialog(dialog);
    }

    @Override
    public void release() {
        if(dialog != null){
            dialog.release();
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public boolean initSuccess() {
        return dialog != null ? dialog.isInited() : false;
    }

    /**
     * 参数设置
     * @return
     */
    @SuppressLint("SdCardPath")
    public void setParam(){
        /*updateLanguage();
        // 设置语音前端点
        mIat.setParameter(SpeechConstant.VAD_BOS, "5000");
        // 设置语音后端点
        mIat.setParameter(SpeechConstant.VAD_EOS, "1800");
        // 设置标点符号
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        // 设置音频保存路径
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, "/sdcard/iflytek/wavaudio.pcm");*/
    }

    private void updateLanguage(){
        /*if (lag.equals(EN_US)) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, EN_US);
        }else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, ZH_CN);
        }
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT,lag);*/
    }


    public void updateLanguage(String lag){
        this.lag = lag;
    }
}
