package com.readboy.mobile.dictionary.dict;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.readboy.Dictionary.R;
import com.readboy.library.utils.DictWordAnalyze;
import com.readboy.library.utils.NormalWordAnalyze;
import com.readboy.library.widget.TextScrollView;

/**
 * Created by Senny on 2015/10/27.
 */
public class DictWordView extends TextScrollView {

    private int radius = 16;
    private int strokeWidth = 2;
    private Paint linePaint;
    private Paint bgPaint;
    private boolean isBlnFrame;
    private int oldWidth;
    private int oldHeight;

    public DictWordView(Context context) {
        super(context);
    }

    public DictWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DictWordView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setDictWordText(byte[] buffer) {
        RBDictWordAnalyze dictWordAnalyze = new RBDictWordAnalyze(buffer);
        setText(dictWordAnalyze);
    }

    public void setDictWordText(String keyWord, byte[] buffer) {
        RBDictWordAnalyze dictWordAnalyze = new RBDictWordAnalyze(keyWord, buffer);
        setText(dictWordAnalyze);
    }

    /*public void setDictWordText(String string) {
        NormalWordAnalyze normalWordAnalyze = new NormalWordAnalyze(string);
        setText(normalWordAnalyze);
    }*/

    public void setDictWordText(String keyWord, String expain) {
        expain = keyWord + "\n" + expain;
        RBNormalWordAnalyze normalWordAnalyze = new RBNormalWordAnalyze(expain);
        setText(normalWordAnalyze);
    }

    public void setFrame(boolean isBlnFrame) {
        this.isBlnFrame = isBlnFrame;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setSelectBackgroundColor(getResources().getColor(R.color.light_blue));
        getItemGroup().setPadding(getItemGroup().getPaddingLeft(),
                30, getItemGroup().getPaddingRight(),
                30);

        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.light_grey_4));
        linePaint.setStrokeWidth(strokeWidth);
        bgPaint = new Paint();
        bgPaint.setColor(getResources().getColor(R.color.middle_grey));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > (getPaddingLeft() + getPaddingRight())
                && height > (getPaddingTop() + getPaddingBottom())) {
            oldWidth = width;
            oldHeight = height;
        } else if (width > (getPaddingLeft() + getPaddingRight())) {
            oldWidth = width;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(oldHeight, MeasureSpec.EXACTLY);
        } else if (height > (getPaddingTop() + getPaddingBottom())) {
            oldHeight = height;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(oldWidth, MeasureSpec.EXACTLY);
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(oldHeight, MeasureSpec.EXACTLY);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(oldWidth, MeasureSpec.EXACTLY);
        }

        //check again.
        if ((getPaddingLeft() + getPaddingRight()) >= oldWidth) {
            oldWidth = (getPaddingLeft() + getPaddingRight()) * 2;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(oldWidth, MeasureSpec.EXACTLY);
        }
        if ((getPaddingTop() + getPaddingBottom()) >= oldHeight) {
            oldHeight = (getPaddingTop() + getPaddingBottom()) * 20;
            if (oldHeight == 0) {
                oldHeight = 200;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(oldHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isBlnFrame) {
//            canvas.drawLine(radius, 0, getWidth() - radius, 0, linePaint);
            canvas.drawLine(strokeWidth / 2, radius, 0, getHeight(), linePaint);
            canvas.drawLine(getWidth() - strokeWidth / 2, radius, getWidth() - strokeWidth / 2, getHeight(), linePaint);
            canvas.drawLine(0, getHeight() - strokeWidth / 2, getWidth(), getHeight() - strokeWidth / 2, linePaint);
            canvas.drawCircle(0, 0, radius, linePaint);
            canvas.drawCircle(getWidth(), 0, radius, linePaint);
            canvas.drawCircle(0, 0, radius - strokeWidth, bgPaint);
            canvas.drawCircle(getWidth(), 0, radius - strokeWidth, bgPaint);
        }


    }
}
