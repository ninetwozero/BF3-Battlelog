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

import java.util.ArrayList;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.FriendListAdapter;
import com.ninetwozero.battlelog.adapters.NotificationListAdapter;
import com.ninetwozero.battlelog.adapters.RequestListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncLogout;
import com.ninetwozero.battlelog.datatypes.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatypes.FriendListDataWrapper;
import com.ninetwozero.battlelog.datatypes.NotificationData;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.fragments.FeedFragment;
import com.ninetwozero.battlelog.fragments.MenuFragment;
import com.ninetwozero.battlelog.fragments.MenuProfileFragment;
import com.ninetwozero.battlelog.fragments.NewsFragment;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class Dashboard extends FragmentActivity implements DefaultFragmentActivity {

    // Attributes
    final private Context context = this;
    private String[] valueFieldsArray;
    private PostData[] postDataArray;
    private List<NotificationData> notificationArray;
    private FriendListDataWrapper friendListData;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    
    // COM-related
    private SlidingDrawer slidingDrawer;
    private TextView slidingDrawerHandle;
    private OnDrawerOpenListener onDrawerOpenListener;
    private OnDrawerCloseListener onDrawerCloseListener;
    private ListView listFriendRequests, listFriends, listNotifications;
    private NotificationListAdapter notificationListAdapter;
    private FriendListAdapter friendListAdapter;
    private RequestListAdapter friendRequestListAdapter;
    private OnItemClickListener onItemClickListener;
    private Button buttonRefresh;

    // Fragment related
    private SwipeyTabs tabs;
    private SwipeyTabsPagerAdapter pagerAdapter;
    private List<Fragment> listFragments;
    private FragmentManager fragmentManager;
    private NewsFragment fragmentNews;
    private MenuFragment fragmentMenu;
    private MenuProfileFragment fragmentMenuProfile;
    private FeedFragment fragmentFeed;
    private ViewPager viewPager;

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
        setContentView(R.layout.viewpager_default);

        // Get the layoutInflater
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fragmentManager = getSupportFragmentManager();

        // Setup the fragments
        setupFragments();

        // Setup COM & feed
        initActivity();
    }

    public final void initActivity() {

        // Setup the data
        notificationArray = new ArrayList<NotificationData>();
        friendListData = new FriendListDataWrapper(null, null, null);

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

    public void onMenuClick(View v) {

        ((MenuFragment) fragmentMenu).onMenuClick(v);

    }

    public void setupFragments() {

        // Do we need to setup the fragments?
        if (listFragments == null) {

            // Add them to the list
            listFragments = new Vector<Fragment>();
            listFragments.add(fragmentNews = (NewsFragment) Fragment.instantiate(this,
                    NewsFragment.class.getName()));
            listFragments.add(fragmentMenu = (MenuFragment) Fragment.instantiate(this,
                    MenuFragment.class.getName()));
            listFragments.add(fragmentMenuProfile = (MenuProfileFragment) Fragment.instantiate(this,
                    MenuProfileFragment.class.getName()));
            listFragments.add(fragmentFeed = (FeedFragment) Fragment.instantiate(this,
                    FeedFragment.class.getName()));

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
                            "NEWS", "HOME", "PROFILE", "FEED"
                    },
                    listFragments,
                    viewPager,
                    layoutInflater
                    );
            viewPager.setAdapter(pagerAdapter);
            tabs.setAdapter(pagerAdapter);

            // Make sure the tabs follow
            viewPager.setOnPageChangeListener(tabs);
            viewPager.setCurrentItem(2);

        }

    }

    public void validateSession() {

        // We should've gotten a profile
        if (SessionKeeper.getProfileData() == null) {

            if (getIntent().hasExtra("myProfile")) {

                SessionKeeper.setProfileData((ProfileData) getIntent()
                        .getParcelableExtra("myProfile"));

            } else {

                Toast.makeText(this, R.string.info_txt_session_lost,
                        Toast.LENGTH_SHORT).show();
                return;

            }

        }

    }

    @Override
    public void reload() {
        // TODO COM

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        switch (viewPager.getCurrentItem()) {

            case 0:
                break;

            case 1:
                fragmentFeed.createContextMenu(menu, view, menuInfo);
                break;

            default:
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

        switch (viewPager.getCurrentItem()) {

            case 1:
                return fragmentFeed.handleSelectedContextItem(info, item);

            default:
                break;

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

            fragmentFeed.reload();
//            fragmentCOM.reload();
//            fragmentNotifications.reload();

        } else if (item.getItemId() == R.id.option_settings) {

            startActivity(new Intent(this, SettingsView.class));
            finish();

        } else if (item.getItemId() == R.id.option_logout) {

            new AsyncLogout(this).execute();

        } else if (item.getItemId() == R.id.option_about) {

            startActivity(new Intent(this, AboutView.class));

        }

        // Return true yo
        return true;

    }

}
