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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Toast;

import com.ninetwozero.battlelog.asynctasks.AsyncComRefresh;
import com.ninetwozero.battlelog.asynctasks.AsyncComRequest;
import com.ninetwozero.battlelog.asynctasks.AsyncFetchDataToCompare;
import com.ninetwozero.battlelog.asynctasks.AsyncLogout;
import com.ninetwozero.battlelog.asynctasks.AsyncStatusUpdate;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.SerializedCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class Dashboard extends Activity {

	//Attributes
	final private Context context = this;
	private EditText fieldStatusUpdate;
	private String[] valueFields;
	private PostData[] postDataArray;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	
	//COM-related
	private SlidingDrawer slidingDrawer;
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
        
        //Set the attirbutes
        fieldStatusUpdate = (EditText) findViewById(R.id.field_status);
        valueFields = new String[2];
        postDataArray = new PostData[2];
        
        //Set sharedPreferences
        sharedPreferences = getSharedPreferences( Constants.fileSharedPrefs, 0);

        //Setup COM
        setupCOM();
	}	
	
	public void onClick(View v) {
		
		if( v.getId() == R.id.button_refresh ) {
			
			refreshCOM();
			
		} else if ( v.getId() == R.id.button_unlocks ) {
		
			 startActivity( new Intent(this, UnlocksView.class) );
			 
		} else if( v.getId() == R.id.button_view_self ) {
			
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
						sharedPreferences.getLong( "battlelog_platform_id", 0 )
						
					)
				
				)
				
			);
		
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
			
		} else if( v.getId() == R.id.button_compare ) {
			
			generateDialogCompare(this).show();
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
	    final View layout = inflater.inflate(R.layout.compare_dialog, (ViewGroup) findViewById(R.id.dialog_root));
		
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
			      
					
					new AsyncFetchDataToCompare(context).execute(fieldUsername.getText().toString());
						
			   
				}
				
			}
			
		);
		
		//CREATE
		return builder.create();
		
	}
	
	
	
	private void setupCOM() {
		
        //Define the SlidingDrawer
		slidingDrawer = (SlidingDrawer) findViewById( R.id.com_slider);
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
			listFriends, 
			layoutInflater,
			buttonRefresh
			
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
					listFriends, 
					layoutInflater,
					buttonRefresh
					
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
					listFriends, 
					layoutInflater,
					buttonRefresh
					
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
    	
    	//Get it right
    	if( view.getId()  == R.id.list_requests ) { menuId = 0;  } 
    	else if( view.getId() == R.id.list_friends) { menuId = 0; }
    	
    	//Show the menu
		menu.add( menuId, 0, 0, "Open chat");
		menu.add( menuId, 1, 0, "View soldier");
		menu.add( menuId, 2, 0, "Compare battle scars");
    	
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
}
