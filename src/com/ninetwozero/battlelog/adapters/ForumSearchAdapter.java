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
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.Board;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class ForumSearchAdapter extends BaseAdapter {
	
	//Attributes
	private Context context;
	private ArrayList<Board.SearchResult> itemArray;
	private LayoutInflater layoutInflater;
	
	//Construct
	public ForumSearchAdapter(Context c, ArrayList<Board.SearchResult> m, LayoutInflater l) {
	
		context = c;
		itemArray = m;
		layoutInflater = l;

	}

	@Override
	public int getCount() {

		return ( itemArray != null )? itemArray.size() : 0;
		
	}

	@Override
	public Board.SearchResult getItem( int position ) {

		return this.itemArray.get( position );

	}

	@Override
	public long getItemId( int position ) {

		return this.itemArray.get( position ).getThreadId();
		
	}
	
	@Override
	public int getItemViewType(int position) {
	    
		return 0;
		
	}

	@Override
	public int getViewTypeCount() {
	   
		return 1;
	
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {

		//Get the current item
		Board.SearchResult currentItem = getItem(position);
		
		//Recycle
		if ( convertView == null ) {

			convertView = layoutInflater.inflate( R.layout.list_item_forum_search, parent, false );
			
		}

		//Let's do the coloring
		if( currentItem.isOfficial() ) { 
			
			//Set the colors
			convertView.findViewById( R.id.bar_official ).setBackgroundColor( context.getResources().getColor( R.color.blue ) );
			
		} else {
			
			//Set the colors
			convertView.findViewById( R.id.bar_official ).setBackgroundColor( context.getResources().getColor( R.color.lightgrey ) );
			
		}
		
		//Set the TextViews
		((TextView) convertView.findViewById( R.id.string_title )).setText( currentItem.getTitle() );
		((TextView) convertView.findViewById( R.id.string_owner )).setText( 
				
			Html.fromHtml( 
					
				context.getString( R.string.info_xml_threaddate ).replace(
					
					"{date}",
					PublicUtils.getRelativeDate( context, currentItem.getDate() )
					
				).replace(
				
					"{user}",
					currentItem.getOwner().getAccountName()
						
				)
				
			)
				
		);
		
		
		//Store the object
		convertView.setTag( currentItem );

		//R-TURN
		return convertView;
	
	}
	
	public void setItemArray(ArrayList<Board.SearchResult> array) {
		
		this.itemArray = array;
		this.notifyDataSetInvalidated();
		
	}
	
	public void addItem( ArrayList<Board.SearchResult> array ) {
	
		this.itemArray.addAll( array );
		
	}
	
}