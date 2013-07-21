/*
    This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.datatype;

import android.os.Parcel;
import android.os.Parcelable;

public class WeaponStats implements Parcelable {

    // Base-section
    private String mName;
    private String mGuid;
    private String mSlug;
    private int mKills;
    private int mHeadshots;
    private int mKitId;
    private long mShotsFired;
    private long mShotsHit;
    private long mTimeEquipped;
    private double mAccuracy;
    private double mServiceStars;
    private double mServiceStarProgress;

    // Construct
    public WeaponStats(String n, String g, String s, int k, int h, int kId, long sF, long sH,
                       long tE, double a, double sS, double sSP) {

        mName = n;
        mGuid = g;
        mSlug = s;
        mKills = k;
        mHeadshots = h;
        mKitId = kId;
        mShotsFired = sF;
        mShotsHit = sH;
        mTimeEquipped = tE;
        mAccuracy = a;
        mServiceStars = sS;
        mServiceStarProgress = sSP;

    }

    public WeaponStats(Parcel in) {

        mName = in.readString();
        mGuid = in.readString();
        mSlug = in.readString();
        mKills = in.readInt();
        mHeadshots = in.readInt();
        mKitId = in.readInt();
        mShotsFired = in.readLong();
        mShotsHit = in.readLong();
        mTimeEquipped = in.readLong();
        mAccuracy = in.readDouble();
        mServiceStars = in.readDouble();
        mServiceStarProgress = in.readDouble();

    }

    // Getters
    public String getName() {
        return mName;
    }

    public String getGuid() {
        return mGuid;
    }

    public String getSlug() {
        return mSlug;
    }

    public int getKills() {
        return mKills;
    }

    public int getHeadshots() {
        return mHeadshots;
    }

    public int getKitId() {
        return mKitId;
    }

    public long getShotsFired() {
        return mShotsFired;
    }

    public long getShotsHit() {
        return mShotsHit;
    }

    public long getTimeEquipped() {
        return mTimeEquipped;
    }

    public double getAccuracy() {
        return mAccuracy;
    }

    public double getServiceStars() {
        return mServiceStars;
    }

    public double getServiceStarProgress() {
        return mServiceStarProgress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeString(mName);
        out.writeString(mGuid);
        out.writeString(mSlug);
        out.writeInt(mKills);
        out.writeInt(mHeadshots);
        out.writeInt(mKitId);
        out.writeLong(mShotsFired);
        out.writeLong(mShotsHit);
        out.writeLong(mTimeEquipped);
        out.writeDouble(mAccuracy);
        out.writeDouble(mServiceStars);
        out.writeDouble(mServiceStarProgress);

    }

    public static final Parcelable.Creator<WeaponStats> CREATOR = new Parcelable.Creator<WeaponStats>() {

        public WeaponStats createFromParcel(Parcel in) {
            return new WeaponStats(in);
        }

        public WeaponStats[] newArray(int size) {
            return new WeaponStats[size];
        }

    };

}
