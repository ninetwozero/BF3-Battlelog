package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;

public class MemberStats {

    @SerializedName("kitsVehicles")
    private KitsVehicles kitsVehicles;
    /*@SerializedName("topPlayers")
    private TopPlayers topPlayers;
    @SerializedName("general")
    private GeneralStats generalStats;*/

    public MemberStats(KitsVehicles kitsVehicles/*, TopPlayers topPlayers, GeneralStats generalStats*/) {
        this.kitsVehicles = kitsVehicles;
        /*this.topPlayers = topPlayers;
        this.generalStats = generalStats;*/
    }

    public KitsVehicles getKitsVehicles() {
        return kitsVehicles;
    }

    /*public TopPlayers getTopPlayers() {
        return topPlayers;
    }

    public GeneralStats getGeneralStats() {
        return generalStats;
    }*/
}
