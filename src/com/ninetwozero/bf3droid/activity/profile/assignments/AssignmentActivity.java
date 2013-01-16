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

package com.ninetwozero.bf3droid.activity.profile.assignments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.dialog.ListDialogFragment;
import com.ninetwozero.bf3droid.jsonmodel.assignments.Assignments;
import com.ninetwozero.bf3droid.jsonmodel.assignments.MissionPack;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.model.SelectedOption;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;
import com.ninetwozero.bf3droid.util.Platform;
import com.squareup.otto.Subscribe;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

import org.apache.http.client.methods.HttpGet;

public class AssignmentActivity extends CustomFragmentActivity implements LoaderManager.LoaderCallbacks<CompletedTask> {

    public static final String ASSIGNMENTS = "assignments";
    private final int ASSIGNMENT_CODE = 15;
    private Assignments assignments;
    private final int[] expansionId = new int[]{512, 1024, 2048, 4096, 8192};
    public static final String EXPANSION_ID = "expansionID";
    private Bundle bundle;
    private final String DIALOG = "AssignmentActivity";

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        this.bundle = icicle;
        if (!getIntent().hasExtra("profile")) {
            finish();
        }
        setContentView(R.layout.viewpager_default);
        setup();
    }

    private URI buildCallUri() {
        return UriFactory.assignments(username(), personaId(), userId(), platformId());
    }

    private String username() {
        return BF3Droid.getUser();
    }

    private long personaId() {
        return selectedPersona().getPersonaId();
    }

    private long userId() {
        return BF3Droid.getUserId();
    }

    private int platformId() {
        return Platform.resolveIdFromPlatformName(selectedPersona().getPlatform());
    }

    private SimplePersona selectedPersona() {
        return BF3Droid.selectedUserPersona();
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    public void setup() {
        if (mListFragments == null) {
            mListFragments = new ArrayList<Fragment>();
            for (int expansion : expansionId) {
                Bundle bundle = new Bundle();
                bundle.putInt(EXPANSION_ID, expansion);
                mListFragments.add(Fragment.instantiate(this, AssignmentFragment.class.getName(), bundle));
            }

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            mPagerAdapter = new SwipeyTabsPagerAdapter(mFragmentManager,
                    new String[]{"Back to Karkand", "Premium", "Close Quarters", "Armored Kill", "Aftermath"},
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

    @Override
    public Loader<CompletedTask> onCreateLoader(int i, Bundle bundle) {
        startLoadingDialog(DIALOG);
        return new Bf3Loader(getApplicationContext(), new Bf3ServerCall.HttpData(buildCallUri(), HttpGet.METHOD_NAME));
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> loader, CompletedTask task) {
        if (task.result.equals(CompletedTask.Result.SUCCESS)) {
            assignments = assignmentsFrom(task);
            closeLoadingDialog(DIALOG);
            BusProvider.getInstance().post(ASSIGNMENTS);
        }
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> loader) {
    }

    private Assignments assignmentsFrom(CompletedTask task) {
        Gson gson = new Gson();
        return gson.fromJson(task.jsonObject, Assignments.class);
    }

    public void reload() {

    }

    private void refresh() {
        getSupportLoaderManager().restartLoader(ASSIGNMENT_CODE, bundle, this);
    }

    public MissionPack getMissionPack(int id) {
        return assignments != null ? assignments.getMissionPacksList().get(id) : null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_unlock, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_reload) {
            refresh();
        } else if (item.getItemId() == R.id.option_change) {
            if (BF3Droid.getGuestPersonas().size() > 1) {
                ListDialogFragment dialog = ListDialogFragment.newInstance(personasToMap(), SelectedOption.PERSONA);
                dialog.show(mFragmentManager, DIALOG);
            }
        } else if (item.getItemId() == R.id.option_back) {
            finish();
        }
        return true;
    }

    private Map<Long, String> personasToMap() {
        Map<Long, String> map = new HashMap<Long, String>();
        for (SimplePersona persona : BF3Droid.getUserPersonas()) {
            map.put(persona.getPersonaId(), persona.getPersonaName() + " [" + persona.getPlatform()+"]");
        }
        return map;
    }

    @Subscribe
    public void personaChanged(SelectedOption selectedOption) {
        if (selectedOption.getChangedGroup().equals(SelectedOption.PERSONA)) {
            BF3Droid.setSelectedUserPersona(selectedOption.getSelectedId());
            buildCallUri();
            refresh();
        }
    }
}
