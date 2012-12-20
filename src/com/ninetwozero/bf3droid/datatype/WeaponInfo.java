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

import android.os.Parcel;
import android.os.Parcelable;

import com.ninetwozero.bf3droid.R;

public class WeaponInfo implements Parcelable {

    // Attributes
    private String mIdentifier;
    private String mName;
    private String mSlug;
    private String mAmmo;
    private int mRange;
    private int mRateOfFire;
    private boolean mRateAuto;
    private boolean mRateBurst;
    private boolean mRateSingle;

    // Constants
    final public static int RANGE_SHORT = 0;
    final public static int RANGE_MEDIUM = 1;
    final public static int RANGE_LONG = 2;
    final public static int RANGE_VLONG = 3;

    public WeaponInfo(Parcel in) {

        mIdentifier = in.readString();
        mName = in.readString();
        mSlug = in.readString();
        mRateOfFire = in.readInt();
        mRange = in.readInt();
        mAmmo = in.readString();

        mRateAuto = in.readInt() == 1;
        mRateBurst = in.readInt() == 1;
        mRateSingle = in.readInt() == 1;

    }

    public WeaponInfo(String i, String n, String l, int rof, int r, String a,
                      boolean ra, boolean rb, boolean rs) {

        mIdentifier = i;
        mName = n;
        mSlug = l;
        mRateOfFire = rof;
        mRange = r;
        mAmmo = a;

        mRateAuto = ra;
        mRateBurst = rb;
        mRateSingle = rs;

    }

    // GETTERS
    public final String getIdentifier() {
        return mIdentifier;
    }

    public final String getName() {
        return mName;
    }

    public final String getSlug() {
        return mSlug;
    }

    public final int getRateOfFire() {
        return mRateOfFire;
    }

    public final int getRange() {
        return mRange;
    }

    public final boolean isAuto() {

        return mRateAuto;
    }

    public final boolean isBurst() {

        return mRateBurst;
    }

    public final boolean isSingle() {

        return mRateSingle;
    }

    public final int getRangeTitle() {

        switch (mRange) {

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
        return mAmmo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeString(mIdentifier);
        out.writeString(mName);
        out.writeString(mSlug);
        out.writeInt(mRateOfFire);
        out.writeInt(mRange);
        out.writeString(mAmmo);

        out.writeInt(mRateAuto ? 1 : 0);
        out.writeInt(mRateBurst ? 1 : 0);
        out.writeInt(mRateSingle ? 1 : 0);

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
