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

import android.app.Activity;
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
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

import java.util.ArrayList;

public class ForumActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    // Fragment related
    private ForumFragment mFragmentForum;
    private ForumThreadFragment mFragmentForumThread;
    private SavedForumThreadData mSavedThread;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set the content view
        setContentView(R.layout.viewpager_default);

        // Let's setup the fragments too
        setup();

        // Last but not least - init
        init();

    }

    public void init() {

    }

    @Override
    public void onResume() {

        super.onResume();

        // Let's try this
        openFromIntent(getIntent());

        // Reload
        reload();

    }

    public void setup() {

        // Do we need to setup the fragments?
        if (mListFragments == null) {

            // Add them to the list
            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(Fragment.instantiate(this,
                    BoardFragment.class.getName()));
            mListFragments.add(mFragmentForum = (ForumFragment) Fragment.instantiate(this,
                    ForumFragment.class.getName()));
            mListFragments.add(mFragmentForumThread = (ForumThreadFragment) Fragment.instantiate(
                    this, ForumThreadFragment.class.getName()));

            // Set the cache-mode
            if (getIntent().hasExtra("savedThread")) {
                mFragmentForumThread.setCaching(true);
            }

            // Get the ViewPager
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            mPagerAdapter = new SwipeyTabsPagerAdapter(

                    mFragmentManager,
                    new String[]{
                            "FORUMS", "THREADS", "POSTS"
                    },
                    mListFragments,
                    mViewPager,
                    mLayoutInflater
            );
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            // Make sure the tabs follow
            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setOffscreenPageLimit(2);
            mViewPager.setCurrentItem(0);

        }

    }

    public void reload() {

        // ASYNC!!!
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

        // Let's act!
        if (item.getItemId() == R.id.option_reload) {

            this.reload();

        } else if (item.getItemId() == R.id.option_save) {

            mFragmentForumThread.handleSelectedOption(item);

        } else if (item.getItemId() == R.id.option_back) {

            ((Activity) this).finish();

        }

        // Return true yo
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

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() > 0) {

            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
            return true;

        }

        return super.onKeyDown(keyCode, event);

    }

    public void openFromIntent(Intent intent) {

        // Do we have a saved thread?
        if (intent.hasExtra("savedThread") && mSavedThread == null) {

            mSavedThread = intent.getParcelableExtra("savedThread");
            openForum(new Intent().putExtra("forumTitle", "N/A").putExtra("forumId",
                    mSavedThread.getForumId()));
            openThread(new Intent().putExtra("threadTitle", mSavedThread.getTitle()).putExtra(
                    "threadId", mSavedThread.getId())
                    .putExtra("pageId", mSavedThread.getNumPageLastRead()));

        } else if (intent.hasExtra("searchedThread")) {

            ForumSearchResult thread = intent.getParcelableExtra("searchedThread");
            openThread(new Intent().putExtra("threadTitle", thread.getTitle())
                    .putExtra("threadId", thread.getId()).putExtra("pageId", 1));

        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenuInfo menuInfo) {

        if (mViewPager.getCurrentItem() == 2) {

            mFragmentForumThread.createContextMenu(menu, view, menuInfo);

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
