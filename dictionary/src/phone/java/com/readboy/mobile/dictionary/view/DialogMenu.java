package com.readboy.mobile.dictionary.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.readboy.Dictionary.R;
import com.readboy.mobile.dictionary.utils.MenuController;

/**
 * Created by 123 on 2016/8/8.
 */

public class DialogMenu extends DialogFragment {

    private MenuController menuController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.LocalDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getDialog() != null) {
            // 设置宽度为屏宽、靠近屏幕底部。
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
            window.setLayout(-1, -2);
        }
        return inflater.inflate(R.layout.layout_menu, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (menuController != null) {
            menuController.reset(view, this);
        }
    }

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

}
