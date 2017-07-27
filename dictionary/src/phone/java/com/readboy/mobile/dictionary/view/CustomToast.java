package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Senny on 2015/12/10.
 */
public class CustomToast {

    private Toast toast;
    private static CustomToast instance;

    public static CustomToast getInstance(Context context) {
        if (instance == null) {
            instance = new CustomToast(context);
        }
        return instance;
    }

    private CustomToast(Context context) {
        toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
    }

    public void showToast(String str) {
        if (toast != null) {
            toast.setText(str);
            toast.show();
        }
    }

}
