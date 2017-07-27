package com.readboy.Dictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.readboy.library.utils.DelayClick;
import com.readboy.library.utils.DictInfo;

/**
 * Created by Senny on 2016/6/23.
 */
public class DictActivity extends Activity implements View.OnClickListener {

    /*
        private GridBookView gridBookView = null;
    */
    private DelayClick delayClick = null;

    private int doubleClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gridbook);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (delayClick == null) {
            delayClick = new DelayClick();
        }
        doubleClickTime = 0;
    }

    @Override
    protected void onDestroy() {
        if (delayClick != null) {
            delayClick = null;
        }
        super.onDestroy();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFoused) {
        super.onWindowFocusChanged(hasWindowFocus());
    }

    @Override
    public void onClick(View v) {
        if (delayClick.canClickByTime(DelayClick.DELAY_500MS)) {
            int dictID = Integer.parseInt((String) v.getTag());
            Intent intent = new Intent();
            if (dictID == DictInfo.DICT_SHENGCI) {
                intent.setAction("com.readboy.Dictionary.NEWWORD");
            } else {
                /**
                 * 每本词典的旧入口
                 */
                intent.setAction("com.readboy.Dictionary.DICTAPP");
            }
            intent.putExtra("rbapk_extradata", dictID);// id为传入词典值
            startActivity(intent);
            overridePendingTransition(R.anim.push_right_in, R.anim.non_animation);
        }
    }

    @Override
    public void onBackPressed() {
        if (delayClick.canClickByTime(DelayClick.DELAY_500MS)) {
            super.onBackPressed();
        }

    }
}
