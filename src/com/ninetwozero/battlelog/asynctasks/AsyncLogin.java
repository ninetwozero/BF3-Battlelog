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

import com.ninetwozero.battlelog.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.battlelog.Dashboard;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncLogin extends AsyncTask<PostData, Integer, Boolean> {

	//Attribute
	ProgressDialog progressDialog;
	Context context;
	boolean fromWidget;
	boolean savePassword;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEdit;
	ProfileData profile;
	
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
			this.progressDialog.setTitle(context.getString( R.string.general_wait ));
			this.progressDialog.setMessage( context.getString( R.string.msg_logging_in ) );
			this.progressDialog.setCancelable( false );
			this.progressDialog.show();
		
		}
		
	}
	
	@Override
	protected Boolean doInBackground( PostData... arg0 ) {
		
		try {
		
			profile = WebsiteHandler.doLogin( context, arg0, savePassword );
			
			return (profile != null)? true : false;
			
		} catch ( WebsiteHandlerException e ) {
			
			return false;
			
		}
		
	}
	
	@Override
	protected void onPostExecute(Boolean results) {

		if( !fromWidget ) {
			
			if( this.progressDialog != null ) { this.progressDialog.dismiss(); }
			
			if( results ) { 

				//Start the activity
				if( PublicUtils.isMyServiceRunning( context ) ) {
					
					((Activity)context).finish();
					
				}
				
				this.context.startActivity( 
						
					new Intent(context, Dashboard.class).putExtra( 
							
						"myProfile", 
						profile
						
					)
					
				); 
				
			} else {
				
				Toast.makeText( this.context, R.string.msg_login_fail, Toast.LENGTH_SHORT).show(); 
				
			}
			
		}
		return;
		
	}	
	
}
