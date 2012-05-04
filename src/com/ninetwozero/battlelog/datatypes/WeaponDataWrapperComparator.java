
package com.ninetwozero.battlelog.datatypes;

import java.util.Comparator;

public class WeaponDataWrapperComparator implements Comparator<WeaponDataWrapper> {

    public int compare(WeaponDataWrapper o1, WeaponDataWrapper o2) {

        // Grab the data
        WeaponStats p1 = ((WeaponStats) ((WeaponDataWrapper) o1).getStats());
        WeaponStats p2 = ((WeaponStats) ((WeaponDataWrapper) o2).getStats());

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
