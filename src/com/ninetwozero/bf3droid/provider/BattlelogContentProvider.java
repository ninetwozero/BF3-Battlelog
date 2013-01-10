package com.ninetwozero.bf3droid.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteTransactionListener;
import android.net.Uri;
import android.util.Log;

public class BattlelogContentProvider extends ContentProvider implements SQLiteTransactionListener {

    //private DatabaseManager databaseManager;
    private SQLiteOpenHelper helper;
    private volatile boolean notifyChange;

    /*public synchronized SQLiteDatabase getDatabase() {
        if (database == null) {
            databaseManager = new DatabaseManager(getContext());
            database = databaseManager.getWritableDatabase();
        }
        return database;
    }*/

    @Override
    public boolean onCreate() {
        //getDatabase();
        helper = new DatabaseManager(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(getType(uri));
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (UriFactory.URI_MATCHER.match(uri)) {
            case UriFactory.URI_CODES.PROFILE_INFO:
                return UriFactory.URI_PATH.PROFILE_INFO;
            case UriFactory.URI_CODES.RANK_PROGRESS:
                return UriFactory.URI_PATH.RANK_PROGRESS;
            case UriFactory.URI_CODES.PERSONA_STATISTICS:
                return UriFactory.URI_PATH.PERSONA_STATISTICS;
            case UriFactory.URI_CODES.SCORE_STATISTICS:
                return UriFactory.URI_PATH.SCORE_STATISTICS;
            case UriFactory.URI_CODES.PLATOON_INFO:
                return UriFactory.URI_PATH.PLATOON_INFO;
            case UriFactory.URI_CODES.PERSONAS:
                return UriFactory.URI_PATH.PERSONAS;
            default:
                return "";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = helper.getWritableDatabase();
        long id = database.replaceOrThrow(getType(uri), null, contentValues);
        Log.e("Bulk Insert", "ID = " + id);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(getType(uri) + "/" + id);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numValues = values.length;
        SQLiteDatabase database = helper.getWritableDatabase();
        database.beginTransactionWithListener(this);
        try {
            for (int i = 0; i < numValues; i++) {
                Uri result = insert(uri, values[i]);
                if (result != null) {
                    notifyChange = true;
                }
                database.yieldIfContendedSafely();
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        onEndTransaction();
        return numValues;
    }

    @Override
    public int delete(Uri uri, String where, String[] selection) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int status = database.delete(getType(uri), where, selection);
        getContext().getContentResolver().notifyChange(uri, null);
        return status;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int status = database.update(getType(uri), contentValues, s, strings);
        getContext().getContentResolver().notifyChange(uri, null);
        return status;
    }
    @Override
    public void onBegin() {
        onBeginTransaction();
    }

    @Override
    public void onCommit() {
        beforeTransactionCommit();
    }

    @Override
    public void onRollback() {
        // not used
    }

    protected void onBeginTransaction() {
    }

    protected void beforeTransactionCommit() {
    }

    protected void onEndTransaction() {
        if (notifyChange) {
            notifyChange = false;
        }
    }
}
