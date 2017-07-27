package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Senny on 2015/11/19.
 */
public class LinearLayoutToucher extends LinearLayout {

    private OnDispatchTouchEventListener listener;

    public void setOnDispatchTouchEventListener(OnDispatchTouchEventListener listener) {
        this.listener = listener;
    }

    public LinearLayoutToucher(Context context) {
        super(context);
    }

    public LinearLayoutToucher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutToucher(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (listener != null) {
            listener.dispatchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface OnDispatchTouchEventListener {
        void dispatchEvent(MotionEvent ev);
    }
}
