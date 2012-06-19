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

public class PersonaData implements Parcelable {

    // Attributes
    protected long id;
    protected String name, logo;
    protected int platformId;

    // Constructs
    public PersonaData(Parcel in) {

        id = in.readLong();
        name = in.readString();
        platformId = in.readInt();
        logo = in.readString(); // TODO: This needs to be incorporated into SP

    }

    public PersonaData(String n) {

        this(0, n, 0, null);

    }

    public PersonaData(long i, String n, int pId, String l) {

        id = i;
        name = n;
        platformId = pId;
        logo = l;

    }

    // Getters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public int getPlatformId() {
        return platformId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(platformId);
        dest.writeString(logo);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<PersonaData> CREATOR = new Parcelable.Creator<PersonaData>() {

        public PersonaData createFromParcel(Parcel in) {
            return new PersonaData(in);
        }

        public PersonaData[] newArray(int size) {
            return new PersonaData[size];
        }

    };

    public String resolvePlatformId() {

        switch (platformId) {

            case 0:
            case 1:
                return "[PC]";

            case 2:
                return "[360]";

            case 4:
                return "[PS3]";

            default:
                return "[N/A]";

        }

    }

    // toString
    @Override
    public String toString() {

        return (

        id + ":" + name + ":" + logo + ":" + platformId

        );
    }
}
