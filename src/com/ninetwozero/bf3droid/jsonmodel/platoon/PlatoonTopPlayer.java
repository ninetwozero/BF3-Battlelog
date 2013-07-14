package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;

public class PlatoonTopPlayer {
    @SerializedName("personaId")
    private long personaId;
    @SerializedName("spm")
    private int scorePerMinute;

    public PlatoonTopPlayer(long personaId, int scorePerMinute) {
        this.personaId = personaId;
        this.scorePerMinute = scorePerMinute;
    }

    public long getPersonaId() {
        return personaId;
    }

    public int getScorePerMinute() {
        return scorePerMinute;
    }
}
