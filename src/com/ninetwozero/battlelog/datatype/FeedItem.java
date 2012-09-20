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

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.forum.ForumActivity;
import com.ninetwozero.battlelog.activity.platoon.PlatoonActivity;
import com.ninetwozero.battlelog.activity.profile.assignments.AssignmentActivity;
import com.ninetwozero.battlelog.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.battlelog.activity.profile.unlocks.UnlockActivity;

public class FeedItem implements Parcelable {

    // Constants
    public final static String LABEL_NEW_STATUS = "statusmessage";
    public final static int TYPE_NEW_STATUS = 0;
    public final static int TYPE_NEW_FRIEND = 1;
    public final static int TYPE_NEW_FORUM_THREAD = 2;
    public final static int TYPE_NEW_FORUM_POST = 3;
    public final static int TYPE_GOT_WALL_POST = 4;
    public final static int TYPE_GOT_PLATOON_POST = 5;
    public final static int TYPE_NEW_FAVSERVER = 6;
    public final static int TYPE_NEW_RANK = 7;
    public final static int TYPE_COMPLETED_LEVEL = 8;
    public final static int TYPE_NEW_PLATOON = 9;
    public final static int TYPE_NEW_EMBLEM = 10;
    public final static int TYPE_JOINED_PLATOON = 11;
    public final static int TYPE_KICKED_PLATOON = 12;
    public final static int TYPE_LEFT_PLATOON = 13;
    public final static int TYPE_COMPLETED_GAME = 14;
    public final static int TYPE_GOT_AWARD = 15;
    public final static int TYPE_COMPLETED_ASSIGNMENT = 16;
    public final static int TYPE_NEW_COMMENT_GAME = 17;
    public final static int TYPE_NEW_COMMENT_BLOG = 18;
    public final static int TYPE_NEW_EXPANSION = 19;
    public final static int TYPE_NEW_SHARED_GAMEEVENT = 20;

    // Attributes
    private long id, itemId, date;
    private int numLikes, numComments, type;
    private String title, content;
    private ProfileData[] profileData;
    private boolean liked, censored;
    private String gravatarHash;

    // Construct
    public FeedItem(

            long i, long iid, long nDate, int num, int numC, int tp,
            String t, String c, ProfileData[] pd, boolean il, boolean cs, String im

    ) {

        id = i;
        itemId = iid;
        date = nDate;
        numLikes = num;
        numComments = numC;
        type = tp;
        title = t;
        content = c;
        profileData = pd.clone();
        liked = il;
        censored = cs;
        gravatarHash = im;

    }

    public FeedItem(Parcel in) {

        id = in.readLong();
        itemId = in.readLong();
        date = in.readLong();
        numLikes = in.readInt();
        numComments = in.readInt();
        type = in.readInt();
        title = in.readString();
        content = in.readString();
        liked = (in.readInt() == 1);
        censored = (in.readInt() == 1);
        gravatarHash = in.readString();
        profileData = in.createTypedArray(ProfileData.CREATOR);

    }

    // Getters
    public long getId() {
        return id;
    }

    public long getItemId() {
        return itemId;
    }

    public long getDate() {
        return date;
    }

    public int getNumComments() {
        return numComments;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public String getTitle() {
        return title;

    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public ProfileData[] getProfileData() {
        return profileData;
    }

    public ProfileData getProfile(int i) {
        return (i <= profileData.length) ? profileData[i] : null;
    }

    public boolean isLiked() {
        return liked;
    }

    public boolean isCensored() {
        return censored;
    }

    public String getAvatarForPost() {
        return gravatarHash;
    }

    public Intent getIntent(Context c) {

        // Get the correct format depending on the type
        switch (type) {
            case FeedItem.TYPE_NEW_FORUM_THREAD:
            case FeedItem.TYPE_NEW_FORUM_POST:

                return new Intent(c, ForumActivity.class).putExtra(

                        "threadId", itemId

                        ).putExtra(

                                "threadTitle", "N/A"

                        ).putExtra("forumId", 0).putExtra("forumTitle", "N/A");

            case FeedItem.TYPE_NEW_PLATOON:
            case FeedItem.TYPE_NEW_EMBLEM:
            case FeedItem.TYPE_JOINED_PLATOON:
            case FeedItem.TYPE_KICKED_PLATOON:
            case FeedItem.TYPE_GOT_PLATOON_POST:
            case FeedItem.TYPE_LEFT_PLATOON:
                return new Intent(c, PlatoonActivity.class).putExtra("platoon",
                        new PlatoonData(itemId));

            case FeedItem.TYPE_COMPLETED_GAME:
                return new Intent(c, UnlockActivity.class).putExtra("profile",
                        profileData[0]);

            case FeedItem.TYPE_NEW_RANK:
            case FeedItem.TYPE_NEW_FRIEND:
            case FeedItem.TYPE_GOT_WALL_POST:
            case FeedItem.TYPE_COMPLETED_LEVEL:
            case FeedItem.TYPE_NEW_STATUS:
            case FeedItem.TYPE_NEW_FAVSERVER:
            case FeedItem.TYPE_NEW_COMMENT_GAME:
            case FeedItem.TYPE_NEW_COMMENT_BLOG:
            case FeedItem.TYPE_NEW_EXPANSION:
            case FeedItem.TYPE_GOT_AWARD:
                return new Intent(c, ProfileActivity.class).putExtra(

                        "profile", profileData[0]

                        );

            case FeedItem.TYPE_COMPLETED_ASSIGNMENT:
                return new Intent(c, AssignmentActivity.class).putExtra(
                        "profile",
                        profileData[0]
                        );

            default:
                return new Intent();
        }

    }

    public String getOptionTitle(Context c) {

        // Get the correct format depending on the type
        switch (type) {
            case FeedItem.TYPE_NEW_FORUM_THREAD:
            case FeedItem.TYPE_NEW_FORUM_POST:
                return c.getString(R.string.label_goto_forum_thread);

            case FeedItem.TYPE_NEW_PLATOON:
            case FeedItem.TYPE_NEW_EMBLEM:
            case FeedItem.TYPE_JOINED_PLATOON:
            case FeedItem.TYPE_KICKED_PLATOON:
            case FeedItem.TYPE_GOT_PLATOON_POST:
            case FeedItem.TYPE_LEFT_PLATOON:
                return c.getString(R.string.label_goto_platoon);

            case FeedItem.TYPE_COMPLETED_GAME:
                return c.getString(R.string.label_goto_unlocks);

            case FeedItem.TYPE_NEW_RANK:
            case FeedItem.TYPE_NEW_FRIEND:
            case FeedItem.TYPE_GOT_WALL_POST:
            case FeedItem.TYPE_COMPLETED_LEVEL:
            case FeedItem.TYPE_NEW_STATUS:
            case FeedItem.TYPE_NEW_FAVSERVER:
            case FeedItem.TYPE_NEW_COMMENT_GAME:
            case FeedItem.TYPE_NEW_COMMENT_BLOG:
            case FeedItem.TYPE_NEW_EXPANSION:
            case FeedItem.TYPE_GOT_AWARD:
                return c.getString(R.string.label_goto_profile);

            case FeedItem.TYPE_COMPLETED_ASSIGNMENT:
                return c.getString(R.string.label_goto_assignments);

            default:
                return "N/A";

        }

    }

    @Override
    public int describeContents() {

        return 0;

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(id);
        dest.writeLong(itemId);
        dest.writeLong(date);
        dest.writeInt(numLikes);
        dest.writeInt(numComments);
        dest.writeInt(type);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeInt(liked ? 1 : 0);
        dest.writeInt(censored ? 1 : 0);
        dest.writeString(gravatarHash);
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
