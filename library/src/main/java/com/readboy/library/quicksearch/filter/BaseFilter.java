package com.readboy.library.quicksearch.filter;

/**
 * Created by 123 on 2016/9/7.
 */

class BaseFilter {

    static String getFilterEnglishLowerLetter(String str) {
        String result = "";
        if (str != null) {
            for (char c : str.toCharArray()) {
                char rc = getLowerEnglishChar(c);
                if (rc != ' ') {
                    result += rc;
                }
            }
        }
        return result;
    }

    static char getLowerEnglishChar(char c) {
        if (c >= 'a' && 'z' >= c) {
            return c;
        } else if (c >= 'A' && 'Z' >= c) {
            c += 32;// 转为小写字母
            return c;
        }
        return ' ';
    }

    static boolean isConsonant(char c) {
        if (c == 'a'
                || c == 'e'
                || c == 'i'
                || c == 'o'
                || c == 'u' ) {
            return true;
        }
        return false;
    }

}
