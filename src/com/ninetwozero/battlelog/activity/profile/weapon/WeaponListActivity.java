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

package com.ninetwozero.battlelog.activity.profile.weapon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.datatype.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.WeaponDataWrapper;
import com.ninetwozero.battlelog.http.ProfileClient;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaponListActivity extends CustomFragmentActivity implements
        DefaultFragmentActivity {

    // Attributes
    private ProfileData mProfileData;
    private Map<Long, List<WeaponDataWrapper>> mItems;
    private long mSelectedPersona;
	private AsyncRefresh myRefresher;
	public boolean refreshOngoing;

    // private int mSelectedPosition;

    @Override
    public void onCreate(final Bundle icicle) {
        // onCreate - save the instance state
        super.onCreate(icicle);
        refreshOngoing = false;
        

        // Get the intent
        if (!getIntent().hasExtra("profile")) {
            finish();
        }

        // Get the profile
        mProfileData = getIntent().getParcelableExtra("profile");

        // Set the content view
        setContentView(R.layout.viewpager_default);

        // Let's setup the fragments too
        setup();

        // Last but not least - init
        init();

    }

    public void init() {

        mItems = new HashMap<Long, List<WeaponDataWrapper>>();

        // Set the selected persona
        if (mProfileData.getNumPersonas() > 0) {

            mSelectedPersona = mProfileData.getPersona(0).getId();

        }

    }

    @Override
    public void onResume() {
    	Log.i(getClass().getName(),"onResume...");
        super.onResume();

        // Reload
        reload();

    }

    public void setup() {

        // Do we need to setup the fragments?
        if (mListFragments == null) {

            // Add them to the list
            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(Fragment.instantiate(this,
                    WeaponListFragment.class.getName()));

            // Iterate over the fragments
            for (int i = 0, max = mListFragments.size(); i < max; i++) {

                ((WeaponListFragment) mListFragments.get(i))
                        .setViewPagerPosition(i);

            }

            // Get the ViewPager
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            mPagerAdapter = new SwipeyTabsPagerAdapter(

                    mFragmentManager, new String[]{"WEAPONS"}, mListFragments,
                    mViewPager, mLayoutInflater);
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            // Make sure the tabs follow
            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setCurrentItem(0);

        }

    }

    public void reload() {
    	Log.i(getClass().getName(),"reload...");
    	if (!refreshOngoing || myRefresher.isCancelled()) {
    		refreshOngoing = true;
    		myRefresher = new AsyncRefresh(this);
    		myRefresher.execute();
		} 
    }

    public void doFinish() {
    }

    private class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context mContext;
        private ProgressDialog mProgressDialog;

        // Construct
        public AsyncRefresh(Context c) {

            mContext = c;

        }

        @Override
        protected void onPreExecute() {
        	refreshOngoing = true;
            if (mItems.isEmpty()) {
                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.setTitle(mContext
                        .getString(R.string.general_wait));
                mProgressDialog.setMessage(mContext
                        .getString(R.string.general_downloading));
                mProgressDialog.show();
            }

        }

        @Override
        protected Boolean doInBackground(Void... arg) {

            try {

                if (mProfileData.getNumPersonas() == 0) {

                    mProfileData = ProfileClient
                            .resolveFullProfileDataFromProfileData(mProfileData);
                    mSelectedPersona = mProfileData.getPersona(0).getId();

                }

                mItems = new ProfileClient(mProfileData).getWeapons();
                Log.i(getClass().getName(), "doInBackground completed...");
                
                return true;

            } catch (Exception ex) {

                ex.printStackTrace();
                Log.i(getClass().getName(), "doInBackground completed...");
                return false;
            }
            
        }

        @Override
        protected void onPostExecute(Boolean result) {
        	
            if (mContext != null) {

                if (result) {

                    ((WeaponListFragment) mListFragments.get(mViewPager
                            .getCurrentItem())).showWeapons(mItems
                            .get(mSelectedPersona));

                } else {

                    Toast.makeText(mContext, R.string.general_no_data,
                            Toast.LENGTH_SHORT).show();

                }

                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }

            }
            refreshOngoing = false;
        }

    }

    public List<WeaponDataWrapper> getItemsForFragment(int p) {

        // Let's see if we got anything
        if (mItems == null || mItems.get(mSelectedPersona) == null) {

            return new ArrayList<WeaponDataWrapper>();

        } else {

            // Get the UnlockDataWrapper
            return mItems.get(mSelectedPersona);

        }

    }

    public void open(WeaponDataWrapper w) {

        startActivity(new Intent(this, SingleWeaponActivity.class).putExtra(
                "profile", mProfileData).putExtra("weapon", w));

    }

}
