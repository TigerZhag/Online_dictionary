package com.readboy.offline.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.readboy.Dictionary.R;
import com.readboy.offline.dialog.RemindDialog;
import com.readboy.offline.dialog.SelectWordDialog;
import com.readboy.dict.DictWordView;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.search.DictWordSound;
import com.readboy.library.utils.DelayClick;
import com.readboy.library.utils.DictInfo;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 14-8-27.
 */
public class BaseDictFragment extends Fragment {

    public static final String FRAGMENT_TAG_DICT_APP = "dict_app_tag";
    public static final String FRAGMENT_TAG_DICT_JUMP = "dict_jump_tag";
    public static final String FRAGMENT_TAG_NEW_DICT_SEARCH = "new_dict_search_tag";
    public static final String FRAGMENT_TAG_DICT_SEARCH = "dict_search_tag";
    public static final String FRAGMENT_TAG_NEW_WORD = "new_word_tag";
    //grey
    public static final String FRAGMENT_TAG_GREY_MAIN = "grey_main";
    public static final String FRAGMENT_TAG_GREY_RECORD = "grey_grid";
    public static final String FRAGMENT_TAG_GREY_CONTENT = "grey_content";
    public static final String FRAGMENT_TAG_GREY_NONE_FOND = "grey_none_fond";

    public static final int REQUEST_CODE = 0x01;
    public static final int RESULT_OK = 0x01;
    public static final int RESULT_CANEL = 0x02;
    public static final int RESULT_FAILED = 0x03;

    public static final int FRAGMENT_DICT_APP = R.id.dictApp;
    public static final int FRAGMENT_DICT_JUMP = R.id.dictJump;
    public static final int FRAGMENT_NEW_DICT_SEARCH = R.id.newDictSearch;
    public static final int FRAGMENT_DICT_SEARCH = R.id.dictSearch;
    public static final int FRAGMENT_NEW_WORD = R.id.newWord;
    public static final int FRAGMENT_GREY_MAIN = R.id.grey_main;

    private FragmentInfo fragmentInfo;

    /**
     * 词典文件操作
     */
    protected DictWordSearch dictWordSearch = null;
    /**
     * 单词声音文件操作
     */
    protected DictWordSound dictWordSound = null;

    /**
     * 提示框
     */
    private RemindDialog remindDialog = null;
    /**
     * 取词框
     */
    private SelectWordDialog mSelectWordDialog = null;
    /**
     * 语速框
     */
    private Dialog mSoundSpeedDialog = null;
    /**
     * 延迟点击
     */
    protected DelayClick delayClick = null;
    /**
     * 语速
     */
    protected int mAudioRate = 0;
    /**
     * 字体大小
     */
    protected int textSize = 24;
    /**
     * 当前词典ID
     */
    protected int mDictID = -1;
    /**
     * 单词或生词位置
     */
    protected int mKeyIndex = -1;
    /**
     * 单词或生词
     */
    protected String mKeyWord = null;
    /**
     * 笔画的拼音
     */
    protected String mKeyWordPhonetic = null;
    /**
     * 是否能发音
     */
    protected boolean mCanPlaySound = false;

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nonAnimation) {
            setAnimation(0);
            return super.onCreateAnimation(transit, enter, 0);
        }
        if (enter && nextAnim != 0) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    animationStart();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animationEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return animation;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        delayClick = new DelayClick();
        dictWordSearch = new DictWordSearch();
        dictWordSound = new DictWordSound();
        return view;
    }

    public void onResumeView() {

    }

    public void onPauseView() {

    }

    public void onNewIntent(Intent intent) {
    }

    /**
     * 动画开始
     */
    public void animationStart() {

    }

    /**
     * 动画结束
     */
    public void animationEnd() {

    }

    /**
     * 是否有dialog显示
     * @return
     */
    public boolean isDialogShow() {
        if ((remindDialog != null && remindDialog.remindIsShowing()) ||
                (mSelectWordDialog != null && mSelectWordDialog.isShowing()) ||
                (mSoundSpeedDialog != null && mSoundSpeedDialog.isShowing())) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        if (mSoundSpeedDialog != null) {
            mSoundSpeedDialog.dismiss();
            mSoundSpeedDialog = null;
        }
        if (mSelectWordDialog != null) {
            mSelectWordDialog.dismiss();
            mSelectWordDialog = null;
        }
        if (delayClick != null) {
            delayClick = null;
        }
        if (dictWordSearch != null) {
            if (dictWordSearch.getHanziPackage() != null) {
                dictWordSearch.getHanziPackage().colse();
            }
            dictWordSearch.close();
            dictWordSearch.closeHanziPackage();
//            dictWordSearch = null;
        }
        if (dictWordSound != null) {
            dictWordSound.close();
            dictWordSound = null;
        }
        if (remindDialog != null) {
            remindDialog.remindDismiss();
            remindDialog = null;
        }

        mKeyWord = null;
        mKeyWordPhonetic = null;

        finishOnResult();

        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        if (onDictFragmentListener != null) {
            onDictFragmentListener.attachFragment(getId());
        }
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (onDictFragmentListener != null) {
            onDictFragmentListener.detachFragment(getId());
        }
        onDictFragmentListener = null;
    }

    public static int getDictType(int dictID) {
        switch (dictID) {
            case DictInfo.DICT_BIHUA:
            case DictInfo.DICT_DONGMAN:
            case DictInfo.DICT_GUHAN:
            case DictInfo.DICT_HANCHENG:
            case DictInfo.DICT_XIANHAN:
            case DictInfo.DICT_YINGHAN:
            case DictInfo.DICT_JIANHAN:
            case DictInfo.DICT_HANYING:
                 return DictWordSearch.DICT_TYPE_CHI;
            default:
                return DictWordSearch.DICT_TYPE_ENG;
        }
    }

    public boolean nonAnimation = false;

    /**
     * 通过反射设置进入或退出动画
     * @param id
     */
    public void setAnimation(int id) {
        try {
            Field mNextAnim = Fragment.class.getDeclaredField("mNextAnim");
            mNextAnim.setAccessible(true);
            mNextAnim.set(this, id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    protected void showToastText(int id, int duration) {
        Toast.makeText(getActivity(), getResources().getString(id), duration).show();
    }

    protected void showReminDialog(int stringID) {
        if (remindDialog == null) {
            remindDialog = new RemindDialog(getActivity());
        }
        remindDialog.remindShow(null, getResources().getString(stringID));
    }

    protected void showDialog(boolean autoDimiss, int titleID, int msgID, View.OnClickListener onClickListener) {
        if (remindDialog == null) {
            remindDialog = new RemindDialog(getActivity());
        }
        remindDialog.setCancelEnable(true);
        remindDialog.setAutoDismiss(autoDimiss);
        remindDialog.setConfirmOnClickListener(onClickListener);
        remindDialog.remindShow(titleID, msgID);
    }

    /**
     * 语速
     */
    public void showSoundSpeedDialog() {
        if (mSoundSpeedDialog == null) {
            mSoundSpeedDialog = new Dialog(getActivity(), R.style.dialog);
            View audioSpeedView = View.inflate(getActivity(), R.layout.dialog_audio_speed, null);
            final SeekBar mSeekBar = (SeekBar) audioSpeedView.findViewById(R.id.audio_speed_progress);
//        mSeekBar.setOnSeekBarChangeListener(this);
            mSoundSpeedDialog.addContentView(audioSpeedView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mSoundSpeedDialog.setCancelable(true);
            mSoundSpeedDialog.setCanceledOnTouchOutside(false);
            mSoundSpeedDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    if (mSeekBar != null) {
                        mSeekBar.setProgress((mAudioRate+100)/10);
                    }
                }
            });

            ((ImageButton)audioSpeedView.findViewById(R.id.audio_speed_sure)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mAudioRate = mSeekBar.getProgress() * 10 - 100;
                    mSoundSpeedDialog.dismiss();
                }
            });
            ((ImageButton)audioSpeedView.findViewById(R.id.audio_speed_cancel)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mSoundSpeedDialog.dismiss();
                }
            });
        }
        mSoundSpeedDialog.show();
    }

    /**
     * 取词弹出框
     * @param selectWord
     * @param dictType
     * @param scrollTextView
     */
    public void showSelectWordDialog(String selectWord, int dictType, final View scrollTextView) {
        if (selectWord == null || (selectWord != null && selectWord.trim().isEmpty())) {
            return;
        }

        if (mSelectWordDialog == null) {
            mSelectWordDialog = new SelectWordDialog(getActivity(), R.style.dialogWithDim);
            mSelectWordDialog.setOnSelectWordDialogListener(new SelectWordDialog.OnSelectWordDialogListener() {
                @Override
                public void getSelectToNewDictionary(int mDictID, String searchword) {
                    selectWordToIntent(mDictID, searchword);
                }
            });
            mSelectWordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    dismissSelectWordDialog(scrollTextView);
                }
            });
        }
        if (!mSelectWordDialog.showSelectText(selectWord, dictType)) {
            if (scrollTextView != null && scrollTextView instanceof DictWordView) {
                ((DictWordView)scrollTextView).clearSelectWords();
            }
            Toast.makeText(getActivity(), getResources().getString(R.string.select_search_failed), Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void selectWordToIntent(int mDictID, String searchword) {
//        Intent intent = new Intent("com.readboy.Dictionary.DICTSEARCH");
//        intent.putExtra("dict_id", mDictID);
//        intent.putExtra("dict_key", searchword);
//        getActivity().startActivity(intent);
//        overridePendingTransition(com.readboy.plug.R.anim.push_left_in, com.readboy.plug.R.anim.non_animation);
        BaseDictFragment resultFragment = showFragment(BaseDictFragment.FRAGMENT_TAG_DICT_SEARCH,
                                                       BaseDictFragment.FRAGMENT_DICT_SEARCH,
                                                       mDictID,
                                                       searchword);
        if (resultFragment != null) {
            startForResult(REQUEST_CODE, resultFragment);
        }
    }

    public void dismissSelectWordDialog(View scrollTextView) {
        if (scrollTextView != null && scrollTextView instanceof DictWordView) {
            ((DictWordView)scrollTextView).clearSelectWords();
        }
    }

    public void getDataFromFragment() {

    }

    public BaseDictFragment showFragment(String showFragmentTag, int fragmentID, int dictID, String str) {
        if (onDictFragmentListener != null) {
            return onDictFragmentListener.showFragment(showFragmentTag, fragmentID, dictID, str);
        }
        return null;
    }

    public void dismissAllDialog() {
        if (mSoundSpeedDialog != null && mSoundSpeedDialog.isShowing()) {
            mSoundSpeedDialog.dismiss();
        }
        if (mSelectWordDialog != null && mSelectWordDialog.isShowing()) {
            mSelectWordDialog.dismiss();
        }
        if (remindDialog != null) {
            remindDialog.remindDismiss();
        }
    }

    public void dismissSoundSpeedDialog() {
        if (mSoundSpeedDialog != null) {
            mSoundSpeedDialog.dismiss();
            mSoundSpeedDialog = null;
        }
    }

    public void dismissSelectWordDialog() {
        if (mSelectWordDialog != null) {
            mSelectWordDialog.dismiss();
            mSelectWordDialog = null;
        }
    }

    public void dismissRemindDialog() {
        if (remindDialog != null) {
            remindDialog.remindDismiss();
        }
    }

    public int getTextSize() {
        if (textSize == 26) {
            textSize = 22;
        } else {
            textSize += 2;
        }
        return textSize;
    }

    public void startForResult(int requestCode, BaseDictFragment targetFragment) {
        String fromTag = getTag();
        if (fromTag == null) {
            throw new NullPointerException("The reusult of getTag() from Fragment can not be null");
        }
        startForResultFromFragment(requestCode, fromTag, targetFragment);
    }

    public void startForResultFromFragment(int requestCode, String fromTag, BaseDictFragment targetFragment) {
        if (targetFragment != null) {
            targetFragment.setFragmentInfo(requestCode, fromTag);
        }
    }

    public void setFragmentInfo(int requestCode, String fromTag) {
        if (fragmentInfo == null) {
            fragmentInfo = new FragmentInfo();
        }
        fragmentInfo.requestCode = requestCode;
        fragmentInfo.fromTag = fromTag;
    }

    public void setResult(int resultCode) {
        setResult(resultCode, null);
    }

    public void setResult(int resultCode, Intent data) {
        if (fragmentInfo != null) {
            fragmentInfo.resultCode = resultCode;
            fragmentInfo.data = data;
        }
    }

    public void finishOnResult() {
        if (fragmentInfo != null && fragmentInfo.fromTag != null) {
            String fromTag = fragmentInfo.fromTag;
            BaseDictFragment fromFragment = (BaseDictFragment)getFragmentManager().findFragmentByTag(fromTag);
            if (fromFragment != null) {
                fromFragment.onResultFromFragment(fragmentInfo.requestCode, fragmentInfo.resultCode, fragmentInfo.data);
            }
        }
        fragmentInfo = null;
    }

    public void onResultFromFragment(int requestCode, int resultCode, Intent data) {
        data = null;
    }

    public OnDictFragmentListener onDictFragmentListener = null;
    public void setOnDictFragmentListener(OnDictFragmentListener onDictFragmentListener) {
        this.onDictFragmentListener = onDictFragmentListener;
    }

    public interface OnDictFragmentListener {
        public BaseDictFragment showFragment(String showFragmentTag, int showFragmentID, int dictID, String str);
        public void attachFragment(int fragmentID);
        public void detachFragment(int fragmentID);
    }

    class FragmentInfo {
        public int requestCode = -1;
        public int resultCode = -1;
        public String fromTag;
        public Intent data;
    }
}
