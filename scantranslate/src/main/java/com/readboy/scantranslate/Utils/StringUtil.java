package com.readboy.scantranslate.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhang shixin
 * @date 16-7-15.
 */
public class StringUtil {
    public static String filterAlphabet(String alph) {
//        alph = alph.replaceAll("[^(A-Za-z'\\-)]", "");
        return alph;
    }

    private static final String TAG = "StringUtil";
    public static List<String> split(String text) {
        List<String> temp = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0 ; i < text.length() ; i++){
            if (Character.isLetter(text.charAt(i))){
                builder.append(text.charAt(i));
            }else {
                if (builder.length() > 0){
                    temp.add(builder.toString());
                    builder = new StringBuilder();
                }
                temp.add(text.charAt(i) + "");
            }
        }
        return temp;
    }
}
