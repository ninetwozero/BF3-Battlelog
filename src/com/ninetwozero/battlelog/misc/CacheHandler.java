/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
*/   

package com.ninetwozero.battlelog.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.text.TextUtils;
import android.util.Log;

import com.ninetwozero.battlelog.datatypes.PersonaStats;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.ProfileInformation;


/* 
 * Methods of this class should be loaded in AsyncTasks, as they would probably lock up the GUI
 */

public class CacheHandler {
	
	public static class Persona {
		
		public static long insert(Context context, PersonaStats stats) {
			
			try {
				//Use the SQLiteManager to get a cursor
				SQLiteManager manager = new SQLiteManager( context );
				
				//Get them!!
				long results = manager.insert( 
						
					DatabaseStructure.PersonaStatistics.TABLE_NAME, 
					DatabaseStructure.PersonaStatistics.getColumns(),
					stats.toStringArray()
					
				);
				
				manager.close();
				return results;
				
			} catch( Exception ex ) {
				
				ex.printStackTrace();
				return -1;
				
				
			}
			
		}
		
		public static long insert(Context context, HashMap<Long, PersonaStats> statsArray) {

			//Use the SQLiteManager to get a cursor
			SQLiteManager manager = new SQLiteManager( context );
			long results = 0;
			
			try {
				
				//Loop loop
				for( long key : statsArray.keySet() ) {

					//Get the object
					PersonaStats stats = statsArray.get( key );
					
					//Get them!!
					results += manager.insert( 
							
						DatabaseStructure.PersonaStatistics.TABLE_NAME, 
						DatabaseStructure.PersonaStatistics.getColumns(),
						stats.toStringArray()
						
					);
				
				}
				
				manager.close();
				return results;
				
			} catch( SQLiteConstraintException ex ) { //Duplicate input, no worries!

				manager.close();
				return 0;
			
			} catch( Exception ex ) {

				manager.close();
				ex.printStackTrace();
				return -1;
				
					
			}
			
		}
		
		public static boolean update(Context context, PersonaStats stats) {

			//Use the SQLiteManager to get a cursor
			SQLiteManager manager = new SQLiteManager( context );

			try {
				//UPDATE them!!
				manager.update(
						
					DatabaseStructure.PersonaStatistics.TABLE_NAME, 
					DatabaseStructure.PersonaStatistics.getColumns(),
					stats.toStringArray(),
					DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_PERSONA,
					stats.getPersonaId()
					
				);
				
				manager.close();
				return true;
				
			} catch( Exception ex ) {
				
				manager.close();
				ex.printStackTrace();
				return false;
				
				
			}
		
		}
			
		public static boolean update(Context context, HashMap<Long, PersonaStats> statsArray) {

			//Use the SQLiteManager to get a cursor
			SQLiteManager manager = new SQLiteManager( context );
			int results = 0;
			
			try {
				
				//Loop over the keys
				for( long key : statsArray.keySet() ) {

					//Get the object
					PersonaStats stats = statsArray.get( key );
					
					//UPDATE them!!
					results += manager.update(
							
						DatabaseStructure.PersonaStatistics.TABLE_NAME, 
						DatabaseStructure.PersonaStatistics.getColumns(),
						stats.toStringArray(),
						DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_PERSONA,
						stats.getPersonaId()
						
					);
					
				}

				//Close the manager
				manager.close();
				
				Log.d(Constants.DEBUG_TAG, "Rows updated: " + results);
				
				//Check the results
				return ( results > 0 );
				
			} catch( Exception ex ) {

				manager.close();
				ex.printStackTrace();
				return false;
				
			}
			
		}
	
		public static HashMap<Long, PersonaStats> select(final Context context, final long[] personaId) {

		
			SQLiteManager manager = new SQLiteManager( context );
			
			try {
	
				//Init
				String strQuestionMarks = "?";
				String[] personaIdArray = new String[personaId.length];
				HashMap<Long, PersonaStats> stats = new HashMap<Long, PersonaStats>();
				
				//Loop to string the array
				for( int i = 0, max = personaId.length; i < max; i++ ) { 
					
					if( i > 0 ) { strQuestionMarks += ",?"; }
					personaIdArray[i] = String.valueOf( personaId[i] ); 
					
				}
				
				//Use the SQLiteManager to get a cursor
				Cursor results = manager.query(
						
					DatabaseStructure.PersonaStatistics.TABLE_NAME, 
					null, 
					DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_PERSONA + " IN (" + strQuestionMarks + ")", 
					personaIdArray,
					null, 
					null, 
					DatabaseStructure.PersonaStatistics.DEFAULT_SORT_ORDER
					
				);
				
				//Loop over the results
				if( results.moveToFirst() ) {
				
					do {
						
						stats.put( 

							results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_PERSONA) ),							
							new PersonaStats(
									
								results.getString( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_ACCOUNT_NAME) ),
								results.getString( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_PERSONA_NAME) ),
								results.getString( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_RANK) ), 
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_RANK) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_PERSONA) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_USER) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_PLATFORM) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_TIME) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_POINTS_THIS) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_POINTS_NEXT) ),
								results.getInt( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_KILLS) ),
								results.getInt( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_ASSISTS) ),
								results.getInt( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_VEHICLES) ),
								results.getInt( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_VASSISTS) ),
								results.getInt( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_HEALS) ),
								results.getInt( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_REVIVES) ),
								results.getInt( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_REPAIRS) ),
								results.getInt( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_RESUPPLIES) ),
								results.getInt( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_DEATHS) ),
								results.getInt( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_WINS) ),
								results.getInt( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_NUM_LOSSES) ),
								results.getDouble( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_KDR) ),
								results.getDouble( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_ACCURACY) ),
								results.getDouble( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_LONGEST_HS) ),
								results.getDouble( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_LONGEST_KS) ),
								results.getDouble( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_SKILL) ),
								results.getDouble( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_STATS_SPM) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_ASSAULT) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_ENGINEER) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_SUPPORT) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_RECON) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_VEHICLE) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_COMBAT) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_AWARDS) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_UNLOCKS) ),
								results.getLong( results.getColumnIndex(DatabaseStructure.PersonaStatistics.COLUMN_NAME_SCORE_TOTAL ) )
						
							)	
								
						);
						
					} while( results.moveToNext() );
					
				}
				
				//PERSONA PERSONA BABY
				results.close();
				manager.close();
				return stats;
			
			} catch( Exception ex ) {
				
				ex.printStackTrace();
				manager.close();
				return null;
				
			}
			
		}
		
		public static boolean delete(final Context context, final long[] personaId) {

			SQLiteManager manager = new SQLiteManager( context );
			
			try {
				
				//Loop to string the array
				String[] personaIdArray = new String[personaId.length];				
				for( int i = 0, max = personaId.length; i < max; i++ ) { personaIdArray[i] = String.valueOf( personaId[i] ); }
				
				//Use the SQLiteManager to get a cursor
				int results = manager.delete( 
						
					DatabaseStructure.PersonaStatistics.TABLE_NAME,	
					DatabaseStructure.PersonaStatistics.COLUMN_NAME_ID_PERSONA,
					personaIdArray
					
				);

				manager.close();
				return (results > 0);
				
			} catch( Exception ex ) {
				
				ex.printStackTrace();
				manager.close();
				return false;
			
			}
			
		}
	}
		
	public static class Profile {
		
		public static long insert(Context context, ProfileInformation stats) {

			//Use the SQLiteManager to get a cursor
			SQLiteManager manager = new SQLiteManager( context );

			try {
	
				//Get them!!
				long results = manager.insert( 
						
					DatabaseStructure.UserProfile.TABLE_NAME, 
					DatabaseStructure.UserProfile.getColumns(),
					stats.toStringArray()
					
				);

				manager.close();
				return results;
				
			} catch( SQLiteConstraintException ex ) { //Duplicate input, no worries!

				manager.close();
				return 0;
			
			} catch( Exception ex ) {

				manager.close();
				ex.printStackTrace();
				return -1;
				
				
			}
			
		}
		
		public static boolean update(Context context, ProfileInformation stats) {

			//Use the SQLiteManager to get a cursor
			SQLiteManager manager = new SQLiteManager( context );
			int results = 0;
			
			try {

				//UPDATE them!!
				results += manager.update(
						
					DatabaseStructure.UserProfile.TABLE_NAME, 
					DatabaseStructure.UserProfile.getColumns(),
					stats.toStringArray(),
					DatabaseStructure.UserProfile.COLUMN_NAME_NUM_UID,
					stats.getUserId()
					
				);
				
				manager.close();
				return (results > 0);
				
			} catch( Exception ex ) {

				manager.close();
				ex.printStackTrace();
				return false;
				
				
			}
			
		}
	
		public static ProfileInformation select(final Context context, final long userId) {
	
			try {
				
				//Use the SQLiteManager to get a cursor
				SQLiteManager manager = new SQLiteManager( context );
				Cursor results = manager.query(
						
					DatabaseStructure.UserProfile.TABLE_NAME, 
					null, 
					DatabaseStructure.UserProfile.COLUMN_NAME_NUM_UID + " = ?", 
					new String[] { userId + "" },
					null, 
					null, 
					DatabaseStructure.UserProfile.DEFAULT_SORT_ORDER
					
				);
				
				//Loop over the results
				if( results.moveToFirst() ) {
				
					//Any platoons?
					String platoons = results.getString( results.getColumnIndex("platoons") );
					ArrayList<PlatoonData> platoonArray = new ArrayList<PlatoonData>();
					if( !"".equals( platoons ) ) {
						
						//platoonArray = CacheHandler.Platoons.select(context, platoons.split( ":" ));
						
					}
					
					//Get the strings
					String personaIdString = results.getString( results.getColumnIndex("persona_id") );
					String platformIdString = results.getString( results.getColumnIndex("platform_id") );
					String personaNameString = results.getString( results.getColumnIndex("persona_name") );
					//Split them
					String[] personaStringArray = TextUtils.split( personaIdString, ":" );
					String[] platformStringArray = TextUtils.split( platformIdString, ":" );
					String[] personaNameStringArray = TextUtils.split( personaNameString, ":" );
					
					//How many do we have? -1 due to last occurence being empty
					int numPersonas = personaStringArray.length-1;
					
					//Create two new arrays for this
					long[] personaIdArray = new long[numPersonas];
					String[] personaNameArray = new String[numPersonas];
					long[] platformIdArray = new long[numPersonas];
					
					for( int i = 0; i < numPersonas; i++ ) { 
						
						personaIdArray[i] = Long.parseLong( personaStringArray[i] );
						platformIdArray[i] = Long.parseLong( platformStringArray[i] );
						personaNameArray[i] = personaNameStringArray[i];
					
					}
					
					//Get the profile
					ProfileInformation profile = new ProfileInformation(
							
						results.getInt( results.getColumnIndex("age") ),
						results.getLong( results.getColumnIndex("user_id") ),
						results.getLong( results.getColumnIndex("birth_date") ),
						results.getLong( results.getColumnIndex("last_login") ),
						results.getLong( results.getColumnIndex("status_changed") ),
						personaIdArray,
						platformIdArray,
						results.getString( results.getColumnIndex("name") ),
						results.getString( results.getColumnIndex("username") ),
						results.getString( results.getColumnIndex("presentation") ),
						results.getString( results.getColumnIndex("location") ), 
						results.getString( results.getColumnIndex("status_message") ),
						results.getString( results.getColumnIndex("current_server") ),
						personaNameArray,
						( results.getString( results.getColumnIndex("allow_friendrequests") ).equalsIgnoreCase( "true" ) ),
						( results.getString( results.getColumnIndex("is_online") ).equalsIgnoreCase( "true" ) ),
						( results.getString( results.getColumnIndex("is_playing") ).equalsIgnoreCase( "true" ) ),
						( results.getString( results.getColumnIndex("is_friend") ).equalsIgnoreCase( "true" ) ),
						null,
						platoonArray
						
					);

					//PERSONA PERSONA BABY
					results.close();
					manager.close();
					
					//RETURN
					return profile;
					
					
				} else {
					
					results.close();
					manager.close();
					
					return null;
					
				}
			
			} catch( Exception ex ) {
				
				ex.printStackTrace();
				return null;
				
			}
			
		}
		
		public static boolean delete(final Context context, final long[] userId) {
	
			try {
				
				//Loop to string the array
				String[] userIdArray = new String[userId.length];				
				for( int i = 0, max = userId.length; i < max; i++ ) { userIdArray[i] = String.valueOf( userId[i] ); }
				
				//Use the SQLiteManager to get a cursor
				SQLiteManager manager = new SQLiteManager( context );
				int results = manager.delete( 
						
					DatabaseStructure.UserProfile.TABLE_NAME,	
					DatabaseStructure.UserProfile.COLUMN_NAME_NUM_UID,
					userIdArray
					
				);

				manager.close();
				return (results > 0);
				
			} catch( Exception ex ) {
				
				ex.printStackTrace();
				return false;
			
			}
			
		}
			
	}
	
	public static boolean isCached(Context c, String f) { 
		Log.d(Constants.DEBUG_TAG, "FILE: " + c.getFileStreamPath( PublicUtils.getCacheFileHandler(c).getPath() + f) );
		return ( c.getFileStreamPath( PublicUtils.getCacheFileHandler(c).getPath() + f ).exists() ); 
		
	}
}