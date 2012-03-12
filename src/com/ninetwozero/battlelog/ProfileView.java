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
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.ninetwozero.battlelog.datatypes.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.fragments.FeedFragment;
import com.ninetwozero.battlelog.fragments.ProfileOverviewFragment;
import com.ninetwozero.battlelog.fragments.ProfileStatsFragment;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class ProfileView extends FragmentActivity implements DefaultFragmentActivity {

    // Attributes
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;

    // Fragment related
    private SwipeyTabs tabs;
    private SwipeyTabsPagerAdapter pagerAdapter;
    private List<Fragment> listFragments;
    private FragmentManager fragmentManager;
    private ProfileOverviewFragment fragmentOverview;
    private ProfileStatsFragment fragmentStats;
    private FeedFragment fragmentFeed;
    private ViewPager viewPager;

    // Misc
    private ProfileData profileData;

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

        // Setup the trinity
        PublicUtils.setupLocale(this, sharedPreferences);
        PublicUtils.setupSession(this, sharedPreferences);

        // Set the content view
        setContentView(R.layout.viewpager_default);

        // Let's setup the fragments too
        setupFragments();

        // Init
        initActivity();

    }

    public void initActivity() {

    }

    public void reload() {

        // ASYNC!!
        fragmentOverview.reload();

    }

    public void setupFragments() {

        // Do we need to setup the fragments?
        if (listFragments == null) {

            // Add them to the list
            listFragments = new Vector<Fragment>();
            listFragments.add(fragmentOverview = (ProfileOverviewFragment) Fragment.instantiate(
                    this, ProfileOverviewFragment.class.getName()));
            listFragments.add(fragmentStats = (ProfileStatsFragment) Fragment.instantiate(this,
                    ProfileStatsFragment.class.getName()));
            listFragments.add(fragmentFeed = (FeedFragment) Fragment.instantiate(this,
                    FeedFragment.class.getName()));

            // Add the profileData
            fragmentOverview.setProfileData(profileData);
            fragmentStats.setProfileData(profileData);
            
            // We need to set the type
            fragmentFeed.setTitle(profileData.getAccountName());
            fragmentFeed.setType(FeedFragment.TYPE_PROFILE);
            fragmentFeed.setId(profileData.getProfileId());
            fragmentFeed.setCanWrite(false);

            // Get the ViewPager
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            pagerAdapter = new SwipeyTabsPagerAdapter(

                    fragmentManager,
                    new String[] {
                            "OVERVIEW", "STATS", "FEED"
                    },
                    listFragments,
                    viewPager,
                    layoutInflater
                    );
            viewPager.setAdapter(pagerAdapter);
            tabs.setAdapter(pagerAdapter);

            // Make sure the tabs follow
            viewPager.setOnPageChangeListener(tabs);
            viewPager.setCurrentItem(0);
            viewPager.setOffscreenPageLimit(2);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate!!
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_profileview, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Our own profile, no need to show the "extra" buttons
        if (profileData.getProfileId() == SessionKeeper.getProfileData()
                .getProfileId()) {

            menu.removeItem(R.id.option_friendadd);
            menu.removeItem(R.id.option_frienddel);
            menu.removeItem(R.id.option_compare);
            menu.removeItem(R.id.option_unlocks);

        } else {

            // Which tab is operating?
            if (viewPager.getCurrentItem() == 0) {

                return super.onPrepareOptionsMenu( fragmentOverview.prepareOptionsMenu(menu) );
                
            } else if (viewPager.getCurrentItem() == 1) {

                return super.onPrepareOptionsMenu( fragmentStats.prepareOptionsMenu(menu) );

            } else if (viewPager.getCurrentItem() == 2) {

                ((MenuItem) menu.findItem(R.id.option_friendadd))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_frienddel))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_compare))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_unlocks))
                        .setVisible(false);

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

        // Let's act!
        if (item.getItemId() == R.id.option_reload) {

            this.reload();

        } else if (item.getItemId() == R.id.option_back) {

            ((Activity) this).finish();

        } else {
            
            if( viewPager.getCurrentItem() == 0 ) { 

                return fragmentOverview.handleSelectedOption(item);
                
            } else if( viewPager.getCurrentItem() == 1 ) { 
                
                return fragmentStats.handleSelectedOption(item);
                
            }
                            
        }

        // Return true yo
        return true;

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);

        // We need to initialize
        initActivity();

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
        
        switch( viewPager.getCurrentItem() ) {
            
            case 0:
                break;
                
            case 1:
                break;
                
            case 2:
                fragmentFeed.createContextMenu(menu, view, menuInfo);
                break;
            
        }
        return;

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

        switch( viewPager.getCurrentItem() ) {
            
            case 2:
                return fragmentFeed.handleSelectedContextItem(info, item);

            default: 
               break;
            
        }

        return true;
    }

    public void openStats(ProfileData p) {

        fragmentStats.setProfileData(p);
        fragmentStats.reload();

    }

    public void setFeedPermission(boolean c) {

        fragmentFeed.setCanWrite(c);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if( viewPager.getCurrentItem() > 0 ) {
                
                viewPager.setCurrentItem( viewPager.getCurrentItem()-1, true );
                
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    
}
