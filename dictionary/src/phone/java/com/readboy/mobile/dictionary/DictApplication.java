package com.readboy.mobile.dictionary;

import android.app.Application;

import com.readboy.mobile.dictionary.utils.CheckHanziData;
import com.readboy.translation.Translator;

/**
 * Created by Senny on 2016/5/14.
 */
public class DictApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfigInfo.DENSITYDPI = getResources().getDisplayMetrics().densityDpi;
        AppConfigInfo.RATIO_DENSITY = (AppConfigInfo.DENSITYDPI + 0.0f) / AppConfigInfo.DEFAULT_DENSITYDPI;
        CheckHanziData.init(this);

        Translator.init(this);
    }
}
