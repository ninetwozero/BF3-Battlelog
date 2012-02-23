package com.ninetwozero.battlelog;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;

import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.services.BattlelogService;


public class SettingsView extends PreferenceActivity  {
	
	//Attributes
	private long originalInterval;
	private SharedPreferences sharedPreferences;
	
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
    	
    	//Get the SharedPreferences
    	sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
    	
    	//Let's put 'em there
    	addPreferencesFromResource(R.xml.settings_view);
    	
    	//Set the originalInterval
    	originalInterval = sharedPreferences.getInt( Constants.SP_BL_INTERVAL_SERVICE, 0 );
    	
	}
	
	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {

		// Hotkeys
		if ( keyCode == KeyEvent.KEYCODE_BACK ) {
			
			//Finns det 
	    	if( originalInterval != sharedPreferences.getInt( Constants.SP_BL_INTERVAL_SERVICE, 0 ) ) {
	    		
	    		//Get the interval
	    		long serviceInterval = sharedPreferences.getLong( Constants.SP_BL_INTERVAL_SERVICE, (Constants.HOUR_IN_SECONDS/2) )*1000;
	    		
	    		//Restart the AlarmManager
	    		AlarmManager alarmManager = (AlarmManager) getSystemService( Context.ALARM_SERVICE );
	    		PendingIntent pendingIntent = PendingIntent.getService( this, 0, new Intent(this, BattlelogService.class), 0);
	    		alarmManager.cancel( pendingIntent  );
	    		alarmManager.setInexactRepeating( 
	    				
	    			AlarmManager.ELAPSED_REALTIME, 
	    			0, 
	    			serviceInterval,
	    			pendingIntent 
	    			
	    		);
	    		

	    		Log.d( Constants.DEBUG_TAG, "Setting the service to update every " + serviceInterval/60000 + " minutes" );
	    	}
	    	this.finish();
			return true;
			
		}
		
		return super.onKeyDown( keyCode, event );
	
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) { 
    	
    	super.onConfigurationChanged(newConfig); 
    
    }  
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(Constants.SUPER_COOKIES, RequestHandler.getCookies());
	
	}
	
}
