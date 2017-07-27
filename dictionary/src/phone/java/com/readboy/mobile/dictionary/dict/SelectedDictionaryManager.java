package com.readboy.mobile.dictionary.dict;

import android.content.Context;

import com.readboy.Dictionary.R;
import com.readboy.library.search.DictWordSearch;

/**
 * Created by Senny on 2015/10/30.
 */
public class SelectedDictionaryManager {

    public static String[] getDictionaryLabelArray(Context context, int dictType) {
        String[] dictItems;
        if(dictType == DictWordSearch.DICT_TYPE_CHI) {
            dictItems = context.getResources().getStringArray(R.array.dict_select_chi_list);
        } else {
            dictItems = context.getResources().getStringArray(R.array.dict_select_eng_list);
        }
        return dictItems;
    }

    public static int getDictionaryId(Context context, int dictType, int index) {
        int[] selectDictIDs = null;
        if (dictType == DictWordSearch.DICT_TYPE_CHI) {
            selectDictIDs = context.getResources()
                    .getIntArray(R.array.dict_select_chi_id);
        } else if (dictType == DictWordSearch.DICT_TYPE_ENG) {
            selectDictIDs = context.getResources()
                    .getIntArray(R.array.dict_search_eng_id);
        }
        if (selectDictIDs != null && selectDictIDs.length > index && index >= 0) {
            return selectDictIDs[index];
        }
        return -1;
    }

}
