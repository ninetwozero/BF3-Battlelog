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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.Config;
import com.ninetwozero.battlelog.WebsiteHandler;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;

public class AsyncComRefresh extends AsyncTask<Void, Integer, Boolean> {

	//Attribute
	Context context;
	SharedPreferences sharedPreferences;
	ArrayList<ArrayList<ProfileData>> profileArray = new ArrayList<ArrayList<ProfileData>>();
	ListView listRequests, listFriends;
	
	//Constructor
	public AsyncComRefresh( Context c, ListView r, ListView f ) { 
		
		this.context = c;
		this.listRequests = r;
		this.listFriends = f;
		this.sharedPreferences = context.getSharedPreferences(Config.fileSharedPrefs, 0);
		
	}	
	
	@Override
	protected void onPreExecute() {
		
		//Let's see
		Toast.makeText( context, "Updating the COM", Toast.LENGTH_SHORT).show();
		
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
		
		if( results ) Toast.makeText( context, "COM updated.", Toast.LENGTH_SHORT).show();
		else Toast.makeText( context, "COM could not be updated.", Toast.LENGTH_SHORT).show();				
		return;
		
	}	
	
}
