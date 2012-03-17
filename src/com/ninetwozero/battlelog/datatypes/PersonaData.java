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

public class PersonaData implements Parcelable {

    // Attributes
    protected long id;
    protected String name, logo;
    protected int platformId;

    // Constructs
    public PersonaData(Parcel in) {
        readFromParcel(in);
    }

    public PersonaData(long i, String n, String l, int pId) {

        id = i;
        name = n;
        logo = l;
        platformId = pId;
        

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
        dest.writeString(logo);
        dest.writeInt(platformId);

    }

    private void readFromParcel(Parcel in) {

        // Let's retrieve them, same order as above
        id = in.readLong();
        name = in.readString();
        logo = in.readString();
        platformId = in.readInt();

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

    // toString
    @Override
    public String toString() {

        return (

                id + ":" + name + ":" + logo + ":" + platformId

        );
    }
}
