package com.ninetwozero.battlelog;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
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
	    		
	    		//Restart the service
	    		BattlelogService.restart();
	    		
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
