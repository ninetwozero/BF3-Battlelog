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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.UnlockData;
import com.ninetwozero.battlelog.misc.Constants;

public class UnlockListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private List<UnlockData> dataArray;
    private LayoutInflater layoutInflater;
    private ProgressBar progressBar;

    // Construct
    public UnlockListAdapter(Context c, List<UnlockData> u,
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
    public UnlockData getItem(int position) {

        return dataArray.get(position);

    }

    @Override
    public long getItemId(int position) {

        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        UnlockData currentUnlock = getItem(position);

        // Recycle
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_item_unlock,
                    parent, false);

        }

        // Grab the progressBar
        progressBar = ((ProgressBar) convertView
                .findViewById(R.id.progress_unlock));

        // Set the TextViews
        ((View) convertView.findViewById(R.id.divider_left))
                .setBackgroundColor(context.getResources().getColor(
                        getColorForKit(currentUnlock.getKitId())));
        ((TextView) convertView.findViewById(R.id.text_unlock_percent))
                .setText(currentUnlock.getUnlockPercentage() + "%");
        ((ImageView) convertView.findViewById(R.id.image_unlock))
                .setImageResource(currentUnlock.getImageResource());

        // Title
        ((TextView) convertView.findViewById(R.id.text_unlock_title))
                .setText(currentUnlock.getTitle(context));

        // Description
        ((TextView) convertView.findViewById(R.id.text_unlock_desc))
                .setText(currentUnlock.getObjective(context));

        // Update the progress
        progressBar.setMax(100);
        progressBar.setProgress((int) Math.round(currentUnlock
                .getUnlockPercentage()));

        // Tag it!
        convertView.setTag(currentUnlock);

        return convertView;
    }

    public int getColorForKit(int kitId) {

        switch (kitId) {

            case 1:
                return R.color.kit_assault;

            case 2:
                return R.color.kit_engineer;

            case 8:
                return R.color.kit_recon;

            case 32:
                return R.color.kit_support;

            default:
                return R.color.kit_general;

        }

    }

    public void setDataArray(List<UnlockData> data) {

        // Let's do this
        Log.d(Constants.DEBUG_TAG, "New data y'all");
        dataArray = data;
        notifyDataSetChanged();

    }

}
