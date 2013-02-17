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

package com.ninetwozero.bf3droid.activity.profile.unlocks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.datatype.*;
import com.ninetwozero.bf3droid.http.ProfileClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

public class UnlockActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    private static final String DIALOG = "UnlockActivity";
    private Map<Long, UnlockDataWrapper> unlocks;

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
        reload();
    }

    public void setup() {
        if (mListFragments == null) {
            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            mListFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            mListFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            mListFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            mListFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));

            for (int i = 0, max = mListFragments.size(); i < max; i++) {
                ((UnlockFragment) mListFragments.get(i)).setViewPagerPosition(i);
            }

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);
            mPagerAdapter = new SwipeyTabsPagerAdapter(

                    mFragmentManager,
                    new String[]{"WEAPONS", "ATTACHMENTS", "KIT UNLOCKS", "VEHICLE ADDONS", "SKILLS"},
                    mListFragments,
                    mViewPager,
                    mLayoutInflater
            );
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setCurrentItem(0);
        }
    }

    public void reload() {
        new AsyncGetDataSelf(this).execute();
    }

    public void doFinish() {
    }

    private class AsyncGetDataSelf extends AsyncTask<ProfileData, Void, Boolean> {
        private Context mContext;

        public AsyncGetDataSelf(Context c) {
            mContext = c;
        }

        @Override
        protected void onPreExecute() {
                startLoadingDialog(DIALOG);
        }

        @Override
        protected Boolean doInBackground(ProfileData... arg0) {
            try {
                ProfileClient profileHandler = new ProfileClient();
                unlocks = profileHandler.getUnlocks(1);
                return (unlocks != null);
            } catch (WebsiteHandlerException ex) {
                Log.i("UnlockActivity", ex.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                closeLoadingDialog(DIALOG);
                Toast.makeText(mContext, R.string.general_no_data, Toast.LENGTH_SHORT).show();
                ((Activity) mContext).finish();
            }

            int num = mViewPager.getCurrentItem();
            showInFragment(unlocks.get(selectedPersonaId()), num);
            if (num > 0) {
                showInFragment(unlocks.get(selectedPersonaId()), num - 1);
            }
            if (num < mViewPager.getChildCount()) {
                showInFragment(unlocks.get(selectedPersonaId()), num + 1);
            }
            closeLoadingDialog(DIALOG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_unlock, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void showInFragment(UnlockDataWrapper data, int position) {
        ((UnlockFragment) mListFragments.get(position)).showUnlocks(getItemsForFragment(position));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_reload) {
            reload();
        } else if (item.getItemId() == R.id.option_back) {
            this.finish();
        }
        return true;
    }

    public List<UnlockData> getItemsForFragment(int p) {
        if (unlocks == null) {
            return new ArrayList<UnlockData>();
        } else {
            UnlockDataWrapper unlockDataWrapper = unlocks.get(selectedPersonaId());

            if (unlockDataWrapper == null) {
                return new ArrayList<UnlockData>();
            }

            switch (p) {
                case 0:
                    return unlockDataWrapper.getWeapons();
                case 1:
                    return unlockDataWrapper.getAttachments();
                case 2:
                    return unlockDataWrapper.getKitUnlocks();
                case 3:
                    return unlockDataWrapper.getVehicleUpgrades();
                case 4:
                    return unlockDataWrapper.getSkills();
                default:
                    return null;
            }
        }
    }

    private long selectedPersonaId(){
        return BF3Droid.getUser().selectedPersona().getPersonaId();
    }
}
