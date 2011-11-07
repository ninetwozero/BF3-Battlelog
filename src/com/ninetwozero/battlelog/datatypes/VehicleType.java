package com.ninetwozero.battlelog.datatypes;

import java.io.Serializable;
import java.util.HashMap;


public class VehicleType implements Serializable {

	//Attributes
	private static final long serialVersionUID = -6682902181714468486L;
	private String identifier, name, label;
	
	//Construct
	public VehicleType(String i, String n, String l) {
		
		this.identifier = i;
		this.name = n;
		this.label = l;
		
	}
	
	//Getters
	public String getIdentifier() { return this.identifier; }
	public String getName() { return this.name; }
	public String getLabel() { return this.label; }
	
}
