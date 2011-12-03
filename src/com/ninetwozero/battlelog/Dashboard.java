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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.FeedListAdapter;
import com.ninetwozero.battlelog.adapters.GridMenuAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncComRefresh;
import com.ninetwozero.battlelog.asynctasks.AsyncComRequest;
import com.ninetwozero.battlelog.asynctasks.AsyncFetchDataToCompare;
import com.ninetwozero.battlelog.asynctasks.AsyncFetchDataToPlatoonView;
import com.ninetwozero.battlelog.asynctasks.AsyncFetchDataToProfileView;
import com.ninetwozero.battlelog.asynctasks.AsyncLogout;
import com.ninetwozero.battlelog.asynctasks.AsyncStatusUpdate;
import com.ninetwozero.battlelog.datatypes.DashboardItem;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.SerializedCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class Dashboard extends TabActivity {

	//Attributes
	final private Context context = this;
	private EditText fieldStatusUpdate;
	private String[] valueFields;
	private PostData[] postDataArray;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private ArrayList<FeedItem> feedItems;
	private DashboardItem[] menuItems;
	
	//Elements
	private TabHost mTabHost;
	private GridView gridMenu;
	private ListView listFeed;
	private FeedListAdapter feedListAdapter;
	
	//COM-related
	private SlidingDrawer slidingDrawer;
	private TextView slidingDrawerHandle;
	private OnDrawerOpenListener onDrawerOpenListener;
	private OnDrawerCloseListener onDrawerCloseListener;
	private ListView listFriendsRequests, listFriends;
	private Button buttonRefresh;
	private AsyncComRefresh asyncComRefresh;
	
	//Async
	AsyncLogout asyncLogout = null;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( "serializedCookies" ) ) {
    		
    		RequestHandler.setSerializedCookies( (ArrayList<SerializedCookie> ) icicle.getSerializable("serializedCookies") );
    	
    	}
    	
    	//Set the content view
        setContentView(R.layout.dashboard);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        feedItems = new ArrayList<FeedItem>();
        
        //Fix the tabs
    	mTabHost = (TabHost) findViewById(android.R.id.tabhost);
    	setupTabs(
    			
    		new String[] { "Menu", "Feed" }, 
    		new int[] { R.layout.tab_content_dashboard_menu, R.layout.tab_content_dashboard_feed }
    		
    	);
    
        //Set sharedPreferences
        sharedPreferences = getSharedPreferences( Constants.fileSharedPrefs, 0);

        //Build the menu items
        menuItems = new DashboardItem[]{ 

        	new DashboardItem(Constants.MENU_ME, "My soldier"),
        	new DashboardItem(Constants.MENU_UNLOCKS, "My unlocks"),
        	new DashboardItem(Constants.MENU_SOLDIER, "Find soldier"),
        	new DashboardItem(Constants.MENU_PLATOON, "Find platoon"),
        	new DashboardItem(Constants.MENU_COMPARE, "Compare battle scars"),
        	new DashboardItem(Constants.MENU_SETTINGS, "Settings")
        	
        };
        
        //Setup COM
        setupCOM();
        
        //Setup everything
        switch( mTabHost.getCurrentTab() ) {
        	
        	case 0:
        		drawHome();
        		break;
        		
        	case 1:
        		drawFeed(feedItems);
        		break;
        		
        	default:
        		break;
        	
        }
	}	
	
	public final void drawHome() {
    	
    	//Let's see
		if( gridMenu == null ) {
			
			gridMenu = (GridView) findViewById(R.id.grid_menu);
			gridMenu.setAdapter( new GridMenuAdapter(this, menuItems, layoutInflater ) );
		
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
    
    public void drawFeed(ArrayList<FeedItem> items) {
    	
    	//Do we have it already? If no, we init
		if( listFeed == null ) { 
			
			//
			listFeed = ((ListView) findViewById(R.id.list_feed)); 
			registerForContextMenu(listFeed);

			//Set the attributes
	        fieldStatusUpdate = (EditText) findViewById(R.id.field_status);
	        valueFields = new String[2];
	        postDataArray = new PostData[2];
	        

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
	
							if( !((FeedItem) a.getItemAtPosition( pos ) ).getContent().equals( "" ) ) {
								
								View viewContainer = (View) v.findViewById(R.id.wrap_contentbox);
								viewContainer.setVisibility( ( viewContainer.getVisibility() == View.GONE ) ? View.VISIBLE : View.GONE );
							
							}
							
						}
						
						
						
					}
						
				);
			
			}
			
		} else {
			
			
			feedListAdapter.notifyDataSetChanged();
		}
    
    }
	
	public void onClick(View v) {
		
		if( v.getId() == R.id.button_refresh ) {
			
			refreshCOM();
			
		} else if( v.getId() == R.id.button_status ) {
			
			//Let's set 'em values
    		valueFields[0] = fieldStatusUpdate.getText().toString();
    		valueFields[1] = sharedPreferences.getString( "battlelog_post_checksum", "");
    		
    		//Validate
    		if( valueFields[0].equals( "" ) ) {
    			
    			Toast.makeText( this, "Please enter a status text to continue.", Toast.LENGTH_SHORT).show();
    			return;
    			
    		} else if( valueFields[1].equals( "" ) ) {
    			
    			Toast.makeText( this, "An error has occured: please relogin and try again.", Toast.LENGTH_SHORT).show();
    			return;
    			
    		}
    		//Iterate and conquer
    		for( int i = 0; i < Constants.fieldNamesStatus.length; i++ ) {

    			postDataArray[i] =	new PostData(
	    			
    				Constants.fieldNamesStatus[i],
	    			(Constants.fieldValuesStatus[i] == null) ? valueFields[i] : Constants.fieldValuesStatus[i] 
	    		
    			);
    		
    		}
    		
    		//Do the async
    		AsyncStatusUpdate asu = new AsyncStatusUpdate(this, false);
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
		if( item.getItemId() == R.id.option_logout ) {
			
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
				
			} else {
				
				if( asyncLogout == null ) {
					
					(asyncLogout = new AsyncLogout(this)).execute();
				
				} else {
					
					return true;
					
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
		builder.setTitle("Compare battle scars");
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
						
						Toast.makeText( context, "You need to enter a username", Toast.LENGTH_SHORT ).show();
						
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
		builder.setTitle("View soldier profile of...");
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
						
						Toast.makeText( context, "You need to enter a username", Toast.LENGTH_SHORT ).show();
						
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
		builder.setTitle("View the platoon profile for...");
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
						
						Toast.makeText( context, "You need to enter a name", Toast.LENGTH_SHORT ).show();
						
					}
			   
				}
				
			}
			
		);
		
		//CREATE
		return builder.create();
		
	}
	
	private void setupCOM() {
		
        //Define the SlidingDrawer
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
		
		//Grab the ListViews
		listFriendsRequests = (ListView) findViewById( R.id.list_requests );
		listFriendsRequests.setChoiceMode( ListView.CHOICE_MODE_NONE );
		listFriends = (ListView) findViewById( R.id.list_friends);

		//Set the context menus
		registerForContextMenu( listFriends );
		
		//Setup the onClicks
		OnItemClickListener onItemClickListener = new OnItemClickListener() {

			@Override public void onItemClick( AdapterView<?> a, View v, int p, long i ) {

				onCOMRowClick(a, v, p, i);
				
			}
			
		};

		listFriendsRequests.setOnItemClickListener( onItemClickListener );
		listFriends.setOnItemClickListener( onItemClickListener );
		
		//refresh the COM
		refreshCOM();
		
	}
	
	private void refreshCOM() {

		//Done? No? Let's populate in an async task!
		asyncComRefresh = new AsyncComRefresh(
			
			context, 
			listFriendsRequests, 
			listFriends, 
			layoutInflater,
			buttonRefresh,
			slidingDrawerHandle
			
		);
		asyncComRefresh.execute();
		
	}
	
	public void onRequestActionClick(View v) {

		//...
		if( v.getId() == R.id.button_accept ) { 
			
			new AsyncComRequest(
					
				this, 
				((ProfileData)v.getTag()).getProfileId(),
				new AsyncComRefresh(
						
					this, 
					listFriendsRequests, 
					listFriends, 
					layoutInflater,
					buttonRefresh,
					slidingDrawerHandle
					
				)
			
			).execute(true); 
			
		} else { 
			
			new AsyncComRequest(
					
				this, 
				((ProfileData)v.getTag()).getProfileId(),
				new AsyncComRefresh(
						
					this, 
					listFriendsRequests, 
					listFriends, 
					layoutInflater,
					buttonRefresh,
					slidingDrawerHandle
					
				)
				
			).execute(false); 
		
		}
		
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putSerializable("serializedCookies", RequestHandler.getSerializedCookies());
	
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
    	ProfileData selectedUser = (ProfileData) info.targetView.getTag();
    	
    	//Get it right
    	if( view.getId()  == R.id.list_requests ) { menuId = 0;  } 
    	else if( view.getId() == R.id.list_friends) { menuId = 0; }
        		
    	//Wait, is the position 0? If so, it's the heading...
    	if( !selectedUser.getAccountName().startsWith( "0000000" ) ) {
    		
			menu.add( menuId, 0, 0, "Open chat");
			menu.add( menuId, 1, 0, "View soldier");
			menu.add( menuId, 2, 0, "Compare battle scars");
			
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
					
	    	} else {}
		
		} catch ( WebsiteHandlerException e ) {
				
			e.printStackTrace();
		
		}
		
		return true;
	}

	
	public void onCOMRowClick(AdapterView<?> a, View view, int position, long id) {
	
		startActivity( new Intent(this, ProfileView.class).putExtra( "profile", (ProfileData) a.getItemAtPosition( position )));
		
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
    	
    	//Assign values
    	mTabHost.setOnTabChangedListener(
    			
    		new OnTabChangeListener() {

    			@Override
    			public void onTabChanged(String tabId) {

    				switch( getTabHost().getCurrentTab() ) {
    					
    					case 0:
    						drawHome();
    						break;
    						
    					case 1:
    						drawFeed(null);
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
			 
		} else if( id == Constants.MENU_SOLDIER ) {
			
			generateDialogFindSoldier(this).show();
			
		} else if( id == Constants.MENU_PLATOON ) {
			
			generateDialogFindPlatoon(this).show();
			
		} else if( id == Constants.MENU_ME ) {
			
			startActivity( 
					
				new Intent(
					
					this, 
					ProfileView.class
					
				).putExtra( 
						
					"profile",
					new ProfileData(

						sharedPreferences.getString( "battlelog_username", "" ),
						sharedPreferences.getString( "battlelog_persona", "" ),
						sharedPreferences.getLong( "battlelog_persona_id", 0 ),	
						sharedPreferences.getLong( "battlelog_profile_id", 0 ),	
						sharedPreferences.getLong( "battlelog_platform_id", 0 ),
						sharedPreferences.getString( "battlelog_gravatar_hash", "" )
						
					)
				
				)
				
			);
		
		} else if( id == Constants.MENU_COMPARE ) {
			
			generateDialogCompare(this).show();
			return;
			
		} else {
			
			Toast.makeText( this, "Unimplemented menu alternative.", Toast.LENGTH_SHORT).show();
			
		}
    	
    	
    }
    
}
