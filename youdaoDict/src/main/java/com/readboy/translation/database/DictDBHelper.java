package com.readboy.translation.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.readboy.translation.bean.TransResult;
import com.readboy.translation.utils.StringUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhang shixin
 * @date 16-8-3.
 */
public class DictDBHelper extends SQLiteOpenHelper {
    private static String DBName = "english_words.db";
    private static String tableName = "dictionary";

    private static String path = "";
    private static int version = 1;

    private WeakReference<Context> con;
    private static int DEFAULT_PAGE_SIZE = 30;

    public DictDBHelper(Context context) {
        super(context, DBName, null, version);
        con = new WeakReference<>(context);
    }

    /**
     * 检索单词
     * @param query 需要检索的单词
     * @param isFuzzy 是否需要模糊检索
     * @return
     */
    public List<TransResult> search(String query ,boolean isFuzzy){
        query = StringUtil.filterAlpha(query).toLowerCase();
        String index = "index_word ";
        if (StringUtil.filterPureAlpha(query).length() == 0) return new ArrayList<TransResult>();
        else if (query.contains("-") || query.contains("'")) index = "word ";
        String sql = "";
        if (isFuzzy){
            //初版模糊搜索
            //暂未启用
            //优化后按相关程度排序的模糊检索
            return search(query,DEFAULT_PAGE_SIZE,0);
        }else {
            //精确搜索
            /**
             * tips: 在检索时最好不用like关键字,对于like检索不能使用索引,效率低下
             */
//            sql = "select * from dictionary where " + index + " >= ? and " + index + " <= ? limit " + DEFAULT_PAGE_SIZE + ";";
        }

        //查看是否有该单词
        List<TransResult> results = searchAccurateWord(query);
        //查看是否有变形
        if (results.size() == 0) searchAccurateDeformation(query);
        //不是变形词,正常检索
        if (results.size() == 0) results = searchPrefix(query);
        //无该前缀词则搜索单词
        if (results.size() == 0 && query.contains("-") || query.contains("'")) results = search(StringUtil.filterPureAlpha(query),isFuzzy);
        //二分法查找最长匹配前缀
        if (results.size() == 0 && StringUtil.filterPureAlpha(query).length() > 1){
            int start = 0, end = query.length();
            int mid = start + (end - start) / 2;
            while (mid > start && mid < end) {
                if (searchPrefix(query.substring(0, mid)).size() > 0) {
                    start = mid;
                    mid = start + (end - start) / 2;
                } else {
                    end = mid;
                    mid = start + (end - start) / 2;
                }
            }
            results = searchPrefix(query.substring(0,mid));
        }
        return results;
    }

    private List<TransResult> searchPrefix(String prefix){
        String index = "index_word";
        SQLiteDatabase database = getReadableDatabase();
        StringBuilder builder = new StringBuilder("select * from dictionary where ").append(index).append(" >= ? and ").append(index).append(" <= ? limit " + DEFAULT_PAGE_SIZE);
        Cursor cursor = database.rawQuery(builder.toString(),new String[]{prefix,prefix + "z"});
        List<TransResult> results = new ArrayList<>();
        if (cursor.moveToNext()){
            TransResult result = new TransResult();
            result.id = cursor.getInt(cursor.getColumnIndex("id"));
            result.word = cursor.getString(cursor.getColumnIndex("word"));
            result.us_pronunciation = cursor.getString(cursor.getColumnIndex("us_pronunciation"));
            result.uk_pronunciation = cursor.getString(cursor.getColumnIndex("uk_pronunciation"));
            result.meaning = cursor.getString(cursor.getColumnIndex("meaning"));
//            result.example = cursor.getString(cursor.getColumnIndex("example"));
            results.add(result);
        }
        return results;
    }

    private List<TransResult> searchAccurateDeformation(String defor){
        SQLiteDatabase database = getReadableDatabase();
        String sql = "select * from deformation where deformation = ?;";
        Cursor cursor = database.rawQuery(sql,new String[]{defor});
        if (cursor.moveToNext()){
            String origin_word = cursor.getString(cursor.getColumnIndex("origin_word"));
            return searchPrefix(origin_word);
        }
        return new ArrayList<>();
    }

    private List<TransResult> searchAccurateWord(String word){
        SQLiteDatabase database = getReadableDatabase();
        String sql = "select * from dictionary where index_word = ?;";
        Cursor cursor = database.rawQuery(sql,new String[]{word});
        List<TransResult> results = new ArrayList<>();
        if (cursor.moveToNext()){
            TransResult result = new TransResult();
            result.id = cursor.getInt(cursor.getColumnIndex("id"));
            result.word = cursor.getString(cursor.getColumnIndex("word"));
            result.us_pronunciation = cursor.getString(cursor.getColumnIndex("us_pronunciation"));
            result.uk_pronunciation = cursor.getString(cursor.getColumnIndex("uk_pronunciation"));
            result.meaning = cursor.getString(cursor.getColumnIndex("meaning"));
//            result.example = cursor.getString(cursor.getColumnIndex("example"));
            results.add(result);
        }
        return results;
    }

    /**
     * 模糊檢索,結果分頁
     * @param query         需要檢索的單詞
     * @param pageSize      每一頁最大數量
     * @param pageNum       頁碼,從第0頁開始,[0,n-1]
     * @return
     */
    public List<TransResult> search(String query, int pageSize ,int pageNum){
        StringBuilder fuzzy = new StringBuilder(StringUtil.filterAlpha(query));
//        String[] fuzzies = new String[fuzzy.length() + 2];
//
//        fuzzies[0] = fuzzy.toString();

        String sql = "select * from dictionary where index_word like ? ";
        StringBuilder sb = new StringBuilder(sql);

        sb.append("limit " + pageSize * pageNum + "," + pageSize + ";");
        List<TransResult> results = new ArrayList<>();

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(sb.toString(), new String[]{query + "%"});

        while (cursor.moveToNext()){
            TransResult result = new TransResult();
            result.id = cursor.getInt(cursor.getColumnIndex("id"));
            result.word = cursor.getString(cursor.getColumnIndex("word"));
            result.us_pronunciation = cursor.getString(cursor.getColumnIndex("us_pronunciation"));
            result.uk_pronunciation = cursor.getString(cursor.getColumnIndex("uk_pronunciation"));
            result.meaning = cursor.getString(cursor.getColumnIndex("meaning"));
//            result.example = cursor.getString(cursor.getColumnIndex("example"));
            results.add(result);
        }
        return results;
    }

    public List<TransResult> search(int id){
        List<TransResult> results = new ArrayList<>();
        String sql = "select * from dictionary where id >= ? and id <= ?";
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(sql,new String[]{id + "",(id + DEFAULT_PAGE_SIZE) + ""});
        while (cursor.moveToNext()){
            TransResult result = new TransResult();
            result.id = cursor.getInt(cursor.getColumnIndex("id"));
            result.word = cursor.getString(cursor.getColumnIndex("word"));
            result.us_pronunciation = cursor.getString(cursor.getColumnIndex("us_pronunciation"));
            result.uk_pronunciation = cursor.getString(cursor.getColumnIndex("uk_pronunciation"));
            result.meaning = cursor.getString(cursor.getColumnIndex("meaning"));
//            result.example = cursor.getString(cursor.getColumnIndex("example"));
            results.add(result);
        }
        return results;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
