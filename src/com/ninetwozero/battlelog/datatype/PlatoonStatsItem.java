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

public class PlatoonStatsItem implements Parcelable {

    // Base-section
    private String label;
    private int min, mid, max, avg;
    private double dMin, dMid, dMax, dAvg;
    private ProfileData profile;

    // Construct
    public PlatoonStatsItem(Parcel in) {

        this.label = in.readString();
        this.min = in.readInt();
        this.mid = in.readInt();
        this.max = in.readInt();
        this.avg = in.readInt();
        this.dMin = in.readDouble();
        this.dMid = in.readDouble();
        this.dMax = in.readDouble();
        this.dAvg = in.readDouble();
        this.profile = in.readParcelable(ProfileData.class.getClassLoader());

    }

    public PlatoonStatsItem(String l, int a, int b, int c, int d, ProfileData p) {

        this.label = l;
        this.min = a;
        this.mid = b;
        this.max = c;
        this.avg = d;
        this.dMin = 0;
        this.dMid = 0;
        this.dMax = 0;
        this.dAvg = 0;
        this.profile = p;

    }

    public PlatoonStatsItem(String l, double a, double b, double c, double d,
            ProfileData p) {

        this.label = l;
        this.min = 0;
        this.mid = 0;
        this.max = 0;
        this.avg = 0;
        this.dMin = a;
        this.dMid = b;
        this.dMax = c;
        this.dAvg = d;
        this.profile = p;

    }

    // Getters
    public String getLabel() {
        return this.label;
    }

    public int getMin() {
        return this.min;
    }

    public int getMid() {
        return this.mid;
    }

    public int getMax() {
        return this.max;
    }

    public int getAvg() {
        return this.avg;
    }

    public double getDMin() {
        return this.dMin;
    }

    public double getDMid() {
        return this.dMid;
    }

    public double getDMax() {
        return this.dMax;
    }

    public double getDAvg() {
        return this.dAvg;
    }

    public ProfileData getProfile() {
        return this.profile;
    }

    // Setters
    public void setMid(int m) {
        this.mid = m;
    }

    public void add(PlatoonStatsItem p) {

        this.min = (p.getMin() < this.min) ? p.getMin() : this.min;
        this.mid = p.getMid();
        this.max = (p.getMax() > this.max) ? p.getMax() : this.max;
        this.avg += p.getAvg();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static final Parcelable.Creator<PlatoonStatsItem> CREATOR = new Parcelable.Creator<PlatoonStatsItem>() {

        public PlatoonStatsItem createFromParcel(Parcel in) {
            return new PlatoonStatsItem(in);
        }

        public PlatoonStatsItem[] newArray(int size) {
            return new PlatoonStatsItem[size];
        }

    };
}
