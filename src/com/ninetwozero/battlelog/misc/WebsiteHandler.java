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

import net.sf.andhsli.hotspotlogin.SimpleCrypto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.ninetwozero.battlelog.datatypes.Config;
import com.ninetwozero.battlelog.datatypes.PlayerData;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileComparator;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.UnlockComparator;
import com.ninetwozero.battlelog.datatypes.UnlockData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;

/* 
 * Methods of this class should be loaded in AsyncTasks, as they would probably lock up the GUI
 */

public class WebsiteHandler {
	
	public static boolean doLogin(Context context, PostData[] postDataArray, boolean savePassword) throws WebsiteHandlerException {
	
		//Init
		SharedPreferences sharedPreferences = context.getSharedPreferences( Config.fileSharedPrefs, 0);
		SharedPreferences.Editor spEdit = sharedPreferences.edit();
		String[] tempString = new String[10];
		String httpContent;
	
		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
    		httpContent = wh.post( Config.urlLogin, postDataArray );

    		//Did we manage?
    		if( httpContent != null && !httpContent.equals( "" ) ) {
    			
    			//Set the int
    			int startPosition = httpContent.indexOf( Config.elementUIDLink );
    			String[] bits;
    			
    			//Did we find it?
    			if( startPosition == -1 ) {
    				
    				return false;

    			}
    			
    			//Cut out the appropriate bits (<a class="SOME CLASS HERE" href="A LONG LINK HERE">NINETWOZERO
	    		httpContent = httpContent.substring( startPosition );
				tempString[0] = httpContent.substring( 0, httpContent.indexOf("\">") ); 
				bits = TextUtils.split( tempString[0].replace( Config.elementUIDLink, ""), "/");
				
				//Get the checksum
				tempString[1] = httpContent.substring( httpContent.indexOf( Config.elementStatusChecksumLink ) );
				tempString[1] = tempString[1].substring( 0, tempString[1].indexOf( "\" />") ).replace( Config.elementStatusChecksumLink, "" );
				
				//Further more, we would actually like to store the userid and name
				spEdit.putString( "origin_email", postDataArray[0].getValue() );
				
				//Should we remember the password?
				if( savePassword ) {

					spEdit.putString( "origin_password", SimpleCrypto.encrypt( postDataArray[0].getValue(), postDataArray[1].getValue() ) );
					spEdit.putBoolean( "remember_password", true );
					
				} else {
					
					spEdit.putString( "origin_password", "" );
					spEdit.putBoolean( "remember_password", false);
				}
				
				spEdit.putString( "battlelog_persona",  bits[0]);
				spEdit.putLong( "battlelog_persona_id",  Long.parseLong( bits[2] ));				
				spEdit.putLong( "battlelog_platform_id",  Config.getPlatformId(bits[3]) );
				spEdit.putString( "battlelog_post_checksum", tempString[1]);
				
				//Co-co-co-commit
				spEdit.commit();
				
    		}
    		
		} catch( Exception ex ) {
			
			throw new WebsiteHandlerException("Failed to log-in.");
			
		}

		return true;
		
	}
	
	public static ProfileData getIDFromSearch(final String keyword, final String checksum) throws WebsiteHandlerException {
		
		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			ProfileData profile = null;
			
			//Get the content
			httpContent = wh.post( 
				
				Config.urlSearch, 
				new PostData[] {
						 
					new PostData(Config.fieldNamesSearch[0], keyword),
					new PostData(Config.fieldNamesSearch[1], checksum)
					
				}
				
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
					for( int i = 0; i < searchResults.length(); i++ ) {
						
						//Get the JSONObject
						JSONObject tempObj = searchResults.optJSONObject( i );
						
						//A perfect match?
						if( tempObj.getString( "username" ).equals( keyword ) ) {
							
							profile = new ProfileData(
								tempObj.getString( "username" ),
								0,
								Long.parseLong( tempObj.getString( "userId" ) ),
								0
							);
							
							break;
						
						}
						
						//Grab the "cost"
						costCurrent = PublicUtils.getLevenshteinDistance( keyword, tempObj.getString( "username" ) );
						
						//Somewhat of a match? Get the "best" one!
						if( costOld > costCurrent ) {
	
							profile = new ProfileData(
								
								tempObj.getString( "username" ),
								0,
								Long.parseLong( tempObj.getString( "userId" ) ),
								0
							
							);
							
						}
						
						//Shuffle!
						costOld = costCurrent;
						
					}
					
					return WebsiteHandler.getIDFromProfile( profile.getProfileId() );

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
	
	public static ProfileData getIDFromProfile(long profileId) throws WebsiteHandlerException {

		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			
			//Get the content
			httpContent = wh.get( Config.urlIDGrabber.replace( "{PID}", profileId + "") );

			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Generate an object
				JSONObject personaObject = new JSONObject(httpContent).getJSONObject( "data" ).getJSONArray( "soldiersBox" ).optJSONObject( 0 ).getJSONObject( "persona" );
				
				return new ProfileData( 
					personaObject.getString("personaName"),
					personaObject.getLong( "personaId" ), 
					profileId,
					Config.getPlatformId( personaObject.getString( "namespace" ) ) 
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
	
public static PlayerData getStatsForUser(ProfileData pd) throws WebsiteHandlerException {
		
		try {
			
	    	//Get the data
	    	RequestHandler wh = new RequestHandler();
	    	String content = wh.get( 
    	
    			Config.urlStatsOverview.replace(
    					
					"{UID}", pd.getPersonaId() + ""
				
				).replace( 
				
					"{PLATFORM_ID}", pd.getPlatformId() + ""
				)
				
			);
	    	JSONObject dataObject = new JSONObject(content).getJSONObject( "data" );
	    	JSONObject statsOverview = dataObject.getJSONObject( "overviewStats" );
	    	JSONObject kitScores = statsOverview.getJSONObject( "kitScores" );
	    	JSONObject nextRankInfo = dataObject.getJSONObject( "rankNeeded" );
	    	JSONObject currRankInfo = dataObject.getJSONObject( "currentRankNeeded" );
	   
	        //Yay
	        return new PlayerData(
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
	        	statsOverview.getInt( "heals" ),
	        	statsOverview.getInt( "revives" ),
	        	statsOverview.getInt( "deaths" ),
	        	statsOverview.getInt( "numWins" ),
	        	statsOverview.getInt( "numLosses" ),
	        	statsOverview.getDouble( "kdRatio" ),
	        	statsOverview.getDouble( "accuracy" ),
	        	statsOverview.getDouble( "longestHeadshot" ),
	        	statsOverview.getDouble( "killStreakBonus" ),
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
	
			Config.urlStatsUpcoming.replace(
					
				"{UID}", pd.getPersonaId() + ""
			
			).replace( 
			
				"{PLATFORM_ID}", pd.getPlatformId() + ""
			)
			
		);
    	JSONObject dataObject = new JSONObject(content).getJSONObject( "data" );
    	JSONArray unlockResults = dataObject.getJSONArray( "unlocks" );
    	JSONObject unlockRow, detailObject;
    	int unlockKit;
    	HashMap<String, String> foo = new HashMap<String, String>();

    	//Iterate over the unlocksArray
    	for( int i = 0; i < unlockResults.length(); i++ ) {
    	
    		//Get the temporary object
    		unlockRow = unlockResults.optJSONObject( i );

    		//Empty?
    		//if( unlockRow.getDouble( "unlockPercentage" ) < 1.0 ) { continue; }
    		
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
    	    	if( !foo.containsKey( detailObject.getString( "codeNeeded" ) ) ) { foo.put( detailObject.getString( "codeNeeded" ), "" ); }
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
    	    	if( !foo.containsKey( detailObject.getString( "codeNeeded" ) ) ) { foo.put( detailObject.getString( "codeNeeded" ), "" ); }
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
    	    	if( !foo.containsKey( detailObject.getString( "codeNeeded" ) ) ) { foo.put( detailObject.getString( "codeNeeded" ), "" ); }
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
    	    	if( !foo.containsKey( detailObject.getString( "codeNeeded" ) ) ) { foo.put( detailObject.getString( "codeNeeded" ), "" ); }
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
	
	public static final ArrayList<ArrayList<ProfileData>> getFriendsCOM(String checksum) throws WebsiteHandlerException {
		
		try {
				
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			ArrayList<ArrayList<ProfileData>> profileArray = new ArrayList<ArrayList<ProfileData>>();
			
			//Get the content
			httpContent = wh.post( Config.urlFriends, new PostData[] { new PostData(Config.fieldNamesCHSUM[0], checksum) });

			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Generate an object
				JSONObject comData = new JSONObject(httpContent).getJSONObject( "data" );
				JSONArray friendsObject = comData.getJSONArray( "friendscomcenter" );
				JSONArray requestsObject = comData.getJSONArray( "friendrequests" );
				JSONObject tempObj, presenceObj;

				//Arraylists!
				ArrayList<ProfileData> profileRowRequests = new ArrayList<ProfileData>();
				ArrayList<ProfileData> profileRowOnline = new ArrayList<ProfileData>();
				ArrayList<ProfileData> profileRowOffline = new ArrayList<ProfileData>();
				//TreeMap<String, ProfileData> tempRow = new TreeMap<String, ProfileData>();
				
				//Grab the lengths
				int numRequests = requestsObject.length(), numFriends = friendsObject.length();
				
				//Got requests?
				if( numRequests  > 0 ) {
					
					//Iterate baby!
					for( int i = 0; i < requestsObject.length(); i++ ) {
						
						//Grab the object
						tempObj = requestsObject.optJSONObject( i );
						
						//Save it
						profileRowRequests.add(
								
							new ProfileData(
									
								tempObj.getString( "username" ),
								0,
								Long.parseLong( tempObj.getString( "userId" ) ),
								0
							
							) 
							
						);
				
					}

					//Sort it out
					Collections.sort( profileRowOnline, new ProfileComparator() );
			 
					profileArray.add( profileRowRequests );
					
				} else {
					
					profileArray.add( null );
					
				}
				
				if( numFriends > 0 ) {
					
					//Iterate baby!
					for( int i = 0; i < friendsObject.length(); i++ ) {
						
						//Grab the object
						tempObj = friendsObject.optJSONObject( i );
						presenceObj = tempObj.getJSONObject( "presence" );
						
						//Save it
						if( presenceObj.getBoolean( "isOnline" ) ) {
							
							
							profileRowOnline.add(								
							
								new ProfileData(
										
									tempObj.getString( "username" ),
									0,
									Long.parseLong( tempObj.getString( "userId" ) ),
									0,
									true,
									tempObj.getJSONObject( "presence" ).getBoolean( "isPlaying" )
								
								) 
								
							);
				
						} else {
						
							profileRowOffline.add(								
									
								new ProfileData(
											
									tempObj.getString( "username" ),
									0,
									Long.parseLong( tempObj.getString( "userId" ) ),
									0
									
								) 
									
							);
							
						}
					
					}
					
					//Sort it out
					Collections.sort( profileRowOnline, new ProfileComparator() );
					Collections.sort( profileRowOffline, new ProfileComparator() );
					
					//Add it man!
					profileArray.add( profileRowOnline );
					profileArray.add( profileRowOffline );
				
				} else {
					
					profileArray.add( null );
					profileArray.add( null );
					
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

	public static final ArrayList<ProfileData> getFriends(String checksum, boolean noOffline) throws WebsiteHandlerException {
		
		try {
				
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			ArrayList<ProfileData> profileArray = new ArrayList<ProfileData>();
			
			//Get the content
			httpContent = wh.post( Config.urlFriends, new PostData[] { new PostData(Config.fieldNamesCHSUM[0], checksum) });
	
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Generate an object
				JSONArray profileObject = new JSONObject(httpContent).getJSONObject( "data" ).getJSONArray( "friendscomcenter" ); 
				JSONObject tempObj;
				
				//Iterate baby!
				for( int i = 0; i < profileObject.length(); i++ ) {
					
					//Grab the object
					tempObj = profileObject.optJSONObject( i );
					
					//Only online friends?
					if( noOffline && !tempObj.getJSONObject( "presence").getBoolean( "isOnline") ) { continue; }
					
					//Save it
					profileArray.add( 
							
						new ProfileData(
								
							tempObj.getString( "username" ),
							0,
							Long.parseLong( tempObj.getString( "userId" ) ),
							0
						
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
						
					Config.urlFriendAccept.replace( 
						
						"{PID}", 
						pId + ""
					), 
					new PostData[] { 
					
						new PostData(
							
							Config.fieldNamesCHSUM[0], 
							checksum
						
						) 
						
					}
			
				);
			
			} else {

				httpContent = wh.post( 
					
					Config.urlFriendDecline.replace( 
						
						"{PID}", 
						pId + ""
					), 
					new PostData[] { 
					
						new PostData(
							
							Config.fieldNamesCHSUM[0], 
							checksum
						
						) 
						
					}
			
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
	
}
