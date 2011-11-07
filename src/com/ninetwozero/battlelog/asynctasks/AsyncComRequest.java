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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncComRequest extends AsyncTask<Boolean, Integer, Boolean> {

	//Attribute
	Context context;
	SharedPreferences sharedPreferences;
	long profileId;
	AsyncComRefresh refreshMethod;

	//Constructor
	public AsyncComRequest( Context c, long p, AsyncComRefresh acr ) { 
		
		this.context = c;
		this.profileId = p;
		this.sharedPreferences = context.getSharedPreferences( "battlelog", 0 );
		this.refreshMethod = acr;
	}	
	
	@Override
	protected void onPreExecute() {
		
		//Let's see
		Toast.makeText( context, "Updating the COM CENTER", Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	protected Boolean doInBackground( Boolean... arg0) {
		
		try {
		
			//Let's get this!!
			return WebsiteHandler.answerFriendRequest( profileId, arg0[0], sharedPreferences.getString( "battlelog_post_checksum", "") );
			
			
		} catch ( WebsiteHandlerException e ) {
			
			return false;
			
		}
		
	}
	
	@Override
	protected void onPostExecute(Boolean results) {
		
		//Let the user know and then refresh!
		Toast.makeText( context, "Friend request responded to.", Toast.LENGTH_SHORT).show();				
		refreshMethod.execute();
		return;
		
	}	

}