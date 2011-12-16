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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.FeedListAdapter;
import com.ninetwozero.battlelog.adapters.PlatoonMemberData;
import com.ninetwozero.battlelog.adapters.PlatoonUserListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncComRequest;
import com.ninetwozero.battlelog.asynctasks.AsyncFeedHooah;
import com.ninetwozero.battlelog.asynctasks.AsyncPlatoonRequest;
import com.ninetwozero.battlelog.asynctasks.AsyncPlatoonRespond;
import com.ninetwozero.battlelog.asynctasks.AsyncPostToWall;
import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.PlatoonInformation;
import com.ninetwozero.battlelog.datatypes.PlatoonStats;
import com.ninetwozero.battlelog.datatypes.PlatoonStatsItem;
import com.ninetwozero.battlelog.datatypes.PlatoonTopStatsItem;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class PlatoonView extends TabActivity {

	//Attributes
	private final Context CONTEXT = this;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private PlatoonData platoonData;
	private PlatoonInformation platoonInformation;
	private TabHost mTabHost;
	
	//Elements
	private View cacheView, cacheView2;
	private RelativeLayout wrapGeneral, wrapScore, wrapSPM, wrapTime, wrapTopList;
	private ListView listFeed, listUsers;
	private TableLayout tableScores, tableSPM, tableTime, tableTopList;
	private TableRow cacheTableRow;
	private FeedListAdapter feedListAdapter;
	private PlatoonUserListAdapter platoonUserListAdapter;
	private Bitmap platoonBadge;
	private ImageView imageViewBadge;
	private EditText fieldMessage;
	
	//CONTROLLERS 
	private final int VIEW_MEMBERS = 0, VIEW_FANS = 1;
	private boolean isViewingMembers = true;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		RequestHandler.setCookies(
    				
    			(ArrayList<ShareableCookie> ) icicle.getParcelable(Constants.SUPER_COOKIES) 
    			
    		);
    	
    	}
        
        //Prepare to tango
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.layoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        
    	//Get the intent
        if( getIntent().hasExtra( "platoon" ) ) { platoonData = (PlatoonData) getIntent().getParcelableExtra( "platoon" ); }
        
        //Is the profileData null?!
        if( platoonData == null || platoonData.getId() == 0 ) { finish(); return; }
    	
    	//Set the content view
        setContentView(R.layout.platoon_view);
        
        //Fix the tabs
    	mTabHost = (TabHost) findViewById(android.R.id.tabhost);
    	setupTabs(
    			
    		new String[] { 
    				
				getString(R.string.label_home ), 
				getString(R.string.label_stats ), 
				getString(R.string.label_users ), 
    			getString(R.string.label_feed ) 
    			
    		}, 
    		new int[] { 
    				
				R.layout.tab_content_platoon_overview, 
				R.layout.tab_content_platoon_stats, 
				R.layout.tab_content_platoon_users, 
				R.layout.tab_content_platoon_feed 
				
    		}
    		
    	);
        
        //Let's see
    	initLayout();
    	
	}        

	public void initLayout() {
		
		//Eventually get a *cached* version instead    
		new AsyncPlatoonRefresh(this, false, platoonData, sharedPreferences.getLong( Constants.SP_BL_PROFILE_ID, 0 ), true).execute();
		
	}
	
    public void reloadLayout() {
    	
    	//ASYNC!!!
    	new AsyncPlatoonRefresh(this, true, platoonData, sharedPreferences.getLong( Constants.SP_BL_PROFILE_ID, 0 ), false).execute();
    	
    	
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
    
    public class AsyncPlatoonRefresh extends AsyncTask<Void, Void, Boolean> {
    
    	//Attributes
    	private Context context;
    	private ProgressDialog progressDialog;
    	private PlatoonData platoonData;
    	private long activeProfileId;
    	private boolean hideDialog, loadImage;
    	
    	public AsyncPlatoonRefresh(Context c, boolean f, PlatoonData pd, long pId, boolean lImg) {
    		
    		this.context = c;
    		this.hideDialog = f;
    		this.platoonData = pd;
    		this.progressDialog = null;
    		this.activeProfileId = pId;
    		this.loadImage = lImg;
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		
    		//Do we?
    		if( !hideDialog ) {

    			//Let's see
				this.progressDialog = new ProgressDialog(this.context);
				this.progressDialog.setTitle(context.getString( R.string.general_wait ));
				this.progressDialog.setMessage( context.getString( R.string.general_downloading ) );
				this.progressDialog.show();
    		
    		}	
    	
    	}

		@Override
		protected Boolean doInBackground( Void... arg0 ) {
			
			try {
				
				//Get...
				platoonInformation = WebsiteHandler.getProfileInformationForPlatoon(
					
					context,
					this.platoonData, 
					sharedPreferences.getInt( Constants.SP_BL_NUM_FEED, Constants.DEFAULT_NUM_FEED ),
					this.activeProfileId,
					this.loadImage
					
				);
				
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
				Toast.makeText( this.context, R.string.general_no_data, Toast.LENGTH_SHORT).show(); 
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
								setupHome(platoonInformation);
								break;
								
							case 1:
								setupStats(platoonInformation.getStats());
								break;
								
							case 2:
								setupUsers(platoonInformation);
								break;
								
							case 3:
								setupFeed(platoonInformation);
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
					setupHome(platoonInformation);
					break;
					
				case 1:
					setupStats(platoonInformation.getStats());
					break;
				
				case 2:
					setupUsers(platoonInformation);
					break;
					
				case 3:
					setupFeed(platoonInformation);
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
    
    public final void setupHome(PlatoonInformation data) {
    	
    	//Let's start by getting an ImageView
		if( imageViewBadge == null ) { imageViewBadge = (ImageView) findViewById(R.id.image_badge); }
		
		//Set some TextViews
    	((TextView) findViewById(R.id.text_name_platoon)).setText( data.getName() + " [" + data.getTag() + "]" );
    	((TextView) findViewById(R.id.text_date)).setText( 
    			
    		getString(R.string.info_platoon_created ).replace( 
    				
    			"{DATE}", 
    			PublicUtils.getDate( data.getDate() )
    			
    		).replace(
    				
    			"{RELATIVE DATE}",
    			PublicUtils.getRelativeDate( CONTEXT, data.getDate() )
    			
    		)
    	);

    	//Platform!!
    	switch( data.getPlatformId() ) {
    		
    		case 1:
    			((ImageView) findViewById(R.id.image_platform)).setImageResource( R.drawable.logo_pc );
    			break;
    			
    		case 2:
    			((ImageView) findViewById(R.id.image_platform)).setImageResource( R.drawable.logo_xbox );
    			break;
    			
    		case 4:
    			((ImageView) findViewById(R.id.image_platform)).setImageResource( R.drawable.logo_ps3 );
    			break;
    			
			default: 
    			((ImageView) findViewById(R.id.image_platform)).setImageResource( R.drawable.logo_pc );
				break;
    		
    	}
    	
    	//Is the platoon badge null?
    	if( data.hasImage() ) {
    		
    		//Set the properties
    		imageViewBadge.setImageBitmap( platoonBadge = data.getImage() );
    	
    	}
    	
    	//Do we have a link?!
    	if( data.getWebsite() != null && !data.getWebsite().equals( "" ) ) {
    		
    		((TextView) findViewById(R.id.text_web)).setText( data.getWebsite() );
    		((View) findViewById(R.id.wrap_web)).setTag( data.getWebsite() );
    		
    	} else {
    		
    		((View) findViewById(R.id.wrap_web)).setVisibility( View.GONE );
    		
    	}
    	//Do we have a presentation?
    	if( data.getPresentation() != null && !data.getPresentation().equals( "" ) ) {
    		
    		((TextView) findViewById(R.id.text_presentation)).setText( data.getPresentation() );
		
    	} else {
    		
    		((TextView) findViewById(R.id.text_presentation)).setText( R.string.info_profile_empty_pres );

    	}
    	
    }
    
    public void setupStats(PlatoonStats pd) {
    	
    	//Let's start drawing the... layout
    	((TextView) findViewById(R.id.text_name_platoon)).setText( pd.getName() );
    	
    	//Are they null?
    	if( wrapGeneral == null ) {
	    
    		//General ones
    		wrapGeneral = (RelativeLayout) findViewById(R.id.wrap_general);
	    	
    		//Kits & vehicles
    		wrapScore = (RelativeLayout) findViewById(R.id.wrap_score);
    		wrapSPM = (RelativeLayout) findViewById(R.id.wrap_spm);
    		wrapTime = (RelativeLayout) findViewById(R.id.wrap_time);
	    	tableScores = (TableLayout) wrapScore.findViewById( R.id.tbl_stats );
    		tableSPM = (TableLayout) wrapSPM.findViewById( R.id.tbl_stats );
    		tableTime = (TableLayout) wrapTime.findViewById( R.id.tbl_stats );
	    	
    		//Top list
    		wrapTopList = (RelativeLayout) findViewById(R.id.wrap_toplist);
    		tableTopList = (TableLayout) wrapTopList.findViewById( R.id.tbl_stats );
	    	
    	} else {
    		
    		tableScores.removeAllViews();
    		tableSPM.removeAllViews();
    		tableTime.removeAllViews();
    		tableTopList.removeAllViews();
    		
    	}
    	
    	//Let's grab the different data
    	PlatoonStatsItem generalSPM = pd.getGlobalTop().get(0);
    	PlatoonStatsItem generalKDR = pd.getGlobalTop().get(1);
    	PlatoonStatsItem generalRank = pd.getGlobalTop().get(2);

    	//Set the general stats
    	( (TextView) wrapGeneral.findViewById( R.id.text_average_spm )).setText( generalSPM.getAvg() + "" );
    	( (TextView) wrapGeneral.findViewById( R.id.text_max_spm )).setText( generalSPM.getMax() + "" );
    	( (TextView) wrapGeneral.findViewById( R.id.text_mid_spm )).setText( generalSPM.getMid() + "" );
    	( (TextView) wrapGeneral.findViewById( R.id.text_min_spm )).setText( generalSPM.getMin() + "" );
    	( (TextView) wrapGeneral.findViewById( R.id.text_average_rank )).setText( generalRank.getAvg() + "" );
    	( (TextView) wrapGeneral.findViewById( R.id.text_max_rank )).setText( generalRank.getMax() + "" );
    	( (TextView) wrapGeneral.findViewById( R.id.text_mid_rank )).setText( generalRank.getMid() + "" );
    	( (TextView) wrapGeneral.findViewById( R.id.text_min_rank )).setText( generalRank.getMin() + "" );
    	( (TextView) wrapGeneral.findViewById( R.id.text_average_kdr )).setText( generalKDR.getDAvg() + "" );
    	( (TextView) wrapGeneral.findViewById( R.id.text_max_kdr )).setText( generalKDR.getDMax() + "" );
    	( (TextView) wrapGeneral.findViewById( R.id.text_mid_kdr )).setText( generalKDR.getDMid() + "" );
    	( (TextView) wrapGeneral.findViewById( R.id.text_min_kdr )).setText( generalKDR.getDMin() + "" );
    	
    	//Top Players
    	ArrayList<PlatoonTopStatsItem> topStats = pd.getTopPlayers();
    	PlatoonTopStatsItem tempTopStats = null;
    	
    	//Loop over them, *one* by *one*
    	int numCols = 2;
    	for( int i = 0, max = topStats.size(); i < max; i++ ) {
    		
    		//Oh well, couldn't quite cache it could we?
    		cacheView = (RelativeLayout) layoutInflater.inflate( R.layout.grid_item_platoon_top_stats, null );
    		
    		//Add the new TableRow
    		if( cacheTableRow == null || (i % numCols) == 0 ) { 
    			
    			tableTopList.addView( cacheTableRow = new TableRow(this) );
	    		cacheTableRow.setLayoutParams( 
	    				
					new TableRow.LayoutParams(
					
						TableLayout.LayoutParams.FILL_PARENT,
						TableLayout.LayoutParams.WRAP_CONTENT
							
					)
				
				);
    	
    		}
    		
    		//Add the *layout* into the TableRow
    		cacheTableRow.addView( cacheView );
    		
    		//Grab *this* item
    		tempTopStats = topStats.get(i);
    		
    		//Say cheese... (mister Bitmap)
    		if( tempTopStats.getProfile() != null ) {
    			
    			
    			((ImageView) cacheView.findViewById( R.id.image_avatar )).setImageBitmap( 
    				
					WebsiteHandler.bitmapCache.get( 
					
						tempTopStats.getProfile().getGravatarHash()
							
					) 
	    				
	    		);
    		
    		} else {
    			
    			((ImageView) cacheView.findViewById( R.id.image_avatar )).setImageResource(R.drawable.default_avatar);
    			
    		}
    		//Set the TextViews accordingly
    		( (TextView) cacheView.findViewById( R.id.text_label )).setText( tempTopStats.getLabel().toUpperCase() + "" );
    		( (TextView) cacheView.findViewById( R.id.text_name )).setText( tempTopStats.getProfile().getAccountName() + "" );
        	( (TextView) cacheView.findViewById( R.id.text_spm )).setText( tempTopStats.getSPM() + "" );
    			
    	}
    	
    	//Let's generate the table rows!
    	generateTableRows(tableScores, pd.getScores(), false );
    	generateTableRows(tableSPM, pd.getSpm(), false );
    	generateTableRows(tableTime, pd.getTime(), true );
    	
    }
    
    public final void setupUsers(PlatoonInformation data) {
    	
    	//Do we have the ListView?
    	if( listUsers == null ) {
    		
    		listUsers = (ListView) findViewById(R.id.list_users);
    		registerForContextMenu(listUsers);
    		
    	}
    	
    	//Do we have an adapter?
    	if( listUsers.getAdapter() == null ) {
    		
    		//Create new adapter & set it onto our ListView
    		platoonUserListAdapter = new PlatoonUserListAdapter(this, data.getMembers(), layoutInflater);
    		listUsers.setAdapter( platoonUserListAdapter );
			
    		//Do we have the onClick?
			if( listUsers.getOnItemClickListener() == null ) {
				
				listUsers.setOnItemClickListener( 
						
					new OnItemClickListener() {
	
						@Override
						public void onItemClick( AdapterView<?> a, View v, int pos, long id ) {
	
							startActivity(
									
								new Intent(CONTEXT, ProfileView.class).putExtra(
										
									"profile", 
									(PlatoonMemberData) v.getTag() 
									
								)
								
							);
							
						}
						
					}
						
				);
			
			}
    		
    	} else {
    		
			//Get the appropriate data
			if( isViewingMembers ) { 
				
				((PlatoonUserListAdapter)listUsers.getAdapter()).setProfileArray( platoonInformation.getMembers());
			
			} else {
				
				((PlatoonUserListAdapter)listUsers.getAdapter()).setProfileArray( platoonInformation.getFans());
			
			}
    			
    		//Update it!
			((PlatoonUserListAdapter)listUsers.getAdapter()).notifyDataSetChanged();
			
    	}
    	
    	//Which view are we on?
    	if( isViewingMembers ) { 
    		
    		((TextView) findViewById(R.id.text_name_platoon)).setText( R.string.label_members );
    		
    	} else { 
    		
    		((TextView) findViewById(R.id.text_name_platoon)).setText( R.string.label_fans );
    		
    	}
    	
    }
    
    public void setupFeed(PlatoonInformation data) {
    	
    	//Do we have it already?
		if( listFeed == null ) { 
			
			listFeed = ((ListView) findViewById(R.id.list_feed)); 
			registerForContextMenu(listFeed);
			
		}
        
		((TextView) findViewById(R.id.text_name_platoon)).setText( data.getName() );
        
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
	
							final FeedItem currItem = (FeedItem) a.getItemAtPosition( pos );
							if( !currItem.getContent().equals( "" ) ) {
								
								generateDialogContent(CONTEXT, currItem.getUsername()[0], currItem.getContent()).show();
								
							}
							
						}
						
					}
						
				);
			
			}
			
		} else {
			
			feedListAdapter.setItemArray( data.getFeedItems() );
			feedListAdapter.notifyDataSetChanged();
		}
    }

    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

    	//Inflate!!
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_platoonview, menu );		
		return super.onCreateOptionsMenu( menu );
	
    }
	
    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
    	
    	//Our own profile, no need to show the "extra" buttons
		if( mTabHost.getCurrentTab() == 0 ) {			
					
			if( platoonInformation.isOpenForNewMembers() ) {
					
				if( platoonInformation.isMember() ) {
					
					((MenuItem) menu.findItem( R.id.option_join )).setVisible( false );
					((MenuItem) menu.findItem( R.id.option_leave )).setVisible( true );
					((MenuItem) menu.findItem( R.id.option_fans )).setVisible( false );
					((MenuItem) menu.findItem( R.id.option_members )).setVisible( false );
										
				} else if( platoonInformation.isOpenForNewMembers() ) {

					((MenuItem) menu.findItem( R.id.option_join )).setVisible( true );
					((MenuItem) menu.findItem( R.id.option_leave )).setVisible( false );
					((MenuItem) menu.findItem( R.id.option_fans )).setVisible( false );
					((MenuItem) menu.findItem( R.id.option_members )).setVisible( false );
					
				} else {

					((MenuItem) menu.findItem( R.id.option_join )).setVisible( false );
					((MenuItem) menu.findItem( R.id.option_leave )).setVisible( false );
					((MenuItem) menu.findItem( R.id.option_fans )).setVisible( false );
					((MenuItem) menu.findItem( R.id.option_members )).setVisible( false );
				}
					
			} else {

				((MenuItem) menu.findItem( R.id.option_join )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_leave )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_fans )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_members )).setVisible( false );
				
			}
		
		} else if( mTabHost.getCurrentTab() == 1 ) {

			((MenuItem) menu.findItem( R.id.option_join )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_leave )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_fans )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_members )).setVisible( false );
			
		} else if( mTabHost.getCurrentTab() == 2 ) {
			
			if( isViewingMembers ) {
				
				((MenuItem) menu.findItem( R.id.option_join )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_leave )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_fans )).setVisible( true );
				((MenuItem) menu.findItem( R.id.option_members )).setVisible( false );
				
			} else {

				((MenuItem) menu.findItem( R.id.option_join )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_leave )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_fans )).setVisible( false );
				((MenuItem) menu.findItem( R.id.option_members )).setVisible( true );
				
			}
			
		} else if( mTabHost.getCurrentTab() == 3 ) {

			((MenuItem) menu.findItem( R.id.option_join )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_leave )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_fans )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_members )).setVisible( false );
			
		} else {
			
			((MenuItem) menu.findItem( R.id.option_join )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_leave )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_fans )).setVisible( false );
			((MenuItem) menu.findItem( R.id.option_members )).setVisible( false );
			
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
			
		} else if( item.getItemId() == R.id.option_members || item.getItemId() == R.id.option_fans ) {
			
			isViewingMembers = !isViewingMembers;
			setupUsers( platoonInformation );
			
		} else if( item.getItemId() == R.id.option_compare ) {
			
			Toast.makeText( this, R.string.info_platoon_compare, Toast.LENGTH_SHORT).show();
			
		} else if( item.getItemId() == R.id.option_join ) {
			
			new AsyncPlatoonRequest(

				this,
				platoonData.getId(),
				Dashboard.getProfile().getProfileId(),
				sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" )
				
			).execute(true);
			
		} else if( item.getItemId() == R.id.option_leave ) {
			
			new AsyncPlatoonRequest(

				this,
				platoonData.getId(),
				Dashboard.getProfile().getProfileId(),
				sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" )
				
			).execute(false);
		
		}
	
		// Return true yo
		return true;

	}  
	
	@Override
	public void onResume() {
		
		super.onResume();
		reloadLayout();
		
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
	
	public void onClick(View v) {
		
		//Check which view we clicked
		if( v.getId() == R.id.wrap_web ) {
			
			startActivity(
			
				new Intent(Intent.ACTION_VIEW).setData(
						
					Uri.parse(
							
						String.valueOf( v.getTag() ) 
						
					)
					
				)
					
			);
			
		} else if( v.getId() == R.id.button_send ) {
				
			//Is it non-existent
			if( fieldMessage == null ) { fieldMessage = (EditText) findViewById(R.id.field_message); }
			
			//Empty message?
			if( fieldMessage.getText().toString().equals("") ) {
				
				Toast.makeText(this, R.string.info_empty_msg, Toast.LENGTH_SHORT).show();
				return;
				
			}
			
			new AsyncPostToWall(
			
				this, 
				platoonData.getId(),
				true
				
			).execute(
					
				sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ),
				fieldMessage.getText().toString()
				
			);
			fieldMessage.setText("");
			
		}

	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

		//Grab the info
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		//Show the menu
		if( !((FeedItem) ((View) info.targetView).getTag()).isLiked() ) {
			
			menu.add( 0, 0, 0, R.string.label_hooah);
		
		} else {
			
			menu.add( 0, 0, 0, R.string.label_unhooah);
			
		}
		menu.add( 0, 1, 0, R.string.label_comment_view);

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
							sharedPreferences.getLong( Constants.SP_BL_PROFILE_ID, 0 ),
							false
							
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
							
						).putExtra( 
								
							"canComment",
							platoonInformation.isMember()
							
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
	
	public void generateTableRows(TableLayout parent, ArrayList<PlatoonStatsItem> stats, boolean isTime) {
	
		//Make sure the cache is null, as well as the table being cleared
    	cacheTableRow = null;
    	parent.removeAllViews();
		
		//Loop over them, *one* by *one*
    	if( stats != null ) {

        	//The number of items (-1) as the overall is a field that shouldn't be counted
        	int numItems = stats.size() - 1;
    		int avg;
    		
        	//Iterate over the stats
    		for( int i = 0, max = (numItems+1); i < max; i++ ) {
    		
    			//Set the average
    			avg = (i == 0) ? (stats.get(i).getAvg()/numItems) : stats.get( i ).getAvg();
    			
	    		//Is it null?
    			cacheView = (RelativeLayout) layoutInflater.inflate( R.layout.grid_item_platoon_stats, null );
	    		
	    		//Add the new TableRow
	    		if( cacheTableRow == null || (i % 3) == 0 ) { 
	    			
	    			parent.addView( cacheTableRow = new TableRow(this) );
		    		cacheTableRow.setLayoutParams( 
		    				
						new TableRow.LayoutParams(
						
							TableRow.LayoutParams.FILL_PARENT,
							TableRow.LayoutParams.WRAP_CONTENT
								
						)
					
					);
	    	
	    		}
	    		
	    		//Add the *layout* into the TableRow
	    		cacheTableRow.addView( cacheView );
	    		
	    		//Set the label
	    		( (TextView) cacheView.findViewById( R.id.text_label )).setText( stats.get(i).getLabel().toUpperCase() + "" );

	    		//If (i == 0) => Overall
	    		if( isTime ) {
	    			
		    		( (TextView) cacheView.findViewById( R.id.text_average )).setText( PublicUtils.timeToLiteral( avg ) );
		        	( (TextView) cacheView.findViewById( R.id.text_max )).setText( PublicUtils.timeToLiteral( stats.get(i).getMax()) );
		        	( (TextView) cacheView.findViewById( R.id.text_mid )).setText( PublicUtils.timeToLiteral( stats.get(i).getMid()) );
		        	( (TextView) cacheView.findViewById( R.id.text_min )).setText( PublicUtils.timeToLiteral( stats.get(i).getMin()) ); 

	    		} else {
	    			
		    		( (TextView) cacheView.findViewById( R.id.text_average )).setText( avg  + "" );
		        	( (TextView) cacheView.findViewById( R.id.text_max )).setText( stats.get(i).getMax() + "" );
		        	( (TextView) cacheView.findViewById( R.id.text_mid )).setText( stats.get(i).getMid() + "" );
		        	( (TextView) cacheView.findViewById( R.id.text_min )).setText( stats.get(i).getMin() + "" ); 
	    			
	    		}
	    		
	    	}
		
    	} else {
    		
    		//Create a new row
			parent.addView( cacheTableRow = new TableRow(this) );
    		cacheTableRow.setLayoutParams( 
    				
				new TableRow.LayoutParams(
				
					TableRow.LayoutParams.FILL_PARENT,
					TableRow.LayoutParams.WRAP_CONTENT
						
				)
			
			);
    		
    		//Add a TextView & set it up
    		cacheTableRow.addView( cacheView = new TextView(this) );
    		((TextView) cacheView).setText("No stats found.");
    		((TextView) cacheView).setGravity( Gravity.CENTER );
    		
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
		
		//CREATE
		return builder.create();
		
	}
	
	public void onRequestActionClick(View v) {

		new AsyncPlatoonRespond(
				
			this, 
			platoonData.getId(),
			((PlatoonMemberData)((View)v.getParent()).getTag()).getProfileId(),
			( v.getId() == R.id.button_accept )
		
		).execute(sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "")); 

	}

}