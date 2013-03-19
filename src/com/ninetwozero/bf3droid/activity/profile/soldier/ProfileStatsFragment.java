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

package com.ninetwozero.bf3droid.activity.profile.soldier;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3Fragment;
import com.ninetwozero.bf3droid.activity.profile.unlocks.UnlockActivity;
import com.ninetwozero.bf3droid.dao.PersonaStatisticsDAO;
import com.ninetwozero.bf3droid.dao.RankProgressDAO;
import com.ninetwozero.bf3droid.dao.ScoreStatisticsDAO;
import com.ninetwozero.bf3droid.datatype.*;
import com.ninetwozero.bf3droid.dialog.ListDialogFragment;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.PersonaInfo;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.misc.SessionKeeper;
import com.ninetwozero.bf3droid.model.SelectedOption;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.provider.table.PersonaStatistics;
import com.ninetwozero.bf3droid.provider.table.RankProgress;
import com.ninetwozero.bf3droid.provider.table.ScoreStatistics;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;
import com.ninetwozero.bf3droid.util.Platform;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;

import static com.ninetwozero.bf3droid.dao.PersonaStatisticsDAO.personaStatisticsFromJSON;
import static com.ninetwozero.bf3droid.dao.RankProgressDAO.rankProgressFromJSON;
import static com.ninetwozero.bf3droid.dao.ScoreStatisticsDAO.scoreStatisticsFromJSON;
import static com.ninetwozero.bf3droid.misc.NumberFormatter.format;

public class ProfileStatsFragment extends Bf3Fragment {
    private Context context;
    private LayoutInflater layoutInflater;

    private ProgressBar progressBar;
    private TextView personaName;
    private TextView rankTitle;
    private TextView rankId;
    private TextView currentLevelPoints;
    private TextView nextLevelPoints;
    private TextView pointsToMake;

    private Map<Long, PersonaStats> personaStats;
    private boolean comparing;
    private final String DIALOG = "dialog";

    private Bundle bundle;
    private RankProgress rankProgress;
    private TableLayout personaStatisticsTable;
    private TableLayout scoreStatisticsTable;
    private List<Statistics> listPersonaStatistics;
    private List<Statistics> listScoreStatistics;
    private PersonaInfo personaInfo;
    private static final int LOADER_STATS = 23;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        layoutInflater = inflater;

        View view = layoutInflater.inflate(R.layout.tab_content_profile_stats, container, false);
        initFragment(view);
        return view;
    }

    public void initFragment(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress_level);

        RelativeLayout wrapPersona = (RelativeLayout) view.findViewById(R.id.wrap_persona);
        wrapPersona.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View sv) {
                        if (userPersonasCount() > 1) {
                            FragmentManager manager = getFragmentManager();
                            ListDialogFragment dialog = ListDialogFragment.newInstance(personasToMap(), SelectedOption.PERSONA);
                            dialog.show(manager, DIALOG);
                        }
                    }
                });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.bundle = savedInstanceState;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        findViews();
        getData();
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    private void getData() {
        if (dbHasData()) {
            populateView();
        } else {
            restartLoader();
        }
    }

    @Subscribe
    public void selectionChanged(SelectedOption selectedOption) {
        if (selectedOption.getChangedGroup().equals(SelectedOption.PERSONA)) {
            user().selectPersona(selectedOption.getSelectedId());
            getData();
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_STATS, bundle, this);
    }

    private boolean dbHasData() {
        return hasRankData() && hasPersonaStatistics() && hasScoreStatistics();
    }

    private boolean hasRankData() {
        Cursor cursor = getContext().getContentResolver().query(
                RankProgress.URI,
                RankProgress.RANK_PROGRESS_PROJECTION,
                RankProgress.Columns.PERSONA_ID + "=?",
                new String[]{String.valueOf(selectedPersonaId())},
                null
        );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            rankProgress = RankProgressDAO.rankProgressFromCursor(cursor);
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    private boolean hasPersonaStatistics() {
        Cursor cursor = getContext().getContentResolver().query(
                PersonaStatistics.URI,
                PersonaStatistics.PERSONA_STATS_PROJECTION,
                PersonaStatistics.Columns.PERSONA_ID + "=?",
                new String[]{String.valueOf(selectedPersonaId())},
                null
        );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            listPersonaStatistics = PersonaStatisticsDAO.personaStaticsFromCursor(cursor);
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    private boolean hasScoreStatistics() {
        Cursor cursor = getContext().getContentResolver().query(
                ScoreStatistics.URI,
                ScoreStatistics.SCORE_STATISTICS_PROJECTION,
                ScoreStatistics.Columns.PERSONA_ID + "=?",
                new String[]{String.valueOf(selectedPersonaId())},
                null
        );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            listScoreStatistics = ScoreStatisticsDAO.scoreStatisticsFromCursor(cursor);
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    @Override
    public Loader<CompletedTask> onCreateLoader(int id, Bundle bundle) {
        startLoadingDialog(ProfileStatsFragment.class.getSimpleName());
        return new Bf3Loader(getContext(), httpDataStats());
    }

    private Bf3ServerCall.HttpData httpDataStats() {
        return new Bf3ServerCall.HttpData(UriFactory.getPersonaOverviewUri(selectedPersonaId(), platformId()), HttpGet.METHOD_NAME);
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> loader, CompletedTask completedTask) {
        if (isTaskSuccess(completedTask.result)) {
            processStatsLoaderResult(completedTask);
        } else {
            Log.e("ProfileOverviewFragment", "User data extraction failed for " + user().getName());
        }
        closeLoadingDialog(ProfileStatsFragment.class.getSimpleName());
    }

    private void processStatsLoaderResult(CompletedTask completedTask) {
        PersonaInfo personaInfo = personaStatsFrom(completedTask);
        updateDatabase(personaInfo);
        populateView();
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }

    private boolean isTaskSuccess(CompletedTask.Result result) {
        return result == CompletedTask.Result.SUCCESS;
    }

    private PersonaInfo personaStatsFrom(CompletedTask task) {
        Gson gson = new Gson();
        PersonaInfo data = gson.fromJson(task.jsonObject, PersonaInfo.class);
        return data;
    }

    public void findViews() {

        View view = getView();
        if (view == null) {
            return;
        }

        if (userPersonasCount() == 1) {
            view.findViewById(R.id.img_persona_list).setVisibility(View.INVISIBLE);
        }

        personaName = (TextView) view.findViewById(R.id.string_persona);
        rankTitle = (TextView) view.findViewById(R.id.string_rank_title);
        rankId = (TextView) view.findViewById(R.id.string_rank_short);

        currentLevelPoints = (TextView) view.findViewById(R.id.string_progress_curr);
        nextLevelPoints = (TextView) view.findViewById(R.id.string_progress_max);
        pointsToMake = (TextView) view.findViewById(R.id.string_progress_left);

        personaStatisticsTable = (TableLayout) view.findViewById(R.id.persona_statistics);
        scoreStatisticsTable = (TableLayout) view.findViewById(R.id.score_statistics);

        // Are we going to compare?
        /*
         * if (comparing) { ((CompareActivity)
         * getActivity()).sendToCompare(profileData,
         * personaStats,selectedPersona,toggle); }
         */
    }

    private void populateView() {
        populateRankProgress();
        if (personaStatisticsTable.getChildCount() > 0) {
            personaStatisticsTable.removeAllViews();
        }
        populateStatistics(listPersonaStatistics, personaStatisticsTable);
        if (scoreStatisticsTable.getChildCount() > 0) {
            scoreStatisticsTable.removeAllViews();
        }
        populateStatistics(listScoreStatistics, scoreStatisticsTable);
    }

    private void populateRankProgress() {
        if (rankProgress == null) {
            return;
        }
        personaName.setText(rankProgress.getPersonaName() + " " + rankProgress.getPlatform());
        rankTitle.setText(fromResource(rankProgress.getRank()));
        rankId.setText(format(rankProgress.getRank()));

        progressBar.setMax((int) (rankProgress.getNextRankScore() - rankProgress.getCurrentRankScore()));
        progressBar.setProgress((int) (rankProgress.getScore() - rankProgress.getCurrentRankScore()));
        currentLevelPoints.setText(format(rankProgress.getScore() - rankProgress.getCurrentRankScore()));
        nextLevelPoints.setText(format(rankProgress.getNextRankScore() - rankProgress.getCurrentRankScore()));
        pointsToMake.setText(format(rankProgress.getNextRankScore() - rankProgress.getScore()));
    }

    private void populateStatistics(List<Statistics> statistics, TableLayout layout) {
        for (Statistics ps : statistics) {
            View tr = layoutInflater.inflate(
                    R.layout.list_item_assignment_popup, null);
            ((TextView) tr.findViewById(R.id.text_obj_title)).setText(ps.getTitle());
            ((TextView) tr.findViewById(R.id.text_obj_values)).setText(ps.getValue());
            layout.addView(tr);
        }
    }

    private Context getContext() {
        return getActivity().getApplicationContext();
    }

   /* @Override
    public void loadFinished(Loader<CompletedTask> loader, CompletedTask task) {
        if (task != null && task.result.equals(CompletedTask.Result.SUCCESS)) {
            findViews();
            updateDatabase(pi);
            populateView();
            closeLoadingDialog(ProfileStatsFragment.class.getSimpleName());
        }
    }*/

    private String fromResource(int rank) {
        return getResources().getStringArray(R.array.rank)[rank];
    }

    private void updateDatabase(PersonaInfo pi) {
        updateRankProgressDB(pi);
        updatePersonaStats(pi);
        updateScoreStatistics(pi);
    }

    private void updateRankProgressDB(PersonaInfo pi) {
        rankProgress = rankProgressFromJSON(pi);
        ContentValues contentValues = RankProgressDAO.rankProgressForDB(pi, selectedPersonaId());
        getContext().getContentResolver().insert(RankProgress.URI, contentValues);
    }

    private void updatePersonaStats(PersonaInfo pi) {
        listPersonaStatistics = personaStatisticsFromJSON(pi);
        ContentValues contentValues = PersonaStatisticsDAO.personaStatisticsForDB(pi, selectedPersonaId());
        getContext().getContentResolver().insert(PersonaStatistics.URI, contentValues);
    }

    private void updateScoreStatistics(PersonaInfo pi) {
        listScoreStatistics = scoreStatisticsFromJSON(pi);
        ContentValues contentValues = ScoreStatisticsDAO.scoreStatisticsForDB(pi, selectedPersonaId());
        getContext().getContentResolver().insert(ScoreStatistics.URI, contentValues);
    }

    private Map<Long, String> personasToMap() {
        Map<Long, String> map = new HashMap<Long, String>();
        for (SimplePersona persona : user().getPersonas()) {
            map.put(persona.getPersonaId(), persona.getPersonaName() + " " + persona.getPlatform());
        }
        return map;
    }

    public Menu prepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.option_friendadd).setVisible(false);
        menu.findItem(R.id.option_frienddel).setVisible(false);
        menu.findItem(R.id.option_compare).setVisible(true);
        menu.findItem(R.id.option_unlocks).setVisible(true);
        return menu;
    }

    public boolean handleSelectedOption(MenuItem item) {
        if (item.getItemId() == R.id.option_compare) {
            startActivity(new Intent(context, CompareActivity.class)
                    .putExtra("profile1", SessionKeeper.getProfileData())
                    .putExtra("profile2", /*profileData*/new ProfileData(""))
                    .putExtra("selectedPosition", 0)); //TODO should only provide profile against which to compare and use currently selected user persona
        } else if (item.getItemId() == R.id.option_unlocks) {
            int position = 0;
            for (long key : personaStats.keySet()) {
                if (key == selectedPersonaId()) {
                    break;
                } else {
                    position++;
                }
            }
            startActivity(
                    new Intent(context, UnlockActivity.class)
                            .putExtra("profile", new ProfileData(""))
                            .putExtra("selectedPosition", position));
        }
        return true;
    }

    public void setComparing(boolean c) {
        comparing = c;
    }

    @Override
    public void reload() {
        restartLoader();
    }

    private int userPersonasCount() {
        return user().getPersonas().size();
    }

    private User user() {
        if (isUser()) {
            return BF3Droid.getUser();
        } else {
            return BF3Droid.getGuest();
        }
    }

    private boolean isUser() {
        return getArguments().getString("user").equals(User.USER);
    }

    private long selectedPersonaId() {
        return user().selectedPersona().getPersonaId();
    }

    private int platformId() {
        return Platform.resolveIdFromPlatformName(user().selectedPersona().getPlatform());
    }
}
