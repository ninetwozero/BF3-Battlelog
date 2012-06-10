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

package com.ninetwozero.battlelog.datatype;

import java.util.List;

public class UnlockDataWrapper {

    // Attributes
    private List<UnlockData> weapons, attachments, kitUnlocks,
            vehicleUpgrades, skills, unlocks;

    // Construct
    public UnlockDataWrapper(

            List<UnlockData> w, List<UnlockData> a, List<UnlockData> k,
            List<UnlockData> v, List<UnlockData> s,
            List<UnlockData> u

    ) {

        weapons = w;
        attachments = a;
        kitUnlocks = k;
        vehicleUpgrades = v;
        skills = s;
        unlocks = u;

    }

    // Getters
    public List<UnlockData> getWeapons() {
        return weapons;
    }

    public List<UnlockData> getAttachments() {
        return attachments;
    }

    public List<UnlockData> getKitUnlocks() {
        return kitUnlocks;
    }

    public List<UnlockData> getVehicleUpgrades() {
        return vehicleUpgrades;
    }

    public List<UnlockData> getSkills() {
        return skills;
    }

    public List<UnlockData> getUnlocks() {
        return unlocks;
    }

}
