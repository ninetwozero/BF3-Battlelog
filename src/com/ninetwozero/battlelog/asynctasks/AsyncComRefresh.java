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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapters.FriendListAdapter;
import com.ninetwozero.battlelog.adapters.RequestListAdapter;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncComRefresh extends AsyncTask<Void, Integer, Boolean> {

	//Attribute
	Context context;
	SharedPreferences sharedPreferences;
	ArrayList<ArrayList<ProfileData>> profileArray = new ArrayList<ArrayList<ProfileData>>();
	ListView listRequests, listFriendsOnline, listFriendsOffline;
	LayoutInflater layoutInflater;
	Button buttonRefresh;
	TextView drawerHandle;
	
	//Constructor
	public AsyncComRefresh( Context c, ListView r, ListView fon, ListView fof, LayoutInflater l, Button b, TextView t ) { 
		
		this.context = c;
		this.listRequests = r;
		this.listFriendsOnline = fon;
		this.listFriendsOffline = fof;
		this.layoutInflater = l;
		this.sharedPreferences = context.getSharedPreferences(Constants.fileSharedPrefs, 0);
		this.buttonRefresh = b;
		this.drawerHandle = t;
	
	}	
	
	@Override
	protected void onPreExecute() {
		
		this.buttonRefresh.setEnabled(false);
		this.buttonRefresh.setText( "Please wait..." );
		
	}
	
	@Override
	protected Boolean doInBackground( Void... arg0) {
		
		try {
		
			//Let's get this!!
			profileArray = WebsiteHandler.getFriendsCOM( sharedPreferences.getString( "battlelog_post_checksum", "") );
			return true;
			
		} catch ( WebsiteHandlerException e ) {
			
			return false;
			
		}
		
	}
	
	@Override
	protected void onPostExecute(Boolean results) {

		//Boolean
		int emptyLists = 0;
		int numOnlineFriends = 0;
		
		//Fill the listviews!!
		if( profileArray.size() > 0 ) {
			
			if( profileArray.get( 0 ) == null || profileArray.get( 0 ).size() == 0 ) {
			
				((Activity)context).findViewById(R.id.wrap_friends_requests).setVisibility( View.GONE );
			
			} else {
				
				//VISIBILITY!!!
				((Activity)context).findViewById(R.id.wrap_friends_requests).setVisibility( View.VISIBLE );
				
				//Set the adapter
				listRequests.setAdapter( new RequestListAdapter(context, profileArray.get(0), layoutInflater) );
				
			}

			if( profileArray.get( 1 ).size() > 0 || profileArray.get( 2 ).size() > 0 ) {

				//Set the visibility (could've been hidden)
				((Activity)context).findViewById(R.id.list_friends).setVisibility( View.VISIBLE );
				
				//Create a copy so that we can merge later on
				ArrayList<ProfileData> mergedArray = profileArray.get( 1 );
				
				//...but first we want the lenght, oorah!
				numOnlineFriends = mergedArray.size()-1;
				
				//...and now we can merge 'em!
				mergedArray.addAll( profileArray.get(2) );
				
				//Set the adapter
				listFriendsOnline.setAdapter( new FriendListAdapter(context, mergedArray, layoutInflater) );
				
				
			} else {
				
				//No friends found :-(
				((Activity)context).findViewById(R.id.list_friends).setVisibility( View.GONE );
				
			}
			
			//Update the sliding drawer handle
			drawerHandle.setText( "COM CENTER (" + numOnlineFriends + " ONLINE)" ); //-1 to compensate for the header
		
		} else {
			
			results = false;
			
		}
		

		//Update the button y'all
		this.buttonRefresh.setEnabled(true);
		this.buttonRefresh.setText( "Refresh now" );
		
		//How did go?
		if( results ) Toast.makeText( context, "COM CENTER up to date.", Toast.LENGTH_SHORT).show();
		else Toast.makeText( context, "COM CENTER could not be refreshed.", Toast.LENGTH_SHORT).show();				
		return;
		
	}	

}