package com.ninetwozero.battlelog.datatype;

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
    private final long mId;
    private final String mUsername;
    private String mGravatarHash;
    private PersonaData[] mPersona;
    private boolean mPlaying;
    private boolean mOnline;
    private boolean mFriend;
    private int mMembershipLevel;

    // Constructs
    public ProfileData(String u) {
        mId = 0;
        mUsername = u;
    }

    public ProfileData(final Parcel in) {
        mId = in.readLong();
        mUsername = in.readString();
        mGravatarHash = in.readString();
        mOnline = (in.readInt() == 1);
        mPlaying = (in.readInt() == 1);
        mPersona = in.createTypedArray(PersonaData.CREATOR);
        mFriend = (in.readInt() == 1);
        mMembershipLevel = in.readInt();
    }

    protected ProfileData(final Builder builder) {
        mId = builder.mId;
        mUsername = builder.mUsername;
        mGravatarHash = builder.mGravatarHash;
        mOnline = builder.mOnline;
        mPlaying = builder.mPlaying;
        mPersona = builder.mPersona;
        mFriend = builder.mFriend;
        mMembershipLevel = builder.mMembershipLevel;
    }

    // Getters
    public long getId() {
        return mId;
    }

    public String getUsername() {
        return mUsername;
    }

    public PersonaData getPersona(int p) {
        return (mPersona.length == 0) ? null : mPersona[p];
    }

    public PersonaData[] getPersonaArray() {
        return mPersona.clone();
    }

    public int getNumPersonas() {
        return (mPersona == null) ? 0 : mPersona.length;
    }

    public String getGravatarHash() {
        return mGravatarHash;
    }

    // is ... ?
    public boolean isOnline() {
        return mOnline;
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    public boolean isFriend() {
        return mFriend;
    }

    public boolean isAdmin() {
        return (mMembershipLevel == 128);
    }

    public int getMembershipLevel() {
        return mMembershipLevel;
    }

    // Setters
    public void setPersona(PersonaData[] p) {
        mPersona = p.clone();

    }

    public void setFriend(boolean b) {
        mFriend = b;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mUsername);
        dest.writeString(mGravatarHash);
        dest.writeInt(mOnline ? 1 : 0);
        dest.writeInt(mPlaying ? 1 : 0);
        dest.writeTypedArray(mPersona, flags);
        dest.writeInt(mFriend ? 1 : 0);
        dest.writeInt(mMembershipLevel);
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
        private final long mId;
        private final String mUsername;

        // Optional params
        private String mGravatarHash;
        private PersonaData[] mPersona;
        private boolean mPlaying;
        private boolean mOnline;
        private boolean mFriend;
        private int mMembershipLevel;

        public Builder(long i, String u) {
            mId = i;
            mUsername = u;

        }

        public Builder gravatarHash(String s) {
            mGravatarHash = s;
            return this;
        }

        public Builder isOnline(boolean b) {
            mOnline = b;
            return this;
        }

        public Builder isPlaying(boolean b) {
            mPlaying = b;
            return this;
        }

        public Builder isFriend(boolean b) {
            mFriend = b;
            return this;
        }

        public Builder persona(PersonaData... p) {
            mPersona = p;
            return this;
        }

        public Builder membershipLevel(int i) {
            mMembershipLevel = i;
            return this;
        }

        public ProfileData build() {
            return new ProfileData(this);
        }
    }

    // toString
    @Override
    public String toString() {
        return (mId + ":" + mUsername + ":#" + getNumPersonas());
    }
}
