package com.readboy.mobile.dictionary.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senny on 2015/10/27.
 */
public class DictHotWordTableInfo {

    protected final static String TABLE_NAME = "dict_hot_word_record";
    protected final static String ID = "_id";
    protected final static String DICT_TYPE = "dict_type";
    protected final static String DICT_WORD = "dict_word";
    protected final static String DICT_WORD_INDEX = "dict_word_index";
    protected final static String NUMBER_OF_SELECTING = "number_of_selecting";

    protected final static String[] COLUMNS = {
            ID,
            DICT_TYPE,
            DICT_WORD,
            DICT_WORD_INDEX,
            NUMBER_OF_SELECTING
    };

    protected final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DICT_TYPE + " VARCHAR(256), "
            + DICT_WORD + " VARCHAR(256), "
            + DICT_WORD_INDEX + " VARCHAR(256), "
            + NUMBER_OF_SELECTING + " INTEGER);";

    protected final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public int id;
    public String dictType;
    public String dictWord;
    public String dictWordIndex;
    public int numbersOfSelecting;

    void addHotWord(BaseProviderManager providerManager, ContentResolver cr, String type, String word, int index) {
        if (providerManager != null && type != null && word != null) {
            Cursor cursor = providerManager.querry(cr, DictHotWordTableInfo.TABLE_NAME, DictHotWordTableInfo.COLUMNS,
                    DictHotWordTableInfo.DICT_WORD + " =? ",
                    new String[]{word}, null);
            List<DictHotWordTableInfo> data = getHotWordRecordList(cursor);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DictHotWordTableInfo.DICT_TYPE, type);
            contentValues.put(DictHotWordTableInfo.DICT_WORD, word);
            contentValues.put(DictHotWordTableInfo.DICT_WORD_INDEX, index);
            int numberOfSelecting = 1;
            if (data != null && data.size() > 0) {
                DictHotWordTableInfo dictHotWordTableInfo = data.get(0);
                numberOfSelecting += dictHotWordTableInfo.numbersOfSelecting;
                contentValues.put(DictHotWordTableInfo.NUMBER_OF_SELECTING, numberOfSelecting);
                providerManager.update(cr, DictHotWordTableInfo.TABLE_NAME, contentValues, DictHotWordTableInfo.ID + " =? ",
                        new String[]{dictHotWordTableInfo.id+""});
                /*delete(cr, DictHotWordTableInfo.TABLE_NAME, DictHotWordTableInfo.ID + " =? ",
                            new String[]{dictHotWordTableInfo.id+""});*/
            } else {
                contentValues.put(DictHotWordTableInfo.NUMBER_OF_SELECTING, numberOfSelecting);
                providerManager.insert(cr, DictHotWordTableInfo.TABLE_NAME, contentValues);
            }

        }
    }

    List<DictHotWordTableInfo> getHotWordRecordList(BaseProviderManager providerManager, ContentResolver cr) {
        List<DictHotWordTableInfo> data = null;
        if (providerManager != null && cr != null) {
            Cursor cursor = providerManager.querry(cr, DictHotWordTableInfo.TABLE_NAME,
                    DictHotWordTableInfo.COLUMNS, DictHotWordTableInfo.ID + " >? " + " order by " + DictHotWordTableInfo.NUMBER_OF_SELECTING + " DESC ",
                    new String[]{0+""}, null);
            data = getHotWordRecordList(cursor);
            if (data != null && data.size() > MAX_NUMBER_FOR_HOT_SAVE) {
                for (int i = MAX_NUMBER_FOR_HOT_SAVE;i < data.size();i++) {
                    providerManager.delete(cr, DictHotWordTableInfo.TABLE_NAME,
                            DictHotWordTableInfo.DICT_TYPE + " =? and " + DictHotWordTableInfo.DICT_WORD_INDEX + " =? ",
                            new String[]{data.get(i).dictType, data.get(i).dictWordIndex});
                }
                data = data.subList(0, SHOW_NUMBER);
            } if (data != null && data.size() > SHOW_NUMBER) {
                data = data.subList(0, SHOW_NUMBER);
            }
        }
        return data;
    }

    private List<DictHotWordTableInfo> getHotWordRecordList(Cursor cursor) {

        List<DictHotWordTableInfo> data = null;

        if (cursor != null) {
            try {
                if (!cursor.moveToFirst()) {
                    return data;
                }
                data = new ArrayList<DictHotWordTableInfo>();
                do {
                    DictHotWordTableInfo dictHotWordTableInfo = new DictHotWordTableInfo();
                    dictHotWordTableInfo.id = cursor.getInt(0);
                    dictHotWordTableInfo.dictType = cursor.getString(1);
                    dictHotWordTableInfo.dictWord = cursor.getString(2);
                    dictHotWordTableInfo.dictWordIndex = cursor.getString(3);
                    dictHotWordTableInfo.numbersOfSelecting = cursor.getInt(4);
                    data.add(dictHotWordTableInfo);
                } while (cursor.moveToNext());
            } finally {
                cursor.close();
            }
        }

        return data;
    }

    private static final int MAX_NUMBER_FOR_HOT_SAVE = 320;
    private static final int SHOW_NUMBER = 9;
}
