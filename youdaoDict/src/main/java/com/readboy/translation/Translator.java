package com.readboy.translation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.readboy.translation.bean.TransResult;
import com.readboy.translation.database.DictDBHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Zhang shixin
 * @date 16-8-3.
 */
public class Translator {
    private static final String TAG = "Translator";
    private DictDBHelper dbHelper;

    private static String assetName = "english_words.db";
    private static String dbName = "english_words.db";

    private OnSearchListener callback;
    private ExecutorService executor;
    private Handler handler;

    private static boolean isInit = false;

    public Translator(Context context){
        dbHelper = new DictDBHelper(context);
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public void setListener(OnSearchListener listener){
        this.callback = listener;
    }

    /**
     * 初始化,拷贝辞典数据库文件
     * @param context
     */
    public static void init(final Context context){
        //检查数据库文件是否已拷贝并且文件完整
        new Thread(new Runnable() {
            @Override
            public void run() {
                File db = context.getDatabasePath(dbName);
                Log.e(TAG, "run: db path:" + db.getAbsolutePath());
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                Log.d(TAG, "run: 开始拷贝");
                if (db.exists() && db.length() != 16022528){
                    db.delete();
                }
                if (!db.exists()){
                    //拷贝
                    try {
                        File dbDir = new File(context.getApplicationInfo().dataDir + "/databases");
                        if (!dbDir.exists()){
                            dbDir.mkdir();
                        }
                        db.createNewFile();
                        inputStream = context.getAssets().open(assetName);
                        outputStream = new FileOutputStream(db);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ( (len = inputStream.read(buffer)) != -1){
                            outputStream.write(buffer,0,len);
                        }
                        outputStream.flush();
                        Log.d(TAG, "run: 拷贝完毕");
                        isInit = true;
                        inputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "run: 拷贝失败");
                        e.printStackTrace();
                    }
                }else {
                    //已经初始化
                    isInit = true;
                }
            }
        }).start();

    }

    /**
     * 精确检索
     * @param query 需要检索的单词
     */
    public void search(String query){
        search(query,false);
    }

    /**
     * 简单模糊检索
     * @param query 需要检索的字符串
     * @param isFuzzy 是否需要模糊检索 true为模糊检索,false为精确检索
     */
    public void search(final String query, final boolean isFuzzy){
        if (isInit) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    long time = System.currentTimeMillis();
                    final List<TransResult> results = dbHelper.search(query, isFuzzy);
                    Log.e(TAG, "translate takes times :" + (System.currentTimeMillis() - time) + " ms");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSearchResult(results);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 模糊检索,结果分页
     * @param query
     * @param pageSize
     * @param pageNum
     */
    public void search(final String query, final int pageSize, final int pageNum){
        if (isInit){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    final List<TransResult> results = dbHelper.search(query, pageSize,pageNum);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSearchResult(results);
                            }
                        }
                    });
                }
            });
        }
    }

    public void search(final int id){
        if (isInit){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    final List<TransResult> results = dbHelper.search(id);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSearchResult(results);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 关闭数据库连接
     */
    public void release(){
        dbHelper.close();
    }
}
