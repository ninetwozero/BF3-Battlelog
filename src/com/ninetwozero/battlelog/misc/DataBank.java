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

import com.ninetwozero.battlelog.datatypes.PlatformData;
import com.ninetwozero.battlelog.datatypes.VehicleType;
import com.ninetwozero.battlelog.datatypes.WeaponType;



public class DataBank {

	//Construct = none
	public DataBank() {}
	
	//Getters
	public static String getRankTitle( String key ) { return RANKS.containsKey( key )? RANKS.get( key ): ""; }
	public static String getWeaponTitle( String key ) { return WEAPONS.containsKey( key )? WEAPONS.get( key ).getName(): ""; }
	public static String getAttachmentTitle( String key ) { return WEAPON_ATTACHMENTS.containsKey( key )? WEAPON_ATTACHMENTS.get( key ): ""; }
	public static String getVehicleTitle( String key ) { return VEHICLES.containsKey( key )? VEHICLES.get( key ).getName(): ""; }
	public static String getVehicleAddon( String key ) { return VEHICLE_ADDONS.containsKey( key )? VEHICLE_ADDONS.get( key ): ""; }
	public static String getSkillTitle( String key ) { return SKILLS.containsKey( key )? SKILLS.get( key ): ""; }
	public static String getKitUnlockTitle( String key ) { return KIT_ITEMS.containsKey( key )? KIT_ITEMS.get( key ): ""; }
	public static String getUnlockGoal( String key ) { return UNLOCK_GOALS.containsKey( key )? UNLOCK_GOALS.get( key ): ""; }

	private static PlatformData[] PLATFORMS;
	private static HashMap<String, String> RANKS;
	private static HashMap<String, VehicleType> VEHICLES;
	private static HashMap<String, String> KIT_ITEMS;
	private static HashMap<String, String> SKILLS;
	private static HashMap<String, String> VEHICLE_ADDONS;
	private static HashMap<String, String> WEAPON_ATTACHMENTS;
	private static HashMap<String, WeaponType> WEAPONS;
	private static HashMap<String, String> UNLOCK_GOALS;
	static {
  
		PLATFORMS = new PlatformData[4];
    	WEAPONS = new HashMap<String, WeaponType>();
    	VEHICLES = new HashMap<String, VehicleType>();
    	RANKS = new HashMap<String, String>();
    	KIT_ITEMS = new HashMap<String, String>();
    	SKILLS = new HashMap<String, String>();
    	VEHICLE_ADDONS = new HashMap<String, String>();
    	WEAPON_ATTACHMENTS = new HashMap<String, String>();
    	UNLOCK_GOALS = new HashMap<String, String>();

    	//PLATFORMS
    	PLATFORMS[0] = new PlatformData(1, "pc");
    	PLATFORMS[1] = new PlatformData(2, "xbox");
    	PLATFORMS[2] = new PlatformData(1, "");
    	PLATFORMS[3] = new PlatformData(4, "ps3");
    	
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
    	
    	//RANKS
    	RANKS.put("ID_P_RANK00_NAME", "RECRUIT");
    	RANKS.put("ID_P_RANK01_NAME", "PRIVATE FIRST CLASS");
    	RANKS.put("ID_P_RANK02_NAME", "PRIVATE FIRST CLASS 1 STAR");
    	RANKS.put("ID_P_RANK03_NAME", "PRIVATE FIRST CLASS 2 STAR");
    	RANKS.put("ID_P_RANK04_NAME", "PRIVATE FIRST CLASS 3 STAR");
    	RANKS.put("ID_P_RANK05_NAME", "LANCE CORPORAL");
    	RANKS.put("ID_P_RANK06_NAME", "LANCE CORPORAL 1 STAR");
    	RANKS.put("ID_P_RANK07_NAME", "LANCE CORPORAL 2 STAR");
    	RANKS.put("ID_P_RANK08_NAME", "LANCE CORPORAL 3 STAR");
    	RANKS.put("ID_P_RANK09_NAME", "CORPORAL");
    	RANKS.put("ID_P_RANK10_NAME", "CORPORAL 1 STAR");
    	RANKS.put("ID_P_RANK11_NAME", "CORPORAL 2 STAR");
    	RANKS.put("ID_P_RANK12_NAME", "CORPORAL 3 STAR");
    	RANKS.put("ID_P_RANK13_NAME", "SERGEANT");
    	RANKS.put("ID_P_RANK14_NAME", "SERGEANT 1 STAR");
    	RANKS.put("ID_P_RANK15_NAME", "SERGEANT 2 STAR");
    	RANKS.put("ID_P_RANK16_NAME", "SERGEANT 3 STAR");
    	RANKS.put("ID_P_RANK17_NAME", "STAFF SERGEANT");
    	RANKS.put("ID_P_RANK18_NAME", "STAFF SERGEANT 1 STAR");
    	RANKS.put("ID_P_RANK19_NAME", "STAFF SERGEANT 2 STAR");
    	RANKS.put("ID_P_RANK20_NAME", "GUNNERY SERGEANT");
    	RANKS.put("ID_P_RANK21_NAME", "GUNNERY SERGEANT 1 STAR");
    	RANKS.put("ID_P_RANK22_NAME", "GUNNERY SERGEANT 2 STAR");
    	RANKS.put("ID_P_RANK23_NAME", "MASTER SERGEANT");
    	RANKS.put("ID_P_RANK24_NAME", "MASTER SERGEANT 1 STAR");
    	RANKS.put("ID_P_RANK25_NAME", "MASTER SERGEANT 2 STAR");
    	RANKS.put("ID_P_RANK26_NAME", "FIRST SERGEANT");
    	RANKS.put("ID_P_RANK27_NAME", "FIRST SERGEANT 1 STAR");
    	RANKS.put("ID_P_RANK28_NAME", "FIRST SERGEANT 2 STAR");
    	RANKS.put("ID_P_RANK29_NAME", "MASTER GUNNERY SERGEANT");
    	RANKS.put("ID_P_RANK30_NAME", "MASTER GUNNERY SERGEANT 1 STAR");
    	RANKS.put("ID_P_RANK31_NAME", "MASTER GUNNERY SERGEANT 2 STAR");
    	RANKS.put("ID_P_RANK32_NAME", "SERGEANT MAJOR");
    	RANKS.put("ID_P_RANK33_NAME", "SERGEANT MAJOR 1 STAR");
    	RANKS.put("ID_P_RANK34_NAME", "SERGEANT MAJOR 2 STAR");
    	RANKS.put("ID_P_RANK35_NAME", "WARRANT OFFICER ONE");
    	RANKS.put("ID_P_RANK36_NAME", "CHIEF WARRANT OFFICER TWO");
    	RANKS.put("ID_P_RANK37_NAME", "CHIEF WARRANT OFFICER THREE");
    	RANKS.put("ID_P_RANK38_NAME", "CHIEF WARRANT OFFICER FOUR");
    	RANKS.put("ID_P_RANK39_NAME", "CHIEF WARRANT OFFICER FIVE");
    	RANKS.put("ID_P_RANK40_NAME", "SECOND LIEUTENANT");
    	RANKS.put("ID_P_RANK41_NAME", "FIRST LIEUTENANT");
    	RANKS.put("ID_P_RANK42_NAME", "CAPTAIN");
    	RANKS.put("ID_P_RANK43_NAME", "MAJOR");
    	RANKS.put("ID_P_RANK44_NAME", "LIEUTENANT COLONEL");
    	RANKS.put("ID_P_RANK45_NAME", "COLONEL");
    	RANKS.put("ID_P_RANK46_NAME", "COLONEL SERVICE STAR 1");
    	RANKS.put("ID_P_RANK47_NAME", "COLONEL SERVICE STAR 2");
    	RANKS.put("ID_P_RANK48_NAME", "COLONEL SERVICE STAR 3");
    	RANKS.put("ID_P_RANK49_NAME", "COLONEL SERVICE STAR 4");
    	RANKS.put("ID_P_RANK50_NAME", "COLONEL SERVICE STAR 5");
    	RANKS.put("ID_P_RANK51_NAME", "COLONEL SERVICE STAR 6");
    	RANKS.put("ID_P_RANK52_NAME", "COLONEL SERVICE STAR 7");
    	RANKS.put("ID_P_RANK53_NAME", "COLONEL SERVICE STAR 8");
    	RANKS.put("ID_P_RANK54_NAME", "COLONEL SERVICE STAR 9");
    	RANKS.put("ID_P_RANK55_NAME", "COLONEL SERVICE STAR 10");
    	RANKS.put("ID_P_RANK56_NAME", "COLONEL SERVICE STAR 11");
    	RANKS.put("ID_P_RANK57_NAME", "COLONEL SERVICE STAR 12");
    	RANKS.put("ID_P_RANK58_NAME", "COLONEL SERVICE STAR 13");
    	RANKS.put("ID_P_RANK59_NAME", "COLONEL SERVICE STAR 14");
    	RANKS.put("ID_P_RANK60_NAME", "COLONEL SERVICE STAR 15");
    	RANKS.put("ID_P_RANK61_NAME", "COLONEL SERVICE STAR 16");
    	RANKS.put("ID_P_RANK62_NAME", "COLONEL SERVICE STAR 17");
    	RANKS.put("ID_P_RANK63_NAME", "COLONEL SERVICE STAR 18");
    	RANKS.put("ID_P_RANK64_NAME", "COLONEL SERVICE STAR 19");
    	RANKS.put("ID_P_RANK65_NAME", "COLONEL SERVICE STAR 20");
    	RANKS.put("ID_P_RANK66_NAME", "COLONEL SERVICE STAR 21");
    	RANKS.put("ID_P_RANK67_NAME", "COLONEL SERVICE STAR 22");
    	RANKS.put("ID_P_RANK68_NAME", "COLONEL SERVICE STAR 23");
    	RANKS.put("ID_P_RANK69_NAME", "COLONEL SERVICE STAR 24");
    	RANKS.put("ID_P_RANK70_NAME", "COLONEL SERVICE STAR 25");
    	RANKS.put("ID_P_RANK71_NAME", "COLONEL SERVICE STAR 26");
    	RANKS.put("ID_P_RANK72_NAME", "COLONEL SERVICE STAR 27");
    	RANKS.put("ID_P_RANK73_NAME", "COLONEL SERVICE STAR 28");
    	RANKS.put("ID_P_RANK74_NAME", "COLONEL SERVICE STAR 29");
    	RANKS.put("ID_P_RANK75_NAME", "COLONEL SERVICE STAR 30");
    	RANKS.put("ID_P_RANK76_NAME", "COLONEL SERVICE STAR 31");
    	RANKS.put("ID_P_RANK77_NAME", "COLONEL SERVICE STAR 32");
    	RANKS.put("ID_P_RANK78_NAME", "COLONEL SERVICE STAR 33");
    	RANKS.put("ID_P_RANK79_NAME", "COLONEL SERVICE STAR 34");
    	RANKS.put("ID_P_RANK80_NAME", "COLONEL SERVICE STAR 35");
    	RANKS.put("ID_P_RANK81_NAME", "COLONEL SERVICE STAR 36");
    	RANKS.put("ID_P_RANK82_NAME", "COLONEL SERVICE STAR 37");
    	RANKS.put("ID_P_RANK83_NAME", "COLONEL SERVICE STAR 38");
    	RANKS.put("ID_P_RANK84_NAME", "COLONEL SERVICE STAR 39");
    	RANKS.put("ID_P_RANK85_NAME", "COLONEL SERVICE STAR 40");
    	RANKS.put("ID_P_RANK86_NAME", "COLONEL SERVICE STAR 41");
    	RANKS.put("ID_P_RANK87_NAME", "COLONEL SERVICE STAR 42");
    	RANKS.put("ID_P_RANK88_NAME", "COLONEL SERVICE STAR 43");
    	RANKS.put("ID_P_RANK89_NAME", "COLONEL SERVICE STAR 44");
    	RANKS.put("ID_P_RANK90_NAME", "COLONEL SERVICE STAR 45");
    	RANKS.put("ID_P_RANK91_NAME", "COLONEL SERVICE STAR 46");
    	RANKS.put("ID_P_RANK92_NAME", "COLONEL SERVICE STAR 47");
    	RANKS.put("ID_P_RANK93_NAME", "COLONEL SERVICE STAR 48");
    	RANKS.put("ID_P_RANK94_NAME", "COLONEL SERVICE STAR 49");
    	RANKS.put("ID_P_RANK95_NAME", "COLONEL SERVICE STAR 50");
    	RANKS.put("ID_P_RANK96_NAME", "COLONEL SERVICE STAR 51");
    	RANKS.put("ID_P_RANK97_NAME", "COLONEL SERVICE STAR 52");
    	RANKS.put("ID_P_RANK98_NAME", "COLONEL SERVICE STAR 53");
    	RANKS.put("ID_P_RANK99_NAME", "COLONEL SERVICE STAR 54");
    	RANKS.put("ID_P_RANK100_NAME", "COLONEL SERVICE STAR 55");
    	RANKS.put("ID_P_RANK101_NAME", "COLONEL SERVICE STAR 56");
    	RANKS.put("ID_P_RANK102_NAME", "COLONEL SERVICE STAR 57");
    	RANKS.put("ID_P_RANK103_NAME", "COLONEL SERVICE STAR 58");
    	RANKS.put("ID_P_RANK104_NAME", "COLONEL SERVICE STAR 59");
    	RANKS.put("ID_P_RANK105_NAME", "COLONEL SERVICE STAR 60");
    	RANKS.put("ID_P_RANK106_NAME", "COLONEL SERVICE STAR 61");
    	RANKS.put("ID_P_RANK107_NAME", "COLONEL SERVICE STAR 62");
    	RANKS.put("ID_P_RANK108_NAME", "COLONEL SERVICE STAR 63");
    	RANKS.put("ID_P_RANK109_NAME", "COLONEL SERVICE STAR 64");
    	RANKS.put("ID_P_RANK110_NAME", "COLONEL SERVICE STAR 65");
    	RANKS.put("ID_P_RANK111_NAME", "COLONEL SERVICE STAR 66");
    	RANKS.put("ID_P_RANK112_NAME", "COLONEL SERVICE STAR 67");
    	RANKS.put("ID_P_RANK113_NAME", "COLONEL SERVICE STAR 68");
    	RANKS.put("ID_P_RANK114_NAME", "COLONEL SERVICE STAR 69");
    	RANKS.put("ID_P_RANK115_NAME", "COLONEL SERVICE STAR 70");
    	RANKS.put("ID_P_RANK116_NAME", "COLONEL SERVICE STAR 71");
    	RANKS.put("ID_P_RANK117_NAME", "COLONEL SERVICE STAR 72");
    	RANKS.put("ID_P_RANK118_NAME", "COLONEL SERVICE STAR 73");
    	RANKS.put("ID_P_RANK119_NAME", "COLONEL SERVICE STAR 74");
    	RANKS.put("ID_P_RANK120_NAME", "COLONEL SERVICE STAR 75");
    	RANKS.put("ID_P_RANK121_NAME", "COLONEL SERVICE STAR 76");
    	RANKS.put("ID_P_RANK122_NAME", "COLONEL SERVICE STAR 77");
    	RANKS.put("ID_P_RANK123_NAME", "COLONEL SERVICE STAR 78");
    	RANKS.put("ID_P_RANK124_NAME", "COLONEL SERVICE STAR 79");
    	RANKS.put("ID_P_RANK125_NAME", "COLONEL SERVICE STAR 80");
    	RANKS.put("ID_P_RANK126_NAME", "COLONEL SERVICE STAR 81");
    	RANKS.put("ID_P_RANK127_NAME", "COLONEL SERVICE STAR 82");
    	RANKS.put("ID_P_RANK128_NAME", "COLONEL SERVICE STAR 83");
    	RANKS.put("ID_P_RANK129_NAME", "COLONEL SERVICE STAR 84");
    	RANKS.put("ID_P_RANK130_NAME", "COLONEL SERVICE STAR 85");
    	RANKS.put("ID_P_RANK131_NAME", "COLONEL SERVICE STAR 86");
    	RANKS.put("ID_P_RANK132_NAME", "COLONEL SERVICE STAR 87");
    	RANKS.put("ID_P_RANK133_NAME", "COLONEL SERVICE STAR 88");
    	RANKS.put("ID_P_RANK134_NAME", "COLONEL SERVICE STAR 89");
    	RANKS.put("ID_P_RANK135_NAME", "COLONEL SERVICE STAR 90");
    	RANKS.put("ID_P_RANK136_NAME", "COLONEL SERVICE STAR 91");
    	RANKS.put("ID_P_RANK137_NAME", "COLONEL SERVICE STAR 92");
    	RANKS.put("ID_P_RANK138_NAME", "COLONEL SERVICE STAR 93");
    	RANKS.put("ID_P_RANK139_NAME", "COLONEL SERVICE STAR 94");
    	RANKS.put("ID_P_RANK140_NAME", "COLONEL SERVICE STAR 95");
    	RANKS.put("ID_P_RANK141_NAME", "COLONEL SERVICE STAR 96");
    	RANKS.put("ID_P_RANK142_NAME", "COLONEL SERVICE STAR 97");
    	RANKS.put("ID_P_RANK143_NAME", "COLONEL SERVICE STAR 98");
    	RANKS.put("ID_P_RANK144_NAME", "COLONEL SERVICE STAR 99");
    	RANKS.put("ID_P_RANK145_NAME", "COLONEL SERVICE STAR 100");

		VEHICLES.put("trITV", new VehicleType("trITV", "GROWLER ITV", "Vehicle Jeep Growler ITV"));
		VEHICLES.put("shAH6", new VehicleType("shAH6", "AH-6J LITTLE BIRD", "Vehicle Air Helicopter Scout AH6"));
		VEHICLES.put("mbtT90", new VehicleType("mbtT90", "T-90A", "Vehicle MBT T90"));
		VEHICLES.put("ifvBMP", new VehicleType("ifvBMP", "BMP-2M", "Vehicle IFV BMP"));
		VEHICLES.put("trVDV", new VehicleType("trVDV", "VDV BUGGY", "Vehicle Jeep VDV"));
		VEHICLES.put("atTOW", new VehicleType("atTOW", "M220 TOW LAUNCHER", "Stationary AT TOW"));
		VEHICLES.put("saaCRAM", new VehicleType("saaCRAM", "CENTURION C-RAM", "Stationary AA Centurion C-RAM"));
		VEHICLES.put("trHum", new VehicleType("trHum", "M1114 HMMWV", "Vehicle Jeep Humvee"));
		VEHICLES.put("saaPant", new VehicleType("saaPant", "PANTSIR-S1", "Stationary AA Pantsir-S1"));
		VEHICLES.put("sKorn", new VehicleType("sKorn", "9M133 KORNET LAUNCHER", "Stationary AT Kornet"));
		VEHICLES.put("trRIB", new VehicleType("trRIB", "RHIB BOAT", "Vehicle Transport RIB Boat"));
		VEHICLES.put("ahAH1Z", new VehicleType("ahAH1Z", "AH-1Z VIPER", "Vehicle Air Helicopter Attack AH1Z"));
		VEHICLES.put("trUH1", new VehicleType("trUH1", "UH-1Y VENOM", "Vehicle Transport UH-1Y Venom"));
		VEHICLES.put("aaLAV", new VehicleType("aaLAV", "LAV-AD", "Vehicle AA LAV-AD"));
		VEHICLES.put("trVod", new VehicleType("trVod", "GAZ-3937 VODNIK", "Vehicle Jeep Vodnik"));
		VEHICLES.put("seqM224", new VehicleType("seqM224", "M224 MORTAR", "M224 Mortar"));
		VEHICLES.put("jaA10", new VehicleType("jaA10", "A-10 THUNDERBOLT", "Vehicle Air Jet Attack A10"));
		VEHICLES.put("jaSU25", new VehicleType("jaSU25", "SU-25TM FROGFOOT", "Vehicle Air Jet Attack SU25"));
		VEHICLES.put("ahMi28", new VehicleType("ahMi28", "MI-28 HAVOC", "Vehicle Air Helicopter Attack Mi28"));
		VEHICLES.put("aa9k22", new VehicleType("aa9k22", "9K22 TUNGUSKA-M", "Vehicle AA 9K22 Tunguska"));
		VEHICLES.put("trKA60", new VehicleType("trKA60", "KA-60 KASATKA", "Vehicle Transport KA-60"));
		VEHICLES.put("mbtM1A", new VehicleType("mbtM1A", "M1 ABRAMS", "Vehicle MBT M1 Abrams"));
		VEHICLES.put("shz11", new VehicleType("shz11", "Z-11W", "Vehicle Air Helicopter Scout z11"));
		VEHICLES.put("jfF18", new VehicleType("jfF18", "F/A-18E SUPER HORNET", "Vehicle Air Jet Fighter F18"));
		VEHICLES.put("jfSu35", new VehicleType("jfSu35", "SU-35BM FLANKER-E", "Vehicle Air Jet Fighter SU35"));
		VEHICLES.put("seqEOD", new VehicleType("seqEOD", "EOD BOT", "EODBot"));
		VEHICLES.put("trAAV", new VehicleType("trAAV", "AAV-7A1 AMTRAC", "Vehicle Transport AAV-7A1"));
		VEHICLES.put("ifvLAV", new VehicleType("ifvLAV", "LAV-25", "Vehicle IFV LAV-25"));

		WEAPONS.put("smP90", new WeaponType("smP90", "P90", "P90", 900, "SHORT", "50 [5.7x28mm]", true, false, true));
		WEAPONS.put("pM1911", new WeaponType("pM1911", "M1911", "M1911", "SEMIAUTO", "SHORT", "9 [.45ACP]", false, false, true));
		WEAPONS.put("pM1911L", new WeaponType("pM1911L", "M1911 TACT.", "M1911 LIT", "SEMIAUTO", "SHORT", "9 [.45ACP]", false, false, true));
		WEAPONS.put("pMP443L", new WeaponType("pMP443L", "MP443 TACT.", "MP443 LIT", "SEMIAUTO", "SHORT", "18 [9x19mm Parabellum]", false, false, true));
		WEAPONS.put("smUMP", new WeaponType("smUMP", "UMP-45", "UMP", 600, "SHORT", "25 [.45ACP]", true, true, true));
		WEAPONS.put("sgM1014", new WeaponType("sgM1014", "M1014", "M1014", "SEMIAUTO", "SHORT", "ID_P_AMMO_M1014", false, false, true));
		WEAPONS.put("caM4", new WeaponType("caM4", "M4A1", "M4A1", 800, "MEDIUM", "30 [5.56x45mm NATO]", true, false, true));
		WEAPONS.put("pM1911T", new WeaponType("pM1911T", "M1911 S-TAC", "M1911 Tactical", "SEMIAUTO", "SHORT", "9 [.45ACP]", false, false, true));
		WEAPONS.put("mgT88", new WeaponType("mgT88", "TYPE 88 LMG", "Type88", 700, "MEDIUM", "200 [5.8x42mm DAP-87]", true, false, false));
		WEAPONS.put("ID_P_WNAME_M4", new WeaponType("ID_P_WNAME_M4", "M4", "M4A1", 800, "MEDIUM", "30 [5.56x45mm NATO]", false, true, true));
		WEAPONS.put("smVAL", new WeaponType("smVAL", "AS VAL", "AS-VAL", 900, "SHORT", "30 [9x39mm]", true, false, true));
		WEAPONS.put("wLATJAV", new WeaponType("wLATJAV", "FGM-148 JAVELIN", "FGM-148 JAVELIN", "SINGLE SHOT", "MEDIUM", "ID_P_AMMO_JAVELIN", false, false, true));
		WEAPONS.put("pM1911S", new WeaponType("pM1911S", "M1911 SUPP.", "M1911 SILENCED", "SEMIAUTO", "SHORT", "9 [.45ACP]", false, false, true));
		WEAPONS.put("pg18S", new WeaponType("pg18S", "G18 SUPP.", "Glock 18 Silenced", 1100, "SHORT", "20 [9x19mm Parabellum]", true, false, true));
		WEAPONS.put("smMP7", new WeaponType("smMP7", "MP7", "MP7", 950, "SHORT", "20 [4.6x30mm]", true, false, true));
		WEAPONS.put("pG17S", new WeaponType("pG17S", "G17C SUPP.", "Glock 17 Silenced", "SEMIAUTO", "SHORT", "17 [9x19mm Parabellum]", false, false, true));
		WEAPONS.put("srSKS", new WeaponType("srSKS", "SKS", "SKS", "SEMIAUTO", "VERY LONG", "10 [7.62x39mm WP]", false, false, true));
		WEAPONS.put("ID_P_WNAME_M16A4", new WeaponType("ID_P_WNAME_M16A4", "M16A3", "M16A4", 800, "LONG", "30 [5.56x45mm NATO]", true, true, true));
		WEAPONS.put("pTaur", new WeaponType("pTaur", ".44 MAGNUM", "Taurus 44", "SEMIAUTO", "SHORT", "ID_P_AMMO_T44", false, false, true));
		WEAPONS.put("ID_P_WNAME_AK74M", new WeaponType("ID_P_WNAME_AK74M", "AK-74M", "AK74M", 650, "LONG", "30 [5.45x39mm WP]", true, false, true));
		WEAPONS.put("srSVD", new WeaponType("srSVD", "SVD", "SVD", "SEMIAUTO", "VERY LONG", "10 [7.62x54mm R]", false, false, true));
		WEAPONS.put("sg870", new WeaponType("sg870", "870MCS", "870", "PUMP ACTION", "SHORT", "ID_P_AMMO_870", false, false, true));
		WEAPONS.put("pM412", new WeaponType("pM412", "MP412 REX", "M412 Rex", "SEMIAUTO", "SHORT", "ID_P_AMMO_REX", false, false, true));
		WEAPONS.put("ID_P_WNAME_M4A1", new WeaponType("ID_P_WNAME_M4A1", "M4A1", "M4A1", 800, "MEDIUM", "30 [5.56x45mm NATO]", true, true, true));
		WEAPONS.put("arG3", new WeaponType("arG3", "G3A3", "G3A4", 500, "LONG", "20 [7.62x51mm NATO]", true, false, true));
		WEAPONS.put("ID_P_WNAME_Mk11", new WeaponType("ID_P_WNAME_Mk11", "MK11 MOD 0", "MK11", "SEMIAUTO", "VERY LONG", "10 [7.62x51mm NATO]", false, false, true));
		WEAPONS.put("arAK74", new WeaponType("arAK74", "AK-74M", "AK74M", 650, "LONG", "30 [5.45x39mm WP]", true, false, true));
		WEAPONS.put("pG17", new WeaponType("pG17", "G17C", "Glock 17", "SEMIAUTO", "SHORT", "17 [9x19mm Parabellum]", false, false, true));
		WEAPONS.put("pMP443", new WeaponType("pMP443", "MP443", "MP 443", "SEMIAUTO", "SHORT", "18 [9x19mm Parabellum]", false, false, true));
		WEAPONS.put("pMP443S", new WeaponType("pMP443S", "MP443 SUPP.", "MP443 Silenced", "SEMIAUTO", "SHORT", "18 [9x19mm Parabellum]", false, false, true));
		WEAPONS.put("ID_P_WNAME_M27", new WeaponType("ID_P_WNAME_M27", "M27 IAR", "M27", 750, "MEDIUM", "45 [5.56x45mm NATO]", true, false, true));
		WEAPONS.put("mgM240", new WeaponType("mgM240", "M240B", "M240", 750, "MEDIUM", "100 [7.62x51mm NATO]", true, false, false));
		WEAPONS.put("wLATSMAW", new WeaponType("wLATSMAW", "SMAW", "Mk153 SMAW", "SINGLE SHOT", "MEDIUM", "ID_P_AMMO_SMAW", false, false, true));
		WEAPONS.put("pTaurS", new WeaponType("pTaurS", ".44 SCOPED", "Taurus 44 scoped", "SEMIAUTO", "LONG", "ID_P_AMMO_T44", false, false, true));
		WEAPONS.put("caSG553", new WeaponType("caSG553", "SG553", "SG553", 700, "MEDIUM", "30 [5.56x45mm NATO]", true, true, true));
		WEAPONS.put("pM93R", new WeaponType("pM93R", "93R", "M93R", 1100, "SHORT", "20 [9x19mm Parabellum]", false, true, true));
		WEAPONS.put("mgM249", new WeaponType("mgM249", "M249", "M249", 800, "MEDIUM", "200 [5.56x45mm NATO]", true, false, false));
		WEAPONS.put("pg18", new WeaponType("pg18", "G18", "Glock 18", 1100, "SHORT", "20 [9x19mm Parabellum]", true, false, true));
		WEAPONS.put("srMK11", new WeaponType("srMK11", "MK11 MOD 0", "MK11", "SEMIAUTO", "VERY LONG", "10 [7.62x51mm NATO]", false, false, true));
		WEAPONS.put("sgUSAS", new WeaponType("sgUSAS", "USAS-12", "USAS", 300, "SHORT", "ID_P_AMMO_USAS12", false, false, true));
		WEAPONS.put("srM40", new WeaponType("srM40", "M40A5", "M40A5", "BOLT ACTION", "VERY LONG", "11 [7.62x51mm NATO]", false, false, true));
		WEAPONS.put("caSCAR", new WeaponType("caSCAR", "SCAR-H", "SCAR", 600, "MEDIUM", "20 [7.62x51mm NATO]", true, false, true));
		WEAPONS.put("pM9F", new WeaponType("pM9F", "M9 TACT.", "M9 Flashlight", "SEMIAUTO", "SHORT", "15 [9x19mm Parabellum]", false, false, true));
		WEAPONS.put("mgM27", new WeaponType("mgM27", "M27 IAR", "M27", 750, "MEDIUM", "45 [5.56x45mm NATO]", true, false, true));
		WEAPONS.put("wasK", new WeaponType("wasK", "KNIFE", "Knife", "", "", "None", false, false, false));
		WEAPONS.put("smPDR", new WeaponType("smPDR", "PDW-R", "PDR", 750, "SHORT", "30 [5.56x45mm NATO]", true, false, true));
		WEAPONS.put("smPP2000", new WeaponType("smPP2000", "PP-2000", "PP2000", 600, "SHORT", "20 [9x19mm Parabellum]", true, false, true));
		WEAPONS.put("wahUGL", new WeaponType("wahUGL", "M320", "Underslung Launcher", "SINGLE SHOT", "MEDIUM", "1 [40mm]", false, false, true));
		WEAPONS.put("wLAAIGL", new WeaponType("wLAAIGL", "SA-18 IGLA", "SA-18 IGLA AA", "SINGLE SHOT", "LONG", "ID_P_AMMO_IGLA", false, false, true));
		WEAPONS.put("sgSaiga", new WeaponType("sgSaiga", "SAIGA 12K", "Saiga", "SEMIAUTO", "SHORT", "ID_P_AMMO_SAIGA12", false, false, true));
		WEAPONS.put("pM9", new WeaponType("pM9", "M9", "M9", "SEMIAUTO", "SHORT", "15 [9x19mm Parabellum]", false, false, true));
		WEAPONS.put("arM16", new WeaponType("arM16", "M16A3", "M16A4", 800, "LONG", "30 [5.56x45mm NATO]", true, false, true));
		WEAPONS.put("ID_P_WNAME_RPK", new WeaponType("ID_P_WNAME_RPK", "RPK-74M", "RPK", 600, "MEDIUM", "45 [5.45x39mm WP]", true, false, true));
		WEAPONS.put("caAKS", new WeaponType("caAKS", "AKS-74u", "AKS74U", 650, "MEDIUM", "30 [5.45x39mm WP]", true, false, true));
		WEAPONS.put("caG36", new WeaponType("caG36", "G36C", "G36C", 750, "MEDIUM", "30 [5.56x45mm NATO]", true, true, true));
		WEAPONS.put("mgPech", new WeaponType("mgPech", "PKP PECHENEG", "Pecheneg", 650, "MEDIUM", "100 [7.62x54mm R]", true, false, false));
		WEAPONS.put("mgRPK", new WeaponType("mgRPK", "RPK-74M", "RPK", 600, "MEDIUM", "45 [5.45x39mm WP]", true, false, true));
		WEAPONS.put("pM9S", new WeaponType("pM9S", "M9 SUPP.", "M9 Silenced", "SEMIAUTO", "SHORT", "15 [9x19mm Parabellum]", false, false, true));
		WEAPONS.put("wahUSG", new WeaponType("wahUSG", "M26 MASS", "Underslung Shotgun", "SEMIAUTO", "SHORT", "ID_P_AMMO_M26", false, false, true));
		WEAPONS.put("arAN94", new WeaponType("arAN94", "AN-94", "AN94", 600, "LONG", "30 [5.45x39mm WP]", true, true, false));
		WEAPONS.put("caA91", new WeaponType("caA91", "A-91", "A91", 800, "MEDIUM", "30 [7.62x39mm WP]", true, false, true));
		WEAPONS.put("ID_P_WNAME_SVD", new WeaponType("ID_P_WNAME_SVD", "SVD", "SVD", "SEMIAUTO", "VERY LONG", "10 [7.62x54mm R]", false, false, true));
		WEAPONS.put("arKH", new WeaponType("arKH", "KH2002", "KH2002", 850, "LONG", "30 [5.56x45mm NATO]", true, true, true));
		WEAPONS.put("mgM60", new WeaponType("mgM60", "M60E4", "M60", 500, "MEDIUM", "100 [7.62x51mm NATO]", true, false, false));
		WEAPONS.put("arF2", new WeaponType("arF2", "F2000", "F2000", 850, "LONG", "30 [5.56x45mm NATO]", true, false, true));
		WEAPONS.put("ID_P_WNAME_AKS74u", new WeaponType("ID_P_WNAME_AKS74u", "AKS-74u", "AKS74U", 650, "MEDIUM", "30 [5.45x39mm WP]", true, false, true));
		WEAPONS.put("wLATRPG", new WeaponType("wLATRPG", "RPG-7V2", "RPG-7", "SINGLE SHOT", "MEDIUM", "ID_P_AMMO_RPG7", false, false, true));
		WEAPONS.put("arM416", new WeaponType("arM416", "M416", "M416", 750, "LONG", "30 [5.56x45mm NATO]", true, false, true));
		WEAPONS.put("wLAAFIM", new WeaponType("wLAAFIM", "FIM-92 STINGER", "FIM-92 STINGER AA", "SINGLE SHOT", "LONG", "ID_P_AMMO_STINGER", false, false, true));
		WEAPONS.put("srSV98", new WeaponType("srSV98", "SV98", "SV98", "BOLT ACTION", "VERY LONG", "10 [7.62x54mm R]", false, false, true));
		WEAPONS.put("ID_P_WNAME_M16", new WeaponType("ID_P_WNAME_M16", "M16A4", "M16A4", 800, "LONG", "30 [5.56x45mm NATO]", false, true, true));
		WEAPONS.put("arAEK", new WeaponType("arAEK", "AEK-971", "AEK971", 750, "LONG", "30 [5.45x39mm WP]", true, false, true));
		WEAPONS.put("srM39", new WeaponType("srM39", "M39 EMR", "M39", "SEMIAUTO", "VERY LONG", "20 [7.62x51mm NATO]", false, false, true));
		WEAPONS.put("sgDAO", new WeaponType("sgDAO", "DAO-12", "DAO", "SEMIAUTO", "SHORT", "ID_P_AMMO_DAO12", false, false, true));
		WEAPONS.put("srM98", new WeaponType("srM98", "M98B", "M98B", "BOLT ACTION", "VERY LONG", "ID_P_AMMO_M98B", false, false, true));
		WEAPONS.put("ID_P_WNAME_M39", new WeaponType("ID_P_WNAME_M39", "M39 EMR", "M39", "SEMIAUTO", "VERY LONG", "20 [7.62x51mm NATO]", false, false, true));

		WEAPON_ATTACHMENTS.put("ID_P_ANAME_LASER", "Laser Sight");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_FOREGRIP", "Foregrip");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_RX01", "Reflex ");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_FLECHETTE", "12G Flechette");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_MAG", "Extended Mag");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_PKAS", "PKA");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_BOLT", "Straight Pull Bolt");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_PKS", "PKS");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_PKA", "PK");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_BIPOD", "Bipod");
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_PSO", "PSO");
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
		WEAPON_ATTACHMENTS.put("ID_P_ANAME_EOTECH", "Holographic ");

		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVWPNEFF", "BELT SPEED");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHSTEALTH", "STEALTH");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVENVG", "THERMAL OPTICS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_NOPASSIVE", "NO UPGRADE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETROCKET", "ROCKET PODS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAARMOR", "REACTIVE ARMOR");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTCOAX", "COAXIAL LMG");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHFIREEX", "EXTINGUISHER");
		VEHICLE_ADDONS.put("ID_P_VUNAME_NOACTIVE", "NO GADGET");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVSMOKE", "IR SMOKE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETBRADAR", "BELOW RADAR");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHBRADAR", "BELOW RADAR");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTCANISTER", "CANISTER SHELL");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETAA", "HEAT SEEKERS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETECM", "ECM JAMMER");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETSTEALTH", "STEALTH");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTSMOKE", "IR SMOKE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AASMOKE", "IR SMOKE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHARADAR", "AIR RADAR");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETPROX", "PROXIMITY SCAN");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHSTEALTH", "STEALTH");
		VEHICLE_ADDONS.put("ID_P_VUNAME_NOSTANCE", "NO WEAPON");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AASTEALTH", "THERMAL CAMO");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHFIREEX", "EXTINGUISHER");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTENVG", "THERMAL OPTICS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVPREVENT", "MAINTENANCE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETPREV", "MAINTENANCE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETFIREEX", "EXTINGUISHER");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHBRADAR", "BELOW RADAR");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVPROX", "PROXIMITY SCAN");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHECM", "ECM JAMMER");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTWPNEFF", "AUTOLOADER");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTPROX", "PROXIMITY SCAN");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVARMOR", "REACTIVE ARMOR");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVHELL", "GUIDED MISSILE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTZOOM", "ZOOM OPTICS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHTVG", "TV MISSILE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHZOOM", "ZOOM OPTICS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVSTEALTH", "THERMAL CAMO");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETAVIONIC", "BEAM SCANNING");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVAPFSDS", "APFSDS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHARADAR", "AIR RADAR");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTSTEALTH", "THERMAL CAMO");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHECM", "ECM JAMMER");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVCOAX", "COAXIAL LMG");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHHELL", "GUIDED MISSILE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETMAVER", "GUIDED MISSILE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAENVG", "THERMAL OPTICS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTCITV", "CITV STATION");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHENVG", "THERMAL OPTICS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAARADAR", "AIR RADAR");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAAA", "ANTI");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHLASER", "LASER PAINTER");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVZOOM", "ZOOM OPTICS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHGUIDE", "GUIDED ROCKET");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETARADAR", "AIR RADAR");
		VEHICLE_ADDONS.put("ID_P_VUNAME_IFVAT", "ATGM LAUNCHER");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETWPNEFF", "BELT SPEED");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAWPNEFF", "BELT SPEED");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHPREV", "MAINTENANCE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHWPNEFF", "AUTOLOADER");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAPREVENT", "MAINTENANCE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHGHELL", "GUIDED MISSILE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHPROX", "PROXIMITY SCAN");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHAA", "HEAT SEEKERS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTPREVENT", "MAINTENANCE");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHAA", "HEAT SEEKERS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAPROX", "PROXIMITY SCAN");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AAZOOM", "ZOOM OPTICS");
		VEHICLE_ADDONS.put("ID_P_VUNAME_JETFLARE", "IR FLARES");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHFLARE", "IR FLARES");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTARMOR", "REACTIVE ARMOR");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHPROX", "PROXIMITY SCAN");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTHMG", "COAXIAL HMG");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHWPNEFF", "BELT SPEED");
		VEHICLE_ADDONS.put("ID_P_VUNAME_AHLASER", "LASER PAINTER");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHFLARE", "IR FLARES");
		VEHICLE_ADDONS.put("ID_P_VUNAME_MBTATGM", "GUIDED SHELL");
		VEHICLE_ADDONS.put("ID_P_VUNAME_SHPREV", "MAINTENANCE");

		SKILLS.put("ID_P_SNAME_ExplRes", "FLAK JACKET");
		SKILLS.put("ID_P_SNAME_Expl", "EXPLOSIVES");
		SKILLS.put("ID_P_SNAME_Heal", "HEAL");
		SKILLS.put("ID_P_SNAME_Gren", "GRENADES");
		SKILLS.put("ID_P_SNAME_Clips2", "AMMO (SQUAD)");
		SKILLS.put("ID_P_SNAME_SuppRes", "SUPPRESION RESIST");
		SKILLS.put("ID_P_SNAME_SuppRes2", "SUPPRESSION RESIST (SQUAD)");
		SKILLS.put("ID_P_SNAME_Clips", "AMMO");
		SKILLS.put("ID_P_SNAME_Gren2", "GRENADES (SQUAD)");
		SKILLS.put("ID_P_SNAME_Suppr2", "SUPRESSION (SQUAD)");
		SKILLS.put("ID_P_SNAME_Sprint2", "SPRINT (SQUAD)");
		SKILLS.put("ID_P_SNAME_Suppr", "SUPRESSION");
		SKILLS.put("ID_P_SNAME_Expl2", " EXPLOSIVES (SQUAD)");
		SKILLS.put("ID_P_SNAME_ExplRes2", "FLAK JACKET (SQUAD)");
		SKILLS.put("ID_P_SNAME_Heal2", "HEAL (SQUAD)");
		SKILLS.put("ID_P_SNAME_Sprint", "SPRINT");
		SKILLS.put("ID_P_SNAME_NoSpec", "NO");

		KIT_ITEMS.put("ID_P_INAME_C4", "C4 EXPLOSIVES");
		KIT_ITEMS.put("ID_P_INAME_SOFLAM", "SOFLAM");
		KIT_ITEMS.put("ID_P_INAME_MEDKIT", "MEDIC KIT");
		KIT_ITEMS.put("ID_P_INAME_AMMO", "AMMO BOX");
		KIT_ITEMS.put("ID_P_INAME_MORTAR", "M224 MORTAR");
		KIT_ITEMS.put("ID_P_INAME_UGS", "T");
		KIT_ITEMS.put("ID_P_INAME_EOD", "EOD BOT");
		KIT_ITEMS.put("ID_P_INAME_DEFIB", "DEFIBRILLATOR");
		KIT_ITEMS.put("ID_P_INAME_REPAIR", "REPAIR TOOL");
		KIT_ITEMS.put("ID_P_INAME_CLAYMORE", "M18 CLAYMORE");
		KIT_ITEMS.put("ID_P_INAME_FLASHBANG", "M84 FLASHBANG");
		KIT_ITEMS.put("ID_P_INAME_BEACON", "RADIO BEACON");
		KIT_ITEMS.put("ID_P_INAME_SMOKE", "M18 SMOKE");
		KIT_ITEMS.put("ID_P_INAME_MINE", "M15 AT MINE");
		KIT_ITEMS.put("ID_P_INAME_MAV", "MAV");
		KIT_ITEMS.put("ID_P_INAME_NOGADGET2", "No Gadget");
		KIT_ITEMS.put("ID_P_INAME_NOGADGET1", "No Gadget");
		KIT_ITEMS.put("ID_P_INAME_M67", "M67 GRENADE");
	}
	
	//Static methods
	public static int getPlatformIdFromName( String p ) {
			
		for( int i = 0; i < PLATFORMS.length; i++ ) {
			
			if( DataBank.PLATFORMS[i].getName().equals( p ) ) {
				
				return DataBank.PLATFORMS[i].getId();
		
			}
			
		}
		
		return DataBank.PLATFORMS[0].getId();
	}
	
	public static String getPlatformNameFromId( int pId ) {
		
		for( int i = 0; i < PLATFORMS.length; i++ ) {
			
			if( DataBank.PLATFORMS[i].getId() == pId ) {
				
				return DataBank.PLATFORMS[i].getName();
		
			}
			
		}
		
		return DataBank.PLATFORMS[0].getName();
	}

	public static String getKitTitle( int number ) {
	
		switch( number ) {
	
			case 1:
				return "Assault";
				
			case 2:	
				return "Engineer";
				
			case 8:	
				return "Recon";
				
			case 16: 
				return "Vehicle";
				
			case 32: 
				return "Support";
				
			case 64: 
				return "General";
				
			default:
				return "";
			
		}
		
	}
}