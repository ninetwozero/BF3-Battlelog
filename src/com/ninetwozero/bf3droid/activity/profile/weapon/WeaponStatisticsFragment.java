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

package com.ninetwozero.bf3droid.activity.profile.weapon;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.DefaultFragmentActivity;
import com.ninetwozero.bf3droid.datatype.WeaponStats;
import com.ninetwozero.bf3droid.misc.PublicUtils;

public class WeaponStatisticsFragment extends Fragment implements
        DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mViewPagerPosition;

    // Elements
    private TextView mKills, mHeadShots, mShotFired, mShotHit, mAccuracy,
            mTimeEquipped, mServiceStars, mServiceStarProgress;

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
        mKills = (TextView) v.findViewById(R.id.text_kills);
        mHeadShots = (TextView) v.findViewById(R.id.text_hs);
        mShotFired = (TextView) v.findViewById(R.id.text_sf);
        mShotHit = (TextView) v.findViewById(R.id.text_sh);
        mAccuracy = (TextView) v.findViewById(R.id.text_accuracy);
        mTimeEquipped = (TextView) v.findViewById(R.id.text_time);
        mServiceStars = (TextView) v.findViewById(R.id.text_sstars);
        mServiceStarProgress = (TextView) v
                .findViewById(R.id.text_sstars_progress);

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

        mKills.setText(String.valueOf(w.getKills()));
        mHeadShots.setText(String.valueOf(w.getHeadshots()));
        mShotFired.setText(String.valueOf(w.getShotsFired()));
        mShotHit.setText(String.valueOf(w.getShotsHit()));
        mAccuracy.setText(String.format("%.1f", (w.getAccuracy() * 100)) + "%");
        mTimeEquipped.setText(PublicUtils.timeToLiteral(w.getTimeEquipped()));
        mServiceStars.setText(String.valueOf((int) w.getServiceStars()));
        mServiceStarProgress.setText(String.valueOf((int) w
                .getServiceStarProgress()));

    }

}
