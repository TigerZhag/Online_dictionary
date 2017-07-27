package com.readboy.translation.utils;

/**
 * @author Zhang shixin
 * @date 16-8-6.
 */
public class StringUtil {
    public static String generateReg(String oringin){
        String reg = "_%";
        StringBuilder des = new StringBuilder(oringin);

        return des.toString();
    }

    public static String filterAlpha(String query) {
        return query.replaceAll("[^a-zA-Z'\\-]","");
    }

    public static String filterPureAlpha(String query){
        return query.replaceAll("[^a-zA-Z]","");
    }
}
