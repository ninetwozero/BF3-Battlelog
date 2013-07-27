package com.ninetwozero.bf3droid.datatype;

/*
    This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class ProfileData implements Parcelable {

    private final long id;
    private final String username;
    private String gravatarHash;
    private PersonaData[] personas;
    private boolean isPlaying;
    private boolean isOnline;
    private boolean isFriend;
    private boolean isAway;
    private int membershipLevel;

    public ProfileData(String username) {
        id = 0;
        this.username = username;
    }

    public ProfileData(final Parcel in) {
        id = in.readLong();
        username = in.readString();
        gravatarHash = in.readString();
        isOnline = (in.readInt() == 1);
        isPlaying = (in.readInt() == 1);
        personas = in.createTypedArray(PersonaData.CREATOR);
        isFriend = (in.readInt() == 1);
        membershipLevel = in.readInt();
    }

    protected ProfileData(final Builder builder) {
        id = builder.id;
        username = builder.username;
        gravatarHash = builder.gravatarHash;
        isOnline = builder.isOnline;
        isPlaying = builder.isPlaying;
        personas = builder.personas;
        isFriend = builder.isFriend;
        membershipLevel = builder.membershipLevel;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public PersonaData getPersona(int p) {
        return (personas.length == 0) ? null : personas[p];
    }

    public PersonaData[] getPersonaArray() {
        return personas.clone();
    }

    public int getNumPersonas() {
        return (personas == null) ? 0 : personas.length;
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
        personas = p.clone();

    }

    public void setFriend(boolean b) {
        isFriend = b;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeString(gravatarHash);
        dest.writeInt(isOnline ? 1 : 0);
        dest.writeInt(isPlaying ? 1 : 0);
        dest.writeTypedArray(personas, flags);
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

        // Optional params
        private String gravatarHash = "";
        private PersonaData[] personas = new PersonaData[0];
        private boolean isPlaying = false;
        private boolean isOnline = false;
        private boolean isFriend = false;
        private int membershipLevel = 0;
        private boolean isAway = false;

        public Builder(long i, String u) {
            id = i;
            username = u;
        }

        public Builder persona(PersonaData... p) {
            personas = p;
            return this;
        }

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

        public Builder isAway(boolean isAway){
            this.isAway = isAway;
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
        return (id + ":" + username + ":#" + getNumPersonas());
    }
}
