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
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ninetwozero.battlelog.datatypes.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.UnlockData;
import com.ninetwozero.battlelog.datatypes.UnlockDataWrapper;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.fragments.UnlockFragment;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class UnlockView extends FragmentActivity implements DefaultFragmentActivity {

    // Attributes
    private final Context CONTEXT = this;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    private ProfileData profileData;
    private HashMap<Long, UnlockDataWrapper> unlocks;
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

        // Init to winit
        unlocks = new HashMap<Long, UnlockDataWrapper>();

        // Set the selected persona
        selectedPersona = profileData.getPersonaId();

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

    public Dialog generateDialogPersonaList(final Context context,
            final long[] personaId, final String[] persona, final long[] ls) {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and the view
        builder.setTitle(R.string.info_dialog_soldierselect);
        String[] listNames = new String[personaId.length];

        for (int i = 0, max = personaId.length; i < max; i++) {

            listNames[i] = persona[i] + " "
                    + DataBank.resolvePlatformId((int) ls[i]);

        }
        builder.setSingleChoiceItems(

                listNames, selectedPosition, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        if (personaId[item] != selectedPersona) {

                            // Update it
                            selectedPersona = profileData.getPersonaId(item);

                            // Store selected position
                            selectedPosition = item;

                            // Load the new!
                            setupList(unlocks.get(selectedPersona), viewPager.getCurrentItem());

                        }

                        dialog.dismiss();

                    }

                }

                );

        // CREATE
        return builder.create();

    }

    public void setupFragments() {

        // Do we need to setup the fragments?
        if (listFragments == null) {

            // Add them to the list
            listFragments = new Vector<Fragment>();
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
        Context context;
        ProgressDialog progressDialog;

        public AsyncGetDataSelf(Context c) {

            this.context = c;
            this.progressDialog = null;

        }

        @Override
        protected void onPreExecute() {

            // Let's see
            if (unlocks == null) {

                this.progressDialog = new ProgressDialog(this.context);
                this.progressDialog.setTitle(context
                        .getString(R.string.general_wait));
                this.progressDialog
                        .setMessage(getString(R.string.general_downloading));
                this.progressDialog.show();

            }

        }

        @Override
        protected Boolean doInBackground(ProfileData... arg0) {

            try {

                if (arg0[0].getPersonaId() == 0) {

                    profileData = WebsiteHandler
                            .getPersonaIdFromProfile(profileData);
                    if (selectedPersona == 0) {
                        selectedPersona = profileData.getPersonaId();
                    }
                    unlocks = WebsiteHandler.getUnlocksForUser(profileData);

                } else {

                    if (selectedPersona == 0) {
                        selectedPersona = arg0[0].getPersonaId();
                    }
                    unlocks = WebsiteHandler.getUnlocksForUser(arg0[0]);

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

                if (this.progressDialog != null)
                    this.progressDialog.dismiss();
                Toast.makeText(this.context, R.string.general_no_data,
                        Toast.LENGTH_SHORT).show();
                ((Activity) this.context).finish();
                return;

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
            if (this.progressDialog != null)
                this.progressDialog.dismiss();
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_unlock, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public void setupList(UnlockDataWrapper data, int position) {

        ((UnlockFragment) listFragments.get(position)).showUnlocks(getUnlocksForFragment(position));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Let's act!
        if (item.getItemId() == R.id.option_reload) {

            this.reload();

        } else if (item.getItemId() == R.id.option_change) {

            generateDialogPersonaList(

                    this, profileData.getPersonaIdArray(),
                    profileData.getPersonaNameArray(),
                    profileData.getPlatformIdArray()

            ).show();

        } else if (item.getItemId() == R.id.option_back) {

            ((Activity) this).finish();

        }

        // Return true yo
        return true;

    }

    public List<UnlockData> getUnlocksForFragment(int p) {

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
