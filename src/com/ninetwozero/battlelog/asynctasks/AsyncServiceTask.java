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

package com.ninetwozero.battlelog.asynctasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ninetwozero.battlelog.Dashboard;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class AsyncServiceTask extends AsyncTask<String, Integer, Integer> {

	//Attribute
	Context context;
	
	//Constructor
	public AsyncServiceTask( Context c ) { 
		
		this.context = c; 
	
	}
	
	@Override
	protected void onPreExecute() {}
	
	@Override
	protected Integer doInBackground( String... arg0 ) {
		
		try {
			
			//Let's try to setActive
			if( WebsiteHandler.setActive() ) {
				
				//The user is active, so how many notifications does he have?
				int numNotifications = WebsiteHandler.getNewNotificationsCount( arg0[0] );
				
				//R-turn
				return numNotifications;
				
			} else {
				
				//Do the login
				ProfileData profileData = WebsiteHandler.doLogin( 
						
					context, 
					new PostData[] {

						new PostData(Constants.FIELD_NAMES_LOGIN[0], arg0[1]),
						new PostData(Constants.FIELD_NAMES_LOGIN[1], arg0[2] ),
						new PostData(Constants.FIELD_NAMES_LOGIN[2], Constants.FIELD_VALUES_LOGIN[2]),
						new PostData(Constants.FIELD_NAMES_LOGIN[3], Constants.FIELD_VALUES_LOGIN[3]),
							
					}, 
					true 
					
				);
				
				//Did it work?
				if( profileData != null ) { SessionKeeper.setProfileData(profileData); }
				
				//Restart the AsyncTask and return -1
				new AsyncServiceTask(context).execute( arg0[0], arg0[1], arg0[2] );
				return -1;
				
			}

		} catch( Exception ex ) {
			
			Log.d(Constants.DEBUG_TAG, "The service has encountered an error.");
			ex.printStackTrace();
			return -2;
			
		}
		
	}
	
	@Override
	protected void onPostExecute(Integer results) {
		
		//Is the result >= 0 
		if( results >= 0 ) {
			 
			//results == numNotifications
			if ( results > 0 ) {
				
				//We had a "positive" outcome
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification battlelogNotification = new Notification();
				battlelogNotification.icon = R.drawable.app_logo;
				battlelogNotification.when = System.currentTimeMillis();
	
				Intent notificationIntent = new Intent(context, Dashboard.class).putExtra(
						
					"openCOMCenter", true
						
				).putExtra(
						
					"openTabId", 1
					
				);
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
					
				//So...
				String text;
				
				if( results == 1 ) {
					
					text = context.getString( R.string.info_txt_notification_new );
				
				} else {
					
					text = context.getString( R.string.info_txt_notification_new_p ).replace("{num}", results + "");					
					
				}
						
				
				//Set the ticker
				battlelogNotification.tickerText =  context.getString( R.string.msg_notification );
				battlelogNotification.setLatestEventInfo(
						
					context, 
					text,
					context.getString( R.string.info_tap_notification ), 
					contentIntent
				
				);
				battlelogNotification.flags |= Notification.FLAG_AUTO_CANCEL;
				
				//Notify yo
				notificationManager.notify(0, battlelogNotification);
			
			} else {
			
				Log.d(Constants.DEBUG_TAG, "No unread notifications");
				return;

			}	
				
		} else if( results == -1 ) {
			
			Log.d(Constants.DEBUG_TAG, "Trying to login...");
			return;
			
		} else {
		
			//Error in previous method
			Toast.makeText( context, "Error while updating the service.", Toast.LENGTH_SHORT).show();
			return;

		}
		
	}

}
