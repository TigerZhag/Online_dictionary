package com.readboy.translation;

import com.readboy.translation.bean.TransResult;

import java.util.List;

/**
 * @author Zhang shixin
 * @date 16-8-3.
 */
public interface OnSearchListener {
    void onSearchResult(List<TransResult> results);
}
