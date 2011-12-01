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

import com.ninetwozero.battlelog.misc.DataBank;


public class PlatoonStats implements Serializable {

	//Serializable
	private static final long serialVersionUID = -4498099516324216414L;
	
	//Base-section
	private String name;
	private long id;
	
	//General stats
	private PlatoonStatsItem[] globalTop, topPlayers, scores, spm, time;
	
	//Construct
	public PlatoonStats(
		String sName, long lId, PlatoonStatsItem[] gS, PlatoonStatsItem[] tP, PlatoonStatsItem[] kS, PlatoonStatsItem[] kSPM, PlatoonStatsItem[] kT
	) {
		
		//Basic attributes
		this.name = sName;
		this.id = lId;

		this.globalTop = gS;
		this.topPlayers = tP;
		this.scores = kS;
		this.spm = kSPM;
		this.time = kT;
		
	}

	//Getters	
	public final String getAccountName() { return name; }
	public final long getId() { return id; }
	public final PlatoonStatsItem[] getTopPlayers() { return this.topPlayers; }
	public final PlatoonStatsItem[] getGlobalTop() { return this.globalTop; }
	public final PlatoonStatsItem[] getScores() { return this.scores; }
	public final PlatoonStatsItem[] getSpm() { return this.spm; }
	public final PlatoonStatsItem[] getTime() { return this.time; }	


}
