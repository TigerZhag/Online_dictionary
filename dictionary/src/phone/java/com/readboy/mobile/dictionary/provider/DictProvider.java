package com.readboy.mobile.dictionary.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;


/**
 * Created by Senny on 2015/10/27.
 */
public class DictProvider extends ContentProvider {

    private final static String DATABASE_NAME = "dict_manager.db";
    private final static int DATABASE_VERSION = 3;
    private final static String SCHEME = "content://";
    private final static String AUTHORITIES = "com.readboy.mobile.dictionary";
    private final static String PATH = "dict_word_record";
    private DictSqliteOpenHelper dictSqliteOpenHelper;

    public final static String BASE_URI_STR= SCHEME + AUTHORITIES + "/" + PATH;

    @Override
    public boolean onCreate() {
        dictSqliteOpenHelper = new DictSqliteOpenHelper(getContext(),
                DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dictSqliteOpenHelper.getReadableDatabase();
        String table = getTable(uri);
        if (table != null) {
            try {
                return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public String getType(Uri uri) {
        throw new IllegalArgumentException("Unsupported URI: " + uri);
//        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dictSqliteOpenHelper.getWritableDatabase();
        String table = getTable(uri);
        if (table != null) {
            try {
                long id = db.insert(table, null, values);
                return ContentUris.withAppendedId(uri, id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dictSqliteOpenHelper.getWritableDatabase();
        String table = getTable(uri);
        if (table != null) {
            try {
                return db.delete(table, selection, selectionArgs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dictSqliteOpenHelper.getWritableDatabase();
        String table = getTable(uri);
        if (table != null) {
            try {
                return db.update(table, values, selection, selectionArgs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private String getTable(Uri uri) {
        if (uri != null) {
            return uri.getLastPathSegment();
        }
        return null;
    }

    class DictSqliteOpenHelper extends SQLiteOpenHelper {

        public DictSqliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DictHotWordTableInfo.CREATE_TABLE);
            db.execSQL(DictLatelyWordRecordTableInfo.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL(DictHotWordTableInfo.DROP_TABLE);
            db.execSQL(DictLatelyWordRecordTableInfo.DROP_TABLE);

            db.execSQL(DictHotWordTableInfo.CREATE_TABLE);
            db.execSQL(DictLatelyWordRecordTableInfo.CREATE_TABLE);
        }
    }
}
