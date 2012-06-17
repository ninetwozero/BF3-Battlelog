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

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class DashboardPopupPlatoonListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private List<PlatoonData> platoonArray;
    private LayoutInflater layoutInflater;

    // Construct
    public DashboardPopupPlatoonListAdapter(Context c,
            List<PlatoonData> p, LayoutInflater l) {

        context = c;
        platoonArray = p;
        layoutInflater = l;

    }

    @Override
    public int getCount() {

        return (platoonArray != null) ? platoonArray.size() : 0;

    }

    @Override
    public PlatoonData getItem(int position) {

        return this.platoonArray.get(position);

    }

    @Override
    public long getItemId(int position) {

        return this.platoonArray.get(position).getId();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        PlatoonData currentPlatoon = getItem(position);

        // Recycle
        if (convertView == null) {

            convertView = layoutInflater.inflate(
                    R.layout.list_item_dashboard_platoon, parent, false);

        }

        // Set the TextViews
        ((ImageView) convertView.findViewById(R.id.image_badge))
                .setImageBitmap(

                BitmapFactory.decodeFile(currentPlatoon.getImage() + ".jpeg")

                );
        ((TextView) convertView.findViewById(R.id.text_title))
                .setText(currentPlatoon.getName());

        // Almost forgot - we got a Bitmap too!
        ((ImageView) convertView.findViewById(R.id.image_badge))
                .setImageBitmap(

                BitmapFactory.decodeFile(PublicUtils.getCachePath(context)
                        + currentPlatoon.getId() + ".jpeg")

                );

        // Store it in the tag
        convertView.setTag(currentPlatoon);

        return convertView;

    }

}
