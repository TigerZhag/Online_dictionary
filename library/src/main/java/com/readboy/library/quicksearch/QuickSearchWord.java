package com.readboy.library.quicksearch;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.readboy.library.io.DictIOFile;
import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.library.quicksearch.filter.FilterCharManager;
import com.readboy.library.search.DictWordSearch;
import com.readboy.library.utils.PhoneticUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 123 on 2016/7/12.
 */

public class QuickSearchWord {

    public final int STATUS_SUCCESS = 0x01;
    public final int STATUS_ERROR = STATUS_SUCCESS + 1;

    private ExecutorService executors;
    private Context mContext;

    private ResultHandler mHandler = new ResultHandler(this);
    private YoudaoTranslateEngine youdaoTranslateEngine;

    private OnQuickSearchListener listener;

    private int maxCount = -1;

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public void setListener(OnQuickSearchListener listener) {
        this.listener = listener;
    }

    public QuickSearchWord(Context mContext) {
        this.mContext = mContext;
        executors = Executors.newSingleThreadExecutor();
        youdaoTranslateEngine = new YoudaoTranslateEngine(mContext);
    }

    public void search(String word) {
        ResultRunnable resultRunnable = new ResultRunnable(word);
        executors.execute(resultRunnable);
    }

    public void search(int index) {
        ResultRunnable resultRunnable = new ResultRunnable(index);
        executors.execute(resultRunnable);
    }

    public void destroy() {
        executors = null;
        listener = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
    }

    private void sendMessages(int what, int status, Object result) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = status;
        msg.obj = result;
        if (mHandler != null) {
            mHandler.sendMessage(msg);
        }
    }

    private class ResultHandler extends Handler {

        final static int STATUS_RESULT = 0x01;
        WeakReference<QuickSearchWord> weakReference;

        public ResultHandler(QuickSearchWord instance) {
            weakReference = new WeakReference<QuickSearchWord>(instance);
        }

        @Override
        public void handleMessage(Message msg) {

            QuickSearchWord instance = weakReference.get();
            if (instance != null) {
                switch (msg.what) {

                    case STATUS_RESULT:
                        if (listener != null && msg.obj instanceof QuickSearchWordInfo) {
                            QuickSearchWordInfo info = (QuickSearchWordInfo) msg.obj;
                            PlugWorldInfo plugWorldInfo = info.plugWorldInfo;
                            info.plugWorldInfo = null;
                            listener.onResult(msg.arg1, info.keyWord, info.expain, plugWorldInfo);
                        }
                        break;

                }
            }

        }
    }

    private class ResultRunnable implements Runnable {

        String searchWord;
        int index;

        ResultRunnable(String searchWord) {
            this.searchWord = searchWord;
        }

        ResultRunnable(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            findWord();
//            test();
        }

        void findWord() {
            int youdaoIndexID = find(youdaoTranslateEngine, searchWord);
            String youdaoKeyWord = youdaoTranslateEngine.getWord(youdaoIndexID);
            if (youdaoIndexID != -1 && youdaoKeyWord != null) {
                QuickSearchWordInfo quickSearchWordInfo = new QuickSearchWordInfo();
                quickSearchWordInfo.index = youdaoIndexID;
                quickSearchWordInfo.keyWord = youdaoKeyWord;
                quickSearchWordInfo.expain = youdaoTranslateEngine.getMeaning(youdaoIndexID);

                PlugWorldInfo plugWorldInfo = findDict(mContext, DictIOFile.ID_YHDICT, youdaoKeyWord);
                if (plugWorldInfo == null) {
                    plugWorldInfo = findDict(mContext, DictIOFile.ID_LWDICT, youdaoKeyWord);
                }
                quickSearchWordInfo.plugWorldInfo = plugWorldInfo;
                sendMessages(ResultHandler.STATUS_RESULT, STATUS_SUCCESS, quickSearchWordInfo);
            }
            sendMessages(ResultHandler.STATUS_RESULT, STATUS_ERROR, null);
        }

        PlugWorldInfo findDict(Context context, int dictID, String searchStr) {
            DictSearch dictWordSearch = new DictSearch();
            try {
                if (dictWordSearch.open(dictID, context, null) && searchStr != null) {
                    int index = dictWordSearch.getSearchKeyIndexByAccurate(searchStr);
                    if (index != -1) {
                        String dictKey = dictWordSearch.getKeyWord(index);
                        String lowYoudaoKeyWord = getFilterEnglishLowerLetter(searchStr);
                        String lowDictKey = getFilterEnglishLowerLetter(dictKey);
                        if (lowYoudaoKeyWord != null && lowDictKey != null
                                && lowYoudaoKeyWord.contentEquals(lowDictKey)) {
                            PlugWorldInfo plugWorldInfo = new PlugWorldInfo();
                            plugWorldInfo.dictID = dictID;
                            plugWorldInfo.index = index;
                            plugWorldInfo.world = dictKey;
                            return plugWorldInfo;
                        }
                    }
                }
            } finally {
                if (dictWordSearch != null) {
                    dictWordSearch.close();
                }
            }
            return null;
        }

        int find(SearchEngine searchEngine, String findStr) {
            int index = searchEngine.find(findStr);
            if (index != -1) {
                String lowerFindStr = getFilterEnglishLowerLetter(findStr);
                index = FilterCharManager.filter(searchEngine, index, lowerFindStr);
            }
            return index;
        }

        private String getFilterEnglishLowerLetter(String str) {
            String result = "";
            if (str != null) {
                for (char c : str.toCharArray()) {
                    char rc = getLowerEnglishChar(c);
                    if (rc != ' ') {
                        result += rc;
                    }
                }
            }
            return result;
        }

        private char getLowerEnglishChar(char c) {
            if (c >= 'a' && 'z' >= c) {
                return c;
            } else if (c >= 'A' && 'Z' >= c) {
                c += 32;// 转为小写字母
                return c;
            }
            return ' ';
        }

        void test() {
            DictWordSearch dictWordSearch = new DictWordSearch();
            int dictID = DictIOFile.ID_LWDICT;
            try {
                if (dictWordSearch.open(dictID, mContext, null)) {
                    String key = dictWordSearch.getKeyWord(index);
                    if (dictWordSearch.getAllKeyCount() > index) {
                        byte[] buffer = dictWordSearch.getExplainByte(index);
                        if (buffer != null) {
                            int start = dictWordSearch.getExplainByteStart(buffer);
                            for (int i = 0; i < start; i++) {
                                buffer[i] = 0;
                            }
                            DictWordStringAnalyze dictWordStringAnalyze = new DictWordStringAnalyze(key, buffer, maxCount);
                            String result = dictWordStringAnalyze.getResult();
                            sendMessages(ResultHandler.STATUS_RESULT, STATUS_SUCCESS, result);
                        }
                    }
                }
            } finally {
                dictWordSearch.close();
            }
        }
    }

    private class QuickSearchWordInfo {
        //        int dictID;
        int index;
        String keyWord;
        String expain;
        PlugWorldInfo plugWorldInfo;
    }

    public interface OnQuickSearchListener {
        void onResult(int status, String keyWord, String expain, PlugWorldInfo wordInfo);
    }
}
