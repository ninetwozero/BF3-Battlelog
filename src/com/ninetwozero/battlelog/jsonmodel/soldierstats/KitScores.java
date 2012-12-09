package com.ninetwozero.battlelog.jsonmodel.soldierstats;

import com.google.gson.annotations.SerializedName;

public class KitScores {

    @SerializedName("1")
    private long assaultScore;
    @SerializedName("2")
    private long engineerScore;
    @SerializedName("8")
    private long reconScore;
    @SerializedName("32")
    private long supportScore;

    private KitScores(long assaultScore, long engineerScore, long supportScore,
                      long reconScore) {
        this.assaultScore = assaultScore;
        this.engineerScore = engineerScore;
        this.supportScore = supportScore;
        this.reconScore = reconScore;
    }

    public long getAssaultScore() {
        return assaultScore;
    }

    public void setAssaultScore(long assaultScore) {
        this.assaultScore = assaultScore;
    }

    public long getEngineerScore() {
        return engineerScore;
    }

    public void setEngineerScore(long engineerScore) {
        this.engineerScore = engineerScore;
    }

    public long getSupportScore() {
        return supportScore;
    }

    public void setSupportScore(long supportScore) {
        this.supportScore = supportScore;
    }

    public long getReconScore() {
        return reconScore;
    }

    public void setReconScore(long reconScore) {
        this.reconScore = reconScore;
    }
}
