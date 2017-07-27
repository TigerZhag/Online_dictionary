package com.readboy.mobile.dictionary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.readboy.Dictionary.R;
import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.fragment.DictSearchFragment;

/**
 * Created by 123 on 2016/8/24.
 */

public class ShowDataActivity extends FragmentActivity {

    public static final String EXTRA_BUNDLE = "bundle";
    public static final String EXTRA_DICT_ID = "dict_id";
    public static final String EXTRA_WORLD = "world";
    public static final String EXTRA_WORLD_INDEX = "world_index";
    public static final String EXTRA_STATE = "state";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_acitivity_show_data);

        PlugWorldInfo plugWorldInfo = null;
        int state = -1;
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra(EXTRA_BUNDLE);
            if (bundle != null) {
                plugWorldInfo = new PlugWorldInfo();
                plugWorldInfo.dictID = bundle.getInt(EXTRA_DICT_ID, -1);
                plugWorldInfo.world = bundle.getString(EXTRA_WORLD);
                plugWorldInfo.index = bundle.getInt(EXTRA_WORLD_INDEX, -1);
            }
            state = intent.getIntExtra(EXTRA_STATE, -1);
        }
        DictSearchFragment dictSearchFragment = new DictSearchFragment();
        dictSearchFragment.setWoid(plugWorldInfo);
        if (state != -1) {
            dictSearchFragment.setState(state);
        }
        DictManagerControler.replaceFragment(getSupportFragmentManager().beginTransaction(),
                R.id.activity_main_content,
                DictSearchFragment.class.getSimpleName(),
                DictSearchFragment.class.getSimpleName(),
                dictSearchFragment);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.right_out);
    }
}
