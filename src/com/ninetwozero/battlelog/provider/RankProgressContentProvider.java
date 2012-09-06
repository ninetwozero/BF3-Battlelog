package com.ninetwozero.battlelog.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.ninetwozero.battlelog.Battlelog;

import static com.ninetwozero.battlelog.provider.BattlelogUriMatcher.*;

public class RankProgressContentProvider  extends ContentProvider {

    private DatabaseManager databaseManager;

    private SQLiteDatabase database;

    private BattlelogUriMatcher uriMatcher = new BattlelogUriMatcher(UriMatcher.NO_MATCH);

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
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        /*SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection);
        queryBuilder.setTables(RANK_PROGRESS);
        int uriType = uriMatcher.match(uri);
        switch (uriType){
            case PERSONAS_RANK_PROGRESS:
                queryBuilder.appendWhere("personaId = ");
        }*/
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = uriMatcher.match(uri);
        int rowsDeleted = 0;
        long id = 0;
        switch(uriType){
            case PERSONAS_RANK_PROGRESS:
                id = database.insert(RANK_PROGRESS, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(RANK_PROGRESS + "/" +id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void checkColumns(String[] projections){

    }
}
