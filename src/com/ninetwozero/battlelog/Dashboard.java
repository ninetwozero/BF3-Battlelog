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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.FriendSpinnerAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncComRefresh;
import com.ninetwozero.battlelog.asynctasks.AsyncLogout;
import com.ninetwozero.battlelog.asynctasks.AsyncStatusUpdate;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;


public class Dashboard extends Activity {

	//Attributes
	final private Context context = this;
	private EditText fieldStatusUpdate;
	private String[] valueFields;
	private PostData[] postDataArray;
	private SharedPreferences sharedPreferences;
	
	//COM-related
	private SlidingDrawer slidingDrawer;
	private OnDrawerOpenListener onDrawerOpenListener;
	private OnDrawerCloseListener onDrawerCloseListener;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    
    	//onCreate - save the instance state
    	super.onCreate(savedInstanceState);
    	
    	//Set the content view
        setContentView(R.layout.dashboard);
        
        //Set the attirbutes
        fieldStatusUpdate = (EditText) findViewById(R.id.field_status);
        valueFields = new String[2];
        postDataArray = new PostData[2];
        
        //Set sharedPreferences
        sharedPreferences = getSharedPreferences( Config.fileSharedPrefs, 0);

        //Setup COM
        setupCOM();
	}	
	
	public void onClick(View v) {
		
		if( v.getId() == R.id.button_view_self ) {
			
			 startActivity( new Intent(this, ProfileView.class) );
		
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
    		for( int i = 0; i < Config.fieldNamesStatus.length; i++ ) {

    			postDataArray[i] =	new PostData(
	    			
    				Config.fieldNamesStatus[i],
	    			(Config.fieldValuesStatus[i] == null) ? valueFields[i] : Config.fieldValuesStatus[i] 
	    		
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
		
			new AsyncLogout(this).execute();
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
		final Spinner spinnerFriends = (Spinner) layout.findViewById(R.id.spinner_username);
		final EditText fieldUsername = (EditText) layout.findViewById(R.id.field_username);
		
		//ASYNCTASK THE POPULATION
		new AsyncFriendListForSpinner(this, spinnerFriends).execute(this.sharedPreferences.getString( "battlelog_post_checksum", ""));

		//What to do upon selection?
		spinnerFriends.setOnItemSelectedListener(
				
			new OnItemSelectedListener() {
			 
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
				
					fieldUsername.setText( "" );
					
				}
	
				 @Override
				 public void onNothingSelected(AdapterView<?> arg0) {
					 
					 spinnerFriends.performItemClick( arg0, 0, 0 );
					 
				 }
				    
			}
		
		);
		
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
			      
					
					new AsyncFetchDataToCompare(context, spinnerFriends).execute(fieldUsername.getText().toString());
						
			   
				}
				
			}
			
		);
		
		//CREATE
		return builder.create();
		
	}
	
	private class AsyncFetchDataToCompare extends AsyncTask<String, Void, Boolean> {
	    
    	//Context
		private Context context;
		private Activity origin;
		    	
    	//Elements
    	private Spinner spinnerFriends;
    	
		//Data
    	ProfileData userData;
    	
    	//Error message
    	private String error;
    	
    	public AsyncFetchDataToCompare(Context c, Spinner s) {
    		
    		context = c;
    		origin = (Activity) context;
    		
    		userData = null;
    		error = "";
    		spinnerFriends = s;
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {}

		@Override
		protected Boolean doInBackground( String... arg0 ) {
			
			//Did the user pick one of his friends?
			if( arg0[0].equals( "" ) ) {
				
				try {

					//Grab the position in the Spinner
					ProfileData selectedFriend = (ProfileData) spinnerFriends.getAdapter().getItem( spinnerFriends.getSelectedItemPosition() );
					
					//Get the ID son!
					userData = WebsiteHandler.getIDFromProfile( selectedFriend.getProfileId() );
					
					//Did we get an actual user?
					if( userData == null|| userData.getPersonaId() == 0 ) { 
						
						error = "No user data found for the selected friend.";
						return false;
					
					}
					
					//Set the name too yo
					
				} catch(Exception ex) {
					
					//
					error = "No user data found for the selected friend.";
					return false;
					
				}
				
			} else {
				
				//We got to do it the hard way - let's see if we can find a user with that name.
				String searchString = arg0[0];
				
				//Try&Catch
				try {
					
					//Post the world!
					userData = WebsiteHandler.getIDFromSearch(
						
						searchString, 
						sharedPreferences.getString( "battlelog_post_checksum", "" ) 
					
					);

					//Did we get an actual user?
					if( userData == null || userData.getPersonaId() == 0 ) { 
						
						//Persona
						error = "No user found matching the following keyword: " + searchString;
						return false; 
						
					}
					
				} catch(Exception ex) {
					
					//D'oh	
					error = "No user found matching the following keyword: " + searchString;
					return false;
					
				}
			}

			return true;

		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			
			//How copy?
			if( result ) {
			
				//To the Batmobile!
				startActivity(
					
					new Intent(
						
						context,
						CompareView.class
						
					).putExtra(
							
						"profile", 
						userData	
					
					)
					
				);
				
			} else {
				
				Toast.makeText( context, error, Toast.LENGTH_SHORT ).show();
				
			}
			
			return;
		}
    	
    }

	private class AsyncFriendListForSpinner extends AsyncTask<String, Void, Boolean> {
	    
		//Attributes
		Context context;
		Spinner friendSpinner;
		ArrayList<ProfileData> profileArray;
		
		public AsyncFriendListForSpinner(Context c, Spinner s) {
			
			this.context = c;
			this.friendSpinner = s;
			this.profileArray = new ArrayList<ProfileData>();
	
		}
		
		@Override
		protected void onPreExecute() {}
		
	
		@Override
		protected Boolean doInBackground( String... arg0 ) {
			
			try {
				
				this.profileArray = WebsiteHandler.getFriendList(arg0[0], false);
				return true;
				
			} catch ( WebsiteHandlerException ex ) {
				
				Log.d("com.ninetwozero.battlelog", ex.getMessage() );
				return false;
				
			}
	
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			
			//How copy?
			if( result ) {
			
				//The adapter
				FriendSpinnerAdapter fsAdapter = new FriendSpinnerAdapter(
						
					context, 
					profileArray,
					(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
					
				);
				
				//Set the adapter
				friendSpinner.setAdapter( fsAdapter );
			
				//Enable it!
				friendSpinner.setEnabled( true );
				
			}
			
			return;
		}
		
	}
	
	
	private void setupCOM() {
		
        //Define the SlidingDrawer
		slidingDrawer = (SlidingDrawer) findViewById( R.id.com_slider);
		
		//Set the drawer listeners
		onDrawerCloseListener = new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() { slidingDrawer.setClickable( false ); }
		
		};
		onDrawerOpenListener = new OnDrawerOpenListener() { 
		
			@Override 
			public void onDrawerOpened() { slidingDrawer.setClickable( true ); } 
			
		};
		
		//Done? No? Let's populate in an async task!
		/*AsyncComRefresh acr = AsyncComRefresh(this);
		acr.execute();
	*/
	}

}
