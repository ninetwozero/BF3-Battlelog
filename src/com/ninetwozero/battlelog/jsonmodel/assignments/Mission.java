package com.ninetwozero.battlelog.jsonmodel.assignments;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Mission {

    @SerializedName("code")
    private String code;
    @SerializedName("completion")
    private int completion;
    @SerializedName("criterias")
    private List<Criteria> criterias = new ArrayList<Criteria>();
    @SerializedName("dependencies")
    private List<MissionDependency> missionDependencies = new ArrayList<MissionDependency>();

    public String getCode() {
        return code;
    }

    public int getCompletion() {
        return completion;
    }

    public List<Criteria> getCriterias() {
        return criterias;
    }

    public List<MissionDependency> getMissionDependencies() {
        return missionDependencies;
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
