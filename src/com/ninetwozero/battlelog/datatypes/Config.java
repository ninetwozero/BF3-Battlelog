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


public final class Config {

	//URLs to POST to
	public static final String urlMain = "http://battlelog.battlefield.com/bf3/";
	public static final String urlMainSecure = "https://battlelog.battlefield.com/bf3/";
	public static final String urlLogin = urlMainSecure + "gate/login/";
	public static final String urlStatusSend = urlMain + "user/setStatusmessage/";
	public static final String urlSearch = urlMain + "search/getMatches/";
	public static final String urlFriends = urlMain + "comcenter/sync/";
	public static final String urlFriendAccept = urlMain + "friend/acceptFriendship/{PID}/";
	public static final String urlFriendDecline = urlMain + "friend/declineFriendship/{PID}/";
	public static final String urlNotifications = urlMain + "notification/loadNotifications/";
	
	public static final String urlLogout = urlMain + "session/logout/";
	
	//URLs to JSON-files
	public static final String urlIDGrabber = urlMain + "user/overviewBoxStats/{PID}/";
	public static final String urlStatsOverview = urlMain +"overviewPopulateStats/{UID}/None/{PLATFORM_ID}/";
	public static final String urlStatsWeapons = urlMain +"weaponsPopulateStats/{UID}/{PLATFORM_ID}/";
	public static final String urlStatsVehicles = urlMain +"vehiclesPopulateStats/{UID}/{PLATFORM_ID}/";
	public static final String urlStatsAwards = urlMain +"awardsPopulateStats/{UID}/{PLATFORM_ID}/";
	public static final String urlStatsUpcoming = urlMain +"upcomingUnlocksPopulateStats/{UID}/{PLATFORM_ID}/";
	public static final String urlStatsDogtags = urlMain +"soldier/dogtagsPopulateStats/{UID}/2832658801548551060/{PLATFORM_ID}/";
	
	//Fields needed for the posts fields
	public static final String[] fieldNamesLogin = new String[] {"email", "password", "redirect", "submit"};
	public static final String[] fieldValuesLogin = new String[] {null, null, "", "Sign+in"};

	public static final String[] fieldNamesStatus = new String[] { "message", "post-check-sum" };
	public static final String[] fieldValuesStatus = new String[] { null, null };

	public static final String[] fieldNamesSearch = new String[] { "username", "post-check-sum" };
	public static final String[] fieldValuesSearch = new String[] { null, null };

	public static final String[] fieldNamesCHSUM = new String[] { "post-check-sum" };
	public static final String[] fieldValuesCHSUM = new String[] { null };
		
	//HTML-elements to grab
	public static final String elementUIDLink = "<a class=\"main-loggedin-leftcolumn-active-soldier-name\" href=\"/bf3/soldier/";
	public static final String elementStatusOK = "";
	public static final String elementStatusChecksumLink = "<input type=\"hidden\" name=\"post-check-sum\" value=\"";

	//Platform ID:s
	public static final long platformIdPC = 1;
	public static final long platformIdX360 = 2;
	public static final long platformIdPS3 = 4; //What the he...
	
	//Files
	public static final String fileSharedPrefs = "battlelog";

	//Static methods
	public static long getPlatformId( String p ) {
			
		if( p.equals( "" ) ) return Config.platformIdPC;
		else if( p.equals( "xbox" ) ) return Config.platformIdX360;
		else if( p.equals( "ps3" ) ) return Config.platformIdPS3;
		else return Config.platformIdPC;

	}
	
	public static String getKitTitle( int number ) {
	
		switch( number ) {

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
				return "Misc";
			
		}
		
	}
	
}