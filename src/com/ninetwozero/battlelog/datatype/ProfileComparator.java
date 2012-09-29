package com.ninetwozero.battlelog.datatype;

import java.util.Comparator;

public class ProfileComparator implements Comparator<ProfileData> {

    public int compare(ProfileData p1, ProfileData p2) {

        return p1.getUsername().compareToIgnoreCase(p2.getUsername());

    }

}
