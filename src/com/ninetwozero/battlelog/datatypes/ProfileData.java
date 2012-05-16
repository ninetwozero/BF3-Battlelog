
package com.ninetwozero.battlelog.datatypes;

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

import android.os.Parcel;
import android.os.Parcelable;

public class ProfileData implements Parcelable {

    // Attributes
    private long id;
    private String username, gravatarHash;
    private PersonaData[] persona;
    private boolean isPlaying, isOnline;
    private boolean isFriend;
    private int membershipLevel;

    // Constructs
    public ProfileData(String u) {

        username = u;

    }

    public ProfileData(Parcel in) {

        // Init from the parcel
        id = in.readLong();
        username = in.readString();
        gravatarHash = in.readString();
        isOnline = (in.readInt() == 1);
        isPlaying = (in.readInt() == 1);
        persona = in.createTypedArray(PersonaData.CREATOR);
        isFriend = (in.readInt() == 1);
        membershipLevel = in.readInt();

    }

    protected ProfileData(Builder builder) {

        id = builder.id;
        username = builder.username;
        gravatarHash = builder.gravatarHash;
        isOnline = builder.isOnline;
        isPlaying = builder.isPlaying;
        persona = builder.persona;
        isFriend = builder.isFriend;
        membershipLevel = builder.membershipLevel;

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

    public boolean isFriend() {

        return isFriend;
    }

    public boolean isAdmin() {

        return (membershipLevel == 128);
    }

    public int getMembershipLevel() {

        return membershipLevel;
    }

    // Setters
    public void setPersona(PersonaData[] p) {

        persona = p.clone();

    }

    public void setFriend(boolean b) {

        isFriend = b;
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
        dest.writeInt(isFriend ? 1 : 0);
        dest.writeInt(membershipLevel);
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

    public static class Builder {

        // Required params
        private final long id;
        private final String username;

        // Optional parameters
        private String gravatarHash = "";
        private boolean isOnline = false;
        private boolean isPlaying = false;
        private boolean isFriend = true;
        private PersonaData[] persona = null;
        private int membershipLevel = 0;

        // Create the mandatory builder
        public Builder(long i, String u) {

            id = i;
            username = u;

        }

        // Set the data
        public Builder gravatarHash(String s) {

            gravatarHash = s;
            return this;

        }

        public Builder isOnline(boolean b) {

            isOnline = b;
            return this;

        }

        public Builder isPlaying(boolean b) {

            isPlaying = b;
            return this;

        }

        public Builder isFriend(boolean b) {

            isFriend = b;
            return this;

        }

        public Builder persona(PersonaData... p) {

            persona = p;
            return this;

        }

        public Builder membershipLevel(int i) {

            membershipLevel = i;
            return this;

        }

        public ProfileData build() {

            return new ProfileData(this);

        }

    }

    // toString
    @Override
    public String toString() {

        return (

        id + ":" + username + ":pX" + persona.length

        );
    }
}
