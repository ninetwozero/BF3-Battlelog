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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.AppContributorData;

public class CreditListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private List<AppContributorData> contributors;
    private LayoutInflater layoutInflater;

    // Construct
    public CreditListAdapter(Context c, List<AppContributorData> a,
            LayoutInflater l) {

        context = c;
        contributors = a;
        layoutInflater = l;

    }

    @Override
    public int getCount() {

        return (contributors != null) ? contributors.size() : 0;

    }

    @Override
    public int getItemViewType(int position) {

        if (getItem(position).getStringId() != 0) {

            return 1;

        } else {

            return 0;

        }

    }

    @Override
    public int getViewTypeCount() {

        return 2;

    }

    @Override
    public AppContributorData getItem(int position) {

        return contributors.get(position);

    }

    @Override
    public long getItemId(int position) {

        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        AppContributorData data = getItem(position);

        // Let's see what we found
        if (getItemViewType(position) == 1) {

            // Can we recycle?
            if (convertView == null) {

                convertView = layoutInflater.inflate(
                        R.layout.list_item_credits_heading, parent, false);

            }

            // Set the fields
            ((TextView) convertView.findViewById(R.id.text_title))
                    .setText(data.getName());
            convertView.setOnClickListener(null);

        } else {

            // Recycle
            if (convertView == null) {

                convertView = layoutInflater.inflate(R.layout.list_item_credits_person,
                        parent, false);

            }

            // Set the field
            ((TextView) convertView.findViewById(R.id.text_name)).setText(data.getName());

        }

        // Set the tag
        convertView.setTag(data.getUrl());

        return convertView;

    }

}
