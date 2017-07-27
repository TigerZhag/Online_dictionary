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
public class WordView extends TextView {

    private Paint wordPaint,borderPaint,linePaint;
    private String word;

    public WordView(Context context) {
        super(context);
        setWordStyle(context);
        initPaint();

    }

    public WordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWordStyle(context);
        initPaint();
    }

    public WordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWordStyle(context);
        initPaint();
    }

    /*   设置字体样式   */
    private void setWordStyle(Context context){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/2.TTF");
        this.setTypeface(typeface);
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
        wordPaint.setStrokeWidth(5);
        wordPaint.setTextSize(textSize);

        linePaint = new Paint();
        linePaint.setAntiAlias(false);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.parseColor("#ff0000"));
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeWidth(2);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(false);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.parseColor("#ff0000"));
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setStrokeWidth(4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //要先绘画九宫格在继承,否则字体会被九宫格遮住
        paintBorder(canvas);
        paintLine(canvas);
//        word = getText().toString();
//        paintWord(canvas,word);
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

    /*   绘画交叉线   */
    private void paintLine(Canvas canvas){
        int x = getWidth();
        int y = getHeight();
        canvas.drawLine(0,0,x,y,linePaint);
        canvas.drawLine(0,y,x,0,linePaint);
        canvas.drawLine(0,y/2,x,y/2,linePaint);
        canvas.drawLine(x/2,0,x/2,y,linePaint);
    }

    /*   没有继承TextView时绘画字体,位于中间   */
    private void paintWord(Canvas canvas,String word){
        int x = getWidth();
        int y = getHeight();
        Rect rect = new Rect(0,0,x,y);
        Paint.FontMetricsInt fontMetrics = wordPaint.getFontMetricsInt();
        int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        wordPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(word, rect.centerX(), baseline, wordPaint);
    }

    /*
        <liujiawei.com.demo.view.WordView
            android:id="@+id/word"
            android:layout_width="120dp"
            android:layout_height="120dp"
            android:text="啊"
            android:textSize="105dp"
            android:gravity="center"
            android:textColor="#000"
        />
    */
}
