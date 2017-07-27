package com.readboy.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.readboy.library.magnify.IMagnifier;
import com.readboy.library.magnify.MagnifyGlasses;

/**
 * Created by 123 on 2016/7/8.
 */

public class MagnifyGlassesView extends FrameLayout implements View.OnTouchListener {

    private String MAGNIFY_TARGET_VIEW = "MagnifyTagetView";
    private float offsetX;
    private float offsetY;
    private boolean isScreenShot = false;
    private boolean isReset = true;

    private IMagnifier iMagnifier;

    public void setScreenShot(boolean isScreenShot) {
        this.isScreenShot = isScreenShot;
    }

    public MagnifyGlassesView(Context context) {
        super(context);
    }

    public MagnifyGlassesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MagnifyGlassesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void reset() {
        isReset = true;
    }

    public void resetTargetXY() {
        View targetView = findViewWithTag(MAGNIFY_TARGET_VIEW);
        if (targetView != null) {
            targetView.setOnTouchListener(this);
            offsetX = 0;
            offsetY = 0;
            do {
                offsetX += targetView.getX();
                offsetY += targetView.getY();
                targetView = (View) targetView.getParent();
                if (targetView ==  this) {
                    break;
                }
            } while (targetView != null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (iMagnifier == null) {
            iMagnifier = new MagnifyGlasses(this, getChildAt(0));
        }
        if (changed || isReset) {
            resetTargetXY();
            isReset = false;
        }

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (isScreenShot) {
            if (iMagnifier != null) {
                iMagnifier.onDraw(canvas);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isScreenShot) {
            float relativeX = event.getX() + offsetX;
            float relativeY = event.getY() + offsetY;
            if (offsetX > relativeX) {
                relativeX = offsetX;
            }
            if (offsetY > relativeY) {
                relativeY = offsetY;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (iMagnifier != null) {
                        iMagnifier.onTouch(relativeX, relativeY);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (iMagnifier != null) {
                        iMagnifier.onTouch(relativeX, relativeY);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (iMagnifier != null) {
                        iMagnifier.cancel();
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                    if (iMagnifier != null) {
                        iMagnifier.cancel();
                    }
                    break;
            }
        }
        return false;
    }
}
