package com.readboy.mobile.dictionary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.readboy.Dictionary.R;

/**
 * Created by Senny on 2015/10/30.
 */
public class SelectDictionaryDialog implements View.OnClickListener, PopupWindow.OnDismissListener{

    private PopupWindow popupWindow;
    private ViewGroup parentContainer;
    private OnSelectedItemListener listener;
    private int offsetX;
    private int offsetY;
    private int rootViewWidth;

    public void setOnSelectedItemListener(OnSelectedItemListener listener) {
        this.listener = listener;
    }

    public SelectDictionaryDialog(Context context, ViewGroup container) {
        parentContainer = container;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_popu_select_dictionary, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        popupWindow.setOnDismissListener(this);
        view.findViewById(R.id.popu_select_dictionary_button_1).setOnClickListener(this);
        view.findViewById(R.id.popu_select_dictionary_button_2).setOnClickListener(this);
        view.findViewById(R.id.popu_select_dictionary_button_3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int itemIndex = -1;
        switch (v.getId()) {

            case R.id.popu_select_dictionary_button_1:
                itemIndex = 0;
                break;

            case R.id.popu_select_dictionary_button_2:
                itemIndex = 1;
                break;

            case R.id.popu_select_dictionary_button_3:
                itemIndex = 2;
                break;

        }
        if (listener != null) {
            listener.onSelected(itemIndex);
        }
        dismiss();

    }

    public void showDialog(int[] position, String[] items, boolean isUp) {
        if (popupWindow != null && popupWindow.getContentView() instanceof TrangleLinearLayout) {
            TrangleLinearLayout trangleLinearLayout = (TrangleLinearLayout) popupWindow.getContentView();
            trangleLinearLayout.setDirection(isUp);
            trangleLinearLayout.setTrangleEnable(true);
        }
        showDialog(position, items);
    }

    public void showDialog(int gravity, int[] position, String[] items, boolean isUp) {
        if (popupWindow != null && popupWindow.getContentView() instanceof TrangleLinearLayout) {
            TrangleLinearLayout trangleLinearLayout = (TrangleLinearLayout) popupWindow.getContentView();
            trangleLinearLayout.setDirection(isUp);
            trangleLinearLayout.setTrangleEnable(true);
        }
        showDialog(gravity, position, items);
    }

    public void showDialog(int[] position, String[] items) {
        /*if (parentContainer != null &&
                popupWindow != null &&
                items != null &&
                items.length == 3) {
            computeOffsetXY();
            if (!popupWindow.isShowing()) {
                ((TextView) popupWindow.getContentView()
                        .findViewById(R.id.popu_select_dictionary_button_1)).setText(items[0]);
                ((TextView) popupWindow.getContentView()
                        .findViewById(R.id.popu_select_dictionary_button_2)).setText(items[1]);
                ((TextView) popupWindow.getContentView()
                        .findViewById(R.id.popu_select_dictionary_button_3)).setText(items[2]);
                popupWindow.showAtLocation(parentContainer,
                        Gravity.NO_GRAVITY, parentContainer.getLeft() + position[0], offsetY + position[1]);
            }
        }*/
        showDialog(Gravity.NO_GRAVITY, position, items);
    }

    public void showDialog(int gravity, int[] position, String[] items) {
        if (parentContainer != null &&
                popupWindow != null &&
                items != null &&
                items.length == 3) {
            computeOffsetXY();
            if (!popupWindow.isShowing()) {
                ((TextView) popupWindow.getContentView()
                        .findViewById(R.id.popu_select_dictionary_button_1)).setText(items[0]);
                ((TextView) popupWindow.getContentView()
                        .findViewById(R.id.popu_select_dictionary_button_2)).setText(items[1]);
                ((TextView) popupWindow.getContentView()
                        .findViewById(R.id.popu_select_dictionary_button_3)).setText(items[2]);
                int contentViewHeight = popupWindow.getContentView().getHeight();
                int contentViewWidth = popupWindow.getContentView().getWidth();
                if (contentViewHeight == 0 || contentViewWidth == 0) {
                    popupWindow.getContentView().measure(parentContainer.getWidth(), parentContainer.getHeight());
                    contentViewHeight = popupWindow.getContentView().getMeasuredHeight();
                    contentViewWidth = popupWindow.getContentView().getMeasuredWidth();
                }
//                int x = parentContainer.getLeft() + position[0];
                int x = (rootViewWidth - contentViewWidth) / 2;
                if (gravity == Gravity.TOP) {
                    popupWindow.showAtLocation(parentContainer,
                            Gravity.NO_GRAVITY, x,
                            offsetY + position[1] - contentViewHeight);
                } else {
                    popupWindow.showAtLocation(parentContainer,
                            Gravity.NO_GRAVITY, x, offsetY + position[1]);
                }
            }
        }
    }

    private void computeOffsetXY() {
        ViewGroup superViewGroup = parentContainer;
        if (superViewGroup != null) {
            offsetX = 0;
            offsetY = 0;
            do {
                offsetX += superViewGroup.getLeft() + superViewGroup.getPaddingLeft();
                offsetY += superViewGroup.getTop() + superViewGroup.getPaddingTop();
                rootViewWidth = superViewGroup.getWidth();
                if (superViewGroup.getParent() instanceof ViewGroup) {
                    superViewGroup = (ViewGroup) superViewGroup.getParent();
                } else {
                    superViewGroup = null;
                }
            } while (superViewGroup != null);
        }
    }

    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onDismiss() {
        if (popupWindow != null && popupWindow.getContentView() instanceof TrangleLinearLayout) {
            TrangleLinearLayout trangleLinearLayout = (TrangleLinearLayout) popupWindow.getContentView();
            trangleLinearLayout.setTrangleEnable(false);
        }
        if (listener != null) {
            listener.dismiss();
        }
    }

    public interface OnSelectedItemListener {
        void onSelected(int itemIndex);
        void dismiss();
    }

}
