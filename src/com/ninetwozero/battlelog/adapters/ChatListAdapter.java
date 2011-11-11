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

package com.ninetwozero.battlelog.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.ChatMessage;

public class ChatListAdapter extends BaseAdapter {
	
	//Attributes
	Context context;
	ArrayList<ChatMessage> messageArray;
	LayoutInflater layoutInflater;
	TextView textMessage;
	
	//Construct
	public ChatListAdapter(Context c, ArrayList<ChatMessage> m, LayoutInflater l) {
	
		context = c;
		messageArray = m;
		layoutInflater = l;
		
	}

	@Override
	public int getCount() {

		return ( messageArray != null )? messageArray.size() : 0;
		
	}

	@Override
	public ChatMessage getItem( int position ) {

		return this.messageArray.get( position );

	}

	@Override
	public long getItemId( int position ) {

		return this.messageArray.get( position ).getChatId();
		
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {

		//Get the current item
		ChatMessage currentMessage = getItem(position);
		
		//Recycle
		if ( convertView == null ) {

			convertView = layoutInflater.inflate( R.layout.list_item_chat, parent, false );

		}

		//Set the TextViews
		textMessage = (TextView) convertView.findViewById( R.id.text_message);
		
		convertView.setTag( currentMessage );

		return convertView;
	}
	
}