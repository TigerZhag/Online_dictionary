package com.readboy.mobile.dictionary.iflytek.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.readboy.Dictionary.R;


/**
 * Created by adapter on 2014/5/28.
 */
public class CircleImage extends TextView {

    private static final String TAG = "LoadingImage";
    private LoopHandler handler ;
//    private static final int MA_ROTATION= 360;

    private static final int DEFAULT_PER_ROTATION = 40;
    private int cyclePerRotation = DEFAULT_PER_ROTATION;
    private int num = 0;
    private int delay = LoopHandler.DEFAULT_DELAY;
    private int count = 1;

    public CircleImage(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImage, defStyle, 0);
        if(a != null) {
            cyclePerRotation = a.getInteger(R.styleable.CircleImage_perRotation, DEFAULT_PER_ROTATION);
            delay = a.getInteger(R.styleable.CircleImage_delayTime, LoopHandler.DEFAULT_DELAY);
            count = a.getInteger(R.styleable.CircleImage_count, 1);
            a.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    public void start(){
        if(handler == null){
            handler = new LoopHandler(true,delay){
                @Override
                public void update(){
//                    LoadingImage.this.setRotation(cyclePerRotation * num++);
//                    setBackground(getResources().getDrawable(R.drawable.circle_progress_01 + num ++));
                    onCircle(num++);
                    if(num >= count){
                        num = 0;
                    }
                    super.update();
                }
            };
        }else{
            handler.startLoop();
        }
    }

    public void stop(){
        if(handler != null) {
            handler.stop();
        }
    }

    public void onCircle(int i){
        setBackground(getResources().getDrawable(R.drawable.circle_progress_01 + i));
    }
}
