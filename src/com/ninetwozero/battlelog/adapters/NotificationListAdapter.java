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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.NotificationData;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class NotificationListAdapter extends BaseAdapter {
	
	//Attributes
	Context context;
	ArrayList<NotificationData> itemArray;
	LayoutInflater layoutInflater;
	String tempStatus;
	TextView textPersona, textStatus;
	long activeUserId;
	
	//Construct
	public NotificationListAdapter(Context c, ArrayList<NotificationData> fi, LayoutInflater l, long auid) {
	
		context = c;
		itemArray = fi;
		layoutInflater = l;
		activeUserId = auid;
	
	}

	@Override
	public int getCount() {

		return ( itemArray != null )? itemArray.size() : 0;
		
	}

	@Override
	public NotificationData getItem( int position ) {

		return this.itemArray.get( position );

	}

	@Override
	public long getItemId( int position ) {

		return this.itemArray.get( position ).getItemId();
		
	}
	
	public void setItemArray( ArrayList<NotificationData> ia ) { this.itemArray = ia; }
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {

		//Get the current item
		NotificationData currentItem = getItem(position);
		
		//Recycle
		if ( convertView == null ) {

			convertView = layoutInflater.inflate( R.layout.list_item_feed, parent, false );

		}
	
		//Set the views
		((TextView) convertView.findViewById(R.id.text_title)).setText( currentItem.getMessage( this.activeUserId ) );
		((TextView) convertView.findViewById(R.id.text_date)).setText( PublicUtils.getRelativeDate( currentItem.getDate() ) );

		
		//Hook it up on the tag
		convertView.setTag( currentItem );

		//Send it back
		return convertView;
	}
	
	@Override
	public void notifyDataSetChanged() {
		
		super.notifyDataSetChanged();

	}
}