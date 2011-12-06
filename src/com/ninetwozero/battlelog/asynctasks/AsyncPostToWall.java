package com.ninetwozero.battlelog.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.battlelog.PlatoonView;
import com.ninetwozero.battlelog.ProfileView;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class AsyncPostToWall extends AsyncTask<String, Void, Boolean> {
    
	//Context
	private Context context;
	private Activity origin;
	private SharedPreferences sharedPreferences;
	private boolean isPlatoon;
	
	//Data
	private long profileId;
	
	//Error message
	private String error;
	
	public AsyncPostToWall(Context c, long pId, boolean p) {
		
		context = c;
		origin = (Activity) context;
		sharedPreferences = context.getSharedPreferences( Constants.fileSharedPrefs, 0 );
		profileId = pId;
		isPlatoon = p;
		
	}
	
	@Override
	protected void onPreExecute() {}

	@Override
	protected Boolean doInBackground( String... arg0 ) {
		
		try {
				
			return WebsiteHandler.postToWall(profileId, arg0[0], arg0[1], isPlatoon);
				
		} catch(Exception ex) {
			
			//D'oh	
			ex.printStackTrace();
			return false;
		
		}

	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		
		//How'd it go?
		if( result ) {

			Toast.makeText( context, "Message posted successfully!", Toast.LENGTH_SHORT).show();
			
			try { wait( 1000 ); } catch( Exception ex ) { ex.printStackTrace(); }
			if( !isPlatoon ) { ((ProfileView) context).reloadLayout(); }
			else { ((PlatoonView) context).reloadLayout(); }
		
		} else {
		

			Toast.makeText( context, "Message could not be posted (you may have reached your limit).", Toast.LENGTH_SHORT).show();
			
		}
		
	}
	
}