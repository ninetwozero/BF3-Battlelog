package com.ninetwozero.bf3droid.datatype;

import com.ninetwozero.bf3droid.provider.table.UserProfileData;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {

    private final UserProfileData userProfileData;
    private final List<SimplePersona> personas;
    private final List<SimplePlatoon> platoons;

    public UserInfo(List<SimplePlatoon> platoons, UserProfileData userProfileData){
        this(new ArrayList<SimplePersona>(), platoons, userProfileData);
    }

    public UserInfo(List<SimplePersona> personas, List<SimplePlatoon> platoons, UserProfileData userProfileData){
        this.userProfileData = userProfileData;
        this.personas = personas;
        this.platoons = platoons;
    }

    public UserProfileData getUserProfileData() {
        return userProfileData;
    }

    public List<SimplePersona> getPersonas() {
        return personas;
    }

    public List<SimplePlatoon> getPlatoons() {
        return platoons;
    }
}
