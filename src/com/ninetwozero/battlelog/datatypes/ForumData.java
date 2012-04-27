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

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import com.ninetwozero.battlelog.R;

public class ForumData implements Parcelable {

    // Attributes
    private long forumId, categoryId, latestPostDate, latestPostThreadId,
            latestPostId;
    private long numPosts, numThreads, numPages;
    private String title, description;
    private String latestThreadTitle, latestPostUsername;
    private List<ForumThreadData> threads;

    // Constructs
    public ForumData(String fTitle, String fDescription, long nPosts,
            long nThreads, long nPages, List<ForumThreadData> aThreads) {

        forumId = 0;
        categoryId = 0;
        latestPostDate = 0;
        latestPostThreadId = 0;
        latestPostId = 0;
        numPosts = nPosts;
        numThreads = nThreads;
        numPages = nPages;

        title = fTitle;
        description = fDescription;
        latestThreadTitle = null;
        latestPostUsername = null;

        threads = aThreads;

    }

    public ForumData(

            long fId, long cId, long lpDate, long lpTId, long lpId, long nPosts,
            long nThreads, long nPages, String t, String d, String ltTitle,
            String lpUser

    ) {

        forumId = fId;
        categoryId = cId;
        latestPostDate = lpDate;
        latestPostThreadId = lpTId;
        latestPostId = lpId;
        numPosts = nPosts;
        numThreads = nThreads;
        numPages = nPages;

        title = t;
        description = d;
        latestThreadTitle = ltTitle;
        latestPostUsername = lpUser;

        threads = null;

    }

    public ForumData(Parcel in) {

        forumId = in.readLong();
        categoryId = in.readLong();
        latestPostDate = in.readLong();
        latestPostThreadId = in.readLong();
        latestPostId = in.readLong();
        numPosts = in.readLong();
        numThreads = in.readLong();
        numPages = in.readLong();

        title = in.readString();
        description = in.readString();
        latestThreadTitle = in.readString();
        latestPostUsername = in.readString();

        in.readTypedList(threads, ForumThreadData.CREATOR);

    }

    // Getters
    public long getForumId() {
        return forumId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public long getLatestPostDate() {
        return latestPostDate;
    }

    public long getLatestPostForumId() {
        return latestPostThreadId;
    }

    public long getLatestPostId() {
        return latestPostId;
    }

    public long getNumPosts() {
        return numPosts;
    }

    public long getNumThreads() {
        return numThreads;
    }

    public long getNumPages() {
        return numPages;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLatestThreadTitle() {
        return latestThreadTitle;
    }

    public String getLatestPostUsername() {
        return latestPostUsername;
    }

    public List<ForumThreadData> getThreads() {
        return threads;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(forumId);
        dest.writeLong(categoryId);
        dest.writeLong(latestPostDate);
        dest.writeLong(latestPostThreadId);
        dest.writeLong(latestPostId);
        dest.writeLong(numPosts);
        dest.writeLong(numThreads);
        dest.writeLong(numPages);

        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(latestThreadTitle);
        dest.writeString(latestPostUsername);

        dest.writeTypedList(threads);

    }

    public static final Parcelable.Creator<ForumData> CREATOR = new Parcelable.Creator<ForumData>() {

        public ForumData createFromParcel(Parcel in) {
            return new ForumData(in);
        }

        public ForumData[] newArray(int size) {
            return new ForumData[size];
        }

    };

}
