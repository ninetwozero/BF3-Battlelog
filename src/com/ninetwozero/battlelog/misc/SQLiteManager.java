package com.ninetwozero.battlelog.misc;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.ninetwozero.battlelog.datatypes.DatabaseInformationException;

public class SQLiteManager {

	class OpenHelper extends SQLiteOpenHelper {

		OpenHelper( Context context ) {

			super( context, DATABASE_NAME, null, DATABASE_VERSION );
		}

		@Override
		public void onCreate( SQLiteDatabase db ) {
			
			db.execSQL( 
				"CREATE TABLE "
				+ DatabaseStructure.PersonaStatistics.TABLE_NAME + " ("
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID
				+ " INTEGER PRIMARY KEY, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_TITLE
				+ " TEXT, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_TOKEN
				+ " TEXT, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_OWNER
				+ " TEXT, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_USERNAME
				+ " TEXT, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_AVGSPEED
				+ " TEXT, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_DISTANCE
				+ " TEXT, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_DURATION
				+ " INTEGER, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_MAXALTITUDE
				+ " TEXT, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_DEST_LATITUDE
				+ " TEXT, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_DEST_LONGITUDE
				+ " TEXT, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_DEST_ALTITUDE
				+ " TEXT, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATUS 
				+ " TEXT, "
				+ DatabaseStructure.PersonaStatistics.COLUMN_NAME_DATE
				+ " INTEGER NOT NULL "
				+ ")"
			);

			db.execSQL(
				"CREATE TABLE "
				+ DatabaseStructure.PrivateLocations.TABLE_NAME + " ("
				+ DatabaseStructure.PrivateLocations.COLUMN_NAME_ID
				+ " INTEGER PRIMARY KEY, "
				+ DatabaseStructure.PrivateLocations.COLUMN_NAME_TITLE
				+ " TEXT, "
				+ DatabaseStructure.PrivateLocations.COLUMN_NAME_USERNAME
				+ " TEXT, "
				+ DatabaseStructure.PrivateLocations.COLUMN_NAME_DEST_LATITUDE
				+ " TEXT, "
				+ DatabaseStructure.PrivateLocations.COLUMN_NAME_DEST_LONGITUDE
				+ " TEXT, "
				+ DatabaseStructure.PrivateLocations.COLUMN_NAME_DEST_ALTITUDE
				+ " TEXT, "
				+ DatabaseStructure.PrivateLocations.COLUMN_NAME_DATE
				+ " INTEGER NOT NULL "
				+ ")" 
			);
			
			db.execSQL(
					"CREATE TABLE "
					+ DatabaseStructure.Invites.TABLE_NAME + " ("
					+ DatabaseStructure.Invites.COLUMN_NAME_ID
					+ " INTEGER, "
					+ DatabaseStructure.Invites.COLUMN_NAME_TRANSMISSION_ID
					+ " INTEGER, "
					+ DatabaseStructure.Invites.COLUMN_NAME_TITLE
					+ " TEXT, "
					+ DatabaseStructure.Invites.COLUMN_NAME_USERNAME_FROM
					+ " TEXT, "
					+ DatabaseStructure.Invites.COLUMN_NAME_USERNAME_TO
					+ " TEXT, "
					+ DatabaseStructure.Invites.COLUMN_NAME_DEST_LATITUDE
					+ " TEXT, "
					+ DatabaseStructure.Invites.COLUMN_NAME_DEST_LONGITUDE
					+ " TEXT, "
					+ DatabaseStructure.Invites.COLUMN_NAME_DEST_ALTITUDE
					+ " TEXT, "
					+ DatabaseStructure.Invites.COLUMN_NAME_DATE
					+ " INTEGER NOT NULL "
					+ ")" 
				);
		}

		@Override
		public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {

			/* TODO: Handle DB UPDATEs */
			if ( ( newVersion - 2 ) > oldVersion ) {

				this.onCreate( db );

			} else {

				if ( oldVersion == 1 ) {

					/*
					 * If it's the first version, then we do the specific
					 * things
					 */

				} else if ( oldVersion == 2 ) {

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

	public SQLiteManager( Context context ) {

		// Set the context
		this.CONTEXT = context;

		// Initialize an "OpenHelper"
		OpenHelper openHelper = new OpenHelper( this.CONTEXT );

		// Set the DB as writeAble
		this.DB = openHelper.getWritableDatabase();

	}
	public final void close() {
		
		if( this.DB != null ) this.DB.close();
		if( this.STATEMENT != null) this.STATEMENT.close();
		return;
		
	}
	
	public int delete( String table, String field, String[] values ) throws DatabaseInformationException {

		//Construct the Where
		String stringWhere = "";
		
		//How many values did we actually get?
		if( values == null || values.length == 0 ) {
			
			throw new DatabaseInformationException("No values recieved.");
		
		} else if( values.length == 1 ) {
			
			stringWhere = field + " = ?";
			
		} else {
			
			for( int i = 0; i < values.length; i++ ) {
				
				if( i == 0 ) stringWhere += field + " = ?";
				else stringWhere += " AND " + field + "= ?";
			
			}
		
		}
		
		//Let's remove from the DB
		return this.DB.delete( table, stringWhere, values); 
		
	}
	

	public int deleteAll( String table ) throws DatabaseInformationException { 
		
		//Let's validate the table
		if( table == null || table.equals( "" ) ) throw new DatabaseInformationException("No table selected.");
		
		//Clear it
		return this.DB.delete( table, "1", null ); 
			
		}
		
public long insert( String table, String[] fields, ArrayList<String[]> values ) throws DatabaseInformationException {
			
			//Let's validate the table
		if( table == null || table.equals( "" ) ) throw new DatabaseInformationException("No table selected.");
		
		//Get the number of fields and values
		int countFields = fields.length;
		int countRows = values.size();
		int countValues = ( countRows > 0 )? values.get( 0 ).length : 0;
		
		String stringFields = "", stringValues = "";
		
		//Validate the number, ie 6 fields should have 6^(n rows) values
		if( countValues % countFields != 0 ) {
		
			throw new DatabaseInformationException("Database mismatch - numFields <> numValues.");
			
		} else {
			
			if( countFields == 0 ) {
	
				throw new DatabaseInformationException("Storage failed - no fields found.");
				
			} else if( countValues == 0 ) {
				
				throw new DatabaseInformationException("Storage failed - no values found.");
				
			} else {
				
				//Append the fields
				stringFields = TextUtils.join(",", fields);		
				
				//Let's bind the parameters
				for( int i = 0; i < countRows; i++ ) {
					
					stringValues += ( i > 0 )? ", (" : "(";
					
					for( int j = 0; j < countValues; j++ ) {

						stringValues += ( j > 0 )? ", ?" : "?";
					
					}
					
					stringValues += ")";
			
				}
				
			}
			
		}
		
		this.STATEMENT = this.DB.compileStatement(
			"INSERT INTO " + table + 
			"( " + stringFields + ") VALUES " + stringValues
		);
		
		//Let's bind the parameters
		for( int i = 1; i <= countRows; i++ ) {
			
			for( int j = 1; j <= countValues; j++ ) {
				
				this.STATEMENT.bindString( (i*j), values.get( i-1 )[j-1] );
			
			}
	
		}
		//this.STATEMENT.bindString( 1, name );
		return this.STATEMENT.executeInsert();
	}

	public void update( String table, String[] fields, String[] values, long id ) throws DatabaseInformationException {
		
		//Let's validate the table
		if( table == null || table.equals( "" ) ) throw new DatabaseInformationException("No table selected.");
		if( fields == null || fields.length < 1 ) throw new DatabaseInformationException("No fields selected.");
		if( values == null || values.length < 1 ) throw new DatabaseInformationException("No values sent.");
		
		//Get the number of fields and values
		int countFields = fields.length;
		int countValues = values.length;
		String genQueryString = "", whereQueryString = " WHERE " + table + ".`_id` = ?";
		
		//Validate the number, ie 6 fields should have 6^(n rows) values
		if( countValues % countFields != 0 ) {
		
			throw new DatabaseInformationException("Database mismatch - numFields <> numValues.");
			
		}
	
		//Let's bind the parameters
		for( int i = 0; i < countFields; i++ ) { genQueryString += ( i == 0 )? " SET " + fields[i] + " = ?" : ", " + fields[i] + " = ?"; }
				
		
		this.STATEMENT = this.DB.compileStatement(
			"UPDATE " + table + genQueryString + whereQueryString
		);

		//Let's bind the parameters
		for( int i = 0; i < countValues; i++ ) { this.STATEMENT.bindString(i+1, values[i] ); }
		this.STATEMENT.bindLong( countValues+1, id );
		
		//EXECUTE!!!
		this.STATEMENT.execute();
		return;
		
	}

	public long insert( String table, String[] fields, String[] values ) throws DatabaseInformationException {
		
		//Let's validate the table
		if( table == null || table.equals( "" ) ) throw new DatabaseInformationException("No table selected.");
		
		//Get the number of fields and values
		int countFields = fields.length;
		int countValues = values.length;
		
		String stringFields = "", stringValues = "";
		
		//Validate the number, ie 6 fields should have 6^(n rows) values
		if( countValues % countFields != 0 ) {
		
			throw new DatabaseInformationException("Database mismatch - numFields <> numValues.");
			
		} else {
			
			if( countFields == 0 ) {
	
				throw new DatabaseInformationException("Storage failed - no fields found.");
				
			} else if( countValues == 0 ) {
				
				throw new DatabaseInformationException("Storage failed - no values found.");
				
			} else {
				
				//Append the fields
				stringFields = TextUtils.join(",", fields);		
				
				//Let's bind the parameters
				for( int j = 0; j < countValues; j++ ) { stringValues += ( j > 0 )? ", ?" : "?"; }
				
			}
			
		}
		
		this.STATEMENT = this.DB.compileStatement(
			"INSERT INTO " + table + 
			"( " + stringFields + ") VALUES " + "(" + stringValues + ")"
		);
		
		//Let's bind the parameters
		for( int j = 1; j <= countValues; j++ ) { this.STATEMENT.bindString( j, values[j-1] ); }

		//this.STATEMENT.bindString( 1, name );
		return this.STATEMENT.executeInsert();
	}

	public final Cursor query( String t, String[] p, String s, String[] sA,
			String g, String h, String o ) throws DatabaseInformationException {

		//Let's validate the table
		if( t == null || t.equals( "" ) ) throw new DatabaseInformationException("No table selected.");
		if( p == null || p.length == 0 ) p = new String[]{"*"};
		
		//Let's return the query
		return this.DB.query( t, p, s, sA, g, h, o );

	}
	
	public Cursor selectAll( String table, String orderBy ) throws DatabaseInformationException {

		//Let's validate the table
		if( table == null || table.equals( "" ) ) throw new DatabaseInformationException("No table selected.");
		
		//We need to select a table
		return this.DB.query( table, new String[] { "*" }, null, null, null, null, orderBy );

	}
}