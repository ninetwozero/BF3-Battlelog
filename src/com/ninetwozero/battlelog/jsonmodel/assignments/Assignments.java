package com.ninetwozero.battlelog.jsonmodel.assignments;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Assignments {

    @SerializedName("bf3GadgetsLocale")
    private Gadgets gadgets;
    @SerializedName("personaId")
    private int personaId;
    @SerializedName("missionTrees")
    private Map<Integer, MissionPacks> missionPacksList = new HashMap<Integer, MissionPacks>();

}
