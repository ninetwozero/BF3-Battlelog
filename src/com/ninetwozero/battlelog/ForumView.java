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

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ninetwozero.battlelog.adapters.ThreadListAdapter;
import com.ninetwozero.battlelog.datatypes.Board;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class ForumView extends ListActivity {

	//Attributes
	private final Context CONTEXT = this;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private Board.Forum currentForum;
	private long forumId;
	private String forumTitle;
	
	//Elements
	private ListView listView;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		RequestHandler.setCookies( (ArrayList<ShareableCookie>) icicle.getParcelable(Constants.SUPER_COOKIES) );
    		
    	}
    	//Set the content view
        setContentView(R.layout.board_forum_view);

        //Prepare to tango
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.layoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        
        //Get the forumId
        forumId = getIntent().getLongExtra( "forumId", 0 );
        forumTitle = getIntent().getStringExtra( "forumTitle" );
        
        //Init
		initLayout(); 
	
	}        

	@Override
	public void onResume() {
		
		super.onResume();
		reloadLayout();
		
	}
	
	public void initLayout() {
		
		//Set the top
		if( forumTitle != null ) {
			
			( (TextView) findViewById( R.id.text_title) ).setText( forumTitle );
			
		}
		
        //Get the listView
        if( listView == null ) { 
        	
        	listView = getListView(); 
        	if( currentForum == null ) { 
        		
        		listView.setAdapter( new ThreadListAdapter( this, null, layoutInflater ) );
        		
        	} else {
        		
        		listView.setAdapter( new ThreadListAdapter( this, currentForum.getThreads(), layoutInflater ) );
        		
        	}
        	
        }
		
	}
	
    public void reloadLayout() { 
    	
    	if( currentForum == null ) {
    		
    		new AsyncGetThreads(this, listView).execute( forumId ); 
    	
    	} else {
    		
    		new AsyncGetThreads(null, listView).execute( forumId ); 
    		
    	}
    	
    }
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

    	//Inflate!!
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_boardview, menu );		
		return super.onCreateOptionsMenu( menu );
	
    }
    
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		//Let's act!
		if( item.getItemId() == R.id.option_reload ) {
	
			this.reloadLayout();
			
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
	
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int p, long id) {
		
		startActivity( 
				
			new Intent(this, ForumThreadView.class).putExtra(
					
				"threadId", 
				id 
				
			).putExtra(
			
				"threadTitle",
				((Board.ThreadData) v.getTag()).getTitle()
					
			)	
				
		);
		
	}

	private class AsyncGetThreads extends AsyncTask<Long, Void, Boolean>{

		//Attributes
		private Context context;
		private ProgressDialog progressDialog;
		private ListView list;
		
		//Construct
		public AsyncGetThreads(Context c, ListView l) {
						
			context = c;
			list = l;
			
		}
		
		@Override
		protected void onPreExecute() {

			if( context != null ) {
				
				progressDialog = new ProgressDialog(this.context);
				progressDialog.setTitle( R.string.general_wait );
				progressDialog.setMessage( "Downloading the threads..." );
				progressDialog.show();
			
			}	
				
		}
		
		@Override
		protected Boolean doInBackground( Long... arg0 ) {

			try {
				
				currentForum = WebsiteHandler.getThreadsForForum( arg0[0] );
				return ( currentForum != null );
				
			} catch( Exception ex ) {
				
				ex.printStackTrace();
				return false;
				
			}
			
		}
		
		@Override
		protected void onPostExecute(Boolean results) {
		
			if( context != null && this.progressDialog != null ) { 
					
				this.progressDialog.dismiss();
			
			}
			
			if( results ) { 

				if( list.getAdapter() != null ) { 
				
					( (ThreadListAdapter) list.getAdapter() ).setItemArray( currentForum.getThreads() ); 

				}
				
			}
			
			
		}
	
	}
	
}