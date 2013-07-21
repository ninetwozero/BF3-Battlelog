/*
    This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.misc;

import com.ninetwozero.bf3droid.R;

import java.util.HashMap;

public final class DrawableResourceList {

    // Attributes
    private static HashMap<String, Integer> weapons; // <GUID, resId>
    private static HashMap<String, Integer> attachments; // <GUID, resId>
    private static HashMap<String, Integer> vehicles; // <GUID, resId>
    private static HashMap<String, Integer> vehicleUpgrades; // <GUID, resId>
    private static HashMap<String, Integer> kits; // <GUID, resId>
    private static HashMap<String, Integer> awards; // <GUID, resId>
    private static HashMap<String, Integer> skills; // <GUID, resId>
    private static HashMap<String, Integer> dogtags; // <GUID, resId>

    // Construct = none
    private DrawableResourceList() {
    }

    static {

        // Let's init
        weapons = new HashMap<String, Integer>();
        attachments = new HashMap<String, Integer>();
        vehicles = new HashMap<String, Integer>();
        vehicleUpgrades = new HashMap<String, Integer>();
        kits = new HashMap<String, Integer>();
        awards = new HashMap<String, Integer>();
        skills = new HashMap<String, Integer>();
        dogtags = new HashMap<String, Integer>();

        weapons.put("75D1FFC8-D442-4212-B668-96AED9030FC6", R.drawable.w_rpk);
        weapons.put("DC356150-2A5F-4FCA-BE6C-B993EE7F8A8B", R.drawable.w_fgm148);
        weapons.put("CB018ADD-3648-4504-9359-9BAFB8D92F7D", R.drawable.w_mk11_russian);
        weapons.put("A9F5B1F6-D83E-4BD8-AFE8-08C4B0A3E697", R.drawable.w_svd_american);
        weapons.put("CB651B07-2CE4-4527-B1AC-2AEB6D04CBF5", R.drawable.w_sks);
        weapons.put("F0B12FF6-7D20-4E49-8F19-EF5F1E9CBA6D", R.drawable.w_m39);
        weapons.put("EB17660D-D81B-4BB7-BE95-70662855489E", R.drawable.w_m39);
        weapons.put("3F06931A-443C-6E78-DEF9-A33EB9F43D35", R.drawable.w_pecheneg);
        weapons.put("3A6B6A16-E5A1-33E0-5B53-56E77833DAF4", R.drawable.w_m416);
        weapons.put("5244385C-B7ED-4266-AB7C-C1C1B222A9CD", R.drawable.w_m4a1);
        weapons.put("42DB9F03-0224-4676-99FC-ED444E356290", R.drawable.w_m16a4_russian);
        weapons.put("1E5E0296-CFD0-448E-B1D7-F795E8F98E2C", R.drawable.w_g36c);
        weapons.put("38C20C39-EE43-489F-AE95-DF0519F72409", R.drawable.w_mp412rex);
        weapons.put("9351DDBF-795A-4BC6-84D7-37B537E3D049", R.drawable.w_sv98);
        weapons.put("A4F108EB-1FA2-4C94-93FE-357B1D7EBF4A", R.drawable.w_aks74u);
        weapons.put("08F58ECD-BC99-48AA-A9B3-47D412E99A4E", R.drawable.w_rpg7);
        weapons.put("96FC0A67-DEA2-4061-B955-E173A8DBB00D", R.drawable.w_saiga12);
        weapons.put("04FD6527-0BF0-4A67-9ABB-9F992BF2CBA0", R.drawable.w_glock17);
        weapons.put("65D4A9F9-0ACD-46FD-9AE2-3E9670DD22FB", R.drawable.w_an94);
        weapons.put("1C689273-7637-40FF-89A5-E75F120D9E02", R.drawable.w_m27_russian);
        weapons.put("4F90708E-6875-4A5E-B685-CAF310A0BA95", R.drawable.w_m1911flashlight_fancy);
        weapons.put("386F9329-7DE7-6FB9-1366-2877C698D9B7", R.drawable.w_scarh);
        weapons.put("655D5E41-6DB8-4F3C-9F2A-8117AE11699C", R.drawable.w_mk11);
        weapons.put("AC994B66-DA51-42FB-A234-FCBA33EB9AB7", R.drawable.w_glock18_silenced);
        weapons.put("2A267103-14F2-4255-B0D4-819139A4E202", R.drawable.w_ump);
        weapons.put("F3EF48EB-37C3-4F5E-A2ED-ACE7E4D419DD", R.drawable.w_magpul);
        weapons.put("05EB2892-8B51-488E-8956-4350C3D2BA27", R.drawable.w_m98b);
        weapons.put("A4F683C2-40E2-464C-AE85-AFE4462F2D40", R.drawable.w_m9_silenced);
        weapons.put("07A6AB6A-457D-4481-94F9-A3FE15C3D923", R.drawable.w_mp443_silenced);
        weapons.put("6F741867-AE83-CC7D-BFB1-035452D7A5B4", R.drawable.w_usas12);
        weapons.put("FE05ACAA-32FC-4FD7-A34B-61413F6F7B1A", R.drawable.w_xp1_qbb95);
        weapons.put("E7266EC2-4977-60F2-A7CB-6EF7D98A5E2E", R.drawable.w_xp1_hk53);
        weapons.put("4D06B1BF-8002-44E2-851E-EAAC8C4FDD1A", R.drawable.w_taurus44);
        weapons.put("A76BB99E-ABFE-48E9-9972-5D87E5365DAB", R.drawable.w_m1911);
        weapons.put("AFF7E14E-5918-456B-9922-6ED1A50F3F15", R.drawable.w_xp1_qbu88);
        weapons.put("5E2D49D1-D1BB-F553-78A5-8D537C43E624", R.drawable.w_aek971);
        weapons.put("405F32BB-3E1A-4201-B96D-10B231D91BA5", R.drawable.w_m60);
        weapons.put("F3DF4C76-FD8F-0F11-3B8C-8B9C756EF089", R.drawable.w_m1014);
        weapons.put("A7278B05-8D76-4A40-B65D-4414490F6886", R.drawable.w_m16a4);
        weapons.put("512819DB-3E82-33B7-F1D5-E612C9A396BF", R.drawable.w_g3);
        weapons.put("1EA227D8-2EB5-A63B-52FF-BBA9CFE34AD8", R.drawable.w_taurus44);
        weapons.put("120A0838-9E95-4564-A6F6-5A14A1E0AF85", R.drawable.w_aks74u_american);
        weapons.put("AEFD4BF4-4C08-4834-9DFF-1F7C529175AF", R.drawable.w_glock17_silenced);
        weapons.put("283C66DE-3866-46CD-A1C0-B456A5916537", R.drawable.w_m320);
        weapons.put("DB364A96-08FB-4C6E-856B-BD9749AE0A92", R.drawable.w_glock18);
        weapons.put("69A28562-5569-4D76-82FB-98C4047306F1", R.drawable.w_m4a1_russian);
        weapons.put("A0222E93-0887-4FC4-90D4-51838945343E", R.drawable.w_m93r);
        weapons.put("DB94F5EC-74D5-4DB2-9A15-E0C4154BFFD4", R.drawable.w_f2000);
        weapons.put("1C426722-C0B6-492F-98B2-3B9D2B97C808", R.drawable.w_m1911silenced_fancy);
        weapons.put("E43287AB-529D-4803-B585-6C17E2DD6AEB", R.drawable.w_mp443_flashlight);
        weapons.put("6F3993B9-E8B2-412F-9361-341140FCBF79", R.drawable.w_taurus_scope);
        weapons.put("CBAEC77C-A6AD-4D63-96BD-61FCA6C18417", R.drawable.w_xp1_l96);
        weapons.put("A607E88D-90B0-4ECD-B892-8BF66AEF90ED", R.drawable.w_rpk_american);
        weapons.put("9A97A9FE-DCE5-41E8-8D89-A421B103FA75", R.drawable.w_xp1_famas);
        weapons.put("3BA55147-6619-4697-8E2B-AC6B1D183C0E", R.drawable.w_ak74m);
        weapons.put("AEAA518B-9253-40C2-AA18-A11F8F2D474C", R.drawable.w_m249);
        weapons.put("C79AAC6E-566E-40E1-B373-3B0029530393", R.drawable.w_a91);
        weapons.put("21A80B6B-8FA6-4BCC-84D3-7ED782E978FA", R.drawable.w_asval);
        weapons.put("C12E6868-FC08-4E25-8AD0-1C51201EA69B", R.drawable.w_p90);
        weapons.put("A8C7508A-F43B-4446-ACCE-F350EDFEDB28", R.drawable.w_m93r);
        weapons.put("CECC74B7-403F-4BA1-8ECD-4A59FB5379BD", R.drawable.w_xp1_pp19);
        weapons.put("27F63AEA-DD70-4929-9B08-5FF8F075B75E", R.drawable.w_dao12);
        weapons.put("93CC5226-4381-7458-509E-B2D6F4498164", R.drawable.w_m40a5);
        weapons.put("6921F5C9-3487-43E6-86F5-296DA097FFFB", R.drawable.w_m16a4);
        weapons.put("D4FF4D2C-361F-491E-B53D-207CF77FA609", R.drawable.w_m9_flashlight);
        weapons.put("50849B49-F3DA-4C92-9830-D4A2932BC9E7", R.drawable.w_pp2000);
        weapons.put("04C8604E-37DE-4B51-B70A-66468003D604", R.drawable.w_mp7);
        weapons.put("84B78F21-217A-4A46-A06E-34A90637BAC8", R.drawable.w_ak74m_american);
        weapons.put("B2DEF86D-A127-769E-23ED-C9F47F29FAD3", R.drawable.w_m26mass);
        weapons.put("71B0A1D6-9E4F-40A3-9906-1A7F3AAD573A", R.drawable.w_mp443_grach);
        weapons.put("8DCA9ABD-0723-454C-9575-7E4CA0791D0B", R.drawable.w_xp1_l85a2);
        weapons.put("07A4C87A-D325-4A73-8C5A-C001ACD13334", R.drawable.w_remington870);
        weapons.put("32B899E5-0542-45E6-A34B-86871C7FE098", R.drawable.w_m27);
        weapons.put("0733BF61-8EBC-4666-9610-7E27D7313791", R.drawable.w_sg553lb);
        weapons.put("D0E124FB-7116-4FBB-AF00-D8994AEB548D", R.drawable.w_type88);
        weapons.put("35796A7B-C7FA-4C0F-AD99-3DDB3B60A293", R.drawable.w_sa18igla);
        weapons.put("8963F500-E71D-41FC-4B24-AE17D18D8C73", R.drawable.w_knife);
        weapons.put("414C4598-4089-43E0-82FB-BBF7031D02E8", R.drawable.w_m240);
        weapons.put("FEFBA819-898F-4B66-8596-B6576FA9B28A", R.drawable.w_svd);
        weapons.put("0E0E4701-359B-48FF-B91A-F4B6373435E4", R.drawable.w_kh2002);
        weapons.put("AAE173E4-8DD7-5C25-1763-7A4D9380EB10", R.drawable.w_xp1_jackhammer);
        weapons.put("9B3AF503-2018-4BC9-893F-CD393D3BAD77", R.drawable.w_m1911tactical_fancy);
        weapons.put("E9BEDD8F-899F-3A3C-C561-5E58B350C60D", R.drawable.w_fim92a_stinger);
        weapons.put("B145A444-BC4D-48BF-806A-0CEFA0EC231B", R.drawable.w_m9);
        weapons.put("B1575807-C480-7286-719C-EE2520292A79", R.drawable.w_m4a1);
        weapons.put("D20984F3-364E-4C06-9879-09280EDF6DF3", R.drawable.w_xp1_qbz95b);
        weapons.put("95E00B23-BAD4-4F3B-A85E-990204EFF26B", R.drawable.w_xp1_mg36);
        weapons.put("BFAC29DB-5193-4E69-96D9-37D4124C44C2", R.drawable.w_smaw);
        /* CLOSE QUARTERS EXPANSION */
        weapons.put("94FADBCE-8D16-4736-85E8-D42FADCD174F", R.drawable.w_xp2_scarl);
        weapons.put("DFBF6EA5-39C5-4ABA-B2C6-CAA6AD6C3786", R.drawable.w_xp2_mp5k);
        weapons.put("95AA1865-4324-4B23-BCC4-84F8F4D91922", R.drawable.w_xp2_acr);
        weapons.put("B8CA6D09-62C2-4208-A094-B8E50F716E47", R.drawable.w_xp2_jng90);
        weapons.put("60F775C4-7D70-4898-BD00-03AA60C8CE91", R.drawable.w_xp2_steyraug);
        weapons.put("BA0AF247-2E5B-4574-8F89-515DFA1C767D", R.drawable.w_xp2_l86);
        weapons.put("C48BC95B-1271-4F19-9D6C-A91C836F5432", R.drawable.w_xp2_hk417);
        weapons.put("6D99F118-04BD-449A-BA0E-1978DDF5894D", R.drawable.w_xp2_spas12);
        weapons.put("0A54B1A8-0368-4939-9558-D31E40FE488F", R.drawable.w_xp2_mtar);
        weapons.put("BF6E6CB2-D5AA-4425-A0C8-0FB8D89A1372", R.drawable.w_xp2_lsat);
        weapons.put("F444F973-F411-4616-9A85-395B8ED7FEF2", R.drawable.w_combatknife);
        weapons.put("B20C05A8-D9E7-4ECB-AE8C-DEAD08A2E61E", R.drawable.w_xp4_crossbowkobra);
        weapons.put("12DA9127-4627-4A8F-88E5-1D43B4B9FBB6", R.drawable.w_xp4_crossbowriflescope);

        // Set up the vehicles
        vehicles.put("F998F5E4-220D-463A-A437-1C18D5C3A19E", R.drawable.v_btr90);
        vehicles.put("89BEA1D6-2FF4-11DE-8C23-DAB1D69416DE", R.drawable.v_mi28);
        vehicles.put("C645317B-45BB-E082-7E5C-918388C22D59", R.drawable.v_pantsir);
        vehicles.put("98E5B9BF-0B68-4AF0-A20D-1A23D6B6BF8B", R.drawable.v_vodnik);
        vehicles.put("D780AFF6-38B7-11DE-BF1C-984D9AEE762C", R.drawable.v_z11);
        vehicles.put("60106975-DD7D-11DD-A030-B04E425BA11E", R.drawable.v_t90);
        vehicles.put("860157CA-6527-4123-B60E-71117DD878D7", R.drawable.v_lav_ad);
        vehicles.put("E7A99B55-B5BD-C101-2384-97458D4AC23C", R.drawable.v_growler);
        vehicles.put("F1FFAE42-B67A-4E4F-2626-3CBF37AE287B", R.drawable.v_su35);
        vehicles.put("CD8C281F-579D-4E7B-BE3D-F206E91407F8", R.drawable.v_su39);
        vehicles.put("D35CA587-79AF-D351-6F65-967794C7F1B7", R.drawable.v_centurion);
        vehicles.put("B26FD546-2ADF-1A90-3044-F7748B86DA26", R.drawable.v_rhib);
        vehicles.put("A676D498-A524-42AD-BE78-72B071D8CD6A", R.drawable.v_ah1z);
        vehicles.put("B3E9860F-EE10-44F3-B4DC-5730BE251159", R.drawable.v_a10);
        vehicles.put("D7BAB9C1-1208-4923-BD3A-56EB945E04E1", R.drawable.v_kornet);
        vehicles.put("9D35A483-0B6B-91AE-5025-351AD87B2B46", R.drawable.v_dpv);
        vehicles.put("C81F8757-E6D2-DF2D-1CFE-B72B4F74FE98", R.drawable.v_f18);
        vehicles.put("0E09B2D0-BA4A-1509-E1D2-949FB0C04DBE", R.drawable.v_venom);
        vehicles.put("3F18FCA6-A7D4-D3B5-28E5-44A5CAFFE6BE", R.drawable.v_f35);
        vehicles.put("B06A08AB-EECF-11DD-8117-9421284A74E5", R.drawable.v_m1a2);
        vehicles.put("37A53096-BA80-5498-1347-8C7B238680C8", R.drawable.v_tow);
        vehicles.put("B9BAE0F8-72B9-4E1C-B5AD-F9353727C990", R.drawable.v_ka60);
        vehicles.put("ADF563C9-28B1-C42B-993E-B2FD40F36078", R.drawable.v_lav_25);
        vehicles.put("D68E417F-6103-5140-3ABC-4C7505160A09", R.drawable.v_vdv);
        vehicles.put("D1B516CA-6119-F025-C923-1B0700B6AEBA", R.drawable.v_humvee);
        vehicles.put("1E8653E6-11A0-DF93-C808-E48351D2F578", R.drawable.v_aav);
        vehicles.put("FD8AB748-FF4D-11DD-A7B1-F7C6DEEC9D32", R.drawable.v_ah6);
        vehicles.put("74866776-D5AF-BD32-7964-CD234506235D", R.drawable.v_skidloader);
        vehicles.put("AAE95907-AFD4-11DD-84FB-9FA71F68ED5E", R.drawable.v_bmp2);
        vehicles.put("A36C9712-54B3-A5FF-8627-7BC7EFA0C668", R.drawable.v_tunguska);
        vehicles.put("19B63F53-5FD5-4A82-9EF8-B43A2243E9D9", R.drawable.v_centurion);

        // Vehicle upgrades
        vehicleUpgrades.put("ID_P_VUNAME_JETAVIONIC", R.drawable.vu_jetavionics);
        vehicleUpgrades.put("ID_P_VUNAME_AHBRADAR", R.drawable.vu_attackhelibelowradar);
        vehicleUpgrades.put("ID_P_VUNAME_MBTPREVENT", R.drawable.vu_mbtpreventive);
        vehicleUpgrades.put("ID_P_VUNAME_IFVENVG", R.drawable.vu_ifvenvg);
        vehicleUpgrades.put("ID_P_VUNAME_JETECM", R.drawable.vu_jetecm);
        vehicleUpgrades.put("ID_P_VUNAME_AAARADAR", R.drawable.vu_aaradar);
        vehicleUpgrades.put("ID_P_VUNAME_SHFLARE", R.drawable.vu_scoutflares);
        vehicleUpgrades.put("ID_P_VUNAME_JETSTEALTH", R.drawable.vu_jetstealth);
        vehicleUpgrades.put("ID_P_VUNAME_AHECM", R.drawable.vu_attackheliecm);
        vehicleUpgrades.put("ID_P_VUNAME_MBTCITV", R.drawable.vu_mbtcitv);
        vehicleUpgrades.put("ID_P_VUNAME_AASTEALTH", R.drawable.vu_aastealth);
        vehicleUpgrades.put("ID_P_VUNAME_JETARADAR", R.drawable.vu_jetradar);
        vehicleUpgrades.put("ID_P_VUNAME_AHARADAR", R.drawable.vu_attackheliradar);
        vehicleUpgrades.put("ID_P_VUNAME_AAPROX", R.drawable.vu_aaproximity);
        vehicleUpgrades.put("ID_P_VUNAME_IFVSMOKE", R.drawable.vu_ifvsmoke);
        vehicleUpgrades.put("ID_P_VUNAME_SHFIREEX", R.drawable.vu_scoutextinguisher);
        vehicleUpgrades.put("ID_P_VUNAME_IFVCOAX", R.drawable.vu_ifvcoax);
        vehicleUpgrades.put("ID_P_VUNAME_MBTWPNEFF", R.drawable.vu_mbtweaponeff);
        vehicleUpgrades.put("ID_P_VUNAME_JETFIREEX", R.drawable.vu_jetextinguisher);
        vehicleUpgrades.put("ID_P_VUNAME_SHECM", R.drawable.vu_scoutecm);
        vehicleUpgrades.put("ID_P_VUNAME_AAPREVENT", R.drawable.vu_aapreventive);
        vehicleUpgrades.put("ID_P_VUNAME_JETMAVER", R.drawable.vu_jetmaverik);
        vehicleUpgrades.put("ID_P_VUNAME_MBTCANISTER", R.drawable.vu_mbtcanister);
        vehicleUpgrades.put("ID_P_VUNAME_SHPREV", R.drawable.vu_scoutpreventive);
        vehicleUpgrades.put("ID_P_VUNAME_MBTCOAX", R.drawable.vu_mbtcoax);
        vehicleUpgrades.put("ID_P_VUNAME_AHFLARE", R.drawable.vu_attackheliflares);
        vehicleUpgrades.put("ID_P_VUNAME_AAARMOR", R.drawable.vu_aaarmor);
        vehicleUpgrades.put("ID_P_VUNAME_AHSTEALTH", R.drawable.vu_attackhelistealth);
        vehicleUpgrades.put("ID_P_VUNAME_IFVSTEALTH", R.drawable.vu_ifvstealth);
        vehicleUpgrades.put("ID_P_VUNAME_AHGUIDE", R.drawable.vu_attackhelirocketguide);
        vehicleUpgrades.put("ID_P_VUNAME_AASMOKE", R.drawable.vu_aasmoke);
        vehicleUpgrades.put("ID_P_VUNAME_AHFIREEX", R.drawable.vu_attackheliextinguisher);
        vehicleUpgrades.put("ID_P_VUNAME_MBTATGM", R.drawable.vu_mbtatgm);
        vehicleUpgrades.put("ID_P_VUNAME_AHAA", R.drawable.vu_attackhelisidewinders);
        vehicleUpgrades.put("ID_P_VUNAME_MBTARMOR", R.drawable.vu_mbtarmor);
        vehicleUpgrades.put("ID_P_VUNAME_SHARADAR", R.drawable.vu_scoutradar);
        vehicleUpgrades.put("ID_P_VUNAME_SHBRADAR", R.drawable.vu_scoutbelowradar);
        vehicleUpgrades.put("ID_P_VUNAME_SHPROX", R.drawable.vu_scoutproximity);
        vehicleUpgrades.put("ID_P_VUNAME_AHPROX", R.drawable.vu_attackheliproximity);
        vehicleUpgrades.put("ID_P_VUNAME_AAZOOM", R.drawable.vu_aazoom);
        vehicleUpgrades.put("ID_P_VUNAME_IFVHELL", R.drawable.vu_ifvhellfire);
        vehicleUpgrades.put("ID_P_VUNAME_SHAA", R.drawable.vu_scoutsidewinder);
        vehicleUpgrades.put("ID_P_VUNAME_JETBRADAR", R.drawable.vu_jetbelowradar);
        vehicleUpgrades.put("ID_P_VUNAME_AHWPNEFF", R.drawable.vu_attackheliweaponefficiency);
        vehicleUpgrades.put("ID_P_VUNAME_AAENVG", R.drawable.vu_aaenvg);
        vehicleUpgrades.put("ID_P_VUNAME_MBTZOOM", R.drawable.vu_mbtzoom);
        vehicleUpgrades.put("ID_P_VUNAME_SHLASER", R.drawable.vu_scoutlaser);
        vehicleUpgrades.put("ID_P_VUNAME_AHTVG", R.drawable.vu_attackhelitvg);
        vehicleUpgrades.put("ID_P_VUNAME_IFVPREVENT", R.drawable.vu_ifvpreventive);
        vehicleUpgrades.put("ID_P_VUNAME_AAWPNEFF", R.drawable.vu_aaweaponefficiency);
        vehicleUpgrades.put("ID_P_VUNAME_MBTHMG", R.drawable.vu_mbthmg);
        vehicleUpgrades.put("ID_P_VUNAME_IFVWPNEFF", R.drawable.vu_ifvweaponefficiency);
        vehicleUpgrades.put("ID_P_VUNAME_IFVZOOM", R.drawable.vu_ifvzoom);
        vehicleUpgrades.put("ID_P_VUNAME_IFVAPFSDS", R.drawable.vu_ifvapfsds_t);
        vehicleUpgrades.put("ID_P_VUNAME_IFVAT", R.drawable.vu_ifvtow);
        vehicleUpgrades.put("ID_P_VUNAME_MBTSMOKE", R.drawable.vu_mbtsmoke);
        vehicleUpgrades.put("ID_P_VUNAME_IFVPROX", R.drawable.vu_ifvproximity);
        vehicleUpgrades.put("ID_P_VUNAME_AHLASER", R.drawable.vu_attackhelilaser);
        vehicleUpgrades.put("ID_P_VUNAME_IFVARMOR", R.drawable.vu_ifvarmor);
        vehicleUpgrades.put("ID_P_VUNAME_MBTENVG", R.drawable.vu_mbtenvg);
        vehicleUpgrades.put("ID_P_VUNAME_JETPROX", R.drawable.vu_jetproximity);
        vehicleUpgrades.put("ID_P_VUNAME_JETAA", R.drawable.vu_jetsidewinder);
        vehicleUpgrades.put("ID_P_VUNAME_SHHELL", R.drawable.vu_scouthellfire);
        vehicleUpgrades.put("ID_P_VUNAME_JETFLARE", R.drawable.vu_jetflares);
        vehicleUpgrades.put("ID_P_VUNAME_JETROCKET", R.drawable.vu_rocketpod);
        vehicleUpgrades.put("ID_P_VUNAME_MBTSTEALTH", R.drawable.vu_mbtstealth);
        vehicleUpgrades.put("ID_P_VUNAME_AHPREV", R.drawable.vu_attackhelipreventive);
        vehicleUpgrades.put("ID_P_VUNAME_AHENVG", R.drawable.vu_attackhelienvg);
        vehicleUpgrades.put("ID_P_VUNAME_JETPREV", R.drawable.vu_jetpreventive);
        vehicleUpgrades.put("ID_P_VUNAME_AHZOOM", R.drawable.vu_attackhelizoom);
        vehicleUpgrades.put("ID_P_VUNAME_SHWPNEFF", R.drawable.vu_scoutweaponefficiency);
        vehicleUpgrades.put("ID_P_VUNAME_JETWPNEFF", R.drawable.vu_jetweaponefficiency);
        vehicleUpgrades.put("ID_P_VUNAME_MBTPROX", R.drawable.vu_mbtproximity);
        vehicleUpgrades.put("ID_P_VUNAME_AHGHELL", R.drawable.vu_attackhelihellfire);
        vehicleUpgrades.put("ID_P_VUNAME_AAAA", R.drawable.vu_aastinger);
        vehicleUpgrades.put("ID_P_VUNAME_SHSTEALTH", R.drawable.vu_scoutstealth);

        // Attachments
        attachments.put("ID_P_ANAME_FLECHETTE", R.drawable.a_flechette);
        attachments.put("ID_P_ANAME_FOREGRIP", R.drawable.a_foregrip);
        attachments.put("55ADFBFC-12CC-28E7-4DA4-3D4C49CDF7F3", R.drawable.a_40mm_shotgunshell);
        attachments.put("ID_P_ANAME_RX01", R.drawable.a_rx01);
        attachments.put("ID_P_ANAME_PKS", R.drawable.a_pks07);
        attachments.put("ID_P_ANAME_SGA_SLUG", R.drawable.a_slug);
        attachments.put("ID_P_ANAME_BARREL", R.drawable.a_heavybarrel);
        attachments.put("ID_P_ANAME_SLUG", R.drawable.a_slug);
        attachments.put("ID_P_ANAME_BOLT", R.drawable.a_straightbolt);
        attachments.put("ID_P_ANAME_RIFLE", R.drawable.a_riflescope);
        attachments.put("ID_P_ANAME_PKA", R.drawable.a_pka);
        attachments.put("Ballistic (20x)", R.drawable.a_ballistic);
        attachments.put("ID_P_ANAME_PKAS", R.drawable.a_pkas);
        attachments.put("ID_P_ANAME_EOTECH", R.drawable.a_eotech);
        attachments.put("ID_P_ANAME_LIGHT", R.drawable.a_flashlight);
        attachments.put("ID_P_ANAME_SUPPRESS", R.drawable.a_flashsuppressor);
        attachments.put("ID_P_ANAME_LASER", R.drawable.a_targetpointer);
        attachments.put("ID_P_ANAME_IRNV", R.drawable.a_irnv);
        attachments.put("ID_P_ANAME_BIPOD", R.drawable.a_bipod);
        attachments.put("ID_P_ANAME_KOBRA", R.drawable.a_kobra);
        attachments.put("ID_P_ANAME_PSO", R.drawable.a_pso);
        attachments.put("ID_P_ANAME_SILENCER", R.drawable.a_soundsuppressor);
        attachments.put("ID_P_ANAME_BALL", R.drawable.a_ballistic);
        attachments.put("ID_P_ANAME_ACOG", R.drawable.a_acog);
        attachments.put("ID_P_ANAME_SNIPERRIFLE", R.drawable.a_riflescope);
        attachments.put("FE6292D5-79AD-1230-7B9A-AF80D020E256", R.drawable.a_40mm_smoke);
        attachments.put("ID_P_ANAME_MAG", R.drawable.a_ext_mag01);
        attachments.put("ID_P_ANAME_FRAG", R.drawable.a_frag);
        attachments.put("ID_P_ANAME_M145", R.drawable.a_m145);
        attachments.put("ID_P_ANAME_SGA_FLECH", R.drawable.a_flechette);
        attachments.put("ID_P_ANAME_BOLT_STANDARD", R.drawable.a_bolt_standard);
        attachments.put("ID_P_ANAME_BOLT_SCAN", R.drawable.a_bolt_scan);
        attachments.put("ID_P_ANAME_BOLT_BA", R.drawable.a_bolt_ba);
        attachments.put("ID_P_ANAME_BOLT_HE", R.drawable.a_bolt_he);

        // Kits
        kits.put("DC9734CD-D3D7-4870-A6A9-07B99BEE6DAC", R.drawable.k_soflam);
        kits.put("90E12AB5-CF0F-4439-AFD0-C86E6C71BB7D", R.drawable.k_mine);
        kits.put("6EF48118-EF16-4D47-BD18-F57792D88AB1", R.drawable.k_m224_mortar);
        kits.put("3DBDDF9C-C07D-4714-89D2-178B9AE0C42B", R.drawable.k_repairtool);
        kits.put("9F789F05-CE7B-DADC-87D7-16E847DBDD09", R.drawable.k_m67_grenade);
        kits.put("2430C5A8-AB47-406F-B983-1BF7289CF8E6", R.drawable.k_tugs);
        kits.put("13C4927A-C5B3-4075-A570-7FAA6A712C18", R.drawable.k_eod);
        kits.put("D78EB213-CCB5-43FE-B148-E581575036B4", R.drawable.k_ammobag);
        kits.put("1122B462-64B1-4AE6-8ED4-CEA3BF1BDFEF", R.drawable.k_c4);
        kits.put("3EA94E83-0E83-4ABF-9D8E-0B37DA37A243", R.drawable.k_mav);
        kits.put("6369B996-235A-AF95-4AE3-118E46C3926C", R.drawable.k_radiobeacon);
        kits.put("4C73E401-E151-459E-BD40-C394437533EA", R.drawable.k_claymore);
        kits.put("7D11603B-8188-45FD-AD95-B27A4B35980E", R.drawable.k_defib);
        kits.put("00F16262-38F3-45F0-B577-C243CDB10A9E", R.drawable.k_medkit);

        // Skills
        skills.put("BCE7C360-DFE6-4BF0-B020-7F2482177652", R.drawable.s_explresist);
        skills.put("7B001B2F-5B24-4AE8-9C2E-3B8663E2F6C0", R.drawable.s_explosives);
        skills.put("A8C8F62B-0E6C-462B-8489-4A5C46C9170F", R.drawable.s_grenades);
        skills.put("5F1603F6-C6F5-438D-AF7A-FD45258BB81C", R.drawable.s_ammo2);
        skills.put("9A845313-8450-442C-A30F-FAF5977B3DBA", R.drawable.s_supprresist);
        skills.put("204407B2-2186-4526-A50F-6F423E455311",
                R.drawable.s_supprresist2);
        skills.put("CCBBB3DB-F134-4CCA-A2C4-C74CA618C9DF", R.drawable.s_ammo);
        skills.put("B128BA3C-1AFA-4487-84EF-A17F0D4C90EB", R.drawable.s_grenades2);
        skills.put("4D450662-7615-4F14-9C19-A27A6B45BA93", R.drawable.s_suppression2);
        skills.put("B8CEA0C3-E1FF-47CB-9D00-7022D819F973", R.drawable.s_sprint2);
        skills.put("3991B59A-8CFB-4590-BAE2-64EED9B4E485", R.drawable.s_suppression);
        skills.put("9E4734B9-D312-4E38-84A7-73E0F1259EEB", R.drawable.s_explosives2);
        skills.put("9DFDE21C-A50D-48B7-B6C0-F1E4107D2F12",
                R.drawable.s_explresist2);
        skills.put("350432B9-5B42-49F4-8343-CD20DE1B82BD", R.drawable.s_sprint);

        // Awards
        /* TODO: */

    }

    // Getters
    public static int getWeapon(String s) {

        return weapons.containsKey(s) ? weapons.get(s) : 0;

    }

    public static int getAttachment(String s) {

        return attachments.containsKey(s) ? attachments.get(s) : 0;

    }

    public static int getVehicle(String s) {

        return vehicles.containsKey(s) ? vehicles.get(s) : 0;

    }

    public static int getVehicleUpgrade(String s) {

        return vehicleUpgrades.containsKey(s) ? vehicleUpgrades.get(s) : 0;

    }

    public static int getKit(String s) {

        return kits.containsKey(s) ? kits.get(s) : 0;

    }

    public static int getAward(String s) {

        return awards.containsKey(s) ? awards.get(s) : 0;

    }

    public static int getSkill(String s) {

        return skills.containsKey(s) ? skills.get(s) : 0;

    }

    public static int getDogtag(String s) {

        return dogtags.containsKey(s) ? dogtags.get(s) : 0;

    }

}
