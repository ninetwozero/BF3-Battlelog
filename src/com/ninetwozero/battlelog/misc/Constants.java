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

import com.ninetwozero.battlelog.R;

public final class Constants {

    // The base urls
    public static final String URL_MAIN = "http://battlelog.battlefield.com/bf3/";
    public static final String URL_MAIN_SECURE = "https://battlelog.battlefield.com/bf3/";
    public static final String URL_STATIC_CONTENT = "http://static.cdn.ea.com/battlelog/";
    public static final String URL_IMAGE_PACK = "http://192.168.0.11:10920/file.zip";
    public static final String URL_GRAVATAR = "http://www.gravatar.com/avatar/{hash}?s={size}&d=http://battlelog-cdn.battlefield.com/public/base/shared/default-avatar-{default}.png?v=7909";

    // URLs that REQUIRE POST-DATA
    public static final String URL_LOGIN = Constants.URL_MAIN_SECURE + "gate/login/";

    public static final String URL_FRIEND_ACCEPT = Constants.URL_MAIN
            + "friend/acceptFriendship/{UID}/";
    public static final String URL_FRIEND_DECLINE = Constants.URL_MAIN
            + "friend/declineFriendship/{UID}/";
    public static final String URL_NOTIFICATIONS_ALL = Constants.URL_MAIN
            + "notification/";
    public static final String URL_NOTIFICATIONS_TOP5 = Constants.URL_MAIN
            + "notification/loadNotifications/";
    public static final String URL_LOGOUT = Constants.URL_MAIN + "session/logout/";

    // URLs to JSON-files (FEED-related)

    // News
    public static final String URL_NEWS = Constants.URL_MAIN + "news/{COUNT}/";

    // Fields needed for the posts fields
    public static final String[] FIELD_NAMES_LOGIN = new String[] {
            "email",
            "password", "redirect", "submit"
    };
    public static final String[] FIELD_VALUES_LOGIN = new String[] {
            null,
            null, "", "Sign+in"
    };

    public static final String[] FIELD_NAMES_CHECKSUM = new String[] {
            "post-check-sum"
    };

    // HTML-elements to grab
    public static final String ELEMENT_UID_LINK = "<a class=\"main-loggedin-leftcolumn-active-soldier-name\" href=\"/bf3/soldier/";
    public static final String ELEMENT_ERROR_MESSAGE = "<div class=\"gate-login-errormsg wfont\">";
    public static final String ELEMENT_USERNAME_LINK = "<a href=\"/bf3/user/";
    public static final String ELEMENT_STATUS_OK = "";
    public static final String ELEMENT_STATUS_CHECKSUM = "<input type=\"hidden\" name=\"post-check-sum\" value=\"";

    public static final String PATTERN_POST_SINGLE_ID = "<li id=\"feed-item-([0-9]+)\" class=\"feed-single-item feed-no-border\">";
    public static final String PATTERN_POST_SINGLE_UID = "<div rel=\"([0-9]+)\" class=\"base-avatar-container base-avatar-size-medium\">";
    public static final String PATTERN_POST_SINGLE_USERNAME = "<a href=\"/bf3/user/([^/]+)/\" class=\"base-avatar-status-overlay";
    public static final String PATTERN_POST_SINGLE_GRAVATAR = "<img src=\"http://www.gravatar.com/avatar/([^\\?]+)";
    public static final String PATTERN_POST_SINGLE_TITLE = "<a class=\"base-profile-link\" href=\"/bf3/user/([^/]+)/\">([^\"</a>\"]+)</a>([^\"</div>\"]+)";
    public static final String PATTERN_POST_SINGLE_BODY = "<div class=\"wallpost-body\">([^\"</div>\"]+)</div>";
    public static final String PATTERN_POST_SINGLE_DATE = "<span data-timestamp=\"([0-9]+)\" class=\"base-ago\">";
    public static final String PATTERN_POST_FORUM_LINK = "<a href=\"([^\\\"]+)\" rel=\"nofollow\">([^\\<]+)<\\/a> \\[([^\\]]+)\\]";

    // BBCODE
    public static final String BBCODE_TAG_BOLD_IN = "**{text}**";
    public static final String BBCODE_TAG_BOLD_OUT = "[b]{text}[/b]";
    public static final String BBCODE_TAG_STRIKE_IN = "--{text}--";
    public static final String BBCODE_TAG_STRIKE_OUT = "[s]{text}[/s]";
    public static final String BBCODE_TAG_UNDERLINE_IN = "__{text}__";
    public static final String BBCODE_TAG_UNDERLINE_OUT = "[u]{text}[/u]";
    public static final String BBCODE_TAG_ITALIC_IN = "_-{text}-_";
    public static final String BBCODE_TAG_ITALIC_OUT = "[i]{text}[/i]";
    public static final String BBCODE_TAG_QUOTE_IN = "@q:{number}:{username}@\n";
    public static final String BBCODE_TAG_QUOTE_OUT = "[quote {username} said:]{text}[/quote]";

    // Files
    public static final String FILE_SHPREF = "battlelog";

    // Changelog version
    public final static int CHANGELOG_VERSION = 2;

    // MENU IDs
    public final static int MENU_ID_FEED = 2;

    // DEBUG
    public static final String DEBUG_TAG = "com.ninetwozero.battlelog";

    // Date-related
    public static final int MINUTE_IN_SECONDS = 60;
    public static final int HOUR_IN_SECONDS = 3600;
    public static final int DAY_IN_SECONDS = 86400;
    public static final int WEEK_IN_SECONDS = 604800;
    public static final int YEAR_IN_SECONDS = 31449600;

    // Defaults
    public static final int DEFAULT_AVATAR_SIZE = 52;
    public static final int DEFAULT_BADGE_SIZE = 320;
    public static final int DEFAULT_CACHE_LIMIT = 25;
    public static final int DEFAULT_NUM_FEED = 20;

    // Assignments
    public static final int[] ASSIGNMENT_RESOURCES_SCHEMATICS = new int[] {

            R.drawable.assignment_01, R.drawable.assignment_02,
            R.drawable.assignment_03, R.drawable.assignment_04,
            R.drawable.assignment_05, R.drawable.assignment_06,
            R.drawable.assignment_07, R.drawable.assignment_08,
            R.drawable.assignment_09, R.drawable.assignment_10

    };

    public static final int[] ASSIGNMENT_RESOURCES_UNLOCKS = new int[] {

            R.drawable.assignment_01_u, R.drawable.assignment_02_u,
            R.drawable.assignment_03_u, R.drawable.assignment_04_u,
            R.drawable.assignment_05_u, R.drawable.assignment_06_u,
            R.drawable.assignment_07_u, R.drawable.assignment_08_u,
            R.drawable.assignment_09_u, R.drawable.assignment_10_u

    };

    // Menu
    public static final long MENU_MY_SOLDIER = 0;
    public static final long MENU_UNLOCKS = 1;
    public static final long MENU_SEARCH = 2;
    public static final long MENU_PLATOON = 3;
    public static final long MENU_COMPARE = 4;
    public static final long MENU_SETTINGS = 5;
    public static final long MENU_FORUM = 6;
    public static final long MENU_MY_PLATOON = 7;
    public static final long MENU_ASSIGNMENTS = 8;
    public static final long MENU_DEBUG = 255555555;

    // SH
    public static final String SP_V_FILE = "file_version";
    public static final String SP_V_CHANGELOG = "latest_changelog_version";
    public static final String SP_BL_SERVICE = "allow_service";
    public static final String SP_BL_PROFILE_EMAIL = "origin_email";
    public static final String SP_BL_PROFILE_PASSWORD = "origin_password";
    public static final String SP_BL_PROFILE_NAME = "battlelog_username";
    public static final String SP_BL_PROFILE_ID = "battlelog_profile_id";
    public static final String SP_BL_PROFILE_CHECKSUM = "battlelog_post_checksum";
    public static final String SP_BL_PROFILE_GRAVATAR = "battlelog_gravatar_hash";
    public static final String SP_BL_PROFILE_REMEMBER = "remember_password";
    public static final String SP_BL_PERSONA_NAME = "battlelog_persona";
    public static final String SP_BL_PERSONA_ID = "battlelog_persona_id";
    public static final String SP_BL_PERSONA_CURRENT_ID = "battlelog_persona_current_id";
    public static final String SP_BL_PERSONA_CURRENT_POS = "battlelog_persona_current_pos";
    public static final String SP_BL_PERSONA_LOGO = "battlelog_persona_logo";
    public static final String SP_BL_PLATFORM_ID = "battlelog_platform_id";
    public static final String SP_BL_INTERVAL_SERVICE = "battlelog_service_interval";
    public static final String SP_BL_INTERVAL_CHAT = "battlelog_chat_interval";
    public static final String SP_BL_NUM_FEED = "battlelog_feed_count";
    public static final String SP_BL_LANG = "app_locale";
    public static final String SP_BL_FULLSCREEN = "fullscreen_mode";
    public static final String SP_BL_FORUM_LOCALE = "battlelog_locale";
    public static final String SP_BL_FORUM_LOCALE_POSITION = "battlelog_locale_pos";
    public static final String SP_BL_FORUM_CACHE = "battlelog_enable_forum_cache";
    public static final String SP_BL_COOKIE_NAME = "battlelog_cookie_name";
    public static final String SP_BL_COOKIE_VALUE = "battlelog_cookie_value";
    public static final String SP_BL_PLATOON_ID = "battlelog_platoon_id";
    public static final String SP_BL_PLATOON = "battlelog_platoon";
    public static final String SP_BL_PLATOON_TAG = "battlelog_platoon_tag";
    public static final String SP_BL_PLATOON_IMAGE = "battlelog_platoon_image";
    public static final String SP_BL_PLATOON_PLATFORM_ID = "battlelog_platoon_platform_id";
    public static final String SP_BL_PLATOON_CURRENT_ID = "battlelog_platoon_current_id";
    public static final String SP_BL_PLATOON_CURRENT_POS = "battlelog_platoon_current_pos";
    public static final String SP_BL_UNLOCKS_LIMIT_MIN = "battlelog_unlocks_progress_min";

    // Cookie-related
    public static final String COOKIE_DOMAIN = "battlelog.battlefield.com";

    // Misc
    public static final String SUPER_COOKIES = "superCookies";
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux i686; rv:7.0.1) Gecko/20100101 Firefox/7.0.1";
    public static final String LOCALE = "en";

}
