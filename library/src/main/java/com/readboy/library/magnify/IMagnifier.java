package com.readboy.library.magnify;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by 123 on 2016/7/8.
 */

public interface IMagnifier {
    boolean onTouch(MotionEvent event);
    void onTouch(float x, float y);
    void onDraw(Canvas canvas);
    void cancel();
}
