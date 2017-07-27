package com.readboy.mobile.dictionary.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readboy.Dictionary.R;
import com.readboy.depict.data.HanziPackage;
import com.readboy.depict.widget.HanziDemonstrationView;
import com.readboy.depict.widget.HanziDepictView;
import com.readboy.depict.widget.ScoreView;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.search.DictWordSound;
import com.readboy.mobile.dictionary.control.DictManagerControler;
import com.readboy.mobile.dictionary.utils.BaseHandler;
import com.readboy.mobile.dictionary.utils.CheckHanziData;
import com.readboy.sound.NewSound;

import java.io.IOException;

/**
 * Created by Senny on 2015/11/5.
 */
public class DictStrokeViewFragment extends BaseFragment implements HanziDepictView.OnEndListener, View.OnClickListener{

    private static final int DEFALUT_DELAY_MILLIES = 200;

    private static final int[] EVALUATION;
    private HanziPackage hanziPackage;
    private HanziDemonstrationView hanziDemonstrationView;
    private HanziDepictView hanziDepictView;
    private ScoreView scoreView;
    private View scoreViewBg;
//    private Sound mSound;
    private NewSound mSound;
    private DictWordSearch dictWordSearch;
    private DictWordSound dictWordSound;
    private TextView title;

    private View demoButton;
    private View writeButton;
    private View rewriteButton;
    private View soundButton;
    private View exitButton;
    private StrokeViewHandler strokeViewHandler = new StrokeViewHandler(this);

    private Message tempMessage;
    private boolean isActiviting;

//    private

    /*private BroadcastReceiver actionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action != null) {
                    if (action.contentEquals(ACTION_LOADING_HANZI_DATA_FINISH)) {
                        if (strokeViewHandler != null
                                && tempMessage != null) {
                            strokeViewHandler.sendMessage(tempMessage);
                        }
                        tempMessage = null;
                        dismissLoadingDialog();
                    }
                }
            }
        }
    };*/

    public void setStrokePackage(HanziPackage hanziPackage) {
        this.hanziPackage = hanziPackage;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dictWordSearch = new DictWordSearch();
        dictWordSearch.open(DictIOFile.ID_BHDICT, container.getContext(), null);
        dictWordSound = new DictWordSound();
        dictWordSound.open();
        View root = inflater.inflate(R.layout.fragment_stroke_view, null);
        hanziDemonstrationView = (HanziDemonstrationView) root.findViewById(R.id.fragment_stroke_view_demo);
        hanziDepictView = (HanziDepictView) root.findViewById(R.id.fragment_stroke_view_write);
        scoreView = (ScoreView) root.findViewById(R.id.fragment_stroke_view_score);
        scoreViewBg = root.findViewById(R.id.fragment_stroke_view_score_bg);
        hanziDemonstrationView.setVisibility(View.VISIBLE);
        /*hanziDemonstrationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (hanziDemonstrationView != null) {
                    hanziDemonstrationView.forceEnd();
                }
            }
        }, 50);*/
        hanziDepictView.setOnEndListener(this);


        demoButton = root.findViewById(R.id.fragment_stroke_view_button_demo);
        demoButton.setOnClickListener(this);
        writeButton = root.findViewById(R.id.fragment_stroke_view_button_write);
        writeButton.setOnClickListener(this);
        rewriteButton = root.findViewById(R.id.fragment_stroke_view_button_rewrite);
        rewriteButton.setOnClickListener(this);
        exitButton = root.findViewById(R.id.fragment_stroke_view_exit);
        exitButton.setOnClickListener(this);
        soundButton = root.findViewById(R.id.fragment_stroke_view_sound);
        soundButton.setOnClickListener(this);

        title = (TextView) root.findViewById(R.id.fragment_stroke_view_title);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*if (hanziDemonstrationView != null) {
            hanziDemonstrationView.setHanziWithoutSound(hanziPackage);
        }*/
        if (hanziDepictView != null) {
            hanziDepictView.setHanzi(hanziPackage);
        }

        sendMessagesRemoveCallback(R.id.fragment_stroke_view_button_demo, 400);
        sendMessages(StrokeViewHandler.HIDE_INPUT, 300);
    }

    @Override
    public void onResume() {
        isActiviting = true;
        super.onResume();
        if (CheckHanziData.getInstance() != null
                && CheckHanziData.getInstance().isNeedUpdate()) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
        if (hanziDemonstrationView != null) {
            hanziDemonstrationView.resume();
        }
        if (hanziDepictView != null) {
            hanziDepictView.resume();
        }

        /*IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_LOADING_HANZI_DATA_FINISH);
        if (getActivity() != null && actionReceiver != null) {
            getActivity().registerReceiver(actionReceiver, intentFilter);
        }*/
        if (mSound != null) {
            mSound.start();
        }
    }

    @Override
    public void onPause() {
        isActiviting = false;
        super.onPause();
        /*if (getActivity() != null && actionReceiver != null) {
            getActivity().unregisterReceiver(actionReceiver);
        }*/
        if (hanziDemonstrationView != null) {
            hanziDemonstrationView.pause();
        }
        if (hanziDepictView != null) {
            hanziDepictView.pause();
        }
        if (mSound != null && mSound.isPlaying()) {
            mSound.pause();
        }
    }

    @Override
    public void onDestroyView() {
        if (dictWordSearch != null) {
            dictWordSearch.close();
        }
        if (dictWordSound != null) {
            dictWordSound.close();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (strokeViewHandler != null) {
            strokeViewHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fragment_stroke_view_exit:
//                getFragmentManager().popBackStackImmediate();
                getActivity().finish();
                break;

            case R.id.fragment_stroke_view_sound:
                byte[] soundBytes;
                soundBytes = dictWordSearch.getHanziSoundBytes(hanziPackage.getHanziInfo().phonetic);
                if (soundBytes != null) {
                    if (hanziDemonstrationView != null && hanziDemonstrationView.getVisibility() == View.VISIBLE) {
                        hanziDemonstrationView.forceEnd();
                    }
                    dictWordSound.playNewSound(soundBytes, 0);
                    /*SoundAnimationFragment.show(getFragmentManager());*/
                    if (v.getBackground() instanceof AnimationDrawable) {
                        AnimationDrawable animationDrawable = (AnimationDrawable) v.getBackground();
                        animationDrawable.stop();
                        animationDrawable.start();
                    }
                }
                break;

            case R.id.fragment_stroke_view_button_demo:
                title.setText(R.string.stroke_demo);
                /*if (scoreView != null && scoreView.getVisibility() == View.VISIBLE) {
                    scoreView.setVisibility(View.GONE);
                }
                if (hanziDepictView != null && hanziDepictView.getVisibility() == View.VISIBLE) {
                    hanziDepictView.stop();
                    hanziDepictView.setVisibility(View.GONE);
                }
                if (hanziDemonstrationView != null && hanziDemonstrationView.getVisibility() != View.VISIBLE) {
                    hanziDemonstrationView.setVisibility(View.VISIBLE);
                }
                hanziDemonstrationView.setHanzi(hanziPackage);
                getView().findViewById(R.id.fragment_stroke_view_sound).setVisibility(View.VISIBLE);*/
                sendMessagesRemoveCallback(R.id.fragment_stroke_view_button_demo);
                break;

            case R.id.fragment_stroke_view_button_write:
                title.setText(R.string.stroke_write);
                /*if (hanziDemonstrationView != null && hanziDemonstrationView.getVisibility() == View.VISIBLE) {
                    hanziDemonstrationView.stop();
                    hanziDemonstrationView.setVisibility(View.GONE);
                }
                if (hanziDepictView != null && hanziDepictView.getVisibility() != View.VISIBLE) {
                    hanziDepictView.setVisibility(View.VISIBLE);
                }
                hanziDepictView.depict();*/
                sendMessagesRemoveCallback(R.id.fragment_stroke_view_button_write);
                break;

            case R.id.fragment_stroke_view_button_rewrite:
                title.setText(R.string.stroke_write);
                sendMessagesRemoveCallback(R.id.fragment_stroke_view_button_rewrite);
                break;
        }

    }

    @Override
    public void onDepictEnd(int score) {
        if (rewriteButton != null) {
            rewriteButton.setVisibility(View.VISIBLE);
        }
        if (writeButton != null) {
            writeButton.setVisibility(View.INVISIBLE);
        }
        if (soundButton != null) {
            soundButton.setVisibility(View.INVISIBLE);
        }
        title.setText(R.string.stroke_score);
        hanziDemonstrationView.setVisibility(View.GONE);
        hanziDepictView.setVisibility(View.GONE);
        scoreView.setVisibility(View.VISIBLE);
        scoreViewBg.setVisibility(View.VISIBLE);
        scoreView.setScore(score);
        byte level;
        if(score < 60) {
            level = 0;
        } else if(score < 80) {
            level = 1;
        } else {
            level = 2;
        }

        this.mSound = new NewSound();

        try {
            this.mSound.setDataSource(this.getActivity(), EVALUATION[level]);
            this.mSound.start();
            this.mSound.setOnCompletionListener(new NewSound.OnCompletionListener() {
                @Override
                public void onComplete(NewSound sound) {
                    DictStrokeViewFragment.this.mSound.release();
                    DictStrokeViewFragment.this.mSound = null;
                }
            });
        } catch (IOException var5) {
            this.mSound.release();
            this.mSound = null;
        }
    }

    private void sendMessagesRemoveCallback(int what) {
        if (strokeViewHandler != null) {
            strokeViewHandler.removeCallbacksAndMessages(null);
            strokeViewHandler.sendMessages(what, null, 0, 0, DEFALUT_DELAY_MILLIES);
        }
    }

    private void sendMessagesRemoveCallback(int what, int delayMillis) {
        if (strokeViewHandler != null) {
            strokeViewHandler.removeCallbacksAndMessages(null);
            strokeViewHandler.sendMessages(what, null, 0, 0, delayMillis);
        }
    }

    private void sendMessages(int what, int delayMillis) {
        if (strokeViewHandler != null) {
            strokeViewHandler.sendMessages(what, null, 0, 0, delayMillis);
        }
    }

    static {
        EVALUATION = new int[]{com.readboy.depict.R.raw.level0, com.readboy.depict.R.raw.level1, com.readboy.depict.R.raw.level2};
    }

    private class StrokeViewHandler extends BaseHandler<DictStrokeViewFragment> {

        static final int HIDE_INPUT = 0x01;

        public StrokeViewHandler(DictStrokeViewFragment dictStrokeViewFragment) {
            super(dictStrokeViewFragment);
        }

        @Override
        public void handleMessage(Message msg) {

            DictStrokeViewFragment instance = getReference().get();
            if (instance == null) {
                return;
            }

            if (isNeedUpdateHanziData()) {
                /*if (getActivity() != null) {
                    Intent intent = new Intent();
                    intent.setAction(HanziDataService.ACTION_UPDATE);
                    intent.setPackage(getActivity().getPackageName());
                    getActivity().startService(intent);
                }*/
                if (CheckHanziData.getInstance() != null) {
                    CheckHanziData.getInstance().check();
                }
                tempMessage = Message.obtain(msg);
                showLoadingDialog(getActivity());
                return;
            }


            switch (msg.what) {
                case R.id.fragment_stroke_view_button_demo:
                    if (scoreView != null && scoreView.getVisibility() == View.VISIBLE) {
                        scoreView.setVisibility(View.GONE);
                        scoreViewBg.setVisibility(View.GONE);
                    }
                    if (rewriteButton != null && rewriteButton.getVisibility() == View.VISIBLE) {
                        rewriteButton.setVisibility(View.GONE);
                    }
                    if (writeButton != null && writeButton.getVisibility() != View.VISIBLE) {
                        writeButton.setVisibility(View.VISIBLE);
                    }
                    if (hanziDepictView != null && hanziDepictView.getVisibility() == View.VISIBLE) {
                        hanziDepictView.stop();
                        hanziDepictView.setVisibility(View.GONE);
                    }
                    if (hanziDemonstrationView != null && hanziDemonstrationView.getVisibility() != View.VISIBLE) {
                        hanziDemonstrationView.setVisibility(View.VISIBLE);
                    }
                    try {
                        hanziDemonstrationView.setHanzi(hanziPackage);
                        if (!isActiviting) {
                            hanziDemonstrationView.post(new Runnable() {
                                @Override
                                public void run() {
                                    hanziDemonstrationView.pause();
                                }
                            });
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    if (soundButton != null) {
                        soundButton.setVisibility(View.VISIBLE);
                    }
                    if (mSound != null && mSound.isPlaying()) {
                        mSound.stop();
                    }
                    break;

                case R.id.fragment_stroke_view_button_write:
                    hanziDepictView.stop();
                    if (hanziDemonstrationView != null && hanziDemonstrationView.getVisibility() == View.VISIBLE) {
                        hanziDemonstrationView.stop();
                        hanziDemonstrationView.setVisibility(View.GONE);
                    }
                    if (hanziDepictView != null && hanziDepictView.getVisibility() != View.VISIBLE) {
                        hanziDepictView.setVisibility(View.VISIBLE);
                    }
                    hanziDepictView.depict();
                    if (mSound != null && mSound.isPlaying()) {
                        mSound.stop();
                    }
                    if (strokeViewHandler != null) {
                        strokeViewHandler.removeMessages(R.id.fragment_stroke_view_button_demo);
                    }
                    break;

                case R.id.fragment_stroke_view_button_rewrite:
                    if (scoreView != null && scoreView.getVisibility() == View.VISIBLE) {
                        scoreView.setVisibility(View.GONE);
                        scoreViewBg.setVisibility(View.GONE);
                    }
                    if (hanziDemonstrationView != null && hanziDemonstrationView.getVisibility() == View.VISIBLE) {
                        hanziDemonstrationView.stop();
                        hanziDemonstrationView.setVisibility(View.GONE);
                    }
                    if (hanziDepictView != null && hanziDepictView.getVisibility() == View.GONE) {
                        hanziDepictView.setVisibility(View.VISIBLE);
                        hanziDepictView.depict();
                    }
                    if (rewriteButton != null) {
                        rewriteButton.setVisibility(View.INVISIBLE);
                    }
                    if (writeButton != null) {
                        writeButton.setVisibility(View.VISIBLE);
                    }
                    if (soundButton != null) {
                        soundButton.setVisibility(View.VISIBLE);
                    }
                    if (mSound != null && mSound.isPlaying()) {
                        mSound.stop();
                    }
                    break;

                case HIDE_INPUT:
                    DictManagerControler.getInstance().hideInput(getActivity());
                    break;
            }

        }
    }
}
