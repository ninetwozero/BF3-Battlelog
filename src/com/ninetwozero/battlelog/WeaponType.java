package com.ninetwozero.battlelog;

import java.util.HashMap;


public class WeaponType {

	//Attributes
	String identifier, name, label, rateOfFireText, range, ammo;
	boolean automatic, burst, singleshot;
	int rateOfFireNum;
	
	public WeaponType(String i, String n, String l, String rof, String r, String a, boolean au, boolean b, boolean s) {
		
		this.identifier = i;
		this.name = n;
		this.label = l;
		this.rateOfFireText = rof;
		this.rateOfFireNum = 0;
		this.range = r;
		this.ammo = a;
		this.automatic = au;
		this.burst = b;
		this.singleshot = s;
	
	}

	public WeaponType(String i, String n, String l, int rof, String r, String a, boolean au, boolean b, boolean s) {
		
		this(i, n, l, "", r, a, au, b, s);
		this.rateOfFireNum = rof;
		
	}
}