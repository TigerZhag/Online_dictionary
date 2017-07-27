package com.readboy.library.quicksearch;

import com.readboy.library.search.DictWordSearch;

/**
 * Created by 123 on 2016/9/12.
 */

public class DictSearch extends DictWordSearch implements SearchEngine {
    @Override
    public int find(String str) {
        return getSearchKeyIndexByAccurate(str);
    }

    @Override
    public String getWord(int index) {
        return getKeyWord(index);
    }

    @Override
    public String getMeaning(int index) {
        return null;
    }
}
