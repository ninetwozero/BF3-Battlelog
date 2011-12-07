package com.ninetwozero.battlelog.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ninetwozero.battlelog.ProfileView;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class AsyncFetchDataToProfileView extends AsyncTask<String, Void, Boolean> {
    
	//Context
	private Context context;
	private Activity origin;
	private SharedPreferences sharedPreferences;
	
	//Data
	ProfileData userData;
	
	//Error message
	private String error;
	
	public AsyncFetchDataToProfileView(Context c) {
		
		context = c;
		origin = (Activity) context;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
		
		userData = null;
		error = "";
		
	}
	
	@Override
	protected void onPreExecute() {}

	@Override
	protected Boolean doInBackground( String... arg0 ) {
		
			
		//We got to do it the hard way - let's see if we can find a user with that name.
		String searchString = arg0[0];

		try {
				
			//Post the world!
			userData = WebsiteHandler.getProfileIdFromSearch(
				
				searchString, 
				sharedPreferences.getString( "battlelog_post_checksum", "" ) 
			
			);

			//Did we get an actual user?
			if( userData == null || userData.getPersonaId() == 0 ) { 
				
				//Persona
				error = "No user found matching the following keyword: " + searchString;
				return false; 
				
			}
				
		} catch(Exception ex) {
			
			//D'oh	
			error = "No user found matching the following keyword: " + searchString;
			return false;
		
		}

		return true;

	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		
		//How copy?
		if( result ) {
		
			//To the Batmobile!
			context.startActivity(
				
				new Intent(
					
					context,
					ProfileView.class
					
				).putExtra(
						
					"profile", 
					userData	
				
				)
				
			);
			
		} else {
			
			Toast.makeText( context, error, Toast.LENGTH_SHORT ).show();
			
		}
		
		return;
	}
	
}