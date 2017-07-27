package com.readboy.mobile.dictionary.iflytek;

/**
 * Created by adapter on 2014/6/30.
 */
public interface IVoiceRecognition {

    public void start();
    public void stop();
//    public void cancel();
    public void release();
//    public void resume();
    public boolean initSuccess();
//    public void reInitialization(Context context,RecognizerDialog.RecognizerDialogListener listener);
//    public void updateLanguage(String lan);

    public static final String EN_US = "en_us";
    public static final String ZH_CN = "zh_cn";
}
