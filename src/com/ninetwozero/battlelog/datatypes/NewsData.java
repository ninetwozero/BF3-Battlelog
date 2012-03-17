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
import com.ninetwozero.battlelog.R;

public class NewsData implements Parcelable {

    // Attributes
    private long id, date;
    private int numComments;
    private String title, content;
    private ProfileData author;

    // Constructs
    public NewsData(long id, long cDate, int n, String t, String c, ProfileData a) {

        this.id = id;
        this.date = cDate;
        this.numComments = n;
        this.title = t;
        this.content = c;
        this.author = a;

    }

    public NewsData(Parcel in) {

        this.id = in.readLong();
        this.date = in.readLong();
        this.numComments = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.author = in.readParcelable(ProfileData.class.getClassLoader());

    }

    // Getters
    public long getId() {
        return this.id;
    }

    public long getDate() {
        return this.date;
    }

    public int getNumComments() {
        
        return numComments;
        
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getContent() {
        return this.content;
    }

    public ProfileData getAuthor() {
        return this.author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeLong(this.id);
        out.writeLong(this.date);
        out.writeInt(this.numComments);
        out.writeString(this.title);
        out.writeString(this.content);
        out.writeParcelable(this.author, arg1);

    }

    public static final Parcelable.Creator<NewsData> CREATOR = new Parcelable.Creator<NewsData>() {

        public NewsData createFromParcel(Parcel in) {
            return new NewsData(in);
        }

        public NewsData[] newArray(int size) {
            return new NewsData[size];
        }

    };
}
