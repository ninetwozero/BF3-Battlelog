
package com.ninetwozero.battlelog.http;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.FeedItem;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.RequestHandlerException;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;

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

    public final static String LABEL_NEW_STATUS = "statusmessage";
    public final static String LABEL_NEW_FRIEND = "becamefriends";
    public final static String LABEL_NEW_THREAD = "createdforumthread";
    public final static String LABEL_NEW_POST = "wroteforumpost";
    public final static String LABEL_GOT_POST = "receivedwallpost";
    public final static String LABEL_GOT_PLATOON_POST = "receivedplatoonwallpost";
    public final static String LABEL_NEW_FAVSERVER = "addedfavserver";
    public final static String LABEL_NEW_RANK = "rankedup";
    public final static String LABEL_COMPLETED_LEVEL = "levelcomplete";
    public final static String LABEL_NEW_PLATOON = "createdplatoon";
    public final static String LABEL_NEW_EMBLEM = "platoonbadgesaved";
    public final static String LABEL_JOINED_PLATOON = "joinedplatoon";
    public final static String LABEL_KICKED_PLATOON = "kickedplatoon";
    public final static String LABEL_LEFT_PLATOON = "leftplatoon";
    public final static String LABEL_COMPLETED_GAME = "gamereport";
    public final static String LABEL_GOT_AWARD = "receivedaward";
    public final static String LABEL_COMPLETED_ASSIGNMENT = "assignmentcomplete";
    public final static String LABEL_NEW_COMMENT_GAME = "commentedgamereport";
    public final static String LABEL_NEW_COMMENT_BLOG = "commentedblog";
    public final static String LABEL_NEW_EXPANSION = "gameaccess";

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
            JSONObject otherUserObject = null;
            JSONObject tempSubItem = null;

            // Strings
            StringBuilder tempTitle = new StringBuilder();
            StringBuilder itemTitle = new StringBuilder();
            StringBuilder itemContent = new StringBuilder();

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
            ProfileData profile1 = new ProfileData.Builder(
                    Long.parseLong(currItem.getString("ownerId")),
                    ownerObject.getString("username")
                    ).gravatarHash(ownerObject.getString("gravatarMd5")).build();
            ProfileData profile2 = null;
            
            // What do we have *here*?
            if (!currItem.isNull("BECAMEFRIENDS")) {

                // Grab the specific object
                tempSubItem = currItem.optJSONObject("BECAMEFRIENDS");
                JSONObject friendUser = tempSubItem
                        .getJSONObject("friendUser");

                // Set the first profile
                profile2 = new ProfileData.Builder(
                        Long.parseLong(friendUser.getString("userId")),
                        friendUser.getString("username")
                        ).gravatarHash(friendUser.getString("gravatarMd5")).build();

                // Get the title
                itemTitle.append(context.getString(R.string.info_p_friendship));

            } else if (!currItem.isNull("ASSIGNMENTCOMPLETE")) {

                // Grab the specific object
                tempSubItem = currItem.optJSONObject("ASSIGNMENTCOMPLETE");
                JSONObject statsItem = tempSubItem
                        .getJSONArray("statItems").getJSONObject(0);
                String[] tempInfo = DataBank.getAssignmentTitle(statsItem
                        .getString("nameSID"));

                // Set the title
                itemTitle.append(

                        context.getString(R.string.info_txt_assignment_ok).replace(

                                "{assignment}", tempInfo[0]

                                ).replace(

                                        "{unlock}", tempInfo[1]

                                )

                        );

            } else if (!currItem.isNull("CREATEDFORUMTHREAD")) {

                // Grab the specific object
                tempSubItem = currItem.optJSONObject("CREATEDFORUMTHREAD");
                itemTitle.append(context.getString(R.string.info_p_forumthread).replace(

                        "{thread}", tempSubItem.getString("threadTitle")

                        )

                        );
                itemContent.append(tempSubItem.getString("threadBody"));

            } else if (!currItem.isNull("WROTEFORUMPOST")) {

                // Grab the specific object
                tempSubItem = currItem.optJSONObject("WROTEFORUMPOST");
                itemTitle.append(context.getString(R.string.info_p_forumpost)
                        .replace(

                                "{thread}", tempSubItem.getString("threadTitle")

                        )

                        );
                itemContent.append(tempSubItem.getString("postBody"));

            } else if (!currItem.isNull("GAMEREPORT")) {

                // Grab the specific object
                JSONArray tempStatsArray = currItem.optJSONObject(
                        "GAMEREPORT").optJSONArray("statItems");

                /* TODO: EXPORT TO SMALLER METHODS */
                for (int statsCounter = 0, maxCounter = tempStatsArray
                        .length(); statsCounter < maxCounter; statsCounter++) {

                    // Let's get the item
                    String tempKey;
                    tempSubItem = tempStatsArray
                            .optJSONObject(statsCounter);
                    if (tempTitle.length() == 0) {
                        tempTitle.append("<b>");
                    }

                    // Do we need to append anything?
                    if (statsCounter > 0) {

                        if (statsCounter == (maxCounter - 1)) {

                            tempTitle.append(" </b>and<b> ");

                        } else {

                            tempTitle.append(", ");

                        }

                    }

                    // Weapon? Attachment?
                    if (!tempSubItem.isNull("parentNameSID")) {

                        // Let's see
                        String parentKey = tempSubItem.getString("parentNameSID");
                        tempKey = DataBank.getWeaponTitle(context, parentKey);

                        // Is it empty?
                        if (!parentKey.equals(tempKey)) {

                            tempTitle
                                    .append(tempKey)
                                    .append(" ")
                                    .append(DataBank.getAttachmentTitle(tempSubItem
                                            .getString("nameSID")));

                        } else {

                            // Grab a vehicle title then
                            tempKey = DataBank.getVehicleTitle(parentKey);

                            // Validate
                            if (!parentKey.equals(tempKey)) {

                                tempTitle
                                        .append(tempKey)
                                        .append(" ")
                                        .append(DataBank.getVehicleUpgradeTitle(tempSubItem
                                                .getString("nameSID")));

                            } else {

                                tempTitle.append(tempKey);

                            }

                        }

                    } else {

                        // Let's see
                        String key = tempSubItem.getString("nameSID");
                        String guid = tempSubItem.getString("guid");

                        if (key.startsWith("ID_P_ANAME_")) {

                            tempTitle.append(DataBank.getAttachmentTitle(key));

                        } else if (key.startsWith("ID_P_WNAME_")) {

                            tempTitle.append(DataBank.getWeaponTitle(context, guid));

                        } else if (key.startsWith("ID_P_VUNAME_")) {

                            tempTitle.append(DataBank.getVehicleUpgradeTitle(key));

                        } else if (key.startsWith("ID_P_SNAME")) {

                            tempTitle.append(DataBank.getSkillTitle(key));

                        } else {

                            tempTitle.append(DataBank.getKitTitle(key));

                        }

                    }

                }

                // Set the things straight
                itemTitle.append(
                        context.getString(
                                tempStatsArray.length() > 1 ? R.string.info_p_newunlocks
                                        : R.string.info_p_newunlock).replace("{item}",
                                tempTitle + "</b>")

                        );

            } else if (!currItem.isNull("STATUSMESSAGE")) {

                // Get the JSON-Object
                tempSubItem = currItem.optJSONObject("STATUSMESSAGE");

                // Set the title
                itemTitle.append("<b>{username}</b> ").append(
                        tempSubItem.getString("statusMessage"));

            } else if (!currItem.isNull("ADDEDFAVSERVER")) {

                // Get it!
                tempSubItem = currItem.getJSONObject("ADDEDFAVSERVER");

                // Set the title
                itemTitle.append(context.getString(R.string.info_p_favserver).replace(

                        "{server}", tempSubItem.getString("serverName")

                        )
                        );

            } else if (!currItem.isNull("RANKEDUP")) {

                // Get it!
                tempSubItem = currItem.getJSONObject("RANKEDUP");

                // Set it!
                itemTitle.append(context
                        .getString(R.string.info_p_promotion)
                        .replace(

                                "{rank title}",
                                DataBank.getRankTitle(tempSubItem
                                        .getString("nameSID"))

                        ).replace(

                                "{rank}", tempSubItem.getString("rank")

                        )

                        );

            } else if (!currItem.isNull("COMMENTEDGAMEREPORT")) {

                // Get it!
                tempSubItem = currItem.getJSONObject("COMMENTEDGAMEREPORT");

                // Set it!
                itemTitle.append(context
                        .getString(R.string.info_p_greport_comment)
                        .replace(

                                "{server name}",
                                tempSubItem.getString("serverName")

                        )
                        .replace(

                                "{map}",
                                DataBank.getMapTitle(tempSubItem
                                        .getString("map"))

                        )
                        .replace(

                                "{game mode}",
                                DataBank.getGameModeFromId(tempSubItem
                                        .getInt("gameMode"))

                        )

                        );
                itemContent.append(tempSubItem.getString("gameReportComment"));

            } else if (!currItem.isNull("COMMENTEDBLOG")) {

                // Get it!
                tempSubItem = currItem.getJSONObject("COMMENTEDBLOG");

                // Set it!
                itemTitle.append(context.getString(R.string.info_p_blog_comment)
                        .replace(

                                "{post name}", tempSubItem.getString("blogTitle")

                        )

                        );

                itemContent.append(tempSubItem.getString("blogCommentBody"));

            } else if (!currItem.isNull("JOINEDPLATOON")) {

                // Get it!
                tempSubItem = currItem.getJSONObject("JOINEDPLATOON")
                        .getJSONObject("platoon");

                // Set it!
                itemTitle.append(context.getString(R.string.info_p_platoon_join)
                        .replace(

                                "{platoon}", tempSubItem.getString("name")

                        )

                        );

            } else if (!currItem.isNull("KICKEDPLATOON")) {

                // Get it!
                tempSubItem = currItem.getJSONObject("KICKEDPLATOON")
                        .getJSONObject("platoon");

                // Set it!
                itemTitle.append(context.getString(R.string.info_p_platoon_kick)
                        .replace(

                                "{platoon}", tempSubItem.getString("name")

                        )

                        );

            } else if (!currItem.isNull("CREATEDPLATOON")) {

                // Get it!
                tempSubItem = currItem.getJSONObject("CREATEDPLATOON")
                        .getJSONObject("platoon");

                // Set it!
                itemTitle.append(context.getString(
                        R.string.info_p_platoon_create).replace(

                        "{platoon}", tempSubItem.getString("name")

                        )

                        );

            } else if (!currItem.isNull("PLATOONBADGESAVED")) {

                // Get it!
                tempSubItem = currItem.getJSONObject("PLATOONBADGESAVED")
                        .getJSONObject("platoon");

                // Set it!
                itemTitle.append(context
                        .getString(R.string.info_p_platoon_badge).replace(

                                "{platoon}", tempSubItem.getString("name")

                        )

                        );

            } else if (!currItem.isNull("LEFTPLATOON")) {

                // Get it!
                tempSubItem = currItem.getJSONObject("LEFTPLATOON")
                        .getJSONObject("platoon");

                // Set it!
                itemTitle.append(context.getString(R.string.info_p_platoon_left)
                        .replace(

                                "{platoon}", tempSubItem.getString("name")

                        )

                        );

            } else if (!currItem.isNull("RECEIVEDPLATOONWALLPOST")) {

                // Get it!
                tempSubItem = currItem
                        .getJSONObject("RECEIVEDPLATOONWALLPOST");

                // Set it!
                itemTitle.append(context.getString(R.string.info_p_platoon_feed)
                        .replace(

                                "{platoon}",
                                tempSubItem.getJSONObject("platoon")
                                        .getString("name")

                        )

                        );

                itemContent.append(tempSubItem.getString("wallBody"));

            } else if (!currItem.isNull("LEVELCOMPLETE")) {

                // Get em!
                tempSubItem = currItem.getJSONObject("LEVELCOMPLETE");
                JSONObject friendObject = tempSubItem
                        .getJSONObject("friend");

                // Set it!
                itemTitle.append(context
                        .getString(R.string.info_p_coop_level_comp)
                        .replace(

                                "{level}",
                                DataBank.getCoopLevelTitle(tempSubItem
                                        .getString("level"))

                        ).replace(

                                "{difficulty}", tempSubItem.getString("difficulty")

                        )

                        );

                // Set the second profile
                profile2 = new ProfileData.Builder(
                        Long.parseLong(friendObject.getString("userId")),
                        ownerObject.getString("username")
                        ).gravatarHash(ownerObject.getString("gravatarMd5")).build();

            } else if (!currItem.isNull("RECEIVEDAWARD")) {

                // Get it!
                JSONArray tempStatsArray = currItem.optJSONObject(
                        "RECEIVEDAWARD").optJSONArray("statItems");

                for (int statsCounter = 0, maxCounter = tempStatsArray
                        .length(); statsCounter < maxCounter; statsCounter++) {

                    // Let's get the item
                    tempSubItem = tempStatsArray.optJSONObject(statsCounter);
                    String tempKey = tempSubItem.getString("nameSID");
                    if (tempTitle.length() == 0) {
                        tempTitle.append("<b>");
                    }

                    // Do we need to append anything?
                    if (statsCounter > 0) {

                        if (statsCounter == (maxCounter - 1)) {

                            tempTitle.append("</b> and <b>");

                        } else {

                            tempTitle.append(", ");

                        }

                    }

                    // Weapon? Attachment?
                    tempTitle.append(DataBank.getAwardTitle(tempKey));

                }

                // Set the title
                itemTitle.append(

                        context.getString(

                                tempStatsArray.length() > 1 ? R.string.info_p_awards
                                        : R.string.info_p_award

                                ).replace("{award}", tempTitle + "</b>")

                        );

            } else if (!currItem.isNull("RECEIVEDWALLPOST")) {

                // Get it!
                tempSubItem = currItem.optJSONObject("RECEIVEDWALLPOST");

                // Set it!
                itemTitle.append("<b>{username1} » {username2}:</b> {message}"
                        .replace(

                                "{message}", tempSubItem.getString("wallBody")

                        )

                        );

                // Let's get it!
                otherUserObject = tempSubItem.getJSONObject("writerUser");
                tempGravatarHash = otherUserObject.getString("gravatarMd5");

                // Set the other profile
                profile2 = profile1;
                profile1 = new ProfileData.Builder(
                        Long.parseLong(otherUserObject.getString("userId")),
                        otherUserObject.getString("username")
                        ).gravatarHash(otherUserObject.getString("gravatarMd5")).build();

            } else if (!currItem.isNull("GAMEACCESS")) {

                // Get it!
                tempSubItem = currItem.optJSONObject("GAMEACCESS");

                // Set it!
                itemTitle
                        .append("<b>{username} now has access to <b>{title}</b> for <b>Battlefield 3</b>."
                                .replace(

                                        "{title}", DataBank.getExpansionTitle(tempSubItem
                                                .getString("expansion"))

                                )

                        );

                // Let's get it!
                tempGravatarHash = ownerObject.getString("gravatarMd5");

            } else {

                Log.d(Constants.DEBUG_TAG, "event => " + event);
                throw new WebsiteHandlerException("Unknown event: " + event);

            }
            int typeId = 0; // based on currItem.getString("event")

            // Temporary storage
            FeedItem feedItem = new FeedItem(

                    Long.parseLong(currItem.getString("id")),
                    Long.parseLong(currItem.getString("itemId")),
                    currItem.getLong("creationDate"), numLikes,
                    numComments, typeId,
                    itemTitle.toString(), itemContent.toString(), new ProfileData[] {

                            profile1,
                            profile2

                    }, liked, censored, tempGravatarHash

                    );

            // Fix a filename
            String filename = tempGravatarHash + ".png";

            // Before I forget - let's download the gravatar too!
            if (!CacheHandler.isCached(context, filename)) {

                ProfileClient.cacheGravatar(context, filename,
                        Constants.DEFAULT_AVATAR_SIZE);

            }
            return feedItem;

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
