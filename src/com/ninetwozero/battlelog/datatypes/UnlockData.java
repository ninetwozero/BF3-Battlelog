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

import com.ninetwozero.battlelog.misc.DataBank;



public class UnlockData {

	//Attributes
	private int kitId;
	private double unlockPercentage;
	private long scoreNeeded, scoreCurrent;
	private String parentIdentifier, unlockIdentifier, objective, type;
	
	public UnlockData(int k, double u, long scn, long scc, String pi, String ui, String o, String t) {
		
		this.kitId = k;
		this.unlockPercentage = u;
		this.scoreNeeded = scn;
		this.scoreCurrent = scc;
		this.parentIdentifier = pi;
		this.unlockIdentifier = ui;
		this.objective = o;
		this.type = t;		
		
	}
	
	//Getters
	public int getKitId() { return kitId; }
	public String getKitTitle() { return DataBank.getKitTitle( kitId ); }
	public double getUnlockPercentage() { return Math.round( this.unlockPercentage * 100 ) / 100; }
	public long getScoreNeeded() { return this.scoreNeeded; }
	public long getScoreCurrent() { return this.scoreCurrent; }
	public String getParent() { 
	
		if( this.type == "weapon+" ) {
			
			return DataBank.getWeaponTitle( this.parentIdentifier );
			
		} else if( this.type == "vehicle+" ) {
			
			return DataBank.getVehicleTitle( this.parentIdentifier );
			
		} else return "";
	}
	public String getTitle() { 
	
		if( this.type.equals( "weapon" ) ) {
			
			return DataBank.getWeaponTitle( this.unlockIdentifier );
			
		} else if ( this.type.equals( "weapon+" ) ) {
		
			return DataBank.getAttachmentTitle( this.unlockIdentifier );
			
		} else if( this.type.equals( "vehicle+" ) ) {

			return DataBank.getVehicleAddon( this.unlockIdentifier );
			
		} else if( this.type.equals( "kit+" ) ) {
			
			return DataBank.getKitUnlockTitle( this.unlockIdentifier );
			
		} else if( this.type.equals( "skill" ) ) {
			
			return DataBank.getSkillTitle( this.unlockIdentifier );
			
		} else {
			
			return "";
		
		}
		
	}
	public String getName() { 
		
		//Check how it went
		if( this.type.equals( "weapon" ) ) {
			
			return getTitle(); 
		
		} else if( this.type.equals( "weapon+" ) ) {
			
			return getParent() + " " + getTitle();
			
		} else if( this.type.equals( "vehicle+" ) ) {
			
			return getParent() + " " + getTitle();
			
		} else if( this.type.equals( "kit+" ) ) {
			
			return getTitle();
			
		} else if( this.type.equals( "skill" ) ) {
			
			return getTitle();
			
		} else {
			
			return "";
			
		}
		
	}
	public String getObjective() {

		if( this.objective.startsWith( "sc_" ) ) {
			
			return DataBank.getUnlockGoal( this.objective ).replace( 
				
				"{scoreCurr}/{scoreNeeded}", 
				this.scoreCurrent + "/" + this.scoreNeeded
			
			);
			
		} else if ( "rank".equals( this.objective ) ) {
			
			return DataBank.getUnlockGoal( this.objective ).replace( 
					
				"{rank}", this.getScoreNeeded() + "" 
				
			).replace(
						
				"{rankCurr}", this.getScoreCurrent() + "" 
					
			);
			
		} else if( this.objective.startsWith( "c_" ) ) {
			
			return DataBank.getUnlockGoal( "c_" ).replace( 
					
				"{scoreCurr}/{scoreNeeded} {name}", 
				this.scoreCurrent + "/" + this.scoreNeeded + " " + getParent()
				
			);
			
		}
		
		return this.objective;
		
	}
	public String getType() { return this.type; }
	public String getTypeTitle() {
		
		if( this.type.equals("weapon") ) return "Weapon";
		else if( this.type.equals( "weapon+" ) ) return "Attachment";
		else if( this.type.equals( "vehicle+" ) ) return "Upgrade";
		else if( this.type.equals( "skill" ) ) return "Skill";
		else if( this.type.equals( "kit+" ) ) return "Kit";
		else return "N/A";
		
	}
}