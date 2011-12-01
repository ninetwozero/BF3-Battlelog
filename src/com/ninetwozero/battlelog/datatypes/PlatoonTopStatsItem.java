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

import java.io.Serializable;

import com.ninetwozero.battlelog.misc.DataBank;


public class PlatoonTopStatsItem implements Serializable {

	//Serializable 
	private static final long serialVersionUID = 7460289426277720097L;
	
	//Base-section
	private String label;
	private double spm;
	private ProfileData profile;
	
	//Construct
	public PlatoonTopStatsItem( String l, double s, ProfileData p ) {
		
		this.label = l;
		this.spm = s;
		this.profile = p;
	
	}

	//Getters	
	public String getLabel() { return this.label; }
	public double getSPM() { return this.spm; }
	public ProfileData getProfile() { return this.profile; }
}
