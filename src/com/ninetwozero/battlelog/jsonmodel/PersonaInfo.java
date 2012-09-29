package com.ninetwozero.battlelog.jsonmodel;

import com.google.gson.annotations.SerializedName;

public class PersonaInfo {

    @SerializedName("overviewStats")
    private PersonaStatsOverview statsOverview;
    @SerializedName("currentRankNeeded")
    private Rank currentRank;
    @SerializedName("rankNeeded")
    private Rank nextRank;
    @SerializedName("personaId")
    private long personaId;
    @SerializedName("user")
    private User user;
    @SerializedName("platformInt")
    private int platform;

    public PersonaInfo(PersonaStatsOverview statsOverview, Rank currentRank, Rank nextRank,
                       long personaId, User user,
                       int platform) {
        this.statsOverview = statsOverview;
        this.currentRank = currentRank;
        this.nextRank = nextRank;
        this.personaId = personaId;
        this.user = user;
        this.platform = platform;
    }

    public PersonaStatsOverview getStatsOverview() {
        return statsOverview;
    }

    public void setStatsOverview(PersonaStatsOverview statsOverview) {
        this.statsOverview = statsOverview;
    }

    public Rank getCurrentRank() {
        return currentRank;
    }

    public void setCurrentRank(Rank currentRank) {
        this.currentRank = currentRank;
    }

    public Rank getNextRank() {
        return nextRank;
    }

    public void setNextRank(Rank nextRank) {
        this.nextRank = nextRank;
    }

    public long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(long personaId) {
        this.personaId = personaId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }
}
