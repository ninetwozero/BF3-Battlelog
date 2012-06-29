
package com.ninetwozero.battlelog.http;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.FeedItem;
import com.ninetwozero.battlelog.datatype.ParsedFeedItemData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.RequestHandlerException;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class FeedClient extends DefaultClient {

    // Attributes
    private long id;
    private int type;

    // URLS
    public static final String URL_FEED = Constants.URL_MAIN + "feed/?start={NUMSTART}";
    public static final String URL_PLATOON = Constants.URL_MAIN
            + "feed/platoonevents/{PLATOON_ID}/?start={NUMSTART}";
    public static final String URL_FRIEND_FEED = Constants.URL_MAIN
            + "feed/homeevents/?start={NUMSTART}";
    public static final String URL_PROFILE = Constants.URL_MAIN
            + "feed/profileevents/{PID}/?start={NUMSTART}";
    public static final String URL_POST = Constants.URL_MAIN + "wall/postmessage";
    public static final String URL_REPORT = Constants.URL_MAIN
            + "viewcontent/reportFeedItemAbuse/{POST_ID}/0/"; /* TODO */
    public static final String URL_REPORT_COMMENT = Constants.URL_MAIN
            + "viewcontent/reportFeedItemAbuse/{POST_ID}/{CID}/"; /* TODO */
    public static final String URL_HOOAH = Constants.URL_MAIN
            + "like/postlike/{POST_ID}/feed-item-like/";
    public static final String URL_UNHOOAH = Constants.URL_MAIN
            + "like/postunlike/{POST_ID}/feed-item-like/";

    // Constants
    public static final String[] FIELD_NAMES_POST = new String[] {
            "wall-message", "post-check-sum", "wall-ownerId", "wall-platoonId"
    };

    public final static String[] EVENT_LABELS = new String[] {
            "statusmessage",
            "becamefriends",
            "createdforumthread",
            "wroteforumpost",
            "receivedwallpost",
            "receivedplatoonwallpost",
            "addedfavserver",
            "rankedup",
            "levelcomplete",
            "createdplatoon",
            "platoonbadgesaved",
            "joinedplatoon",
            "kickedplatoon",
            "leftplatoon",
            "gamereport",
            "receivedaward",
            "assignmentcomplete",
            "commentedgamereport",
            "commentedblog",
            "gameaccess"
    };

    public static final int TYPE_GLOBAL = 0;
    public static final int TYPE_PROFILE = 1;
    public static final int TYPE_PLATOON = 2;

    public FeedClient(long i, int t) {

        mRequestHandler = new RequestHandler();
        id = i;
        type = t;
    }

    public boolean post(String checksum,
            String content) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

                    URL_POST,
                    RequestHandler.generatePostData(

                            FIELD_NAMES_POST,
                            content,
                            checksum,
                            type == TYPE_PLATOON ? null : id,
                            type == TYPE_PLATOON ? id : null

                            ),
                    RequestHandler.HEADER_JSON

                    );

            // Did we manage?
            if ("".equals(httpContent)) {

                throw new WebsiteHandlerException("Post could not be saved.");

            } else {

                // Check the JSON
                String status = new JSONObject(httpContent).optString("message", "");
                return (status.matches("_POST_CREATED"));

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public ArrayList<FeedItem> get(Context context, int num,
            long profileId) throws WebsiteHandlerException {

        try {

            // Attributes
            List<FeedItem> feedItems = new ArrayList<FeedItem>();
            JSONArray jsonArray;
            String url = "";
            String httpContent = null;

            // What's the url?
            switch (type) {

                case TYPE_GLOBAL:
                    url = URL_FRIEND_FEED;
                    break;

                case TYPE_PROFILE:
                    url = RequestHandler.generateUrl(URL_PROFILE, id);
                    break;

                case TYPE_PLATOON:
                    url = RequestHandler.generateUrl(URL_PLATOON, id);
                    break;

                default:
                    url = URL_FRIEND_FEED;
                    break;

            }

            // Let's see
            for (int i = 0, max = Math.round(num / 10); i < max; i++) {

                // Get the content, and create a JSONArray
                httpContent = mRequestHandler.get(
                        url.replace(
                                "{NUMSTART}",
                                String.valueOf(i * 10)
                                ),
                        RequestHandler.HEADER_AJAX
                        );

                // Convert the JSON
                jsonArray = new JSONObject(httpContent).getJSONObject("data")
                        .getJSONArray("feedEvents");

                // Gather them
                feedItems.addAll(getFeedItemsFromJSON(context, jsonArray, profileId));

            }

            // Return it
            return (ArrayList<FeedItem>) feedItems;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    /* TODO: */
    private ArrayList<FeedItem> getFeedItemsFromJSON(Context context,
            JSONArray jsonArray, long activeProfileId) {

        // Variables needed
        List<FeedItem> feedItemArray = new ArrayList<FeedItem>();
        try {

            for (int i = 0, max = jsonArray.length(); i < max; i++) {

                feedItemArray.add(getFeedItemFromJSON(context, jsonArray.getJSONObject(i),
                        activeProfileId));

            }

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        return (ArrayList<FeedItem>) feedItemArray;

    }

    private FeedItem getFeedItemFromJSON(Context context,
            JSONObject currItem, long activeProfileId)
            throws WebsiteHandlerException {

        try {

            // Variables that we need
            JSONObject ownerObject = currItem.optJSONObject("owner");
            final String event = currItem.getString("event");
            String tempGravatarHash = ownerObject.getString("gravatarMd5");

            // Process the likes & comments
            JSONArray likeUsers = currItem.getJSONArray("likeUserIds");
            int numLikes = likeUsers.length();
            int numComments = currItem.getInt("numComments");
            boolean liked = false;
            boolean censored = currItem.getBoolean("hidden");

            // Iterate and see if the user has *liked* it already
            for (int likeCount = 0; likeCount < numLikes; likeCount++) {

                if (Long.parseLong(likeUsers.getString(likeCount)) == activeProfileId) {

                    liked = true;
                    break;

                }

            }

            // Set the first profile
            ProfileData mainProfile = new ProfileData.Builder(
                    Long.parseLong(currItem.getString("ownerId")),
                    ownerObject.getString("username")
                    ).gravatarHash(ownerObject.getString("gravatarMd5")).build();

            int typeId = getTypeIdFromEvent(event);
            ParsedFeedItemData feedItemData = null;
            switch (typeId) {
                case FeedItem.TYPE_NEW_STATUS:
                    feedItemData = generateFromNewStatus(context,
                            currItem.getJSONObject("STATUSMESSAGE"), mainProfile);
                    break;
                case FeedItem.TYPE_NEW_FRIEND:
                    feedItemData = generateFromNewFriend(context,
                            currItem.getJSONObject("BECAMEFRIENDS"), mainProfile);
                    break;
                case FeedItem.TYPE_NEW_FORUM_THREAD:
                    feedItemData = generateFromNewThread(context,
                            currItem.getJSONObject("CREATEDFORUMTHREAD"), mainProfile);
                    break;
                case FeedItem.TYPE_NEW_FORUM_POST:
                    feedItemData = generateFromNewForumPost(context,
                            currItem.getJSONObject("WROTEFORUMPOST"), mainProfile);
                    break;
                case FeedItem.TYPE_GOT_WALL_POST:
                    feedItemData = generateFromWallPost(context,
                            currItem.getJSONObject("RECEIVEDWALLPOST"), mainProfile);
                    break;
                case FeedItem.TYPE_GOT_PLATOON_POST:
                    feedItemData = generateFromPlatoonPost(context,
                            currItem.getJSONObject("RECEIVEDPLATOONWALLPOST"), mainProfile);
                    break;
                case FeedItem.TYPE_NEW_FAVSERVER:
                    feedItemData = generateFromFavoritingServer(context,
                            currItem.getJSONObject("ADDEDFAVSERVER"), mainProfile);
                    break;
                case FeedItem.TYPE_NEW_RANK:
                    feedItemData = generateFromReachingNewRank(context,
                            currItem.getJSONObject("RANKEDUP"), mainProfile);
                    break;
                case FeedItem.TYPE_COMPLETED_LEVEL:
                    feedItemData = generateFromCompletingLevel(context,
                            currItem.getJSONObject("LEVELCOMPLETE"), mainProfile);
                    break;
                case FeedItem.TYPE_NEW_PLATOON:
                    feedItemData = generateFromCreatingNewPlatoon(context,
                            currItem.getJSONObject("CREATEDPLATOON"), mainProfile);
                    break;
                case FeedItem.TYPE_NEW_EMBLEM:
                    feedItemData = generateFromNewPlatoonEmblem(context,
                            currItem.getJSONObject("PLATOONBADGESAVED"), mainProfile);
                    break;
                case FeedItem.TYPE_JOINED_PLATOON:
                    feedItemData = generateFromJoiningPlatoon(context,
                            currItem.getJSONObject("JOINEDPLATOON"), mainProfile);
                    break;
                case FeedItem.TYPE_KICKED_PLATOON:
                    feedItemData = generateFromGettingKickedFromPlatoon(context,
                            currItem.getJSONObject("KICKEDPLATOON"), mainProfile);
                    break;
                case FeedItem.TYPE_LEFT_PLATOON:
                    feedItemData = generateFromLeavingPlatoon(context,
                            currItem.getJSONObject("LEFTPLATOON"), mainProfile);
                    break;
                case FeedItem.TYPE_COMPLETED_GAME:
                    feedItemData = generateFromGameReport(context,
                            currItem.getJSONObject("GAMEREPORT"), mainProfile);
                    break;
                case FeedItem.TYPE_GOT_AWARD:
                    feedItemData = generateFromAward(context,
                            currItem.getJSONObject("RECEIVEDAWARD"), mainProfile);
                    break;
                case FeedItem.TYPE_COMPLETED_ASSIGNMENT:
                    feedItemData = generateFromCompletingAssignment(context,
                            currItem.getJSONObject("COMPLETEDASSIGNMENT"), mainProfile);
                    break;
                case FeedItem.TYPE_NEW_COMMENT_GAME:
                    feedItemData = generateFromCommentOnGameReport(context,
                            currItem.getJSONObject("COMMENTEDGAMEREPORT"), mainProfile);
                    break;
                case FeedItem.TYPE_NEW_COMMENT_BLOG:
                    feedItemData = generateFromCommentOnBlog(context,
                            currItem.getJSONObject("COMMENTEDBLOG"), mainProfile);
                    break;
                case FeedItem.TYPE_NEW_EXPANSION:
                    feedItemData = generateFromUnlockedExpansion(context,
                            currItem.getJSONObject("GAMEACCESS"), mainProfile);
                    break;

                default:
                    throw new WebsiteHandlerException("Unknown event: " + event);

            }

            // Fix a filename
            String filename = tempGravatarHash + ".png";

            // Before I forget - let's download the gravatar too!
            if (!CacheHandler.isCached(context, filename)) {

                ProfileClient.cacheGravatar(context, filename,
                        Constants.DEFAULT_AVATAR_SIZE);

            }
            return new FeedItem(

                    Long.parseLong(currItem.getString("id")),
                    Long.parseLong(currItem.getString("itemId")),
                    currItem.getLong("creationDate"),
                    numLikes,
                    numComments,
                    typeId,
                    feedItemData.getTitle(),
                    feedItemData.getContent(),
                    feedItemData.getProfileData(),
                    liked,
                    censored,
                    tempGravatarHash

            );

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }
    }

    public static int getTypeIdFromEvent(String event) {

        for (int i = 0, max = EVENT_LABELS.length; i < max; i++) {

            if (EVENT_LABELS[i].equals(event)) {
                return i;
            }
        }
        return -1;

    }

    private ParsedFeedItemData generateFromNewForumPost(Context context, JSONObject currItem,
            ProfileData profile) throws JSONException {

        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_forumthread,
                        profile.getUsername(),
                        currItem.getString("threadTitle")
                        ),
                currItem.getString("threadBody"),
                new ProfileData[] {

                        profile,
                        null

                });
    }

    private ParsedFeedItemData generateFromUnlockedExpansion(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {

        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_gameaccess,
                        profile.getUsername(),
                        DataBank.getExpansionTitle(currItem.getString("expansion"))
                        ),
                "",
                new ProfileData[] {

                        profile,
                        null

                });
    }

    private ParsedFeedItemData generateFromCommentOnBlog(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {
        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_blog_comment,
                        profile.getUsername(),
                        currItem.getString("blogTitle")
                        ),
                currItem.getString("blogCommentBody"),
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromCommentOnGameReport(Context context,
            JSONObject currItem,
            ProfileData profile)
            throws JSONException {
        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_greport_comment,
                        profile.getUsername(),
                        currItem.getString("serverName"),
                        DataBank.getMapTitle(currItem.getString("map")),
                        DataBank.getGameModeFromId(currItem.getInt("gameMode"))
                        ),
                currItem.getString("gameReportComment"),
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromCompletingAssignment(Context context,
            JSONObject currItem,
            ProfileData profile)
            throws JSONException {

        JSONObject statsItem = currItem.getJSONArray("statItems").getJSONObject(0);
        Object[] tempInfo = DataBank.getAssignmentTitle(statsItem.getString("nameSID"));

        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_txt_assignment_ok,
                        profile.getUsername(),
                        tempInfo
                        ),
                "",
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromAward(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {
        JSONArray stats = currItem.optJSONArray("statItems");
        StringBuilder title = new StringBuilder();

        for (int counter = 0, maxCounter = stats.length(); counter < maxCounter; counter++) {

            // Let's get the item
            JSONObject subItem = stats.optJSONObject(counter);
            String tempKey = subItem.getString("nameSID");
            if (title.length() == 0) {
                title.append("<b>");
            }

            // Do we need to append anything?
            if (counter > 0) {

                if (counter == (maxCounter - 1)) {

                    title.append("</b> and <b>");

                } else {

                    title.append(", ");

                }

            }

            // Weapon? Attachment?
            title.append(DataBank.getAwardTitle(tempKey));

        }

        // Set the title
        String generatedTitle;
        if (stats.length() > 1) {

            generatedTitle = PublicUtils.createStringWithData(
                    context,
                    R.string.info_p_awards,
                    profile.getUsername(),
                    title.append("</b>")
                    );

        } else {

            generatedTitle = PublicUtils.createStringWithData(
                    context,
                    R.string.info_p_award,
                    profile.getUsername(),
                    title.append("</b>")
                    );

        }
        return new ParsedFeedItemData(

                generatedTitle,
                "",
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromGameReport(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {

        // Init
        JSONArray stats = currItem.optJSONArray("statItems");
        StringBuilder title = new StringBuilder();

        /* TODO: EXPORT TO SMALLER METHODS */
        for (int counter = 0, maxCounter = stats.length(); counter < maxCounter; counter++) {

            // Let's get the item
            String tempKey;
            JSONObject tempSubItem = stats.optJSONObject(counter);

            // Append
            if (title.length() == 0) {
                title.append("<b>");
            }

            // Do we need to append anything?
            if (counter > 0) {

                if (counter == (maxCounter - 1)) {

                    title.append(" </b>and<b> ");

                } else {

                    title.append(", ");

                }

            }

            // Weapon? Attachment?
            if (!tempSubItem.isNull("parentNameSID")) {

                // Let's see
                String parentKey = tempSubItem.getString("parentNameSID");
                tempKey = DataBank.getWeaponTitle(context, parentKey);

                // Is it empty?
                if (!parentKey.equals(tempKey)) {

                    title
                            .append(tempKey)
                            .append(" ")
                            .append(DataBank.getAttachmentTitle(tempSubItem
                                    .getString("nameSID")));

                } else {

                    // Grab a vehicle title then
                    tempKey = DataBank.getVehicleTitle(parentKey);

                    // Validate
                    if (!parentKey.equals(tempKey)) {

                        title
                                .append(tempKey)
                                .append(" ")
                                .append(DataBank.getVehicleUpgradeTitle(tempSubItem
                                        .getString("nameSID")));

                    } else {

                        title.append(tempKey);

                    }

                }

            } else {

                // Let's see
                String key = tempSubItem.getString("nameSID");
                String guid = tempSubItem.getString("guid");

                if (key.startsWith("ID_P_ANAME_")) {

                    title.append(DataBank.getAttachmentTitle(key));

                } else if (key.startsWith("ID_P_WNAME_")) {

                    title.append(DataBank.getWeaponTitle(context, guid));

                } else if (key.startsWith("ID_P_VUNAME_")) {

                    title.append(DataBank.getVehicleUpgradeTitle(key));

                } else if (key.startsWith("ID_P_SNAME")) {

                    title.append(DataBank.getSkillTitle(key));

                } else {

                    title.append(DataBank.getKitTitle(key));

                }

            }

        }

        // Set the things straight
        String generatedTitle;
        if (stats.length() > 1) {

            generatedTitle = PublicUtils.createStringWithData(
                    context,
                    R.string.info_p_newunlocks,
                    profile.getUsername(),
                    title.append("</b>")
                    );

        } else {

            generatedTitle = PublicUtils.createStringWithData(
                    context,
                    R.string.info_p_newunlock,
                    profile.getUsername(),
                    title.append("</b>")
                    );

        }
        return new ParsedFeedItemData(

                generatedTitle, "", new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromLeavingPlatoon(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {
        JSONObject platoonObject = currItem.getJSONObject("platoon");
        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_platoon_left,
                        profile.getUsername(),
                        platoonObject.getString("name")
                        ),
                "",
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromGettingKickedFromPlatoon(Context context,
            JSONObject currItem,
            ProfileData profile)
            throws JSONException {

        JSONObject platoonObject = currItem.getJSONObject("platoon");
        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_platoon_kick,
                        profile.getUsername(),
                        platoonObject.getString("name")
                        ),
                "",
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromJoiningPlatoon(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {
        JSONObject platoonObject = currItem.getJSONObject("platoon");
        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_platoon_join,
                        profile.getUsername(),
                        platoonObject.getString("name")
                        ),
                "",
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromNewPlatoonEmblem(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {
        JSONObject platoonObject = currItem.getJSONObject("platoon");
        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_platoon_badge,
                        profile.getUsername(),
                        platoonObject.getString("name")
                        ),
                "",
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromCreatingNewPlatoon(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {

        JSONObject platoonObject = currItem.getJSONObject("platoon");
        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_platoon_create,
                        profile.getUsername(),
                        platoonObject.getString("name")
                        ),
                "",
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromCompletingLevel(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {
        JSONObject friendObject = currItem.getJSONObject("friend");
        ProfileData profile2 = new ProfileData.Builder(
                Long.parseLong(friendObject.getString("userId")),
                friendObject.getString("username")
                ).gravatarHash(friendObject.getString("gravatarMd5")).build();

        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_coop_level_comp,
                        profile.getUsername(),
                        profile2.getUsername(),
                        DataBank.getCoopLevelTitle(currItem.getString("level")),
                        currItem.getString("difficulty")
                        ),
                "",
                new ProfileData[] {

                        profile,
                        profile2

                });

    }

    private ParsedFeedItemData generateFromReachingNewRank(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {
        return new ParsedFeedItemData(
                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_promotion,
                        profile.getUsername(),
                        DataBank.getRankTitle(currItem.getString("nameSID")),
                        currItem.getString("rank")),
                "",
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromFavoritingServer(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {
        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_favserver,
                        profile.getUsername(),
                        currItem.getString("serverName")
                        ),
                "",
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromPlatoonPost(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {
        JSONObject platoonObject = currItem.getJSONObject("platoon");
        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_platoon_feed,
                        profile.getUsername(),
                        platoonObject.getString("name")
                        ),
                currItem.getString("wallBody"),
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromWallPost(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {

        // Let's get it!
        JSONObject otherUserObject = currItem.getJSONObject("writerUser");
        String tempGravatarHash = otherUserObject.getString("gravatarMd5");

        // Set the other profile
        ProfileData profile2 = profile;
        ProfileData profile1 = new ProfileData.Builder(
                Long.parseLong(otherUserObject.getString("userId")),
                otherUserObject.getString("username")
                ).gravatarHash(otherUserObject.getString("gravatarMd5")).build();

        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_got_wallpost,
                        profile.getUsername(),
                        profile2.getUsername(),
                        currItem.getString("wallBody")
                        ),
                "",
                new ProfileData[] {

                        profile1,
                        profile2

                });

    }

    private ParsedFeedItemData generateFromNewThread(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {

        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_forumthread,
                        profile.getUsername(),
                        currItem.getString("threadTitle")
                        ),
                currItem.getString("threadBody"),
                new ProfileData[] {

                        profile,
                        null

                });

    }

    private ParsedFeedItemData generateFromNewFriend(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {

        JSONObject friendUser = currItem.getJSONObject("friendUser");

        // Set the first profile
        ProfileData profile2 = new ProfileData.Builder(
                Long.parseLong(friendUser.getString("userId")),
                friendUser.getString("username")
                ).gravatarHash(friendUser.getString("gravatarMd5")).build();

        // Get the title
        return new ParsedFeedItemData(

                PublicUtils.createStringWithData(
                        context,
                        R.string.info_p_friendship,
                        profile.getUsername(),
                        profile2.getUsername()
                        ),
                "",
                new ProfileData[] {

                        profile,
                        profile2

                });

    }

    private ParsedFeedItemData generateFromNewStatus(Context context, JSONObject currItem,
            ProfileData profile)
            throws JSONException {
        return new ParsedFeedItemData(

                "<b>{username}</b> " + currItem.getString("statusMessage"),
                "",
                new ProfileData[] {

                        profile,
                        null

                });

    }

    public static boolean hooah(long postId, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            String httpContent = new RequestHandler().post(

                    RequestHandler.generateUrl(URL_HOOAH, postId),
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_AJAX

                    );

            // Did we manage?
            return (!"".equals(httpContent));

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean unhooah(long postId, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            String httpContent = new RequestHandler().post(

                    RequestHandler.generateUrl(URL_UNHOOAH, postId),
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_AJAX

                    );

            // Did we manage?
            return (!"".equals(httpContent));

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }
}
