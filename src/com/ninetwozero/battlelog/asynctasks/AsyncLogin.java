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
import com.ninetwozero.battlelog.ViewPagerDashboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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
	private ProgressDialog progressDialog;
	private Context context;
	private AsyncLogin origin;
	private boolean savePassword;
	private ProfileData profile;
	private String locale;
	
	//Constructor
	public AsyncLogin( Context c ) { 

		this.origin = this;
		this.context = c; 
	
	}	
	
	//Constructor
	public AsyncLogin( Context c, boolean s) { 
		
		this(c);
		this.savePassword = s;
		
	}	
	
	@Override
	protected void onPreExecute() {
	
		
		//Let's see
		this.progressDialog = new ProgressDialog(this.context);
		this.progressDialog.setTitle(context.getString( R.string.general_wait ));
		this.progressDialog.setMessage( context.getString( R.string.msg_logging_in ) );
		this.progressDialog.setOnCancelListener( 
				
			new OnCancelListener() {

				@Override
				public void onCancel( DialogInterface dialog ) {

					origin.cancel( true );
					dialog.dismiss();
					
					
				}}
		
		);
		this.progressDialog.show();
		
	}
	
	@Override
	protected Boolean doInBackground( PostData... arg0 ) {
		
		try {
		
			profile = WebsiteHandler.doLogin( context, arg0, savePassword );
			
			//Did it go ok?
			return (profile != null );
			
		} catch ( WebsiteHandlerException e ) {
			
			return false;
			
		}
		
	}
	
	@Override
	protected void onPostExecute(Boolean results) {

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
					
				).putExtra(
						
					"myLocale", 
					locale
					
				)
				
			); 
			
		} else {
			
			Toast.makeText( this.context, R.string.msg_login_fail, Toast.LENGTH_SHORT).show(); 
			
		}
		return;
		
	}	
	
}
