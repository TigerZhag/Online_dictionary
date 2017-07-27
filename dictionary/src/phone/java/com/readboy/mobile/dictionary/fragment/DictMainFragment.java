package com.readboy.mobile.dictionary.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.readboy.Dictionary.R;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.library.search.DictWordSearch;
import com.readboy.mobile.dictionary.AppConfigInfo;
import com.readboy.mobile.dictionary.activity.ScanActivity;
import com.readboy.mobile.dictionary.activity.ShowDataActivity;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.utils.BaseHandler;
import com.readboy.mobile.dictionary.view.DictListPopupWindow;
import com.readboy.mobile.dictionary.view.RippleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senny on 2015/10/27.
 */
public class DictMainFragment extends Fragment implements
        DictListPopupWindow.OnSelectedItemWordListener {

    private final String SHAREPREFERENCE_CONFIG = "config";
    private final String SHAREPREFERENCE_KEY_CLICK_TIP = "click_tip";
    private DictSearchHandler dictSearchHandler;
    private DictListPopupWindow popupListWindow1;
    private String searchWordTag;
    private TextWatcher textWatcher;
    private DictHotWordFragment dictHotWordFragment;
    private View homePageView;
    private RippleView rippleView;
    private DictSearchFragment dictSearchFragment;
//    private ScanFragment scanFragment;

    private boolean canAction = true;
    private int REQUEST_CODE_SCAN = 0x101;
    private int REQUEST_SHOW_DATA = REQUEST_CODE_SCAN + 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dictSearchHandler = new DictSearchHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, null);
        homePageView = root.findViewById(R.id.fragment_main_home_page);
        rippleView = (RippleView) root.findViewById(R.id.fragment_main_rippleview);
        rippleView.setEnabled(false);
        dictHotWordFragment = new DictHotWordFragment();

        root.findViewById(R.id.fragment_main_search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleView.startWaveAnimation(60 * AppConfigInfo.RATIO_DENSITY,
                        rippleView.getWidth() + 20 * AppConfigInfo.RATIO_DENSITY,
                        450, new RippleView.OnRippleViewAnimationListener() {
                    @Override
                    public void onAnimationEnd() {
                        if (canAction) {
//                            addDictSearchFragment(null);
                            addScanSearchFragment();
                            canAction = false;
                        }
                        int radius = getView().findViewById(R.id.fragment_main_ripple_target).getWidth() / 2;
                        if (dictSearchHandler != null) {
                            dictSearchHandler.sendMessages(DictSearchHandler.RIPPLEVIEW_LOADING, radius, 0, 0, 200);
                        }
                    }
                });
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHAREPREFERENCE_CONFIG, Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(SHAREPREFERENCE_KEY_CLICK_TIP, false).commit();
            }
        });

        root.post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                dictHotWordFragment.startSearchHotWord(getActivity().getContentResolver(), getFragmentManager(), getActivity());
                dictHotWordFragment.setOnHotWordSearchListener(new DictHotWordFragment.OnHotWordSearchListener() {
                    @Override
                    public void loadWord(int dictID, String word, int index) {
//                        searchWord(dictID, word, index, false);
                        if (canAction) {
                            PlugWorldInfo plugWorldInfo = new PlugWorldInfo();
                            plugWorldInfo.dictID = dictID;
                            plugWorldInfo.world = word;
                            plugWorldInfo.index = index;
                            addDictSearchFragment(plugWorldInfo);
                            canAction = false;
                        }
                    }
                });
                /*TestWordContentFragment testWordContentFragment = new TestWordContentFragment();
                testWordContentFragment.test(getFragmentManager(), getActivity());*/
                int radius = getView().findViewById(R.id.fragment_main_ripple_target).getWidth() / 2;
//                rippleView.startRepeatWaveAnimation(60, radius + 200, 2000);
                if (dictSearchHandler != null) {
                    dictSearchHandler.sendMessages(DictSearchHandler.RIPPLEVIEW_LOADING, radius, 0, 0, 200);
                }
            }
        });

        root.findViewById(R.id.fragment_main_text_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canAction) {
                    addDictSearchFragment(DictSearchFragment.STATE_VIEW_TEXT_SEARCH, null);
                    canAction = false;
                }
            }
        });

        root.findViewById(R.id.fragment_main_voice_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDictSearchFragment(DictSearchFragment.STATE_VIEW_VOICE_RECOGNITE_SEARCH_1, null);
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHAREPREFERENCE_CONFIG, Context.MODE_PRIVATE);
        boolean isTipShow = sharedPreferences.getBoolean(SHAREPREFERENCE_KEY_CLICK_TIP, true);
        return root;
    }

    @Override
    public void onSelected(int dictID, String word, int worldIndex) {
//        searchWord(dictID, word, worldIndex, true);

    }

    @Override
    public void onDismiss() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN
                || requestCode == REQUEST_SHOW_DATA) {
            reLoadData();
        }
    }

    private void addDictSearchFragment(PlugWorldInfo plugWorldInfo) {
        /*dictSearchFragment = new DictSearchFragment();
        dictSearchFragment.setWoid(plugWorldInfo);
        DictManagerControler.addStackFragmentWithAnim(getFragmentManager().beginTransaction(),
                R.id.activity_main_content,
                DictSearchFragment.class.getSimpleName(),
                DictSearchFragment.class.getSimpleName(),
                dictSearchFragment, R.anim.right_in, 0, 0, R.anim.right_out);*/
        showData(-1, plugWorldInfo);
    }

    private void addDictSearchFragment(int state, PlugWorldInfo plugWorldInfo) {
        /*dictSearchFragment = new DictSearchFragment();
        dictSearchFragment.setWoid(plugWorldInfo);
        dictSearchFragment.setState(state);
        DictManagerControler.addStackFragmentWithAnim(getFragmentManager().beginTransaction(),
                R.id.activity_main_content,
                DictSearchFragment.class.getSimpleName(),
                DictSearchFragment.class.getSimpleName(),
                dictSearchFragment, R.anim.right_in, 0, 0, R.anim.right_out);*/
        showData(state, plugWorldInfo);
    }

    private void showData(int state, PlugWorldInfo plugWorldInfo) {
        Intent intent = new Intent(getActivity(), ShowDataActivity.class);
        Bundle bundle = new Bundle();
        if (plugWorldInfo != null) {
            bundle.putInt(ShowDataActivity.EXTRA_DICT_ID, plugWorldInfo.dictID);
            bundle.putString(ShowDataActivity.EXTRA_WORLD, plugWorldInfo.world);
            bundle.putInt(ShowDataActivity.EXTRA_WORLD_INDEX, plugWorldInfo.index);
            intent.putExtra(ShowDataActivity.EXTRA_BUNDLE, bundle);
        }
        intent.putExtra(ShowDataActivity.EXTRA_STATE, state);
        startActivityForResult(intent, REQUEST_SHOW_DATA);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.fake_anim);
    }

    public void addScanSearchFragment() {
        /*final ScanFragment scanFragment = new ScanFragment();
        scanFragment.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate(ScanFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                addDictSearchFragment(DictSearchFragment.STATE_VIEW_SCAN_SEARCH, scanFragment.getWordInfo());
            }
        });
        scanFragment.setExitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
                reLoadData();
            }
        });
        DictManagerControler.addStackFragmentWithAnim(getFragmentManager().beginTransaction(),
                R.id.activity_main_content,
                ScanFragment.class.getSimpleName(),
                ScanFragment.class.getSimpleName(),
                scanFragment, R.anim.right_in, 0, 0, R.anim.right_out);*/
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.fake_anim);
    }

    public void reLoadData() {
        if (dictHotWordFragment != null && getActivity() != null) {
            dictHotWordFragment.reStart(getActivity().getContentResolver(), getFragmentManager(), getActivity());
        }
        canAction = true;
    }

    private class DictSearchHandler extends BaseHandler<DictMainFragment> {

        final static int MATCH_WORD_FINISH = 0x01;
        final static int RIPPLEVIEW_LOADING = MATCH_WORD_FINISH + 1;

        public DictSearchHandler(DictMainFragment dictMainFragment) {
            super(dictMainFragment);
        }

        @Override
        public void handleMessage(Message msg) {

            if (dictSearchHandler == null) {
                return;
            }

            switch (msg.what) {

                case MATCH_WORD_FINISH:
                    if (searchWordTag != null && msg.getData() != null
                            && msg.getData().getString(BUNDLE_DEFAULT_STRING_KEY) != null) {
                        if (searchWordTag.contentEquals(msg.getData().getString(BUNDLE_DEFAULT_STRING_KEY))) {

                            DictListPopupWindow popupWindow = null;
                            if (getReference().get().homePageView != null && getReference().get().homePageView.getVisibility() == View.VISIBLE) {
                                if (getReference() != null && getReference().get() != null
                                        && getReference().get().popupListWindow1 != null) {
                                    popupWindow = getReference().get().popupListWindow1;
                                }
                            }
                            if (popupWindow != null) {
                                popupWindow.dismiss();
                            }
                            if (msg.obj instanceof List) {
                                List<PlugWorldInfo> data = (List<PlugWorldInfo>) msg.obj;
                                if (popupWindow != null) {
                                    if (data.size() > 0) {
                                        popupWindow.showDropWindow(data, 2);
                                    }
                                }
                            }
                        }
                    }
                    break;

                case RIPPLEVIEW_LOADING:
                    if (msg.obj instanceof Integer) {
                        int radius = (Integer) msg.obj;
                        rippleView.startRepeatWaveAnimation(60 * AppConfigInfo.RATIO_DENSITY,
                                radius + 200 * AppConfigInfo.RATIO_DENSITY,
                                2000);
                    }
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

}
