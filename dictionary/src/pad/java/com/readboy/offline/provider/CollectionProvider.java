package com.readboy.offline.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class CollectionProvider extends ContentProvider
{
    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.readboy.dictionary.collection";
    private static final int ALL_MESSAGES = 11;
    private static final int SPECIFIC_MESSAGE = 22;

    private static final String DATABASE_NAME = "newword.db";
    private static final String TABLE_NAME = "newword";
    private static final int DB_VERSION = 2;

    // Here are our column name constants, used to query for field values.
    public static final String ID = "_id";
    public static final String DICTIONARY_ID = "dict_id";
    public static final String WORD_INDEX = "dict_index";
    public static final String READABLE = "read_enable";
    public static final String DICTIONARY_TYPE = "dict_type";
    public static final String WORD = "dict_key";
    public static final String FORMATTED_WORD = "plastic_key";
    public static final String DEFAULT_SORT_ORDER = WORD + ", "
            + DICTIONARY_ID + ", " + WORD_INDEX + " COLLATE LOCALIZED ASC";

    private static final UriMatcher sUriMatcher;

    static
    {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "item", ALL_MESSAGES);
        sUriMatcher.addURI(AUTHORITY, "item/#", SPECIFIC_MESSAGE);
    }

    private DatabaseHelper mDbHelper;

    @Override
    public boolean onCreate()
    {
        mDbHelper = new DatabaseHelper(getContext(), DATABASE_NAME);
        return true;
    }

    @Override
    public String getType(Uri uri)
    {
        switch (sUriMatcher.match(uri))
        {
            case ALL_MESSAGES:
                return "vnd.android.cursor.dir/rssitem"; // List of items.
            case SPECIFIC_MESSAGE:
                return "vnd.android.cursor.item/rssitem"; // Specific item.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private String matchTable(Uri uri)
    {
        String table;
        switch (sUriMatcher.match(uri))
        {
            case ALL_MESSAGES:
            case SPECIFIC_MESSAGE:
                table = TABLE_NAME;
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return table;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        synchronized (sUriMatcher)
        {
            String table = matchTable(uri);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            long rowId;
            db.beginTransaction();
            rowId = db.insert(table, WORD, values);
            db.setTransactionSuccessful();
            db.endTransaction();

            if (rowId > 0)
            {
                Uri returnUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnUri;
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        synchronized (sUriMatcher)
        {
            String table = matchTable(uri);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            int count;
            db.beginTransaction();
            count = db.delete(table, selection, selectionArgs);
            db.setTransactionSuccessful();
            db.endTransaction();

            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        synchronized (sUriMatcher)
        {
            String table = matchTable(uri);
            SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
            qBuilder.setTables(table);

            // If the query ends in a specific record number, we're
            // being asked for a specific record, so set the
            // WHERE clause in our query.
            if ((sUriMatcher.match(uri)) == SPECIFIC_MESSAGE)
            {
                qBuilder.appendWhere("_id=" + uri.getLastPathSegment());
            }


            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor c = qBuilder.query(db,
                    projection, // The columns to return from the queryFromDB
                    selection, // The columns for the where clause
                    selectionArgs, // The values for the where clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    sortOrder == null ? DEFAULT_SORT_ORDER : sortOrder);

            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs)
    {
        synchronized (sUriMatcher)
        {
            String table = matchTable(uri);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            int count;
            db.beginTransaction();
            count = db.update(table, values, selection, selectionArgs);
            db.setTransactionSuccessful();
            db.endTransaction();

            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    //------------------------------------------------------------------------------------
    private class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context, String db_name)
        {
            this(context, db_name, null, DB_VERSION);
            // In the case where neither onCreate nor onUpgrade gets called,
            // we read the maxId from the DictDB here
        }

        public DatabaseHelper(Context context, String name, CursorFactory factory,
                              int version)
        {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            String sql = "CREATE TABLE " + TABLE_NAME
                    + "("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DICTIONARY_ID + " INTEGER,"
                    + WORD_INDEX + " INTEGER,"
                    + READABLE + " INTEGER,"
                    + DICTIONARY_TYPE + " INTEGER,"
                    + FORMATTED_WORD + " TEXT,"
                    + WORD + " TEXT"
                    + ");";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}