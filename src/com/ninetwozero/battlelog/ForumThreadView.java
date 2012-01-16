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
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.ThreadListAdapter;
import com.ninetwozero.battlelog.adapters.ThreadPostListAdapter;
import com.ninetwozero.battlelog.datatypes.Board;
import com.ninetwozero.battlelog.datatypes.Board.ThreadData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class ForumThreadView extends ListActivity {

	//Attributes
	private final Context CONTEXT = this;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private ThreadData currentThread;
	private long threadId;
	private String threadTitle;

	//Elements
	private ListView listView;
	
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
    	
    	//Set the content view
        setContentView(R.layout.board_forum_thread_view);

        //Prepare to tango
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.layoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        
        //Get the threadId
        threadId = getIntent().getLongExtra( "threadId", 0 );
        threadTitle = getIntent().getStringExtra( "threadTitle" );

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
		if( threadTitle != null ) {
			
			( (TextView) findViewById( R.id.text_title_thread) ).setText( threadTitle );
			
		}
		
        //Get the listView
        if( listView == null ) { 
        	
        	listView = getListView(); 
        	
        	if( currentThread == null ) {
        	
        		listView.setAdapter( new ThreadPostListAdapter( this, null, layoutInflater ) );
        	
        	} else {
        		
            	listView.setAdapter( new ThreadPostListAdapter( this, currentThread.getPosts(), layoutInflater ) );
        	
        	}
        	
        }
		
	}
	
    public void reloadLayout() { 
    	
    	if( currentThread == null ) {
    		
    		new AsyncGetPosts(this, listView).execute( threadId ); 
    	
    	} else {
    		
    		new AsyncGetPosts(null, listView).execute( threadId ); 
    		
    	}
    	
    }
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

    	//Inflate!!
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_forumview, menu );		
		return super.onCreateOptionsMenu( menu );
	
    }
    
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		//Let's act!
		if( item.getItemId() == R.id.option_reload ) {
	
			this.reloadLayout();
			
		} else if( item.getItemId() == R.id.option_new ) {
			
			this.generateDialogNew( this ).show();
			
		} else if( item.getItemId() == R.id.option_back ) {
			
			this.finish();
			
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
		outState.putParcelableArrayList(Constants.SUPER_COOKIES, RequestHandler.getCookies());
	
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int p, long id) {
		
		Toast.makeText( this, "Unimplemented feature - what should this do - contextMenu with option to quote?", Toast.LENGTH_SHORT ).show();
		
	}

	private class AsyncGetPosts extends AsyncTask<Long, Void, Boolean>{

		//Attributes
		private Context context;
		private ProgressDialog progressDialog;
		private ListView list;
		
		//Construct
		public AsyncGetPosts(Context c, ListView l) {
						
			context = c;
			list = l;
			
		}
		
		@Override
		protected void onPreExecute() {

			if( context != null ) {
				
				progressDialog = new ProgressDialog(this.context);
				progressDialog.setTitle( R.string.general_wait );
				progressDialog.setMessage( "Downloading the posts..." );
				progressDialog.show();
			
			}	
				
		}
		
		@Override
		protected Boolean doInBackground( Long... arg0 ) {

			try {
				
				currentThread = WebsiteHandler.getPostsForThread( arg0[0] );
				return ( currentThread != null );
				
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
				
					( (ThreadPostListAdapter) list.getAdapter() ).setItemArray( currentThread.getPosts() ); 

				}
				
			}
			
			
		}
	
	}
	
public Dialog generateDialogNew(final Context context) {
		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	    final View layout = inflater.inflate(R.layout.dialog_threadpost_new, (ViewGroup) findViewById(R.id.dialog_root));
		
	    //Set the title and the view
		builder.setTitle( R.string.info_dialog_threadpost );
		builder.setView(layout);
		
		//Dialog options
		builder.setPositiveButton(
				
			android.R.string.ok, 
			new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
					String content = ((EditText) layout.findViewById(R.id.text_content)).getText().toString();
					
					boolean status = WebsiteHandler.postReplyInThread( 
						
						CONTEXT, 
						content,
						sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ), 
						threadId
						
					);
					
					//Well, how'd it go?
					if( status ) { 
						
						Toast.makeText( CONTEXT, R.string.info_forum_newpost_true, Toast.LENGTH_SHORT).show();
						reloadLayout();
						dialog.dismiss(); 
						
					} else {
						
						Toast.makeText( CONTEXT, R.string.info_forum_newpost_false, Toast.LENGTH_SHORT).show();
						
					}
			   
				}
				
			}
			
		);
		
		builder.setNegativeButton(
				
			android.R.string.cancel, 
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