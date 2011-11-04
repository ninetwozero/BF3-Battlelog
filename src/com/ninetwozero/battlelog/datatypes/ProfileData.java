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


public class ProfileData implements Serializable {

	//Serializable
	private static final long serialVersionUID = 4037268866097818638L;

	//Attributes
	private String personaName;
	private long personaId, profileId, platformId;
	private boolean isPlaying, isOnline;
	
	//Construct
	public ProfileData(String pn, long p, long pf, long n) {
		
		this.personaName = pn;
		this.personaId = p;
		this.profileId = pf;
		this.platformId = n;

		this.isOnline = false;
		this.isPlaying = false;
		
	}
	
	public ProfileData(String pn, long p, long pf, long n, boolean io, boolean ip ) {
		
		this(pn, p, pf, n);
		this.isOnline = io;
		this.isPlaying = ip;
		
	}
	
	//Getters
	public String getPersonaName() { return this.personaName; }
	public long getPersonaId() { return this.personaId; }
	public long getProfileId() { return this.profileId; }
	public long getPlatformId() { return this.platformId; }
	
	//is ... ?
	public boolean isOnline() { return this.isOnline; }
	public boolean isPlaying() { return this.isPlaying; }
	
}
