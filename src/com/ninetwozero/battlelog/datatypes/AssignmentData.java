/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.battlelog.datatypes;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class AssignmentData implements Parcelable {

    // Attributes
    private int resourceId, unlockResourceId;
    private String id, description, set;
    private List<AssignmentData.Objective> objectives;
    private List<AssignmentData.Dependency> dependencies;
    private List<AssignmentData.Unlock> unlocks;

    // Constructs
    public AssignmentData(

            int rId, int uId, String id, String d, String s,
            List<AssignmentData.Objective> c,
            List<AssignmentData.Dependency> dp,
            List<AssignmentData.Unlock> u

    ) {

        resourceId = rId;
        unlockResourceId = uId;
        id = id;
        description = d;
        set = s;
        objectives = c;
        dependencies = dp;
        unlocks = u;

    }

    public AssignmentData(Parcel in) {
    }

    @Override
    /* TODO */
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static final Parcelable.Creator<AssignmentData> CREATOR = new Parcelable.Creator<AssignmentData>() {

        public AssignmentData createFromParcel(Parcel in) {
            return new AssignmentData(in);
        }

        public AssignmentData[] newArray(int size) {
            return new AssignmentData[size];
        }

    };

    // Getters
    public int getResourceId() {
        return resourceId;
    }

    public int getUnlockResourceId() {
        return unlockResourceId;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getSet() {
        return set;
    }

    public List<AssignmentData.Objective> getObjectives() {
        return objectives;
    }

    public List<AssignmentData.Dependency> getDependencies() {
        return dependencies;
    }

    public List<AssignmentData.Unlock> getUnlocks() {
        return unlocks;
    }

    public int getProgress() {

        // How many?
        final int numObjectives = objectives.size();
        double count = 0;

        // Iterate
        for (AssignmentData.Objective obj : objectives) {

            count += (obj.getCurrentValue() / obj.getGoalValue()); // 0 <= x <=
            // 1

        }
        return (int) Math.round((count / numObjectives) * 100);

    }

    public boolean isCompleted() {

        for (AssignmentData.Objective obj : objectives) {

            if (obj.getCurrentValue() < obj.getGoalValue()) {

                return false;

            }

        }

        return true;

    }

    // toString()
    @Override
    public String toString() {
        return "#:" + objectives.size() + ":" + dependencies.size() + ":"
                + unlocks.size();
    }

    // Subclass
    public static class Objective {

        // Attributes
        private double currentValue, goalValue;
        private String id, weapon, kit, description, unit;

        // Construct
        public Objective(double c, double g, String i, String w, String k,
                String d, String u) {

            currentValue = c;
            goalValue = g;
            id = i;
            weapon = w;
            kit = k;
            description = d;
            unit = u;

        }

        // Getters
        public double getCurrentValue() {
            return currentValue;
        }

        public double getGoalValue() {
            return goalValue;
        }

        public String getId() {
            return id;
        }

        public String getWeapon() {
            return weapon;
        }

        public String getKit() {
            return kit;
        }

        public String getDescription() {
            return description;
        }

        public String getUnit() {
            return unit;
        }

    }

    public static class Dependency {

        // Attributes
        private int count;
        private String id;

        // Construct
        public Dependency(int c, String i) {

            count = c;
            id = i;

        }

        // Getters
        public int getCount() {
            return count;
        }

        public String getId() {
            return id;
        }

    }

    public static class Unlock {

        // Attributes
        private String id, type;
        private boolean visible;

        // Construct
        public Unlock(String i, String t, boolean v) {

            id = i;
            type = t;
            visible = v;

        }

        // Getters
        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public boolean isVisible() {
            return visible;
        }

    }
}
