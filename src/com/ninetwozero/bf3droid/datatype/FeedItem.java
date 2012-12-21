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

package com.ninetwozero.bf3droid.datatype;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.ninetwozero.bf3droid.activity.forum.ForumActivity;
import com.ninetwozero.bf3droid.activity.platoon.PlatoonActivity;
import com.ninetwozero.bf3droid.activity.profile.assignments.AssignmentActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.bf3droid.activity.profile.unlocks.UnlockActivity;

import java.util.List;

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
	private long mId, mItemId, mDate;
	private int mNumLikes, mNumComments, mType;
	private String mTitle, mContent;
	private ProfileData[] mProfileData;
	private boolean mLiked, mCensored;
	private String mGravatarHash;
	private List<CommentData> mPreloadedComments;

	// Construct
	public FeedItem(long i, long iid, long nDate, int num, int numC, int tp,
			String t, String c, ProfileData[] pd, boolean il, boolean cs,
			String im, List<CommentData> prlc) {
		mId = i;
		mItemId = iid;
		mDate = nDate;
		mNumLikes = num;
		mNumComments = numC;
		mType = tp;
		mTitle = t;
		mContent = c;
		mProfileData = pd.clone();
		mLiked = il;
		mCensored = cs;
		mGravatarHash = im;
		mPreloadedComments = prlc;
	}

	public FeedItem(Parcel in) {
		mId = in.readLong();
		mItemId = in.readLong();
		mDate = in.readLong();
		mNumLikes = in.readInt();
		mNumComments = in.readInt();
		mType = in.readInt();
		mTitle = in.readString();
		mContent = in.readString();
		mLiked = (in.readInt() == 1);
		mCensored = (in.readInt() == 1);
		mGravatarHash = in.readString();
		mProfileData = in.createTypedArray(ProfileData.CREATOR);
		mPreloadedComments = in.createTypedArrayList(CommentData.CREATOR);
	}

	// Getters
	public long getId() {
		return mId;
	}

	public long getItemId() {
		return mItemId;
	}

	public long getDate() {
		return mDate;
	}

	public int getNumComments() {
		return mNumComments;
	}

	public int getNumLikes() {
		return mNumLikes;
	}

	public String getTitle() {
		return mTitle;

	}

	public String getContent() {
		return mContent;
	}

	public int getType() {
		return mType;
	}

	public ProfileData[] getProfileData() {
		return mProfileData;
	}

	public ProfileData getProfile(int i) {
		return (i <= mProfileData.length) ? mProfileData[i] : null;
	}

	public boolean isLiked() {
		return mLiked;
	}

	public boolean isCensored() {
		return mCensored;
	}

	public String getAvatarForPost() {
		return mGravatarHash;
	}

	public boolean hasPreloadedComments() {
		return mPreloadedComments != null && mPreloadedComments.size() > 0;
	}

	public List<CommentData> getPreloadedComments() {
		return mPreloadedComments;
	}

	@Deprecated
	public Intent getIntent(Context c) {

		// Get the correct format depending on the type
		switch (mType) {
		case FeedItem.TYPE_NEW_FORUM_THREAD:
		case FeedItem.TYPE_NEW_FORUM_POST:

			return new Intent(c, ForumActivity.class).putExtra(

			"threadId", mItemId

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
					new PlatoonData(mItemId, ""));

		case FeedItem.TYPE_COMPLETED_GAME:
			return new Intent(c, UnlockActivity.class).putExtra("profile",
					mProfileData[0]);

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

			"profile", mProfileData[0]

			);

		case FeedItem.TYPE_COMPLETED_ASSIGNMENT:
			return new Intent(c, AssignmentActivity.class).putExtra("profile",
					mProfileData[0]);

		default:
			return new Intent();
		}

	}

	@Override
	public int describeContents() {

		return 0;

	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeLong(mId);
		dest.writeLong(mItemId);
		dest.writeLong(mDate);
		dest.writeInt(mNumLikes);
		dest.writeInt(mNumComments);
		dest.writeInt(mType);
		dest.writeString(mTitle);
		dest.writeString(mContent);
		dest.writeInt(mLiked ? 1 : 0);
		dest.writeInt(mCensored ? 1 : 0);
		dest.writeString(mGravatarHash);
		dest.writeTypedArray(mProfileData, flags);
		dest.writeTypedList(mPreloadedComments);

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
