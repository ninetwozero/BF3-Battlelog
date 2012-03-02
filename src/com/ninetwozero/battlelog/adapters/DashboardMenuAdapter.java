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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.DashboardItem;

public class DashboardMenuAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private DashboardItem[] items;
    private LayoutInflater layoutInflater;

    // Construct
    public DashboardMenuAdapter(Context c, DashboardItem[] d, LayoutInflater l) {

        context = c;
        items = d;
        layoutInflater = l;

    }

    @Override
    public int getCount() {

        return (items != null) ? items.length : 0;

    }

    @Override
    public DashboardItem getItem(int position) {

        return this.items[position];

    }

    @Override
    public long getItemId(int position) {

        return this.items[position].getId();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        DashboardItem currentItem = getItem(position);

        // Recycle
        if (convertView == null) {

            convertView = layoutInflater.inflate(
                    R.layout.list_item_dashboard_menu, parent, false);

        }

        // Get the TextView
        ((TextView) convertView.findViewById(R.id.text_title))
                .setText(currentItem.getTitle());

        // Store the object
        convertView.setTag(currentItem);

        return convertView;
    }

}
