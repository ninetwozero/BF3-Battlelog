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

package com.ninetwozero.battlelog.activity.platoon;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.activity.feed.FeedFragment;
import com.ninetwozero.battlelog.datatype.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.PlatoonInformation;
import com.ninetwozero.battlelog.http.FeedClient;
import com.ninetwozero.battlelog.http.RequestHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

import java.util.ArrayList;

public class PlatoonActivity extends CustomFragmentActivity implements
        DefaultFragmentActivity {

    // Fragment related
    private PlatoonOverviewFragment mFragmentOverview;
    private PlatoonStatsFragment mFragmentStats;
    private PlatoonMemberFragment mFragmentMember;
    private FeedFragment mFragmentFeed;

    // Misc
    private PlatoonData mPlatoonData;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Get the intent
        if (!getIntent().hasExtra("platoon")) {
            finish();
        }

        // Get the platoon data
        mPlatoonData = (PlatoonData) getIntent().getParcelableExtra("platoon");

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

        // ASYNC!!
        mFragmentOverview.reload();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate!!
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_platoonview, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Our own profile, no need to show the "extra" buttons
        if (mViewPager.getCurrentItem() == 0) {

            return super.onPrepareOptionsMenu(mFragmentOverview
                    .prepareOptionsMenu(menu));

        } else if (mViewPager.getCurrentItem() == 1) {

            return super.onPrepareOptionsMenu(mFragmentStats
                    .prepareOptionsMenu(menu));

        } else if (mViewPager.getCurrentItem() == 2) {

            return super.onPrepareOptionsMenu(mFragmentMember
                    .prepareOptionsMenu(menu));

        } else {

            menu.removeItem(R.id.option_friendadd);
            menu.removeItem(R.id.option_frienddel);
            menu.removeItem(R.id.option_compare);
            menu.removeItem(R.id.option_unlocks);

        }

        return super.onPrepareOptionsMenu(menu);

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

                return mFragmentOverview.handleSelectedOption(item);

            } else if (mViewPager.getCurrentItem() == 1) {

                return mFragmentStats.handleSelectedOption(item);

            } else if (mViewPager.getCurrentItem() == 2) {

                return mFragmentMember.handleSelectedOption(item);

            }

        }

        // Return true yo
        return true;

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, mSharedPreferences);

        // We need to initialize
        init();

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenuInfo menuInfo) {

        switch (mViewPager.getCurrentItem()) {

            case 0:
                break;

            case 1:
                break;

            case 2:
                mFragmentMember.createContextMenu(menu, view, menuInfo);
                break;

            case 3:
                mFragmentFeed.createContextMenu(menu, view, menuInfo);
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // Declare...
        AdapterView.AdapterContextMenuInfo info;

        // Let's try to get some menu information via a try/catch
        try {

            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        } catch (ClassCastException e) {

            e.printStackTrace();
            return false;

        }

        switch (mViewPager.getCurrentItem()) {

            case 2:
                return mFragmentMember.handleSelectedContextItem(info, item);

            case 3:
                return mFragmentFeed.handleSelectedContextItem(info, item);

            default:
                break;

        }

        return true;
    }

    public void setup() {

        // Do we need to setup the fragments?
        if (mListFragments == null) {

            // Add them to the list
            mListFragments = new ArrayList<Fragment>();
            mListFragments
                    .add(mFragmentOverview = (PlatoonOverviewFragment) Fragment
                            .instantiate(this,
                                    PlatoonOverviewFragment.class.getName()));
            mListFragments.add(mFragmentStats = (PlatoonStatsFragment) Fragment
                    .instantiate(this, PlatoonStatsFragment.class.getName()));
            mListFragments
                    .add(mFragmentMember = (PlatoonMemberFragment) Fragment
                            .instantiate(this,
                                    PlatoonMemberFragment.class.getName()));
            mListFragments.add(mFragmentFeed = (FeedFragment) Fragment
                    .instantiate(this, FeedFragment.class.getName()));

            // Add the profileData
            mFragmentOverview.setPlatoonData(mPlatoonData);
            mFragmentMember.setPlatoonData(mPlatoonData);

            // We need to set the type
            mFragmentFeed.setTitle(mPlatoonData.getName());
            mFragmentFeed.setType(FeedClient.TYPE_PLATOON);
            mFragmentFeed.setId(mPlatoonData.getId());
            mFragmentFeed.setCanWrite(false);

            // Get the ViewPager
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            mPagerAdapter = new SwipeyTabsPagerAdapter(

                    mFragmentManager, new String[]{"OVERVIEW", "STATS", "USERS",
                    "FEED"}, mListFragments, mViewPager, mLayoutInflater);
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            // Make sure the tabs follow
            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setCurrentItem(0);
            mViewPager.setOffscreenPageLimit(3);

        }

    }

    public void openStats(PlatoonInformation p) {

        mFragmentStats.setPlatoonInformation(p);
        mFragmentStats.reload();

    }

    public void openMembers(PlatoonInformation p) {

        mFragmentMember.showMembers(p);

    }

    public void setFeedPermission(boolean c) {

        mFragmentFeed.setCanWrite(c);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() > 0) {

            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

}
