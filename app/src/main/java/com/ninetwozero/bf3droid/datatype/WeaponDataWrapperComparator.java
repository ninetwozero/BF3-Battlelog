package com.ninetwozero.bf3droid.datatype;

import java.util.Comparator;

public class WeaponDataWrapperComparator implements
        Comparator<WeaponDataWrapper> {

    public int compare(WeaponDataWrapper o1, WeaponDataWrapper o2) {

        // Grab the data
        WeaponStats p1 = ((WeaponStats) o1.getStats());
        WeaponStats p2 = ((WeaponStats) o2.getStats());

        // Return!
        if (p1.getKills() < p2.getKills()) {

            return 1;

        } else if (p1.getKills() > p2.getKills()) {

            return -1;

        } else {

            return 0;

        }

    }

}
