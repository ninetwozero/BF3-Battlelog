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

package com.ninetwozero.battlelog;

import java.util.HashMap;

public class UnlockData {

	//Attributes
	long parentId, valueNeeded, actualValue;
	double unlockPercentage;
	
	
	//Getters
	
	static HashMap<String, String> ATTACHMENT_MAPPING;
	static {
		ATTACHMENT_MAPPING.put("ID_P_ANAME_LASER", "Laser Sight");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_FOREGRIP", "Foregrip");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_RX01", "Reflex ");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_FLECHETTE", "12G Flechette");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_MAG", "Extended Mag");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_PKAS", "PKA");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_BOLT", "Straight Pull Bolt");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_PKS", "PKS");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_PKA", "PK");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_BIPOD", "Bipod");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_PSO", "PSO");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_SLUG", "12G Slug");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_FRAG", "12G Frag");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_NOSECONDARYRAIL", "Underslung Rail");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_SUPPRESS", "Flash Supp");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_SNIPERRIFLE", "Rifle Scope ");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_IRNV", "IRNV ");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_BUCK", "12G Buckshot");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_LIGHT", "Tactical Light");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_NOPRIMARY", "No Primary");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_NOSECONDARY", "No Secondary");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_BALL", "Ballistic ");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_BARREL", "Heavy Barrel");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_M145", "M145 ");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_SILENCER", "Suppressor");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_ACOG", "ACOG ");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_NOOPTIC", "No Optics");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_RIFLE", "Rifle Scope ");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_KOBRA", "KOBRA ");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_NOPRIMARYRAIL", "Underslung rail");
		ATTACHMENT_MAPPING.put("ID_P_ANAME_EOTECH", "Holographic ");
	}

}