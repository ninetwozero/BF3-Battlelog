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
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class AsyncFriendRequest extends AsyncTask<String, Integer, Boolean> {

	//Attribute
	Context context;
	long profileId;
	String httpContent;
	
	//Constructor
	public AsyncFriendRequest( Context c, long pId) { 
		
		this.context = c;
		this.profileId = pId;
		
	}	
	
	@Override
	protected void onPreExecute() { }
	
	@Override
	protected Boolean doInBackground( String... arg0 ) {
		
		try {
			
    		return WebsiteHandler.sendFriendRequest( profileId, arg0[0] );
    		
		} catch( Exception ex ) {
			
			ex.printStackTrace();
			return false;
			
		}
		
	}
	
	@Override
	protected void onPostExecute(Boolean results) {
		
		if( results ) { 
			
			Toast.makeText(this.context, "Friend request sent!", Toast.LENGTH_SHORT).show(); 
			
		} else { 
			
			Toast.makeText( this.context, "Friend request could not be sent.", Toast.LENGTH_SHORT).show(); 
			
		}

		return;
		
	}	
	
}
