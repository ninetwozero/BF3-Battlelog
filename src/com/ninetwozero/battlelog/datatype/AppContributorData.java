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

public class AppContributorData implements Parcelable {

    // Attributes
    private int stringId;
    private String name, url;

    // Constructs
    public AppContributorData(Parcel in) {
        readFromParcel(in);
    }

    public AppContributorData(String n, String u) {

        stringId = 0;
        name = n;
        url = u;
    }

    public AppContributorData(int s) {

        stringId = s;
        name = null;
        url = null;

    }

    public int getStringId() {

        return stringId;

    }

    public String getName() {

        return name;

    }

    public String getUrl() {

        return url;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // Special cases
        dest.writeInt(stringId);
        dest.writeString(name);
        dest.writeString(url);

    }

    private void readFromParcel(Parcel in) {

        // Let's retrieve them, same order as above
        stringId = in.readInt();
        name = in.readString();
        url = in.readString();

    }

    public static final Parcelable.Creator<AppContributorData> CREATOR = new Parcelable.Creator<AppContributorData>() {

        public AppContributorData createFromParcel(Parcel in) {
            return new AppContributorData(in);
        }

        public AppContributorData[] newArray(int size) {
            return new AppContributorData[size];
        }

    };
}
