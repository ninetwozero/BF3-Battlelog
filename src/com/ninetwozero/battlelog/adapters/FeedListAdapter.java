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
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class FeedListAdapter extends BaseAdapter {
	
	//Attributes
	private Context context;
	private ArrayList<FeedItem> itemArray;
	private LayoutInflater layoutInflater;
	
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
	
	public void setItemArray( ArrayList<FeedItem> ia ) { 
		
		this.itemArray = ia; 
		this.notifyDataSetInvalidated();
	
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
		((TextView) convertView.findViewById(R.id.text_title)).setText( 
				
			!currentItem.isCensored() ? Html.fromHtml( currentItem.getTitle() ) : context.getString( R.string.general_censored ) 
						
		);

		//How many likes/comments?
		String textHooah = ( currentItem.getNumLikes() == 1 )? context.getString( R.string.info_hooah_s ) : context.getString( R.string.info_hooah_p );
		String textComments = ( currentItem.getNumComments() == 1 )? context.getString( R.string.info_comment_s ) : context.getString( R.string.info_comment_p );
		String content = textComments.replace("{num}", currentItem.getNumComments() + "");

		//Set the fields
		((ImageView) convertView.findViewById(R.id.image_avatar)).setImageBitmap( 

			BitmapFactory.decodeFile( PublicUtils.getCachePath( context ).toString() + currentItem.getAvatarForPost() + ".png" )

		);
		((TextView) convertView.findViewById(R.id.text_date)).setText( PublicUtils.getRelativeDate( context, currentItem.getDate() ) );
		((TextView) convertView.findViewById(R.id.text_hooah)).setText( textHooah.replace("{num}", currentItem.getNumLikes() + ""));
		((TextView) convertView.findViewById(R.id.text_comment)).setText(content);
		
		//Hook it up on the tag
		convertView.setTag( currentItem );

		//Send it back
		return convertView;
	}

}