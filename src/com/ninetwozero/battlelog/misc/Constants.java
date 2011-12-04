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


public final class Constants {
	
	//The base urls
	public static final String urlMain = "http://battlelog.battlefield.com/bf3/";
	public static final String urlMainSecure = "https://battlelog.battlefield.com/bf3/";
	public static final String urlStaticContent = "http://static.cdn.ea.com/battlelog/";
	public static final String urlImagePack = "http://192.168.0.11:10920/file.zip";
	public static final String urlGravatar = "http://www.gravatar.com/avatar/{hash}?s={size}&d=http://battlelog-cdn.battlefield.com/public/base/shared/default-avatar-{default}.png?v=7909";
	
	//URLs that REQUIRE POST-DATA
	public static final String urlLogin = urlMainSecure + "gate/login/";
	public static final String urlStatusSend = urlMain + "user/setStatusmessage/";
	public static final String urlSearch = urlMain + "search/getMatches/";
	public static final String urlPlatoonSearch = urlMain + "platoon/search/";
	public static final String urlFriendAccept = urlMain + "friend/acceptFriendship/{UID}/";
	public static final String urlFriendDecline = urlMain + "friend/declineFriendship/{UID}/";
	public static final String urlNotifications = urlMain + "notification/";
//	public static final String urlNotifications = urlMain + "notification/loadNotifications/";
	public static final String urlLogout = urlMain + "session/logout/";
	
	//URLs for CHAT
	public static final String urlFriends = urlMain + "comcenter/sync/";
	public static final String urlChatContents = urlMain + "comcenter/getChatId/{UID}/";
	public static final String urlChatSend = urlMain + "comcenter/sendChatMessage/";
	public static final String urlChatClose = urlMain + "comcenter/hideChat/{CID}/";
	
	//URLS for PROFILE
	public static final String urlProfilePost = urlMain + "wall/postmessage";
	public static final String urlPlatoonImage = urlStaticContent + "prod/emblems/320/";
	public static final String urlPlatoonImageThumbs = urlStaticContent + "prod/emblems/60/";
	public static final String urlFriendRequest = urlMain + "friend/requestFriendship/{UID}/";
	
	//URLS for PLATOONS
	public static final String urlPlatoon = urlMain + "platoon/{PLATOON_ID}/";
	public static final String urlPlatoonFans = urlMain + "platoon/{PLATOON_ID}/listfans/";
	public static final String urlPlatoonMembers = urlMain + "platoon/{PLATOON_ID}/listmembers/";
	public static final String urlPlatoonStats = urlMain + "platoon/platoonMemberStats/{PLATOON_ID}/2/{PLATFORM_ID}/";
	public static final String urlPlatoonPromote = urlMain + "platoon/promotemember/{PLATOON_ID}/{UID}/";
	//URLS for FEED
	public static final String urlFeedReport = urlMain + "viewcontent/reportFeedItemAbuse/{POST_ID}/0/"; /* TODO */
	public static final String urlFeedCommentReport = urlMain + "viewcontent/reportFeedItemAbuse/{POST_ID}/{CID}/"; /* TODO */
	public static final String urlHooah = urlMain + "like/postlike/{POST_ID}/feed-item-like/";
	public static final String urlUnHooah = urlMain + "like/postunlike/{POST_ID}/feed-item-like/";
	public static final String urlComment = urlMain + "comment/postcomment/{POST_ID}/feed-item-comment/";
	
	//URLs to JSON-files (FEED-related)
	public static final String urlFeed = urlMain + "feed/?start={NUMSTART}"; /* TODO */
	public static final String urlFeedGlobal = urlMain + "feed/homeevents/?start={NUMSTART}";
	public static final String urlFeedComments = urlMain + "feed/getComments/{POST_ID}/"; /* TODO */
	public static final String urlUserFeed = urlMain + "feed/profileevents/{POST_ID}/?start={NUMSTART}"; /* TODO */
	
	//URL to JSON-files (PROFILE-related)
	public static final String urlProfile = urlMain + "user/overviewBoxStats/{UID}/";
	public static final String urlProfileInfo = urlMain + "user/{UNAME}/";
	
	//URL to JSON-files (STATS-related)
	public static final String urlStatsOverview = urlMain +"overviewPopulateStats/{PID}/None/{PLATFORM_ID}/";
	public static final String urlStatsWeapons = urlMain +"weaponsPopulateStats/{PID}/{PLATFORM_ID}/";
	public static final String urlStatsVehicles = urlMain +"vehiclesPopulateStats/{PID}/{PLATFORM_ID}/";
	public static final String urlStatsAwards = urlMain +"awardsPopulateStats/{PID}/{PLATFORM_ID}/";
	public static final String urlStatsUpcoming = urlMain +"upcomingUnlocksPopulateStats/{PID}/{PLATFORM_ID}/";
	public static final String urlStatsDogtags = urlMain +"soldier/dogtagsPopulateStats/{PID}/{UID}/{PLATFORM_ID}/";
	public static final String urlStatsAll = urlMain + "indexstats/{PID}/{PLATFORM_NAME}/";
	
	//Fields needed for the posts fields
	public static final String[] fieldNamesLogin = new String[] {"email", "password", "redirect", "submit"};
	public static final String[] fieldValuesLogin = new String[] {null, null, "", "Sign+in"};

	public static final String[] fieldNamesStatus = new String[] { "message", "post-check-sum" };
	public static final String[] fieldValuesStatus = new String[] { null, null };

	public static final String[] fieldNamesSearch = new String[] { "username", "post-check-sum" };
	public static final String[] fieldValuesSearch = new String[] { null, null };

	public static final String[] fieldNamesPlatoonSearch = new String[] { "searchplat", "post-check-sum" };
	public static final String[] fieldValuesPlatoonSearch = new String[] { null, null };
	
	public static final String[] fieldNamesChat = new String[] { "message", "chatId", "post-check-sum" };
	public static final String[] fieldValuesChat = new String[] { null, null, null };
	
	public static final String[] fieldNamesProfilePost = new String[] { "wall-message", "wall-ownerId", "post-check-sum" };
	public static final String[] fieldValuesProfilePost = new String[] { null, null, null };
	
	public static final String[] fieldNamesFeedComment = new String[] { "comment", "post-check-sum" };
	public static final String[] fieldValuesFeedComment = new String[] { null, null };
	
	public static final String[] fieldNamesCHSUM = new String[] { "post-check-sum" };
	public static final String[] fieldValuesCHSUM = new String[] { null };
		
	//HTML-elements to grab
	public static final String elementUIDLink = "<a class=\"main-loggedin-leftcolumn-active-soldier-name\" href=\"/bf3/soldier/";
	public static final String elementUsernameLink = "<a href=\"/bf3/user/";
	public static final String elementStatusOK = "";
	public static final String elementStatusChecksumLink = "<input type=\"hidden\" name=\"post-check-sum\" value=\"";
	
	//Files
	public static final String fileSharedPrefs = "battlelog";
	
	//DEBUG
	public static final String debugTag = "com.ninetwozero.battlelog";

	//Date-related
	public static final int minuteInSeconds = 60;
	public static final int hourInSeconds = 3600;
	public static final int dayInSeconds = 86400;
	public static final int weekInSeconds = 604800;
	public static final int yearInSeconds = 31449600;
	
	//Grawratar
	public static final int defaultAvatarSize = 52; 
	public static final int bitmapCacheLimit = 25;
	
	//Menu
	public static final long MENU_ME = 0;
	public static final long MENU_UNLOCKS = 1;
	public static final long MENU_SOLDIER = 2;
	public static final long MENU_PLATOON = 3;
	public static final long MENU_COMPARE = 4;
	public static final long MENU_SETTINGS = 5;
	
	
	//Misc
	public static final String userAgent = "Mozilla/5.0 (X11; Linux i686; rv:7.0.1) Gecko/20100101 Firefox/7.0.1";

}