package com.readboy.Dictionary;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.readboy.dict.DictWordView;
import com.readboy.library.io.DictFile;
import com.readboy.library.provider.info.PlugWorldInfo;
import com.readboy.library.quicksearch.DictWordStringAnalyze;
import com.readboy.library.quicksearch.QuickSearchWord;
import com.readboy.library.search.DictWordSearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Senny on 2016/6/23.
 */
public class TestActivity extends Activity implements QuickSearchWord.OnQuickSearchListener{

    private DictWordView dictWordView;
    private DictWordSearch dictWordSearch;
    private int currentIndex;

    private QuickSearchWord quickSearchWord;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            System.out.println("sgc: current " + currentIndex + " " + dictWordSearch.getAllKeyCount());
            if (dictWordSearch.getAllKeyCount() > currentIndex) {
                byte[] buffer = dictWordSearch.getExplainByte(currentIndex);
                int start = dictWordSearch.getExplainByteStart(buffer);
                for (int i=0;i<start;i++) {
                    buffer[i] = 0;
                }
                dictWordView.setDictWordText(buffer);
                currentIndex++;
                sendEmptyMessageDelayed(0, /*300*/1 * 1000);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);

        dictWordView = (DictWordView) findViewById(R.id.test_content_view);
        dictWordView.setChangeLineTextSize(0, 200);
//        new Thread(new TestRunnable()).start();
//        cut();
        normal();
    }

    private void cut() {
        quickSearchWord = new QuickSearchWord(this);
        quickSearchWord.setMaxCount(100);
        quickSearchWord.setListener(this);
        quickSearchWord.search(0);
    }

    private void normal() {
        dictWordSearch = new DictWordSearch();
        dictWordSearch.open(DictFile.ID_LWDICT, this, null);

        mHandler.sendEmptyMessageDelayed(0, 10 * 1000);

        findViewById(R.id.test_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    currentIndex--;
                }
                byte[] buffer = dictWordSearch.getExplainByte(currentIndex);
                int start = dictWordSearch.getExplainByteStart(buffer);
                for (int i = 0; i < start; i++) {
                    buffer[i] = 0;
                }
                dictWordView.setDictWordText(buffer);
                System.out.println("sgc: previous " + currentIndex);
            }
        });

        findViewById(R.id.test_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex++;
                byte[] buffer = dictWordSearch.getExplainByte(currentIndex);
                int start = dictWordSearch.getExplainByteStart(buffer);
                for (int i=0;i<start;i++) {
                    buffer[i] = 0;
                }
                dictWordView.setDictWordText(buffer);
                System.out.println("sgc: next " + currentIndex);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onResult(int status, String keyWord, String expain, PlugWorldInfo wordInfo) {
        if (expain != null) {
            dictWordView.setDictWordText("", expain);
            /*dictWordView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    quickSearchWord.find(currentIndex);
                }
            }, 100);*/
//            currentIndex++;
//            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    private class TestRunnable implements Runnable {

        @Override
        public void run() {
            DictWordSearch dictWordSearch = new DictWordSearch();
            String str = "";
            System.out.println("sgc start!");
            File file = new File("/mnt/sdcard/key.txt");
            if (file.exists()) {
                file.delete();
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dictWordSearch.open(DictFile.ID_LWDICT, TestActivity.this, null)) {
                String key = "";
                int count = dictWordSearch.getAllKeyCount();
                for (int i = 0 ; count > i ; i++) {
                    key += dictWordSearch.getKeyWord(i) + "#";
                }
                append(file, key);

                System.out.println("sgc finish!");
                dictWordSearch.close();
            }
        }

        void append(File file, String str) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.append(str + "#");
                fileWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
