/*
	This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.datatype;

import java.util.ArrayList;
import java.util.List;
@Deprecated
public class AssignmentDataWrapper {

    // Attributes
    private final List<AssignmentData> mB2KAssignments;
    private final List<AssignmentData> mPremiumAssignments;
    private final List<AssignmentData> mCQAssignments;

    // Construct
    public AssignmentDataWrapper() {

        mB2KAssignments = new ArrayList<AssignmentData>();
        mPremiumAssignments = new ArrayList<AssignmentData>();
        mCQAssignments = new ArrayList<AssignmentData>();

    }

    public AssignmentDataWrapper(List<AssignmentData> b, List<AssignmentData> p,
                                 List<AssignmentData> c) {

        mB2KAssignments = b;
        mCQAssignments = c;
        mPremiumAssignments = p;

    }

    // Getters
    public List<AssignmentData> getB2KAssignments() {
        return mB2KAssignments;
    }

    public List<AssignmentData> getCQAssignments() {
        return mCQAssignments;
    }

    public List<AssignmentData> getPremiumAssignments() {
        return mPremiumAssignments;
    }

}
