package com.ninetwozero.battlelog.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class BattlelogContentProvider extends ContentProvider {
    private DatabaseManager mDatabaseManager;
    private SQLiteDatabase mDatabase;
    public static final String WHERE_PERSONA_ID = "personaId=?";

    public synchronized SQLiteDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabaseManager = new DatabaseManager(getContext());
            mDatabase = mDatabaseManager.getWritableDatabase();
        }
        return mDatabase;
    }

    @Override
    public boolean onCreate() {
        getDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(getType(uri));
        SQLiteDatabase db = getDatabase();
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
            default:
                return "";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = mDatabase.insert(getType(uri), null, contentValues);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(getType(uri) + "/" + id);
    }

    @Override
    public int delete(Uri uri, String where, String[] selection) {
        return mDatabase.delete(getType(uri), where, selection);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
