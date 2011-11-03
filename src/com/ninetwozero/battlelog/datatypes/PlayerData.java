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
import java.util.HashMap;

public class PlayerData implements Serializable {

	//Serializable
	private static final long serialVersionUID = -4227286910413194182L;

	//Base-section
	String personaName, rankTitle;
	long rankId, personaId, playerId, platformId, timePlayed;
	
	//EXP-section
	long pointsThisLvl, pointsNextLvl;
	
	//STATS-section
	int numKills, numAssists, numHeals, numRevives, numDeaths, numWins, numLosses;
	double kdRatio, accuracy, longestHS, longestKS, scorePerMinute;
	
	//SCORE-section
	long scoreAssault, scoreEngineer, scoreSupport, scoreRecon, scoreVehicle, scoreCombat, scoreAwards, scoreUnlocks, scoreTotal;
	
	//Construct
	public PlayerData(
		String pName, String rTitle, long rId, long prsId, long plyrId, long pltfId, long tPlayed,
		long ptsThisLvl, long ptsNxtLvl, 
		int nKills, int nAssists, int nHeals, int nRevives, int nDeaths, int nWins, int nLosses,
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
	public final String getRankTitle() { return PlayerData.RANK_MAPPING.get( rankTitle ); }
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
	
	
	static HashMap<String, String> RANK_MAPPING;
    static {
  
    	RANK_MAPPING = new HashMap<String, String>();
    	RANK_MAPPING.put("ID_P_RANK00_NAME", "RECRUIT");
    	RANK_MAPPING.put("ID_P_RANK01_NAME", "PRIVATE FIRST CLASS");
    	RANK_MAPPING.put("ID_P_RANK02_NAME", "PRIVATE FIRST CLASS 1 STAR");
    	RANK_MAPPING.put("ID_P_RANK03_NAME", "PRIVATE FIRST CLASS 2 STAR");
    	RANK_MAPPING.put("ID_P_RANK04_NAME", "PRIVATE FIRST CLASS 3 STAR");
    	RANK_MAPPING.put("ID_P_RANK05_NAME", "LANCE CORPORAL");
    	RANK_MAPPING.put("ID_P_RANK06_NAME", "LANCE CORPORAL 1 STAR");
    	RANK_MAPPING.put("ID_P_RANK07_NAME", "LANCE CORPORAL 2 STAR");
    	RANK_MAPPING.put("ID_P_RANK08_NAME", "LANCE CORPORAL 3 STAR");
    	RANK_MAPPING.put("ID_P_RANK09_NAME", "CORPORAL");
    	RANK_MAPPING.put("ID_P_RANK10_NAME", "CORPORAL 1 STAR");
    	RANK_MAPPING.put("ID_P_RANK11_NAME", "CORPORAL 2 STAR");
    	RANK_MAPPING.put("ID_P_RANK12_NAME", "CORPORAL 3 STAR");
    	RANK_MAPPING.put("ID_P_RANK13_NAME", "SERGEANT");
    	RANK_MAPPING.put("ID_P_RANK14_NAME", "SERGEANT 1 STAR");
    	RANK_MAPPING.put("ID_P_RANK15_NAME", "SERGEANT 2 STAR");
    	RANK_MAPPING.put("ID_P_RANK16_NAME", "SERGEANT 3 STAR");
    	RANK_MAPPING.put("ID_P_RANK17_NAME", "STAFF SERGEANT");
    	RANK_MAPPING.put("ID_P_RANK18_NAME", "STAFF SERGEANT 1 STAR");
    	RANK_MAPPING.put("ID_P_RANK19_NAME", "STAFF SERGEANT 2 STAR");
    	RANK_MAPPING.put("ID_P_RANK20_NAME", "GUNNERY SERGEANT");
    	RANK_MAPPING.put("ID_P_RANK21_NAME", "GUNNERY SERGEANT 1 STAR");
    	RANK_MAPPING.put("ID_P_RANK22_NAME", "GUNNERY SERGEANT 2 STAR");
    	RANK_MAPPING.put("ID_P_RANK23_NAME", "MASTER SERGEANT");
    	RANK_MAPPING.put("ID_P_RANK24_NAME", "MASTER SERGEANT 1 STAR");
    	RANK_MAPPING.put("ID_P_RANK25_NAME", "MASTER SERGEANT 2 STAR");
    	RANK_MAPPING.put("ID_P_RANK26_NAME", "FIRST SERGEANT");
    	RANK_MAPPING.put("ID_P_RANK27_NAME", "FIRST SERGEANT 1 STAR");
    	RANK_MAPPING.put("ID_P_RANK28_NAME", "FIRST SERGEANT 2 STAR");
    	RANK_MAPPING.put("ID_P_RANK29_NAME", "MASTER GUNNERY SERGEANT");
    	RANK_MAPPING.put("ID_P_RANK30_NAME", "MASTER GUNNERY SERGEANT 1 STAR");
    	RANK_MAPPING.put("ID_P_RANK31_NAME", "MASTER GUNNERY SERGEANT 2 STAR");
    	RANK_MAPPING.put("ID_P_RANK32_NAME", "SERGEANT MAJOR");
    	RANK_MAPPING.put("ID_P_RANK33_NAME", "SERGEANT MAJOR 1 STAR");
    	RANK_MAPPING.put("ID_P_RANK34_NAME", "SERGEANT MAJOR 2 STAR");
    	RANK_MAPPING.put("ID_P_RANK35_NAME", "WARRANT OFFICER ONE");
    	RANK_MAPPING.put("ID_P_RANK36_NAME", "CHIEF WARRANT OFFICER TWO");
    	RANK_MAPPING.put("ID_P_RANK37_NAME", "CHIEF WARRANT OFFICER THREE");
    	RANK_MAPPING.put("ID_P_RANK38_NAME", "CHIEF WARRANT OFFICER FOUR");
    	RANK_MAPPING.put("ID_P_RANK39_NAME", "CHIEF WARRANT OFFICER FIVE");
    	RANK_MAPPING.put("ID_P_RANK40_NAME", "SECOND LIEUTENANT");
    	RANK_MAPPING.put("ID_P_RANK41_NAME", "FIRST LIEUTENANT");
    	RANK_MAPPING.put("ID_P_RANK42_NAME", "CAPTAIN");
    	RANK_MAPPING.put("ID_P_RANK43_NAME", "MAJOR");
    	RANK_MAPPING.put("ID_P_RANK44_NAME", "LIEUTENANT COLONEL");
    	RANK_MAPPING.put("ID_P_RANK45_NAME", "COLONEL");
    	RANK_MAPPING.put("ID_P_RANK46_NAME", "COLONEL SERVICE STAR 1");
    	RANK_MAPPING.put("ID_P_RANK47_NAME", "COLONEL SERVICE STAR 2");
    	RANK_MAPPING.put("ID_P_RANK48_NAME", "COLONEL SERVICE STAR 3");
    	RANK_MAPPING.put("ID_P_RANK49_NAME", "COLONEL SERVICE STAR 4");
    	RANK_MAPPING.put("ID_P_RANK50_NAME", "COLONEL SERVICE STAR 5");
    	RANK_MAPPING.put("ID_P_RANK51_NAME", "COLONEL SERVICE STAR 6");
    	RANK_MAPPING.put("ID_P_RANK52_NAME", "COLONEL SERVICE STAR 7");
    	RANK_MAPPING.put("ID_P_RANK53_NAME", "COLONEL SERVICE STAR 8");
    	RANK_MAPPING.put("ID_P_RANK54_NAME", "COLONEL SERVICE STAR 9");
    	RANK_MAPPING.put("ID_P_RANK55_NAME", "COLONEL SERVICE STAR 10");
    	RANK_MAPPING.put("ID_P_RANK56_NAME", "COLONEL SERVICE STAR 11");
    	RANK_MAPPING.put("ID_P_RANK57_NAME", "COLONEL SERVICE STAR 12");
    	RANK_MAPPING.put("ID_P_RANK58_NAME", "COLONEL SERVICE STAR 13");
    	RANK_MAPPING.put("ID_P_RANK59_NAME", "COLONEL SERVICE STAR 14");
    	RANK_MAPPING.put("ID_P_RANK60_NAME", "COLONEL SERVICE STAR 15");
    	RANK_MAPPING.put("ID_P_RANK61_NAME", "COLONEL SERVICE STAR 16");
    	RANK_MAPPING.put("ID_P_RANK62_NAME", "COLONEL SERVICE STAR 17");
    	RANK_MAPPING.put("ID_P_RANK63_NAME", "COLONEL SERVICE STAR 18");
    	RANK_MAPPING.put("ID_P_RANK64_NAME", "COLONEL SERVICE STAR 19");
    	RANK_MAPPING.put("ID_P_RANK65_NAME", "COLONEL SERVICE STAR 20");
    	RANK_MAPPING.put("ID_P_RANK66_NAME", "COLONEL SERVICE STAR 21");
    	RANK_MAPPING.put("ID_P_RANK67_NAME", "COLONEL SERVICE STAR 22");
    	RANK_MAPPING.put("ID_P_RANK68_NAME", "COLONEL SERVICE STAR 23");
    	RANK_MAPPING.put("ID_P_RANK69_NAME", "COLONEL SERVICE STAR 24");
    	RANK_MAPPING.put("ID_P_RANK70_NAME", "COLONEL SERVICE STAR 25");
    	RANK_MAPPING.put("ID_P_RANK71_NAME", "COLONEL SERVICE STAR 26");
    	RANK_MAPPING.put("ID_P_RANK72_NAME", "COLONEL SERVICE STAR 27");
    	RANK_MAPPING.put("ID_P_RANK73_NAME", "COLONEL SERVICE STAR 28");
    	RANK_MAPPING.put("ID_P_RANK74_NAME", "COLONEL SERVICE STAR 29");
    	RANK_MAPPING.put("ID_P_RANK75_NAME", "COLONEL SERVICE STAR 30");
    	RANK_MAPPING.put("ID_P_RANK76_NAME", "COLONEL SERVICE STAR 31");
    	RANK_MAPPING.put("ID_P_RANK77_NAME", "COLONEL SERVICE STAR 32");
    	RANK_MAPPING.put("ID_P_RANK78_NAME", "COLONEL SERVICE STAR 33");
    	RANK_MAPPING.put("ID_P_RANK79_NAME", "COLONEL SERVICE STAR 34");
    	RANK_MAPPING.put("ID_P_RANK80_NAME", "COLONEL SERVICE STAR 35");
    	RANK_MAPPING.put("ID_P_RANK81_NAME", "COLONEL SERVICE STAR 36");
    	RANK_MAPPING.put("ID_P_RANK82_NAME", "COLONEL SERVICE STAR 37");
    	RANK_MAPPING.put("ID_P_RANK83_NAME", "COLONEL SERVICE STAR 38");
    	RANK_MAPPING.put("ID_P_RANK84_NAME", "COLONEL SERVICE STAR 39");
    	RANK_MAPPING.put("ID_P_RANK85_NAME", "COLONEL SERVICE STAR 40");
    	RANK_MAPPING.put("ID_P_RANK86_NAME", "COLONEL SERVICE STAR 41");
    	RANK_MAPPING.put("ID_P_RANK87_NAME", "COLONEL SERVICE STAR 42");
    	RANK_MAPPING.put("ID_P_RANK88_NAME", "COLONEL SERVICE STAR 43");
    	RANK_MAPPING.put("ID_P_RANK89_NAME", "COLONEL SERVICE STAR 44");
    	RANK_MAPPING.put("ID_P_RANK90_NAME", "COLONEL SERVICE STAR 45");
    	RANK_MAPPING.put("ID_P_RANK91_NAME", "COLONEL SERVICE STAR 46");
    	RANK_MAPPING.put("ID_P_RANK92_NAME", "COLONEL SERVICE STAR 47");
    	RANK_MAPPING.put("ID_P_RANK93_NAME", "COLONEL SERVICE STAR 48");
    	RANK_MAPPING.put("ID_P_RANK94_NAME", "COLONEL SERVICE STAR 49");
    	RANK_MAPPING.put("ID_P_RANK95_NAME", "COLONEL SERVICE STAR 50");
    	RANK_MAPPING.put("ID_P_RANK96_NAME", "COLONEL SERVICE STAR 51");
    	RANK_MAPPING.put("ID_P_RANK97_NAME", "COLONEL SERVICE STAR 52");
    	RANK_MAPPING.put("ID_P_RANK98_NAME", "COLONEL SERVICE STAR 53");
    	RANK_MAPPING.put("ID_P_RANK99_NAME", "COLONEL SERVICE STAR 54");
    	RANK_MAPPING.put("ID_P_RANK100_NAME", "COLONEL SERVICE STAR 55");
    	RANK_MAPPING.put("ID_P_RANK101_NAME", "COLONEL SERVICE STAR 56");
    	RANK_MAPPING.put("ID_P_RANK102_NAME", "COLONEL SERVICE STAR 57");
    	RANK_MAPPING.put("ID_P_RANK103_NAME", "COLONEL SERVICE STAR 58");
    	RANK_MAPPING.put("ID_P_RANK104_NAME", "COLONEL SERVICE STAR 59");
    	RANK_MAPPING.put("ID_P_RANK105_NAME", "COLONEL SERVICE STAR 60");
    	RANK_MAPPING.put("ID_P_RANK106_NAME", "COLONEL SERVICE STAR 61");
    	RANK_MAPPING.put("ID_P_RANK107_NAME", "COLONEL SERVICE STAR 62");
    	RANK_MAPPING.put("ID_P_RANK108_NAME", "COLONEL SERVICE STAR 63");
    	RANK_MAPPING.put("ID_P_RANK109_NAME", "COLONEL SERVICE STAR 64");
    	RANK_MAPPING.put("ID_P_RANK110_NAME", "COLONEL SERVICE STAR 65");
    	RANK_MAPPING.put("ID_P_RANK111_NAME", "COLONEL SERVICE STAR 66");
    	RANK_MAPPING.put("ID_P_RANK112_NAME", "COLONEL SERVICE STAR 67");
    	RANK_MAPPING.put("ID_P_RANK113_NAME", "COLONEL SERVICE STAR 68");
    	RANK_MAPPING.put("ID_P_RANK114_NAME", "COLONEL SERVICE STAR 69");
    	RANK_MAPPING.put("ID_P_RANK115_NAME", "COLONEL SERVICE STAR 70");
    	RANK_MAPPING.put("ID_P_RANK116_NAME", "COLONEL SERVICE STAR 71");
    	RANK_MAPPING.put("ID_P_RANK117_NAME", "COLONEL SERVICE STAR 72");
    	RANK_MAPPING.put("ID_P_RANK118_NAME", "COLONEL SERVICE STAR 73");
    	RANK_MAPPING.put("ID_P_RANK119_NAME", "COLONEL SERVICE STAR 74");
    	RANK_MAPPING.put("ID_P_RANK120_NAME", "COLONEL SERVICE STAR 75");
    	RANK_MAPPING.put("ID_P_RANK121_NAME", "COLONEL SERVICE STAR 76");
    	RANK_MAPPING.put("ID_P_RANK122_NAME", "COLONEL SERVICE STAR 77");
    	RANK_MAPPING.put("ID_P_RANK123_NAME", "COLONEL SERVICE STAR 78");
    	RANK_MAPPING.put("ID_P_RANK124_NAME", "COLONEL SERVICE STAR 79");
    	RANK_MAPPING.put("ID_P_RANK125_NAME", "COLONEL SERVICE STAR 80");
    	RANK_MAPPING.put("ID_P_RANK126_NAME", "COLONEL SERVICE STAR 81");
    	RANK_MAPPING.put("ID_P_RANK127_NAME", "COLONEL SERVICE STAR 82");
    	RANK_MAPPING.put("ID_P_RANK128_NAME", "COLONEL SERVICE STAR 83");
    	RANK_MAPPING.put("ID_P_RANK129_NAME", "COLONEL SERVICE STAR 84");
    	RANK_MAPPING.put("ID_P_RANK130_NAME", "COLONEL SERVICE STAR 85");
    	RANK_MAPPING.put("ID_P_RANK131_NAME", "COLONEL SERVICE STAR 86");
    	RANK_MAPPING.put("ID_P_RANK132_NAME", "COLONEL SERVICE STAR 87");
    	RANK_MAPPING.put("ID_P_RANK133_NAME", "COLONEL SERVICE STAR 88");
    	RANK_MAPPING.put("ID_P_RANK134_NAME", "COLONEL SERVICE STAR 89");
    	RANK_MAPPING.put("ID_P_RANK135_NAME", "COLONEL SERVICE STAR 90");
    	RANK_MAPPING.put("ID_P_RANK136_NAME", "COLONEL SERVICE STAR 91");
    	RANK_MAPPING.put("ID_P_RANK137_NAME", "COLONEL SERVICE STAR 92");
    	RANK_MAPPING.put("ID_P_RANK138_NAME", "COLONEL SERVICE STAR 93");
    	RANK_MAPPING.put("ID_P_RANK139_NAME", "COLONEL SERVICE STAR 94");
    	RANK_MAPPING.put("ID_P_RANK140_NAME", "COLONEL SERVICE STAR 95");
    	RANK_MAPPING.put("ID_P_RANK141_NAME", "COLONEL SERVICE STAR 96");
    	RANK_MAPPING.put("ID_P_RANK142_NAME", "COLONEL SERVICE STAR 97");
    	RANK_MAPPING.put("ID_P_RANK143_NAME", "COLONEL SERVICE STAR 98");
    	RANK_MAPPING.put("ID_P_RANK144_NAME", "COLONEL SERVICE STAR 99");
    	RANK_MAPPING.put("ID_P_RANK145_NAME", "COLONEL SERVICE STAR 100");
    	
    }
}
