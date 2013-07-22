package com.ninetwozero.bf3droid.jsonmodel.assignments;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Assignments {

    @SerializedName("bf3GadgetsLocale")
    private Gadgets gadgets;
    @SerializedName("personaId")
    private int personaId;
    @SerializedName("missionTrees")
    private Map<Integer, MissionPack> missionPacksList = new HashMap<Integer, MissionPack>();


    public Assignments(Gadgets gadgets, int personaId, Map<Integer, MissionPack> missionPacksList) {
        this.gadgets = gadgets;
        this.personaId = personaId;
        this.missionPacksList = missionPacksList;
    }

    public Gadgets getGadgets() {
        return gadgets;
    }

    public int getPersonaId() {
        return personaId;
    }

    public Map<Integer, MissionPack> getMissionPacksList() {
        return missionPacksList;
    }
}
