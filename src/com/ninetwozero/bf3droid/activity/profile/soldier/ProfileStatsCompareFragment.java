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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.*;
import android.widget.TextView;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.PersonaStats;

import java.util.HashMap;
import java.util.Map;

public class ProfileStatsCompareFragment extends Fragment implements DefaultFragment {

    // Attributes
    private LayoutInflater mLayoutInflater;
    private long[] mSelectedPersona;
    private Map<Long, PersonaStats> mPersonaStats;
    private int[] mDifferences;
    private int mNumCalls;

    // These are the different fields
    private final int FIELD_PERSONA[] = new int[]{
            R.id.string_persona_0,
            R.id.string_persona_1
    };
    private final int FIELD_RANK[] = new int[]{
            R.id.string_rank_short_0,
            R.id.string_rank_short_1
    };
    private final int FIELD_PROGRESS_CURR[] = new int[]{
            R.id.string_progress_curr_0, R.id.string_progress_curr_1
    };
    private final int FIELD_PROGRESS_MAX[] = new int[]{
            R.id.string_progress_max_0, R.id.string_progress_max_1
    };

    private final int FIELD_SCORE_ASSAULT[] = new int[]{
            R.id.string_score_assault_0, R.id.string_score_assault_1
    };
    private final int FIELD_SCORE_ENGINEER[] = new int[]{
            R.id.string_score_engineer_0, R.id.string_score_engineer_1
    };
    private final int FIELD_SCORE_SUPPORT[] = new int[]{
            R.id.string_score_support_0, R.id.string_score_support_1
    };
    private final int FIELD_SCORE_RECON[] = new int[]{
            R.id.string_score_recon_0, R.id.string_score_recon_1
    };
    private final int FIELD_SCORE_VEHICLES[] = new int[]{
            R.id.string_score_vehicles_0, R.id.string_score_vehicles_1
    };
    private final int FIELD_SCORE_COMBAT[] = new int[]{
            R.id.string_score_combat_0, R.id.string_score_combat_1
    };
    private final int FIELD_SCORE_AWARD[] = new int[]{
            R.id.string_score_award_0, R.id.string_score_award_1
    };
    private final int FIELD_SCORE_UNLOCKS[] = new int[]{
            R.id.string_score_unlock_0, R.id.string_score_unlock_1
    };
    private final int FIELD_SCORE_TOTAL[] = new int[]{
            R.id.string_score_total_0, R.id.string_score_total_1
    };

    private final int FIELD_STATS_KILLS[] = new int[]{
            R.id.string_stats_kills_0, R.id.string_stats_kills_1
    };
    private final int FIELD_STATS_ASSISTS[] = new int[]{
            R.id.string_stats_assists_0, R.id.string_stats_assists_1
    };
    private final int FIELD_STATS_VKILLS[] = new int[]{
            R.id.string_stats_vkills_0, R.id.string_stats_vkills_1
    };
    private final int FIELD_STATS_VASSISTS[] = new int[]{
            R.id.string_stats_vassists_0, R.id.string_stats_vassists_1
    };
    private final int FIELD_STATS_HEALS[] = new int[]{
            R.id.string_stats_heals_0, R.id.string_stats_heals_1
    };
    private final int FIELD_STATS_REVIVES[] = new int[]{
            R.id.string_stats_revives_0, R.id.string_stats_revives_1
    };
    private final int FIELD_STATS_REPAIRS[] = new int[]{
            R.id.string_stats_repairs_0, R.id.string_stats_repairs_1
    };
    private final int FIELD_STATS_RESUPPLIES[] = new int[]{
            R.id.string_stats_resupplies_0, R.id.string_stats_resupplies_1
    };
    private final int FIELD_STATS_DEATHS[] = new int[]{
            R.id.string_stats_deaths_0, R.id.string_stats_deaths_1
    };
    private final int FIELD_STATS_KDR[] = new int[]{
            R.id.string_stats_kdr_0,
            R.id.string_stats_kdr_1
    };
    private final int FIELD_STATS_WINS[] = new int[]{
            R.id.string_stats_wins_0,
            R.id.string_stats_wins_1
    };
    private final int FIELD_STATS_LOSSES[] = new int[]{
            R.id.string_stats_losses_0, R.id.string_stats_losses_1
    };
    private final int FIELD_STATS_WLR[] = new int[]{
            R.id.string_stats_wlr_0,
            R.id.string_stats_wlr_1
    };
    private final int FIELD_STATS_ACCURACY[] = new int[]{
            R.id.string_stats_accuracy_0, R.id.string_stats_accuracy_1
    };
    private final int FIELD_STATS_TIME[] = new int[]{
            R.id.string_stats_time_0,
            R.id.string_stats_time_1
    };
    private final int FIELD_STATS_SKILLS[] = new int[]{
            R.id.string_stats_skill_0, R.id.string_stats_skill_1
    };
    private final int FIELD_STATS_SPM[] = new int[]{
            R.id.string_stats_spm_0,
            R.id.string_stats_spm_1
    };
    private final int FIELD_STATS_LKS[] = new int[]{
            R.id.string_stats_lks_0,
            R.id.string_stats_lks_1
    };
    private final int FIELD_STATS_LHS[] = new int[]{
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
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.tab_content_compare,
                container, false);

        // Init
        initFragment(view);

        // Return
        return view;

    }

    public void initFragment(View view) {

        mPersonaStats = new HashMap<Long, PersonaStats>();
        mSelectedPersona = new long[2];

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

        if (mDifferences[6] == pos) {
            tvRank[pos].setText(Html.fromHtml("<b>" + ps.getRankId() + "</b>"));
        } else {
            tvRank[pos].setText(String.valueOf(ps.getRankId()));
        }

        // Progress
        tvProgressCurr[pos].setText(String.valueOf(ps.getPointsProgressLvl()));
        tvProgressMax[pos].setText(String.valueOf(ps.getPointsNeededToLvlUp()));

        // Stats
        if (mDifferences[9] == pos) {
            tvStatsTime[pos].setText(Html.fromHtml("<b>" + ps.getTimePlayedString() + "</b>"));
        } else {
            tvStatsTime[pos].setText(ps.getTimePlayedString());
        }

        if (mDifferences[10] == pos) {
            tvStatsKills[pos].setText(Html.fromHtml("<b>" + ps.getNumKills() + "</b>"));
        } else {
            tvStatsKills[pos].setText(String.valueOf(ps.getNumKills()));
        }

        if (mDifferences[11] == pos) {
            tvStatsAssists[pos].setText(Html.fromHtml("<b>" + ps.getNumAssists() + "</b>"));
        } else {
            tvStatsAssists[pos].setText(String.valueOf(ps.getNumAssists()));
        }

        if (mDifferences[12] == pos) {
            tvStatsVKills[pos].setText(Html.fromHtml("<b>" + ps.getNumVehicles() + "</b>"));
        } else {
            tvStatsVKills[pos].setText(String.valueOf(ps.getNumVehicles()));
        }

        if (mDifferences[13] == pos) {
            tvStatsVAssists[pos].setText(Html.fromHtml("<b>" + ps.getNumVehicleAssists() + "</b>"));
        } else {
            tvStatsVAssists[pos].setText(String.valueOf(ps.getNumVehicleAssists()));
        }

        if (mDifferences[14] == pos) {
            tvStatsHeals[pos].setText(Html.fromHtml("<b>" + ps.getNumHeals() + "</b>"));
        } else {
            tvStatsHeals[pos].setText(String.valueOf(ps.getNumHeals()));
        }

        if (mDifferences[15] == pos) {
            tvStatsRevives[pos].setText(Html.fromHtml("<b>" + ps.getNumRevives() + "</b>"));
        } else {
            tvStatsRevives[pos].setText(String.valueOf(ps.getNumRevives()));
        }

        if (mDifferences[16] == pos) {
            tvStatsRepairs[pos].setText(Html.fromHtml("<b>" + ps.getNumRepairs() + "</b>"));
        } else {
            tvStatsRepairs[pos].setText(String.valueOf(ps.getNumRepairs()));
        }

        if (mDifferences[17] == pos) {
            tvStatsResupplies[pos].setText(Html.fromHtml("<b>" + ps.getNumResupplies() + "</b>"));
        } else {
            tvStatsResupplies[pos].setText(String.valueOf(ps.getNumResupplies()));
        }

        if (mDifferences[18] == pos) {
            tvStatsDeath[pos].setText(Html.fromHtml("<b>" + ps.getNumDeaths() + "</b>"));
        } else {
            tvStatsDeath[pos].setText(String.valueOf(ps.getNumDeaths()));
        }

        if (mDifferences[19] == pos) {
            tvStatsWins[pos].setText(Html.fromHtml("<b>" + ps.getNumWins() + "</b>"));
        } else {
            tvStatsWins[pos].setText(String.valueOf(ps.getNumWins()));
        }

        if (mDifferences[20] == pos) {
            tvStatsLosses[pos].setText(Html.fromHtml("<b>" + ps.getNumLosses() + "</b>"));
        } else {
            tvStatsLosses[pos].setText(String.valueOf(ps.getNumLosses()));
        }

        if (mDifferences[21] == pos) {
            tvStatsKDR[pos].setText(Html.fromHtml("<b>" + ps.getKDRatio() + "</b>"));
        } else {
            tvStatsKDR[pos].setText(String.valueOf(ps.getKDRatio()));
        }

        if (mDifferences[22] == pos) {
            tvStatsAccuracy[pos].setText(Html.fromHtml("<b>" + ps.getAccuracy() + "%</b>"));
        } else {
            tvStatsAccuracy[pos].setText(ps.getAccuracy() + "%");
        }

        if (mDifferences[23] == pos) {
            tvStatsSkill[pos].setText(Html.fromHtml("<b>" + ps.getSkill() + "</b>"));
        } else {
            tvStatsSkill[pos].setText(String.valueOf(ps.getSkill()));
        }

        if (mDifferences[24] == pos) {
            tvStatsLKS[pos].setText(Html.fromHtml("<b>" + ps.getLongestKS() + "</b>"));
        } else {
            tvStatsLKS[pos].setText(String.valueOf(ps.getLongestKS()));
        }

        if (mDifferences[25] == pos) {
            tvStatsLHS[pos].setText(Html.fromHtml("<b>" + ps.getLongestHS() + " m</b>"));
        } else {
            tvStatsLHS[pos].setText(ps.getLongestHS() + " m");
        }

        if (mDifferences[26] == pos) {
            tvStatsSPM[pos].setText(Html.fromHtml("<b>" + ps.getScorePerMinute() + "</b>"));
        } else {
            tvStatsSPM[pos].setText(String.valueOf(ps.getScorePerMinute()));
        }

        // Score
        if (mDifferences[27] == pos) {
            tvScoreAssault[pos].setText(Html.fromHtml("<b>" + ps.getScoreAssault() + "</b>"));
        } else {
            tvScoreAssault[pos].setText(String.valueOf(ps.getScoreAssault()));
        }

        if (mDifferences[28] == pos) {
            tvScoreEngineer[pos].setText(Html.fromHtml("<b>" + ps.getScoreEngineer() + "</b>"));
        } else {
            tvScoreEngineer[pos].setText(String.valueOf(ps.getScoreEngineer()));
        }

        if (mDifferences[29] == pos) {
            tvScoreSupport[pos].setText(Html.fromHtml("<b>" + ps.getScoreSupport() + "</b>"));
        } else {
            tvScoreSupport[pos].setText(String.valueOf(ps.getScoreSupport()));
        }

        if (mDifferences[30] == pos) {
            tvScoreRecon[pos].setText(Html.fromHtml("<b>" + ps.getScoreRecon() + "</b>"));
        } else {
            tvScoreRecon[pos].setText(String.valueOf(ps.getScoreRecon()));
        }

        if (mDifferences[31] == pos) {
            tvScoreVehicles[pos].setText(Html.fromHtml("<b>" + ps.getScoreVehicles() + "</b>"));
        } else {
            tvScoreVehicles[pos].setText(String.valueOf(ps.getScoreVehicles()));
        }

        if (mDifferences[32] == pos) {
            tvScoreCombat[pos].setText(Html.fromHtml("<b>" + ps.getScoreCombat() + "</b>"));
        } else {
            tvScoreCombat[pos].setText(String.valueOf(ps.getScoreCombat()));
        }

        if (mDifferences[33] == pos) {
            tvScoreAward[pos].setText(Html.fromHtml("<b>" + ps.getScoreAwards() + "</b>"));
        } else {
            tvScoreAward[pos].setText(String.valueOf(ps.getScoreAwards()));
        }

        if (mDifferences[34] == pos) {
            tvScoreUnlocks[pos].setText(Html.fromHtml("<b>" + ps.getScoreUnlocks() + "</b>"));
        } else {
            tvScoreUnlocks[pos].setText(String.valueOf(ps.getScoreUnlocks()));
        }

        if (mDifferences[35] == pos) {
            tvScoreTotal[pos].setText(Html.fromHtml("<b>" + ps.getScoreTotal() + "</b>"));
        } else {
            tvScoreTotal[pos].setText(String.valueOf(ps.getScoreTotal()));
        }

        /* SPECIAL CASES */

        if (mDifferences[36] == pos) {
            tvStatsWLR[pos].setText(Html.fromHtml("<b>" + ps.getWLRatio() + "</b>"));
        } else {
            tvStatsWLR[pos].setText(String.valueOf(ps.getWLRatio()));
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
        mPersonaStats.putAll(ps);

        // Update the selected persona
        mSelectedPersona[pos] = id;

        // Calculate differences
        if (mSelectedPersona[0] > 0 && mSelectedPersona[1] > 0 && (mNumCalls == 2 || toggle)) {

            // Detect the differences
            detectDifferences();

            // Here's what we're gonna do
            populateStats(mPersonaStats.get(mSelectedPersona[0]), 0);
            populateStats(mPersonaStats.get(mSelectedPersona[1]), 1);

            // Zero it
            mNumCalls = 1;

        } else {

            mNumCalls = 2;

        }

    }

    public void detectDifferences() {

        // Let's do it this way
        PersonaStats[] personas = new PersonaStats[]{
                mPersonaStats.get(mSelectedPersona[0]), mPersonaStats.get(mSelectedPersona[1])
        };
        Object[] left = personas[0].toArray();
        Object[] right = personas[1].toArray();
        int numItems = left.length;

        // Is it empty?
        if (mDifferences == null) {

            mDifferences = new int[numItems + 1]; // +1 => WLR

        }

        // Iterate (from index #5 to skip the names)
        for (int counter = 5, max = left.length; counter < max; counter++) {

            double valueLeft = Double.parseDouble(left[counter].toString());
            double valueRight = Double.parseDouble(right[counter].toString());
            mDifferences[counter] = valueLeft > valueRight ? 0 : 1;

        }

        // Setup the WLR part
        mDifferences[numItems] = personas[0].getWLRatio() > personas[1].getWLRatio() ? 0 : 1;
    }

}
