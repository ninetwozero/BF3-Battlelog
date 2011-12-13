package com.ninetwozero.battlelog.adapters;

import android.os.Parcel;
import android.os.Parcelable;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.ProfileData;


public class PlatoonMemberData extends ProfileData implements Parcelable {

	//Attributes
	int membershipLevel;
	
	public PlatoonMemberData( String an, String pn, long p, long pf, long n, String im, int m ) {
		
		super( an, pn, p, pf, n, im );
		this.membershipLevel = m;
	
	}
	
	public PlatoonMemberData( String an, String pn, long p, long pf, long n, String im, boolean on, boolean pl, int m ) {
		
		super(an, pn, p, pf, n, im, on, pl);
		this.membershipLevel = m;
		
	}
		
	public PlatoonMemberData( Parcel in ) {

		super( in );
		membershipLevel = in.readInt();

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
		this.membershipLevel = in.readInt();
	
	}
	
	@Override
	public int describeContents() { return 0; }
	
	public static final Parcelable.Creator<PlatoonMemberData> CREATOR = new Parcelable.Creator<PlatoonMemberData>() {
	
		public PlatoonMemberData createFromParcel(Parcel in) { return new PlatoonMemberData(in); }
        public PlatoonMemberData[] newArray(int size) { return new PlatoonMemberData[size]; }
	
	};
	

}
