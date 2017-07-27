package com.readboy.mobile.dictionary.iflytek.speech;

import android.content.Context;

import com.iflytek.business.speech.SpeechError;
import com.readboy.Dictionary.R;


/**
 * Created by adapter on 2014/7/8.
 */
public class RecognizerErrorHelper {

    public static String getErrorStr(Context context,int errorCode){
        String ret = context.getResources().getString(R.string.generic_error);
        switch (errorCode){
            case SpeechError.ERROR_NETWORK_TIMEOUT:
            case SpeechError.ERROR_SPEECH_TIMEOUT:
            case SpeechError.ERROR_RESPONSE_TIMEOUT:
            case SpeechError.ERROR_MSP_TIMEOUT:
            case SpeechError.ERROR_INVALID_RESULT:
                ret = context.getResources().getString(R.string.timeOut);
                break;
            case SpeechError.ERROR_NO_NETWORK:
//                ret = context.getResources().getString(R.string.no_internet);
                ret = context.getResources().getString(R.string.error_internet);
                break;
            case SpeechError.ERROR_NETWORK:
                ret = context.getResources().getString(R.string.no_internet);
                break;
            case SpeechError.NETWORK_NOT_AVAILABLE:
                ret = context.getResources().getString(R.string.netFail);
                break;
            case SpeechError.ERROR_MSP_NO_DATA:
            case SpeechError.ERROR_NO_DATA:
                ret = context.getResources().getString(R.string.noVoice);
                break;
            case SpeechError.ERROR_EMPTY_UTTERANCE:
            case SpeechError.ERROR_AUDIO_RECORD:
                ret = context.getResources().getString(R.string.recordFail);
                break;
        }
        return ret;
    }
}
