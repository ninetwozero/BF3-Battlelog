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

import java.util.ArrayList;
import java.util.List;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.aboutapp.AboutActivity;
import com.ninetwozero.battlelog.activity.feed.FeedFragment;
import com.ninetwozero.battlelog.activity.forum.MenuForumFragment;
import com.ninetwozero.battlelog.activity.news.NewsListFragment;
import com.ninetwozero.battlelog.activity.platoon.MenuPlatoonFragment;
import com.ninetwozero.battlelog.activity.profile.MenuProfileFragment;
import com.ninetwozero.battlelog.activity.social.ComFriendFragment;
import com.ninetwozero.battlelog.activity.social.ComNotificationFragment;
import com.ninetwozero.battlelog.asynctask.AsyncLogout;
import com.ninetwozero.battlelog.datatype.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatype.FeedItem;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class DashboardActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    // COM-related
    private SlidingDrawer slidingDrawer;
    private TextView slidingDrawerHandle;

    // Fragment related
    private SwipeyTabs tabsCom;
    private SwipeyTabsPagerAdapter pagerAdapterCom;
    private List<Fragment> listFragmentsCom;
    private MenuPlatoonFragment fragmentMenuPlatoon;
    private FeedFragment fragmentFeed;
    private ComFriendFragment fragmentComFriends;
    private ComNotificationFragment fragmentComNotifications;
    private ViewPager viewPager, viewPagerCom;
    private final int VIEWPAGER_POSITION_FEED = 4;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Validate our session
        validateSession();

        // Set the content view
        setContentView(R.layout.viewpager_dashboard);

        // Setup the fragments
        setup();

        // Setup COM & feed
        init();

    }

    public final void init() {

        slidingDrawer = (SlidingDrawer) findViewById(R.id.com_slider);
        slidingDrawerHandle = (TextView) findViewById(R.id.com_slide_handle_text);

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    public void setup() {

        // Do we need to setup the fragments?
        if (listFragments == null) {

            // Add them to the list
            listFragments = new ArrayList<Fragment>();
            listFragments.add(Fragment.instantiate(this,
                    NewsListFragment.class.getName()));
            listFragments.add(Fragment.instantiate(
                    this,
                    MenuProfileFragment.class.getName()));
            listFragments.add(fragmentMenuPlatoon = (MenuPlatoonFragment) Fragment.instantiate(
                    this,
                    MenuPlatoonFragment.class.getName()));
            listFragments.add(Fragment.instantiate(
                    this,
                    MenuForumFragment.class.getName()));
            listFragments.add(fragmentFeed = (FeedFragment) Fragment.instantiate(this,
                    FeedFragment.class.getName()));

            // Setup platoon tab
            fragmentMenuPlatoon.setPlatoonData(SessionKeeper.getPlatoonData());

            // Setup the feed
            fragmentFeed.setType(FeedItem.TYPE_GLOBAL);
            fragmentFeed.setCanWrite(true);

            // Get the ViewPager
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            pagerAdapter = new SwipeyTabsPagerAdapter(

                    fragmentManager,
                    new String[] {
                            "NEWS", "PROFILE", "PLATOON", "FORUM", "FEED"
                    },
                    listFragments,
                    viewPager,
                    layoutInflater
                    );
            viewPager.setAdapter(pagerAdapter);
            tabs.setAdapter(pagerAdapter);

            // Make sure the tabs follow
            viewPager.setOnPageChangeListener(tabs);
            viewPager.setOffscreenPageLimit(4);
            viewPager.setCurrentItem(1);

        }

        if (listFragmentsCom == null) {

            // Add them to the list
            listFragmentsCom = new ArrayList<Fragment>();
            listFragmentsCom.add(fragmentComFriends = (ComFriendFragment) Fragment.instantiate(
                    this,
                    ComFriendFragment.class.getName()));
            listFragmentsCom.add(fragmentComNotifications = (ComNotificationFragment) Fragment
                    .instantiate(this, ComNotificationFragment.class.getName()));

            // Get the ViewPager
            viewPagerCom = (ViewPager) findViewById(R.id.viewpager_sub);
            tabsCom = (SwipeyTabs) findViewById(R.id.swipeytabs_sub);

            // Fill the PagerAdapter & set it to the viewpager
            pagerAdapterCom = new SwipeyTabsPagerAdapter(

                    fragmentManager,
                    new String[] {
                            "FRIENDS", "NOTIFICATIONS"
                    },
                    listFragmentsCom,
                    viewPagerCom,
                    layoutInflater
                    );
            viewPagerCom.setAdapter(pagerAdapterCom);
            tabsCom.setAdapter(pagerAdapterCom);

            // Make sure the tabs follow
            viewPagerCom.setOnPageChangeListener(tabsCom);
            viewPagerCom.setOffscreenPageLimit(1);
            viewPagerCom.setCurrentItem(0);

        }

    }

    public void validateSession() {

        // We should've gotten a profile
        if (SessionKeeper.getProfileData() == null) {

            if (getIntent().hasExtra("myProfile")) {

                // Get 'em
                ProfileData profileData = getIntent().getParcelableExtra("myProfile");
                List<PlatoonData> platoonArray = getIntent().getParcelableArrayListExtra(
                        "myPlatoon");

                // Set 'em
                SessionKeeper.setProfileData(profileData);
                SessionKeeper.setPlatoonData(platoonArray);

            } else {

                Toast.makeText(this, R.string.info_txt_session_lost, Toast.LENGTH_SHORT).show();

            }

        }

    }

    @Override
    public void reload() {

        // Update the COM
        fragmentComFriends.reload();
        fragmentComNotifications.reload();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        if (slidingDrawer.isOpened()) {

            switch (viewPagerCom.getCurrentItem()) {

                case 0:
                    fragmentComFriends.createContextMenu(menu, view, menuInfo);
                    break;

                default:
                    break;

            }

        } else {

            switch (viewPager.getCurrentItem()) {

                case VIEWPAGER_POSITION_FEED:
                    fragmentFeed.createContextMenu(menu, view, menuInfo);
                    break;

                default:
                    break;

            }

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

        if (slidingDrawer.isOpened()) {

            switch (viewPagerCom.getCurrentItem()) {

                case 0:
                    fragmentComFriends.handleSelectedContextItem(info, item);
                    break;

                default:
                    break;

            }

        } else {

            switch (viewPager.getCurrentItem()) {

                case VIEWPAGER_POSITION_FEED:
                    return fragmentFeed.handleSelectedContextItem(info, item);

                default:
                    break;

            }

        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_dashboard, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Let's act!
        if (item.getItemId() == R.id.option_refresh) {

            reload();

        } else if (item.getItemId() == R.id.option_settings) {

            startActivity(new Intent(this, SettingsActivity.class));
            finish();

        } else if (item.getItemId() == R.id.option_logout) {

            new AsyncLogout(this).execute();

        } else if (item.getItemId() == R.id.option_about) {

            startActivity(new Intent(this, AboutActivity.class));

        }

        // Return true yo
        return true;

    }

    public void setComLabel(String str) {

        slidingDrawerHandle.setText(str);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (slidingDrawer.isOpened()) {

                slidingDrawer.animateClose();
                return true;

            } else if (viewPager.getCurrentItem() > 1) {

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                return true;

            }

        } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {

            startActivity(new Intent(this, SearchActivity.class));

        }
        return super.onKeyDown(keyCode, event);

    }
}
