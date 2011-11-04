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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.ninetwozero.battlelog.Config;
import com.ninetwozero.battlelog.RequestHandler;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;


public class AsyncLogout extends AsyncTask<Void, Integer, Integer> {

	//Attribute
	Context context;
	String httpContent;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEdit;
	ProgressDialog progressDialog;
	
	//Constructor
	public AsyncLogout( Context c ) { 
		
		this.context = c; 
		this.sharedPreferences = this.context.getSharedPreferences( Config.fileSharedPrefs, 0);
		this.spEdit = this.sharedPreferences.edit();
	
	}	
	
	@Override
	protected void onPreExecute() {
		
		this.progressDialog = new ProgressDialog(this.context);
		this.progressDialog.setTitle("Please wait");
		this.progressDialog.setMessage( "Logging out..." );
		this.progressDialog.show();
		
	}
	
	@Override
	protected Integer doInBackground( Void... arg0 ) {
		
		try {
		
    		//Let's login everybody!
			RequestHandler wh = new RequestHandler();
    		httpContent = wh.get( Config.urlLogout );

    		//Did we manage?
    		if( httpContent != null && !httpContent.equals( "" ) ) {
    			
				//Further more, we would actually like to store the userid and name
				spEdit.putString( "battlelog_persona",  "");
				spEdit.putString( "battlelog_personaid",  "");
				spEdit.putString( "battlelog_platform", "");
				
				//Co-co-co-commit
				spEdit.commit();
				
    		}
    		wh.close();
    		
		} catch ( RequestHandlerException ex ) {
			
			Log.e("com.ninetwozero.battlelog", "", ex);
			return 1;
		}

		return 0;
		
	}
	
	@Override
	protected void onPostExecute(Integer results) {
		
		if( this.progressDialog != null && this.progressDialog.isShowing() ) {
			
			this.progressDialog.dismiss();
			((Activity)this.context).finish();
			
		}
		
	}

	
	
}
