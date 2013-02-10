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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.WeaponDataWrapper;
import com.ninetwozero.bf3droid.datatype.WeaponStats;
import com.ninetwozero.bf3droid.misc.DrawableResourceList;

import java.util.List;

public class WeaponListAdapter extends BaseAdapter {
    private List<WeaponDataWrapper> dataArray;
    private LayoutInflater layoutInflater;

    public WeaponListAdapter(List<WeaponDataWrapper> u, LayoutInflater l) {
        dataArray = u;
        layoutInflater = l;
    }

    @Override
    public int getCount() {
        return (dataArray != null) ? dataArray.size() : 0;
    }

    @Override
    public WeaponDataWrapper getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        WeaponDataWrapper base = getItem(index);
        WeaponStats data = base.getStats();

        double unlockProgress = base.getNumUnlocks() == 0 ? 1 : base.getNumUnlocked() / ((double) base.getNumUnlocks());

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_weapon, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.text_id)).setText(index+1 + "");
        ((TextView) convertView.findViewById(R.id.text_title)).setText(data.getSlug().toUpperCase());
        ((TextView) convertView.findViewById(R.id.text_sstars)).setText((int) data.getServiceStars() + "");
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progress_unlocks);
        TextView textProgress = (TextView) convertView.findViewById(R.id.text_progress);

        if (unlockProgress >= 1.0) {
            ((TextView) convertView.findViewById(R.id.text_status)).setText(R.string.info_unlocks_done);
            progressBar.setVisibility(View.GONE);
            textProgress.setVisibility(View.GONE);
        } else {
            progressBar.setMax(1000);
            progressBar.setProgress(((int) (unlockProgress * 1000)));
            progressBar.setVisibility(View.VISIBLE);

            textProgress.setText((Math.round(unlockProgress * 1000) / 10.0) + "%");
            textProgress.setVisibility(View.VISIBLE);

            ((TextView) convertView.findViewById(R.id.text_status)).setText(base.getNumUnlocks()+ "/" + base.getNumUnlocks());
        }

        ((ImageView) convertView.findViewById(R.id.image_item)).setImageResource(DrawableResourceList.getWeapon(data.getGuid()));
        convertView.setTag(base);
        return convertView;
    }

    public void setData(List<WeaponDataWrapper> data) {
        dataArray = data;
        notifyDataSetChanged();
    }
}
