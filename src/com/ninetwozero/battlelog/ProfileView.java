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
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.FeedListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncFeedHooah;
import com.ninetwozero.battlelog.asynctasks.AsyncFriendRemove;
import com.ninetwozero.battlelog.asynctasks.AsyncFriendRequest;
import com.ninetwozero.battlelog.asynctasks.AsyncPostToWall;
import com.ninetwozero.battlelog.asynctasks.AsyncSessionSetActive;
import com.ninetwozero.battlelog.asynctasks.AsyncSessionValidate;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.PersonaStats;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ProfileInformation;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class ProfileView extends TabActivity {

	//Attributes
	private final Context CONTEXT = this;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private ProfileData profileData;
	private HashMap<Long, PersonaStats> personaStats;
	private ProfileInformation profileInformation;
	private long selectedPersona;
	private int selectedPosition;
	
	//Elements
	private ProgressBar progressBar;
	private TabHost mTabHost;
	private FeedListAdapter feedListAdapter;
	private ListView listFeed;
	private EditText fieldMessage;
	
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
        if( getIntent().hasExtra( "profile" ) ) { profileData = getIntent().getParcelableExtra( "profile" ); }
        
        //Is the profileData null?!
        if( profileData == null || profileData.getProfileId() == 0 ) { return; }
    	personaStats = new HashMap<Long, PersonaStats>();

        //Set the selected persona
        selectedPersona = profileData.getPersonaId();
        
    	//Set the content view
        setContentView(R.layout.profile_view);
        
        //Setup the tabs
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        setupTabs(
    			
        		new String[] { 
        				
        			getString(R.string.label_home ), 
    				getString(R.string.label_stats ), 
    				getString(R.string.label_feed )
        			
        		}, 
        		new int[] { 
        				
        			R.layout.tab_content_profile_overview, 
        			R.layout.tab_content_profile_stats, 
        			R.layout.tab_content_profile_feed 
        			
        		}
        		
        	);
        
	}        

	public void initLayout() {

		//Fix the tabs
    	if( mTabHost == null ) { mTabHost = (TabHost) findViewById(android.R.id.tabhost); }
		
		//Let's try something new
		if( SessionKeeper.getProfileData() == null ) { finish(); }
		
		//Get a *cached* version instead    
		if( profileInformation == null || personaStats == null ) { 
			
			new AsyncProfileCacheLoad(CONTEXT).execute();
		
		}
		
	}
	
    public void reloadLayout() {
    	
    	//ASYNC!!!
    	new AsyncProfileRefresh(CONTEXT, SessionKeeper.getProfileData().getProfileId()).execute();
    	
    	
    }
    
    private void setupTabs(final String[] tags, final int[] layouts) {

		//Init
    	TabHost.TabSpec spec;
    	
    	//Iterate them tabs
    	for(int i = 0, max = tags.length; i < max; i++) {

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
    
    public class AsyncProfileCacheLoad extends AsyncTask<Void, Void, Boolean> {
        
    	//Attributes
    	private Context context;
    	private ProgressDialog progressDialog;
    	
    	public AsyncProfileCacheLoad(Context c) {
    		
    		this.context = c;
    		this.progressDialog = null;
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		
    		//Do we?
    		this.progressDialog = new ProgressDialog(context);
			this.progressDialog.setTitle(context.getString( R.string.general_wait ));
			this.progressDialog.setMessage( context.getString( R.string.general_downloading ) );
			this.progressDialog.show();
    	
    	}

		@Override
		protected Boolean doInBackground( Void... arg0 ) {
			
			try {
				
				//Get...
				profileInformation = CacheHandler.Profile.select( context, profileData.getProfileId() );
				
				//We got one?!
				if( profileInformation != null ) { 

					personaStats = CacheHandler.Persona.select( context, profileInformation.getAllPersonas() );
					selectedPersona = profileInformation.getPersona( 0 );
					
				} else {
					
					personaStats = null;
					
				}
				
				//...validate!
				return( personaStats != null && personaStats.size() > 0 );
				
			} catch ( Exception ex ) {
				
				ex.printStackTrace();
				return false;
				
			}

		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			
			//Assign values
			if( result ) { 
				
				mTabHost.setOnTabChangedListener(
						
					new OnTabChangeListener() {
	
						@Override
						public void onTabChanged(String tabId) {
		
							switch( mTabHost.getCurrentTab() ) {
								
								case 0:
									setupHome(profileInformation);
									break;
									
								case 1:
									setupStats(personaStats.get( selectedPersona ));
									break;
								
								case 2:
									setupFeed(profileInformation);
									break;
									
								default:
									break;
						
							}
			
						}
						
					}
				
				);

				//Let's see what we need to update *directly*
				switch( mTabHost.getCurrentTab() ) {
					
					case 0:
						setupHome(profileInformation);
						break;
						
					case 1:
						setupStats(personaStats.get( selectedPersona ));
						break;
					
					case 2:
						setupFeed(profileInformation);
						break;
						
					default:
						break;
			
				}
			
				//Siiiiiiiiilent refresh
				new AsyncProfileRefresh(CONTEXT, SessionKeeper.getProfileData().getProfileId() ).execute();	
				if( this.progressDialog != null ) { this.progressDialog.dismiss(); }
			
			} else {

				new AsyncProfileRefresh(CONTEXT, SessionKeeper.getProfileData().getProfileId(), progressDialog).execute();
			
			}
	        
	        //Get back here!
	        return;
		        
		}
		
    }
    
    public class AsyncProfileRefresh extends AsyncTask<Void, Void, Boolean> {
        
    	//Attributes
    	private Context context;
    	private long activeProfileId;
    	private ProgressDialog progressDialog;
    	
    	public AsyncProfileRefresh(Context c, long pId) {
    		
    		this.context = c;
    		this.activeProfileId = pId;
    		this.progressDialog = null;
    		
    	}
    	
    	public AsyncProfileRefresh(Context c, long pId, ProgressDialog pDialog ) {
    		
    		this.context = c;
    		this.activeProfileId = pId;
    		this.progressDialog = pDialog;
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {}

		@Override
		protected Boolean doInBackground( Void... arg0 ) {
			
			try {
				
				//Let's get the personas!
				profileInformation = WebsiteHandler.getProfileInformationForUser(
						
					context, 
					profileData, 
					sharedPreferences.getInt( Constants.SP_BL_NUM_FEED, Constants.DEFAULT_NUM_FEED ),
					this.activeProfileId
					
				);
				
				if( profileInformation != null ) {
				
					//Need for clear?
					//if( personaStats != null ) { personaStats.clear(); }
					
					//Set the persona
					profileData.setPersonaId( profileInformation.getAllPersonas() );
					profileData.setPersonaName( profileInformation.getAllPersonaNames() );
					profileData.setPlatformId( profileInformation.getAllPlatforms() );

					//Set the selected persona?
					selectedPersona = ( selectedPersona == 0 ) ? profileData.getPersonaId( 0 ) : selectedPersona;
					
					//Grab the stats
					personaStats = WebsiteHandler.getStatsForUser( context, profileData );
					
					//...validate!
					return ( personaStats != null && personaStats.size() > 0 );
					
				} else {

					return false;
				
				}
				
				
			} catch ( WebsiteHandlerException ex ) {
				
				ex.printStackTrace();
				return false;
				
			}

		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			
			//Fail?
			if( !result ) { 
				
				Toast.makeText( context, R.string.general_no_data, Toast.LENGTH_SHORT).show(); 
			
			} else {
	
	
				//Assign values
				mTabHost.setOnTabChangedListener(
						
					new OnTabChangeListener() {
	
						@Override
						public void onTabChanged(String tabId) {
		
							switch( getTabHost().getCurrentTab() ) {
								
								case 0:
									setupHome(profileInformation);
									break;
									
								case 1:
									setupStats(personaStats.get( selectedPersona ));
									break;
								
								case 2:
									setupFeed(profileInformation);
									break;
									
								default:
									break;
						
							}
			
						}
						
					}
					
				);
	
				//Let's see what we need to update *directly*
				switch( mTabHost.getCurrentTab() ) {
					
					case 0:
						setupHome(profileInformation);
						break;
						
					case 1:
						setupStats(personaStats.get( selectedPersona ));
						break;
					
					case 2:
						setupFeed(profileInformation);
						break;
						
					default:
						break;
			
				}
				
			}
			
			//Do we have a dialog?
			if( progressDialog != null ) {
				
				if( progressDialog.isShowing() ) {
					
					progressDialog.dismiss();
					progressDialog = null;
					
				}
				
			}
			
	        //Get back here!
	        return;
		        
		}
		
    }
    
    public final void setupHome(ProfileInformation data) {
    	
    	//Do we have valid data?
    	if( data == null ) { return; }
    	
    	//Let's start drawing the... layout
    	((TextView) findViewById(R.id.text_username)).setText( data.getUsername() );
    	
    	//When did was the users last login?
    	if( data.isPlaying() && data.isOnline() ) { 
    		
    		((TextView) findViewById(R.id.text_online)).setText( 
    				
				getString(R.string.info_profile_playing ).replace(
					
					"{server name}",
					data.getCurrentServer()
					
				)
    				
			);
        	
    	} else if( data.isOnline() ) {
    	
    		((TextView) findViewById(R.id.text_online)).setText( R.string.info_profile_online ); 
    		
    	} else {
    		
    		((TextView) findViewById(R.id.text_online)).setText( data.getLastLogin(CONTEXT) ); 
    		
    	}
    	
    	//Is the status ""?
    	if( !data.getStatusMessage().equals( "" ) ) {
			
    		//Set the status
    		((TextView) findViewById(R.id.text_status)).setText( data.getStatusMessage() );
    		((TextView) findViewById(R.id.text_status_date)).setText( PublicUtils.getRelativeDate( CONTEXT, data.getStatusMessageChanged(), R.string.info_lastupdate) );
    	
    	} else {
    		
    		//Hide the view
    		((RelativeLayout) findViewById(R.id.wrap_status)).setVisibility(View.GONE);
    		
    	}
    	
    	//Do we have a presentation?
    	if( data.getPresentation() != null && !data.getPresentation().equals( "" ) ) {
    		
    		((TextView) findViewById(R.id.text_presentation)).setText( data.getPresentation() );
		
    	} else {
    		
    		((TextView) findViewById(R.id.text_presentation)).setText( R.string.info_profile_empty_pres );

    	}
    	
    	//Any platoons?
    	if( data.getNumPlatoons() > 0 ) {

    		//Init
    		View convertView;
    		LinearLayout platoonWrapper = (LinearLayout) findViewById(R.id.list_platoons);
    		
    		//Clear the platoonWrapper
    		((TextView) findViewById(R.id.text_platoon)).setVisibility( View.GONE );
    		platoonWrapper.removeAllViews();
    		
    		//Iterate over the platoons
    		for( PlatoonData currentPlatoon : data.getPlatoons() ) {

	    		//Does it already exist?
    			if( platoonWrapper.findViewWithTag( currentPlatoon ) != null ) { continue; }
    			
    			//Recycle
	    		convertView = layoutInflater.inflate( R.layout.list_item_platoon, platoonWrapper, false );
	
	    		//Set the TextViews
	    		((TextView) convertView.findViewById( R.id.text_name ) ).setText( currentPlatoon.getName() );
	    		((TextView) convertView.findViewById( R.id.text_tag ) ).setText( "[" + currentPlatoon.getTag() + "]" );
	    		((TextView) convertView.findViewById( R.id.text_members ) ).setText( currentPlatoon.getCountMembers() + "");
	    		((TextView) convertView.findViewById( R.id.text_fans ) ).setText( currentPlatoon.getCountFans() + "");
	    		
	    		//Almost forgot - we got a Bitmap too!
	    		((ImageView) convertView.findViewById( R.id.image_badge ) ).setImageBitmap( 
    				
    				BitmapFactory.decodeFile( PublicUtils.getCachePath( this ) + currentPlatoon.getImage() )
    					
    			);
	    		
	    		//Store it in the tag
	    		convertView.setTag( currentPlatoon );
	    		convertView.setOnClickListener(
	    			
	    			new OnClickListener() {

						@Override
						public void onClick( View v ) {

							//On-click
							startActivity( 
									
								new Intent(CONTEXT, PlatoonView.class).putExtra(
										
									"platoon", 
									(PlatoonData) v.getTag()
								
								) 
								
							);
							
						}
	    				
	    			}
	    				
	    		);
	    		//Add it!
	    		platoonWrapper.addView( convertView );
	    		
    		}
    		
    	} else {


    		((LinearLayout) findViewById(R.id.list_platoons)).removeAllViews();
    		((TextView) findViewById(R.id.text_platoon)).setVisibility( View.VISIBLE );
    	
    	}
    	
    	//Set the username
    	((TextView) findViewById(R.id.text_username)).setText( data.getUsername() );
    }
    
    public void setupStats(PersonaStats pd) {
    	
    	//Is pd null?
    	if( pd == null ) { return; }
    	    	
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
        ((TextView) findViewById(R.id.string_stats_vkills)).setText( pd.getNumVehicles() + "" );
        ((TextView) findViewById(R.id.string_stats_vassists)).setText( pd.getNumVehicleAssists() + "" );
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
        ((TextView) findViewById(R.id.string_stats_lks)).setText( pd.getLongestKS() + "" );
        ((TextView) findViewById(R.id.string_stats_lhs)).setText( pd.getLongestHS() + " m");
        ((TextView) findViewById(R.id.string_stats_skill)).setText( pd.getSkill() + "" );
        ((TextView) findViewById(R.id.string_stats_time)).setText( pd.getTimePlayedString() + "" );
        ((TextView) findViewById(R.id.string_stats_spm)).setText( pd.getScorePerMinute() + "" );
    	
    }
    
    public void setupFeed(ProfileInformation data) {
    	
    	//If data == null
    	if( data == null ) { return; }
    	
    	//Do we have it already?
		if( listFeed == null ) { 
			
			listFeed = ((ListView) findViewById(R.id.list_feed)); 
			registerForContextMenu(listFeed);
			
			if( profileInformation.isFriend() ) {
				
				findViewById(R.id.feed_update).setVisibility( View.VISIBLE );
				
			}
			
		}
        
		((TextView) findViewById(R.id.feed_username)).setText( data.getUsername() );
		
		//If we don't have it defined, then we need to set it
		if( listFeed.getAdapter() == null ) {
			
			//Create a new FeedListAdapter
			feedListAdapter = new FeedListAdapter(this, data.getFeedItems(), layoutInflater);
			listFeed.setAdapter( feedListAdapter );
			
			//Do we have the onClick?
			if( listFeed.getOnItemClickListener() == null ) {
				
				listFeed.setOnItemClickListener( 
						
					new OnItemClickListener() {
	
						@Override
						public void onItemClick( AdapterView<?> a, View v, int pos, long id ) {
	
							v.showContextMenu();
							/*final FeedItem currItem = (FeedItem) a.getItemAtPosition( pos );
							if( !currItem.getContent().equals( "" ) ) {
								
								generateDialogContent(CONTEXT, currItem.getProfile(0).getAccountName(), currItem.getContent()).show();
								
							}*/
							
						}
						
						
						
					}
						
				);
			
			}
			
		} else {
			
			feedListAdapter.setItemArray( profileInformation.getFeedItems() );
			feedListAdapter.notifyDataSetChanged();
		}
    }

    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

    	//Inflate!!
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_profileview, menu );		
		return super.onCreateOptionsMenu( menu );
	
    }
	
    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
    	
    	//Not loaded yet?
    	if( profileInformation == null ) { return super.onPrepareOptionsMenu( menu ); }
    	
    	//Our own profile, no need to show the "extra" buttons
    	if( profileData.getProfileId() == SessionKeeper.getProfileData().getProfileId() ) {

			menu.removeItem( R.id.option_friendadd );
			menu.removeItem( R.id.option_frienddel );
			menu.removeItem( R.id.option_compare );
			menu.removeItem( R.id.option_unlocks );
	
    	} else {
    	
	    	//Which tab is operating?
	    	if( mTabHost.getCurrentTab() == 0 ) {			
					
				if( profileInformation.getAllowFriendRequests() ) {
					
					if( profileInformation.isFriend() ) {

						((MenuItem) menu.findItem( R.id.option_friendadd )).setVisible( false );
						((MenuItem) menu.findItem( R.id.option_frienddel )).setVisible( true );	
				
					} else {

						((MenuItem) menu.findItem( R.id.option_friendadd )).setVisible( true );
						((MenuItem) menu.findItem( R.id.option_frienddel )).setVisible( false );
					}
					
				} else {

					((MenuItem) menu.findItem( R.id.option_friendadd )).setVisible( false );
					((MenuItem) menu.findItem( R.id.option_frienddel )).setVisible( false );
					
				}
				
				((MenuItem) menu.findItem( R.id.option_compare )).setVisible( false );
			
			} else if( mTabHost.getCurrentTab() == 1 ) {
				
				((MenuItem) menu.findItem( R.id.option_friendadd )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_frienddel )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_compare )).setVisible( true );
				((MenuItem) menu.findItem( R.id.option_unlocks )).setVisible( true );
				
				
			} else if( mTabHost.getCurrentTab() == 2 ) {
				
				((MenuItem) menu.findItem( R.id.option_friendadd )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_frienddel )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_compare )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_unlocks )).setVisible( false );
				
			} else {
				
				menu.removeItem( R.id.option_friendadd );
				menu.removeItem( R.id.option_frienddel );
				menu.removeItem( R.id.option_compare );
				menu.removeItem( R.id.option_unlocks );
				
			}

		}
    	
    	return super.onPrepareOptionsMenu( menu );
    	
    }
    
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		//Let's act!
		if( item.getItemId() == R.id.option_reload ) {
	
			this.reloadLayout();
			
		} else if( item.getItemId() == R.id.option_back ) {
			
			((Activity) this).finish();
			
		} else if( item.getItemId() == R.id.option_friendadd ) {
			
			new AsyncFriendRequest(this, profileData.getProfileId()).execute( 
					
				sharedPreferences.getString( 
						
					Constants.SP_BL_CHECKSUM, 
					"" 
				)
			
			);
				
			
		} else if( item.getItemId() == R.id.option_frienddel ) {
			
			new AsyncFriendRemove(this, profileData.getProfileId()).execute( 
					
				sharedPreferences.getString( 
						
					Constants.SP_BL_CHECKSUM, 
					"" 
				)
			
			);
				
			
		} else if( item.getItemId() == R.id.option_frienddel ) {
		
			Toast.makeText( this, R.string.msg_unimplemented, Toast.LENGTH_SHORT).show();
		
		} else if( item.getItemId() == R.id.option_compare ) {
			
			startActivity(
			
				new Intent(
					
					this,
					CompareView.class
						
				).putExtra(
						
					"profile1",
					SessionKeeper.getProfileData()
				
				).putExtra(
						
					"profile2",
					profileData
				
				).putExtra(
						
					"selectedPosition", 
					selectedPosition
					
					
				)
					
			);
			
		} else if( item.getItemId() == R.id.option_unlocks ) {
			
			int position = 0;
			for( long key : personaStats.keySet() ) {
				
				if( key == selectedPersona ) {
					
					break;
					
				} else {
					
					position++;
					
				}
				
			}
			
			startActivity(
			
				new Intent(
					
					this,
					UnlockView.class
						
				).putExtra(
						
					"profile",
					profileData
				
				).putExtra(
						
					"selectedPosition",
					position
				
				)
					
			);
			
		}
	
		// Return true yo
		return true;

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
        initLayout();
		
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
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

		//Grab the info
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		//Show the menu
		FeedItem feedItem = (FeedItem) info.targetView.getTag();
		
		//Show the menu
		if( !feedItem.isLiked() ) {
			
			menu.add( 0, 0, 0, R.string.label_hooah);
		
		} else {
			
			menu.add( 0, 0, 0, R.string.label_unhooah);
			
		}
		menu.add( 0, 1, 0, R.string.label_single_post_view);
		menu.add( 0, 2, 0, getString(R.string.label_goto_section).replace( "{section}", feedItem.getOptionTitle( this ) ) );

		return;
	
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		//Declare...
		AdapterView.AdapterContextMenuInfo info;
		
		//Let's try to get some menu information via a try/catch
		try {
			
		    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		
		} catch (ClassCastException e) {
		
			e.printStackTrace();
			return false;
		
		}
		
		try {
			
			//Divide & conquer 
			if( item.getGroupId() == 0 ) {
				
				//Grab the data
				FeedItem feedItem = (FeedItem) info.targetView.getTag();
				
				//REQUESTS
				if( item.getItemId() == 0 ) {
						
					new AsyncFeedHooah(
							
						this, 
						info.id, 
						false,
						feedItem.isLiked(),
						new AsyncProfileRefresh(
								
							this,
							SessionKeeper.getProfileData().getProfileId()
							
						)
					
					).execute( 
							
						sharedPreferences.getString( 
								
							Constants.SP_BL_CHECKSUM, 
							""
							
						) 
					
					);
				
				} else if( item.getItemId() == 1 ){
					
					//Yeah
					startActivity(
							
						new Intent(
								
							this, 
							SinglePostView.class
							
						).putExtra(
								
							"feedItem",
							feedItem 
								
						).putExtra(
								
							"canComment",
							profileInformation.isFriend()	
						
						).putExtra( 
								
							"profileId",
							profileInformation.getUserId()
							
						)
						
					);
					
				} else if( item.getItemId() == 2 ) {
					
					if( feedItem.getIntent( this ) != null ) { startActivity(feedItem.getIntent( this )); }
					
				}
				
			}
			
		} catch( Exception ex ) {
		
			ex.printStackTrace();
			return false;
			
		}

		return true;
	}
	
	public void onClick(View v) {
	
		if( v.getId() == R.id.button_send ) {
			
			//Is it non-existent
			if( fieldMessage == null ) { fieldMessage = (EditText) findViewById(R.id.field_message); }
			
			//Empty message?
			if( fieldMessage.getText().toString().equals( "" ) ) {
				
				Toast.makeText(CONTEXT, R.string.info_empty_msg, Toast.LENGTH_SHORT).show();
				return;
				
			}
			
			new AsyncPostToWall(
			
				CONTEXT, 
				profileData.getProfileId(),
				false
				
			).execute(
					
				sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ),
				fieldMessage.getText().toString()
				
			);
			fieldMessage.setText("");
			
		} else if( v.getId() == R.id.wrap_persona ) {
			
			//Let's get it on!
			generateDialogPersonaList(this, profileInformation.getAllPersonas(), profileInformation.getAllPersonaNames()).show();
			
		}

	}
	
	public Dialog generateDialogContent(final Context context, final String username, final String content) {
		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	    final View layout = inflater.inflate(R.layout.dialog_feed_content, (ViewGroup) findViewById(R.id.dialog_root));
		
	    //Set the title and the view
		builder.setTitle( username );
		builder.setView(layout);

		//Grab the fields
		((TextView) layout.findViewById(R.id.feed_content)).setText( content );
		
		//Dialog options
		builder.setPositiveButton(
				
			android.R.string.ok, 
			new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
			      
					dialog.dismiss();
			   
				}
				
			}
			
		);
		
		//Padding fix
		AlertDialog theDialog = builder.create();
	    theDialog.setView( layout, 0, 0, 0, 0);
	    return theDialog;
		
	}
	
	public Dialog generateDialogPersonaList( final Context context, final long[] personaId, final String[] persona ) {
	
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
	    //Set the title and the view
		builder.setTitle( R.string.info_dialog_soldierselect );
		
		builder.setSingleChoiceItems(
				
			persona, -1, new DialogInterface.OnClickListener() {
		  
				public void onClick(DialogInterface dialog, int item) {
			    	
					if( personaId[item] != selectedPersona ) {
						
						//Update it
						selectedPersona = personaId[item];
					
						//Load the new!
						setupStats(personaStats.get( selectedPersona ));
						
						//Store selectedPersonaPos
						selectedPosition = item;
						
					}
					
					dialog.dismiss();
		
				}
				
			}
		
		);
		
		//CREATE
		return builder.create();
		
	}

}