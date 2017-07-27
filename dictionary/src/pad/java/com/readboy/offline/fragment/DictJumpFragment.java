package com.readboy.offline.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import com.readboy.Dictionary.R;
import com.readboy.depict.data.HanziPackage;
import com.readboy.depict.model.HanziInfo;
import com.readboy.dict.DictWordView;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.utils.DelayClick;
import com.readboy.library.utils.DictInfo;
import com.readboy.library.utils.PhoneticUtils;
import com.readboy.offline.adapter.WordListAdapter;
import com.readboy.offline.data.CollectionManager;
import com.readboy.offline.mode.DictModleButton;
import com.readboy.offline.view.DictJumpView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 14-8-27.
 */
public class DictJumpFragment extends BaseDictFragment implements DictJumpView.JumpListener{

    /* 跳查的条目信息 */
    private static final String JUMP_ITEM_DICT_ID = "dict_id";
    private static final String JUMP_ITEM_INDEX	= "index";
    private static final String JUMP_ITEM_TEXT = "text";
    private static final String JUMP_KEY_TEXT = "key_text";
    private static final String JUMP_PHONETIC_TEXT = "phonetic_text";

    private String jumpString = null;
    private DictJumpView dictJumpView = null;
    private List<HashMap<String, Object>> mJumpItemsArry;
    private String[] dictNameList = null;

    private String mPhonetic;
    private List<String> items = new ArrayList<String>();

    private int itemPosition = -1;

    public void setJumpString(String jumpString) {
        this.jumpString = jumpString;
    }

    private boolean isForceExit = false;

    public void setForceExit(boolean isForceExit) {
        this.isForceExit = isForceExit;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter && isForceExit) {
            /**
             * 将动画设置成静态
             * 原因: 进入DictSearchFragment的时候要把DictJumpFragment删掉
             */
            setAnimation(R.anim.non_animation);
//            try {
//                Field mNextAnim = Fragment.class.getDeclaredField("mNextAnim");
//                mNextAnim.setAccessible(true);
//                mNextAnim.set(this, R.anim.non_animation);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            }
            return super.onCreateAnimation(transit, enter, R.anim.non_animation);
        } else {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (reset()) {
            mDictID = (Integer)mJumpItemsArry.get(0).get(JUMP_ITEM_DICT_ID);
            mKeyIndex = (Integer)mJumpItemsArry.get(0).get(JUMP_ITEM_INDEX);
            mKeyWord = (String)mJumpItemsArry.get(0).get(JUMP_ITEM_TEXT);
            mPhonetic = (String)mJumpItemsArry.get(0).get(JUMP_PHONETIC_TEXT);
            dictJumpView = (DictJumpView)inflater.inflate(R.layout.layout_dict_jump, null);
            dictJumpView.setJumpListener(this);
            dictJumpView.post(new Runnable() {
                @Override
                public void run() {
                    if (dictJumpView != null) {
                        dictJumpView.setView(mDictID);
                    }
                }
            });
//            setContentView(jumpMainView.getJumpMainView());
            setResult(RESULT_OK);
            return dictJumpView;
        } else {
//            Toast.makeText(this, getResources().getString(R.string.select_search_failed), Toast.LENGTH_SHORT).show();
            setResult(RESULT_FAILED);
            showToastText(R.string.select_search_failed, Toast.LENGTH_SHORT);
            getActivity().getSupportFragmentManager().popBackStack();
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (items != null && items.size() > 0) {
//            resetView();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dictJumpView != null) {
            if (dictJumpView.getTextView() != null) {
                dictJumpView.getTextView().onResume();
            }
            if (dictJumpView.getBiHuaView() != null && dictJumpView.getBiHuaView().getVisibility() == View.VISIBLE) {
                dictJumpView.getBiHuaView().resume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dictJumpView != null) {
            if (dictJumpView.getTextView() != null) {
                dictJumpView.getTextView().onPause(true);
            }
            if (dictJumpView.getBiHuaView() != null && dictJumpView.getBiHuaView().getVisibility() == View.VISIBLE) {
                dictJumpView.getBiHuaView().pause();
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (dictJumpView != null) {
            dictJumpView = null;
        }
        if (mJumpItemsArry != null) {
            mJumpItemsArry.clear();
            mJumpItemsArry = null;
        }
        super.onDestroyView();
    }

    @Override
    public void animationStart() {
        if (items != null && items.size() > 0) {
            /**
             * 加载列表
             */
            resetView();
        }
    }

    @Override
    public void animationEnd() {
        if (items != null && items.size() > 0) {
            /**
             * 加载内容
             */
            showView(dictJumpView.getTextView(), dictJumpView.getJumpButtonModle());
        }
    }

    @Override
    public void selectWordToIntent(int dictID, String searchword) {
//        jumpMainView.cancleQuciStatu();
        if (canSearchWord(dictID, searchword)) {
            super.selectWordToIntent(dictID, searchword);
        }
    }

    @Override
    public void listSelect(int position, DictWordView scrollView, DictModleButton modleButton) {
        super.dismissAllDialog();
        if (itemPosition == position) {
            return;
        }
        if (mJumpItemsArry != null && mJumpItemsArry.size() > 0) {
            if (dictJumpView.getBiHuaView() != null) {
                dictJumpView.getBiHuaView().dismissDialog();
                if (dictJumpView.getBiHuaView().findViewById(R.id.depict) != null) {
                    dictJumpView.getBiHuaView().findViewById(R.id.depict).setEnabled(false);
                }
            }
            itemPosition = position;
            mDictID = (Integer)mJumpItemsArry.get(position).get(JUMP_ITEM_DICT_ID);
            mKeyIndex = (Integer)mJumpItemsArry.get(position).get(JUMP_ITEM_INDEX);
            mKeyWord = (String)mJumpItemsArry.get(position).get(JUMP_ITEM_TEXT);
            mPhonetic = (String)mJumpItemsArry.get(position).get(JUMP_PHONETIC_TEXT);
            showView(scrollView, modleButton);
        }
    }

    @Override
    public void selectText(String selectStr, View scrollTextView) {
        if (selectStr != null) {
            super.showSelectWordDialog(selectStr, DictWordSearch.getDictType(selectStr), scrollTextView);
        }
    }

    @Override
    public void onClickFaYin() {
        if (dictWordSound != null) {
            if (mDictID != DictInfo.DICT_BIHUA) {
                dictWordSound.playWord(mKeyWord, super.mAudioRate);
            } else if (mPhonetic != null){
//                byte[] soundBytes = dictWordSearch.getHanziSoundBytes(mKeyIndex);
                byte[] soundBytes = dictWordSearch.getHanziSoundBytes(mPhonetic);
                if (soundBytes != null) {
//                    dictWordSound.playBihuaSound(mKeyWordPhonetic, getActivity(), super.mAudioRate);
                    dictWordSound.playSound(soundBytes, super.mAudioRate);
                }
                dictJumpView.getBiHuaView().showCompletedHanzi();
            }
        }
    }

    @Override
    public void onClickZiHao(DictWordView scrollView) {
        /*DictWordSearch dictWordSearch = new DictWordSearch();
        if (dictWordSearch.open(mDictID, getActivity(), null)) {
            if (mDictID == DictIOFile.ID_BHDICT) {
                HanziInfo hanziInfo = dictWordSearch.getHanziPackage().getHanziLib().getHanziNotChangeSpell(mKeyIndex);
                dictWordSearch.getHanziPackage().setHanzi(hanziInfo);
                byte[] byteData = PhoneticUtils.checkString(dictWordSearch.getHanziPackage().getRemark()).getBytes();
                if (byteData != null) {
                    int start = 0;
                    if (mDictID != DictIOFile.ID_BHDICT) {
                        start = dictWordSearch.getExplainByteStart(byteData);
                    }
                    scrollView.showText(mKeyWord, byteData, start, mDictID, getTextSize());
                }
            } else {
                byte[] byteData =  dictWordSearch.getExplainByte(mKeyIndex);
                if (byteData != null) {
                    int start = 0;
                    if (mDictID != DictIOFile.ID_BHDICT) {
                        start = dictWordSearch.getExplainByteStart(byteData);
                    }
                    scrollView.showText(mKeyWord, byteData, start, mDictID, getTextSize());
                }
            }
        }*/
        if (scrollView != null) {
            scrollView.setTextSize(getTextSize());
        }
    }

    @Override
    public void onClickYuSu() {
        if (super.delayClick.canClickByTime(DelayClick.DELAY_300MS)) {
            super.showSoundSpeedDialog();
        }
    }

    @Override
    public void addNewWordButton() {
        if (!super.delayClick.canClickByTime(DelayClick.DELAY_300MS)) {
            return;
        }
        if (mKeyWord != null) {
            int saveResult = CollectionManager.addWord(getActivity(), mKeyWord, mDictID, mKeyIndex, mCanPlaySound);
            switch (saveResult) {
                case CollectionManager.SUCCESSFULLY:
                    showReminDialog(R.string.key_add_success);
                    break;
                case CollectionManager.OVERFLOW:
                    showReminDialog(R.string.key_is_full);
                    break;
                case CollectionManager.EXISTED:
                    /*if (BaseDictFragment.getDictType(mDictID) == DictWordSearch.DICT_TYPE_CHI) {
                        showReminDialog(R.string.key_has_exist_ch);
                    } else {
                        showReminDialog(R.string.key_has_exist_en);
                    }*/
                    showReminDialog(R.string.key_has_exist_ch);
                    break;
            }
        }
    }

    @Override
    public void getTouchScollTextView() {
        super.dismissAllDialog();
    }

    private boolean reset() {
        if (jumpString != null) {
            int dictType = DictWordSearch.getDictType(jumpString);
            int[] dictIdList = null;
            if(dictType == DictWordSearch.DICT_TYPE_ENG) {
                dictIdList = getResources().getIntArray(R.array.dict_search_eng_id);
            } else if(dictType == DictWordSearch.DICT_TYPE_CHI){
                dictIdList = getResources().getIntArray(R.array.dict_search_chi_id);
            } else {	// 未知字典类型
                jumpString = null;
                return false;
            }
            dictWordSearch = new DictWordSearch();
            if (!dictWordSound.open()) {
                showToastText(R.string.open_file_failed, Toast.LENGTH_LONG);
                return false;
            }
            if (mJumpItemsArry == null) {
                mJumpItemsArry = new ArrayList<HashMap<String, Object>>();
            } else {
                mJumpItemsArry.clear();
            }
            if (dictNameList == null) {
                dictNameList = getResources().getStringArray(R.array.dict_name_list);
            }
            if (items == null) {
                items = new ArrayList<String>();
            } else {
                items.clear();
            }

            for (int id:dictIdList) {
                if (dictWordSearch.open(id, getActivity(), null)) {
                    if (id == DictIOFile.ID_BHDICT) {
                        String chineseStr = DictIOFile.getFilterChineseWord(jumpString);
                        if (chineseStr == null || chineseStr.length() == 0 ) {
                            continue;
                        }
                        List<HanziInfo> hanziInfoList = dictWordSearch.getHanziKeyList(chineseStr.charAt(0)+"");
                        dictWordSearch.close();
                        if (hanziInfoList != null) {
                            for (HanziInfo hanziInfo:hanziInfoList) {
                                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                hashMap.put(JUMP_ITEM_DICT_ID, id);
                                hashMap.put(JUMP_ITEM_INDEX, hanziInfo.index);
                                hashMap.put(JUMP_ITEM_TEXT, hanziInfo.word);
                                hashMap.put(JUMP_PHONETIC_TEXT, hanziInfo.phonetic);
                                mJumpItemsArry.add(hashMap);
                                items.add(dictNameList[id] + ": " + hanziInfo.word + " " + PhoneticUtils.checkString(hanziInfo.phonetic));
                            }
                        }
                    } else {
                        int keyIndex = dictWordSearch.getSearchKeyIndex(jumpString);
                        if (keyIndex != -1) {
                            String keyWord = dictWordSearch.getKeyWord(keyIndex);
                            dictWordSearch.close();
                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put(JUMP_ITEM_DICT_ID, id);
                            hashMap.put(JUMP_ITEM_INDEX, keyIndex);
                            hashMap.put(JUMP_ITEM_TEXT, keyWord);
                            mJumpItemsArry.add(hashMap);
                            items.add(dictNameList[id] + ": " + PhoneticUtils.checkString(keyWord));
                        }
                    }



                }
            }
            dictWordSearch = null;
            jumpString = null;
            if (mJumpItemsArry.size() == 0 || items.size() == 0) {
                return false;
            }
            return true;
        }

        return false;
    }

    private void resetView() {
        WordListAdapter wordListAdapter = (WordListAdapter) dictJumpView.getWordList().getAdapter();
        if (wordListAdapter == null) {
            wordListAdapter = new WordListAdapter(getActivity());
        } else {
            wordListAdapter.onDistory();
        }
        wordListAdapter.setListItems(items);
        dictJumpView.getWordList().setAdapter(wordListAdapter);

    }

    private void showView(DictWordView scrollView, DictModleButton modleButton) {
        byte[] byteData = null;
        int start = 0;
        int length = 0;

        if (dictWordSearch != null) {
            dictWordSearch.close();
            dictWordSearch = null;
        }
        dictWordSearch = new DictWordSearch();
        if (dictWordSearch.open(mDictID, getActivity(), null)) {
            if (mDictID == DictIOFile.ID_BHDICT) {
                HanziPackage hanziPackage = dictWordSearch.getHanziPackage();
                HanziInfo hanziInfo = hanziPackage.getHanziLib().getHanziNotChangeSpell(mKeyIndex);
                if (hanziInfo != null) {
                    mKeyWord = hanziInfo.word;
                    mKeyIndex = hanziInfo.index;
                    hanziPackage.setHanzi(hanziInfo);
                    byteData = PhoneticUtils.checkString(dictWordSearch.getHanziPackage().getRemark()).getBytes();
                    dictJumpView.getBiHuaView().initView(getActivity(), dictWordSearch.getHanziPackage());
                    if (dictJumpView.getBiHuaView().getVisibility() != View.VISIBLE) {
                        dictJumpView.getBiHuaView().setVisibility(View.VISIBLE);
                    }
                    if (dictJumpView.getBiHuaView().findViewById(R.id.depict) != null) {
                        dictJumpView.getBiHuaView().findViewById(R.id.depict).setEnabled(true);
                    }

                    modleButton.faYin.setEnabled(true);
                    modleButton.yuSu.setEnabled(true);
                    mCanPlaySound = true;

                }
            } else {
                byteData =  dictWordSearch.getExplainByte(mKeyIndex);
                if (byteData != null) {
                    start = dictWordSearch.getExplainByteStart(byteData);
                }
                mCanPlaySound = dictWordSound.canPlay(mKeyWord);
                if (dictWordSound != null && mCanPlaySound) {
                    modleButton.faYin.setEnabled(true);
                    modleButton.yuSu.setEnabled(true);
                } else {
                    modleButton.faYin.setEnabled(false);
                    modleButton.yuSu.setEnabled(false);
                }
                if (dictJumpView.getBiHuaView().getVisibility() == View.VISIBLE) {
                    dictJumpView.getBiHuaView().pause();
                    dictJumpView.getBiHuaView().setVisibility(View.GONE);
                }
            }
            if (byteData != null) {
                scrollView.showText(mKeyWord, byteData, start, mDictID, textSize);
            }
        }

        if (byteData != null) {
            if (!modleButton.ziHao.isEnabled()) {
                modleButton.ziHao.setEnabled(true);
            }
            if (mDictID != DictInfo.DICT_BIHUA && mDictID != DictInfo.DICT_DONGMAN) {
                if (!modleButton.addNewWord.isEnabled()) {
                    modleButton.addNewWord.setEnabled(true);
                }
            } else {
                if (modleButton.addNewWord.isEnabled()) {
                    modleButton.addNewWord.setEnabled(false);
                }
            }
            dictJumpView.setDicID(mDictID);
        }
    }

    private boolean canSearchWord(int dictID, String searchStr) {
        DictWordSearch search = new DictWordSearch();
        if (search.open(dictID, getActivity(), null)) {
            if (search.getSearchKeyIndex(searchStr) != -1) {
                search.close();
                return true;
            }
        }
        search.close();
        showToastText(R.string.select_search_failed, Toast.LENGTH_SHORT);
        return false;
    }
}
