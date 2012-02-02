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
import android.os.SystemClock;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.Board;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class EndlessThreadListAdapter extends EndlessAdapter {

	private RotateAnimation rotate;
	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<Board.ThreadData> threads;
	private long forumId;
	
	public EndlessThreadListAdapter(Context c, ArrayList<Board.ThreadData> data, LayoutInflater l, long f) {
	
		super( new ThreadListAdapter(c, data, l) );
		
		//Get the data
		context = c;
		layoutInflater = l;
		forumId = f;
		
		//Create a rotation
		rotate = new RotateAnimation(
				
			0f, 360f, Animation.RELATIVE_TO_SELF,
			0.5f, Animation.RELATIVE_TO_SELF,
			0.5f
		);
		rotate.setDuration(600);
		rotate.setRepeatMode(Animation.RESTART);
		rotate.setRepeatCount(Animation.INFINITE);

	}

	@Override
	protected View getPendingView(ViewGroup parent) {
		

		/*View row = layoutInflater.inflate(R.layout.row, null);
		View child = row.findViewById(android.R.id.text1);

		child.setVisibility(View.GONE);

		child = row.findViewById(R.id.throbber);
		child.setVisibility(View.VISIBLE);
		child.startAnimation(rotate);
*/
		return new LinearLayout(context);
	}

	@Override
	protected boolean cacheInBackground() {
		
		try {
			
			threads = WebsiteHandler.getThreadsForForum( forumId, Math.round( getWrappedAdapter().getCount() / 10 ) );
			return ( threads != null );
			
		} catch( Exception ex) {
			
			ex.printStackTrace();
			return false;
			
		}

	}

	@Override
	protected void appendCachedData() {
	
		if (getWrappedAdapter().getCount()<75) {
		
			@SuppressWarnings("unchecked")
			ThreadListAdapter tla = (ThreadListAdapter) getWrappedAdapter();
			tla.add( threads );
			
		}
		  
	}
	
}