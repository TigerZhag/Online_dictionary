package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.readboy.Dictionary.R;

/**
 * Created by Senny on 2015/11/17.
 */
public class CustomStrokeLinearLayout extends LinearLayout{

    private int strokeWidth = 2;
    private Paint linePaint;
    private Paint bgPaint;

    public CustomStrokeLinearLayout(Context context) {
        super(context);
    }

    public CustomStrokeLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomStrokeLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.light_grey_4));
        linePaint.setStrokeWidth(strokeWidth);
        bgPaint = new Paint();
        bgPaint.setColor(getResources().getColor(R.color.middle_grey));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, 0, getWidth(), 0, linePaint);
        canvas.drawLine(strokeWidth / 2, 0, strokeWidth / 2, getHeight(), linePaint);
        canvas.drawLine(getWidth() - strokeWidth / 2, strokeWidth / 2, getWidth() - strokeWidth / 2, getHeight(), linePaint);
        canvas.drawLine(0, getHeight() - strokeWidth / 2, getWidth(), getHeight() - strokeWidth / 2, linePaint);


    }
}
