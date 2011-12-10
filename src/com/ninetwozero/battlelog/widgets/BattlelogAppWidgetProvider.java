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

package com.ninetwozero.battlelog.widgets;


import java.util.ArrayList;

import net.sf.andhsli.hotspotlogin.SimpleCrypto;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ninetwozero.battlelog.Main;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.asynctasks.AsyncLogin;
import com.ninetwozero.battlelog.datatypes.PersonaStats;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class BattlelogAppWidgetProvider extends AppWidgetProvider {

	public static final String DEBUG_TAG = "WidgetProvider";
	public static final String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
	public static final String ACTION_WIDGET_OPENAPP = "Main";
	
	   @Override
	   public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		   
		   //Attributes
		   Intent active = new Intent(context, BattlelogAppWidgetProvider.class).setAction(ACTION_WIDGET_RECEIVER);
		   PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
		   Intent appIntent = new Intent(context, Main.class);
		   PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
		   appIntent.setAction(ACTION_WIDGET_OPENAPP);
		   
		   RemoteViews remoteView = null;
		   ProfileData profileData = null;
		   PersonaStats playerData = null;
		   ArrayList<ProfileData> profileDataArray = null;
		   SharedPreferences sharedPreferences = null;
		   ComponentName BattlelogListWidget;
		   int numFriendsOnline = 0;
	   
		   //Set the values
		   sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);  
		   profileData = new ProfileData(

				sharedPreferences.getString( Constants.SP_BL_USERNAME, "" ),
			    sharedPreferences.getString( Constants.SP_BL_PERSONA, "" ),
			    sharedPreferences.getLong( Constants.SP_BL_PERSONA_ID, 0 ),
			    sharedPreferences.getLong( Constants.SP_BL_PERSONA_ID, 0 ),
			    sharedPreferences.getLong( Constants.SP_BL_PLATFORM_ID, 1),
				sharedPreferences.getString( Constants.SP_BL_GRAVATAR, "" )
		   
		   );
		   remoteView = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
		   
		   try {
			    
			   playerData = WebsiteHandler.getStatsForUser(profileData);
			   remoteView.setTextViewText(R.id.label, playerData.getPersonaName());
			   remoteView.setTextViewText(R.id.title, "Rank: " + playerData.getRankId() + " (" + playerData.getPointsProgressLvl() + "/" + playerData.getPointsNeededToLvlUp() + ")");
			   remoteView.setTextViewText(R.id.stats, "W/L: " + playerData.getWLRatio() + "  K/D: " + playerData.getKDRatio());
			   profileDataArray = WebsiteHandler.getFriends( sharedPreferences.getString(Constants.SP_BL_CHECKSUM, ""), true);
			   numFriendsOnline = profileDataArray.size();
			   
		   } catch (WebsiteHandlerException e) {
				
			    e.printStackTrace();
			   	
				if( !sharedPreferences.getBoolean(Constants.SP_BL_REMEMBER, false) ) {
					
					Toast.makeText(context, R.string.msg_widget_auth_fail, Toast.LENGTH_SHORT).show();
					return;
					
				} else {
					
					try {
						
						//Init
						PostData[] postDataArray = new PostData[Constants.FIELD_NAMES_LOGIN.length];
			    		String[] valueFields = new String[] { 
							
						sharedPreferences.getString( Constants.SP_BL_EMAIL, ""), 
						SimpleCrypto.decrypt( 
										
							sharedPreferences.getString( 
								Constants.SP_BL_EMAIL, 
								"" 
							), 
							sharedPreferences.getString( 
								Constants.SP_BL_PASSWORD, 
									""
								)
							)
							
			    		}; 

						//Iterate and conquer
			    		for( int i = 0, max = Constants.FIELD_NAMES_LOGIN.length; i < max; i++ ) {
	
			    			postDataArray[i] =	new PostData(
				    			
			    				Constants.FIELD_NAMES_LOGIN[i],
				    			(Constants.FIELD_VALUES_LOGIN[i] == null) ? valueFields[i] : Constants.FIELD_VALUES_LOGIN[i] 
				    		);
			    		
			    		}
						
			    		//Login!
						AsyncLogin al = new AsyncLogin(context, true, true);
						al.execute(postDataArray);

						//Get our stats and output them
						playerData = WebsiteHandler.getStatsForUser(profileData);
						remoteView.setTextViewText(R.id.label, playerData.getPersonaName());
						remoteView.setTextViewText(R.id.title, "Rank: " + playerData.getRankId() + " (" + playerData.getPointsProgressLvl() + "/" + playerData.getPointsNeededToLvlUp() + ")");
						remoteView.setTextViewText(R.id.stats, "W/L: " + playerData.getWLRatio() + "  K/D: " + playerData.getKDRatio());
						
						//Get the friends
						profileDataArray = WebsiteHandler.getFriends( sharedPreferences.getString(Constants.SP_BL_CHECKSUM, ""), true );
						numFriendsOnline = profileDataArray.size();
						
					} catch (Exception ex ) {

						ex.printStackTrace();
						
					}
					
				}
		   }
		
		   if (numFriendsOnline > 0) { 
				
				remoteView.setTextColor(R.id.friends, Color.BLACK);
				remoteView.setTextViewText(R.id.friends, ""+ numFriendsOnline);
			
		   } else {
				
				remoteView.setTextColor(R.id.friends, Color.RED);
				remoteView.setTextViewText(R.id.friends, "0");
			
		   }  
		      
		    remoteView.setOnClickPendingIntent(R.id.widget_button, actionPendingIntent);
		    remoteView.setOnClickPendingIntent(R.id.widget_button2, appPendingIntent);
		    BattlelogListWidget = new ComponentName(
	    		context,
		        BattlelogAppWidgetProvider.class
	        );
		    appWidgetManager.updateAppWidget(BattlelogListWidget, remoteView);
	}
	   @Override
	   public void onReceive(Context context, Intent intent) {

		   	super.onReceive(context, intent);
				
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			ComponentName thisAppWidget = new ComponentName(context, BattlelogAppWidgetProvider.class);
			int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
			
			//UPDATE IT !!!!
			onUpdate(context, appWidgetManager, appWidgetIds); 
	    } 
	   
}
	