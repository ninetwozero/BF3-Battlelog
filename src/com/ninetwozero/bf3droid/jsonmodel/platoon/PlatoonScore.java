package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;

public class PlatoonScore {

    @SerializedName("median")
    private int medianScore;
    @SerializedName("average")
    private int averageScore;
    @SerializedName("bestPersonaId")
    private long bestPersonaId;
    @SerializedName("best")
    private int bestScore;
    @SerializedName("min")
    private int minScore;

    public PlatoonScore(int medianScore, int averageScore, long bestPersonaId, int bestScore, int minScore) {
        this.medianScore = medianScore;
        this.averageScore = averageScore;
        this.bestPersonaId = bestPersonaId;
        this.bestScore = bestScore;
        this.minScore = minScore;
    }

    public int getMedianScore() {
        return medianScore;
    }

    public int getAverageScore() {
        return averageScore;
    }

    public long getBestPersonaId() {
        return bestPersonaId;
    }

    public int getBestScore() {
        return bestScore;
    }

    public int getMinScore() {
        return minScore;
    }
}
