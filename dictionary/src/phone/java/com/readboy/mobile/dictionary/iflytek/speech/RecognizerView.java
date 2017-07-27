package com.readboy.mobile.dictionary.iflytek.speech;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.business.speech.SpeechError;
import com.readboy.Dictionary.R;


/**
 * Created by adapter on 2014/7/7.
 */
public class RecognizerView extends LinearLayout {

    private int level = 0;
    private int state = STATE_SPEED_START;

    private View listenerLayout;
    private ImageView micro;

    private RecognizingAnimation recognizingAnimation;
    private View recognizerLayout;

    private View errorLayout;
    private View errorImage;
    private TextView errorInfo;

    private View endRecognizer;
    private ImageView stateRecognizerImageView;
    private OnClickListener listener;

    private Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.speaking_bg_rotate);


    public RecognizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void reset() {
        micro.startAnimation(animation);
//        stateRecognizerImageView.startAnimation(animation);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        listenerLayout = findViewById(R.id.stateListener);
        micro = (ImageView) findViewById(R.id.imageView);
        stateRecognizerImageView = (ImageView) findViewById(R.id.stateRecognizerImageView);

        recognizingAnimation = (RecognizingAnimation) findViewById(R.id.recognizingAnmiation);
        recognizerLayout = findViewById(R.id.stateRecognizer);


        errorLayout = findViewById(R.id.stateError);
        errorImage = findViewById(R.id.errorImage);
        errorInfo = (TextView) findViewById(R.id.errorInfo);

        endRecognizer = findViewById(R.id.endRecognizer);
        endRecognizer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });

        animation.setFillEnabled(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
//                animation.start();
            }
        });
        animation.setRepeatCount(Animation.INFINITE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateState(STATE_SPEED_START);
        updateLevel(0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        updateState(STATE_END);
        updateLevel(0);
    }

    public void setOnEndRecognizer(OnClickListener listener) {
        this.listener = listener;
    }

    public void updateLevel(int level) {
        if (state != STATE_SPEED_START) {
            Log.e("RecognizerView", "updateLevel error");
            return;
        }
        int level1 = level / 5;
        if (this.level == level1) {
            return;
        }
        this.level = level1;

        this.level = this.level > 5 ? 5 : this.level;
//        Log.d("updateLevel","level=" +this.level);
        micro.setImageLevel(this.level);
    }

    private void toListening() {
        recognizerLayout.setVisibility(GONE);
//        recognizingAnimation.setVisibility(GONE);
        errorLayout.setVisibility(GONE);

        listenerLayout.setVisibility(VISIBLE);
    }

    private void toRecognizer() {
        recognizerLayout.setVisibility(VISIBLE);
//        recognizingAnimation.setVisibility(VISIBLE);
        errorLayout.setVisibility(GONE);
        listenerLayout.setVisibility(GONE);
    }

    private void toError(Object id) {
        recognizerLayout.setVisibility(GONE);
        recognizingAnimation.setVisibility(GONE);
        errorLayout.setVisibility(VISIBLE);
        listenerLayout.setVisibility(GONE);
        if (id != null && id instanceof Integer) {
            errorInfo.setText(RecognizerErrorHelper.getErrorStr(getContext(), ((Integer) id)));
        }
    }


    protected void updateState(int state) {
        updateState(state, null);
    }

    public void updateState(int state, Object data) {
        this.state = state;
        switch (state) {
            case STATE_END:
            case STATE_SPEED_START:
                toListening();
                break;
            case STATE_RECOGNIZER:
                toRecognizer();
                break;
            case STATE_ERROR:
                toError(data);
                if (data != null && data instanceof Integer) {
                    updateErrorView((Integer) data);
                }
                break;
        }
    }

    private void updateErrorView(int errorCode) {
        switch (errorCode){
            case SpeechError.ERROR_NO_NETWORK:
                errorImage.setBackground(getResources().getDrawable(R.drawable.warning_no_network));
//                errorLayout.setBackground(getResources().getDrawable(R.drawable.voice_bg_1));
//                errorInfo.setTextColor(getResources().getColor(R.color.black));
                break;
            case SpeechError.ERROR_NETWORK_TIMEOUT:
            case SpeechError.ERROR_SPEECH_TIMEOUT:
            case SpeechError.ERROR_RESPONSE_TIMEOUT:
            case SpeechError.ERROR_MSP_TIMEOUT:
            case SpeechError.ERROR_INVALID_RESULT:
            case SpeechError.ERROR_NETWORK:
            case SpeechError.NETWORK_NOT_AVAILABLE:
            case SpeechError.ERROR_MSP_NO_DATA:
            case SpeechError.ERROR_NO_DATA:
            case SpeechError.ERROR_EMPTY_UTTERANCE:
            case SpeechError.ERROR_AUDIO_RECORD:
                errorImage.setBackground(getResources().getDrawable(R.drawable.error_network));
//                errorLayout.setBackground(getResources().getDrawable(R.drawable.voice_bg_2));
//                errorInfo.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    public static final int STATE_SPEED_START = 1;
    public static final int STATE_RECOGNIZER = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_END = 4;


}
