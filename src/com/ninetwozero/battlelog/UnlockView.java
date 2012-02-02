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
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.UnlockListAdapter;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.datatypes.UnlockData;
import com.ninetwozero.battlelog.datatypes.UnlockDataWrapper;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class UnlockView extends TabActivity {

	//SharedPreferences for shizzle
	private SharedPreferences sharedPreferences;
	private ProgressBar progressBar;
	private AsyncGetDataSelf getDataAsync;
	private ProfileData profileData;
	private UnlockDataWrapper unlocks;
	private TabHost tabHost;
	private LayoutInflater layoutInflater;
	private ListView[] listView;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		RequestHandler.setCookies( (ArrayList<ShareableCookie> ) icicle.getParcelable(Constants.SUPER_COOKIES) );
    	
    	}
    	
    	//Get the intent
        if( !getIntent().hasExtra( "profile" ) ) {
        	
        	profileData = new ProfileData(

        		this.sharedPreferences.getString( Constants.SP_BL_USERNAME, "" ),
        		this.sharedPreferences.getString( Constants.SP_BL_PERSONA, "" ),
    			this.sharedPreferences.getLong( Constants.SP_BL_PERSONA_ID, 0 ),
    			this.sharedPreferences.getLong( Constants.SP_BL_PERSONA_ID, 0 ),
    			this.sharedPreferences.getLong( Constants.SP_BL_PLATFORM_ID, 1),
				sharedPreferences.getString( Constants.SP_BL_GRAVATAR, "" )
    		
    		);
        	
        } else {
        	
        	profileData = (ProfileData) getIntent().getParcelableExtra( "profile" );
        	
        }
        
        //Is the profileData null?!
        if( profileData == null || profileData.getProfileId() == 0 ) { finish(); return; }
    	
    	//Set the content view
        setContentView(R.layout.unlocks_view);

        //Prepare to tango
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.listView = new ListView[5];
        
        //Init!
        initActivity();
        
	}        

	public final void initActivity() {
		
		//Fix the tabs
    	tabHost = (TabHost) findViewById(android.R.id.tabhost);
    	
    	//Let's set them up
    	setupTabsPrimary(
    			
    		new String[] { 
    		
    			"Weapons",
    			"Attachments",
    			"Kit",
    			"Vehicles",
    			"Skills"
    		
    		}, 
    		new int[] { 
    			
    			R.layout.tab_content_unlocks, 
    			R.layout.tab_content_unlocks, 
    			R.layout.tab_content_unlocks, 
    			R.layout.tab_content_unlocks, 
    			R.layout.tab_content_unlocks
    			
    		}

    	);	
		
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
		reloadLayout();
		
	}
	
    private final View createTabView(final Context context, final String text) {
    	
    	View view = LayoutInflater.from(context).inflate(R.layout.profile_tab_layout, null);
    	TextView tv = (TextView) view.findViewById(R.id.tabsText);
    	tv.setText(text);
    	return view;
    
    }
    
	private void setupTabsPrimary( final String[] titleArray, final int[] layoutArray ) {

		//Init
    	TabHost.TabSpec spec;
    	
    	//Iterate them tabs
    	for(int i = 0, max = titleArray.length; i < max; i++) {

    		//Num
    		final int num = i;
			View tabview = createTabView(tabHost.getContext(), titleArray[num]);

			//Let's set the content
			spec = tabHost.newTabSpec(titleArray[num]).setIndicator(tabview).setContent(
	        		
	    		new TabContentFactory() {
	    			
	            	public View createTabContent(String tag) {
	            		
	            		View v = layoutInflater.inflate( layoutArray[num], null );
	            		v.setTag( "tab #" + num );
	            		return v;
	            	}
	            
	            }
	    		
	        );
			
			//Add the tab
			tabHost.addTab( spec ); 
    	
    	}
    	
    	//Assign values
    	tabHost.setOnTabChangedListener(
    			
    		new OnTabChangeListener() {

    			@Override
    			public void onTabChanged(String tabId) {
    				
    				switch( tabHost.getCurrentTab() ) {
						
						case 0:
							setupList(unlocks.getWeapons(), 0);
							break;
							
						case 1:
							setupList(unlocks.getAttachments(), 1);
							break;
							
						case 2:
							setupList(unlocks.getKitUnlocks(), 2);
							break;

						case 3:
							setupList(unlocks.getVehicleUpgrades(), 3);
							break;
							
						case 4:
							setupList(unlocks.getSkills(), 4);
							break;
						
						default:
							break;
						
					}

    			}
    			
    		}
    		
    	);
    	
    }
	
    public void reloadLayout() {
    	
    	//ASYNC!!!
    	new AsyncGetDataSelf(this).execute(
    		
    		profileData
		
		);
    	
    	
    }
    
    public void doFinish() {}
    
    private class AsyncGetDataSelf extends AsyncTask<ProfileData, Void, Boolean> {
    
    	//Attributes
    	Context context;
    	ProgressDialog progressDialog;
    	
    	public AsyncGetDataSelf(Context c) {
    		
    		this.context = c;
    		this.progressDialog = null;
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		
    		//Let's see
			if( unlocks == null ) { 
				
				this.progressDialog = new ProgressDialog(this.context);
				this.progressDialog.setTitle(context.getString( R.string.general_wait ));
				this.progressDialog.setMessage( getString(R.string.general_downloading ) );
				this.progressDialog.show();
    		
			}
			
    	}
    	

		@Override
		protected Boolean doInBackground( ProfileData... arg0 ) {
			
			try {
				
				unlocks = WebsiteHandler.getUnlocksForUser( arg0[0] );
				return true;
				
			} catch ( WebsiteHandlerException ex ) {
				
				ex.printStackTrace();
				return false;
				
			}

		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		
			//Fail?
			if( !result ) { 
				
				if( this.progressDialog != null ) this.progressDialog.dismiss();
				Toast.makeText( this.context, R.string.general_no_data, Toast.LENGTH_SHORT).show(); 
				((Activity) this.context).finish();
				return; 
			
			}

			//Do actual stuff, like sending to an adapter		
			switch( tabHost.getCurrentTab() ) {
				
				case 0:
					setupList(unlocks.getWeapons(), 0);
					break;
					
				case 1:
					setupList(unlocks.getAttachments(), 1);
					break;
					
				case 2:
					setupList(unlocks.getKitUnlocks(), 2);
					break;
					
				case 3:
					setupList(unlocks.getVehicleUpgrades(), 3);
					break;
					
				case 4:
					setupList(unlocks.getSkills(), 4);
					break;
					
				default:
					break;
				
				
			}
			
			//Go go go
	        if( this.progressDialog != null ) this.progressDialog.dismiss();
			return;
		}
    	
    }
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_basic, menu );
		return super.onCreateOptionsMenu( menu );
	
    }
	
	public void setupList( ArrayList<UnlockData> data, int pos ) {
		
		if( listView[pos] == null ) { 
			
			listView[pos] = (ListView) tabHost.findViewWithTag( "tab #" + pos ).findViewById( R.id.list_unlocks ); 
			listView[pos].setAdapter( new UnlockListAdapter(this, data, layoutInflater) );
			
		} else {
			
			((UnlockListAdapter) listView[pos].getAdapter()).setDataArray(data);
			((UnlockListAdapter) listView[pos].getAdapter()).notifyDataSetChanged();
			
		}
		
		return;
		
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
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
    }  
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(Constants.SUPER_COOKIES, RequestHandler.getCookies());
	
	}
    
}