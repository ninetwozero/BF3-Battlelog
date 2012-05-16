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

import java.util.Vector;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.ninetwozero.battlelog.datatypes.DefaultFragmentActivity;
import com.ninetwozero.battlelog.fragments.AboutCreditsFragment;
import com.ninetwozero.battlelog.fragments.AboutFAQFragment;
import com.ninetwozero.battlelog.fragments.AboutMainFragment;

public class AboutActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set the content view
        setContentView(R.layout.viewpager_default);

        // Setup the fragments
        setupFragments();

        // Setup COM & feed
        initActivity();
    }

    public final void initActivity() {

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

    }

    public void setupFragments() {

        // Do we need to setup the fragments?
        if (listFragments == null) {

            // Add them to the list
            listFragments = new Vector<Fragment>();
            listFragments.add(Fragment.instantiate(this, AboutMainFragment.class.getName()));
            listFragments.add(Fragment.instantiate(this, AboutFAQFragment.class.getName()));
            listFragments.add(Fragment.instantiate(this, AboutCreditsFragment.class.getName()));

            // Get the ViewPager
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            pagerAdapter = new SwipeyTabsPagerAdapter(

                    fragmentManager,
                    new String[] {
                            getString(R.string.label_about), getString(R.string.label_faq),
                            getString(R.string.label_credits)

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

        }

    }

    @Override
    public void reload() {
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
}
