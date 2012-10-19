package com.ninetwozero.battlelog.provider;

import java.io.IOException;

import novoda.lib.sqliteprovider.migration.Migrations;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ninetwozero.battlelog.Battlelog;

public class DatabaseManager extends SQLiteOpenHelper {

    private Context context;

    public DatabaseManager(Context context) {
        super(context, Battlelog.NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            Migrations.migrate(database, context.getAssets(), "migrations");
        } catch (IOException e) {
            Log.e(DatabaseManager.class.getSimpleName(),
                    "Error on migration / update");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
        onCreate(database);
    }
}
