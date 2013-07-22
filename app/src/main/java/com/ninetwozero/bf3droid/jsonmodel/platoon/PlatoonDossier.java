package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class PlatoonDossier {
    @SerializedName("platoon")
    private Platoon platoon;
    @SerializedName("members")
    private Map<Long, PlatoonMember> members = new HashMap<Long, PlatoonMember>();

    public PlatoonDossier(Platoon platoon, Map<Long, PlatoonMember> members){
        this.platoon = platoon;
    }

    public Platoon getPlatoon() {
        return platoon;
    }

    public Map<Long, PlatoonMember> getMembers() {
        return members;
    }
}
