
package com.ninetwozero.battlelog.datatype;

import java.util.ArrayList;
import java.util.List;

public class SessionKeeperPackage {

    // Attributes
    private ProfileData profileData;
    private List<PlatoonData> platoons;

    // Construct
    public SessionKeeperPackage(ProfileData pr, List<PlatoonData> pl) {

        // Set the data
        profileData = pr;
        platoons = pl;

    }

    // Getters
    public ProfileData getProfileData() {
        return profileData;

    }

    public ArrayList<PlatoonData> getPlatoons() {
        return (ArrayList<PlatoonData>) platoons;
    }

}
