package com.readboy.Dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.readboy.library.io.DictFile;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.utils.DelayClick;
import com.readboy.offline.fragment.BaseDictFragment;
import com.readboy.offline.fragment.DictSearchBiHuaFragment;
import com.readboy.offline.fragment.DictSearchFragment;

/**
 * Created by Senny on 2016/6/27.
 */
public class DictSearchActivity extends BaseActivity implements BaseDictFragment.OnDictFragmentListener {

    private final String SEARCH_KEY		= "dict_key";
    private final String SEARCH_ID		= "dict_id";

    private static final int UPDATE_FINISH_HANZI_DATA = 0x101;
    private static final int UPDATE_FAILD_HANZI_DATA = 0x102;

    /**
     * 是不是第一次加载
     */
    private boolean isFirstLoaded = true;
    private DelayClick delayClick = null;


    private boolean isNewDict = false;

    private String mKeyWord;
    private int mDictID;
    private int currentFragmentID = -1;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_dict_app);
        delayClick = new DelayClick();
        reset();
    }

    @Override
    public void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
        setIntent(intent);
        isNewDict = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment fragment = getSupportFragmentManager().findFragmentById(BaseDictFragment.FRAGMENT_NEW_DICT_SEARCH);
        if (isNewDict) {
            isNewDict = false;
            reset();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (fragment != null && fragment instanceof DictSearchBiHuaFragment) {
                    ((DictSearchBiHuaFragment) fragment).onResumeView();
                }
            }
        }
    }

    private void reset() {
        Intent intent = getIntent();
        if (intent != null) {
            String mSearchKey = intent.getStringExtra(SEARCH_KEY);
            int mSearchId = intent.getIntExtra(SEARCH_ID, -1);
            if (mSearchKey != null && mSearchId > -1) {
                DictWordSearch dictWordSearch = new DictWordSearch();
                if (mSearchId == DictFile.ID_BHDICT && dictWordSearch.open(mSearchId, this, null)) {

                } else if (mSearchId != DictFile.ID_BHDICT && dictWordSearch.open(mSearchId, this, null)) {
                    int mKeyIndex =  dictWordSearch.getSearchKeyIndex(mSearchKey);
                    if (mKeyIndex == -1) {
                        dictWordSearch.close();
                        Toast.makeText(this, R.string.select_search_failed, Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.select_search_failed), Toast.LENGTH_SHORT).show();
                    finish();
                }
                while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStackImmediate();
                }
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                /*if (getSupportFragmentManager().findFragmentByTag(BaseDictFragment.FRAGMENT_TAG_NEW_DICT_SEARCH) != null) {
                    ft.remove(getSupportFragmentManager().findFragmentByTag(BaseDictFragment.FRAGMENT_TAG_NEW_DICT_SEARCH));
                }
                if (getSupportFragmentManager().findFragmentByTag(BaseDictFragment.FRAGMENT_TAG_DICT_SEARCH) != null) {
                    ft.remove(getSupportFragmentManager().findFragmentByTag(BaseDictFragment.FRAGMENT_TAG_DICT_SEARCH));
                }*/
                if (mSearchId == DictFile.ID_BHDICT) {
                    DictSearchBiHuaFragment dictSearchBiHuaFragment = new DictSearchBiHuaFragment();
                    dictSearchBiHuaFragment.setOnDictFragmentListener(this);
                    dictSearchBiHuaFragment.setKeyWord(mSearchKey);
                    ft.replace(BaseDictFragment.FRAGMENT_NEW_DICT_SEARCH, dictSearchBiHuaFragment, BaseDictFragment.FRAGMENT_TAG_NEW_DICT_SEARCH);
                } else {
                    DictSearchFragment dictSearchFragment = new DictSearchFragment();
                    dictSearchFragment.setOnDictFragmentListener(this);
                    dictSearchFragment.setSearchInfo(mSearchId, mSearchKey);
                    ft.setCustomAnimations(R.anim.non_animation, 0, 0, 0);
                    ft.replace(BaseDictFragment.FRAGMENT_NEW_DICT_SEARCH, dictSearchFragment, BaseDictFragment.FRAGMENT_TAG_NEW_DICT_SEARCH);
                }
                ft.commit();
                dictWordSearch.close();
                return;
            }
        }
        Toast.makeText(this, getResources().getString(R.string.select_search_failed), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public BaseDictFragment showFragment(String showFragmentTag, int fragmentID, int dictID, String str) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(fragmentID);
        if (fragment != null) {
            return null;
        }
        BaseDictFragment addFragment = null;
        switch (fragmentID) {
            case BaseDictFragment.FRAGMENT_DICT_SEARCH:
                addFragment = new DictSearchFragment();
                ((DictSearchFragment)addFragment).setSearchInfo(dictID, str);
                break;
        }
        if (addFragment != null) {
            addFragment.setOnDictFragmentListener(this);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.push_right_in, 0, 0, R.anim.push_right_out);
            ft.add(fragmentID, addFragment, showFragmentTag);
            ft.addToBackStack(null);
            ft.commit();
            currentFragmentID = fragmentID;
        }
        return addFragment;
    }

    @Override
    public void attachFragment(int fragmentID) {
        if (fragmentID == BaseDictFragment.FRAGMENT_DICT_SEARCH) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(BaseDictFragment.FRAGMENT_NEW_DICT_SEARCH);
            if (fragment != null && fragment instanceof DictSearchBiHuaFragment) {
                ((BaseDictFragment) fragment).onPauseView();
            }
        }
    }

    @Override
    public void detachFragment(int fragmentID) {
        if (fragmentID == BaseDictFragment.FRAGMENT_DICT_SEARCH) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(BaseDictFragment.FRAGMENT_NEW_DICT_SEARCH);
            if (fragment != null && fragment instanceof DictSearchBiHuaFragment) {
                ((BaseDictFragment) fragment).onResumeView();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (delayClick == null) {
            return;
        }
        if (isFirstLoaded && !delayClick.canClickByTime(DelayClick.DELAY_500MS)) {
            isFirstLoaded = false;
            return;
        }
        super.onBackPressed();
    }
}
