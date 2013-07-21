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

import java.util.Calendar;
import java.util.List;

public class ForumThreadData implements Parcelable {

    // Attributes
    private long id, forumId, date, lastPostDate;
    private int numOfficialPosts, numPosts, numCurrentPage, numTotalPages;
    private String title;
    private ProfileData owner, lastPoster;
    private boolean sticky, locked;
    private boolean censorPosts, deletePosts, editPosts, admin,
            postOfficial, viewLatestPosts, viewPostHistory;
    private List<ForumPostData> posts;

    // Construct
    public ForumThreadData(String t) {
        title = t;
    }

    public ForumThreadData(Parcel in) {

        id = in.readLong();
        forumId = in.readLong();
        date = in.readLong();
        lastPostDate = in.readLong();

        numOfficialPosts = in.readInt();
        numPosts = in.readInt();
        numCurrentPage = in.readInt();
        numTotalPages = in.readInt();

        title = in.readString();

        owner = in.readParcelable(ProfileData.class.getClassLoader());
        lastPoster = in.readParcelable(ProfileData.class
                .getClassLoader());

        sticky = (in.readInt() == 1);
        locked = (in.readInt() == 1);

        censorPosts = (in.readInt() == 1);
        deletePosts = (in.readInt() == 1);
        editPosts = (in.readInt() == 1);
        admin = (in.readInt() == 1);

        postOfficial = (in.readInt() == 1);
        viewLatestPosts = (in.readInt() == 1);
        viewPostHistory = (in.readInt() == 1);

        in.readTypedList(posts, ForumPostData.CREATOR);

    }

    public ForumThreadData(

            long tId, long fId, long tDate, long lpDate, int nOffPosts, int nPosts, String t,
            ProfileData o, ProfileData lp, boolean st, boolean lo

    ) {

        id = tId;
        forumId = fId;
        date = tDate;
        lastPostDate = lpDate;

        numOfficialPosts = nOffPosts;
        numPosts = nPosts;
        numCurrentPage = 0;
        numTotalPages = 0;

        title = t;

        owner = o;
        lastPoster = lp;

        sticky = st;
        locked = lo;

        censorPosts = false;
        deletePosts = false;
        editPosts = false;
        admin = false;
        postOfficial = false;
        viewLatestPosts = false;
        viewPostHistory = false;

        posts = null;

    }

    public ForumThreadData(

            long tId, long fId, long tDate, long lpDate, int nOffPosts, int nPosts,
            int nCurrPage, int nPages, String t, ProfileData o,
            ProfileData lp, boolean st, boolean lo, boolean cePosts,
            boolean ccPosts, boolean cdPosts, boolean cpOfficial,
            boolean cvlPosts, boolean cvpHistory, boolean ad,
            List<ForumPostData> aPosts

    ) {

        id = tId;
        forumId = fId;
        date = tDate;
        lastPostDate = lpDate;

        numOfficialPosts = nOffPosts;
        numPosts = nPosts;
        numCurrentPage = nCurrPage;
        numTotalPages = nPages;

        title = t;

        owner = o;
        lastPoster = lp;

        sticky = st;
        locked = lo;

        censorPosts = ccPosts;
        deletePosts = cdPosts;
        editPosts = cePosts;
        admin = ad;
        postOfficial = cpOfficial;
        viewLatestPosts = cvlPosts;
        viewPostHistory = cvpHistory;

        posts = aPosts;

    }

    // Getters
    public long getId() {
        return id;
    }

    public long getForumId() {

        return forumId;
    }

    public long getDate() {
        return date;
    }

    public long getLastPostDate() {
        return lastPostDate;
    }

    public int getNumOfficialPosts() {
        return numOfficialPosts;
    }

    public int getNumPosts() {
        return numPosts + numOfficialPosts;
    }

    public int getNumCurrentPage() {
        return numCurrentPage;
    }

    public int getNumPages() {
        return numTotalPages;
    }

    public String getTitle() {
        return title;
    }

    public ProfileData getOwner() {
        return owner;
    }

    public ProfileData getLastPoster() {
        return lastPoster;
    }

    public boolean isSticky() {
        return sticky;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean hasOfficialResponse() {
        return (numOfficialPosts > 0);
    }

    public boolean canEditPosts() {
        return editPosts;
    }

    public boolean canCensorPosts() {
        return censorPosts;
    }

    public boolean canDeletePosts() {
        return deletePosts;
    }

    public boolean canPostOfficial() {
        return postOfficial;
    }

    public boolean canViewLatestPosts() {
        return viewLatestPosts;
    }

    public boolean canViewPostHistory() {
        return viewPostHistory;
    }

    public boolean isAdmin() {
        return admin;
    }

    public List<ForumPostData> getPosts() {
        return posts;
    }

    // Setters
    public void setNumPosts(int n) {

        numPosts = n;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(id);
        dest.writeLong(forumId);
        dest.writeLong(date);
        dest.writeLong(lastPostDate);

        dest.writeInt(numOfficialPosts);
        dest.writeInt(numPosts);
        dest.writeInt(numCurrentPage);
        dest.writeInt(numTotalPages);

        dest.writeString(title);

        dest.writeParcelable(owner, flags);
        dest.writeParcelable(lastPoster, flags);

        dest.writeInt(sticky ? 1 : 0);
        dest.writeInt(locked ? 1 : 0);

        dest.writeInt(censorPosts ? 1 : 0);
        dest.writeInt(deletePosts ? 1 : 0);
        dest.writeInt(editPosts ? 1 : 0);
        dest.writeInt(admin ? 1 : 0);
        dest.writeInt(postOfficial ? 1 : 0);
        dest.writeInt(viewLatestPosts ? 1 : 0);
        dest.writeInt(viewPostHistory ? 1 : 0);

        dest.writeTypedList(posts);

    }

    public static final Parcelable.Creator<ForumThreadData> CREATOR = new Parcelable.Creator<ForumThreadData>() {

        public ForumThreadData createFromParcel(Parcel in) {
            return new ForumThreadData(in);
        }

        public ForumThreadData[] newArray(int size) {
            return new ForumThreadData[size];
        }

    };

    @Override
    public String toString() {
        return owner + ":" + title;
    }

    public Object[] toArray() {

        // Let's det the date
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);

        // Return a new String[]
        return new Object[]{

                id,
                forumId,
                title,
                lastPostDate,
                lastPoster.getUsername(),
                lastPoster.getId(),
                numTotalPages,
                numPosts,
                0,
                time,
                time

        };

    }

}
