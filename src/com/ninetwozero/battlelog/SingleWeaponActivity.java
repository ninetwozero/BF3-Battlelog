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

package com.ninetwozero.battlelog;

import java.util.Vector;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.ninetwozero.battlelog.datatypes.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WeaponDataWrapper;
import com.ninetwozero.battlelog.datatypes.WeaponInfo;
import com.ninetwozero.battlelog.datatypes.WeaponStats;
import com.ninetwozero.battlelog.fragments.UnlockFragment;
import com.ninetwozero.battlelog.fragments.WeaponInformationFragment;
import com.ninetwozero.battlelog.fragments.WeaponStatisticsFragment;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class SingleWeaponActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    // Attributes
    private ProfileData profileData;
    private long selectedPersona;
    //private int selectedPosition;
    private WeaponDataWrapper weaponDataWrapper;
    private WeaponInfo weaponInfo;
    private WeaponStats weaponStats;

    // Fragment related
    private WeaponInformationFragment fragmentWeaponInfo;
    private WeaponStatisticsFragment fragmentWeaponStats;
    private UnlockFragment fragmentUnlocks;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Get the intent
        if (!getIntent().hasExtra("profile") || !getIntent().hasExtra("weapon")) {

            finish();

        }

        // Get the profile
        profileData = getIntent().getParcelableExtra("profile");

        // Get the weapon information
        weaponDataWrapper = getIntent().getParcelableExtra("weapon");
        weaponInfo = weaponDataWrapper.getData();
        weaponStats = weaponDataWrapper.getStats();

        // Set the content view
        setContentView(R.layout.viewpager_default);

        // Last but not least - init
        initActivity();

        // Let's setup the fragments too
        setupFragments();

    }

    public void initActivity() {

        // Set the selected persona
        if (SessionKeeper.getProfileData().getId() != profileData.getId()) {

            selectedPersona = profileData.getPersona(0).getId();

        } else {

            selectedPersona = sharedPreferences.getLong(Constants.SP_BL_PERSONA_CURRENT_ID,
                    profileData.getPersona(0).getId());

        }

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    public void setupFragments() {

        // Do we need to setup the fragments?
        if (listFragments == null) {

            // Add them to the list
            listFragments = new Vector<Fragment>();
            listFragments.add(fragmentWeaponInfo = (WeaponInformationFragment) Fragment
                    .instantiate(this, WeaponInformationFragment.class.getName()));
            listFragments.add(fragmentWeaponStats = (WeaponStatisticsFragment) Fragment
                    .instantiate(this, WeaponStatisticsFragment.class.getName()));
            listFragments.add(fragmentUnlocks = (UnlockFragment) Fragment.instantiate(this,
                    UnlockFragment.class.getName()));

            // Let's set the selectedPersona
            fragmentWeaponInfo.setProfileData(profileData);
            fragmentWeaponInfo.setWeaponInfo(weaponInfo);
            fragmentWeaponInfo.setWeaponStats(weaponStats);
            fragmentWeaponInfo.setSelectedPersona(selectedPersona);

            // Get the ViewPager
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            pagerAdapter = new SwipeyTabsPagerAdapter(

                    fragmentManager,
                    new String[] {
                            "INFORMATION", "STATISTICS", "UNLOCKS"
                    },
                    listFragments,
                    viewPager,
                    layoutInflater
                    );
            viewPager.setAdapter(pagerAdapter);
            tabs.setAdapter(pagerAdapter);

            // Make sure the tabs follow
            viewPager.setOnPageChangeListener(tabs);
            viewPager.setOffscreenPageLimit(2);
            viewPager.setCurrentItem(0);

        }

    }

    public void reload() {

        fragmentWeaponInfo.reload();

    }

    public void showData(WeaponDataWrapper w) {

        fragmentWeaponStats.show(w.getStats());
        fragmentUnlocks.showUnlocks(w.getUnlocks());

    }
}
