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

import java.io.Serializable;


public class PlayerData implements Serializable {

	//Serializable
	private static final long serialVersionUID = -4227286910413194182L;

	//Base-section
	private String personaName, rankTitle;
	private long rankId, personaId, playerId, platformId, timePlayed;
	
	//EXP-section
	private long pointsThisLvl, pointsNextLvl;
	
	//STATS-section
	private int numKills, numAssists, numHeals, numRevives, numRepairs, numResupplies, numDeaths, numWins, numLosses;
	private double kdRatio, accuracy, longestHS, longestKS, scorePerMinute;
	
	//SCORE-section
	private long scoreAssault, scoreEngineer, scoreSupport, scoreRecon, scoreVehicle, scoreCombat, scoreAwards, scoreUnlocks, scoreTotal;
	
	//Construct
	public PlayerData(
		String pName, String rTitle, long rId, long prsId, long plyrId, long pltfId, long tPlayed,
		long ptsThisLvl, long ptsNxtLvl, 
		int nKills, int nAssists, int nHeals, int nRevives, int nRepairs, int nResup, int nDeaths, int nWins, int nLosses,
		double kdRatio, double nAccuracy, double lHS, double lKS, double spm,
		long scrAssault, long scrEngineer, long scrSupport, long scrRecon, long scrVehicle, long scrCombat, 
		long scrAwards, long scrUnlocks, long scrTotal
	) {
		
		this.personaName = pName;
		this.rankId = rId;
		this.rankTitle = rTitle;
		this.personaId = prsId;
		this.playerId = plyrId;
		this.platformId = pltfId;
		this.timePlayed = tPlayed;
		this.pointsThisLvl = ptsThisLvl;
		this.pointsNextLvl = ptsNxtLvl;
		this.numKills = nKills;
		this.numAssists = nAssists;
		this.numHeals = nHeals;
		this.numRevives = nRevives;
		this.numRepairs = nRepairs;
		this.numResupplies = nResup;
		this.numDeaths = nDeaths;
		this.numWins = nWins;
		this.numLosses = nLosses;
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

	//Getters	
	public final String getPersonaName() { return personaName; }
	public final String getRankTitle() { return DataBank.getRankTitle(rankTitle); }
	public final long getRankId() { return rankId; }
	public final long getPersonaId() { return personaId; }	
	public final long getPlayerId() { return playerId; }	
	public final long getPlatformId() { return platformId; }	
	
	public final long getTimePlayed() { return timePlayed; }	
	public final String getTimePlayedString() { return timeToLiteral( timePlayed ); }  
	
	public final long getPointsThisLvl() { return pointsThisLvl; }
	public final long getPointsNextLvl() { return pointsNextLvl; }	
	public final long getPointsProgressLvl() { return scoreTotal - pointsThisLvl; }
	public final long getPointsNeededToLvlUp() { return pointsNextLvl - pointsThisLvl; }
	public final long getPointsLeft() { return getPointsNeededToLvlUp() - getPointsProgressLvl() ; }
	
	public final int getNumKills() { return numKills; }
	public final int getNumAssists() { return numAssists; }	
	public final int getNumHeals() { return numHeals; }	
	public final int getNumRevives() { return numRevives; }	
	public final int getNumRepairs() { return numRepairs; }	
	public final int getNumResupplies() { return numResupplies; }	
	public final int getNumDeaths() { return numDeaths; }	
	public final double getKDRatio() { return Math.floor( kdRatio * 1000 ) / 1000; }
	
	public final int getNumWins() { return numWins; }	
	public final int getNumLosses() { return numLosses; }	
	public final double getWLRatio() { return Math.floor( ( ((double)numWins) / numLosses) * 100 ) / 100; }	
	
	public final double getAccuracy() { return Math.floor( accuracy * 10 ) / 10; }
	
	public final double getLongestHS() { return longestHS; }	
	public final double getLongestKS() { return longestKS; }	
	public final double getScorePerMinute() { return scorePerMinute; }	
	public final long getScoreAssault() { return scoreAssault; }	
	public final long getScoreEngineer() { return scoreEngineer; }
	public final long getScoreSupport() { return scoreSupport; }	
	public final long getScoreRecon() { return scoreRecon; }	
	public final long getScoreVehicle() { return scoreVehicle; }
	public final long getScoreCombat() { return scoreCombat; 	}
	public final long getScoreAwards() { return scoreAwards; }
	public final long getScoreUnlocks() { return scoreUnlocks; }
	public final long getScoreTotal() { return scoreTotal; }
	
	//Misc
	public String timeToLiteral(long s) {
	    
    	//Let's see what we can do
    	if( ( s / 60) < 1 ) return s + "S" ;
    	else if( (s / 3600 ) < 1 ) return (s/60) + "M " + (s % 60) + "S" ;
    	else return (s/3600) + "H " + ((s % 3600)/60) + "M";		
    	
    }
}
