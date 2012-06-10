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

package com.ninetwozero.battlelog.datatype;

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

            int rId, int uId, String i, String d, String s,
            List<AssignmentData.Objective> c,
            List<AssignmentData.Dependency> dp,
            List<AssignmentData.Unlock> u

    ) {

        resourceId = rId;
        unlockResourceId = uId;
        id = i;
        description = d;
        set = s;
        objectives = c;
        dependencies = dp;
        unlocks = u;

    }

    public AssignmentData(Parcel in) {

        resourceId = in.readInt();
        unlockResourceId = in.readInt();
        id = in.readString();
        description = in.readString();
        set = in.readString();

        objectives = in.createTypedArrayList(AssignmentData.Objective.CREATOR);
        dependencies = in.createTypedArrayList(AssignmentData.Dependency.CREATOR);
        unlocks = in.createTypedArrayList(AssignmentData.Unlock.CREATOR);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(resourceId);
        dest.writeInt(unlockResourceId);
        dest.writeString(id);
        dest.writeString(description);
        dest.writeString(set);
        dest.writeList(objectives);
        dest.writeList(dependencies);
        dest.writeList(unlocks);

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
    public static class Objective implements Parcelable {

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

        public Objective(Parcel in) {

            currentValue = in.readDouble();
            goalValue = in.readDouble();
            id = in.readString();
            weapon = in.readString();
            kit = in.readString();
            description = in.readString();
            unit = in.readString();

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeDouble(currentValue);
            dest.writeDouble(goalValue);
            dest.writeString(id);
            dest.writeString(weapon);
            dest.writeString(kit);
            dest.writeString(description);
            dest.writeString(unit);

        }

        public static final Parcelable.Creator<AssignmentData.Objective> CREATOR = new Parcelable.Creator<AssignmentData.Objective>() {

            public AssignmentData.Objective createFromParcel(Parcel in) {
                return new AssignmentData.Objective(in);
            }

            public AssignmentData.Objective[] newArray(int size) {
                return new AssignmentData.Objective[size];
            }

        };

    }

    public static class Dependency implements Parcelable {

        // Attributes
        private int count;
        private String id;

        // Construct
        public Dependency(int c, String i) {

            count = c;
            id = i;

        }

        public Dependency(Parcel in) {

            count = in.readInt();
            id = in.readString();

        }

        // Getters
        public int getCount() {
            return count;
        }

        public String getId() {
            return id;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeInt(count);
            dest.writeString(id);

        }

        public static final Parcelable.Creator<AssignmentData.Dependency> CREATOR = new Parcelable.Creator<AssignmentData.Dependency>() {

            public AssignmentData.Dependency createFromParcel(Parcel in) {
                return new AssignmentData.Dependency(in);
            }

            public AssignmentData.Dependency[] newArray(int size) {
                return new AssignmentData.Dependency[size];
            }

        };

    }

    public static class Unlock implements Parcelable {

        // Attributes
        private String id, type;
        private boolean visible;

        // Construct
        public Unlock(String i, String t, boolean v) {

            id = i;
            type = t;
            visible = v;

        }

        public Unlock(Parcel in) {

            id = in.readString();
            type = in.readString();
            visible = in.readInt() == 1;

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeString(id);
            dest.writeString(type);
            dest.writeInt(visible ? 1 : 0);

        }

        public static final Parcelable.Creator<AssignmentData.Unlock> CREATOR = new Parcelable.Creator<AssignmentData.Unlock>() {

            public AssignmentData.Unlock createFromParcel(Parcel in) {
                return new AssignmentData.Unlock(in);
            }

            public AssignmentData.Unlock[] newArray(int size) {
                return new AssignmentData.Unlock[size];
            }

        };

    }
}
