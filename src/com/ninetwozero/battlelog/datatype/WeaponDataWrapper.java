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

import android.os.Parcel;
import android.os.Parcelable;

public class WeaponDataWrapper implements Parcelable {

    // Attributes
    private int mNumUnlocked;
    private WeaponInfo mData;
    private WeaponStats mStats;
    private List<UnlockData> mUnlocks;

    public WeaponDataWrapper(int nu, WeaponInfo d, WeaponStats w,
                             List<UnlockData> u) {

        mNumUnlocked = nu;
        mData = d;
        mStats = w;
        mUnlocks = u;

    }

    public WeaponDataWrapper(Parcel in) {

        mNumUnlocked = in.readInt();
        mData = in.readParcelable(WeaponInfo.class.getClassLoader());
        mStats = in.readParcelable(WeaponStats.class.getClassLoader());
        mUnlocks = in.createTypedArrayList(UnlockData.CREATOR);

    }

    // Getters
    public int getNumUnlocked() {

        return mNumUnlocked;
    }

    public int getNumUnlocks() {

        return mUnlocks.size();
    }

    public WeaponInfo getData() {

        return mData;
    }

    public WeaponStats getStats() {

        return mStats;
    }

    public List<UnlockData> getUnlocks() {

        return mUnlocks;
    }

    // Setters
    public void setData(WeaponInfo d) {

        mData = d;

    }

    public void setStats(WeaponStats w) {

        mStats = w;
    }

    public void setUnlocks(List<UnlockData> u) {

        mUnlocks = u;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeInt(mNumUnlocked);
        out.writeParcelable(mData, arg1);
        out.writeParcelable(mStats, arg1);
        out.writeTypedList(mUnlocks);

    }

    public static final Parcelable.Creator<WeaponDataWrapper> CREATOR = new Parcelable.Creator<WeaponDataWrapper>() {

        public WeaponDataWrapper createFromParcel(Parcel in) {
            return new WeaponDataWrapper(in);
        }

        public WeaponDataWrapper[] newArray(int size) {
            return new WeaponDataWrapper[size];
        }

    };

}
