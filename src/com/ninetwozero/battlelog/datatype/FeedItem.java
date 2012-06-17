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
import com.ninetwozero.battlelog.http.ProfileClient;

public class FeedItem implements Parcelable {

    // Constants
    public static final int TYPE_GLOBAL = 0;
    public static final int TYPE_PROFILE = 1;
    public static final int TYPE_PLATOON = 2;

    // Attributes
    private long id, itemId, date;
    private int numLikes, numComments;
    private String title, content, type;
    private ProfileData[] profileData;
    private boolean liked, censored;
    private String gravatarHash;

    // Construct
    public FeedItem(

            long i, long iid, long nDate, int num, int numC, String t, String c,
            String tp, ProfileData[] pd, boolean il, boolean cs, String im

    ) {

        id = i;
        itemId = iid;
        date = nDate;
        numLikes = num;
        numComments = numC;
        title = t;
        content = c;
        type = tp;
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
        title = in.readString();
        content = in.readString();
        type = in.readString();
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

        // Get the correct format depending on the type
        if ("becamefriends".equals(type)) {

            return title.replace(

                    "{username1}", profileData[0].getUsername()

                    ).replace(

                            "{username2}", profileData[1].getUsername()

                    );

        } else if ("assignmentcomplete".equals(type)) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if ("createdforumthread".equals(type)
                || "wroteforumpost".equals(type)) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if ("gamereport".equals(type)) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if ("statusmessage".equals(type)) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if ("addedfavserver".equals(type)) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if ("rankedup".equals(type)) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if ("levelcomplete".equals(type)) {

            return title.replace(

                    "{username1}", profileData[0].getUsername()

                    ).replace(

                            "{username2}", profileData[1].getUsername()

                    );

        } else if ("kickedplatoon".equals(type) || "joinedplatoon".equals(type)
                || "leftplatoon".equals(type)) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if ("createdplatoon".equals(type)
                || "platoonbadgesaved".equals(type)
                || "receivedplatoonwallpost".equals(type)) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if ("receivedaward".equals(type)) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if ("receivedwallpost".equals(type)) {

            return title.replace(

                    "{username1}", profileData[0].getUsername()

                    ).replace(

                            "{username2}", profileData[1].getUsername()

                    );

        } else if ("commentedgamereport".equals(type)
                || "commentedblog".equals(type)) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if ("gameaccess".equals(type)) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else {

            return title;

        }

    }

    public String getContent() {
        return content;
    }

    public String getType() {
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

        if ("assignmentcomplete".equals(type)) {

            try {

                return new Intent(c, AssignmentActivity.class).putExtra(

                        "profile",
                        ProfileClient.resolveFullProfileDataFromProfileId(profileData[0].getId())

                        );

            } catch (Exception ex) {

                ex.printStackTrace();
                return null;

            }

        } else if ("createdforumthread".equals(type)
                || "wroteforumpost".equals(type)) {

            return new Intent(c, ForumActivity.class).putExtra(

                    "threadId", itemId

                    ).putExtra(

                            "threadTitle", "N/A"

                    ).putExtra("forumId", 0).putExtra("forumTitle", "N/A");

        } else if (

        "kickedplatoon".equals(type) || "joinedplatoon".equals(type)
                || "leftplatoon".equals(type) || "createdplatoon".equals(type)
                || "platoonbadgesaved".equals(type)
                || "receivedplatoonwallpost".equals(type)

        ) {

            return new Intent(c, PlatoonActivity.class).putExtra("platoon",
                    new PlatoonData(itemId));

        } else if ("gamereport".equals(type)) {

            return new Intent(c, UnlockActivity.class).putExtra("profile",
                    profileData[0]);

        } else if (

        "becamefriends".equals(type) || "receivedaward".equals(type)
                || "receivedwallpost".equals(type)
                || "commentedgamereport".equals(type)
                || "commentedblog".equals(type) || "statusmessage".equals(type)
                || "addedfavserver".equals(type) || "rankedup".equals(type)
                || "levelcomplete".equals(type) || "gameaccess".equals(type)

        ) {

            return new Intent(c, ProfileActivity.class).putExtra(

                    "profile", profileData[0]

                    );

        } else {

            return new Intent();

        }

    }

    public String getOptionTitle(Context c) {

        if ("assignmentcomplete".equals(type)) {

            return c.getString(R.string.label_goto_assignments);

        } else if ("createdforumthread".equals(type)
                || "wroteforumpost".equals(type)) {

            return c.getString(R.string.label_goto_forum_thread);

        } else if (

        "kickedplatoon".equals(type) || "joinedplatoon".equals(type)
                || "leftplatoon".equals(type) || "createdplatoon".equals(type)
                || "platoonbadgesaved".equals(type)
                || "receivedplatoonwallpost".equals(type)

        ) {

            return c.getString(R.string.label_goto_platoon);

        } else if ("gamereport".equals(type)) {

            return c.getString(R.string.label_goto_unlocks);

        } else if (

        "becamefriends".equals(type) || "receivedaward".equals(type)
                || "receivedwallpost".equals(type)
                || "commentedgamereport".equals(type)
                || "commentedblog".equals(type) || "statusmessage".equals(type)
                || "addedfavserver".equals(type) || "rankedup".equals(type)
                || "levelcomplete".equals(type) || "gameaccess".equals(type)

        ) {

            return c.getString(R.string.label_goto_profile);

        } else {

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
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(type);
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
