package com.readboy.library.quicksearch.filter;

import com.readboy.library.quicksearch.SearchEngine;

/**
 * Created by 123 on 2016/9/7.
 */

public class FilterCharManager {

    public static int filter(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr != null) {
            if (lowerFindStr.endsWith(FilterSChar.END_WITH_CHAR_IES)) {
                index = FilterSChar.filterIES(searchEngine, index, lowerFindStr);
            } else if (lowerFindStr.endsWith(FilterSChar.END_WITH_CHAR_VES)) {
                index = FilterSChar.filterVES(searchEngine, index, lowerFindStr);
            } else if (lowerFindStr.endsWith(FilterSChar.END_WITH_CHAR_ICES)) {
                index = FilterSChar.filterICES(searchEngine, index, lowerFindStr);
            } else if (lowerFindStr.endsWith(FilterSChar.END_WITH_CHAR_ES)) {
                if (lowerFindStr.endsWith("s" + FilterSChar.END_WITH_CHAR_ES) ||
                        lowerFindStr.endsWith("sh" + FilterSChar.END_WITH_CHAR_ES) ||
                        lowerFindStr.endsWith("ch" + FilterSChar.END_WITH_CHAR_ES) ||
                        lowerFindStr.endsWith("x" + FilterSChar.END_WITH_CHAR_ES) ||
                        lowerFindStr.endsWith("o" + FilterSChar.END_WITH_CHAR_ES)) {
                    index = FilterSChar.filterES(searchEngine, index, lowerFindStr);
                } else {
                    index = FilterSChar.filter(searchEngine, index, lowerFindStr, FilterSChar.END_WITH_CHAR_S);
                }
            } else if (lowerFindStr.endsWith(FilterSChar.END_WITH_CHAR_S)){
                index = FilterSChar.filter(searchEngine, index, lowerFindStr, FilterSChar.END_WITH_CHAR_S);
            } else if (lowerFindStr.endsWith(FilterSChar.END_WITH_CHAR_I)) {
                index = FilterSChar.filterI(searchEngine, index, lowerFindStr);
            } else if (lowerFindStr.endsWith(FilterSChar.END_WITH_CHAR_A)) {
                index = FilterSChar.filterA(searchEngine, index, lowerFindStr);
            } else if (lowerFindStr.endsWith(FilterINGChar.END_WITH_CHAR_ICKING)) {
                index = FilterINGChar.filterICKING(searchEngine, index, lowerFindStr);
            } else if (lowerFindStr.endsWith(FilterINGChar.END_WITH_CHAR_UING)) {
                index = FilterINGChar.filterUING(searchEngine, index, lowerFindStr);
            } else if (lowerFindStr.endsWith(FilterINGChar.END_WITH_CHAR_YING)) {
                index = FilterINGChar.filterYING(searchEngine, index, lowerFindStr);
            } else if (lowerFindStr.endsWith(FilterINGChar.END_WITH_CHAR_ING)) {
                index = FilterINGChar.filterING(searchEngine, index, lowerFindStr);
            } else if (lowerFindStr.endsWith(FilterEDChar.END_WITH_CHAR_IED)) {
                index = FilterEDChar.filterIED(searchEngine, index, lowerFindStr);
            } else if (lowerFindStr.endsWith(FilterEDChar.END_WITH_CHAR_ED)) {
                index = FilterEDChar.filterED(searchEngine, index, lowerFindStr);
            }
        }
        return index;
    }

}
