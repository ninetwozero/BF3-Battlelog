
package com.ninetwozero.battlelog.misc;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.DatabaseInformationException;

public class SQLiteManager {

    class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE "
                    + DatabaseStructure.PersonaStatistics.TABLE_NAME
                    + " ("
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_RANK
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_PERSONA
                    + " INTEGER UNIQUE, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_USER
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_PLATFORM
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_ACCOUNT_NAME
                    + " TEXT, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_PERSONA_NAME
                    + " TEXT, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_RANK
                    + " TEXT, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_POINTS_NEXT
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_POINTS_THIS
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_KILLS
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_ASSISTS
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_VEHICLES
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_VASSISTS
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_HEALS
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_REVIVES
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_REPAIRS
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_RESUPPLIES
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_DEATHS
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_WINS
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_LOSSES
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_KDR
                    + " DOUBLE, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_ACCURACY
                    + " DOUBLE, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_LONGEST_HS
                    + " DOUBLE, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_LONGEST_KS
                    + " DOUBLE, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_TIME
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_SKILL
                    + " DOUBLE, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_SPM
                    + " DOUBLE, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_ASSAULT
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_ENGINEER
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_SUPPORT
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_RECON
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_VEHICLE
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_COMBAT
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_AWARDS
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_UNLOCKS
                    + " INTEGER, "
                    + DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_TOTAL
                    + " INTEGER" + ")");

            db.execSQL("CREATE TABLE "
                    + DatabaseStructure.UserProfile.TABLE_NAME
                    + " ("
                    + DatabaseStructure.UserProfile.COLUMN_NAME_ID
                    + " INTEGER PRIMARY KEY, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_NUM_AGE
                    + " INTEGER, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_NUM_UID
                    + " INTEGER UNIQUE, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_DATE_BIRTH
                    + " INTEGER, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_DATE_LOGIN
                    + " INTEGER, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_DATE_STATUS
                    + " INTEGER, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_STRING_NAME
                    + " STRING, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_STRING_USERNAME
                    + " STRING, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_STRING_PRESENTATION
                    + " STRING, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_STRING_LOCATION
                    + " STRING, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_STRING_STATUS
                    + " STRING, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_STRING_SERVER
                    + " STRING, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_STRING_PERSONA
                    + " STRING, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_STRING_PERSONA_NAME
                    + " STRING, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_STRING_PLATFORM
                    + " STRING, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_BOOL_ALLOW_REQUESTS
                    + " INTEGER, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_BOOL_ONLINE
                    + " INTEGER, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_BOOL_PLAYING
                    + " INTEGER, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_BOOL_IS_FRIEND
                    + " INTEGER, "
                    + DatabaseStructure.UserProfile.COLUMN_NAME_BOOL_PLATOONS
                    + " STRING " + " )");

            db.execSQL(

                    "CREATE TABLE "
                            + DatabaseStructure.PlatoonProfile.TABLE_NAME
                            + " ("
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

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            /* TODO: Handle DB UPDATEs */
            if ((newVersion - 2) > oldVersion) {

                this.onCreate(db);

            } else {

                if (oldVersion == 1) {

                    /*
                     * If it's the first version, then we do the specific things
                     */

                } else if (oldVersion == 2) {

                    /* if it's the second, we do specific things */

                } else {

                    /* Current version */
                    return;

                }

            }

        }

    }

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;
    private Context CONTEXT;
    private SQLiteDatabase DB;
    private SQLiteStatement STATEMENT;

    public SQLiteManager(Context context) {

        // Set the context
        this.CONTEXT = context;

        // Initialize an "OpenHelper"
        OpenHelper openHelper = new OpenHelper(this.CONTEXT);

        // Set the DB as writeAble
        this.DB = openHelper.getWritableDatabase();

    }

    public final void close() {

        if (this.STATEMENT != null)
            this.STATEMENT.close();
        if (this.DB != null)
            this.DB.close();
        return;

    }

    public int delete(String table, String field, String[] values)
            throws DatabaseInformationException {

        // Construct the Where
        String stringWhere = "";

        // How many values did we actually get?
        if (values == null || values.length == 0) {

            throw new DatabaseInformationException("No values received.");

        } else if (values.length == 1) {

            stringWhere = field + " = ?";

        } else {

            for (int i = 0; i < values.length; i++) {

                if (i == 0)
                    stringWhere += field + " = ?";
                else
                    stringWhere += " AND " + field + "= ?";

            }

        }

        // Let's remove from the DB
        return this.DB.delete(table, stringWhere, values);

    }

    public int deleteAll(String table) throws DatabaseInformationException {

        // Let's validate the table
        if (table == null || table.equals("")) {
            throw new DatabaseInformationException("No table selected.");
        }

        // Clear it
        return this.DB.delete(table, "1", null);

    }

    public long insert(String table, String[] fields, List<String[]> values)
            throws DatabaseInformationException {

        // Let's validate the table
        if (table == null || table.equals(""))
            throw new DatabaseInformationException("No table selected.");

        // Get the number of fields and values
        int countFields = fields.length;
        int countRows = values.size();
        int countValues = (countRows > 0) ? values.get(0).length : 0;

        String stringFields = "", stringValues = "";

        // Validate the number, ie 6 fields should have 6^(n rows) values
        if (countValues % countFields != 0) {

            throw new DatabaseInformationException(
                    "Database mismatch - numFields <> numValues.");

        } else {

            if (countFields == 0) {

                throw new DatabaseInformationException(
                        "Storage failed - no fields found.");

            } else if (countValues == 0) {

                throw new DatabaseInformationException(
                        "Storage failed - no values found.");

            } else {

                // Append the fields
                stringFields = TextUtils.join(",", fields);

                // Let's bind the parameters
                for (int i = 0; i < countRows; i++) {

                    stringValues += (i > 0) ? ", (" : "(";

                    for (int j = 0; j < countValues; j++) {

                        stringValues += (j > 0) ? ", ?" : "?";

                    }

                    stringValues += ")";

                }

            }

        }

        this.STATEMENT = this.DB.compileStatement("INSERT INTO " + table + "( "
                + stringFields + ") VALUES " + stringValues);

        // Let's bind the parameters
        for (int i = 1; i <= countRows; i++) {

            for (int j = 1; j <= countValues; j++) {

                this.STATEMENT.bindString((i * j), values.get(i - 1)[j - 1]);

            }

        }
        // this.STATEMENT.bindString( 1, name );
        return this.STATEMENT.executeInsert();
    }

    public int update(String table, String[] fields, String[] values,
            String whereField, long id) throws DatabaseInformationException {

        // Init
        ContentValues contentValues = new ContentValues();

        // Let's validate the table
        if (table == null || table.equals(""))
            throw new DatabaseInformationException("No table selected.");
        if (fields == null || fields.length < 1)
            throw new DatabaseInformationException("No fields selected.");
        if (values == null || values.length < 1)
            throw new DatabaseInformationException("No values sent.");

        // Get the number of fields and values
        int countFields = fields.length;
        int countValues = values.length;

        // Validate the number, ie 6 fields should have 6^(n rows) values
        if (countValues % countFields != 0) {

            throw new DatabaseInformationException(
                    "Database mismatch - numFields <> numValues.");

        }

        // Let's bind the parameters
        for (int i = 0; i < countFields; i++) {

            contentValues.put(fields[i], values[i]);

        }

        // EXECUTE!!!
        return this.DB.update(table, contentValues, whereField + " = ?",
                new String[] {
                    id + ""
                });

    }

    public long insert(String table, String[] fields, String[] values)
            throws DatabaseInformationException {

        // Let's validate the table
        if (table == null || table.equals(""))
            throw new DatabaseInformationException("No table selected.");

        // Get the number of fields and values
        int countFields = fields.length;
        int countValues = values.length;

        String stringFields = "", stringValues = "";

        // Validate the number, ie 6 fields should have 6^(n rows) values
        if (countValues % countFields != 0) {

            throw new DatabaseInformationException(
                    "Database mismatch - numFields <> numValues.");

        } else {

            if (countFields == 0) {

                throw new DatabaseInformationException(
                        "Storage failed - no fields found.");

            } else if (countValues == 0) {

                throw new DatabaseInformationException(
                        "Storage failed - no values found.");

            } else {

                // Append the fields
                stringFields = TextUtils.join(",", fields);

                // Let's bind the parameters
                for (int j = 0; j < countValues; j++) {
                    stringValues += (j > 0) ? ", ?" : "?";
                }

            }

        }

        this.STATEMENT = this.DB.compileStatement("INSERT INTO " + table + "( "
                + stringFields + ") VALUES " + "(" + stringValues + ")");

        // Let's bind the parameters
        for (int j = 1; j <= countValues; j++) {
            this.STATEMENT.bindString(j, values[j - 1]);
        }

        // this.STATEMENT.bindString( 1, name );
        return this.STATEMENT.executeInsert();
    }

    public final Cursor query(String t, String[] p, String s, String[] sA,
            String g, String h, String o) throws DatabaseInformationException {

        // Let's validate the table
        if (t == null || t.equals(""))
            throw new DatabaseInformationException("No table selected.");
        if (p == null || p.length == 0)
            p = new String[] {
                    "*"
            };

        // Let's return the query
        return this.DB.query(t, p, s, sA, g, h, o);

    }

    public Cursor selectAll(String table, String orderBy)
            throws DatabaseInformationException {

        // Let's validate the table
        if (table == null || table.equals(""))
            throw new DatabaseInformationException("No table selected.");

        // We need to select a table
        return this.DB.query(table, new String[] {
                "*"
        }, null, null, null,
                null, orderBy);

    }
}
