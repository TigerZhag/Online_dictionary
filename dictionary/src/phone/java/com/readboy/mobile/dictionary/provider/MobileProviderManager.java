package com.readboy.mobile.dictionary.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-4-14.
 */
public class MobileProviderManager extends BaseProviderManager {


    private static MobileProviderManager instance;
    private DictHotWordTableInfo hotWordManager;
    private DictLatelyWordRecordTableInfo latelyRecordWordManager;

    private MobileProviderManager() {
        hotWordManager = new DictHotWordTableInfo();
        latelyRecordWordManager = new DictLatelyWordRecordTableInfo();
    }

    public static MobileProviderManager getInstance() {
        if (instance == null) {
            instance = new MobileProviderManager();
        }
        return instance;
    }

    /*List<DictHotWordTableInfo> getHotWordRecordList(Cursor cursor) {

        if (hotWordManager != null) {
            return hotWordManager.getHotWordRecordList(cursor);
        }

        return null;
    }

    List<DictLatelyWordRecordTableInfo> getLatelyWordRecordList(Cursor cursor) {

        if (latelyRecordWordManager != null) {
            return latelyRecordWordManager.getLatelyWordRecordList(cursor);
        }

        return null;
    }*/

    public void addWordRecord(ContentResolver cr, String type, String word, int index) {
        addHotWord(cr, type, word, index);
        addLatelyWord(cr, type, word, index);
    }

    public void addWordLately(ContentResolver cr, String type, String word, int index) {
        addLatelyWord(cr, type, word, index);
    }

    private void addHotWord(ContentResolver cr, String type, String word, int index) {
        if (hotWordManager != null) {
            hotWordManager.addHotWord(this, cr, type, word, index);
        }
    }

    private void addLatelyWord(ContentResolver cr, String type, String word, int index) {
        if (latelyRecordWordManager != null) {
            latelyRecordWordManager.addLatelyWord(this, cr, type, word, index);
        }
    }

    public void clearLatelyWord(ContentResolver cr) {
        if (cr != null) {
            delete(cr, DictLatelyWordRecordTableInfo.TABLE_NAME, DictLatelyWordRecordTableInfo.ID + " >? ",
                    new String[]{0 + ""});
        }
    }

    public List<DictHotWordTableInfo> getHotWordRecordList(ContentResolver cr) {
        if (hotWordManager != null) {
            return hotWordManager.getHotWordRecordList(this, cr);
        }
        return null;
    }

    public List<DictLatelyWordRecordTableInfo> getLatelyWordRecordList(ContentResolver cr) {
        if (latelyRecordWordManager != null) {
            return latelyRecordWordManager.getLatelyWordRecordList(this, cr);
        }
        return null;
    }

    public Uri getUri(String table) {
        if (table != null) {
            return Uri.parse(DictProvider.BASE_URI_STR + "/" + table);
        }
        return null;
    }
}
