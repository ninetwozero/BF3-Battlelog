
package com.ninetwozero.battlelog.datatypes;

import com.ninetwozero.battlelog.R;
import java.util.Comparator;

public class ProfileComparator implements Comparator<ProfileData> {

    public int compare(ProfileData o1, ProfileData o2) {

        ProfileData p1 = (ProfileData) o1;
        ProfileData p2 = (ProfileData) o2;
        return p1.getUsername().compareToIgnoreCase(p2.getUsername());

    }

}
