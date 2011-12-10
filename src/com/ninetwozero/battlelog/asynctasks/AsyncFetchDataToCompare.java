package com.ninetwozero.battlelog.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ninetwozero.battlelog.CompareView;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class AsyncFetchDataToCompare extends AsyncTask<String, Void, Boolean> {
    
	//Context
	private Context context;
	private Activity origin;
	private SharedPreferences sharedPreferences; 
	//Data
	ProfileData userData;
	
	//Error message
	private String error;
	
	public AsyncFetchDataToCompare(Context c) {
		
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
				sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ) 
			
			);

			//Did we get an actual user?
			if( userData == null || userData.getPersonaId() == 0 ) { 
				
				//Persona
				error = context.getString( R.string.msg_search_nouser ).replace( "{keyword}", searchString );
				return false; 
				
			}
				
		} catch(Exception ex) {
			
			//D'oh	
			error = context.getString( R.string.msg_search_nouser ).replace( "{keyword}", searchString );
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
					CompareView.class
					
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