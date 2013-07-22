package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class KitsVehicles {

    @SerializedName("scorePerMinute")
    private Map<String, PlatoonScore> scorePerMinute;
    @SerializedName("score")
    private Map<String, PlatoonScore> score;
    @SerializedName("time")
    private Map<String, PlatoonScore> time;

    public KitsVehicles(Map<String, PlatoonScore> scorePerMinute, Map<String, PlatoonScore> score, Map<String, PlatoonScore> time) {
        this.scorePerMinute = scorePerMinute;
        this.score = score;
        this.time = time;
    }

    public Map<String, PlatoonScore> getScorePerMinute() {
        return scorePerMinute;
    }

    public Map<String, PlatoonScore> getScore() {
        return score;
    }

    public Map<String, PlatoonScore> getTime() {
        return time;
    }
}
