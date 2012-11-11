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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.gson.Gson;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.datatype.PersonaData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.dialog.ListDialogFragment;
import com.ninetwozero.battlelog.jsonmodel.assignments.Assignments;
import com.ninetwozero.battlelog.jsonmodel.assignments.MissionPack;
import com.ninetwozero.battlelog.loader.Bf3Loader;
import com.ninetwozero.battlelog.loader.CompletedTask;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.model.SelectedPersona;
import com.ninetwozero.battlelog.provider.BusProvider;
import com.ninetwozero.battlelog.provider.UriFactory;
import com.squareup.otto.Subscribe;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_ID;
import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_POS;

public class AssignmentActivity extends CustomFragmentActivity implements LoaderManager.LoaderCallbacks<CompletedTask> {

    private ProfileData mProfileData;
    public static final String ASSIGNMENTS = "assignments";
    private final int ASSIGNMENT_CODE = 15;
    private long mSelectedPersona;
    private int mSelectedPosition;
    private ProgressDialog progressDialog;
    private URI callURI;
    private Assignments assignments;
    private final int[] expansionId = new int[]{512, 1024, 2048, 4096};
    public static final String EXPANSION_ID = "expansionID";
    private Bundle bundle;
    private final String DIALOG = "dialog";
    private PersonaData[] personaData;

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
        buildCallUri();
    }

    private void buildCallUri() {
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
        getSupportLoaderManager().restartLoader(ASSIGNMENT_CODE, bundle, this);
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
            Log.e("AssignmentActivity", "Reload pressed");
            refresh();
        } else if (item.getItemId() == R.id.option_change) {
            if (personaArrayLength() > 1) {
                ListDialogFragment dialog = ListDialogFragment.newInstance(personasToMap());
                dialog.show(mFragmentManager, DIALOG);
            }
        } else if (item.getItemId() == R.id.option_back) {
            finish();
        }
        return true;
    }

    private Map<Long, String> personasToMap() {
        personaData = SessionKeeper.getProfileData().getPersonaArray();
        Map<Long, String> map = new HashMap<Long, String>();
        for (PersonaData pd : personaData) {
            map.put(pd.getId(), pd.getName() + " " + pd.resolvePlatformId());
        }
        return map;
    }
    private int personaArrayLength() {
        return mProfileData.getPersonaArray().length;
    }

    private void startLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getApplication().getString(R.string.general_wait));
        progressDialog.setMessage(getApplication().getString(R.string.general_downloading));
        progressDialog.show();
    }

    @Subscribe
    public void personaChanged(SelectedPersona selectedPersona){
        updateSharedPreference(selectedPersona.getPersonaId());
        buildCallUri();
        refresh();
    }

    private void updateSharedPreference(long personaId) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(SP_BL_PERSONA_CURRENT_ID, personaId);
        editor.putInt(SP_BL_PERSONA_CURRENT_POS, indexOfPersona(personaId));
        editor.commit();
    }

    private int indexOfPersona(long platoonId){
        for(int i = 0; i <  personaData.length; i++){
            if(personaData[i].getId() == platoonId){
                return i;
            }
        }
        Log.w(AssignmentActivity.class.getSimpleName(), "Failed to find index of the platoon!");
        return 0;
    }
}
