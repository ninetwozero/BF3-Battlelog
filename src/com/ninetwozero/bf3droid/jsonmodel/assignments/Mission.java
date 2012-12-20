package com.ninetwozero.bf3droid.jsonmodel.assignments;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Mission {

    @SerializedName("code")
    private String code;
    @SerializedName("completion")
    private int completion;
    @SerializedName("criterias")
    private List<Criteria> criterias = new ArrayList<Criteria>();
    @SerializedName("upcomingUnlocks")
    private List<AddonUnlocks> addonUnlocks = new ArrayList<AddonUnlocks>();
    @SerializedName("dependencies")
    private List<MissionDependency> missionDependencies = new ArrayList<MissionDependency>();
    @SerializedName("stringID")
    private String missionId;
    @SerializedName("active")
    private boolean active;

    public String getCode() {
        return code;
    }

    public int getCompletion() {
        return completion;
    }

    public List<Criteria> getCriterias() {
        return criterias;
    }

    public List<AddonUnlocks> getAddonUnlocks() {
        return addonUnlocks;
    }

    public List<MissionDependency> getMissionDependencies() {
        return missionDependencies;
    }

    public String getMissionId() {
        return missionId;
    }

    public boolean hasDependencies(){
        return missionDependencies.size() > 0;
    }

    public boolean isDependentOn(String code){
        for(MissionDependency dependency : missionDependencies){
            if(dependency.getCode().equals(code)){
                return true;
            }
        }
        return false;
    }

    public boolean isActive() {
        return active;
    }

    public class MissionDependency{
        @SerializedName("count")
        private int count;
        @SerializedName("code")
        private String code;

        public int getCount() {
            return count;
        }

        public String getCode() {
            return code;
        }
    }
}
