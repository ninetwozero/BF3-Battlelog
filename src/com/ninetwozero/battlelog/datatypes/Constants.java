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


public final class Constants {
	
	//The base urls
	public static final String urlMain = "http://battlelog.battlefield.com/bf3/";
	public static final String urlMainSecure = "https://battlelog.battlefield.com/bf3/";
	
	//URLs that REQUIRE POST-DATA
	public static final String urlLogin = urlMainSecure + "gate/login/";
	public static final String urlStatusSend = urlMain + "user/setStatusmessage/";
	public static final String urlSearch = urlMain + "search/getMatches/";
	public static final String urlFriendAccept = urlMain + "friend/acceptFriendship/{PID}/";
	public static final String urlFriendDecline = urlMain + "friend/declineFriendship/{PID}/";
	public static final String urlNotifications = urlMain + "notification/loadNotifications/";
	public static final String urlLogout = urlMain + "session/logout/";
	
	//URLs for CHAT
	public static final String urlFriends = urlMain + "comcenter/sync/";
	public static final String urlChatContents = urlMain + "comcenter/getChatId/{PID}/";
	public static final String urlChatSend = urlMain + "comcenter/sendChatMessage/";
	
	//URLS for PROFILE
	public static final String urlProfilePost = urlMain + "wall/postmessage";
	
	//URLs to JSON-files
	public static final String urlProfile = urlMain + "user/overviewBoxStats/{PID}/";
	public static final String urlProfileInfo = urlMain + "user/{UNAME}/";
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

	public static final String[] fieldNamesChat = new String[] { "message", "chatId", "post-check-sum" };
	public static final String[] fieldValuesChat = new String[] { null, null, null };
	
	public static final String[] fieldNamesProfilePost = new String[] {"message", "wall-ownerId", "post-check-sum"};
	public static final String[] fieldValuesProfilePost = new String[] {null, null, null};
	
	public static final String[] fieldNamesCHSUM = new String[] { "post-check-sum" };
	public static final String[] fieldValuesCHSUM = new String[] { null };
		
	//HTML-elements to grab
	public static final String elementUIDLink = "<a class=\"main-loggedin-leftcolumn-active-soldier-name\" href=\"/bf3/soldier/";
	public static final String elementStatusOK = "";
	public static final String elementStatusChecksumLink = "<input type=\"hidden\" name=\"post-check-sum\" value=\"";
	
	//Files
	public static final String fileSharedPrefs = "battlelog";

	//DEBUG
	public static final String debugTag = "com.ninetwozero.battlelog";
	
	//Misc
	public static final String userAgent = "Mozilla/5.0 (X11; Linux i686; rv:7.0.1) Gecko/20100101 Firefox/7.0.1";

}