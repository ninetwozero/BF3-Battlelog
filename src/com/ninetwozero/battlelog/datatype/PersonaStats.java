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

import android.os.Parcel;
import android.os.Parcelable;

import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class PersonaStats implements Parcelable {

    // Base-section
    private String accountName, personaName, rankTitle;
    private long rankId, personaId, userId, platformId, timePlayed;

    // EXP-section
    private long pointsThisLvl, pointsNextLvl;

    // STATS-section
    private int numKills, numAssists, numVehicles, numVehicleAssists;
    private int numHeals, numRevives, numRepairs, numResupplies, numDeaths,
            numWins, numLosses;
    private double kdRatio, accuracy, longestHS, longestKS, skill,
            scorePerMinute;

    // SCORE-section
    private long scoreAssault, scoreEngineer, scoreSupport, scoreRecon,
            scoreVehicle, scoreCombat, scoreAwards, scoreUnlocks, scoreTotal;

    // Construct
    public PersonaStats(String uName, long uId, String pName, long pId, long plId) {

        accountName = uName;
        personaName = pName;
        rankTitle = "Unknown";
        rankId = 1;
        personaId = pId;
        userId = uId;
        platformId = plId;

    }

    public PersonaStats(String aName, String pName, String rTitle, long rId,
            long prsId, long plyrId, long pltfId, long tPlayed,
            long ptsThisLvl, long ptsNxtLvl, int nKills, int nAssists,
            int nVDestroyed, int nVDestroyedAssists, int nHeals, int nRevives,
            int nRepairs, int nResup, int nDeaths, int nWins, int nLosses,
            double kDRatio, double nAccuracy, double lHS, double lKS,
            double nSkill, double spm, long scrAssault, long scrEngineer,
            long scrSupport, long scrRecon, long scrVehicle, long scrCombat,
            long scrAwards, long scrUnlocks, long scrTotal) {

        accountName = aName;
        personaName = pName;
        rankTitle = rTitle;
        rankId = rId;
        personaId = prsId;
        userId = plyrId;
        platformId = pltfId;
        timePlayed = tPlayed;
        pointsThisLvl = ptsThisLvl;
        pointsNextLvl = ptsNxtLvl;
        numKills = nKills;
        numAssists = nAssists;
        numVehicles = nVDestroyed;
        numVehicleAssists = nVDestroyedAssists;
        numHeals = nHeals;
        numRevives = nRevives;
        numRepairs = nRepairs;
        numResupplies = nResup;
        numDeaths = nDeaths;
        numWins = nWins;
        numLosses = nLosses;
        skill = nSkill;
        kdRatio = kDRatio;
        accuracy = nAccuracy;
        longestHS = lHS;
        longestKS = lKS;
        scorePerMinute = spm;
        scoreAssault = scrAssault;
        scoreEngineer = scrEngineer;
        scoreSupport = scrSupport;
        scoreRecon = scrRecon;
        scoreVehicle = scrVehicle;
        scoreCombat = scrCombat;
        scoreAwards = scrAwards;
        scoreUnlocks = scrUnlocks;
        scoreTotal = scrTotal;

    }

    public PersonaStats(Parcel in) {

        accountName = in.readString();
        personaName = in.readString();
        rankId = in.readLong();
        rankTitle = in.readString();
        personaId = in.readLong();
        userId = in.readLong();
        platformId = in.readLong();
        timePlayed = in.readLong();
        pointsThisLvl = in.readLong();
        pointsNextLvl = in.readLong();
        numKills = in.readInt();
        numAssists = in.readInt();
        numVehicles = in.readInt();
        numVehicleAssists = in.readInt();
        numHeals = in.readInt();
        numRevives = in.readInt();
        numRepairs = in.readInt();
        numResupplies = in.readInt();
        numDeaths = in.readInt();
        numWins = in.readInt();
        numLosses = in.readInt();
        skill = in.readDouble();
        kdRatio = in.readDouble();
        accuracy = in.readDouble();
        longestHS = in.readDouble();
        longestKS = in.readDouble();
        scorePerMinute = in.readDouble();
        scoreAssault = in.readLong();
        scoreEngineer = in.readLong();
        scoreSupport = in.readLong();
        scoreRecon = in.readLong();
        scoreVehicle = in.readLong();
        scoreCombat = in.readLong();
        scoreAwards = in.readLong();
        scoreUnlocks = in.readLong();
        scoreTotal = in.readLong();

    }

    // Getters
    public final String getAccountName() {
        return accountName;
    }

    public final String getPersonaName() {
        return personaName;
    }

    public final String getRankTitle() {
        return DataBank.getRankTitle(rankTitle);
    }

    public final long getRankId() {
        return rankId;
    }

    public final long getPersonaId() {
        return personaId;
    }

    public final long getUserId() {
        return userId;
    }

    public final long getPlatformId() {
        return platformId;
    }

    public final long getTimePlayed() {
        return timePlayed;
    }

    public final String getTimePlayedString() {
        return PublicUtils.timeToLiteral(timePlayed);
    }

    public final long getPointsThisLvl() {
        return pointsThisLvl;
    }

    public final long getPointsNextLvl() {
        return pointsNextLvl;
    }

    public final long getPointsProgressLvl() {
        return scoreTotal - pointsThisLvl;
    }

    public final long getPointsNeededToLvlUp() {
        return pointsNextLvl - pointsThisLvl;
    }

    public final long getPointsLeft() {
        return getPointsNeededToLvlUp() - getPointsProgressLvl();
    }

    public final int getNumKills() {
        return numKills;
    }

    public final int getNumAssists() {
        return numAssists;
    }

    public final int getNumVehicles() {
        return numVehicles;
    }

    public final int getNumVehicleAssists() {
        return numVehicleAssists;
    }

    public final int getNumHeals() {
        return numHeals;
    }

    public final int getNumRevives() {
        return numRevives;
    }

    public final int getNumRepairs() {
        return numRepairs;
    }

    public final int getNumResupplies() {
        return numResupplies;
    }

    public final int getNumDeaths() {
        return numDeaths;
    }

    public final double getKDRatio() {
        return Math.floor(kdRatio * 1000) / 1000;
    }

    public final int getNumWins() {
        return numWins;
    }

    public final int getNumLosses() {
        return numLosses;
    }

    public final double getWLRatio() {
        return Math.floor((((double) numWins) / numLosses) * 100) / 100;
    }

    public final double getAccuracy() {
        return Math.floor(accuracy * 10) / 10;
    }

    public final double getLongestHS() {
        return longestHS;
    }

    public final double getLongestKS() {
        return longestKS;
    }

    public final double getSkill() {
        return skill;
    }

    public final double getScorePerMinute() {
        return scorePerMinute;
    }

    public final long getScoreAssault() {
        return scoreAssault;
    }

    public final long getScoreEngineer() {
        return scoreEngineer;
    }

    public final long getScoreSupport() {
        return scoreSupport;
    }

    public final long getScoreRecon() {
        return scoreRecon;
    }

    public final long getScoreVehicles() {
        return scoreVehicle;
    }

    public final long getScoreCombat() {
        return scoreCombat;
    }

    public final long getScoreAwards() {
        return scoreAwards;
    }

    public final long getScoreUnlocks() {
        return scoreUnlocks;
    }

    public final long getScoreTotal() {
        return scoreTotal;
    }

    public String resolvePlatformId() {
        switch ((int)platformId) {
            case 0:
            case 1:
                return "[PC]";
            case 2:
                return "[360]";
            case 4:
                return "[PS3]";
            default:
                return "[N/A]";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeString(accountName);
        out.writeString(personaName);
        out.writeLong(rankId);
        out.writeString(rankTitle);
        out.writeLong(personaId);
        out.writeLong(userId);
        out.writeLong(platformId);
        out.writeLong(timePlayed);
        out.writeLong(pointsThisLvl);
        out.writeLong(pointsNextLvl);
        out.writeInt(numKills);
        out.writeInt(numAssists);
        out.writeInt(numVehicles);
        out.writeInt(numVehicleAssists);
        out.writeInt(numHeals);
        out.writeInt(numRevives);
        out.writeInt(numRepairs);
        out.writeInt(numResupplies);
        out.writeInt(numDeaths);
        out.writeInt(numWins);
        out.writeInt(numLosses);
        out.writeDouble(skill);
        out.writeDouble(kdRatio);
        out.writeDouble(accuracy);
        out.writeDouble(longestHS);
        out.writeDouble(longestKS);
        out.writeDouble(scorePerMinute);
        out.writeLong(scoreAssault);
        out.writeLong(scoreEngineer);
        out.writeLong(scoreSupport);
        out.writeLong(scoreRecon);
        out.writeLong(scoreVehicle);
        out.writeLong(scoreCombat);
        out.writeLong(scoreAwards);
        out.writeLong(scoreUnlocks);
        out.writeLong(scoreTotal);

    }

    public static final Parcelable.Creator<PersonaStats> CREATOR = new Parcelable.Creator<PersonaStats>() {

        public PersonaStats createFromParcel(Parcel in) {
            return new PersonaStats(in);
        }

        public PersonaStats[] newArray(int size) {
            return new PersonaStats[size];
        }

    };

    public final String[] toStringArray() {

        return new String[] {

                accountName + "", personaName + "", rankTitle + "",
                personaId + "", userId + "", platformId + "",
                rankId + "", pointsThisLvl + "",
                pointsNextLvl + "", timePlayed + "",
                numKills + "", numAssists + "",
                numVehicles + "", numVehicleAssists + "",
                numHeals + "", numRevives + "", numRepairs + "",
                numResupplies + "", numDeaths + "",
                numWins + "", numLosses + "", kdRatio + "",
                accuracy + "", longestHS + "", longestKS + "",
                skill + "", scorePerMinute + "",
                scoreAssault + "", scoreEngineer + "",
                scoreSupport + "", scoreRecon + "",
                scoreVehicle + "", scoreCombat + "",
                scoreAwards + "", scoreUnlocks + "",
                scoreTotal + ""

        };

    }
}
