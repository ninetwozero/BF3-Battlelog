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

public class ProfileData implements Parcelable {

    // Attributes
    protected long id;
    protected String username, gravatarHash;
    protected PersonaData[] persona;
    protected boolean isPlaying, isOnline;

    // Constructs
    public ProfileData(Parcel in) {

        id = in.readLong();
        username = in.readString();
        gravatarHash = in.readString();
        isOnline = (in.readInt() == 1);
        isPlaying = (in.readInt() == 1);
        persona = in.createTypedArray(PersonaData.CREATOR);

    }

    public ProfileData(long pf, String un) {

        id = pf;
        username = un;

    }

    public ProfileData(long pf, String un, PersonaData p, String im) {

        id = pf;
        username = un;
        persona = new PersonaData[] {
                p
        };
        gravatarHash = im;

        isOnline = false;
        isPlaying = false;

    }

    public ProfileData(long pf, String un, PersonaData[] p, String im) {

        id = pf;
        username = un;
        persona = p.clone();
        gravatarHash = im;

        isOnline = false;
        isPlaying = false;

    }

    public ProfileData(long pf, String un, PersonaData p, String im, boolean on, boolean pl) {

        this(pf, un, new PersonaData[] {
                p
        }, im);
        isOnline = on;
        isPlaying = pl;

    }

    public ProfileData(long pf, String un, PersonaData[] p, String im, boolean on, boolean pl) {

        this(pf, un, p.clone(), im);
        isOnline = on;
        isPlaying = pl;

    }

    // Getters
    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public PersonaData getPersona(int p) {

        return (persona.length != 0) ? persona[p] : null;

    }

    public PersonaData[] getPersonaArray() {

        return persona;

    }

    public int getNumPersonas() {

        return persona.length;

    }

    public String getGravatarHash() {
        return gravatarHash;
    }

    // is ... ?
    public boolean isOnline() {
        return isOnline;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    // Setters
    public void setPersona(PersonaData[] p) {

        persona = p;

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // Everything else
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeString(gravatarHash);
        dest.writeInt(isOnline ? 1 : 0);
        dest.writeInt(isPlaying ? 1 : 0);
        dest.writeTypedArray(persona, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ProfileData> CREATOR = new Parcelable.Creator<ProfileData>() {

        public ProfileData createFromParcel(Parcel in) {
            return new ProfileData(in);
        }

        public ProfileData[] newArray(int size) {
            return new ProfileData[size];
        }

    };

    // toString
    @Override
    public String toString() {

        return (

        id + ":" + username + ":pX" + persona.length

        );
    }
}
