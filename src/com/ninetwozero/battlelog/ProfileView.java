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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.FeedListAdapter;
import com.ninetwozero.battlelog.adapters.PlatoonListAdapter;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.PlayerData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ProfileInformation;
import com.ninetwozero.battlelog.datatypes.SerializedCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class ProfileView extends TabActivity {

	//Attributes
	private SharedPreferences sharedPreferences;
	private ProgressBar progressBar;
	private ProfileData profileData;
	private LayoutInflater layoutInflater;
	private TabHost mTabHost;
	
	//Elements
	private ListView listFeed;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( "serializedCookies" ) ) {
    		
    		RequestHandler.setSerializedCookies( (ArrayList<SerializedCookie> ) icicle.getSerializable("serializedCookies") );
    	
    	}
    	
    	//Set the content view
        setContentView(R.layout.profile_view);
        
        //Prepare to tango
        this.sharedPreferences = this.getSharedPreferences( Constants.fileSharedPrefs, 0);
        this.layoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        
        //Fix the tabs
    	mTabHost = (TabHost) findViewById(android.R.id.tabhost);
    	setupTabs(new String[] { "Home", "Stats", "Feed" }, new int[] {R.layout.tab_content_overview, R.layout.tab_content_stats, R.layout.tab_content_feed} );
    	
        //Get the intent
        if( !getIntent().hasExtra( "profile" ) ) {
        	
        	profileData = new ProfileData(
				
    			this.sharedPreferences.getString( "battlelog_persona", "" ),
				this.sharedPreferences.getLong( "battlelog_persona_id", 0 ),
				this.sharedPreferences.getLong( "battlelog_persona_id", 0 ),
				this.sharedPreferences.getLong( "battlelog_platform_id", 1)
			
			);
        	
        } else {
        	
        	profileData = (ProfileData) getIntent().getSerializableExtra( "profile" );
        	
        }

        initLayout();
	}        

	public void initLayout() {
		
		//Eventually get a *cached* version instead
		reloadLayout();
		
	}
	
    public void reloadLayout() {
    	
    	//ASYNC!!!
    	new GetDataSelfAsync(this).execute(
    		
    		profileData

		);
    	
    	
    }
    
    private void setupTabs(final String[] tags, final int[] layouts) {

		//Init
    	TabHost.TabSpec spec;
    	
    	//Iterate them tabs
    	for(int i = 0; i < tags.length; i++) {

    		//Num
    		final int num = i;
			View tabview = createTabView(mTabHost.getContext(), tags[num]);
			
			//Let's set the content
			spec = mTabHost.newTabSpec(tags[num]).setIndicator(tabview).setContent(
	        		
	    		new TabContentFactory() {
	    			
	            	public View createTabContent(String tag) {
	            		
	            		return layoutInflater.inflate( layouts[num], null );
	    
	            	}
	            
	            }
	    		
	        );
			
			//Add the tab
			mTabHost.addTab( spec ); 
    	
    	}
    	
    }

    private static View createTabView(final Context context, final String text) {
    	
    	View view = LayoutInflater.from(context).inflate(R.layout.profile_tab_layout, null);
    	TextView tv = (TextView) view.findViewById(R.id.tabsText);
    	tv.setText(text);
    	return view;
    
    }
    
    public void doFinish() {}
    
    private class GetDataSelfAsync extends AsyncTask<ProfileData, Void, Boolean> {
    
    	//Attributes
    	Context context;
    	ProgressDialog progressDialog;
    	ProfileInformation profileInformation;
    	PlayerData playerData;
    	
    	public GetDataSelfAsync(Context c) {
    		
    		this.context = c;
    		this.progressDialog = null;
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		
    		//Let's see
			this.progressDialog = new ProgressDialog(this.context);
			this.progressDialog.setTitle("Please wait");
			this.progressDialog.setMessage( "Downloading the data..." );
			this.progressDialog.show();
    		
    	}

		@Override
		protected Boolean doInBackground( ProfileData... arg0 ) {
			
			try {
				
				this.profileInformation = WebsiteHandler.getProfileInformationForUser( arg0[0] );
				this.playerData = WebsiteHandler.getStatsForUser( arg0[0] );
				return true;
				
			} catch ( WebsiteHandlerException ex ) {
				
				Log.d(Constants.debugTag, ex.getMessage() );
				return false;
				
			}

		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		
			//Fail?
			if( !result ) { 
				
				if( this.progressDialog != null ) this.progressDialog.dismiss();
				Toast.makeText( this.context, "No data found.", Toast.LENGTH_SHORT).show(); 
				((Activity) this.context).finish();
				return; 
			
			}

			//Assign values
			mTabHost.setOnTabChangedListener(
					
				new OnTabChangeListener() {

					@Override
					public void onTabChanged(String tabId) {
	
						switch( getTabHost().getCurrentTab() ) {
							
							case 0:
								drawLayout(profileInformation);
								break;
								
							case 1:
								drawStats(playerData);
								break;
							
							case 2:
								drawFeed(profileInformation);
								break;
								
							default:
								break;
					
						}
		
					}
					
				}
				
			);

			drawLayout(this.profileInformation);
			
			//Done!
	        if( this.progressDialog != null ) this.progressDialog.dismiss();
			return;
		}
    	
    }
    
    public final void drawLayout(ProfileInformation data) {
    	
    	//Let's start drawing the... layout
    	((TextView) findViewById(R.id.text_username)).setText( data.getUsername() );
    	
    	//When did was the users last login?
    	if( data.isPlaying() && data.isOnline() ) { 
    		
    		((TextView) findViewById(R.id.text_online)).setText( 
    				
				"Playing on {SERVER_NAME}".replace(
					
					"{SERVER_NAME}",
					data.getCurrentServer()
					
				)
    				
			);
        	
    	} else if( data.isOnline() ) {
    	
    		((TextView) findViewById(R.id.text_online)).setText( "Online but not currently playing" ); 
    		
    	} else {
    		
    		((TextView) findViewById(R.id.text_online)).setText( data.getLastLogin() ); 
    		
    	}
    	
    	//Is the status ""?
    	if( !data.getStatusMessage().equals( "" ) ) {
			
    		//Set the status
    		((TextView) findViewById(R.id.text_status)).setText( data.getStatusMessage() );
    		((TextView) findViewById(R.id.text_status_date)).setText( PublicUtils.getRelativeDate( data.getStatusMessageChanged(), "Last updated ") );
    	
    	} else {
    		
    		//Hide the view
    		((RelativeLayout) findViewById(R.id.wrap_status)).setVisibility(View.GONE);
    		
    	}
    	
    	//Do we have a presentation?
    	if( !data.getPresentation().equals( "" ) ) {
    		
    		((TextView) findViewById(R.id.text_presentation)).setText( data.getPresentation() );
		
    	} else {
    		
    		((RelativeLayout) findViewById(R.id.wrap_presentation)).setVisibility(View.GONE);

    	}
    	
    	//Any platoons?
    	if( data.getPlatoons().size() > 0 ) {

    		((ListView) findViewById(R.id.list_platoons)).setAdapter(  
    		
    			new PlatoonListAdapter(this, data.getPlatoons(), layoutInflater)
    			
    		);
    		
    	} else {
        	
    		((TextView) findViewById(R.id.text_platoon)).setVisibility( View.VISIBLE );
    	
    	}
    	
    	//Set the username
    	((TextView) findViewById(R.id.text_username)).setText( data.getUsername() );
    }
    
    public void drawStats(PlayerData pd) {
    	
		//Persona & rank
        ((TextView) findViewById(R.id.string_persona)).setText( pd.getPersonaName() );
        ((TextView) findViewById(R.id.string_rank_title)).setText( pd.getRankTitle() );
        ((TextView) findViewById(R.id.string_rank_short)).setText( pd.getRankId() + "" );
        
        //Progress
        progressBar = ( (ProgressBar) findViewById(R.id.progress_level));
        progressBar.setMax( (int) pd.getPointsNeededToLvlUp()  );
        progressBar.setProgress( (int) pd.getPointsProgressLvl() );
        ((TextView) findViewById(R.id.string_progress_curr)).setText( pd.getPointsProgressLvl() + "" );
        ((TextView) findViewById(R.id.string_progress_max)).setText( pd.getPointsNeededToLvlUp() + "" );
        ((TextView) findViewById(R.id.string_progress_left)).setText( pd.getPointsLeft() + "" );
        
        //Score
        ((TextView) findViewById(R.id.string_score_assault)).setText( pd.getScoreAssault() + "" );
        ((TextView) findViewById(R.id.string_score_engineer)).setText( pd.getScoreEngineer() + "" );
        ((TextView) findViewById(R.id.string_score_support)).setText( pd.getScoreSupport() + "" );
        ((TextView) findViewById(R.id.string_score_recon)).setText( pd.getScoreRecon() + "" );
        ((TextView) findViewById(R.id.string_score_vehicles)).setText( pd.getScoreVehicle() + "" );
        ((TextView) findViewById(R.id.string_score_combat)).setText( pd.getScoreCombat() + "" );
        ((TextView) findViewById(R.id.string_score_award)).setText( pd.getScoreAwards() + "" );
        ((TextView) findViewById(R.id.string_score_unlock)).setText( pd.getScoreUnlocks() + "" );
        ((TextView) findViewById(R.id.string_score_total)).setText( pd.getScoreTotal() + "" );
        
        //Stats
        ((TextView) findViewById(R.id.string_stats_kills)).setText( pd.getNumKills() + "" );
        ((TextView) findViewById(R.id.string_stats_assists)).setText( pd.getNumAssists() + "" );
        ((TextView) findViewById(R.id.string_stats_heals)).setText( pd.getNumHeals() + "" );
        ((TextView) findViewById(R.id.string_stats_revives)).setText( pd.getNumRevives() + "" );
        ((TextView) findViewById(R.id.string_stats_repairs)).setText( pd.getNumRepairs() + "" );
        ((TextView) findViewById(R.id.string_stats_resupplies)).setText( pd.getNumResupplies() + "" );
        ((TextView) findViewById(R.id.string_stats_deaths)).setText( pd.getNumDeaths() + "" );
        ((TextView) findViewById(R.id.string_stats_kdr)).setText( pd.getKDRatio() + "" );
        ((TextView) findViewById(R.id.string_stats_wins)).setText( pd.getNumWins() + "" );
        ((TextView) findViewById(R.id.string_stats_losses)).setText( pd.getNumLosses() + "" );
        ((TextView) findViewById(R.id.string_stats_wlr)).setText( pd.getWLRatio() + "" );
        ((TextView) findViewById(R.id.string_stats_accuracy)).setText( pd.getAccuracy() + "%" );
        ((TextView) findViewById(R.id.string_stats_time)).setText( pd.getTimePlayedString() + "" );
        ((TextView) findViewById(R.id.string_stats_spm)).setText( pd.getScorePerMinute() + "" );
        ((TextView) findViewById(R.id.string_stats_lks)).setText( pd.getLongestKS() + "" );
        ((TextView) findViewById(R.id.string_stats_lhs)).setText( pd.getLongestHS() + " m");
    	
    }
    
    public void drawFeed(ProfileInformation data) {
    	
    	//Do we have it already?
		if( listFeed == null ) { listFeed = ((ListView) findViewById(R.id.list_feed)); }
        
		((TextView) findViewById(R.id.feed_username)).setText( data.getUsername() );
        
		//Always and forever
		listFeed.setAdapter( 
				
			new FeedListAdapter(this, data.getFeedItems(), layoutInflater) 
			
		);
		
		//Do we have the onClick?
		if( listFeed.getOnItemLongClickListener() == null ) {
			
			listFeed.setOnItemClickListener( 
					
				new OnItemClickListener() {

					@Override
					public void onItemClick( AdapterView<?> a, View v, int pos, long id ) {

						if( !((FeedItem) a.getItemAtPosition( pos ) ).getContent().equals( "" ) ) {
							
							View viewContainer = (View) v.findViewById(R.id.text_content);
							viewContainer.setVisibility( ( viewContainer.getVisibility() == View.GONE ) ? View.VISIBLE : View.GONE );
						
						}
						
					}
					
					
					
				}
					
			);
			
		}
    	
    }
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_profileview, menu );
		return super.onCreateOptionsMenu( menu );
	
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
		outState.putSerializable("serializedCookies", RequestHandler.getSerializedCookies());
	
	}
}