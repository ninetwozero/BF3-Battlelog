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

import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_ID;
import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_POS;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.profile.unlocks.UnlockActivity;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.PersonaStats;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.dialog.ProfilePersonaListDialog;
import com.ninetwozero.battlelog.handlers.ProfileHandler;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class ProfileStatsFragment extends Fragment implements DefaultFragment {

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

        // Return
        return view;

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
                            FragmentTransaction transaction = getFragmentManager()
                                    .beginTransaction();
                            ProfilePersonaListDialog dialog = ProfilePersonaListDialog
                                    .newInstance(profileData);
                            dialog.show(transaction, "ProfilePersonaListDialog");
                            reload();
                        }
                    }
                });
    }

    public void showPersona(long pid) {

        showPersona(personaStats.get(pid), true);

    }

    public void showPersona(PersonaStats pd, boolean toggle) {

        // Is pd null?
        if (pd == null) {
            return;
        }

        // Let's find it
        View view = getView();
        if (view == null) {
            return;
        }

        if (personaArrayLength() == 1) {
            ((ImageView) view.findViewById(R.id.img_persona_list)).setVisibility(View.INVISIBLE);
        }

        // Persona & rank
        ((TextView) view.findViewById(R.id.string_persona)).setText(pd
                .getPersonaName());
        ((TextView) view.findViewById(R.id.string_rank_title)).setText(pd
                .getRankTitle());
        ((TextView) view.findViewById(R.id.string_rank_short)).setText(pd
                .getRankId() + "");

        // Progress
        progressBar.setMax((int) pd.getPointsNeededToLvlUp());
        progressBar.setProgress((int) pd.getPointsProgressLvl());
        ((TextView) view.findViewById(R.id.string_progress_curr)).setText(pd
                .getPointsProgressLvl() + "");
        ((TextView) view.findViewById(R.id.string_progress_max)).setText(pd
                .getPointsNeededToLvlUp() + "");
        ((TextView) view.findViewById(R.id.string_progress_left)).setText(pd
                .getPointsLeft() + "");

        // Score
        ((TextView) view.findViewById(R.id.string_score_assault)).setText(pd
                .getScoreAssault() + "");
        ((TextView) view.findViewById(R.id.string_score_engineer)).setText(pd
                .getScoreEngineer() + "");
        ((TextView) view.findViewById(R.id.string_score_support)).setText(pd
                .getScoreSupport() + "");
        ((TextView) view.findViewById(R.id.string_score_recon)).setText(pd
                .getScoreRecon() + "");
        ((TextView) view.findViewById(R.id.string_score_vehicles)).setText(pd
                .getScoreVehicles() + "");
        ((TextView) view.findViewById(R.id.string_score_combat)).setText(pd
                .getScoreCombat() + "");
        ((TextView) view.findViewById(R.id.string_score_award)).setText(pd
                .getScoreAwards() + "");
        ((TextView) view.findViewById(R.id.string_score_unlock)).setText(pd
                .getScoreUnlocks() + "");
        ((TextView) view.findViewById(R.id.string_score_total)).setText(pd
                .getScoreTotal() + "");

        // Stats
        ((TextView) view.findViewById(R.id.string_stats_kills)).setText(pd
                .getNumKills() + "");
        ((TextView) view.findViewById(R.id.string_stats_assists)).setText(pd
                .getNumAssists() + "");
        ((TextView) view.findViewById(R.id.string_stats_vkills)).setText(pd
                .getNumVehicles() + "");
        ((TextView) view.findViewById(R.id.string_stats_vassists)).setText(pd
                .getNumVehicleAssists() + "");
        ((TextView) view.findViewById(R.id.string_stats_heals)).setText(pd
                .getNumHeals() + "");
        ((TextView) view.findViewById(R.id.string_stats_revives)).setText(pd
                .getNumRevives() + "");
        ((TextView) view.findViewById(R.id.string_stats_repairs)).setText(pd
                .getNumRepairs() + "");
        ((TextView) view.findViewById(R.id.string_stats_resupplies)).setText(pd
                .getNumResupplies() + "");
        ((TextView) view.findViewById(R.id.string_stats_deaths)).setText(pd
                .getNumDeaths() + "");
        ((TextView) view.findViewById(R.id.string_stats_kdr)).setText(pd
                .getKDRatio() + "");
        ((TextView) view.findViewById(R.id.string_stats_wins)).setText(pd
                .getNumWins() + "");
        ((TextView) view.findViewById(R.id.string_stats_losses)).setText(pd
                .getNumLosses() + "");
        ((TextView) view.findViewById(R.id.string_stats_wlr)).setText(pd
                .getWLRatio() + "");
        ((TextView) view.findViewById(R.id.string_stats_accuracy)).setText(pd
                .getAccuracy() + "%");
        ((TextView) view.findViewById(R.id.string_stats_lks)).setText(pd
                .getLongestKS() + "");
        ((TextView) view.findViewById(R.id.string_stats_lhs)).setText(pd
                .getLongestHS() + " m");
        ((TextView) view.findViewById(R.id.string_stats_skill)).setText(pd
                .getSkill() + "");
        ((TextView) view.findViewById(R.id.string_stats_time)).setText(pd
                .getTimePlayedString() + "");
        ((TextView) view.findViewById(R.id.string_stats_spm)).setText(pd
                .getScorePerMinute() + "");

        // Are we going to compare?
        if (comparing) {

            ((CompareActivity) getActivity()).sendToCompare(profileData, personaStats,
                    selectedPersona,
                    toggle);

        }

    }

    private int personaArrayLength() {
        return profileData.getPersonaArray().length;
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
                showPersona(personaStats.get(selectedPersona), false);
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
                    selectedPersona = (selectedPersona == 0) ? profileData
                            .getPersona(0).getId() : selectedPersona;

                    // Grab the stats
                    personaStats = new ProfileHandler(profileData).getStats(context);

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

                showPersona(personaStats.get(selectedPersona), false);

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

            new Intent(

                    context, CompareActivity.class

            ).putExtra(

                    "profile1", SessionKeeper.getProfileData()

                    ).putExtra(

                            "profile2", profileData

                    ).putExtra(

                            "selectedPosition", selectedPosition

                    )

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

            new Intent(

                    context, UnlockActivity.class

            ).putExtra(

                    "profile", profileData

                    ).putExtra(

                            "selectedPosition", position

                    )

            );

        }

        return true;

    }

    public void setComparing(boolean c) {

        comparing = c;
    }

    public Dialog generateDialogPersonaList(final long[] personaId, final String[] persona) {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and the view
        builder.setTitle(R.string.info_dialog_soldierselect);

        builder.setSingleChoiceItems(

                persona, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        if (personaId[item] != selectedPersona) {

                            // Update it

                            selectedPersona = personaId[item];

                            // Load the new!
                            showPersona(personaStats.get(selectedPersona), true);

                            // Store selectedPersonaPos
                            selectedPosition = item;

                            // Let's update SP
                            updateSharedPreference();

                        }

                        dialog.dismiss();

                    }

                }

                );

        // CREATE
        return builder.create();

    }

    private void updateSharedPreference() {

        // Is this someone else than the logged in user?
        if (SessionKeeper.getProfileData().getId() != profileData.getId()) {

            return;

        }
        // Update the sharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SP_BL_PERSONA_CURRENT_ID, selectedPersona);
        editor.putInt(SP_BL_PERSONA_CURRENT_POS, selectedPosition);
        editor.commit();

    }

}
