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

import java.util.List;

public class PlatoonStats implements Parcelable {
    private String mName;
    private long mId;

    private List<PlatoonStatsItem> mGlobalToplist; 
    private List<PlatoonStatsItem> mScores; 
    private List<PlatoonStatsItem> mScorePerMinute;
    private List<PlatoonStatsItem> mTime;
    private List<PlatoonTopStatsItem> mTopPlayers;

    // Construct
    public PlatoonStats(
		String sName, 
		long lId, 
		List<PlatoonStatsItem> gS,
		List<PlatoonTopStatsItem> tP, 
		List<PlatoonStatsItem> kS,	
		List<PlatoonStatsItem> kSPM, 
		List<PlatoonStatsItem> kT
	) {
        mName = sName;
        mId = lId;

        mGlobalToplist = gS;
        mTopPlayers = tP;
        mScores = kS;
        mScorePerMinute = kSPM;
        mTime = kT;
    }

    @SuppressWarnings("unchecked")
    public PlatoonStats(Parcel in) {
        mName = in.readString();
        mId = in.readLong();

        mGlobalToplist = (List<PlatoonStatsItem>) in.readParcelable(PlatoonStatsItem.class.getClassLoader());
        mTopPlayers = (List<PlatoonTopStatsItem>) in.readParcelable(PlatoonStatsItem.class.getClassLoader());
        mScores = (List<PlatoonStatsItem>) in.readParcelable(PlatoonStatsItem.class.getClassLoader());
        mScorePerMinute = (List<PlatoonStatsItem>) in.readParcelable(PlatoonStatsItem.class.getClassLoader());
        mTime = (List<PlatoonStatsItem>) in.readParcelable(PlatoonTopStatsItem.class.getClassLoader());
    }

    // Getters
    public final String getName() {
        return mName;
    }

    public final long getId() {
        return mId;
    }

    public final List<PlatoonTopStatsItem> getTopPlayers() {
        return mTopPlayers;
    }

    public final List<PlatoonStatsItem> getGlobalTop() {
        return mGlobalToplist;
    }

    public final List<PlatoonStatsItem> getScores() {
        return mScores;
    }

    public final List<PlatoonStatsItem> getSpm() {
        return mScorePerMinute;
    }

    public final List<PlatoonStatsItem> getTime() {
        return mTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static final Parcelable.Creator<PlatoonStats> CREATOR = new Parcelable.Creator<PlatoonStats>() {
        public PlatoonStats createFromParcel(Parcel in) {
            return new PlatoonStats(in);
        }

        public PlatoonStats[] newArray(int size) {
            return new PlatoonStats[size];
        }
    };

	@Override
	public String toString() {
		return "PlatoonStats [mName=" + mName + ", mId=" + mId
				+ ", mGlobalToplist=" + mGlobalToplist + ", mScores=" + mScores
				+ ", mScorePerMinute=" + mScorePerMinute + ", mTime=" + mTime
				+ ", mTopPlayers=" + mTopPlayers + "]";
	}
    
}
