package com.readboy.online.model.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DbUtil {

    private static final String TAG = DbUtil.class.getSimpleName();
    private DictDB db;
    private SQLiteDatabase dbRead,dbWrite;
    private Context context;
    public DbUtil(Context context){
        this.context = context;
    }

    public void init(){
        db = new DictDB(context);
        dbRead = db.openDatabase(context.getApplicationContext());
        dbWrite = db.openDatabase(context.getApplicationContext());
    }

    /*   获取笔画列表   */
    public List<String> getBiHuaList(){
        Cursor c = dbRead.query("stroks_list", null, null, null, null, null, null);
        List<String> bihuaList = new ArrayList<String>();
        String bihua;
        while (c.moveToNext()){
            bihua = c.getString(c.getColumnIndex("F_bihua"));
            bihuaList.add(bihua);
        }
        c.close();
        return bihuaList;
    }

    /*   插入笔画列表   */
    public void insertBiHua(String bihua){
        String str="insert into stroks_list (F_bihua) values ('"+bihua+"');";
        dbWrite.execSQL(str);
    }

    /*   根据笔画数获取查字结果   */
    public List<String> getBuShouFromBiHua(String bihua){
        boolean exit = false;
        List<String> bushouList = new ArrayList<String>();
        Cursor c = dbRead.query("stroks_search_result", new String[]{"F_bushou"}, "F_bihua = ?",
                new String[]{bihua}, null, null, null);
        String bushou;
        while (c.moveToNext()){
            bushou = c.getString(c.getColumnIndex("F_bushou"));
            for(int i=0;i<bushouList.size();i++){
                if(bushou.equals(bushouList.get(i))){
                    exit = true;
                    break;
                }
            }
            if(!exit){
                bushouList.add(bushou);
            }
            exit = false;
        }
        return bushouList;
    }

    public Cursor getResultFromBiHuaAndBushou(String bihua,String bushou){
        Cursor c = dbRead.query("stroks_search_result", new String[]{"F_zi,F_pinyin"}, "F_bihua = ? and F_bushou = ?",
                new String[]{bihua,bushou}, null, null, null);
        return c;
    }

    /*   插入相应笔画的查字结果   */
    public void insertBiHuaResult(String bihua,String F_zi,String F_bushou,String F_pinyin){
        String str="insert into stroks_search_result (F_bihua,F_zi,F_bushou,F_pinyin) values ('"+bihua+"','"+F_zi+"','"+F_bushou+"','"+F_pinyin+"');";
        dbWrite.execSQL(str);
    }

    /*   获取拼音列表   */
    public void insertPinYin(String key,String pinyin){
        String str="insert into pinyin_list (F_pinyin_key,F_pinyin) values ('"+key+"','"+pinyin+"');";
        dbWrite.execSQL(str);
    }

    /*   首字母列表   */
    public String[] getFirstCharString(){
        String[] firstChar={"a","b","c","d","e","f","g","h","j","k","l","m","n","o","p","q","r","s","t","w","x","y","z",};
        return firstChar;
    }

    /*   获取首字母对应的拼音你列表   */
    public List<String> getCharListFromFirstChar(String firstChar){
        List<String> charList = new ArrayList<String>();
        Cursor c = dbRead.query("pinyin_list", new String[]{"F_pinyin"}, "F_pinyin_key = ?",
                new String[]{firstChar}, null, null, null);
        while (c.moveToNext()){
            charList.add(c.getString(c.getColumnIndex("F_pinyin")));
        }
        return charList;
    }

    public void insertRedical(String bihua,String bushou){
        String str="insert into redical_list (F_bihua,F_bushou) values ('"+bihua+"','"+bushou+"');";
        dbWrite.execSQL(str);
    }

    public List<String> getRedicalFromBihua(String bihua){
        List<String> bushouList = new ArrayList<String>();
        Cursor c = dbRead.query("redical_list", new String[]{"F_bushou"}, "F_bihua = ?",
                new String[]{bihua}, null, null, null);
        while (c.moveToNext()){
            bushouList.add(c.getString(c.getColumnIndex("F_bushou")));
        }
        return bushouList;
    }
}