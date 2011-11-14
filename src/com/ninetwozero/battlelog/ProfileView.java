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

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.FeedListAdapter;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ProfileInformation;
import com.ninetwozero.battlelog.datatypes.SerializedCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class ProfileView extends ListActivity {

	//Attributes
	private SharedPreferences sharedPreferences;
	private ProgressBar progressBar;
	private ProfileData profileData;
	private LayoutInflater layoutInflater;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( "serializedCookies" ) ) {
    		
    		RequestHandler.setSerializedCookies( (ArrayList<SerializedCookie> ) icicle.getSerializable("serializedCookies") );
    	
    	}
    	
    	//Set the content view
        setContentView(R.layout.profile_view);

        //Prepare to tango
        this.sharedPreferences = this.getSharedPreferences( Constants.fileSharedPrefs, 0);
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        //Get the intent
        if( !getIntent().hasExtra( "profile" ) ) {
        	
        	profileData = new ProfileData(
				
    			this.sharedPreferences.getString( "battlelog_persona", "" ),
				this.sharedPreferences.getLong( "battlelog_persona_id", 0 ),
				this.sharedPreferences.getLong( "battlelog_persona_id", 0 ),
				this.sharedPreferences.getLong( "battlelog_platform_id", 1)
			
			);
        	
        } else {
        	
        	profileData = (ProfileData) getIntent().getSerializableExtra( "profile" );
        	
        }

		Log.d(Constants.debugTag, profileData.toString());
        initLayout();
	}        

	public void initLayout() {
		
		//Eventually get a *cached* version instead
		reloadLayout();
		
	}
	
    public void reloadLayout() {
    	
    	//ASYNC!!!
    	new GetDataSelfAsync(this).execute(
    		
    		profileData

		);
    	
    	
    }
    
    public void doFinish() {}
    
    private class GetDataSelfAsync extends AsyncTask<ProfileData, Void, Boolean> {
    
    	//Attributes
    	Context context;
    	ProgressDialog progressDialog;
    	ProfileInformation profileInformation;
    	
    	public GetDataSelfAsync(Context c) {
    		
    		this.context = c;
    		this.progressDialog = null;
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		
    		//Let's see
			this.progressDialog = new ProgressDialog(this.context);
			this.progressDialog.setTitle("Please wait");
			this.progressDialog.setMessage( "Downloading the data..." );
			this.progressDialog.show();
    		
    	}
    	

		@Override
		protected Boolean doInBackground( ProfileData... arg0 ) {
			
			try {
				
				this.profileInformation = WebsiteHandler.getProfileInformationForUser( arg0[0] );
				return true;
				
			} catch ( WebsiteHandlerException ex ) {
				
				Log.d(Constants.debugTag, ex.getMessage() );
				return false;
				
			}

		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		
			//Fail?
			if( !result ) { 
				
				if( this.progressDialog != null ) this.progressDialog.dismiss();
				Toast.makeText( this.context, "No data found.", Toast.LENGTH_SHORT).show(); 
				((Activity) this.context).finish();
				return; 
			
			}

			//Assign values
			drawLayout(this.profileInformation);
			
			//Done!
	        if( this.progressDialog != null ) this.progressDialog.dismiss();
			return;
		}
    	
    }
    
    public final void drawLayout(ProfileInformation data) {
    	
    	//Let's start drawing the... layout
    	((TextView) findViewById(R.id.text_username)).setText( data.getUsername() );

    	//When did was the users last login?
    	if( data.isPlaying() && data.isOnline() ) { 
    		
    		((TextView) findViewById(R.id.text_online)).setText( 
    				
				"Playing on {SERVER_NAME}".replace(
					
					"{SERVER_NAME}",
					data.getCurrentServer()
					
				)
    				
			); 
        	
    	} else if( data.isOnline() ) {
    	
    		((TextView) findViewById(R.id.text_online)).setText( "Online but not currently playing" ); 
    		
    	} else {
    		
    		((TextView) findViewById(R.id.text_online)).setText( data.getLastLogin() ); 
    		
    	}
    	
    	//Is the status ""?
    	if( !data.getStatusMessage().equals( "" ) ) {
			
    		//Set the status
    		((TextView) findViewById(R.id.text_status)).setText( data.getStatusMessage() );
        	//((TextView) findViewById(R.id.text_online)).setText( PublicUtils.getRelativeDate( data.getStatusMessageChanged(), "Last updated ") );

    	
    	} else {
    		
    		//Hide the view
    		((TextView) findViewById(R.id.wrap_status)).setVisibility(View.GONE);
    		
    	}
    	
    	//Do we have a presentation?
    	if( !data.getPresentation().equals( "" ) ) {
    		
    		((TextView) findViewById(R.id.text_presentation)).setText( data.getPresentation() );
		
    	} else {
    		
    		((TextView) findViewById(R.id.wrap_presentation)).setVisibility(View.GONE);
	
    	}
    		
    	//Set the username
    	((TextView) findViewById(R.id.text_username)).setText( data.getUsername() );
    	((TextView) findViewById(R.id.text_username)).setText( data.getUsername() );
    	getListView().setAdapter( 
    			
			new FeedListAdapter(this, data.getFeedItems(), layoutInflater) 
			
		);
    }
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_profileview, menu );
		return super.onCreateOptionsMenu( menu );
	
    }
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		//Let's act!
		if( item.getItemId() == R.id.option_reload ) {
	
			this.reloadLayout();
			
		} else if( item.getItemId() == R.id.option_back ) {
			
			((Activity) this).finish();
			
		}
		
		// Return true yo
		return true;

	}  
    
	@Override
    public void onListItemClick(ListView l, View v, int p, long i) {
		
		//Do we have content to display?
		if( !((FeedItem) l.getItemAtPosition( p ) ).getContent().equals( "" ) ) {
			
			View viewContainer = (View) v.findViewById(R.id.text_content);
			viewContainer.setVisibility( ( viewContainer.getVisibility() == View.GONE ) ? View.VISIBLE : View.GONE );
		
		}
		
		return;
    	
    }	
	
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
    }  
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putSerializable("serializedCookies", RequestHandler.getSerializedCookies());
	
	}
}