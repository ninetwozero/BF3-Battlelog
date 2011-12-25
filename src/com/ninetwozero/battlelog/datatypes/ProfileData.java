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


public class ProfileData implements Parcelable {

	//Attributes
	protected String accountName, personaName, gravatarHash;
	protected long personaId, profileId, platformId;
	protected boolean isPlaying, isOnline;
	
	//Constructs
	public ProfileData(Parcel in) { readFromParcel(in); }
	public ProfileData(String an, String pn, long p, long pf, long n, String im) {
		
		this.accountName = an;
		this.personaName = pn;
		this.personaId = p;
		this.profileId = pf;
		this.platformId = n;
		this.gravatarHash = im;

		this.isOnline = false;
		this.isPlaying = false;
		
	}
	
	public ProfileData(String an, String pn,  long p, long pf, long n, String im, boolean io, boolean ip ) {
		
		this(an, pn, p, pf, n, im);
		this.isOnline = io;
		this.isPlaying = ip;
		
	}
	
	//Getters
	public String getAccountName() { return this.accountName; }
	public String getPersonaName() { return this.personaName; }
	public long getPersonaId() { return this.personaId; }
	public long getProfileId() { return this.profileId; }
	public long getPlatformId() { return this.platformId; }
	public String getGravatarHash() { return this.gravatarHash; }
	
	//is ... ?
	public boolean isOnline() { return this.isOnline; }
	public boolean isPlaying() { return this.isPlaying; }
	
	@Override
	public void writeToParcel( Parcel dest, int flags ) {
		
		//Everything else
		dest.writeString( this.accountName );
		dest.writeString( this.personaName );
		dest.writeLong( this.profileId );
		dest.writeLong( this.personaId );
		dest.writeLong( this.platformId );
		dest.writeString( this.gravatarHash );
		dest.writeInt( this.isOnline? 1 : 0 );
		dest.writeInt( this.isPlaying? 1 : 0 );
	}
	
	private void readFromParcel(Parcel in) {

		//Let's retrieve them, same order as above
		this.accountName = in.readString();
		this.personaName = in.readString();
		this.profileId = in.readLong();
		this.personaId = in.readLong();
		this.platformId = in.readLong();
		this.gravatarHash = in.readString();
		this.isOnline = (in.readInt() == 1);
		this.isPlaying = (in.readInt() == 1);
	
	}
	
	@Override
	public int describeContents() { return 0; }
	
	public static final Parcelable.Creator<ProfileData> CREATOR = new Parcelable.Creator<ProfileData>() {
	
		public ProfileData createFromParcel(Parcel in) { return new ProfileData(in); }
        public ProfileData[] newArray(int size) { return new ProfileData[size]; }
	
	};
	
	
	//toString
	@Override
	public String toString() { 
		
		return (
				
			getAccountName() + ":" + 
			getPersonaName() + ":" + 
			getPersonaId() + ":" + 
			getProfileId() + ":" + 
			getPlatformId() + ":" + 
			isOnline() + ":" + 
			isPlaying() 
		
		);
	}
}
