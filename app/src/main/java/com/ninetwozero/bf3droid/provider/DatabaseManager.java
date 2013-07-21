package com.ninetwozero.bf3droid.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.ninetwozero.bf3droid.BF3Droid;
import novoda.lib.sqliteprovider.migration.Migrations;

import java.io.IOException;

public class DatabaseManager extends SQLiteOpenHelper {
    private Context mContext;
    
    public DatabaseManager(Context context) {
        super(context, BF3Droid.NAME, null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            Migrations.migrate(database, mContext.getAssets(), "migrations");
        } catch (IOException e) {
            Log.e(DatabaseManager.class.getSimpleName(), "Error on migration / update");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int from, int to) {
        onCreate(database);
    }
}
