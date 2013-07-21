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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.UnlockData;

import java.util.List;

public class UnlockListAdapter extends BaseAdapter {
    private Context context;
    private List<UnlockData> unlockData;
    private LayoutInflater layoutInflater;
    private ProgressBar progressBar;

    public UnlockListAdapter(Context c, List<UnlockData> u, LayoutInflater l) {
        context = c;
        unlockData = u;
        layoutInflater = l;
    }

    @Override
    public int getCount() {
        return (unlockData != null) ? unlockData.size() : 0;
    }

    @Override
    public UnlockData getItem(int position) {
        return unlockData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UnlockData currentUnlock = getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_unlock, parent, false);
        }

        progressBar = ((ProgressBar) convertView.findViewById(R.id.progress_unlock));

        convertView.findViewById(R.id.divider_left).setBackgroundColor(
                context.getResources().getColor(getColorForKit(currentUnlock.getKitId())));
        ((TextView) convertView.findViewById(R.id.text_unlock_percent)).setText(
                currentUnlock.getUnlockPercentage() + "%");
        ((ImageView) convertView.findViewById(R.id.image_unlock)).setImageResource(
                currentUnlock.getImageResource());

        ((TextView) convertView.findViewById(R.id.text_unlock_title)).setText(currentUnlock.getTitle(context));
        ((TextView) convertView.findViewById(R.id.text_unlock_desc)).setText(currentUnlock.getObjective(context));

        progressBar.setMax(100);
        progressBar.setProgress((int) Math.round(currentUnlock.getUnlockPercentage()));

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

    public void setData(List<UnlockData> data) {
        unlockData = data;
        notifyDataSetChanged();
    }
}
