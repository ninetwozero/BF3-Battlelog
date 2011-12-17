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

import com.ninetwozero.battlelog.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.FeedListAdapter;
import com.ninetwozero.battlelog.adapters.FriendListAdapter;
import com.ninetwozero.battlelog.adapters.GridMenuAdapter;
import com.ninetwozero.battlelog.adapters.NotificationListAdapter;
import com.ninetwozero.battlelog.adapters.RequestListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncComRefresh;
import com.ninetwozero.battlelog.asynctasks.AsyncComRequest;
import com.ninetwozero.battlelog.asynctasks.AsyncFeedHooah;
import com.ninetwozero.battlelog.asynctasks.AsyncFetchDataToCompare;
import com.ninetwozero.battlelog.asynctasks.AsyncFetchDataToPlatoonView;
import com.ninetwozero.battlelog.asynctasks.AsyncFetchDataToProfileView;
import com.ninetwozero.battlelog.asynctasks.AsyncLogout;
import com.ninetwozero.battlelog.asynctasks.AsyncStatusUpdate;
import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.datatypes.DashboardItem;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.FriendListDataWrapper;
import com.ninetwozero.battlelog.datatypes.NotificationData;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class Dashboard extends TabActivity {

	//Attributes
	final private Context context = this;
	private DashboardItem[] menuArray;
	private String[] valueFieldsArray;
	private PostData[] postDataArray;
	private ArrayList<FeedItem> feedArray;
	private ArrayList<NotificationData> notificationArray;
	private FriendListDataWrapper friendListData;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	
	//Elements
	private View wrapFriendRequests;
	private TabHost mTabHost, cTabHost;
	private GridView gridMenu;
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
	
	//Async
	private AsyncFeedRefresh asyncFeedRefresh;
	private AsyncCOMReload asyncComReload;
	private AsyncLogout asyncLogout;
	
	//Misc
	private static ProfileData profile;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);

        //Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		RequestHandler.setCookies( (ArrayList<ShareableCookie> ) icicle.getParcelable(Constants.SUPER_COOKIES) );
    	
    	}
    	
    	//We should've gotten a profile
    	if( Dashboard.profile == null ) {
    		
    		if( getIntent().hasExtra( "myProfile" ) ) {
    			
    			Dashboard.profile = (ProfileData) getIntent().getParcelableExtra( "myProfile" );
    			
    		}
    		
    	}
    	
    	//Set the content view
        setContentView(R.layout.dashboard);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        //Setup the data
        feedArray = new ArrayList<FeedItem>();
        notificationArray = new ArrayList<NotificationData>();
    	friendListData = new FriendListDataWrapper(null, null, null);
        
        //Fix the tabs
    	mTabHost = (TabHost) findViewById(android.R.id.tabhost);
    	cTabHost = (TabHost) findViewById(R.id.com_tabhost);
    	cTabHost.setup();
    	
    	//Let's set them up
    	setupTabsPrimary(
    			
    		new String[] { 
    		
    			getString(R.string.label_menu ), 
    			getString(R.string.label_feed) 
    		
    		}, 
    		new int[] { 
    			
    			R.layout.tab_content_dashboard_menu, 
    			R.layout.tab_content_dashboard_feed 
    			
    		}

    	);
    	setupTabsSecondary(
    			
    		new String[] { 
    				
    			getString(R.string.label_friends ), 
				getString(R.string.label_notifications ) 
    							
    		}, 
    		new int[] { R.layout.tab_content_com_friends, R.layout.tab_content_com_notifications }
    		
    	);

        //Build the menu items
        menuArray = new DashboardItem[]{ 

    		new DashboardItem(Constants.MENU_MY_SOLDIER, getString(R.string.label_me )),
    		new DashboardItem(Constants.MENU_MY_PLATOON, getString( R.string.info_xml_platoons_my )),
        	new DashboardItem(Constants.MENU_SOLDIER, getString(R.string.label_find_soldier )),
        	new DashboardItem(Constants.MENU_PLATOON, getString(R.string.label_find_platoon )),
    		new DashboardItem(Constants.MENU_UNLOCKS, getString(R.string.label_unlocks )),
    		new DashboardItem(Constants.MENU_ASSIGNMENTS, getString( R.string.info_xml_assignments_my )),
        	new DashboardItem(Constants.MENU_COMPARE, getString(R.string.label_compare_bs)),
        	new DashboardItem(Constants.MENU_FORUM, getString(R.string.label_forum))
        	
        };
        
        //Setup COM & feed
        refreshFeed();
        setupCOM();

	}	
	
	public final void setupHome() {
    	
    	//Let's see
		if( gridMenu == null ) {
			
			gridMenu = (GridView) findViewById(R.id.grid_menu);
			gridMenu.setAdapter( new GridMenuAdapter(this, menuArray, layoutInflater ) );
		
			//Do we have the onClick?
			if( gridMenu.getOnItemClickListener() == null ) {
				
				gridMenu.setOnItemClickListener( 
					
					new OnItemClickListener() {

						@Override
						public void onItemClick( AdapterView<?> a, View v, int p, long id ) {

							onMenuClick( id );
								
						}
						
					}
						
				);
				
			}
			
		}
		
    	
    }
    
	public void setupFeed(ArrayList<FeedItem> items) {
    	
    	//Do we have it already? If no, we init
		if( listFeed == null ) { 
			
			//Get the ListView
			listFeed = ((ListView) findViewById(R.id.list_feed)); 
			feedStatusText = ((TextView) findViewById(R.id.status_feed));
			registerForContextMenu(listFeed);

			//Set the attributes
	        fieldStatusUpdate = (EditText) findViewById(R.id.field_status);
	        valueFieldsArray = new String[2];
	        postDataArray = new PostData[2];
	        

		}
		

		//If empty --> show other
		if( items == null || items.size() == 0 ) {
		
			feedStatusText.setVisibility( View.VISIBLE );
			
		} else {
			
			feedStatusText.setVisibility( View.GONE );
			
		}
        
		//If we don't have it defined, then we need to set it
		if( listFeed.getAdapter() == null ) {

			//Create a new FeedListAdapter
			feedListAdapter = new FeedListAdapter(this, items, layoutInflater);
			listFeed.setAdapter( feedListAdapter );
				
			//Do we have the onClick?
			if( listFeed.getOnItemClickListener() == null ) {
				
				listFeed.setOnItemClickListener( 
						
					new OnItemClickListener() {
	
						@Override
						public void onItemClick( AdapterView<?> a, View v, int pos, long id ) {
	
							final FeedItem currItem = (FeedItem) a.getItemAtPosition( pos );
							if( !currItem.getContent().equals( "" ) ) {
								
								generateDialogContent(context, currItem.getUsername()[0], currItem.getContent()).show();
								
							}
							
						}
						
						
					}
						
				);
			
			}
			
		} else {
			
			feedListAdapter.setItemArray( items );
			feedListAdapter.notifyDataSetChanged();
		
		}
		
    }

	public void setupFriendList( FriendListDataWrapper items ) {

		//Do we have it already? If no, we init
		if( listFriends == null ) { 
			
			//Get the ListView
			listFriends = ((ListView) findViewById(R.id.list_friends)); 
			listFriends.setOnItemClickListener( onItemClickListener );
		
			registerForContextMenu(listFriends);
	        
		}
		
		if( listFriendRequests == null ) {
			
			//Get the ListView
			listFriendRequests = ((ListView) findViewById(R.id.list_requests)); 
			listFriendRequests.setOnItemClickListener( onItemClickListener );
			friendsStatusText = ((TextView) findViewById(R.id.text_status_friends));
			
			registerForContextMenu(listFriendRequests);
			
			//Get the wrap
			wrapFriendRequests = findViewById(R.id.wrap_friends_requests);
		
		}
		
	
		//If empty --> show other
		if( items != null ) {
		
			boolean hasRequests = !(items.getRequests() == null || items.getRequests().size() == 0);
			boolean hasFriends = !(items.getFriends() == null || items.getFriends().size() == 0);
			
			if( !hasRequests && !hasFriends ) {

				if( friendsStatusText.getVisibility() == View.GONE ) { friendsStatusText.setVisibility( View.VISIBLE ); }
				if( wrapFriendRequests.getVisibility() == View.VISIBLE ) { wrapFriendRequests.setVisibility( View.GONE ); }
				
			} else {
				
				if( !hasRequests ) {
					
					if( wrapFriendRequests.getVisibility() == View.VISIBLE ) { 

						wrapFriendRequests.setVisibility( View.GONE ); 
					}

					friendsStatusText.setVisibility( View.GONE  );	
					
				} else if( !hasFriends ) {

					if( wrapFriendRequests.getVisibility() == View.GONE ) { wrapFriendRequests.setVisibility( View.VISIBLE  ); }
					if( friendsStatusText.getVisibility() == View.GONE ) { friendsStatusText.setVisibility( View.VISIBLE ); }
					
				} else {
				
					if( friendsStatusText.getVisibility() == View.VISIBLE ) { friendsStatusText.setVisibility( View.GONE ); }
					if( wrapFriendRequests.getVisibility() == View.GONE ) { wrapFriendRequests.setVisibility( View.VISIBLE  ); }
					
				}
		
			}

			
			//If we don't have it defined, then we need to set it
			if( listFriends.getAdapter() == null ) {
		
				//Create a new NotificationListAdapter
				friendListAdapter = new FriendListAdapter(this, items.getFriends(), layoutInflater);
				listFriends.setAdapter( friendListAdapter );
				
			} else {
	
				friendListAdapter.setItemArray( items.getFriends() );
				friendListAdapter.notifyDataSetChanged();
				
			}
			
			if( listFriendRequests.getAdapter() == null ) {

				//Create a new NotificationListAdapter
				friendRequestListAdapter = new RequestListAdapter(this, items.getRequests(), layoutInflater);
				listFriendRequests.setAdapter( friendRequestListAdapter );
			
			} else {
				
				
				friendRequestListAdapter.setItemArray( 
						
					items.getRequests() 
					
				);
				friendRequestListAdapter.notifyDataSetChanged();

			}
				
		}
		
	}
	
	public void setupNotifications(ArrayList<NotificationData> items) {
		
		//Do we have it already? If no, we init
		if( listNotifications == null ) { 
			
			//Get the ListView
			listNotifications = ((ListView) findViewById(R.id.list_notifications)); 
			notificationStatusText = ((TextView) findViewById(R.id.status_notifications));
			//registerForContextMenu(listNotifications); //Needed?
	        
		}
		
	
		//If empty --> show other
		if( items == null || items.size() == 0 ) {
		
			notificationStatusText.setVisibility( View.VISIBLE );
			
		} else {
			
			notificationStatusText.setVisibility( View.GONE );
			
		}
	    
		//If we don't have it defined, then we need to set it
		if( listNotifications.getAdapter() == null ) {
	
			//Create a new NotificationListAdapter
			notificationListAdapter = new NotificationListAdapter(this, items, layoutInflater, Dashboard.profile.getProfileId());
			listNotifications.setAdapter( notificationListAdapter );
				
			//Do we have the onClick?
			if( listNotifications.getOnItemClickListener() == null ) {
				
				listNotifications.setOnItemClickListener( 
						
					new OnItemClickListener() {
	
						@Override
						public void onItemClick( AdapterView<?> a, View v, int pos, long id ) {
	
							Toast.makeText( context, "Clicked on #" + pos + "(" + id + ")", Toast.LENGTH_SHORT ).show();
							
						}
						
						
						
					}
						
				);
			
			}
			
		} else {
			
			notificationListAdapter.setItemArray( items );
			notificationListAdapter.notifyDataSetChanged();
		
		}
		
	}
	
	public void onClick(View v) {
		
		if( v.getId() == R.id.button_refresh ) {
			
			refreshFeed();
			refreshCOM();
			
		} else if( v.getId() == R.id.button_status ) {
			
			//Let's set 'em values
    		valueFieldsArray[0] = fieldStatusUpdate.getText().toString();
    		valueFieldsArray[1] = sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "");
    		
    		//Validate
    		if( valueFieldsArray[0].equals( "" ) ) {
    			
    			Toast.makeText( this, R.string.status_empty_input, Toast.LENGTH_SHORT).show();
    			return;
    			
    		} else if( valueFieldsArray[1].equals( "" ) ) {
    			
    			Toast.makeText( this, R.string.status_error_unknown, Toast.LENGTH_SHORT).show();
    			return;
    			
    		}
    		//Iterate and conquer
    		for( int i = 0, max = Constants.FIELD_NAMES_STATUS.length; i < max; i++ ) {

    			postDataArray[i] =	new PostData(
	    			
    				Constants.FIELD_NAMES_STATUS[i],
	    			(Constants.FIELD_VALUES_STATUS[i] == null) ? valueFieldsArray[i] : Constants.FIELD_VALUES_STATUS[i] 
	    		
    			);
    		
    		}
    		
    		//Do the async
    		AsyncStatusUpdate asu = new AsyncStatusUpdate(this, new AsyncFeedRefresh(this, Dashboard.profile.getProfileId() ));
    		asu.execute( postDataArray );
    		return;
			
		} else {
			
			Toast.makeText(this, "Unimplemented feature.", Toast.LENGTH_SHORT).show();
			return;
		
		}
		
	}
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_dashboard, menu );
		return super.onCreateOptionsMenu( menu );
	
    }
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		//Let's act!
		if( item.getItemId() == R.id.option_refresh ) {

			refreshFeed();
			refreshCOM();
		
		} else if( item.getItemId() == R.id.option_settings ) {
		
			startActivity(
				
				new Intent(this, SettingsView.class)
				
			);
			
		} else if( item.getItemId() == R.id.option_logout ) {
			
			new AsyncLogout(this).execute();
			
		}
		
		// Return true yo
		return true;

	} 
	
	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {

		// Hotkeys
		if ( keyCode == KeyEvent.KEYCODE_BACK ) {
		
			if( slidingDrawer.isOpened() ) {
			
				slidingDrawer.animateClose();
				
			} else if( mTabHost.getCurrentTab() != 0 ) { 
			
				mTabHost.setCurrentTab( 0 );
				
			} else {
				
				//Are we running a service?
				if( !PublicUtils.isMyServiceRunning( context ) ) {

					if( asyncLogout == null ) {
					
						(asyncLogout = new AsyncLogout(context)).execute();
					
					} else {
						
						return true;
						
					}
					
				} else {
					
					((Activity)this).finish();
					
				}
			
			}	
			return true;
			
		}
		return super.onKeyDown( keyCode, event );
	}
	
	public Dialog generateDialogCompare(final Context context) {
		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	    final View layout = inflater.inflate(R.layout.dialog_compare, (ViewGroup) findViewById(R.id.dialog_root));
		
	    //Set the title and the view
		builder.setTitle(R.string.label_compare_bs);
		builder.setView(layout);

		//Grab the fields
		final EditText fieldUsername = (EditText) layout.findViewById(R.id.field_username);
		
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
			      
					
					if( !fieldUsername.getText().toString().equals( "" ) ) {
						
						new AsyncFetchDataToCompare(context).execute(fieldUsername.getText().toString());
					
					} else {
						
						Toast.makeText( context, R.string.general_empty_user, Toast.LENGTH_SHORT ).show();
						
					}
						
			   
				}
				
			}
			
		);
		
		//CREATE
		return builder.create();
		
	}
	
	public Dialog generateDialogFindSoldier(final Context context) {
		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	    final View layout = inflater.inflate(R.layout.dialog_compare, (ViewGroup) findViewById(R.id.dialog_root));
		
	    //Set the title and the view
		builder.setTitle(R.string.info_profile_soldier);
		builder.setView(layout);

		//Grab the fields
		final EditText fieldUsername = (EditText) layout.findViewById(R.id.field_username);
		
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
			      
					if( !fieldUsername.getText().toString().equals( "" ) ) {
							
						new AsyncFetchDataToProfileView(context).execute(fieldUsername.getText().toString());
					
					} else {
						
						Toast.makeText( context, R.string.general_empty_user, Toast.LENGTH_SHORT ).show();
						
					}
			   
				}
				
			}
			
		);
		
		//CREATE
		return builder.create();
		
	}
	
	public Dialog generateDialogFindPlatoon(final Context context) {
		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	    final View layout = inflater.inflate(R.layout.dialog_find_platoon, (ViewGroup) findViewById(R.id.dialog_root));
		
	    //Set the title and the view
		builder.setTitle(R.string.info_profile_platoon);
		builder.setView(layout);

		//Grab the fields
		final EditText fieldName = (EditText) layout.findViewById(R.id.field_name);
		
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
			      
					if( !fieldName.getText().toString().equals( "" ) ) {
							
						new AsyncFetchDataToPlatoonView(context).execute(fieldName.getText().toString());
					
					} else {
						
						Toast.makeText( context, R.string.general_empty_name, Toast.LENGTH_SHORT ).show();
						
					}
			   
				}
				
			}
			
		);
		
		//CREATE
		return builder.create();
		
	}
	
	private void setupCOM() {
		
        //Define the SlidingDrawer
		if( slidingDrawer == null ) {

			slidingDrawer = (SlidingDrawer) findViewById( R.id.com_slider);
			slidingDrawerHandle = (TextView) findViewById( R.id.com_slide_handle_text );
			buttonRefresh = (Button) findViewById( R.id.button_refresh );
			
			//Set the drawer listeners
			onDrawerCloseListener = new OnDrawerCloseListener() {
	
				@Override
				public void onDrawerClosed() { slidingDrawer.setClickable( false ); }
			
			};
			onDrawerOpenListener = new OnDrawerOpenListener() { 
			
				@Override 
				public void onDrawerOpened() { slidingDrawer.setClickable( true ); } 
				
			};
			
			//Attach the listeners
			slidingDrawer.setOnDrawerOpenListener( onDrawerOpenListener );
			slidingDrawer.setOnDrawerCloseListener( onDrawerCloseListener );
			
			//Setup the onClicks
			onItemClickListener = new OnItemClickListener() {

				@Override public void onItemClick( AdapterView<?> a, View v, int p, long i ) {

					onCOMRowClick(a, v, p, i);

				}

			};
			
		}
		
		switch( cTabHost.getCurrentTab() ) {
			
			case 0:
				setupFriendList(friendListData);
				break;
				
			case 1:
				setupNotifications(notificationArray);
				break;
				
			default:
				break;
				
			
		}
	
		//refresh the COM
		refreshCOM();
		
	}
	
	private void refreshCOM() {

		//Done? No? Let's populate in an async task!
		new AsyncCOMReload().execute( sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ));
		
	}
	
	private void refreshFeed() {
		
		//Feed refresh!
		asyncFeedRefresh = new AsyncFeedRefresh(
			
			context,
			Dashboard.profile.getProfileId()
				
		);
		asyncFeedRefresh.execute();
		
	}
	
	public void onRequestActionClick(View v) {

		new AsyncComRequest(
				
			this, 
			((ProfileData)v.getTag()).getProfileId(),
			new AsyncComRefresh(
					
				this, 
				listFriendRequests, 
				listFriends, 
				layoutInflater,
				buttonRefresh,
				slidingDrawerHandle
				
			),
			( v.getId() == R.id.button_accept )
		
		).execute(sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "")); 
		
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(Constants.SUPER_COOKIES, RequestHandler.getCookies());
	
	}

	
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
    	
        super.onConfigurationChanged(newConfig);       
        
    }
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

    	
    	//init
    	int menuId = 2;
    	
    	//Get the actual menu item and tag
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    	
    	//Get it right
    	if( view.getId()  == R.id.list_requests || view.getId() == R.id.list_friends ) { menuId = 0;  } 
    	else if( view.getId() == R.id.list_feed ) { menuId = 1; }

    	//Do the right thing
    	if(menuId == 0) { 
    		
    		//Get the "object"
        	ProfileData selectedUser = (ProfileData) info.targetView.getTag();
    		
    		//Wait, is the position 0? If so, it's the heading...
	    	if( !selectedUser.getAccountName().startsWith( "0000000" ) ) {
	    		
				menu.add( menuId, 0, 0, R.string.label_chat_open);
				menu.add( menuId, 1, 0, R.string.label_soldier_view);
				menu.add( menuId, 2, 0, R.string.label_compare_bs);
				
	    	}
		
    	} else if( menuId == 1 ) {
    	
    		//Show the menu
    		if( !((FeedItem) ((View) info.targetView).getTag()).isLiked() ) {
    			
    			menu.add( menuId, 0, 0, R.string.label_hooah);
    		
    		} else {
    			
    			menu.add( menuId, 0, 0, R.string.label_unhooah);
    			
    		}
    		menu.add( menuId, 1, 0, R.string.label_comment_view);
    		
    	}
    	
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
					
					startActivity(
							
						new Intent( 
								
							this, 
							ChatView.class
							
						).putExtra( 
							
							"profile", 
							(ProfileData) info.targetView.getTag()
							
						)
					
					);
					
				} else if( item.getItemId() == 1 ) {
					
					startActivity(
							
						new Intent( 
								
							this, 
							ProfileView.class
							
						).putExtra( 
							
							"profile", 
							((ProfileData) info.targetView.getTag())
							
						)
					
					);
					
				} else if( item.getItemId() == 2 ) {
					
					startActivity(
							
						new Intent( 
								
							this, 
							CompareView.class
							
						).putExtra( 
							
							"profile", 
							WebsiteHandler.getPersonaIdFromProfile(
								
								((ProfileData) info.targetView.getTag()).getProfileId() 
						
							)
							
						)
					
					);
					
				}
					
	    	} else if( item.getGroupId() == 1 ) {
	    		
	    		//REQUESTS
				if( item.getItemId() == 0 ) {
						
					new AsyncFeedHooah(
							
						this, 
						info.id, 
						false,
						( (FeedItem)info.targetView.getTag()).isLiked(),
						new AsyncFeedRefresh(
								
							this, 
							Dashboard.profile.getProfileId()
							
						)
					
					).execute();
				
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
								
							"profileId",
							((FeedItem) info.targetView.getTag()).getOwnerId()
							
						).putExtra(
						
							"canComment",
							true
								
						)
						
					);
					
				}
	    		
	    	}
		
		} catch ( WebsiteHandlerException e ) {
				
			e.printStackTrace();
		
		}
		
		closeContextMenu();
		
		return true;
	}

	
	public void onCOMRowClick(AdapterView<?> a, View view, int position, long id) {
	
		openContextMenu( view );
		
	}
	
	private void setupTabsPrimary( final String[] titleArray, final int[] layoutArray ) {

		//Init
    	TabHost.TabSpec spec;
    	
    	//Iterate them tabs
    	for(int i = 0, max = titleArray.length; i < max; i++) {

    		//Num
    		final int num = i;
			View tabview = createTabView(mTabHost.getContext(), titleArray[num]);

			//Let's set the content
			spec = mTabHost.newTabSpec(titleArray[num]).setIndicator(tabview).setContent(
	        		
	    		new TabContentFactory() {
	    			
	            	public View createTabContent(String tag) {
	            		
	            		return layoutInflater.inflate( layoutArray[num], null );
	    
	            	}
	            
	            }
	    		
	        );
			
			//Add the tab
			mTabHost.addTab( spec ); 
    	
    	}
    	
    	//Assign values
    	mTabHost.setOnTabChangedListener(
    			
    		new OnTabChangeListener() {

    			@Override
    			public void onTabChanged(String tabId) {

    				switch( mTabHost.getCurrentTab() ) {
    					
    					case 0:
    						setupHome();
    						break;
    						
    					case 1:
    						setupFeed(feedArray);
    						break;
    						
    					default:
    						break;
    			
    				}

    			}
    			
    		}
    		
    	);
    	
    }

	private void setupTabsSecondary( final String[] titleArray, final int[] layoutArray ) {

		//Init
    	TabHost.TabSpec spec;
    	
    	//Iterate them tabs
    	for(int i = 0, max = titleArray.length; i < max; i++) {

    		//Num
    		final int num = i;
			View tabview = createTabView(cTabHost.getContext(), titleArray[num]);
			
			//Let's set the content
			spec = cTabHost.newTabSpec(titleArray[num]).setIndicator(tabview).setContent(
	        		
	    		new TabContentFactory() {
	    			
	            	public View createTabContent(String tag) {
	            		
	            		return layoutInflater.inflate( layoutArray[num], null );
	    
	            	}
	            
	            }
	    		
	        );
			
			//Add the tab
			cTabHost.addTab( 
				
				spec 
				
			); 
    	
    	}
    	
    	//Assign values
    	cTabHost.setOnTabChangedListener(
    			
    		new OnTabChangeListener() {

    			@Override
    			public void onTabChanged(String tabId) {

    				switch( cTabHost.getCurrentTab() ) {
    					
    					case 0:
    						setupHome();
    						break;
    						
    					case 1:
    						setupNotifications(notificationArray);
    						break;
    						
    					default:
    						break;
    			
    				}

    			}
    			
    		}
    		
    	);
    	
    }

	
    private final View createTabView(final Context context, final String text) {
    	
    	View view = LayoutInflater.from(context).inflate(R.layout.profile_tab_layout, null);
    	TextView tv = (TextView) view.findViewById(R.id.tabsText);
    	tv.setText(text);
    	return view;
    
    }
 
    private final void onMenuClick(long id) {
    	
    	if ( id == Constants.MENU_UNLOCKS ) {
    		
			startActivity( new Intent(this, UnlockView.class) );
			 
		} else if( id == Constants.MENU_ASSIGNMENTS ) {
			
			startActivity( new Intent(this, AssignmentView.class) );
			
		} else if( id == Constants.MENU_SOLDIER ) {
			
			generateDialogFindSoldier(this).show();
			
		} else if( id == Constants.MENU_PLATOON ) {
			
			generateDialogFindPlatoon(this).show();
			
		} else if( id == Constants.MENU_MY_SOLDIER ) {
			
			startActivity( 
					
				new Intent(
					
					this, 
					ProfileView.class
					
				).putExtra( 
						
					"profile",
					Dashboard.profile
				
				)
				
			);
		
		} else if( id == Constants.MENU_COMPARE ) {
			
			generateDialogCompare(this).show();
			return;
			
		} else if( id == Constants.MENU_SETTINGS ) { 
			
			startActivity( new Intent(this, SettingsView.class) );
			return;
			
		} else if( id == Constants.MENU_FORUM ) { 
			
			Toast.makeText( this, R.string.msg_unimplemented, Toast.LENGTH_SHORT ).show();
//			startActivity( new Intent(this, ForumView.class) );
			return;
			
		} else {
			
			Toast.makeText( this, R.string.msg_unimplemented, Toast.LENGTH_SHORT).show();
			return;
		
		}
    	
    }
    
    public class AsyncFeedRefresh extends AsyncTask<Void, Void, Boolean> {
        
    	//Attributes
    	private Context context;
    	private long activeProfileId;
    	
    	public AsyncFeedRefresh(Context c, long pId) {
    		
    		this.context = c;
    		this.activeProfileId = pId;
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {}

		@Override
		protected Boolean doInBackground( Void... arg0 ) {
			
			try {
				
				//Get...
				feedArray = WebsiteHandler.getPublicFeed(

					context,
					sharedPreferences.getInt( Constants.SP_BL_NUM_FEED, Constants.DEFAULT_NUM_FEED ),
					activeProfileId
					
				);
				
				//...validate!
				if( feedArray == null ) { 
					
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
				
				Toast.makeText( this.context, R.string.info_feed_empty, Toast.LENGTH_SHORT).show(); 
				return; 
			
			}

			//Let's see what we need to update *directly*
			switch( mTabHost.getCurrentTab() ) {
				
				case 0:
					setupHome();
					break;
					
				case 1:
					setupFeed(feedArray);
					break;
					
				default:
					break;
		
			}
			
			//Get back here!
	        return;
		        
		}
		
    }
    
    public class AsyncCOMReload extends AsyncTask<String, Integer, Boolean> {
    	
    	//Constructor
    	public AsyncCOMReload() {}	
    	
    	@Override
    	protected void onPreExecute() {
    		
    		buttonRefresh.setText( R.string.label_wait );
    		buttonRefresh.setEnabled( !buttonRefresh.isEnabled() );
    		
    	}
    	
    	@Override
    	protected Boolean doInBackground( String... arg0) {
    		
    		try {
    		
    			//Let's get this!!
    			notificationArray = WebsiteHandler.getNotifications( arg0[0] );
    			friendListData = WebsiteHandler.getFriendsCOM( arg0[0] );
    			return true;
    			
    		} catch ( WebsiteHandlerException e ) {
    			
    			return false;
    			
    		}
    		
    	}
    	
    	@Override
    	protected void onPostExecute(Boolean results) {
    		
    		switch( cTabHost.getCurrentTab() ) {
    			
    			case 0:
    				setupFriendList(friendListData);
    				break;
    				
    			case 1:
    				setupNotifications(notificationArray);
    				break;
    				
    			default:
    				break;    				
    			
    		}
    		
    		//Update the text
    		buttonRefresh.setText( R.string.lable_refresh );
    		buttonRefresh.setEnabled( !buttonRefresh.isEnabled() );
    		
    		//Update the sliding drawer handle
    		slidingDrawerHandle.setText( 
    				
				getString(R.string.label_com_handle ).replace(
    					
    				"{num}", friendListData.getOnlineCount() + ""
    				
    			) 
    			
    		);
    		
    		//R-turn
    		return;
    		
    	}
    
    }

    
    @Override
    public void onResume() {
    
    	super.onResume();
    	
    	//Let's see
    	if( Dashboard.profile == null ) {
    		
    		Dashboard.profile = new ProfileData(
    			
    			sharedPreferences.getString( Constants.SP_BL_USERNAME, "" ),
    			sharedPreferences.getString( Constants.SP_BL_PERSONA, "" ),
    			sharedPreferences.getLong( Constants.SP_BL_PROFILE_ID, 0 ),
    			sharedPreferences.getLong( Constants.SP_BL_PERSONA_ID, 0 ),
    			sharedPreferences.getLong( Constants.SP_BL_PLATFORM_ID, 0 ),
    			null
    				
    		);
    		
    	}
    	
    	switch( mTabHost.getCurrentTab() ) {
			
			case 0:
				setupHome();
				break;
				
			case 1:
				setupFeed(feedArray);
				break;
				
			default:
				break;
	
		}

    }
    
    public final static ProfileData getProfile() { return Dashboard.profile; }
    
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
       
}
