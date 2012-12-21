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

package com.ninetwozero.bf3droid.datatype;

import android.os.Parcel;
import android.os.Parcelable;

public class ForumPostData implements Parcelable {

    // Attributes
    private long postId, date, threadId;
    private ProfileData profileData;
    private String content;
    private int numReports;
    private boolean censored, official;

    // Construct
    public ForumPostData(

            long pId, long d, long thId, ProfileData p, String c, int cReports,
            boolean iCensored, boolean iOfficial

    ) {

        postId = pId;
        date = d;
        threadId = thId;

        profileData = p;

        content = c;

        numReports = cReports;

        censored = iCensored;
        official = iOfficial;

    }

    public ForumPostData(Parcel in) {

        postId = in.readLong();
        date = in.readLong();
        threadId = in.readLong();

        profileData = in.readParcelable(ProfileData.class
                .getClassLoader());

        content = in.readString();

        numReports = in.readInt();

        censored = (in.readInt() == 1);
        official = (in.readInt() == 1);

    }

    // Getters
    public long getPostId() {
        return postId;
    }

    public long getDate() {
        return date;
    }

    public long getThreadId() {
        return threadId;
    }

    public ProfileData getProfileData() {
        return profileData;
    }

    public String getContent() {
        return content;
    }

    public int getNumReports() {
        return numReports;
    }

    public boolean isCensored() {
        return censored;
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

        dest.writeLong(postId);
        dest.writeLong(date);
        dest.writeLong(threadId);

        dest.writeParcelable(profileData, flags);

        dest.writeString(content);

        dest.writeInt(numReports);

        dest.writeInt(censored ? 1 : 0);
        dest.writeInt(official ? 1 : 0);

    }

    public static final Parcelable.Creator<ForumPostData> CREATOR = new Parcelable.Creator<ForumPostData>() {

        public ForumPostData createFromParcel(Parcel in) {
            return new ForumPostData(in);
        }

        public ForumPostData[] newArray(int size) {
            return new ForumPostData[size];
        }

    };

    @Override
    public String toString() {
        return profileData.getUsername() + ":" + content;
    }

}
