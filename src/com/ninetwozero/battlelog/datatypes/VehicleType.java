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
