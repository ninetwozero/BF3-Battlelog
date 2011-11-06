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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.battlelog.Dashboard;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncLogin extends AsyncTask<PostData, Integer, Boolean> {

	//Attribute
	ProgressDialog progressDialog;
	Context context;
	boolean fromWidget;
	boolean savePassword;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEdit;
	
	//Constructor
	public AsyncLogin( Context c, boolean w ) { 
		
		this.context = c; 
		this.fromWidget = w;
	
	}	
	
	//Constructor
	public AsyncLogin( Context c, boolean w, boolean s) { 
		
		this(c, w);
		this.savePassword = s;
		
	}	
	
	@Override
	protected void onPreExecute() {
	
		
		//Let's see
		if( !fromWidget ) {
		
			this.progressDialog = new ProgressDialog(this.context);
			this.progressDialog.setTitle("Please wait");
			this.progressDialog.setMessage( "Logging in..." );
			this.progressDialog.setCancelable( false );
			this.progressDialog.show();
		
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
			
			if( this.progressDialog != null ) { this.progressDialog.dismiss(); }
			
			if( results ) { this.context.startActivity( new Intent(this.context, Dashboard.class) ); }
			else { Toast.makeText( this.context, "Login failed.", Toast.LENGTH_SHORT).show(); }
			
		}
		return;
		
	}	
	
}
