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

public class CommentData implements Parcelable {

    // Attributes
    private long mId;
    private long mItemId;
    private long mTimestamp;
    private String mContent;
    private ProfileData mAuthor;

    // Constants
    public static final int TYPE_NONE = 0;
    public static final int TYPE_FEED = 1;
    public static final int TYPE_NEWS = 2;

    // Constructs
    public CommentData(long i, long iId, long cDate, String c, ProfileData a) {

        mId = i;
        mItemId = iId;
        mTimestamp = cDate;
        mContent = c;
        mAuthor = a;

    }

    public CommentData(Parcel in) {

        mId = in.readLong();
        mItemId = in.readLong();
        mTimestamp = in.readLong();
        mContent = in.readString();
        mAuthor = in.readParcelable(ProfileData.class.getClassLoader());

    }

    // Getters
    public long getId() {
        return mId;
    }

    public long getItemId() {
        return mItemId;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public String getContent() {
        return mContent;
    }

    public ProfileData getAuthor() {
        return mAuthor;
    }

    @Override
    public String toString() {
        return getId() + ":" + getAuthor() + " |: " + getContent();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeLong(mId);
        out.writeLong(mItemId);
        out.writeLong(mTimestamp);
        out.writeString(mContent);
        out.writeParcelable(mAuthor, arg1);

    }

    public static final Parcelable.Creator<CommentData> CREATOR = new Parcelable.Creator<CommentData>() {

        public CommentData createFromParcel(Parcel in) {
            return new CommentData(in);
        }

        public CommentData[] newArray(int size) {
            return new CommentData[size];
        }

    };

	public String getGravatar() {
		return mAuthor.getGravatarHash();
	}
}
