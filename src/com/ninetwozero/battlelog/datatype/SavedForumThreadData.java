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
import android.util.Log;

import com.ninetwozero.battlelog.misc.Constants;

public class SavedForumThreadData implements Parcelable {

    // Attributes
    private long mId;
    private long mForumId;
    private long mDateLastPost;
    private long mDateLastChecked;
    private long mDateLastRead;
    private long mProfileId;
    private String mTitle;
    private ProfileData mLastPoster;
    private int mNumPageLastRead;
    private int mNumPosts;
    private boolean mUnread;

    // Construct
    public SavedForumThreadData(long i, String t, long fId, long dlp, ProfileData p, long dlc,
            long dlr, int nplr, int np, boolean u, long pId) {

        mId = i;
        mTitle = t;
        mForumId = fId;
        mDateLastPost = dlp;
        mLastPoster = p;
        mDateLastChecked = dlc;
        mDateLastRead = dlr;
        mNumPageLastRead = nplr;
        mNumPosts = np;
        mUnread = u;
        mProfileId = pId;

    }

    public SavedForumThreadData(Parcel in) {

        mId = in.readLong();
        mTitle = in.readString();
        mForumId = in.readLong();
        mDateLastPost = in.readLong();
        mLastPoster = in.readParcelable(ProfileData.class.getClassLoader());
        mDateLastChecked = in.readLong();
        mDateLastRead = in.readLong();
        mNumPageLastRead = in.readInt();
        mNumPosts = in.readInt();
        mUnread = in.readInt() == 1;
        mProfileId = in.readLong();

    }

    // Getters
    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public long getForumId() {

        return mForumId;

    }

    public long getProfileId() {

        return mProfileId;

    }

    public long getDateLastPost() {

        return mDateLastPost;

    }

    public ProfileData getLastPoster() {
        return mLastPoster;
    }

    public long getDateLastChecked() {

        return mDateLastChecked;

    }

    public long getDateLastRead() {

        return mDateLastRead;

    }

    public int getNumPageLastRead() {

        return mNumPageLastRead;
    }

    public int getNumPosts() {

        return mNumPosts;
    }

    public boolean hasUnread() {

        return mUnread;
    }

    // Setters
    public void setDateLastPost(long d) {

        mDateLastPost = d;

    }

    public void setLastPoster(ProfileData l) {
        mLastPoster = l;
    }

    public void setNumPosts(int n) {

        mNumPosts = n;
    }

    public void setDateLastRead(long d) {

        mDateLastRead = d;
    }

    public void setDateLastChecked(long d) {

        mDateLastChecked = d;

    }

    public void setUnread(boolean b) {

        mUnread = b;

    }

    public Object[] toArray() {

        return new Object[] {

                mId ,
                mForumId ,
                mTitle,
                mDateLastPost ,
                mLastPoster.getUsername(),
                mLastPoster.getId() ,
                mNumPageLastRead ,
                mNumPosts ,
                mUnread ? "1" : "0",
                mDateLastRead ,
                mDateLastChecked ,
                mProfileId ,

        };

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeLong(mForumId);
        dest.writeLong(mDateLastPost);
        dest.writeParcelable(mLastPoster, flags);
        dest.writeLong(mDateLastChecked);
        dest.writeLong(mDateLastRead);
        dest.writeInt(mNumPageLastRead);
        dest.writeInt(mNumPosts);
        dest.writeInt(mUnread ? 1 : 0);
        dest.writeLong(mProfileId);

    }

    public static final Parcelable.Creator<SavedForumThreadData> CREATOR = new Parcelable.Creator<SavedForumThreadData>() {

        public SavedForumThreadData createFromParcel(Parcel in) {
            return new SavedForumThreadData(in);
        }

        public SavedForumThreadData[] newArray(int size) {
            return new SavedForumThreadData[size];
        }

    };

}
