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

package com.ninetwozero.bf3droid.activity.aboutapp;

import java.util.ArrayList;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.datatype.DefaultFragmentActivity;

public class AboutActivity extends CustomFragmentActivity implements
		DefaultFragmentActivity {

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.viewpager_default);

		// Setup the fragments
		init();
		setup();
	}

	public final void init() {
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void setup() {
		// Do we need to setup the fragments?
		if (mListFragments == null) {

			// Add them to the list
			mListFragments = new ArrayList<Fragment>();
			mListFragments.add(Fragment.instantiate(this,
					AboutLicenseFragment.class.getName()));
			mListFragments.add(Fragment.instantiate(this,
					AboutMainFragment.class.getName()));
			mListFragments.add(Fragment.instantiate(this,
					AboutFAQFragment.class.getName()));
			mListFragments.add(Fragment.instantiate(this,
					AboutCreditsFragment.class.getName()));

			// Get the ViewPager
			mViewPager = (ViewPager) findViewById(R.id.viewpager);
			mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

			// Fill the PagerAdapter & set it to the viewpager
			mPagerAdapter = new SwipeyTabsPagerAdapter(

			mFragmentManager, new String[] { 
					getString(R.string.label_license), 
					getString(R.string.label_about),
					getString(R.string.label_faq),
					getString(R.string.label_credits)

			}, mListFragments, mViewPager, mLayoutInflater);
			mViewPager.setAdapter(mPagerAdapter);
			mTabs.setAdapter(mPagerAdapter);

			// Make sure the tabs follow
			mViewPager.setOnPageChangeListener(mTabs);
			mViewPager.setCurrentItem(1);

		}

	}

	@Override
	public void reload() {
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& (mViewPager.getCurrentItem() > 1 )) {
			mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
