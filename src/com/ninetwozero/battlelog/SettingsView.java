package com.ninetwozero.battlelog;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;


public class SettingsView extends PreferenceActivity {
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		RequestHandler.setCookies( (ArrayList<ShareableCookie> ) icicle.getParcelable(Constants.SUPER_COOKIES) );
    	
    	}
    	
    	//Let's put 'em there
    	addPreferencesFromResource(R.xml.settings_view);
    	
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) { super.onConfigurationChanged(newConfig); }  
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putSerializable(Constants.SUPER_COOKIES, RequestHandler.getSerializedCookies());
	
	}
	
}
