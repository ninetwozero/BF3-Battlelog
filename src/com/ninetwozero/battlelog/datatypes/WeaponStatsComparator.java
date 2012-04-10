
package com.ninetwozero.battlelog.datatypes;

import java.util.Comparator;

public class WeaponStatsComparator implements Comparator<WeaponStats> {

    public int compare(WeaponStats o1, WeaponStats o2) {

        // Grab the data
        WeaponStats p1 = (WeaponStats) o1;
        WeaponStats p2 = (WeaponStats) o2;

        // Return!
        if (p1.getServiceStars() < p2.getServiceStars()) {
            
            return 1;
        
        } else if (p1.getServiceStars() > p2.getServiceStars()) {
        
            return -1;

        } else {
            
            return ( p1.getServiceStarProgress() > p2.getServiceStarProgress() )? -1 : 0;
            
        }
    }

}
