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

package com.ninetwozero.battlelog.activity.news;

import java.util.ArrayList;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.activity.feed.PostOverviewFragment;
import com.ninetwozero.battlelog.datatype.CommentData;
import com.ninetwozero.battlelog.datatype.FeedItem;
import com.ninetwozero.battlelog.datatype.NewsData;
import com.ninetwozero.battlelog.http.RequestHandler;
import com.ninetwozero.battlelog.misc.Constants;

public class SinglePostActivity extends CustomFragmentActivity {

    // Fragment related
    private PostOverviewFragment mFragmentOverview;
    private CommentListFragment mFragmentComment;

    // Misc
    private FeedItem mFeedData;
    private NewsData mNewsData;
    private boolean mNews = false;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        if (getIntent().hasExtra("feed")) {
            mFeedData = getIntent().getParcelableExtra("feed");
            mNews = false;
        } else if (getIntent().hasExtra("news")) {
            mNewsData = getIntent().getParcelableExtra("news");
            mNews = true;
        } else {
            finish();
        }
        
        setContentView(R.layout.viewpager_default);
        init();
        setup();
    }

    public void init() {

    }

    public void reload() {
        mFragmentOverview.reload();
        mFragmentComment.reload();
    }

    public void setup() {
        if (mListFragments == null) {
            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(
            	mFragmentOverview = (PostOverviewFragment) Fragment.instantiate(
                    this, PostOverviewFragment.class.getName()
                )
            );
            mListFragments.add(
        		mFragmentComment = (CommentListFragment) Fragment.instantiate(
                    this,
                    CommentListFragment.class.getName()
                )
            );

            if (mNews) {
                mFragmentOverview.setData(mNewsData);
                mFragmentComment.setId(mNewsData.getId());
                mFragmentComment.setType(CommentData.TYPE_NEWS);
            } else {
                mFragmentOverview.setData(mFeedData);
                mFragmentComment.setId(mFeedData.getId());
                mFragmentComment.setType(CommentData.TYPE_FEED);
            }
            
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);
            mPagerAdapter = new SwipeyTabsPagerAdapter(
                mFragmentManager,
                new String[]{
                        "OVERVIEW", "COMMENTS"
                },
                mListFragments,
                mViewPager,
                mLayoutInflater
            );
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setCurrentItem(0);
            mViewPager.setOffscreenPageLimit(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_basic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_reload) {
            this.reload();
        } else if (item.getItemId() == R.id.option_back) {
            ((Activity) this).finish();
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SUPER_COOKIES, RequestHandler.getCookies());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0, true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
