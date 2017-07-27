package com.readboy.Dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.readboy.library.utils.DelayClick;
import com.readboy.offline.fragment.BaseDictFragment;
import com.readboy.offline.fragment.DictJumpFragment;
import com.readboy.offline.fragment.DictMainFragment;
import com.readboy.offline.fragment.DictSearchFragment;

/**
 * Created by Senny on 2016/7/2.
 */
public class DictAppActivity extends BaseActivity implements BaseDictFragment.OnDictFragmentListener {

    private boolean isNewDict = false;
    /**
     * 是不是第一次加载
     */
    private boolean isFirstLoaded = true;
    private DelayClick delayClick = null;
    private int currentFragmentID = BaseDictFragment.FRAGMENT_DICT_APP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict_app);

//        getSupportFragmentManager().addOnBackStackChangedListener(onBackStackChangedListener);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DictMainFragment dictApp = new DictMainFragment();
        dictApp.setOnDictFragmentListener(this);
        ft.add(BaseDictFragment.FRAGMENT_DICT_APP, dictApp, BaseDictFragment.FRAGMENT_TAG_DICT_APP);
        ft.commit();
        currentFragmentID = BaseDictFragment.FRAGMENT_DICT_APP;
        delayClick = new DelayClick();

//        Log.i("Dictionary", "DictFragment onCreate!");
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        isNewDict = true;
    }

    @Override
    protected void onResume() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(BaseDictFragment.FRAGMENT_DICT_APP);
        if (isNewDict) {
            isNewDict = false;
            BaseDictFragment dictJump = (BaseDictFragment)getSupportFragmentManager().findFragmentById(BaseDictFragment.FRAGMENT_DICT_JUMP);
            BaseDictFragment dictSearch = (BaseDictFragment)getSupportFragmentManager().findFragmentById(BaseDictFragment.FRAGMENT_DICT_SEARCH);
            if (dictJump != null) {
                dictJump.nonAnimation = true;
            }
            if (dictSearch != null) {
                dictSearch.nonAnimation = true;
            }
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
            if (fragment != null && fragment instanceof DictMainFragment) {
                ((DictMainFragment) fragment).onNewIntent(null);
                ((DictMainFragment) fragment).onResumeView();
            }
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (fragment != null && fragment instanceof DictMainFragment) {
                    ((DictMainFragment) fragment).onResumeView();
                }
            }
        }
        super.onResume();
//        Log.i("Dictionary", "DictFragment onResume!");
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.non_animation, R.anim.push_right_out);
    }

    @Override
    public void onDestroy() {
//        getSupportFragmentManager().removeOnBackStackChangedListener(onBackStackChangedListener);
//        DictWordSearch.clearDictCache();

        super.onDestroy();
//        Log.i("Dictionary", "DictFragment onDestroyed!");
    }

    @Override
    public BaseDictFragment showFragment(String showFragmentTag, int fragmentID, int dictID, String str) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(fragmentID);
        if (fragment != null) {
            return null;
        }
        BaseDictFragment addFragment = null;
        switch (fragmentID) {
            case BaseDictFragment.FRAGMENT_DICT_JUMP:
                addFragment = new DictJumpFragment();
                ((DictJumpFragment)addFragment).setJumpString(str);
                break;
            case BaseDictFragment.FRAGMENT_DICT_SEARCH:
                DictJumpFragment dictJump = (DictJumpFragment)getSupportFragmentManager().findFragmentById(BaseDictFragment.FRAGMENT_DICT_JUMP);
                if (dictJump != null) {
                    dictJump.setForceExit(true);
                    getSupportFragmentManager().popBackStack();
                }
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
        if (fragmentID == BaseDictFragment.FRAGMENT_DICT_JUMP || fragmentID == BaseDictFragment.FRAGMENT_DICT_SEARCH) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(BaseDictFragment.FRAGMENT_DICT_APP);
            if (fragment != null && fragment instanceof DictMainFragment) {
                ((BaseDictFragment) fragment).onPauseView();
            }
        }
    }

    @Override
    public void detachFragment(int fragmentID) {
        if (fragmentID == BaseDictFragment.FRAGMENT_DICT_SEARCH || (fragmentID == BaseDictFragment.FRAGMENT_DICT_JUMP &&
                currentFragmentID != BaseDictFragment.FRAGMENT_DICT_SEARCH)) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(BaseDictFragment.FRAGMENT_DICT_APP);
            if (fragment != null && fragment instanceof DictMainFragment) {
                ((BaseDictFragment) fragment).onResumeView();
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
