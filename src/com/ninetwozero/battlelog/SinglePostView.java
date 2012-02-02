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
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.asynctasks.AsyncCommentSend;
import com.ninetwozero.battlelog.asynctasks.AsyncCommentsRefresh;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.NotificationData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class SinglePostView extends ListActivity {

	//Attributes
	private Intent intent;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private FeedItem item;
	private NotificationData notification;
	
	//Elements
	private ListView listView;
	private TextView textHeading, textTitle,textContent, textDate;
	private EditText fieldMessage;
	private Button buttonSend;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		RequestHandler.setCookies( (ArrayList<ShareableCookie> ) icicle.getParcelable(Constants.SUPER_COOKIES) );
    	
    	}
    	
    	//Set the content view
        setContentView(R.layout.single_post_view);

        //Prepare to tango
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        //Get the ListView
        listView = getListView();
        listView.setChoiceMode( ListView.CHOICE_MODE_NONE );
        
        //Let's grab the intent
        intent = getIntent();
        if( intent.hasExtra( "feedItem" ) ) { item = intent.getParcelableExtra( "feedItem" ); }
        if( intent.hasExtra( "notification" ) ) { notification = intent.getParcelableExtra( "notification" ); }
        
        //Do we have what we need?
        if( item == null && notification == null ) {
        	
        	Toast.makeText( this, R.string.general_no_data, Toast.LENGTH_SHORT ).show();
        	return;
        	
        }
        
        //Get data if we need it via the id
        if( notification != null ) {
        	
        	//Get data here async
        	new AsyncGetDataForPost(this).execute(notification);
        	
        } else {

        	setupLayout( item );
        	
        }
                
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
    public void onResume() {

    	//OnResume
    	super.onResume();

        //Let's setup the adapter
        if(item != null) { this.reloadComments(); }
    	
    }
    
    public void reloadComments() {
    	
    	if( item != null ) { new AsyncCommentsRefresh(this, listView, layoutInflater).execute( item.getId() ); }
    
    }
    
    public void onClick(View v) {

    	//Send?
    	if( v.getId() == R.id.button_send ) {

    		//Send it!
    		new AsyncCommentSend(

				this,
				item.getId(),
				buttonSend, 
				false, 
				new AsyncCommentsRefresh(this, listView, layoutInflater)

    		).execute(

				sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ),
				fieldMessage.getText().toString()

			);

    		//Clear the field
    		fieldMessage.setText( "" );

    	}

    }
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_basic, menu );
		return super.onCreateOptionsMenu( menu );
	
    }
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		//Let's act!
		if( item.getItemId() == R.id.option_reload ) {
	
			this.reloadComments();
			
		} else if( item.getItemId() == R.id.option_back ) {
			
			((Activity) this).finish();
			
		}
		
		// Return true yo
		return true;

	} 
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

       	//Show the menu
		menu.add( 0, 0, 0, "Report comment");

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

					/*
					 * 
					
						new AsyncCommentReport(this, info.id, false, new AsyncProfileRefresh(this, true, profileData)).execute( 
								
							sharedPreferences.getString( 
									
								Constants.SP_BL_CHECKSUM, 
								""
								
							) 
						
						);
					
					*/
					Toast.makeText(this, "Reporting comment number " + item.getItemId(), Toast.LENGTH_SHORT).show();
					
				}
				
			}
			
		} catch( Exception ex ) {
		
			ex.printStackTrace();
			return false;
			
		}

		return true;
	
	}
	
	public void checkCommentRights() {
		
    	//Is the user allowed to post?
    	if( !intent.getBooleanExtra( "canComment", false ) ) {

        	if( SessionKeeper.getProfileData().getProfileId() != intent.getLongExtra( "profileId", 0) ) {
        		
        		Log.d( Constants.DEBUG_TAG, "No comment due to... I don't know!");
        		buttonSend.setVisibility( View.GONE );
        		fieldMessage.setVisibility( View.GONE );
        	
        		return;
        		
        	}
        	
        } 
    	
    	if( item == null || item.getType().equals( "wroteforumpost" ) || item.getType().equals( "createdforumthread" ) ) {

    		Log.d( Constants.DEBUG_TAG, "No comment due to... it being a forum post?!");
    		buttonSend.setVisibility( View.GONE );
    		fieldMessage.setVisibility( View.GONE );
    	
    	} else {
    		
    		buttonSend.setVisibility( View.VISIBLE );
    		fieldMessage.setVisibility( View.VISIBLE );
    		
    	}
		
	}
	
	public class AsyncGetDataForPost extends AsyncTask<NotificationData, Void, Boolean> {
		
		//Attributes
		private Context context;
		
		//Constructs
		public AsyncGetDataForPost(Context c) {
		
			this.context = c;
			
		}
		
		@Override
		protected Boolean doInBackground( NotificationData... arg0 ) {
		
			try {
				
				item = WebsiteHandler.getPostForNotification(arg0[0]);
				return true;
			
			} catch( Exception ex ) {
			
				ex.printStackTrace();
				return false;
				
			}

		}
		
		@Override
		protected void onPostExecute( Boolean results ) {
			
			//Let's see...
			if( results ) {
				
				setupLayout( item );
				reloadComments();
				
			}
			
		}
		
	}
	
	public void setupLayout( FeedItem feedItem ) {
	
		//Let's do this
		if( feedItem == null ) {
			
			Toast.makeText( this, R.string.general_no_data, Toast.LENGTH_SHORT).show();
			return;
			
		}
		
		//Get the fields
        if( textHeading == null ) { textHeading = (TextView) findViewById(R.id.text_heading); }
        if( textTitle == null ) { textTitle = (TextView) findViewById(R.id.text_title); }
    	if( textContent == null ) { textContent = (TextView) findViewById(R.id.text_content); }
    	if( textDate == null ) { textDate = (TextView) findViewById(R.id.text_date); }
    	if( fieldMessage == null ) { fieldMessage = (EditText) findViewById(R.id.field_message); }
    	if( buttonSend == null ) { buttonSend = (Button) findViewById(R.id.button_send); }
		
        //Set up the top-place
        String username = ( feedItem.getProfile(1) == null ) ? feedItem.getProfile( 0 ).getAccountName() : feedItem.getProfile( 1 ).getAccountName();
        if( !username.endsWith( "s" ) ) {
    		
    		textHeading.setText( getString( R.string.info_spost_heading ).replace("{username}", username) );
    	
    	} else {
    		
    		textHeading.setText( getString( R.string.info_spost_heading_s ).replace("{username}", username) );
    		
    	}
        
        textTitle.setText( Html.fromHtml( feedItem.getTitle() ) );
        textDate.setText( PublicUtils.getRelativeDate( this, feedItem.getDate() ) );
        
        //Handle the content
        if( feedItem.getContent() != null && feedItem.getContent().length() > 0 ) {
        	
        	textContent.setText( Html.fromHtml( feedItem.getContent() ) ); 
        	
        } else {
        
        	textContent.setVisibility( View.GONE );
        	
        }
        
        //Validate comment rights
		checkCommentRights();

	}
	
}