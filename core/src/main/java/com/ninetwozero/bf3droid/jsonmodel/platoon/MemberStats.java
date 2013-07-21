package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class MemberStats {

    @SerializedName("kitsVehicles")
    private KitsVehicles kitsVehicles;
    @SerializedName("topPlayers")
    private Map<String, PlatoonTopPlayer> topPlayers = new HashMap<String, PlatoonTopPlayer>();
    @SerializedName("general")
    private Map<String, PlatoonScore> generalStats = new HashMap<String, PlatoonScore>();

    public MemberStats(KitsVehicles kitsVehicles, Map<String, PlatoonTopPlayer> topPlayers, Map<String, PlatoonScore> generalStats) {
        this.kitsVehicles = kitsVehicles;
        this.topPlayers = topPlayers;
        this.generalStats = generalStats;
    }

    public KitsVehicles getKitsVehicles() {
        return kitsVehicles;
    }

    public Map<String, PlatoonTopPlayer> getTopPlayers() {
        return topPlayers;
    }

    public Map<String, PlatoonScore> getGeneralStats() {
        return generalStats;
    }
}
