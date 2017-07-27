package com.readboy.mobile.dictionary.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.readboy.depict.ui.LoadingDialog;
import com.readboy.mobile.dictionary.utils.CheckHanziData;

/**
 * Created by 123 on 2016/8/23.
 */

public class BaseFragment extends Fragment {

    public static final String ACTION_LOADING_HANZI_DATA_FINISH = "loading_hanzi_data_finish";

    private LoadingDialog mLoadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showLoadingDialog(Context context) {
        if (mLoadingDialog == null
                && context != null) {
            mLoadingDialog = new LoadingDialog(context);
        }
        if (mLoadingDialog != null
                && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null
                && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    protected boolean isNeedUpdateHanziData() {
        if (CheckHanziData.getInstance() != null) {
            return CheckHanziData.getInstance().isNeedUpdate();
        }
        return false;
    }

}
