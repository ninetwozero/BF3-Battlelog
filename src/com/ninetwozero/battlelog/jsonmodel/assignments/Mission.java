package com.ninetwozero.battlelog.jsonmodel.assignments;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Mission {

    @SerializedName("completion")
    private int completion;
    @SerializedName("criterias")
    private List<Criteria> criterias = new ArrayList<Criteria>();
}
