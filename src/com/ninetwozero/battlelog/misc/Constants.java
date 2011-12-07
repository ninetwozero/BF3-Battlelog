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
	public static final String URL_MAIN = "http://battlelog.battlefield.com/bf3/";
	public static final String URL_MAIN_SECURE = "https://battlelog.battlefield.com/bf3/";
	public static final String URL_STATIC_CONTENT = "http://static.cdn.ea.com/battlelog/";
	public static final String URL_IMAGE_PACK = "http://192.168.0.11:10920/file.zip";
	public static final String URL_GRAVATAR = "http://www.gravatar.com/avatar/{hash}?s={size}&d=http://battlelog-cdn.battlefield.com/public/base/shared/default-avatar-{default}.png?v=7909";
	
	//URLs that REQUIRE POST-DATA
	public static final String URL_LOGIN = URL_MAIN_SECURE + "gate/login/";
	public static final String URL_STATUS_SEND = URL_MAIN + "user/setStatusmessage/";
	public static final String URL_PROFILE_SEARCH = URL_MAIN + "search/getMatches/";
	public static final String URL_PLATOON_SEARCH = URL_MAIN + "platoon/search/";
	public static final String URL_FRIEND_ACCEPT = URL_MAIN + "friend/acceptFriendship/{UID}/";
	public static final String URL_FRIEND_DECLINE = URL_MAIN + "friend/declineFriendship/{UID}/";
	public static final String URL_NOTIFICATIONS_ALL = URL_MAIN + "notification/";
	public static final String URL_NOTIFICATIONS_TOP5 = URL_MAIN + "notification/loadNotifications/";
	public static final String URL_LOGOUT = URL_MAIN + "session/logout/";
	
	//URLs for CHAT
	public static final String URL_FRIENDS = URL_MAIN + "comcenter/sync/";
	public static final String URL_CHAT_CONTENTS = URL_MAIN + "comcenter/getChatId/{UID}/";
	public static final String URL_CHAT_SEND = URL_MAIN + "comcenter/sendChatMessage/";
	public static final String URL_CHAT_CLOSE = URL_MAIN + "comcenter/hideChat/{CID}/";
	public static final String URL_CHAT_SETACTIVE = URL_MAIN + "comcenter/setActive/";
	//URLS for PROFILE
	public static final String URL_FEED_POST = URL_MAIN + "wall/postmessage";
	public static final String URL_PLATOON_IMAGE = URL_STATIC_CONTENT + "prod/emblems/320/";
	public static final String URL_PLATOON_IMAGE_THUMBS = URL_STATIC_CONTENT + "prod/emblems/60/";
	public static final String URL_FRIEND_REQUESTS = URL_MAIN + "friend/requestFriendship/{UID}/";
	
	//URLS for PLATOONS
	public static final String URL_PLATOON = URL_MAIN + "platoon/{PLATOON_ID}/";
	public static final String URL_PLATOON_FANS = URL_MAIN + "platoon/{PLATOON_ID}/listfans/";
	public static final String URL_PLATOON_MEMBERS = URL_MAIN + "platoon/{PLATOON_ID}/listmembers/";
	public static final String URL_PLATOON_STATS = URL_MAIN + "platoon/platoonMemberStats/{PLATOON_ID}/2/{PLATFORM_ID}/";
	public static final String URL_PLATOON_PROMOTE = URL_MAIN + "platoon/promotemember/{PLATOON_ID}/{UID}/";
	
	//URLS for FEED
	public static final String URL_FEED_REPORT = URL_MAIN + "viewcontent/reportFeedItemAbuse/{POST_ID}/0/"; /* TODO */
	public static final String URL_FEED_REPORT_COMMENT = URL_MAIN + "viewcontent/reportFeedItemAbuse/{POST_ID}/{CID}/"; /* TODO */
	public static final String URL_HOOAH = URL_MAIN + "like/postlike/{POST_ID}/feed-item-like/";
	public static final String URL_UNHOOAH = URL_MAIN + "like/postunlike/{POST_ID}/feed-item-like/";
	public static final String URL_COMMENT = URL_MAIN + "comment/postcomment/{POST_ID}/feed-item-comment/";
	
	//URLs to JSON-files (FEED-related)
	public static final String URL_FEED = URL_MAIN + "feed/?start={NUMSTART}"; /* TODO */
	public static final String URL_FRIEND_FEED = URL_MAIN + "feed/homeevents/?start={NUMSTART}";
	public static final String URL_PROFILE_FEED = URL_MAIN + "feed/profileevents/{POST_ID}/?start={NUMSTART}"; /* TODO */
	public static final String URL_FEED_COMMENTS = URL_MAIN + "feed/getComments/{POST_ID}/"; /* TODO */
	
	//URL to JSON-files (PROFILE-related)
	public static final String URL_PROFILE = URL_MAIN + "user/overviewBoxStats/{UID}/";
	public static final String URL_PROFILE_INFO = URL_MAIN + "user/{UNAME}/";
	
	//URL to JSON-files (STATS-related)
	public static final String URL_STATS_OVERVIEW = URL_MAIN +"overviewPopulateStats/{PID}/None/{PLATFORM_ID}/";
	public static final String URL_STATS_WEAPONS = URL_MAIN +"weaponsPopulateStats/{PID}/{PLATFORM_ID}/";
	public static final String URL_STATS_VEHICLES = URL_MAIN +"vehiclesPopulateStats/{PID}/{PLATFORM_ID}/";
	public static final String URL_STATS_AWARDS = URL_MAIN +"awardsPopulateStats/{PID}/{PLATFORM_ID}/";
	public static final String URL_STATS_UNLOCKS = URL_MAIN +"upcomingUnlocksPopulateStats/{PID}/{PLATFORM_ID}/";
	public static final String URL_STATS_DOGTAGS = URL_MAIN +"soldier/dogtagsPopulateStats/{PID}/{UID}/{PLATFORM_ID}/";
	public static final String URL_STATS_ALL = URL_MAIN + "indexstats/{PID}/{PLATFORM_NAME}/";
	
	//Fields needed for the posts fields
	public static final String[] FIELD_NAMES_LOGIN = new String[] {"email", "password", "redirect", "submit"};
	public static final String[] FIELD_VALUES_LOGIN = new String[] {null, null, "", "Sign+in"};

	public static final String[] FIELD_NAMES_STATUS = new String[] { "message", "post-check-sum" };
	public static final String[] FIELD_VALUES_STATUS = new String[] { null, null };

	public static final String[] FIELD_NAMES_PROFILE_SEARCH = new String[] { "username", "post-check-sum" };
	public static final String[] FIELD_VALUES_PROFILE_SEARCH = new String[] { null, null };

	public static final String[] FIELD_NAMES_PLATOON_SEARCH = new String[] { "searchplat", "post-check-sum" };
	public static final String[] FIELD_VALUES_PLATOON_SEARCH = new String[] { null, null };
	
	public static final String[] FIELD_NAMES_CHAT = new String[] { "message", "chatId", "post-check-sum" };
	public static final String[] FIELD_VALUES_CHAT = new String[] { null, null, null };
	
	public static final String[] FIELD_NAMES_FEED_POST = new String[] { "wall-message", "post-check-sum", "wall-ownerId", "wall-platoonId" };
	public static final String[] FIELD_VALUES_FEED_POST = new String[] { null, null, null, null };
	
	public static final String[] FIELD_NAMES_FEED_COMMENT = new String[] { "comment", "post-check-sum" };
	public static final String[] FIELD_VALUES_FEED_COMMENT = new String[] { null, null };
	
	public static final String[] FIELD_NAMES_CHECKSUM = new String[] { "post-check-sum" };
	public static final String[] FIELD_VALUES_CHECKSUM = new String[] { null };
		
	//HTML-elements to grab
	public static final String ELEMENT_UID_LINK = "<a class=\"main-loggedin-leftcolumn-active-soldier-name\" href=\"/bf3/soldier/";
	public static final String ELEMENT_USERNAME_LINK = "<a href=\"/bf3/user/";
	public static final String ELEMENT_STATUS_OK = "";
	public static final String ELEMENT_STATUS_CHECKSUM = "<input type=\"hidden\" name=\"post-check-sum\" value=\"";
	
	//Files
	public static final String FILE_SHPREF = "battlelog";
	
	//Changelog version
	public final static int CHANGELOG_VERSION = 4;

	//DEBUG
	public static final String DEBUG_TAG = "com.ninetwozero.battlelog";

	//Date-related
	public static final int MINUTE_IN_SECONDS = 60;
	public static final int HOUR_IN_SECONDS = 3600;
	public static final int DAY_IN_SECONDS = 86400;
	public static final int WEEK_IN_SECONDS = 604800;
	public static final int YEAR_IN_SECONDS = 31449600;
	
	//Grawratar
	public static final int DEFAULT_AVATAR_SIZE = 52; 
	public static final int DEFAULT_CACHE_LIMIT = 25;
	
	//Menu
	public static final long MENU_ME = 0;
	public static final long MENU_UNLOCKS = 1;
	public static final long MENU_SOLDIER = 2;
	public static final long MENU_PLATOON = 3;
	public static final long MENU_COMPARE = 4;
	public static final long MENU_SETTINGS = 5;
	
	
	//Misc
	public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux i686; rv:7.0.1) Gecko/20100101 Firefox/7.0.1";

}