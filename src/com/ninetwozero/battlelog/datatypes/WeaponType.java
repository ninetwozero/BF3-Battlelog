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

import com.ninetwozero.battlelog.R;


public class WeaponType {

    // Attributes
    private String identifier, name, slug, rateOfFireText, range, ammo;
    private boolean automatic, burst, singleshot;
    private int rateOfFireNum;

    public WeaponType(String i, String n, String l, String rof, String r,
            String a, boolean au, boolean b, boolean s) {

        this.identifier = i;
        this.name = n;
        this.slug = l;
        this.rateOfFireText = rof;
        this.rateOfFireNum = 0;
        this.range = r;
        this.ammo = a;
        this.automatic = au;
        this.burst = b;
        this.singleshot = s;

    }

    public WeaponType(String i, String n, String l, int rof, String r,
            String a, boolean au, boolean b, boolean s) {

        this(i, n, l, "", r, a, au, b, s);
        this.rateOfFireNum = rof;

    }

    // GETTERS
    public final String getIdentifier() {
        return identifier;
    }

    public final String getName() {
        return name;
    }

    public final String getSlug() {
        return slug;
    }

    public final String getRateOfFireText() {
        return rateOfFireText;
    }

    public final String getRange() {
        return range;
    }

    public final String getAmmo() {
        return ammo;
    }

    public final boolean isAutomatic() {
        return automatic;
    }

    public final boolean isBurst() {
        return burst;
    }

    public final boolean isSingleshot() {
        return singleshot;
    }

    public final int getRateOfFireNum() {
        return rateOfFireNum;
    }

}
