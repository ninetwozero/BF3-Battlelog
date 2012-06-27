
package com.ninetwozero.battlelog.datatype;

import java.util.Comparator;

public class AssignmentComparator implements Comparator<AssignmentData> {

    public int compare(AssignmentData a1, AssignmentData a2) {

        int n1 = Integer.parseInt(a1.getId().substring(a1.getId().lastIndexOf("_") + 1));
        int n2 = Integer.parseInt(a2.getId().substring(a2.getId().lastIndexOf("_") + 1));
        return n1 <= n2 ? (n1 == n2 ? 0 : -1) : 1;

    }

}
