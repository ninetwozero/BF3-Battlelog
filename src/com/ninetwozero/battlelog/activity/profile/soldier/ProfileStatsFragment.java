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

package com.ninetwozero.battlelog.activity.profile.soldier;

import java.net.URI;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.Bf3Fragment;
import com.ninetwozero.battlelog.activity.profile.unlocks.UnlockActivity;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.PersonaStats;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.dialog.ListDialogFragment;
import com.ninetwozero.battlelog.dialog.OnCloseListDialogListener;
import com.ninetwozero.battlelog.factory.URIFactory;
import com.ninetwozero.battlelog.jsonmodel.PersonaInfo;
import com.ninetwozero.battlelog.loader.Bf3Loader;
import com.ninetwozero.battlelog.loader.CompletedTask;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class ProfileStatsFragment extends Bf3Fragment implements DefaultFragment,
        OnCloseListDialogListener {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SharedPreferences mSharedPreferences;

    // Elements
    private RelativeLayout mWrapPersona;
    private ProgressBar mProgressBar;

    // Misc
    private ProfileData mProfileData;
    private Map<Long, PersonaStats> mPersonaStats;
    private long mSelectedPersona;
    private int mSelectedPosition;
    private int mSelectedPlatformId;
    private String mSelectedPersonaName;
    private boolean mComparing;

    private URI callURI;
    private final String DIALOG = "dialog";
    private TextView personaName, rankTitle, rankId, currentLevelPoints, nextLevelPoints,
            pointsToMake, assaultScore, engineerScore, supportScore, reconScore,
            vehiclesScore, combatScore, awardsScore, unlocksScore, totalScore, numberOfKills,
            numberOfAssists, vehiclesDestroyed, vehiclesDestroyedAssists,
            heals, revives, repairs, resupplies, deaths, kdRatio, numberOfWins, numberOfLosses,
            wnRatio, accuracy, killStreak, longestHeadshot, skill, timePlayed, scorePerMinute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.tab_content_profile_stats,
                container, false);

        // Init
        initFragment(view);

        // Return
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO create loader
        getLoaderManager().initLoader(0, savedInstanceState, this);
    }

    public void initFragment(View view) {

        // Progressbar
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_level);

        // Let's try something out
        if (mProfileData.getId() == SessionKeeper.getProfileData().getId()) {
            mSelectedPosition = mSharedPreferences.getInt(Constants.SP_BL_PERSONA_CURRENT_POS, 0);
            mSelectedPersona = getSelectedPersonaId(mSelectedPosition);
            mSelectedPlatformId = getPlatformIdFor(mSelectedPosition);
            mSelectedPersonaName = getSelectedPersonaName(mSelectedPosition);
            callURI = URIFactory.personaOverview(mSelectedPersona, mSelectedPlatformId);
        }

        // Click on the wrap
        mWrapPersona = (RelativeLayout) view.findViewById(R.id.wrap_persona);
        mWrapPersona.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View sv) {

                        if (personaArrayLength() > 1) {
                            FragmentManager manager = getFragmentManager();
                            ListDialogFragment dialog = ListDialogFragment.newInstance(
                                    mProfileData.getPersonaArray(), getTag());
                            dialog.show(manager, DIALOG);
                        }
                    }
                });
    }

    private long getSelectedPersonaId(int position) {
        String idsString = mSharedPreferences.getString(Constants.SP_BL_PERSONA_ID, "");
        String[] ids = idsString.split(":");
        return Long.parseLong(ids[position]);
    }

    private int getPlatformIdFor(int position) {
        String idsString = mSharedPreferences.getString(Constants.SP_BL_PLATFORM_ID, "");
        String[] ids = idsString.split(":");
        return Integer.parseInt(ids[position]);
    }

    private String getSelectedPersonaName(int position) {
        String names = mSharedPreferences.getString(Constants.SP_BL_PERSONA_NAME, "");
        String[] namesArray = names.split(":");
        return namesArray[position];
    }

    @Override
    public void onDialogListSelection() {
        Log.e("ProfileStatsFragment", "I AM BACK ! ! !");
    }

    public void findViews() {

        // Let's find it
        View view = getView();
        if (view == null) {

            return;
        }

        if (personaArrayLength() == 1) {
            ((ImageView) view.findViewById(R.id.img_persona_list)).setVisibility(View.INVISIBLE);
        }

        // Persona & rank
        personaName = (TextView) view.findViewById(R.id.string_persona);
        rankTitle = (TextView) view.findViewById(R.id.string_rank_title);
        rankId = (TextView) view.findViewById(R.id.string_rank_short);

        // Progress
        currentLevelPoints = (TextView) view.findViewById(R.id.string_progress_curr);
        nextLevelPoints = (TextView) view.findViewById(R.id.string_progress_max);
        pointsToMake = (TextView) view.findViewById(R.id.string_progress_left);

        // Score
        assaultScore = (TextView) view.findViewById(R.id.string_score_assault);
        engineerScore = (TextView) view.findViewById(R.id.string_score_engineer);
        supportScore = (TextView) view.findViewById(R.id.string_score_support);
        reconScore = (TextView) view.findViewById(R.id.string_score_recon);
        vehiclesScore = (TextView) view.findViewById(R.id.string_score_vehicles);
        combatScore = (TextView) view.findViewById(R.id.string_score_combat);
        awardsScore = (TextView) view.findViewById(R.id.string_score_award);
        unlocksScore = (TextView) view.findViewById(R.id.string_score_unlock);
        totalScore = (TextView) view.findViewById(R.id.string_score_total);

        // Stats
        numberOfKills = (TextView) view.findViewById(R.id.string_stats_kills);
        numberOfAssists = (TextView) view.findViewById(R.id.string_stats_assists);
        vehiclesDestroyed = (TextView) view.findViewById(R.id.string_stats_vkills);
        vehiclesDestroyedAssists = (TextView) view.findViewById(R.id.string_stats_vassists);
        heals = (TextView) view.findViewById(R.id.string_stats_heals);
        revives = (TextView) view.findViewById(R.id.string_stats_revives);
        repairs = (TextView) view.findViewById(R.id.string_stats_repairs);
        resupplies = (TextView) view.findViewById(R.id.string_stats_resupplies);
        deaths = (TextView) view.findViewById(R.id.string_stats_deaths);
        kdRatio = (TextView) view.findViewById(R.id.string_stats_kdr);
        numberOfWins = (TextView) view.findViewById(R.id.string_stats_wins);
        numberOfLosses = (TextView) view.findViewById(R.id.string_stats_losses);
        wnRatio = (TextView) view.findViewById(R.id.string_stats_wlr);
        accuracy = (TextView) view.findViewById(R.id.string_stats_accuracy);
        killStreak = (TextView) view.findViewById(R.id.string_stats_lks);
        longestHeadshot = (TextView) view.findViewById(R.id.string_stats_lhs);
        skill = (TextView) view.findViewById(R.id.string_stats_skill);
        timePlayed = (TextView) view.findViewById(R.id.string_stats_time);
        scorePerMinute = (TextView) view.findViewById(R.id.string_stats_spm);

        // Are we going to compare?
        /*
         * if (comparing) { ((CompareActivity)
         * getActivity()).sendToCompare(profileData,
         * personaStats,selectedPersona,toggle); }
         */
    }

    private void populateStats(PersonaStats pd) {

        if (pd == null) {
            return;
        }

        personaName.setText(mSelectedPersonaName + " " + pd.resolvePlatformId());
        rankTitle.setText(fromResource((int) pd.getRankId()));
        rankId.setText(String.valueOf(pd.getRankId()));

        // Progress
        mProgressBar.setMax((int) pd.getPointsNeededToLvlUp());
        mProgressBar.setProgress((int) pd.getPointsProgressLvl());
        currentLevelPoints.setText(String.valueOf(pd.getPointsProgressLvl()));
        nextLevelPoints.setText(String.valueOf(pd.getPointsNeededToLvlUp()));
        pointsToMake.setText(String.valueOf(pd.getPointsLeft()));

        // Score
        assaultScore.setText(String.valueOf(pd.getScoreAssault()));
        engineerScore.setText(String.valueOf(pd.getScoreEngineer()));
        supportScore.setText(String.valueOf(pd.getScoreSupport()));
        reconScore.setText(String.valueOf(pd.getScoreRecon()));
        vehiclesScore.setText(String.valueOf(pd.getScoreVehicles()));
        combatScore.setText(String.valueOf(pd.getScoreCombat()));
        awardsScore.setText(String.valueOf(pd.getScoreAwards()));
        unlocksScore.setText(String.valueOf(pd.getScoreUnlocks()));
        totalScore.setText(String.valueOf(pd.getScoreTotal()));

        // Stats
        numberOfKills.setText(String.valueOf(pd.getNumKills()));
        numberOfAssists.setText(String.valueOf(pd.getNumAssists()));
        vehiclesDestroyed.setText(String.valueOf(pd.getNumVehicles()));
        vehiclesDestroyedAssists.setText(String.valueOf(pd.getNumVehicleAssists()));
        heals.setText(String.valueOf(pd.getNumHeals()));
        revives.setText(String.valueOf(pd.getNumRevives()));
        repairs.setText(String.valueOf(pd.getNumRepairs()));
        resupplies.setText(String.valueOf(pd.getNumResupplies()));
        deaths.setText(String.valueOf(pd.getNumDeaths()));
        kdRatio.setText(String.valueOf(pd.getKDRatio()));
        numberOfWins.setText(String.valueOf(pd.getNumWins()));
        numberOfLosses.setText(String.valueOf(pd.getNumLosses()));
        wnRatio.setText(String.valueOf(pd.getWLRatio()));
        accuracy.setText(pd.getAccuracy() + "%");
        killStreak.setText(String.valueOf(pd.getLongestKS()));
        longestHeadshot.setText(pd.getLongestHS() + " m");
        skill.setText(String.valueOf(pd.getSkill()));
        timePlayed.setText(String.valueOf(pd.getTimePlayedString()));
        scorePerMinute.setText(String.valueOf(pd.getScorePerMinute()));
    }

    private int personaArrayLength() {
        return mProfileData.getPersonaArray().length;
    }

    @Override
    protected Loader<CompletedTask> createLoader(int id, Bundle bundle) {
        return new Bf3Loader(getContext(), callURI);
    }

    private Context getContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void loadFinished(Loader<CompletedTask> loader, CompletedTask task) {
        if (task.result.equals(CompletedTask.Result.SUCCESS)) {
            findViews();
            populateStats(personaStatsFrom(task));
        }
    }

    private PersonaStats personaStatsFrom(CompletedTask task) {
        Gson gson = new Gson();
        PersonaInfo data = gson.fromJson(task.jsonObject, PersonaInfo.class);
        return new PersonaStats(data);
    }

    private String fromResource(int rank) {
        return getResources().getStringArray(R.array.rank)[rank];
    }

    /*
     * public class AsyncCache extends AsyncTask<Void, Void, Boolean> { //
     * Attributes public AsyncCache() { }
     * @Override protected void onPreExecute() { }
     * @Override protected Boolean doInBackground(Void... arg0) { try { //
     * Get... if (mProfileData != null && mProfileData.getNumPersonas() > 0) {
     * mPersonaStats = CacheHandler.Persona.select(mContext,
     * mProfileData.getPersonaArray()); // Is this the user? mSelectedPersona =
     * mProfileData.getPersona(mSelectedPosition).getId(); } // ...validate!
     * return (mPersonaStats != null && !mPersonaStats.isEmpty()); } catch
     * (Exception ex) { ex.printStackTrace(); return false; } }
     * @Override protected void onPostExecute(Boolean result) { if (result) { //
     * Siiiiiiiiilent refresh //
     * populateStats(personaStats.get(selectedPersona)); new
     * AsyncRefresh().execute(); } else { new AsyncRefresh().execute(); } } }
     */

    /*
     * public class AsyncRefresh extends AsyncTask<Void, Void, Boolean> { public
     * AsyncRefresh() { }
     * @Override protected void onPreExecute() {
     *//* LOADER LIKE IN FORUMS? *//*
                                    * }
                                    * @Override protected Boolean
                                    * doInBackground(Void... arg0) { try {
                                    * Log.d(Constants.DEBUG_TAG, "profile => " +
                                    * mProfileData); // Do we have any personas?
                                    * if (mProfileData.getNumPersonas() > 0) {
                                    * // Set the selected persona?
                                    * mSelectedPersona = (mSelectedPersona == 0)
                                    * ? mProfileData.getPersona(0).getId() :
                                    * mSelectedPersona; // Grab the stats
                                    * mPersonaStats = new
                                    * ProfileClient(mProfileData
                                    * ).getStats(mContext); } // ...validate!
                                    * return (mPersonaStats != null &&
                                    * !mPersonaStats.isEmpty()); } catch
                                    * (Exception ex) { ex.printStackTrace();
                                    * return false; } }
                                    * @Override protected void
                                    * onPostExecute(Boolean result) { // Fail?
                                    * if (result) {
                                    * populateStats(mPersonaStats.get
                                    * (mSelectedPersona)); } else {
                                    * Toast.makeText(mContext,
                                    * R.string.general_no_data,
                                    * Toast.LENGTH_SHORT).show(); } } }
                                    */

    public void reload() {

        /*
         * // ASYNC!!! if (mPersonaStats == null) { new AsyncCache().execute();
         * } else { new AsyncRefresh().execute(); }
         */

    }

    public void setProfileData(ProfileData p) {
        mProfileData = p;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public Menu prepareOptionsMenu(Menu menu) {

        ((MenuItem) menu.findItem(R.id.option_friendadd))
                .setVisible(false);
        ((MenuItem) menu.findItem(R.id.option_frienddel))
                .setVisible(false);
        ((MenuItem) menu.findItem(R.id.option_compare))
                .setVisible(true);
        ((MenuItem) menu.findItem(R.id.option_unlocks))
                .setVisible(true);
        return menu;

    }

    public boolean handleSelectedOption(MenuItem item) {

        if (item.getItemId() == R.id.option_compare) {

            startActivity(new Intent(mContext, CompareActivity.class)
                    .putExtra("profile1", SessionKeeper.getProfileData())
                    .putExtra("profile2", mProfileData)
                    .putExtra("selectedPosition", mSelectedPosition));

        } else if (item.getItemId() == R.id.option_unlocks) {

            int position = 0;
            for (long key : mPersonaStats.keySet()) {

                if (key == mSelectedPersona) {
                    break;
                } else {
                    position++;
                }
            }

            startActivity(

            new Intent(mContext, UnlockActivity.class)
                    .putExtra("profile", mProfileData)
                    .putExtra("selectedPosition", position));
        }
        return true;
    }

    public void setComparing(boolean c) {
        mComparing = c;
    }

}
