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

package com.ninetwozero.bf3droid.activity.profile.unlocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.datatype.DefaultFragmentActivity;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.datatype.UnlockData;
import com.ninetwozero.bf3droid.datatype.UnlockDataWrapper;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.http.ProfileClient;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.SessionKeeper;

public class UnlockActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    // Attributes
    private ProfileData mProfileData;
    private Map<Long, UnlockDataWrapper> mUnlocks;
    private long mSelectedPersona;
    private int mSelectedPosition;
    private long[] mPersonaId;
    private String[] mPersonaName;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

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

        // Init to winit
        mUnlocks = new HashMap<Long, UnlockDataWrapper>();

        // Let's try something out
        if (mProfileData.getId() == SessionKeeper.getProfileData().getId()) {

            mSelectedPersona = mSharedPreferences.getLong(Constants.SP_BL_PERSONA_CURRENT_ID, 0);
            mSelectedPosition = mSharedPreferences.getInt(Constants.SP_BL_PERSONA_CURRENT_POS, 0);

        } else {

            if (mProfileData.getNumPersonas() > 0) {

                mSelectedPersona = mProfileData.getPersona(0).getId();

            }
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        // Reload
        reload();

    }

    public Dialog generateDialogPersonaList() {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title and the view
        builder.setTitle(R.string.info_dialog_selection_generic);

        // Do we have items to show?
        if (mPersonaId == null) {

            // Init
            mPersonaId = new long[mProfileData.getNumPersonas()];
            mPersonaName = new String[mProfileData.getNumPersonas()];

            // Iterate
            for (int count = 0, max = mPersonaId.length; count < max; count++) {

                mPersonaId[count] = mProfileData.getPersona(count).getId();
                mPersonaName[count] = mProfileData.getPersona(count).getName();

            }

        }

        // Set it up
        builder.setSingleChoiceItems(

                mPersonaName, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                if (mPersonaId[item] != mSelectedPersona) {

                    // Update it
                    mSelectedPersona = mPersonaId[item];

                    // Store selectedPersonaPos
                    mSelectedPosition = item;

                    // Load the new!
                    showInFragment(mUnlocks.get(mSelectedPersona),
                            mViewPager.getCurrentItem());

                    // Save it
                    if (mProfileData.getId() == SessionKeeper.getProfileData().getId()) {
                        SharedPreferences.Editor spEdit = mSharedPreferences.edit();
                        spEdit.putLong(Constants.SP_BL_PERSONA_CURRENT_ID, mSelectedPersona);
                        spEdit.putInt(Constants.SP_BL_PERSONA_CURRENT_POS,
                                mSelectedPosition);
                        spEdit.commit();
                    }

                }

                dialog.dismiss();

            }

        }

        );

        // CREATE
        return builder.create();

    }

    public void setup() {

        // Do we need to setup the fragments?
        if (mListFragments == null) {

            // Add them to the list
            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            mListFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            mListFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            mListFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            mListFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));

            // Iterate over the fragments
            for (int i = 0, max = mListFragments.size(); i < max; i++) {

                ((UnlockFragment) mListFragments.get(i)).setViewPagerPosition(i);

            }

            // Get the ViewPager
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            mPagerAdapter = new SwipeyTabsPagerAdapter(

                    mFragmentManager,
                    new String[]{
                            "WEAPONS", "ATTACHMENTS", "KIT UNLOCKS", "VEHICLE ADDONS", "SKILLS"
                    },
                    mListFragments,
                    mViewPager,
                    mLayoutInflater
            );
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            // Make sure the tabs follow
            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setCurrentItem(0);

        }

    }

    public void reload() {

        // ASYNC!!!
        new AsyncGetDataSelf(this).execute(mProfileData);

    }

    public void doFinish() {
    }

    private class AsyncGetDataSelf extends
            AsyncTask<ProfileData, Void, Boolean> {

        // Attributes
        private Context mContext;
        private ProgressDialog mProgressDialog;

        public AsyncGetDataSelf(Context c) {

            mContext = c;

        }

        @Override
        protected void onPreExecute() {

            // Let's see
            if (mUnlocks.isEmpty()) {

                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.setTitle(mContext
                        .getString(R.string.general_wait));
                mProgressDialog
                        .setMessage(getString(R.string.general_downloading));
                mProgressDialog.show();

            }

        }

        @Override
        protected Boolean doInBackground(ProfileData... arg0) {

            try {

                if (arg0[0].getNumPersonas() == 0) {

                    mProfileData = ProfileClient
                            .resolveFullProfileDataFromProfileData(mProfileData);
                    if (mSelectedPersona == 0) {
                        mSelectedPersona = mProfileData.getPersona(0).getId();
                    }

                    // Get the unlocks
                    ProfileClient profileHandler = new ProfileClient(mProfileData);
                    mUnlocks = profileHandler.getUnlocks(mSharedPreferences.getInt(
                            Constants.SP_BL_UNLOCKS_LIMIT_MIN, 1));

                } else {

                    if (mSelectedPersona == 0) {
                        mSelectedPersona = arg0[0].getPersona(0).getId();
                    }

                    // Get the unlocks
                    ProfileClient profileHandler = new ProfileClient(mProfileData);
                    mUnlocks = profileHandler.getUnlocks(mSharedPreferences.getInt(
                            Constants.SP_BL_UNLOCKS_LIMIT_MIN, 1));

                }

                return (mUnlocks != null);

            } catch (WebsiteHandlerException ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Fail?
            if (!result) {

                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();

                }
                Toast.makeText(mContext, R.string.general_no_data,
                        Toast.LENGTH_SHORT).show();
                ((Activity) mContext).finish();

            }

            // Do actual stuff, like sending to an adapter
            int num = mViewPager.getCurrentItem();
            showInFragment(mUnlocks.get(mSelectedPersona), num);
            if (num > 0) {
                showInFragment(mUnlocks.get(mSelectedPersona), num - 1);
            }
            if (num < mViewPager.getChildCount()) {
                showInFragment(mUnlocks.get(mSelectedPersona), num + 1);
            }

            // Go go go
            if (mProgressDialog != null) {

                mProgressDialog.dismiss();

            }

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

        // Let's act!
        if (item.getItemId() == R.id.option_reload) {

            reload();

        } else if (item.getItemId() == R.id.option_change) {

            generateDialogPersonaList().show();

        } else if (item.getItemId() == R.id.option_back) {

            ((Activity) this).finish();

        }

        // Return true yo
        return true;

    }

    public List<UnlockData> getItemsForFragment(int p) {

        // Let's see if we got anything
        if (mUnlocks == null) {

            return new ArrayList<UnlockData>();

        } else {

            // Get the UnlockDataWrapper
            UnlockDataWrapper unlockDataWrapper = mUnlocks.get(mSelectedPersona);

            // Is the UnlockDataWrapper null?
            if (unlockDataWrapper == null) {
                return new ArrayList<UnlockData>();
            }

            // Switch over the position
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

}
