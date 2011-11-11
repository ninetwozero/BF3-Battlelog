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
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ninetwozero.battlelog.Main;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.asynctasks.AsyncLogin;
import com.ninetwozero.battlelog.datatypes.Constants;
import com.ninetwozero.battlelog.datatypes.PlayerData;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
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
		   PlayerData playerData = null;
		   ArrayList<ProfileData> profileDataArray = null;
		   SharedPreferences sharedPreferences = null;
		   ComponentName BattlelogListWidget;
		   int numFriendsOnline = 0;
	   
		   //Set the values
		   sharedPreferences = context.getSharedPreferences( Constants.fileSharedPrefs, 0);  
		   profileData = new ProfileData(
				
			    sharedPreferences.getString( "battlelog_persona", "" ),
			    sharedPreferences.getLong( "battlelog_persona_id", 0 ),
			    sharedPreferences.getLong( "battlelog_persona_id", 0 ),
			    sharedPreferences.getLong( "battlelog_platform_id", 1)
		   
		   );
		   remoteView = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
		   
		   try {
			    
			   playerData = WebsiteHandler.getStatsForUser(profileData);
			   remoteView.setTextViewText(R.id.label, "Name: "+ playerData.getPersonaName());
			   remoteView.setTextViewText(R.id.title, "Rank: " + playerData.getRankId() + " (" + playerData.getPointsProgressLvl() + "/" + playerData.getPointsNeededToLvlUp() + ")");
			   remoteView.setTextViewText(R.id.stats, "W/L: " + playerData.getWLRatio() + "  K/D: " + playerData.getKDRatio());
			   profileDataArray = WebsiteHandler.getFriends( sharedPreferences.getString("battlelog_post_checksum", ""), true);
			   numFriendsOnline = profileDataArray.size();
			   
		   } catch (WebsiteHandlerException e) {
				
			    e.printStackTrace();
			   	
				if( !sharedPreferences.getBoolean("remember_password", false) ) {
					
					Toast.makeText(context, "Authentication failed - is your password saved?", Toast.LENGTH_SHORT).show();
					return;
					
				} else {
					
					try {
						
						//Init
						PostData[] postDataArray = new PostData[Constants.fieldNamesLogin.length];
			    		String[] valueFields = new String[] { 
							
						sharedPreferences.getString( "origin_email", ""), 
						SimpleCrypto.decrypt( 
										
							sharedPreferences.getString( 
								"origin_email", 
								"" 
							), 
							sharedPreferences.getString( 
								"origin_password", 
									""
								)
							)
							
			    		}; 

						//Iterate and conquer
			    		for( int i = 0; i < Constants.fieldNamesLogin.length; i++ ) {
	
			    			postDataArray[i] =	new PostData(
				    			
			    				Constants.fieldNamesLogin[i],
				    			(Constants.fieldValuesLogin[i] == null) ? valueFields[i] : Constants.fieldValuesLogin[i] 
				    		);
			    		
			    		}
						
			    		//Login!
						AsyncLogin al = new AsyncLogin(context, true, true);
						al.execute(postDataArray);

						//Get our stats and output them
						playerData = WebsiteHandler.getStatsForUser(profileData);
						remoteView.setTextViewText(R.id.label, "Name: "+ playerData.getPersonaName());
						remoteView.setTextViewText(R.id.title, "Rank: " + playerData.getRankId() + " (" + playerData.getPointsProgressLvl() + "/" + playerData.getPointsNeededToLvlUp() + ")");
						remoteView.setTextViewText(R.id.stats, "W/L: " + playerData.getWLRatio() + "  K/D: " + playerData.getKDRatio());
						
						//Get the friends
						profileDataArray = WebsiteHandler.getFriends( sharedPreferences.getString("battlelog_post_checksum", ""), true );
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
	