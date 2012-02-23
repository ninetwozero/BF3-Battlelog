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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.ForumSearchAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncChatRefresh;
import com.ninetwozero.battlelog.asynctasks.AsyncSessionValidate;
import com.ninetwozero.battlelog.datatypes.Board;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class ForumReportView extends Activity {

	//Attributes
	private LayoutInflater layoutInflater;
	private SharedPreferences sharedPreferences;
	
	//Elements
	private ListView listView;
	private EditText fieldReport;
	private Button buttonReport;
	
	//Misc
	private long postId;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		ArrayList<ShareableCookie> shareableCookies = icicle.getParcelableArrayList(Constants.SUPER_COOKIES);
			
    		if( shareableCookies != null ) { 
    			
    			RequestHandler.setCookies( shareableCookies );
    		
    		} else {
    			
    			finish();
    			
    		}
    		
    	}
    	
        //Do we have a postId?
    	postId = getIntent().getLongExtra( "postId", 0 );
    	if( postId == 0 ) { return; }
    	
    	//Set the content view
        setContentView(R.layout.forum_report_view);
        
        //Prepare to tango
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        
        //Get the elements
        buttonReport = (Button) findViewById(R.id.button_report);
        fieldReport = (EditText) findViewById(R.id.field_reason);
	
	}    
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(Constants.SUPER_COOKIES, RequestHandler.getCookies());
	
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
    }

    public void onClick(View v) {
    	
    	//Send?
    	if( v.getId() == R.id.button_report ) {
    	
    		//Get & validate
    		String reason = fieldReport.getText().toString();
    		if( reason.equals( "" ) ) {
    			
    			Toast.makeText( this, R.string.info_forum_report_error, Toast.LENGTH_SHORT ).show();
    			
    		} else {
    		
    			new AsyncReportPost( this, postId ).execute( reason );
    		
    		}
    	}
    	
    }
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_basic, menu );
		return super.onCreateOptionsMenu( menu );
	
    }
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		//Let's act!
		if( item.getItemId() == R.id.option_back ) {
			
			((Activity) this).finish();
			
		}
		
		// Return true yo
		return true;

	}  
	
	public class AsyncReportPost extends AsyncTask<String, Void, Boolean> {
		
		//Attributes
		private Context context;
		private long postId;

		//Constructs
		public AsyncReportPost( Context c, long p ) {
		
			this.context = c;
			this.postId = p;
			
		}
		
		@Override
		protected void onPreExecute() {
			
			if( context instanceof ForumReportView ) {
				
				buttonReport.setText( R.string.info_report_active );
				buttonReport.setEnabled( false );
				
			}
			
		}
		
		@Override
		protected Boolean doInBackground( String... arg0 ) {
		
			try {
				
				return WebsiteHandler.reportPostInThread( context, postId, arg0[0] );
				
			} catch( Exception ex ) {
				
				ex.printStackTrace();
				return false;
				
			}
		
		}
		
		@Override
		protected void onPostExecute( Boolean results ) {
		
			if( context instanceof ForumReportView ) {
				
				if( results ) {

					Toast.makeText( context, R.string.info_forum_report_true, Toast.LENGTH_SHORT ).show();
					( (ForumReportView) context ).finish();
					
				} else {
					
					Toast.makeText( context, R.string.info_forum_report_false, Toast.LENGTH_SHORT ).show();
					buttonReport.setText( R.string.label_report );
					buttonReport.setEnabled( true );
				}
				
			}
			

		}
	
	}
	
	@Override
	public void onResume() {
	
		super.onResume();
		
		//If we don't have a profile...
    	if( SessionKeeper.getProfileData() == null ) {
    		
    		//...but we do indeed have a cookie...
    		if( !sharedPreferences.getString( Constants.SP_BL_COOKIE_VALUE, "" ).equals( "" ) ){
    			
    			//...we set the SessionKeeper, but also reload the cookies! Easy peasy!
    			SessionKeeper.setProfileData( SessionKeeper.generateProfileDataFromSharedPreferences(sharedPreferences) );
    			RequestHandler.setCookies( 
    			
    				new ShareableCookie(

    					sharedPreferences.getString( Constants.SP_BL_COOKIE_NAME, "" ),
    					sharedPreferences.getString( Constants.SP_BL_COOKIE_VALUE, "" ),
    					Constants.COOKIE_DOMAIN
    						
    				)
    				
    			);
    			
    			//...but just to be sure, we try to verify our session "behind the scenes"
    			new AsyncSessionValidate(this, sharedPreferences).execute();
    			
    		} else {
    			
    			//Aw man, that backfired.
    			Toast.makeText( this, R.string.info_txt_session_lost, Toast.LENGTH_SHORT).show();
    			startActivity( new Intent(this, Main.class) );
    			finish();
    			
    		}
    		
    	}
    	
	}
	
}