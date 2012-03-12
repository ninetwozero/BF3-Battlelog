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

public class PlatoonTopStatsItem implements Parcelable {

    // Base-section
    private String label;
    private int spm;
    private ProfileData profile;

    // Construct
    public PlatoonTopStatsItem(String l, int s, ProfileData p) {

        this.label = l;
        this.spm = s;
        this.profile = p;

    }

    public PlatoonTopStatsItem(Parcel in) {

        this.label = in.readString();
        this.spm = in.readInt();
        this.profile = in.readParcelable(ProfileData.class.getClassLoader());

    }

    // Getters
    public String getLabel() {
        return this.label;
    }

    public int getSPM() {
        return this.spm;
    }

    public ProfileData getProfile() {
        return this.profile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(label);
        dest.writeInt(spm);
        dest.writeParcelable(profile, flags);

    }

    public static final Parcelable.Creator<PlatoonTopStatsItem> CREATOR = new Parcelable.Creator<PlatoonTopStatsItem>() {

        public PlatoonTopStatsItem createFromParcel(Parcel in) {
            return new PlatoonTopStatsItem(in);
        }

        public PlatoonTopStatsItem[] newArray(int size) {
            return new PlatoonTopStatsItem[size];
        }

    };

}
