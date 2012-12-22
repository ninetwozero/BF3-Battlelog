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

package com.ninetwozero.bf3droid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.ProfileData;

import java.util.List;

public class PlatoonInviteListAdapter extends BaseAdapter {

    // Attributes
    private List<ProfileData> profileArray;
    private LayoutInflater layoutInflater;

    // Construct
    public PlatoonInviteListAdapter(List<ProfileData> p, LayoutInflater l) {

        profileArray = p;
        layoutInflater = l;

    }

    @Override
    public int getCount() {

        return (profileArray != null) ? profileArray.size() : 0;

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
    public ProfileData getItem(int position) {

        return this.profileArray.get(position);

    }

    @Override
    public long getItemId(int position) {

        return this.profileArray.get(position).getId();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        ProfileData currentProfile = getItem(position);

        // Recycle
        if (convertView == null) {

            convertView = layoutInflater.inflate(
                    R.layout.list_item_platoon_invite, parent, false);

        }

        // Set the TextViews
        ((TextView) convertView.findViewById(R.id.text_name))
                .setText(currentProfile.getUsername());

        // Set the tag either way
        convertView.setTag(currentProfile);

        return convertView;
    }

    public void setItemArray(List<ProfileData> data) {
        this.profileArray = data;
    }

}
