package com.ninetwozero.battlelog.datatype;

import android.os.Parcel;
import android.os.Parcelable;

public class GeneralSearchResult implements Parcelable {

	// Attributes
	private PlatoonData platoon;
	private ProfileData profile;

	// Constructs
	public GeneralSearchResult(PlatoonData p) {

		platoon = p;

	}

	public GeneralSearchResult(ProfileData p) {

		profile = p;

	}

	public GeneralSearchResult(Parcel in) {

		platoon = in.readParcelable(PlatoonData.class.getClassLoader());
		profile = in.readParcelable(ProfileData.class.getClassLoader());

	}

	// Getters
	public String getName() {

		return (profile == null) ? platoon.getName() : profile.getUsername();

	}

	public boolean hasPlatoonData() {
		return platoon != null;
	}

	public PlatoonData getPlatoonData() {
		return platoon;
	}

	public boolean hasProfileData() {
		return profile != null;
	}

	public ProfileData getProfileData() {
		return profile;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeParcelable(platoon, flags);
		dest.writeParcelable(profile, flags);

	}

	public static final Parcelable.Creator<GeneralSearchResult> CREATOR = new Parcelable.Creator<GeneralSearchResult>() {

		public GeneralSearchResult createFromParcel(Parcel in) {
			return new GeneralSearchResult(in);
		}

		public GeneralSearchResult[] newArray(int size) {
			return new GeneralSearchResult[size];
		}

	};

}
