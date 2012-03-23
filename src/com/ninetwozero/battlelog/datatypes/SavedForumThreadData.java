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

public class SavedForumThreadData implements Parcelable {
    public static final String COLUMN_NAME_NUM_ID = "thread_id";
    public static final String COLUMN_NAME_NUM_DATE_LAST_POST = "date_last_post";
    public static final String COLUMN_NAME_STRING_LAST_AUTHOR = "last_post_author";
    public static final String COLUMN_NAME_NUM_LAST_AUTHOR_ID = "last_post_author_id";
    public static final String COLUMN_NAME_NUM_LAST_PAGE_ID = "last_page_id";
    public static final String COLUMN_NAME_NUM_DATE_READ = "date_read";
    public static final String COLUMN_NAME_NUM_DATE_CHECKED = "date_checked";
    // Attributes
    private long threadId, dateLastPost, dateLastChecked, dateLastRead;
    private String title;
    private ProfileData lastPoster;
    private int numPageLastRead;

    // Construct
    public SavedForumThreadData(String t) {
        title = t;
    }

    public SavedForumThreadData(Parcel in) {

        threadId = in.readLong();
        lastPoster = in.readParcelable(ProfileData.class.getClassLoader());

 

    }

    public SavedForumThreadData(

            long tId, long tDate, long lpDate, int nOffPosts, int nPosts, String t,
            ProfileData o, ProfileData lp, boolean st, boolean lo

    ) {

        threadId = tId;
       
        title = t;

        lastPoster = lp;


    }

    // Getters
    public long getThreadId() {
        return threadId;
    }

   

    public String getTitle() {
        return title;
    }

    
    public ProfileData getLastPoster() {
        return lastPoster;
    }

    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


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
