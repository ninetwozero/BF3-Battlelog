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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import net.sf.andhsli.hotspotlogin.SimpleCrypto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ninetwozero.battlelog.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ninetwozero.battlelog.datatypes.ChatMessage;
import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.FriendListDataWrapper;
import com.ninetwozero.battlelog.datatypes.NotificationData;
import com.ninetwozero.battlelog.datatypes.PersonaStats;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.PlatoonInformation;
import com.ninetwozero.battlelog.datatypes.PlatoonStats;
import com.ninetwozero.battlelog.datatypes.PlatoonStatsItem;
import com.ninetwozero.battlelog.datatypes.PlatoonTopStatsItem;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileComparator;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ProfileInformation;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.TopStatsComparator;
import com.ninetwozero.battlelog.datatypes.UnlockComparator;
import com.ninetwozero.battlelog.datatypes.UnlockData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.services.BattlelogService;

/* 
 * Methods of this class should be loaded in AsyncTasks, as they would probably lock up the GUI
 */

public class WebsiteHandler {
	
	//Let's have this one ready
	public static HashMap<String, Bitmap> bitmapCache = new HashMap<String, Bitmap>();
	public static HashMap<String, Object> feedCache = new HashMap<String, Object>();
	
	public static ProfileData doLogin(Context context, PostData[] postDataArray, boolean savePassword) throws WebsiteHandlerException {
	
		//Init
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
		SharedPreferences.Editor spEdit = sharedPreferences.edit();
		String[] tempString = new String[10];
		String httpContent = "";
		ProfileData profile = null;
	
		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
    		httpContent = wh.post( Constants.URL_LOGIN, postDataArray, 0);
    		
    		//Did we manage?
    		if( httpContent != null && !httpContent.equals( "" ) ) {
    			
    			//Set the int
    			int startPosition = httpContent.indexOf( Constants.ELEMENT_UID_LINK );
    			String[] bits;
    			
    			//Did we find it?
    			if( startPosition == -1 ) {
    				
    				tempString[0] = httpContent.substring( 
    						
    					httpContent.indexOf( 
    							
    						Constants.ELEMENT_ERROR_MESSAGE 
    						
    					) 
    					
    				).replace( "</div>" , "" ).replace( Constants.ELEMENT_ERROR_MESSAGE, "" );
        				
       				Toast.makeText( context, tempString[0], Toast.LENGTH_SHORT).show();
    				return null;

    			}
    			
    			//Cut out the appropriate bits (<a class="SOME CLASS HERE" href="A LONG LINK HERE">NINETWOZERO
	    		tempString[0] = httpContent.substring( startPosition );
				tempString[0] = tempString[0].substring( 0, tempString[0].indexOf("\">") ); 
				bits = TextUtils.split( tempString[0].replace( Constants.ELEMENT_UID_LINK, ""), "/");
				
				//Get the checksum
				tempString[1] = httpContent.substring( httpContent.indexOf( Constants.ELEMENT_STATUS_CHECKSUM ) );
				tempString[1] = tempString[1].substring( 0, tempString[1].indexOf( "\" />") ).replace( Constants.ELEMENT_STATUS_CHECKSUM, "" );
				
				//Let's work on getting the "username", not persona name --> profileId
				tempString[2] = httpContent.substring( httpContent.indexOf( Constants.ELEMENT_USERNAME_LINK ) );
				tempString[2] = tempString[2].substring( 0, tempString[2].indexOf( "/\">") ).replace( Constants.ELEMENT_USERNAME_LINK, "" );
				profile = WebsiteHandler.getProfileIdFromSearch( tempString[2], tempString[1]);

				//Further more, we would actually like to store the userid and name
				spEdit.putString( Constants.SP_BL_EMAIL, postDataArray[0].getValue() );
				
				//Should we remember the password?
				if( savePassword ) {

					spEdit.putString( Constants.SP_BL_PASSWORD, SimpleCrypto.encrypt( postDataArray[0].getValue(), postDataArray[1].getValue() ) );
					spEdit.putBoolean( Constants.SP_BL_REMEMBER, true );
					
				} else {
					
					spEdit.putString( Constants.SP_BL_PASSWORD, "" );
					spEdit.putBoolean( Constants.SP_BL_REMEMBER, false);
				}
				
				//This we keep!!!
				spEdit.putString( Constants.SP_BL_USERNAME, tempString[2] );
				spEdit.putString( Constants.SP_BL_PERSONA,  bits[0]);
				spEdit.putLong( Constants.SP_BL_PROFILE_ID, profile.getProfileId());
				spEdit.putLong( Constants.SP_BL_PERSONA_ID,  Long.parseLong( bits[2] ));				
				spEdit.putLong( Constants.SP_BL_PLATFORM_ID,  DataBank.getPlatformIdFromName(bits[3]) );
				spEdit.putString( Constants.SP_BL_CHECKSUM, tempString[1]);
				
				//Co-co-co-commit
				spEdit.commit();
		        
		        //Do we want to start a service?
		        if( !PublicUtils.isMyServiceRunning(context) && sharedPreferences.getBoolean( Constants.SP_BL_SERVICE, true ) ) {
		        	
		        	context.startService( new Intent(context, BattlelogService.class) );
		        
		        } else if( !BattlelogService.isRunning() ) {
		        	
		        	BattlelogService.start(context);
		        	
		        }
				
				//Return it!!
				return profile;
				
    		} else {
    			
    			return null;
    			
    		}
    		
		} catch( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException("Failed to log-in.");
			
		}
		
	}
	
	public static ProfileData getProfileIdFromSearch(final String keyword, final String checksum) throws WebsiteHandlerException {
		
		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			ProfileData profile = null;
			
			//Get the content
			httpContent = wh.post( 
				
				Constants.URL_PROFILE_SEARCH, 
				new PostData[] {
						 
					new PostData(Constants.FIELD_NAMES_PROFILE_SEARCH[0], keyword),
					new PostData(Constants.FIELD_NAMES_PROFILE_SEARCH[1], checksum)
					
				}, 
				0
				
			);

			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Generate an object
				JSONArray searchResults = new JSONObject(httpContent).getJSONObject( "data" ).getJSONArray( "matches" ); 

				//Did we get any results?
				if( searchResults.length() > 0 ) {
					
					//init cost counters
					int costOld = 999, costCurrent = 0;
					
					//Iterate baby!
					for( int i = 0, max = searchResults.length(); i < max; i++ ) {
						
						//Get the JSONObject
						JSONObject tempObj = searchResults.optJSONObject( i );
						
						//A perfect match?
						if( tempObj.getString( "username" ).equals( keyword ) ) {
							
							profile = new ProfileData(
								tempObj.getString( "username" ),
								tempObj.getString( "username" ),
								0,
								Long.parseLong( tempObj.getString( "userId" ) ),
								0,
								tempObj.optString( "gravatarMd5", "" )

							);
							
							break;
						
						}
						
						//Grab the "cost"
						costCurrent = PublicUtils.getLevenshteinDistance( keyword, tempObj.getString( "username" ) );
						
						//Somewhat of a match? Get the "best" one!
						if( costOld > costCurrent ) {
	
							profile = new ProfileData(
								
								tempObj.getString( "username" ),
								tempObj.getString( "username" ),
								0,
								Long.parseLong( tempObj.getString( "userId" ) ),
								0,
								tempObj.optString( "gravatarMd5", "" )
							
							);
							
						}
						
						//Shuffle!
						costOld = costCurrent;
						
					}
					
					return WebsiteHandler.getPersonaIdFromProfile( profile );

				}
				
				return null;
			
			} else {
			
				throw new WebsiteHandlerException("Could not retrieve the ProfileIDs.");
			
			}
		
		} catch ( JSONException e ) {
		
			throw new WebsiteHandlerException(e.getMessage());
		
		} catch ( RequestHandlerException ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}

	public static int applyForPlatoonMembership(final long platoonId, final String checksum) throws WebsiteHandlerException {
		
		try {
			
			//Let's set it up!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			
			//Do the actual request
			httpContent = wh.post(
					
				Constants.URL_PLATOON_APPLY, 
				new PostData[] {
						
					new PostData(Constants.FIELD_NAMES_PLATOON_APPLY[0], platoonId + ""),
					new PostData(Constants.FIELD_NAMES_PLATOON_APPLY[1], checksum)
					
				},
				3
				
			);
			
			//What up?
			if( httpContent == null || httpContent.equals( "" ) ) {
				
				return -1; // Invalid request
				
			} else {

				if( httpContent.equals( "success" ) ) { //OK!
					
					return 0; 
					
				} else if( httpContent.equals( "wrongplatform" ) ) { //Wrong platform
					
					return 1; 
					
				} else if( httpContent.equals( "maxmembersreached" ) ) { //Full platoon
					
					return 2;
				
				} else { //unknown
					
					return 3;
					
				}
				
			}
			
		} catch( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException( ex.getMessage() );
		
		}
		
	}

	public static boolean closePlatoonMembership(final long platoonId, final long userId, final String checksum) throws WebsiteHandlerException {
		
		try {
			
			//Let's set it up!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			
			//Do the actual request
			httpContent = wh.post(
					
				Constants.URL_PLATOON_LEAVE, 
				new PostData[] {

					new PostData(Constants.FIELD_NAMES_PLATOON_LEAVE[0], platoonId + ""),
					new PostData(Constants.FIELD_NAMES_PLATOON_LEAVE[1], userId + ""),
					new PostData(Constants.FIELD_NAMES_PLATOON_LEAVE[2], checksum)
					
				}, 
				3
				
			);
			
			//What up?
			if( httpContent == null || httpContent.equals( "" ) ) {
				
				return false;
				
			} else {
			
				return true;
				
			}
			
		} catch( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException( ex.getMessage() );
		
		}
		
	}
	
	public static PlatoonData getPlatoonIdFromSearch(final String keyword, final String checksum) throws WebsiteHandlerException {
		
		try {
			
			//Let's do this!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			PlatoonData platoon = null;
			
			//Get the content
			httpContent = wh.post( 
				
				Constants.URL_PLATOON_SEARCH, 
				new PostData[] {
						 
					new PostData(Constants.FIELD_NAMES_PLATOON_SEARCH[0], keyword),
					new PostData(Constants.FIELD_NAMES_PLATOON_SEARCH[1], checksum)
					
				}, 
				3
				
			);
			
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Generate an object
				JSONArray searchResults = new JSONArray(httpContent);
				
				//Did we get any results?
				if( searchResults.length() > 0 ) {
					
					//init cost counters
					int costOld = 999, costCurrent = 0;
					
					//Iterate baby!
					for( int i = 0, max = searchResults.length(); i < max; i++ ) {
						
						//Get the JSONObject
						JSONObject tempObj = searchResults.optJSONObject( i );
						
						//Is it visible?
						if( tempObj.getBoolean("hidden") ) { continue; }
						
						//A perfect match?
						if( tempObj.getString( "name" ).equals( keyword ) ) {
							
							platoon = new PlatoonData(
								Long.parseLong( tempObj.getString( "id" ) ),
								tempObj.getInt( "fanCounter" ),
								tempObj.getInt( "memberCounter" ),
								tempObj.getInt( "platform" ),
								tempObj.getString( "name" ),
								tempObj.getString( "tag" ),
								null,
								true
							);
							
							break;
						
						}
						
						//Grab the "cost"
						costCurrent = PublicUtils.getLevenshteinDistance( keyword, tempObj.getString( "name" ) );
						
						//Somewhat of a match? Get the "best" one!
						if( costOld > costCurrent ) {
	
							platoon = new PlatoonData(
									
								Long.parseLong( tempObj.getString( "id" ) ),
								tempObj.getInt( "fanCounter" ),
								tempObj.getInt( "memberCounter" ),
								tempObj.getInt( "platform" ),
								tempObj.getString( "name" ),
								tempObj.getString( "tag" ),
								null,
								true
								
							);
							
						}
						
						//Shuffle!
						costOld = costCurrent;
						
					}
					
					return platoon;

				}
				
				throw new WebsiteHandlerException("No platoons found.");
			
			} else {
			
				throw new WebsiteHandlerException("Could not retreive the ProfileIDs.");
			
			}
		
		} catch ( Exception ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
	public static ProfileData getPersonaIdFromProfile(long profileId) throws WebsiteHandlerException {

		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			
			//Get the content
			httpContent = wh.get( Constants.URL_PROFILE.replace( "{UID}", profileId + ""), 0);

			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Generate an object
				JSONObject personaObject = new JSONObject(httpContent).getJSONObject( "data" ).getJSONArray( "soldiersBox" ).optJSONObject( 0 ).getJSONObject( "persona" );
				
				return new ProfileData( 
					personaObject.getString("personaName"),
					personaObject.getString("personaName"),
					personaObject.getLong( "personaId" ), 
					profileId,
					DataBank.getPlatformIdFromName( personaObject.getString( "namespace" ) ),
					personaObject.optString( "gravatarMd5", "" )
				);
			
			} else {
			
				throw new WebsiteHandlerException("Could not retrieve the PersonaID.");
			
			}
			
		
		} catch ( JSONException e ) {
		
			throw new WebsiteHandlerException(e.getMessage());
		
		} catch ( RequestHandlerException ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
	public static ProfileData getPersonaIdFromProfile(ProfileData p) throws WebsiteHandlerException {

		try {
			
			ProfileData profile = WebsiteHandler.getPersonaIdFromProfile( p.getProfileId() );
			return new ProfileData(
				
				p.getAccountName(),
				profile.getPersonaName(),
				profile.getPersonaId(),
				p.getProfileId(),
				profile.getPlatformId(),
				profile.getGravatarHash()
					
			);
			
		} catch( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException("No profile found");
			
		}
	}
	
	public static PersonaStats getStatsForUser(ProfileData pd) throws WebsiteHandlerException {
		
		try {
			
			//Do we have a personaId?
			if( pd.getPersonaId() == 0 ) {
				
				pd = getPersonaIdFromProfile(pd.getProfileId());
				
			}
			
	    	//Get the data
	    	RequestHandler wh = new RequestHandler();
	    	String content = wh.get( 
    	
    			Constants.URL_STATS_OVERVIEW.replace(
    					
					"{PID}", pd.getPersonaId() + ""
				
				).replace( 
				
					"{PLATFORM_ID}", pd.getPlatformId() + ""
				),
				0
			
			);
	    	
	    	//JSON Objects
	    	JSONObject dataObject = new JSONObject(content).getJSONObject( "data" );
	    	
	    	//Is overviewStats NULL? If so, no data.
	    	if( dataObject.isNull( "overviewStats" ) ) { return null; }
	    	
	    	//Keep it up!
	    	JSONObject statsOverview = dataObject.getJSONObject( "overviewStats" );
	    	JSONObject kitScores = statsOverview.getJSONObject( "kitScores" );
	    	JSONObject nextRankInfo = dataObject.getJSONObject( "rankNeeded" );
	    	JSONObject currRankInfo = dataObject.getJSONObject( "currentRankNeeded" );
	   
	        //Yay
	        return new PersonaStats(
	        	pd.getAccountName(),
	        	pd.getPersonaName(),
	        	currRankInfo.getString( "name" ),
	        	statsOverview.getLong( "rank" ),
	        	pd.getPersonaId(),
	        	pd.getProfileId(),
	        	pd.getPlatformId(),
	        	statsOverview.getLong( "timePlayed" ),
	        	currRankInfo.getLong( "pointsNeeded" ),
	        	nextRankInfo.getLong( "pointsNeeded" ),
	        	statsOverview.getInt( "kills" ),
	        	statsOverview.getInt( "killAssists" ),
	        	statsOverview.getInt( "vehiclesDestroyed" ),
	        	statsOverview.getInt( "vehiclesDestroyedAssists" ),
	        	statsOverview.getInt( "heals" ),
	        	statsOverview.getInt( "revives" ),
	        	statsOverview.getInt( "repairs" ),
	        	statsOverview.getInt( "resupplies" ),
	        	statsOverview.getInt( "deaths" ),
	        	statsOverview.getInt( "numWins" ),
	        	statsOverview.getInt( "numLosses" ),
	        	statsOverview.getDouble( "kdRatio" ),
	        	statsOverview.getDouble( "accuracy" ),
	        	statsOverview.getDouble( "longestHeadshot" ),
	        	statsOverview.getDouble( "killStreakBonus" ),
	        	statsOverview.getDouble( "elo" ),
	        	statsOverview.getDouble( "scorePerMinute" ),
	        	kitScores.getLong( "1" ),
	        	kitScores.getLong( "2" ),
	        	kitScores.getLong( "32" ),
	        	kitScores.getLong( "8" ),
	        	statsOverview.getLong( "sc_vehicle" ),
	        	statsOverview.getLong( "combatScore" ),
	        	statsOverview.getLong( "sc_award" ),
	        	statsOverview.getLong( "sc_unlock" ),
	        	statsOverview.getLong( "totalScore" )
    		);
	    
		} catch ( Exception ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}

	public static ArrayList<UnlockData> getUnlocksForUser(ProfileData pd) throws WebsiteHandlerException {
		
		try {
			
	    	//Get the data
	    	RequestHandler wh = new RequestHandler();
	    	ArrayList<UnlockData> unlockArray = new ArrayList<UnlockData>();
	    	String content = wh.get( 
		
				Constants.URL_STATS_UNLOCKS.replace(
						
					"{PID}", pd.getPersonaId() + ""
				
				).replace( 
				
					"{PLATFORM_ID}", pd.getPlatformId() + ""
				), 
				0
				
			);
	    	
	    	//JSON Objects
	    	JSONObject dataObject = new JSONObject(content).getJSONObject( "data" );
	    	JSONArray unlockResults = dataObject.getJSONArray( "unlocks" );
	    	JSONObject unlockRow, detailObject;
	    	int unlockKit;
	    	
	    	//Iterate over the unlocksArray
	    	for( int i = 0, max = unlockResults.length(); i < max; i++ ) {
	    	
	    		//Get the temporary object
	    		unlockRow = unlockResults.optJSONObject( i );
	
	    		//Less than 1.0?
	    		if( unlockRow.getDouble( "unlockPercentage" ) < 1.0 ) { continue; }
	    		
	    		try {
	
	    			unlockKit = unlockRow.getInt( "kit" );
	    		
	    		} catch( Exception ex ) {
	    			
	    			unlockKit = 0;
	    			
	    		}
	    		
	    		//What did we get?
	    		if( !unlockRow.isNull( "weaponAddonUnlock" ) ) {
	    			
	    			//Get the object
	    			detailObject = unlockRow.getJSONObject( "weaponAddonUnlock" );
	    			
	    			//Add them to the array
	    			unlockArray.add( 
	    				
	    				new UnlockData(
	    				
							unlockKit,
							unlockRow.getDouble( "unlockPercentage" ),
							detailObject.getLong( "valueNeeded" ),
							detailObject.getLong( "actualValue" ),
							detailObject.getString( "weaponCode" ),
							detailObject.getString( "unlockId" ),
							detailObject.getString( "codeNeeded" ),
							"weapon+"
	    						
						)	
	    					
					);
	  
	    		} else if( !unlockRow.isNull( "kitItemUnlock" ) ) {
	
	    			//Get the object
	    			detailObject = unlockRow.getJSONObject( "kitItemUnlock" );
	    			
	    			//Add them to the array
	    			unlockArray.add( 
	    				
	    				new UnlockData(
	    				
							unlockKit,
							unlockRow.getDouble( "unlockPercentage" ),
							detailObject.getLong( "valueNeeded" ),
							detailObject.getLong( "actualValue" ),
							unlockRow.getString( "parentId" ),
							detailObject.getString( "unlockId" ),
							detailObject.getString( "codeNeeded" ),
							"kit+"
	    						
						)	
	    					
					);
	    	    	
	    		} else if( !unlockRow.isNull( "vehicleAddonUnlock" ) ) {
	
	    			//Get the object
	    			detailObject = unlockRow.getJSONObject( "vehicleAddonUnlock" );
	    			
	    			//Add them to the array
	    			unlockArray.add( 
	    				
	    				new UnlockData(
	    				
							unlockKit,
							unlockRow.getDouble( "unlockPercentage" ),
							detailObject.getLong( "valueNeeded" ),
							detailObject.getLong( "actualValue" ),
							unlockRow.getString( "parentId" ),
							detailObject.getString( "unlockId" ),
							detailObject.getString( "codeNeeded" ),
							"vehicle+"
	    						
						)	
	    					
					);
	    	    	
	    		} else if( !unlockRow.isNull( "weaponUnlock" ) ) {
	
	    			//Get the object
	    			detailObject = unlockRow.getJSONObject( "weaponUnlock" );
	    			
	    			//Add them to the array
	    			unlockArray.add( 
	    				
	    				new UnlockData(
	    				
							unlockKit,
							unlockRow.getDouble( "unlockPercentage" ),
							detailObject.getLong( "valueNeeded" ),
							detailObject.getLong( "actualValue" ),
							unlockRow.getString( "parentId" ),
							detailObject.getString( "unlockId" ),
							detailObject.getString( "codeNeeded" ),
							"weapon"
	    						
						)	
	    					
					);
	    	    	
	    		} else if( !unlockRow.isNull( "soldierSpecializationUnlock" ) ) {
	
	    			//Get the object
	    			detailObject = unlockRow.getJSONObject( "soldierSpecializationUnlock" );
	    			
	    			//Add them to the array
	    			unlockArray.add( 
	    				
	    				new UnlockData(
	    				
							unlockKit,
							unlockRow.getDouble( "unlockPercentage" ),
							detailObject.getLong( "valueNeeded" ),
							detailObject.getLong( "actualValue" ),
							unlockRow.getString( "parentId" ),
							detailObject.getString( "unlockId" ),
							detailObject.getString( "codeNeeded" ),
							"skill"
	    						
						)	
	    					
					);
	    			
	    		} else {}
	    	}
	    	
	        //Yay
	    	Collections.sort( unlockArray, new UnlockComparator() );
	
	    	//RETURN TO SENDER
	        return unlockArray;
	    
		} catch ( Exception ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
	public static final FriendListDataWrapper getFriendsCOM(String checksum) throws WebsiteHandlerException {
		
		try {
				
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			
			//Get the content
			httpContent = wh.post( 
					
				Constants.URL_FRIENDS, 
				new PostData[] { 
						
					new PostData(
							
						Constants.FIELD_NAMES_CHECKSUM[0], 
						checksum
						
					) 
					
				}, 
				0
				
			);

			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Generate an object
				JSONObject comData = new JSONObject(httpContent).getJSONObject( "data" );
				JSONArray friendsObject = comData.getJSONArray( "friendscomcenter" );
				JSONArray requestsObject = comData.getJSONArray( "friendrequests" );
				JSONObject tempObj, presenceObj;

				//Arraylists!
				ArrayList<ProfileData> profileRowRequests = new ArrayList<ProfileData>();
				ArrayList<ProfileData> profileRowPlaying = new ArrayList<ProfileData>();
				ArrayList<ProfileData> profileRowOnline = new ArrayList<ProfileData>();
				ArrayList<ProfileData> profileRowOffline = new ArrayList<ProfileData>();
				
				//Grab the lengths
				int numRequests = requestsObject.length(), numFriends = friendsObject.length();
				
				//Got requests?
				if( numRequests  > 0 ) {
					
					//Iterate baby!
					for( int i = 0, max = requestsObject.length(); i < max; i++ ) {
						
						//Grab the object
						tempObj = requestsObject.optJSONObject( i );
						
						//Save it
						profileRowRequests.add(
								
							new ProfileData(

								tempObj.getString( "username" ),
								tempObj.getString( "username" ),
								0,
								Long.parseLong( tempObj.getString( "userId" ) ),
								0,
								tempObj.optString( "gravatarMd5", "" )
							
							) 
							
						);
				
					}

					//Sort it out
					Collections.sort( profileRowRequests, new ProfileComparator() );
			 	
				}
				
				if( numFriends > 0 ) {
					
					//Iterate baby!
					for( int i = 0, max = friendsObject.length(); i < max; i++ ) {
						
						//Grab the object
						tempObj = friendsObject.optJSONObject( i );
						presenceObj = tempObj.getJSONObject( "presence" );
						
						//Save it
						if( presenceObj.getBoolean( "isOnline" ) ) {
							
							
							if( presenceObj.getBoolean( "isPlaying" ) ) {
								
								profileRowPlaying.add(								
							
									new ProfileData(

										tempObj.getString( "username" ),
										tempObj.getString( "username" ),
										0,
										Long.parseLong( tempObj.getString( "userId" ) ),
										0,
										tempObj.optString( "gravatarMd5", "" ),
										true,
										true
									
									) 
									
								);
				
							} else {
							
								profileRowOnline.add(								
										
									new ProfileData(
											
										tempObj.getString( "username" ),
										tempObj.getString( "username" ),
										0,
										Long.parseLong( tempObj.getString( "userId" ) ),
										0,
										tempObj.optString( "gravatarMd5", "" ),
										true,
										false
									
									) 
									
								);
								
							}
							
						} else {
						
							profileRowOffline.add(								
									
								new ProfileData(
											
									tempObj.getString( "username" ),
									tempObj.getString( "username" ),
									0,
									Long.parseLong( tempObj.getString( "userId" ) ),
									0,
									tempObj.optString( "gravatarMd5", "" )
									
								) 
									
							);
							
						}
					
					}
					
					//How many "online" friends do we have? Playing + idle
					numFriends = profileRowPlaying.size() + profileRowOnline.size();
					
					//First add the separators)...
					if( numFriends > 0 ){
						
						profileRowPlaying.add(  new ProfileData( "00000000", "Online friends", 0, 0, 0, null ) );
					
					}
					
					
					if( profileRowOffline.size() > 0 ) {
						
						profileRowOffline.add(  new ProfileData( "00000001", "Offline friends", 0, 0, 0, null ) );
					
					}
					
					//...then we sort it out...
					Collections.sort( profileRowPlaying, new ProfileComparator() );
					Collections.sort( profileRowOnline, new ProfileComparator() );
					Collections.sort( profileRowOffline, new ProfileComparator() );
					
					//...sprinkle a little merging here and there...
					profileRowPlaying.addAll( profileRowOnline );
					
				}
				
				return new FriendListDataWrapper(profileRowRequests, profileRowPlaying, profileRowOffline );
			
			} else {
			
				throw new WebsiteHandlerException("Could not retrieve the ProfileIDs.");
			
			}
		
		} catch ( JSONException e ) {
		
			throw new WebsiteHandlerException(e.getMessage());
		
		} catch ( RequestHandlerException ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}

	public static final ArrayList<ProfileData> getFriends(String checksum, boolean noOffline) throws WebsiteHandlerException {
		
		try {
				
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			ArrayList<ProfileData> profileArray = new ArrayList<ProfileData>();
			
			//Get the content
			httpContent = wh.post( Constants.URL_FRIENDS, new PostData[] { new PostData(Constants.FIELD_NAMES_CHECKSUM[0], checksum) }, 0);
	
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Generate an object
				JSONArray profileObject = new JSONObject(httpContent).getJSONObject( "data" ).getJSONArray( "friendscomcenter" ); 
				JSONObject tempObj;
				
				//Iterate baby!
				for( int i = 0, max = profileObject.length(); i < max; i++ ) {
					
					//Grab the object
					tempObj = profileObject.optJSONObject( i );
					
					//Only online friends?
					if( noOffline && !tempObj.getJSONObject( "presence").getBoolean( "isOnline") ) { continue; }
					
					//Save it
					profileArray.add( 
							
						new ProfileData(
								
							tempObj.getString( "username" ),
							tempObj.getString( "username" ),
							0,
							Long.parseLong( tempObj.getString( "userId" ) ),
							0,
							tempObj.optString( "gravatarMd5", "" )
						
						) 
						
					);
				}
				
				return profileArray;
			
			} else {
			
				throw new WebsiteHandlerException("Could not retrieve the ProfileIDs.");
			
		}
		
		} catch ( JSONException e ) {
		
			throw new WebsiteHandlerException(e.getMessage());
		
		} catch ( RequestHandlerException ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}

	public static boolean answerFriendRequest( long pId, Boolean accepting, String checksum) throws WebsiteHandlerException {

		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			
			//Get the content
			if( accepting ) {

				httpContent = wh.post( 
						
					Constants.URL_FRIEND_ACCEPT.replace( 
						
						"{UID}", 
						pId + ""
					), 
					new PostData[] { 
					
						new PostData(
							
							Constants.FIELD_NAMES_CHECKSUM[0], 
							checksum
						
						) 
						
					}, 
					0

				);
			
			} else {

				httpContent = wh.post( 
					
					Constants.URL_FRIEND_DECLINE.replace( 
						
						"{UID}", 
						pId + ""
					), 
					new PostData[] { 
					
						new PostData(
							
							Constants.FIELD_NAMES_CHECKSUM[0], 
							checksum
						
						) 
						
					}, 
					0
			
				);
				
			}
			
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				return true;
				
			} else {
			
				throw new WebsiteHandlerException("Could not respond to the friend request.");
			
			}	
		
		} catch ( RequestHandlerException ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
	public static Long getChatId(long profileId, String checksum) throws WebsiteHandlerException {
		
		try {
			
			//Let's do this!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			
			//Get the content
			httpContent = wh.post( 
					
				Constants.URL_CHAT_CONTENTS.replace( 
					
					"{UID}", 
					profileId + ""
				), 
				new PostData[] { 
				
					new PostData(
						
						Constants.FIELD_NAMES_CHECKSUM[0], 
						checksum
					
					) 
					
				},
				0
		
			);
						
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Get the messages
				return new JSONObject(httpContent).getJSONObject( "data" ).getLong("chatId");
				
			} else {
			
				throw new WebsiteHandlerException("Could not get the chatId");
			
			}	
		
		} catch ( Exception ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}

	}
	
	public static ArrayList<ChatMessage> getChatMessages(long profileId, String checksum) throws WebsiteHandlerException {
		
		try {
			
			//Let's do this!
			RequestHandler wh = new RequestHandler();
			ArrayList<ChatMessage> messageArray = new ArrayList<ChatMessage>();
			String httpContent;
			
			//Get the content
			httpContent = wh.post( 
					
				Constants.URL_CHAT_CONTENTS.replace( 
					
					"{UID}", 
					profileId + ""
				), 
				new PostData[] { 
				
					new PostData(
						
						Constants.FIELD_NAMES_CHECKSUM[0], 
						checksum
					
					) 
					
				},
				0
		
			);
						
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Get the messages
				JSONArray messages = new JSONObject(httpContent).getJSONObject("data").getJSONObject( "chat" ).getJSONArray( "messages" );
				JSONObject tempObject;
				
				//Iterate
				for( int i = 0, max = messages.length(); i < max; i++ ) {
					
					tempObject = messages.optJSONObject( i );
					messageArray.add( 
					
						new ChatMessage(
						
							profileId,
							tempObject.getLong( "timestamp" ),
							tempObject.getString( "fromUsername" ),
							tempObject.getString( "message" )
								
						)
							
					);

				}
				
				return messageArray;
				
			} else {
			
				throw new WebsiteHandlerException("Could not get the chat messages.");
			
			}	
		
		} catch ( Exception ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}

	}
	
	public static boolean sendChatMessages(long chatId, String checksum, String message) throws WebsiteHandlerException {
		
		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			
			//Get the content
			httpContent = wh.post( 
					
				Constants.URL_CHAT_SEND, 
				new PostData[] { 
					 new PostData(
								
						Constants.FIELD_NAMES_CHAT[2], 
						checksum
					
					),
					new PostData(
							
						Constants.FIELD_NAMES_CHAT[1], 
						chatId
						
					),
					new PostData(
							
						Constants.FIELD_NAMES_CHAT[0], 
						message
					
					)
					 
				},
				1
		
			);
						
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				return true;
				
			} else {
			
				throw new WebsiteHandlerException("Could not send the chat message.");
				
			}	
		
		} catch ( RequestHandlerException ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}

	}
	
	public static boolean closeChatWindow(long chatId) throws WebsiteHandlerException {
		
		try {
			
			//Let's login everybody!
			RequestHandler rh = new RequestHandler();
			
			//Get the content
			rh.get(
					
				Constants.URL_CHAT_CLOSE.replace(
						
					"{CID}", 
					chatId + ""
					
				),
				1
					
			);
						
			//Did we manage?
			return true;	
		
		} catch ( RequestHandlerException ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}

	}
	
	public static ProfileInformation getProfileInformationForUser(Context context, ProfileData profileData, int num, long activeProfileId ) throws WebsiteHandlerException {
		
		try {
			
			//Let's go!
			RequestHandler rh = new RequestHandler();
			ArrayList<FeedItem> feedItemArray = new ArrayList<FeedItem>();
			ArrayList<PlatoonData> platoonDataArray = new ArrayList<PlatoonData>();
			String httpContent;
			
			//Get the content
			httpContent = rh.get( Constants.URL_PROFILE_INFO.replace( "{UNAME}", Uri.encode( profileData.getAccountName() ) ), 1 );

			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//JSON Objects
				JSONObject contextObject = new JSONObject(httpContent).optJSONObject( "context" );
				JSONObject profileCommonObject = contextObject.optJSONObject( "profileCommon" );
				JSONObject userInfo = profileCommonObject.optJSONObject( "userinfo" );
				JSONObject presenceObject = profileCommonObject.getJSONObject( "user" ).getJSONObject("presence");
				//JSONArray gameReports = contextObject.getJSONArray( "gameReportPreviewGroups" );
				JSONArray feedItems = contextObject.getJSONArray( "feed" );
				JSONArray platoonArray = profileCommonObject.getJSONArray( "platoons" );
				JSONObject statusMessage = profileCommonObject.optJSONObject( "userStatusMessage" );
				JSONObject currItem;
				String playingOn;
				
				//Is status messages null?
				if( statusMessage == null ) { statusMessage = new JSONObject("{'statusMessage':'', 'statusMessageChanged':0}"); }
				
				//What's up with the user?
				if( presenceObject.isNull( "serverName") && presenceObject.getBoolean( "isPlaying" ) ) {
					
					playingOn = ( presenceObject.getInt("platform") == 2 ) ? "Xbox Live": "Playstation Network";
					
				} else {
					
					playingOn = presenceObject.optString("serverName", null);
					
				}
				
				//Iterate over the platoons
				for( int i = 0, max = platoonArray.length(); i < max; i++ ) {
					
					//Each loop is an object
					currItem = platoonArray.getJSONObject( i );
					
					//Store the data
					platoonDataArray.add(

						new PlatoonData(
						
							Long.parseLong( currItem.getString( "id" ) ),
							currItem.getInt( "fanCounter" ),
							currItem.getInt( "memberCounter" ),
							currItem.getInt( "platform" ),
							currItem.getString( "name" ),
							currItem.getString( "tag" ),
							rh.getImageFromStream( Constants.URL_PLATOON_IMAGE_THUMBS + currItem.getString( "badgePath" ), false),
							!currItem.getBoolean("hidden")
								
						)
					
					);
					
				}
				
				//Parse the feed
				feedItemArray = getFeedItemsFromJSON(context, feedItems, activeProfileId);
				
				//Let's see
				for(int i = 1, max = Math.round( num / 10 ); i < max; i++ ) {

					//Get the content, and create a JSONArray
					String tempContent = rh.get( 
							
						Constants.URL_PROFILE_FEED.replace( 
								
							"{PID}",
							profileData.getProfileId() + ""
							
						).replace( 
								
							"{NUMSTART}", 
							String.valueOf( i*10 ) 
							
						), 
						1 
					
					);
					JSONArray jsonArray = new JSONObject(tempContent).getJSONObject("data").getJSONArray( "feedEvents" );
					
					//Gather them
					feedItemArray.addAll( WebsiteHandler.getFeedItemsFromJSON(context, jsonArray, activeProfileId ) );
					
				}
				
				return new ProfileInformation(
					
					userInfo.optInt( "age", 0 ), 
					profileData.getProfileId(), 
					userInfo.optLong("birthdate", 0), 
					userInfo.optLong("lastLogin", 0),
					statusMessage.optLong("statusMessageChanged", 0), 
					userInfo.optString( "name", "N/A" ), 
					profileCommonObject.getJSONObject( "user" ).getString( "username" ),
					userInfo.isNull( "presentation" ) ? null : userInfo.getString( "presentation" ),  
					userInfo.optString( "location", "us" ),  
					statusMessage.optString( "statusMessage", "" ), 
					playingOn,
					userInfo.optBoolean( "allowFriendRequests", true ), 
					presenceObject.getBoolean("isOnline"),
					presenceObject.getBoolean("isPlaying"),
					profileCommonObject.getString( "friendStatus" ).equals("ACCEPTED"),
					feedItemArray,
					platoonDataArray
				);
				
			} else {
			
				throw new WebsiteHandlerException("Could not get the profile.");
				
			}	
		
		} catch ( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}

	public static ArrayList<ProfileData> getFansForPlatoon(long platoonId) throws WebsiteHandlerException {
		
		try {
			
			//Let's go!
			RequestHandler rh = new RequestHandler();
			ArrayList<ProfileData> fans = new ArrayList<ProfileData>();
			String httpContent;

			//Do the request
			httpContent = rh.get( 
					
				Constants.URL_PLATOON_FANS.replace( "{PLATOON_ID}",  platoonId + ""), 
				1
				
			);
			
			//Let's start with the JSON shall we?
			JSONObject fanArray = new JSONObject(httpContent).getJSONObject("context").getJSONObject("fans");
			JSONArray fanIdArray = fanArray.names();
			JSONObject tempObject = null;
			
			//Iterate over the fans
			if( fanIdArray != null ) {
				
				for( int i = 0, max = fanIdArray.length(); i < max; i++ ) {
				
					//Grab the fan
					tempObject = fanArray.getJSONObject( fanIdArray.getString( i ) );
					
					//Store him in the ArrayList
					fans.add( 
						
						new ProfileData(
	
							tempObject.getString( "username" ),
							null,
							Long.parseLong(tempObject.getString( "userId" ) ),
							0,
							0,
							tempObject.optString( "gravatarMd5", "" )
						)				
							
					);
				
				}
				
			}
			
			//Did we get more than 0?
			if( fans.size() > 0 ) {
			
				//Add a header just 'cause we can
				fans.add( new ProfileData("00000000", "Loyal fans", 0, 0, 0, null) );
				
				//a-z please!
				Collections.sort( fans, new ProfileComparator() );
			
			}
			//Return
			return fans;
			
		} catch( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
	}
	
	public static PlatoonInformation getProfileInformationForPlatoon(Context context, PlatoonData platoonData, int num, long activeProfileId, boolean loadImage ) throws WebsiteHandlerException {
		
		try {
			
			//Let's go!
			RequestHandler rh = new RequestHandler();
			ArrayList<FeedItem> feedItemArray = new ArrayList<FeedItem>();
			ArrayList<ProfileData> fans = new ArrayList<ProfileData>(); 
			ArrayList<ProfileData> members = new ArrayList<ProfileData>();
			PlatoonStats stats = null;
			
			//Arrays to divide the users in
			ArrayList<ProfileData> founderMembers = new ArrayList<ProfileData>();
			ArrayList<ProfileData> adminMembers = new ArrayList<ProfileData>();
			ArrayList<ProfileData> regularMembers = new ArrayList<ProfileData>();
			ArrayList<ProfileData> invitedMembers = new ArrayList<ProfileData>();
			ArrayList<ProfileData> requestMembers = new ArrayList<ProfileData>();
			
			//Get the content
			String httpContent = rh.get( Constants.URL_PLATOON.replace( "{PLATOON_ID}", platoonData.getId() + "" ), 1 );
			boolean isAdmin = false;
			boolean isMember = false;
			
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
				
				//JSON Objects
				JSONObject contextObject = new JSONObject(httpContent).optJSONObject( "context" );
				JSONObject profileCommonObject = contextObject.optJSONObject( "platoon" );
				JSONObject memberArray = profileCommonObject.optJSONObject( "members" );
				JSONArray feedItems = contextObject.getJSONArray( "feed" );
				JSONObject currItem;
				
				//Temporary var
				ProfileData tempProfile;
				
				//Let's iterate over the members
				JSONArray idArray = memberArray.names();
				for( int counter = 0; counter < idArray.length(); counter++ ) {
					
					//Get the current item
					currItem = memberArray.optJSONObject( idArray.getString( counter ) );
					
					//Check the *rights* of the user
					if( idArray.getString( counter ).equals("" + activeProfileId)  ) {
						
						isMember = true;
						if( currItem.getInt("membershipLevel") >= 128 ) { isAdmin = true; }
					}
					
					
					//If we have a persona >> add it to the members
					if( !currItem.isNull( "persona" ) ) {

						tempProfile = new ProfileData(

							currItem.getJSONObject( "user" ).getString( "username" ),
							currItem.getJSONObject( "persona" ).getString( "personaName" ),
							Long.parseLong( currItem.getString( "personaId" ) ),
							Long.parseLong( currItem.getString( "userId" ) ),
							profileCommonObject.getLong( "platform" ),
							currItem.optString( "gravatarMd5", "" ),
							currItem.getJSONObject( "user" ).getJSONObject("presence").getBoolean( "isOnline" ),
							currItem.getJSONObject( "user" ).getJSONObject("presence").getBoolean( "isPlaying" )
								
						);
						
					} else {
						
						continue;
						
					}
					
					//Add the user to the correct *part* of the (future) merged list
					switch( currItem.getInt( "membershipLevel" ) ) { 

						case 1:
							requestMembers.add( tempProfile );
							break;

						case 2:
							invitedMembers.add( tempProfile );
							break;
						
						case 4:
							regularMembers.add( tempProfile );
							break;

						case 128:
							adminMembers.add( tempProfile );
							break;

						case 256:
							founderMembers.add( tempProfile );
							break;
							
						default:
							regularMembers.add( tempProfile );
							break;
					}
					
				}
				
				//Let's sort the members...
				Collections.sort( requestMembers, new ProfileComparator() );
				Collections.sort( invitedMembers, new ProfileComparator() );
				Collections.sort( regularMembers, new ProfileComparator() );
				Collections.sort( adminMembers, new ProfileComparator() );
				Collections.sort( founderMembers, new ProfileComparator() );

				//...and then merge them with their *labels*
				if( founderMembers.size() > 0 ) { 
					
					//Plural?
					if( founderMembers.size() > 1 ) {
						
						members.add( new ProfileData("00000000", "Founders", 0, 0, 0, null) );
					
					} else { 
						
						members.add( new ProfileData("00000001", "Founder", 0, 0, 0, null) );
						
					}
			
					//Add them to the members
					members.addAll( founderMembers );
					
				}
		
				if( adminMembers.size() > 0 ) { 
					
					//Plural?
					if( adminMembers.size() > 1 ) {
						
						members.add( new ProfileData("00000002", "Admins", 0, 0, 0, null) );
					
					} else { 
						
						members.add( new ProfileData("00000003", "Admin", 0, 0, 0, null) );
						
					}
			
					//Add them to the members
					members.addAll( adminMembers );
					
				}
				
				if( regularMembers.size() > 0 ) { 
					
					//Plural?
					if( regularMembers.size() > 1 ) {
						
						members.add( new ProfileData("00000004", "Regular members", 0, 0, 0, null) );
					
					} else { 
						
						members.add( new ProfileData("00000005", "Regular member", 0, 0, 0, null) );
						
					}
			
					//Add them to the members
					members.addAll( regularMembers );
					

				}

				//Is the user *admin* or higher?
				if( isAdmin ) {
					
					if( invitedMembers.size() > 0 ) {
						
						//Just add them
						members.add( new ProfileData("00000006", "Invited to join", 0, 0, 0, null) );
						members.addAll( invitedMembers );
						
					}
					
					if( requestMembers.size() > 0 ) {
	
						//Just add them
						members.add( new ProfileData("00000007", "Requested to join", 0, 0, 0, null) );
						members.addAll( requestMembers);
	
					}

				}
					
				//Let's get 'em fans too
				fans = WebsiteHandler.getFansForPlatoon(platoonData.getId());
				
				//Oh man, don't forget the stats!!!
				stats = WebsiteHandler.getStatsForPlatoon( platoonData );
				
				//Parse the feed
				feedItemArray = getFeedItemsFromJSON(context, feedItems, activeProfileId);
				
				//Let's see
				for(int i = 1, max = Math.round( num / 10 ); i < max; i++ ) {

					//Get the content, and create a JSONArray
					String tempContent = rh.get( 
							
						Constants.URL_PLATOON_FEED.replace( 
								
							"{PLATOON_ID}",
							platoonData.getId() + ""
							
						).replace( 
								
							"{NUMSTART}", 
							String.valueOf( i*10 ) 
							
						), 
						1 
					
					);
					JSONArray jsonArray = new JSONObject(tempContent).getJSONObject("data").getJSONArray( "feedEvents" );
					
					//Gather them
					feedItemArray.addAll( WebsiteHandler.getFeedItemsFromJSON(context, jsonArray, activeProfileId ) );
					
				}
				
				//Do we need the image?
				Bitmap image;
				if( loadImage ) { 
					
					image = rh.getImageFromStream( 
							
						Constants.URL_PLATOON_IMAGE + profileCommonObject.getString( "badgePath" ), 
						true
						
					);
				
				} else {
					
					image = null;
					
				}
				
				//Return it!
				return new PlatoonInformation(
				
					profileCommonObject.getInt( "platform" ),
					profileCommonObject.getInt( "game" ),
					profileCommonObject.getInt( "fanCounter" ),
					profileCommonObject.getInt( "memberCounter" ),
					profileCommonObject.getInt( "blazeClubId" ),
					profileCommonObject.getLong( "id" ),
					profileCommonObject.getLong( "creationDate" ),
					profileCommonObject.getString( "name" ),
					profileCommonObject.getString( "tag" ),
					profileCommonObject.getString( "presentation" ),
					PublicUtils.normalizeUrl( profileCommonObject.optString( "website", "" ) ),
					image,
					!profileCommonObject.getBoolean( "hidden" ),
					isMember,
					isAdmin,
					profileCommonObject.getBoolean( "allowNewMembers" ),
					feedItemArray,
					members,
					fans,
					stats
					
				);
				
			} else {
			
				throw new WebsiteHandlerException("Could not get the profile.");
				
			}	
		
		} catch ( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
	public static ArrayList<FeedItem> getPublicFeed(Context context, int num, long profileId) throws WebsiteHandlerException {
		
		try {
			
			//Attributes
			RequestHandler rh = new RequestHandler();
			ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>();
			JSONArray jsonArray;
			String httpContent = null;
			
			//Let's see
			for(int i = 0, max = Math.round( num / 10 ); i < max; i++ ) {

				//Get the content, and create a JSONArray
				httpContent = rh.get( Constants.URL_FRIEND_FEED.replace( "{NUMSTART}", String.valueOf( i*10 ) ), 1 );
				jsonArray = new JSONObject(httpContent).getJSONObject("data").getJSONArray( "feedEvents" );
				
				//Gather them
				feedItems.addAll( WebsiteHandler.getFeedItemsFromJSON(context, jsonArray, profileId ) );
				
			}
			
			//Return it
			return feedItems;
			
		} catch(Exception ex) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
		
		}
		
	}
	
	public static boolean setActive() {
		
		try {
			
			//Let's see
			String httpContent = new RequestHandler().get( Constants.URL_CHAT_SETACTIVE, 1 );
			JSONObject httpResponse = new JSONObject(httpContent);
			
			//Is it ok?
			if( httpResponse.optString( "message", "FAIL" ).equals( "OK" ) ) {
				
				return true;
				
			} else {
				
				return false;
				
			}
			
			
		} catch( Exception ex ) {
			
			ex.printStackTrace();
			return false;
			
		}
		
	}
	
	public static int getNewNotificationsCount(String checksum) throws WebsiteHandlerException {
		
		try {
			
			//Init
			RequestHandler rh = new RequestHandler();
			String httpContent;
			
			//Get the content
			httpContent = rh.post( 
					
				Constants.URL_NOTIFICATIONS_TOP5,
				new PostData[] {
						
					new PostData(Constants.FIELD_NAMES_CHECKSUM[0], checksum)	
						
				},
				3
		
			);
			
			//Got httpContent
			if( httpContent != null && !httpContent.equals( "" ) ) {
				
				//Grab the notifications
				return new JSONObject(httpContent).getJSONObject("data").getInt( "numUnread" );
				
			} else {
				
				throw new WebsiteHandlerException("No notifications found.");
				
			}

		} catch( Exception ex ) {
		
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
	public static ArrayList<NotificationData> getNotifications(String checksum) throws WebsiteHandlerException {
		
		try {
			
			//Init
			RequestHandler rh = new RequestHandler();
			ArrayList<NotificationData> notifications = new ArrayList<NotificationData>();
			String httpContent;
			
			//Get the content
			httpContent = rh.get( 
					
				Constants.URL_NOTIFICATIONS_ALL,
				1
		
			);
			
			//Got httpContent
			if( httpContent != null && !httpContent.equals( "" ) ) {
				
				//Grab the notifications
				JSONObject contextObject = new JSONObject(httpContent).getJSONObject("context");
				JSONObject platoonObject = contextObject.getJSONObject( "platoons" );
				JSONArray notificationArray = contextObject.getJSONArray( "notifications" );
				
				//Now we store the information - easy peasy
				for( int i = 0, max = notificationArray.length(); i < max; i++ ) {
					
					//Grab the current item
					JSONObject currItem = notificationArray.getJSONObject( i );
					
					//Let's see
					String extra = null;
					if( currItem.has( "platoonId" ) ) {
						
						extra = (
								
							"[" + platoonObject.getJSONObject( currItem.getString( "platoonId" ) ).getString( "tag" ) + "] " +
							platoonObject.getJSONObject( currItem.getString( "platoonId" ) ).getString( "name" )
							
						);
						
					}
					
					//Add!!
					notifications.add(
					
						new NotificationData(

							Long.parseLong(currItem.optString( "feedItemId", "0" )),
							Long.parseLong(currItem.optString( "itemOwnerId", "0" )),
							Long.parseLong(currItem.getString( "userId" )),
							Long.parseLong(currItem.getString( "timestamp" )),
							currItem.optInt( "feedItemType", 0 ),	
							currItem.optString( "itemOwnerUsername", "" ),
							currItem.getString( "username" ),
							currItem.getString( "type" ),
							extra 
								
						)	
							
					);
					
				}
				
			} else {
				
				throw new WebsiteHandlerException("No notifications found.");
				
			}
			
			//Return!!
			return notifications;

		} catch( Exception ex ) {
		
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
	public static PlatoonStats getStatsForPlatoon( PlatoonData platoonData ) throws WebsiteHandlerException {

		try {
			
			//Let's go!
			RequestHandler rh = new RequestHandler();
			
			//Get the content
			String httpContent = rh.get( 
					
				Constants.URL_PLATOON_STATS.replace(
						
					"{PLATOON_ID}", 
					platoonData.getId() + "" 
					
				).replace( 
						
					"{PLATFORM_ID}", 
					platoonData.getPlatformId() + ""
					
				), 
				1
				
			);
			
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
	
				//JSON-base!!
				JSONObject personaList = new JSONObject(httpContent).getJSONObject( "data" ).getJSONObject( "platoonPersonas" );
				JSONObject memberStats = new JSONObject(httpContent).getJSONObject( "data" ).getJSONObject( "memberStats" );
				
				//JSON data-branches
				JSONObject objectGeneral = memberStats.getJSONObject( "general" );
				JSONObject objectKit = memberStats.getJSONObject( "kitsVehicles" );
				JSONObject objectTop = memberStats.getJSONObject( "topPlayers" );
				JSONObject objectScore = objectKit.getJSONObject( "score" );
				JSONObject objectSPM = objectKit.getJSONObject( "scorePerMinute" );
				JSONObject objectTime = objectKit.getJSONObject( "time" );
				JSONObject currObj = null;
				JSONObject currUser = null;
				JSONArray currObjNames = null;
				
				//Let's iterate and create the containers
				ArrayList<PlatoonStatsItem> arrayGeneral = new ArrayList<PlatoonStatsItem>();
				ArrayList<PlatoonStatsItem> arrayScore = new ArrayList<PlatoonStatsItem>();
				ArrayList<PlatoonStatsItem> arraySPM = new ArrayList<PlatoonStatsItem>();
				ArrayList<PlatoonStatsItem> arrayTime = new ArrayList<PlatoonStatsItem>();
				ArrayList<PlatoonTopStatsItem> arrayTop = new ArrayList<PlatoonTopStatsItem>();
				
				//More temp
				ArrayList<Integer> tempMid = new ArrayList<Integer>();
				String tempGravatarHash = null;
				
				//Get the *general* stats
				currObjNames = objectGeneral.names();
				for( int i = 0, max = currObjNames.length(); i < max; i++ ) {

					//Grab the current object
					currObj = objectGeneral.getJSONObject( currObjNames.getString( i ) );
					
					//Is it the KD? The KD == DOUBLE
					if( currObjNames.getString( i ).equals( "kd" ) ) {
						
						//Create a new "stats item"
						arrayGeneral.add(
								
							new PlatoonStatsItem(
						
								currObjNames.getString( i ),
								currObj.getDouble( "min" ),
								currObj.getDouble( "median" ),
								currObj.getDouble( "best" ),
								currObj.getDouble( "average" ),
								null
								
							)
						
						);
					
					} else {
						
						//Create a new "stats item"
						arrayGeneral.add( 
								
							new PlatoonStatsItem(
						
								currObjNames.getString( i ),
								currObj.getInt( "min" ),
								currObj.getInt( "median" ),
								currObj.getInt( "best" ),
								currObj.getInt( "average" ),
								null
								
							)
							
						);
						
					}
					
				}
				
				//Create a new "overall item"
				arrayScore.add( new PlatoonStatsItem( "Overall", 0, 0, 0, 0, null ) );
				
				//Get the *kit* scores
				currObjNames = objectScore.names();
				for( int i = 0, max = currObjNames.length(); i < max; i++ ) {

					//Grab the current object
					currObj = objectScore.getJSONObject( currObjNames.getString( i ) );

					//Hmm, where is the userInfo?
					if( currObj.has( "bestPersonaId" ) ) { currUser = personaList.getJSONObject( currObj.getString( "bestPersonaId" ) ); }
					else { currUser = personaList.getJSONObject( currObj.getString( "personaId" ) ); }

					//Store the gravatar
					tempGravatarHash = currUser.optString( "gravatarMd5", "" );
					
					//Create a new "stats item"
					arrayScore.add(
							
							
						new PlatoonStatsItem(
							
							currObjNames.getString( i ),
							currObj.getInt( "min" ),
							currObj.getInt( "median" ),
							currObj.getInt( "best" ),
							currObj.getInt( "average" ),
							new ProfileData(
								
								currUser.optString("username", ""),
								currUser.optString("username", ""),
								Long.parseLong(currObj.optString( "bestPersonaId", "" )),
								Long.parseLong(currUser.optString( "userId", "0" )),
								platoonData.getPlatformId(),
								tempGravatarHash
											
							)
							
						)
						
					);

					//Is it ! the first?
					if( i > 0 ) { tempMid.add(arrayScore.get(i+1).getMid()); }
					
					//Update the "overall"
					arrayScore.get(0).add(arrayScore.get(i+1));
					
				}
				Collections.sort( tempMid );
				arrayScore.get( 0 ).setMid( tempMid.get( (int) Math.ceil(tempMid.size()/2) ) );
				tempMid.clear();
				
				//Create a new "overall item"
				arraySPM.add( new PlatoonStatsItem( "Overall", 0, 0, 0, 0, null ) );
				
				//Get the *kit* score/min
				currObjNames = objectSPM.names();
				for( int i = 0, max = currObjNames.length(); i < max; i++ ) {

					//Grab the current object
					currObj = objectSPM.getJSONObject( currObjNames.getString( i ) );

					//Hmm, where is the userInfo?
					if( currObj.has( "bestPersonaId" ) ) { currUser = personaList.getJSONObject( currObj.getString( "bestPersonaId" ) ); }
					else { currUser = personaList.getJSONObject( currObj.getString( "personaId" ) ); }

					//Store the gravatar
					tempGravatarHash = currUser.optString( "gravatarMd5", "" );
					
					//Create a new "stats item"
					arraySPM.add(
							
						new PlatoonStatsItem(
							
							currObjNames.getString( i ),
							currObj.getInt( "min" ),
							currObj.getInt( "median" ),
							currObj.getInt( "best" ),
							currObj.getInt( "average" ),
							new ProfileData(
								
								currUser.optString("username", ""),
								currUser.optString("username", ""),
								Long.parseLong(currObj.optString( "bestPersonaId", "" )),
								Long.parseLong(currUser.optString( "userId", "0" )),
								platoonData.getPlatformId(),
								tempGravatarHash
											
							)
							
						)
						
					);

						//Is it ! the first?
						if( i > 0 ) { tempMid.add(arraySPM.get( 0 ).getMid()); }
						
						//Update the "overall"
						arraySPM.get(0).add(arraySPM.get(i+1));
			
				}
				Collections.sort( tempMid );
				arraySPM.get(0).setMid( tempMid.get( (int) Math.ceil(tempMid.size()/2) ) );
				tempMid.clear();
				
				//Create a new "overall item"
				arrayTime.add( new PlatoonStatsItem( "Overall", 0, 0, 0, 0, null ) );
				
				//Get the *kit* times
				currObjNames = objectTime.names();
				for( int i = 0, max = currObjNames.length(); i < max; i++ ) {

					//Grab the current object
					currObj = objectTime.getJSONObject( currObjNames.getString( i ) );

					//Hmm, where is the userInfo?					
					if( currObj.has( "bestPersonaId" ) ) { currUser = personaList.getJSONObject( currObj.getString( "bestPersonaId" ) ); }
					else { currUser = personaList.getJSONObject( currObj.getString( "personaId" ) ); }

					//Store the gravatar
					tempGravatarHash = currUser.optString( "gravatarMd5", "" );
					
					//Create a new "stats item"
					arrayTime.add(
							
						new PlatoonStatsItem(
							
							currObjNames.getString( i ),
							currObj.getInt( "min" ),
							currObj.getInt( "median" ),
							currObj.getInt( "best" ),
							currObj.getInt( "average" ),
							new ProfileData(
								
								currUser.optString("username", ""),
								currUser.optString("username", ""),
								Long.parseLong(currObj.optString( (currObj.has( "bestPersonaId")? "bestPersonaId" : "personaId" ), "" )),
								Long.parseLong(currUser.optString( "userId", "0" )),
								platoonData.getPlatformId(),
								tempGravatarHash
											
							)
						
						)
							
					);

					//Is it ! the first?
					if( i > 0 ) { tempMid.add(arrayTime.get( 0 ).getMid()); }
					
					//Update the "overall"
					arrayTime.get(0).add(arrayTime.get( i+1 ));
					
				}
				Collections.sort( tempMid );
				arrayTime.get(0).setMid( tempMid.get( (int) Math.ceil(tempMid.size()/2) ) );
				tempMid.clear();
				
				//Get the *top player* Tops
				PlatoonTopStatsItem highestSPM = null;
				currObjNames = objectTop.names();
				
				Log.d(Constants.DEBUG_TAG, "Before for-loop");
				for( int i = 0, max = currObjNames.length(); i < max; i++ ) {

					Log.d(Constants.DEBUG_TAG, "In for-loop (loop #" + i + " )");
					//Grab the current object
					currObj = objectTop.getJSONObject( currObjNames.getString( i ) );
					
					//Hmm, where is the userInfo?					
					if( currObj.has( "bestPersonaId" ) ) { currUser = personaList.getJSONObject( currObj.getString( "bestPersonaId" ) ); }
					else if( !currObj.getString( "personaId" ).equals( "0" ) ) {

						currUser = personaList.getJSONObject( currObj.getString( "personaId" ) ); 
						
					} else {

						Log.d(Constants.DEBUG_TAG, "No personaId - oh noes!");
						//Create a new "stats item"
						arrayTop.add(
								
							new PlatoonTopStatsItem(
						
								"N/A",
								0,
								null
							
							)
							
						);
						
						//Continue
						continue;
						
					}
					
					//Store the gravatar
					tempGravatarHash = currUser.optString( "gravatarMd5", "" );
					
					//Do we need to download a new image?
					if( !WebsiteHandler.bitmapCache.containsKey( tempGravatarHash ) ) {
						
						WebsiteHandler.cacheGravatar( tempGravatarHash, 40 );
						
					}
					
					//Create a new "stats item"
					arrayTop.add(
							
						new PlatoonTopStatsItem(
					
							currObjNames.getString( i ),
							currObj.getInt( "spm" ),
							new ProfileData(
								
								currUser.optString("username", ""),
								currUser.optString("username", ""),
								Long.parseLong(currObj.optString( "personaId", "" )),
								Long.parseLong(currUser.optString( "userId", "0" )),
								platoonData.getPlatformId(),
								tempGravatarHash
											
							)
						
						)
						
					);
					
					//Store it if it's the highest
					if( highestSPM == null || highestSPM.getSPM() < arrayTop.get( i ).getSPM() ) {
						
						highestSPM = arrayTop.get( i );
						
					}
					
					
				}

				Log.d(Constants.DEBUG_TAG, "After for-loop");
				
				//Set the best & sort
				arrayTop.add( 
				
					new PlatoonTopStatsItem(
					
						"TOP",
						highestSPM.getSPM(),
						highestSPM.getProfile()
							
					)	
						
				);
				Collections.sort( arrayTop, new TopStatsComparator() );
				Log.d(Constants.DEBUG_TAG, "Returning!");
				//Return it now!!
				return new PlatoonStats(
				
					platoonData.getName(),
					platoonData.getId(),
					arrayGeneral,
					arrayTop,
					arrayScore,
					arraySPM,
					arrayTime
					
				);

			} else {
				
				return null;
			
			}
			
		} catch( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}

	private static ArrayList<FeedItem> getFeedItemsFromJSON( Context c, JSONArray jsonArray, long activeProfileId ) throws WebsiteHandlerException {

		try {
			
			//Variables that we need
			JSONObject currItem = null;
			JSONObject tempSubItem = null;
			JSONObject tempCommentItem = null;
			JSONObject ownerObject = null;
			JSONObject otherUserObject = null;
			FeedItem tempFeedItem = null;
			ArrayList<CommentData> comments = null;
			ArrayList<FeedItem> feedItemArray = new ArrayList<FeedItem>();
			
			//Iterate over the feed
			for( int i = 0, max = jsonArray.length(); i < max; i++ ) {
				
				//Once per loop
				comments = new ArrayList<CommentData>();
				
				//Each loop is an object
				currItem = jsonArray.getJSONObject( i );
				ownerObject = currItem.getJSONObject( "owner" );

				//Let's get the comments
				if( currItem.getInt("numComments") > 2 ) {
					
					comments = WebsiteHandler.getCommentsForPost( Long.parseLong(currItem.getString("id")) );
					
					
				} else if( currItem.getInt( "numComments" ) == 0 ) {
					
					//For now, we do nothing here
					
				} else {
					
					//Iterate, as there's only comment1 & comment2
					for( int cCounter = 1; cCounter < 3; cCounter++ ) {
						
						//No comment?
						if( currItem.isNull( "comment"+cCounter  ) ) break;
						
						//Grab a temporary comment and add it to our ArrayList
						tempCommentItem = currItem.getJSONObject( "comment"+cCounter );
						comments.add(
	
							new CommentData(
							
								Long.parseLong( tempCommentItem.getString("id")),
								0,
								tempCommentItem.getLong( "creationDate" ),
								tempCommentItem.getLong( "ownerId" ),
								currItem.getJSONObject("owner").getString("username"),
								tempCommentItem.getString( "body" )
							
							)
								
						);
					
					}
					
				}
				//Variables if *modification* is needed
				String itemTitle = null;
				String itemContent = null;
				String tempGravatarHash = ownerObject.getString("gravatarMd5");
				
				//Process the likes
				JSONArray likeUsers = currItem.getJSONArray( "likeUserIds" );
				int numLikes = likeUsers.length();
				boolean liked = false;
				
				//Iterate and see if the user has *liked* it already
				for( int likeCount = 0; likeCount < numLikes; likeCount++ ) {
				
					if( Long.parseLong(likeUsers.getString( likeCount )) == activeProfileId ) {
						
						liked = true;
						break;
						
					}
					
				}
				
				//What do we have *here*?
				if( !currItem.isNull( "BECAMEFRIENDS" )) {
				
					//Grab the specific object
					tempSubItem = currItem.optJSONObject( "BECAMEFRIENDS" );
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						
						numLikes,
						c.getResources().getString( R.string.info_p_friendship ),
						"",
						currItem.getString("event"),
						new String[] { 
							
							ownerObject.getString("username"), 
							tempSubItem.getJSONObject( "friendUser" ).getString( "username" )
							
						},
						liked,
						comments,
						tempGravatarHash
							
					);
					
				} else if( !currItem.isNull( "CREATEDFORUMTHREAD" )) {
				
					//Grab the specific object
					tempSubItem = currItem.optJSONObject( "CREATEDFORUMTHREAD" );
					itemTitle = c.getString( R.string.info_p_forumthread ).replace( 
						
						"{thread}", 
						tempSubItem.getString( "threadTitle" )
						
					);
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						tempSubItem.getString( "threadBody" ),
						currItem.getString("event"),
						new String[] {
							
							ownerObject.getString("username"), 
							null 
						
						},
						liked,
						comments,
						tempGravatarHash
							
					);
					
				} else if( !currItem.isNull( "WROTEFORUMPOST" )) {
				
					//Grab the specific object
					tempSubItem = currItem.optJSONObject( "WROTEFORUMPOST" );
					itemTitle = c.getString( R.string.info_p_forumpost ).replace( 
						
						"{thread}", 
						tempSubItem.getString( "threadTitle" )
						
					);
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						tempSubItem.getString( "postBody" ),
						currItem.getString("event"),
						new String[] {
							
							ownerObject.getString("username"), 
							null 
						
						},
						liked,
						comments,
						tempGravatarHash
							
					);
					
				} else if( !currItem.isNull( "GAMEREPORT" )) {
				
					//Grab the specific object
					JSONArray tempStatsArray = currItem.optJSONObject( "GAMEREPORT" ).optJSONArray( "statItems" );
					tempSubItem = tempStatsArray.optJSONObject( 0 );
					itemTitle = c.getString( R.string.info_p_newunlock );
					
					//Weapon? Attachment?
					if( !tempSubItem.isNull( "parentLangKeyTitle" ) ) {
						
						//Let's see
						itemContent = DataBank.getWeaponTitleShort( tempSubItem.getString( "parentLangKeyTitle") );
						
						//Is it empty?
						if( !itemContent.equals( "" ) ) {
							
							itemContent +=  " " + DataBank.getAttachmentTitle( tempSubItem.getString( "langKeyTitle" ) );
							
						} else {
							
							//Grab a vehicle title then
							itemContent = DataBank.getVehicleTitle( tempSubItem.getString("parentLangKeyTitle") );
							
							//Validate
							if( !itemContent.equals( "" ) ) {
								
								itemContent += " " + DataBank.getVehicleAddon( tempSubItem.getString( "langKeyTitle" ) );
								
							} else {
								
								itemContent = tempSubItem.getString("parentLangKeyTitle");
								
							} 
							
						}
						
					} else {
						
						itemContent = DataBank.getWeaponTitleShort( tempSubItem.getString("langKeyTitle") );
						
					}
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle.replace( "{item}", itemContent),
						"",
						currItem.getString("event"),
						new String[] { 
							
							ownerObject.getString("username"), 
							null 
						
						},
						liked,
						comments,
						tempGravatarHash
							
					);
					
				} else if( !currItem.isNull( "STATUSMESSAGE" )) {
					
					//Get the JSON-Object
					tempSubItem = currItem.optJSONObject( "STATUSMESSAGE" );
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						"<b>{username}</b> " + tempSubItem.getString( "statusMessage" ),
						"",
						currItem.getString("event"),
						new String[] {
							
							ownerObject.getString("username"),
							null 
							
						},
						liked,
						comments,
						tempGravatarHash
							
					);
				
				} else if( !currItem.isNull( "ADDEDFAVSERVER" )) {
					
					//Get it!
					tempSubItem = currItem.getJSONObject( "ADDEDFAVSERVER" );
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						c.getResources().getString( R.string.info_p_favserver ).replace( 
								
							"{server}", 
							tempSubItem.getString( "serverName" ) 
							
						),
						"",
						currItem.getString("event"),
						new String[] { 
							
							ownerObject.getString("username"), 
							null 
						
						},
						liked,
						comments,
						tempGravatarHash
							
					);
					
				} else if( !currItem.isNull( "RANKEDUP" )) {
					
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "RANKEDUP" );
					
					//Set it!
					itemTitle = c.getString( R.string.info_p_promotion ).replace( 
						
						"{rank title}", 
						DataBank.getRankTitle( tempSubItem.getString( "langKeyTitle" ) )
					
					).replace( 
	
						"{rank}", 
						tempSubItem.getString("rank")
					
					);	
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						"",
						currItem.getString("event"),
						new String[] { 
							
							ownerObject.getString("username"), 
							null 
							
						},
						liked,
						comments,
						tempGravatarHash
							
					);
	
				} else if( !currItem.isNull( "COMMENTEDGAMEREPORT" )) {
				
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "COMMENTEDGAMEREPORT" );
					
					//Set it!
					itemTitle = c.getString( R.string.info_p_greport_comment ).replace(
							
						"{server name}",
						tempSubItem.getString( "serverName" )
						
					).replace(
							
						"{map}",
						DataBank.getMapTitle( tempSubItem.getString( "map" ) )
						
					).replace(
							
						"{game mode}",
						DataBank.getGameModeFromId( tempSubItem.getInt("gameMode") )
						
					);
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						tempSubItem.getString( "gameReportComment" ),
						currItem.getString("event"),
						new String[] {
							
							ownerObject.getString("username"), 
							null 
							
						},
						liked,
						comments,
						tempGravatarHash
							
					);
				
				} else if( !currItem.isNull( "COMMENTEDBLOG" )) {
				
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "COMMENTEDBLOG" );
					
					//Set it!
					itemTitle = c.getString( R.string.info_p_blog_comment ).replace(
							
						"{post name}",
						tempSubItem.getString( "blogTitle" )
						
					);
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						tempSubItem.getString( "blogCommentBody" ),
						currItem.getString("event"),
						new String[] {
							
							ownerObject.getString("username"), 
							null 
							
						},
						liked,
						comments,
						tempGravatarHash
							
					);
				
				} else if( !currItem.isNull( "JOINEDPLATOON" )) {
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "JOINEDPLATOON" ).getJSONObject( "platoon" );
					
					//Set it!
					itemTitle = c.getString( R.string.info_p_platoon_join ).replace( 
				
						"{platoon}",
						tempSubItem.getString("name")
				
					);
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						"",
						currItem.getString("event"),
						new String[] { 
								
								ownerObject.getString("username"),
								null 
						},								
						liked,
						comments,
						tempGravatarHash
							
					);
				
				}  else if( !currItem.isNull( "KICKEDPLATOON" )) {
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "KICKEDPLATOON" ).getJSONObject( "platoon" );
					
					//Set it!
					itemTitle = c.getString( R.string.info_p_platoon_kick ).replace( 
				
						"{platoon}",
						tempSubItem.getString("name")
				
					);
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						"",
						currItem.getString("event"),
						new String[] { 
								
								ownerObject.getString("username"),
								null 
						},								
						liked,
						comments,
						tempGravatarHash
							
					);
				
				} else if( !currItem.isNull( "CREATEDPLATOON" )) {
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "CREATEDPLATOON" ).getJSONObject( "platoon" );
					
					//Set it!
					itemTitle = c.getString( R.string.info_p_platoon_create ).replace( 
				
						"{platoon}",
						tempSubItem.getString("name")
				
					);
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						"",
						currItem.getString("event"),
						new String[] { 
								
								ownerObject.getString("username"),
								null 
						},								
						liked,
						comments,
						tempGravatarHash
							
					);
				
				} else if( !currItem.isNull( "PLATOONBADGESAVED" )) {
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "PLATOONBADGESAVED" ).getJSONObject( "platoon" );
					
					//Set it!
					itemTitle = c.getString( R.string.info_p_platoon_badge ).replace( 
				
						"{platoon}",
						tempSubItem.getString("name")
				
					);
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						"",
						currItem.getString("event"),
						new String[] { 
								
								ownerObject.getString("username"),
								null 
						},								
						liked,
						comments,
						tempGravatarHash
							
					);
				
				} else if( !currItem.isNull( "LEFTPLATOON" )) {
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "LEFTPLATOON" ).getJSONObject( "platoon" );
					
					//Set it!
					itemTitle = c.getString( R.string.info_p_platoon_left ).replace( 
				
						"{platoon}",
						tempSubItem.getString("name")
				
					);
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						"",
						currItem.getString("event"),
						new String[] { 
							
							ownerObject.getString("username"),
							null 
						},
						liked,
						comments,
						tempGravatarHash
							
					);
					
				} else if( !currItem.isNull( "RECEIVEDPLATOONWALLPOST" )) {
					
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "RECEIVEDPLATOONWALLPOST" );
					
					//Set it!
					itemTitle = c.getString( R.string.info_p_platoon_feed ).replace(
							
						"{platoon}", 
						tempSubItem.getJSONObject("platoon").getString( "name" )
						
					);
							
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						tempSubItem.getString( "wallBody" ),
						currItem.getString("event"),
						new String[] { 
						
							ownerObject.getString("username"),
							null 
							
						},
						liked,
						comments,
						tempGravatarHash
							
					);
					
				} else if( !currItem.isNull( "LEVELCOMPLETE" )) {
						
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "LEVELCOMPLETE" );
					
					//Set it!
					itemTitle = c.getString( R.string.info_p_coop_level_comp ).replace(
							
						"{level}", 
						DataBank.getCoopLevelTitle( tempSubItem.getString( "level" ) )
						
					).replace(
						
						"{difficulty}",	
						tempSubItem.getString( "difficulty" )
	
					);
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						"",
						currItem.getString("event"),
						new String[] { 
							
							ownerObject.getString("username"), 
							tempSubItem.getJSONObject( "friend" ).getString( "username" ) 
						
						},
						liked,
						comments,
						tempGravatarHash
							
					);
					
				} else if( !currItem.isNull( "RECEIVEDAWARD" )) {
					
	
					//Get it!
					tempSubItem = currItem.optJSONObject( "RECEIVEDAWARD" ).optJSONArray( "statItems" ).getJSONObject( 0 );
					
					//Set it!
					itemTitle = c.getString( R.string.info_p_award ).replace( 
							
						"{award}", 
						DataBank.getAwardTitle( tempSubItem.getString( "langKeyTitle" ) )
	
					);
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						"",
						currItem.getString("event"),
						new String[] { 
							
							ownerObject.getString("username"),
							null 
							
						},
						liked,
						comments,
						tempGravatarHash
							
					);
					
				} else if( !currItem.isNull( "RECEIVEDWALLPOST" )) {
					
	
					//Get it!
					tempSubItem = currItem.optJSONObject( "RECEIVEDWALLPOST" );
					
					//Set it!
					itemTitle = "<b>{username1} » {username2}:</b> {message}".replace( 
							
						"{message}", 
						tempSubItem.getString( "wallBody" )
	
					);
					
					//Let's get it!
					otherUserObject = tempSubItem.getJSONObject( "writerUser" );
					tempGravatarHash = otherUserObject.getString( "gravatarMd5" );
					
					//Temporary storage						
					tempFeedItem = new FeedItem(
	
						Long.parseLong( currItem.getString("id") ),
						Long.parseLong( currItem.getString("ownerId") ),
						Long.parseLong( currItem.getString("itemId") ),
						currItem.getLong( "creationDate" ),
						numLikes,
						itemTitle,
						"",
						currItem.getString("event"),
						new String[] { 
							
							otherUserObject.getString( "username" ), 
							ownerObject.getString("username") 
							
						},
						liked,
						comments,
						tempGravatarHash
							
					);
					
				} else {
					
					Log.d(Constants.DEBUG_TAG, "event => " + currItem.getString( "event" ) );
					tempFeedItem = null;
				
				}
				
				//Append it to the array
				if( tempFeedItem != null ) { feedItemArray.add( tempFeedItem ); }
				
				//Before I forget - let's download the gravatar too!
				if( !WebsiteHandler.bitmapCache.containsKey( tempGravatarHash ) ) {

					WebsiteHandler.cacheGravatar( tempGravatarHash, 40 );
					
				}
				
			}
			
			return feedItemArray;
		
		} catch( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
	}

	public static ArrayList<CommentData> getCommentsForPost( long postId ) throws WebsiteHandlerException {
			
		try {
			
			//Let's do this!
			RequestHandler wh = new RequestHandler();
			ArrayList<CommentData> comments = new ArrayList<CommentData>();
			String httpContent;
			
			//Get the content
			httpContent = wh.get( 
					
				Constants.URL_FEED_COMMENTS.replace( 
					
					"{POST_ID}", 
					postId + ""
				
				),
				0
		
			);
						
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Get the messages
				JSONArray commentArray = new JSONObject(httpContent).getJSONObject("data").getJSONArray( "comments" );
				JSONObject tempObject;
				
				//Iterate
				for( int i = 0, max = commentArray.length(); i < max; i++ ) {
					
					tempObject = commentArray.optJSONObject( i );
					comments.add( 

						new CommentData(
						
							postId,
							Long.parseLong(tempObject.getString( "itemId" )),
							Long.parseLong(tempObject.getString("creationDate")),
							Long.parseLong(tempObject.getString( "ownerId" )),
							tempObject.getJSONObject( "owner" ).getString( "username" ),
							tempObject.getString( "body" )
								
						)
							
					);

				}
				
				return comments;
				
			} else {
			
				throw new WebsiteHandlerException("Could not get the comments.");
			
			}	
		
		} catch ( Exception ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
	}

	public static boolean doHooahInFeed(long postId, String checksum ) throws WebsiteHandlerException {
		
		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			
			//Get the content
			httpContent = wh.post( 
					
				Constants.URL_HOOAH.replace( "{POST_ID}", postId + "" ), 
				new PostData[] { 
					 
					new PostData(
								
						Constants.FIELD_NAMES_CHECKSUM[0], 
						checksum
					
					)	 
				},
				1
		
			);
						
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				return true;
				
			} else {
			
				throw new WebsiteHandlerException("Could not hooah the message.");
				
			}	
		
		} catch ( RequestHandlerException ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}

	}

	public static boolean unHooahInFeed(long postId, String checksum ) throws WebsiteHandlerException {
		
		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			
			//Get the content
			httpContent = wh.post( 
					
				Constants.URL_UNHOOAH.replace( "{POST_ID}", postId + "" ), 
				new PostData[] { 
					 
					new PostData(
								
						Constants.FIELD_NAMES_CHECKSUM[0], 
						checksum
					
					)	 
				},
				1
				
			);
						
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				return true;
				
			} else {
			
				throw new WebsiteHandlerException("Could not un-hooah the message.");
				
			}	
		
		} catch ( RequestHandlerException ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}

	}
	
	public static boolean commentOnFeedPost(long postId, String checksum, String comment ) throws WebsiteHandlerException {
		
		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;

			//Get the content
			httpContent = wh.post( 
					
				Constants.URL_COMMENT.replace( "{POST_ID}", postId + "" ), 
				new PostData[] { 
					 
					new PostData(
								
						Constants.FIELD_NAMES_FEED_COMMENT[0], 
						comment
					
					),
					new PostData(
							
						Constants.FIELD_NAMES_FEED_COMMENT[1], 
						checksum
					
					)	
				},
				2 //Noticed the 2?
		
			);
						
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
				
				//Hopefully this goes as planned
				if( !httpContent.equals( "Internal server error" )) {
					
					return true;
				
				} else {
					
					return false;
					
				}
				
			} else {
			
				throw new WebsiteHandlerException("Could not post the comment.");
				
			}	
		
		} catch ( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}

	}
	
	public static boolean sendFriendRequest(long profileId, String checksum) throws WebsiteHandlerException {
	
		try {

			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent = wh.post( 
					
				Constants.URL_FRIEND_REQUESTS.replace( 
						
					"{UID}", 
					profileId + ""
					
				), 
				new PostData[] {
					
					new PostData(
					
						Constants.FIELD_NAMES_CHECKSUM[0],
						checksum
							
					)
				},
				1
				
			);
			
			//Did we manage?
			if( httpContent != null ) {
	
				return true;
				
			} else {
				
				return false;
				
			}
		
		} catch(Exception ex) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}

	}

	public static boolean postToWall(long profileId, String checksum, String content, boolean isPlatoon) throws WebsiteHandlerException {
		
		try {

			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent = wh.post( 
					
				Constants.URL_FEED_POST,
				new PostData[] {
					
					new PostData(
							
						Constants.FIELD_NAMES_FEED_POST[0],
						content
							
					),new PostData(
							
						Constants.FIELD_NAMES_FEED_POST[1],
						checksum
							
					),new PostData(
							
						Constants.FIELD_NAMES_FEED_POST[(!isPlatoon?2:3)],
						profileId + ""
								
					)
				},
				2
				
			);
			
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
	
				//Check the JSON
				String status = new JSONObject(httpContent).optString( "message", "" );
				if( status.equals( "WALL_POST_CREATED" ) || status.equals( "PLATOONWALL_POST_CREATED" ) ) {
					
					return true;
				
				} else {
				
					return false;
					
				}

			} else {
				
				return false;
				
			}
		
		} catch(Exception ex) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
 
	}
	
	public static String downloadZip(Context context, String url, String title) throws WebsiteHandlerException {
		
		try {
			
			//Attributes
			RequestHandler rh = new RequestHandler();
			
			//Get the *content*
			if( !rh.saveFileFromURI( Constants.URL_IMAGE_PACK, "", "imagepack-001.zip" ) ) {
			
				return "<this is supposed to be the path to the zip>";
				
			} else {
				
				throw new WebsiteHandlerException("No zip found.");
				
			}
			
		} catch( Exception ex ) {
		
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
	public static Bitmap downloadGravatarToCache(String hash, int size) throws WebsiteHandlerException {
		
		try {
			
			//Any size requirements? Otherwise we just pick the standard number
			return new RequestHandler().getImageFromStream( 
					
				Constants.URL_GRAVATAR.replace(
				
					"{hash}",
					hash
					
				).replace(
						
					"{size}", 
					(
						(size > 0)? size : Constants.DEFAULT_AVATAR_SIZE
								
					) + ""
					
				).replace( 
				
					"{default}",
					Constants.DEFAULT_AVATAR_SIZE + ""
						
				),
				true 
				
			);

		} catch( Exception ex ) {
		
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
	public static Bitmap getGravatarFromCache(String identifier, Resources r) {
		
		if( WebsiteHandler.bitmapCache.containsKey( identifier ) ) {
					
			return WebsiteHandler.bitmapCache.get( identifier );
				
		} else {
			
			return BitmapFactory.decodeResource( r, R.drawable.test_avatar_48);
			
		}
		
	}
	
	
	public static Bitmap getImage(String url) throws WebsiteHandlerException {
	
		try {
			
			return new RequestHandler().getImageFromStream( url, true );

		} catch( Exception ex ) {
		
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
	public static void cacheGravatar(String h, int s) {
	
		try { 
			
			
			if( WebsiteHandler.bitmapCache.size() > Constants.DEFAULT_CACHE_LIMIT ) {
				
				//Loops & removes five bitmaps from the cache
				int counter = 0;
				Object[] identifierArray = new HashSet(WebsiteHandler.bitmapCache.keySet()).toArray();
				for( Object identifier : identifierArray ) {
	
					//> 4 = stop
					if( counter > 4 ) { break; }
					
					//Remove and increment
					WebsiteHandler.bitmapCache.remove(String.valueOf( identifier ) );
					counter++;			
				
				}
			
			}
		
			//Add it!
			WebsiteHandler.bitmapCache.put( h, null );
			WebsiteHandler.bitmapCache.put( h, WebsiteHandler.downloadGravatarToCache(h, s) );
		
		} catch(Exception ex) {
			
			ex.printStackTrace();
			
		}
		
	}
	
	public static void cacheObject(String h, Object o) {
		
		try { 
			
			
			if( WebsiteHandler.feedCache.size() > Constants.DEFAULT_CACHE_LIMIT ) {
				
				//Loops & removes five bitmaps from the cache
				int counter = 0;
				for( String identifier : WebsiteHandler.feedCache.keySet() ) {
	
					//> 4 = stop
					if( counter > 4 ) { break; }
					
					//Remove and increment
					WebsiteHandler.feedCache.remove(identifier);
					counter++;			
				
				}
			
			}
		
			//Add it!
			WebsiteHandler.feedCache.put( h, o );
		
		} catch(Exception ex) {
			
			ex.printStackTrace();

		}
		
	}
	
}