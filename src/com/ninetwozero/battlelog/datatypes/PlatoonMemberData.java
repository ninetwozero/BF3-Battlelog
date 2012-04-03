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

public class PlatoonMemberData extends ProfileData implements Parcelable {

    // Attributes
    private int membershipLevel;

    // Constructs
    public PlatoonMemberData(long pf, String un, PersonaData p, String im, int m) {

        super(pf, un, p, im);
        membershipLevel = m;

    }

    public PlatoonMemberData(long pf, String un, PersonaData[] p, String im, int m) {

        super(pf, un, p.clone(), im);
        membershipLevel = m;

    }

    public PlatoonMemberData(long pf, String un, PersonaData p, String im, boolean on, boolean pl,
            int m) {

        super(pf, un, p, im, on, pl);
        membershipLevel = m;

    }

    public PlatoonMemberData(long pf, String un, PersonaData[] p, String im, boolean on,
            boolean pl, int m) {

        super(pf, un, p.clone(), im, on, pl);
        membershipLevel = m;

    }

    public PlatoonMemberData(Parcel in) {

        super(in);
        membershipLevel = in.readInt();

    }

    // Getters
    public int getMembershipLevel() {
        return membershipLevel;
    }

    public boolean isAdmin() {
        return (membershipLevel == 128);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // Everything else
        super.writeToParcel(dest, flags);
        dest.writeInt(membershipLevel);
    }

    public static final Parcelable.Creator<PlatoonMemberData> CREATOR = new Parcelable.Creator<PlatoonMemberData>() {

        public PlatoonMemberData createFromParcel(Parcel in) {
            return new PlatoonMemberData(in);
        }

        public PlatoonMemberData[] newArray(int size) {
            return new PlatoonMemberData[size];
        }

    };

}
