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

import com.ninetwozero.battlelog.R;
import java.io.Serializable;


public class PlatoonStatsItem implements Serializable {

	//Serializable 
	private static final long serialVersionUID = -5628303442912315179L;

	//Base-section
	private String label;
	private int min, mid, max, avg;
	private double dMin, dMid, dMax, dAvg;
	private ProfileData profile;
	
	//Construct
	public PlatoonStatsItem( String l, int a, int b, int c, int d, ProfileData p ) {
		
		this.label = l;
		this.min = a;
		this.mid = b;
		this.max = c;
		this.avg = d;
		this.profile = p;
	
	}

	public PlatoonStatsItem( String l, double a, double b, double c, double d, ProfileData p ) {
		
		this.label = l;
		this.dMin = a;
		this.dMid = b;
		this.dMax = c;
		this.dAvg = d;
		this.profile = p;
	
	}
	
	//Getters	
	public String getLabel() { return this.label; }
	public int getMin() { return this.min; }
	public int getMid() { return this.mid; }
	public int getMax() { return this.max; }
	public int getAvg() { return this.avg; }
	public double getDMin() { return this.dMin; }
	public double getDMid() { return this.dMid; }
	public double getDMax() { return this.dMax; }
	public double getDAvg() { return this.dAvg; }
	public ProfileData getProfile() { return this.profile; }
	
	//Setters
	public void setMid(int m) { this.mid = m; }
	public void add(PlatoonStatsItem p) {
		
		this.min = ( p.getMin() < this.min ) ? p.getMin() : this.min;
		this.mid = p.getMid();
		this.max = ( p.getMax() > this.max ) ? p.getMax() : this.max;
		this.avg += p.getAvg();
		
	}
}
