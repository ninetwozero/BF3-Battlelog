package com.ninetwozero.battlelog.jsonmodel.assignments;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class MissionPacks {

    @SerializedName("count")
    private int count;
    @SerializedName("gameExpansion")
    private int gameExpansion;
    @SerializedName("missions")
    private Map<String, Mission> missions = new HashMap<String, Mission>();
    @SerializedName("completedCount")
    private int completedCount;
    @SerializedName("layout")
    private String layout;
}
