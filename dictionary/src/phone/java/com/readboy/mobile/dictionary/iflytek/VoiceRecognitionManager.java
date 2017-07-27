package com.readboy.mobile.dictionary.iflytek;

import android.content.Context;

import com.readboy.mobile.dictionary.iflytek.speech.RecognizerDialog;

/**
 * Created by Black_Horse on 2016/7/6.
 */

public class VoiceRecognitionManager implements RecognizerDialog.RecognizerDialogListener {

    private VoiceRecognition voiceRecognition;
    private OnVoiceRecognitionListener listener;
    private Context mContext;

    public void init(Context context, OnVoiceRecognitionListener listener) {
        voiceRecognition = new VoiceRecognition(context, this);
        this.listener = listener;
        mContext = context;
    }

    public void showVoiceRecognitionDialog(Context context) {
        if (voiceRecognition != null && voiceRecognition.initSuccess()) {
            voiceRecognition.start();
        } else {
            if (voiceRecognition == null) {
                voiceRecognition = new VoiceRecognition(context, this);
            } else {
                voiceRecognition.release();
                voiceRecognition = new VoiceRecognition(context, this);
            }
            if (!voiceRecognition.initSuccess()) {

            }
        }
    }

    public void onStop() {
        if (voiceRecognition != null) {
            voiceRecognition.stop();
        }
    }

    public void onDestroy() {
        if (voiceRecognition != null) {
            voiceRecognition.release();
        }
    }

    @Override
    public void onResult(String text) {
        if (listener != null) {
            listener.onResult(text);
        }
    }

    @Override
    public void onError(int error) {
    }

    public interface OnVoiceRecognitionListener {
        void onResult(String text);
    }
}
