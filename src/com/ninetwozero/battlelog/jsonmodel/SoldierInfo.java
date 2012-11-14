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

package com.ninetwozero.battlelog.jsonmodel;

import com.google.gson.annotations.SerializedName;
import com.ninetwozero.battlelog.datatype.PersonaData;

public class SoldierInfo {
	
	@SerializedName("kills")
	private int kills;
	
	@SerializedName("deaths")
	private int deaths;
	
	@SerializedName("numWins")
	private int wins;
	
	@SerializedName("numLosses")
	private int losses;
	
	@SerializedName("score")
	private int score;
	
	@SerializedName("timePlayeed")
	private int timePlayed;
	
    @SerializedName("persona")
    private PersonaData persona;

	public SoldierInfo(int kills, int deaths, int wins, int losses, int score, int timePlayed, PersonaData persona) {
		this.kills = kills;
		this.deaths = deaths;
		this.wins = wins;
		this.losses = losses;
		this.score = score;
		this.timePlayed = timePlayed;
		this.persona = persona;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int mKills) {
		this.kills = mKills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int mDeaths) {
		this.deaths = mDeaths;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int mWins) {
		this.wins = mWins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int mLosses) {
		this.losses = mLosses;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int mScore) {
		this.score = mScore;
	}

	public int getTimePlayed() {
		return timePlayed;
	}

	public void setTimePlayed(int mTimePlayed) {
		this.timePlayed = mTimePlayed;
	}

	public PersonaData getPersona() {
		return persona;
	}

	public void setPersonas(PersonaData mPersona) {
		this.persona = mPersona;
	}
    
}
