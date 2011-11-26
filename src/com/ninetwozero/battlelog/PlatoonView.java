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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.ninetwozero.battlelog.asynctasks.AsyncFriendRequest;
import com.ninetwozero.battlelog.asynctasks.AsyncPostToWall;
import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.PlayerData;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.PlatoonInformation;
import com.ninetwozero.battlelog.datatypes.SerializedCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class PlatoonView extends TabActivity {

	//Attributes
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private ProgressBar progressBar;
	private PlatoonData platoonData;
	private PlayerData playerData;
	private PlatoonInformation platoonInformation;
	private TabHost mTabHost;
	private FeedListAdapter feedListAdapter;
	
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
        
        //Prepare to tango
        this.sharedPreferences = this.getSharedPreferences( Constants.fileSharedPrefs, 0);
        this.layoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        
    	//Get the intent
        if( getIntent().hasExtra( "platoon" ) ) {
        	
        	platoonData = (PlatoonData) getIntent().getSerializableExtra( "platoon" );
        	
        }
        
        //Is the profileData null?!
        if( platoonData == null || platoonData.getId() == 0 ) { finish(); return; }
    	
    	//Set the content view
        setContentView(R.layout.platoon_view);
        
        //Fix the tabs
    	mTabHost = (TabHost) findViewById(android.R.id.tabhost);
    	setupTabs(
    			
    		new String[] { "Home", "Stats", "Feed" }, 
    		new int[] { R.layout.tab_content_overview_platoon, R.layout.tab_content_stats, R.layout.tab_content_feed } );
        
        initLayout();
	}        

	public void initLayout() {
		
		//Eventually get a *cached* version instead    
		new AsyncPlatoonRefresh(this, false, platoonData, sharedPreferences.getLong( "battlelog_profile_id", 0 )).execute();
		
	}
	
    public void reloadLayout() {
    	
    	//ASYNC!!!
    	new AsyncPlatoonRefresh(this, true, platoonData, sharedPreferences.getLong( "battlelog_profile_id", 0 )).execute();
    	
    	
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
    
    public class AsyncPlatoonRefresh extends AsyncTask<Void, Void, Boolean> {
    
    	//Attributes
    	private Context context;
    	private ProgressDialog progressDialog;
    	private PlatoonData platoonData;
    	private long activeProfileId;
    	private boolean hideDialog;
    	
    	public AsyncPlatoonRefresh(Context c, boolean f, PlatoonData pd, long pId) {
    		
    		this.context = c;
    		this.hideDialog = f;
    		this.platoonData = pd;
    		this.progressDialog = null;
    		this.activeProfileId = pId;
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		
    		//Do we?
    		if( !hideDialog ) {

    			//Let's see
				this.progressDialog = new ProgressDialog(this.context);
				this.progressDialog.setTitle("Please wait");
				this.progressDialog.setMessage( "Downloading the data..." );
				this.progressDialog.show();
    		
    		}	
    	
    	}

		@Override
		protected Boolean doInBackground( Void... arg0 ) {
			
			try {
				
				//Get...
				platoonInformation = WebsiteHandler.getProfileInformationForPlatoon( this.platoonData, this.activeProfileId);
				
				//...validate!
				if( platoonInformation == null ) { 
					
					return false; 
				
				} else {
					
					return true;
				
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
				
				if ( !hideDialog ) { if( this.progressDialog != null ) this.progressDialog.dismiss(); }
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
								drawHome(platoonInformation);
								break;
								
							case 1:
								drawStats(playerData);
								break;
							
							case 2:
								drawFeed(platoonInformation);
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
					drawHome(platoonInformation);
					break;
					
				case 1:
					drawStats(playerData);
					break;
				
				case 2:
					drawFeed(platoonInformation);
					break;
					
				default:
					break;
		
			}
			
			//Done!
	        if( this.progressDialog != null && !hideDialog ) this.progressDialog.dismiss();
	        
	        //Get back here!
	        return;
		        
		}
		
    }
    
    public final void drawHome(PlatoonInformation data) {
    	
    	//Let's start drawing the... layout
    	((TextView) findViewById(R.id.text_name)).setText( data.getName() );
    	
    	//Set the *created*
    	((TextView) findViewById(R.id.text_date)).setText( 
    			
    		PublicUtils.getDate( data.getDate(), "Created on" ) + " (" +
    		PublicUtils.getRelativeDate( data.getDate() ) + ")"
    	);
    	
    	//Do we have a link?!
    	if( data.getWebsite() != null && !data.getWebsite().equals( "" ) ) {
    		
    		((TextView) findViewById(R.id.text_web)).setText( data.getWebsite() );
    		
    	}
    	//Do we have a presentation?
    	if( data.getPresentation() != null && !data.getPresentation().equals( "" ) ) {
    		
    		((TextView) findViewById(R.id.text_presentation)).setText( data.getPresentation() );
		
    	} else {
    		
    		((TextView) findViewById(R.id.text_presentation)).setText( "No presentation found." );

    	}
    	
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
        ((TextView) findViewById(R.id.string_stats_skill)).setText( pd.getNumVehicles() + "" );
        ((TextView) findViewById(R.id.string_stats_time)).setText( pd.getTimePlayedString() + "" );
        ((TextView) findViewById(R.id.string_stats_spm)).setText( pd.getScorePerMinute() + "" );
    	
    }
    
    public void drawFeed(PlatoonInformation data) {
    	
    	//Do we have it already?
		if( listFeed == null ) { 
			
			listFeed = ((ListView) findViewById(R.id.list_feed)); 
			registerForContextMenu(listFeed);
			
		}
        
		((TextView) findViewById(R.id.feed_username)).setText( data.getName() );
        
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
	
							if( !((FeedItem) a.getItemAtPosition( pos ) ).getContent().equals( "" ) ) {
								
								View viewContainer = (View) v.findViewById(R.id.wrap_contentbox);
								viewContainer.setVisibility( ( viewContainer.getVisibility() == View.GONE ) ? View.VISIBLE : View.GONE );
							
							}
							
						}
						
						
						
					}
						
				);
			
			}
			
		} else {
			
			feedListAdapter.setItemArray( platoonInformation.getFeedItems() );
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
    	
    	//Our own profile, no need to show the "extra" buttons
		if( mTabHost.getCurrentTab() == 0 ) {			
					
			if( platoonInformation.isOpenForNewMembers() ) {
					
				if( platoonInformation.isOpenForNewMembers() ) {

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
			((MenuItem) menu.findItem( R.id.option_newpost )).setVisible( false );
		
		} else if( mTabHost.getCurrentTab() == 1 ) {
			
			((MenuItem) menu.findItem( R.id.option_friendadd )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_frienddel )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_compare )).setVisible( true );
			((MenuItem) menu.findItem( R.id.option_newpost )).setVisible( false );
			
		} else if( mTabHost.getCurrentTab() == 2 ) {
			
			((MenuItem) menu.findItem( R.id.option_friendadd )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_frienddel )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_compare )).setVisible( false );
			
		} else {
			
			menu.removeItem( R.id.option_friendadd );
			menu.removeItem( R.id.option_frienddel );
			menu.removeItem( R.id.option_compare );
			menu.removeItem( R.id.option_newpost );
			
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
			
			new AsyncFriendRequest(this, platoonData.getId()).execute( 
					
				sharedPreferences.getString( 
						
					"battlelog_post_checksum", 
					"" 
				)
			
			);
				
			
		} else if( item.getItemId() == R.id.option_frienddel ) {
		
			Toast.makeText( this, "Friends can't be deleted at this time, currently looking for a solution!", Toast.LENGTH_SHORT).show();
		
		} else if( item.getItemId() == R.id.option_compare ) {
			
			Toast.makeText( this, "You can't compare platoons... duh", Toast.LENGTH_SHORT).show();
			
		} else if( item.getItemId() == R.id.option_newpost ) {
			
			generateDialogPost(this).show();
			
		}
	
		// Return true yo
		return true;

	}  
	
	@Override
	public void onResume() {
		
		super.onResume();
		this.reloadLayout();
		
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
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

		//Grab the info
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		//Show the menu
		if( !((FeedItem) ((View) info.targetView).getTag()).isLiked() ) {
			
			menu.add( 0, 0, 0, "Hooah!");
		
		} else {
			
			menu.add( 0, 0, 0, "Un-hooah!");
			
		}
		menu.add( 0, 1, 0, "View comments");

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
				
				//REQUESTS
				if( item.getItemId() == 0 ) {
						
					new AsyncFeedHooah(
							
						this, 
						info.id, 
						false,
						( (FeedItem)info.targetView.getTag()).isLiked(),
						new AsyncPlatoonRefresh(
								
							this, 
							true, 
							platoonData,
							sharedPreferences.getLong( "battlelog_profile_id", 0 )
							
						)
					
					).execute( 
							
						sharedPreferences.getString( 
								
							"battlelog_post_checksum", 
							""
							
						) 
					
					);
				
				} else if( item.getItemId() == 1 ){
					
					//Yeah
					startActivity(
							
						new Intent(
								
							this, 
							CommentView.class
							
						).putExtra(
								
							"comments", 
							(ArrayList<CommentData>) ((FeedItem) info.targetView.getTag()).getComments()
					
						).putExtra( 

							"postId", 
							((FeedItem) info.targetView.getTag()).getId()
							
						).putExtra( 
								
							"platoonId",
							platoonInformation.getId()
							
						)
						
					);
					
				}
				
			}
			
		} catch( Exception ex ) {
		
			ex.printStackTrace();
			return false;
			
		}

		return true;
	}
	
	public Dialog generateDialogPost(final Context context) {
		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	    final View layout = inflater.inflate(R.layout.dialog_newpost, (ViewGroup) findViewById(R.id.dialog_root));
		
	    //Set the title and the view
		builder.setTitle("New wall post");
		builder.setView(layout);

		//Grab the fields
		final EditText fieldMessage = (EditText) layout.findViewById(R.id.field_message);
		
		//Dialog options
		builder.setNegativeButton(
				
			android.R.string.cancel, 
			
			new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int whichButton) { 
					
					dialog.dismiss(); 
					
				}
				
			}
			
		);
			 
		builder.setPositiveButton(
				
			android.R.string.ok, 
			new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
			      
					
					new AsyncPostToWall(
							
						context, 
						platoonData.getId()
						
					).execute(
							
						sharedPreferences.getString( "battlelog_post_checksum", "" ),
						fieldMessage.getText().toString()
						
					);
			   
				}
				
			}
			
		);
		
		//CREATE
		return builder.create();
		
	}
}