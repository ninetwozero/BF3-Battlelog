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

import android.content.Context;
import android.util.Log;

import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.DrawableResourceList;

public class UnlockData {

    // Attributes
    private int kitId;
    private double unlockPercentage;
    private long scoreNeeded, scoreCurrent;
    private String parentIdentifier, unlockIdentifier, objective, type;

    //Constants
    public final static String CATEGORY_WEAPON = "weaponUnlock";
    public final static String CATEGORY_ATTACHMENT = "weaponAddonUnlock";
    public final static String CATEGORY_KIT = "kitItemUnlock";
    public final static String CATEGORY_VEHICLE = "vehicleAddonUnlock";
    public final static String CATEGORY_SKILL = "soldierSpecializationUnlock";
    
    public UnlockData(int k, double u, long scn, long scc, String pi,
            String ui, String o, String t) {

        kitId = k;
        unlockPercentage = u;
        scoreNeeded = scn;
        scoreCurrent = scc;
        parentIdentifier = pi;
        unlockIdentifier = ui;
        objective = o;
        type = t;

    }

    // Getters
    public int getKitId() {
        return kitId;
    }

    public String getKitTitle(Context c) {
        return DataBank.getKitTitle(c, kitId);
    }

    public double getUnlockPercentage() {
        return Math.round(unlockPercentage * 100) / 100;
    }

    public long getScoreNeeded() {
        return scoreNeeded;
    }

    public long getScoreCurrent() {
        return scoreCurrent;
    }

    public String getParent() {

        if (type.equals(CATEGORY_ATTACHMENT) ) {

            return DataBank.getWeaponTitle(parentIdentifier);

        } else if ( type.equals(CATEGORY_VEHICLE) ) {

            return DataBank.getVehicleTitle(parentIdentifier);

        } else
            return "";
    }

    public String getTitle() {

        if (type.equals(CATEGORY_WEAPON)) {

            return DataBank.getWeaponTitle(unlockIdentifier);

        } else if (type.equals(CATEGORY_ATTACHMENT)) {

            return DataBank.getAttachmentTitle(unlockIdentifier);

        } else if (type.equals(CATEGORY_VEHICLE)) {

            return DataBank.getVehicleAddon(unlockIdentifier);

        } else if (type.equals(CATEGORY_KIT)) {

            return DataBank.getKitUnlockTitle(unlockIdentifier);

        } else if (type.equals(CATEGORY_SKILL)) {

            return DataBank.getSkillTitle(unlockIdentifier);

        } else {

            return unlockIdentifier;

        }

    }
    
    public int getImageResource() {
        
        if (type.equals(CATEGORY_WEAPON)) {

            return DrawableResourceList.getWeapon(unlockIdentifier);

        } else if (type.equals(CATEGORY_ATTACHMENT)) {

            return DrawableResourceList.getAttachment(unlockIdentifier);

        } else if (type.equals(CATEGORY_VEHICLE)) {

            return DrawableResourceList.getVehicleUpgrade(unlockIdentifier);

        } else if (type.equals(CATEGORY_KIT)) {

            return DrawableResourceList.getKit(unlockIdentifier);

        } else if (type.equals(CATEGORY_SKILL)) {

            return DrawableResourceList.getSkill(unlockIdentifier);

        } else {

            return 0;

        }
        
    }

    public String getName() {

        // Check how it went
        if (type.equals(CATEGORY_WEAPON)) {

            return getTitle();

        } else if (type.equals(CATEGORY_ATTACHMENT)) {

            return getParent() + " " + getTitle();

        } else if (type.equals(CATEGORY_VEHICLE)) {

            // return getParent() + " " + getTitle();
            return getTitle();

        } else if (type.equals(CATEGORY_KIT)) {

            return getTitle();

        } else if (type.equals(CATEGORY_SKILL)) {

            return getTitle();

        } else {

            return getTitle();

        }

    }

    public String getObjective() {

        if (objective.startsWith("sc_")) {

            return DataBank.getUnlockGoal(objective).replace(

                    "{scoreCurr}/{scoreNeeded}",
                    scoreCurrent + "/" + scoreNeeded

                    );

        } else if ("rank".equals(objective)) {

            return DataBank.getUnlockGoal(objective).replace(

                    "{rank}", getScoreNeeded() + ""

                    ).replace(

                            "{rankCurr}", getScoreCurrent() + ""

                    );

        } else if (objective.startsWith("c_")) {

            return DataBank.getUnlockGoal("c_").replace(

                    "{scoreCurr}/{scoreNeeded} {name}",
                    scoreCurrent + "/" + scoreNeeded + " "
                            + getParent()

                    );

        } else if (objective.startsWith("xpm")) {

            String digits = objective.subSequence(4, 6).toString();

            if (digits.startsWith("0")) {
                digits = digits.substring(1);
            }

            return DataBank.getUnlockGoal("xpm")
                    .replace(

                            "{name}",
                            DataBank.getAssignmentTitle("ID_XP1_ASSIGNMENT_"
                                    + digits)[0]

                    );

        }

        return objective;

    }

    public String getType() {
        return type;
    }

    public String getTypeTitle() {

        if (type.equals(CATEGORY_WEAPON))
            return "Weapon";
        else if (type.equals(CATEGORY_ATTACHMENT))
            return "Attachment";
        else if (type.equals(CATEGORY_VEHICLE))
            return "Upgrade";
        else if (type.equals(CATEGORY_SKILL))
            return "Skill";
        else if (type.equals(CATEGORY_KIT))
            return "Kit";
        else
            return "N/A";

    }
}
