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
import com.ninetwozero.battlelog.datatypes.PlatoonMemberData;

public class PlatoonUserListAdapter extends BaseAdapter {
	
	//Attributes
	private Context context;
	private ArrayList<PlatoonMemberData> profileArray;
	private LayoutInflater layoutInflater;
	private TextView textUser;
	private int lastSeparator;
	
	//Construct
	public PlatoonUserListAdapter(Context c, ArrayList<PlatoonMemberData> p, LayoutInflater l) {
	
		context = c;
		profileArray = p;
		layoutInflater = l;
		
	}

	@Override
	public int getCount() {

		return ( profileArray != null )? profileArray.size() : 0;
		
	}
	
	@Override
	public int getItemViewType(int position) {
	    
		if( getItem(position).getAccountName().startsWith( "0000000" ) ) {
			
			if( getItem(position).getAccountName().equals("00000007") ) {
				
				lastSeparator = position;
				
			}
			return 0;
		
		} else if( lastSeparator > 0 && position > lastSeparator ) {
			
			if( profileArray.get( position ).getMembershipLevel() == 1 ) { return 1; } 
			else { return 2; }
			
		} else {
			
			return 2;
		
		}
	
	}

	@Override
	public int getViewTypeCount() {
	   
		return 3;
	
	}

	@Override
	public PlatoonMemberData getItem( int position ) {

		return this.profileArray.get( position );

	}

	@Override
	public long getItemId( int position ) {

		return this.profileArray.get( position ).getProfileId();
		
	}

	public long getPersonaId( int position ) {
	
		return this.profileArray.get( position ).getPersonaId();
		
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {

		//Get the current item
		PlatoonMemberData currentProfile = getItem(position);
		
		//Let's see what we found
		if( getItemViewType(position) == 0 ) {
			
			//Can we recycle?
			if( convertView == null ) {

				convertView = layoutInflater.inflate( R.layout.list_friends_divider, parent, false);
			
			} 
			
			//Set the fields
			((TextView) convertView.findViewById( R.id.text_title )).setText( currentProfile.getPersonaName() );
			convertView.setOnClickListener( null );
			convertView.setOnLongClickListener( null );
			
		} else if( getItemViewType(position) == 1 ) {
			
			//Can we recycle
			if( convertView == null ) {

				convertView = layoutInflater.inflate( R.layout.list_item_request, parent, false);
				
			} 
			
			//Set the TextViews
			textUser = (TextView) convertView.findViewById( R.id.text_user );
			textUser.setText( currentProfile.getAccountName() );
			
			convertView.setTag( currentProfile );
			
		} else {
		
			//Recycle
			if ( convertView == null ) {
	
				convertView = layoutInflater.inflate( R.layout.list_item_user, parent, false );
	
			}
	
			//Set the TextViews
			textUser = (TextView) convertView.findViewById( R.id.text_user );
			textUser.setText( currentProfile.getAccountName() );
			
			convertView.setTag( currentProfile );
		
		}

		return convertView;
	}
	
	public void setProfileArray( ArrayList<PlatoonMemberData> pa ) { this.profileArray = pa; }

	
}