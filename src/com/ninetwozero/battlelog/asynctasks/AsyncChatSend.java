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
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class AsyncChatSend extends AsyncTask<String, Integer, Boolean> {

	//Attribute
	Context context;
	Button buttonSend;
	long profileId;
	boolean fromWidget;
	String httpContent;
	
	
	//Constructor
	public AsyncChatSend( Context c, long p, Button b, boolean w ) { 
		
		this.context = c; 
		this.profileId = p;
		this.fromWidget = w;
		this.buttonSend = b;
	}	
	
	@Override
	protected void onPreExecute() {
	
		//Let's see
		if( !fromWidget ) {
	
			buttonSend.setEnabled( false );
			buttonSend.setText( "Sending..." );
			
		}
		
	}
	
	@Override
	protected Boolean doInBackground( String... arg0 ) {
		
		try {
		
    		//Did we manage?
    		if( WebsiteHandler.sendChatMessages(profileId, arg0[0], arg0[1]) ) { return true; } 
    		else { return false; }
    		
		} catch( Exception ex ) {
			
			Log.e(Constants.debugTag, "", ex);
			return false;
			
		}
		
	}
	
	@Override
	protected void onPostExecute(Boolean results) {

		if( !fromWidget ) {
			
			buttonSend.setEnabled( true );
			buttonSend.setText( "Send" );
			
			if( results ) Toast.makeText( context, "Message sent!", Toast.LENGTH_SHORT).show();
			else Toast.makeText( context, "Message could not be sent!", Toast.LENGTH_SHORT).show();
			
		}
		return;
		
	}

	
	
}
