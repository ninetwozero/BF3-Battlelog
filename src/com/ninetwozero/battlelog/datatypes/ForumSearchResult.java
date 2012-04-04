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

public class ForumSearchResult implements Parcelable {

    // Attributes
    private long threadId, date;
    private String title;
    private ProfileData owner;
    private boolean sticky, official;

    // Construct
    public ForumSearchResult(String t) {
        title = t;
    }

    public ForumSearchResult(Parcel in) {

        threadId = in.readLong();
        date = in.readLong();

        title = in.readString();

        owner = in.readParcelable(ProfileData.class.getClassLoader());

        sticky = (in.readInt() == 1);
        official = (in.readInt() == 1);

    }

    public ForumSearchResult(

            long tId, long tDate, String t, ProfileData o, boolean st, boolean of

    ) {

        threadId = tId;
        date = tDate;

        title = t;

        owner = o;

        sticky = st;
        official = of;

    }

    // Getters
    public long getThreadId() {
        return threadId;
    }

    public long getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public ProfileData getOwner() {
        return owner;
    }

    public boolean isSticky() {
        return sticky;
    }

    public boolean isOfficial() {
        return official;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(threadId);
        dest.writeLong(date);

        dest.writeString(title);

        dest.writeParcelable(owner, flags);

        dest.writeInt(sticky ? 1 : 0);
        dest.writeInt(official ? 1 : 0);

    }

    public static final Parcelable.Creator<ForumSearchResult> CREATOR = new Parcelable.Creator<ForumSearchResult>() {

        public ForumSearchResult createFromParcel(Parcel in) {
            return new ForumSearchResult(in);
        }

        public ForumSearchResult[] newArray(int size) {
            return new ForumSearchResult[size];
        }

    };

    @Override
    public String toString() {
        return owner + ":" + title;
    }

}
