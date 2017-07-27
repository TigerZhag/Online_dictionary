package com.readboy.library.quicksearch;

/**
 * Created by 123 on 2016/9/12.
 */

public interface SearchEngine {

    int find(String str);
    String getWord(int index);
    String getMeaning(int index);

}
