package com.readboy.mobile.dictionary.fragment;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readboy.Dictionary.R;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.widget.TextScrollView;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.dict.DictWordView;
import com.readboy.mobile.dictionary.dict.SelectedDictionaryManager;
import com.readboy.mobile.dictionary.utils.TextSizeManage;
import com.readboy.mobile.dictionary.view.CustomToast;
import com.readboy.mobile.dictionary.view.SelectDictionaryDialog;

/**
 * Created by Senny on 2015/10/29.
 */
public class DictStrokeWordContentFragment extends Fragment {

    private final static String STATE_WORD_IS_SELECT = "is_select";
    private final static String STATE_WORD_TEXTSIZE = "textSize";

    private String expain;
    private SelectDictionaryDialog selectDictionaryDialog;
    private DictWordView dictWordView;
    private String selectString;
    private int selectDictType;
    private TextSizeManage textSizeManage;
    private String keyWord;
    private boolean isSelected;

    public void setExpain(String keyWord, String expain) {
        this.keyWord = keyWord;
        this.expain = expain;
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

    public int getTextSize() {
        if (dictWordView != null) {
            return dictWordView.getTextSize();
        }
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        textSizeManage = new TextSizeManage();
        if (savedInstanceState != null) {
            if (savedInstanceState.get(STATE_WORD_IS_SELECT) instanceof Boolean) {
                isSelected = savedInstanceState.getBoolean(STATE_WORD_IS_SELECT);
            }
            if (savedInstanceState.get(STATE_WORD_TEXTSIZE) instanceof Integer) {
                textSizeManage.setCurrentTextSize(savedInstanceState.getInt(STATE_WORD_TEXTSIZE));
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stroke_word_content, null);
        dictWordView = (DictWordView) root.findViewById(R.id.fragment_stroke_word_content_view);
        root.post(new Runnable() {
            @Override
            public void run() {
                if (expain != null) {
                    dictWordView.setTextSize(textSizeManage.getCurrentTextSize());
                    dictWordView.setChangeLineTextSize(0, textSizeManage.getFirstLineTextSize());
                    dictWordView.setDictWordText(keyWord, expain);
                    dictWordView.setSelectText(isSelected);
                }
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
                if (endRectF != null && startRectF != null) {
                    selectDictType = DictWordSearch.getDictType(text);
                    selectString = text;
                    String[] items = SelectedDictionaryManager.getDictionaryLabelArray(getActivity(),
                            DictWordSearch.getDictType(text));
                    if (endRectF.top > startRectF.top) {
                        /*int bottom = ((int) endRectF.bottom - dictWordView.getScrollY());
                        selectDictionaryDialog
                                .showDialog(new int[]{(int) endRectF.right, bottom}, items, true);*/
                        int bottom = ((int) endRectF.bottom - dictWordView.getScrollY());
                        if (getView().getHeight() > (bottom + 120)) {
                            selectDictionaryDialog
                                    .showDialog(new int[]{(int) endRectF.right, bottom}, items, true);
                        } else {
                            int top = ((int) startRectF.top - dictWordView.getScrollY());
                            selectDictionaryDialog
                                    .showDialog(Gravity.TOP, new int[]{(int) startRectF.left, top}, items, false);
                        }
                    } else if (startRectF.top > endRectF.top) {
                        int top = ((int) endRectF.top - dictWordView.getScrollY());
                        selectDictionaryDialog
                                .showDialog(Gravity.TOP, new int[]{(int) endRectF.left, top}, items, false);
                    } else {
                        int top = ((int) endRectF.top - dictWordView.getScrollY());
                        selectDictionaryDialog
                                .showDialog(Gravity.TOP, new int[]{(int) endRectF.left, top}, items, false);
                    }
                }
                DictManagerControler.getInstance().hideInput(getActivity());
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
                    int dictId = SelectedDictionaryManager.getDictionaryId(getActivity(), selectDictType, itemIndex);
                    DictSelectWordContentFragment dictSelectWordContentFragment = new DictSelectWordContentFragment();
                    if (getParentFragment() != null && getParentFragment().getParentFragment() != null) {
                        dictSelectWordContentFragment.startSearch(getParentFragment().getParentFragment().getFragmentManager(), getActivity(), dictId, selectString);
                    }
                } else {
                    if (getActivity() != null) {
                        CustomToast.getInstance(getActivity()).showToast(getActivity().getString(R.string.tip_searched_world_failed));
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
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_WORD_TEXTSIZE, textSizeManage.getCurrentTextSize());
        outState.putBoolean(STATE_WORD_IS_SELECT, dictWordView.isSelectText());
        super.onSaveInstanceState(outState);
    }

    public void dismissAllDialog() {
        if (selectDictionaryDialog != null) {
            selectDictionaryDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        dismissAllDialog();
        super.onDestroyView();
    }
}
