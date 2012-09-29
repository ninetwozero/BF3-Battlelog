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

package com.ninetwozero.battlelog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.ninetwozero.battlelog.datatype.ProfileData;

import java.util.List;

public class FriendSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private List<ProfileData> profileArray;
    private LayoutInflater layoutInflater;

    public FriendSpinnerAdapter(List<ProfileData> p,
                                LayoutInflater l) {

        profileArray = p;
        layoutInflater = l;
    }

    @Override
    public int getCount() {

        if (profileArray != null) {

            return profileArray.size();

        } else {

            return 0;

        }
    }

    @Override
    public ProfileData getItem(int position) {

        return profileArray.get(position);
    }

    @Override
    public long getItemId(int position) {

        return profileArray.get(position).getId();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Declare the current array
        ProfileData currentItem = profileArray.get(position);

        // Recycle view - it's good for Mother Nature!
        if (convertView == null) {

            convertView = layoutInflater.inflate(
                    android.R.layout.simple_spinner_dropdown_item, parent,
                    false);

        }

        // Set the TextView
        ((TextView) convertView.findViewById(android.R.id.text1))
                .setText(currentItem.getUsername());

        // Return the view
        return convertView;
    }

}
