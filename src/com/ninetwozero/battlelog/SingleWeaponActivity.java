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

import java.util.List;
import java.util.Vector;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;

import com.ninetwozero.battlelog.datatypes.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WeaponDataWrapper;
import com.ninetwozero.battlelog.datatypes.WeaponInfo;
import com.ninetwozero.battlelog.datatypes.WeaponStats;
import com.ninetwozero.battlelog.fragments.UnlockFragment;
import com.ninetwozero.battlelog.fragments.WeaponInformationFragment;
import com.ninetwozero.battlelog.fragments.WeaponStatisticsFragment;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class SingleWeaponActivity extends FragmentActivity implements DefaultFragmentActivity {

    // Attributes
    private final Context CONTEXT = this;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    private ProfileData profileData;
    private long selectedPersona;
    private int selectedPosition;
    private WeaponDataWrapper weaponDataWrapper;
    private WeaponInfo weaponInfo;
    private WeaponStats weaponStats;

    // Fragment related
    private SwipeyTabs tabs;
    private SwipeyTabsPagerAdapter pagerAdapter;
    private List<Fragment> listFragments;
    private FragmentManager fragmentManager;
    private ViewPager viewPager;
    private WeaponInformationFragment fragmentWeaponInfo;
    private WeaponStatisticsFragment fragmentWeaponStats;
    private UnlockFragment fragmentUnlocks;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);
        
        // Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Restore the cookies
        PublicUtils.setupFullscreen(this, sharedPreferences);
        PublicUtils.restoreCookies(this, icicle);

        // Prepare to tango
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fragmentManager = getSupportFragmentManager();

        // Get the intent
        if (getIntent().hasExtra("profile")) {

            profileData = getIntent().getParcelableExtra("profile");

        } else {

            return;

        }
        
        if( getIntent().hasExtra("weapon") ) {
            
            weaponDataWrapper = getIntent().getParcelableExtra("weapon");
            weaponInfo = weaponDataWrapper.getData();
            weaponStats = weaponDataWrapper.getStats();
            
        } else {
         
            return;
            
        }

        // Setup the trinity
        PublicUtils.setupLocale(this, sharedPreferences);
        PublicUtils.setupSession(this, sharedPreferences);

        // Set the content view
        setContentView(R.layout.viewpager_default);

        // Last but not least - init
        initActivity();
        
        // Let's setup the fragments too
        setupFragments();

    }

    public void initActivity() {
        
        // Set the selected persona
        if( SessionKeeper.getProfileData().getId() != profileData.getId() ) {
            
            selectedPersona = profileData.getPersona(0).getId();
        
        } else {
            
            selectedPersona = sharedPreferences.getLong(Constants.SP_BL_PERSONA_CURRENT_ID, profileData.getPersona(0).getId());
            
        }

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SUPER_COOKIES,
                RequestHandler.getCookies());

    }

    public void setupFragments() {

        // Do we need to setup the fragments?
        if (listFragments == null) {

            // Add them to the list
            listFragments = new Vector<Fragment>();
            listFragments.add(fragmentWeaponInfo = (WeaponInformationFragment) Fragment.instantiate(this, WeaponInformationFragment.class.getName()));
            listFragments.add(fragmentWeaponStats = (WeaponStatisticsFragment) Fragment.instantiate(this, WeaponStatisticsFragment.class.getName()));
            listFragments.add(fragmentUnlocks = (UnlockFragment) Fragment.instantiate(this, UnlockFragment.class.getName()));

            //Let's set the selectedPersona
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
