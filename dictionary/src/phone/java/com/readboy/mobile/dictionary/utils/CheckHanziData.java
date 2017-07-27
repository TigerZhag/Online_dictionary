package com.readboy.mobile.dictionary.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.readboy.HanziManager.HanziDataManager;
import com.readboy.depict.data.DictData;
import com.readboy.mobile.dictionary.fragment.BaseFragment;

/**
 * Created by 123 on 2016/8/23.
 */

public class CheckHanziData {

    public static boolean isLoading = false;
//    private DictData mDictData;

    public final static String ACTION_UPDATE = "com.readboy.mobile.dictionary.ACTION_UPDATE_HANZI";
//    public final static String EXTRA_CHECK = "check";

    private final int STATE_LOAD_DATA_FINISH = 0x01;

    private DictData dictData;
    private Context mContext;

    private static CheckHanziData instance;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                switch (msg.what) {
                    case STATE_LOAD_DATA_FINISH:
                        Intent intent = new Intent(BaseFragment.ACTION_LOADING_HANZI_DATA_FINISH);
                        if (mContext != null) {
                            mContext.sendBroadcast(intent);
                        }
                        break;
                }
            }

        }
    };

    public static CheckHanziData getInstance() {
        return instance;
    }

    public static void init(Context mContext) {
        if (instance == null) {
            instance = new CheckHanziData(mContext);
        }
    }

    CheckHanziData(Context mContext) {
        this.mContext = mContext;
        if (mContext != null) {
//            mDictData = new DictData(mContext);
            dictData = new DictData(mContext);
        }
    }

    public boolean isNeedUpdate() {
        if (mContext != null && dictData != null) {
            String olDHanziversion = dictData.getHanziversion();
            String newHanziversion = HanziDataManager.getVersion(mContext);
            if (olDHanziversion == null ||
                    newHanziversion == null ||
                    !olDHanziversion.equals(newHanziversion)) {
                return true;
            }
        }
        return false;
    }

    public void check() {
        handleAction(ACTION_UPDATE);
    }

    private void handleAction(String action) {
        if (action.contentEquals(ACTION_UPDATE)) {
            if (isNeedUpdate()) {
                boolean result = checking();
            }
        }
    }

    /*private  boolean isNeedUpdate() {
        String hanziversion = dictData.getHanziversion();
        if (hanziversion == null ||
                HanziDataManager.getVersion(mContext) == null ||
                !hanziversion.equals(HanziDataManager.getVersion(mContext))) {
            return true;
        }
        return false;
    }*/

    private boolean checking() {
        if (!CheckHanziData.isLoading) {
            CheckHanziData.isLoading = true;
            if (HanziDataManager.getDataState(mContext) == 0) {
                if (listener != null) {
                    listener.updataFinish(true);
                }
            } else {
                HanziDataManager.updateCheck(mContext, listener, false);
            }
            return true;
        }
        return false;
    }

    private HanziDataManager.UpdateListener listener = new HanziDataManager.UpdateListener() {
        @Override
        public void updataFinish(boolean b) {
            CheckHanziData.isLoading = false;
            if (mContext != null) {
                dictData.updataTable();
                ContentValues contentValues = new ContentValues();
                contentValues.put(DictData.HANZI_VERSION, HanziDataManager.getVersion(mContext));
                dictData.insertWord(contentValues);
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(STATE_LOAD_DATA_FINISH);
                }
            }
        }
    };

}
