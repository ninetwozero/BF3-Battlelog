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

package com.ninetwozero.bf3droid.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;

import com.ninetwozero.bf3droid.dialog.ProgressDialogFragment;
import com.ninetwozero.bf3droid.http.RequestHandler;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;

import java.util.List;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

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

    private Handler handler = new Handler();

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PublicUtils.setupFullscreen(this, mSharedPreferences);
        PublicUtils.restoreCookies(this, icicle);
        PublicUtils.setupLocale(this, mSharedPreferences);
        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        PublicUtils.setupLocale(this, mSharedPreferences);
        PublicUtils.setupSession(this, mSharedPreferences);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /* @see http://stackoverflow.com/a/9021487 */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SUPER_COOKIES, RequestHandler.getCookies());
    }

    /*TEMPORARY SOLUTION SO PROGRESS DIALOG CAN BE EXTRACTED*/
    public void startLoadingDialog(String tag) {
        DialogFragment dialog = new ProgressDialogFragment();
        dialog.show(getSupportFragmentManager(), tag);
    }

    public void closeLoadingDialog(final String tag) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                FragmentManager manager = getSupportFragmentManager();
                DialogFragment fragment = (DialogFragment) manager.findFragmentByTag(tag);
                if (fragment != null) {
                    Log.i("Bf3FragmentActivity", "Closing dialog " + tag);
                    fragment.dismiss();
                } else {
                    Log.i("Bf3FragmentActivity", "Couldn't close dialog, didn't found " + tag);
                }
            }
        });
    }

    protected String[] tabTitles(int id) {
        Resources r = getResources();
        return r.getStringArray(id);
    }
}
