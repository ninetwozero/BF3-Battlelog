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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.FeedListAdapter;
import com.ninetwozero.battlelog.adapters.FriendListAdapter;
import com.ninetwozero.battlelog.adapters.NotificationListAdapter;
import com.ninetwozero.battlelog.adapters.RequestListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncLogout;
import com.ninetwozero.battlelog.asynctasks.AsyncSessionValidate;
import com.ninetwozero.battlelog.datatypes.FriendListDataWrapper;
import com.ninetwozero.battlelog.datatypes.NotificationData;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.fragments.FeedFragment;
import com.ninetwozero.battlelog.fragments.MenuFragment;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class ViewPagerDashboard extends FragmentActivity {

	//Attributes
	final private Context context = this;
	private String[] valueFieldsArray;
	private PostData[] postDataArray;
	private ArrayList<NotificationData> notificationArray;
	private FriendListDataWrapper friendListData;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	
	//Elements
	private View wrapFriendRequests;
	private TabHost mTabHost, cTabHost;
	private ListView listFeed;
	private EditText fieldStatusUpdate;
	private FeedListAdapter feedListAdapter;
	private TextView feedStatusText, notificationStatusText, friendsStatusText;
	
	//COM-related
	private SlidingDrawer slidingDrawer;
	private TextView slidingDrawerHandle;
	private OnDrawerOpenListener onDrawerOpenListener;
	private OnDrawerCloseListener onDrawerCloseListener;
	private ListView listFriendRequests, listFriends, listNotifications;
	private NotificationListAdapter notificationListAdapter;
	private FriendListAdapter friendListAdapter;
	private RequestListAdapter friendRequestListAdapter;
	private OnItemClickListener onItemClickListener;
	private Button buttonRefresh;
	
	//Fragment related
	private MenuFragment menuFragment;
	private FeedFragment feedFragment;
	private FragmentManager fragmentManager;
	
	//Async
	private AsyncLogout asyncLogout;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);

        //Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		ArrayList<ShareableCookie> shareableCookies = icicle.getParcelableArrayList(Constants.SUPER_COOKIES);
			
    		if( shareableCookies != null ) { 
    			
    			RequestHandler.setCookies( shareableCookies );
    		
    		} else {
    			
    			finish();
    			
    		}
    		
    	}
    	
    	//We should've gotten a profile
    	if( SessionKeeper.getProfileData() == null ) {
    		
    		if( getIntent().hasExtra( "myProfile" ) ) {
    			
    			SessionKeeper.setProfileData( (ProfileData) getIntent().getParcelableExtra( "myProfile" ) );
    			
    		} else {
    			
    			Toast.makeText( this, R.string.info_txt_session_lost, Toast.LENGTH_SHORT).show();
    			return;
    			
    		}
    		
    	}
    	
    	//Set the content view
        setContentView(R.layout.viewpager_dashboard);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fragmentManager = getSupportFragmentManager();
        
        //Let's setup the fragments
        if( menuFragment == null ) {
        	
        	 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        	 fragmentTransaction.add(R.id.fragment_content, new MenuFragment());
        	 fragmentTransaction.add(R.id.fragment_content, new MenuFragment());
             fragmentTransaction.commit();

        }
	        
        //Setup the data
        notificationArray = new ArrayList<NotificationData>();
    	friendListData = new FriendListDataWrapper(null, null, null);
        
        //Setup COM & feed
    	initActivity();
	}	

	public final void initActivity() {}

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
	
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
    	
        super.onConfigurationChanged(newConfig);       
        
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(Constants.SUPER_COOKIES, RequestHandler.getCookies());
	
	}
	
	public void onMenuClick(View v) { menuFragment.onMenuClick(v); }
    
}