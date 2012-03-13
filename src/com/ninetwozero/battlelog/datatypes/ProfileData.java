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
    protected String username, gravatarHash;
    protected String[] personaName;
    protected long profileId;
    protected long[] personaId;
    protected int[] platformId;
    protected boolean isPlaying, isOnline;

    // Constructs
    public ProfileData(Parcel in) {
        readFromParcel(in);
    }

    public ProfileData(String un, long pf) {

        this.username = un;
        this.profileId = pf;

    }

    public ProfileData(String un, String[] pn, long[] p, long pf, int[] n,
            String im) {

        this.username = un;
        this.personaName = pn;
        this.personaId = p;
        this.profileId = pf;
        this.platformId = n;
        this.gravatarHash = im;

        this.isOnline = false;
        this.isPlaying = false;

    }

    public ProfileData(String an, String pn, long p, long pf, int n, String im) {

        this(an, new String[] {
                pn
        }, new long[] {
                p
        }, pf, new int[] {
                n
        },
                im);

    }

    public ProfileData(String an, String pn, long p, long pf, int n,
            String im, boolean b, boolean c) {

        this(an, new String[] {
                pn
        }, new long[] {
                p
        }, pf, new int[] {
                n
        },
                im, b, c);

    }

    public ProfileData(String an, String[] pn, long[] p, long pf, int[] n,
            String im, boolean io, boolean ip) {

        this(an, pn, p, pf, n, im);
        this.isOnline = io;
        this.isPlaying = ip;

    }

    // Getters
    public String getAccountName() {
        return this.username;
    }

    public String getPersonaName() {
        return (personaName.length > 0) ? this.personaName[0] : null;
    }

    public String getPersonaName(int pos) {
        return ((personaName.length - 1) >= pos) ? this.personaName[pos] : null;
    }

    public String[] getPersonaNameArray() {
        return this.personaName;
    }

    public int getNumPersonas() {
        return this.personaId.length;
    }

    public long[] getPersonaIdArray() {
        return this.personaId;
    }

    public int[] getPlatformIdArray() {
        return this.platformId;
    }

    public long getPersonaId() {
        return (personaId.length > 0) ? this.personaId[0] : 0;
    }

    public long getPersonaId(int pos) {
        return ((personaId.length - 1) >= pos) ? personaId[pos] : 0;
    }

    public long getProfileId() {
        return this.profileId;
    }

    public int getPlatformId() {
        return (platformId.length > 0) ? platformId[0] : 0;
    }

    public int getPlatformId(int pos) {
        return ((platformId.length - 1) >= pos) ? platformId[pos] : 0;
    }

    public String getGravatarHash() {
        return this.gravatarHash;
    }

    // is ... ?
    public boolean isOnline() {
        return this.isOnline;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // Special cases
        dest.writeStringArray(this.personaName);
        dest.writeLongArray(this.personaId);
        dest.writeIntArray(this.platformId);

        // Everything else
        dest.writeString(this.username);
        dest.writeLong(this.profileId);
        dest.writeString(this.gravatarHash);
        dest.writeInt(this.isOnline ? 1 : 0);
        dest.writeInt(this.isPlaying ? 1 : 0);
    }

    private void readFromParcel(Parcel in) {

        // Let's retrieve them, same order as above
        this.personaName = in.createStringArray();
        this.personaId = in.createLongArray();
        this.platformId = in.createIntArray();

        this.username = in.readString();
        this.profileId = in.readInt();
        this.gravatarHash = in.readString();
        this.isOnline = (in.readInt() == 1);
        this.isPlaying = (in.readInt() == 1);

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

    // Setters
    public void setPersonaId(long[] array) {
        this.personaId = array;
    }

    public void setPlatformId(int[] array) {
        this.platformId = array;
    }

    public void setPersonaName(String[] array) {
        this.personaName = array;
    }

    // toString
    @Override
    public String toString() {

        return (

        getAccountName() + ":" + getPersonaName() + ":" + getPersonaId() + ":"
                + getProfileId() + ":" + getPlatformId() + ":" + isOnline()
                + ":" + isPlaying()

        );
    }
}
