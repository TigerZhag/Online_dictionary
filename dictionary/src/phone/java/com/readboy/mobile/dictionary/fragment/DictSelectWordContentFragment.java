package com.readboy.mobile.dictionary.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.search.DictWordSound;
import com.readboy.library.widget.TextScrollView;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.dict.SelectedDictionaryManager;
import com.readboy.mobile.dictionary.provider.MobileProviderManager;
import com.readboy.mobile.dictionary.utils.BaseHandler;
import com.readboy.mobile.dictionary.utils.IWordContentController;
import com.readboy.mobile.dictionary.utils.MenuController;
import com.readboy.mobile.dictionary.utils.TextSizeManage;
import com.readboy.mobile.dictionary.view.CustomToast;
import com.readboy.mobile.dictionary.view.DialogMenu;
import com.readboy.mobile.dictionary.view.DictWordControlViewGroup;
import com.readboy.mobile.dictionary.dict.DictWordView;
import com.readboy.mobile.dictionary.view.SelectDictionaryDialog;

/**
 * Created by Senny on 2015/11/3.
 */
public class DictSelectWordContentFragment extends Fragment implements IWordContentController {

    private SelectWordContentHandler selectWordContentHandler;
    private DictWordView dictWordView;
    private FragmentManager fragmentManager;
    private Context parentContext;
    private SelectedWordInfo selectedWordInfo;
    private SelectDictionaryDialog selectDictionaryDialog;
    private String selectString;
    private int selectDictType;
    private TextSizeManage textSizeManage;
    private DictWordSound dictWordSound;
    private TextView titleView;
    private DictWordControlViewGroup dictWordControlViewGroup;
//    private PopupWindow mPopupMenu;
    private MenuController menuController;
    private DialogMenu mDialogMenu;

    public void startSearch(FragmentManager fragmentManager,
                            Context context, int dictID, String str) {
        init();
        this.fragmentManager = fragmentManager;
        this.parentContext = context;
        SelectWordContentRunnable runnable = new SelectWordContentRunnable(context, dictID, str);
        DictManagerControler.getInstance().execute(runnable);
    }

    public void setSelectedText(boolean isSelected) {
        if (dictWordView != null) {
            dictWordView.setSelectText(isSelected);
        }
    }

    public boolean isSelectedText() {
        if (dictWordView != null) {
            return dictWordView.isSelectText();
        }
        return false;
    }

    public void enlargeTextSize() {
        if (textSizeManage != null && dictWordView != null) {
            textSizeManage.enlargeTextSize();
            if (textSizeManage.getCurrentTextSize() != dictWordView.getTextSize()) {
                dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
                dictWordView.setChangeLineTextSize(0, textSizeManage.getFirstLineTextSize());
            }
        }
    }

    public void reduceTextSize() {
        if (textSizeManage != null && dictWordView != null) {
            textSizeManage.reduceTextSize();
            if (textSizeManage.getCurrentTextSize() != dictWordView.getTextSize()) {
                dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
                dictWordView.setChangeLineTextSize(0, textSizeManage.getFirstLineTextSize());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_select_word_content, null);
        return root;
    }

    @Override
    public void onViewCreated(View root, @Nullable Bundle savedInstanceState) {
        textSizeManage = new TextSizeManage();
        dictWordSound = new DictWordSound();
        dictWordSound.open();
        root.findViewById(R.id.fragment_select_word_sound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dictWordSound.playWord2(selectedWordInfo.word, null, null);
                /*SoundAnimationFragment.show(getFragmentManager());*/
                if (v.getBackground() instanceof AnimationDrawable) {
                    AnimationDrawable animationDrawable = (AnimationDrawable) v.getBackground();
                    animationDrawable.stop();
                    animationDrawable.start();
                }
            }
        });
        if (selectedWordInfo != null
                && dictWordSound.canPlay(selectedWordInfo.word)) {
            root.findViewById(R.id.fragment_select_word_sound).setVisibility(View.VISIBLE);
        } else {
            root.findViewById(R.id.fragment_select_word_sound).setVisibility(View.GONE);
        }
        titleView = (TextView) root.findViewById(R.id.fragment_select_word_title);
        if (selectedWordInfo != null) {
            titleView.setText(getResources()
                    .getStringArray(R.array.dictNames)[selectedWordInfo.dictID]);
        }
        dictWordView = (DictWordView) root.findViewById(R.id.fragment_select_word_content_view);
        dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
        dictWordView.setChangeLineTextSize(0, textSizeManage.getFirstLineTextSize());
        if (selectedWordInfo.dictID == DictIOFile.ID_XDDICT
                || selectedWordInfo.dictID == DictIOFile.ID_CYDICT
                || selectedWordInfo.dictID == DictIOFile.ID_YHDICT) {
            dictWordView.setDictWordText(selectedWordInfo.word, selectedWordInfo.data);
        } else {
            dictWordView.setDictWordText(selectedWordInfo.data);
        }

        dictWordControlViewGroup = (DictWordControlViewGroup) root.findViewById(R.id.fragment_select_word_content_control_group);
        dictWordControlViewGroup.setOnWordControlButtonListener(new DictWordControlViewGroup.OnWordControlButtonListener() {

            @Override
            public void onShow(View enlargeButton, View reduceButton, View selectButton) {

                boolean isSelectedText = isSelectedText();
                int textSize = getTextSize();

                if (isSelectedText) {
                    selectButton.setBackgroundResource(R.drawable.selecting_text);
                } else {
                    selectButton.setBackgroundResource(R.drawable.btn_select_word);
                }

                if (textSize == TextSizeManage.MAX_TEXT_SIZE) {
                    enlargeButton.setBackgroundResource(R.drawable.enlarge_text_size_disable);
                    reduceButton.setBackgroundResource(R.drawable.btn_reduce_text_size);
                } else if (textSize == TextSizeManage.MIN_TEXT_SIZE) {
                    enlargeButton.setBackgroundResource(R.drawable.btn_enlarge_text_size);
                    reduceButton.setBackgroundResource(R.drawable.reduce_text_size_disable);
                } else {
                    enlargeButton.setBackgroundResource(R.drawable.btn_enlarge_text_size);
                    reduceButton.setBackgroundResource(R.drawable.btn_reduce_text_size);
                }
            }

            @Override
            public void onEnlargeTextSize(View enlargeButton, View reduceButton, View selectButton) {

                int textSize = setEnlargeTextSize();

                if (textSize == TextSizeManage.MAX_TEXT_SIZE) {
                    enlargeButton.setBackgroundResource(R.drawable.enlarge_text_size_disable);
                    reduceButton.setBackgroundResource(R.drawable.btn_reduce_text_size);
                } else if (textSize == TextSizeManage.MIN_TEXT_SIZE) {
                    enlargeButton.setBackgroundResource(R.drawable.btn_enlarge_text_size);
                    reduceButton.setBackgroundResource(R.drawable.reduce_text_size_disable);
                } else {
                    enlargeButton.setBackgroundResource(R.drawable.btn_enlarge_text_size);
                    reduceButton.setBackgroundResource(R.drawable.btn_reduce_text_size);
                }

            }

            @Override
            public void onReducesTextSize(View enlargeButton, View reduceButton, View selectButton) {

                int textSize = setReduceTextSize();

                if (textSize == TextSizeManage.MAX_TEXT_SIZE) {
                    enlargeButton.setBackgroundResource(R.drawable.enlarge_text_size_disable);
                    reduceButton.setBackgroundResource(R.drawable.btn_reduce_text_size);
                } else if (textSize == TextSizeManage.MIN_TEXT_SIZE) {
                    enlargeButton.setBackgroundResource(R.drawable.btn_enlarge_text_size);
                    reduceButton.setBackgroundResource(R.drawable.reduce_text_size_disable);
                } else {
                    enlargeButton.setBackgroundResource(R.drawable.btn_enlarge_text_size);
                    reduceButton.setBackgroundResource(R.drawable.btn_reduce_text_size);
                }
            }

            @Override
            public boolean onSelectedText(View enlargeButton, View reduceButton, View selectButton) {

                boolean isSelectedText = setSelectTextState();

                if (isSelectedText) {
                    selectButton.setBackgroundResource(R.drawable.selecting_text);
                } else {
                    selectButton.setBackgroundResource(R.drawable.btn_select_word);
                }

                return isSelectedText;
            }
        });
        dictWordView.setOnTextScrollViewListener(new TextScrollView.OnTextScrollViewListener() {
            @Override
            public void startAnalyTextPage() {

            }

            @Override
            public void loadingTextPage() {

            }

            @Override
            public void startSelectText() {

            }

            @Override
            public void endSelectText(String text, RectF startRectF, RectF endRectF) {
                if (endRectF != null && endRectF != null) {
                    selectDictType = DictWordSearch.getDictType(text);
                    selectString = text;
                    String[] items = SelectedDictionaryManager.getDictionaryLabelArray(getActivity(),
                            DictWordSearch.getDictType(text));
                    if (endRectF.top > startRectF.top) {
                        /*int bottom = ((int) endRectF.bottom - dictWordView.getScrollY());
                        selectDictionaryDialog
                                .showDialog(new int[]{(int) endRectF.right, bottom}, items, true);*/
                        int bottom = ((int) endRectF.bottom - dictWordView.getScrollY());
                        if (((View) dictWordView.getParent().getParent()).getHeight() > (bottom + 200)) {
                            selectDictionaryDialog
                                    .showDialog(new int[]{(int) endRectF.right, bottom}, items, true);
                        } else {
                            int top = ((int) startRectF.top - dictWordView.getScrollY());
                            if (0 > top) {
                                top = 0;
                            }
                            selectDictionaryDialog
                                    .showDialog(Gravity.TOP, new int[]{(int) startRectF.left, top}, items, false);
                        }
                    } else if (startRectF.top > endRectF.top) {
                        int top = ((int) endRectF.top - dictWordView.getScrollY());
                        if (0 > top) {
                            top = 0;
                        }
                        selectDictionaryDialog
                                .showDialog(Gravity.TOP, new int[]{(int) endRectF.left, top}, items, false);
                    } else {
                        int top = ((int) endRectF.top - dictWordView.getScrollY());
                        selectDictionaryDialog
                                .showDialog(Gravity.TOP, new int[]{(int) endRectF.left, top}, items, false);
                    }
                }

            }

            @Override
            public void onMoveSelect(float x, float y) {

            }
        });

        selectDictionaryDialog = new SelectDictionaryDialog(getActivity(), dictWordView);
        selectDictionaryDialog.setOnSelectedItemListener(new SelectDictionaryDialog.OnSelectedItemListener() {
            @Override
            public void onSelected(int itemIndex) {
                if (selectDictType != -1 && selectString != null) {
                    setSelectedText(false);
                    int dictId = SelectedDictionaryManager.getDictionaryId(getActivity(), selectDictType, itemIndex);
                    SelectWordContentRunnable runnable = new SelectWordContentRunnable(getActivity(), dictId, selectString);
                    DictManagerControler.getInstance().execute(runnable);
                } else {
                    if (selectWordContentHandler != null) {
                        selectWordContentHandler.sendEmptyMessage(SelectWordContentHandler.LOAD_DATA_FAILED);
                    }
                }
                if (dictWordView != null) {
                    dictWordView.clearSelectedBackground();
                }
                selectDictType = -1;
                selectString = null;
            }

            @Override
            public void dismiss() {
                if (dictWordView != null) {
                    dictWordView.clearSelectedBackground();
                }
                selectDictType = -1;
                selectString = null;
            }
        });
        root.findViewById(R.id.fragment_select_word_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        View showMenuButton = root.findViewById(R.id.fragment_select_word_menu);
        View menuLayout = getLayoutInflater(savedInstanceState).inflate(R.layout.layout_menu, null);
        menuLayout.measure(root.getWidth(), root.getHeight());
        /*mPopupMenu = new PopupWindow(menuLayout, menuLayout.getMeasuredWidth(), menuLayout.getMeasuredHeight(), false);
        mPopupMenu.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupMenu.setOutsideTouchable(true);*/
        showMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mPopupMenu.showAsDropDown((View) v.getParent(), 0, 0, Gravity.RIGHT);
                if (mDialogMenu != null) {
                    if (mDialogMenu.getDialog() == null) {
                        mDialogMenu.show(getFragmentManager(), DialogMenu.class.getSimpleName());
                    } else if (mDialogMenu.getDialog() != null && !mDialogMenu.getDialog().isShowing()) {
                        getFragmentManager().beginTransaction().show(mDialogMenu);
                    }
                }
            }
        });
        menuController = new MenuController();
        mDialogMenu = new DialogMenu();
        mDialogMenu.setMenuController(menuController);

        menuController.setController(this);
    }

    @Override
    public void onDestroyView() {
        if (dictWordSound != null) {
            dictWordSound.close();
        }
        if (selectDictionaryDialog != null) {
            selectDictionaryDialog.dismiss();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        if (selectWordContentHandler == null) {
            selectWordContentHandler = new SelectWordContentHandler(this);
        }
    }

    @Override
    public int setEnlargeTextSize() {
        enlargeTextSize();
        int textSize = getTextSize();
        return textSize;
    }

    @Override
    public int setReduceTextSize() {
        reduceTextSize();
        int textSize = getTextSize();
        return textSize;
    }

    @Override
    public boolean setSelectTextState() {
        boolean isSelectedText = !isSelectedText();
        setSelectedText(isSelectedText);
        return isSelectedText;
    }

    @Override
    public boolean getSelectTextState() {
        return isSelectedText();
    }

    @Override
    public int getTextSize() {
        if (dictWordView != null) {
            return dictWordView.getTextSize();
        }
        return 0;
    }

    private class SelectWordContentHandler extends BaseHandler<DictSelectWordContentFragment> {

        final static int LOAD_DATA_SUCCESS = 0x01;
        final static int LOAD_DATA_FAILED = LOAD_DATA_SUCCESS + 1;

        public SelectWordContentHandler(DictSelectWordContentFragment dictSelectWordContentFragment) {
            super(dictSelectWordContentFragment);
        }

        @Override
        public void handleMessage(Message msg) {

            DictSelectWordContentFragment instance = getReference().get();

            if (instance == null) {
                return;
            }
            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    if (msg.obj instanceof SelectedWordInfo) {
                        SelectedWordInfo newSelectWordInfo = ((SelectedWordInfo)msg.obj);
                        if (instance.selectedWordInfo == null ||
                                newSelectWordInfo.dictID != instance.selectedWordInfo.dictID ||
                                newSelectWordInfo.wordIndex != instance.selectedWordInfo.wordIndex) {
                            instance.selectedWordInfo = newSelectWordInfo;
                            if (instance.getFragmentManager() != null) {
                                if (dictWordSound.canPlay(selectedWordInfo.word)) {
                                    instance.getView().findViewById(R.id.fragment_select_word_sound).setVisibility(View.VISIBLE);
                                } else {
                                    instance.getView().findViewById(R.id.fragment_select_word_sound).setVisibility(View.GONE);
                                }
                                if (selectedWordInfo != null) {
                                    titleView.setText(getResources()
                                            .getStringArray(R.array.dictNames)[selectedWordInfo.dictID]);
                                }
                                if (instance.selectedWordInfo.dictID == DictIOFile.ID_XDDICT
                                        || instance.selectedWordInfo.dictID == DictIOFile.ID_CYDICT
                                        || instance.selectedWordInfo.dictID == DictIOFile.ID_YHDICT) {
                                    instance.dictWordView.setDictWordText(instance.selectedWordInfo.word,
                                            instance.selectedWordInfo.data);
                                } else {
                                    instance.dictWordView.setDictWordText(instance.selectedWordInfo.data);
                                }

                                if (instance.selectedWordInfo.dictID != DictIOFile.ID_YYDICT) {
                                    MobileProviderManager.getInstance().addWordRecord(getActivity().getContentResolver(),
                                            instance.selectedWordInfo.dictID + "",
                                            instance.selectedWordInfo.word,
                                            instance.selectedWordInfo.wordIndex);
                                }

                            } else {
                                if (instance.selectedWordInfo.dictID != DictIOFile.ID_YYDICT) {
                                    MobileProviderManager.getInstance().addWordRecord(parentContext.getContentResolver(),
                                            instance.selectedWordInfo.dictID + "",
                                            instance.selectedWordInfo.word,
                                            instance.selectedWordInfo.wordIndex);
                                }
                                DictManagerControler.addStackFragmentWithAnim(fragmentManager.beginTransaction(),
                                        R.id.fragment_main_extend_content,
                                        DictSelectWordContentFragment.class.getSimpleName(),
                                        DictSelectWordContentFragment.class.getSimpleName(),
                                        getReference().get(), R.anim.right_in, 0, 0, R.anim.right_out);
                            }
                        }


                    }
                    break;

                case LOAD_DATA_FAILED:
                    if (getActivity() != null) {
                        CustomToast.getInstance(parentContext).showToast(getActivity().getString(R.string.tip_searched_world_failed));
                    } else if (parentContext != null) {
                        CustomToast.getInstance(parentContext).showToast(parentContext.getString(R.string.tip_searched_world_failed));
                    }
                    break;

            }
        }
    }

    private class SelectWordContentRunnable implements Runnable {

        Context context;
        int dictID;
        String selectString;

        SelectWordContentRunnable(Context context, int dictID, String selectString) {
            this.context = context;
            this.dictID = dictID;
            this.selectString = selectString;
        }

        @Override
        public void run() {
            DictWordSearch dictWordSearch = new DictWordSearch();
            dictWordSearch.open(dictID, context, null);
            int keyIndex = dictWordSearch.getSearchKeyIndex(selectString);
            String keyWord = dictWordSearch.getKeyWord(keyIndex);
            if (keyIndex == -1) {
                if (selectWordContentHandler != null) {
                    selectWordContentHandler.sendEmptyMessage(SelectWordContentHandler.LOAD_DATA_FAILED);
                }
            } else {
                byte[] buffer = dictWordSearch.getExplainByte(keyIndex);
                int start = dictWordSearch.getExplainByteStart(buffer);
                for (int i = 0;i < start;i++) {
                    buffer[i] = 0;
                }
                SelectedWordInfo selectedWordInfo = new SelectedWordInfo();
                selectedWordInfo.dictID = dictID;
                selectedWordInfo.word = keyWord;
                selectedWordInfo.data = buffer;
                selectedWordInfo.wordIndex = keyIndex;
                if (selectWordContentHandler != null) {
                    selectWordContentHandler.sendMessages(SelectWordContentHandler.LOAD_DATA_SUCCESS, selectedWordInfo, 0, 0, 0);
                }
            }

        }
    }

    private class SelectedWordInfo {
        int dictID;
        String word;
        byte[] data;
        int wordIndex;
    }
}
