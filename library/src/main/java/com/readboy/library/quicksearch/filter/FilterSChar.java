package com.readboy.library.quicksearch.filter;

import com.readboy.library.quicksearch.SearchEngine;

/**
 * Created by 123 on 2016/9/7.
 */

class FilterSChar extends BaseFilter{

    public final static String END_WITH_CHAR_S = "s";
    public final static String END_WITH_CHAR_ES = "es";
    public final static String END_WITH_CHAR_IES = "ies";
    public final static String END_WITH_CHAR_VES = "ves";
    public final static String END_WITH_CHAR_ICES = "ices";
    public final static String END_WITH_CHAR_I = "i";
    public final static String END_WITH_CHAR_A = "a";
    public final static String END_WITH_CHAR_ES_CHANGE_IS = "es";

    /*public static int filter(DictWordSearch dictWordSearch, int index, String lowerFindStr) {
        if (lowerFindStr != null) {
            if (lowerFindStr.endsWith(END_WITH_CHAR_IES)) {
                index = filterIES(dictWordSearch, index, lowerFindStr);
            } else if (lowerFindStr.endsWith(END_WITH_CHAR_ES)) {
                if (lowerFindStr.endsWith("s" + END_WITH_CHAR_ES) ||
                        lowerFindStr.endsWith("sh" + END_WITH_CHAR_ES) ||
                        lowerFindStr.endsWith("x" + END_WITH_CHAR_ES) ||
                        lowerFindStr.endsWith("o" + END_WITH_CHAR_ES)) {
                    index = filterES(dictWordSearch, index, lowerFindStr);
                } else {
                    index = filter(dictWordSearch, index, lowerFindStr, END_WITH_CHAR_S);
                }
            } else if (lowerFindStr.endsWith(END_WITH_CHAR_S)){
                index = filter(dictWordSearch, index, lowerFindStr, END_WITH_CHAR_S);
            }
        }
        return index;
    }*/

    public static int filterES(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_ES.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_ES.length());
                int newIndex = searchEngine.find(cutFindStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null) {
                    if (cutFindStr.contentEquals(lowerNewKey)) {
                        return newIndex;
                    } else {
                        index = filterESChangeIS(searchEngine, index, lowerFindStr);
                    }
                }
            }
        }
        return index;
    }

    public static int filterIES(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_IES.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_IES.length());
                cutFindStr += "y";
                int newIndex = searchEngine.find(cutFindStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null) {
                    if (cutFindStr.contentEquals(lowerNewKey)) {
                        return newIndex;
                    } else {
                        index = filterES(searchEngine, index, lowerFindStr);
                    }
                }
            }
        }
        return index;
    }

    public static int filterVES(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_VES.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_VES.length());
                int newIndex = searchEngine.find(cutFindStr + "f");
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && (cutFindStr + "f").contentEquals(lowerNewKey)) {
                    return newIndex;
                }
                newIndex = searchEngine.find(cutFindStr + "fe");
                newKey = searchEngine.getWord(newIndex);
                lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && (cutFindStr + "fe").contentEquals(lowerNewKey)) {
                    return newIndex;
                }
                index = filterES(searchEngine, index, lowerFindStr);
            }
        }
        return index;
    }

    public static int filterESChangeIS(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_ES_CHANGE_IS.length()) {
            String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_ES_CHANGE_IS.length());
            cutFindStr += "is";
            int newIndex = searchEngine.find(cutFindStr);
            String newKey = searchEngine.getWord(newIndex);
            String lowerNewKey = getFilterEnglishLowerLetter(newKey);
            if (lowerNewKey != null && cutFindStr.contentEquals(lowerNewKey)) {
                return newIndex;
            }
            index = filter(searchEngine, index, lowerFindStr, END_WITH_CHAR_S);
        }
        return index;
    }

    public static int filterI(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_I.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_I.length());
                cutFindStr += "us";
                int newIndex = searchEngine.find(cutFindStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && cutFindStr.contentEquals(lowerNewKey)) {
                    return newIndex;
                }
            }
        }
        return index;
    }

    public static int filterICES(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_ICES.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_ICES.length());
                cutFindStr += "ix";
                int newIndex = searchEngine.find(cutFindStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && cutFindStr.contentEquals(lowerNewKey)) {
                    return newIndex;
                }
                index = filterES(searchEngine, index, lowerFindStr);
            }
        }
        return index;
    }

    public static int filterA(SearchEngine searchEngine, int index, String lowerFindStr) {
        if (lowerFindStr.length() > 3 && lowerFindStr.length() > END_WITH_CHAR_A.length()) {
            String key = searchEngine.getWord(index);
            String lowerKey = getFilterEnglishLowerLetter(key);
            if (lowerKey != null && !lowerFindStr.contentEquals(lowerKey)) {
                String cutFindStr = lowerFindStr.substring(0, lowerFindStr.length() - END_WITH_CHAR_A.length());
                cutFindStr += "um";
                int newIndex = searchEngine.find(cutFindStr);
                String newKey = searchEngine.getWord(newIndex);
                String lowerNewKey = getFilterEnglishLowerLetter(newKey);
                if (lowerNewKey != null && cutFindStr.contentEquals(lowerNewKey)) {
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
