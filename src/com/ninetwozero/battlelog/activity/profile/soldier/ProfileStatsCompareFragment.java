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

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.PersonaStats;

public class ProfileStatsCompareFragment extends Fragment implements DefaultFragment {

    // Attributes
    private LayoutInflater layoutInflater;
    private long[] selectedPersona;
    private Map<Long, PersonaStats> personaStats;
    private int[] differences;
    private int numCalls;

    // These are the different fields
    private final int FIELD_PERSONA[] = new int[] {
            R.id.string_persona_0,
            R.id.string_persona_1
    };
    private final int FIELD_RANK[] = new int[] {
            R.id.string_rank_short_0,
            R.id.string_rank_short_1
    };
    private final int FIELD_PROGRESS_CURR[] = new int[] {
            R.id.string_progress_curr_0, R.id.string_progress_curr_1
    };
    private final int FIELD_PROGRESS_MAX[] = new int[] {
            R.id.string_progress_max_0, R.id.string_progress_max_1
    };

    private final int FIELD_SCORE_ASSAULT[] = new int[] {
            R.id.string_score_assault_0, R.id.string_score_assault_1
    };
    private final int FIELD_SCORE_ENGINEER[] = new int[] {
            R.id.string_score_engineer_0, R.id.string_score_engineer_1
    };
    private final int FIELD_SCORE_SUPPORT[] = new int[] {
            R.id.string_score_support_0, R.id.string_score_support_1
    };
    private final int FIELD_SCORE_RECON[] = new int[] {
            R.id.string_score_recon_0, R.id.string_score_recon_1
    };
    private final int FIELD_SCORE_VEHICLES[] = new int[] {
            R.id.string_score_vehicles_0, R.id.string_score_vehicles_1
    };
    private final int FIELD_SCORE_COMBAT[] = new int[] {
            R.id.string_score_combat_0, R.id.string_score_combat_1
    };
    private final int FIELD_SCORE_AWARD[] = new int[] {
            R.id.string_score_award_0, R.id.string_score_award_1
    };
    private final int FIELD_SCORE_UNLOCKS[] = new int[] {
            R.id.string_score_unlock_0, R.id.string_score_unlock_1
    };
    private final int FIELD_SCORE_TOTAL[] = new int[] {
            R.id.string_score_total_0, R.id.string_score_total_1
    };

    private final int FIELD_STATS_KILLS[] = new int[] {
            R.id.string_stats_kills_0, R.id.string_stats_kills_1
    };
    private final int FIELD_STATS_ASSISTS[] = new int[] {
            R.id.string_stats_assists_0, R.id.string_stats_assists_1
    };
    private final int FIELD_STATS_VKILLS[] = new int[] {
            R.id.string_stats_vkills_0, R.id.string_stats_vkills_1
    };
    private final int FIELD_STATS_VASSISTS[] = new int[] {
            R.id.string_stats_vassists_0, R.id.string_stats_vassists_1
    };
    private final int FIELD_STATS_HEALS[] = new int[] {
            R.id.string_stats_heals_0, R.id.string_stats_heals_1
    };
    private final int FIELD_STATS_REVIVES[] = new int[] {
            R.id.string_stats_revives_0, R.id.string_stats_revives_1
    };
    private final int FIELD_STATS_REPAIRS[] = new int[] {
            R.id.string_stats_repairs_0, R.id.string_stats_repairs_1
    };
    private final int FIELD_STATS_RESUPPLIES[] = new int[] {
            R.id.string_stats_resupplies_0, R.id.string_stats_resupplies_1
    };
    private final int FIELD_STATS_DEATHS[] = new int[] {
            R.id.string_stats_deaths_0, R.id.string_stats_deaths_1
    };
    private final int FIELD_STATS_KDR[] = new int[] {
            R.id.string_stats_kdr_0,
            R.id.string_stats_kdr_1
    };
    private final int FIELD_STATS_WINS[] = new int[] {
            R.id.string_stats_wins_0,
            R.id.string_stats_wins_1
    };
    private final int FIELD_STATS_LOSSES[] = new int[] {
            R.id.string_stats_losses_0, R.id.string_stats_losses_1
    };
    private final int FIELD_STATS_WLR[] = new int[] {
            R.id.string_stats_wlr_0,
            R.id.string_stats_wlr_1
    };
    private final int FIELD_STATS_ACCURACY[] = new int[] {
            R.id.string_stats_accuracy_0, R.id.string_stats_accuracy_1
    };
    private final int FIELD_STATS_TIME[] = new int[] {
            R.id.string_stats_time_0,
            R.id.string_stats_time_1
    };
    private final int FIELD_STATS_SKILLS[] = new int[] {
            R.id.string_stats_skill_0, R.id.string_stats_skill_1
    };
    private final int FIELD_STATS_SPM[] = new int[] {
            R.id.string_stats_spm_0,
            R.id.string_stats_spm_1
    };
    private final int FIELD_STATS_LKS[] = new int[] {
            R.id.string_stats_lks_0,
            R.id.string_stats_lks_1
    };
    private final int FIELD_STATS_LHS[] = new int[] {
            R.id.string_stats_lhs_0,
            R.id.string_stats_lhs_1
    };

    // These are the different elements
    private TextView tvPersona[] = new TextView[2];
    private TextView tvRank[] = new TextView[2];
    private TextView tvProgressCurr[] = new TextView[2];
    private TextView tvProgressMax[] = new TextView[2];

    private TextView tvScoreAssault[] = new TextView[2];
    private TextView tvScoreEngineer[] = new TextView[2];
    private TextView tvScoreSupport[] = new TextView[2];
    private TextView tvScoreRecon[] = new TextView[2];
    private TextView tvScoreVehicles[] = new TextView[2];
    private TextView tvScoreCombat[] = new TextView[2];
    private TextView tvScoreAward[] = new TextView[2];
    private TextView tvScoreUnlocks[] = new TextView[2];
    private TextView tvScoreTotal[] = new TextView[2];

    private TextView tvStatsKills[] = new TextView[2];
    private TextView tvStatsAssists[] = new TextView[2];
    private TextView tvStatsVKills[] = new TextView[2];
    private TextView tvStatsVAssists[] = new TextView[2];
    private TextView tvStatsHeals[] = new TextView[2];
    private TextView tvStatsRevives[] = new TextView[2];
    private TextView tvStatsRepairs[] = new TextView[2];
    private TextView tvStatsResupplies[] = new TextView[2];
    private TextView tvStatsDeath[] = new TextView[2];
    private TextView tvStatsKDR[] = new TextView[2];
    private TextView tvStatsWins[] = new TextView[2];
    private TextView tvStatsLosses[] = new TextView[2];
    private TextView tvStatsWLR[] = new TextView[2];
    private TextView tvStatsAccuracy[] = new TextView[2];
    private TextView tvStatsTime[] = new TextView[2];
    private TextView tvStatsSkill[] = new TextView[2];
    private TextView tvStatsSPM[] = new TextView[2];
    private TextView tvStatsLKS[] = new TextView[2];
    private TextView tvStatsLHS[] = new TextView[2];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        layoutInflater = inflater;

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_compare,
                container, false);

        // Init
        initFragment(view);

        // Return
        return view;

    }

    public void initFragment(View view) {

        personaStats = new HashMap<Long, PersonaStats>();
        selectedPersona = new long[2];

        // Loop over 'em
        for (int i = 0, max = tvPersona.length; i < max; i++) {

            // General
            tvRank[i] = (TextView) view.findViewById(FIELD_RANK[i]);
            tvPersona[i] = (TextView) view.findViewById(FIELD_PERSONA[i]);

            // Progress
            tvProgressCurr[i] = (TextView) view.findViewById(FIELD_PROGRESS_CURR[i]);
            tvProgressMax[i] = (TextView) view.findViewById(FIELD_PROGRESS_MAX[i]);

            // Score
            tvScoreAssault[i] = (TextView) view.findViewById(FIELD_SCORE_ASSAULT[i]);
            tvScoreEngineer[i] = (TextView) view.findViewById(FIELD_SCORE_ENGINEER[i]);
            tvScoreSupport[i] = (TextView) view.findViewById(FIELD_SCORE_SUPPORT[i]);
            tvScoreRecon[i] = (TextView) view.findViewById(FIELD_SCORE_RECON[i]);
            tvScoreVehicles[i] = (TextView) view.findViewById(FIELD_SCORE_VEHICLES[i]);
            tvScoreCombat[i] = (TextView) view.findViewById(FIELD_SCORE_COMBAT[i]);
            tvScoreAward[i] = (TextView) view.findViewById(FIELD_SCORE_AWARD[i]);
            tvScoreUnlocks[i] = (TextView) view.findViewById(FIELD_SCORE_UNLOCKS[i]);
            tvScoreTotal[i] = (TextView) view.findViewById(FIELD_SCORE_TOTAL[i]);

            // Stats
            tvStatsKills[i] = (TextView) view.findViewById(FIELD_STATS_KILLS[i]);
            tvStatsAssists[i] = (TextView) view.findViewById(FIELD_STATS_ASSISTS[i]);
            tvStatsVKills[i] = (TextView) view.findViewById(FIELD_STATS_VKILLS[i]);
            tvStatsVAssists[i] = (TextView) view.findViewById(FIELD_STATS_VASSISTS[i]);
            tvStatsHeals[i] = (TextView) view.findViewById(FIELD_STATS_HEALS[i]);
            tvStatsRevives[i] = (TextView) view.findViewById(FIELD_STATS_REVIVES[i]);
            tvStatsRepairs[i] = (TextView) view.findViewById(FIELD_STATS_REPAIRS[i]);
            tvStatsResupplies[i] = (TextView) view.findViewById(FIELD_STATS_RESUPPLIES[i]);
            tvStatsDeath[i] = (TextView) view.findViewById(FIELD_STATS_DEATHS[i]);
            tvStatsKDR[i] = (TextView) view.findViewById(FIELD_STATS_KDR[i]);
            tvStatsWins[i] = (TextView) view.findViewById(FIELD_STATS_WINS[i]);
            tvStatsLosses[i] = (TextView) view.findViewById(FIELD_STATS_LOSSES[i]);
            tvStatsWLR[i] = (TextView) view.findViewById(FIELD_STATS_WLR[i]);
            tvStatsAccuracy[i] = (TextView) view.findViewById(FIELD_STATS_ACCURACY[i]);
            tvStatsTime[i] = (TextView) view.findViewById(FIELD_STATS_TIME[i]);
            tvStatsSkill[i] = (TextView) view.findViewById(FIELD_STATS_SKILLS[i]);
            tvStatsSPM[i] = (TextView) view.findViewById(FIELD_STATS_SPM[i]);
            tvStatsLKS[i] = (TextView) view.findViewById(FIELD_STATS_LKS[i]);
            tvStatsLHS[i] = (TextView) view.findViewById(FIELD_STATS_LHS[i]);

        }
    }

    public void populateStats(PersonaStats ps, int pos) {

        // If ps == null
        if (ps == null) {
            return;
        }

        /*
         * This is how they' come served right now... ouch 0 => personaId, 1 =>
         * userId 2 => platformId - 6 => rankId 7 => pointsThisLvl 8 =>
         * pointsNextLvl 9 => timePlayed 10 => numKills 11 => numAssists 12 =>
         * numVehicles 13 => numVehicleAssists 14 => numHeals 15 => numRevives
         * 16 => numRepairs 17 => numResupplies 18 => numDeaths 19 => numWins 20
         * => numLosses 21 => kdRatio 22 => accuracy 23 => longestHS 24 =>
         * longestKS 25 => skill 26 => scorePerMinute 27 => scoreAssault 28 =>
         * scoreEngineer 29 => scoreSupport 30 => scoreRecon 31 => scoreVehicle
         * 32 => scoreCombat 33 => scoreAwards 34 => scoreUnlocks 35 =>
         * scoreTotal 36 => wlRatio
         */

        // Persona & rank
        tvPersona[pos].setText(ps.getPersonaName()
                .replaceAll("(\\[^\\]]+)", ""));

        if (differences[6] == pos) {
            tvRank[pos].setText(Html.fromHtml("<b>" + ps.getRankId() + "</b>"));
        }
        else {
            tvRank[pos].setText(ps.getRankId() + "");
        }

        // Progress
        tvProgressCurr[pos].setText(ps.getPointsProgressLvl() + "");
        tvProgressMax[pos].setText(ps.getPointsNeededToLvlUp() + "");

        // Stats
        if (differences[9] == pos) {
            tvStatsTime[pos].setText(Html.fromHtml("<b>" + ps.getTimePlayedString() + "</b>"));
        }
        else {
            tvStatsTime[pos].setText(ps.getTimePlayedString());
        }

        if (differences[10] == pos) {
            tvStatsKills[pos].setText(Html.fromHtml("<b>" + ps.getNumKills() + "</b>"));
        }
        else {
            tvStatsKills[pos].setText(ps.getNumKills() + "");
        }

        if (differences[11] == pos) {
            tvStatsAssists[pos].setText(Html.fromHtml("<b>" + ps.getNumAssists() + "</b>"));
        }
        else {
            tvStatsAssists[pos].setText(ps.getNumAssists() + "");
        }

        if (differences[12] == pos) {
            tvStatsVKills[pos].setText(Html.fromHtml("<b>" + ps.getNumVehicles() + "</b>"));
        }
        else {
            tvStatsVKills[pos].setText(ps.getNumVehicles() + "");
        }

        if (differences[13] == pos) {
            tvStatsVAssists[pos].setText(Html.fromHtml("<b>" + ps.getNumVehicleAssists() + "</b>"));
        }
        else {
            tvStatsVAssists[pos].setText(ps.getNumVehicleAssists() + "");
        }

        if (differences[14] == pos) {
            tvStatsHeals[pos].setText(Html.fromHtml("<b>" + ps.getNumHeals() + "</b>"));
        }
        else {
            tvStatsHeals[pos].setText(ps.getNumHeals() + "");
        }

        if (differences[15] == pos) {
            tvStatsRevives[pos].setText(Html.fromHtml("<b>" + ps.getNumRevives() + "</b>"));
        }
        else {
            tvStatsRevives[pos].setText(ps.getNumRevives() + "");
        }

        if (differences[16] == pos) {
            tvStatsRepairs[pos].setText(Html.fromHtml("<b>" + ps.getNumRepairs() + "</b>"));
        }
        else {
            tvStatsRepairs[pos].setText(ps.getNumRepairs() + "");
        }

        if (differences[17] == pos) {
            tvStatsResupplies[pos].setText(Html.fromHtml("<b>" + ps.getNumResupplies() + "</b>"));
        }
        else {
            tvStatsResupplies[pos].setText(ps.getNumResupplies() + "");
        }

        if (differences[18] == pos) {
            tvStatsDeath[pos].setText(Html.fromHtml("<b>" + ps.getNumDeaths() + "</b>"));
        }
        else {
            tvStatsDeath[pos].setText(ps.getNumDeaths() + "");
        }

        if (differences[19] == pos) {
            tvStatsWins[pos].setText(Html.fromHtml("<b>" + ps.getNumWins() + "</b>"));
        }
        else {
            tvStatsWins[pos].setText(ps.getNumWins() + "");
        }

        if (differences[20] == pos) {
            tvStatsLosses[pos].setText(Html.fromHtml("<b>" + ps.getNumLosses() + "</b>"));
        }
        else {
            tvStatsLosses[pos].setText(ps.getNumLosses() + "");
        }

        if (differences[21] == pos) {
            tvStatsKDR[pos].setText(Html.fromHtml("<b>" + ps.getKDRatio() + "</b>"));
        }
        else {
            tvStatsKDR[pos].setText(ps.getKDRatio() + "");
        }

        if (differences[22] == pos) {
            tvStatsAccuracy[pos].setText(Html.fromHtml("<b>" + ps.getAccuracy() + "%</b>"));
        }
        else {
            tvStatsAccuracy[pos].setText(ps.getAccuracy() + "%");
        }

        if (differences[23] == pos) {
            tvStatsSkill[pos].setText(Html.fromHtml("<b>" + ps.getSkill() + "</b>"));
        }
        else {
            tvStatsSkill[pos].setText(ps.getSkill() + "");
        }

        if (differences[24] == pos) {
            tvStatsLKS[pos].setText(Html.fromHtml("<b>" + ps.getLongestKS() + "</b>"));
        }
        else {
            tvStatsLKS[pos].setText(ps.getLongestKS() + "");
        }

        if (differences[25] == pos) {
            tvStatsLHS[pos].setText(Html.fromHtml("<b>" + ps.getLongestHS() + " m</b>"));
        }
        else {
            tvStatsLHS[pos].setText(ps.getLongestHS() + " m");
        }

        if (differences[26] == pos) {
            tvStatsSPM[pos].setText(Html.fromHtml("<b>" + ps.getScorePerMinute() + "</b>"));
        }
        else {
            tvStatsSPM[pos].setText(ps.getScorePerMinute() + "");
        }

        // Score
        if (differences[27] == pos) {
            tvScoreAssault[pos].setText(Html.fromHtml("<b>" + ps.getScoreAssault() + "</b>"));
        }
        else {
            tvScoreAssault[pos].setText(ps.getScoreAssault() + "");
        }

        if (differences[28] == pos) {
            tvScoreEngineer[pos].setText(Html.fromHtml("<b>" + ps.getScoreEngineer() + "</b>"));
        }
        else {
            tvScoreEngineer[pos].setText(ps.getScoreEngineer() + "");
        }

        if (differences[29] == pos) {
            tvScoreSupport[pos].setText(Html.fromHtml("<b>" + ps.getScoreSupport() + "</b>"));
        }
        else {
            tvScoreSupport[pos].setText(ps.getScoreSupport() + "");
        }

        if (differences[30] == pos) {
            tvScoreRecon[pos].setText(Html.fromHtml("<b>" + ps.getScoreRecon() + "</b>"));
        }
        else {
            tvScoreRecon[pos].setText(ps.getScoreRecon() + "");
        }

        if (differences[31] == pos) {
            tvScoreVehicles[pos].setText(Html.fromHtml("<b>" + ps.getScoreVehicles() + "</b>"));
        }
        else {
            tvScoreVehicles[pos].setText(ps.getScoreVehicles() + "");
        }

        if (differences[32] == pos) {
            tvScoreCombat[pos].setText(Html.fromHtml("<b>" + ps.getScoreCombat() + "</b>"));
        }
        else {
            tvScoreCombat[pos].setText(ps.getScoreCombat() + "");
        }

        if (differences[33] == pos) {
            tvScoreAward[pos].setText(Html.fromHtml("<b>" + ps.getScoreAwards() + "</b>"));
        }
        else {
            tvScoreAward[pos].setText(ps.getScoreAwards() + "");
        }

        if (differences[34] == pos) {
            tvScoreUnlocks[pos].setText(Html.fromHtml("<b>" + ps.getScoreUnlocks() + "</b>"));
        }
        else {
            tvScoreUnlocks[pos].setText(ps.getScoreUnlocks() + "");
        }

        if (differences[35] == pos) {
            tvScoreTotal[pos].setText(Html.fromHtml("<b>" + ps.getScoreTotal() + "</b>"));
        }
        else {
            tvScoreTotal[pos].setText(ps.getScoreTotal() + "");
        }

        /* SPECIAL CASES */

        if (differences[36] == pos) {
            tvStatsWLR[pos].setText(Html.fromHtml("<b>" + ps.getWLRatio() + "</b>"));
        }
        else {
            tvStatsWLR[pos].setText(ps.getWLRatio() + "");
        }

    }

    public void reload() {

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    public Menu prepareOptionsMenu(Menu menu) {

        return menu;

    }

    public boolean handleSelectedOption(MenuItem item) {

        return true;

    }

    public void showStats(Map<Long, PersonaStats> ps, long id, int pos, boolean toggle) {

        // Let's overwrite
        personaStats.putAll(ps);

        // Update the selected persona
        selectedPersona[pos] = id;

        // Calculate differences
        if (selectedPersona[0] > 0 && selectedPersona[1] > 0 && (numCalls == 2 || toggle)) {

            // Detect the differences
            detectDifferences();

            // Here's what we're gonna do
            populateStats(personaStats.get(selectedPersona[0]), 0);
            populateStats(personaStats.get(selectedPersona[1]), 1);

            // Zero it
            numCalls = 1;

        } else {

            numCalls = 2;

        }

    }

    public void detectDifferences() {

        // Let's do it this way
        PersonaStats[] personas = new PersonaStats[] {
                personaStats.get(selectedPersona[0]), personaStats.get(selectedPersona[1])
        };
        String[] left = personas[0].toStringArray();
        String[] right = personas[1].toStringArray();
        int numItems = left.length;

        // Is it empty?
        if (differences == null) {

            differences = new int[numItems + 1]; // +1 => WLR

        }

        // Iterate (from index #5 to skip the names)
        for (int counter = 5, max = left.length; counter < max; counter++) {

            // Log.d(Constants.DEBUG_TAG, "#" + counter + ": " +
            // Double.valueOf(left[counter]) + " > " + Double.valueOf(
            // right[counter] ) + " == " + (Double.valueOf(left[counter]) >
            // Double.valueOf( right[counter] )) );
            differences[counter] = Double.valueOf(left[counter]) > Double.valueOf(right[counter]) ? 0
                    : 1;

        }

        // Setup the WLR part
        differences[numItems] = personas[0].getWLRatio() > personas[1].getWLRatio() ? 0 : 1;
    }

}
