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

import android.content.SharedPreferences;
import android.util.Log;

import com.ninetwozero.battlelog.datatypes.ProfileData;

public class SessionKeeper {

    // Attributes
    private static ProfileData profileData;

    // Construct
    private SessionKeeper() {
    }

    // Getters
    public static ProfileData getProfileData() {
        return SessionKeeper.profileData;
    }

    // Setters
    public static void setProfileData(ProfileData p) {
        SessionKeeper.profileData = p;
    }

    // Generate session data
    public static ProfileData generateProfileDataFromSharedPreferences(
            SharedPreferences sp) {

        // Get the different strings
        String cookie = sp.getString(Constants.SP_BL_COOKIE_VALUE, "");
        String personaIdString = sp.getString(Constants.SP_BL_PERSONA_ID, "");
        String personaNameString = sp.getString(Constants.SP_BL_PERSONA, "");
        String platformIdString = sp.getString(Constants.SP_BL_PLATFORM_ID, "");

        // Let's split them up...
        String[] personaIdArray = personaIdString.split(":");
        String[] personaNameArray = personaNameString.split(":");
        String[] platformIdArray = platformIdString.split(":");

        // How many do we have?
        int max = ( personaIdString.equals( "" ) ) ? 0 : personaIdArray.length;
        
        // We need to init the resulting arrays
        long[] personaId = new long[max];
        long[] platformId = new long[max];
        
        // ...and populate them
        for (int i = 0; i < max; i++) {

            personaId[i] = Long.parseLong(personaIdArray[i]);
            platformId[i] = Long.parseLong(platformIdArray[i]);

        }

        //If we even *might* have a session
        if( !cookie.equals("") ) {
            
            
            return new ProfileData(

                sp.getString(Constants.SP_BL_USERNAME, ""), personaNameArray,
                personaId, sp.getLong(Constants.SP_BL_PROFILE_ID, 0),
                platformId, sp.getString(Constants.SP_BL_GRAVATAR, "")

            );
            
        } else {
            
            return null;
            
        }

    }

}
