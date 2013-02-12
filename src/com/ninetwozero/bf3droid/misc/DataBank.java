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

package com.ninetwozero.bf3droid.misc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.PlatformData;
import com.ninetwozero.bf3droid.datatype.VehicleType;

import java.util.HashMap;
import java.util.Map;

public final class DataBank {

    // Construct = none
    private DataBank() {
    }

    // Maps
    private static PlatformData[] PLATFORMS;
    private static String[] LANGUAGES;
    private static String[] LOCALES;
    private static Map<String, Integer> PERSONA_IMAGES;
    private static Map<Integer, Intent> CONTACT_INTENTS;

    private static Map<String, VehicleType> VEHICLES;
    private static Map<String, String> KIT_ITEMS;
    private static Map<String, String> SKILLS;
    private static Map<String, String> VEHICLE_ADDONS;
    private static Map<String, String> WEAPON_ATTACHMENTS;
    private static Map<String, Integer> WEAPONS;
    private static Map<String, String> UNLOCK_GOALS;

    private static Map<String, String> MAPS;
    private static Map<String, String> COOP_DATA;
    private static Map<String, String> DIFFICULTY_MAP;
    @Deprecated
    private static Map<String, String[]> ASSIGNMENTS;

    static {

        // Init!
        PLATFORMS = new PlatformData[4];
        CONTACT_INTENTS = new HashMap<Integer, Intent>();
        PERSONA_IMAGES = new HashMap<String, Integer>();
        VEHICLES = new HashMap<String, VehicleType>();
        KIT_ITEMS = new HashMap<String, String>();
        SKILLS = new HashMap<String, String>();
        VEHICLE_ADDONS = new HashMap<String, String>();
        WEAPONS = new HashMap<String, Integer>();
        WEAPON_ATTACHMENTS = new HashMap<String, String>();
        UNLOCK_GOALS = new HashMap<String, String>();
        MAPS = new HashMap<String, String>();
        COOP_DATA = new HashMap<String, String>();
        DIFFICULTY_MAP = new HashMap<String, String>();
        ASSIGNMENTS = new HashMap<String, String[]>();

        // LANG & LOCALE
        LANGUAGES = new String[]{"English", "Deutsch", "FranÃ§ais",
                "EspaÃ±ol", "Italiano", "Polski", "PÑƒÑ�Ñ�ÐºÐ¸Ð¹", "æ—¥æœ¬èªž",
                "í•œêµ­ì–´", "ä¸­æ–‡", "ÄŒesky"};
        LOCALES = new String[]{"en", "de", "fr", "es", "it", "pl", "ru",
                "jp", "kr", "zh", "cz"};

        // PLATFORMS
        PLATFORMS[0] = new PlatformData(1, "pc");
        PLATFORMS[1] = new PlatformData(2, "xbox");
        PLATFORMS[2] = new PlatformData(1, "");
        PLATFORMS[3] = new PlatformData(4, "ps3");

        // CONTACT INTENTS
        CONTACT_INTENTS.put(
                R.id.wrap_web,
                new Intent(Intent.ACTION_VIEW, Uri
                        .parse("http://www.ninetwozero.com")));
        CONTACT_INTENTS.put(R.id.wrap_twitter, new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.twitter.com/karllindmark")));
        CONTACT_INTENTS.put(R.id.wrap_email, new Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:support@ninetwozero.com")));

        CONTACT_INTENTS.put(
                R.id.wrap_forum,
                new Intent(Intent.ACTION_VIEW, Uri
                        .parse("http://www.ninetwozero.com/forum")));
        CONTACT_INTENTS
                .put(R.id.wrap_xbox,
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://live.xbox.com/en-US/Profile?gamertag=NINETWOZERO")));
        CONTACT_INTENTS
                .put(R.id.wrap_paypal,
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=Y8GLB993JKTCL")));

		// PERSONA IMAGES
		PERSONA_IMAGES.put("bf3-us-engineer", R.drawable.bf3_us_engineer);
		PERSONA_IMAGES.put("bf3-ru-engineer", R.drawable.bf3_ru_engineer);
		PERSONA_IMAGES.put("bf3-us-assault", R.drawable.bf3_us_assault);
		PERSONA_IMAGES.put("bf3-ru-assault", R.drawable.bf3_ru_assault);
		PERSONA_IMAGES.put("bf3-us-support", R.drawable.bf3_us_support);
		PERSONA_IMAGES.put("bf3-ru-support", R.drawable.bf3_ru_support);
		PERSONA_IMAGES.put("bf3-us-recon", R.drawable.bf3_us_recon);
		PERSONA_IMAGES.put("bf3-ru-recon", R.drawable.bf3_ru_recon);
		PERSONA_IMAGES.put("bf3-us-engineer2", R.drawable.bf3_us_engineer2);
		PERSONA_IMAGES.put("bf3-ru-engineer2", R.drawable.bf3_ru_engineer2);
		PERSONA_IMAGES.put("bf3-us-assault2", R.drawable.bf3_us_assault2);
		PERSONA_IMAGES.put("bf3-ru-assault2", R.drawable.bf3_ru_assault2);
		PERSONA_IMAGES.put("bf3-us-support2", R.drawable.bf3_us_support2);
		PERSONA_IMAGES.put("bf3-ru-support2", R.drawable.bf3_ru_support2);
		PERSONA_IMAGES.put("bf3-us-recon2", R.drawable.bf3_us_recon2);
		PERSONA_IMAGES.put("bf3-ru-recon2", R.drawable.bf3_ru_recon2);

        // DIFFICULTIES
        DIFFICULTY_MAP.put("ID_RP_DIFFICULTY_MEDIUM", "Normal");
        DIFFICULTY_MAP.put("ID_RP_DIFFICULTY_HARDCORE", "Hardcore");
        DIFFICULTY_MAP.put("ID_RP_DIFFICULTY_HARD", "Hard");
        DIFFICULTY_MAP.put("ID_RP_DIFFICULTY_EASY", "Easy");

        // MAPS
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

        // UNLOCKS
        UNLOCK_GOALS.put("rank", "Reach rank {rank} (you are rank {rankCurr})");
        UNLOCK_GOALS.put("sc_assault",
                "{scoreCurr}/{scoreNeeded} Assault Score");
        UNLOCK_GOALS.put("sc_engineer",
                "{scoreCurr}/{scoreNeeded} Engineer Score");
        UNLOCK_GOALS.put("sc_recon", "{scoreCurr}/{scoreNeeded} Recon Score");
        UNLOCK_GOALS.put("sc_support",
                "{scoreCurr}/{scoreNeeded} Support Score");
        UNLOCK_GOALS.put("sc_coop", "{scoreCurr}/{scoreNeeded} CO-OP Score");
        UNLOCK_GOALS.put("sc_vehicleah",
                "{scoreCurr}/{scoreNeeded} Attack Helicopter Score"); // Vehicle
        // Air
        UNLOCK_GOALS.put("sc_vehicleaa",
                "{scoreCurr}/{scoreNeeded} Anti Air Score");
        UNLOCK_GOALS.put("sc_vehiclejet",
                "{scoreCurr}/{scoreNeeded} Jetplane Score"); // Vehicle Air
        UNLOCK_GOALS.put("sc_vehicleifv",
                "{scoreCurr}/{scoreNeeded} Infantry Fighting Vehicle Score");
        UNLOCK_GOALS.put("sc_vehiclesh",
                "{scoreCurr}/{scoreNeeded}  Scout Helicopter Score"); // Vehicle
        // Air
        UNLOCK_GOALS.put("sc_vehiclembt",
                "{scoreCurr}/{scoreNeeded} Battle Tanks Score"); // Vehicle Main
        UNLOCK_GOALS.put("c_", "{scoreCurr}/{scoreNeeded} {name} kills");
        UNLOCK_GOALS.put("xpm", "Complete \"{name}\"");


        /* TODO: GENERATE STRINGS FOR VEHICLES */
        VEHICLES.put("0E09B2D0-BA4A-1509-E1D2-949FB0C04DBE", new VehicleType(
                "0E09B2D0-BA4A-1509-E1D2-949FB0C04DBE", "UH-1Y VENOM",
                "uh-1y-venom"));
        VEHICLES.put("1E8653E6-11A0-DF93-C808-E48351D2F578", new VehicleType(
                "1E8653E6-11A0-DF93-C808-E48351D2F578", "AAV-7A1 AMTRAC",
                "aav-7a1-amtrac"));
        VEHICLES.put("37A53096-BA80-5498-1347-8C7B238680C8", new VehicleType(
                "37A53096-BA80-5498-1347-8C7B238680C8", "M220 TOW LAUNCHER",
                "m220-tow-launcher"));
        VEHICLES.put("3F18FCA6-A7D4-D3B5-28E5-44A5CAFFE6BE", new VehicleType(
                "3F18FCA6-A7D4-D3B5-28E5-44A5CAFFE6BE", "F-35", "f-35"));
        VEHICLES.put("5AF4330A-7202-11DE-B32B-D34A31A54ED5", new VehicleType(
                "5AF4330A-7202-11DE-B32B-D34A31A54ED5", "A-10 THUNDERBOLT",
                "a-10-thunderbolt"));
        VEHICLES.put("60106975-DD7D-11DD-A030-B04E425BA11E", new VehicleType(
                "60106975-DD7D-11DD-A030-B04E425BA11E", "T-90A", "t-90a"));
        VEHICLES.put("74866776-D5AF-BD32-7964-CD234506235D", new VehicleType(
                "74866776-D5AF-BD32-7964-CD234506235D", "SKID LOADER",
                "skid-loader"));
        VEHICLES.put("860157CA-6527-4123-B60E-71117DD878D7", new VehicleType(
                "860157CA-6527-4123-B60E-71117DD878D7", "LAV-AD", "lav-ad"));
        VEHICLES.put("89BEA1D6-2FF4-11DE-8C23-DAB1D69416DE", new VehicleType(
                "89BEA1D6-2FF4-11DE-8C23-DAB1D69416DE", "MI-28 HAVOC",
                "mi-28-havoc"));
        VEHICLES.put("98E5B9BF-0B68-4AF0-A20D-1A23D6B6BF8B", new VehicleType(
                "98E5B9BF-0B68-4AF0-A20D-1A23D6B6BF8B", "GAZ-3937 VODNIK",
                "gaz-3937-vodnik"));
        VEHICLES.put("9D35A483-0B6B-91AE-5025-351AD87B2B46", new VehicleType(
                "9D35A483-0B6B-91AE-5025-351AD87B2B46", "DPV", "dpv"));
        VEHICLES.put("A36C9712-54B3-A5FF-8627-7BC7EFA0C668", new VehicleType(
                "A36C9712-54B3-A5FF-8627-7BC7EFA0C668", "9K22 TUNGUSKA-M",
                "9k22-tunguska-m"));
        VEHICLES.put("A676D498-A524-42AD-BE78-72B071D8CD6A", new VehicleType(
                "A676D498-A524-42AD-BE78-72B071D8CD6A", "AH-1Z VIPER",
                "ah-1z-viper"));
        VEHICLES.put("AAE95907-AFD4-11DD-84FB-9FA71F68ED5E", new VehicleType(
                "AAE95907-AFD4-11DD-84FB-9FA71F68ED5E", "BMP-2M", "bmp-2m"));
        VEHICLES.put("ADF563C9-28B1-C42B-993E-B2FD40F36078", new VehicleType(
                "ADF563C9-28B1-C42B-993E-B2FD40F36078", "LAV-25", "lav-25"));
        VEHICLES.put("B06A08AB-EECF-11DD-8117-9421284A74E5", new VehicleType(
                "B06A08AB-EECF-11DD-8117-9421284A74E5", "M1 ABRAMS",
                "m1-abrams"));
        VEHICLES.put("B26FD546-2ADF-1A90-3044-F7748B86DA26", new VehicleType(
                "B26FD546-2ADF-1A90-3044-F7748B86DA26", "RHIB BOAT",
                "rhib-boat"));
        VEHICLES.put("B9BAE0F8-72B9-4E1C-B5AD-F9353727C990", new VehicleType(
                "B9BAE0F8-72B9-4E1C-B5AD-F9353727C990", "KA-60 KASATKA",
                "ka-60-kasatka"));
        VEHICLES.put("C645317B-45BB-E082-7E5C-918388C22D59", new VehicleType(
                "C645317B-45BB-E082-7E5C-918388C22D59", "PANTSIR-S1",
                "pantsir-s1"));
        VEHICLES.put("C81F8757-E6D2-DF2D-1CFE-B72B4F74FE98", new VehicleType(
                "C81F8757-E6D2-DF2D-1CFE-B72B4F74FE98", "F/A-18E SUPER HORNET",
                "f-a-18e-super-hornet"));
        VEHICLES.put("CD8C281F-579D-4E7B-BE3D-F206E91407F8", new VehicleType(
                "CD8C281F-579D-4E7B-BE3D-F206E91407F8", "SU-25TM FROGFOOT",
                "su-25tm-frogfoot"));
        VEHICLES.put("D1B516CA-6119-F025-C923-1B0700B6AEBA", new VehicleType(
                "D1B516CA-6119-F025-C923-1B0700B6AEBA", "M1114 HMMWV",
                "m1114-hmmwv"));
        VEHICLES.put("D35CA587-79AF-D351-6F65-967794C7F1B7", new VehicleType(
                "D35CA587-79AF-D351-6F65-967794C7F1B7", "CENTURION C-RAM",
                "centurion-c-ram"));
        VEHICLES.put("D68E417F-6103-5140-3ABC-4C7505160A09", new VehicleType(
                "D68E417F-6103-5140-3ABC-4C7505160A09", "VDV BUGGY",
                "vdv-buggy"));
        VEHICLES.put("D780AFF6-38B7-11DE-BF1C-984D9AEE762C", new VehicleType(
                "D780AFF6-38B7-11DE-BF1C-984D9AEE762C", "Z-11W", "z-11w"));
        VEHICLES.put("D7BAB9C1-1208-4923-BD3A-56EB945E04E1", new VehicleType(
                "D7BAB9C1-1208-4923-BD3A-56EB945E04E1",
                "9M133 KORNET LAUNCHER", "9m133-kornet-launcher"));
        VEHICLES.put("E7A99B55-B5BD-C101-2384-97458D4AC23C", new VehicleType(
                "E7A99B55-B5BD-C101-2384-97458D4AC23C", "GROWLER ITV",
                "growler-itv"));
        VEHICLES.put("F1FFAE42-B67A-4E4F-2626-3CBF37AE287B", new VehicleType(
                "F1FFAE42-B67A-4E4F-2626-3CBF37AE287B", "SU-35BM FLANKER-E",
                "su-35bm-flanker-e"));
        VEHICLES.put("F998F5E4-220D-463A-A437-1C18D5C3A19E", new VehicleType(
                "F998F5E4-220D-463A-A437-1C18D5C3A19E", "BTR-90", "btr-90"));
        VEHICLES.put("FD8AB748-FF4D-11DD-A7B1-F7C6DEEC9D32", new VehicleType(
                "FD8AB748-FF4D-11DD-A7B1-F7C6DEEC9D32", "AH-6J LITTLE BIRD",
                "ah-6j-little-bird"));

        WEAPONS.put("04C8604E-37DE-4B51-B70A-66468003D604",
                R.string.name_weapon_mp7);
        WEAPONS.put("04FD6527-0BF0-4A67-9ABB-9F992BF2CBA0",
                R.string.name_weapon_glock17);
        WEAPONS.put("05EB2892-8B51-488E-8956-4350C3D2BA27",
                R.string.name_weapon_m98b);
        WEAPONS.put("0733BF61-8EBC-4666-9610-7E27D7313791",
                R.string.name_weapon_553);
        WEAPONS.put("07A4C87A-D325-4A73-8C5A-C001ACD13334",
                R.string.name_weapon_870);
        WEAPONS.put("07A6AB6A-457D-4481-94F9-A3FE15C3D923",
                R.string.name_weapon_mp443silenced);
        WEAPONS.put("08F58ECD-BC99-48AA-A9B3-47D412E99A4E",
                R.string.name_weapon_rpg7);
        WEAPONS.put("0A54B1A8-0368-4939-9558-D31E40FE488F",
                R.string.name_weapon_mtar21);
        WEAPONS.put("0E0E4701-359B-48FF-B91A-F4B6373435E4",
                R.string.name_weapon_kh2002);
        WEAPONS.put("120A0838-9E95-4564-A6F6-5A14A1E0AF85",
                R.string.name_weapon_aks74u);
        WEAPONS.put("1C426722-C0B6-492F-98B2-3B9D2B97C808",
                R.string.name_weapon_m1911silenced);
        WEAPONS.put("1C689273-7637-40FF-89A5-E75F120D9E02",
                R.string.name_weapon_m27);
        WEAPONS.put("1E5E0296-CFD0-448E-B1D7-F795E8F98E2C",
                R.string.name_weapon_g36c);
        WEAPONS.put("1EA227D8-2EB5-A63B-52FF-BBA9CFE34AD8",
                R.string.name_weapon_taurus44);
        WEAPONS.put("21A80B6B-8FA6-4BCC-84D3-7ED782E978FA",
                R.string.name_weapon_asval);
        WEAPONS.put("27F63AEA-DD70-4929-9B08-5FF8F075B75E",
                R.string.name_weapon_dao12);
        WEAPONS.put("283C66DE-3866-46CD-A1C0-B456A5916537",
                R.string.name_weapon_40mm);
        WEAPONS.put("2A267103-14F2-4255-B0D4-819139A4E202",
                R.string.name_weapon_ump45);
        WEAPONS.put("32B899E5-0542-45E6-A34B-86871C7FE098",
                R.string.name_weapon_m27);
        WEAPONS.put("35796A7B-C7FA-4C0F-AD99-3DDB3B60A293",
                R.string.name_weapon_igla);
        WEAPONS.put("386F9329-7DE7-6FB9-1366-2877C698D9B7",
                R.string.name_weapon_scarh);
        WEAPONS.put("38C20C39-EE43-489F-AE95-DF0519F72409",
                R.string.name_weapon_m412rex);
        WEAPONS.put("3A6B6A16-E5A1-33E0-5B53-56E77833DAF4",
                R.string.name_weapon_m416);
        WEAPONS.put("3BA55147-6619-4697-8E2B-AC6B1D183C0E",
                R.string.name_weapon_ak74m);
        WEAPONS.put("3F06931A-443C-6E78-DEF9-A33EB9F43D35",
                R.string.name_weapon_pecheng);
        WEAPONS.put("405F32BB-3E1A-4201-B96D-10B231D91BA5",
                R.string.name_weapon_m60);
        WEAPONS.put("414C4598-4089-43E0-82FB-BBF7031D02E8",
                R.string.name_weapon_m240);
        WEAPONS.put("42DB9F03-0224-4676-99FC-ED444E356290",
                R.string.name_weapon_m16a4);
        WEAPONS.put("4D06B1BF-8002-44E2-851E-EAAC8C4FDD1A",
                R.string.name_weapon_taurus44);
        WEAPONS.put("4F90708E-6875-4A5E-B685-CAF310A0BA95",
                R.string.name_weapon_m1911lit);
        WEAPONS.put("50849B49-F3DA-4C92-9830-D4A2932BC9E7",
                R.string.name_weapon_pp2000);
        WEAPONS.put("512819DB-3E82-33B7-F1D5-E612C9A396BF",
                R.string.name_weapon_g3);
        WEAPONS.put("5244385C-B7ED-4266-AB7C-C1C1B222A9CD",
                R.string.name_weapon_m4);
        WEAPONS.put("5E2D49D1-D1BB-F553-78A5-8D537C43E624",
                R.string.name_weapon_aek971);
        WEAPONS.put("60F775C4-7D70-4898-BD00-03AA60C8CE91",
                R.string.name_weapon_steyraug);
        WEAPONS.put("655D5E41-6DB8-4F3C-9F2A-8117AE11699C",
                R.string.name_weapon_mk11);
        WEAPONS.put("65D4A9F9-0ACD-46FD-9AE2-3E9670DD22FB",
                R.string.name_weapon_an94);
        WEAPONS.put("6921F5C9-3487-43E6-86F5-296DA097FFFB",
                R.string.name_weapon_m16);
        WEAPONS.put("69A28562-5569-4D76-82FB-98C4047306F1",
                R.string.name_weapon_m4a1);
        WEAPONS.put("6D99F118-04BD-449A-BA0E-1978DDF5894D",
                R.string.name_weapon_spas12);
        WEAPONS.put("6F3993B9-E8B2-412F-9361-341140FCBF79",
                R.string.name_weapon_taurus44scoped);
        WEAPONS.put("6F741867-AE83-CC7D-BFB1-035452D7A5B4",
                R.string.name_weapon_usas12);
        WEAPONS.put("71B0A1D6-9E4F-40A3-9906-1A7F3AAD573A",
                R.string.name_weapon_mp443);
        WEAPONS.put("75D1FFC8-D442-4212-B668-96AED9030FC6",
                R.string.name_weapon_rpk);
        WEAPONS.put("84B78F21-217A-4A46-A06E-34A90637BAC8",
                R.string.name_weapon_ak74m);
        WEAPONS.put("8963F500-E71D-41FC-4B24-AE17D18D8C73",
                R.string.name_weapon_knife);
        WEAPONS.put("8DCA9ABD-0723-454C-9575-7E4CA0791D0B",
                R.string.name_weapon_l85a2);
        WEAPONS.put("9351DDBF-795A-4BC6-84D7-37B537E3D049",
                R.string.name_weapon_sv98);
        WEAPONS.put("93CC5226-4381-7458-509E-B2D6F4498164",
                R.string.name_weapon_m40a5);
        WEAPONS.put("94FADBCE-8D16-4736-85E8-D42FADCD174F",
                R.string.name_weapon_scarl);
        WEAPONS.put("95AA1865-4324-4B23-BCC4-84F8F4D91922",
                R.string.name_weapon_acr);
        WEAPONS.put("95E00B23-BAD4-4F3B-A85E-990204EFF26B",
                R.string.name_weapon_mg36);
        WEAPONS.put("96FC0A67-DEA2-4061-B955-E173A8DBB00D",
                R.string.name_weapon_saiga12);
        WEAPONS.put("9A97A9FE-DCE5-41E8-8D89-A421B103FA75",
                R.string.name_weapon_famas);
        WEAPONS.put("9B3AF503-2018-4BC9-893F-CD393D3BAD77",
                R.string.name_weapon_m1911tactical);
        WEAPONS.put("A0222E93-0887-4FC4-90D4-51838945343E",
                R.string.name_weapon_m93r);
        WEAPONS.put("A4F108EB-1FA2-4C94-93FE-357B1D7EBF4A",
                R.string.name_weapon_aks74u);
        WEAPONS.put("A4F683C2-40E2-464C-AE85-AFE4462F2D40",
                R.string.name_weapon_m9silenced);
        WEAPONS.put("A607E88D-90B0-4ECD-B892-8BF66AEF90ED",
                R.string.name_weapon_rpk);
        WEAPONS.put("A7278B05-8D76-4A40-B65D-4414490F6886",
                R.string.name_weapon_m16a4);
        WEAPONS.put("A76BB99E-ABFE-48E9-9972-5D87E5365DAB",
                R.string.name_weapon_m1911);
        WEAPONS.put("A8C7508A-F43B-4446-ACCE-F350EDFEDB28",
                R.string.name_weapon_m93r);
        WEAPONS.put("A9F5B1F6-D83E-4BD8-AFE8-08C4B0A3E697",
                R.string.name_weapon_svd);
        WEAPONS.put("AAE173E4-8DD7-5C25-1763-7A4D9380EB10",
                R.string.name_weapon_jackhammer);
        WEAPONS.put("AC994B66-DA51-42FB-A234-FCBA33EB9AB7",
                R.string.name_weapon_glock18silenced);
        WEAPONS.put("AEAA518B-9253-40C2-AA18-A11F8F2D474C",
                R.string.name_weapon_m249);
        WEAPONS.put("AEFD4BF4-4C08-4834-9DFF-1F7C529175AF",
                R.string.name_weapon_glock17silenced);
        WEAPONS.put("AFF7E14E-5918-456B-9922-6ED1A50F3F15",
                R.string.name_weapon_qbu88);
        WEAPONS.put("B145A444-BC4D-48BF-806A-0CEFA0EC231B",
                R.string.name_weapon_m9);
        WEAPONS.put("B1575807-C480-7286-719C-EE2520292A79",
                R.string.name_weapon_m4a1);
        WEAPONS.put("B2DEF86D-A127-769E-23ED-C9F47F29FAD3",
                R.string.name_weapon_m26mass);
        WEAPONS.put("B8CA6D09-62C2-4208-A094-B8E50F716E47",
                R.string.name_weapon_jng90);
        WEAPONS.put("BA0AF247-2E5B-4574-8F89-515DFA1C767D",
                R.string.name_weapon_l86a1);
        WEAPONS.put("BF6E6CB2-D5AA-4425-A0C8-0FB8D89A1372",
                R.string.name_weapon_lsat);
        WEAPONS.put("BFAC29DB-5193-4E69-96D9-37D4124C44C2",
                R.string.name_weapon_smaw);
        WEAPONS.put("C12E6868-FC08-4E25-8AD0-1C51201EA69B",
                R.string.name_weapon_p90);
        WEAPONS.put("C48BC95B-1271-4F19-9D6C-A91C836F5432",
                R.string.name_weapon_hk417);
        WEAPONS.put("C79AAC6E-566E-40E1-B373-3B0029530393",
                R.string.name_weapon_a91);
        WEAPONS.put("CB018ADD-3648-4504-9359-9BAFB8D92F7D",
                R.string.name_weapon_mk11);
        WEAPONS.put("CB651B07-2CE4-4527-B1AC-2AEB6D04CBF5",
                R.string.name_weapon_sks);
        WEAPONS.put("CBAEC77C-A6AD-4D63-96BD-61FCA6C18417",
                R.string.name_weapon_l96);
        WEAPONS.put("CECC74B7-403F-4BA1-8ECD-4A59FB5379BD",
                R.string.name_weapon_pp19);
        WEAPONS.put("D0E124FB-7116-4FBB-AF00-D8994AEB548D",
                R.string.name_weapon_type88);
        WEAPONS.put("D20984F3-364E-4C06-9879-09280EDF6DF3",
                R.string.name_weapon_qbz95b);
        WEAPONS.put("D4FF4D2C-361F-491E-B53D-207CF77FA609",
                R.string.name_weapon_m9lit);
        WEAPONS.put("DB364A96-08FB-4C6E-856B-BD9749AE0A92",
                R.string.name_weapon_glock18);
        WEAPONS.put("DB94F5EC-74D5-4DB2-9A15-E0C4154BFFD4",
                R.string.name_weapon_f2000);
        WEAPONS.put("DC356150-2A5F-4FCA-BE6C-B993EE7F8A8B",
                R.string.name_weapon_javelin);
        WEAPONS.put("DFBF6EA5-39C5-4ABA-B2C6-CAA6AD6C3786",
                R.string.name_weapon_mp5k);
        WEAPONS.put("E43287AB-529D-4803-B585-6C17E2DD6AEB",
                R.string.name_weapon_mp443lit);
        WEAPONS.put("E7266EC2-4977-60F2-A7CB-6EF7D98A5E2E",
                R.string.name_weapon_hk53);
        WEAPONS.put("E9BEDD8F-899F-3A3C-C561-5E58B350C60D",
                R.string.name_weapon_stinger);
        WEAPONS.put("EB17660D-D81B-4BB7-BE95-70662855489E",
                R.string.name_weapon_m39);
        WEAPONS.put("F0B12FF6-7D20-4E49-8F19-EF5F1E9CBA6D",
                R.string.name_weapon_m39);
        WEAPONS.put("F3DF4C76-FD8F-0F11-3B8C-8B9C756EF089",
                R.string.name_weapon_m1014);
        WEAPONS.put("F3EF48EB-37C3-4F5E-A2ED-ACE7E4D419DD",
                R.string.name_weapon_pdr);
        WEAPONS.put("F444F973-F411-4616-9A85-395B8ED7FEF2",
                R.string.name_weapon_knife_razor);
        WEAPONS.put("FE05ACAA-32FC-4FD7-A34B-61413F6F7B1A",
                R.string.name_weapon_qbb95);
        WEAPONS.put("FEFBA819-898F-4B66-8596-B6576FA9B28A",
                R.string.name_weapon_svd);

        WEAPONS.put("ID_P_WNAME_MP7", R.string.name_weapon_mp7);
        WEAPONS.put("ID_P_WNAME_Glock17", R.string.name_weapon_glock17);
        WEAPONS.put("ID_P_WNAME_M98B", R.string.name_weapon_m98b);
        WEAPONS.put("ID_P_WNAME_553", R.string.name_weapon_553);
        WEAPONS.put("ID_P_WNAME_870", R.string.name_weapon_870);
        WEAPONS.put("ID_P_WNAME_MP443SILENCED",
                R.string.name_weapon_mp443silenced);
        WEAPONS.put("ID_P_WNAME_RPG7", R.string.name_weapon_rpg7);
        WEAPONS.put("ID_P_XP2_WNAME_MTAR21", R.string.name_weapon_mtar21);
        WEAPONS.put("ID_P_WNAME_KH2002", R.string.name_weapon_kh2002);
        WEAPONS.put("ID_P_WNAME_AKS74u", R.string.name_weapon_aks74u);
        WEAPONS.put("ID_P_WNAME_M1911SILENCED",
                R.string.name_weapon_m1911silenced);
        WEAPONS.put("ID_P_WNAME_M27", R.string.name_weapon_m27);
        WEAPONS.put("ID_P_WNAME_G36C", R.string.name_weapon_g36c);
        WEAPONS.put("ID_P_WNAME_Taurus44", R.string.name_weapon_taurus44);
        WEAPONS.put("ID_P_WNAME_ASVal", R.string.name_weapon_asval);
        WEAPONS.put("ID_P_WNAME_DAO12", R.string.name_weapon_dao12);
        WEAPONS.put("ID_P_WNAME_40MM", R.string.name_weapon_40mm);
        WEAPONS.put("ID_P_WNAME_UMP45", R.string.name_weapon_ump45);
        WEAPONS.put("ID_P_WNAME_M27", R.string.name_weapon_m27);
        WEAPONS.put("ID_P_WNAME_IGLA", R.string.name_weapon_igla);
        WEAPONS.put("ID_P_WNAME_SCARH", R.string.name_weapon_scarh);
        WEAPONS.put("ID_P_WNAME_M412Rex", R.string.name_weapon_m412rex);
        WEAPONS.put("ID_P_WNAME_M416", R.string.name_weapon_m416);
        WEAPONS.put("ID_P_WNAME_AK74M", R.string.name_weapon_ak74m);
        WEAPONS.put("ID_P_WNAME_Pecheng", R.string.name_weapon_pecheng);
        WEAPONS.put("ID_P_WNAME_M60", R.string.name_weapon_m60);
        WEAPONS.put("ID_P_WNAME_M240", R.string.name_weapon_m240);
        WEAPONS.put("ID_P_WNAME_M16A4", R.string.name_weapon_m16a4);
        WEAPONS.put("ID_P_WNAME_Taurus44", R.string.name_weapon_taurus44);
        WEAPONS.put("ID_P_WNAME_M1911LIT", R.string.name_weapon_m1911lit);
        WEAPONS.put("ID_P_WNAME_PP2000", R.string.name_weapon_pp2000);
        WEAPONS.put("ID_P_WNAME_G3", R.string.name_weapon_g3);
        WEAPONS.put("ID_P_WNAME_M4", R.string.name_weapon_m4);
        WEAPONS.put("ID_P_WNAME_AEK971", R.string.name_weapon_aek971);
        WEAPONS.put("ID_P_XP2_WNAME_STEYRAUG", R.string.name_weapon_steyraug);
        WEAPONS.put("ID_P_WNAME_Mk11", R.string.name_weapon_mk11);
        WEAPONS.put("ID_P_WNAME_AN94", R.string.name_weapon_an94);
        WEAPONS.put("ID_P_WNAME_M16", R.string.name_weapon_m16);
        WEAPONS.put("ID_P_WNAME_M4A1", R.string.name_weapon_m4a1);
        WEAPONS.put("ID_P_XP2_WNAME_SPAS12", R.string.name_weapon_spas12);
        WEAPONS.put("ID_P_WNAME_Taurus44SCOPED",
                R.string.name_weapon_taurus44scoped);
        WEAPONS.put("ID_P_WNAME_USAS12", R.string.name_weapon_usas12);
        WEAPONS.put("ID_P_WNAME_MP443", R.string.name_weapon_mp443);
        WEAPONS.put("ID_P_WNAME_RPK", R.string.name_weapon_rpk);
        WEAPONS.put("ID_P_WNAME_AK74M", R.string.name_weapon_ak74m);
        WEAPONS.put("ID_P_WNAME_knife", R.string.name_weapon_knife);
        WEAPONS.put("ID_P_XP1_WNAME_L85A2", R.string.name_weapon_l85a2);
        WEAPONS.put("ID_P_WNAME_SV98", R.string.name_weapon_sv98);
        WEAPONS.put("ID_P_WNAME_M40A5", R.string.name_weapon_m40a5);
        WEAPONS.put("ID_P_XP2_WNAME_SCARL", R.string.name_weapon_scarl);
        WEAPONS.put("ID_P_XP2_WNAME_ACR", R.string.name_weapon_acr);
        WEAPONS.put("ID_P_XP1_WNAME_MG36", R.string.name_weapon_mg36);
        WEAPONS.put("ID_P_WNAME_Saiga12", R.string.name_weapon_saiga12);
        WEAPONS.put("ID_P_XP1_XP1_WNAME_FAMAS", R.string.name_weapon_famas);
        WEAPONS.put("ID_P_WNAME_M1911TACTICAL",
                R.string.name_weapon_m1911tactical);
        WEAPONS.put("ID_P_WNAME_M93R", R.string.name_weapon_m93r);
        WEAPONS.put("ID_P_WNAME_AKS74u", R.string.name_weapon_aks74u);
        WEAPONS.put("ID_P_WNAME_M9SILENCED", R.string.name_weapon_m9silenced);
        WEAPONS.put("ID_P_WNAME_RPK", R.string.name_weapon_rpk);
        WEAPONS.put("ID_P_WNAME_M16A4", R.string.name_weapon_m16a4);
        WEAPONS.put("ID_P_WNAME_M1911", R.string.name_weapon_m1911);
        WEAPONS.put("ID_P_WNAME_M93R", R.string.name_weapon_m93r);
        WEAPONS.put("ID_P_WNAME_SVD", R.string.name_weapon_svd);
        WEAPONS.put("ID_P_XP1_WNAME_JACKHAMMER",
                R.string.name_weapon_jackhammer);
        WEAPONS.put("ID_P_WNAME_GLOCK18SILENCED",
                R.string.name_weapon_glock18silenced);
        WEAPONS.put("ID_P_WNAME_M249", R.string.name_weapon_m249);
        WEAPONS.put("ID_P_WNAME_GLOCK17SILENCED",
                R.string.name_weapon_glock17silenced);
        WEAPONS.put("ID_P_XP1_WNAME_QBU88", R.string.name_weapon_qbu88);
        WEAPONS.put("ID_P_WNAME_M9", R.string.name_weapon_m9);
        WEAPONS.put("ID_P_WNAME_M4A1", R.string.name_weapon_m4a1);
        WEAPONS.put("ID_P_WNAME_M26Mass", R.string.name_weapon_m26mass);
        WEAPONS.put("ID_P_XP2_WNAME_JNG90", R.string.name_weapon_jng90);
        WEAPONS.put("ID_P_XP2_WNAME_L86A1", R.string.name_weapon_l86a1);
        WEAPONS.put("ID_P_XP2_WNAME_LSAT", R.string.name_weapon_lsat);
        WEAPONS.put("ID_P_WNAME_SMAW", R.string.name_weapon_smaw);
        WEAPONS.put("ID_P_WNAME_P90", R.string.name_weapon_p90);
        WEAPONS.put("ID_P_XP2_WNAME_HK417", R.string.name_weapon_hk417);
        WEAPONS.put("ID_P_WNAME_A91", R.string.name_weapon_a91);
        WEAPONS.put("ID_P_WNAME_Mk11", R.string.name_weapon_mk11);
        WEAPONS.put("ID_P_WNAME_SKS", R.string.name_weapon_sks);
        WEAPONS.put("ID_P_XP1_WNAME_L96", R.string.name_weapon_l96);
        WEAPONS.put("ID_P_XP1_WNAME_PP19", R.string.name_weapon_pp19);
        WEAPONS.put("ID_P_WNAME_Type88", R.string.name_weapon_type88);
        WEAPONS.put("ID_P_XP1_WNAME_QBZ95B", R.string.name_weapon_qbz95b);
        WEAPONS.put("ID_P_WNAME_M9LIT", R.string.name_weapon_m9lit);
        WEAPONS.put("ID_P_WNAME_Glock18", R.string.name_weapon_glock18);
        WEAPONS.put("ID_P_WNAME_F2000", R.string.name_weapon_f2000);
        WEAPONS.put("ID_P_WNAME_Javelin", R.string.name_weapon_javelin);
        WEAPONS.put("ID_P_XP2_WNAME_MP5K", R.string.name_weapon_mp5k);
        WEAPONS.put("ID_P_WNAME_MP443LIT", R.string.name_weapon_mp443lit);
        WEAPONS.put("ID_P_XP1_WNAME_HK53", R.string.name_weapon_hk53);
        WEAPONS.put("ID_P_WNAME_Stinger", R.string.name_weapon_stinger);
        WEAPONS.put("ID_P_WNAME_M39", R.string.name_weapon_m39);
        WEAPONS.put("ID_P_WNAME_M39", R.string.name_weapon_m39);
        WEAPONS.put("ID_P_WNAME_M1014", R.string.name_weapon_m1014);
        WEAPONS.put("ID_P_WNAME_PDR", R.string.name_weapon_pdr);
        WEAPONS.put("ID_P_XP2_PREMIUM_WNAME_KNIFE_RAZOR", R.string.name_weapon_knife_razor);
        WEAPONS.put("ID_P_XP1_WNAME_QBB95", R.string.name_weapon_qbb95);
        WEAPONS.put("ID_P_WNAME_SVD", R.string.name_weapon_svd);

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
        WEAPON_ATTACHMENTS.put("ID_P_ANAME_M415", "M415 (3.4x)");
        WEAPON_ATTACHMENTS.put("ID_P_ANAME_MAG", "Extended Mags");
        WEAPON_ATTACHMENTS.put("ID_P_ANAME_RX01", "Reflex (RDS)");
        WEAPON_ATTACHMENTS.put("ID_P_ANAME_SGA_SLUG", "12G Slug");
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
        SKILLS.put("204407B2-2186-4526-A50F-6F423E455311",
                "SUPPRESSION RESIST (SQUAD)");
        SKILLS.put("CCBBB3DB-F134-4CCA-A2C4-C74CA618C9DF", "AMMO");
        SKILLS.put("B128BA3C-1AFA-4487-84EF-A17F0D4C90EB", "GRENADES (SQUAD)");
        SKILLS.put("4D450662-7615-4F14-9C19-A27A6B45BA93", "SUPRESSION (SQUAD)");
        SKILLS.put("B8CEA0C3-E1FF-47CB-9D00-7022D819F973", "SPRINT (SQUAD)");
        SKILLS.put("3991B59A-8CFB-4590-BAE2-64EED9B4E485", "SUPRESSION");
        SKILLS.put("9E4734B9-D312-4E38-84A7-73E0F1259EEB", "EXPLOSIVES (SQUAD)");
        SKILLS.put("9DFDE21C-A50D-48B7-B6C0-F1E4107D2F12",
                "FLAK JACKET (SQUAD)");
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

        ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_2", new String[]{
                "Professional Russian", "L85A2"});
        ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_3", new String[]{"Fixing it",
                "HK53"});
        ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_1", new String[]{
                "Best Friend Forever", "FAMAS"});
        ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_6", new String[]{
                "Keep your head down", "MG36"});
        ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_7", new String[]{"Specops",
                "QBU-88"});
        ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_4", new String[]{"It goes Boom",
                "QBZ-95B"});
        ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_5", new String[]{"Let it rain",
                "QBB-95"});
        ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_9", new String[]{
                "Familiar Territory", "PP-19"});
        ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_8", new String[]{"Creeping Death",
                "L96"});
        ASSIGNMENTS.put("ID_XP1_ASSIGNMENT_10", new String[]{
                "Scarred Veteran", "MK3A1"});

        ASSIGNMENTS.put("ID_PRE1_ASSIGNMENT_1", new String[]{"Life Saver",
                "Life Saver Dogtag"});
        ASSIGNMENTS.put("ID_PRE1_ASSIGNMENT_2", new String[]{
                "Bullet Provider", "Bullet Provider Dogtag"});
        ASSIGNMENTS.put("ID_PRE1_ASSIGNMENT_3", new String[]{
                "Location Scout", "Location Scout Dogtag"});
        ASSIGNMENTS.put("ID_PRE1_ASSIGNMENT_4", new String[]{
                "Wrench Wielder", "Wrench Wielder Dogtag"});
        ASSIGNMENTS.put("ID_PRE1_ASSIGNMENT_5", new String[]{
                "Jack of all trades", "Jack of all trades Dogtag"});
        ASSIGNMENTS.put("ID_PRE1_ASSIGNMENT_6", new String[]{
                "F2000 Specialist", "F2000 Woodland Oak Camo"});
        ASSIGNMENTS.put("ID_PRE1_ASSIGNMENT_7", new String[]{
                "Pecheneg Specialist", "Pecheneg Tactical Camo"});
        ASSIGNMENTS.put("ID_PRE1_ASSIGNMENT_8", new String[]{
                "L96 Specialist", "L96 Digital Woodland Camo"});
        ASSIGNMENTS.put("ID_PRE1_ASSIGNMENT_9", new String[]{
                "SCAR-H Specialist", "SCAR-H Desert Stripe Camo"});
        ASSIGNMENTS.put("ID_PRE1_ASSIGNMENT_10", new String[]{
                "Only for the Dedicated", "Secret item"});

        ASSIGNMENTS.put("ID_XP2_ASSIGNMENT_1", new String[]{"Shepard",
                "AUG A3"});
        ASSIGNMENTS.put("ID_XP2_ASSIGNMENT_2", new String[]{
                "Set us up the bomb", "SCAR-L"});
        ASSIGNMENTS.put("ID_XP2_ASSIGNMENT_3", new String[]{"No shortage",
                "L86A2"});
        ASSIGNMENTS.put("ID_XP2_ASSIGNMENT_4", new String[]{"Point Blank",
                "LSAT"});
        ASSIGNMENTS.put("ID_XP2_ASSIGNMENT_5", new String[]{"Done Fixing",
                "ACW-R"});
        ASSIGNMENTS.put("ID_XP2_ASSIGNMENT_6", new String[]{
                "My Own Terminator", "MTAR-21"});
        ASSIGNMENTS.put("ID_XP2_ASSIGNMENT_7", new String[]{"Team Player",
                "M417"});
        ASSIGNMENTS.put("ID_XP2_ASSIGNMENT_8", new String[]{"Bullet Point",
                "JNG-90"});
        ASSIGNMENTS.put("ID_XP2_ASSIGNMENT_9", new String[]{"These hurt too",
                "SPAS-12"});
        ASSIGNMENTS.put("ID_XP2_ASSIGNMENT_10", new String[]{
                "Hold the trigger", "M5K"});
    }

    // Getters

    public static String getWeaponTitle(Context c, String key) {
        return WEAPONS.containsKey(key) ? c.getString(WEAPONS.get(key)) : key;
    }

    public static int getWeaponTitleResourceId(String key) {
        return WEAPONS.containsKey(key) ? WEAPONS.get(key)
                : R.string.general_not_available;
    }

    public static String getAttachmentTitle(String key) {
        return WEAPON_ATTACHMENTS.containsKey(key) ? WEAPON_ATTACHMENTS
                .get(key) : key;
    }

    public static String getVehicleTitle(String key) {
        return VEHICLES.containsKey(key) ? VEHICLES.get(key).getName() : key;
    }

    public static String getVehicleUpgradeTitle(String key) {
        return VEHICLE_ADDONS.containsKey(key) ? VEHICLE_ADDONS.get(key) : key;
    }

    public static String getSkillTitle(String key) {
        return SKILLS.containsKey(key) ? SKILLS.get(key) : key;
    }

    public static String getKitTitle(String key) {
        return KIT_ITEMS.containsKey(key) ? KIT_ITEMS.get(key) : key;
    }

    public static String getUnlockGoal(String key) {
        return UNLOCK_GOALS.containsKey(key) ? UNLOCK_GOALS.get(key) : key;
    }

    public static String getMapTitle(String key) {
        return MAPS.containsKey(key) ? MAPS.get(key) : key;
    }

    public static String getCoopLevelTitle(String key) {
        return COOP_DATA.containsKey(key) ? COOP_DATA.get(key) : key;
    }

    public static String getDifficultyTitle(String key) {
        return DIFFICULTY_MAP.containsKey(key) ? DIFFICULTY_MAP.get(key) : key;
    }

    @Deprecated
    public static String[] getAssignmentTitle(String key) {
        return ASSIGNMENTS.containsKey(key) ? ASSIGNMENTS.get(key)
                : new String[]{key, key};
    }

    public static String getExpansionTitle(int key) {

        switch (key) {
            case 0:
                return "Open Beta";
            case 512:
                return "Back to Karkand";
            case 1024:
                return "Premium";
            case 2048:
                return "Close Quarters";
            case 4096:
                return "Armored Kill";
            case 8192:
                return "Aftermath";
            case 16384:
                return "Endgame";
            default:
                return "N/A(#" + key + ")";
        }
    }

    public static String getLanguage(int p) {
        return LANGUAGES[p];
    }

    public static String[] getLanguages() {
        return LANGUAGES;
    }

    public static String[] getLocales() {
        return LOCALES;
    }

    public static int getImageForPersona(String s) {
        return PERSONA_IMAGES.containsKey(s) ? PERSONA_IMAGES.get(s)
                : R.drawable.bf3_persona_none;
    }

    public static Intent getContactIntent(int res) {
        return CONTACT_INTENTS.get(res);
    }

    public static String getLocale(int p) {
        switch (p) {
            case 1:
                return "en";
            case 2:
                return "de";
            case 4:
                return "fr";
            case 8:
                return "es";
            case 16:
                return "it";
            case 32:
                return "pl";
            case 64:
                return "ru";
            case 128:
                return "jp";
            case 256:
                return "kr";
            case 512:
                return "zh";
            case 1024:
                return "cz";
            default:
                return "en";
        }
    }

    // Static methods
    public static int getPlatformIdFromName(String p) {
        for (int i = 0, max = PLATFORMS.length; i < max; i++) {
            if (DataBank.PLATFORMS[i].getName().equals(p)) {
                return DataBank.PLATFORMS[i].getId();
            }
        }
        return DataBank.PLATFORMS[0].getId();
    }

    public static String getKitTitle(final Context c, final int number) {
        switch (number) {
            case 1:
                return c.getString(R.string.info_xml_assault);
            case 2:
                return c.getString(R.string.info_xml_engineer);
            case 8:
                return c.getString(R.string.info_xml_recon);
            case 16:
                return c.getString(R.string.info_xml_vehicles);
            case 32:
                return c.getString(R.string.info_xml_support);
            case 64:
                return c.getString(R.string.info_xml_general);
            default:
                return "";
        }
    }

    public static int getKitIdFromTitle(final Context c, final String s) {
        if (s.equalsIgnoreCase("assault")) {
            return 1;
        } else if (s.equalsIgnoreCase("engineer")) {
            return 2;
        } else if (s.equalsIgnoreCase("recon")) {
            return 8;
        } else if (s.equalsIgnoreCase("vehicles")) {
            return 16;
        } else if (s.equalsIgnoreCase("support")) {
            return 32;
        } else if (s.equalsIgnoreCase("general")) {
            return 64;
        } else {
            return 64;
        }
    }

    public static String getGameModeFromId(int number) {
        switch (number) {
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

    public static String getGameFromId(int id) {
        if (id == 2) {
            return "Battlefield 3";
        } else if (id == 4096) {
            return "Medal of Honor: Warfighter";
        } else {
            return "N/A";
        }
    }

    public static String getRoleFromId(Context c, int number) {
        switch (number) {
            case 0:
                return c.getString(R.string.info_platoon_member_nonmember);
            case 1:
                return c.getString(R.string.info_platoon_member_evaluate);
            case 2:
                return c.getString(R.string.info_platoon_member_invited);
            case 4:
                return c.getString(R.string.info_platoon_member_member);
            case 128:
                return c.getString(R.string.info_platoon_member_admin);
            case 256:
                return c.getString(R.string.info_platoon_member_founder);
            default:
                return "(unknown user role)";
        }
    }

    public static Map<Integer, Intent> getContactIntents() {
        return CONTACT_INTENTS;
    }
}