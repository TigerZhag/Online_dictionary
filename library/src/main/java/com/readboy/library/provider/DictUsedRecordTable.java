package com.readboy.library.provider;

import android.content.Context;
import android.net.Uri;

import dream.library.R;

/**
 * Created by Administrator on 15-4-14.
 */
public class DictUsedRecordTable {

    protected static Uri uri;
    protected static final String TABLE = "used_record";
    protected static final String ID = "_id";
    protected static final String DICT_ID = "dict_id";
    protected static final String DICT_WORLD = "dict_world";
    protected static final String DICT_WORLD_INDEX = "dict_world_index";

    protected static final String COLUMNS[] = {ID, DICT_ID, DICT_WORLD, DICT_WORLD_INDEX};

    protected static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE + " ( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DICT_ID + " INTEGER, " + DICT_WORLD + " VARCHAR(256), "+DICT_WORLD_INDEX+" VARCHAR(256));";

    protected static final Uri getUrl(Context context) {

        if (context != null && uri == null) {
            String authorities = context.getString(R.string.provider_authorities_dictionary);
            String path = context.getString(R.string.provider_path_dict_record);
            String urlStr = "content://"+authorities+"/"+path;
            uri = Uri.parse(urlStr);
        }

        return uri;
    }

    public int id;
    public int dictID;
    public String dictWorld;
    public int dictWorldIndex;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDictID() {
        return dictID;
    }

    public void setDictID(int dictID) {
        this.dictID = dictID;
    }

    public String getDictWorld() {
        return dictWorld;
    }

    public void setDictWorld(String dictWorld) {
        this.dictWorld = dictWorld;
    }

    public int getDictWorldIndex() {
        return dictWorldIndex;
    }

    public void setDictWorldIndex(int dictWorldIndex) {
        this.dictWorldIndex = dictWorldIndex;
    }
}
