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
import android.widget.ImageView;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.ProfileData;

public class RequestListAdapter extends BaseAdapter {
	
	//Attributes
	private Context context;
	private ArrayList<ProfileData> profileArray;
	private LayoutInflater layoutInflater;
	
	//Construct
	public RequestListAdapter(Context c, ArrayList<ProfileData> p, LayoutInflater l) {
	
		context = c;
		profileArray = p;
		layoutInflater = l;
		
	}

	@Override
	public int getCount() {

		return ( profileArray != null )? profileArray.size() : 0;
		
	}

	@Override
	public ProfileData getItem( int position ) {

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
		ProfileData currentProfile = getItem(position);
		
		//Recycle
		if ( convertView == null ) {

			convertView = layoutInflater.inflate( R.layout.list_item_request, parent, false );

		}

		//Set the TextView
		( (TextView) convertView.findViewById( R.id.text_user ) ).setText( currentProfile.getAccountName() );
		
		//Hot-wire the views
		( (ImageView) convertView.findViewById(R.id.button_accept) ).setTag( currentProfile );
		( (ImageView) convertView.findViewById(R.id.button_decline) ).setTag( currentProfile );
		
		//Set the tag so it's up for grabs
		convertView.setTag( currentProfile );
		
		return convertView;
	
	}
	
	public void setItemArray(ArrayList<ProfileData> p) { this.profileArray = p; }		
	
}