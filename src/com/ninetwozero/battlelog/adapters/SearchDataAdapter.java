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
import com.ninetwozero.battlelog.datatypes.GeneralSearchResult;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.ProfileData;

public class SearchDataAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private ArrayList<GeneralSearchResult> itemArray;
    private LayoutInflater layoutInflater;

    // Construct
    public SearchDataAdapter(Context c, ArrayList<GeneralSearchResult> m,
            LayoutInflater l) {

        context = c;
        itemArray = m;
        layoutInflater = l;

    }

    @Override
    public int getCount() {

        return (itemArray != null) ? itemArray.size() : 0;

    }

    @Override
    public GeneralSearchResult getItem(int position) {

        return this.itemArray.get(position);

    }

    @Override
    public long getItemId(int position) {

        GeneralSearchResult temp = this.itemArray.get(position);
        return (temp.hasProfileData() ? temp.getProfileData().getProfileId()
                : temp.getPlatoonData().getId());

    }

    @Override
    public int getItemViewType(int position) {

        if (getItem(position).hasProfileData()) {

            return 0;

        } else {

            return 1;

        }

    }

    @Override
    public int getViewTypeCount() {

        return 2;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        GeneralSearchResult currentItem = getItem(position);

        // Recycle
        if (getItemViewType(position) == 0) {

            if (convertView == null) {

                convertView = layoutInflater.inflate(
                        R.layout.list_item_search_profile, parent, false);

            }

            // Get the ProfileData
            ProfileData profileData = currentItem.getProfileData();

            // Set the fields
            ((TextView) convertView.findViewById(R.id.string_name))
                    .setText(profileData.getAccountName());
            /*
             * ((ImageView) convertView.findViewById( R.id.image_avatar
             * )).setImageBitmap( BitmapFactory.decodeFile(
             * PublicUtils.getCachePath( context ) +
             * profileData.getGravatarHash() + ".png" ) );
             */

        } else {

            if (convertView == null) {

                convertView = layoutInflater.inflate(
                        R.layout.list_item_search_platoon, parent, false);

            }

            // Get the PlatoonData
            PlatoonData platoonData = currentItem.getPlatoonData();

            // Set the fields
            ((TextView) convertView.findViewById(R.id.string_name)).setText("["
                    + platoonData.getTag() + "] " + platoonData.getName());
            /*
             * ((ImageView) convertView.findViewById( R.id.image_avatar
             * )).setImageBitmap( BitmapFactory.decodeFile(
             * PublicUtils.getCachePath( context ) + platoonData.getImage() +
             * ".jpeg" ) );
             */

        }

        // Store the object
        convertView.setTag(currentItem);

        // R-TURN
        return convertView;

    }

    public void setItemArray(ArrayList<GeneralSearchResult> array) {

        this.itemArray = array;
        this.notifyDataSetInvalidated();

    }

    public void addItem(ArrayList<GeneralSearchResult> array) {

        this.itemArray.addAll(array);

    }

}
