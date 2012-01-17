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

package com.ninetwozero.battlelog.misc;

import com.ninetwozero.battlelog.datatypes.ProfileData;


public class SessionKeeper {

	//Attributes
	private static ProfileData profileData;
	
	//Construct
	private SessionKeeper() {}
	
	//Getters
	public static ProfileData getProfileData() { return SessionKeeper.profileData; }
	
	//Setters
	public static void setProfileData( ProfileData p ) { SessionKeeper.profileData = p; }
	
	
}
