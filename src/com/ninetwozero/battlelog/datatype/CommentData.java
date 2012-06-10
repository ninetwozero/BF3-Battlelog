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
    private long id, itemId, timestamp;
    private String content;
    private ProfileData author;
    
    // Constants
    public final static int TYPE_NONE = 0;
    public final static int TYPE_FEED = 1;
    public final static int TYPE_NEWS = 2;

    // Constructs
    public CommentData(long i, long iId, long cDate, String c, ProfileData a) {

        id = i;
        itemId = iId;
        timestamp = cDate;
        content = c;
        author = a;

    }

    public CommentData(Parcel in) {

        id = in.readLong();
        itemId = in.readLong();
        timestamp = in.readLong();
        content = in.readString();
        author = in.readParcelable(ProfileData.class.getClassLoader());

    }

    // Getters
    public long getId() {
        return id;
    }

    public long getItemId() {
        return itemId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public ProfileData getAuthor() {
        return author;
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

        out.writeLong(id);
        out.writeLong(itemId);
        out.writeLong(timestamp);
        out.writeString(content);
        out.writeParcelable(author, arg1);

    }

    public static final Parcelable.Creator<CommentData> CREATOR = new Parcelable.Creator<CommentData>() {

        public CommentData createFromParcel(Parcel in) {
            return new CommentData(in);
        }

        public CommentData[] newArray(int size) {
            return new CommentData[size];
        }

    };
}
