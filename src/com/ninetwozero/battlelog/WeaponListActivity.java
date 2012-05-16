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
import java.util.Map;
import java.util.Vector;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.ninetwozero.battlelog.datatypes.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WeaponDataWrapper;
import com.ninetwozero.battlelog.fragments.WeaponListFragment;
import com.ninetwozero.battlelog.handlers.ProfileHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class WeaponListActivity extends FragmentActivity implements DefaultFragmentActivity {

    // Attributes
    private final Context CONTEXT = this;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    private ProfileData profileData;
    private Map<Long, List<WeaponDataWrapper>> items;
    private long selectedPersona;
    private int selectedPosition;

    // Fragment related
    private SwipeyTabs tabs;
    private SwipeyTabsPagerAdapter pagerAdapter;
    private List<Fragment> listFragments;
    private FragmentManager fragmentManager;
    private ViewPager viewPager;

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

        // Last but not least - init
        initActivity();

    }

    public void initActivity() {

        // Are we there yet?
        // Set the selected persona
        selectedPersona = profileData.getPersona(0).getId();

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);

        // Reload
        reload();

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
            listFragments.add(Fragment.instantiate(this, WeaponListFragment.class.getName()));

            // Iterate over the fragments
            for (int i = 0, max = listFragments.size(); i < max; i++) {

                ((WeaponListFragment) listFragments.get(i)).setViewPagerPosition(i);

            }

            // Get the ViewPager
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            pagerAdapter = new SwipeyTabsPagerAdapter(

                    fragmentManager,
                    new String[] {
                            "WEAPONS"
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

    public void reload() {

        new AsyncRefresh(this).execute();

    }

    public void doFinish() {
    }

    private class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context context;

        // Construct
        public AsyncRefresh(Context c) {

            context = c;

        }

        @Override
        protected Boolean doInBackground(Void... arg) {

            try {

                items = ProfileHandler.getWeapons(profileData);
                return true;

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (context != null) {

                if (result) {

                    ((WeaponListFragment) listFragments.get(viewPager.getCurrentItem()))
                            .showWeapons(items.get(selectedPersona));

                } else {

                    Toast.makeText(context, R.string.general_no_data, Toast.LENGTH_SHORT).show();

                }

            }
        }

    }

    public List<WeaponDataWrapper> getItemsForFragment(int p) {

        // Let's see if we got anything
        if (items == null || items.get(selectedPersona) == null) {

            return new ArrayList<WeaponDataWrapper>();

        } else {

            // Get the UnlockDataWrapper
            return items.get(selectedPersona);

            /*
             * UnlockDataunlockDataWrapper = unlocks.get(selectedPersona); // Is
             * the UnlockDataWrapper null? if (unlockDataWrapper == null) {
             * return new ArrayList<UnlockData>(); } // Switch over the position
             * switch (p) { case 0: return unlockDataWrapper.getWeapons(); case
             * 1: return unlockDataWrapper.getAttachments(); case 2: return
             * unlockDataWrapper.getKitUnlocks(); case 3: return
             * unlockDataWrapper.getVehicleUpgrades(); case 4: return
             * unlockDataWrapper.getSkills(); default: return null; }
             */

        }

    }

    public void open(WeaponDataWrapper w) {

        startActivity(new Intent(this, SingleWeaponActivity.class).putExtra("profile", profileData)
                .putExtra("weapon", w));

    }

}
