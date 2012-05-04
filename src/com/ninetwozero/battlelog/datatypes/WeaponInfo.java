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

import android.os.Parcel;
import android.os.Parcelable;

import com.ninetwozero.battlelog.R;

public class WeaponInfo implements Parcelable {

    // Attributes
    private String identifier, name, slug, ammo;
    private int range, rateOfFire;
    private boolean rateAuto, rateBurst, rateSingle;

    // Constants
    final public static int RANGE_SHORT = 0;
    final public static int RANGE_MEDIUM = 1;
    final public static int RANGE_LONG = 2;
    final public static int RANGE_VLONG = 3;

    public WeaponInfo(Parcel in) {

        identifier = in.readString();
        name = in.readString();
        slug = in.readString();
        rateOfFire = in.readInt();
        range = in.readInt();
        ammo = in.readString();

        rateAuto = in.readInt() == 1;
        rateBurst = in.readInt() == 1;
        rateSingle = in.readInt() == 1;

    }

    public WeaponInfo(String i, String n, String l, int rof, int r,
            String a, boolean ra, boolean rb, boolean rs) {

        identifier = i;
        name = n;
        slug = l;
        rateOfFire = rof;
        range = r;
        ammo = a;

        rateAuto = ra;
        rateBurst = rb;
        rateSingle = rs;

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

    public final int getRateOfFire() {
        return rateOfFire;
    }

    public final int getRange() {
        return range;
    }

    public final boolean isAuto() {

        return rateAuto;
    }

    public final boolean isBurst() {

        return rateBurst;
    }

    public final boolean isSingle() {

        return rateSingle;
    }

    public final int getRangeTitle() {

        switch (range) {

            case WeaponInfo.RANGE_SHORT:
                return R.string.info_weapon_range_short;

            case WeaponInfo.RANGE_MEDIUM:
                return R.string.info_weapon_range_medium;
            case WeaponInfo.RANGE_LONG:
                return R.string.info_weapon_range_long;
            case WeaponInfo.RANGE_VLONG:
                return R.string.info_weapon_range_vlong;

            default:
                return R.string.general_not_available;
        }
    }

    public final String getAmmo() {
        return ammo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeString(identifier);
        out.writeString(name);
        out.writeString(slug);
        out.writeInt(rateOfFire);
        out.writeInt(range);
        out.writeString(ammo);

        out.writeInt(rateAuto ? 1 : 0);
        out.writeInt(rateBurst ? 1 : 0);
        out.writeInt(rateSingle ? 1 : 0);

    }

    public static final Parcelable.Creator<WeaponInfo> CREATOR = new Parcelable.Creator<WeaponInfo>() {

        public WeaponInfo createFromParcel(Parcel in) {
            return new WeaponInfo(in);
        }

        public WeaponInfo[] newArray(int size) {
            return new WeaponInfo[size];
        }

    };

}
