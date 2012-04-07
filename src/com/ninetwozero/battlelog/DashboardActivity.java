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
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.asynctasks.AsyncLogout;
import com.ninetwozero.battlelog.datatypes.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.fragments.ComFriendFragment;
import com.ninetwozero.battlelog.fragments.ComNotificationFragment;
import com.ninetwozero.battlelog.fragments.FeedFragment;
import com.ninetwozero.battlelog.fragments.MenuForumFragment;
import com.ninetwozero.battlelog.fragments.MenuPlatoonFragment;
import com.ninetwozero.battlelog.fragments.MenuProfileFragment;
import com.ninetwozero.battlelog.fragments.NewsFragment;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class DashboardActivity extends FragmentActivity implements DefaultFragmentActivity {

    // Attributes
    final private Context context = this;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;

    // COM-related
    private SlidingDrawer slidingDrawer;
    private TextView slidingDrawerHandle;
    private OnDrawerOpenListener onDrawerOpenListener;
    private OnDrawerCloseListener onDrawerCloseListener;
    private Button buttonRefresh;

    // Fragment related
    private SwipeyTabs tabs, tabsCom;
    private SwipeyTabsPagerAdapter pagerAdapter, pagerAdapterCom;
    private List<Fragment> listFragments, listFragmentsCom;
    private FragmentManager fragmentManager;
    private NewsFragment fragmentNews;
    private MenuProfileFragment fragmentMenuProfile;
    private MenuPlatoonFragment fragmentMenuPlatoon;
    private MenuForumFragment fragmentMenuForum;
    private FeedFragment fragmentFeed;
    private ComFriendFragment fragmentComFriends;
    private ComNotificationFragment fragmentComNotifications;
    private ViewPager viewPager, viewPagerCom;
    private final int VIEWPAGER_POSITION_FEED = 4;

    // Async
    private AsyncLogout asyncLogout;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Should we display a title bar?
        PublicUtils.setupFullscreen(this, sharedPreferences);
        PublicUtils.restoreCookies(this, icicle);

        // Validate our session
        validateSession();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Set the content view
        setContentView(R.layout.viewpager_dashboard);

        // Get the layoutInflater
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fragmentManager = getSupportFragmentManager();

        // Setup the fragments
        setupFragments();

        // Setup COM & feed
        initActivity();

    }

    public final void initActivity() {

        slidingDrawer = (SlidingDrawer) findViewById(R.id.com_slider);
        slidingDrawerHandle = (TextView) findViewById(R.id.com_slide_handle_text);

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);

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

    public void setupFragments() {

        // Do we need to setup the fragments?
        if (listFragments == null) {

            // Add them to the list
            listFragments = new Vector<Fragment>();
            listFragments.add(fragmentNews = (NewsFragment) Fragment.instantiate(this,
                    NewsFragment.class.getName()));
            // listFragments.add(fragmentMenu = (MenuFragment)
            // Fragment.instantiate(this, MenuFragment.class.getName()));
            listFragments.add(fragmentMenuProfile = (MenuProfileFragment) Fragment.instantiate(
                    this,
                    MenuProfileFragment.class.getName()));
            listFragments.add(fragmentMenuPlatoon = (MenuPlatoonFragment) Fragment.instantiate(
                    this,
                    MenuPlatoonFragment.class.getName()));
            listFragments.add(fragmentMenuForum = (MenuForumFragment) Fragment.instantiate(
                    this,
                    MenuForumFragment.class.getName()));
            listFragments.add(fragmentFeed = (FeedFragment) Fragment.instantiate(this,
                    FeedFragment.class.getName()));

            // Setup platoon tab
            fragmentMenuPlatoon.setPlatoonData(SessionKeeper.getPlatoonData());

            // Setup the feed
            fragmentFeed.setType(FeedFragment.TYPE_GLOBAL);
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
            viewPager.setOffscreenPageLimit(2);
            viewPager.setCurrentItem(1);

        }

        if (listFragmentsCom == null) {

            // Add them to the list
            listFragmentsCom = new Vector<Fragment>();
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
                    viewPager,
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

    @SuppressWarnings("unchecked")
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

                Toast.makeText(this, R.string.info_txt_session_lost,
                        Toast.LENGTH_SHORT).show();
                return;

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
            return;

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

            if( slidingDrawer.isOpened() ) {
                
                slidingDrawer.animateClose();
                return true;
                
            } else if (viewPager.getCurrentItem() > 1) {

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                return true;

            }

        }
        return super.onKeyDown(keyCode, event);

    }
}
