package com.readboy.mobile.dictionary.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senny on 2015/10/27.
 */
public class DictLatelyWordRecordTableInfo {

    protected final static String TABLE_NAME = "dict_lately_word_record";
    protected final static String ID = "_id";
    protected final static String DICT_TYPE = "dict_type";
    protected final static String DICT_WORD = "dict_word";
    protected final static String DICT_WORD_INDEX = "dict_word_index";

    protected final static String[] COLUMNS = {
            ID,
            DICT_TYPE,
            DICT_WORD,
            DICT_WORD_INDEX
    };

    protected final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DICT_TYPE + " VARCHAR(256), "
            + DICT_WORD + " VARCHAR(256), "
            + DICT_WORD_INDEX + " VARCHAR(256));";

    protected final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public int id;
    public String dictType;
    public String dictWord;
    public String dictWordIndex;

    void addLatelyWord(BaseProviderManager providerManager, ContentResolver cr, String type, String word, int index) {
        if (providerManager != null && type != null && word != null) {
            Cursor cursor = providerManager.querry(cr, DictLatelyWordRecordTableInfo.TABLE_NAME, DictLatelyWordRecordTableInfo.COLUMNS,
                    DictLatelyWordRecordTableInfo.DICT_WORD + " =? ",
                    new String[]{word}, null);
            List<DictLatelyWordRecordTableInfo> data = getLatelyWordRecordList(cursor);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DictLatelyWordRecordTableInfo.DICT_TYPE, type);
            contentValues.put(DictLatelyWordRecordTableInfo.DICT_WORD, word);
            contentValues.put(DictLatelyWordRecordTableInfo.DICT_WORD_INDEX, index);
            if (data != null && data.size() > 0) {
                for (DictLatelyWordRecordTableInfo info : data) {
                    providerManager.delete(cr, DictLatelyWordRecordTableInfo.TABLE_NAME, DictLatelyWordRecordTableInfo.ID + " =? ",
                            new String[]{info.id + ""});
                }
            }
            providerManager.insert(cr, DictLatelyWordRecordTableInfo.TABLE_NAME, contentValues);
        }
    }

    List<DictLatelyWordRecordTableInfo> getLatelyWordRecordList(BaseProviderManager providerManager, ContentResolver cr) {
        List<DictLatelyWordRecordTableInfo> data = null;
        if (providerManager != null && cr != null) {
            Cursor cursor = providerManager.querry(cr, DictLatelyWordRecordTableInfo.TABLE_NAME,
                    DictLatelyWordRecordTableInfo.COLUMNS,
                    DictLatelyWordRecordTableInfo.ID + " >? order by " + DictLatelyWordRecordTableInfo.ID + " DESC",
                    new String[]{0+""}, null);
            data = getLatelyWordRecordList(cursor);
            if (data != null && data.size() > 9) {
                for (int i = 9;i < data.size();i++) {
                    providerManager.delete(cr, DictLatelyWordRecordTableInfo.TABLE_NAME,
                            DictLatelyWordRecordTableInfo.DICT_WORD + " =? ",
                            new String[]{data.get(i).dictWord});
                }
                data = data.subList(0, 9);
            }
        }
        return data;
    }

    private List<DictLatelyWordRecordTableInfo> getLatelyWordRecordList(Cursor cursor) {

        List<DictLatelyWordRecordTableInfo> data = null;

        if (cursor != null) {
            try {
                if (!cursor.moveToFirst()) {
                    return data;
                }
                data = new ArrayList<DictLatelyWordRecordTableInfo>();
                do {
                    DictLatelyWordRecordTableInfo dictLatelyWordRecordTableInfo = new DictLatelyWordRecordTableInfo();
                    dictLatelyWordRecordTableInfo.id = cursor.getInt(0);
                    dictLatelyWordRecordTableInfo.dictType = cursor.getString(1);
                    dictLatelyWordRecordTableInfo.dictWord = cursor.getString(2);
                    dictLatelyWordRecordTableInfo.dictWordIndex = cursor.getString(3);
                    data.add(dictLatelyWordRecordTableInfo);
                } while (cursor.moveToNext());
            } finally {
                cursor.close();
            }
        }

        return data;
    }
}
