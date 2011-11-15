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
import android.text.TextUtils;
import android.util.Log;

import com.ninetwozero.battlelog.datatypes.ChatMessage;
import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
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
    		httpContent = wh.post( Constants.urlLogin, postDataArray, false);

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
				false
				
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
					
					return WebsiteHandler.getPersonaIdFromProfile( profile.getProfileId() );

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
			httpContent = wh.post( Constants.urlFriends, new PostData[] { new PostData(Constants.fieldNamesCHSUM[0], checksum) }, false);

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
					
					//First add the separators)...
					
					if( (profileRowPlaying.size() + profileRowOnline.size()) > 0 ){
						
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
					profileRowPlaying.addAll( profileRowOffline );
					
					//...and add it to the list!
					profileArray.add( profileRowPlaying );
				
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
			httpContent = wh.post( Constants.urlFriends, new PostData[] { new PostData(Constants.fieldNamesCHSUM[0], checksum) }, false);
	
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
					false

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
					false
			
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
				false
		
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
				false
		
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
				true
		
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
	
	public static ProfileInformation getProfileInformationForUser( ProfileData profileData ) throws WebsiteHandlerException {
		
		try {
			
			//Let's go!
			RequestHandler rh = new RequestHandler();
			ArrayList<FeedItem> feedItemArray = new ArrayList<FeedItem>();
			ArrayList<PlatoonData> platoonDataArray = new ArrayList<PlatoonData>();
			String httpContent;
			
			Log.d(Constants.debugTag, Constants.urlProfileInfo.replace( "{UNAME}", profileData.getAccountName()));
			
			//Get the content
			httpContent = rh.get( Constants.urlProfileInfo.replace( "{UNAME}", profileData.getAccountName() ), true );

			//Did we manage?
			if( httpContent != null && !httpContent.equals( "" ) ) {
			
				//JSON Objects
				JSONObject contextObject = new JSONObject(httpContent).optJSONObject( "context" );
				JSONObject profileCommonObject = contextObject.optJSONObject( "profileCommon" );
				JSONObject userInfo = profileCommonObject.optJSONObject( "userinfo" );
				JSONObject presenceObject = profileCommonObject.getJSONObject( "user" ).getJSONObject("presence");
				JSONArray gameReports = contextObject.getJSONArray( "gameReportPreviewGroups" );
				JSONArray feedItems = contextObject.getJSONArray( "feed" );
				JSONArray platoonArray = profileCommonObject.getJSONArray( "platoons" );
				JSONObject statusMessage = profileCommonObject.optJSONObject( "userStatusMessage" );
				JSONObject currItem;
				JSONObject tempSubItem;
				JSONObject tempCommentItem;
				JSONArray commentArray;
				
				//Is status messages null?
				if( statusMessage == null ) { statusMessage = new JSONObject("{'statusMessage':'', 'statusMessageChanged':0}"); }
				
				//Re-usable variable!!
				FeedItem tempFeedItem = null;
				PlatoonData tempPlatoonData = null;
				ArrayList<CommentData> comments = new ArrayList<CommentData>();
				
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
				
				//Iterate over the feed
				for( int i = 0; i < feedItems.length(); i++ ) {

					//Once per loop
					comments.clear();
					
					//Each loop is an object
					currItem = feedItems.getJSONObject( i );
					
					//Let's get the comments
					if( currItem.getInt("numComments") > 2 ) {
						
						/* TODO: WebsiteHandler.getCommentForPost(long postId); */ 
						
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
									tempCommentItem.getJSONObject("owner").getString("username"),
									tempCommentItem.getString( "body" )
								
								)
									
							);
						
						}
						
					}
					
					//What do we have *here*?
					if( !currItem.isNull( "BECAMEFRIENDS" )) {
					
						//Grab the specific object
						tempSubItem = currItem.optJSONObject( "BECAMEFRIENDS" );
						
						//The usernames are flushed...
						String username = "";
						if( tempSubItem.getJSONObject( "friendUser" ).getString( "username" ).equals( profileData.getAccountName() ) ) {
							
							username = currItem.getJSONObject( "owner" ).getString( "username" );

						} else {
							
							username = tempSubItem.getJSONObject( "friendUser" ).getString( "username" );

						}
						
						//Temporary storage						
						tempFeedItem = new FeedItem(
							
							profileData.getProfileId(),
							Long.parseLong( currItem.getString("itemId") ),
							currItem.getLong( "creationDate" ),
							currItem.getInt( "numLikes" ),
							"{player1} and {player2} are now friends",
							"",
							currItem.getString("event"),
							new String[] { profileData.getAccountName(), username },
							comments
								
						);
						
					} else if( !currItem.isNull( "WROTEFORUMPOST" )) {
					
						//Grab the specific object
						tempSubItem = currItem.optJSONObject( "WROTEFORUMPOST" );
						
						//Temporary storage						
						tempFeedItem = new FeedItem(
							
							profileData.getProfileId(),
							Long.parseLong( currItem.getString("itemId") ),
							currItem.getLong( "creationDate" ),
							currItem.getInt( "numLikes" ),
							tempSubItem.getString( "threadTitle" ),
							tempSubItem.getString( "postBody" ),
							currItem.getString("section"),
							new String[] { profileData.getAccountName(), null },
							comments
								
						);
						
					} else if( !currItem.isNull( "GAMEREPORT" )) {
					
						//Grab the specific object
						JSONArray tempStatsArray = currItem.optJSONObject( "GAMEREPORT" ).optJSONArray( "statItems" );
						tempSubItem = tempStatsArray.optJSONObject( 0 );
						String itemTitle;
						
						//Weapon? Attachment?
						if( !tempSubItem.isNull( "parentLangKeyTitle" ) ) {
							
							//Let's see
							itemTitle = DataBank.getWeaponTitle( tempSubItem.getString( "parentLangKeyTitle") );
							
							//Is it empty?
							if( !itemTitle.equals( "" ) ) {
								
								itemTitle += " - " + DataBank.getAttachmentTitle( tempSubItem.getString( "langKeyTitle" ) );
								
							} else {
								
								//Grab a vehicle title then
								itemTitle = DataBank.getVehicleTitle( tempSubItem.getString("parentLangKeyTitle") );
								
								//Validate
								if( !itemTitle.equals( "" ) ) {
									
									itemTitle += " - " + DataBank.getVehicleAddon( tempSubItem.getString( "langKeyTitle" ) );
									
								} else {
									
									itemTitle = tempSubItem.getString("parentLangKeyTitle");
									
								} 
								
							}
							
						} else {
							
							itemTitle = DataBank.getWeaponTitle( "langKeyTitle" );
							
						}
						
						//Temporary storage						
						tempFeedItem = new FeedItem(
							
							profileData.getProfileId(),
							Long.parseLong( currItem.getString("itemId") ),
							currItem.getLong( "creationDate" ),
							currItem.getInt( "numLikes" ),
							itemTitle,
							"",
							currItem.getString("section"),
							new String[] { profileData.getAccountName(), null },
							comments
								
						);
						
					}
					
					//Append it to the array
					if( tempFeedItem != null ) { feedItemArray.add( tempFeedItem ); }
					
				}
				
				return new ProfileInformation(
					
					userInfo.optInt( "age", 0 ), 
					profileData.getProfileId(), 
					userInfo.optLong("birthdate", 0), 
					userInfo.optLong("lastLogin", 0),
					statusMessage.optLong("statusMessageChanged", 0), 
					userInfo.optString( "name", "N/A" ), 
					profileCommonObject.getJSONObject( "user" ).getString( "username" ),
					userInfo.optString( "presentation", "" ),  
					userInfo.optString( "location", "us" ),  
					statusMessage.optString( "statusMessage", "" ), 
					presenceObject.optString("serverName", ""),
					userInfo.optBoolean( "allowFriendRequests", true ), 
					presenceObject.getBoolean("isOnline"),
					presenceObject.getBoolean("isPlaying"),
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
}
