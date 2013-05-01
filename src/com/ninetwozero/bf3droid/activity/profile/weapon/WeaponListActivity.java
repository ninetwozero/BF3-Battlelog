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

package com.ninetwozero.bf3droid.activity.profile.weapon;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.datatype.DefaultFragmentActivity;
import com.ninetwozero.bf3droid.datatype.WeaponDataWrapper;
import com.ninetwozero.bf3droid.http.ProfileClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

public class WeaponListActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    private Map<Long, List<WeaponDataWrapper>> weapons = new HashMap<Long, List<WeaponDataWrapper>>();

    private final String DIALOG = "WeaponListActivity";

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.viewpager_default);

        setup();
    }

    @Override
    public void init() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (weapons == null || weapons.size() == 0) {
            reload();
        }
    }

    public void setup() {
        if (mListFragments == null) {
            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(Fragment.instantiate(this,
                    WeaponListFragment.class.getName()));

            for (int i = 0, max = mListFragments.size(); i < max; i++) {
                ((WeaponListFragment) mListFragments.get(i)).setViewPagerPosition(i);
            }

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            mPagerAdapter = new SwipeyTabsPagerAdapter(
                    mFragmentManager, tabTitles(R.array.weapon_list_title), mListFragments,
                    mViewPager, mLayoutInflater);
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setCurrentItem(0);
        }
    }

    public void reload() {
        new AsyncRefresh(this).execute();
    }

    private class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;

        public AsyncRefresh(Context c) {
            mContext = c;
        }

        @Override
        protected void onPreExecute() {
            if (weapons.isEmpty()) {
                startLoadingDialog(DIALOG);
            }
        }

        @Override
        protected Boolean doInBackground(Void... arg) {
            try {
                weapons = new ProfileClient().getWeapons(userType());
                return true;
            } catch (Exception ex) {
                Log.d("WeaponListActivity", ex.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (mContext != null) {
                if (result) {
                    ((WeaponListFragment) mListFragments.get(mViewPager
                            .getCurrentItem())).showWeapons(weapons
                            .get(selectedPersonaId()));
                } else {
                    Toast.makeText(mContext, R.string.general_no_data, Toast.LENGTH_SHORT).show();
                }
                closeLoadingDialog(DIALOG);
            }
        }
    }

    public List<WeaponDataWrapper> getItemsForFragment(int p) {
        if (weapons == null || weapons.get(selectedPersonaId()) == null) {
            return new ArrayList<WeaponDataWrapper>();
        } else {
            return weapons.get(selectedPersonaId());
        }
    }

    public void open(WeaponDataWrapper w) {
        startActivity(new Intent(this,
                SingleWeaponActivity.class)
                .putExtra("weapon", w)
                .putExtra("user", userType()));
    }

    private String userType() {
        return getIntent().getStringExtra("user");
    }

    private long selectedPersonaId() {
        return BF3Droid.getUserBy(userType()).selectedPersona().getPersonaId();
    }
}
