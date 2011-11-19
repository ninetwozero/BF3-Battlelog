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


public class AsyncCommentSend extends AsyncTask<String, Integer, Boolean> {

	//Attribute
	Context context;
	Button buttonSend;
	long postId;
	boolean fromWidget;
	String httpContent;
	AsyncCommentsRefresh asyncCommentsRefresh;
	
	
	//Constructor
	public AsyncCommentSend( Context c, long pId, Button b, boolean w, AsyncCommentsRefresh acr ) { 
		
		this.context = c; 
		this.postId = pId;
		this.fromWidget = w;
		this.buttonSend = b;
		this.asyncCommentsRefresh = acr;
	
	}
	
	@Override
	protected void onPreExecute() {
		
		//Set the button
		buttonSend.setEnabled( false );
		buttonSend.setText( "Please wait..." );
		
	}
	
	@Override
	protected Boolean doInBackground( String... arg0 ) {
		
		try {
		
    		//Did we manage?
    		if( WebsiteHandler.commentOnFeedPost( postId, arg0[0], arg0[1]) ) { return true; } 
    		else { return false; }
    		
		} catch( Exception ex ) {
			
			Log.e(Constants.debugTag, "", ex);
			return false;
			
		}
		
	}
	
	@Override
	protected void onPostExecute(Boolean results) {

		if( !fromWidget ) {
			
			//Reload
			asyncCommentsRefresh.execute( postId );
			
			//Set the button
			buttonSend.setEnabled( true );
			buttonSend.setText( "Send" );
			
			if( results ) Toast.makeText( context, "Comment posted!", Toast.LENGTH_SHORT).show();
			else Toast.makeText( context, "Comment could not be posted!", Toast.LENGTH_SHORT).show();
			
		}
		return;
		
	}

	
	
}
