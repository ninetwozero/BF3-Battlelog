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

import com.ninetwozero.battlelog.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.battlelog.datatypes.ProfileData;

public class PlatoonInviteListAdapter extends BaseAdapter {
	
	//Attributes
	private Context context;
	private ArrayList<ProfileData> profileArray;
	private LayoutInflater layoutInflater;
	
	//Construct
	public PlatoonInviteListAdapter(Context c, ArrayList<ProfileData> p, LayoutInflater l) {
	
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
	    
		return 0;
	
	}

	@Override
	public int getViewTypeCount() {
	   
		return 1;
	
	}

	@Override
	public ProfileData getItem( int position ) {

		return this.profileArray.get( position );

	}

	@Override
	public long getItemId( int position ) {

		return this.profileArray.get( position ).getProfileId();
		
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {

		//Get the current item
		ProfileData currentProfile = getItem(position);
		
		//Recycle
		if ( convertView == null ) {
	
			convertView = layoutInflater.inflate( R.layout.list_item_platoon_invite, parent, false );
	
		}
	
		//Set the TextViews
		((TextView) convertView.findViewById( R.id.text_name )).setText(currentProfile.getAccountName());

		//Set the tag either way
		convertView.setTag( currentProfile );

		return convertView;
	}
	
	public void setItemArray(ArrayList<ProfileData> data) { this.profileArray = data; }
	
}