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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.WeaponDataWrapper;
import com.ninetwozero.battlelog.datatypes.WeaponStats;
import com.ninetwozero.battlelog.misc.DrawableResourceList;

public class WeaponListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private List<WeaponDataWrapper> dataArray;
    private LayoutInflater layoutInflater;

    // Construct
    public WeaponListAdapter(Context c, List<WeaponDataWrapper> u,
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
    public WeaponDataWrapper getItem(int position) {

        return dataArray.get(position);

    }

    @Override
    public long getItemId(int position) {

        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        WeaponDataWrapper base = getItem(position);
        WeaponStats data = base.getWeaponStats();
        
        //Calculate teh unlock progress
        double unlockProgress = base.getNumUnlocks() == 0 ? 1 : base.getNumUnlocked()/((double) base.getNumUnlocks());

        // Recycle
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_item_weapon,
                    parent, false);

        }

        // Populate fields
        ((TextView) convertView.findViewById(R.id.text_title)).setText(data.getName());
        ((TextView) convertView.findViewById(R.id.text_sstars)).setText(data.getServiceStars() + "");
        
        // Setup the progress
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progress_unlocks);
        TextView textProgress = (TextView) convertView.findViewById(R.id.text_progress);
        if( unlockProgress >= 1.0 ) {

            progressBar.setVisibility(View.GONE);
            textProgress.setVisibility( View.GONE );
            
        } else {
        
            progressBar.setMax(1000);
            progressBar.setProgress(((int)(unlockProgress * 1000)));
            progressBar.setVisibility(View.VISIBLE);
            
            textProgress.setText( (Math.round(unlockProgress * 1000)/10.0) + "%");
            textProgress.setVisibility( View.VISIBLE);
            
        }
        
        //Last but not least - the almighty image
        ((ImageView) convertView.findViewById(R.id.image_item)).setImageResource( DrawableResourceList.getWeapon( data.getGuid() ) );

        // Tag it!
        convertView.setTag(data);
        return convertView;
    }

    public void setDataArray(List<WeaponDataWrapper> data) {

        // Let's do this
        dataArray = data;
        notifyDataSetChanged();

    }

}
