package com.readboy.library.widget.analy;

import android.graphics.Paint;
import android.graphics.Typeface;

import com.readboy.library.widget.IParaser;

/**
 * Editor: sgc
 * Date: 2015/01/04
 */
public interface ImlAnalyze<T> {

    void setObtainer(T t);
    void start();
    void stop();
    void clear();
    IParaser createParaser(AnalysisInfo analysisInfo);
    Typeface getPaintTypeface();

}
