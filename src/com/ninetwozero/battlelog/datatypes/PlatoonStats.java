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

public class PlatoonStats implements Parcelable {

    // Base-section
    private String name;
    private long id;

    // General stats
    private List<PlatoonStatsItem> globalTop, scores, spm, time;
    private List<PlatoonTopStatsItem> topPlayers;

    // Construct
    public PlatoonStats(String sName, long lId, List<PlatoonStatsItem> gS,
            List<PlatoonTopStatsItem> tP, List<PlatoonStatsItem> kS,
            List<PlatoonStatsItem> kSPM, List<PlatoonStatsItem> kT) {

        // Basic attributes
        name = sName;
        id = lId;

        globalTop = gS;
        topPlayers = tP;
        scores = kS;
        spm = kSPM;
        time = kT;

    }

    @SuppressWarnings("unchecked")
    public PlatoonStats(Parcel in) {

        // Basic attributes
        name = in.readString();
        id = in.readLong();

        globalTop = (List<PlatoonStatsItem>) in.readParcelable(PlatoonStatsItem.class
                .getClassLoader());
        topPlayers = (List<PlatoonTopStatsItem>) in.readParcelable(PlatoonStatsItem.class
                .getClassLoader());
        scores = (List<PlatoonStatsItem>) in
                .readParcelable(PlatoonStatsItem.class.getClassLoader());
        spm = (List<PlatoonStatsItem>) in.readParcelable(PlatoonStatsItem.class.getClassLoader());
        time = (List<PlatoonStatsItem>) in.readParcelable(PlatoonTopStatsItem.class
                .getClassLoader());

    }

    // Getters
    public final String getName() {
        return name;
    }

    public final long getId() {
        return id;
    }

    public final List<PlatoonTopStatsItem> getTopPlayers() {
        return topPlayers;
    }

    public final List<PlatoonStatsItem> getGlobalTop() {
        return globalTop;
    }

    public final List<PlatoonStatsItem> getScores() {
        return scores;
    }

    public final List<PlatoonStatsItem> getSpm() {
        return spm;
    }

    public final List<PlatoonStatsItem> getTime() {
        return time;
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

}
