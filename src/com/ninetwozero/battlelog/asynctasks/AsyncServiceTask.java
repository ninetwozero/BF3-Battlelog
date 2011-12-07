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
import com.ninetwozero.battlelog.misc.Constants;
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
				
				//Logging
				Log.d(Constants.DEBUG_TAG, "The user is active");
				
				//The user is active, so how many notifications does he have?
				int numNotifications = WebsiteHandler.getNewNotificationsCount( arg0[0] );
				
				//R-turn
				return numNotifications;
				
			} else {
				
				//Logging
				Log.d(Constants.DEBUG_TAG, "The user isn't logged in. Let's login!");
				
				//Do the login
				WebsiteHandler.doLogin( 
						
					context, 
					new PostData[] {

						new PostData(Constants.FIELD_NAMES_LOGIN[0], arg0[1]),
						new PostData(Constants.FIELD_NAMES_LOGIN[1], arg0[2] )
							
					}, 
					true 
					
				);
				
				//Restart the AsyncTask and return -1
				new AsyncServiceTask(context).execute();
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
				battlelogNotification.icon = android.R.drawable.ic_dialog_info;
				battlelogNotification.when = System.currentTimeMillis();
	
				Intent notificationIntent = new Intent(context, Dashboard.class).putExtra( "openCOMCenter", true).putExtra("tabid", 1);
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
					
				//Set the ticker
				battlelogNotification.tickerText =  "Someone has interacted with you on Battlelog!";
				battlelogNotification.setLatestEventInfo(
						
					context, 
					"Someone has interacted with you on Battlelog!", 
					"Tap this item to go to view notifications.", 
					contentIntent
				
				);
	
				//Notify yo
				notificationManager.notify(0, battlelogNotification);
			
			} else {
			
				Log.d(Constants.DEBUG_TAG, "No unread notifications");
				return;

			}	
				
		} else if( results == -1 ) {
			
			Log.d(Constants.DEBUG_TAG, "User was not logged in - retrying.");
			return;
			
		} else {
		
			//Error in previous method
			Toast.makeText( context, "Error while updating the service.", Toast.LENGTH_SHORT).show();
			return;

		}
		
	}

}
