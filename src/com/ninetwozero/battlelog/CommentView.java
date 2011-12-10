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
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ninetwozero.battlelog.adapters.CommentListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncCommentSend;
import com.ninetwozero.battlelog.asynctasks.AsyncCommentsRefresh;
import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class CommentView extends ListActivity {

	//Attributes
	private long postId;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private ArrayList<CommentData> comments;
	
	//Elements
	private ListView listView;
	private EditText fieldMessage;
	private Button buttonSend;
	
	//CHAT-related
	private AsyncCommentsRefresh asyncCommentsRefresh;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		RequestHandler.setCookies( (ArrayList<ShareableCookie> ) icicle.getParcelable(Constants.SUPER_COOKIES) );
    	
    	}
    	
    	//Set the content view
        setContentView(R.layout.comments_view);

        //Prepare to tango
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        //Get the ListView
        listView = getListView();
        listView.setChoiceMode( ListView.CHOICE_MODE_NONE );
        
        //Let's get the other comments participant
        comments = (ArrayList<CommentData>) getIntent().getSerializableExtra( "comments" );
        postId = getIntent().getLongExtra( "postId", 0);     
        
        //Get the elements    
        buttonSend = (Button) findViewById(R.id.button_send);
    	fieldMessage = (EditText) findViewById(R.id.field_message);
    
    	//Is the user allowed to post?
        if( !getIntent().getBooleanExtra( "canComment", false ) ) {

        	if( sharedPreferences.getLong( Constants.SP_BL_PROFILE_ID, 0 ) != getIntent().getLongExtra( "profileId", 0) ) {
        		
        		buttonSend.setVisibility( View.GONE );
        		fieldMessage.setVisibility( View.GONE );
        	
        	}
        	
        }
        	
        //Let's setup the adapter
        if( listView.getAdapter() == null ) {
        	
        	listView.setAdapter( new CommentListAdapter(this, comments, layoutInflater) );
        	
        }
                
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putSerializable(Constants.SUPER_COOKIES, RequestHandler.getSerializedCookies());
	
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
    }
    
    public void reloadComments() {
    	
    	new AsyncCommentsRefresh(this, listView, layoutInflater).execute( postId );
    
    }
    
    public void onClick(View v) {
    	
    	//Send?
    	if( v.getId() == R.id.button_send ) {
    	
    		//Send it!
    		new AsyncCommentSend(
    				
				this,
				postId,
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
	
	/*
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

					new AsyncCommentReport(this, info.id, false, new AsyncProfileRefresh(this, true, profileData)).execute( 
							
						sharedPreferences.getString( 
								
							Constants.SP_BL_CHECKSUM, 
							""
							
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
    */
}