package com.readboy.library.provider;

import android.content.Context;
import android.net.Uri;

import dream.library.R;

/**
 * Created by Administrator on 15-4-14.
 */
public class DictCatalogTable {

    protected static Uri uri;
    protected static final String TABLE = "dict_engliish_catalog";
    protected static final String ID = "_id";
    protected static final String DICT_ID = "dict_id";
    protected static final String CATALOG = "catalog";

    protected static String COLUMNS[] = {ID, DICT_ID, CATALOG};

    protected static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE + " ( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DICT_ID + " INTEGER, " + CATALOG + " VARCHAR(256));";

    protected static final Uri getUrl(Context context) {

        if (context != null && uri == null) {
            String authorities = context.getString(R.string.provider_authorities_dictionary);
            String path = context.getString(R.string.provider_path_dict_english_catalog);
            String urlStr = "content://"+authorities+"/"+path;
            uri = Uri.parse(urlStr);
        }

        return uri;
    }

    public int id;
    public int dictID;
    public String catalog;

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

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
}
