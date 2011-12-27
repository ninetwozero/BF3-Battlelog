package com.ninetwozero.battlelog.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.ninetwozero.battlelog.R;


public class PlatoonMemberData extends ProfileData implements Parcelable {

	//Attributes
	int membershipLevel;
	
	public PlatoonMemberData( String an, String[] pn, long[] p, long pf, long[] n, String im, int m ) {
		
		super( an, pn, p, pf, n, im );
		this.membershipLevel = m;
	
	}
	
	public PlatoonMemberData( String an, String pn, long p, long pf, long n, String im, int m ) {
		
		this( an, new String[] { pn }, new long[] { p }, pf, new long[] { n }, im, m );
		
	}
	
	public PlatoonMemberData( String an, String[] pn, long[] p, long pf, long[] n, String im, boolean on, boolean pl, int m ) {
		
		super(an, pn, p, pf, n, im, on, pl);
		this.membershipLevel = m;
		
	}
		
	public PlatoonMemberData( Parcel in ) {

		super( in );
		membershipLevel = in.readInt();

	}
	
	private void readFromParcel(Parcel in) {

		//Arrays
		this.personaName = in.createStringArray();
		this.personaId = in.createLongArray();
		this.platformId = in.createLongArray();
		
		//Let's retrieve them, same order as above
		this.accountName = in.readString();
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
