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

public class ParsedFeedItemData implements Parcelable {

    // Attributes
    private String title, content;
    private ProfileData[] profileData;

    // Construct
    public ParsedFeedItemData(

            String t, String c, ProfileData[] pd

    ) {

        title = t;
        content = c;
        profileData = pd.clone();

    }

    public ParsedFeedItemData(Parcel in) {

        title = in.readString();
        content = in.readString();
        profileData = in.createTypedArray(ProfileData.CREATOR);

    }

    // Getters
    public String getTitle() {

        return title;

    }

    public String getContent() {
        return content;
    }

    public ProfileData[] getProfileData() {
        return profileData;
    }

    public ProfileData getProfile(int i) {
        return (i <= profileData.length) ? profileData[i] : null;
    }

    @Override
    public int describeContents() {

        return 0;

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(content);
        dest.writeTypedArray(profileData, flags);

    }

    public static final Parcelable.Creator<FeedItem> CREATOR = new Parcelable.Creator<FeedItem>() {

        public FeedItem createFromParcel(Parcel in) {
            return new FeedItem(in);
        }

        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }

    };
}
