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

import net.sf.andhsli.hotspotlogin.SimpleCrypto;

import com.ninetwozero.battlelog.Config;
import com.ninetwozero.battlelog.Dashboard;
import com.ninetwozero.battlelog.RequestHandler;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


public class AsyncLogin extends AsyncTask<PostData, Integer, Integer> {

	//Attribute
	ProgressDialog progressDialog;
	Context context;
	boolean fromWidget;
	boolean savePassword;
	String httpContent;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEdit;
	
	//Constructor
	public AsyncLogin( Context c, boolean w ) { 
		
		this.context = c; 
		this.sharedPreferences = this.context.getSharedPreferences( Config.fileSharedPrefs, 0);
		this.spEdit = this.sharedPreferences.edit();
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
			this.progressDialog.show();
		
		}
	}
	
	@Override
	protected Integer doInBackground( PostData... arg0 ) {
		
		try {
		
			//TEMP
			String[] tempString = new String[10];
			
    		//Let's login everybody!
			RequestHandler wh = new RequestHandler();
    		httpContent = wh.post( Config.urlLogin, arg0 );

    		//Did we manage?
    		if( httpContent != null && !httpContent.equals( "" ) ) {
    			
    			//Set the int
    			int startPosition = httpContent.indexOf( Config.elementUIDLink );
    			String platformId;
    			String[] bits;
    			
    			
    			//Did we find it?
    			if( startPosition == -1 ) {
    				
    				return 1;

    			}
    			
    			//Cut out the appropriate bits (<a class="SOME CLASS HERE" href="A LONG LINK HERE">NINETWOZERO
	    		httpContent = httpContent.substring( startPosition );
				tempString[0] = httpContent.substring( 0, httpContent.indexOf("\">") ); 
				bits = TextUtils.split( tempString[0].replace( Config.elementUIDLink, ""), "/");
				
				//Get the checksum
				tempString[1] = httpContent.substring( httpContent.indexOf( Config.elementStatusChecksumLink ) );
				tempString[1] = tempString[1].substring( 0, tempString[1].indexOf( "\" />") ).replace( Config.elementStatusChecksumLink, "" );
				
				//Further more, we would actually like to store the userid and name
				spEdit.putString( "origin_email", arg0[0].getValue() );
				
				//Should we remember the password?
				if( savePassword ) {

					spEdit.putString( "origin_password", SimpleCrypto.encrypt( arg0[0].getValue(), arg0[1].getValue() ) );
					spEdit.putBoolean( "remember_password", true );
					
				} else {
					
					spEdit.putString( "origin_password", "" );
					spEdit.putBoolean( "remember_password", false);
				}
				
				spEdit.putString( "battlelog_persona",  bits[0]);
				spEdit.putLong( "battlelog_persona_id",  Long.parseLong( bits[2] ));				
				spEdit.putLong( "battlelog_platform_id",  Config.getPlatformId(bits[3]) );
				spEdit.putString( "battlelog_post_checksum", tempString[1]);
				
				//Co-co-co-commit
				spEdit.commit();
				
    		}
    		
		} catch ( RequestHandlerException ex ) {
			
			Log.e("com.ninetwozero.battlelog", "", ex);
			return 1;
		
		} catch( Exception ex ) {
			
			Log.e("com.ninetwozero.battlelog", "", ex);
			return 1;
			
		}

		return 0;
		
	}
	
	@Override
	protected void onPostExecute(Integer results) {

		if( !fromWidget ) {
			
			if( this.progressDialog != null ) { this.progressDialog.dismiss(); }
			
			if( results == 0 ) { this.context.startActivity( new Intent(this.context, Dashboard.class) ); }
			else { Toast.makeText( this.context, "Login failed.", Toast.LENGTH_SHORT).show(); }
			
		}
		return;
		
	}

	
	
}
