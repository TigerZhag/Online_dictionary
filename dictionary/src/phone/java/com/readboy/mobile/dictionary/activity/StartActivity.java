package com.readboy.mobile.dictionary.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.readboy.Dictionary.R;
import com.readboy.HanziManager.HanziDataManager;
import com.readboy.depict.data.DictData;
import com.readboy.mobile.dictionary.fragment.DictHotWord2Fragment;
import com.readboy.mobile.dictionary.fragment.DictMainFragment;
import com.readboy.mobile.dictionary.fragment.DictRecordWordFragment;
import com.readboy.mobile.dictionary.fragment.DictSearchFragment;
import com.readboy.mobile.dictionary.fragment.DictWordItemFragment;
import com.readboy.mobile.dictionary.fragment.SoundAnimationFragment;
import com.readboy.mobile.dictionary.fragment.StartUpFragment;
import com.readboy.scantranslate.ScanFragment;

/**
 * Created by 123 on 2016/8/22.
 */

public class StartActivity extends FragmentActivity {

    private DictMainFragment dictMainFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);
        dictMainFragment = new DictMainFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_main_content, dictMainFragment, DictMainFragment.class.getSimpleName());
        ft.commit();
        /*DictWordSearch dictWordSearch = new DictWordSearch();
        dictWordSearch.open(DictIOFile.ID_BHDICT, this, new HanziPackage.DataChangeListener() {
            @Override
            public void onDataChange(boolean b) {
                if (!b) {
                    finish();
                }
            }
        });
        dictWordSearch.close();*/

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }*/
    }

    @Override
    public void onBackPressed() {
        DictSearchFragment dictSearchFragment = (DictSearchFragment) getSupportFragmentManager().findFragmentByTag(DictSearchFragment.class.getSimpleName());
        if (dictSearchFragment != null && dictSearchFragment.dismissPopuWindow()) {
            return;
        }
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(count - 1);
            if (backStackEntry.getName().contentEquals(DictWordItemFragment.class.getSimpleName())
                    || backStackEntry.getName().contentEquals(DictHotWord2Fragment.class.getSimpleName())
                    || backStackEntry.getName().contentEquals(DictRecordWordFragment.class.getSimpleName())
                    || backStackEntry.getName().contentEquals(DictSearchFragment.class.getSimpleName())
                    || backStackEntry.getName().contentEquals(ScanFragment.class.getSimpleName())) {
                if (dictMainFragment != null) {
                    dictMainFragment.reLoadData();
                }
//                getSupportFragmentManager().popBackStackImmediate();
//                return;
            } else if (backStackEntry.getName().contentEquals(SoundAnimationFragment.class.getSimpleName())) {
                return;
            }
        }
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

}
