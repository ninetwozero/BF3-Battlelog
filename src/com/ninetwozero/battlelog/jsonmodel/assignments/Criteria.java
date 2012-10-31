package com.ninetwozero.battlelog.jsonmodel.assignments;

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
}
