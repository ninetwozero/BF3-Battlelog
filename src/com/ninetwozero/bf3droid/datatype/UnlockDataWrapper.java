/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.datatype;

import java.util.List;

public class UnlockDataWrapper {

    // Attributes
    private final List<UnlockData> mWeapons;
    private final List<UnlockData> mAttachments;
    private final List<UnlockData> mKitUnlocks;
    private final List<UnlockData> mVehicleUpgrades;
    private final List<UnlockData> mSkills;
    private final List<UnlockData> mUnlocks;

    // Construct
    public UnlockDataWrapper(

            List<UnlockData> w, List<UnlockData> a, List<UnlockData> k,
            List<UnlockData> v, List<UnlockData> s,
            List<UnlockData> u

    ) {

        mWeapons = w;
        mAttachments = a;
        mKitUnlocks = k;
        mVehicleUpgrades = v;
        mSkills = s;
        mUnlocks = u;

    }

    // Getters
    public List<UnlockData> getWeapons() {
        return mWeapons;
    }

    public List<UnlockData> getAttachments() {
        return mAttachments;
    }

    public List<UnlockData> getKitUnlocks() {
        return mKitUnlocks;
    }

    public List<UnlockData> getVehicleUpgrades() {
        return mVehicleUpgrades;
    }

    public List<UnlockData> getSkills() {
        return mSkills;
    }

    public List<UnlockData> getUnlocks() {
        return mUnlocks;
    }

}
