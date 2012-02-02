package com.ninetwozero.battlelog.datatypes;

import android.os.Parcel;
import android.os.Parcelable;
import com.ninetwozero.battlelog.R;


public class GeneralSearchResult implements Parcelable {
	
	//Attributes
	private PlatoonData platoon;
	private ProfileData profile;
	
	//Constructs
	public GeneralSearchResult( PlatoonData p ) {
		
		this.platoon = p;
		this.profile = null;
		
	}

	public GeneralSearchResult( ProfileData p ) {
		
		this.platoon = null;
		this.profile = p;
		
	}
	
	public GeneralSearchResult( Parcel in ) {

		this.platoon = in.readParcelable( PlatoonData.class.getClassLoader() );
		this.profile = in.readParcelable( ProfileData.class.getClassLoader() );

	}
	
	//Getters
	public boolean hasPlatoonData() { return this.platoon != null; }
	public PlatoonData getPlatoonData() { return this.platoon; }
	public boolean hasProfileData() { return this.profile != null; }
	public ProfileData getProfileData() { return this.profile; }

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel( Parcel dest, int flags ) {

		dest.writeParcelable( platoon, flags );
		dest.writeParcelable( profile, flags );
		
	}
	
	public static final Parcelable.Creator<GeneralSearchResult> CREATOR = new Parcelable.Creator<GeneralSearchResult>() {
		
		public GeneralSearchResult createFromParcel(Parcel in) { return new GeneralSearchResult(in); }
        public GeneralSearchResult [] newArray(int size) { return new GeneralSearchResult[size]; }
	
	};
	
}