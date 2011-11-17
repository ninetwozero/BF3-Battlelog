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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class FeedListAdapter extends BaseAdapter {
	
	//Attributes
	Context context;
	ArrayList<FeedItem> itemArray;
	LayoutInflater layoutInflater;
	String tempStatus;
	TextView textPersona, textStatus;
	
	//Construct
	public FeedListAdapter(Context c, ArrayList<FeedItem> fi, LayoutInflater l) {
	
		context = c;
		itemArray = fi;
		layoutInflater = l;
		
	}

	@Override
	public int getCount() {

		return ( itemArray != null )? itemArray.size() : 0;
		
	}

	@Override
	public FeedItem getItem( int position ) {

		return this.itemArray.get( position );

	}

	@Override
	public long getItemId( int position ) {

		return this.itemArray.get( position ).getId();
		
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {

		//Get the current item
		FeedItem currentItem = getItem(position);
		
		//Recycle
		if ( convertView == null ) {

			convertView = layoutInflater.inflate( R.layout.list_item_feed, parent, false );

		}
	
		//Set the views
		((TextView) convertView.findViewById(R.id.text_title)).setText( Html.fromHtml( currentItem.getTitle() ) );
		if( !currentItem.getContent().equals( "" ) ) {

			((TextView) convertView.findViewById(R.id.text_content)).setText( currentItem.getContent() );
		
		}
		
		((TextView) convertView.findViewById(R.id.text_date)).setText( PublicUtils.getRelativeDate( currentItem.getDate() ) );
		((TextView) convertView.findViewById(R.id.text_hooah)).setText( currentItem.getNumLikes() + " hoahs" );
		((TextView) convertView.findViewById(R.id.text_comment)).setText( currentItem.getComments().size() + " comments");
		
		//Hook it up on the tag
		convertView.setTag( currentItem );

		//Send it back
		return convertView;
	}
	
	@Override
	public void notifyDataSetChanged() {
		
		super.notifyDataSetChanged();
		
		Log.d(Constants.debugTag, "This is me doing a notification of it!!");
		
	}
}