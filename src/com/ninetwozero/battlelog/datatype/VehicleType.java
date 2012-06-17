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

public class VehicleType implements Parcelable {

    // Attributes
    private String mIdentifier;
    private String mName;
    private String mLabel;

    // Construct
    public VehicleType(String i, String n, String l) {

        this.mIdentifier = i;
        this.mName = n;
        this.mLabel = l;

    }

    public VehicleType(Parcel in) {

        this.mIdentifier = in.readString();
        this.mName = in.readString();
        this.mLabel = in.readString();

    }

    // Getters
    public String getIdentifier() {
        return this.mIdentifier;
    }

    public String getName() {
        return this.mName;
    }

    public String getLabel() {
        return this.mLabel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeString(this.mIdentifier);
        out.writeString(this.mName);
        out.writeString(this.mLabel);

    }

}

