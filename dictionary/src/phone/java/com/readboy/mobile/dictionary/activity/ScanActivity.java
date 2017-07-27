package com.readboy.mobile.dictionary.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.readboy.Dictionary.R;
import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.fragment.DictSearchFragment;
import com.readboy.scantranslate.ScanFragment;

/**
 * Created by 123 on 2016/8/18.
 */

public class ScanActivity extends FragmentActivity {

    public final static String ACTION_TO_EXIT = "exit";

    private DictSearchFragment dictSearchFragment;
    private ScanFragment scanFragment;
    private boolean isExitFakeAnim = false;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action != null) {
                    if (action.contentEquals(ACTION_TO_EXIT)) {
                        isExitFakeAnim = true;
                        finish();
                    }
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_acitivity_scan);
        if (getSupportFragmentManager().findFragmentByTag(ScanFragment.class.getSimpleName()) == null) {
            scanFragment = new ScanFragment();
        } else {
            scanFragment = (ScanFragment) getSupportFragmentManager().findFragmentByTag(ScanFragment.class.getSimpleName());
        }

        scanFragment.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDictSearchFragment(DictSearchFragment.STATE_VIEW_SCAN_SEARCH, scanFragment.getWordInfo());
            }
        });
        scanFragment.setExitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        DictManagerControler.replaceFragment(getSupportFragmentManager().beginTransaction(),
                R.id.activity_main_content,
                ScanFragment.class.getSimpleName(),
                ScanFragment.class.getSimpleName(),
                scanFragment);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TO_EXIT);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    private void addDictSearchFragment(int state, PlugWorldInfo plugWorldInfo) {
        if (scanFragment != null) {
            scanFragment.lightOff();
        }
        dictSearchFragment = new DictSearchFragment();
        dictSearchFragment.setWoid(plugWorldInfo);
        dictSearchFragment.setState(state);
        DictManagerControler.addFragmentWithAnim(getSupportFragmentManager().beginTransaction(),
                R.id.activity_main_content,
                DictSearchFragment.class.getSimpleName(),
                DictSearchFragment.class.getSimpleName(),
                dictSearchFragment, R.anim.right_in, 0, 0, R.anim.right_out);
        /*Intent intent = new Intent(this, ShowDataActivity.class);
        intent.putExtra(ShowDataActivity.EXTRA_STATE, state);
        intent.putExtra(ShowDataActivity.EXTRA_DICT_ID, plugWorldInfo.dictID);
        intent.putExtra(ShowDataActivity.EXTRA_WORLD_INDEX, plugWorldInfo.index);
        intent.putExtra(ShowDataActivity.EXTRA_WORLD, plugWorldInfo.world);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, 0);*/
    }

    @Override
    public void finish() {
        super.finish();
        if (isExitFakeAnim) {
//            overridePendingTransition(0, R.anim.fake_anim);
        } else {
            overridePendingTransition(0, R.anim.right_out);
        }
    }
}
