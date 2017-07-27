package com.readboy.library.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import dream.library.R;

/**
 * Created by Administrator on 15-4-14.
 */
public class DictContentProvider extends ContentProvider {

    private SQLiteOpenHelper sqLiteOpenHelper;
    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        sqLiteOpenHelper = new DictSQLiteOpenHelper(getContext(),
                getContext().getString(R.string.provider_database_name),
                null,
                getContext().getResources().getInteger(R.integer.provider_version));
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authorities = getContext().getString(R.string.provider_authorities_dictionary);
        String englishCatalogPath = getContext().getString(R.string.provider_path_dict_english_catalog);
        uriMatcher.addURI(authorities, englishCatalogPath, 1);
        uriMatcher.addURI(authorities, englishCatalogPath + "/#", 2);
        String usedRecordPath = getContext().getString(R.string.provider_path_dict_record);
        uriMatcher.addURI(authorities, usedRecordPath, 3);
        uriMatcher.addURI(authorities, usedRecordPath + "/#", 4);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        String table = getTable(uri);
        if (table != null) {
            return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
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
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        String table = getTable(uri);
        if (table != null) {
            long id = db.insert(table, null, values);
            return ContentUris.withAppendedId(uri, id);
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        String table = getTable(uri);
        if (table != null) {
            return db.delete(table, selection, selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        String table = getTable(uri);
        if (table != null) {
            return db.update(table, values, selection, selectionArgs);
        }
        return 0;
    }

    public String getTable(Uri uri) {

        if (uriMatcher != null) {
            switch (uriMatcher.match(uri)) {
                case 1:
                case 2:
                    return DictCatalogTable.TABLE;
                case 3:
                case 4:
                    return DictUsedRecordTable.TABLE;
            }
        }
        return null;
    }

    private class DictSQLiteOpenHelper extends SQLiteOpenHelper {

        public DictSQLiteOpenHelper(
                Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DictCatalogTable.CREATE_TABLE);
            db.execSQL(DictUsedRecordTable.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /*db.execSQL("DROP TABLE IF EXISTS "+DictUsedRecordTable.TABLE);
            db.execSQL(DictUsedRecordTable.CREATE_TABLE);*/
        }
    }
}
