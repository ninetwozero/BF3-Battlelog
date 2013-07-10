package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;

public class PlatoonStat {

    @SerializedName("memberStats")
    private MemberStats memberStats;
    /*@SerializedName("platoonPersonas")
    private List<PlatoonPersona> personas;*/

    public PlatoonStat(MemberStats memberStats) {
        this.memberStats = memberStats;
    }

    public MemberStats getMemberStats() {
        return memberStats;
    }
}
