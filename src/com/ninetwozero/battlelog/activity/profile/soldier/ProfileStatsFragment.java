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
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.*;

import com.google.gson.Gson;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.Bf3Fragment;
import com.ninetwozero.battlelog.activity.profile.unlocks.UnlockActivity;
import com.ninetwozero.battlelog.dao.PersonaStatisticsDAO;
import com.ninetwozero.battlelog.dao.RankProgressDAO;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.PersonaStats;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.dialog.ListDialogFragment;
import com.ninetwozero.battlelog.dialog.OnCloseListDialogListener;
import com.ninetwozero.battlelog.factory.UriFactory;
import com.ninetwozero.battlelog.jsonmodel.PersonaInfo;
import com.ninetwozero.battlelog.loader.Bf3Loader;
import com.ninetwozero.battlelog.loader.CompletedTask;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.provider.table.PersonaStatistics;
import com.ninetwozero.battlelog.provider.table.RankProgress;

import static com.ninetwozero.battlelog.dao.RankProgressDAO.*;
import static com.ninetwozero.battlelog.dao.PersonaStatisticsDAO.*;

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

    private PersonaStats ps;
    private Bundle bundle;
    private ProgressDialog progressDialog;
    private boolean hasDBData = false;
    private RankProgress rankProgress;
    private ListView personaStatisticsListView;
    private List<PersonaStatistics> listPersonaStatistics;

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
        // TODO create loader
        Cursor cursor = getContext().getContentResolver()
                .query(RankProgress.URI, RankProgress.RANK_PROGRESS_PROJECTION,
                        RankProgress.Columns.PERSONA_ID + "=?", new String[]{String.valueOf(mSelectedPersona)}, null);
        if(cursor.getCount()> 0){
            cursor.moveToFirst();
            rankProgress = RankProgressDAO.fromCursor(cursor);
            findViews();
            hasDBData = true;
        } else{
            getLoaderManager().initLoader(0, bundle, this);
        }
        // Return
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.bundle = savedInstanceState;
        if(hasDBData){
            findViews();
            populateStats();
        }
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
            callURI = UriFactory.personaOverview(mSelectedPersona, mSelectedPlatformId);
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

        personaStatisticsListView = (ListView) view.findViewById(R.id.persona_statistics);
        /*// Score
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
        scorePerMinute = (TextView) view.findViewById(R.id.string_stats_spm);*/

        // Are we going to compare?
        /*
         * if (comparing) { ((CompareActivity)
         * getActivity()).sendToCompare(profileData,
         * personaStats,selectedPersona,toggle); }
         */
    }

    private void populateStats() {

        if (rankProgress == null) {
            return;
        }
        Log.e("STATS", "Populating view");
        personaName.setText(rankProgress.getPersonaName() + " " + rankProgress.getPlatform());
        rankTitle.setText(fromResource(rankProgress.getRank()));
        rankId.setText(String.valueOf(rankProgress.getRank()));

        // Progress
        mProgressBar.setMax((int)(rankProgress.getNextRankScore() - rankProgress.getCurrentRankScore()));
        mProgressBar.setProgress((int) (rankProgress.getScore() - rankProgress.getCurrentRankScore()));
        currentLevelPoints.setText(String.valueOf(rankProgress.getScore() - rankProgress.getCurrentRankScore()));
        nextLevelPoints.setText(String.valueOf(rankProgress.getNextRankScore() - rankProgress.getCurrentRankScore()));
        pointsToMake.setText(String.valueOf(rankProgress.getNextRankScore() - rankProgress.getScore()));
        PersonaStatisticsAdapter psa = new PersonaStatisticsAdapter(getContext(), R.layout.list_item_persona_statistics, listPersonaStatistics);
        personaStatisticsListView.setAdapter(psa);
        /*personaName.setText(mSelectedPersonaName + " " + ps.resolvePlatformId());
        rankTitle.setText(fromResource((int) ps.getRankId()));
        rankId.setText(String.valueOf(ps.getRankId()));

        // Progress
        mProgressBar.setMax((int) ps.getPointsNeededToLvlUp());
        mProgressBar.setProgress((int) ps.getPointsProgressLvl());
        currentLevelPoints.setText(String.valueOf(ps.getPointsProgressLvl()));
        nextLevelPoints.setText(String.valueOf(ps.getPointsNeededToLvlUp()));
        pointsToMake.setText(String.valueOf(ps.getPointsLeft()));*/

        /*// Score
        assaultScore.setText(String.valueOf(ps.getScoreAssault()));
        engineerScore.setText(String.valueOf(ps.getScoreEngineer()));
        supportScore.setText(String.valueOf(ps.getScoreSupport()));
        reconScore.setText(String.valueOf(ps.getScoreRecon()));
        vehiclesScore.setText(String.valueOf(ps.getScoreVehicles()));
        combatScore.setText(String.valueOf(ps.getScoreCombat()));
        awardsScore.setText(String.valueOf(ps.getScoreAwards()));
        unlocksScore.setText(String.valueOf(ps.getScoreUnlocks()));
        totalScore.setText(String.valueOf(ps.getScoreTotal()));

        // Stats
        numberOfKills.setText(String.valueOf(ps.getNumKills()));
        numberOfAssists.setText(String.valueOf(ps.getNumAssists()));
        vehiclesDestroyed.setText(String.valueOf(ps.getNumVehicles()));
        vehiclesDestroyedAssists.setText(String.valueOf(ps.getNumVehicleAssists()));
        heals.setText(String.valueOf(ps.getNumHeals()));
        revives.setText(String.valueOf(ps.getNumRevives()));
        repairs.setText(String.valueOf(ps.getNumRepairs()));
        resupplies.setText(String.valueOf(ps.getNumResupplies()));
        deaths.setText(String.valueOf(ps.getNumDeaths()));
        kdRatio.setText(String.valueOf(ps.getKDRatio()));
        numberOfWins.setText(String.valueOf(ps.getNumWins()));
        numberOfLosses.setText(String.valueOf(ps.getNumLosses()));
        wnRatio.setText(String.valueOf(ps.getWLRatio()));
        accuracy.setText(ps.getAccuracy() + "%");
        killStreak.setText(String.valueOf(ps.getLongestKS()));
        longestHeadshot.setText(ps.getLongestHS() + " m");
        skill.setText(String.valueOf(ps.getSkill()));
        timePlayed.setText(String.valueOf(ps.getTimePlayedString()));
        scorePerMinute.setText(String.valueOf(ps.getScorePerMinute()));*/
    }

    private int personaArrayLength() {
        return mProfileData.getPersonaArray().length;
    }

    @Override
    protected Loader<CompletedTask> createLoader(int id, Bundle bundle) {
        startLoadingDialog();
        return new Bf3Loader(getContext(), callURI);
    }

    private Context getContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void loadFinished(Loader<CompletedTask> loader, CompletedTask task) {
        if (task.result.equals(CompletedTask.Result.SUCCESS)) {  Log.e("STATS", "Load finished");
            findViews();
            PersonaInfo pi = personaStatsFrom(task);
            updateDatabase(pi);
            if(progressDialog != null){
                progressDialog.dismiss();
            }
            populateStats();
        }
    }

    private PersonaInfo personaStatsFrom(CompletedTask task) {
        Gson gson = new Gson();
        PersonaInfo data = gson.fromJson(task.jsonObject, PersonaInfo.class);
        return data;
    }

    private String fromResource(int rank) {
        return getResources().getStringArray(R.array.rank)[rank];
    }

    private void updateDatabase(PersonaInfo pi){
        updateRankProgressDB(pi);
        updatePersonaStats(pi);
    }

    private void updateRankProgressDB(PersonaInfo pi){
        rankProgress = rankProgressFromJSON(pi);     Log.e("STATS", "Populated rankProgress");
        getContext().getContentResolver().insert(RankProgress.URI, rankProgressForDB(pi, mSelectedPersona));
    }

    private void updatePersonaStats(PersonaInfo pi){
        listPersonaStatistics = PersonaStatisticsDAO.personaStatisticsFromJSON(pi);      Log.e("STATS", "Populated personaStatistics");
        getContext().getContentResolver().insert(PersonaStatistics.URI, personaStatisticsForDB(pi, mSelectedPersona));
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

    @Override
    public void reload() {}

    private void startLoadingDialog(){   //TODO extract multiple duplicates of same code
        this.progressDialog = new ProgressDialog(mContext);
        this.progressDialog.setTitle(mContext
                .getString(R.string.general_wait));
        this.progressDialog.setMessage(mContext
                .getString(R.string.general_downloading));
        this.progressDialog.show();
    }

    public class PersonaStatisticsAdapter extends ArrayAdapter<PersonaStatistics>{
        private final Context context;
        private final List<PersonaStatistics> statistics;

        public PersonaStatisticsAdapter(Context context, int textViewResourceId, List<PersonaStatistics> statistics) {
            super(context, textViewResourceId, statistics);
            this.context = context;
            this.statistics = statistics;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_persona_statistics, null);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.persona_statistics_title);
                holder.value = (TextView) convertView.findViewById(R.id.persona_statistics_value);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(context.getResources().getString(statistics.get(position).getTitle()));
            holder.value.setText(statistics.get(position).getValue());
            return convertView;
        }

        private class ViewHolder{
            TextView title;
            TextView value;
        }
    }
}
