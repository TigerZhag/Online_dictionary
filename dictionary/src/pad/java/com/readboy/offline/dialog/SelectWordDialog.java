package com.readboy.offline.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.readboy.Dictionary.R;
import com.readboy.library.search.DictWordSearch;

/**
 * Created by Administrator on 14-7-29.
 */
public class SelectWordDialog extends Dialog implements View.OnClickListener{

    private Context mContext = null;
    private TextView selectTextView = null;

    private Button selectDict1 = null;
    private Button selectDict2 = null;
    private Button selectDict3 = null;

    private String mSelectText = null;
    private int dictType = 0;

    public SelectWordDialog(Context context) {
        super(context);
    }

    public SelectWordDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    protected SelectWordDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init(Context mContext) {
        this.mContext = mContext;
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
        View v = View.inflate(this.mContext, R.layout.dialog_list, null);
        this.setContentView(v);

        selectTextView = (TextView) v.findViewById(R.id.list_title);
        selectTextView.setSelected(true);

        selectDict1 = (Button)v.findViewById(R.id.dialog_dict_1);
        selectDict2 = (Button)v.findViewById(R.id.dialog_dict_2);
        selectDict3 = (Button)v.findViewById(R.id.dialog_dict_3);
        selectDict1.setTag(0);
        selectDict2.setTag(1);
        selectDict3.setTag(2);
        selectDict1.setOnClickListener(this);
        selectDict2.setOnClickListener(this);
        selectDict3.setOnClickListener(this);

        if (mContext instanceof OnSelectWordDialogListener) {
            setOnSelectWordDialogListener((OnSelectWordDialogListener)mContext);
        }
    }

    public boolean showSelectText(String mSelectText, int dictType) {
        this.mSelectText = mSelectText;
        this.dictType = dictType;
        String[] dictItems = null;
//        if(this.dictType == DictWordSearch.DICT_TYPE_CHI) {
//            dictItems = mContext.getResources().getStringArray(R.array.dict_select_chi_list);
//        } else if(this.dictType == DictWordSearch.DICT_TYPE_ENG) {
//            dictItems = mContext.getResources().getStringArray(R.array.dict_select_eng_list);
//        } else {
//            return false;
//        }

        if(this.dictType == DictWordSearch.DICT_TYPE_CHI) {
            dictItems = mContext.getResources().getStringArray(R.array.dict_select_chi_list);
        } else {
            dictItems = mContext.getResources().getStringArray(R.array.dict_select_eng_list);
        }
        if (dictItems == null) {
            return false;
        }
        ((ScrollView)selectTextView.getParent()).scrollTo(0, 0);
        selectTextView.setText(mSelectText);
        selectDict1.setText(dictItems[0]);
        selectDict2.setText(dictItems[1]);
        selectDict3.setText(dictItems[2]);
        this.show();
        return true;
    }

    public boolean isSpaceString(String searchStr) {
        if (searchStr != null) {
            for (char c:searchStr.toCharArray()) {
                if (c != ' ') {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (this.dictType == -1) {
            Toast.makeText(this.getContext(), this.getContext().getResources().getString(R.string.select_search_failed), Toast.LENGTH_LONG).show();
            this.dismiss();
            return;
        }
        int[] selectDictIDs = null;
        if (this.dictType == DictWordSearch.DICT_TYPE_CHI) {
            selectDictIDs = mContext.getResources().getIntArray(R.array.dict_select_chi_id);
        } else if (this.dictType == DictWordSearch.DICT_TYPE_ENG) {
            selectDictIDs = mContext.getResources().getIntArray(R.array.dict_search_eng_id);
        }
        this.dismiss();
        if (selectWordDialog != null) {
            selectWordDialog.getSelectToNewDictionary(selectDictIDs[(Integer)v.getTag()], mSelectText);
        }

    }

    public OnSelectWordDialogListener selectWordDialog = null;
    public void setOnSelectWordDialogListener(OnSelectWordDialogListener selectWordDialog) {
        this.selectWordDialog = selectWordDialog;
    }
    public interface OnSelectWordDialogListener {
        void getSelectToNewDictionary(int mDictID, String searchword);
    }
}
