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
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ninetwozero.battlelog.asynctasks.AsyncChatRefresh;
import com.ninetwozero.battlelog.asynctasks.AsyncChatSend;
import com.ninetwozero.battlelog.datatypes.Constants;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.SerializedCookie;
import com.ninetwozero.battlelog.misc.RequestHandler;


public class ChatView extends ListActivity {

	//Attributes
	final private Context context = this;
	private ProfileData profileData;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	
	//Elements
	private ListView listView;
	private EditText fieldMessage;
	private Button buttonSend;
	
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
        
        //Let's reload the chat will we?
        reloadChat();
        
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

    public void reloadChat() {
    	
    	new AsyncChatRefresh(this, listView, layoutInflater).execute( profileData.getPersonaId() );
    
    }
    
    public void onClick(View v) {
    	
    	//Send?
    	if( v.getId() == R.id.button_send ) {
    	
    		
    		new AsyncChatSend(this, profileData.getProfileId(), buttonSend, false).execute(

				sharedPreferences.getString( "battlelog_post_checksum", "" ),
				fieldMessage.getText().toString()
    				
			);
    		
    	}
    	
    }
    
}