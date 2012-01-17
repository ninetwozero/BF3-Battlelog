package com.ninetwozero.battlelog.asynctasks;

import com.ninetwozero.battlelog.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ninetwozero.battlelog.CompareView;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class AsyncFetchDataToCompare extends AsyncTask<String, Void, Boolean> {
    
	//Context
	private Context context;
	private Activity origin;
	private SharedPreferences sharedPreferences; 
	
	//Data
	private ProfileData playerOne, playerTwo;
	//Error message
	private String error;
	
	public AsyncFetchDataToCompare(Context c, ProfileData p) {
		
		context = c;
		origin = (Activity) context;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
		
		playerOne = p;
		playerTwo = null;
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
			playerTwo = WebsiteHandler.getProfileIdFromSearch(
				
				searchString, 
				sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ) 
			
			);

			//Did we get an actual user?
			if( playerTwo == null || playerTwo.getPersonaId() == 0 ) { 
				
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
					
					"profile1",
					playerOne
						
				).putExtra(
						
					"profile2", 
					playerTwo	
				
				)
				
			);
			
		} else {
			
			Toast.makeText( context, error, Toast.LENGTH_SHORT ).show();
			
		}
		
		return;
	}
	
}