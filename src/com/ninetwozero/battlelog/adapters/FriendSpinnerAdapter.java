package com.ninetwozero.battlelog.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ninetwozero.battlelog.datatypes.ProfileData;


public class FriendSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
	
	private Context context;
	private ArrayList<ProfileData> profileArray;
	private LayoutInflater layoutInflater;

	public FriendSpinnerAdapter(Context c, ArrayList<ProfileData> p, LayoutInflater l) {
		
		this.context = c;
		this.profileArray = p;
		this.layoutInflater = l;
	}

	@Override
	public int getCount() {

		if ( profileArray != null ) {

			return profileArray.size();

		} else {

			return 0;

		}
	}

	@Override
	public ProfileData getItem( int position ) {

		return this.profileArray.get( position );
	}

	@Override
	public long getItemId( int position ) {

		return profileArray.get( position ).getProfileId();
		
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {

		// Declare the current array
		ProfileData current_item = profileArray.get( position );

		//Recycle view - it's good for Mother Nature!
		if ( convertView == null ) {

			convertView = this.layoutInflater.inflate( android.R.layout.simple_spinner_dropdown_item, parent, false );

		}

		//Set the TextView
		( (TextView) convertView.findViewById( android.R.id.text1 ) ).setText( current_item.getPersonaName() );

		//Return the view
		return convertView;
	}

}