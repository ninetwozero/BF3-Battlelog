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

package com.ninetwozero.bf3droid.activity.profile.soldier;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.datatype.DefaultFragmentActivity;
import com.ninetwozero.bf3droid.datatype.PersonaStats;
import com.ninetwozero.bf3droid.datatype.ProfileData;

import java.util.ArrayList;
import java.util.Map;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

public class CompareActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    private ProfileStatsFragment[] statsFragments;
    private ProfileStatsCompareFragment compareFragment;

    // Misc
    private ProfileData[] profileData;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        profileData = new ProfileData[]{
                (ProfileData) getIntent().getParcelableExtra("profile1"),
                (ProfileData) getIntent().getParcelableExtra("profile2")
        };
        setContentView(R.layout.viewpager_default);
        setup();
        init();
    }

    public void init() {
    }

    public void reload() {
        statsFragments[0].reload();
        statsFragments[1].reload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_basic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_reload) {
            this.reload();
        } else if (item.getItemId() == R.id.option_back) {
            finish();
        } else {
            if (mViewPager.getCurrentItem() == 0) {
                return statsFragments[0].handleSelectedOption(item);
            } else if (mViewPager.getCurrentItem() == 2) {
                return statsFragments[1].handleSelectedOption(item);
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
        reload();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return true;
    }

    public void setup() {
        if (mListFragments == null) {
            mListFragments = new ArrayList<Fragment>();
            statsFragments = new ProfileStatsFragment[2];
            mListFragments.add(statsFragments[0] = (ProfileStatsFragment) Fragment.instantiate(
                    this, ProfileStatsFragment.class.getName()));
            mListFragments.add(compareFragment = (ProfileStatsCompareFragment) Fragment
                    .instantiate(
                            this,
                            ProfileStatsCompareFragment.class.getName()));
            mListFragments.add(statsFragments[1] = (ProfileStatsFragment) Fragment.instantiate(
                    this,
                    ProfileStatsFragment.class.getName()));

            /* TODO introduce bundle boolean to decide when to use user and visitor personas
            statsFragments[0].setProfileData(profileData[0]);
            statsFragments[1].setProfileData(profileData[1]);*/

            statsFragments[0].setComparing(true);
            statsFragments[1].setComparing(true);

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            mPagerAdapter = new SwipeyTabsPagerAdapter(

                    mFragmentManager,
                    new String[]{
                            profileData[0].getUsername(), "Compare",
                            profileData[1].getUsername()
                    },
                    mListFragments,
                    mViewPager,
                    mLayoutInflater
            );
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setCurrentItem(1);
            mViewPager.setOffscreenPageLimit(2);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() > 0) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void sendToCompare(ProfileData p, Map<Long, PersonaStats> ps, long id, boolean toggle) {
        compareFragment.showStats(ps, id, p.getId() == profileData[0].getId() ? 0 : 1, toggle);
    }
}
