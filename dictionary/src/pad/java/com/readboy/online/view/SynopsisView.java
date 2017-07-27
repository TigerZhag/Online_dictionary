package com.readboy.online.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiawei on 16-6-30.
 */
public class SynopsisView extends TextView {

    private Paint wordPaint,synopsisPaint;
    private List<String> mList;

    public SynopsisView(Context context) {
        super(context);
        initPaint();

    }

    public SynopsisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public SynopsisView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    /*   初始化画笔   */
    private void initPaint(){
        wordPaint = new Paint();
        wordPaint.setAntiAlias(false);
        wordPaint.setStyle(Paint.Style.FILL);
        wordPaint.setColor(Color.BLACK);
        wordPaint.setStrokeCap(Paint.Cap.ROUND);
        wordPaint.setStrokeJoin(Paint.Join.ROUND);
        wordPaint.setStrokeWidth(100);
        wordPaint.setTextSize(24);
        wordPaint.setColor(Color.parseColor("#000000"));

        synopsisPaint = new Paint();
        synopsisPaint.setAntiAlias(false);
        synopsisPaint.setStyle(Paint.Style.FILL);
        synopsisPaint.setStrokeCap(Paint.Cap.ROUND);
        synopsisPaint.setStrokeJoin(Paint.Join.ROUND);
        synopsisPaint.setStrokeWidth(5);
        synopsisPaint.setTextSize(24);
        synopsisPaint.setColor(Color.parseColor("#7e7e7e"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(int i=0;i<mList.size();i++){
            if(mList.get(i).equals("")){
                canvas.drawText(mList.get(i),46,i*100,wordPaint);
            }else {
                canvas.drawText(mList.get(i),46,i*25,wordPaint);
            }
        }
        super.onDraw(canvas);
    }

    public List<String> getmList() {
        return mList;
    }

    public void setmList(List<String> mList) {
        this.mList = mList;
    }
}
