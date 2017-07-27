package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.readboy.Dictionary.R;
import com.readboy.library.gif.GifView;

/**
 * Created by Senny on 2015/11/9.
 */
public class CustomGifView extends GifView {

    private int radius = 16;
    private int strokeWidth = 2;
    private Paint linePaint;
    private Paint bgPaint;

    public CustomGifView(Context context) {
        super(context);
    }

    public CustomGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomGifView(Context context, AttributeSet attrs, int defStyle) {
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
        canvas.drawLine(strokeWidth / 2, 0, strokeWidth / 2, getHeight() - radius, linePaint);
        canvas.drawLine(getWidth() - strokeWidth / 2, 0, getWidth() - strokeWidth / 2, getHeight() - radius, linePaint);
        canvas.drawLine(radius, getHeight() - strokeWidth / 2, getWidth() - radius, getHeight() - strokeWidth / 2, linePaint);
        canvas.drawCircle(0, getHeight(), radius, linePaint);
        canvas.drawCircle(getWidth(), getHeight(), radius, linePaint);
        canvas.drawCircle(0, getHeight(), radius - strokeWidth, bgPaint);
        canvas.drawCircle(getWidth(), getHeight(), radius - strokeWidth, bgPaint);


    }
}
