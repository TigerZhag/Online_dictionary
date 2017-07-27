package com.readboy.mobile.dictionary.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Administrator on 15-4-14.
 */
abstract class BaseProviderManager {

    protected Cursor querry(ContentResolver cr, String table,
                            String[] clumns, String wheres, String[] projections, String sortOrder) {
        Uri uri = getUri(table);
        if (cr != null && uri != null) {
            return cr.query(uri, clumns, wheres, projections, sortOrder);
        }
        return null;
    }

    protected Uri insert(ContentResolver cr, String table,
                         ContentValues contentValues) {
        Uri uri = getUri(table);
        if (cr != null && uri != null) {
            return cr.insert(uri, contentValues);
        }

        return null;
    }

    protected int update(ContentResolver cr, String table,
                         ContentValues contentValues, String select, String[] selectArgs) {
        Uri uri = getUri(table);
        if (cr != null && uri != null) {
            return cr.update(uri, contentValues, select, selectArgs);
        }
        return  -1;
    }

    protected int delete(ContentResolver cr, String table, String wheres, String[] projections) {
        Uri uri = getUri(table);
        if (cr != null && uri != null) {
            return cr.delete(uri, wheres, projections);
        }
        return -1;
    }

    public abstract Uri getUri(String table);

}
