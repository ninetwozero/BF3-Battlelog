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

package com.ninetwozero.battlelog.activity.profile.unlocks;

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

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.datatype.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.UnlockData;
import com.ninetwozero.battlelog.datatype.UnlockDataWrapper;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class UnlockActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    // Attributes
    private ProfileData profileData;
    private Map<Long, UnlockDataWrapper> unlocks;
    private long selectedPersona;
    private int selectedPosition;
    private long[] personaId;
    private String[] personaName;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Get the intent
        if (!getIntent().hasExtra("profile")) {
            finish();
        }

        // Get the profile
        profileData = getIntent().getParcelableExtra("profile");

        // Set the content view
        setContentView(R.layout.viewpager_default);

        // Let's setup the fragments too
        setup();

        // Last but not least - init
        init();

    }

    public void init() {

        // Init to winit
        unlocks = new HashMap<Long, UnlockDataWrapper>();

        // Let's try something out
        if (profileData.getId() == SessionKeeper.getProfileData().getId()) {

            selectedPersona = sharedPreferences.getLong(Constants.SP_BL_PERSONA_CURRENT_ID, 0);
            selectedPosition = sharedPreferences.getInt(Constants.SP_BL_PERSONA_CURRENT_POS, 0);

        } else {

            selectedPersona = profileData.getPersona(0).getId();

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
        builder.setTitle(R.string.info_dialog_soldierselect);

        // Do we have items to show?
        if (personaId == null) {

            // Init
            personaId = new long[profileData.getNumPersonas()];
            personaName = new String[profileData.getNumPersonas()];

            // Iterate
            for (int count = 0, max = personaId.length; count < max; count++) {

                personaId[count] = profileData.getPersona(count).getId();
                personaName[count] = profileData.getPersona(count).getName();

            }

        }

        // Set it up
        builder.setSingleChoiceItems(

                personaName, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        if (personaId[item] != selectedPersona) {

                            // Update it
                            selectedPersona = personaId[item];

                            // Store selectedPersonaPos
                            selectedPosition = item;

                            // Load the new!
                            setupList(unlocks.get(selectedPersona), viewPager.getCurrentItem());

                            // Save it
                            if (profileData.getId() == SessionKeeper.getProfileData().getId()) {
                                SharedPreferences.Editor spEdit = sharedPreferences.edit();
                                spEdit.putLong(Constants.SP_BL_PERSONA_CURRENT_ID, selectedPersona);
                                spEdit.putInt(Constants.SP_BL_PERSONA_CURRENT_POS, selectedPosition);
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
        if (listFragments == null) {

            // Add them to the list
            listFragments = new ArrayList<Fragment>();
            listFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            listFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            listFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            listFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));
            listFragments.add(Fragment.instantiate(this, UnlockFragment.class.getName()));

            // Iterate over the fragments
            for (int i = 0, max = listFragments.size(); i < max; i++) {

                ((UnlockFragment) listFragments.get(i)).setViewPagerPosition(i);

            }

            // Get the ViewPager
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            // Fill the PagerAdapter & set it to the viewpager
            pagerAdapter = new SwipeyTabsPagerAdapter(

                    fragmentManager,
                    new String[] {
                            "WEAPONS", "ATTACHMENTS", "KIT UNLOCKS", "VEHICLE ADDONS", "SKILLS"
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

        // ASYNC!!!
        new AsyncGetDataSelf(this).execute(profileData);

    }

    public void doFinish() {
    }

    private class AsyncGetDataSelf extends
            AsyncTask<ProfileData, Void, Boolean> {

        // Attributes
        private Context context;
        private ProgressDialog progressDialog;

        public AsyncGetDataSelf(Context c) {

            context = c;

        }

        @Override
        protected void onPreExecute() {

            // Let's see
            if (unlocks == null) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle(context
                        .getString(R.string.general_wait));
                progressDialog
                        .setMessage(getString(R.string.general_downloading));
                progressDialog.show();

            }

        }

        @Override
        protected Boolean doInBackground(ProfileData... arg0) {

            try {

                if (arg0[0].getNumPersonas() == 0) {

                    profileData = ProfileClient.resolveFullProfileDataFromProfileData(profileData);
                    if (selectedPersona == 0) {
                        selectedPersona = profileData.getPersona(0).getId();
                    }

                    // Get the unlocks
                    ProfileClient profileHandler = new ProfileClient(profileData);
                    unlocks = profileHandler.getUnlocks(sharedPreferences.getInt(
                            Constants.SP_BL_UNLOCKS_LIMIT_MIN, 1));

                } else {

                    if (selectedPersona == 0) {
                        selectedPersona = arg0[0].getPersona(0).getId();
                    }

                    // Get the unlocks
                    ProfileClient profileHandler = new ProfileClient(profileData);
                    unlocks = profileHandler.getUnlocks(sharedPreferences.getInt(
                            Constants.SP_BL_UNLOCKS_LIMIT_MIN, 1));

                }

                return (unlocks != null);

            } catch (WebsiteHandlerException ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Fail?
            if (!result) {

                if (progressDialog != null) {
                    progressDialog.dismiss();

                }
                Toast.makeText(context, R.string.general_no_data,
                        Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();

            }

            // Do actual stuff, like sending to an adapter
            int num = viewPager.getCurrentItem();
            setupList(unlocks.get(selectedPersona), num);
            if (num > 0) {
                setupList(unlocks.get(selectedPersona), num - 1);
            }
            if (num < viewPager.getChildCount()) {
                setupList(unlocks.get(selectedPersona), num + 1);
            }

            // Go go go
            if (progressDialog != null) {

                progressDialog.dismiss();

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_unlock, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public void setupList(UnlockDataWrapper data, int position) {

        ((UnlockFragment) listFragments.get(position)).showUnlocks(getItemsForFragment(position));
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
        if (unlocks == null) {

            return new ArrayList<UnlockData>();

        } else {

            // Get the UnlockDataWrapper
            UnlockDataWrapper unlockDataWrapper = unlocks.get(selectedPersona);

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
