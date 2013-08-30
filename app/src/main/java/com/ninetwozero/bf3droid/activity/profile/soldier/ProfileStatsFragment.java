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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3Fragment;
import com.ninetwozero.bf3droid.activity.profile.soldier.restorer.PersonaStatisticsRestorer;
import com.ninetwozero.bf3droid.activity.profile.unlocks.UnlockActivity;
import com.ninetwozero.bf3droid.datatype.*;
import com.ninetwozero.bf3droid.dialog.ListDialogFragment;
import com.ninetwozero.bf3droid.misc.SessionKeeper;
import com.ninetwozero.bf3droid.model.SelectedOption;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.ninetwozero.bf3droid.provider.table.PersonaStatistics;
import com.ninetwozero.bf3droid.provider.table.RankProgress;
import com.ninetwozero.bf3droid.provider.table.ScoreStatistics;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import static com.ninetwozero.bf3droid.misc.NumberFormatter.format;

public class ProfileStatsFragment extends Bf3Fragment implements ProfileStatsLoader.Callback{
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
    private final String DIALOG = "dialog";

    private Bundle bundle;
    private TableLayout personaStatisticsTable;
    private TableLayout scoreStatisticsTable;
    private RankProgress rankProgress;
    private Map<String, Statistics> personaStatisticsMap;
    private Map<String, Statistics> scoreStatisticsMap;

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
                            ListDialogFragment dialog = ListDialogFragment.newInstance(personasToMap(), userType());
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
            startLoadingDialog(ProfileStatsFragment.class.getSimpleName());
            new ProfileStatsLoader(this, getContext(), userType(), getLoaderManager()).restart();
        }
    }

    @Subscribe
    public void selectionChanged(SelectedOption selectedOption) {
        if (selectedOption.getChangedGroup().equals(userType())) {
            user(userType()).selectPersona(selectedOption.getSelectedId());
            getData();
        }
    }

    private boolean dbHasData() {
        PersonaOverviewStatistics pos = new PersonaStatisticsRestorer(context, userType()).fetch();
        if (pos.isEmpty()) {
            return false;
        } else {
            rankProgress = pos.getRankProgress();
            personaStatisticsMap = pos.getPersonaStats();
            scoreStatisticsMap = pos.getScoreStats();
            return true;
        }
    }

    @Override
    public void onLoadFinished(PersonaOverviewStatistics pos) {
        rankProgress = pos.getRankProgress();
        personaStatisticsMap = pos.getPersonaStats();
        scoreStatisticsMap = pos.getScoreStats();
        closeLoadingDialog(ProfileStatsFragment.class.getSimpleName());
        populateView();
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
        populateStatistics(personaStatisticsMap, personaStatisticsTable, PersonaStatistics.PERSONA_STATISTICS);
        if (scoreStatisticsTable.getChildCount() > 0) {
            scoreStatisticsTable.removeAllViews();
        }
        populateStatistics(scoreStatisticsMap, scoreStatisticsTable, ScoreStatistics.SCORE_STATISTICS);
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

    private void populateStatistics(Map<String, Statistics> statistics, TableLayout layout, String[] keyOrder ) {
        for (String key : keyOrder) {
            Statistics ps = statistics.get(key);
            View tr = layoutInflater.inflate(R.layout.profile_statistics_table_row, null);
            ((TextView) tr.findViewById(R.id.statistics_title)).setText(ps.getTitle());
            ((TextView) tr.findViewById(R.id.statistics_values)).setText(ps.getValue());
            layout.addView(tr);
        }
    }

    private Context getContext() {
        return getActivity().getApplicationContext();
    }

    private String fromResource(int rank) {
        return getResources().getStringArray(R.array.rank)[rank];
    }

    private Map<Long, String> personasToMap() {
        Map<Long, String> map = new HashMap<Long, String>();
        for (SimplePersona persona : user(userType()).getPersonas()) {
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

    @Override
    public void reload() {
        new ProfileStatsLoader(this, getContext(), userType(), getLoaderManager()).restart();
    }

    private int userPersonasCount() {
        return user(userType()).getPersonas().size();
    }

    private String userType() {
        return getArguments().getString("user");
    }

    private long selectedPersonaId() {
        return user(userType()).selectedPersona().getPersonaId();
    }
}
