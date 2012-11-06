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

package com.ninetwozero.battlelog.activity.profile.assignments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.gson.Gson;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.datatype.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatype.PersonaData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.jsonmodel.assignments.Assignments;
import com.ninetwozero.battlelog.jsonmodel.assignments.MissionPack;
import com.ninetwozero.battlelog.loader.Bf3Loader;
import com.ninetwozero.battlelog.loader.CompletedTask;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.provider.BusProvider;
import com.ninetwozero.battlelog.provider.UriFactory;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

import java.net.URI;
import java.util.ArrayList;

public class AssignmentActivity extends CustomFragmentActivity implements DefaultFragmentActivity, LoaderManager.LoaderCallbacks<CompletedTask> {

    // Attributes
    private ProfileData mProfileData;
    public static final String ASSIGNMENTS = "assignments";
    private final int ASSIGNMENT_CODE = 15;
    private long mSelectedPersona;
    private int mSelectedPosition;
    private long[] mPersonaId;
    private String[] mPersonaName;
    private ProgressDialog progressDialog;
    private URI callURI;
    private Assignments assignments;
    private final int[] expansionId = new int[]{512, 1024, 2048, 4096};
    public static final String EXPANSION_ID = "expansionID";
    private Bundle bundle;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        this.bundle = icicle;
        if (!getIntent().hasExtra("profile")) {
            finish();
        }
        mProfileData = getIntent().getParcelableExtra("profile");
        setContentView(R.layout.viewpager_default);
        init();
        setup();
    }

    public void init() {
        if (mProfileData.getId() == SessionKeeper.getProfileData().getId()) {
            mSelectedPersona = mSharedPreferences.getLong(Constants.SP_BL_PERSONA_CURRENT_ID,
                    mProfileData.getPersona(0).getId());
            mSelectedPosition = mSharedPreferences.getInt(Constants.SP_BL_PERSONA_CURRENT_POS, 0);
        } else {
            if (mProfileData.getNumPersonas() > 0) {
                mSelectedPersona = mProfileData.getPersona(0).getId();
            }
        }
        PersonaData pd = mProfileData.getPersonaArray()[mSelectedPosition];
        callURI = UriFactory.assignments(pd.getName(), pd.getId(), mProfileData.getId(), pd.getPlatformId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().restartLoader(ASSIGNMENT_CODE, bundle, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void setup(){
        if (mListFragments == null) {
            mListFragments = new ArrayList<Fragment>();
            for(int expansion : expansionId){
                Bundle bundle = new Bundle();
                bundle.putInt(EXPANSION_ID, expansion);
                mListFragments.add(Fragment.instantiate(this, AssignmentFragment.class.getName(), bundle));
            }

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            mPagerAdapter = new SwipeyTabsPagerAdapter(mFragmentManager,
                    new String[]{"Back to Karkand", "Premium", "Close Quarters", "Armored Kill"},
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
        startLoadingDialog();
        return new Bf3Loader(getApplicationContext(), callURI);
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> loader, CompletedTask task) {
        if (task.result.equals(CompletedTask.Result.SUCCESS)) {
            assignments = assignmentsFrom(task);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
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

    private void refresh(){
        //getSupportLoaderManager().restartLoader(ASSIGNMENT_CODE, bundle, this);
    }

    public MissionPack getMissionPack(int id){
        return assignments != null ?assignments.getMissionPacksList().get(id):null;
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
        } /*else if (item.getItemId() == R.id.option_change) {
            generateDialogPersonaList().show();
        } */else if (item.getItemId() == R.id.option_back) {
            finish();
        }
        return true;
    }

    private void startLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getApplication().getString(R.string.general_wait));
        progressDialog.setMessage(getApplication().getString(R.string.general_downloading));
        progressDialog.show();
    }

}
