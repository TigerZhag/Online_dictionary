package com.readboy.library.widget;

/**
 * Created by Black_Horse on 2016/7/11.
 */

public interface IParaser {
    void init(float mScreenWidth, float mScreenHeight);
    void setCharsParaseObtainer(ISpanParaseObtainer iObtainer);
    void start();

}
