package com.ninetwozero.battlelog.asynctasks;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Spinner;

import com.ninetwozero.battlelog.adapters.FriendSpinnerAdapter;
import com.ninetwozero.battlelog.datatypes.Constants;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class AsyncFriendListSpinner extends AsyncTask<String, Void, Boolean> {
    
	//Attributes
	Context context;
	Spinner friendSpinner;
	ArrayList<ProfileData> profileArray;
	
	public AsyncFriendListSpinner(Context c, Spinner s) {
		
		this.context = c;
		this.friendSpinner = s;
		this.profileArray = new ArrayList<ProfileData>();

	}
	
	@Override
	protected void onPreExecute() {}
	

	@Override
	protected Boolean doInBackground( String... arg0 ) {
		
		try {
			
			this.profileArray = WebsiteHandler.getFriends(arg0[0], false);
			return true;
			
		} catch ( WebsiteHandlerException ex ) {
			
			Log.d(Constants.debugTag, ex.getMessage() );
			return false;
			
		}

	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		
		//How copy?
		if( result ) {
		
			//The adapter
			FriendSpinnerAdapter fsAdapter = new FriendSpinnerAdapter(
					
				context, 
				profileArray,
				(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
				
			);
			
			//Set the adapter
			friendSpinner.setAdapter( fsAdapter );
		
			//Enable it!
			friendSpinner.setEnabled( true );
			
		}
		
		return;
	}
	
}