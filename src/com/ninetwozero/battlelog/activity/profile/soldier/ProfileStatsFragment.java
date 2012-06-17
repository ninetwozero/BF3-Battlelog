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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.profile.unlocks.UnlockActivity;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.PersonaStats;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.dialog.ListDialogFragment;
import com.ninetwozero.battlelog.dialog.OnCloseListDialogListener;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.loader.CompletedTask;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;

import java.util.HashMap;

public class ProfileStatsFragment extends Fragment implements DefaultFragment, OnCloseListDialogListener, LoaderCallbacks<CompletedTask> {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;

    // Elements
    private RelativeLayout wrapPersona;
    private ProgressBar progressBar;

    // Misc
    private ProfileData profileData;
    private HashMap<Long, PersonaStats> personaStats;
    private long selectedPersona;
    private int selectedPosition;
    private boolean comparing;
    private String[] personaNames;
    private long[] personaIds;
    private final String DIALOG = "dialog";
    private TextView personaName, rankTitle, rankId, currentLevelPoints, nextLevelPoints, pointsToMake,
            assaultScore, engineerScore, supportScore, reconScore, vehiclesScore, combatScore, awardsScore,
            unlocksScore, totalScore, numberOfKills, numberOfAssists, vehiclesDestroyed, vehiclesDestroyedAssists,
            heals, revives, repairs, resupplies, deaths, kdRatio, numberOfWins, numberOfLosses, wnRatio,
            accuracy, killStreak, longestHeadshot, skill, timePlayed, scorePerMinute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_profile_stats,
                container, false);

        // Init
        initFragment(view);
        findViews();
        // Return
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO create loader
        getLoaderManager().initLoader(0, null, this);
    }

    public void initFragment(View view) {

        // Progressbar
        progressBar = (ProgressBar) view.findViewById(R.id.progress_level);

        // Let's try something out
        if (profileData.getId() == SessionKeeper.getProfileData().getId()) {

            selectedPersona = sharedPreferences.getLong(Constants.SP_BL_PERSONA_CURRENT_ID, 0);
            selectedPosition = sharedPreferences.getInt(Constants.SP_BL_PERSONA_CURRENT_POS, 0);

        }

        // How many personas do we have?
        int numPersonas = profileData.getNumPersonas();

        // Init the arrays
        personaIds = new long[numPersonas];
        personaNames = new String[numPersonas];

        // Iterate for the horde
        for (int count = 0; count < numPersonas; count++) {

            personaIds[count] = profileData.getPersona(count).getId();
            personaNames[count] = profileData.getPersona(count).getName();

        }

        // Click on the wrap
        wrapPersona = (RelativeLayout) view.findViewById(R.id.wrap_persona);
        wrapPersona.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View sv) {

                        if (personaArrayLength() > 1) {
                            FragmentManager manager = getFragmentManager();
                            ListDialogFragment dialog = ListDialogFragment.newInstance(profileData.getPersonaArray(), getTag());
                            dialog.show(manager, DIALOG);
                        }
                    }
                });
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
        /*if (comparing) {
            ((CompareActivity) getActivity()).sendToCompare(profileData, personaStats,selectedPersona,toggle);
        }*/
    }

    private void populateStats(PersonaStats pd){
        personaName.setText(pd.getPersonaName() + pd.resolvePlatformId());
        rankTitle.setText(pd.getRankTitle());
        rankId.setText(pd.getRankId() + "");

        // Progress
        progressBar.setMax((int) pd.getPointsNeededToLvlUp());
        progressBar.setProgress((int) pd.getPointsProgressLvl());
        currentLevelPoints.setText(pd.getPointsProgressLvl() + "");
        nextLevelPoints.setText(pd.getPointsNeededToLvlUp() + "");
        pointsToMake.setText(pd.getPointsLeft() + "");

        // Score
        assaultScore.setText(pd.getScoreAssault() + "");
        engineerScore.setText(pd.getScoreEngineer() + "");
        supportScore.setText(pd.getScoreSupport() + "");
        reconScore.setText(pd.getScoreRecon() + "");
        vehiclesScore.setText(pd.getScoreVehicles() + "");
        combatScore.setText(pd.getScoreCombat() + "");
        awardsScore.setText(pd.getScoreAwards() + "");
        unlocksScore.setText(pd.getScoreUnlocks() + "");
        totalScore.setText(pd.getScoreTotal() + "");

        // Stats
        numberOfKills.setText(pd.getNumKills() + "");
        numberOfAssists.setText(pd.getNumAssists() + "");
        vehiclesDestroyed.setText(pd.getNumVehicles() + "");
        vehiclesDestroyedAssists.setText(pd.getNumVehicleAssists() + "");
        heals.setText(pd.getNumHeals() + "");
        revives.setText(pd.getNumRevives() + "");
        repairs.setText(pd.getNumRepairs() + "");
        resupplies.setText(pd.getNumResupplies() + "");
        deaths.setText(pd.getNumDeaths() + "");
        kdRatio.setText(pd.getKDRatio() + "");
        numberOfWins.setText(pd.getNumWins() + "");
        numberOfLosses.setText(pd.getNumLosses() + "");
        wnRatio.setText(pd.getWLRatio() + "");
        accuracy.setText(pd.getAccuracy() + "%");
        killStreak.setText(pd.getLongestKS() + "");
        longestHeadshot.setText(pd.getLongestHS() + " m");
        skill.setText(pd.getSkill() + "");
        timePlayed.setText(pd.getTimePlayedString() + "");
        scorePerMinute.setText(pd.getScorePerMinute() + "");
    }

    private int personaArrayLength() {
        return profileData.getPersonaArray().length;
    }

    @Override
    public Loader<CompletedTask> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> completedTaskLoader, CompletedTask completedTask) {
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }

    public class AsyncCache extends AsyncTask<Void, Void, Boolean> {

        // Attributes

        public AsyncCache() {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {

                // Get...
                if (profileData != null && profileData.getNumPersonas() > 0) {

                    personaStats = CacheHandler.Persona.select(context,
                            profileData.getPersonaArray());

                    // Is this the user?
                    selectedPersona = profileData.getPersona(selectedPosition).getId();

                } else {

                    personaStats = null;

                }

                // ...validate!
                return (personaStats != null && personaStats.size() > 0);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {

                // Siiiiiiiiilent refresh
                //populateStats(personaStats.get(selectedPersona));
                new AsyncRefresh().execute();

            } else {

                new AsyncRefresh().execute();

            }

        }

    }

    public class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {

        public AsyncRefresh() {

        }

        @Override
        protected void onPreExecute() {

            /* LOADER LIKE IN FORUMS? */
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {

                Log.d(Constants.DEBUG_TAG, "profile => " + profileData);
                // Do we have any personas?
                if (profileData.getNumPersonas() > 0) {

                    // Set the selected persona?
                    selectedPersona = (selectedPersona == 0) ? profileData.getPersona(0).getId() : selectedPersona;

                    // Grab the stats
                    personaStats = new ProfileClient(profileData).getStats(context);

                }
                // ...validate!
                return (personaStats != null && personaStats.size() > 0);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Fail?
            if (!result) {

                Toast.makeText(context, R.string.general_no_data,
                        Toast.LENGTH_SHORT).show();

            } else {

                populateStats(personaStats.get(selectedPersona));

            }

        }

    }

    public void reload() {

        // ASYNC!!!
        if (personaStats == null) {

            new AsyncCache().execute();

        } else {

            new AsyncRefresh().execute();

        }

    }

    public void setProfileData(ProfileData p) {

        profileData = p;

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

            startActivity(
                    new Intent(context, CompareActivity.class)
                            .putExtra("profile1", SessionKeeper.getProfileData())
                            .putExtra("profile2", profileData)
                            .putExtra("selectedPosition", selectedPosition)
            );

        } else if (item.getItemId() == R.id.option_unlocks) {

            int position = 0;
            for (long key : personaStats.keySet()) {

                if (key == selectedPersona) {

                    break;

                } else {

                    position++;

                }

            }

            startActivity(

                    new Intent(context, UnlockActivity.class)
                            .putExtra("profile", profileData)
                            .putExtra("selectedPosition", position)
            );
        }
        return true;
    }

    public void setComparing(boolean c) {
        comparing = c;
    }
}
