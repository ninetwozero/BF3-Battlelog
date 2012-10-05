package com.ninetwozero.battlelog.jsonmodel.assignments;

import com.google.gson.annotations.SerializedName;

public class MissionPacks {

    @SerializedName("count")
    private int count;
    @SerializedName("gameExpansion")
    private int gameExpansion;
    @SerializedName("completedCount")
    private int completedCount;
    @SerializedName("layout")
    private String layout;
}
