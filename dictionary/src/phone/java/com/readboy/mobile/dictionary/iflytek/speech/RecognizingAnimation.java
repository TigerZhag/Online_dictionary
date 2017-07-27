package com.readboy.mobile.dictionary.iflytek.speech;

import android.content.Context;
import android.util.AttributeSet;

import com.readboy.mobile.dictionary.iflytek.widget.CircleImage;


/**
 * Created by adapter on 2014/7/7.
 */
public class RecognizingAnimation extends CircleImage {

    public RecognizingAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onCircle(int i) {
//        setBackground();
//        setBackground(getResources().getDrawable(R.drawable.circle_progress_01 + num ++));
//        Log.d("TEstVIew","onCircle " +i);
        this.setRotation(30 * i);
    }
}
