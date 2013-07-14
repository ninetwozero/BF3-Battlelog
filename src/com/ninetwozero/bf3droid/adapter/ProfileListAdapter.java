/*
    This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.misc.PublicUtils;

import java.util.List;

public class ProfileListAdapter extends BaseAdapter {

	// Attributes
	private Context mContext;
	private List<ProfileData> mData;
	private LayoutInflater mLayoutInflater;

	// Construct
	public ProfileListAdapter(Context c, List<ProfileData> p, LayoutInflater l) {
		mContext = c;
		mData = p;
		mLayoutInflater = l;
	}

	@Override
	public int getCount() {
		return (mData != null) ? mData.size() : 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public ProfileData getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mData.get(position).getId();
	}

	public long getPersonaId(int position) {
		return mData.get(position).getPersona(0).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Get the current item
		ProfileData current = getItem(position);
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.list_item_generic_profile, parent, false);
		}

		// Set the image & name
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.image_avatar);
		imageView.setImageBitmap(BitmapFactory.decodeFile(PublicUtils
				.getCachePath(mContext).toString()
				+ current.getGravatarHash()
				+ ".png"));
		if (imageView.getBackground() == null) {
			imageView.setImageResource(R.drawable.default_avatar);
		}
		((TextView) convertView.findViewById(R.id.text_username))
				.setText(current.getUsername());
		return convertView;
	}

	public void setData(List<ProfileData> pa) {
		mData = pa;
		notifyDataSetChanged();
	}
}
