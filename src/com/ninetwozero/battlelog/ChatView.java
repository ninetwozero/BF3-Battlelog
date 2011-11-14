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
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ninetwozero.battlelog.asynctasks.AsyncChatRefresh;
import com.ninetwozero.battlelog.asynctasks.AsyncChatSend;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.SerializedCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class ChatView extends ListActivity {

	//Attributes
	private long chatId;
	private ProfileData profileData;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	
	//Elements
	private ListView listView;
	private EditText fieldMessage;
	private Button buttonSend;
	
	//Misc
	Timer timerReload;
	
	//CHAT-related
	private AsyncChatRefresh asyncChatRefresh;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( "serializedCookies" ) ) {
    		
    		RequestHandler.setSerializedCookies( (ArrayList<SerializedCookie> ) icicle.getSerializable("serializedCookies") );
    	
    	}
    	
        //Did we get someone to chat with?
        if( !getIntent().hasExtra( "profile" ) ) { finish(); }
    	
    	//Set the content view
        setContentView(R.layout.chat_view);

        //Prepare to tango
        this.sharedPreferences = this.getSharedPreferences( Constants.fileSharedPrefs, 0);
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = getListView();
        
        //Let's get the other chat participant
        profileData = (ProfileData) getIntent().getSerializableExtra( "profile" );
	
        //Setup the title
        setTitle( getTitle().toString().replace( "...", profileData.getAccountName() ) );
        
        //Get the elements
        buttonSend = (Button) findViewById(R.id.button_send);
        fieldMessage = (EditText) findViewById(R.id.field_message);
        
        //Try to get the chatid
        new AsyncGetChatId(this, profileData.getProfileId() ).execute( sharedPreferences.getString( "battlelog_post_checksum", "" ) );
        
        //Let's reload the chat will we?
        timerReload = new Timer();
        timerReload.schedule(
        		
    		new TimerTask() {

				@Override
				public void run() {

					reloadChat();
					
				}
			}, 
    		0,
    		30000
    	
        ); 
        
	}    
	
	public class AsyncGetChatId extends AsyncTask<String, Void, Boolean> {

		//Attributes
		private Context context;
		private long chatId;
		private long profileId;
		
		//Construct
		public AsyncGetChatId(Context c, long pId) {
			
			this.context = c;
			this.profileId = pId;
			
		}
		
		@Override
		protected Boolean doInBackground( String... arg0 ) {

			try {
				
				this.chatId = WebsiteHandler.getChatId(profileId, arg0[0]);
				return true;
			
			} catch( WebsiteHandlerException ex ) {
			
				ex.printStackTrace();
				return false;
					
			}
			
		}
		
		@Override
		protected void onPostExecute( Boolean results ) {
			
			//If we succeeded, we're ok with this
			if( results ) {
			
				setChatId(chatId);
				buttonSend.setEnabled( true );

			}
			
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
    public void onDestroy() {
    	
    	super.onDestroy();
    	if( timerReload != null ) timerReload.cancel();
    	
    }
    
    public void reloadChat() {
    	
    	new AsyncChatRefresh(this, listView, layoutInflater).execute( profileData.getProfileId() );
    
    }
    
    public void onClick(View v) {
    	
    	//Send?
    	if( v.getId() == R.id.button_send ) {
    	
    		
    		new AsyncChatSend(
    				
				this,
				profileData.getProfileId(),
				chatId, 
				buttonSend, 
				false, 
				new AsyncChatRefresh(this, listView, layoutInflater)
			
    		).execute(

				sharedPreferences.getString( "battlelog_post_checksum", "" ),
				fieldMessage.getText().toString()
    				
			);
    		
    	}
    	
    }
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_profileview, menu );
		return super.onCreateOptionsMenu( menu );
	
    }
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		//Let's act!
		if( item.getItemId() == R.id.option_reload ) {
	
			this.reloadChat();
			
		} else if( item.getItemId() == R.id.option_back ) {
			
			((Activity) this).finish();
			
		}
		
		// Return true yo
		return true;

	}  
    
    public void setChatId(long cId) { this.chatId = cId; }

}