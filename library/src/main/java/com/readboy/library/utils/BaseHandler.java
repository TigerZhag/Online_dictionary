package com.readboy.library.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import java.lang.ref.WeakReference;

/**
 * Created by Sgc on 2015/6/16.
 */
public class BaseHandler<T> extends Handler {

    public final static String BUNDLE_DEFAULT_STRING_KEY = "default_key";
    public final static String BUNDLE_DEFAULT_PARCELABLEARRAY_KEY = "default_key";

    private WeakReference<T> mReference;

    public BaseHandler(T t) {
        mReference = new WeakReference<T>(t);
    }

    public WeakReference<T> getReference() {
        return mReference;
    }

    public void sendMessages(int what, Object obj, int arg1, int arg2, int delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        sendMessageDelayed(msg, delayMillis);
    }

    public void sendMessages(int what, String tag, Object obj, int arg1, int arg2, int delayMillis) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_DEFAULT_STRING_KEY, tag);
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.setData(bundle);
        sendMessageDelayed(msg, delayMillis);
    }

    public void sendMessages(int what, Parcelable[] parcelableArray, Object obj, int arg1, int arg2, int delayMillis) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(BUNDLE_DEFAULT_PARCELABLEARRAY_KEY, parcelableArray);
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.setData(bundle);
        sendMessageDelayed(msg, delayMillis);
    }
}
