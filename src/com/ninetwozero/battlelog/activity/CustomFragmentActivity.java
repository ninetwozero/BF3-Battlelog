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

package com.ninetwozero.battlelog.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import com.ninetwozero.battlelog.http.RequestHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

import java.util.List;

public class CustomFragmentActivity extends FragmentActivity {

    // Attributes
    protected SharedPreferences mSharedPreferences;
    protected LayoutInflater mLayoutInflater;

    // Fragment related
    protected SwipeyTabs mTabs;
    protected SwipeyTabsPagerAdapter mPagerAdapter;
    protected FragmentManager mFragmentManager;
    protected ViewPager mViewPager;
    protected List<Fragment> mListFragments;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        // Should we display a title bar?
        PublicUtils.setupFullscreen(this, mSharedPreferences);
        PublicUtils.restoreCookies(this, icicle);

        // Setup the locale
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Get the layoutInflater
        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFragmentManager = getSupportFragmentManager();

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, mSharedPreferences);

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
}
