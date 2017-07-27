package com.readboy.offline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.readboy.library.io.DictFile;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.utils.PhoneticUtils;
import com.readboy.offline.adapter.BaseDictListAdapter;

import java.util.ArrayList;
import java.util.List;


public class DictListView extends ListView implements OnScrollListener{

    private static final int LIST_ADD_ITEMS_ONCE_MAX = 300;
    /* 字典列表添加模式 */
    private static final int LIST_LOAD_MODE_BEHIND = 0;	// 往后添加
    private static final int LIST_LOAD_MODE_FRONT = 1;	// 往前添加
    private static final int LIST_LOAD_MODE_RESET = 2;	// 重置

    private int mDispStart;
    private int mDispCount;

    private int mKeycount = 0;
    private DictWordSearch mDictWordSearch = null;

    public DictListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public DictListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public DictListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    /**
     * 初始化必须调用
     * @param mDictWordSearch
     */
    public void init(DictWordSearch mDictWordSearch) {
        this.mDictWordSearch = mDictWordSearch;
        if (mDictWordSearch != null) {
            mKeycount = mDictWordSearch.getAllKeyCount();
        }
        DictListView.this.setOnScrollListener(DictListView.this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            onListItemCacheAddFirstMethod();
        }
        if (dictListOnScrollListener != null) {
            dictListOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        mDispStart = firstVisibleItem;
        mDispCount = visibleItemCount;
        if (dictListOnScrollListener != null) {
            dictListOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);;
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null && adapter instanceof BaseDictListAdapter) {
            addListItems(0, LIST_ADD_ITEMS_ONCE_MAX,LIST_LOAD_MODE_RESET);
        }
    }

    public void setKeySelectByTextWathcer(int keyIndex) {
        BaseDictListAdapter baseDictListAdapter = (BaseDictListAdapter)getAdapter();
        if (baseDictListAdapter != null) {
            if(keyIndex < LIST_ADD_ITEMS_ONCE_MAX/2) {
                addListItems(0, LIST_ADD_ITEMS_ONCE_MAX, LIST_LOAD_MODE_RESET);
                baseDictListAdapter.setSelection(keyIndex);
                this.setSelection(keyIndex);
            } else if(mKeycount - keyIndex < LIST_ADD_ITEMS_ONCE_MAX/2) {
                addListItems(keyIndex-LIST_ADD_ITEMS_ONCE_MAX/2,
                        mKeycount - keyIndex+LIST_ADD_ITEMS_ONCE_MAX/2,
                        LIST_LOAD_MODE_RESET);
                baseDictListAdapter.setSelection(LIST_ADD_ITEMS_ONCE_MAX/2);
                this.setSelection(LIST_ADD_ITEMS_ONCE_MAX/2);
            } else {
                addListItems(keyIndex-LIST_ADD_ITEMS_ONCE_MAX/2, LIST_ADD_ITEMS_ONCE_MAX, LIST_LOAD_MODE_RESET);
                baseDictListAdapter.setSelection(LIST_ADD_ITEMS_ONCE_MAX/2);
                this.setSelection(LIST_ADD_ITEMS_ONCE_MAX/2);
            }
        }
    }

    private void onListItemCacheAddFirstMethod(){
        BaseDictListAdapter baseDictListAdapter = (BaseDictListAdapter)getAdapter();
        if (baseDictListAdapter != null) {
            int start = baseDictListAdapter.getStartId();
            int total = baseDictListAdapter.getCount();
            if(start > 0 && 2*mDispStart < total) {	// 往前添加
                if(start >= LIST_ADD_ITEMS_ONCE_MAX) {
                    addListItems(start-LIST_ADD_ITEMS_ONCE_MAX,LIST_ADD_ITEMS_ONCE_MAX,LIST_LOAD_MODE_FRONT);
                } else {
                    addListItems(0,start,LIST_LOAD_MODE_FRONT);
                }
            } else {	// 往后添加
                if(start + total + LIST_ADD_ITEMS_ONCE_MAX <= mKeycount) {
                    addListItems(start+total,LIST_ADD_ITEMS_ONCE_MAX,LIST_LOAD_MODE_BEHIND);
                } else if(start + total < mKeycount) {
                    addListItems(start+total,mKeycount - (start + total),LIST_LOAD_MODE_BEHIND);
                }
            }
        }
    }

    /*
     * mode: 0:往后添加	1:往前添加	2:重置
     */
    private void addListItems(int start, int cnt,int mode) {
        BaseDictListAdapter baseDictListAdapter = (BaseDictListAdapter)getAdapter();
        if (baseDictListAdapter != null) {
            List<String> list = new ArrayList<String>();
            if (mDictWordSearch.getDictId() != DictFile.ID_BHDICT) {
                String itemText;
                for(int i = 0;i < cnt;i++) {
//                itemText = mDictWordSearch.dictFileGetDictKeyByAddressNumber(start+i);
                    itemText = mDictWordSearch.getKeyWord(start+i);
                    itemText = PhoneticUtils.checkString(itemText);
                    list.add(itemText);
                }
            } else {
                list = mDictWordSearch.getHanziWordList(start, start+cnt-1);
            }
            if(mode == 2) {
                baseDictListAdapter.setListItems(list, start);
                baseDictListAdapter.notifyDataSetChanged();
            }else if(mode == 1) {
                baseDictListAdapter.addListItems(list,start);
                baseDictListAdapter.setSelection(baseDictListAdapter.getSelection()+cnt);
                baseDictListAdapter.notifyDataSetChanged();
                int firstVisibleItem = DictListView.this.getFirstVisiblePosition();
                int selection = firstVisibleItem + cnt;
                View v = DictListView.this.getChildAt(0);
                int top = (v == null) ? 0 : v.getTop();
                DictListView.this.invalidate();
                DictListView.this.setSelectionFromTop(selection, top);
            } else {
                baseDictListAdapter.addListItems(list,start);
                baseDictListAdapter.notifyDataSetChanged();
            }
        }

    }

    private DictListOnScrollListener dictListOnScrollListener = null;
    public void setDictListOnScrollListener(DictListOnScrollListener dictListOnScrollListener) {
        this.dictListOnScrollListener = dictListOnScrollListener;
    }
    public interface DictListOnScrollListener {
        public void onScrollStateChanged(AbsListView view, int scrollState);
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount);
    }
}
