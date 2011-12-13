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

public class VehicleType implements Parcelable {

	//Attributes
	private String identifier, name, label;
	
	//Construct
	public VehicleType(String i, String n, String l) {
		
		this.identifier = i;
		this.name = n;
		this.label = l;
		
	}
	
	public VehicleType(Parcel in) {
		
		this.identifier = in.readString();
		this.name = in.readString();
		this.label = in.readString();
		
	}
	
	//Getters
	public String getIdentifier() { return this.identifier; }
	public String getName() { return this.name; }
	public String getLabel() { return this.label; }


	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel( Parcel out, int arg1 ) {

		out.writeString(this.identifier);
		out.writeString(this.name);
		out.writeString(this.label);
		
	}
	
}