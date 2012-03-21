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
import com.ninetwozero.battlelog.datatypes.WeaponVehicleListData;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class WeaponVehicleListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private List<WeaponVehicleListData> dataArray;
    private LayoutInflater layoutInflater;

    // Construct
    public WeaponVehicleListAdapter(Context c, List<WeaponVehicleListData> u,
            LayoutInflater l) {

        context = c;
        dataArray = u;
        layoutInflater = l;

    }

    @Override
    public int getCount() {

        return (dataArray != null) ? dataArray.size() : 0;

    }

    @Override
    public WeaponVehicleListData getItem(int position) {

        return dataArray.get(position);

    }

    @Override
    public long getItemId(int position) {

        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        WeaponVehicleListData data = getItem(position);

        // Recycle
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_item_weaponvehicle,
                    parent, false);

        }

        // Populate fields
        ((TextView) convertView.findViewById(R.id.text_title)).setText(data.getTitle());
        ((ImageView) convertView.findViewById(R.id.image_item)).setImageBitmap(

                BitmapFactory.decodeFile(PublicUtils.getCachePath(context) + data.getTitle()
                        + ".png")

                );

        // Tag it!
        convertView.setTag(data);
        return convertView;
    }

    public void setDataArray(List<WeaponVehicleListData> data) {

        // Let's do this
        dataArray = data;
        notifyDataSetChanged();

    }

}
