package com.ninetwozero.bf3droid.jsonmodel.assignments;

import com.google.gson.annotations.SerializedName;

public class Criteria {

    @SerializedName("descriptionID")
    private String descriptionId;
    @SerializedName("completion")
    private int completion;
    @SerializedName("actualValue")
    private double actualValue;
    @SerializedName("completionValue")
    private double completionValue;
    @SerializedName("statCode")
    private String statCode;
    @SerializedName("unit")
    private String unit;

    public String getDescriptionId() {
        return descriptionId;
    }

    public int getCompletion() {
        return completion;
    }

    public double getActualValue() {
        return actualValue;
    }

    public double getCompletionValue() {
        return completionValue;
    }

    public String getStatCode() {
        return statCode;
    }

    public String getUnit() {
        return unit;
    }
}
