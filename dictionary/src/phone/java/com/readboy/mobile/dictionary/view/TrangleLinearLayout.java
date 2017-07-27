package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.readboy.Dictionary.R;


/**
 * Created by Senny on 2015/11/11.
 */
public class TrangleLinearLayout extends FrameLayout {

    private int trangleHeight = 20;
    private int trangleAngle = 30;
    private boolean isBlnUp;
    private Path tranglePath;
    private Paint tranglePaint;
    private boolean blnTrangleEnable;

    public TrangleLinearLayout(Context context) {
        super(context);
    }

    public TrangleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public TrangleLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
    }

    public void setTrangleEnable(boolean enable) {
        blnTrangleEnable = enable;
    }

    public void setDirection(boolean isUp) {
        this.isBlnUp = isUp;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for (int i = 0;i < getChildCount();i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            if (child.getMeasuredHeight() > height) {
                height = child.getMeasuredHeight();
            }
        }
        if (height != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(trangleHeight + height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        tranglePaint = new Paint();
        tranglePaint.setStyle(Paint.Style.FILL);
        tranglePaint.setColor(getResources().getColor(R.color.dark_overlay));
        int leave = (r - l) / 2 - trangleHeight;
        if (isBlnUp) {
            tranglePath = new Path();
            tranglePath.moveTo(leave, 0);
            tranglePath.lineTo(leave + trangleHeight, trangleHeight);
            tranglePath.lineTo(leave - trangleHeight, trangleHeight);
            tranglePath.lineTo(leave, 0);
            setPadding(getPaddingLeft(), trangleHeight, getPaddingRight(), getPaddingBottom());
        } else {
            tranglePath = new Path();
            tranglePath.moveTo(leave, b -t);
            tranglePath.lineTo(leave + trangleHeight, b -t - trangleHeight);
            tranglePath.lineTo(leave - trangleHeight, b -t - trangleHeight);
            tranglePath.lineTo(leave, b -t);
            setPadding(getPaddingLeft(), 0, getPaddingRight(), getPaddingBottom());
        }
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (blnTrangleEnable) {
            canvas.drawPath(tranglePath, tranglePaint);
        }
    }
}
