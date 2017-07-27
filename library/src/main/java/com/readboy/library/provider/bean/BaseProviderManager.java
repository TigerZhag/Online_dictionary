package com.readboy.library.provider.bean;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

/**
 * Created by Administrator on 15-4-14.
 */
public abstract class BaseProviderManager {

    public void insert(Context context, String table, ContentValues contentValues) {
        if (context != null) {
            Uri uri = getUri(context, table);
            if (uri != null) {
                insert(context.getContentResolver(), uri, contentValues);
            }
        }
    }

    private Uri insert(ContentResolver cr, Uri uri, ContentValues contentValues) {

        if (cr != null) {
            return cr.insert(uri, contentValues);
        }

        return null;
    }

    public int update(Context context, String table, ContentValues contentValues, String select, String[] selectArgs) {
        if (context != null) {
            Uri uri = getUri(context, table);
            if (uri != null) {
                return update(context.getContentResolver(), uri, contentValues, select, selectArgs);
            }
        }
        return -1;
    }

    private int update(ContentResolver cr, Uri uri, ContentValues contentValues, String select, String[] selectArgs) {
        if (cr != null) {
            return cr.update(uri, contentValues, select, selectArgs);
        }
        return  -1;
    }

    public abstract Uri getUri(Context context, String table);

}
