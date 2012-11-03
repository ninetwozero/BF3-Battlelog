package com.ninetwozero.battlelog.misc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import com.ninetwozero.battlelog.datatype.DatabaseInformationException;

import java.util.List;

public class SQLiteManager {
    class OpenHelper extends SQLiteOpenHelper {
        public OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `"
                    + DatabaseStructure.PlatoonProfile.TABLE_NAME
                    + "` ("
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_ID
                    + " INTEGER UNIQUE, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_DATE
                    + " INTEGER, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_PLATFORM_ID
                    + " INTEGER, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_GAME_ID
                    + " INTEGER, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_FANS
                    + " INTEGER, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_MEMBERS
                    + " INTEGER, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_NUM_BLAZE_ID
                    + " INTEGER, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_STRING_NAME
                    + " STRING, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_STRING_TAG
                    + " STRING, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_STRING_INFO
                    + " STRING, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_STRING_WEB
                    + " STRING, "
                    + DatabaseStructure.PlatoonProfile.COLUMN_NAME_BOOL_VISIBLE
                    + " )"
            );

            db.execSQL(
            "CREATE TABLE IF NOT EXISTS `"
                + DatabaseStructure.ForumThreads.TABLE_NAME
                + "` ("
                + DatabaseStructure.ForumThreads.COLUMN_NAME_ID
                + " INTEGER PRIMARY KEY, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_ID
                + " INTEGER UNIQUE, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_FORUM_ID
                + " INTEGER, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_STRING_TITLE
                + " STRING, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_LAST_POST
                + " INTEGER, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_STRING_LAST_AUTHOR
                + " STRING, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_LAST_AUTHOR_ID
                + " INTEGER, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_LAST_PAGE_ID
                + " INTEGER, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_READ
                + " INTEGER, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_DATE_CHECKED
                + " INTEGER, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_POSTS
                + " INTEGER, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_HAS_UNREAD
                + " INTEGER, "
                + DatabaseStructure.ForumThreads.COLUMN_NAME_NUM_PROFILE_ID
                + " INTEGER "
                + " )"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /* TODO: Handle DB UPDATEs */
            if (oldVersion == 1) {
                onCreate(db);
            } else {
                if (oldVersion == 1) {
                    /*
                     * If it's the first version, then we do the specific things
                     */
                } else if (oldVersion == 2) {
                    /* if it's the second, we do specific things */
                } else {
                    /* Current version */
                }
            }
        }
    }

    private static final String DATABASE_NAME = "battlelog.db";
    private static final int DATABASE_VERSION = 1;
    private Context mContext;
    private SQLiteDatabase mDatabaseHandler;
    private SQLiteStatement mStatement;

    public SQLiteManager(Context context) {
        mContext = context;
        mDatabaseHandler = new OpenHelper(mContext).getWritableDatabase();
    }

    public final void close() {
        if (mStatement != null) {
            mStatement.close();
        }

        if (mDatabaseHandler != null) {
            mDatabaseHandler.close();
        }
    }

    public int delete(String table, String field, String[] values) throws DatabaseInformationException {
        // Construct the Where
        StringBuilder stringWhere = new StringBuilder();

        // How many values did we actually get?
        if (values == null || values.length == 0) {
            throw new DatabaseInformationException("No values received.");
        } else if (values.length == 1) {
            stringWhere.append(field).append(" = ?");
        } else {
            for (int i = 0; i < values.length; i++) {
                if (i == 0) {
                    stringWhere.append(field).append(" = ?");
                } else {
                    stringWhere.append(" AND ").append(field).append(" = ?");
                }
            }
        }
        return mDatabaseHandler.delete(table, stringWhere.toString(), values);

    }

    public int deleteAll(String table) throws DatabaseInformationException {
        if (table == null || table.equals("")) {
            throw new DatabaseInformationException("No table selected.");
        }
        return mDatabaseHandler.delete(table, "1", null);
    }

    public long insert(String table, String[] fields, Object[] values) throws DatabaseInformationException {
        if (table == null || table.equals("")) {
            throw new DatabaseInformationException("No table selected.");
        }

        int countFields = fields.length;
        int countValues = values.length;

        StringBuilder stringFields = new StringBuilder();
        StringBuilder stringValues = new StringBuilder();

        // Validate the number, ie 6 fields should have 6^(n rows) values
        if (countValues % countFields == 0) {
            if (countFields == 0) {
                throw new DatabaseInformationException("Storage failed - no fields found.");
            } else if (countValues == 0) {
                throw new DatabaseInformationException("Storage failed - no values found.");
            } else {
                // Append the fields
                stringFields.append(TextUtils.join(",", fields));

                // Let's bind the parameters
                for (int j = 0; j < countValues; j++) {
                    stringValues.append((j == 0) ? "?" : ", ?");
                }
            }
        } else {
            throw new DatabaseInformationException("Database mismatch - numFields <> numValues.");
        }

        mStatement = mDatabaseHandler.compileStatement(
            "INSERT INTO " +
            table +
            "( "+ stringFields + ") " +
            "VALUES " + "(" + stringValues + ")"
        );

        // Let's bind the parameters
        for (int j = 1; j <= countValues; j++) {
            mStatement.bindString(j, String.valueOf(values[j - 1]));
        }
        return mStatement.executeInsert();
    }

    public long insert(String table, String[] fields, List<Object[]> values) throws DatabaseInformationException {
        if (table == null || table.equals("")) {
            throw new DatabaseInformationException("No table selected.");
        }

        int countFields = fields.length;
        int countRows = values.size();
        int countValues = (countRows > 0) ? values.get(0).length : 0;

        StringBuilder stringFields = new StringBuilder();
        StringBuilder stringValues = new StringBuilder();

        // Validate the number, ie 6 fields should have 6^(n rows) values
        if (countValues % countFields == 0) {
            if (countFields == 0) {
                throw new DatabaseInformationException("Storage failed - no fields found.");
            } else if (countValues == 0) {
                throw new DatabaseInformationException("Storage failed - no values found.");
            } else {
                // Append the fields
                stringFields.append(TextUtils.join(",", fields));

                // Let's bind the parameters
                for (int i = 0; i < countRows; i++) {
                    stringValues.append((i == 0) ? "(" : ", (");
                    for (int j = 0; j < countValues; j++) {
                        stringValues.append((j > 0) ? ", ?" : "?");
                    }
                    stringValues.append(")");
                }
            }
        } else {
            throw new DatabaseInformationException("Database mismatch - numFields <> numValues.");
        }

        mStatement = mDatabaseHandler.compileStatement(
            "INSERT INTO " +
            table +
            "( " + stringFields + ") VALUES " +
            stringValues
        );

        for (int i = 1; i <= countRows; i++) {
            for (int j = 1; j <= countValues; j++) {
                mStatement.bindString((i * j), String.valueOf(values.get(i - 1)[j - 1]));
            }
        }
        return mStatement.executeInsert();
    }

    public int update(String table, String[] fields, Object[] values, String whereField, long id) throws DatabaseInformationException {
        ContentValues contentValues = new ContentValues();

        if (table == null || table.equals("")) {
            throw new DatabaseInformationException("No table selected.");
        } else if (fields == null || fields.length < 1) {
            throw new DatabaseInformationException("No fields selected.");
        } else if ( values == null || values.length < 1) {
            throw new DatabaseInformationException("No values sent.");
        }

        int countFields = fields.length;
        int countValues = values.length;

        // Validate the number, ie 6 fields should have 6^(n rows) values
        if (countValues % countFields != 0) {
            throw new DatabaseInformationException("Database mismatch - numFields <> numValues.");
        }

        // Let's bind the parameters
        for (int i = 0; i < countFields; i++) {
            contentValues.put(fields[i], String.valueOf(values[i]));
        }
        return mDatabaseHandler.update(table, contentValues, whereField + " = ?", new String[]{ String.valueOf(id) });
    }

    public final Cursor query(String t, String[] p, String s, String[] sA, String g, String h, String o) throws DatabaseInformationException {
        if (t == null || t.equals("")) {
            throw new DatabaseInformationException("No table selected.");
        } else if (p == null || p.length == 0) {
            p = new String[]{ "*" };
        }
        return mDatabaseHandler.query(t, p, s, sA, g, h, o);
    }

    public Cursor selectAll(String table, String orderBy) throws DatabaseInformationException {
        if (table == null || table.equals("")) {
            throw new DatabaseInformationException("No table selected.");
        } else {
            return mDatabaseHandler.query(
                table,
                new String[]{"*"},
                null,
                null,
                null,
                null,
                orderBy
            );
        }
    }
}
