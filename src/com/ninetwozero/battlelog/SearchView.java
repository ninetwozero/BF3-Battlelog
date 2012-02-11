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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.SearchDataAdapter;
import com.ninetwozero.battlelog.datatypes.GeneralSearchResult;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class SearchView extends ListActivity {

	//Attributes
	private LayoutInflater layoutInflater;
	private SharedPreferences sharedPreferences;
	
	//Elements
	private ListView listView;
	private EditText fieldSearch;
	private Button buttonSearch;
	
	//Misc
	private ArrayList<GeneralSearchResult> searchResults;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		ArrayList<ShareableCookie> shareableCookies = icicle.getParcelableArrayList(Constants.SUPER_COOKIES);
			
    		if( shareableCookies != null ) { 
    			
    			RequestHandler.setCookies( shareableCookies );
    		
    		} else {
    			
    			finish();
    			
    		}
    		
    	}
    	
    	//Set the content view
        setContentView(R.layout.search_view);

        //Prepare to tango
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        
        //Get the elements
        buttonSearch = (Button) findViewById(R.id.button_search);
        fieldSearch = (EditText) findViewById(R.id.field_search);
        
        //Threads!
        searchResults = new ArrayList<GeneralSearchResult>();
        setupList( searchResults );
	}    
	
	public void setupList(ArrayList<GeneralSearchResult> results) {
	
		//Do we have it?
		if( listView == null ) { 
	        
	        //Get the ListView
	        listView = getListView();
	        listView.setChoiceMode( ListView.CHOICE_MODE_NONE );

		}
		
		//Does it have an adapter?
		if( listView.getAdapter() == null ) { 
			
			listView.setAdapter( new SearchDataAdapter(this, results, layoutInflater ) );
			
		} else {
		
			((SearchDataAdapter) listView.getAdapter()).setItemArray(results);
			
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

    public void onClick(View v) {
    	
    	//Send?
    	if( v.getId() == R.id.button_search ) {
    	
    		//Send it!
    		new AsyncForumSearch( this ).execute( fieldSearch.getText().toString(), sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ));
    		
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
		if( item.getItemId() == R.id.option_back ) {
			
			((Activity) this).finish();
			
		}
		
		// Return true yo
		return true;

	}  
	
	public class AsyncForumSearch extends AsyncTask<String, Void, Boolean> {
	
		//Attributes
		private Context context;
		
		//Construct
		public AsyncForumSearch(Context c) { this.context = c; }
		
		@Override
		protected void onPreExecute() {
			
			if( context instanceof SearchView ) { 
				
				((SearchView) context).toggleSearchButton();
				
			}
			
		}
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			
			try {

				searchResults = WebsiteHandler.searchBattlelog(context, arg0[0], arg0[1] );
				return true;
			
			} catch( Exception ex ) { 
				
				ex.printStackTrace();
				return false;
				
			}
			
		}
		
		@Override
		protected void onPostExecute( Boolean results ) {
		
			//Let's evaluate
			if( results ) { 
			
				if( context instanceof SearchView ) { 
				
					((SearchView) context).setupList(searchResults);
					((SearchView) context).toggleSearchButton();
					
				}
				
			} else {
				
				if( context instanceof SearchView ) { 
					
					((SearchView) context).toggleSearchButton();
					
				}
				Toast.makeText( context, R.string.info_xml_generic_error, Toast.LENGTH_SHORT).show();
				
			}
			
		}
	
	}

	public void toggleSearchButton() {

		buttonSearch.setEnabled( !buttonSearch.isEnabled() );
		
		//Update the text
		if( buttonSearch.isEnabled() ) { buttonSearch.setText( R.string.label_search ); }
		else { buttonSearch.setText( R.string.label_search_ongoing ); }
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int p, long id) {

		//Init
		Intent intent = null;
		GeneralSearchResult result = (GeneralSearchResult) v.getTag();
		
		//Build the intent
		if( result.hasProfileData() ) {

			intent = new Intent(this, ProfileView.class).putExtra( "profile" , result.getProfileData() );
			
		} else {
		
			intent = new Intent(this, PlatoonView.class).putExtra( "platoon", result.getPlatoonData() );	
		}
		
		//Start the activity
		startActivity(intent);
		
	}
}