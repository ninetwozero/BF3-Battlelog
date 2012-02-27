/*
	context file is part of BF3 Battlelog

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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class DebugActivity extends Activity {

	//SP
	private SharedPreferences sharedPreferences;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
    	//onCreate - save the instance state
    	super.onCreate(savedInstanceState);
    	
    	//Set the content view
        setContentView(R.layout.debug_activity);
        
        new LocalAsyncTask(this).execute();
        
	}
    
    public void onClick(View v) {}
    
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
    }
    
    public class LocalAsyncTask extends AsyncTask<Void, Integer, Void> {

    	//Constructor
    	private Context context;
    	private boolean isServiceRunning, hasConnection, isLoggedIn;
    	private boolean canInsert, canSelect, canUpdate, canDelete;
    	public LocalAsyncTask(Context c) {
    		
    		this.context = c;
    		
    	}	

    	@Override
    	protected void onPreExecute() {}

    	@Override
    	protected Void doInBackground( Void... arg0) {

    		try { this.isServiceRunning = PublicUtils.isMyServiceRunning( context ); } 
    		catch ( Exception ex ) { ex.printStackTrace(); }
    		
    		try { this.hasConnection = PublicUtils.isNetworkAvailable( context ); } 
    		catch ( Exception ex ) { ex.printStackTrace(); }
    		
    		try { this.isLoggedIn = WebsiteHandler.setActive(); } 
    		catch ( Exception ex ) { ex.printStackTrace(); }
    		
    		try { this.canInsert = ( CacheHandler.Persona.insert( context, WebsiteHandler.getStatsForUser( context, Dashboard.getProfile() ) ) > 0 ); } 
    		catch ( Exception ex ) { ex.printStackTrace(); }
    		
    		try { this.canSelect = ( CacheHandler.Persona.select( context, new long[] { Dashboard.getProfile().getPersonaId() } ).size() > 0 ); } 
    		catch ( Exception ex ) { ex.printStackTrace(); }
    		
    		try { 
    			
    			this.canUpdate = CacheHandler.Persona.update( context, WebsiteHandler.getStatsForUser( context, Dashboard.getProfile() ) );
    		
    		} 
    		catch ( Exception ex ) { ex.printStackTrace(); }
    		
    		try { this.canDelete = CacheHandler.Persona.delete( context, new long[] { Dashboard.getProfile().getProfileId() }); } 
    		catch ( Exception ex ) { ex.printStackTrace(); }
    		
    		return null;

    	}

    	@Override
    	protected void onPostExecute(Void results) {

    		//Set it up
    		try {

    			((TextView) findViewById(R.id.stats_service)).setText( isServiceRunning + "");
    			((TextView) findViewById(R.id.stats_connection)).setText( hasConnection + "");
    			((TextView) findViewById(R.id.stats_login)).setText( isLoggedIn + "");
    			((TextView) findViewById(R.id.stats_insert)).setText( canInsert + "");
    			((TextView) findViewById(R.id.stats_select)).setText( canSelect + "" );
    			((TextView) findViewById(R.id.stats_update)).setText( canUpdate + "");
    			((TextView) findViewById(R.id.stats_delete)).setText( canDelete + "");

    		} catch( Exception ex ) {

    			ex.printStackTrace();

    		}

    		//R-turn
    		return;

    	}

    }

}