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

package com.ninetwozero.bf3droid.activity.profile.soldier;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3Fragment;
import com.ninetwozero.bf3droid.activity.profile.unlocks.UnlockActivity;
import com.ninetwozero.bf3droid.dao.PersonaStatisticsDAO;
import com.ninetwozero.bf3droid.dao.ScoreStatisticsDAO;
import com.ninetwozero.bf3droid.datatype.PersonaData;
import com.ninetwozero.bf3droid.datatype.PersonaStats;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.datatype.Statistics;
import com.ninetwozero.bf3droid.dialog.ListDialogFragment;
import com.ninetwozero.bf3droid.jsonmodel.personas.Soldier;
import com.ninetwozero.bf3droid.jsonmodel.personas.UserSoldiers;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.PersonaInfo;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.DataBank;
import com.ninetwozero.bf3droid.misc.SessionKeeper;
import com.ninetwozero.bf3droid.model.SelectedPersona;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.provider.table.PersonaStatistics;
import com.ninetwozero.bf3droid.provider.table.RankProgress;
import com.ninetwozero.bf3droid.provider.table.ScoreStatistics;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;
import com.squareup.otto.Subscribe;
import org.apache.http.client.methods.HttpGet;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ninetwozero.bf3droid.dao.RankProgressDAO.*;
import static com.ninetwozero.bf3droid.misc.Constants.SP_BL_PERSONA_CURRENT_ID;
import static com.ninetwozero.bf3droid.misc.Constants.SP_BL_PERSONA_CURRENT_POS;
import static com.ninetwozero.bf3droid.misc.NumberFormatter.format;

public class ProfileStatsFragment extends Bf3Fragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;

    // Elements
    private RelativeLayout mWrapPersona;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private TextView personaName;
    private TextView rankTitle;
    private TextView rankId;
    private TextView currentLevelPoints;
    private TextView nextLevelPoints;
    private TextView pointsToMake;

    private PersonaData[] personaData;
    private ProfileData profileData;
    private Map<Long, PersonaStats> personaStats;
    private long selectedPersona;
    private int selectedPosition;
    private int selectedPlatformId;
    private String selectedPersonaName;
    private boolean comparing;

    private URI callURI;
    private final String DIALOG = "dialog";
    private final int LOADER_PERSONA = 20;
    private final int LOADER_STATS = 21;

    private Bundle bundle;
    private RankProgress rankProgress;
    private TableLayout personaStatisticsTable;
    private TableLayout scoreStatisticsTable;
    private List<Statistics> listPersonaStatistics;
    private List<Statistics> listScoreStatistics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        layoutInflater = inflater;

        View view = layoutInflater.inflate(R.layout.tab_content_profile_stats, container, false);
        initFragment(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.bundle = savedInstanceState;
        getData();
    }

    public void initFragment(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress_level);

        if (profileData.getId() == SessionKeeper.getProfileData().getId()) {
            setSelectedPersonaVariables();
        }

        mWrapPersona = (RelativeLayout) view.findViewById(R.id.wrap_persona);
        mWrapPersona.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View sv) {
                        if (personaArrayLength() > 1) {
                            FragmentManager manager = getFragmentManager();
                            ListDialogFragment dialog = ListDialogFragment.newInstance(personasToMap());
                            dialog.show(manager, DIALOG);
                        }
                    }
                }
        );
    }

    /* FIXME: if no personas are passed to this activity, then selectedPersona will be 0? */
    private void setSelectedPersonaVariables() {
        selectedPosition = sharedPreferences.getInt(Constants.SP_BL_PERSONA_CURRENT_POS, 0);
        selectedPersona = getSelectedPersonaId(selectedPosition);
        selectedPlatformId = getPlatformIdFor(selectedPosition);
        selectedPersonaName = getSelectedPersonaName(selectedPosition);
        callURI = UriFactory.getPersonaOverviewUri(selectedPersona, selectedPlatformId);
    }

    private long getSelectedPersonaId(int position) {
        return profileData.getPersonaArray()[position].getId();
    }

    private int getPlatformIdFor(int position) {
        return profileData.getPersonaArray()[position].getPlatformId();
    }

    private String getSelectedPersonaName(int position) {
        return profileData.getPersonaArray()[position].getName();
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void personaChanged(SelectedPersona selectedPersona) {
        updateSharedPreference(selectedPersona.getPersonaId());
        setSelectedPersonaVariables();
        getData();
    }

    private void getData() {
        if (dbHasData()) {
            findViews();
            populateView();
        } else {
            restartLoader();
        }
    }

    private boolean dbHasData() {
        return hasRankData() && hasPersonaStatistics() && hasScoreStatistics();
    }

    private boolean hasRankData() {
        Cursor cursor = getContext().getContentResolver().query(
                RankProgress.URI,
                RankProgress.RANK_PROGRESS_PROJECTION,
                RankProgress.Columns.PERSONA_ID + "=?",
                new String[]{String.valueOf(selectedPersona)},
                null
        );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            rankProgress = rankProgressFromCursor(cursor);
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
                new String[]{String.valueOf(selectedPersona)},
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
                new String[]{String.valueOf(selectedPersona)},
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

    public void findViews() {

        View view = getView();
        if (view == null) {
            return;
        }

        if (personaArrayLength() == 1) {
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
        personaStatisticsTable.removeAllViews();
        populateStatistics(listPersonaStatistics, personaStatisticsTable);
        scoreStatisticsTable.removeAllViews();
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

    private int personaArrayLength() {
        return profileData.getPersonaArray().length;
    }

    @Override
    protected Loader<CompletedTask> createLoader(int id, Bundle bundle) {
        if (id == LOADER_PERSONA) {
            startLoadingDialog();
            return new Bf3Loader(getContext(), new Bf3ServerCall.HttpData(UriFactory.getProfilePersonasUri(profileData.getId()), HttpGet.METHOD_NAME));
        } else {
            startLoadingDialog();
            return new Bf3Loader(getContext(), new Bf3ServerCall.HttpData(UriFactory.getPersonaOverviewUri(selectedPersona, selectedPlatformId), HttpGet.METHOD_NAME));
        }
    }

    private Context getContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void loadFinished(Loader<CompletedTask> loader, CompletedTask task) {
        /* FIXME: This doesn't seem right, maybe due to the lack of personas? */
        if (task != null && task.result.equals(CompletedTask.Result.SUCCESS)) {
            if (loader.getId() == LOADER_STATS) {
                findViews();
                PersonaInfo pi = personaStatsFrom(task);
                updateDatabase(pi);
                populateView();
            } else if(loader.getId() == LOADER_PERSONA){
                userSoldiersFrom(task);
            }
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private PersonaData[] personaArrayFrom(CompletedTask task) {
        try {
            JsonArray personaArray = task.jsonObject.getAsJsonArray("soldiersBox");
            int numOfPersonas = personaArray.size();
            PersonaData[] personas = new PersonaData[numOfPersonas];
            for (int i = 0; i < numOfPersonas; i++) {
                JsonObject personaObject = personaArray.get(i).getAsJsonObject().get("persona").getAsJsonObject();
                String picture = personaObject.get("picture").isJsonNull() ? "" : personaObject.get("picture").getAsString();
                personas[i] = new PersonaData(
                        personaObject.get("personaId").getAsLong(),
                        personaObject.get("personaName").getAsString(),
                        DataBank.getPlatformIdFromName(personaObject.get("namespace").getAsString()),
                        picture
                );
            }
            return personas;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void userSoldiersFrom(CompletedTask task) {
        Gson gson = new Gson();
        UserSoldiers userSoldiers = gson.fromJson(task.jsonObject, UserSoldiers.class);
        List<Soldier> soldiers = userSoldiers.getSoldierInfo();
        int numSoldiers = soldiers.size();

        /*PersonaData[] personas = new PersonaData[numSoldiers];
        for( int i = 0; i < numSoldiers; i++ ) {
            personas[i] = soldiers.get(i).getPersona();
            Log.d(Constants.DEBUG_TAG, "persona => " + personas[i]);
        }*/
    }

    private PersonaInfo personaStatsFrom(CompletedTask task) {
        Gson gson = new Gson();
        PersonaInfo data = gson.fromJson(task.jsonObject, PersonaInfo.class);
        return data;
    }

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
        ContentValues contentValues = rankProgressForDB(pi, selectedPersona);
        getContext().getContentResolver().insert(RankProgress.URI, contentValues);
    }

    private void updatePersonaStats(PersonaInfo pi) {
        listPersonaStatistics = PersonaStatisticsDAO.personaStatisticsFromJSON(pi);
        ContentValues contentValues = PersonaStatisticsDAO.personaStatisticsForDB(pi, selectedPersona);
        getContext().getContentResolver().insert(PersonaStatistics.URI, contentValues);
    }

    private void updateScoreStatistics(PersonaInfo pi) {
        listScoreStatistics = ScoreStatisticsDAO.scoreStatisticsFromJSON(pi);
        ContentValues contentValues = ScoreStatisticsDAO.scoreStatisticsForDB(pi, selectedPersona);
        getContext().getContentResolver().insert(ScoreStatistics.URI, contentValues);
    }

    private Map<Long, String> personasToMap() {
        Map<Long, String> map = new HashMap<Long, String>();
        for (PersonaData pd : personaData) {
            map.put(pd.getId(), pd.getName() + " " + pd.resolvePlatformId());
        }
        return map;
    }

    private void updateSharedPreference(long personaId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(SP_BL_PERSONA_CURRENT_ID, personaId);
        editor.putInt(SP_BL_PERSONA_CURRENT_POS, indexOfPersona(personaId));
        editor.commit();
    }

    private int indexOfPersona(long personaId) {
        for (int i = 0; i < personaData.length; i++) {
            if (personaData[i].getId() == personaId) {
                return i;
            }
        }
        Log.w(ProfileStatsFragment.class.getSimpleName(), "Failed to find index of the platoon!");
        return 0;
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
                    .putExtra("profile2", profileData)
                    .putExtra("selectedPosition", selectedPosition));
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
                            .putExtra("selectedPosition", position));
        }
        return true;
    }

    public void setComparing(boolean c) {
        comparing = c;
    }

    public void reloadFromCache() {
        getData();
    }

    @Override
    public void reload() {
        restartLoader();
    }

    private void restartLoader() {
        Log.d(Constants.DEBUG_TAG, "numPersonas => " + profileData.getNumPersonas());
        if (profileData.getNumPersonas() == 0) {
            getLoaderManager().restartLoader(LOADER_PERSONA, bundle, this);
        }
        getLoaderManager().restartLoader(LOADER_STATS, bundle, this);
    }

    private void startLoadingDialog() {   //TODO extract multiple duplicates of same code
        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setTitle(context.getString(R.string.general_wait));
            this.progressDialog.setMessage(context.getString(R.string.general_downloading));
            this.progressDialog.show();
        }
    }
}
