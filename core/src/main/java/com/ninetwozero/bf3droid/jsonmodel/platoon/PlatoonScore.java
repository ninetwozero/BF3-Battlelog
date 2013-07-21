package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;

public class PlatoonScore {

    @SerializedName("median")
    private double medianScore;
    @SerializedName("average")
    private double averageScore;
    @SerializedName("bestPersonaId")
    private long bestPersonaId;
    @SerializedName("best")
    private double bestScore;
    @SerializedName("min")
    private double minScore;

    public PlatoonScore(double medianScore, double averageScore, long bestPersonaId, double bestScore, double minScore) {
        this.medianScore = medianScore;
        this.averageScore = averageScore;
        this.bestPersonaId = bestPersonaId;
        this.bestScore = bestScore;
        this.minScore = minScore;
    }

    public double getMedianScore() {
        return medianScore;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public long getBestPersonaId() {
        return bestPersonaId;
    }

    public double getBestScore() {
        return bestScore;
    }

    public double getMinScore() {
        return minScore;
    }
}
