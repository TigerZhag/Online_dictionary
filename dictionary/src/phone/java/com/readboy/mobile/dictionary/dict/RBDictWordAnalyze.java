package com.readboy.mobile.dictionary.dict;

import android.graphics.Typeface;

import com.readboy.library.utils.DictWordAnalyze;

/**
 * Created by 123 on 2016/7/30.
 */

public class RBDictWordAnalyze extends DictWordAnalyze {
    public RBDictWordAnalyze(byte[] byteBuffer) {
        super(byteBuffer);
    }

    public RBDictWordAnalyze(String keyWord, byte[] byteBuffer) {
        super(keyWord, byteBuffer);
    }

    @Override
    public Typeface getPaintTypeface() {
        return Typeface.create("readboy-code", Typeface.NORMAL);
    }
}
