package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.readboy.Dictionary.R;

/**
 * Created by Senny on 2015/11/10.
 */
public class CustomRadiusLinearLayout extends LinearLayout {

    private int radius = 16;
    private int strokeWidth = 2;
    private Paint linePaint;
    private Paint bgPaint;

    public CustomRadiusLinearLayout(Context context) {
        super(context);
    }

    public CustomRadiusLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRadiusLinearLayout(Context context, AttributeSet attrs, int defStyle) {
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
//            canvas.drawLine(radius, 0, getWidth() - radius, 0, linePaint);
        canvas.drawLine(strokeWidth / 2, radius, strokeWidth / 2, getHeight(), linePaint);
        canvas.drawLine(getWidth() - strokeWidth / 2, radius, getWidth() - strokeWidth / 2, getHeight(), linePaint);
        canvas.drawLine(0, getHeight() - strokeWidth / 2, getWidth(), getHeight() - strokeWidth / 2, linePaint);
        canvas.drawCircle(0, 0, radius, linePaint);
        canvas.drawCircle(getWidth(), 0, radius, linePaint);
        canvas.drawCircle(0, 0, radius - strokeWidth, bgPaint);
        canvas.drawCircle(getWidth(), 0, radius - strokeWidth, bgPaint);


    }
}
