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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.activity.profile.unlocks.UnlockFragment;
import com.ninetwozero.battlelog.datatype.*;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

import java.util.ArrayList;

public class SingleWeaponActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    // Attributes
    private ProfileData mProfileData;
    private long mSelectedPersona;
    // private int mSelectedPosition;
    private WeaponDataWrapper mWeaponDataWrapper;
    private WeaponInfo mWeaponInfo;
    private WeaponStats mWeaponStats;

    // Fragment related
    private WeaponInformationFragment mFragmentWeaponInfo;
    private WeaponStatisticsFragment mFragmentWeaponStats;
    private UnlockFragment mFragmentUnlocks;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Get the intent
        if (!getIntent().hasExtra("profile") || !getIntent().hasExtra("weapon")) {

            finish();

        }

        // Get the profile
        mProfileData = getIntent().getParcelableExtra("profile");

        // Get the weapon information
        mWeaponDataWrapper = getIntent().getParcelableExtra("weapon");
        mWeaponInfo = mWeaponDataWrapper.getData();
        mWeaponStats = mWeaponDataWrapper.getStats();

        // Set the content view
        setContentView(R.layout.viewpager_default);

        // Last but not least - init
        init();

        // Let's setup the fragments too
        setup();

    }

    public void init() {

        // Set the selected persona
        if (SessionKeeper.getProfileData().getId() == mProfileData.getId()) {

            mSelectedPersona = mSharedPreferences.getLong(Constants.SP_BL_PERSONA_CURRENT_ID,
                    mProfileData.getPersona(0).getId());

        } else {

            if (mProfileData.getNumPersonas() > 0) {

                mSelectedPersona = mProfileData.getPersona(0).getId();

            }

        }

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    public void setup() {

        // Do we need to setup the fragments?
        if (mListFragments == null) {

            // Add them to the list
            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(mFragmentWeaponInfo = (WeaponInformationFragment) Fragment
                    .instantiate(this, WeaponInformationFragment.class.getName()));
            mListFragments.add(mFragmentWeaponStats = (WeaponStatisticsFragment) Fragment
                    .instantiate(this, WeaponStatisticsFragment.class.getName()));
            mListFragments.add(mFragmentUnlocks = (UnlockFragment) Fragment.instantiate(this,
                    UnlockFragment.class.getName()));

            // Let's set the selectedPersona
            mFragmentWeaponInfo.setProfileData(mProfileData);
            mFragmentWeaponInfo.setWeaponInfo(mWeaponInfo);
            mFragmentWeaponInfo.setWeaponStats(mWeaponStats);
            mFragmentWeaponInfo.setSelectedPersona(mSelectedPersona);

            // Get the ViewPager
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            mPagerAdapter = new SwipeyTabsPagerAdapter(

                    mFragmentManager,
                    new String[]{
                            "INFORMATION", "STATISTICS", "UNLOCKS"
                    },
                    mListFragments,
                    mViewPager,
                    mLayoutInflater
            );
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            // Make sure the tabs follow
            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setOffscreenPageLimit(2);
            mViewPager.setCurrentItem(0);

        }

    }

    public void reload() {

        mFragmentWeaponInfo.reload();

    }

    public void showData(WeaponDataWrapper w) {

        mFragmentWeaponStats.show(w.getStats());
        mFragmentUnlocks.showUnlocks(w.getUnlocks());

    }
}
