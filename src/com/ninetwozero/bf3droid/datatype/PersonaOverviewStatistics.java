package com.ninetwozero.bf3droid.datatype;

import com.ninetwozero.bf3droid.provider.table.RankProgress;

import java.util.Map;

public class PersonaOverviewStatistics {

    private RankProgress rankProgress;
    private Map<String, Statistics> personaStats;
    private Map<String, Statistics> scoreStats;

    public PersonaOverviewStatistics(RankProgress rankProgress, Map<String, Statistics> personaStats, Map<String, Statistics> scoreStats) {
        this.rankProgress = rankProgress;
        this.personaStats = personaStats;
        this.scoreStats = scoreStats;
    }

    public RankProgress getRankProgress() {
        return rankProgress;
    }

    public Map<String, Statistics> getPersonaStats() {
        return personaStats;
    }

    public Map<String, Statistics> getScoreStats() {
        return scoreStats;
    }

    public boolean isEmpty(){
        return (rankProgress == null || personaStats.isEmpty() || scoreStats.isEmpty())? true : false;
    }
}
