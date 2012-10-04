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

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.DrawableResourceList;

public class UnlockData implements Parcelable {

    // Attributes
    private int mKitId;
    private double mUnlockPercentage;
    private long mScoreNeeded;
    private long mScoreCurrent;
    private String mParentIdentifier;
    private String mUnlockIdentifier;
    private String mObjective;
    private String mType;

    // Constants
    public static final String CATEGORY_WEAPON = "weaponUnlock";
    public static final String CATEGORY_ATTACHMENT = "weaponAddonUnlock";
    public static final String CATEGORY_KIT = "kitItemUnlock";
    public static final String CATEGORY_VEHICLE = "vehicleAddonUnlock";
    public static final String CATEGORY_SKILL = "soldierSpecializationUnlock";

    public UnlockData(Parcel in) {

        mKitId = in.readInt();
        mUnlockPercentage = in.readDouble();
        mScoreNeeded = in.readLong();
        mScoreCurrent = in.readLong();
        mParentIdentifier = in.readString();
        mUnlockIdentifier = in.readString();
        mObjective = in.readString();
        mType = in.readString();

    }

    public UnlockData(int k, double u, long scn, long scc, String pi,
                      String ui, String o, String t) {

        mKitId = k;
        mUnlockPercentage = u;
        mScoreNeeded = scn;
        mScoreCurrent = scc;
        mParentIdentifier = pi;
        mUnlockIdentifier = ui;
        mObjective = o;
        mType = t;

    }

    // Getters
    public int getKitId() {
        return mKitId;
    }

    public String getKitTitle(Context c) {
        return DataBank.getKitTitle(c, mKitId);
    }

    public double getUnlockPercentage() {
        return Math.round(mUnlockPercentage * 100) / 100;
    }

    public long getScoreNeeded() {
        return mScoreNeeded;
    }

    public long getScoreCurrent() {
        return mScoreCurrent;
    }

    public String getParent(Context c) {

        if (mType.equals(CATEGORY_ATTACHMENT)) {

            return DataBank.getWeaponTitle(c, mParentIdentifier);

        } else if (mType.equals(CATEGORY_VEHICLE)) {

            return DataBank.getVehicleTitle(mParentIdentifier);

        } else {

            return "";

        }

    }

    public String getTitle(Context c) {

        if (mType.equals(CATEGORY_WEAPON)) {

            return DataBank.getWeaponTitle(c, mUnlockIdentifier);

        } else if (mType.equals(CATEGORY_ATTACHMENT)) {

            return DataBank.getAttachmentTitle(mUnlockIdentifier);

        } else if (mType.equals(CATEGORY_VEHICLE)) {

            return DataBank.getVehicleUpgradeTitle(mUnlockIdentifier);

        } else if (mType.equals(CATEGORY_KIT)) {

            return DataBank.getKitTitle(mUnlockIdentifier);

        } else if (mType.equals(CATEGORY_SKILL)) {

            return DataBank.getSkillTitle(mUnlockIdentifier);

        } else {

            return mUnlockIdentifier;

        }

    }

    public int getImageResource() {

        if (mType.equals(CATEGORY_WEAPON)) {

            return DrawableResourceList.getWeapon(mUnlockIdentifier);

        } else if (mType.equals(CATEGORY_ATTACHMENT)) {

            return DrawableResourceList.getAttachment(mUnlockIdentifier);

        } else if (mType.equals(CATEGORY_VEHICLE)) {

            return DrawableResourceList.getVehicleUpgrade(mUnlockIdentifier);

        } else if (mType.equals(CATEGORY_KIT)) {

            return DrawableResourceList.getKit(mUnlockIdentifier);

        } else if (mType.equals(CATEGORY_SKILL)) {

            return DrawableResourceList.getSkill(mUnlockIdentifier);

        } else {

            return 0;

        }

    }

    public String getName(Context c) {

        // Check how it went
        if (mType.equals(CATEGORY_WEAPON)) {

            return getTitle(c);

        } else if (mType.equals(CATEGORY_ATTACHMENT)) {

            return getParent(c) + " " + getTitle(c);

        } else if (mType.equals(CATEGORY_VEHICLE)) {

            // return getParent() + " " + getTitle();
            return getTitle(c);

        } else if (mType.equals(CATEGORY_KIT)) {

            return getTitle(c);

        } else if (mType.equals(CATEGORY_SKILL)) {

            return getTitle(c);

        } else {

            return getTitle(c);

        }

    }

    public String getObjective(Context c) {

        if (mObjective.startsWith("sc_")) {

            return DataBank.getUnlockGoal(mObjective).replace(

                    "{scoreCurr}/{scoreNeeded}",
                    mScoreCurrent + "/" + mScoreNeeded

            );

        } else if ("rank".equals(mObjective)) {

            return DataBank.getUnlockGoal(mObjective).replace(

                    "{rank}", String.valueOf(getScoreNeeded())

            ).replace(

                    "{rankCurr}", String.valueOf(getScoreCurrent())

            );

        } else if (mObjective.startsWith("c_")) {

            return DataBank.getUnlockGoal("c_").replace(

                    "{scoreCurr}/{scoreNeeded} {name}",
                    mScoreCurrent + "/" + mScoreNeeded + " "
                            + getParent(c)

            );

        } else if (mObjective.startsWith("xpm")) {

            String digits = mObjective.subSequence(4, 6).toString();

            if (digits.charAt(0) == 0) {
                digits = digits.substring(1);
            }

            return DataBank.getUnlockGoal("xpm")
                    .replace(

                            "{name}",
                            DataBank.getAssignmentTitle("ID_XP1_ASSIGNMENT_"
                                    + digits)[0]

                    );

        }

        return mObjective;

    }

    public String getType() {
        return mType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeInt(mKitId);
        out.writeDouble(mUnlockPercentage);
        out.writeLong(mScoreNeeded);
        out.writeLong(mScoreCurrent);
        out.writeString(mParentIdentifier);
        out.writeString(mUnlockIdentifier);
        out.writeString(mObjective);
        out.writeString(mType);

    }

    public static final Parcelable.Creator<UnlockData> CREATOR = new Parcelable.Creator<UnlockData>() {

        public UnlockData createFromParcel(Parcel in) {
            return new UnlockData(in);
        }

        public UnlockData[] newArray(int size) {
            return new UnlockData[size];
        }

    };
}
