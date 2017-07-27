package com.readboy.library.quicksearch.filter;

import com.readboy.library.quicksearch.SearchEngine;

/**
 * Created by 123 on 2016/9/7.
 */

class FilterEDChar extends BaseFilter {

    public final static String END_WITH_CHAR_ED = "ed";
    public final static String END_WITH_CHAR_IED = "ied";

    public static int filterIED(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_IED.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_IED.length());
                cutFindStr += "y";
                int newIndex = searchEngine.find(cutFindStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && cutFindStr.contentEquals(lowerNewKey)) {
                    return newIndex;
                }
                index = filterED(searchEngine, index, lowerFindStr);
            }
        }
        return index;
    }

    public static int filterED(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_ED.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_ED.length());
                cutFindStr += "e";
                int newIndex = searchEngine.find(cutFindStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && cutFindStr.contentEquals(lowerNewKey)) {
                    return newIndex;
                }
                cutFindStr = cutFindStr.substring(0, cutFindStr.length() - 1);
                newIndex = searchEngine.find(cutFindStr);
                newKey = searchEngine.getWord(newIndex);
                lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && cutFindStr.contentEquals(lowerNewKey)) {
                    return newIndex;
                }
                int cutFindStrLength = cutFindStr.length();
                if (cutFindStrLength > 2
                        && cutFindStr.charAt(cutFindStrLength - 2) == cutFindStr.charAt(cutFindStrLength - 1)) {
                    cutFindStr = cutFindStr.substring(0, cutFindStr.length() - 1);
                    newIndex = searchEngine.find(cutFindStr);
                    newKey = searchEngine.getWord(newIndex);
                    lowerNewKey = getFilterEnglishLowerLetter(newKey);
                    if (lowerNewKey != null && cutFindStr.contentEquals(lowerNewKey)) {
                        return newIndex;
                    }
                }
            }
        }
        return index;
    }

    public static int filter(SearchEngine searchEngine, int index, String lowerFindStr, String filterStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > filterStr.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                lowerFindStr = lowerFindStr.substring(0, lowerFindStr.length() - filterStr.length());
                int newIndex = searchEngine.find(lowerFindStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && lowerFindStr.contentEquals(lowerNewKey)) {
                    return newIndex;
                }
            }
        }
        return index;
    }



}
