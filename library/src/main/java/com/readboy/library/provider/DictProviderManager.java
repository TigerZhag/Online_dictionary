package com.readboy.library.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.readboy.library.provider.bean.BaseProviderManager;
import com.readboy.library.provider.info.PlugWorldInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-4-14.
 */
public class DictProviderManager extends BaseProviderManager {

    private static final int MAX_USED_RECORD_SIZE = 18;
    private static DictProviderManager instance;

    private DictProviderManager() {

    }

    public static DictProviderManager getInstance() {
        if (instance == null) {
            instance = new DictProviderManager();
        }
        return instance;
    }

    public byte[] getDictOneLevelByID(Context context, int dicID) {
        Cursor cursor = getDictOneLevelSQL(context, dicID);
        if (cursor != null) {
            try {
                if (!cursor.moveToFirst()) {
                    return null;
                }
                byte[] catalog = cursor.getBlob(2);
                return catalog;
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }

        }
        return null;
    }

    public List<PlugWorldInfo> getUsedRecord(Context context) {
        List<PlugWorldInfo> data = null;
        Cursor cursor = getUsedReocrdSQL(context);
        if (cursor != null) {
            try {
                if (!cursor.moveToLast()) {
                    return data;
                }
                data = new ArrayList<PlugWorldInfo>();
                do {
                    PlugWorldInfo plugWorldInfo = new PlugWorldInfo();
                    plugWorldInfo.dictID = cursor.getInt(1);
                    plugWorldInfo.world = cursor.getString(2);
                    plugWorldInfo.index = cursor.getInt(3);
                    data.add(plugWorldInfo);
                } while (cursor.moveToPrevious());
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return data;
    }

    public List<PlugWorldInfo> getUsedRecordWidthPrimaryID(Context context) {
        List<PlugWorldInfo> data = null;
        Cursor cursor = getUsedReocrdSQL(context);
        if (cursor != null) {
            try {
                if (!cursor.moveToFirst()) {
                    return data;
                }
                data = new ArrayList<PlugWorldInfo>();
                do {
                    PlugWorldInfo plugWorldInfo = new PlugWorldInfo();
                    plugWorldInfo.primaryID = cursor.getInt(0);
                    plugWorldInfo.dictID = cursor.getInt(1);
                    plugWorldInfo.world = cursor.getString(2);
                    plugWorldInfo.index = cursor.getInt(3);
                    data.add(plugWorldInfo);
                } while (cursor.moveToNext());
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return data;
    }

    public void addDictOneLevel(Context context, int dictID, byte[] catalogBuffer) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DictCatalogTable.DICT_ID, dictID);
        contentValues.put(DictCatalogTable.CATALOG, catalogBuffer);
        insert(context, DictCatalogTable.TABLE, contentValues);
    }

    public void addDictUsedWorld(Context context, int dictID, String dictWorld, int dictWorldIndex) {

        Cursor cursor = null;
        try {
            cursor = getUsedReocrdSQL(context, dictID, dictWorldIndex);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    deleteUsedRecordSQL(context, cursor.getInt(0));
//                    return;
                }
            }
            List<PlugWorldInfo> data = getUsedRecordWidthPrimaryID(context);
        if (data != null && data.size() >= MAX_USED_RECORD_SIZE) {
            for (int i = 0; data.size() - MAX_USED_RECORD_SIZE >= i; i++) {
                deleteUsedRecordSQL(context, data.get(i).primaryID);
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(DictUsedRecordTable.DICT_ID, dictID);
        contentValues.put(DictUsedRecordTable.DICT_WORLD, dictWorld);
        contentValues.put(DictUsedRecordTable.DICT_WORLD_INDEX, dictWorldIndex);
        insert(context, DictUsedRecordTable.TABLE, contentValues);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Cursor getDictOneLevelSQL(Context context, int dicID) {
        if (context != null) {
            Uri uri = DictCatalogTable.getUrl(context);
            return getDictOneLevelSQL(context.getContentResolver(), uri, dicID);
        }
        return null;
    }

    private Cursor getDictOneLevelSQL(ContentResolver cr, Uri uri, int dicID) {
        if (cr != null) {
            return cr.query(uri,
                    DictCatalogTable.COLUMNS,
                    DictCatalogTable.DICT_ID + " =? ",
                    new String[]{dicID + ""},
                    null);
        }
        return null;
    }

    private Cursor getUsedReocrdSQL(Context context) {
        if (context != null) {
            Uri uri = DictUsedRecordTable.getUrl(context);
            return getUsedReocrdSQL(context.getContentResolver(), uri);
        }
        return null;
    }

    private Cursor getUsedReocrdSQL(ContentResolver cr, Uri uri) {
        if (cr != null) {
            return cr.query(uri, DictUsedRecordTable.COLUMNS, null, null, null);
        }
        return null;
    }

    private Cursor getUsedReocrdSQL(Context context, int dictID, int worldIndex) {
        if (context != null) {
            Uri uri = DictUsedRecordTable.getUrl(context);
            return getUsedReocrdSQL(context.getContentResolver(), uri, dictID, worldIndex);
        }
        return null;
    }

    private Cursor getUsedReocrdSQL(ContentResolver cr, Uri uri, int dictID, int worldIndex) {
        if (cr != null) {
            return cr.query(uri, DictUsedRecordTable.COLUMNS, DictUsedRecordTable.DICT_ID + " =? and "
                    + DictUsedRecordTable.DICT_WORLD_INDEX + " =? ", new String[]{dictID + "", worldIndex + ""}, null);
        }
        return null;
    }

    private int deleteUsedRecordSQL(Context context, int primaryID) {
        if (context != null) {
            Uri uri = DictUsedRecordTable.getUrl(context);
            return deleteUsedRecordSQL(context.getContentResolver(), uri, primaryID);
        }
        return -1;
    }

    private int deleteUsedRecordSQL(ContentResolver cr, Uri uri, int primaryID) {
        if (cr != null && uri != null) {
            return cr.delete(uri, DictUsedRecordTable.ID + " =? ", new String[]{primaryID + ""});
        }
        return -1;
    }

    public Uri getUri(Context context, String table) {
        if (table != null) {
            if (table.contentEquals(DictCatalogTable.TABLE)) {
                return DictCatalogTable.getUrl(context);
            } else if (table.contentEquals(DictUsedRecordTable.TABLE)) {
                return DictUsedRecordTable.getUrl(context);
            }
        }
        return null;
    }
}
