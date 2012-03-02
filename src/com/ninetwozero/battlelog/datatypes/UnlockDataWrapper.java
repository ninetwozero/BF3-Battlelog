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

package com.ninetwozero.battlelog.datatypes;

import java.util.ArrayList;

public class UnlockDataWrapper {

    // Attributes
    private ArrayList<UnlockData> weapons, attachments, kitUnlocks,
            vehicleUpgrades, skills, unlocks;

    // Construct
    public UnlockDataWrapper(

            ArrayList<UnlockData> w, ArrayList<UnlockData> a, ArrayList<UnlockData> k,
            ArrayList<UnlockData> v, ArrayList<UnlockData> s,
            ArrayList<UnlockData> u

    ) {

        this.weapons = w;
        this.attachments = a;
        this.kitUnlocks = k;
        this.vehicleUpgrades = v;
        this.skills = s;
        this.unlocks = u;

    }

    // Getters
    public ArrayList<UnlockData> getWeapons() {
        return this.weapons;
    }

    public ArrayList<UnlockData> getAttachments() {
        return this.attachments;
    }

    public ArrayList<UnlockData> getKitUnlocks() {
        return this.kitUnlocks;
    }

    public ArrayList<UnlockData> getVehicleUpgrades() {
        return this.vehicleUpgrades;
    }

    public ArrayList<UnlockData> getSkills() {
        return this.skills;
    }

    public ArrayList<UnlockData> getUnlocks() {
        return this.unlocks;
    }

}
