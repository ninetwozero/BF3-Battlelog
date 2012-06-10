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
    private long id, forumId, dateLastPost, dateLastChecked, dateLastRead, profileId;
    private String title;
    private ProfileData lastPoster;
    private int numPageLastRead, numPosts;
    private boolean unread;

    // Construct
    public SavedForumThreadData(long i, String t, long fId, long dlp, ProfileData p, long dlc,
            long dlr, int nplr, int np, boolean u, long pId) {

        Log.d(Constants.DEBUG_TAG, "n => " + nplr);
        id = i;
        title = t;
        forumId = fId;
        dateLastPost = dlp;
        lastPoster = p;
        dateLastChecked = dlc;
        dateLastRead = dlr;
        numPageLastRead = nplr;
        numPosts = np;
        unread = u;
        profileId = pId;

    }

    public SavedForumThreadData(Parcel in) {

        id = in.readLong();
        title = in.readString();
        forumId = in.readLong();
        dateLastPost = in.readLong();
        lastPoster = in.readParcelable(ProfileData.class.getClassLoader());
        dateLastChecked = in.readLong();
        dateLastRead = in.readLong();
        numPageLastRead = in.readInt();
        numPosts = in.readInt();
        unread = in.readInt() == 1;
        profileId = in.readLong();

    }

    // Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getForumId() {

        return forumId;

    }

    public long getProfileId() {

        return profileId;

    }

    public long getDateLastPost() {

        return dateLastPost;

    }

    public ProfileData getLastPoster() {
        return lastPoster;
    }

    public long getDateLastChecked() {

        return dateLastChecked;

    }

    public long getDateLastRead() {

        return dateLastRead;

    }

    public int getNumPageLastRead() {

        return numPageLastRead;
    }

    public int getNumPosts() {

        return numPosts;
    }

    public boolean hasUnread() {

        return unread;
    }

    // Setters
    public void setDateLastPost(long d) {

        dateLastPost = d;

    }

    public void setLastPoster(ProfileData l) {
        lastPoster = l;
    }

    public void setNumPosts(int n) {

        numPosts = n;
    }

    public void setDateLastRead(long d) {

        dateLastRead = d;
    }

    public void setDateLastChecked(long d) {

        dateLastChecked = d;

    }

    public void setUnread(boolean b) {

        unread = b;

    }

    public String[] toStringArray() {

        return new String[] {

                id + "",
                forumId + "",
                title,
                dateLastPost + "",
                lastPoster.getUsername(),
                lastPoster.getId() + "",
                numPageLastRead + "",
                numPosts + "",
                unread ? "1" : "0",
                dateLastRead + "",
                dateLastChecked + "",
                profileId + "",

        };

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(id);
        dest.writeString(title);
        dest.writeLong(forumId);
        dest.writeLong(dateLastPost);
        dest.writeParcelable(lastPoster, flags);
        dest.writeLong(dateLastChecked);
        dest.writeLong(dateLastRead);
        dest.writeInt(numPageLastRead);
        dest.writeInt(numPosts);
        dest.writeInt(unread ? 1 : 0);
        dest.writeLong(profileId);

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
