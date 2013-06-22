package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;

public class PlatoonDossier {
    @SerializedName("platoon")
    private Platoon platoon;

    public PlatoonDossier(Platoon platoon){
        this.platoon = platoon;
    }

    public Platoon getPlatoon() {
        return platoon;
    }
}
