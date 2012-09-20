package com.ninetwozero.battlelog.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.ninetwozero.battlelog.factory.UriFactory;

import static com.ninetwozero.battlelog.factory.UriFactory.URI_PATH;
import static com.ninetwozero.battlelog.factory.UriFactory.URI_CODES;

public class BattlelogContentProvider extends ContentProvider {

    private DatabaseManager databaseManager;

    private SQLiteDatabase database;

    public synchronized SQLiteDatabase getDatabase(){
        if(database == null){
            databaseManager = new DatabaseManager(getContext());
            database = databaseManager.getWritableDatabase();
        }
        return database;
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
        switch(UriFactory.URI_MATCHER.match(uri)){
            case URI_CODES.RANK_PROGRESS:
                return URI_PATH.RANK_PROGRESS;
            case URI_CODES.PERSONA_STATISTICS:
                return URI_PATH.PERSONA_STATISTICS;
            case URI_CODES.SCORE_STATISTICS:
                return URI_PATH.SCORE_STATISTICS;
            default:
                return "";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = database.insert(getType(uri), null, contentValues);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(getType(uri) + "/" +id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
