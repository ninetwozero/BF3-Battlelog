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

package com.ninetwozero.battlelog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatypes.WeaponStats;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class WeaponStatisticsFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private int viewPagerPosition;

    // Elements
    private TextView textKills, textHS, textSF, textSH, textAccuracy, textTE, textSS, textSSP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_weapon_stats,
                container, false);

        // Init views
        initFragment(view);

        // Return the view
        return view;

    }

    public void initFragment(View v) {

        // Let's setup the fields
        textKills = (TextView) v.findViewById(R.id.text_kills);
        textHS = (TextView) v.findViewById(R.id.text_hs);
        textSF = (TextView) v.findViewById(R.id.text_sf);
        textSH = (TextView) v.findViewById(R.id.text_sh);
        textAccuracy = (TextView) v.findViewById(R.id.text_accuracy);
        textTE = (TextView) v.findViewById(R.id.text_time);
        textSS = (TextView) v.findViewById(R.id.text_sstars);
        textSSP = (TextView) v.findViewById(R.id.text_sstars_progress);

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    public int getViewPagerPosition() {

        return viewPagerPosition;

    }

    @Override
    public void reload() {

        ((DefaultFragmentActivity) context).reload();

    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return null;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }

    public void show(WeaponStats w) {

        textKills.setText(w.getKills() + "");
        textHS.setText(w.getHeadshots() + "");
        textSF.setText(w.getShotsFired() + "");
        textSH.setText(w.getShotsHit() + "");
        textAccuracy.setText((Math.round(w.getAccuracy() * 1000) / 10.0) + "%");
        textTE.setText(PublicUtils.timeToLiteral(w.getTimeEquipped()));
        textSS.setText(w.getServiceStars() + "");
        textSSP.setText(w.getServiceStarProgress() + "");

    }

}
