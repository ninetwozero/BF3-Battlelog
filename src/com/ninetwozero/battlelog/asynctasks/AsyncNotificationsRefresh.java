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

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapters.NotificationListAdapter;
import com.ninetwozero.battlelog.datatypes.NotificationData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncNotificationsRefresh extends AsyncTask<String, Integer, Boolean> {

	//Attribute
	private Context context;
	private ArrayList<NotificationData> notifications = new ArrayList<NotificationData>();
	private ListView listNotifications;
	private TextView status;
	private LayoutInflater layoutInflater;
	
	//Constructor
	public AsyncNotificationsRefresh( Context c, ListView f, TextView s, LayoutInflater l ) { 
		
		this.context = c;
		this.listNotifications = f;
		this.layoutInflater = l;
		this.status = s;
	
	}	
	
	@Override
	protected void onPreExecute() {}
	
	@Override
	protected Boolean doInBackground( String... arg0) {
		
		try {
		
			//Let's get this!!
			notifications = WebsiteHandler.getNotifications( arg0[0] );
			return true;
			
		} catch ( WebsiteHandlerException e ) {
			
			return false;
			
		}
		
	}
	
	@Override
	protected void onPostExecute(Boolean results) {
		
		//Fill the listviews!!
		if( listNotifications != null ) { 
			
			if( notifications.size() > 0 ) {
				
				//Set the adapter
				if( status.getVisibility() != View.GONE ) { status.setVisibility( View.GONE ); }
				if( listNotifications.getVisibility() != View.VISIBLE ) { listNotifications.setVisibility( View.VISIBLE ); }
				((NotificationListAdapter) listNotifications.getAdapter()).setItemArray(notifications);
				((NotificationListAdapter) listNotifications.getAdapter()).notifyDataSetChanged();
					
			} else {
				
				//Do wee need to think about the listview?
				if( status.getVisibility() == View.GONE ) { status.setVisibility( View.VISIBLE); }
				if( listNotifications.getVisibility() != View.GONE ) { listNotifications.setVisibility( View.GONE ); }
				
			}

		}

			
		return;
		
	}	

}