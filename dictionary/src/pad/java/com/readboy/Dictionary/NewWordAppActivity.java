package com.readboy.Dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.readboy.library.utils.DelayClick;
import com.readboy.offline.fragment.BaseDictFragment;
import com.readboy.offline.fragment.DictMainFragment;
import com.readboy.offline.fragment.DictSearchFragment;
import com.readboy.offline.fragment.NewWordFragment;

/**
 * Created by Administrator on 14-11-19.
 */
public class NewWordAppActivity extends BaseActivity implements BaseDictFragment.OnDictFragmentListener{

    /**
     * 是不是第一次加载
     */
    private boolean isFirstLoaded = true;
    private DelayClick delayClick = null;
    private int currentFragmentID = BaseDictFragment.FRAGMENT_NEW_WORD;

    private boolean isNewDict = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict_app);

//        getSupportFragmentManager().addOnBackStackChangedListener(onBackStackChangedListener);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        NewWordFragment newWord = new NewWordFragment();
        newWord.setOnDictFragmentListener(this);
        ft.add(BaseDictFragment.FRAGMENT_NEW_WORD, newWord, BaseDictFragment.FRAGMENT_TAG_NEW_WORD);
        ft.commit();
        currentFragmentID = BaseDictFragment.FRAGMENT_NEW_WORD;
        delayClick = new DelayClick();

        /*IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeKeyEventReceiver, intentFilter);*/
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
        if (isNewDict) {
            isNewDict = false;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(BaseDictFragment.FRAGMENT_TAG_NEW_WORD);
            if (fragment != null) {
                ft.remove(fragment);
            }
            NewWordFragment newWord = new NewWordFragment();
            newWord.setOnDictFragmentListener(this);
            ft.add(BaseDictFragment.FRAGMENT_NEW_WORD, newWord, BaseDictFragment.FRAGMENT_TAG_NEW_WORD);
            ft.commit();
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.non_animation, R.anim.push_right_out);
    }

    @Override
    public void onDestroy() {
//        getSupportFragmentManager().removeOnBackStackChangedListener(onBackStackChangedListener);
        /*if (mHomeKeyEventReceiver != null) {
            unregisterReceiver(mHomeKeyEventReceiver);
        }*/
        super.onDestroy();
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
            Fragment fragment = getSupportFragmentManager().findFragmentById(BaseDictFragment.FRAGMENT_NEW_WORD);
            if (fragment != null && fragment instanceof DictMainFragment) {
                ((BaseDictFragment) fragment).onPauseView();
            }
        }
    }

    @Override
    public void detachFragment(int fragmentID) {
        if (fragmentID == BaseDictFragment.FRAGMENT_DICT_SEARCH) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(BaseDictFragment.FRAGMENT_NEW_WORD);
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

    /**
     * home listener
     */
//    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
//        String SYSTEM_REASON = "reason";
//        String SYSTEM_HOME_KEY = "homekey";
//        String SYSTEM_HOME_KEY_LONG = "recentapps";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//                String reason = intent.getStringExtra(SYSTEM_REASON);
//                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
//                    //表示按了home键,程序到了后台
//                    NewWordAppActivity.this.finish();
//                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){
//                    //表示长按home键,显示最近使用的程序列表
//                }
//            }
//        }
//    };
}
