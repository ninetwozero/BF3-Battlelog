package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlatoonStat {

    @SerializedName("membersStats")
    private MemberStats membersStats;
    /*@SerializedName("platoonPersonas")
    private List<PlatoonPersona> personas;*/

    public PlatoonStat(MemberStats membersStats) {
        this.membersStats = membersStats;
    }

    public MemberStats getMembersStats() {
        return membersStats;
    }
}
