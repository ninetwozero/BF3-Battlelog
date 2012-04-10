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

import java.util.List;

public class WeaponDataWrapper {

    // Attributes
    private int imageId;
    private String name, description, specifications;
    private WeaponStats weaponStats;
    private List<UnlockData> unlocks;

    public WeaponDataWrapper(int i, String n, String d, String s, WeaponStats w, List<UnlockData> u) {

        imageId = i;
        name = n;
        description = d;
        specifications = s;
        weaponStats = w;
        unlocks = u;

    }

    // Getters
    public int getImageId() {

        return imageId;
    }

    public String getName() {

        return name;
    }

    public String getDescription() {

        return description;
    }

    public String getSpecifications() {

        return specifications;
    }

    public WeaponStats getWeaponStats() {

        return weaponStats;
    }

    public List<UnlockData> getUnlocks() {

        return unlocks;
    }

    // Setters
    public void setImage(int i) {

        imageId = i;
    }

    public void setName(String n) {

        name = n;
    }

    public void setWeaponStats(WeaponStats w) {

        weaponStats = w;
    }

    public void setUnlocks(List<UnlockData> u) {

        unlocks = u;
    }

}
