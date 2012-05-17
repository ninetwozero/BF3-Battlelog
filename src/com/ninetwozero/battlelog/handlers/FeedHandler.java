
package com.ninetwozero.battlelog.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.NotificationData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class FeedHandler {

    // URLS
    public static final String URL_FEED = Constants.URL_MAIN + "feed/?start={NUMSTART}";
    public static final String URL_PLATOON = Constants.URL_MAIN
            + "feed/platoonevents/{PLATOON_ID}/?start={NUMSTART}";
    public static final String URL_FRIEND_FEED = Constants.URL_MAIN
            + "feed/homeevents/?start={NUMSTART}";
    public static final String URL_PROFILE = Constants.URL_MAIN
            + "feed/profileevents/{PID}/?start={NUMSTART}";
    public static final String URL_SINGLE = Constants.URL_MAIN
            + "feed/show/{POST_ID}/";
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

    public static FeedItem getPostForNotification(NotificationData n) {

        // Init
        RequestHandler rh = new RequestHandler();

        try {

            // Get the data
            String httpContent = rh.get(

                    RequestHandler.generateUrl(URL_SINGLE, n.getItemId()),
                    RequestHandler.HEADER_AJAX

                    );

            // Did we actually get it?
            if (!"".equals(httpContent)) {

                // Attributes
                long id = 0, uid = 0, date = 0;
                String gravatar = null, title = null, content = null;
                String[] username = new String[2];

                // Patterns
                Pattern patternId = Pattern
                        .compile(Constants.PATTERN_POST_SINGLE_ID);
                Pattern patternUid = Pattern
                        .compile(Constants.PATTERN_POST_SINGLE_UID);
                Pattern patternUsername = Pattern
                        .compile(Constants.PATTERN_POST_SINGLE_USERNAME);
                Pattern patternGravatar = Pattern
                        .compile(Constants.PATTERN_POST_SINGLE_GRAVATAR);
                Pattern patternTitle = Pattern
                        .compile(Constants.PATTERN_POST_SINGLE_TITLE);
                Pattern patternContent = Pattern
                        .compile(Constants.PATTERN_POST_SINGLE_BODY);
                Pattern patternDate = Pattern
                        .compile(Constants.PATTERN_POST_SINGLE_DATE);

                // Matchers
                Matcher matcherId = patternId.matcher(httpContent);
                Matcher matcherUid = patternUid.matcher(httpContent);
                Matcher matcherUsername = patternUsername.matcher(httpContent);
                Matcher matcherGravatar = patternGravatar.matcher(httpContent);
                Matcher matcherTitle = patternTitle.matcher(httpContent);
                Matcher matcherContent = patternContent.matcher(httpContent);
                Matcher matcherDate = patternDate.matcher(httpContent);

                // Loop!
                while (matcherId.find()) {
                    id = Long.parseLong(matcherId.group(1));
                }
                while (matcherUid.find()) {
                    uid = Long.parseLong(matcherUid.group(1));
                }
                while (matcherUsername.find()) {
                    username[0] = matcherUsername.group(1);
                }
                while (matcherGravatar.find()) {
                    gravatar = matcherGravatar.group(1);
                }
                while (matcherTitle.find()) {

                    username[1] = matcherTitle.group(1);
                    title = "<b>" + matcherTitle.group(2) + "</b>";
                    title += matcherTitle.group(3);
                    break;
                }
                while (matcherContent.find()) {
                    content = matcherContent.group(1);
                }
                while (matcherDate.find()) {
                    date = Long.parseLong(matcherDate.group(1));
                }

                return new FeedItem(

                        id, id, date, 0,
                        0,
                        title, content, "n/a",
                        new ProfileData[] {

                                new ProfileData.Builder(uid, username[0]).gravatarHash(gravatar)
                                        .build(),
                                new ProfileData.Builder(0, username[1]).build()
                        },
                        false,
                        false,
                        gravatar

                );

            }

            return null;

        } catch (Exception ex) {

            ex.printStackTrace();
            return null;

        }

    }

    public static boolean post(long profileId, String checksum,
            String content, boolean isPlatoon) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

                    URL_POST,
                    RequestHandler.generatePostData(

                            FIELD_NAMES_POST,
                            content,
                            checksum,
                            !isPlatoon ? profileId : null,
                            isPlatoon ? profileId : null

                            ),
                    RequestHandler.HEADER_JSON

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Check the JSON
                String status = new JSONObject(httpContent).optString("message", "");
                return (status.matches("_POST_CREATED"));

            } else {

                throw new WebsiteHandlerException("Post could not be saved.");

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    private static ArrayList<FeedItem> getFeedItemsFromJSON(Context context,
            JSONArray jsonArray, long activeProfileId)
            throws WebsiteHandlerException {

        try {

            // Variables that we need
            JSONObject currItem = null;
            JSONObject tempSubItem = null;
            JSONObject ownerObject = null;
            JSONObject otherUserObject = null;
            FeedItem tempFeedItem = null;
            List<FeedItem> feedItemArray = new ArrayList<FeedItem>();

            // Iterate over the feed
            for (int i = 0, max = jsonArray.length(); i < max; i++) {

                // Each loop is an object
                currItem = jsonArray.getJSONObject(i);

                // If we get a null, we do it my way!
                ownerObject = currItem.optJSONObject("owner");
                if (ownerObject == null) {
                    continue;
                }

                // Variables if *modification* is needed
                String itemTitle = "";
                String itemContent = "";
                String tempGravatarHash = ownerObject.getString("gravatarMd5");

                // Temporary
                ProfileData profile1 = null, profile2 = null;

                // Process the likes
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
                profile1 = new ProfileData.Builder(
                        Long.parseLong(currItem.getString("ownerId")),
                        ownerObject.getString("username")
                        ).gravatarHash(ownerObject.getString("gravatarMd5")).build();

                // What do we have *here*?
                if (!currItem.isNull("BECAMEFRIENDS")) {

                    // Grab the specific object
                    tempSubItem = currItem.optJSONObject("BECAMEFRIENDS");
                    JSONObject friendUser = tempSubItem
                            .getJSONObject("friendUser");

                    // Set the first profile
                    profile2 = new ProfileData.Builder(
                            Long.parseLong(friendUser.getString("ownerId")),
                            friendUser.getString("username")
                            ).gravatarHash(friendUser.getString("gravatarMd5")).build();

                    // Get the title
                    itemTitle = context.getString(R.string.info_p_friendship);

                } else if (!currItem.isNull("ASSIGNMENTCOMPLETE")) {

                    // Grab the specific object
                    tempSubItem = currItem.optJSONObject("ASSIGNMENTCOMPLETE");
                    JSONObject statsItem = tempSubItem
                            .getJSONArray("statItems").getJSONObject(0);
                    String[] tempInfo = DataBank.getAssignmentTitle(statsItem
                            .getString("langKeyTitle"));

                    // Set the title
                    itemTitle = context
                            .getString(R.string.info_txt_assignment_ok)
                            .replace(

                                    "{assignment}", tempInfo[0]

                            ).replace(

                                    "{unlock}", tempInfo[1]

                            );

                } else if (!currItem.isNull("CREATEDFORUMTHREAD")) {

                    // Grab the specific object
                    tempSubItem = currItem.optJSONObject("CREATEDFORUMTHREAD");
                    itemTitle = context.getString(R.string.info_p_forumthread)
                            .replace(

                                    "{thread}", tempSubItem.getString("threadTitle")

                            );
                    itemContent = tempSubItem.getString("threadBody");

                } else if (!currItem.isNull("WROTEFORUMPOST")) {

                    // Grab the specific object
                    tempSubItem = currItem.optJSONObject("WROTEFORUMPOST");
                    itemTitle = context.getString(R.string.info_p_forumpost)
                            .replace(

                                    "{thread}", tempSubItem.getString("threadTitle")

                            );
                    itemContent = tempSubItem.getString("postBody");

                } else if (!currItem.isNull("GAMEREPORT")) {

                    // Grab the specific object
                    JSONArray tempStatsArray = currItem.optJSONObject(
                            "GAMEREPORT").optJSONArray("statItems");

                    // INit the String
                    String tempTitle = "";

                    // Set the things straight
                    itemTitle = context
                            .getString(tempStatsArray.length() > 1 ? R.string.info_p_newunlocks
                                    : R.string.info_p_newunlock);

                    /* TODO: EXPORT TO SMALLER METHODS */
                    for (int statsCounter = 0, maxCounter = tempStatsArray
                            .length(); statsCounter < maxCounter; statsCounter++) {

                        // Let's get the item
                        String tempKey;
                        tempSubItem = tempStatsArray
                                .optJSONObject(statsCounter);
                        if (tempTitle.equals("")) {
                            tempTitle = "<b>";
                        }

                        // Do we need to append anything?
                        if (statsCounter > 0) {

                            if (statsCounter == (maxCounter - 1)) {

                                tempTitle += " </b>and<b> ";

                            } else {

                                tempTitle += ", ";

                            }

                        }

                        // Weapon? Attachment?
                        if (!tempSubItem.isNull("parentLangKeyTitle")) {

                            // Let's see
                            String parentKey = tempSubItem
                                    .getString("parentLangKeyTitle");
                            tempKey = DataBank.getWeaponTitle(parentKey);

                            // Is it empty?
                            if (!parentKey.equals(tempKey)) {

                                tempTitle += tempKey
                                        + " "
                                        + DataBank
                                                .getAttachmentTitle(tempSubItem
                                                        .getString("langKeyTitle"));

                            } else {

                                // Grab a vehicle title then
                                tempKey = DataBank.getVehicleTitle(parentKey);

                                // Validate
                                if (!parentKey.equals(tempKey)) {

                                    tempTitle += tempKey
                                            + " "
                                            + DataBank
                                                    .getVehicleUpgradeTitle(tempSubItem
                                                            .getString("langKeyTitle"));

                                } else {

                                    tempTitle += tempKey;

                                }

                            }

                        } else {

                            // Let's see
                            String key = tempSubItem.getString("langKeyTitle");
                            tempKey = DataBank.getWeaponTitle(key);

                            if (key.equals(tempKey)) {

                                tempKey = DataBank.getVehicleUpgradeTitle(key);

                                if (key.equals(tempKey)) {

                                    tempKey = DataBank.getKitTitle(key);

                                    if (key.equals(tempKey)) {

                                        tempKey = DataBank.getSkillTitle(key);

                                        if (key.equals(tempKey)) {

                                            tempTitle += tempKey;

                                        } else {

                                            tempTitle += tempKey;

                                        }

                                    } else {

                                        tempTitle += tempKey;

                                    }

                                } else {

                                    tempTitle += tempKey;

                                }

                            } else {

                                tempTitle += tempKey;

                            }

                        }

                    }

                    itemTitle = itemTitle.replace("{item}", tempTitle + "</b>");

                } else if (!currItem.isNull("STATUSMESSAGE")) {

                    // Get the JSON-Object
                    tempSubItem = currItem.optJSONObject("STATUSMESSAGE");

                    // Set the title
                    itemTitle = "<b>{username}</b> {message}"
                            + tempSubItem.getString("statusMessage");

                } else if (!currItem.isNull("ADDEDFAVSERVER")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("ADDEDFAVSERVER");

                    // Set the title
                    itemTitle = context.getString(R.string.info_p_favserver).replace(

                            "{server}", tempSubItem.getString("serverName")

                            );

                } else if (!currItem.isNull("RANKEDUP")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("RANKEDUP");

                    // Set it!
                    itemTitle = context
                            .getString(R.string.info_p_promotion)
                            .replace(

                                    "{rank title}",
                                    DataBank.getRankTitle(tempSubItem
                                            .getString("langKeyTitle"))

                            ).replace(

                                    "{rank}", tempSubItem.getString("rank")

                            );

                } else if (!currItem.isNull("COMMENTEDGAMEREPORT")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("COMMENTEDGAMEREPORT");

                    // Set it!
                    itemTitle = context
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

                            );
                    itemContent = tempSubItem.getString("gameReportComment");

                } else if (!currItem.isNull("COMMENTEDBLOG")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("COMMENTEDBLOG");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_blog_comment)
                            .replace(

                                    "{post name}", tempSubItem.getString("blogTitle")

                            );

                    itemContent = tempSubItem.getString("blogCommentBody");

                } else if (!currItem.isNull("JOINEDPLATOON")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("JOINEDPLATOON")
                            .getJSONObject("platoon");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_platoon_join)
                            .replace(

                                    "{platoon}", tempSubItem.getString("name")

                            );

                } else if (!currItem.isNull("KICKEDPLATOON")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("KICKEDPLATOON")
                            .getJSONObject("platoon");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_platoon_kick)
                            .replace(

                                    "{platoon}", tempSubItem.getString("name")

                            );

                } else if (!currItem.isNull("CREATEDPLATOON")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("CREATEDPLATOON")
                            .getJSONObject("platoon");

                    // Set it!
                    itemTitle = context.getString(
                            R.string.info_p_platoon_create).replace(

                            "{platoon}", tempSubItem.getString("name")

                            );

                } else if (!currItem.isNull("PLATOONBADGESAVED")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("PLATOONBADGESAVED")
                            .getJSONObject("platoon");

                    // Set it!
                    itemTitle = context
                            .getString(R.string.info_p_platoon_badge).replace(

                                    "{platoon}", tempSubItem.getString("name")

                            );

                } else if (!currItem.isNull("LEFTPLATOON")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("LEFTPLATOON")
                            .getJSONObject("platoon");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_platoon_left)
                            .replace(

                                    "{platoon}", tempSubItem.getString("name")

                            );

                } else if (!currItem.isNull("RECEIVEDPLATOONWALLPOST")) {

                    // Get it!
                    tempSubItem = currItem
                            .getJSONObject("RECEIVEDPLATOONWALLPOST");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_platoon_feed)
                            .replace(

                                    "{platoon}",
                                    tempSubItem.getJSONObject("platoon")
                                            .getString("name")

                            );

                    itemContent = tempSubItem.getString("wallBody");

                } else if (!currItem.isNull("LEVELCOMPLETE")) {

                    // Get em!
                    tempSubItem = currItem.getJSONObject("LEVELCOMPLETE");
                    JSONObject friendObject = tempSubItem
                            .getJSONObject("friend");

                    // Set it!
                    itemTitle = context
                            .getString(R.string.info_p_coop_level_comp)
                            .replace(

                                    "{level}",
                                    DataBank.getCoopLevelTitle(tempSubItem
                                            .getString("level"))

                            ).replace(

                                    "{difficulty}", tempSubItem.getString("difficulty")

                            );

                    // Set the second profile
                    profile2 = new ProfileData.Builder(
                            Long.parseLong(friendObject.getString("ownerId")),
                            ownerObject.getString("username")
                            ).gravatarHash(ownerObject.getString("gravatarMd5")).build();

                } else if (!currItem.isNull("RECEIVEDAWARD")) {

                    // Get it!
                    JSONArray tempStatsArray = currItem.optJSONObject(
                            "RECEIVEDAWARD").optJSONArray("statItems");

                    // Init a String
                    String tempTitle = "";

                    // Set it!
                    itemTitle = context
                            .getString(tempStatsArray.length() > 1 ? R.string.info_p_awards
                                    : R.string.info_p_award);

                    for (int statsCounter = 0, maxCounter = tempStatsArray
                            .length(); statsCounter < maxCounter; statsCounter++) {

                        // Let's get the item
                        tempSubItem = tempStatsArray
                                .optJSONObject(statsCounter);
                        String tempKey = tempSubItem.getString("langKeyTitle");
                        if (tempTitle.equals("")) {
                            tempTitle = "<b>";
                        }

                        // Do we need to append anything?
                        if (statsCounter > 0) {

                            if (statsCounter == (maxCounter - 1)) {

                                tempTitle += " </b>and<b> ";

                            } else {

                                tempTitle += ", ";

                            }

                        }

                        // Weapon? Attachment?
                        tempTitle += DataBank.getAwardTitle(tempKey);

                    }

                    // Set the title
                    itemTitle = itemTitle.replace("{award}", tempTitle + "</b>");

                } else if (!currItem.isNull("RECEIVEDWALLPOST")) {

                    // Get it!
                    tempSubItem = currItem.optJSONObject("RECEIVEDWALLPOST");

                    // Set it!
                    itemTitle = "<b>{username1} » {username2}:</b> {message}"
                            .replace(

                                    "{message}", tempSubItem.getString("wallBody")

                            );

                    // Let's get it!
                    otherUserObject = tempSubItem.getJSONObject("writerUser");
                    tempGravatarHash = otherUserObject.getString("gravatarMd5");

                    // Set the other profile
                    profile2 = new ProfileData.Builder(
                            Long.parseLong(currItem.getString("ownerId")),
                            ownerObject.getString("username")
                            ).gravatarHash(ownerObject.getString("gravatarMd5")).build();

                } else if (!currItem.isNull("GAMEACCESS")) {

                    // Get it!
                    tempSubItem = currItem.optJSONObject("GAMEACCESS");

                    // Set it!
                    itemTitle = "<b>{username} now has access to <b>{title}</b> for <b>Battlefield 3</b>."
                            .replace(

                                    "{title}", DataBank.getExpansionTitle(tempSubItem
                                            .getString("expansion"))

                            );

                    // Let's get it!
                    tempGravatarHash = ownerObject.getString("gravatarMd5");

                } else {

                    Log.d(Constants.DEBUG_TAG,
                            "event => " + currItem.getString("event"));
                    continue;

                }

                // Temporary storage
                tempFeedItem = new FeedItem(

                        Long.parseLong(currItem.getString("id")),
                        Long.parseLong(currItem.getString("itemId")),
                        currItem.getLong("creationDate"), numLikes,
                        numComments,
                        itemTitle, itemContent,
                        currItem.getString("event"), new ProfileData[] {

                                profile1,
                                profile2

                        }, liked, censored, tempGravatarHash

                        );

                // Append it to the array
                feedItemArray.add(tempFeedItem);

                // Fix a filename
                String filename = tempGravatarHash + ".png";

                // Before I forget - let's download the gravatar too!
                if (!CacheHandler.isCached(context, filename)) {

                    WebsiteHandler.cacheGravatar(context, filename,
                            Constants.DEFAULT_AVATAR_SIZE);

                }

            }

            return (ArrayList<FeedItem>) feedItemArray;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }
    }

    public static ArrayList<FeedItem> get(Context context, int type, long id, int num,
            long profileId) throws WebsiteHandlerException {

        try {

            // Attributes
            RequestHandler rh = new RequestHandler();
            List<FeedItem> feedItems = new ArrayList<FeedItem>();
            JSONArray jsonArray;
            String url = "";
            String httpContent = null;

            // What's the url?
            switch (type) {

                case FeedItem.TYPE_GLOBAL:
                    url = URL_FRIEND_FEED;
                    break;

                case FeedItem.TYPE_PROFILE:
                    url = RequestHandler.generateUrl(URL_PROFILE, id);
                    break;

                case FeedItem.TYPE_PLATOON:
                    url = RequestHandler.generateUrl(URL_PLATOON, id);
                    break;

                default:
                    url = URL_FRIEND_FEED;
                    break;

            }

            // Let's see
            for (int i = 0, max = Math.round(num / 10); i < max; i++) {

                // Get the content, and create a JSONArray
                httpContent = rh.get(
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
                feedItems.addAll(FeedHandler.getFeedItemsFromJSON(context,
                        jsonArray, profileId));

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
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

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
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

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
