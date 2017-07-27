package com.readboy.library.quicksearch;

import android.content.Context;

import com.readboy.translation.Translator;
import com.readboy.translation.bean.TransResult;
import com.readboy.translation.database.DictDBHelper;

import java.util.List;

/**
 * Created by 123 on 2016/9/13.
 */

public class YoudaoTranslateEngine extends DictDBHelper implements SearchEngine {


    public YoudaoTranslateEngine(Context context) {
        super(context);
    }

    @Override
    public int find(String str) {
        List<TransResult> transResultList = search(str, false);
        if (transResultList != null && transResultList.size() > 0) {
            return transResultList.get(0).id;
        }
        return -1;
    }

    @Override
    public String getWord(int index) {
        List<TransResult> transResultList = search(index);
        if (transResultList != null && transResultList.size() > 0) {
            return transResultList.get(0).word;
        }
        return null;
    }

    @Override
    public String getMeaning(int index) {
        List<TransResult> transResultList = search(index);
        if (transResultList != null && transResultList.size() > 0) {
            return transResultList.get(0).meaning;
        }
        return null;
    }
}
