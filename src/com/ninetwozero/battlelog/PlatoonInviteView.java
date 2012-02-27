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
import java.util.Locale;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.PlatoonInviteListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncPlatoonMemberInvite;
import com.ninetwozero.battlelog.asynctasks.AsyncSessionSetActive;
import com.ninetwozero.battlelog.asynctasks.AsyncSessionValidate;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class PlatoonInviteView extends ListActivity {

	//Attributes
	private final Context CONTEXT = this;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private PlatoonData platoonData;
	private ArrayList<ProfileData> friends;
	private long[] selectedIds;
	private TabHost mTabHost;
	
	//Elements
	private ListView listView;
	
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
        
        //Prepare to tango
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.layoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        
    	//Get the intent
        if( getIntent().hasExtra( "platoon" ) ) { platoonData = getIntent().getParcelableExtra( "platoon" ); }
        if( getIntent().hasExtra( "friends" ) ) { friends = getIntent().getParcelableArrayListExtra( "friends" ); }
        
        //Is the profileData null?!
        if( platoonData == null || platoonData.getId() == 0 ) { finish(); return; }
    	
        //Fix it
        selectedIds = new long[friends.size()];
        
    	//Set the content view
        setContentView(R.layout.platoon_invite_view);
        
        //Let's see
    	initLayout();
    	
	}        

	public void initLayout() {

    	if( listView == null ) { 
    		
    		listView = getListView();
    		listView.setAdapter( new PlatoonInviteListAdapter(this, friends, layoutInflater) );
    	
    	}

	}
	
	public void onListItemClick(ListView lv, View v, int pos, long id) {
		
		if( selectedIds[pos] == 0 ) {
			
			selectedIds[pos] = id;
			((CheckBox) v.findViewById(R.id.checkbox)).setChecked( true );
			
		} else {
			
			selectedIds[pos] = 0;
			((CheckBox) v.findViewById(R.id.checkbox)).setChecked( false );
			
		}
		
	}
	
	public void onClick(View v) {
	
		if( v.getId() == R.id.button_ok ) {
			
			new AsyncPlatoonMemberInvite(
					
				this, 
				selectedIds, 
				platoonData.getId()
				
			).execute( sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ) );
			
		} else if( v.getId() == R.id.button_cancel ) {
		
			this.finish();
			
		}
		
	}

	@Override
	public void onResume() {
	
		super.onResume();
		
		//Setup the locale
    	if( !sharedPreferences.getString( Constants.SP_BL_LANG, "" ).equals( "" ) ) {

    		Locale locale = new Locale( sharedPreferences.getString( Constants.SP_BL_LANG, "en" ) );
	    	Locale.setDefault(locale);
	    	Configuration config = new Configuration();
	    	config.locale = locale;
	    	getResources().updateConfiguration(config, getResources().getDisplayMetrics() );
    	
    	}
 
     	new AsyncSessionSetActive().execute();
		
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