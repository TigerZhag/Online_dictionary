package com.readboy.library.quicksearch.filter;

import com.readboy.library.quicksearch.SearchEngine;

/**
 * Created by 123 on 2016/9/8.
 */

public class FilterINGChar extends BaseFilter {

    public final static String END_WITH_CHAR_ICKING = "icking";
    public final static String END_WITH_CHAR_ING = "ing";
    public final static String END_WITH_CHAR_UING = "uing";
    public final static String END_WITH_CHAR_YING = "ying";

    public static int filterICKING(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_ICKING.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_ICKING.length());
                cutFindStr += "ic";
                int newIndex = searchEngine.find(cutFindStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && cutFindStr.contentEquals(lowerNewKey)) {
                    return newIndex;
                }
                index = filterING(searchEngine, index, lowerFindStr);
            }
        }
        return index;
    }

    public static int filterUING(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_UING.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_UING.length());
                cutFindStr += "ue";
                int newIndex = searchEngine.find(cutFindStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && cutFindStr.contentEquals(lowerNewKey)) {
                    return newIndex;
                }
                index = filterING(searchEngine, index, lowerFindStr);
            }
        }
        return index;
    }

    public static int filterYING(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_YING.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_YING.length());
                cutFindStr += "ie";
                int newIndex = searchEngine.find(cutFindStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && cutFindStr.contentEquals(lowerNewKey)) {
                    return newIndex;
                }
                index = filterING(searchEngine, index, lowerFindStr);
            }
        }
        return index;
    }

    public static int filterING(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_ING.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_ING.length());
                String consonantStr = cutFindStr;
                if (consonantStr.length() > 1 && !isConsonant(consonantStr.charAt(consonantStr.length() - 1))) {
                    consonantStr += "e";
                }
                int newIndex = searchEngine.find(consonantStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && (consonantStr).contentEquals(lowerNewKey)) {
                    return newIndex;
                }
                int cutFindStrLenght = cutFindStr.length();
                if (cutFindStrLenght > 2
                        && cutFindStr.charAt(cutFindStrLenght - 2) == cutFindStr.charAt(cutFindStrLenght - 1)) {
                    String doubleCutFindStr = cutFindStr.substring(0, cutFindStrLenght - 1);
                    newIndex = searchEngine.find(doubleCutFindStr);
                    newKey = searchEngine.getWord(newIndex);
                    lowerNewKey = getFilterEnglishLowerLetter(newKey);
                    if (lowerNewKey != null && (doubleCutFindStr).contentEquals(lowerNewKey)) {
                        return newIndex;
                    }
                }
//                cutFindStr += "e";
                newIndex = searchEngine.find(cutFindStr);
                newKey = searchEngine.getWord(newIndex);
                lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && (cutFindStr).contentEquals(lowerNewKey)) {
                    return newIndex;
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
