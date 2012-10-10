package com.ninetwozero.battlelog.jsonmodel.assignments;

import com.google.gson.annotations.SerializedName;

public class Criteria {

    @SerializedName("completion")
    private int completion;
    @SerializedName("actualValue")
    private double actualValue;
    @SerializedName("completionValue")
    private double completionValue;
    @SerializedName("statCode")
    private String statCode;
}
