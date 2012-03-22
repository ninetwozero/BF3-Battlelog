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
    public PersonaStats(String aName, String pName, String rTitle, long rId,
            long prsId, long plyrId, long pltfId, long tPlayed,
            long ptsThisLvl, long ptsNxtLvl, int nKills, int nAssists,
            int nVDestroyed, int nVDestroyedAssists, int nHeals, int nRevives,
            int nRepairs, int nResup, int nDeaths, int nWins, int nLosses,
            double kdRatio, double nAccuracy, double lHS, double lKS,
            double nSkill, double spm, long scrAssault, long scrEngineer,
            long scrSupport, long scrRecon, long scrVehicle, long scrCombat,
            long scrAwards, long scrUnlocks, long scrTotal) {

        this.accountName = aName;
        this.personaName = pName;
        this.rankTitle = rTitle;
        this.rankId = rId;
        this.personaId = prsId;
        this.userId = plyrId;
        this.platformId = pltfId;
        this.timePlayed = tPlayed;
        this.pointsThisLvl = ptsThisLvl;
        this.pointsNextLvl = ptsNxtLvl;
        this.numKills = nKills;
        this.numAssists = nAssists;
        this.numVehicles = nVDestroyed;
        this.numVehicleAssists = nVDestroyedAssists;
        this.numHeals = nHeals;
        this.numRevives = nRevives;
        this.numRepairs = nRepairs;
        this.numResupplies = nResup;
        this.numDeaths = nDeaths;
        this.numWins = nWins;
        this.numLosses = nLosses;
        this.skill = nSkill;
        this.kdRatio = kdRatio;
        this.accuracy = nAccuracy;
        this.longestHS = lHS;
        this.longestKS = lKS;
        this.scorePerMinute = spm;
        this.scoreAssault = scrAssault;
        this.scoreEngineer = scrEngineer;
        this.scoreSupport = scrSupport;
        this.scoreRecon = scrRecon;
        this.scoreVehicle = scrVehicle;
        this.scoreCombat = scrCombat;
        this.scoreAwards = scrAwards;
        this.scoreUnlocks = scrUnlocks;
        this.scoreTotal = scrTotal;

    }

    public PersonaStats(Parcel in) {

        this.accountName = in.readString();
        this.personaName = in.readString();
        this.rankId = in.readLong();
        this.rankTitle = in.readString();
        this.personaId = in.readLong();
        this.userId = in.readLong();
        this.platformId = in.readLong();
        this.timePlayed = in.readLong();
        this.pointsThisLvl = in.readLong();
        this.pointsNextLvl = in.readLong();
        this.numKills = in.readInt();
        this.numAssists = in.readInt();
        this.numVehicles = in.readInt();
        this.numVehicleAssists = in.readInt();
        this.numHeals = in.readInt();
        this.numRevives = in.readInt();
        this.numRepairs = in.readInt();
        this.numResupplies = in.readInt();
        this.numDeaths = in.readInt();
        this.numWins = in.readInt();
        this.numLosses = in.readInt();
        this.skill = in.readDouble();
        this.kdRatio = in.readDouble();
        this.accuracy = in.readDouble();
        this.longestHS = in.readDouble();
        this.longestKS = in.readDouble();
        this.scorePerMinute = in.readDouble();
        this.scoreAssault = in.readLong();
        this.scoreEngineer = in.readLong();
        this.scoreSupport = in.readLong();
        this.scoreRecon = in.readLong();
        this.scoreVehicle = in.readLong();
        this.scoreCombat = in.readLong();
        this.scoreAwards = in.readLong();
        this.scoreUnlocks = in.readLong();
        this.scoreTotal = in.readLong();

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
        return this.skill;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {

        out.writeString(this.accountName);
        out.writeString(this.personaName);
        out.writeLong(this.rankId);
        out.writeString(this.rankTitle);
        out.writeLong(this.personaId);
        out.writeLong(this.userId);
        out.writeLong(this.platformId);
        out.writeLong(this.timePlayed);
        out.writeLong(this.pointsThisLvl);
        out.writeLong(this.pointsNextLvl);
        out.writeInt(this.numKills);
        out.writeInt(this.numAssists);
        out.writeInt(this.numVehicles);
        out.writeInt(this.numVehicleAssists);
        out.writeInt(this.numHeals);
        out.writeInt(this.numRevives);
        out.writeInt(this.numRepairs);
        out.writeInt(this.numResupplies);
        out.writeInt(this.numDeaths);
        out.writeInt(this.numWins);
        out.writeInt(this.numLosses);
        out.writeDouble(this.skill);
        out.writeDouble(this.kdRatio);
        out.writeDouble(this.accuracy);
        out.writeDouble(this.longestHS);
        out.writeDouble(this.longestKS);
        out.writeDouble(this.scorePerMinute);
        out.writeLong(this.scoreAssault);
        out.writeLong(this.scoreEngineer);
        out.writeLong(this.scoreSupport);
        out.writeLong(this.scoreRecon);
        out.writeLong(this.scoreVehicle);
        out.writeLong(this.scoreCombat);
        out.writeLong(this.scoreAwards);
        out.writeLong(this.scoreUnlocks);
        out.writeLong(this.scoreTotal);

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

                this.accountName + "", this.personaName + "", this.rankTitle + "",
                this.personaId + "", this.userId + "", this.platformId + "",
                this.rankId + "", this.pointsThisLvl + "",
                this.pointsNextLvl + "", this.timePlayed + "",
                this.numKills + "", this.numAssists + "",
                this.numVehicles + "", this.numVehicleAssists + "",
                this.numHeals + "", this.numRevives + "", this.numRepairs + "",
                this.numResupplies + "", this.numDeaths + "",
                this.numWins + "", this.numLosses + "", this.kdRatio + "",
                this.accuracy + "", this.longestHS + "", this.longestKS + "",
                this.skill + "", this.scorePerMinute + "",
                this.scoreAssault + "", this.scoreEngineer + "",
                this.scoreSupport + "", this.scoreRecon + "",
                this.scoreVehicle + "", this.scoreCombat + "",
                this.scoreAwards + "", this.scoreUnlocks + "",
                this.scoreTotal + ""

        };

    }
}
