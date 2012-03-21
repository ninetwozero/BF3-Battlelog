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

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.ninetwozero.battlelog.AssignmentActivity;
import com.ninetwozero.battlelog.Backup_ForumThreadView;
import com.ninetwozero.battlelog.PlatoonActivity;
import com.ninetwozero.battlelog.ProfileActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.UnlockActivity;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class FeedItem implements Parcelable {

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
            String type, ProfileData[] pd, boolean il, boolean cs, String im

    ) {

        this.id = i;
        this.itemId = iid;
        this.date = nDate;
        this.numLikes = num;
        this.numComments = numC;
        this.title = t;
        this.content = c;
        this.type = type;
        this.profileData = pd;
        this.liked = il;
        this.censored = cs;
        this.gravatarHash = im;

    }

    public FeedItem(Parcel in) {

        this.id = in.readLong();
        this.itemId = in.readLong();
        this.date = in.readLong();
        this.numLikes = in.readInt();
        this.numComments = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.type = in.readString();
        this.liked = (in.readInt() == 1);
        this.censored = (in.readInt() == 1);
        this.gravatarHash = in.readString();
        this.profileData = in.createTypedArray(ProfileData.CREATOR);

    }

    // Getters
    public long getId() {
        return this.id;
    }

    public long getItemId() {
        return this.itemId;
    }

    public long getDate() {
        return this.date;
    }

    public int getNumComments() {
        return this.numComments;
    }

    public int getNumLikes() {
        return this.numLikes;
    }

    public String getTitle() {

        // Get the correct format depending on the type
        if (type.equals("becamefriends")) {

            return this.title.replace(

                    "{username1}", profileData[0].getUsername()

                    ).replace(

                            "{username2}", profileData[1].getUsername()

                    );

        } else if (type.equals("assignmentcomplete")) {

            return this.title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("createdforumthread")
                || type.equals("wroteforumpost")) {

            return this.title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("gamereport")) {

            return this.title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("statusmessage")) {

            return this.title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("addedfavserver")) {

            return this.title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("rankedup")) {

            return this.title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("levelcomplete")) {

            return this.title.replace(

                    "{username1}", profileData[0].getUsername()

                    ).replace(

                            "{username2}", profileData[1].getUsername()

                    );

        } else if (type.equals("kickedplatoon") || type.equals("joinedplatoon")
                || type.equals("leftplatoon")) {

            return this.title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("createdplatoon")
                || type.equals("platoonbadgesaved")
                || type.equals("receivedplatoonwallpost")) {

            return this.title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("receivedaward")) {

            return this.title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("receivedwallpost")) {

            return this.title.replace(

                    "{username1}", profileData[0].getUsername()

                    ).replace(

                            "{username2}", profileData[1].getUsername()

                    );

        } else if (type.equals("commentedgamereport")
                || type.equals("commentedblog")) {

            return this.title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else if (type.equals("gameaccess")) {

            return this.title.replace(

                    "{username}", profileData[0].getUsername()

                    );

        } else {

            return this.title;

        }

    }

    public String getContent() {
        return this.content;
    }

    public String getType() {
        return this.type;
    }

    public ProfileData[] getProfileData() {
        return this.profileData;
    }

    public ProfileData getProfile(int i) {
        return (i <= this.profileData.length) ? this.profileData[i] : null;
    }

    public boolean isLiked() {
        return this.liked;
    }

    public boolean isCensored() {
        return this.censored;
    }

    public String getAvatarForPost() {
        return this.gravatarHash;
    }

    public Intent getIntent(Context c) {

        if (type.equals("assignmentcomplete")) {

            try {

                return new Intent(c, AssignmentActivity.class).putExtra(

                        "profile", WebsiteHandler
                                .getPersonaIdFromProfile(profileData[0].getId())

                        );

            } catch (Exception ex) {

                ex.printStackTrace();
                return null;

            }

        } else if (type.equals("createdforumthread")
                || type.equals("wroteforumpost")) {

            return new Intent(c, Backup_ForumThreadView.class).putExtra(

                    "threadId", this.itemId

                    ).putExtra(

                            "threadTitle", "N/A"

                    );

        } else if (

        type.equals("kickedplatoon") || type.equals("joinedplatoon")
                || type.equals("leftplatoon") || type.equals("createdplatoon")
                || type.equals("platoonbadgesaved")
                || type.equals("receivedplatoonwallpost")

        ) {

            return new Intent(c, PlatoonActivity.class).putExtra("platoon",
                    new PlatoonData(this.itemId));

        } else if (type.equals("gamereport")) {

            return new Intent(c, UnlockActivity.class).putExtra("profile",
                    this.profileData[0]);

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

        dest.writeLong(this.id);
        dest.writeLong(this.itemId);
        dest.writeLong(this.date);
        dest.writeInt(this.numLikes);
        dest.writeInt(this.numComments);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.type);
        dest.writeInt(this.liked ? 1 : 0);
        dest.writeInt(this.censored ? 1 : 0);
        dest.writeString(this.gravatarHash);
        dest.writeTypedArray(this.profileData, flags);

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
