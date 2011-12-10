package com.ninetwozero.battlelog.datatypes;

import org.apache.http.cookie.Cookie;

import android.os.Parcel;
import android.os.Parcelable;


public class ShareableCookie implements Parcelable {

	private String name;
	private String value;
	private String domain;

	public ShareableCookie(Cookie cookie){

		this.name = cookie.getName();
		this.value = cookie.getValue();
		this.domain = cookie.getDomain();
	
	}
	
	public ShareableCookie(Parcel in) {

		this.name = in.readString();
		this.value = in.readString();
		this.domain = in.readString();
		
	}

	public String getName(){
		return name;
	}

	public String getValue(){
		return value;
	}
	public String getDomain(){
		return domain;
	}

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel( Parcel dest, int flags ) {

		dest.writeString( this.name );
		dest.writeString( this.value );
		dest.writeString( this.domain );
		
	}
	
	public static final Parcelable.Creator<ShareableCookie> CREATOR = new Parcelable.Creator<ShareableCookie>() {
		
		public ShareableCookie createFromParcel(Parcel in) { return new ShareableCookie(in); }
        public ShareableCookie[] newArray(int size) { return new ShareableCookie[size]; }
	
	};
	
}
