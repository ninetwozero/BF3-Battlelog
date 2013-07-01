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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.aboutapp.AboutActivity;
import com.ninetwozero.bf3droid.activity.aboutapp.FeedbackActivity;
import com.ninetwozero.bf3droid.activity.feed.FeedFragment;
import com.ninetwozero.bf3droid.activity.forum.MenuForumFragment;
import com.ninetwozero.bf3droid.activity.news.NewsListFragment;
import com.ninetwozero.bf3droid.activity.platoon.MenuPlatoonFragment;
import com.ninetwozero.bf3droid.activity.profile.MenuProfileFragment;
import com.ninetwozero.bf3droid.activity.social.ComFriendFragment;
import com.ninetwozero.bf3droid.activity.social.ComNotificationFragment;
import com.ninetwozero.bf3droid.asynctask.AsyncLogout;
import com.ninetwozero.bf3droid.datatype.DefaultFragmentActivity;
import com.ninetwozero.bf3droid.http.FeedClient;

import java.util.ArrayList;
import java.util.List;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

public class DashboardActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    // COM-related
    private SlidingDrawer slidingDrawer;
    private TextView slidingDrawerHandle;

    private SwipeyTabs tabsCom;
    private SwipeyTabsPagerAdapter pagerAdapterCom;
    private List<Fragment> listFragmentsCom;
    private MenuPlatoonFragment platoonMenuFragment;
    private FeedFragment feedFragment;
    private ComFriendFragment comFriendFragment;
    private ComNotificationFragment comNotificationFragment;
    private ViewPager viewPager;
    private ViewPager viewPagerCom;
    private final int VIEWPAGER_POSITION_FEED = 4;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.viewpager_dashboard);
        init();
        setup();
        handleIfOpenedViaNotification();
    }

    private void handleIfOpenedViaNotification() {
		if( getIntent().getBooleanExtra("openCOMCenter", false) ) {
			slidingDrawer.open();
			viewPagerCom.setCurrentItem(1);
		}
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
        if (mListFragments == null) {
            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(Fragment.instantiate(this, NewsListFragment.class.getName()));
            mListFragments.add(Fragment.instantiate(this, MenuProfileFragment.class.getName()));
            mListFragments.add(platoonMenuFragment = (MenuPlatoonFragment) Fragment.instantiate(this, MenuPlatoonFragment.class.getName()));
            mListFragments.add(Fragment.instantiate(this, MenuForumFragment.class.getName()));
            mListFragments.add(feedFragment = (FeedFragment) Fragment.instantiate(this, FeedFragment.class.getName()));

            feedFragment.setType(FeedClient.TYPE_GLOBAL);
            feedFragment.setCanWrite(true);

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            mPagerAdapter = new SwipeyTabsPagerAdapter(
                    mFragmentManager, tabTitles(R.array.dashboard_tab), mListFragments, viewPager,
                    mLayoutInflater);
            viewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            viewPager.setOnPageChangeListener(mTabs);
            viewPager.setOffscreenPageLimit(4);
            viewPager.setCurrentItem(1);
        }

        if (listFragmentsCom == null) {
            listFragmentsCom = new ArrayList<Fragment>();
            listFragmentsCom.add(comFriendFragment = (ComFriendFragment) Fragment.instantiate(this, ComFriendFragment.class.getName()));
            listFragmentsCom.add(comNotificationFragment = (ComNotificationFragment) Fragment.instantiate(this, ComNotificationFragment.class.getName()));

            viewPagerCom = (ViewPager) findViewById(R.id.viewpager_sub);
            tabsCom = (SwipeyTabs) findViewById(R.id.swipeytabs_sub);

            pagerAdapterCom = new SwipeyTabsPagerAdapter(
                    mFragmentManager, 
                    tabTitles(R.array.dashboard_com_tab),
                    listFragmentsCom,
                    viewPagerCom,
                    mLayoutInflater
            );
            viewPagerCom.setAdapter(pagerAdapterCom);
            tabsCom.setAdapter(pagerAdapterCom);

            viewPagerCom.setOnPageChangeListener(tabsCom);
            viewPagerCom.setOffscreenPageLimit(1);
            viewPagerCom.setCurrentItem(0);
        }
    }

    @Override
    public void reload() {
        comFriendFragment.reload();
        comNotificationFragment.reload();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        if (slidingDrawer.isOpened()) {
            switch (viewPagerCom.getCurrentItem()) {
                case 0:
                    comFriendFragment.createContextMenu(menu, view, menuInfo);
                    break;
                default:
                    break;
            }
        } else {
            switch (viewPager.getCurrentItem()) {
                case VIEWPAGER_POSITION_FEED:
                    feedFragment.createContextMenu(menu, view, menuInfo);
                    break;
                default:
                    break;
            }
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
        if (slidingDrawer.isOpened()) {
            switch (viewPagerCom.getCurrentItem()) {
                case 0:
                    comFriendFragment.handleSelectedContextItem(info, item);
                    break;
                default:
                    break;
            }
        } else {
            switch (viewPager.getCurrentItem()) {
                case VIEWPAGER_POSITION_FEED:
                    return feedFragment.handleSelectedContextItem(info, item);
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
        if(item.getItemId() == R.id.option_search) {
        	startActivity(new Intent(this, SearchActivity.class) );
        } else if (item.getItemId() == R.id.option_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        } else if(item.getItemId() == R.id.option_feedback) {
        	startActivity(new Intent(this, FeedbackActivity.class));
        } else if (item.getItemId() == R.id.option_logout) {
            new AsyncLogout(this).execute();
        } else if (item.getItemId() == R.id.option_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return true;
    }

    public void setComLabel(String str) {
        slidingDrawerHandle.setText(str);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (slidingDrawer.isOpened()) {
                slidingDrawer.animateClose();
                return true;
            } else if (viewPager.getCurrentItem() != 1) {
                viewPager.setCurrentItem(1, true);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            startActivity(new Intent(this, SearchActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }
}
