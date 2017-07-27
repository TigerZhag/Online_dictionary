package com.readboy.mobile.dictionary.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.utils.PhoneticUtils;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.iflytek.VoiceRecognitionManager;
import com.readboy.mobile.dictionary.utils.BaseHandler;
import com.readboy.mobile.dictionary.utils.CheckHanziData;
import com.readboy.mobile.dictionary.utils.MenuController;
import com.readboy.mobile.dictionary.view.CustomToast;
import com.readboy.mobile.dictionary.view.DialogMenu;
import com.readboy.mobile.dictionary.view.DictListPopupWindow;
import com.readboy.scantranslate.ScanFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Senny on 2015/11/17.
 */
public class DictSearchFragment extends BaseFragment implements
        DictListPopupWindow.OnSelectedItemWordListener, VoiceRecognitionManager.OnVoiceRecognitionListener, ViewTreeObserver.OnGlobalLayoutListener {

    public static final int STATE_VIEW_TEXT_SEARCH = 0x01;
    public static final int STATE_VIEW_VOICE_RECOGNITE_SEARCH_1 = STATE_VIEW_TEXT_SEARCH + 1;
    public static final int STATE_VIEW_VOICE_RECOGNITE_SEARCH_2 = STATE_VIEW_VOICE_RECOGNITE_SEARCH_1 + 1;
    public static final int STATE_VIEW_SCAN_SEARCH = STATE_VIEW_VOICE_RECOGNITE_SEARCH_2 + 1;
    public static final int STATE_VIEW_INIT = STATE_VIEW_SCAN_SEARCH + 1;
    public static final int STATE_MENU = STATE_VIEW_INIT + 1;
    private static final int STATE_LOAD_TEXT = STATE_MENU + 1;
    private static final int STATE_INIT = STATE_LOAD_TEXT + 1;

    public static final String ACTION_HIDE_INPUT = "hide_input_method";
    private static final String EXTRA_DICT_ID = "dict_id";

    private static final String EXTRA_WORLD = "world";
    private static final String EXTRA_WORLD_INDEX = "world_index";
    private static final String EXTRA_NEED_SET_TEXT = "need_set_text";
    private static final String EXTRA_SEARCH_TEXT = "search_text";
    private int mState = STATE_VIEW_TEXT_SEARCH;

    private DictSearchHandler dictSearchHandler;

    private DictListPopupWindow popupListWindow;
    private String searchWordTag;
    private TextWatcher textWatcher;
    private PlugWorldInfo plugWorldInfo;
    //    private DictWordControlViewGroup dictWordControlViewGroup;
    private EditText searchEditText;
    private SearchWorldInfo tempSearchWorldInfo;

    private VoiceRecognitionManager voiceRecognitionManager;
    private PopupWindow mPopupMenu;
    private MenuController menuController;
    private DialogMenu mDialogMenu;
    private Animation searchOverLayDropOut;

    private Bundle tempHanziBundle;
    private Message tempMessage;
    private Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
    private Matcher emojiMatcher = emoji.matcher("");

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (!isResumed()) {
                tempMessage = Message.obtain(msg);
                return;
            }

            switch (msg.what) {

                case STATE_LOAD_TEXT:
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        if (msg.obj instanceof SearchWorldInfo) {
                            tempSearchWorldInfo = (SearchWorldInfo) msg.obj;
                        } else {
                            tempSearchWorldInfo = null;
                        }
                        int dictID = bundle.getInt(EXTRA_DICT_ID, -1);
                        String world = bundle.getString(EXTRA_WORLD);
                        int worldIndex = bundle.getInt(EXTRA_WORLD_INDEX, -1);
                        boolean needSetText = bundle.getBoolean(EXTRA_NEED_SET_TEXT);
                        searchWord(dictID, world, worldIndex, needSetText);
                    }
                    dismissLoadingDialog();
                    break;

                case STATE_INIT:
                    init();
                    if (plugWorldInfo == null) {
                        searchEditText.post(new Runnable() {
                            @Override
                            public void run() {
                                searchEditText.requestFocus();
                                if (mState == STATE_VIEW_TEXT_SEARCH) {
                                    showSoftInput(searchEditText);
                                }

                            }
                        });
                    }
                    break;
            }
        }
    };

    private BroadcastReceiver actionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action != null) {
                    if (action.contentEquals(ACTION_HIDE_INPUT)) {
                        hideSoftInput(searchEditText);
                    } else if (action.contentEquals(ACTION_LOADING_HANZI_DATA_FINISH)) {
                        /*Message msg = Message.obtain();
                        msg.what = STATE_LOAD_TEXT;
                        msg.setData(tempHanziBundle);
                        if (mHandler != null
                                && tempHanziBundle != null) {
                            mHandler.sendMessage(msg);
                        }*/
                        handleHanziDataFinish();
                    }
                }
            }
        }
    };

    /*private HanziPackage.DataChangeListener dataChangeListener = new HanziPackage.DataChangeListener() {
        @Override
        public void onDataChange(boolean b) {
            dismissLoadingDialog();
            Message msg = Message.obtain();
            msg.what = STATE_LOAD_TEXT;
            msg.obj = plugWorldInfo;
            if (mHandler != null) {
                mHandler.sendMessage(msg);
            }
        }
    };*/

    private Animation.AnimationListener searchOverLayDropOutListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
//            getView().findViewById(R.id.fragment_search_over_lay).setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            getView().findViewById(R.id.fragment_search_over_lay).setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    private Runnable dismissOverlayRunnable = new Runnable() {
        @Override
        public void run() {
            if (getView() != null) {
                Animation animation = getView().findViewById(R.id.fragment_search_over_lay).getAnimation();
                if (animation != null) {
                    animation.cancel();
                }
                if (searchOverLayDropOut == null) {
                    searchOverLayDropOut = AnimationUtils.loadAnimation(getContext(), R.anim.drop_out);
                }
//                getView().findViewById(R.id.fragment_search_over_lay).setAnimation(searchOverLayDropOut);
                searchOverLayDropOut.setAnimationListener(searchOverLayDropOutListener);
                getView().findViewById(R.id.fragment_search_over_lay).startAnimation(searchOverLayDropOut);
            }
        }
    };

    public void setWoid(PlugWorldInfo plugWorldInfo) {
        this.plugWorldInfo = plugWorldInfo;
    }

    public boolean dismissPopuWindow() {
        if (popupListWindow != null) {
            return popupListWindow.dismiss();
        }
        return false;
    }

    public void setState(int mState) {
        this.mState = mState;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, null);
        root.getViewTreeObserver().addOnGlobalLayoutListener(this);
        return root;
    }

    @Nullable
    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {

        dictSearchHandler = new DictSearchHandler(this);
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    searchWordTag = System.currentTimeMillis() + "";
                    SearchWordRange searchWordRange = new SearchWordRange(s.toString());
                    searchWordRange.tag = searchWordTag;
                    DictManagerControler.getInstance().execute(searchWordRange);
                }

                if (s.length() > 0) {
                    PhoneticUtils.checkCharseqence(s);
                    getView().findViewById(R.id.fragment_search_delete).setVisibility(View.VISIBLE);
                } else {
                    getView().findViewById(R.id.fragment_search_delete).setVisibility(View.INVISIBLE);
                    if (popupListWindow != null) {
                        popupListWindow.dismiss();
                    }
                }
            }
        };
        ((EditText) root.findViewById(R.id.fragment_search_edittext)).addTextChangedListener(textWatcher);
        /*root.findViewById(R.id.fragment_search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });*/
        root.findViewById(R.id.fragment_search_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getFragmentManager().popBackStackImmediate();
                Activity activity = (Activity) getActivity();
                if (activity != null) {
                    activity.onBackPressed();
                }
            }
        });
        root.findViewById(R.id.fragment_search_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSearch();
                setSearchText("");
                v.setVisibility(View.INVISIBLE);
            }
        });
        searchEditText = (EditText) root.findViewById(R.id.fragment_search_edittext);
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                dismissPopuWindow();
                search();
                return true;
            }
        });
        root.findViewById(R.id.fragment_search_voice_recognite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voiceRecognitionManager != null) {
                    voiceRecognitionManager.showVoiceRecognitionDialog(getActivity());
                }
            }
        });
        root.findViewById(R.id.fragment_search_scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addScanSearchFragment();
            }
        });
        View showMenuButton = root.findViewById(R.id.fragment_search_menu);
        View menuLayout = getLayoutInflater(savedInstanceState).inflate(R.layout.layout_menu, null);
        menuLayout.measure(root.getWidth(), root.getHeight());
        mPopupMenu = new PopupWindow(menuLayout, menuLayout.getMeasuredWidth(), menuLayout.getMeasuredHeight(), false);
        mPopupMenu.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupMenu.setOutsideTouchable(true);
        showMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput(searchEditText);
//                mPopupMenu.showAsDropDown(getView().findViewById(R.id.fragment_search_box), 0, 0, Gravity.RIGHT);
                if (mDialogMenu != null) {
                    if (mDialogMenu.getDialog() == null){
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

        updateStateView(STATE_VIEW_INIT, root);
        updateStateView(mState, root);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_HIDE_INPUT);
        intentFilter.addAction(ACTION_LOADING_HANZI_DATA_FINISH);
        if (getActivity() != null && actionReceiver != null) {
            getActivity().registerReceiver(actionReceiver, intentFilter);
        }
        if (voiceRecognitionManager == null) {
            voiceRecognitionManager = new VoiceRecognitionManager();
            voiceRecognitionManager.init(getActivity(), DictSearchFragment.this);
        }
        if (tempHanziBundle != null && !isNeedUpdateHanziData()) {
            handleHanziDataFinish();
        }
        if (tempMessage != null && mHandler != null) {
            mHandler.sendMessage(tempMessage);
            tempMessage = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && actionReceiver != null) {
            getActivity().unregisterReceiver(actionReceiver);
        }
        if (voiceRecognitionManager != null) {
            voiceRecognitionManager.onDestroy();
            voiceRecognitionManager = null;
        }

        /*if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }*/
        if (dictSearchHandler != null) {
            dictSearchHandler.removeMessages(DictSearchHandler.TYPE_RECOGNITE_SEARCH);
            dictSearchHandler.removeMessages(DictSearchHandler.TYPE_SEARCH_WORLD);
        }
    }

    private void updateStateView(int state, View root) {
        switch (state) {
            case STATE_VIEW_TEXT_SEARCH:
                showTextSearchView(root);
                break;

            case STATE_VIEW_VOICE_RECOGNITE_SEARCH_1:
                showVoiceRecogniteView1(root);
                break;

            case STATE_VIEW_VOICE_RECOGNITE_SEARCH_2:
                showVoiceRecogniteView2(root);
                break;

            case STATE_VIEW_SCAN_SEARCH:
                showScanSearchView(root);
                break;

            case STATE_VIEW_INIT:
                showSearchButton(root);
                break;

            case STATE_MENU:
                showMenu(root);
                break;
        }
    }

    private void loadDataFinish() {
        updateStateView(STATE_MENU, getView());
        if (mState == STATE_VIEW_VOICE_RECOGNITE_SEARCH_2) {
            updateStateView(mState, getView());
        }
    }

    private void showMenu(View root) {
//        root.findViewById(R.id.fragment_search_button).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.fragment_search_menu).setVisibility(View.VISIBLE);
    }

    private void hideMenu(View root) {
//        root.findViewById(R.id.fragment_search_button).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.fragment_search_menu).setVisibility(View.GONE);
    }

    private void showSearchButton(View root) {
//        root.findViewById(R.id.fragment_search_button).setVisibility(View.VISIBLE);
        root.findViewById(R.id.fragment_search_menu).setVisibility(View.GONE);
    }

    private void showVoiceRecogniteView1(View root) {
        ((EditText) root.findViewById(R.id.fragment_search_edittext)).setInputType(InputType.TYPE_NULL);
        ((EditText) root.findViewById(R.id.fragment_search_edittext)).setHint(root.getResources().getString(R.string.click_voice_recognite_button_tip));
        root.findViewById(R.id.fragment_search_voice_recognite_button).setVisibility(View.VISIBLE);
//        root.findViewById(R.id.fragment_search_button).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.fragment_search_scan_button).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.fragment_search_state_button_bg).setBackgroundColor(0);
    }

    private void showVoiceRecogniteView2(View root) {
        /*((EditText) root.findViewById(R.id.fragment_search_edittext)).setInputType(InputType.TYPE_NULL);
        ((EditText) root.findViewById(R.id.fragment_search_edittext)).setHint(root.getResources().getString(R.string.click_voice_recognite_button_tip));*/
        root.findViewById(R.id.fragment_search_input).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.fragment_search_result_label).setVisibility(View.VISIBLE);
        root.findViewById(R.id.fragment_search_back_text).setVisibility(View.VISIBLE);
        root.findViewById(R.id.fragment_search_voice_recognite_button).setVisibility(View.VISIBLE);
//        root.findViewById(R.id.fragment_search_button).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.fragment_search_scan_button).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.fragment_search_state_button_bg).setBackgroundColor(root.getResources().getColor(R.color.middle_grey));
    }

    private void showScanSearchView(View root) {
        root.findViewById(R.id.fragment_search_input).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.fragment_search_result_label).setVisibility(View.VISIBLE);
        root.findViewById(R.id.fragment_search_back_text).setVisibility(View.VISIBLE);
        root.findViewById(R.id.fragment_search_voice_recognite_button).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.fragment_search_scan_button).setVisibility(View.VISIBLE);
    }

    private void showTextSearchView(View root) {
        ((EditText) root.findViewById(R.id.fragment_search_edittext)).setInputType(InputType.TYPE_CLASS_TEXT);
        root.findViewById(R.id.fragment_search_result_label).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.fragment_search_back_text).setVisibility(View.GONE);
        root.findViewById(R.id.fragment_search_voice_recognite_button).setVisibility(View.GONE);
        root.findViewById(R.id.fragment_search_scan_button).setVisibility(View.GONE);
    }

    private void addScanSearchFragment() {
        Fragment fragment = getFragmentManager().findFragmentByTag(ScanFragment.class.getSimpleName());
        if (fragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }
        final ScanFragment scanFragment = new ScanFragment();
        scanFragment.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addDictSearchFragment(DictSearchFragment.STATE_VIEW_SCAN_SEARCH, scanFragment.getWordInfo());
                getFragmentManager().popBackStackImmediate(ScanFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                search(scanFragment.getWordInfo(), 300);
            }
        });
        scanFragment.setExitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        DictManagerControler.addStackFragmentWithAnim(getFragmentManager().beginTransaction(),
                R.id.activity_main_content,
                ScanFragment.class.getSimpleName(),
                ScanFragment.class.getSimpleName(),
                scanFragment, R.anim.right_in, 0, 0, R.anim.right_out);
    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        if (enter && nextAnim != 0) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            if (animation != null) {
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(STATE_INIT);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            return animation;
        } else {
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(STATE_INIT, 300);
            }
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onDestroyView() {
        if (popupListWindow != null) {
            popupListWindow.dismiss();
        }
        if (mPopupMenu != null) {
            mPopupMenu.dismiss();
        }
        if (searchEditText != null) {
            hideSoftInput(searchEditText);
        }
        DictWordItemFragment dictWordItemFragment = (DictWordItemFragment) getFragmentManager()
                .findFragmentByTag(DictWordItemFragment.class.getSimpleName());
        if (dictWordItemFragment != null) {
            Fragment wordContentFragment = dictWordItemFragment.getCurrentWordContentFragment();
            if (wordContentFragment instanceof DictWordContentFragment) {
                ((DictWordContentFragment) wordContentFragment).dismissAllDialog();
            } else if (wordContentFragment instanceof DictAnimationWordContentFragment) {
                ((DictAnimationWordContentFragment) wordContentFragment).dismissAllDialog();
            } else if (wordContentFragment instanceof DictStrokeWordFragment) {
                ((DictStrokeWordFragment) wordContentFragment).dismissAllDialog();
            }
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tempSearchWorldInfo = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        /*if (checkDictWordSearch != null) {
            checkDictWordSearch.dissMissHanziDataDialog();
        }*/
    }

    @Override
    public void onSelected(int dictID, String word, int worldIndex) {
        hideSoftInput(searchEditText);
//        searchWord(dictID, word, worldIndex, true);
        sendSearchWord(dictID, word, worldIndex, true, null);
    }

    @Override
    public void onDismiss() {
//        getView().postDelayed(dismissOverlayRunnable, 100);
        getView().findViewById(R.id.fragment_search_over_lay).setVisibility(View.GONE);
//        hideSoftInput(searchEditText);
    }

    private void init() {
        popupListWindow = new DictListPopupWindow(getActivity(),
                getView().findViewById(R.id.fragment_search_box).getWidth(),
                getView().findViewById(R.id.fragment_main_item_content).getHeight());
        popupListWindow.setOnSelectedItemWordListener(DictSearchFragment.this);
        popupListWindow.setAnchor(getView().findViewById(R.id.fragment_search_box));

        if (plugWorldInfo != null) {
            sendSearchWord(plugWorldInfo.dictID, plugWorldInfo.world, plugWorldInfo.index, true, null);
            tempSearchWorldInfo = new SearchWorldInfo();
            tempSearchWorldInfo.dictID = -1;
            tempSearchWorldInfo.world = plugWorldInfo.world;
            tempSearchWorldInfo.index = -1;
//            searchWord(plugWorldInfo.dictID, plugWorldInfo.world, plugWorldInfo.index, true);
        } else {
            DictRecordWordFragment dictRecordWordFragment = new DictRecordWordFragment();
            dictRecordWordFragment.setOnRecordWordSearchListener(new DictRecordWordFragment.OnRecordWordSearchListener() {
                @Override
                public void loadSuccess() {}

                @Override
                public void clearLatelyWord() {}

                @Override
                public void loadWord(int dictID, String word, int index) {
                    hideSoftInput(searchEditText);
                    /*PlugWorldInfo plugWorldInfo2 = new PlugWorldInfo();
                    plugWorldInfo2.dictID = dictID;
                    plugWorldInfo2.index = index;
                    plugWorldInfo2.world = word;
                    DictWordItemFragment dictWordItemFragment2 = new DictWordItemFragment();
                    menuController.setController(dictWordItemFragment2);
//                            dictWordItemFragment2.initBackground(plugWorldInfo2, getFragmentManager(), getContext());
                    dictWordItemFragment2.init(plugWorldInfo2);
                        *//*if (getFragmentManager().findFragmentByTag(DictRecordWordFragment.class.getSimpleName()) != null) {
                            getFragmentManager().popBackStackImmediate();
                        }*//*
                    setSearchText(word);
                    DictManagerControler.replaceFragment(getFragmentManager().beginTransaction(),
                            R.id.fragment_main_item_content, DictWordItemFragment.class.getSimpleName(),
                            DictWordItemFragment.class.getSimpleName(), dictWordItemFragment2);
                    loadDataFinish();*/
                    sendSearchWord(dictID, word, index, true, null);
                }
            });
            dictRecordWordFragment.startSearchRecordWord(getActivity().getContentResolver(), getFragmentManager(), getActivity());
        }
    }

    private void search() {
        EditText editText = (EditText) getView().findViewById(R.id.fragment_search_edittext);
        if (editText.getText().toString().length() > 0) {
            search(editText.getText().toString());
        } else {
            CustomToast.getInstance(getContext()).showToast(getString(R.string.search_tip));
        }
    }

    private void search(String str) {
        removeSearch();
        SearchWorldInfo searchWorldInfo = new SearchWorldInfo();
        searchWorldInfo.dictID = -1;
        searchWorldInfo.index = -1;
        searchWorldInfo.needSetText = false;
        searchWorldInfo.world = str;
        sendMessage(DictSearchHandler.TYPE_SEARCH_WORLD, searchWorldInfo);
    }

    private void search(String str, int delayMillis) {
        removeSearch();
        SearchWorldInfo searchWorldInfo = new SearchWorldInfo();
        searchWorldInfo.dictID = -1;
        searchWorldInfo.index = -1;
        searchWorldInfo.needSetText = false;
        searchWorldInfo.world = str;
        sendMessage(DictSearchHandler.TYPE_SEARCH_WORLD, searchWorldInfo, delayMillis);
    }

    private void search(PlugWorldInfo plugWorldInfo, int delayMillis) {
        removeSearch();
        SearchWorldInfo searchWorldInfo = new SearchWorldInfo();
        searchWorldInfo.dictID = plugWorldInfo.dictID;
        searchWorldInfo.index = plugWorldInfo.index;
        searchWorldInfo.needSetText = false;
        searchWorldInfo.world = plugWorldInfo.world;
        sendMessage(DictSearchHandler.TYPE_SEARCH_WORLD, searchWorldInfo, delayMillis);
    }

    private void removeSearch() {
        if (dictSearchHandler != null) {
            dictSearchHandler.removeMessages(DictSearchHandler.TYPE_RECOGNITE_SEARCH);
        }
    }

    private void sendMessage(int what, Object data) {
        if (dictSearchHandler != null) {
            dictSearchHandler.sendMessages(what, data, 0, 0, 200);
        }
    }

    private void sendMessage(int what, Object data, int delayMills) {
        if (dictSearchHandler != null) {
            dictSearchHandler.sendMessages(what, data, 0, 0, delayMills);
        }
    }

    private void showSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    private void hideSoftInput(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.setEnabled(false);
            view.setEnabled(true);
        }
    }

    private void handleHanziDataFinish() {
        sendMessages(STATE_LOAD_TEXT, null, tempHanziBundle, 0);
        tempHanziBundle = null;
        dismissLoadingDialog();
    }

    private void sendSearchWord(int dictID, String word, int worldIndex, boolean needSetText, SearchWorldInfo searchWorldInfo) {

        if (word != null && word.length() > 0) {
            try {
                emojiMatcher.reset(word);
                word = emojiMatcher.replaceAll("");
                if (word != null
                        && word.length() == 0) {
//                    CustomToast.getInstance(getActivity()).showToast(getString(R.string.illegal_text_tip));
                    word="!";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_DICT_ID, dictID);
        bundle.putString(EXTRA_WORLD, word);
        bundle.putInt(EXTRA_WORLD_INDEX, worldIndex);
        bundle.putBoolean(EXTRA_NEED_SET_TEXT, needSetText);

        if (isNeedUpdateHanziData()
                && word != null) {
            /*if (getActivity() != null) {
                Intent intent = new Intent();
                intent.setAction(HanziDataService.ACTION_UPDATE);
                intent.setPackage(getActivity().getPackageName());
                getActivity().startService(intent);
            }*/
            if (CheckHanziData.getInstance() != null) {
                CheckHanziData.getInstance().check();
            }
            showLoading();
            tempHanziBundle = bundle;
        } else {
            sendMessages(STATE_LOAD_TEXT, searchWorldInfo, bundle, 100);
        }

    }

    private void sendMessages(int what, Object obj, Bundle bundle, int delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        msg.setData(bundle);
        if (mHandler != null && isResumed()) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendMessageDelayed(msg, delayMillis);
        }
    }

    private void searchWord(int dictID, String word, final int worldIndex, boolean needSetText) {
        if (dictID != -1 && worldIndex != -1) {
            hideSoftInput(searchEditText);
            if (needSetText) {
                setSearchText(word);
            }

            PlugWorldInfo plugWorldInfo = new PlugWorldInfo();
            plugWorldInfo.dictID = dictID;
            plugWorldInfo.index = worldIndex;
            plugWorldInfo.world = word;
            DictWordItemFragment dictWordItemFragment = new DictWordItemFragment();
            menuController.setController(dictWordItemFragment);
            dictWordItemFragment.init(plugWorldInfo);

            /*if (getFragmentManager().findFragmentByTag(DictWordItemFragment.class.getSimpleName()) != null
                    || getFragmentManager().findFragmentByTag(DictHotWord2Fragment.class.getSimpleName()) != null
                    || getFragmentManager().findFragmentByTag(DictRecordWordFragment.class.getSimpleName()) != null) {
                getFragmentManager().popBackStackImmediate();
            }*/

            DictManagerControler.replaceFragment(getFragmentManager().beginTransaction(),
                    R.id.fragment_main_item_content, DictWordItemFragment.class.getSimpleName(),
                    DictWordItemFragment.class.getSimpleName(), dictWordItemFragment);
            loadDataFinish();

        } else if (word != null && word.length() > 0) {
            hideSoftInput(searchEditText);
            PlugWorldInfo plugWorldInfo = new PlugWorldInfo();
            plugWorldInfo.dictID = dictID;
            plugWorldInfo.index = worldIndex;
            plugWorldInfo.world = word;
            DictWordItemFragment dictWordItemFragment = new DictWordItemFragment();
            menuController.setController(dictWordItemFragment);
            dictWordItemFragment.initBackground(plugWorldInfo, getFragmentManager(), getActivity());
            dictWordItemFragment.setOnDictWordItemFragmentListener(new DictWordItemFragment.OnDictWordItemFragmentListener() {
                @Override
                public void loadSuccess(DictWordItemFragment dictWordItemFragment) {
                    DictManagerControler.replaceFragment(getFragmentManager().beginTransaction(),
                            R.id.fragment_main_item_content, DictWordItemFragment.class.getSimpleName(),
                            DictWordItemFragment.class.getSimpleName(), dictWordItemFragment);
                    loadDataFinish();
                }

                @Override
                public void loadFailed() {
                    if (getView() != null) {
                        hideMenu(getView());
                    }
                    DictHotWord2Fragment hotWord2Fragment = new DictHotWord2Fragment();
                    hotWord2Fragment.startSearchHotWord(getActivity().getContentResolver(), getFragmentManager(), getActivity());
                    hotWord2Fragment.setOnHotWord2SearchListener(new DictHotWord2Fragment.OnHotWord2SearchListener() {
                        @Override
                        public void loadSuccess() {
                        }

                        @Override
                        public void loadFailed() {
                            if (getActivity() != null) {
                                CustomToast.getInstance(getActivity()).showToast(getActivity().getString(R.string.search_world_failed));
                            }
                            if (getFragmentManager() != null) {
                                Fragment fragment = getFragmentManager().findFragmentByTag(DictHotWord2Fragment.class.getSimpleName());
                                if (fragment != null) {
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.remove(fragment);
                                    ft.commit();
                                }
                            }
                        }

                        @Override
                        public void loadWord(int dictID, String word, int index) {
                            /*setSearchText(word);

                            PlugWorldInfo plugWorldInfo2 = new PlugWorldInfo();
                            plugWorldInfo2.dictID = dictID;
                            plugWorldInfo2.index = index;
                            plugWorldInfo2.world = word;
                            DictWordItemFragment dictWordItemFragment2 = new DictWordItemFragment();
                            menuController.setController(dictWordItemFragment2);
//                            dictWordItemFragment2.initBackground(plugWorldInfo2, getFragmentManager(), getContext());
                            dictWordItemFragment2.init(plugWorldInfo2);
                            *//*if (getFragmentManager().findFragmentByTag(DictHotWord2Fragment.class.getSimpleName()) != null) {
                                getFragmentManager().popBackStackImmediate();
                            }*//*
                            DictManagerControler.replaceFragment(getFragmentManager().beginTransaction(),
                                    R.id.fragment_main_item_content, DictHotWord2Fragment.class.getSimpleName(),
                                    DictHotWord2Fragment.class.getSimpleName(), dictWordItemFragment2);
                            loadDataFinish();*/
                            sendSearchWord(dictID, word, index, true, null);
                        }
                    });
                }
            });
        }
    }

    private void setSearchText(String str) {
        EditText editText = ((EditText) getView().findViewById(R.id.fragment_search_edittext));
        editText.removeTextChangedListener(textWatcher);
        editText.setText(PhoneticUtils.checkString(str));
        Editable result = editText.getEditableText();
        if (result != null) {
            editText.setSelection(result.length());
        }
        editText.addTextChangedListener(textWatcher);
        getView().findViewById(R.id.fragment_search_delete).setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        Fragment fragment = getFragmentManager().findFragmentByTag(LoadingFragment.class.getSimpleName());
        if (fragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }
        fragment = new LoadingFragment();
        DictManagerControler.replaceFragment(getFragmentManager().beginTransaction(),
                R.id.fragment_main_item_content, DictWordItemFragment.class.getSimpleName(),
                LoadingFragment.class.getSimpleName(), fragment);
    }

    @Override
    public void onResult(String text) {
        EditText editText = (EditText) getView().findViewById(R.id.fragment_search_edittext);
        if (editText != null) {
            setSearchText(text);
            sendMessage(DictSearchHandler.TYPE_RECOGNITE_SEARCH, null, 1 * 1000);
        }
    }

    @Override
    public void onGlobalLayout() {
        if (popupListWindow != null && getView() != null) {
            if (getView().findViewById(R.id.fragment_main_item_content) != null) {
                popupListWindow.update(getView().findViewById(R.id.fragment_main_item_content).getHeight());
            }
        }
    }

    private class DictSearchHandler extends BaseHandler<DictSearchFragment> {

        final static int MATCH_WORD_FINISH = 0x01;
        final static int TYPE_SEARCH_WORLD = MATCH_WORD_FINISH + 1;
        final static int TYPE_RECOGNITE_SEARCH = TYPE_SEARCH_WORLD + 1;

        public DictSearchHandler(DictSearchFragment dictSearchFragment) {
            super(dictSearchFragment);
        }

        @Override
        public void handleMessage(Message msg) {

            DictSearchFragment instance = getReference().get();
            if (dictSearchHandler == null || instance == null) {
                return;
            }

            switch (msg.what) {

                case MATCH_WORD_FINISH:
                    if (!isResumed()) {
                        break;
                    }
                    DictListPopupWindow popupWindow = null;
                    if (instance.popupListWindow != null) {
                        popupWindow = instance.popupListWindow;
                    }
                    /*if (popupWindow != null) {
                        popupWindow.dismiss();
                    }*/
                    if (searchWordTag != null && msg.getData() != null
                            && msg.getData().getString(BUNDLE_DEFAULT_STRING_KEY) != null) {
                        if (searchWordTag.contentEquals(msg.getData().getString(BUNDLE_DEFAULT_STRING_KEY))) {
                            if (msg.obj instanceof List) {
                                List<PlugWorldInfo> data = (List<PlugWorldInfo>) msg.obj;
                                if (popupWindow != null) {
                                    if (data.size() > 0) {
                                        popupWindow.showDropWindow(data, 2, getView().findViewById(R.id.fragment_main_item_content).getHeight());
                                        getView().removeCallbacks(dismissOverlayRunnable);
                                        Animation animation = getView().findViewById(R.id.fragment_search_over_lay).getAnimation();
                                        if (animation != null) {
                                            animation.cancel();
                                        }
                                        getView().findViewById(R.id.fragment_search_over_lay).setVisibility(View.VISIBLE);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    break;

                case TYPE_SEARCH_WORLD:
                    if (msg.obj instanceof SearchWorldInfo) {
                        SearchWorldInfo searchWorldInfo = (SearchWorldInfo) msg.obj;
                        if (tempSearchWorldInfo != null) {
                            if (tempSearchWorldInfo.index == searchWorldInfo.index
                                    && tempSearchWorldInfo.dictID == searchWorldInfo.dictID
                                    && tempSearchWorldInfo.world.contentEquals(searchWorldInfo.world)
                                    && tempSearchWorldInfo.needSetText == searchWorldInfo.needSetText) {
                                hideSoftInput(searchEditText);
                                return;
                            }
                        }
                        /*instance.searchWord(searchWorldInfo.dictID,
                                searchWorldInfo.world,
                                searchWorldInfo.index,
                                searchWorldInfo.needSetText);*/
                        instance.sendSearchWord(searchWorldInfo.dictID,
                                searchWorldInfo.world,
                                searchWorldInfo.index,
                                searchWorldInfo.needSetText, searchWorldInfo);
                    }
                    break;

                case TYPE_RECOGNITE_SEARCH:
                    instance.mState = STATE_VIEW_VOICE_RECOGNITE_SEARCH_2;
                    instance.search();
                    break;
            }

        }
    }

    private class SearchWordRange implements Runnable {

        String tag;
        String text;
        boolean isStop = false;
        DictWordSearch dictWordSearch;

        SearchWordRange(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            List<PlugWorldInfo> data = null;
            dictWordSearch = new DictWordSearch();
            switch (DictWordSearch.getDictType(text)) {
                case DictWordSearch.DICT_TYPE_ENG:
                    if (dictWordSearch != null) {
                        if (dictWordSearch.open(DictIOFile.ID_LWDICT, getActivity(), null)) {
                            data = getData(text);
                        }
                    }
                    break;
                case DictWordSearch.DICT_TYPE_CHI:
                    if (dictWordSearch != null) {
                        if (dictWordSearch.open(DictIOFile.ID_XDDICT, getActivity(), null)) {
                            data = getData(text);
                        }
                    }
                    break;
            }
            dictWordSearch.close();
            if (!isStop && dictSearchHandler != null) {
                dictSearchHandler.sendMessages(DictSearchHandler.MATCH_WORD_FINISH, tag, data, 0, 0, 0);
            }
        }

        private List<PlugWorldInfo> getData(String text) {
            List<PlugWorldInfo> data = null;
            int[] range = dictWordSearch.getSearchKeyRange(text);
            if (range != null) {
                data = new ArrayList<PlugWorldInfo>();
                for (int i : range) {
                    if (isStop) {
                        return data;
                    }
                    PlugWorldInfo plugWorldInfo = new PlugWorldInfo();
                    if (dictWordSearch != null) {
                        plugWorldInfo.world = dictWordSearch.getKeyWord(i);
                    } else {
                        break;
                    }
                    plugWorldInfo.index = i;
                    plugWorldInfo.dictID = dictWordSearch.getDictId();
                    data.add(plugWorldInfo);
                }
            }
            return data;
        }
    }

    private class SearchWorldInfo {
        int dictID;
        int index;
        boolean needSetText;
        String world;
    }
}
