package com.ninetwozero.battlelog.misc;


public final class DatabaseStructure {

    // Declare the Table-data for each "part"
    public static final class PersonaStatistics {

        // Table-name
        public static final String TABLE_NAME = "persona_statistics";

        // Field-names
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_ACCOUNT_NAME = "account_name";
        public static final String COLUMN_NAME_PERSONA_NAME = "persona_name";
        public static final String COLUMN_NAME_RANK = "rank_title";
        public static final String COLUMN_NAME_ID_RANK = "rank_id";
        public static final String COLUMN_NAME_ID_PERSONA = "persona_id";
        public static final String COLUMN_NAME_ID_USER = "user_id";
        public static final String COLUMN_NAME_ID_PLATFORM = "platform_id";
        public static final String COLUMN_NAME_POINTS_THIS = "points_this_level";
        public static final String COLUMN_NAME_POINTS_NEXT = "points_next_level";
        public static final String COLUMN_NAME_STATS_TIME = "time_played";
        public static final String COLUMN_NAME_NUM_KILLS = "num_kills";
        public static final String COLUMN_NAME_NUM_ASSISTS = "num_assists";
        public static final String COLUMN_NAME_NUM_VEHICLES = "num_vehicles";
        public static final String COLUMN_NAME_NUM_VASSISTS = "num_vehicle_assists";
        public static final String COLUMN_NAME_NUM_HEALS = "num_heals";
        public static final String COLUMN_NAME_NUM_REVIVES = "num_revives";
        public static final String COLUMN_NAME_NUM_REPAIRS = "num_repairs";
        public static final String COLUMN_NAME_NUM_RESUPPLIES = "num_resupplies";
        public static final String COLUMN_NAME_NUM_DEATHS = "num_deaths";
        public static final String COLUMN_NAME_NUM_WINS = "num_wins";
        public static final String COLUMN_NAME_NUM_LOSSES = "num_losses";
        public static final String COLUMN_NAME_STATS_KDR = "kdr";
        public static final String COLUMN_NAME_STATS_ACCURACY = "accuracy";
        public static final String COLUMN_NAME_STATS_LONGEST_HS = "longest_hs";
        public static final String COLUMN_NAME_STATS_LONGEST_KS = "longest_ks";
        public static final String COLUMN_NAME_STATS_SKILL = "skill";
        public static final String COLUMN_NAME_STATS_SPM = "spm";
        public static final String COLUMN_NAME_SCORE_ASSAULT = "score_assault";
        public static final String COLUMN_NAME_SCORE_ENGINEER = "score_engineer";
        public static final String COLUMN_NAME_SCORE_SUPPORT = "score_support";
        public static final String COLUMN_NAME_SCORE_RECON = "score_recon";
        public static final String COLUMN_NAME_SCORE_VEHICLE = "score_vehicle";
        public static final String COLUMN_NAME_SCORE_COMBAT = "score_combat";
        public static final String COLUMN_NAME_SCORE_AWARDS = "score_awards";
        public static final String COLUMN_NAME_SCORE_UNLOCKS = "score_unlocks";
        public static final String COLUMN_NAME_SCORE_TOTAL = "score_total";

        // Sort order
        public static final String DEFAULT_SORT_ORDER = "`_id` DESC";

        // Getter
        public static String[] getColumns() {

            return new String[]{

                    COLUMN_NAME_ACCOUNT_NAME, COLUMN_NAME_PERSONA_NAME,
                    COLUMN_NAME_RANK, COLUMN_NAME_ID_PERSONA,
                    COLUMN_NAME_ID_USER, COLUMN_NAME_ID_PLATFORM,
                    COLUMN_NAME_ID_RANK, COLUMN_NAME_POINTS_THIS,
                    COLUMN_NAME_POINTS_NEXT, COLUMN_NAME_STATS_TIME,
                    COLUMN_NAME_NUM_KILLS, COLUMN_NAME_NUM_ASSISTS,
                    COLUMN_NAME_NUM_VEHICLES, COLUMN_NAME_NUM_VASSISTS,
                    COLUMN_NAME_NUM_HEALS, COLUMN_NAME_NUM_REVIVES,
                    COLUMN_NAME_NUM_REPAIRS, COLUMN_NAME_NUM_RESUPPLIES,
                    COLUMN_NAME_NUM_DEATHS, COLUMN_NAME_NUM_WINS,
                    COLUMN_NAME_NUM_LOSSES, COLUMN_NAME_STATS_KDR,
                    COLUMN_NAME_STATS_ACCURACY, COLUMN_NAME_STATS_LONGEST_HS,
                    COLUMN_NAME_STATS_LONGEST_KS, COLUMN_NAME_STATS_SKILL,
                    COLUMN_NAME_STATS_SPM, COLUMN_NAME_SCORE_ASSAULT,
                    COLUMN_NAME_SCORE_ENGINEER, COLUMN_NAME_SCORE_SUPPORT,
                    COLUMN_NAME_SCORE_RECON, COLUMN_NAME_SCORE_VEHICLE,
                    COLUMN_NAME_SCORE_COMBAT, COLUMN_NAME_SCORE_AWARDS,
                    COLUMN_NAME_SCORE_UNLOCKS, COLUMN_NAME_SCORE_TOTAL

            };

        }
    }

    public static final class UserProfile {

        // Table-name
        public static final String TABLE_NAME = "user_profile";

        // Field-names
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_NUM_AGE = "age";
        public static final String COLUMN_NAME_NUM_UID = "user_id";
        public static final String COLUMN_NAME_DATE_BIRTH = "birth_date";
        public static final String COLUMN_NAME_DATE_LOGIN = "last_login";
        public static final String COLUMN_NAME_DATE_STATUS = "status_changed";
        public static final String COLUMN_NAME_STRING_NAME = "name";
        public static final String COLUMN_NAME_STRING_USERNAME = "username";
        public static final String COLUMN_NAME_STRING_PRESENTATION = "presentation";
        public static final String COLUMN_NAME_STRING_LOCATION = "location";
        public static final String COLUMN_NAME_STRING_STATUS = "status_message";
        public static final String COLUMN_NAME_STRING_SERVER = "current_server";
        public static final String COLUMN_NAME_STRING_PERSONA = "persona_id";
        public static final String COLUMN_NAME_STRING_PERSONA_NAME = "persona_name";
        public static final String COLUMN_NAME_STRING_PLATFORM = "platform_id";
        public static final String COLUMN_NAME_BOOL_ALLOW_REQUESTS = "allow_friendrequests";
        public static final String COLUMN_NAME_BOOL_ONLINE = "is_online";
        public static final String COLUMN_NAME_BOOL_PLAYING = "is_playing";
        public static final String COLUMN_NAME_BOOL_IS_FRIEND = "is_friend";
        public static final String COLUMN_NAME_BOOL_PLATOONS = "platoons";

        // Sort order
        public static final String DEFAULT_SORT_ORDER = "`_id` DESC";

        // Getter
        public static String[] getColumns() {

            return new String[]{

                    COLUMN_NAME_NUM_AGE, COLUMN_NAME_NUM_UID, COLUMN_NAME_DATE_BIRTH,
                    COLUMN_NAME_DATE_LOGIN, COLUMN_NAME_DATE_STATUS,
                    COLUMN_NAME_STRING_NAME, COLUMN_NAME_STRING_USERNAME,
                    COLUMN_NAME_STRING_PRESENTATION,
                    COLUMN_NAME_STRING_LOCATION, COLUMN_NAME_STRING_STATUS,
                    COLUMN_NAME_STRING_SERVER, COLUMN_NAME_STRING_PERSONA,
                    COLUMN_NAME_STRING_PERSONA_NAME,
                    COLUMN_NAME_STRING_PLATFORM,
                    COLUMN_NAME_BOOL_ALLOW_REQUESTS, COLUMN_NAME_BOOL_ONLINE,
                    COLUMN_NAME_BOOL_PLAYING, COLUMN_NAME_BOOL_IS_FRIEND,
                    COLUMN_NAME_BOOL_PLATOONS

            };

        }
    }

    public static final class PlatoonProfile {

        // Table-name
        public static final String TABLE_NAME = "platoon_profile";

        // Field-names
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_NUM_ID = "platoon_id";
        public static final String COLUMN_NAME_NUM_GAME_ID = "game_id";
        public static final String COLUMN_NAME_NUM_FANS = "num_fans";
        public static final String COLUMN_NAME_NUM_MEMBERS = "num_members";
        public static final String COLUMN_NAME_NUM_BLAZE_ID = "blaze_club_id";
        public static final String COLUMN_NAME_NUM_PLATFORM_ID = "platform_id";
        public static final String COLUMN_NAME_NUM_DATE = "date";
        public static final String COLUMN_NAME_STRING_NAME = "name";
        public static final String COLUMN_NAME_STRING_TAG = "tag";
        public static final String COLUMN_NAME_STRING_INFO = "presentation";
        public static final String COLUMN_NAME_STRING_WEB = "website";
        public static final String COLUMN_NAME_BOOL_VISIBLE = "visible";

        // Sort order
        public static final String DEFAULT_SORT_ORDER = "`_id` DESC";

        // Getter
        public static String[] getColumns() {

            return new String[]{

                    COLUMN_NAME_NUM_ID, COLUMN_NAME_NUM_PLATFORM_ID,
                    COLUMN_NAME_NUM_GAME_ID, COLUMN_NAME_NUM_FANS,
                    COLUMN_NAME_NUM_MEMBERS, COLUMN_NAME_NUM_BLAZE_ID,
                    COLUMN_NAME_NUM_DATE, COLUMN_NAME_STRING_NAME,
                    COLUMN_NAME_STRING_TAG, COLUMN_NAME_STRING_INFO,
                    COLUMN_NAME_STRING_WEB, COLUMN_NAME_BOOL_VISIBLE,

            };

        }

    }

    public static final class ForumThreads {

        // Table-name
        public static final String TABLE_NAME = "saved_threads";

        // Field-names
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_NUM_ID = "thread_id";
        public static final String COLUMN_NAME_NUM_FORUM_ID = "forum_id";
        public static final String COLUMN_NAME_NUM_PROFILE_ID = "profile_id";
        public static final String COLUMN_NAME_STRING_TITLE = "thread_title";
        public static final String COLUMN_NAME_NUM_DATE_LAST_POST = "date_last_post";
        public static final String COLUMN_NAME_STRING_LAST_AUTHOR = "last_post_author";
        public static final String COLUMN_NAME_NUM_LAST_AUTHOR_ID = "last_post_author_id";
        public static final String COLUMN_NAME_NUM_LAST_PAGE_ID = "last_page_id";
        public static final String COLUMN_NAME_NUM_POSTS = "num_posts";
        public static final String COLUMN_NAME_NUM_HAS_UNREAD = "has_unread";
        public static final String COLUMN_NAME_NUM_DATE_READ = "date_read";
        public static final String COLUMN_NAME_NUM_DATE_CHECKED = "date_checked";

        // Sort order
        public static final String DEFAULT_SORT_ORDER = "`_id` DESC";

        // Getter
        public static String[] getColumns() {

            return new String[]{

                    COLUMN_NAME_NUM_ID,
                    COLUMN_NAME_NUM_FORUM_ID,
                    COLUMN_NAME_STRING_TITLE,
                    COLUMN_NAME_NUM_DATE_LAST_POST,
                    COLUMN_NAME_STRING_LAST_AUTHOR,
                    COLUMN_NAME_NUM_LAST_AUTHOR_ID,
                    COLUMN_NAME_NUM_LAST_PAGE_ID,
                    COLUMN_NAME_NUM_POSTS,
                    COLUMN_NAME_NUM_DATE_READ,
                    COLUMN_NAME_NUM_DATE_CHECKED,
                    COLUMN_NAME_NUM_HAS_UNREAD,
                    COLUMN_NAME_NUM_PROFILE_ID

            };

        }
    }

    // Non-initializable
    private DatabaseStructure() {
    }
}
