package com.readboy.online.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by liujiawei on 16-6-30.
 */
public class PinYinWordView extends TextView {

    private Paint wordPaint,borderPaint;
    private String pinyin;
    private String zi;

    public PinYinWordView(Context context) {
        super(context);
        initPaint();
    }

    public PinYinWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public PinYinWordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    /*   初始化画笔   */
    private void initPaint(){
        int textSize = (int) getTextSize();
        wordPaint = new Paint();
        wordPaint.setAntiAlias(false);
        wordPaint.setStyle(Paint.Style.FILL);
        wordPaint.setColor(Color.BLACK);
        wordPaint.setStrokeCap(Paint.Cap.ROUND);
        wordPaint.setStrokeJoin(Paint.Join.ROUND);
        wordPaint.setStrokeWidth(6);
        wordPaint.setTextSize(23);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(false);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.parseColor("#000000"));
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paintBorder(canvas);
        paintPinYin(canvas,pinyin);
        paintZi(canvas,zi);
        super.onDraw(canvas);
    }

    /*   绘画边框   */
    private void paintBorder(Canvas canvas){
        int x = getWidth();
        int y = getHeight();
        canvas.drawLine(0,0,0,y,borderPaint);
        canvas.drawLine(0,0,x,0,borderPaint);
        canvas.drawLine(x,0,x,y,borderPaint);
        canvas.drawLine(0,y,x,y,borderPaint);
    }

    private void paintPinYin(Canvas canvas,String word){
        int x = getWidth();
        int y = getHeight();
        Rect rect = new Rect(0,0,x,y);
        Paint.FontMetricsInt fontMetrics = wordPaint.getFontMetricsInt();
        double baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 3.5;
        wordPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(word, rect.centerX(), (float) baseline, wordPaint);
    }

    private void paintZi(Canvas canvas,String word){
        int x = getWidth();
        int y = getHeight();
        Rect rect = new Rect(0,0,x,y);
        Paint.FontMetricsInt fontMetrics = wordPaint.getFontMetricsInt();
        double baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2 + 15 ;
        wordPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(word, rect.centerX(), (float) baseline, wordPaint);
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getZi() {
        return zi;
    }

    public void setZi(String zi) {
        this.zi = zi;
    }
}
