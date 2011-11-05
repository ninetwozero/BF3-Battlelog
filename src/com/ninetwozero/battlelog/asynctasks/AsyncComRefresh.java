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
import java.util.Collections;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.Config;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.WebsiteHandler;
import com.ninetwozero.battlelog.adapters.FriendListAdapter;
import com.ninetwozero.battlelog.adapters.RequestListAdapter;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;

public class AsyncComRefresh extends AsyncTask<Void, Integer, Boolean> {

	//Attribute
	Context context;
	SharedPreferences sharedPreferences;
	ArrayList<ArrayList<ProfileData>> profileArray = new ArrayList<ArrayList<ProfileData>>();
	ListView listRequests, listFriends;
	LayoutInflater layoutInflater;
	
	//Constructor
	public AsyncComRefresh( Context c, ListView r, ListView f, LayoutInflater l ) { 
		
		this.context = c;
		this.listRequests = r;
		this.listFriends = f;
		this.layoutInflater = l;
		this.sharedPreferences = context.getSharedPreferences(Config.fileSharedPrefs, 0);
		
	}	
	
	@Override
	protected void onPreExecute() {
		
		//Let's see
		Toast.makeText( context, "Updating the COM CENTER", Toast.LENGTH_SHORT).show();
		
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

		//Fill the listviews!!
		if( profileArray.size() > 0 ) {
			
			if( profileArray.get( 0 ) == null || profileArray.get( 0 ).size() == 0 ) {
			
				((Activity)context).findViewById(R.id.wrap_requests).setVisibility( View.GONE );
			
			} else {
				
				//Set the adapter
				listRequests.setAdapter( new RequestListAdapter(context, profileArray.get(0), layoutInflater) );
				
			}
			
			if( profileArray.get( 1 ) == null || profileArray.get( 1 ).size() > 0 ) {
				
				//Set the adapter
				listFriends.setAdapter( new FriendListAdapter(context, profileArray.get(1), layoutInflater) );
				
				
			} else {
				
				//No friends found :-(
				
			}
		
		} else {
			
			results = false;
			
		}
		
		//How did go?
		if( results ) Toast.makeText( context, "COM CENTER updated.", Toast.LENGTH_SHORT).show();
		else Toast.makeText( context, "COM CENTER could not be updated.", Toast.LENGTH_SHORT).show();				
		return;
		
	}	

}