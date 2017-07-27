package com.readboy.mobile.dictionary.dict;

import android.graphics.Typeface;

import com.readboy.library.utils.NormalWordAnalyze;

/**
 * Created by 123 on 2016/7/30.
 */

public class RBNormalWordAnalyze extends NormalWordAnalyze {
    public RBNormalWordAnalyze(String string) {
        super(string);
    }

    @Override
    public Typeface getPaintTypeface() {
        return Typeface.create("readboy-code", Typeface.NORMAL);
    }
}
