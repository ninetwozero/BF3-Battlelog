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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.activity.profile.unlocks.UnlockFragment;
import com.ninetwozero.bf3droid.datatype.DefaultFragmentActivity;
import com.ninetwozero.bf3droid.datatype.WeaponDataWrapper;
import com.ninetwozero.bf3droid.datatype.WeaponInfo;
import com.ninetwozero.bf3droid.datatype.WeaponStats;

import java.util.ArrayList;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

public class SingleWeaponActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    private WeaponDataWrapper weaponDataWrapper;
    private WeaponInfo weaponInfo;
    private WeaponStats weaponStats;

    // Fragment related
    private WeaponInformationFragment weaponInformationFragment;
    private WeaponStatisticsFragment weaponStatisticsFragment;
    private UnlockFragment unlockFragment;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        if (!getIntent().hasExtra("weapon")) {
            finish();
        }
        weaponDataWrapper = getIntent().getParcelableExtra("weapon");
        weaponInfo = weaponDataWrapper.getData();
        weaponStats = weaponDataWrapper.getStats();

        setContentView(R.layout.viewpager_default);

        init();
        setup();
    }

    @Override
    public void init() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setup() {
        if (mListFragments == null) {

            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(weaponInformationFragment = (WeaponInformationFragment) Fragment
                    .instantiate(this, WeaponInformationFragment.class.getName()));
            mListFragments.add(weaponStatisticsFragment = (WeaponStatisticsFragment) Fragment
                    .instantiate(this, WeaponStatisticsFragment.class.getName()));
            mListFragments.add(unlockFragment = (UnlockFragment) Fragment.instantiate(this,
                    UnlockFragment.class.getName()));

            weaponInformationFragment.setWeaponInfo(weaponInfo);
            weaponInformationFragment.setWeaponStats(weaponStats);

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            mPagerAdapter = new SwipeyTabsPagerAdapter(
                    mFragmentManager,
                    new String[]{"INFORMATION", "STATISTICS", "UNLOCKS"},
                    mListFragments,
                    mViewPager,
                    mLayoutInflater
            );
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setOffscreenPageLimit(2);
            mViewPager.setCurrentItem(0);
        }
    }

    public void reload() {
        weaponInformationFragment.reload();
    }

    public void showData(WeaponDataWrapper w) {
        weaponStatisticsFragment.show(w.getStats());
        unlockFragment.showUnlocks(w.getUnlocks());
    }
}
