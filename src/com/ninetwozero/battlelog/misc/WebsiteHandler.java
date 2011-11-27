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

import net.sf.andhsli.hotspotlogin.SimpleCrypto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.ninetwozero.battlelog.datatypes.ChatMessage;
import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.PlatoonInformation;
import com.ninetwozero.battlelog.datatypes.PlayerData;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileComparator;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ProfileInformation;
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
		SharedPreferences sharedPreferences = context.getSharedPreferences( Constants.fileSharedPrefs, 0);
		SharedPreferences.Editor spEdit = sharedPreferences.edit();
		String[] tempString = new String[10];
		String httpContent = "";
		long profileId = 0;
	
		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
    		httpContent = wh.post( Constants.urlLogin, postDataArray, 0);

    		//Did we manage?
    		if( httpContent != null && !httpContent.equals( "" ) ) {
    			
    			//Set the int
    			int startPosition = httpContent.indexOf( Constants.elementUIDLink );
    			String[] bits;
    			
    			//Did we find it?
    			if( startPosition == -1 ) {
    				
    				return false;

    			}
    			
    			//Cut out the appropriate bits (<a class="SOME CLASS HERE" href="A LONG LINK HERE">NINETWOZERO
	    		tempString[0] = httpContent.substring( startPosition );
				tempString[0] = tempString[0].substring( 0, tempString[0].indexOf("\">") ); 
				bits = TextUtils.split( tempString[0].replace( Constants.elementUIDLink, ""), "/");
				
				//Get the checksum
				tempString[1] = httpContent.substring( httpContent.indexOf( Constants.elementStatusChecksumLink ) );
				tempString[1] = tempString[1].substring( 0, tempString[1].indexOf( "\" />") ).replace( Constants.elementStatusChecksumLink, "" );
				
				//Let's work on getting the "username", not persona name --> profileId
				tempString[2] = httpContent.substring( httpContent.indexOf( Constants.elementUsernameLink ) );
				tempString[2] = tempString[2].substring( 0, tempString[2].indexOf( "/\">") ).replace( Constants.elementUsernameLink, "" );
				profileId = WebsiteHandler.getIDFromSearch( tempString[2], tempString[1]).getProfileId();

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
				
				//This we keep!!!
				spEdit.putString( "battlelog_username", tempString[2] );
				spEdit.putString( "battlelog_persona",  bits[0]);
				spEdit.putLong( "battlelog_profile_id", profileId);
				spEdit.putLong( "battlelog_persona_id",  Long.parseLong( bits[2] ));				
				spEdit.putLong( "battlelog_platform_id",  DataBank.getPlatformIdFromName(bits[3]) );
				spEdit.putString( "battlelog_post_checksum", tempString[1]);
				
				//Co-co-co-commit
				spEdit.commit();
				
    		} else {
    			
    			return false;
    			
    		}
    		
		} catch( Exception ex ) {
			
			ex.printStackTrace();
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
				
				Constants.urlSearch, 
				new PostData[] {
						 
					new PostData(Constants.fieldNamesSearch[0], keyword),
					new PostData(Constants.fieldNamesSearch[1], checksum)
					
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
					for( int i = 0; i < searchResults.length(); i++ ) {
						
						//Get the JSONObject
						JSONObject tempObj = searchResults.optJSONObject( i );
						
						//A perfect match?
						if( tempObj.getString( "username" ).equals( keyword ) ) {
							
							profile = new ProfileData(
								tempObj.getString( "username" ),
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
								tempObj.getString( "username" ),
								0,
								Long.parseLong( tempObj.getString( "userId" ) ),
								0
							
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
	
	public static ProfileData getPersonaIdFromProfile(long profileId) throws WebsiteHandlerException {

		try {
			
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			
			//Get the content
			httpContent = wh.get( Constants.urlProfile.replace( "{PID}", profileId + ""), false);

			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Generate an object
				JSONObject personaObject = new JSONObject(httpContent).getJSONObject( "data" ).getJSONArray( "soldiersBox" ).optJSONObject( 0 ).getJSONObject( "persona" );
				
				return new ProfileData( 
					personaObject.getString("personaName"),
					personaObject.getString("personaName"),
					personaObject.getLong( "personaId" ), 
					profileId,
					DataBank.getPlatformIdFromName( personaObject.getString( "namespace" ) ) 
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
				profile.getPlatformId()
					
			);
			
		} catch( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException("No profile found");
			
		}
	}
	
	public static PlayerData getStatsForUser(ProfileData pd) throws WebsiteHandlerException {
		
		try {
			
			//Do we have a personaId?
			if( pd.getPersonaId() == 0 ) {
				
				pd = getPersonaIdFromProfile(pd.getProfileId());
				
			}
			
	    	//Get the data
	    	RequestHandler wh = new RequestHandler();
	    	String content = wh.get( 
    	
    			Constants.urlStatsOverview.replace(
    					
					"{UID}", pd.getPersonaId() + ""
				
				).replace( 
				
					"{PLATFORM_ID}", pd.getPlatformId() + ""
				),
				false
			
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
	        return new PlayerData(
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
		
				Constants.urlStatsUpcoming.replace(
						
					"{UID}", pd.getPersonaId() + ""
				
				).replace( 
				
					"{PLATFORM_ID}", pd.getPlatformId() + ""
				), 
				false
				
			);
	    	
	    	//JSON Objects
	    	JSONObject dataObject = new JSONObject(content).getJSONObject( "data" );
	    	JSONArray unlockResults = dataObject.getJSONArray( "unlocks" );
	    	JSONObject unlockRow, detailObject;
	    	int unlockKit;
	    	
	    	//Iterate over the unlocksArray
	    	for( int i = 0; i < unlockResults.length(); i++ ) {
	    	
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
	
	public static final ArrayList<ArrayList<ProfileData>> getFriendsCOM(String checksum) throws WebsiteHandlerException {
		
		try {
				
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			String httpContent;
			ArrayList<ArrayList<ProfileData>> profileArray = new ArrayList<ArrayList<ProfileData>>();
			
			//Get the content
			httpContent = wh.post( 
					
				Constants.urlFriends, 
				new PostData[] { 
						
					new PostData(
							
						Constants.fieldNamesCHSUM[0], 
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
					for( int i = 0; i < requestsObject.length(); i++ ) {
						
						//Grab the object
						tempObj = requestsObject.optJSONObject( i );
						
						//Save it
						profileRowRequests.add(
								
							new ProfileData(

								tempObj.getString( "username" ),
								tempObj.getString( "username" ),
								0,
								Long.parseLong( tempObj.getString( "userId" ) ),
								0
							
							) 
							
						);
				
					}

					//Sort it out
					Collections.sort( profileRowRequests, new ProfileComparator() );
			 
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
							
							
							if( presenceObj.getBoolean( "isPlaying" ) ) {
								
								profileRowPlaying.add(								
							
									new ProfileData(

										tempObj.getString( "username" ),
										tempObj.getString( "username" ),
										0,
										Long.parseLong( tempObj.getString( "userId" ) ),
										0,
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
									0
									
								) 
									
							);
							
						}
					
					}
					
					//How many "online" friends do we have? Playing + idle
					numFriends = profileRowPlaying.size() + profileRowOnline.size();
					
					//First add the separators)...
					if( numFriends > 0 ){
						
						profileRowPlaying.add(  new ProfileData( "00000000", "Online friends", 0, 0, 0 ) );
					
					}
					
					
					if( profileRowOffline.size() > 0 ) {
						
						profileRowOffline.add(  new ProfileData( "00000001", "Offline friends", 0, 0, 0 ) );
					
					}
					
					//...then we sort it out...
					Collections.sort( profileRowPlaying, new ProfileComparator() );
					Collections.sort( profileRowOnline, new ProfileComparator() );
					Collections.sort( profileRowOffline, new ProfileComparator() );
					
					//...sprinkle a little merging here and there...
					profileRowPlaying.addAll( profileRowOnline );
					
					//...and add it to the list!
					profileArray.add( profileRowPlaying );
					profileArray.add( profileRowOffline );
				
				} else {
					
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
			httpContent = wh.post( Constants.urlFriends, new PostData[] { new PostData(Constants.fieldNamesCHSUM[0], checksum) }, 0);
	
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
						
					Constants.urlFriendAccept.replace( 
						
						"{PID}", 
						pId + ""
					), 
					new PostData[] { 
					
						new PostData(
							
							Constants.fieldNamesCHSUM[0], 
							checksum
						
						) 
						
					}, 
					0

				);
			
			} else {

				httpContent = wh.post( 
					
					Constants.urlFriendDecline.replace( 
						
						"{PID}", 
						pId + ""
					), 
					new PostData[] { 
					
						new PostData(
							
							Constants.fieldNamesCHSUM[0], 
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
					
				Constants.urlChatContents.replace( 
					
					"{PID}", 
					profileId + ""
				), 
				new PostData[] { 
				
					new PostData(
						
						Constants.fieldNamesCHSUM[0], 
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
					
				Constants.urlChatContents.replace( 
					
					"{PID}", 
					profileId + ""
				), 
				new PostData[] { 
				
					new PostData(
						
						Constants.fieldNamesCHSUM[0], 
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
				for( int i = 0; i < messages.length(); i++ ) {
					
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
					
				Constants.urlChatSend, 
				new PostData[] { 
					 new PostData(
								
						Constants.fieldNamesChat[2], 
						checksum
					
					),
					new PostData(
							
						Constants.fieldNamesChat[1], 
						chatId
						
					),
					new PostData(
							
						Constants.fieldNamesChat[0], 
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
			String httpContent;
			
			//Get the content
			httpContent = rh.get(
					
				Constants.urlChatClose.replace(
						
					"{CID}", 
					chatId + ""
					
				),
				true
					
			);
						
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				return true;
				
			} else {
			
				throw new WebsiteHandlerException("Could not close the chat.");
				
			}	
		
		} catch ( RequestHandlerException ex ) {
			
			throw new WebsiteHandlerException(ex.getMessage());
			
		}

	}
	
	public static ProfileInformation getProfileInformationForUser( ProfileData profileData, long activeProfileId ) throws WebsiteHandlerException {
		
		try {
			
			//Let's go!
			RequestHandler rh = new RequestHandler();
			ArrayList<FeedItem> feedItemArray = new ArrayList<FeedItem>();
			ArrayList<PlatoonData> platoonDataArray = new ArrayList<PlatoonData>();
			String httpContent;
			
			//Get the content
			httpContent = rh.get( Constants.urlProfileInfo.replace( "{UNAME}", Uri.encode( profileData.getAccountName() ) ), true );

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
				for( int i = 0; i < platoonArray.length(); i++ ) {
					
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
							rh.getImageFromStream( Constants.urlPlatoonThumbs + currItem.getString( "badgePath" ), false),
							!currItem.getBoolean("hidden")
								
						)
					
					);
					
				}
				
				//Parse the feed
				feedItemArray = getFeedItemsFromJSON(feedItems, profileData, activeProfileId);
				
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
					
				Constants.urlPlatoonFans.replace( "{PID}",  platoonId + ""), 
				true
				
			);
			
			//Let's start with the JSON shall we?
			JSONObject fanArray = new JSONObject(httpContent).getJSONObject("context").getJSONObject("fans");
			JSONArray fanIdArray = fanArray.names();
			JSONObject tempObject = null;
			
			//Iterate over the fans
			for( int i = 0; i < fanIdArray.length(); i++ ) {
				
				//Grab the fan
				tempObject = fanArray.getJSONObject( fanIdArray.getString( i ) );
				
				//Store him in the ArrayList
				fans.add( 
					
					new ProfileData(

						tempObject.getString( "username" ),
						null,
						Long.parseLong(tempObject.getString( "userId" ) ),
						0,
						0
					)				
						
				);
				
			}
			
			//Did we get more than 0?
			if( fans.size() > 0 ) {
			
				//Add a header just 'cause we can
				fans.add( new ProfileData("00000000", "Loyal fans", 0, 0, 0) );
				
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
	
	/* TODO */
	public static PlatoonInformation getProfileInformationForPlatoon( PlatoonData platoonData, long activeProfileId ) throws WebsiteHandlerException {
		
		try {
			
			//Let's go!
			RequestHandler rh = new RequestHandler();
			ArrayList<FeedItem> feedItemArray = new ArrayList<FeedItem>();
			ArrayList<ProfileData> fans = new ArrayList<ProfileData>(); 
			ArrayList<ProfileData> members = new ArrayList<ProfileData>();
			
			//Arrays to divide the users in
			ArrayList<ProfileData> founderMembers = new ArrayList<ProfileData>();
			ArrayList<ProfileData> adminMembers = new ArrayList<ProfileData>();
			ArrayList<ProfileData> regularMembers = new ArrayList<ProfileData>();
			ArrayList<ProfileData> invitedMembers = new ArrayList<ProfileData>();
			ArrayList<ProfileData> requestMembers = new ArrayList<ProfileData>();
			
			//Get the content
			String httpContent = rh.get( Constants.urlPlatoon.replace( "{PID}", platoonData.getId() + "" ), true );
			boolean hasAdminRights = false;
	
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
						
						hasAdminRights = true;
						
					}
					
					//Add it to the members
					tempProfile = new ProfileData(

						currItem.getJSONObject( "user" ).getString( "username" ),
						currItem.getJSONObject( "persona" ).getString( "personaName" ),
						Long.parseLong( currItem.getString( "personaId" ) ),
						Long.parseLong( currItem.getString( "userId" ) ),
						profileCommonObject.getLong( "platform" ),
						currItem.getJSONObject( "user" ).getJSONObject("presence").getBoolean( "isOnline" ),
						currItem.getJSONObject( "user" ).getJSONObject("presence").getBoolean( "isPlaying" )
							
					);
						
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
						
						members.add( new ProfileData("00000000", "Founders", 0, 0, 0) );
					
					} else { 
						
						members.add( new ProfileData("00000001", "Founder", 0, 0, 0) );
						
					}
			
					//Add them to the members
					members.addAll( founderMembers );
					
				}
		
				if( adminMembers.size() > 0 ) { 
					
					//Plural?
					if( adminMembers.size() > 1 ) {
						
						members.add( new ProfileData("00000002", "Admins", 0, 0, 0) );
					
					} else { 
						
						members.add( new ProfileData("00000003", "Admin", 0, 0, 0) );
						
					}
			
					//Add them to the members
					members.addAll( adminMembers );
					
				}
				
				if( regularMembers.size() > 0 ) { 
					
					//Plural?
					if( regularMembers.size() > 1 ) {
						
						members.add( new ProfileData("00000004", "Regular members", 0, 0, 0) );
					
					} else { 
						
						members.add( new ProfileData("00000005", "Regular member", 0, 0, 0) );
						
					}
			
					//Add them to the members
					members.addAll( regularMembers );
					

				}

				//Is the user *admin* or higher?
				if( hasAdminRights ) {
					
					if( invitedMembers.size() > 0 ) {
						
						//Just add them
						members.add( new ProfileData("00000006", "Invited to join", 0, 0, 0) );
						members.addAll( invitedMembers );
						
					}
					
					if( requestMembers.size() > 0 ) {
	
						//Just add them
						members.add( new ProfileData("00000007", "Requested to join", 0, 0, 0) );
						members.addAll( requestMembers);
	
					}

				}
					
				//Let's get 'em fans too
				fans = WebsiteHandler.getFansForPlatoon(platoonData.getId());
				
				//Parse the feed
				feedItemArray = getFeedItemsFromJSON(feedItems, null, activeProfileId);
				
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
					profileCommonObject.getString( "badgePath" ),
					PublicUtils.normalizeUrl( profileCommonObject.optString( "website", "" ) ),
					!profileCommonObject.getBoolean( "hidden" ),
					hasAdminRights,
					profileCommonObject.getBoolean( "allowNewMembers" ),
					feedItemArray,
					members,
					fans
					
				);
				
			} else {
			
				throw new WebsiteHandlerException("Could not get the profile.");
				
			}	
		
		} catch ( Exception ex ) {
			
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
	private static ArrayList<FeedItem> getFeedItemsFromJSON( JSONArray jsonArray, ProfileData profileData, long activeProfileId ) throws WebsiteHandlerException {

		try {
			
			//Variables that we need
			JSONObject currItem;
			JSONObject tempSubItem;
			JSONObject tempCommentItem;
			FeedItem tempFeedItem = null;
			ArrayList<FeedItem> feedItemArray = new ArrayList<FeedItem>();
			ArrayList<CommentData> comments;
			
			//Iterate over the feed
			for( int i = 0; i < jsonArray.length(); i++ ) {
				
				//Once per loop
				comments = new ArrayList<CommentData>();
				
				//Each loop is an object
				currItem = jsonArray.getJSONObject( i );
				
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
						"<b>{username1}</b> and <b>{username2}</b> are now friends",
						"",
						currItem.getString("event"),
						new String[] { 
							
							currItem.getJSONObject("owner").getString("username"), 
							tempSubItem.getJSONObject( "friendUser" ).getString( "username" )
							
						},
						liked,
						comments
							
					);
					
				} else if( !currItem.isNull( "WROTEFORUMPOST" )) {
				
					//Grab the specific object
					tempSubItem = currItem.optJSONObject( "WROTEFORUMPOST" );
					itemTitle = "<b>{username}</b> wrote a forum post in the thread <b>\"{thread}\"</b>.".replace( 
						
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
							
							currItem.getJSONObject("owner").getString("username"), 
							null 
						
						},
						liked,
						comments
							
					);
					
				} else if( !currItem.isNull( "GAMEREPORT" )) {
				
					//Grab the specific object
					JSONArray tempStatsArray = currItem.optJSONObject( "GAMEREPORT" ).optJSONArray( "statItems" );
					tempSubItem = tempStatsArray.optJSONObject( 0 );
					itemTitle = "<b>{username}</b> unlocked a new item: <b>{item}</b>.";
					
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
							
							currItem.getJSONObject("owner").getString("username"), 
							null 
						
						},
						liked,
						comments
							
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
							
							currItem.getJSONObject("owner").getString("username"),
							null 
							
						},
						liked,
						comments
							
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
						"<b>{username}</b> favorited a server: <b>" + tempSubItem.getString( "serverName" ) + "</b>.",
						"",
						currItem.getString("event"),
						new String[] { 
							
							currItem.getJSONObject("owner").getString("username"), 
							null 
						
						},
						liked,
						comments
							
					);
					
				} else if( !currItem.isNull( "RANKEDUP" )) {
					
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "RANKEDUP" );
					
					//Set it!
					itemTitle = "<b>{username}</b> got promoted to <b>{rank title}</b> (rank{rank}).".replace( 
						
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
							
							currItem.getJSONObject("owner").getString("username"), 
							null 
							
						},
						liked,
						comments
							
					);
	
				} else if( !currItem.isNull( "COMMENTEDGAMEREPORT" )) {
				
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "COMMENTEDGAMEREPORT" );
					
					//Set it!
					itemTitle = "<b>{username}</b> commented on Battle Report <b>{server name} {map} {game mode}</b>.".replace(
							
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
							
							currItem.getJSONObject("owner").getString("username"), 
							null 
							
						},
						liked,
						comments
							
					);
				
				} else if( !currItem.isNull( "JOINEDPLATOON" )) {
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "JOINEDPLATOON" ).getJSONObject( "platoon" );
					
					//Set it!
					itemTitle = "<b>{username}</b> joined the platoon <b>{platoon}</b>.".replace( 
				
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
								
								currItem.getJSONObject("owner").getString("username"),
								null 
						},								
						liked,
						comments
							
					);
				
				} else if( !currItem.isNull( "CREATEDPLATOON" )) {
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "CREATEDPLATOON" ).getJSONObject( "platoon" );
					
					//Set it!
					itemTitle = "<b>{username}</b> created a platoon named <b>{platoon}</b>.".replace( 
				
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
								
								currItem.getJSONObject("owner").getString("username"),
								null 
						},								
						liked,
						comments
							
					);
				
				} else if( !currItem.isNull( "PLATOONBADGESAVED" )) {
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "PLATOONBADGESAVED" ).getJSONObject( "platoon" );
					
					//Set it!
					itemTitle = "<b>{username}</b> changed the emblem of the platoon <b>{platoon}</b>.".replace( 
				
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
								
								currItem.getJSONObject("owner").getString("username"),
								null 
						},								
						liked,
						comments
							
					);
				
				} else if( !currItem.isNull( "LEFTPLATOON" )) {
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "LEFTPLATOON" ).getJSONObject( "platoon" );
					
					//Set it!
					itemTitle = "<b>{username}</b> left the platoon <b>{platoon}</b>.".replace( 
				
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
							
							currItem.getJSONObject("owner").getString("username"),
							null 
						},
						liked,
						comments
							
					);
					
				} else if( !currItem.isNull( "RECEIVEDPLATOONWALLPOST" )) {
					
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "RECEIVEDPLATOONWALLPOST" );
					
					//Set it!
					itemTitle = "<b>{username}</b> wrote on the wall for <b>{platoon}</b>.".replace(
							
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
						
							currItem.getJSONObject("owner").getString("username"),
							null 
							
						},
						liked,
						comments
							
					);
					
				} else if( !currItem.isNull( "LEVELCOMPLETE" )) {
						
	
					//Get it!
					tempSubItem = currItem.getJSONObject( "LEVELCOMPLETE" );
					
					//Set it!
					itemTitle = "<b>{username1}</b> and <b>{username2}</b> completed <b>\"{level}\"</b> on <b>{difficulty}</b>.".replace(
							
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
							
							currItem.getJSONObject("owner").getString("username"), 
							tempSubItem.getJSONObject( "friend" ).getString( "username" ) 
						
						},
						liked,
						comments
							
					);
					
				} else if( !currItem.isNull( "RECEIVEDAWARD" )) {
					
	
					//Get it!
					tempSubItem = currItem.optJSONObject( "RECEIVEDAWARD" ).optJSONArray( "statItems" ).getJSONObject( 0 );
					
					//Set it!
					itemTitle = "<b>{username}</b> recieved a new award: <b>{award}</b>.".replace( 
							
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
							
							currItem.getJSONObject("owner").getString("username"),
							null 
							
						},
						liked,
						comments
							
					);
					
				} else if( !currItem.isNull( "RECEIVEDWALLPOST" )) {
					
	
					//Get it!
					tempSubItem = currItem.optJSONObject( "RECEIVEDWALLPOST" );
					
					//Set it!
					itemTitle = "<b>{username1} » {username2}:</b> {message}".replace( 
							
						"{message}", 
						tempSubItem.getString( "wallBody" )
	
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
							
							tempSubItem.getJSONObject( "writerUser" ).getString( "username" ), 
							currItem.getJSONObject("owner").getString("username") 
							
						},
						liked,
						comments
							
					);
					
				} else {
					
					Log.d(Constants.debugTag, "event => " + currItem.getString( "event" ) );
					tempFeedItem = null;
				
				}
				
				//Append it to the array
				if( tempFeedItem != null ) { feedItemArray.add( tempFeedItem ); }
				
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
					
				Constants.urlFeedComments.replace( 
					
					"{PID}", 
					postId + ""
				
				),
				false
		
			);
						
			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//Get the messages
				JSONArray commentArray = new JSONObject(httpContent).getJSONObject("data").getJSONArray( "comments" );
				JSONObject tempObject;
				
				//Iterate
				for( int i = 0; i < commentArray.length(); i++ ) {
					
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
					
				Constants.urlHooah.replace( "{ID}", postId + "" ), 
				new PostData[] { 
					 
					new PostData(
								
						Constants.fieldNamesCHSUM[0], 
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
					
				Constants.urlUnHooah.replace( "{ID}", postId + "" ), 
				new PostData[] { 
					 
					new PostData(
								
						Constants.fieldNamesCHSUM[0], 
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
					
				Constants.urlComment.replace( "{ID}", postId + "" ), 
				new PostData[] { 
					 
					new PostData(
								
						Constants.fieldNamesFeedComment[0], 
						comment
					
					),
					new PostData(
							
						Constants.fieldNamesFeedComment[1], 
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
					
				Constants.urlFriendRequest.replace( 
						
					"{PID}", 
					profileId + ""
					
				), 
				new PostData[] {
					
					new PostData(
					
						Constants.fieldNamesCHSUM[0],
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

	public static boolean postToWall(long profileId, String checksum, String content) throws WebsiteHandlerException {
		
		try {

			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
			PostData[] postDataArray;
			String httpContent = wh.post( 
					
				Constants.urlProfilePost,
				postDataArray = new PostData[] {
					
					new PostData(
							
						Constants.fieldNamesProfilePost[0],
						content
							
					),new PostData(
							
						Constants.fieldNamesProfilePost[1],
						profileId + ""
								
					),new PostData(
							
						Constants.fieldNamesProfilePost[2],
						checksum
							
					)
				},
				2
				
			);

			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
	
				//Check the JSON
				if( (new JSONObject(httpContent)).getString( "message" ).equals( "WALL_POST_CREATED" )) {
					
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
			if( !rh.saveFileFromURI( Constants.urlImagePack, "", "imagepack-001.zip" ) ) {
			
				return "<this is supposed to be the path to the zip>";
				
			} else {
				
				throw new WebsiteHandlerException("No zip found.");
				
			}
			
		} catch( Exception ex ) {
		
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
			
		}
		
	}
	
}