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

package com.ninetwozero.battlelog.activity.profile.weapon;

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
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatype.WeaponStats;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class WeaponStatisticsFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mViewPagerPosition;

    // Elements
    private TextView mTextKills, mTextHS, mTextSF, mTextSH, mTextAccuracy, mTextTE, mTextSS,
            mTextSSP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.tab_content_weapon_stats,
                container, false);

        // Init views
        initFragment(view);

        // Return the view
        return view;

    }

    public void initFragment(View v) {

        // Let's setup the fields
        mTextKills = (TextView) v.findViewById(R.id.text_kills);
        mTextHS = (TextView) v.findViewById(R.id.text_hs);
        mTextSF = (TextView) v.findViewById(R.id.text_sf);
        mTextSH = (TextView) v.findViewById(R.id.text_sh);
        mTextAccuracy = (TextView) v.findViewById(R.id.text_accuracy);
        mTextTE = (TextView) v.findViewById(R.id.text_time);
        mTextSS = (TextView) v.findViewById(R.id.text_sstars);
        mTextSSP = (TextView) v.findViewById(R.id.text_sstars_progress);

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    public int getViewPagerPosition() {

        return mViewPagerPosition;

    }

    @Override
    public void reload() {

        ((DefaultFragmentActivity) mContext).reload();

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

        mTextKills.setText(String.valueOf(w.getKills()));
        mTextHS.setText(String.valueOf(w.getHeadshots()));
        mTextSF.setText(String.valueOf(w.getShotsFired()));
        mTextSH.setText(String.valueOf(w.getShotsHit()));
        mTextAccuracy.setText((Math.round(w.getAccuracy() * 1000) / 10.0) + "%");
        mTextTE.setText(PublicUtils.timeToLiteral(w.getTimeEquipped()));
        mTextSS.setText(String.valueOf(w.getServiceStars()));
        mTextSSP.setText(String.valueOf(w.getServiceStarProgress()));

    }

}
