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

package com.ninetwozero.battlelog.activity.profile.soldier;

import java.util.ArrayList;
import java.util.Map;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.datatype.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatype.PersonaStats;
import com.ninetwozero.battlelog.datatype.ProfileData;

public class CompareActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    // Fragment related
    private ProfileStatsFragment[] mFragmentStats;
    private ProfileStatsCompareFragment mFragmentCompare;

    // Misc
    private ProfileData[] mProfileData;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Let's set them straight
        mProfileData = new ProfileData[]{

                (ProfileData) getIntent().getParcelableExtra("profile1"),
                (ProfileData) getIntent().getParcelableExtra("profile2")

        };

        // Set the content view
        setContentView(R.layout.viewpager_default);

        // Let's setup the fragments too
        setup();

        // Init
        init();

    }

    public void init() {

    }

    public void reload() {

        mFragmentStats[0].reload();
        mFragmentStats[1].reload();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate!!
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_basic, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Let's act!
        if (item.getItemId() == R.id.option_reload) {

            this.reload();

        } else if (item.getItemId() == R.id.option_back) {

            ((Activity) this).finish();

        } else {

            if (mViewPager.getCurrentItem() == 0) {

                return mFragmentStats[0].handleSelectedOption(item);

            } else if (mViewPager.getCurrentItem() == 2) {

                return mFragmentStats[1].handleSelectedOption(item);

            }

        }

        // Return true yo
        return true;

    }

    @Override
    public void onResume() {

        super.onResume();

        // We need to initialize
        init();

        // Let's reload
        reload();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenuInfo menuInfo) {

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return true;

    }

    public void setup() {

        // Do we need to setup the fragments?
        if (mListFragments == null) {

            // Add them to the list
            mListFragments = new ArrayList<Fragment>();
            mFragmentStats = new ProfileStatsFragment[2];
            mListFragments.add(mFragmentStats[0] = (ProfileStatsFragment) Fragment.instantiate(
                    this, ProfileStatsFragment.class.getName()));
            mListFragments.add(mFragmentCompare = (ProfileStatsCompareFragment) Fragment
                    .instantiate(
                            this,
                            ProfileStatsCompareFragment.class.getName()));
            mListFragments.add(mFragmentStats[1] = (ProfileStatsFragment) Fragment.instantiate(
                    this,
                    ProfileStatsFragment.class.getName()));

            // Add the profileData
            mFragmentStats[0].setProfileData(mProfileData[0]);
            mFragmentStats[1].setProfileData(mProfileData[1]);

            // Make them know they're about to compare
            mFragmentStats[0].setComparing(true);
            mFragmentStats[1].setComparing(true);

            // Get the ViewPager
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            mPagerAdapter = new SwipeyTabsPagerAdapter(

                    mFragmentManager,
                    new String[]{
                            mProfileData[0].getUsername(), "Compare",
                            mProfileData[1].getUsername()
                    },
                    mListFragments,
                    mViewPager,
                    mLayoutInflater
            );
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            // Make sure the tabs follow
            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setCurrentItem(1);
            mViewPager.setOffscreenPageLimit(2);

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() > 0) {

            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);

        }

        return super.onKeyDown(keyCode, event);
    }

    public void sendToCompare(ProfileData p, Map<Long, PersonaStats> ps, long id, boolean toggle) {

        mFragmentCompare.showStats(

                ps,
                id,
                p.getId() == mProfileData[0].getId() ? 0 : 1,
                toggle

        );

    }

}
