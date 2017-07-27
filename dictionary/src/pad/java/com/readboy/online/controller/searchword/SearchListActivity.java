package com.readboy.online.controller.searchword;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.online.controller.searchword.pinyin.PinYinSearchListFragment;
import com.readboy.online.controller.searchword.redical.RedicalSearchListFragment;
import com.readboy.online.controller.searchword.strokes.StroksSearchListFragment;

public class SearchListActivity extends Activity {

    /*   查字类型   */
    private static final int TO_PINYIN_SEARCH_LIST = 1;
    private static final int TO_STROKS_SEARCH_LIST = 2;
    private static final int TO_REDICAL_SEARCH_LIST = 3;
    private static final int TO_FINGER_SEARCH_LIST = 4;
    private int listType;
    /*   控件   */
    private TextView tvListTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        findViewById();
        initInfo();
    }

    private void findViewById(){
        tvListTitle = (TextView) findViewById(R.id.tv_search_list_title);
    }

    private void initInfo(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        listType = getIntent().getIntExtra("list_type",0);
        Log.i("zaza",listType+"");
        switch (listType){
            case TO_PINYIN_SEARCH_LIST:
                tvListTitle.setText("拼音查字");
                transaction.add(R.id.layout_display_search_list, new PinYinSearchListFragment()).commit();
                break;
            case TO_STROKS_SEARCH_LIST:
                tvListTitle.setText("笔画查字");
                transaction.add(R.id.layout_display_search_list, new StroksSearchListFragment()).commit();
                break;
            case TO_REDICAL_SEARCH_LIST:
                tvListTitle.setText("部首查字");
                transaction.add(R.id.layout_display_search_list, new RedicalSearchListFragment()).commit();
                break;
            case TO_FINGER_SEARCH_LIST:
                //手写的布局不一样.再说吧哎
                break;
        }
    }
}
