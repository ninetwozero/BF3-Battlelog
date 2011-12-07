package com.ninetwozero.battlelog;

import java.util.ArrayList;

import com.ninetwozero.battlelog.datatypes.SerializedCookie;
import com.ninetwozero.battlelog.misc.RequestHandler;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class SettingsView extends PreferenceActivity {
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( "serializedCookies" ) ) {
    		
    		RequestHandler.setSerializedCookies( (ArrayList<SerializedCookie> ) icicle.getSerializable("serializedCookies") );
    	
    	}
    	
    	//Let's put 'em there
    	addPreferencesFromResource(R.xml.settings_view);
    	
	}
}
