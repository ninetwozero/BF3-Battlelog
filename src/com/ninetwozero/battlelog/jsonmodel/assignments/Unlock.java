package com.ninetwozero.battlelog.jsonmodel.assignments;

import com.google.gson.annotations.SerializedName;

public class Unlock {
    @SerializedName("unlockId")
    private String unlockId;
    @SerializedName("guid")
    private String guid;

    public String getUnlockId() {
        return unlockId;
    }

    public String getGuid() {
        return guid;
    }
}
