package com.ninetwozero.battlelog.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.ProfileData;

public class FriendListAdapter extends BaseAdapter {
	
	//Attributes
	Context context;
	ArrayList<ProfileData> profileArray;
	LayoutInflater layoutInflater;
	String tempStatus;
	TextView textPersona, textStatus;
	
	//Construct
	public FriendListAdapter(Context c, ArrayList<ProfileData> p, LayoutInflater l) {
	
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

			convertView = layoutInflater.inflate( R.layout.list_item_friend, parent, false );

		}

		//Set the TextViews
		textPersona = (TextView) convertView.findViewById( R.id.text_persona );
		textPersona.setText( currentProfile.getPersonaName() );
		textStatus = (TextView) convertView.findViewById( R.id.text_status );

		
		//Oh-oh
		if( currentProfile.isPlaying() && currentProfile.isOnline() ) {
			
			textPersona.setTextColor( context.getResources().getColor(R.color.blue) );
			textStatus.setText( "Playing" );
			textStatus.setTextColor( context.getResources().getColor(R.color.blue) );
			
		} else if( currentProfile.isOnline() ) {
			
			textPersona.setTextColor( context.getResources().getColor(R.color.green) );
			textStatus.setText( "Online" );
			textStatus.setTextColor( context.getResources().getColor(R.color.green) );
						
		} else {
			
			textStatus.setText( "Offline" );
			
		}
		
		convertView.setTag( currentProfile );

		return convertView;
	}
	
}