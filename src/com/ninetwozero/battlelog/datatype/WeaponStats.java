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

import android.os.Parcel;
import android.os.Parcelable;

public class WeaponStats implements Parcelable {

    // Base-section
    private String name, guid, slug;
    private int kills, headshots, kitId;
    private long shotsFired, shotsHit, timeEquipped;
    private double accuracy, serviceStars, serviceStarProgress;

    // Construct
    public WeaponStats(String n, String g, String s, int k, int h, int kId, long sF, long sH,
            long tE, double a, double sS, double sSP) {

        name = n;
        guid = g;
        slug = s;
        kills = k;
        headshots = h;
        kitId = kId;
        shotsFired = sF;
        shotsHit = sH;
        timeEquipped = tE;
        accuracy = a;
        serviceStars = sS;
        serviceStarProgress = sSP;

    }

    public WeaponStats(Parcel in) {

        name = in.readString();
        guid = in.readString();
        slug = in.readString();
        kills = in.readInt();
        headshots = in.readInt();
        kitId = in.readInt();
        shotsFired = in.readLong();
        shotsHit = in.readLong();
        timeEquipped = in.readLong();
        accuracy = in.readDouble();
        serviceStars = in.readDouble();
        serviceStarProgress = in.readDouble();

    }

    // Getters
    public String getName() {
        return name;
    }

    public String getGuid() {
        return guid;
    }

    public String getSlug() {
        return slug;
    }

    public int getKills() {
        return kills;
    }

    public int getHeadshots() {
        return headshots;
    }

    public int getKitId() {
        return kitId;
    }

    public long getShotsFired() {
        return shotsFired;
    }

    public long getShotsHit() {
        return shotsHit;
    }

    public long getTimeEquipped() {
        return timeEquipped;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getServiceStars() {
        return serviceStars;
    }

    public double getServiceStarProgress() {
        return serviceStarProgress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeString(name);
        out.writeString(guid);
        out.writeString(slug);
        out.writeInt(kills);
        out.writeInt(headshots);
        out.writeInt(kitId);
        out.writeLong(shotsFired);
        out.writeLong(shotsHit);
        out.writeLong(timeEquipped);
        out.writeDouble(accuracy);
        out.writeDouble(serviceStars);
        out.writeDouble(serviceStarProgress);

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
