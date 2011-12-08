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

package com.ninetwozero.battlelog;

import net.sf.andhsli.hotspotlogin.SimpleCrypto;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.asynctasks.AsyncLogin;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class Main extends Activity {

	//Fields
	private EditText fieldEmail, fieldPassword;
	private CheckBox checkboxSave;
	
	//Values
	private String[] valueFields;
	private PostData[] postDataArray;
	
	//SP
	private SharedPreferences sharedPreferences;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
    	//onCreate - save the instance state
    	super.onCreate(savedInstanceState);
    	
    	//Set the content view
        setContentView(R.layout.main);
        
        //Are we active?
        if( PublicUtils.isMyServiceRunning( this ) || WebsiteHandler.setActive() ) { startActivity( new Intent(this, Dashboard.class) ); }
        
        //Check if the default-file is ok
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        if( sharedPreferences.getInt( "file_version", 0) == 0 ) {
        	
	        //Get the sharedPreferences
        	SharedPreferences sharedPreferencesOld = getSharedPreferences( Constants.FILE_SHPREF, 0 );
        	SharedPreferences.Editor spEdit = sharedPreferences.edit();
        	
        	//Set it up
        	spEdit.putInt( "file_version", sharedPreferencesOld.getInt( "file_version", 0 )+1 );
        	spEdit.putInt( "latest_changelog_version", (int) sharedPreferencesOld.getLong("latest_changelog_version", 0 ) );
        	spEdit.putLong( "battlelog_platform_id", sharedPreferencesOld.getLong("battlelog_platform_id", 0 ) );
        	spEdit.putLong( "battlelog_profile_id", sharedPreferencesOld.getLong("battlelog_profile_id", 0 ) );
        	spEdit.putLong( "battlelog_persona_id", sharedPreferencesOld.getLong("battlelog_persona_id", 0 ) );
        	spEdit.putString( "origin_email", sharedPreferencesOld.getString("origin_email", "" ) );
        	spEdit.putString( "origin_password", sharedPreferencesOld.getString("origin_password", "" ) );
        	spEdit.putString( "battlelog_username", sharedPreferencesOld.getString("battlelog_username", "" ) );
        	spEdit.putString( "battlelog_post_checksum", sharedPreferencesOld.getString("battlelog_post_checksum", "" ) );
        	spEdit.putBoolean( "remember_password", sharedPreferencesOld.getBoolean( "remember_password", false ));
        	
        	//Commit!!
        	spEdit.commit();
        	
        }
        
        //Initialize the attributes
        postDataArray = new PostData[Constants.FIELD_NAMES_LOGIN.length];
        valueFields = new String[2];
        
        //Get the fields
        fieldEmail = (EditText) findViewById(R.id.field_email);
        fieldPassword = (EditText) findViewById(R.id.field_password);
        checkboxSave = (CheckBox) findViewById(R.id.checkbox_save);
        
        //Do we need to show the cool changelog-dialog?
        if( sharedPreferences.getInt( "latest_changelog_version", Constants.CHANGELOG_VERSION-1) < Constants.CHANGELOG_VERSION ) {
        	
        	createChangelogDialog().show();
        	
        }

        //Let's populate... or shall we not?
        if( sharedPreferences.contains( "origin_email" ) ) {
        	
        	//Set the e-mail field
        	fieldEmail.setText( sharedPreferences.getString( "origin_email", "") );

        	//Did the user want us to remember the password?
        	if( sharedPreferences.getBoolean( "remember_password", false) ) {
        		
        		//Do we have a password stored?
        		if( !sharedPreferences.getString( "origin_password", "").equals( "" ) ) {
	        		
	        		try {
	        		
	        			//Set the password (decrypted version)
						fieldPassword.setText( 
							SimpleCrypto.decrypt( 
								sharedPreferences.getString( 
									"origin_email", 
									"" 
								), 
								sharedPreferences.getString( 
									"origin_password", 
									""
								)
							) 
						);
						
						checkboxSave.setChecked( true );
					
	        		} catch ( Exception e ) {
						
	        			e.printStackTrace();
					
	        		}
        		
        		}	
        	
        	}
        }
        
	}
    
    public void onClick(View v) {
    	
    	if( v.getId() == R.id.button_login ) {
    		
    		//Let's set 'em values
    		valueFields[0] = fieldEmail.getText().toString();
    		valueFields[1] = fieldPassword.getText().toString();
    		
    		//Validate
    		if( valueFields[0].equals( "" ) || !valueFields[0].contains( "@" ) ) {
    			
    			Toast.makeText( this, "Invalid e-mail address.", Toast.LENGTH_SHORT).show();
    			return;
    			
    		} else if( valueFields[1].equals( "" ) ) {
    			
    			Toast.makeText( this, "Please enter your password.", Toast.LENGTH_SHORT).show();
    			return;
    			
    		}
    		
    		//Iterate and conquer
    		for( int i = 0; i < Constants.FIELD_NAMES_LOGIN.length; i++ ) {

    			postDataArray[i] =	new PostData(
	    			
    				Constants.FIELD_NAMES_LOGIN[i],
	    			(Constants.FIELD_VALUES_LOGIN[i] == null) ? valueFields[i] : Constants.FIELD_VALUES_LOGIN[i] 
	    		);
    		
    		}
    		
    		//Do the async
    		AsyncLogin al = new AsyncLogin(this, false, checkboxSave.isChecked());
    		al.execute( postDataArray );
    		
    		//Clear the pwd-field
    		if( !checkboxSave.isChecked() ) fieldPassword.setText( "" );
    		return;
    		
    	}
    	
    }
    
    public final Dialog createChangelogDialog() {
    		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	    final View layout = inflater.inflate(R.layout.changelog_dialog, (ViewGroup) findViewById(R.id.dialog_root));
		
	    //Set the title and the view
		builder.setTitle("Changelog version 1.0." + Constants.CHANGELOG_VERSION);
		builder.setView(layout);

		//Grab the fields
		final TextView textView = (TextView) layout.findViewById(R.id.text_changelog);
		textView.setText( Html.fromHtml( getResources().getString( R.string.changelog ) ) );
		
		//Set the button
		builder.setPositiveButton(
				
			android.R.string.ok, 
			new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
			      
					sharedPreferences.edit().putLong( "latest_changelog_version", Constants.CHANGELOG_VERSION).commit();
			   
				}
				
			}
			
		);
    		
		//CREATE
		return builder.create();
    	
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
    }
    
}