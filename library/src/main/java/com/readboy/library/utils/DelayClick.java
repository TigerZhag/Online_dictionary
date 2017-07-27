package com.readboy.library.utils;

import android.os.SystemClock;

/**
 * 防止连续点击
 */
public class DelayClick {

	public static long DELAY_300MS = 300;
    public static long DELAY_500MS = 500;
    public static long DELAY_800MS = 800;
	private long lastClickTime = SystemClock.elapsedRealtime();
	
	public boolean canClickByTime(long delayTime){
        boolean ret = false;
        long now = SystemClock.elapsedRealtime();
        if((now - lastClickTime) > delayTime){
            lastClickTime = now;
            ret = true;
        }
        lastClickTime = now;
        return ret;
    }

}
