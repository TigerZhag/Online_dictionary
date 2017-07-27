package com.readboy.mobile.dictionary.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.readboy.Dictionary.R;
import com.readboy.mobile.dictionary.view.CustomToast;

/**
 * Created by 123 on 2016/7/15.
 */

public class MenuController implements View.OnClickListener, DialogInterface.OnShowListener{

    private IWordContentController controller;
    private PopupWindow popupWindow;
    private DialogFragment dialogFragment;

    private View enlargeTextSizeButton;
    private View reduceTextSizeButton;
    private View selectTextButton;
    private View selectTextButtonBg;

    public void setController(IWordContentController controller) {
        this.controller = controller;
    }

    public MenuController() {

    }

    public MenuController(View menuView, PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
        menuView.findViewById(R.id.menu_enlarge_text_size).setOnClickListener(this);
        menuView.findViewById(R.id.menu_reduce_text_size).setOnClickListener(this);
        menuView.findViewById(R.id.menu_select_text).setOnClickListener(this);
        menuView.findViewById(R.id.menu_cancel).setOnClickListener(this);
    }

    public void reset(View menuView, DialogFragment dialogFragment) {
        this.dialogFragment = dialogFragment;
        enlargeTextSizeButton = menuView.findViewById(R.id.menu_enlarge_text_size);
        enlargeTextSizeButton.setOnClickListener(this);
        reduceTextSizeButton = menuView.findViewById(R.id.menu_reduce_text_size);
        reduceTextSizeButton.setOnClickListener(this);
        selectTextButton = menuView.findViewById(R.id.menu_select_text);
        selectTextButton.setOnClickListener(this);
        selectTextButtonBg = menuView.findViewById(R.id.menu_select_text_bg);
        menuView.findViewById(R.id.menu_cancel).setOnClickListener(this);
        if (dialogFragment.getDialog() != null) {
            dialogFragment.getDialog().setOnShowListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (controller != null) {
            switch (v.getId()) {

                case R.id.menu_enlarge_text_size:
                    int enlargeTextSize = controller.setEnlargeTextSize();
                    /*reduceTextSizeButton.setEnabled(true);
                    if (reduceTextSizeButton instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup) reduceTextSizeButton;
                        for (int i = 0 ; viewGroup.getChildCount() > i ; i++) {
                            viewGroup.getChildAt(i).setEnabled(true);
                        }
                    }*/
                    break;

                case R.id.menu_reduce_text_size:
                    int reduceTextSize = controller.setReduceTextSize();
                    /*enlargeTextSizeButton.setEnabled(true);
                    if (enlargeTextSizeButton instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup) enlargeTextSizeButton;
                        for (int i = 0 ; viewGroup.getChildCount() > i ; i++) {
                            viewGroup.getChildAt(i).setEnabled(true);
                        }
                    }*/
                    break;

                case R.id.menu_select_text:
                    if (controller.setSelectTextState()) {
                        CustomToast.getInstance(v.getContext()).showToast(v.getContext().getString(R.string.tip_slected_world_is_open));
                    } else {
                        CustomToast.getInstance(v.getContext()).showToast(v.getContext().getString(R.string.tip_slected_world_is_close));
                    }
                    break;

                case R.id.menu_cancel:
                    if (dialogFragment != null) {
                        dialogFragment.dismiss();
                    }
                    break;
            }
        }
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (controller != null) {
            int textSize = controller.getTextSize();
            if (textSize == TextSizeManage.MAX_TEXT_SIZE) {
                enlargeTextSizeButton.setEnabled(false);
                if (enlargeTextSizeButton instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) enlargeTextSizeButton;
                    for (int i = 0 ; viewGroup.getChildCount() > i ; i++) {
                        viewGroup.getChildAt(i).setEnabled(false);
                    }
                }
            } else if (textSize == TextSizeManage.MIN_TEXT_SIZE) {
                reduceTextSizeButton.setEnabled(false);
                if (reduceTextSizeButton instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) reduceTextSizeButton;
                    for (int i = 0 ; viewGroup.getChildCount() > i ; i++) {
                        viewGroup.getChildAt(i).setEnabled(false);
                    }
                }
            }
            if (controller.getSelectTextState()) {
                selectTextButtonBg.setBackground(selectTextButtonBg.getResources().getDrawable(R.drawable.select_text_open_bg));
            } else {
                selectTextButtonBg.setBackground(selectTextButtonBg.getResources().getDrawable(R.drawable.select_text_close_bg));
            }
        }
    }
}
