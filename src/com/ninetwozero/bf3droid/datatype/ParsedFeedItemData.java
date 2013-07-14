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

package com.ninetwozero.bf3droid.datatype;

import android.os.Parcel;
import android.os.Parcelable;

public class ParsedFeedItemData implements Parcelable {

    // Attributes
    private String mTitle, mContent, mGravatarHash;
    private ProfileData[] profileData;

    // Construct
    public ParsedFeedItemData(String t, String c, ProfileData[] pd) {
        mTitle = t;
        mContent = c;
        profileData = pd.clone();
        mGravatarHash = pd[0].getGravatarHash();
    }
    
    public ParsedFeedItemData(String t, String c, String g, ProfileData[] pd) {
        mTitle = t;
        mContent = c;
        profileData = pd.clone();
        mGravatarHash = g;
    }

    public ParsedFeedItemData(Parcel in) {
        mTitle = in.readString();
        mContent = in.readString();
        mGravatarHash = in.readString();
        profileData = in.createTypedArray(ProfileData.CREATOR);
    }

    // Getters
    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public ProfileData[] getProfileData() {
        return profileData;
    }

    public ProfileData getProfile(int i) {
        return (i <= profileData.length) ? profileData[i] : null;
    }
    
    public String getGravatarHash() {
    	return mGravatarHash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mContent);
        dest.writeString(mGravatarHash);
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
