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

package com.ninetwozero.bf3droid.activity.forum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.datatype.DefaultFragmentActivity;
import com.ninetwozero.bf3droid.datatype.ForumSearchResult;
import com.ninetwozero.bf3droid.datatype.SavedForumThreadData;

import java.util.ArrayList;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

public class ForumActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    private ForumFragment mFragmentForum;
    private ForumThreadFragment mFragmentForumThread;
    private SavedForumThreadData mSavedThread;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.viewpager_default);
        setup();
        init();
    }

    public void init() {
    }

    @Override
    public void onResume() {
        super.onResume();
        openFromIntent(getIntent());
        reload();
    }

    public void setup() {
        if (mListFragments == null) {

            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(Fragment.instantiate(this, BoardFragment.class.getName()));
            mListFragments.add(mFragmentForum = (ForumFragment) Fragment.instantiate(this, ForumFragment.class.getName()));
            mListFragments.add(mFragmentForumThread = (ForumThreadFragment) Fragment.instantiate(this, ForumThreadFragment.class.getName()));

            if (getIntent().hasExtra("savedThread")) {
                mFragmentForumThread.setCaching(true);
            }

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            mPagerAdapter = new SwipeyTabsPagerAdapter(
                    mFragmentManager,
                    tabTitles(R.array.forum_tab),
                    mListFragments,
                    mViewPager,
                    mLayoutInflater
            );
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setOffscreenPageLimit(2);
            mViewPager.setCurrentItem(0);
        }
    }

    public void reload() {
        mFragmentForum.reload();
        mFragmentForumThread.reload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_forumview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mViewPager.getCurrentItem() == 2) {
            mFragmentForumThread.prepareOptionsMenu(menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_reload) {
            this.reload();
        } else if (item.getItemId() == R.id.option_save) {
            mFragmentForumThread.handleSelectedOption(item);
        } else if (item.getItemId() == R.id.option_back) {
            this.finish();
        }
        return true;
    }

    public void openForum(Intent data) {
        mFragmentForum.openForum(data);
        mViewPager.setCurrentItem(1, true);
    }

    public void openThread(Intent data) {
        mFragmentForumThread.openThread(data);
        mViewPager.setCurrentItem(2, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() > 0) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void openFromIntent(Intent intent) {
        if (intent.hasExtra("savedThread") && mSavedThread == null) {
            mSavedThread = intent.getParcelableExtra("savedThread");
            openForum(new Intent().putExtra("forumTitle", "N/A").putExtra("forumId", mSavedThread.getForumId()));
            openThread(new Intent().putExtra("threadTitle", mSavedThread.getTitle())
                    .putExtra("threadId", mSavedThread.getId())
                    .putExtra("pageId", mSavedThread.getNumPageLastRead()));
        } else if (intent.hasExtra("searchedThread")) {
            ForumSearchResult thread = intent.getParcelableExtra("searchedThread");
            openThread(new Intent().putExtra("threadTitle", thread.getTitle())
                    .putExtra("threadId", thread.getId()).putExtra("pageId", 1));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        if (mViewPager.getCurrentItem() == 2) {
            mFragmentForumThread.createContextMenu(menu, view, menuInfo);
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

        if (mViewPager.getCurrentItem() == 2) {
            return mFragmentForumThread.handleSelectedContextItem(info, item);
        }
        return true;
    }

    public void resetPostFields() {
        if (mViewPager.getCurrentItem() == 2) {
            mFragmentForumThread.resetPostFields();
        }
    }
}
