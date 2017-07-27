package com.readboy.mobile.dictionary.utils;

import com.readboy.mobile.dictionary.AppConfigInfo;

/**
 * Created by Senny on 2015/11/3.
 */
public class TextSizeManage {

    private final static int DEFAULT_TEXT_SIZE = (int) (30 * AppConfigInfo.RATIO_DENSITY);
//    private final static int OFFSET_TEXT_SIZE = 5;
    public final static int MAX_TEXT_SIZE = (int) (36 * AppConfigInfo.RATIO_DENSITY);
    public final static int MIN_TEXT_SIZE = (int) (26 * AppConfigInfo.RATIO_DENSITY);
    public final static int OFFSET_SIZE = (int) (25 * AppConfigInfo.RATIO_DENSITY);

    private int currentTextSize = DEFAULT_TEXT_SIZE;

    public int getCurrentTextSize() {
        return currentTextSize;
    }

    public void setCurrentTextSize(int currentTextSize) {
        this.currentTextSize = currentTextSize;
    }

    public int getFirstLineTextSize() {
        return currentTextSize + OFFSET_SIZE;
    }

    public void enlargeTextSize() {
        if (MAX_TEXT_SIZE > currentTextSize) {
            if (currentTextSize == DEFAULT_TEXT_SIZE) {
                currentTextSize = MAX_TEXT_SIZE;
            } else {
                currentTextSize = DEFAULT_TEXT_SIZE;
            }
        }
    }

    public void reduceTextSize() {
        if (currentTextSize > MIN_TEXT_SIZE) {
            if (currentTextSize == DEFAULT_TEXT_SIZE) {
                currentTextSize = MIN_TEXT_SIZE;
            } else {
                currentTextSize = DEFAULT_TEXT_SIZE;
            }
        }
    }

}
