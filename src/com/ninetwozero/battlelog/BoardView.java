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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.ForumListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncSessionValidate;
import com.ninetwozero.battlelog.datatypes.Board;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class BoardView extends ListActivity {

	//Attributes
	private final Context CONTEXT = this;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private String title;
	private ArrayList<Board.Forum> forums;
	private String locale;

	//Elements
	private ListView listView;
	private TextView textTitle;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		ArrayList<ShareableCookie> shareableCookies = icicle.getParcelableArrayList(Constants.SUPER_COOKIES);
			RequestHandler.setCookies( shareableCookies );
    		
    	}
    	
    	//Set the content view
        setContentView(R.layout.board_view);

        //Prepare to tango
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.layoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        locale = sharedPreferences.getString( Constants.SP_BL_LOCALE, "en" );
        
        //Init
        initLayout();
        
	}        

	@Override
	public void onResume() {
		
		super.onResume();    	
		
		//If we don't have a profile...
    	if( SessionKeeper.getProfileData() == null ) {
    		
    		//...but we do indeed have a cookie...
    		if( !sharedPreferences.getString( Constants.SP_BL_COOKIE_VALUE, "" ).equals( "" ) ){
    			
    			//...we set the SessionKeeper, but also reload the cookies! Easy peasy!
    			SessionKeeper.setProfileData( SessionKeeper.generateProfileDataFromSharedPreferences(sharedPreferences) );
    			RequestHandler.setCookies( 
    			
    				new ShareableCookie(

    					sharedPreferences.getString( Constants.SP_BL_COOKIE_NAME, "" ),
    					sharedPreferences.getString( Constants.SP_BL_COOKIE_VALUE, "" ),
    					Constants.COOKIE_DOMAIN
    						
    				)
    				
    			);
    			
    			//...but just to be sure, we try to verify our session "behind the scenes"
    			new AsyncSessionValidate(this, sharedPreferences).execute();
    			
    		} else {
    			
    			//Aw man, that backfired.
    			Toast.makeText( this, R.string.info_txt_session_lost, Toast.LENGTH_SHORT).show();
    			startActivity( new Intent(this, Main.class) );
    			finish();
    			
    		}
    		
    	}
    	this.reloadLayout();
		
	}
	
	public void initLayout() {
		
		//Title
		if( textTitle == null ) {
			
			textTitle = (TextView) findViewById(R.id.text_board_title);
			
		}
		
        //Get the listView
        if( listView == null ) { 
        	
        	listView = getListView(); 
        	listView.setAdapter( new ForumListAdapter( this, forums, layoutInflater ) );
        	
        }
		
	}
	
    public void reloadLayout() { 
    	
    	if( forums == null ) {
    		
    		new AsyncGetForums(this).execute(); 
    	
    	} else {
    		
    		new AsyncGetForums(null).execute(); 
    		
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
			
		} else if( item.getItemId() == R.id.option_search ) {
			
			startActivity( new Intent(this, ForumSearchView.class ) );
			
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
		
		startActivity( 
				
			new Intent(this, ForumView.class).putExtra(
					
				"forumId",
				id
				
			).putExtra(
			
				"forumTitle",
				((Board.Forum) v.getTag()).getTitle()
					
			)
			
		);
		
	}

	private class AsyncGetForums extends AsyncTask<Void, Void, Boolean>{

		//Attributes
		private Context context;
		private ProgressDialog progressDialog;
		
		//Construct
		public AsyncGetForums(Context c) {
						
			context = c;
			
		}
		
		@Override
		protected void onPreExecute() {

			if( context != null ) {
				
				progressDialog = new ProgressDialog(this.context);
				progressDialog.setTitle( R.string.general_wait );
				progressDialog.setMessage( "Downloading the forums..." );
				progressDialog.show();
			
			}	
				
		}
		
		@SuppressWarnings( "unchecked" ) // I know what I'm doing... :D
		@Override
		protected Boolean doInBackground( Void... arg0 ) {

			try {
				
				Object[] result = WebsiteHandler.getAllForums( locale );
				title = (String) result[0];
				forums = (ArrayList<Board.Forum>) result[1];
				return ( forums != null );
				
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
			
			//update the title
			textTitle.setText( title );
			
			if( listView.getAdapter() != null ) { 
				
				((ForumListAdapter)listView.getAdapter()).setItemArray( forums ); 
				
			}
			
			
		}
	
	}
	
	public Dialog generateDialogLanguageList( final Context context, final String[] languages, final String[] locales ) {
		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
	    //Set the title and the view
		builder.setTitle( R.string.info_forum_lang );
		
		builder.setSingleChoiceItems(
				
			languages, -1, new DialogInterface.OnClickListener() {
		  
				public void onClick(DialogInterface dialog, int item) {
			    	
					sharedPreferences.edit().putString( Constants.SP_BL_LOCALE, locales[item] ).commit();
					locale = locales[item];
					reloadLayout();
					dialog.dismiss();
		
				}
				
			}
		
		);
		
		//CREATE
		return builder.create();
		
	}
	
	public void onClick(View v) {
	
		if( v.getId() == R.id.wrap_top ) {
		
			generateDialogLanguageList(this, DataBank.getLanguages(), DataBank.getLocales()).show();
			
		}
		
	}
	
}