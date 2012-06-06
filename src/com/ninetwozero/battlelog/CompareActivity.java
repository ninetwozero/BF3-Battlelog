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

import java.util.Map;
import java.util.Vector;

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

import com.ninetwozero.battlelog.datatypes.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatypes.PersonaStats;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.fragments.ProfileStatsCompareFragment;
import com.ninetwozero.battlelog.fragments.ProfileStatsFragment;

public class CompareActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    // Fragment related
    private ProfileStatsFragment[] fragmentStats;
    private ProfileStatsCompareFragment fragmentCompare;

    // Misc
    private ProfileData[] profileData;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Let's set them straight
        profileData = new ProfileData[] {

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

        fragmentStats[0].reload();
        fragmentStats[1].reload();

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

            if (viewPager.getCurrentItem() == 0) {

                return fragmentStats[0].handleSelectedOption(item);

            } else if (viewPager.getCurrentItem() == 2) {

                return fragmentStats[1].handleSelectedOption(item);

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

        return;

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return true;

    }

    public void setup() {

        // Do we need to setup the fragments?
        if (listFragments == null) {

            // Add them to the list
            listFragments = new Vector<Fragment>();
            fragmentStats = new ProfileStatsFragment[2];
            listFragments.add(fragmentStats[0] = (ProfileStatsFragment) Fragment.instantiate(
                    this, ProfileStatsFragment.class.getName()));
            listFragments.add(fragmentCompare = (ProfileStatsCompareFragment) Fragment.instantiate(
                    this,
                    ProfileStatsCompareFragment.class.getName()));
            listFragments.add(fragmentStats[1] = (ProfileStatsFragment) Fragment.instantiate(this,
                    ProfileStatsFragment.class.getName()));

            // Add the profileData
            fragmentStats[0].setProfileData(profileData[0]);
            fragmentStats[1].setProfileData(profileData[1]);

            // Make them know they're about to compare
            fragmentStats[0].setComparing(true);
            fragmentStats[1].setComparing(true);

            // Get the ViewPager
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            pagerAdapter = new SwipeyTabsPagerAdapter(

                    fragmentManager,
                    new String[] {
                            profileData[0].getUsername(), "Compare",
                            profileData[1].getUsername()
                    },
                    listFragments,
                    viewPager,
                    layoutInflater
                    );
            viewPager.setAdapter(pagerAdapter);
            tabs.setAdapter(pagerAdapter);

            // Make sure the tabs follow
            viewPager.setOnPageChangeListener(tabs);
            viewPager.setCurrentItem(1);
            viewPager.setOffscreenPageLimit(2);

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (viewPager.getCurrentItem() > 0) {

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);

            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public void sendToCompare(ProfileData p, Map<Long, PersonaStats> ps, long id, boolean toggle) {

        fragmentCompare.showStats(

                ps,
                id,
                p.getId() == profileData[0].getId() ? 0 : 1,
                toggle

                );

    }

}
