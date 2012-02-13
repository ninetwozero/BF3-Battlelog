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
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.UnlockListAdapter;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.datatypes.UnlockData;
import com.ninetwozero.battlelog.datatypes.UnlockDataWrapper;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class UnlockView extends TabActivity {

	//Attributes
	private SharedPreferences sharedPreferences;
	private AsyncGetDataSelf getDataAsync;
	private ProfileData profileData;
	private HashMap<Long, UnlockDataWrapper> unlocks;
	private long selectedPersona;
	private int selectedPosition;

	//Elements
	private ProgressBar progressBar;
	private TabHost tabHost;
	private LayoutInflater layoutInflater;
	private ListView[] listView;
	private TextView textEmpty;
	
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
    	
    	//Get the intent
        if( getIntent().hasExtra( "profile" ) ) {
        	
        	profileData = (ProfileData) getIntent().getParcelableExtra( "profile" );
        	
        } else {
        	
        	Toast.makeText( this, R.string.info_general_noprofile, Toast.LENGTH_SHORT).show();
        	return;
        	
        }
        
        //Is the profileData null?!
        if( profileData == null || profileData.getProfileId() == 0 ) { finish(); return; }

        //Get the pos
        selectedPersona = getIntent().getLongExtra( "selectedPersona", 0 );
        selectedPosition = 0;
        
    	//Set the content view
        setContentView(R.layout.unlocks_view);

        //Prepare to tango
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.listView = new ListView[5];
        this.textEmpty = (TextView) findViewById( R.id.text_empty );
        
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

    			R.drawable.tab_selector_unlocks_weapons,
    			R.drawable.tab_selector_unlocks_attachments,
    			R.drawable.tab_selector_unlocks_kits,
    			R.drawable.tab_selector_unlocks_vehicles,
    			R.drawable.tab_selector_unlocks_skills,
    				
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
	
    private final View createTabView(final Context context, final int logo) {
    	
    	View view = LayoutInflater.from(context).inflate(R.layout.unlock_tab_layout, null);
    	ImageView imageView = (ImageView) view.findViewById( R.id.image_tab );
    	imageView.setImageResource( logo );
    	return view;
    
    }
    
	private void setupTabsPrimary( final String[] titleArray, final int[] logoArray, final int[] layoutArray ) {

		//Init
		TabHost.TabSpec spec;
		
    	//Iterate them tabs
    	for(int i = 0, max = titleArray.length; i < max; i++) {

    		//Num
    		final int num = i;
			View tabview = createTabView(tabHost.getContext(), logoArray[num]);

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
							setupList(unlocks.get( selectedPersona ).getWeapons(), 0);
							break;
							
						case 1:
							setupList(unlocks.get( selectedPersona ).getAttachments(), 1);
							break;
							
						case 2:
							setupList(unlocks.get( selectedPersona ).getKitUnlocks(), 2);
							break;

						case 3:
							setupList(unlocks.get( selectedPersona ).getVehicleUpgrades(), 3);
							break;
							
						case 4:
							setupList(unlocks.get( selectedPersona ).getSkills(), 4);
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
    	new AsyncGetDataSelf(this).execute( profileData );
    	
    	
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

				if( arg0[0].getPersonaId() == 0 ) { 
					
					profileData = WebsiteHandler.getPersonaIdFromProfile( profileData ); 
					if( selectedPersona == 0 ) { selectedPersona = profileData.getPersonaId(); }
					unlocks = WebsiteHandler.getUnlocksForUser( profileData );
					
				} else {
					
					if( selectedPersona == 0 ) { selectedPersona = arg0[0].getPersonaId(); }
					unlocks = WebsiteHandler.getUnlocksForUser( arg0[0] );

				}	
				
				Log.d(Constants.DEBUG_TAG, "# of personas => " + unlocks.size() + "(" + profileData.getNumPersonas() + ")" );
				
				return (unlocks != null);
				
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
					setupList(unlocks.get( selectedPersona ).getWeapons(), 0);
					break;
					
				case 1:
					setupList(unlocks.get( selectedPersona ).getAttachments(), 1);
					break;
					
				case 2:
					setupList(unlocks.get( selectedPersona ).getKitUnlocks(), 2);
					break;
					
				case 3:
					setupList(unlocks.get( selectedPersona ).getVehicleUpgrades(), 3);
					break;
					
				case 4:
					setupList(unlocks.get( selectedPersona ).getSkills(), 4);
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
		inflater.inflate( R.menu.option_unlock, menu );
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
		
		//Is it empty?
		if( data == null || data.size() == 0 ) { 

			textEmpty.setVisibility( View.VISIBLE );
			return;
			
		} else {
			
			textEmpty.setVisibility( View.GONE );
			
		}
		
		return;
		
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		//Let's act!
		if( item.getItemId() == R.id.option_reload ) {
	
			this.reloadLayout();
			
		} else if( item.getItemId() == R.id.option_change ) {
			
			generateDialogPersonaList( 
					
				this, 
				profileData.getPersonaIdArray(), 
				profileData.getPersonaNameArray(), 
				profileData.getPlatformIdArray() 
				
			).show();
			
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
    
	public Dialog generateDialogPersonaList( final Context context, final long[] personaId, final String[] persona, final long[] ls ) {
		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
	    //Set the title and the view
		builder.setTitle( R.string.info_dialog_soldierselect );
		String[] listNames = new String[personaId.length];
		
		for( int i = 0, max = personaId.length; i < max; i++ ) {
			
			listNames[i] = persona[i] + " " + DataBank.resolvePlatformId( (int) ls[i] );
			
		}
		builder.setSingleChoiceItems(
				
			listNames, selectedPosition, new DialogInterface.OnClickListener() {
		  
				public void onClick(DialogInterface dialog, int item) {
			    	
					if( personaId[item] != selectedPersona ) {
						
						//Update it
						selectedPersona = profileData.getPersonaId(item);
						
						//Store selected position
						selectedPosition = item;
					
						//Load the new!
						switch( tabHost.getCurrentTab() ) {
							
							case 0:
								setupList(unlocks.get( selectedPersona ).getWeapons(), 0);
								break;
								
							case 1:
								setupList(unlocks.get( selectedPersona ).getAttachments(), 1);
								break;
								
							case 2:
								setupList(unlocks.get( selectedPersona ).getKitUnlocks(), 2);
								break;
								
							case 3:
								setupList(unlocks.get( selectedPersona ).getVehicleUpgrades(), 3);
								break;
								
							case 4:
								setupList(unlocks.get( selectedPersona ).getSkills(), 4);
								break;
								
							default:
								break;
							
							
						}
						
					}
					
					dialog.dismiss();
		
				}
				
			}
		
		);
		
		//CREATE
		return builder.create();
		
	}
}