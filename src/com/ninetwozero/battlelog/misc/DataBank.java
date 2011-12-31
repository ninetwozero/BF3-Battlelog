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

import java.util.HashMap;

import com.ninetwozero.battlelog.R;
import android.content.Context;

import com.ninetwozero.battlelog.datatypes.PlatformData;
import com.ninetwozero.battlelog.datatypes.VehicleType;
import com.ninetwozero.battlelog.datatypes.WeaponType;



public class DataBank {

	//Construct = none
	public DataBank() {}
	
	//Getters
	public static String getRankTitle( String key ) { return RANKS.containsKey( key )? RANKS.get( key ) : key; }
	public static String getWeaponTitle( String key ) { return WEAPONS.containsKey( key )? WEAPONS.get( key ).getName() : key; }
	public static String getWeaponTitleShort( String key ) { return WEAPONS_SHORT.containsKey( key )? WEAPONS_SHORT.get( key ) : key; }
	public static String getAttachmentTitle( String key ) { return WEAPON_ATTACHMENTS.containsKey( key )? WEAPON_ATTACHMENTS.get( key ) : key; }
	public static String getVehicleTitle( String key ) { return VEHICLES.containsKey( key )? VEHICLES.get( key ).getName() : key; }
	public static String getVehicleAddon( String key ) { return VEHICLE_ADDONS.containsKey( key )? VEHICLE_ADDONS.get( key ) : key; }
	public static String getSkillTitle( String key ) { return SKILLS.containsKey( key )? SKILLS.get( key ) : key; }
	public static String getKitUnlockTitle( String key ) { return KIT_ITEMS.containsKey( key )? KIT_ITEMS.get( key ) : key; }
	public static String getUnlockGoal( String key ) { return UNLOCK_GOALS.containsKey( key )? UNLOCK_GOALS.get( key ) : key; }
	public static String getAwardTitle( String key ) { return AWARDS.containsKey( key ) ? AWARDS.get( key ) : key; }
	public static String getMapTitle( String key ) { return MAPS.containsKey( key ) ? MAPS.get( key ) : key; }
	public static String getCoopLevelTitle( String key ) { return COOP_DATA.containsKey( key ) ? COOP_DATA.get( key ) : key; }
	public static String getDifficultyTitle( String key ) { return DIFFICULTY_MAP.containsKey( key ) ? DIFFICULTY_MAP.get( key ) : key; }
	public static String[] getAssignmentTitle( String key ) { return ASSIGNMENTS.containsKey( key ) ? ASSIGNMENTS.get( key ) : new String[] { key, key }; }
	public static String getAssignmentCriteria( String key ) { return CRITERIAS.containsKey( key ) ? CRITERIAS.get( key ) : key ; }
	public static String getExpansionTitle( String key ) { return EXPANSION.containsKey( key ) ? EXPANSION.get( key ) : key; }
	
	//Maps
	private static PlatformData[] PLATFORMS;
	private static HashMap<String, String> RANKS;
	private static HashMap<String, VehicleType> VEHICLES;
	private static HashMap<String, String> KIT_ITEMS;
	private static HashMap<String, String> SKILLS;
	private static HashMap<String, String> VEHICLE_ADDONS;
	private static HashMap<String, String> WEAPON_ATTACHMENTS;
	private static HashMap<String, WeaponType> WEAPONS;
	private static HashMap<String, String> WEAPONS_SHORT;
	private static HashMap<String, String> UNLOCK_GOALS;		
	private static HashMap<String, String> AWARDS;
	private static HashMap<String, String> MAPS;
	private static HashMap<String, String> COOP_DATA;
	private static HashMap<String, String> DIFFICULTY_MAP;
	private static HashMap<String, String[]> ASSIGNMENTS;
	private static HashMap<String, String> CRITERIAS;
	private static HashMap<String, String> EXPANSION;
	
	static{
  
		//Init!
		PLATFORMS = new PlatformData[4];
    	WEAPONS = new HashMap<String, WeaponType>();
    	VEHICLES = new HashMap<String, VehicleType>();
    	RANKS = new HashMap<String, String>();
    	KIT_ITEMS = new HashMap<String, String>();
    	SKILLS = new HashMap<String, String>();
    	VEHICLE_ADDONS = new HashMap<String, String>();
    	WEAPONS_SHORT = new HashMap<String, String>();
    	WEAPON_ATTACHMENTS = new HashMap<String, String>();
    	UNLOCK_GOALS = new HashMap<String, String>();
    	AWARDS = new HashMap<String, String>();
    	MAPS = new HashMap<String, String>();
    	COOP_DATA = new HashMap<String, String>();
    	DIFFICULTY_MAP = new HashMap<String, String>();
    	ASSIGNMENTS = new HashMap<String, String[]>();
    	CRITERIAS = new HashMap<String, String>();
    	EXPANSION = new HashMap<String, String>();
    	
    	//PLATFORMS
    	PLATFORMS[0] = new PlatformData(1, "pc");
    	PLATFORMS[1] = new PlatformData(2, "xbox");
    	PLATFORMS[2] = new PlatformData(1, "");
    	PLATFORMS[3] = new PlatformData(4, "ps3");

    	//DIFFICULTIES
		DIFFICULTY_MAP.put("ID_RP_DIFFICULTY_MEDIUM", "Normal");
		DIFFICULTY_MAP.put("ID_RP_DIFFICULTY_HARDCORE", "Hardcore");
		DIFFICULTY_MAP.put("ID_RP_DIFFICULTY_HARD", "Hard");
		DIFFICULTY_MAP.put("ID_RP_DIFFICULTY_EASY", "Easy");

    	//MAPS
		MAPS.put("MP_007", "Caspian Border");
		MAPS.put("MP_013", "Damavand Peak");
		MAPS.put("MP_012", "Operation Firestorm");
		MAPS.put("MP_011", "Seine Crossing");
		MAPS.put("MP_017", "Noshahr Canals");
		MAPS.put("MP_018", "Kharg Island");
		MAPS.put("MP_019", "Omaha");
		MAPS.put("MP_001", "Grand Bazaar");
		MAPS.put("MP_003", "Tehran Highway");
		MAPS.put("XP1_003", "Sharqi Peninsula");
		MAPS.put("XP1_002", "Gulf of Oman");
		MAPS.put("XP1_001", "Strike at Karkand");
		MAPS.put("XP1_004", "Wake Island");
		
    	//UNLOCKS
    	UNLOCK_GOALS.put( "rank", "Reach rank {rank} (you are rank {rankCurr})");
    	UNLOCK_GOALS.put( "sc_assault", "{scoreCurr}/{scoreNeeded} Assault Score");
    	UNLOCK_GOALS.put( "sc_engineer", "{scoreCurr}/{scoreNeeded} Engineer Score");
    	UNLOCK_GOALS.put( "sc_recon", "{scoreCurr}/{scoreNeeded} Recon Score");
    	UNLOCK_GOALS.put( "sc_support", "{scoreCurr}/{scoreNeeded} Support Score");
    	UNLOCK_GOALS.put( "sc_coop", "{scoreCurr}/{scoreNeeded} CO-OP Score");
    	UNLOCK_GOALS.put( "sc_vehicleah", "{scoreCurr}/{scoreNeeded} Attack Helicopter Score"); //Vehicle Air
    	UNLOCK_GOALS.put( "sc_vehicleaa", "{scoreCurr}/{scoreNeeded} Anti Air Score");
    	UNLOCK_GOALS.put( "sc_vehiclejet", "{scoreCurr}/{scoreNeeded} Jetplane Score"); //Vehicle Air
    	UNLOCK_GOALS.put( "sc_vehicleifv", "{scoreCurr}/{scoreNeeded} Infantry Fighting Vehicle Score");
    	UNLOCK_GOALS.put( "sc_vehiclesh", "{scoreCurr}/{scoreNeeded}  Scout Helicopter Score"); //Vehicle Air
    	UNLOCK_GOALS.put( "sc_vehiclembt", "{scoreCurr}/{scoreNeeded} Battle Tanks Score"); //Vehicle Main
    	UNLOCK_GOALS.put( "c_", "{scoreCurr}/{scoreNeeded} {name} kills");
    	UNLOCK_GOALS.put( "xpm", "Complete \"{name}\"");
    	
    	//RANKS
    	RANKS.put("ID_P_RANK00_NAME", "Recruit");
    	RANKS.put("ID_P_RANK01_NAME", "Private First Class");
    	RANKS.put("ID_P_RANK02_NAME", "Private First Class 1 Star");
    	RANKS.put("ID_P_RANK03_NAME", "Private First Class 2 Star");
    	RANKS.put("ID_P_RANK04_NAME", "Private First Class 3 Star");
    	RANKS.put("ID_P_RANK05_NAME", "Lance Corporal");
    	RANKS.put("ID_P_RANK06_NAME", "Lance Corporal 1 Star");
    	RANKS.put("ID_P_RANK07_NAME", "Lance Corporal 2 Star");
    	RANKS.put("ID_P_RANK08_NAME", "Lance Corporal 3 Star");
    	RANKS.put("ID_P_RANK09_NAME", "Corporal");
    	RANKS.put("ID_P_RANK10_NAME", "Corporal 1 Star");
    	RANKS.put("ID_P_RANK11_NAME", "Corporal 2 Star");
    	RANKS.put("ID_P_RANK12_NAME", "Corporal 3 Star");
    	RANKS.put("ID_P_RANK13_NAME", "Sergeant");
    	RANKS.put("ID_P_RANK14_NAME", "Sergeant 1 Star");
    	RANKS.put("ID_P_RANK15_NAME", "Sergeant 2 Star");
    	RANKS.put("ID_P_RANK16_NAME", "Sergeant 3 Star");
    	RANKS.put("ID_P_RANK17_NAME", "Staff Sergeant");
    	RANKS.put("ID_P_RANK18_NAME", "Staff Sergeant 1 Star");
    	RANKS.put("ID_P_RANK19_NAME", "Staff Sergeant 2 Star");
    	RANKS.put("ID_P_RANK20_NAME", "Gunnery Sergeant");
    	RANKS.put("ID_P_RANK21_NAME", "Gunnery Sergeant 1 Star");
    	RANKS.put("ID_P_RANK22_NAME", "Gunnery Sergeant 2 Star");
    	RANKS.put("ID_P_RANK23_NAME", "Master Sergeant");
    	RANKS.put("ID_P_RANK24_NAME", "Master Sergeant 1 Star");
    	RANKS.put("ID_P_RANK25_NAME", "Master Sergeant 2 Star");
    	RANKS.put("ID_P_RANK26_NAME", "First Sergeant");
    	RANKS.put("ID_P_RANK27_NAME", "First Sergeant 1 Star");
    	RANKS.put("ID_P_RANK28_NAME", "First Sergeant 2 Star");
    	RANKS.put("ID_P_RANK29_NAME", "Master Gunnery Sergeant");
    	RANKS.put("ID_P_RANK30_NAME", "Master Gunnery Sergeant 1 Star");
    	RANKS.put("ID_P_RANK31_NAME", "Master Gunnery Sergeant 2 Star");
    	RANKS.put("ID_P_RANK32_NAME", "Sergeant Major");
    	RANKS.put("ID_P_RANK33_NAME", "Sergeant Major 1 Star");
    	RANKS.put("ID_P_RANK34_NAME", "Sergeant Major 2 Star");
    	RANKS.put("ID_P_RANK35_NAME", "Warrant Officer 1 Star");
    	RANKS.put("ID_P_RANK36_NAME", "Chief Warrant Officer Two");
    	RANKS.put("ID_P_RANK37_NAME", "Chief Warrant Officer THREE");
    	RANKS.put("ID_P_RANK38_NAME", "Chief Warrant Officer FOUR");
    	RANKS.put("ID_P_RANK39_NAME", "Chief Warrant Officer FIVE");
    	RANKS.put("ID_P_RANK40_NAME", "Second Lieutenant");
    	RANKS.put("ID_P_RANK41_NAME", "First Lieutenant");
    	RANKS.put("ID_P_RANK42_NAME", "Captain");
    	RANKS.put("ID_P_RANK43_NAME", "Major");
    	RANKS.put("ID_P_RANK44_NAME", "Lieutenant Colonel");
    	RANKS.put("ID_P_RANK45_NAME", "Colonel");
    	RANKS.put("ID_P_RANK46_NAME", "Colonel Service Star 1");
    	RANKS.put("ID_P_RANK47_NAME", "Colonel Service Star 2");
    	RANKS.put("ID_P_RANK48_NAME", "Colonel Service Star 3");
    	RANKS.put("ID_P_RANK49_NAME", "Colonel Service Star 4");
    	RANKS.put("ID_P_RANK50_NAME", "Colonel Service Star 5");
    	RANKS.put("ID_P_RANK51_NAME", "Colonel Service Star 6");
    	RANKS.put("ID_P_RANK52_NAME", "Colonel Service Star 7");
    	RANKS.put("ID_P_RANK53_NAME", "Colonel Service Star 8");
    	RANKS.put("ID_P_RANK54_NAME", "Colonel Service Star 9");
    	RANKS.put("ID_P_RANK55_NAME", "Colonel Service Star 10");
    	RANKS.put("ID_P_RANK56_NAME", "Colonel Service Star 11");
    	RANKS.put("ID_P_RANK57_NAME", "Colonel Service Star 12");
    	RANKS.put("ID_P_RANK58_NAME", "Colonel Service Star 13");
    	RANKS.put("ID_P_RANK59_NAME", "Colonel Service Star 14");
    	RANKS.put("ID_P_RANK60_NAME", "Colonel Service Star 15");
    	RANKS.put("ID_P_RANK61_NAME", "Colonel Service Star 16");
    	RANKS.put("ID_P_RANK62_NAME", "Colonel Service Star 17");
    	RANKS.put("ID_P_RANK63_NAME", "Colonel Service Star 18");
    	RANKS.put("ID_P_RANK64_NAME", "Colonel Service Star 19");
    	RANKS.put("ID_P_RANK65_NAME", "Colonel Service Star 20");
    	RANKS.put("ID_P_RANK66_NAME", "Colonel Service Star 21");
    	RANKS.put("ID_P_RANK67_NAME", "Colonel Service Star 22");
    	RANKS.put("ID_P_RANK68_NAME", "Colonel Service Star 23");
    	RANKS.put("ID_P_RANK69_NAME", "Colonel Service Star 24");
    	RANKS.put("ID_P_RANK70_NAME", "Colonel Service Star 25");
    	RANKS.put("ID_P_RANK71_NAME", "Colonel Service Star 26");
    	RANKS.put("ID_P_RANK72_NAME", "Colonel Service Star 27");
    	RANKS.put("ID_P_RANK73_NAME", "Colonel Service Star 28");
    	RANKS.put("ID_P_RANK74_NAME", "Colonel Service Star 29");
    	RANKS.put("ID_P_RANK75_NAME", "Colonel Service Star 30");
    	RANKS.put("ID_P_RANK76_NAME", "Colonel Service Star 31");
    	RANKS.put("ID_P_RANK77_NAME", "Colonel Service Star 32");
    	RANKS.put("ID_P_RANK78_NAME", "Colonel Service Star 33");
    	RANKS.put("ID_P_RANK79_NAME", "Colonel Service Star 34");
    	RANKS.put("ID_P_RANK80_NAME", "Colonel Service Star 35");
    	RANKS.put("ID_P_RANK81_NAME", "Colonel Service Star 36");
    	RANKS.put("ID_P_RANK82_NAME", "Colonel Service Star 37");
    	RANKS.put("ID_P_RANK83_NAME", "Colonel Service Star 38");
    	RANKS.put("ID_P_RANK84_NAME", "Colonel Service Star 39");
    	RANKS.put("ID_P_RANK85_NAME", "Colonel Service Star 40");
    	RANKS.put("ID_P_RANK86_NAME", "Colonel Service Star 41");
    	RANKS.put("ID_P_RANK87_NAME", "Colonel Service Star 42");
    	RANKS.put("ID_P_RANK88_NAME", "Colonel Service Star 43");
    	RANKS.put("ID_P_RANK89_NAME", "Colonel Service Star 44");
    	RANKS.put("ID_P_RANK90_NAME", "Colonel Service Star 45");
    	RANKS.put("ID_P_RANK91_NAME", "Colonel Service Star 46");
    	RANKS.put("ID_P_RANK92_NAME", "Colonel Service Star 47");
    	RANKS.put("ID_P_RANK93_NAME", "Colonel Service Star 48");
    	RANKS.put("ID_P_RANK94_NAME", "Colonel Service Star 49");
    	RANKS.put("ID_P_RANK95_NAME", "Colonel Service Star 50");
    	RANKS.put("ID_P_RANK96_NAME", "Colonel Service Star 51");
    	RANKS.put("ID_P_RANK97_NAME", "Colonel Service Star 52");
    	RANKS.put("ID_P_RANK98_NAME", "Colonel Service Star 53");
    	RANKS.put("ID_P_RANK99_NAME", "Colonel Service Star 54");
    	RANKS.put("ID_P_RANK100_NAME", "Colonel Service Star 55");
    	RANKS.put("ID_P_RANK101_NAME", "Colonel Service Star 56");
    	RANKS.put("ID_P_RANK102_NAME", "Colonel Service Star 57");
    	RANKS.put("ID_P_RANK103_NAME", "Colonel Service Star 58");
    	RANKS.put("ID_P_RANK104_NAME", "Colonel Service Star 59");
    	RANKS.put("ID_P_RANK105_NAME", "Colonel Service Star 60");
    	RANKS.put("ID_P_RANK106_NAME", "Colonel Service Star 61");
    	RANKS.put("ID_P_RANK107_NAME", "Colonel Service Star 62");
    	RANKS.put("ID_P_RANK108_NAME", "Colonel Service Star 63");
    	RANKS.put("ID_P_RANK109_NAME", "Colonel Service Star 64");
    	RANKS.put("ID_P_RANK110_NAME", "Colonel Service Star 65");
    	RANKS.put("ID_P_RANK111_NAME", "Colonel Service Star 66");
    	RANKS.put("ID_P_RANK112_NAME", "Colonel Service Star 67");
    	RANKS.put("ID_P_RANK113_NAME", "Colonel Service Star 68");
    	RANKS.put("ID_P_RANK114_NAME", "Colonel Service Star 69");
    	RANKS.put("ID_P_RANK115_NAME", "Colonel Service Star 70");
    	RANKS.put("ID_P_RANK116_NAME", "Colonel Service Star 71");
    	RANKS.put("ID_P_RANK117_NAME", "Colonel Service Star 72");
    	RANKS.put("ID_P_RANK118_NAME", "Colonel Service Star 73");
    	RANKS.put("ID_P_RANK119_NAME", "Colonel Service Star 74");
    	RANKS.put("ID_P_RANK120_NAME", "Colonel Service Star 75");
    	RANKS.put("ID_P_RANK121_NAME", "Colonel Service Star 76");
    	RANKS.put("ID_P_RANK122_NAME", "Colonel Service Star 77");
    	RANKS.put("ID_P_RANK123_NAME", "Colonel Service Star 78");
    	RANKS.put("ID_P_RANK124_NAME", "Colonel Service Star 79");
    	RANKS.put("ID_P_RANK125_NAME", "Colonel Service Star 80");
    	RANKS.put("ID_P_RANK126_NAME", "Colonel Service Star 81");
    	RANKS.put("ID_P_RANK127_NAME", "Colonel Service Star 82");
    	RANKS.put("ID_P_RANK128_NAME", "Colonel Service Star 83");
    	RANKS.put("ID_P_RANK129_NAME", "Colonel Service Star 84");
    	RANKS.put("ID_P_RANK130_NAME", "Colonel Service Star 85");
    	RANKS.put("ID_P_RANK131_NAME", "Colonel Service Star 86");
    	RANKS.put("ID_P_RANK132_NAME", "Colonel Service Star 87");
    	RANKS.put("ID_P_RANK133_NAME", "Colonel Service Star 88");
    	RANKS.put("ID_P_RANK134_NAME", "Colonel Service Star 89");
    	RANKS.put("ID_P_RANK135_NAME", "Colonel Service Star 90");
    	RANKS.put("ID_P_RANK136_NAME", "Colonel Service Star 91");
    	RANKS.put("ID_P_RANK137_NAME", "Colonel Service Star 92");
    	RANKS.put("ID_P_RANK138_NAME", "Colonel Service Star 93");
    	RANKS.put("ID_P_RANK139_NAME", "Colonel Service Star 94");
    	RANKS.put("ID_P_RANK140_NAME", "Colonel Service Star 95");
    	RANKS.put("ID_P_RANK141_NAME", "Colonel Service Star 96");
    	RANKS.put("ID_P_RANK142_NAME", "Colonel Service Star 97");
    	RANKS.put("ID_P_RANK143_NAME", "Colonel Service Star 98");
    	RANKS.put("ID_P_RANK144_NAME", "Colonel Service Star 99");
    	RANKS.put("ID_P_RANK145_NAME", "Colonel Service Star 100");

    	WEAPONS.put("04C8604E-37DE-4B51-B70A-66468003D604", new WeaponType("04C8604E-37DE-4B51-B70A-66468003D604", "MP7", "mp7", 950, "SHORT", "20 [4.6x30mm]", true, false, true));
    	WEAPONS.put("04FD6527-0BF0-4A67-9ABB-9F992BF2CBA0", new WeaponType("04FD6527-0BF0-4A67-9ABB-9F992BF2CBA0", "G17C", "g17c", "SEMIAUTO", "SHORT", "17 [9x19mm Parabellum]", false, false, true));
    	WEAPONS.put("05EB2892-8B51-488E-8956-4350C3D2BA27", new WeaponType("05EB2892-8B51-488E-8956-4350C3D2BA27", "M98B", "m98b", "BOLT ACTION", "VERY LONG", "ID_P_AMMO_M98B", false, false, true));
    	WEAPONS.put("0733BF61-8EBC-4666-9610-7E27D7313791", new WeaponType("0733BF61-8EBC-4666-9610-7E27D7313791", "SG553", "sg553", 700, "MEDIUM", "30 [5.56x45mm NATO]", true, true, true));
    	WEAPONS.put("07A4C87A-D325-4A73-8C5A-C001ACD13334", new WeaponType("07A4C87A-D325-4A73-8C5A-C001ACD13334", "870MCS", "870mcs", "PUMP ACTION", "SHORT", "ID_P_AMMO_870", false, false, true));
    	WEAPONS.put("07A6AB6A-457D-4481-94F9-A3FE15C3D923", new WeaponType("07A6AB6A-457D-4481-94F9-A3FE15C3D923", "MP443 SUPP.", "mp443-supp", "SEMIAUTO", "SHORT", "17 [9x19mm Parabellum]", false, false, true));
    	WEAPONS.put("08F58ECD-BC99-48AA-A9B3-47D412E99A4E", new WeaponType("08F58ECD-BC99-48AA-A9B3-47D412E99A4E", "RPG-7V2", "rpg-7v2", "SINGLE SHOT", "MEDIUM", "ID_P_AMMO_RPG7", false, false, true));
    	WEAPONS.put("0E0E4701-359B-48FF-B91A-F4B6373435E4", new WeaponType("0E0E4701-359B-48FF-B91A-F4B6373435E4", "KH2002", "kh2002", 850, "LONG", "30 [5.56x45mm NATO]", false, true, true));
    	WEAPONS.put("120A0838-9E95-4564-A6F6-5A14A1E0AF85", new WeaponType("120A0838-9E95-4564-A6F6-5A14A1E0AF85", "AKS-74u", "aks-74u", 650, "MEDIUM", "30 [5.45x39mm WP]", true, false, true));
    	WEAPONS.put("1C426722-C0B6-492F-98B2-3B9D2B97C808", new WeaponType("1C426722-C0B6-492F-98B2-3B9D2B97C808", "M1911 SUPP.", "m1911-supp", "SEMIAUTO", "SHORT", "8 [.45ACP]", false, false, true));
    	WEAPONS.put("1C689273-7637-40FF-89A5-E75F120D9E02", new WeaponType("1C689273-7637-40FF-89A5-E75F120D9E02", "M27 IAR", "m27-iar", 750, "MEDIUM", "45 [5.56x45mm NATO]", true, false, true));
    	WEAPONS.put("1E5E0296-CFD0-448E-B1D7-F795E8F98E2C", new WeaponType("1E5E0296-CFD0-448E-B1D7-F795E8F98E2C", "G36C", "g36c", 750, "MEDIUM", "30 [5.56x45mm NATO]", true, true, true));
    	WEAPONS.put("1EA227D8-2EB5-A63B-52FF-BBA9CFE34AD8", new WeaponType("1EA227D8-2EB5-A63B-52FF-BBA9CFE34AD8", ".44 MAGNUM", "44-magnum", "SEMIAUTO", "SHORT", "ID_P_AMMO_T44", false, false, true));
    	WEAPONS.put("21A80B6B-8FA6-4BCC-84D3-7ED782E978FA", new WeaponType("21A80B6B-8FA6-4BCC-84D3-7ED782E978FA", "AS VAL", "as-val", 900, "SHORT", "20 [9x39mm]", true, false, true));
    	WEAPONS.put("27F63AEA-DD70-4929-9B08-5FF8F075B75E", new WeaponType("27F63AEA-DD70-4929-9B08-5FF8F075B75E", "DAO-12", "dao-12", "SEMIAUTO", "SHORT", "ID_P_AMMO_DAO12", false, false, true));
    	WEAPONS.put("283C66DE-3866-46CD-A1C0-B456A5916537", new WeaponType("283C66DE-3866-46CD-A1C0-B456A5916537", "M320", "m320", "SINGLE SHOT", "MEDIUM", "1 [40mm]", false, false, true));
    	WEAPONS.put("2A267103-14F2-4255-B0D4-819139A4E202", new WeaponType("2A267103-14F2-4255-B0D4-819139A4E202", "UMP-45", "ump-45", 600, "SHORT", "25 [.45ACP]", true, true, true));
    	WEAPONS.put("32B899E5-0542-45E6-A34B-86871C7FE098", new WeaponType("32B899E5-0542-45E6-A34B-86871C7FE098", "M27 IAR", "m27-iar", 750, "MEDIUM", "45 [5.56x45mm NATO]", true, false, true));
    	WEAPONS.put("35796A7B-C7FA-4C0F-AD99-3DDB3B60A293", new WeaponType("35796A7B-C7FA-4C0F-AD99-3DDB3B60A293", "SA-18 IGLA", "sa-18-igla", "SINGLE SHOT", "LONG", "ID_P_AMMO_IGLA", false, false, true));
    	WEAPONS.put("386F9329-7DE7-6FB9-1366-2877C698D9B7", new WeaponType("386F9329-7DE7-6FB9-1366-2877C698D9B7", "SCAR-H", "scar-h", 600, "MEDIUM", "20 [7.62x51mm NATO]", true, false, true));
    	WEAPONS.put("38C20C39-EE43-489F-AE95-DF0519F72409", new WeaponType("38C20C39-EE43-489F-AE95-DF0519F72409", "MP412 REX", "mp412-rex", "SEMIAUTO", "SHORT", "ID_P_AMMO_REX", false, false, true));
    	WEAPONS.put("3A6B6A16-E5A1-33E0-5B53-56E77833DAF4", new WeaponType("3A6B6A16-E5A1-33E0-5B53-56E77833DAF4", "M416", "m416", 750, "LONG", "30 [5.56x45mm NATO]", true, true, true));
    	WEAPONS.put("3BA55147-6619-4697-8E2B-AC6B1D183C0E", new WeaponType("3BA55147-6619-4697-8E2B-AC6B1D183C0E", "AK-74M", "ak-74m", 650, "LONG", "30 [5.45x39mm WP]", true, false, true));
    	WEAPONS.put("3F06931A-443C-6E78-DEF9-A33EB9F43D35", new WeaponType("3F06931A-443C-6E78-DEF9-A33EB9F43D35", "PKP PECHENEG", "pkp-pecheneg", 650, "MEDIUM", "100 [7.62x54mm R]", true, false, false));
    	WEAPONS.put("405F32BB-3E1A-4201-B96D-10B231D91BA5", new WeaponType("405F32BB-3E1A-4201-B96D-10B231D91BA5", "M60E4", "m60e4", 500, "MEDIUM", "100 [7.62x51mm NATO]", true, false, false));
    	WEAPONS.put("414C4598-4089-43E0-82FB-BBF7031D02E8", new WeaponType("414C4598-4089-43E0-82FB-BBF7031D02E8", "M240B", "m240b", 750, "MEDIUM", "100 [7.62x51mm NATO]", true, false, false));
    	WEAPONS.put("42DB9F03-0224-4676-99FC-ED444E356290", new WeaponType("42DB9F03-0224-4676-99FC-ED444E356290", "M16A3", "m16a3", 800, "LONG", "30 [5.56x45mm NATO]", true, true, true));
    	WEAPONS.put("4F90708E-6875-4A5E-B685-CAF310A0BA95", new WeaponType("4F90708E-6875-4A5E-B685-CAF310A0BA95", "M1911 TACT.", "m1911-tact", "SEMIAUTO", "SHORT", "8 [.45ACP]", false, false, true));
    	WEAPONS.put("50849B49-F3DA-4C92-9830-D4A2932BC9E7", new WeaponType("50849B49-F3DA-4C92-9830-D4A2932BC9E7", "PP-2000", "pp-2000", 600, "SHORT", "20 [9x19mm Parabellum]", true, false, true));
    	WEAPONS.put("512819DB-3E82-33B7-F1D5-E612C9A396BF", new WeaponType("512819DB-3E82-33B7-F1D5-E612C9A396BF", "G3A3", "g3a3", 500, "LONG", "20 [7.62x51mm NATO]", true, false, true));
    	WEAPONS.put("5244385C-B7ED-4266-AB7C-C1C1B222A9CD", new WeaponType("5244385C-B7ED-4266-AB7C-C1C1B222A9CD", "M4", "m4", 800, "MEDIUM", "30 [5.56x45mm NATO]", false, true, true));
    	WEAPONS.put("5E2D49D1-D1BB-F553-78A5-8D537C43E624", new WeaponType("5E2D49D1-D1BB-F553-78A5-8D537C43E624", "AEK-971", "aek-971", 750, "LONG", "30 [5.45x39mm WP]", true, true, true));
    	WEAPONS.put("655D5E41-6DB8-4F3C-9F2A-8117AE11699C", new WeaponType("655D5E41-6DB8-4F3C-9F2A-8117AE11699C", "MK11 MOD 0", "mk11-mod-0", "SEMIAUTO", "VERY LONG", "10 [7.62x51mm NATO]", false, false, true));
    	WEAPONS.put("65D4A9F9-0ACD-46FD-9AE2-3E9670DD22FB", new WeaponType("65D4A9F9-0ACD-46FD-9AE2-3E9670DD22FB", "AN-94", "an-94", 600, "LONG", "30 [5.45x39mm WP]", true, true, false));
    	WEAPONS.put("6921F5C9-3487-43E6-86F5-296DA097FFFB", new WeaponType("6921F5C9-3487-43E6-86F5-296DA097FFFB", "M16A4", "m16a4", 800, "LONG", "30 [5.56x45mm NATO]", false, true, true));
    	WEAPONS.put("69A28562-5569-4D76-82FB-98C4047306F1", new WeaponType("69A28562-5569-4D76-82FB-98C4047306F1", "M4A1", "m4a1", 800, "MEDIUM", "30 [5.56x45mm NATO]", true, true, true));
    	WEAPONS.put("6F3993B9-E8B2-412F-9361-341140FCBF79", new WeaponType("6F3993B9-E8B2-412F-9361-341140FCBF79", ".44 SCOPED", "44-scoped", "SEMIAUTO", "LONG", "ID_P_AMMO_T44", false, false, true));
    	WEAPONS.put("6F741867-AE83-CC7D-BFB1-035452D7A5B4", new WeaponType("6F741867-AE83-CC7D-BFB1-035452D7A5B4", "USAS-12", "usas-12", 300, "SHORT", "ID_P_AMMO_USAS12", false, true, true));
    	WEAPONS.put("71B0A1D6-9E4F-40A3-9906-1A7F3AAD573A", new WeaponType("71B0A1D6-9E4F-40A3-9906-1A7F3AAD573A", "MP443", "mp443", "SEMIAUTO", "SHORT", "18 [9x19mm Parabellum]", false, false, true));
    	WEAPONS.put("75D1FFC8-D442-4212-B668-96AED9030FC6", new WeaponType("75D1FFC8-D442-4212-B668-96AED9030FC6", "RPK-74M", "rpk-74m", 600, "MEDIUM", "45 [5.45x39mm WP]", true, false, true));
    	WEAPONS.put("84B78F21-217A-4A46-A06E-34A90637BAC8", new WeaponType("84B78F21-217A-4A46-A06E-34A90637BAC8", "AK-74M", "ak-74m", 650, "LONG", "30 [5.45x39mm WP]", true, false, true));
    	WEAPONS.put("8963F500-E71D-41FC-4B24-AE17D18D8C73", new WeaponType("8963F500-E71D-41FC-4B24-AE17D18D8C73", "KNIFE", "knife", "", "", "", false, false, false));
    	WEAPONS.put("8DCA9ABD-0723-454C-9575-7E4CA0791D0B", new WeaponType("8DCA9ABD-0723-454C-9575-7E4CA0791D0B", "L85A2", "l85a2", 650, "LONG", "30 [5.56x45mm NATO]", true, true, true));
    	WEAPONS.put("9351DDBF-795A-4BC6-84D7-37B537E3D049", new WeaponType("9351DDBF-795A-4BC6-84D7-37B537E3D049", "SV98", "sv98", "BOLT ACTION", "VERY LONG", "10 [7.62x54mm R]", false, false, true));
    	WEAPONS.put("93CC5226-4381-7458-509E-B2D6F4498164", new WeaponType("93CC5226-4381-7458-509E-B2D6F4498164", "M40A5", "m40a5", "BOLT ACTION", "VERY LONG", "10 [7.62x51mm NATO]", false, false, true));
    	WEAPONS.put("95E00B23-BAD4-4F3B-A85E-990204EFF26B", new WeaponType("95E00B23-BAD4-4F3B-A85E-990204EFF26B", "MG36", "mg36", 750, "MEDIUM", "100 [5.56x45mm NATO]", true, false, false));
    	WEAPONS.put("96FC0A67-DEA2-4061-B955-E173A8DBB00D", new WeaponType("96FC0A67-DEA2-4061-B955-E173A8DBB00D", "SAIGA 12K", "saiga-12k", "SEMIAUTO", "SHORT", "ID_P_AMMO_SAIGA12", false, false, true));
    	WEAPONS.put("9A97A9FE-DCE5-41E8-8D89-A421B103FA75", new WeaponType("9A97A9FE-DCE5-41E8-8D89-A421B103FA75", "FAMAS", "famas", 900, "LONG", "30 [5.56x45mm NATO]", true, true, true));
    	WEAPONS.put("9B3AF503-2018-4BC9-893F-CD393D3BAD77", new WeaponType("9B3AF503-2018-4BC9-893F-CD393D3BAD77", "M1911 S-TAC", "m1911-s-tac", "SEMIAUTO", "SHORT", "8 [.45ACP]", false, false, true));
    	WEAPONS.put("A4F108EB-1FA2-4C94-93FE-357B1D7EBF4A", new WeaponType("A4F108EB-1FA2-4C94-93FE-357B1D7EBF4A", "AKS-74u", "aks-74u", 650, "MEDIUM", "30 [5.45x39mm WP]", true, false, true));
    	WEAPONS.put("A4F683C2-40E2-464C-AE85-AFE4462F2D40", new WeaponType("A4F683C2-40E2-464C-AE85-AFE4462F2D40", "M9 SUPP.", "m9-supp", "SEMIAUTO", "SHORT", "15 [9x19mm Parabellum]", false, false, true));
    	WEAPONS.put("A607E88D-90B0-4ECD-B892-8BF66AEF90ED", new WeaponType("A607E88D-90B0-4ECD-B892-8BF66AEF90ED", "RPK-74M", "rpk-74m", 600, "MEDIUM", "45 [5.45x39mm WP]", true, false, true));
    	WEAPONS.put("A7278B05-8D76-4A40-B65D-4414490F6886", new WeaponType("A7278B05-8D76-4A40-B65D-4414490F6886", "M16A3", "m16a3", 800, "LONG", "30 [5.56x45mm NATO]", true, false, true));
    	WEAPONS.put("A76BB99E-ABFE-48E9-9972-5D87E5365DAB", new WeaponType("A76BB99E-ABFE-48E9-9972-5D87E5365DAB", "M1911", "m1911", "SEMIAUTO", "SHORT", "8 [.45ACP]", false, false, true));
    	WEAPONS.put("A8C7508A-F43B-4446-ACCE-F350EDFEDB28", new WeaponType("A8C7508A-F43B-4446-ACCE-F350EDFEDB28", "93R", "93r", 1100, "SHORT", "20 [9x19mm Parabellum]", false, true, true));
    	WEAPONS.put("A9F5B1F6-D83E-4BD8-AFE8-08C4B0A3E697", new WeaponType("A9F5B1F6-D83E-4BD8-AFE8-08C4B0A3E697", "SVD", "svd", "SEMIAUTO", "VERY LONG", "10 [7.62x54mm R]", false, false, true));
    	WEAPONS.put("AAE173E4-8DD7-5C25-1763-7A4D9380EB10", new WeaponType("AAE173E4-8DD7-5C25-1763-7A4D9380EB10", "MK3A1", "mk3a1", 255, "SHORT", "ID_P_XP1_AMMO_JACKHAMMER", true, false, false));
    	WEAPONS.put("AC994B66-DA51-42FB-A234-FCBA33EB9AB7", new WeaponType("AC994B66-DA51-42FB-A234-FCBA33EB9AB7", "G18 SUPP.", "g18-supp", 1100, "SHORT", "19 [9x19mm Parabellum]", true, false, true));
    	WEAPONS.put("AEAA518B-9253-40C2-AA18-A11F8F2D474C", new WeaponType("AEAA518B-9253-40C2-AA18-A11F8F2D474C", "M249", "m249", 800, "MEDIUM", "100 [5.56x45mm NATO]", true, false, false));
    	WEAPONS.put("AEFD4BF4-4C08-4834-9DFF-1F7C529175AF", new WeaponType("AEFD4BF4-4C08-4834-9DFF-1F7C529175AF", "G17C SUPP.", "g17c-supp", "SEMIAUTO", "SHORT", "17 [9x19mm Parabellum]", false, false, true));
    	WEAPONS.put("AFF7E14E-5918-456B-9922-6ED1A50F3F15", new WeaponType("AFF7E14E-5918-456B-9922-6ED1A50F3F15", "QBU-88", "qbu-88", "SEMIAUTO", "VERY LONG", "10 [7.62x54mm R]", false, false, true));
    	WEAPONS.put("B145A444-BC4D-48BF-806A-0CEFA0EC231B", new WeaponType("B145A444-BC4D-48BF-806A-0CEFA0EC231B", "M9", "m9", "SEMIAUTO", "SHORT", "15 [9x19mm Parabellum]", false, false, true));
    	WEAPONS.put("B1575807-C480-7286-719C-EE2520292A79", new WeaponType("B1575807-C480-7286-719C-EE2520292A79", "M4A1", "m4a1", 800, "MEDIUM", "30 [5.56x45mm NATO]", true, false, true));
    	WEAPONS.put("B2DEF86D-A127-769E-23ED-C9F47F29FAD3", new WeaponType("B2DEF86D-A127-769E-23ED-C9F47F29FAD3", "M26 MASS", "m26-mass", "SEMIAUTO", "SHORT", "ID_P_AMMO_M26", false, false, true));
    	WEAPONS.put("BFAC29DB-5193-4E69-96D9-37D4124C44C2", new WeaponType("BFAC29DB-5193-4E69-96D9-37D4124C44C2", "SMAW", "smaw", "SINGLE SHOT", "MEDIUM", "ID_P_AMMO_SMAW", false, false, true));
    	WEAPONS.put("C12E6868-FC08-4E25-8AD0-1C51201EA69B", new WeaponType("C12E6868-FC08-4E25-8AD0-1C51201EA69B", "P90", "p90", 900, "SHORT", "50 [5.7x28mm]", true, false, true));
    	WEAPONS.put("C79AAC6E-566E-40E1-B373-3B0029530393", new WeaponType("C79AAC6E-566E-40E1-B373-3B0029530393", "A-91", "a-91", 800, "MEDIUM", "30 [5.56x45mm NATO]", true, false, true));
    	WEAPONS.put("CB018ADD-3648-4504-9359-9BAFB8D92F7D", new WeaponType("CB018ADD-3648-4504-9359-9BAFB8D92F7D", "MK11 MOD 0", "mk11-mod-0", "SEMIAUTO", "VERY LONG", "10 [7.62x51mm NATO]", false, false, true));
    	WEAPONS.put("CB651B07-2CE4-4527-B1AC-2AEB6D04CBF5", new WeaponType("CB651B07-2CE4-4527-B1AC-2AEB6D04CBF5", "SKS", "sks", "SEMIAUTO", "VERY LONG", "20 [7.62x39mm WP]", false, false, true));
    	WEAPONS.put("CBAEC77C-A6AD-4D63-96BD-61FCA6C18417", new WeaponType("CBAEC77C-A6AD-4D63-96BD-61FCA6C18417", "L96", "l96", "BOLT ACTION", "VERY LONG", "10 [7.62x51mm NATO]", false, false, true));
    	WEAPONS.put("CECC74B7-403F-4BA1-8ECD-4A59FB5379BD", new WeaponType("CECC74B7-403F-4BA1-8ECD-4A59FB5379BD", "PP-19", "pp-19", 900, "SHORT", "50 [9x19mm Parabellum]", true, false, false));
    	WEAPONS.put("D0E124FB-7116-4FBB-AF00-D8994AEB548D", new WeaponType("D0E124FB-7116-4FBB-AF00-D8994AEB548D", "TYPE 88 LMG", "type-88-lmg", 700, "MEDIUM", "100 [5.8x42mm DAP-87]", true, false, false));
    	WEAPONS.put("D20984F3-364E-4C06-9879-09280EDF6DF3", new WeaponType("D20984F3-364E-4C06-9879-09280EDF6DF3", "QBZ-95B", "qbz-95b", 650, "MEDIUM", "30 [5.45x39mm WP]", true, false, true));
    	WEAPONS.put("D4FF4D2C-361F-491E-B53D-207CF77FA609", new WeaponType("D4FF4D2C-361F-491E-B53D-207CF77FA609", "M9 TACT.", "m9-tact", "SEMIAUTO", "SHORT", "15 [9x19mm Parabellum]", false, false, true));
    	WEAPONS.put("DB364A96-08FB-4C6E-856B-BD9749AE0A92", new WeaponType("DB364A96-08FB-4C6E-856B-BD9749AE0A92", "G18", "g18", 1100, "SHORT", "19 [9x19mm Parabellum]", true, false, true));
    	WEAPONS.put("DB94F5EC-74D5-4DB2-9A15-E0C4154BFFD4", new WeaponType("DB94F5EC-74D5-4DB2-9A15-E0C4154BFFD4", "F2000", "f2000", 850, "LONG", "30 [5.56x45mm NATO]", true, false, true));
    	WEAPONS.put("DC356150-2A5F-4FCA-BE6C-B993EE7F8A8B", new WeaponType("DC356150-2A5F-4FCA-BE6C-B993EE7F8A8B", "FGM-148 JAVELIN", "fgm-148-javelin", "SINGLE SHOT", "MEDIUM", "ID_P_AMMO_JAVELIN", false, false, true));
    	WEAPONS.put("E43287AB-529D-4803-B585-6C17E2DD6AEB", new WeaponType("E43287AB-529D-4803-B585-6C17E2DD6AEB", "MP443 TACT.", "mp443-tact", "SEMIAUTO", "SHORT", "17 [9x19mm Parabellum]", false, false, true));
    	WEAPONS.put("E7266EC2-4977-60F2-A7CB-6EF7D98A5E2E", new WeaponType("E7266EC2-4977-60F2-A7CB-6EF7D98A5E2E", "G53", "hk53", 750, "MEDIUM", "30 [5.56x45mm NATO]", true, false, true));
    	WEAPONS.put("E9BEDD8F-899F-3A3C-C561-5E58B350C60D", new WeaponType("E9BEDD8F-899F-3A3C-C561-5E58B350C60D", "FIM-92 STINGER", "fim-92-stinger", "SINGLE SHOT", "LONG", "ID_P_AMMO_STINGER", false, false, true));
    	WEAPONS.put("EB17660D-D81B-4BB7-BE95-70662855489E", new WeaponType("EB17660D-D81B-4BB7-BE95-70662855489E", "M39 EMR", "m39-emr", "SEMIAUTO", "VERY LONG", "10 [7.62x51mm NATO]", false, false, true));
    	WEAPONS.put("F0B12FF6-7D20-4E49-8F19-EF5F1E9CBA6D", new WeaponType("F0B12FF6-7D20-4E49-8F19-EF5F1E9CBA6D", "M39 EMR", "m39-emr", "SEMIAUTO", "VERY LONG", "10 [7.62x51mm NATO]", false, false, true));
    	WEAPONS.put("F3DF4C76-FD8F-0F11-3B8C-8B9C756EF089", new WeaponType("F3DF4C76-FD8F-0F11-3B8C-8B9C756EF089", "M1014", "m1014", "SEMIAUTO", "SHORT", "ID_P_AMMO_M1014", false, false, true));
    	WEAPONS.put("F3EF48EB-37C3-4F5E-A2ED-ACE7E4D419DD", new WeaponType("F3EF48EB-37C3-4F5E-A2ED-ACE7E4D419DD", "PDW-R", "pdw-r", 750, "SHORT", "30 [5.56x45mm NATO]", true, true, true));
    	WEAPONS.put("FE05ACAA-32FC-4FD7-A34B-61413F6F7B1A", new WeaponType("FE05ACAA-32FC-4FD7-A34B-61413F6F7B1A", "QBB-95", "qbb-95", 650, "MEDIUM", "100 [5.45x39mm WP]", true, false, false));
    	WEAPONS.put("FEFBA819-898F-4B66-8596-B6576FA9B28A", new WeaponType("FEFBA819-898F-4B66-8596-B6576FA9B28A", "SVD", "svd", "SEMIAUTO", "VERY LONG", "10 [7.62x54mm R]", false, false, true));
    	
    	VEHICLES.put("0E09B2D0-BA4A-1509-E1D2-949FB0C04DBE", new VehicleType("0E09B2D0-BA4A-1509-E1D2-949FB0C04DBE", "UH-1Y VENOM", "uh-1y-venom") );
    	VEHICLES.put("1E8653E6-11A0-DF93-C808-E48351D2F578", new VehicleType("1E8653E6-11A0-DF93-C808-E48351D2F578", "AAV-7A1 AMTRAC", "aav-7a1-amtrac") );
    	VEHICLES.put("37A53096-BA80-5498-1347-8C7B238680C8", new VehicleType("37A53096-BA80-5498-1347-8C7B238680C8", "M220 TOW LAUNCHER", "m220-tow-launcher") );
    	VEHICLES.put("3F18FCA6-A7D4-D3B5-28E5-44A5CAFFE6BE", new VehicleType("3F18FCA6-A7D4-D3B5-28E5-44A5CAFFE6BE", "F-35", "f-35") );
    	VEHICLES.put("5AF4330A-7202-11DE-B32B-D34A31A54ED5", new VehicleType("5AF4330A-7202-11DE-B32B-D34A31A54ED5", "A-10 THUNDERBOLT", "a-10-thunderbolt") );
    	VEHICLES.put("60106975-DD7D-11DD-A030-B04E425BA11E", new VehicleType("60106975-DD7D-11DD-A030-B04E425BA11E", "T-90A", "t-90a") );
    	VEHICLES.put("74866776-D5AF-BD32-7964-CD234506235D", new VehicleType("74866776-D5AF-BD32-7964-CD234506235D", "SKID LOADER", "skid-loader") );
    	VEHICLES.put("860157CA-6527-4123-B60E-71117DD878D7", new VehicleType("860157CA-6527-4123-B60E-71117DD878D7", "LAV-AD", "lav-ad") );
    	VEHICLES.put("89BEA1D6-2FF4-11DE-8C23-DAB1D69416DE", new VehicleType("89BEA1D6-2FF4-11DE-8C23-DAB1D69416DE", "MI-28 HAVOC", "mi-28-havoc") );
    	VEHICLES.put("98E5B9BF-0B68-4AF0-A20D-1A23D6B6BF8B", new VehicleType("98E5B9BF-0B68-4AF0-A20D-1A23D6B6BF8B", "GAZ-3937 VODNIK", "gaz-3937-vodnik") );
    	VEHICLES.put("9D35A483-0B6B-91AE-5025-351AD87B2B46", new VehicleType("9D35A483-0B6B-91AE-5025-351AD87B2B46", "DPV", "dpv") );
    	VEHICLES.put("A36C9712-54B3-A5FF-8627-7BC7EFA0C668", new VehicleType("A36C9712-54B3-A5FF-8627-7BC7EFA0C668", "9K22 TUNGUSKA-M", "9k22-tunguska-m") );
    	VEHICLES.put("A676D498-A524-42AD-BE78-72B071D8CD6A", new VehicleType("A676D498-A524-42AD-BE78-72B071D8CD6A", "AH-1Z VIPER", "ah-1z-viper") );
    	VEHICLES.put("AAE95907-AFD4-11DD-84FB-9FA71F68ED5E", new VehicleType("AAE95907-AFD4-11DD-84FB-9FA71F68ED5E", "BMP-2M", "bmp-2m") );
    	VEHICLES.put("ADF563C9-28B1-C42B-993E-B2FD40F36078", new VehicleType("ADF563C9-28B1-C42B-993E-B2FD40F36078", "LAV-25", "lav-25") );
    	VEHICLES.put("B06A08AB-EECF-11DD-8117-9421284A74E5", new VehicleType("B06A08AB-EECF-11DD-8117-9421284A74E5", "M1 ABRAMS", "m1-abrams") );
    	VEHICLES.put("B26FD546-2ADF-1A90-3044-F7748B86DA26", new VehicleType("B26FD546-2ADF-1A90-3044-F7748B86DA26", "RHIB BOAT", "rhib-boat") );
    	VEHICLES.put("B9BAE0F8-72B9-4E1C-B5AD-F9353727C990", new VehicleType("B9BAE0F8-72B9-4E1C-B5AD-F9353727C990", "KA-60 KASATKA", "ka-60-kasatka") );
    	VEHICLES.put("C645317B-45BB-E082-7E5C-918388C22D59", new VehicleType("C645317B-45BB-E082-7E5C-918388C22D59", "PANTSIR-S1", "pantsir-s1") );
    	VEHICLES.put("C81F8757-E6D2-DF2D-1CFE-B72B4F74FE98", new VehicleType("C81F8757-E6D2-DF2D-1CFE-B72B4F74FE98", "F/A-18E SUPER HORNET", "f-a-18e-super-hornet") );
    	VEHICLES.put("CD8C281F-579D-4E7B-BE3D-F206E91407F8", new VehicleType("CD8C281F-579D-4E7B-BE3D-F206E91407F8", "SU-25TM FROGFOOT", "su-25tm-frogfoot") );
    	VEHICLES.put("D1B516CA-6119-F025-C923-1B0700B6AEBA", new VehicleType("D1B516CA-6119-F025-C923-1B0700B6AEBA", "M1114 HMMWV", "m1114-hmmwv") );
    	VEHICLES.put("D35CA587-79AF-D351-6F65-967794C7F1B7", new VehicleType("D35CA587-79AF-D351-6F65-967794C7F1B7", "CENTURION C-RAM", "centurion-c-ram") );
    	VEHICLES.put("D68E417F-6103-5140-3ABC-4C7505160A09", new VehicleType("D68E417F-6103-5140-3ABC-4C7505160A09", "VDV BUGGY", "vdv-buggy") );
    	VEHICLES.put("D780AFF6-38B7-11DE-BF1C-984D9AEE762C", new VehicleType("D780AFF6-38B7-11DE-BF1C-984D9AEE762C", "Z-11W", "z-11w") );
    	VEHICLES.put("D7BAB9C1-1208-4923-BD3A-56EB945E04E1", new VehicleType("D7BAB9C1-1208-4923-BD3A-56EB945E04E1", "9M133 KORNET LAUNCHER", "9m133-kornet-launcher") );
    	VEHICLES.put("E7A99B55-B5BD-C101-2384-97458D4AC23C", new VehicleType("E7A99B55-B5BD-C101-2384-97458D4AC23C", "GROWLER ITV", "growler-itv") );
    	VEHICLES.put("F1FFAE42-B67A-4E4F-2626-3CBF37AE287B", new VehicleType("F1FFAE42-B67A-4E4F-2626-3CBF37AE287B", "SU-35BM FLANKER-E", "su-35bm-flanker-e") );
    	VEHICLES.put("F998F5E4-220D-463A-A437-1C18D5C3A19E", new VehicleType("F998F5E4-220D-463A-A437-1C18D5C3A19E", "BTR-90", "btr-90") );
    	VEHICLES.put("FD8AB748-FF4D-11DD-A7B1-F7C6DEEC9D32", new VehicleType("FD8AB748-FF4D-11DD-A7B1-F7C6DEEC9D32", "AH-6J LITTLE BIRD", "ah-6j-little-bird") );
    	
    	WEAPONS_SHORT.put("ID_P_WNAME_MP7", "MP7");
    	WEAPONS_SHORT.put("ID_P_WNAME_Glock17", "G17C");
    	WEAPONS_SHORT.put("ID_P_WNAME_M98B", "M98B");
    	WEAPONS_SHORT.put("ID_P_WNAME_553", "SG553");
    	WEAPONS_SHORT.put("ID_P_WNAME_870", "870MCS");
    	WEAPONS_SHORT.put("ID_P_WNAME_MP443SILENCED", "MP443 SUPP.");
    	WEAPONS_SHORT.put("ID_P_WNAME_RPG7", "RPG-7V2");
    	WEAPONS_SHORT.put("ID_P_WNAME_KH2002", "KH2002");
    	WEAPONS_SHORT.put("ID_P_WNAME_AKS74u", "AKS-74u");
    	WEAPONS_SHORT.put("ID_P_WNAME_M1911SILENCED", "M1911 SUPP.");
    	WEAPONS_SHORT.put("ID_P_WNAME_M27", "M27 IAR");
    	WEAPONS_SHORT.put("ID_P_WNAME_G36C", "G36C");
    	WEAPONS_SHORT.put("ID_P_WNAME_Taurus44", ".44 MAGNUM");
    	WEAPONS_SHORT.put("ID_P_WNAME_ASVal", "AS VAL");
    	WEAPONS_SHORT.put("ID_P_WNAME_DAO12", "DAO-12");
    	WEAPONS_SHORT.put("ID_P_WNAME_40MM", "M320");
    	WEAPONS_SHORT.put("ID_P_WNAME_UMP45", "UMP-45");
    	WEAPONS_SHORT.put("ID_P_WNAME_M27", "M27 IAR");
    	WEAPONS_SHORT.put("ID_P_WNAME_IGLA", "SA-18 IGLA");
    	WEAPONS_SHORT.put("ID_P_WNAME_SCARH", "SCAR-H");
    	WEAPONS_SHORT.put("ID_P_WNAME_M412Rex", "MP412 REX");
    	WEAPONS_SHORT.put("ID_P_WNAME_M416", "M416");
    	WEAPONS_SHORT.put("ID_P_WNAME_AK74M", "AK-74M");
    	WEAPONS_SHORT.put("ID_P_WNAME_Pecheng", "PKP PECHENEG");
    	WEAPONS_SHORT.put("ID_P_WNAME_M60", "M60E4");
    	WEAPONS_SHORT.put("ID_P_WNAME_M240", "M240B");
    	WEAPONS_SHORT.put("ID_P_WNAME_M16A4", "M16A3");
    	WEAPONS_SHORT.put("ID_P_WNAME_M1911LIT", "M1911 TACT.");
    	WEAPONS_SHORT.put("ID_P_WNAME_PP2000", "PP-2000");
    	WEAPONS_SHORT.put("ID_P_WNAME_G3", "G3A3");
    	WEAPONS_SHORT.put("ID_P_WNAME_M4", "M4");
    	WEAPONS_SHORT.put("ID_P_WNAME_AEK971", "AEK-971");
    	WEAPONS_SHORT.put("ID_P_WNAME_Mk11", "MK11 MOD 0");
    	WEAPONS_SHORT.put("ID_P_WNAME_AN94", "AN-94");
    	WEAPONS_SHORT.put("ID_P_WNAME_M16", "M16A4");
    	WEAPONS_SHORT.put("ID_P_WNAME_M4A1", "M4A1");
    	WEAPONS_SHORT.put("ID_P_WNAME_Taurus44SCOPED", ".44 SCOPED");
    	WEAPONS_SHORT.put("ID_P_WNAME_USAS12", "USAS-12");
    	WEAPONS_SHORT.put("ID_P_WNAME_MP443", "MP443");
    	WEAPONS_SHORT.put("ID_P_WNAME_RPK", "RPK-74M");
    	WEAPONS_SHORT.put("ID_P_WNAME_AK74M", "AK-74M");
    	WEAPONS_SHORT.put("ID_P_WNAME_knife", "KNIFE");
    	WEAPONS_SHORT.put("ID_P_XP1_WNAME_L85A2", "L85A2");
    	WEAPONS_SHORT.put("ID_P_WNAME_SV98", "SV98");
    	WEAPONS_SHORT.put("ID_P_WNAME_M40A5", "M40A5");
    	WEAPONS_SHORT.put("ID_P_XP1_WNAME_MG36", "MG36");
    	WEAPONS_SHORT.put("ID_P_WNAME_Saiga12", "SAIGA 12K");
    	WEAPONS_SHORT.put("ID_P_XP1_XP1_WNAME_FAMAS", "FAMAS");
    	WEAPONS_SHORT.put("ID_P_WNAME_M1911TACTICAL", "M1911 S-TAC");
    	WEAPONS_SHORT.put("ID_P_WNAME_AKS74u", "AKS-74u");
    	WEAPONS_SHORT.put("ID_P_WNAME_M9SILENCED", "M9 SUPP.");
    	WEAPONS_SHORT.put("ID_P_WNAME_RPK", "RPK-74M");
    	WEAPONS_SHORT.put("ID_P_WNAME_M16A4", "M16A3");
    	WEAPONS_SHORT.put("ID_P_WNAME_M1911", "M1911");
    	WEAPONS_SHORT.put("ID_P_WNAME_M93R", "93R");
    	WEAPONS_SHORT.put("ID_P_WNAME_SVD", "SVD");
    	WEAPONS_SHORT.put("ID_P_XP1_WNAME_JACKHAMMER", "MK3A1");
    	WEAPONS_SHORT.put("ID_P_WNAME_GLOCK18SILENCED", "G18 SUPP.");
    	WEAPONS_SHORT.put("ID_P_WNAME_M249", "M249");
    	WEAPONS_SHORT.put("ID_P_WNAME_GLOCK17SILENCED", "G17C SUPP.");
    	WEAPONS_SHORT.put("ID_P_XP1_WNAME_QBU88", "QBU-88");
    	WEAPONS_SHORT.put("ID_P_WNAME_M9", "M9");
    	WEAPONS_SHORT.put("ID_P_WNAME_M4A1", "M4A1");
    	WEAPONS_SHORT.put("ID_P_WNAME_M26Mass", "M26 MASS");
    	WEAPONS_SHORT.put("ID_P_WNAME_SMAW", "SMAW");
    	WEAPONS_SHORT.put("ID_P_WNAME_P90", "P90");
    	WEAPONS_SHORT.put("ID_P_WNAME_A91", "A-91");
    	WEAPONS_SHORT.put("ID_P_WNAME_Mk11", "MK11 MOD 0");
    	WEAPONS_SHORT.put("ID_P_WNAME_SKS", "SKS");
    	WEAPONS_SHORT.put("ID_P_XP1_WNAME_L96", "L96");
    	WEAPONS_SHORT.put("ID_P_XP1_WNAME_PP19", "PP-19");
    	WEAPONS_SHORT.put("ID_P_WNAME_Type88", "TYPE 88 LMG");
    	WEAPONS_SHORT.put("ID_P_XP1_WNAME_QBZ95B", "QBZ-95B");
    	WEAPONS_SHORT.put("ID_P_WNAME_M9LIT", "M9 TACT.");
    	WEAPONS_SHORT.put("ID_P_WNAME_Glock18", "G18");
    	WEAPONS_SHORT.put("ID_P_WNAME_F2000", "F2000");
    	WEAPONS_SHORT.put("ID_P_WNAME_Javelin", "FGM-148 JAVELIN");
    	WEAPONS_SHORT.put("ID_P_WNAME_MP443LIT", "MP443 TACT.");
    	WEAPONS_SHORT.put("ID_P_XP1_WNAME_HK53", "G53");
    	WEAPONS_SHORT.put("ID_P_WNAME_Stinger", "FIM-92 STINGER");
    	WEAPONS_SHORT.put("ID_P_WNAME_M39", "M39 EMR");
    	WEAPONS_SHORT.put("ID_P_WNAME_M39", "M39 EMR");
    	WEAPONS_SHORT.put("ID_P_WNAME_M1014", "M1014");
    	WEAPONS_SHORT.put("ID_P_WNAME_PDR", "PDW-R");
    	WEAPONS_SHORT.put("ID_P_XP1_WNAME_QBB95", "QBB-95");
    	WEAPONS_SHORT.put("ID_P_WNAME_SVD", "SVD");

		WEAPON_ATTACHMENTS.put("ID_P_ANAME_LASER", "Laser Sight");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_FOREGRIP", "Foregrip");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_RX01", "Reflex ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_FLECHETTE", "12G Flechette");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_MAG", "Extended Mag");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_PKAS", "PKA-S ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_BOLT", "Straight Pull Bolt");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_PKS", "PKS-07 ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_PKA", "PK-A ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_BIPOD", "Bipod");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_PSO", "PSO-1 ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_SLUG", "12G Slug");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_FRAG", "12G Frag");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_NOSECONDARYRAIL", "Underslung Rail");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_SUPPRESS", "Flash Supp");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_SNIPERRIFLE", "Rifle Scope ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_IRNV", "IRNV ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_BUCK", "12G Buckshot");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_LIGHT", "Tactical Light");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_NOPRIMARY", "No Primary");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_NOSECONDARY", "No Secondary");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_BALL", "Ballistic ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_BARREL", "Heavy Barrel");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_M145", "M145 ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_SILENCER", "Suppressor");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_ACOG", "ACOG ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_NOOPTIC", "No Optics");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_RIFLE", "Rifle Scope ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_KOBRA", "KOBRA ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_NOPRIMARYRAIL", "Underslung rail");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_EOTECH", "Holographic");
		WEAPON_ATTACHMENTS.put("FE6292D5-79AD-1230-7B9A-AF80D020E256", "Smoke");

		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVWPNEFF", "Belt speed");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHSTEALTH", "Stealth");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVENVG", "Thermal Optics");
		VEHICLE_ADDONS.put("ID_P_VUNAME_NOPASSIVE", "No upgrade");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETROCKET", "Rocket Pods");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAARMOR", "Reactive Armor");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTCOAX", "Coaxial LMG");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHFIREEX", "Extinguisher");
		VEHICLE_ADDONS.put("ID_P_VUNAME_NOACTIVE", "No gadget");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVSMOKE", "IR Smoke");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETBRADAR", "Below Radar");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHBRADAR", "Below Radar");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTCANISTER", "Canister Shell");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETAA", "Heat Seekers");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETECM", "ECM Jammer");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETSTEALTH", "Stealth");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTSMOKE", "IR Smoke");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AASMOKE", "IR Smoke");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHARADAR", "Air Radar");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETPROX", "Proximity Scan");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHSTEALTH", "Stealth");
		VEHICLE_ADDONS.put("ID_P_VUNAME_NOSTANCE", "No weapon");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AASTEALTH", "Thermal Camo");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHFIREEX", "Extinguisher");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTENVG", "Thermal Optics");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVPREVENT", "Maintenance");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETPREV", "Maintenance");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETFIREEX", "Extinguisher");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHBRADAR", "Below Radar");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVPROX", "Proximity Scan");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHECM", "ECM Jammer");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTWPNEFF", "Autoloader");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTPROX", "Proximity Scan");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVARMOR", "Reactive Armor");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVHELL", "Guided Missile");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTZOOM", "Zoom Optics");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHTVG", "TV Missile");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHZOOM", "Zoom Optics");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVSTEALTH", "Thermal Camo");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETAVIONIC", "Beam Scanning");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVAPFSDS", "APFSDS-T Shell");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHARADAR", "Air Radar");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTSTEALTH", "Thermal Camo");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHECM", "ECM Jammer");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVCOAX", "Coaxial LMG");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHHELL", "Guided Missile");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETMAVER", "Guided Missile");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAENVG", "Thermal Optics");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTCITV", "CITV Station");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHENVG", "Thermal Optics");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAARADAR", "Air Radar");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAAA", "Anti-Air Missile");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHLASER", "Laser Painter");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVZOOM", "Zoom Optics");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHGUIDE", "Guided Rocket");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETARADAR", "Air Radar");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVAT", "ATGM Launcher");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETWPNEFF", "Belt Speed");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAWPNEFF", "Belt Speed");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHPREV", "Maintenance");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHWPNEFF", "Autoloader");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAPREVENT", "Maintenance");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHGHELL", "Guided Missile");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHPROX", "Proximity Scan");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHAA", "Heat Seekers");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTPREVENT", "Maintenance");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHAA", "Heat Seekers");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAPROX", "Proximity Scan");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAZOOM", "Zoom Optics");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETFLARE", "IR Flares");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHFLARE", "IR Flares");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTARMOR", "Reactive Armor");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHPROX", "Proximity Scan");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTHMG", "Coaxial HMG");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHWPNEFF", "Belt Speed");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHLASER", "Laser Painter");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHFLARE", "IR Flares");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTATGM", "Guided Shell");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHPREV", "Maintenance");

		SKILLS.put("BCE7C360-DFE6-4BF0-B020-7F2482177652", "FLAK JACKET");
		SKILLS.put("7B001B2F-5B24-4AE8-9C2E-3B8663E2F6C0", "EXPLOSIVES");
		SKILLS.put("A8C8F62B-0E6C-462B-8489-4A5C46C9170F", "GRENADES");
		SKILLS.put("5F1603F6-C6F5-438D-AF7A-FD45258BB81C", "AMMO (SQUAD)");
		SKILLS.put("9A845313-8450-442C-A30F-FAF5977B3DBA", "SUPPRESION RESIST");
		SKILLS.put("204407B2-2186-4526-A50F-6F423E455311", "SUPPRESSION RESIST (SQUAD)");
		SKILLS.put("CCBBB3DB-F134-4CCA-A2C4-C74CA618C9DF", "AMMO");
		SKILLS.put("B128BA3C-1AFA-4487-84EF-A17F0D4C90EB", "GRENADES (SQUAD)");
		SKILLS.put("4D450662-7615-4F14-9C19-A27A6B45BA93", "SUPRESSION (SQUAD)");
		SKILLS.put("B8CEA0C3-E1FF-47CB-9D00-7022D819F973", "SPRINT (SQUAD)");
		SKILLS.put("3991B59A-8CFB-4590-BAE2-64EED9B4E485", "SUPRESSION");
		SKILLS.put("9E4734B9-D312-4E38-84A7-73E0F1259EEB", "EXPLOSIVES (SQUAD)");
		SKILLS.put("9DFDE21C-A50D-48B7-B6C0-F1E4107D2F12", "FLAK JACKET (SQUAD)");
		SKILLS.put("350432B9-5B42-49F4-8343-CD20DE1B82BD", "SPRINT");
		SKILLS.put("ID_P_SNAME_ExplRes", "FLAK");
		SKILLS.put("ID_P_SNAME_Expl", "EXPL");
		SKILLS.put("ID_P_SNAME_Heal", "HEAL");
		SKILLS.put("ID_P_SNAME_Gren", "FRAG");
		SKILLS.put("ID_P_SNAME_Clips2", "SQD AMMO");
		SKILLS.put("ID_P_SNAME_SuppRes", "COVR");
		SKILLS.put("ID_P_SNAME_SuppRes2", "SQD COVR");
		SKILLS.put("ID_P_SNAME_Clips", "AMMO");
		SKILLS.put("ID_P_SNAME_Gren2", "SQD FRAG");
		SKILLS.put("ID_P_SNAME_Suppr2", "SQD SUPR");
		SKILLS.put("ID_P_SNAME_Sprint2", "SQD SPRNT");
		SKILLS.put("ID_P_SNAME_Suppr", "SUPR");
		SKILLS.put("ID_P_SNAME_Expl2", "SQD EXPL");
		SKILLS.put("ID_P_SNAME_ExplRes2", "SQD FLAK");
		SKILLS.put("ID_P_SNAME_Heal2", "SQD HEAL");
		SKILLS.put("ID_P_SNAME_Sprint", "SPRNT");
		SKILLS.put("ID_P_SNAME_NoSpec", "NONE");
		SKILLS.put("ID_P_SNAME_Heal", "HEAL");
		SKILLS.put("ID_P_SNAME_Heal2", "HEAL (SQUAD)");

		KIT_ITEMS.put("ID_P_INAME_C4", "C4 Explosives");
		KIT_ITEMS.put("ID_P_INAME_SOFLAM", "SOFLAM");
		KIT_ITEMS.put("ID_P_INAME_MEDKIT", "Medic Kit");
		KIT_ITEMS.put("ID_P_INAME_AMMO", "Ammo Box");
		KIT_ITEMS.put("ID_P_INAME_MORTAR", "M224 Mortar");
		KIT_ITEMS.put("ID_P_INAME_UGS", "T-UGS");
		KIT_ITEMS.put("ID_P_INAME_EOD", "EOD Bot");
		KIT_ITEMS.put("ID_P_INAME_DEFIB", "Defibrillator");
		KIT_ITEMS.put("ID_P_INAME_REPAIR", "Repair Tool");
		KIT_ITEMS.put("ID_P_INAME_CLAYMORE", "M18 Claymore");
		KIT_ITEMS.put("ID_P_INAME_FLASHBANG", "M84 Flashbang");
		KIT_ITEMS.put("ID_P_INAME_BEACON", "Radio Beacon");
		KIT_ITEMS.put("ID_P_INAME_SMOKE", "M18 Smoke");
		KIT_ITEMS.put("ID_P_INAME_MINE", "M15 AT Mine");
		KIT_ITEMS.put("ID_P_INAME_MAV", "MAV");
		KIT_ITEMS.put("ID_P_INAME_NOGADGET2", "No Gadget");
		KIT_ITEMS.put("ID_P_INAME_NOGADGET1", "No Gadget");
		KIT_ITEMS.put("ID_P_INAME_M67", "M67 Grenade");
		KIT_ITEMS.put("1122B462-64B1-4AE6-8ED4-CEA3BF1BDFEF", "C4 Explosives");
		KIT_ITEMS.put("13C4927A-C5B3-4075-A570-7FAA6A712C18", "EOD Bot");
		KIT_ITEMS.put("2430C5A8-AB47-406F-B983-1BF7289CF8E6", "T-UGS");
		KIT_ITEMS.put("3EA94E83-0E83-4ABF-9D8E-0B37DA37A243", "MAV");
		KIT_ITEMS.put("4C73E401-E151-459E-BD40-C394437533EA", "M18 Claymore");
		KIT_ITEMS.put("6EF48118-EF16-4D47-BD18-F57792D88AB1", "M224 Mortar");
		KIT_ITEMS.put("7D11603B-8188-45FD-AD95-B27A4B35980E", "Defibrillator");
		KIT_ITEMS.put("90E12AB5-CF0F-4439-AFD0-C86E6C71BB7D", "M15 AT Mine");
		KIT_ITEMS.put("9F789F05-CE7B-DADC-87D7-16E847DBDD09", "M67 Grenade");
		KIT_ITEMS.put("DC9734CD-D3D7-4870-A6A9-07B99BEE6DAC", "SOFLAM");
		
		AWARDS.put("ID_P_AWARD_R04_NAME", "SNIPER RIFLE RIBBON");
		AWARDS.put("ID_P_AWARD_M22_NAME", "AIR WARFARE MEDAL");
		AWARDS.put("ID_P_AWARD_M39_NAME", "M18 CLAYMORE MEDAL");
		AWARDS.put("ID_P_AWARD_M27_NAME", "CONQUEST MEDAL");
		AWARDS.put("ID_P_AWARD_M08_NAME", "MELEE MEDAL");
		AWARDS.put("ID_P_AWARD_R05_NAME", "HAND GUN RIBBON");
		AWARDS.put("ID_P_AWARD_R18_NAME", "MVP 3 RIBBON");
		AWARDS.put("ID_P_AWARD_R06_NAME", "SHOTGUN RIBBON");
		AWARDS.put("ID_P_AWARD_M02_NAME", "CARBINE MEDAL");
		AWARDS.put("ID_P_AWARD_M36_NAME", "SURVEILLANCE MEDAL");
		AWARDS.put("ID_P_AWARD_M11_NAME", "AVENGER MEDAL");
		AWARDS.put("ID_P_AWARD_M14_NAME", "SUPPRESSION MEDAL");
		AWARDS.put("ID_P_AWARD_R21_NAME", "ANTI EXPLOSIVES RIBBON");
		AWARDS.put("ID_P_AWARD_M44_NAME", "ENGINEER SERVICE MEDAL");
		AWARDS.put("ID_P_AWARD_M21_NAME", "ARMORED WARFARE MEDAL");
		AWARDS.put("ID_P_AWARD_R44_NAME", "MEDICAL EFFICIENCY RIBBON");
		AWARDS.put("ID_P_AWARD_M19_NAME", "COMBAT EFFICIENCY MEDAL");
		AWARDS.put("ID_P_AWARD_R19_NAME", "ACE SQUAD RIBBON");
		AWARDS.put("ID_P_AWARD_R23_NAME", "SQUAD WIPE RIBBON");
		AWARDS.put("ID_P_AWARD_M40_NAME", "RADIO BEACON MEDAL");
		AWARDS.put("ID_P_AWARD_M29_NAME", "SQUAD RUSH MEDAL");
		AWARDS.put("ID_P_AWARD_R11_NAME", "ACCURACY RIBBON");
		AWARDS.put("ID_P_AWARD_R07_NAME", "PDW RIBBON");
		AWARDS.put("ID_P_AWARD_R15_NAME", "SUPPRESSION RIBBON");
		AWARDS.put("ID_P_AWARD_M42_NAME", "RU ARMY SERVICE MEDAL");
		AWARDS.put("ID_P_AWARD_M17_NAME", "3RD MVP MEDAL");
		AWARDS.put("ID_P_AWARD_M45_NAME", "SUPPORT SERVICE MEDAL");
		AWARDS.put("ID_P_AWARD_R10_NAME", "ANTI VEHICLE RIBBON");
		AWARDS.put("ID_P_AWARD_M06_NAME", "SHOTGUN MEDAL");
		AWARDS.put("ID_P_AWARD_R33_NAME", "SQUAD RUSH WINNER RIBBON");
		AWARDS.put("ID_P_AWARD_M23_NAME", "STATIONARY EMPLACEMENT MEDAL");
		AWARDS.put("ID_P_AWARD_M46_NAME", "RECON SERVICE MEDAL");
		AWARDS.put("ID_P_AWARD_M01_NAME", "ASSAULT RIFLE MEDAL");
		AWARDS.put("ID_P_AWARD_M09_NAME", "ANTI VEHICLE MEDAL");
		AWARDS.put("ID_P_AWARD_M05_NAME", "HANDGUN MEDAL");
		AWARDS.put("ID_P_AWARD_R39_NAME", "SQUAD DEATHMATCH RIBBON");
		AWARDS.put("ID_P_AWARD_R36_NAME", "CONQUEST RIBBON");
		AWARDS.put("ID_P_AWARD_R29_NAME", "M-COM DEFENDER RIBBON");
		AWARDS.put("ID_P_AWARD_M41_NAME", "US MARINES SERVICE MEDAL");
		AWARDS.put("ID_P_AWARD_R13_NAME", "SAVIOR RIBBON");
		AWARDS.put("ID_P_AWARD_M03_NAME", "LIGHT MACHINE GUN MEDAL");
		AWARDS.put("ID_P_AWARD_M35_NAME", "MEDICAL MEDAL");
		AWARDS.put("ID_P_AWARD_M16_NAME", "2ND MVP MEDAL");
		AWARDS.put("ID_P_AWARD_M34_NAME", "MAINTENANCE MEDAL");
		AWARDS.put("ID_P_AWARD_R31_NAME", "CONQUEST WINNER RIBBON");
		AWARDS.put("ID_P_AWARD_M37_NAME", "MORTAR MEDAL");
		AWARDS.put("ID_P_AWARD_R02_NAME", "CARBINE RIBBON");
		AWARDS.put("ID_P_AWARD_M32_NAME", "FLAG DEFENDER MEDAL");
		AWARDS.put("ID_P_AWARD_M04_NAME", "SNIPER RIFLE MEDAL");
		AWARDS.put("ID_P_AWARD_M20_NAME", "TRANSPORT WARFARE MEDAL");
		AWARDS.put("ID_P_AWARD_M49_NAME", "JET SERVICE MEDAL");
		AWARDS.put("ID_P_AWARD_R41_NAME", "FLAG DEFENDER RIBBON");
		AWARDS.put("ID_P_AWARD_M28_NAME", "TEAM DEATHMATCH MEDAL");
		AWARDS.put("ID_P_AWARD_R16_NAME", "MVP RIBBON");
		AWARDS.put("ID_P_AWARD_M33_NAME", "RESUPPLY MEDAL");
		AWARDS.put("ID_P_AWARD_R30_NAME", "RUSH WINNER RIBBON");
		AWARDS.put("ID_P_AWARD_M07_NAME", "PDW MEDAL");
		AWARDS.put("ID_P_AWARD_R34_NAME", "SQUAD DEATHMATCH WINNER RIBBON");
		AWARDS.put("ID_P_AWARD_M48_NAME", "HELICOPTER SERVICE MEDAL");
		AWARDS.put("ID_P_AWARD_R01_NAME", "ASSAULT RIFLE RIBBON");
		AWARDS.put("ID_P_AWARD_R40_NAME", "FLAG ATTACKER RIBBON");
		AWARDS.put("ID_P_AWARD_M10_NAME", "ACCURACY MEDAL");
		AWARDS.put("ID_P_AWARD_R25_NAME", "ARMORED WARFARE RIBBON");
		AWARDS.put("ID_P_AWARD_R14_NAME", "NEMESIS RIBBON");
		AWARDS.put("ID_P_AWARD_M50_NAME", "STATIONARY SERVICE MEDAL");
		AWARDS.put("ID_P_AWARD_M13_NAME", "NEMESIS MEDAL");
		AWARDS.put("ID_P_AWARD_R28_NAME", "M-COM ATTACKER RIBBON");
		AWARDS.put("ID_P_AWARD_R45_NAME", "SURVEILLANCE EFFICIENCY RIBBON");
		AWARDS.put("ID_P_AWARD_M43_NAME", "ASSAULT SERVICE MEDAL");
		AWARDS.put("ID_P_AWARD_R38_NAME", "SQUAD RUSH RIBBON");
		AWARDS.put("ID_P_AWARD_M25_NAME", "M-COM DEFENDER MEDAL");
		AWARDS.put("ID_P_AWARD_M18_NAME", "ACE SQUAD MEDAL");
		AWARDS.put("ID_P_AWARD_M31_NAME", "FLAG ATTACKER MEDAL");
		AWARDS.put("ID_P_AWARD_M24_NAME", "M-COM ATTACKER MEDAL");
		AWARDS.put("ID_P_AWARD_R27_NAME", "AIR WARFARE RIBBON");
		AWARDS.put("ID_P_AWARD_R37_NAME", "TEAM DEATHMATCH RIBBON");
		AWARDS.put("ID_P_AWARD_R12_NAME", "AVENGER RIBBON");
		AWARDS.put("ID_P_AWARD_R42_NAME", "RESUPPLY EFFICIENCY RIBBON");
		AWARDS.put("ID_P_AWARD_R20_NAME", "COMBAT EFFICIENCY RIBBON");
		AWARDS.put("ID_P_AWARD_R24_NAME", "TRANSPORT WARFARE RIBBON");
		AWARDS.put("ID_P_AWARD_M30_NAME", "SQUAD DEATHMATCH MEDAL");
		AWARDS.put("ID_P_AWARD_R43_NAME", "MAINTENANCE EFFICIENCY RIBBON");
		AWARDS.put("ID_P_AWARD_M47_NAME", "TANK SERVICE MEDAL");
		AWARDS.put("ID_P_AWARD_R08_NAME", "MELEE RIBBON");
		AWARDS.put("ID_P_AWARD_R09_NAME", "DISABLE VEHICLE RIBBON");
		AWARDS.put("ID_P_AWARD_R22_NAME", "SQUAD SPAWN RIBBON");
		AWARDS.put("ID_P_AWARD_R26_NAME", "STATIONARY EMPLACEMENT RIBBON");
		AWARDS.put("ID_P_AWARD_R35_NAME", "RUSH RIBBON");
		AWARDS.put("ID_P_AWARD_R17_NAME", "MVP 2 RIBBON");
		AWARDS.put("ID_P_AWARD_R32_NAME", "TDM WINNER RIBBON");
		AWARDS.put("ID_P_AWARD_M38_NAME", "LASER DESIGNATOR MEDAL");
		AWARDS.put("ID_P_AWARD_M12_NAME", "SAVIOR MEDAL");
		AWARDS.put("ID_P_AWARD_M26_NAME", "RUSH MEDAL");
		AWARDS.put("ID_P_AWARD_R03_NAME", "LIGHT MACHINE GUN RIBBON");
		AWARDS.put("ID_P_AWARD_M15_NAME", "MVP MEDAL");
		AWARDS.put("ID_P_WSTAR_NAME_M39", "M39 EMR SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M60", "M60E4 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M27", "M27 IAR SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M26", "M26 MASS SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_AN94", "AN-94 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_JACKH", "MK3A1 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_KNIFE", "KNIFE SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M1911SILENCED", "M1911 SUPP");
		AWARDS.put("ID_P_WSTAR_NAME_DAO12", "DAO-12 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_G3A4", "G3A3 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_G36C", "G36C SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_PP19", "PP-19 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_L96", "L96 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_QBU88", "QBU-88 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_PP2000", "PP-2000 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_F2000", "F2000 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M1911", "M1911 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_MP7", "MP7 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_SV98", "SV98 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M9SILENCED", "M9 SUPP");
		AWARDS.put("ID_P_WSTAR_NAME_MP443SILENCED", "MP443 SUPP");
		AWARDS.put("ID_P_WSTAR_NAME_RPG7", "RPG-7V2 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M1911TACTICAL", "M1911 S-TAC SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M16A4", "M16 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_QBB95", "QBB-95 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_GLOCK18SILENCED", "G18 SUPP");
		AWARDS.put("ID_P_WSTAR_NAME_M9", "M9 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_FAMAS", "FAMAS SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_PECHENEG", "PKP PECHENEG SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_C4", "C4 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M240", "M240B SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M249", "M249 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M26FRAG", "M26 FRAG SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_QBZ95B", "QBZ-95B SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_USAS12", "USAS-12 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_GLOCK17SILENCED", "G17C SUPP");
		AWARDS.put("ID_P_WSTAR_NAME_870", "870MCS SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M416", "M416 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_HK53", "G53 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M4A1", "M4A1 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_MP443", "MP443 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_MP443LIT", "MP443 TACT");
		AWARDS.put("ID_P_WSTAR_NAME_AK74M", "AK-74M SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_ASVAL", "AS VAL SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_IGLA", "SA-18 IGLA SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_UMP", "UMP-45 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_SMAW", "SMAW SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_SKS", "SKS SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_MINE", "M15 MINE SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_SVD", "SVD SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_SG553", "SG553 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_GLOCK18", "G18 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_GLOCK17", "G17C SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M1014", "M1014 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M320SG", "M320 SHOTGUN SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_L85A2", "L85A2 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M412REX", "M412 REX SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_JAVELIN", "FGM-148 JAVELIN SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M26FLECHETTE", "M26 DART SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_AKS74U", "AKS-74u SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_MG36", "MG36 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_KH2002", "KH2002 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M320HE", "M320 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M1911LIT", "M1911 TACT");
		AWARDS.put("ID_P_WSTAR_NAME_SAIGA", "SAIGA 12K SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_PDR", "PDW-R SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_CLAY", "CLAYMORE SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_SCARH", "SCAR-H SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_STINGER", "FIM-92 STINGER SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_P90", "P90 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_TYPE88", "TYPE 88 LMG SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_MK11", "MK11 MOD 0 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_RPK", "RPK-74M SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M26SLUG", "M26 SLUG SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_AEK971", "AEK-971 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_A91", "A-91 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M40A5", "M40A5 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M93R", "93R SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M98B", "M98B SERVICE STAR");
		AWARDS.put("ID_P_CLASS_STAR_NAME_SHELI", "SCOUT HELICOPTER SERVICE STAR");
		AWARDS.put("ID_P_CLASS_STAR_NAME_AHELI", "ATTACK HELICOPTER SERVICE STAR");
		AWARDS.put("ID_P_CLASS_STAR_NAME_AA", "AA SERVICE STAR");
		AWARDS.put("ID_P_CLASS_STAR_NAME_RECON", "RECON SERVICE STAR");
		AWARDS.put("ID_P_CLASS_STAR_NAME_ASSAULT", "ASSAULT SERVICE STAR");
		AWARDS.put("ID_P_CLASS_STAR_NAME_IFV", "IFV SERVICE STAR");
		AWARDS.put("ID_P_CLASS_STAR_NAME_SUPPORT", "SUPPORT SERVICE STAR");
		AWARDS.put("ID_P_CLASS_STAR_NAME_ENGINEER", "ENGINEER SERVICE STAR");
		AWARDS.put("ID_P_CLASS_STAR_NAME_JET", "JET SERVICE STAR");
		AWARDS.put("ID_P_CLASS_STAR_NAME_MBT", "MBT SERVICE STAR");
		
		COOP_DATA.put("COOP_EXFILTRATION", "Exfiltration");
		COOP_DATA.put("COOP_ASSASSINATION", "Assassination");
		COOP_DATA.put("COOP_MISSION", "CO-OP MISSION ");
		COOP_DATA.put("COOP_HOSTAGE", "Drop 'Em Like Liquid");
		COOP_DATA.put("COOP_CLASSIFIED", "CLASSIFIED");
		COOP_DATA.put("COOP_010", "The Eleventh Hours");
		COOP_DATA.put("COOP_006", "Fire From The Sky");
		COOP_DATA.put("COOP_007", "Operation Exodus");
		COOP_DATA.put("COOP_004", "Rolling Thunder");
		COOP_DATA.put("COOP_005", "Behind Enemy Lines");
		COOP_DATA.put("COOP_008", "Assasination");
		COOP_DATA.put("COOP_009", "Exfiltration");
		COOP_DATA.put("COOP_002", "Hit And Run");
		COOP_DATA.put("COOP_003", "Drop 'Em Like Liquid");
		COOP_DATA.put("COOP_001", "A-10");
		COOP_DATA.put("COOP_EXTRACTION", "Operation Exodus");
		COOP_DATA.put("COOP_SUPERCOBRA", "Fire From The Sky");
		COOP_DATA.put("COOP_A10", "A-10");
		COOP_DATA.put("COOP_BREACHED", "Hit And Run");
		COOP_DATA.put("COOP_ROLLINGTHUNDER", "Rolling Thunder");
		COOP_DATA.put("COOP_BEHINDENEMYLINES", "Behind Enemy Lines");
		COOP_DATA.put("COOP_SUBWAY", "The Eleventh Hour");
		
		ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_2", new String[] { "Professional Russian", "L85A2" });
		ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_3", new String[] { "Fixing it", "G53" });
		ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_1", new String[] { "Best Friend Forever", "FAMAS" });
		ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_6", new String[] { "Keep your head down", "MG36" });
		ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_7", new String[] { "Specops", "QBU-88" });
		ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_4", new String[] { "It goes Boom", "QBZ-95B" });
		ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_5", new String[] { "Let it rain", "QBB-95" });
		ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_9", new String[] { "Familiar Territory", "PP-19" });
		ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_8", new String[] { "Creeping Death", "L96" });
		ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_10",new String[] {  "Scarred Veteran", "MK3A1" });
		
		CRITERIAS.put("ID_XP1_ASSIGNMENT_2_CRITERIA_1", "100 kills with Assault Rifles");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_2_CRITERIA_2", "20 kills with the Noobtube");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_2_CRITERIA_3", "Win 5 rounds of SQDM");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_4_CRITERIA_3", "Win 5 rounds of Conquest");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_4_CRITERIA_1", "50 AT rocket kills");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_4_CRITERIA_2", "Destroy 1 enemy vehicle with Repair Torch");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_8_CRITERIA_3", "5 Knife takedowns");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_8_CRITERIA_1", "50 Headshots");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_8_CRITERIA_2", "50 Spot Assists");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_6_CRITERIA_3", "50 Ammo Resupplies");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_6_CRITERIA_2", "50 Suppression Assists");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_6_CRITERIA_1", "100 kills with Light Machine Guns");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_1_CRITERIA_2", "10 Heals");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_1_CRITERIA_1", "10 Revives");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_3_CRITERIA_1", "10 repairs");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_3_CRITERIA_2", "Kill 1 enemy with Repair Torch");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_10_CRITERIA_4", "Play 2 hours on Sharqi");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_10_CRITERIA_5", "Play 2 hours on Gulf of Oman");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_10_CRITERIA_2", "5 kills in DPV Jeep");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_10_CRITERIA_3", "10 Kills in BTR-90 IFV");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_10_CRITERIA_1", "10 Kills with PP-19");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_7_CRITERIA_2", "5 Laser Designations");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_7_CRITERIA_1", "20 kills with Sniper Rifles");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_5_CRITERIA_2", "2 mortar kills");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_5_CRITERIA_1", "20 kills with Light Machine Guns");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_9_CRITERIA_1", "Arm 10 MCOMs");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_9_CRITERIA_2", "Capture 10 flags");
		CRITERIAS.put("ID_XP1_ASSIGNMENT_9_CRITERIA_3", "Play 2 hours on Karkand");
		
		EXPANSION.put("512", "Back to Karkand");
		
	}

	//Static methods
	public static int getPlatformIdFromName( String p ) {
			
		for( int i = 0, max = PLATFORMS.length; i < max; i++ ) {
			
			if( DataBank.PLATFORMS[i].getName().equals( p ) ) {
				
				return DataBank.PLATFORMS[i].getId();
		
			}
			
		}
		
		return DataBank.PLATFORMS[0].getId();
	}
	
	public static String getPlatformNameFromId( int pId ) {
		
		for( int i = 0, max = PLATFORMS.length; i < max; i++ ) {
			
			if( DataBank.PLATFORMS[i].getId() == pId ) {
				
				return DataBank.PLATFORMS[i].getName();
		
			}
			
		}
		
		return DataBank.PLATFORMS[0].getName();
	}

	public static String getKitTitle( final Context c, final int number ) {
	
		switch( number ) {
	
			case 1:
				return c.getString( R.string.info_xml_assault );
				
			case 2:	
				return c.getString( R.string.info_xml_engineer );
				
			case 8:	
				return c.getString( R.string.info_xml_recon );
				
			case 16: 
				return c.getString( R.string.info_xml_vehicles );
				
			case 32: 
				return c.getString( R.string.info_xml_support );
				
			case 64: 
				return c.getString( R.string.info_xml_general );
				
			default:
				return "";
			
		}
		
	}
	
	public static int getKitIdFromTitle( final Context c, final String s ) {
	
		if( s.equalsIgnoreCase( "assault" ) ) { return 1; }
		else if( s.equalsIgnoreCase( "engineer" ) ) { return 2;	} 
		else if( s.equalsIgnoreCase( "recon" ) ) { return 8;  } 
		else if( s.equalsIgnoreCase( "vehicles" ) ) { return 16; }
		else if( s.equalsIgnoreCase( "support" ) ) { return 32; }
		else if( s.equalsIgnoreCase( "general" ) ) { return 64; }
		else return 64;

	}
	
	public static String getGameModeFromId( int number ) {
	
		switch( number ) {
			
			case 1:
				return "Conquest";
				
			case 2: 
				return "Rush";
				
			case 32:
				return "TDM";
				
			case 64:
				return "Conquest Large";
			
			default:
				return "(unknown game mode)";
		}
		
	}
	
	public static String getRoleFromId( int number ) {

		switch( number ) {
			
			case 0:
				return "Not a member";
				
			case 1: 
				return "Waiting to be evaluated";
				
			case 2:
				return "Invited";
				
			case 4:
				return "Member";
			
			case 128:
				return "Admin";

			case 256:
				return "Founder";
			
			default:
				return "(unknown user role)";
		
		}
		
	}
}