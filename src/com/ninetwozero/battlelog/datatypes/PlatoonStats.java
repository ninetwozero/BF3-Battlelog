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

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import com.ninetwozero.battlelog.R;


public class PlatoonStats implements Parcelable {

	//Base-section
	private String name;
	private long id;
	
	//General stats
	private ArrayList<PlatoonStatsItem> globalTop, scores, spm, time;
	private ArrayList<PlatoonTopStatsItem> topPlayers;
	
	//Construct
	public PlatoonStats(
			String sName, long lId, ArrayList<PlatoonStatsItem> gS, ArrayList<PlatoonTopStatsItem> tP, ArrayList<PlatoonStatsItem> kS, ArrayList<PlatoonStatsItem> kSPM, ArrayList<PlatoonStatsItem> kT
		) {
			
			//Basic attributes
			this.name = sName;
			this.id = lId;

			this.globalTop = gS;
			this.topPlayers = tP;
			this.scores = kS;
			this.spm = kSPM;
			this.time = kT;
			
	}
	public PlatoonStats(Parcel in) {	
		
			//Basic attributes
			this.name = in.readString();
			this.id = in.readLong();

			this.globalTop = in.readParcelable( PlatoonStatsItem.class.getClassLoader() );
			this.topPlayers = in.readParcelable( PlatoonStatsItem.class.getClassLoader() );
			this.scores = in.readParcelable( PlatoonStatsItem.class.getClassLoader() );
			this.spm = in.readParcelable( PlatoonStatsItem.class.getClassLoader() );
			this.time = in.readParcelable( PlatoonTopStatsItem.class.getClassLoader() );
			
		}

	//Getters	
	public final String getName() { return name; }
	public final long getId() { return id; }
	public final ArrayList<PlatoonTopStatsItem> getTopPlayers() { return this.topPlayers; }
	public final ArrayList<PlatoonStatsItem> getGlobalTop() { return this.globalTop; }
	public final ArrayList<PlatoonStatsItem> getScores() { return this.scores; }
	public final ArrayList<PlatoonStatsItem> getSpm() { return this.spm; }
	public final ArrayList<PlatoonStatsItem> getTime() { return this.time; }

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel( Parcel dest, int flags ) {}

	public static final Parcelable.Creator<PlatoonStats> CREATOR = new Parcelable.Creator<PlatoonStats>() {
		
		public PlatoonStats createFromParcel(Parcel in) { return new PlatoonStats(in); }
        public PlatoonStats[] newArray(int size) { return new PlatoonStats[size]; }
	
	};


}
