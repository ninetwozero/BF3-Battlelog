package com.ninetwozero.bf3droid.jsonmodel.assignments;

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
    @SerializedName("appearanceUnlock")
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

    public Unlock getUpcomingUnlock() {
        return null;
    }

    public String getUnlockId() {
        if (weaponAddonUnlock != null) {
            return weaponAddonUnlock.getUnlockId();
        } else if (soldierSpecializationUnlock != null) {
            return soldierSpecializationUnlock.getUnlockId();
        } else if (dogTagUnlock != null) {
            return dogTagUnlock.getNameSID();
        } else if (weaponUnlock != null) {
            return weaponUnlock.getUnlockId();
        } else if (vehicleAddonUnlock != null) {
            return vehicleAddonUnlock.getUnlockId();
        } else if (appearanceUnlock != null) {
            return appearanceUnlock.getUnlockId();
        } else if (kitItemUnlock != null) {
            return kitItemUnlock.getUnlockId();
        } else {
            return "Unlock not found";
        }
    }

    public class Unlock {
        @SerializedName("unlockId")
        private String unlockId;
        @SerializedName("guid")
        private String guid;
        @SerializedName("nameSID")
        private String nameSID;

        public String getUnlockId() {
            return unlockId;
        }

        public String getGuid() {
            return guid;
        }

        public String getNameSID() {
            return nameSID;
        }
    }
}
