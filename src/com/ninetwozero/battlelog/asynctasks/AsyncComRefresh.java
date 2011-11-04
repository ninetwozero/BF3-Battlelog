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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.battlelog.WebsiteHandler;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;

public class AsyncComRefresh extends AsyncTask<PostData, Integer, Boolean> {

	//Attribute
	ProgressDialog progressDialog;
	Context context;
	boolean fromWidget;
	boolean savePassword;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEdit;
	
	//Constructor
	public AsyncComRefresh( Context c ) { 
		
		this.context = c;
	
	}	
	
	@Override
	protected void onPreExecute() {
		
		//Let's see
		if( !fromWidget ) {
		
			Toast.makeText( context, "Updating the COM", Toast.LENGTH_SHORT).show();
		
		}
		
	}
	
	@Override
	protected Boolean doInBackground( PostData... arg0 ) {
		
		try {
		
			return WebsiteHandler.doLogin( context, arg0, savePassword );
		
		} catch ( WebsiteHandlerException e ) {
			
			return false;
			
		}
		
	}
	
	@Override
	protected void onPostExecute(Boolean results) {

		if( !fromWidget ) {
			
			if( results ) Toast.makeText( context, "COM updated.", Toast.LENGTH_SHORT).show();
			else Toast.makeText( context, "COM could not be updated.", Toast.LENGTH_SHORT).show();
				
		}
		return;
		
	}	
	
}
