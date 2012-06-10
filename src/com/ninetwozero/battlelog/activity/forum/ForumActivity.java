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

package com.ninetwozero.battlelog.activity.forum;

import java.util.Vector;

import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.R;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import com.ninetwozero.battlelog.datatype.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatype.SavedForumThreadData;

public class ForumActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    // Fragment related
    private ForumFragment fragmentForum;
    private ForumThreadFragment fragmentForumThread;
    private SavedForumThreadData savedThread;

    @Override
    public void onCreate(Bundle icicle) {

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

        // Reload
        reload();

        // Let's try this
        openFromIntent(getIntent());

    }

    public void setup() {

        // Do we need to setup the fragments?
        if (listFragments == null) {

            // Add them to the list
            listFragments = new Vector<Fragment>();
            listFragments.add(Fragment.instantiate(this,
                    BoardFragment.class.getName()));
            listFragments.add(fragmentForum = (ForumFragment) Fragment.instantiate(this,
                    ForumFragment.class.getName()));
            listFragments.add(fragmentForumThread = (ForumThreadFragment) Fragment.instantiate(
                    this, ForumThreadFragment.class.getName()));

            // Get the ViewPager
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            pagerAdapter = new SwipeyTabsPagerAdapter(

                    fragmentManager,
                    new String[] {
                            "FORUMS", "THREADS", "POSTS"
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
            viewPager.setCurrentItem(0);

        }

    }

    public void reload() {

        // ASYNC!!!

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_forumview, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (viewPager.getCurrentItem() == 2) {

            fragmentForumThread.prepareOptionsMenu(menu);

        }
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Let's act!
        if (item.getItemId() == R.id.option_reload) {

            this.reload();

        } else if (item.getItemId() == R.id.option_save) {

            fragmentForumThread.handleSelectedOption(item);

        } else if (item.getItemId() == R.id.option_back) {

            ((Activity) this).finish();

        }

        // Return true yo
        return true;

    }

    public void openForum(Intent data) {

        fragmentForum.openForum(data);
        viewPager.setCurrentItem(1, true);

    }

    public void openThread(Intent data) {

        fragmentForumThread.openThread(data);
        viewPager.setCurrentItem(2, true);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (viewPager.getCurrentItem() > 0) {

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                return true;

            }

        }
        return super.onKeyDown(keyCode, event);

    }

    public void openFromIntent(Intent intent) {

        // Do we have a saved thread?
        if (intent.hasExtra("savedThread") && savedThread == null) {

            savedThread = intent.getParcelableExtra("savedThread");
            openForum(new Intent().putExtra("forumTitle", "N/A").putExtra("forumId",
                    savedThread.getForumId()));
            openThread(new Intent().putExtra("threadTitle", savedThread.getTitle()).putExtra(
                    "threadId", savedThread.getId())
                    .putExtra("pageId", savedThread.getNumPageLastRead()));

        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        switch (viewPager.getCurrentItem()) {

            case 0:
                break;

            case 1:
                break;

            case 2:
                fragmentForumThread.createContextMenu(menu, view, menuInfo);
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

            case 2:
                return fragmentForumThread.handleSelectedContextItem(info, item);

            default:
                break;

        }

        return true;

    }

    public void resetPostFields() {

        switch (viewPager.getCurrentItem()) {

            case 2:
                fragmentForumThread.resetPostFields();
                break;

            default:
                break;
        }

    }

}
