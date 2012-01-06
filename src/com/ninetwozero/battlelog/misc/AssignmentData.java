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
package com.ninetwozero.battlelog.misc;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.ninetwozero.battlelog.R;


public class AssignmentData implements Parcelable {

	//Attributes
	private int resourceId;
	private String id, description, set;
	private ArrayList<AssignmentData.Objective> objectives;
	private ArrayList<AssignmentData.Dependency> dependencies;
	private ArrayList<AssignmentData.Unlock> unlocks;
	
	//Constructs
	public AssignmentData(
			
		int rId, String id, String d, String s, 
		ArrayList<AssignmentData.Objective> c, ArrayList<AssignmentData.Dependency> dp, ArrayList<AssignmentData.Unlock> u
		
	) {
		
		this.resourceId = rId;
		this.id = id;
		this.description = d;
		this.set = s;
		this.objectives = c;
		this.dependencies = dp;
		this.unlocks = u;
		
	}
	
	public AssignmentData(Parcel in) {}

	@Override /* TODO */
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel( Parcel dest, int flags ) {}

	public static final Parcelable.Creator<AssignmentData> CREATOR = new Parcelable.Creator<AssignmentData>() {
		
		public AssignmentData createFromParcel(Parcel in) { return new AssignmentData(in); }
        public AssignmentData[] newArray(int size) { return new AssignmentData[size]; }
	
	};
	
	//Getters
	public int getResourceId() { return this.resourceId; }
	public String getId() { return this.id; }
	public String getDescription() { return this.description; }
	public String getSet() { return this.set; }
	public ArrayList<AssignmentData.Objective> getObjectives() { return this.objectives; }
	public ArrayList<AssignmentData.Dependency> getDependencies() { return this.dependencies; }
	public ArrayList<AssignmentData.Unlock> getUnlocks() { return this.unlocks; }
	public int getProgress() { 

		//How many?
		final int numObjectives = this.objectives.size();
		double count = 0;
		
		//Iterate
		for( AssignmentData.Objective obj : this.objectives ) {
			
			count += (obj.getCurrentValue() / obj.getGoalValue()); //0 <= x <= 1
			
		}
		return (int) Math.round( ( count / numObjectives ) * 100 );
		
	}
	
	public boolean isCompleted() { 
		
		for( AssignmentData.Objective obj : this.objectives ) {
		
			if( obj.getCurrentValue() < obj.getGoalValue() ) { 
				
				return false; 
				
			}
			
		}
		
		return true;
		
	}
	
	//toString()
	@Override
	public String toString() { return "#:"+ objectives.size() + ":" + dependencies.size() + ":" + unlocks.size(); }
	
	//Subclass
	public static class Objective {
		
		//Attributes
		private double currentValue, goalValue;
		private String id, weapon, kit, description, unit;
		
		//Construct
		public Objective(double c, double g, String i, String w, String k, String d, String u) {
			
			this.currentValue = c;
			this.goalValue = g;
			this.id = i;
			this.weapon = w;
			this.kit = k;
			this.description = d;
			this.unit = u;
			
		}
		
		//Getters
		public double getCurrentValue() { return this.currentValue; }
		public double getGoalValue() { return this.goalValue; }
		public String getId() { return this.id; }
		public String getWeapon() { return this.weapon; }
		public String getKit() { return this.kit; }
		public String getDescription() { return this.description; }
		public String getUnit() { return this.unit; }
		
	}

	public static class Dependency {
		
		//Attributes
		private int count;
		private String id;
		
		//Construct
		public Dependency(int c, String i ) {
			
			this.count = c;
			this.id = i;
			
		}
		
		//Getters
		public int getCount() { return this.count; }
		public String getId() { return this.id; }
		
	}
	
	public static class Unlock {
		
		//Attributes
		private String id, type;
		private boolean visible;
		
		//Construct
		public Unlock(String i, String t, boolean v) {
			
			this.id = i;
			this.type = t;
			this.visible = v;
			
		}
		
		//Getters
		public String getId() { return this.id; }
		public String getType() { return this.type; }
		public boolean isVisible() { return this.visible; }
		
	}
}