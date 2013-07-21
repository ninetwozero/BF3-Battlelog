/*
    This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.datatype;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.ninetwozero.bf3droid.misc.Constants;

public class PersonaData implements Parcelable {
	
	@SerializedName("personaId")
    protected long id;
	
	@SerializedName("personaName")
    protected String name;
	
	@SerializedName("namespace")
    private String platform;
	
	protected int platformId;
	
	@SerializedName("picture")
    protected String logo;

    public PersonaData(Parcel in) {
        id = in.readLong();
        name = in.readString();
        platformId = in.readInt();
        logo = in.readString();
    }

    public PersonaData(String n) {
        this(0, n, 0, null);
    }

	public PersonaData(long id, String name, int platformId, String logo) {
		super();
		this.id = id;
		this.name = name;
		this.platformId = platformId;
		this.logo = logo;
	}
	
	public PersonaData(long id, String name, String platform, String logo) {
		super();
		this.id = id;
		this.name = name;
		this.platformId = resolveIdFromPlatformName(platform);
		this.logo = logo;
	}

	public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public int getPlatformId() {
        return platformId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(platformId);
        dest.writeString(logo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<PersonaData> CREATOR = new Parcelable.Creator<PersonaData>() {
        public PersonaData createFromParcel(Parcel in) {
            return new PersonaData(in);
        }
        public PersonaData[] newArray(int size) {
            return new PersonaData[size];
        }
    };

    public String resolvePlatformId() {
        switch (platformId) {
            case 0:
            case 1:
                return "[PC]";
            case 2:
                return "[360]";
            case 4:
                return "[PS3]";
            default:
                return "[N/A]";
        }
    }
	
	public int resolveIdFromPlatformName(String platform) {
		Log.d(Constants.DEBUG_TAG, "platform => " + platform);
		if(platform.equals("xbox")) {
			return 2;
		} else if(platform.equals("ps3")) {
			return 4;
		} else {
			return 0;
		}	
	}

	@Override
	public String toString() {
		return "PersonaData [id=" + id + ", name=" + name + ", platformId="
				+ platformId + ", logo=" + logo + "]";
	}
}
