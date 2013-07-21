package com.ninetwozero.bf3droid.datatype;

import java.util.Comparator;

public class UnlockComparator implements Comparator<UnlockData> {

    public int compare(UnlockData o1, UnlockData o2) {

        // Return!
        if (o1.getUnlockPercentage() > o2.getUnlockPercentage()) {

            return -1;

        } else if (o1.getUnlockPercentage() == o2.getUnlockPercentage()) {

            return 0;

        } else {

            return 1;

        }

    }

}
