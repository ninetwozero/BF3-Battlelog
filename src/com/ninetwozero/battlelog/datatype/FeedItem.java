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
    public final static int TYPE_GLOBAL = 0;
    public final static int TYPE_PROFILE = 1;
    public final static int TYPE_PLATOON = 2;

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
        if (type.equals("becamefriends")) {

            return title.replace(

                    "{username1}", profileData[0].getUsername()

                    ).replace(

                            "{username2}", profileData[1].getUsername()

                    );

        } else if (type.equals("assignmentcomplete")) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("createdforumthread")
                || type.equals("wroteforumpost")) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("gamereport")) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("statusmessage")) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("addedfavserver")) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("rankedup")) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("levelcomplete")) {

            return title.replace(

                    "{username1}", profileData[0].getUsername()

                    ).replace(

                            "{username2}", profileData[1].getUsername()

                    );

        } else if (type.equals("kickedplatoon") || type.equals("joinedplatoon")
                || type.equals("leftplatoon")) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("createdplatoon")
                || type.equals("platoonbadgesaved")
                || type.equals("receivedplatoonwallpost")) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("receivedaward")) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("receivedwallpost")) {

            return title.replace(

                    "{username1}", profileData[0].getUsername()

                    ).replace(

                            "{username2}", profileData[1].getUsername()

                    );

        } else if (type.equals("commentedgamereport")
                || type.equals("commentedblog")) {

            return title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("gameaccess")) {

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

        if (type.equals("assignmentcomplete")) {

            try {

                return new Intent(c, AssignmentActivity.class).putExtra(

                        "profile", ProfileClient.resolveFullProfileDataFromProfileId(profileData[0].getId())

                        );

            } catch (Exception ex) {

                ex.printStackTrace();
                return null;

            }

        } else if (type.equals("createdforumthread")
                || type.equals("wroteforumpost")) {

            return new Intent(c, ForumActivity.class).putExtra(

                    "threadId", itemId

                    ).putExtra(

                            "threadTitle", "N/A"

                    ).putExtra("forumId", 0).putExtra("forumTitle", "N/A");

        } else if (

        type.equals("kickedplatoon") || type.equals("joinedplatoon")
                || type.equals("leftplatoon") || type.equals("createdplatoon")
                || type.equals("platoonbadgesaved")
                || type.equals("receivedplatoonwallpost")

        ) {

            return new Intent(c, PlatoonActivity.class).putExtra("platoon",
                    new PlatoonData(itemId));

        } else if (type.equals("gamereport")) {

            return new Intent(c, UnlockActivity.class).putExtra("profile",
                    profileData[0]);

        } else if (

        type.equals("becamefriends") || type.equals("receivedaward")
                || type.equals("receivedwallpost")
                || type.equals("commentedgamereport")
                || type.equals("commentedblog") || type.equals("statusmessage")
                || type.equals("addedfavserver") || type.equals("rankedup")
                || type.equals("levelcomplete") || type.equals("gameaccess")

        ) {

            return new Intent(c, ProfileActivity.class).putExtra(

                    "profile", profileData[0]

                    );

        } else {

            return null;

        }

    }

    public String getOptionTitle(Context c) {

        if (type.equals("assignmentcomplete")) {

            return c.getString(R.string.label_goto_assignments);

        } else if (type.equals("createdforumthread")
                || type.equals("wroteforumpost")) {

            return c.getString(R.string.label_goto_forum_thread);

        } else if (

        type.equals("kickedplatoon") || type.equals("joinedplatoon")
                || type.equals("leftplatoon") || type.equals("createdplatoon")
                || type.equals("platoonbadgesaved")
                || type.equals("receivedplatoonwallpost")

        ) {

            return c.getString(R.string.label_goto_platoon);

        } else if (type.equals("gamereport")) {

            return c.getString(R.string.label_goto_unlocks);

        } else if (

        type.equals("becamefriends") || type.equals("receivedaward")
                || type.equals("receivedwallpost")
                || type.equals("commentedgamereport")
                || type.equals("commentedblog") || type.equals("statusmessage")
                || type.equals("addedfavserver") || type.equals("rankedup")
                || type.equals("levelcomplete") || type.equals("gameaccess")

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
