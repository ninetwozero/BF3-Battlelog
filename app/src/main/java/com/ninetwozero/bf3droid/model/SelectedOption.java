package com.ninetwozero.bf3droid.model;

public class SelectedOption {

    public static final String PERSONA = "persona";
    public static final String PLATOON = "platoon";

    private final long selectedId;
    private final String changedGroup;

    public SelectedOption(long selectedId, String changedGroup) {
        this.selectedId = selectedId;
        this.changedGroup = changedGroup;
    }

    public long getSelectedId() {
        return selectedId;
    }

    public String getChangedGroup() {
        return changedGroup;
    }
}
