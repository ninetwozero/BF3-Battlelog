package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.User;

import java.util.HashMap;
import java.util.Map;

public class PlatoonStat {

    @SerializedName("memberStats")
    private MemberStats memberStats;
    @SerializedName("platoonPersonas")
    private Map<Long, User> platoonMembers = new HashMap<Long, User>();

    public PlatoonStat(MemberStats memberStats, Map<Long, User> platoonMembers) {
        this.memberStats = memberStats;
        this.platoonMembers = platoonMembers;
    }

    public MemberStats getMemberStats() {
        return memberStats;
    }

    public Map<Long, User> getPlatoonMembers() {
        return platoonMembers;
    }
}
