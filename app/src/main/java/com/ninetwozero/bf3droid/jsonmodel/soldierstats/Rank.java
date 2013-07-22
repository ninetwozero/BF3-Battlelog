package com.ninetwozero.bf3droid.jsonmodel.soldierstats;

import com.google.gson.annotations.SerializedName;

public class Rank {

    @SerializedName("level")
    private int level;
    @SerializedName("pointsNeeded")
    private long rankPoints;

    public Rank(int level, long rankPoints) {
        this.level = level;
        this.rankPoints = rankPoints;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getRankPoints() {
        return rankPoints;
    }

    public void setRankPoints(long rankPoints) {
        this.rankPoints = rankPoints;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "level=" + level +
                ", rankPoints=" + rankPoints +
                '}';
    }
}
