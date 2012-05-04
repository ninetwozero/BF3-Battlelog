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

import android.os.Parcel;
import android.os.Parcelable;

public class WeaponDataWrapper implements Parcelable {

    // Attributes
    private int numUnlocked;
    private WeaponInfo data;
    private WeaponStats stats;
    private List<UnlockData> unlocks;

    public WeaponDataWrapper(int nu, WeaponInfo d, WeaponStats w, List<UnlockData> u) {

        numUnlocked = nu;
        data = d;
        stats = w;
        unlocks = u;

    }

    public WeaponDataWrapper(Parcel in) {

        numUnlocked = in.readInt();
        data = in.readParcelable(WeaponInfo.class.getClassLoader());
        stats = in.readParcelable(WeaponStats.class.getClassLoader());
        unlocks = in.createTypedArrayList(UnlockData.CREATOR);

    }

    // Getters
    public int getNumUnlocked() {

        return numUnlocked;
    }

    public int getNumUnlocks() {

        return unlocks.size();
    }

    public WeaponInfo getData() {

        return data;
    }

    public WeaponStats getStats() {

        return stats;
    }

    public List<UnlockData> getUnlocks() {

        return unlocks;
    }

    // Setters
    public void setData(WeaponInfo d) {

        data = d;

    }

    public void setStats(WeaponStats w) {

        stats = w;
    }

    public void setUnlocks(List<UnlockData> u) {

        unlocks = u;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeInt(numUnlocked);
        out.writeParcelable(data, arg1);
        out.writeParcelable(stats, arg1);
        out.writeTypedList(unlocks);

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
