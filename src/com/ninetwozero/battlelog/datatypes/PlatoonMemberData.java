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

import android.os.Parcel;
import android.os.Parcelable;

import com.ninetwozero.battlelog.R;


public class PlatoonMemberData extends ProfileData implements Parcelable {

	//Attributes
	private int membershipLevel;

	//Constructs
	public PlatoonMemberData( String an, String pn, long p, long pf, long n, String im, int m ) {
		
		this( an, new String[] { pn }, new long[] { p }, pf, new long[] { n }, im, m );
		
	}
	
	public PlatoonMemberData( String an, String[] pn, long[] p, long pf, long[] n, String im, int m ) {
		
		super( an, pn, p, pf, n, im );
		this.membershipLevel = m;
	
	}
	
	public PlatoonMemberData( String an, String[] pn, long[] p, long pf, long[] n, String im, boolean on, boolean pl, int m ) {
		
		super(an, pn, p, pf, n, im, on, pl);
		this.membershipLevel = m;
		
	}
		
	public PlatoonMemberData( Parcel in ) {

		super( in );
		membershipLevel = in.readInt();

	}

	//Getters
	public int getMembershipLevel() { return this.membershipLevel; }
	public boolean isAdmin() { return (this.membershipLevel == 128); }
	
	public void readFromParcel(Parcel in) {

		//Arrays
		this.personaName = in.createStringArray();
		this.personaId = in.createLongArray();
		this.platformId = in.createLongArray();
		
		//Let's retrieve them, same order as above
		this.username = in.readString();
		this.profileId = in.readLong();
		this.gravatarHash = in.readString();
		this.isOnline = (in.readInt() == 1);
		this.isPlaying = (in.readInt() == 1);
		this.membershipLevel = in.readInt();
	
	}
	
	@Override
	public int describeContents() { return 0; }
	
	public static final Parcelable.Creator<PlatoonMemberData> CREATOR = new Parcelable.Creator<PlatoonMemberData>() {
	
		public PlatoonMemberData createFromParcel(Parcel in) { return new PlatoonMemberData(in); }
        public PlatoonMemberData[] newArray(int size) { return new PlatoonMemberData[size]; }
	
	};
	

}
