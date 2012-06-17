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

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;

import com.ninetwozero.battlelog.datatype.PersonaData;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.ProfileData;

public final class SessionKeeper {

    // Attributes
    private static ProfileData profileData;
    private static List<PlatoonData> platoonData;

    // Construct
    private SessionKeeper() {
    }

    // Getters
    public static ProfileData getProfileData() {
        return SessionKeeper.profileData;
    }

    public static List<PlatoonData> getPlatoonData() {
        return SessionKeeper.platoonData;
    }

    // Setters
    public static void setProfileData(ProfileData p) {
        SessionKeeper.profileData = p;
    }

    public static void setPlatoonData(List<PlatoonData> p) {

        SessionKeeper.platoonData = p;

    }

    // Generate session data
    public static ProfileData generateProfileDataFromSharedPreferences(
            SharedPreferences sp) {

        // Get the different strings
        String cookie = sp.getString(Constants.SP_BL_COOKIE_VALUE, "");
        String personaIdString = sp.getString(Constants.SP_BL_PERSONA_ID, "");
        String personaNameString = sp.getString(Constants.SP_BL_PERSONA_NAME, "");
        String platformIdString = sp.getString(Constants.SP_BL_PLATFORM_ID, "");
        String personaLogoString = sp.getString(Constants.SP_BL_PERSONA_LOGO, "");

        // Let's split them up...
        String[] personaIdArray = personaIdString.split(":");
        String[] personaNameArray = personaNameString.split(":");
        String[] platformIdArray = platformIdString.split(":");
        String[] personaLogoArray = personaLogoString.split(":");

        // How many do we have?
        int max = (personaIdString.equals("")) ? 0 : personaIdArray.length;

        // We need to init the resulting arrays
        PersonaData[] persona = new PersonaData[max];

        // ...and populate them
        for (int i = 0; i < max; i++) {

            persona[i] = new PersonaData(Long.parseLong(personaIdArray[i]), personaNameArray[i],
                    Integer.parseInt(platformIdArray[i]), personaLogoArray[i]);

        }

        // If we even *might* have a session
        if ("".equals(cookie)) {

            return null;
            
        } else {

            return new ProfileData.Builder(
                    sp.getLong(Constants.SP_BL_PROFILE_ID, 0),
                    sp.getString(Constants.SP_BL_PROFILE_NAME, "")

            ).persona(persona).gravatarHash(
                    sp.getString(Constants.SP_BL_PROFILE_GRAVATAR, "")
                    ).build();

        }

    }

    public static List<PlatoonData> generatePlatoonDataFromSharedPreferences(
            SharedPreferences sp) {

        // Get the different strings
        String platoonIdString = sp.getString(Constants.SP_BL_PLATOON_ID, "");
        String platoonNameString = sp.getString(Constants.SP_BL_PLATOON, "");
        String platoonTagString = sp.getString(Constants.SP_BL_PLATOON_TAG, "");
        String platformIdString = sp.getString(Constants.SP_BL_PLATOON_PLATFORM_ID, "");
        String platoonImageString = sp.getString(Constants.SP_BL_PLATOON_IMAGE, "");

        // Let's split them up...
        String[] platoonIdArray = platoonIdString.split(":");
        String[] platoonNameArray = platoonNameString.split(":");
        String[] platoonTagArray = platoonTagString.split(":");
        String[] platformIdArray = platformIdString.split(":");
        String[] platoonImageArray = platoonImageString.split(":");

        // How many do we have?
        int max = (platoonIdString.equals("")) ? 0 : platoonIdArray.length;

        // We need to init the resulting arrays
        List<PlatoonData> platoon = new ArrayList<PlatoonData>();

        // ...and populate them
        for (int i = 0; i < max; i++) {

            platoon.add(

                    new PlatoonData(

                            Long.parseLong(platoonIdArray[i]),
                            0,
                            0,
                            Integer.parseInt(platformIdArray[i]),
                            platoonNameArray[i],
                            platoonTagArray[i],
                            platoonImageArray[i],
                            true
                    )

                    );

        }

        return (ArrayList<PlatoonData>) platoon;

    }

}
