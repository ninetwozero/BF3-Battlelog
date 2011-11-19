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
	public static String getWeaponTitleShort( String key ) { return WEAPONS_SHORT.containsKey( key )? WEAPONS_SHORT.get( key ): ""; }
	public static String getAttachmentTitle( String key ) { return WEAPON_ATTACHMENTS.containsKey( key )? WEAPON_ATTACHMENTS.get( key ): ""; }
	public static String getVehicleTitle( String key ) { return VEHICLES.containsKey( key )? VEHICLES.get( key ).getName(): ""; }
	public static String getVehicleAddon( String key ) { return VEHICLE_ADDONS.containsKey( key )? VEHICLE_ADDONS.get( key ): ""; }
	public static String getSkillTitle( String key ) { return SKILLS.containsKey( key )? SKILLS.get( key ): ""; }
	public static String getKitUnlockTitle( String key ) { return KIT_ITEMS.containsKey( key )? KIT_ITEMS.get( key ): ""; }
	public static String getUnlockGoal( String key ) { return UNLOCK_GOALS.containsKey( key )? UNLOCK_GOALS.get( key ): ""; }
	public static String getAwardTitle( String key ) { return AWARDS.containsKey( key ) ? AWARDS.get( key ) : ""; }
	public static String getMapTitle( String key ) { return MAPS.containsKey( key ) ? MAPS.get( key ) : ""; }
	public static String getCoopLevelTitle( String key ) { return COOP_DATA.containsKey( key ) ? COOP_DATA.get( key ) : ""; }
	public static String getDifficultyTitle( String key ) { return DIFFICULTY_MAP.containsKey( key ) ? DIFFICULTY_MAP.get( key ) : ""; }
	
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
	private static HashMap<String, String> COOP_DATA = new HashMap<String, String>();
	private static HashMap<String, String> DIFFICULTY_MAP = new HashMap<String, String>();
	static{
  
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
		MAPS.put("MP_007", "CASPIAN BORDER");
		MAPS.put("MP_013", "DAMAVAND PEAK");
		MAPS.put("MP_012", "OPERATION FIRESTORM");
		MAPS.put("MP_011", "SEINE CROSSING");
		MAPS.put("MP_017", "NOSHAHR CANALS");
		MAPS.put("MP_018", "KHARG ISLAND");
		MAPS.put("MP_019", "OMAHA");
		MAPS.put("MP_001", "GRAND BAZAAR");
		MAPS.put("MP_003", "TEHRAN HIGHWAY");
    	
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

		WEAPONS_SHORT.put("ID_P_WNAME_PDR", "PDW-R");
		WEAPONS_SHORT.put("ID_P_WNAME_TYPE88SNIPER", "QBU-88");
		WEAPONS_SHORT.put("ID_P_WNAME_M98B", "M98B");
		WEAPONS_SHORT.put("ID_P_WNAME_A91", "A-91");
		WEAPONS_SHORT.put("ID_P_WNAME_UMP45", "UMP-45");
		WEAPONS_SHORT.put("ID_P_WNAME_M93R", "93R");
		WEAPONS_SHORT.put("ID_P_WNAME_GP30", "GP-30");
		WEAPONS_SHORT.put("ID_P_WNAME_G36C", "G36C");
		WEAPONS_SHORT.put("ID_P_WNAME_FAMAS", "FAMAS");
		WEAPONS_SHORT.put("ID_P_WNAME_MG36", "MG36");
		WEAPONS_SHORT.put("ID_P_WNAME_RPK", "RPK-74M");
		WEAPONS_SHORT.put("ID_P_WNAME_M412Rex", "MP412 REX");
		WEAPONS_SHORT.put("ID_P_WNAME_QBZ95B", "QBZ-95B");
		WEAPONS_SHORT.put("ID_P_WNAME_L85A2", "L85A2");
		WEAPONS_SHORT.put("ID_P_WNAME_USAS12", "USAS-12");
		WEAPONS_SHORT.put("ID_P_WNAME_SKS", "SKS");
		WEAPONS_SHORT.put("ID_P_WNAME_SVD", "SVD");
		WEAPONS_SHORT.put("ID_P_WNAME_Javelin", "FGM-148 JAVELIN");
		WEAPONS_SHORT.put("ID_P_WNAME_SMAW", "SMAW");
		WEAPONS_SHORT.put("ID_P_WNAME_553", "SG553");
		WEAPONS_SHORT.put("ID_P_WNAME_M9", "M9");
		WEAPONS_SHORT.put("ID_P_WNAME_M4", "M4");
		WEAPONS_SHORT.put("ID_P_WNAME_M1014", "M1014");
		WEAPONS_SHORT.put("ID_P_WNAME_M1911LIT", "M1911 TACT");
		WEAPONS_SHORT.put("ID_P_WNAME_Mk11", "MK11 MOD 0");
		WEAPONS_SHORT.put("ID_P_WNAME_M416", "M416");
		WEAPONS_SHORT.put("ID_P_WNAME_Saiga12", "SAIGA 12K");
		WEAPONS_SHORT.put("ID_P_WNAME_AEK971", "AEK-971");
		WEAPONS_SHORT.put("ID_P_WNAME_Glock18", "G18");
		WEAPONS_SHORT.put("ID_P_WNAME_Glock17", "G17C");
		WEAPONS_SHORT.put("ID_P_WNAME_M4A1", "M4A1");
		WEAPONS_SHORT.put("ID_P_WNAME_G3", "G3A3");
		WEAPONS_SHORT.put("ID_P_WNAME_AK74M", "AK-74M");
		WEAPONS_SHORT.put("ID_P_WNAME_Pecheng", "PKP PECHENEG");
		WEAPONS_SHORT.put("ID_P_WNAME_M26Mass", "M26 MASS");
		WEAPONS_SHORT.put("ID_P_WNAME_DAO12", "DAO-12");
		WEAPONS_SHORT.put("ID_P_WNAME_M240", "M240B");
		WEAPONS_SHORT.put("ID_P_WNAME_GLOCK18SILENCED", "G18 SUPP");
		WEAPONS_SHORT.put("ID_P_WNAME_M249", "M249");
		WEAPONS_SHORT.put("ID_P_WNAME_JACKHAMMER", "Jackhammer");
		WEAPONS_SHORT.put("ID_P_WNAME_Stinger", "FIM-92 STINGER");
		WEAPONS_SHORT.put("ID_P_WNAME_M1911TACTICAL", "M1911 S-TAC");
		WEAPONS_SHORT.put("ID_P_WNAME_GLOCK17SILENCED", "G17C SUPP");
		WEAPONS_SHORT.put("ID_P_WNAME_RPG7", "RPG-7V2");
		WEAPONS_SHORT.put("ID_P_WNAME_PP2000", "PP-2000");
		WEAPONS_SHORT.put("ID_P_WNAME_KH2002", "KH2002");
		WEAPONS_SHORT.put("ID_P_WNAME_40MM", "M320");
		WEAPONS_SHORT.put("ID_P_WNAME_M136", "M136");
		WEAPONS_SHORT.put("ID_P_WNAME_M9SILENCED", "M9 SUPP");
		WEAPONS_SHORT.put("ID_P_WNAME_AKS74u", "AKS-74u");
		WEAPONS_SHORT.put("ID_P_WNAME_HK53", "HK53");
		WEAPONS_SHORT.put("ID_P_WNAME_MP443SILENCED", "MP443 SUPP");
		WEAPONS_SHORT.put("ID_P_WNAME_L96", "L96");
		WEAPONS_SHORT.put("ID_P_WNAME_MP443", "MP443");
		WEAPONS_SHORT.put("ID_P_WNAME_IGLA", "SA-18 IGLA");
		WEAPONS_SHORT.put("ID_P_WNAME_SV98", "SV98");
		WEAPONS_SHORT.put("ID_P_WNAME_870", "870MCS");
		WEAPONS_SHORT.put("ID_P_WNAME_AN94", "AN-94");
		WEAPONS_SHORT.put("ID_P_WNAME_Type88", "TYPE 88 LMG");
		WEAPONS_SHORT.put("ID_P_WNAME_ASVal", "AS VAL");
		WEAPONS_SHORT.put("ID_P_WNAME_MP7", "MP7");
		WEAPONS_SHORT.put("ID_P_WNAME_M1911", "M1911");
		WEAPONS_SHORT.put("ID_P_WNAME_M1911SILENCED", "M1911 SUPP");
		WEAPONS_SHORT.put("ID_P_WNAME_M82A3", "M82A3");
		WEAPONS_SHORT.put("ID_P_WNAME_knife", "KNIFE");
		WEAPONS_SHORT.put("ID_P_WNAME_M60", "M60E4");
		WEAPONS_SHORT.put("ID_P_WNAME_M27", "M27 IAR");
		WEAPONS_SHORT.put("ID_P_WNAME_M16", "M16A4");
		WEAPONS_SHORT.put("ID_P_WNAME_M39", "M39 EMR");
		WEAPONS_SHORT.put("ID_P_WNAME_F2000", "F2000");
		WEAPONS_SHORT.put("ID_P_WNAME_P90", "P90");
		WEAPONS_SHORT.put("ID_P_WNAME_QBU88", "QBU-88");
		WEAPONS_SHORT.put("ID_P_WNAME_PP19", "PP-19 Bizon");
		WEAPONS_SHORT.put("ID_P_WNAME_M16A4", "M16A3");
		WEAPONS_SHORT.put("ID_P_WNAME_MP443LIT", "MP443 TACT");
		WEAPONS_SHORT.put("ID_P_WNAME_M40A5", "M40A5");
		WEAPONS_SHORT.put("ID_P_WNAME_M9LIT", "M9 TACT");
		WEAPONS_SHORT.put("ID_P_WNAME_QBB95", "QBB-95");
		WEAPONS_SHORT.put("ID_P_WNAME_SCARH", "SCAR-H");
		
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
		AWARDS.put("ID_P_WSTAR_NAME_KNIFE", "KNIFE SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M1911SILENCED", "M1911 SUPP");
		AWARDS.put("ID_P_WSTAR_NAME_DAO12", "DAO-12 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_G3A4", "G3A3 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_G36C", "G36C SERVICE STAR");
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
		AWARDS.put("ID_P_WSTAR_NAME_GLOCK18SILENCED", "G18 SUPP");
		AWARDS.put("ID_P_WSTAR_NAME_M9", "M9 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_PECHENEG", "PKP PECHENEG SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M240", "M240B SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M249", "M249 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M26FRAG", "M26 FRAG SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_USAS12", "USAS-12 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_GLOCK17SILENCED", "G17C SUPP");
		AWARDS.put("ID_P_WSTAR_NAME_870", "870MCS SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M416", "M416 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M4A1", "M4A1 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_MP443", "MP443 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_MP443LIT", "MP443 TACT");
		AWARDS.put("ID_P_WSTAR_NAME_AK74M", "AK-74M SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_ASVAL", "AS VAL SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_IGLA", "SA-18 IGLA SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_UMP", "UMP-45 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_SMAW", "SMAW SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_SKS", "SKS SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_SVD", "SVD SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_SG553", "SG553 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_GLOCK18", "G18 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_GLOCK17", "G17C SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M1014", "M1014 SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M320SG", "M320 SHOTGUN SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M412REX", "M412 REX SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_JAVELIN", "FGM-148 JAVELIN SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_M26FLECHETTE", "M26 DART SERVICE STAR");
		AWARDS.put("ID_P_WSTAR_NAME_AKS74U", "AKS-74u SERVICE STAR");
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
		
		COOP_DATA.put("COOP_EXFILTRATION", "Exfiltration");
		COOP_DATA.put("COOP_INTRO", "Independent sources have provided consistent information that a major attack is planned by the PLR in the upcoming weeks");
		COOP_DATA.put("COOP_ASSASSINATION", "Assassination");
		COOP_DATA.put("COOP_PATH", "MAIN ");
		COOP_DATA.put("COOP_MISSION", "CO-OP MISSION ");
		COOP_DATA.put("COOP_HOSTAGE", "Drop 'Em Like Liquid");
		COOP_DATA.put("COOP_HEADER", "GAME MENU");
		COOP_DATA.put("COOP_RESUME", "RESUME");
		COOP_DATA.put("COOP_HINT", "Play co-op");
		COOP_DATA.put("COOP_PATH", "MAIN ");
		COOP_DATA.put("COOP_CLASSIFIED", "CLASSIFIED");
		COOP_DATA.put("COOP_QUIT", "QUIT");
		COOP_DATA.put("COOP_010", "THE ELEVENTH HOUR");
		COOP_DATA.put("COOP_006", "FIRE FROM THE SKY");
		COOP_DATA.put("COOP_007", "OPERATION EXODUS");
		COOP_DATA.put("COOP_004", "ROLLING THUNDER");
		COOP_DATA.put("COOP_005", "BEHIND ENEMY LINES");
		COOP_DATA.put("COOP_008", "ASSASSINATION");
		COOP_DATA.put("COOP_009", "EXFILTRATION");
		COOP_DATA.put("COOP_002", "HIT AND RUN");
		COOP_DATA.put("COOP_003", "DROP 'EM LIKE LIQUID");
		COOP_DATA.put("COOP_001", "A-10");
		COOP_DATA.put("COOP_SPOTTING", "SPOTTING");
		COOP_DATA.put("COOP_REVIVE", "CRITICAL HEALTH");
		COOP_DATA.put("COOP_OPTIONS", "OPTIONS");
		COOP_DATA.put("COOP_PRESENTATION", "Independent sources have provided consistent information that a major attack is planned by the PLR in the upcoming weeks");
		COOP_DATA.put("COOP_PATH", "CO-OP ");
		COOP_DATA.put("COOP_LEADERBOARDS", "LEADERBOARDS");
		COOP_DATA.put("COOP_EXTRACTION", "Operation Exodus");
		COOP_DATA.put("COOP_SUPERCOBRA", "Fire From The Sky");
		COOP_DATA.put("COOP_A10", "A-10");
		COOP_DATA.put("COOP_BREACHED", "Hit And Run");
		COOP_DATA.put("COOP_GREAT", "GREAT");
		COOP_DATA.put("COOP_ROLLINGTHUNDER", "Rolling Thunder");
		COOP_DATA.put("COOP_HINT03", "KILLING AN ENEMY SPOTTED BY YOUR FRIEND WILL GIVE HIM A SCORE BONUS");
		COOP_DATA.put("COOP_HINT04", "YOU CAN REPLENISH AMMO FROM WEAPON CRATES");
		COOP_DATA.put("COOP_HINT01", "WHEN YOUR FRIEND ENDS UP IN MAN DOWN");
		COOP_DATA.put("COOP_HINT02", "KILL ENEMIES WHILE IN MAN DOWN TO GET BACK UP");
		COOP_DATA.put("COOP_HINT05", "USE THE AMMO CRATES PLACED IN THE WORLD TO REPLENISH YOUR WEAPON");
		COOP_DATA.put("COOP_BEHINDENEMYLINES", "Behind Enemy Lines");
		COOP_DATA.put("COOP_SUBWAY", "The Eleventh Hour");
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
}