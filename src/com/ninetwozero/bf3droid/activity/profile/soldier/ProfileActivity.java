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

package com.ninetwozero.bf3droid.activity.profile.soldier;

import java.util.ArrayList;

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
import android.widget.AdapterView;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3Fragment;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.activity.feed.FeedFragment;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.http.FeedClient;
import com.ninetwozero.bf3droid.misc.SessionKeeper;

public class ProfileActivity extends CustomFragmentActivity {

	// Fragment related
	private ProfileOverviewFragment fragmentOverview;
	private ProfileStatsFragment fragmentStats;
	private FeedFragment fragmentFeed;

	// Misc
	private ProfileData profileData;

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.viewpager_default);

		if (!getIntent().hasExtra("profile")) {
			finish();
		}
		profileData = getIntent().getParcelableExtra("profile");
		
		setup();
		init();
	}

	public void init() {
	}

	public void reload() {
	}

	public void setup() {
		if (mListFragments == null) {
			mListFragments = new ArrayList<Fragment>();
			mListFragments.add(fragmentOverview = (ProfileOverviewFragment) Fragment.instantiate(this, ProfileOverviewFragment.class.getName()));
			mListFragments.add(fragmentStats = (ProfileStatsFragment) Fragment.instantiate(this, ProfileStatsFragment.class.getName()));
			mListFragments.add(fragmentFeed = (FeedFragment) Fragment.instantiate(this, FeedFragment.class.getName()));

			fragmentOverview.setProfileData(profileData);
			fragmentStats.setProfileData(profileData);

			fragmentFeed.setTitle(profileData.getUsername());
			fragmentFeed.setType(FeedClient.TYPE_PROFILE);
			fragmentFeed.setId(profileData.getId());
			fragmentFeed.setCanWrite(false);

			mViewPager = (ViewPager) findViewById(R.id.viewpager);
			mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

			mPagerAdapter = new SwipeyTabsPagerAdapter(
                mFragmentManager, 
                new String[] { "OVERVIEW", "STATS", "FEED" },
				mListFragments, 
				mViewPager, 
				mLayoutInflater
			);
			mViewPager.setAdapter(mPagerAdapter);
			mTabs.setAdapter(mPagerAdapter);

			mViewPager.setOnPageChangeListener(mTabs);
			mViewPager.setCurrentItem(0);
			mViewPager.setOffscreenPageLimit(2);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_profileview, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (profileData.getId() == SessionKeeper.getProfileData().getId()) {
			menu.removeItem(R.id.option_friendadd);
			menu.removeItem(R.id.option_frienddel);
			menu.removeItem(R.id.option_compare);
			menu.removeItem(R.id.option_unlocks);
		} else {
			if (mViewPager.getCurrentItem() == 0) {
				return super.onPrepareOptionsMenu(fragmentOverview.prepareOptionsMenu(menu));
			} else if (mViewPager.getCurrentItem() == 1) {
				return super.onPrepareOptionsMenu(fragmentStats.prepareOptionsMenu(menu));
			} else if (mViewPager.getCurrentItem() == 2) {
				menu.findItem(R.id.option_friendadd).setVisible(false);
				menu.findItem(R.id.option_frienddel).setVisible(false);
				menu.findItem(R.id.option_compare).setVisible(false);
				menu.findItem(R.id.option_unlocks).setVisible(false);
			} else {
				menu.removeItem(R.id.option_friendadd);
				menu.removeItem(R.id.option_frienddel);
				menu.removeItem(R.id.option_compare);
				menu.removeItem(R.id.option_unlocks);
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.option_reload) {
            Fragment fragment = mPagerAdapter.getItem(mViewPager.getCurrentItem());
            if(fragment instanceof Bf3Fragment){
                ((Bf3Fragment) fragment).reload();
            }
		} else if (item.getItemId() == R.id.option_back) {
			((Activity) this).finish();
		} else {
			if (mViewPager.getCurrentItem() == 0) {
				return fragmentOverview.handleSelectedOption(item);
			} else if (mViewPager.getCurrentItem() == 1) {
				return fragmentStats.handleSelectedOption(item);
			}
		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		init();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		switch (mViewPager.getCurrentItem()) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			fragmentFeed.createContextMenu(menu, view, menuInfo);
			break;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			e.printStackTrace();
			return false;
		}

		switch (mViewPager.getCurrentItem()) {
		case 2:
			return fragmentFeed.handleSelectedContextItem(info, item);
		default:
			break;
		}
		return true;
	}

	public void openStats(ProfileData p) {
		fragmentStats.setProfileData(p);
		fragmentStats.reloadFromCache();
	}

	public void setFeedPermission(boolean c) {
		fragmentFeed.setCanWrite(c);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() > 0) {
			mViewPager.setCurrentItem(0, true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}