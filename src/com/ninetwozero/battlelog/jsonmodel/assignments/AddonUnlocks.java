package com.ninetwozero.battlelog.jsonmodel.assignments;

import com.google.gson.annotations.SerializedName;

public class AddonUnlocks {
    @SerializedName("weaponAddonUnlock")
    private Unlock weaponAddonUnlock;
    @SerializedName("soldierSpecializationUnlock")
    private Unlock soldierSpecializationUnlock;
    @SerializedName("dogTagUnlock")
    private Unlock dogTagUnlock;
    @SerializedName("weaponUnlock")
    private Unlock weaponUnlock;
    @SerializedName("vehicleAddonUnlock")
    private Unlock vehicleAddonUnlock;
    @SerializedName("appearenceUnlock")
    private Unlock appearanceUnlock;
    @SerializedName("kitItemUnlock")
    private Unlock kitItemUnlock;

    public Unlock getWeaponAddonUnlock() {
        return weaponAddonUnlock;
    }

    public Unlock getSoldierSpecializationUnlock() {
        return soldierSpecializationUnlock;
    }

    public Unlock getDogTagUnlock() {
        return dogTagUnlock;
    }

    public Unlock getWeaponUnlock() {
        return weaponUnlock;
    }

    public Unlock getVehicleAddonUnlock() {
        return vehicleAddonUnlock;
    }

    public Unlock getAppearanceUnlock() {
        return appearanceUnlock;
    }

    public Unlock getKitItemUnlock() {
        return kitItemUnlock;
    }

    public Unlock getUpcomingUnlock(){
        return null;
    }

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
}
