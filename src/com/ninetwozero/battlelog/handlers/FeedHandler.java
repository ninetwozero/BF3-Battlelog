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
import com.ninetwozero.battlelog.datatypes.PersonaData;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class FeedHandler {
    public static FeedItem getPostForNotification(NotificationData n) {

        // Init
        RequestHandler rh = new RequestHandler();

        try {

            // Get the data
            String httpContent = rh.get(
                    Constants.URL_FEED_SINGLE.replace("{POST_ID}",
                            n.getItemId() + ""), 1);

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

                    Log.d(Constants.DEBUG_TAG, "matcherTitle.group() => "
                            + matcherTitle.group());
                    username[1] = matcherTitle.group(1);
                    title = "<b>" + matcherTitle.group(2) + "</b>";
                    title += matcherTitle.group(3);
                    Log.d(Constants.DEBUG_TAG, "title => " + title);
                    break;
                }
                while (matcherContent.find()) {
                    content = matcherContent.group(1);
                }
                while (matcherDate.find()) {
                    date = Long.parseLong(matcherDate.group(1));
                }

                return new FeedItem(

                        id, id, date, 0, // numLikes
                        0, // numComments
                        title, content, "n/a", // type
                        new ProfileData[] {

                                new ProfileData(

                                        uid, username[0], new PersonaData[] {}, gravatar),
                                new ProfileData(

                                        0, username[1], new PersonaData[] {}, null)

                        }, false, // liked
                        false, // censored
                        gravatar // gravatar

                );

            }

            return null;

        } catch (Exception ex) {

            ex.printStackTrace();
            return null;

        }

    }

    public static boolean postToWall(long profileId, String checksum,
            String content, boolean isPlatoon) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

                    Constants.URL_FEED_POST, new PostData[] {

                            new PostData(

                                    Constants.FIELD_NAMES_FEED_POST[0], content

                            ),
                            new PostData(

                                    Constants.FIELD_NAMES_FEED_POST[1], checksum

                            ),
                            new PostData(

                                    Constants.FIELD_NAMES_FEED_POST[(!isPlatoon ? 2 : 3)],
                                    profileId + ""

                            )
                    }, 2

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Check the JSON
                String status = new JSONObject(httpContent).optString(
                        "message", "");
                if (status.equals("WALL_POST_CREATED")
                        || status.equals("PLATOONWALL_POST_CREATED")) {

                    return true;

                } else {

                    return false;

                }

            } else {

                return false;

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

                /*
                 * //Once per loop comments = new ArrayList<CommentData>();
                 * //Let's get the comments if( currItem.getInt("numComments") >
                 * 2 ) { comments = WebsiteHandler.getCommentsForPost(
                 * Long.parseLong(currItem.getString("id")) ); } else if(
                 * currItem.getInt( "numComments" ) == 0 ) { //For now, we do
                 * nothing here } else { //Iterate, as there's only comment1 &
                 * comment2 for( int cCounter = 1; cCounter < 3; cCounter++ ) {
                 * //No comment? if( currItem.isNull( "comment"+cCounter ) )
                 * break; //Grab a temporary comment and add it to our ArrayList
                 * tempCommentItem = currItem.getJSONObject( "comment"+cCounter
                 * ); JSONObject tempOwnerItem =
                 * currItem.getJSONObject("owner"); comments.add( new
                 * CommentData( Long.parseLong(
                 * tempCommentItem.getString("id")), 0, tempCommentItem.getLong(
                 * "creationDate" ), tempCommentItem.getLong( "ownerId" ),
                 * tempOwnerItem.getString("username"),
                 * tempCommentItem.getString( "body" ),
                 * tempOwnerItem.getString("gravatarMd5") ) ); } }
                 */

                // Variables if *modification* is needed
                String itemTitle = "";
                String itemContent = "";
                String tempGravatarHash = ownerObject.getString("gravatarMd5");

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

                // What do we have *here*?
                if (!currItem.isNull("BECAMEFRIENDS")) {

                    // Grab the specific object
                    tempSubItem = currItem.optJSONObject("BECAMEFRIENDS");
                    JSONObject friendUser = tempSubItem
                            .getJSONObject("friendUser");

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments,
                            context.getString(R.string.info_p_friendship), "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ),
                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            friendUser.getString("username"),
                                            new PersonaData[] {},
                                            friendUser.getString("gravatarMd5")

                                    )

                            }, liked, censored, tempGravatarHash

                            );

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

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("CREATEDFORUMTHREAD")) {

                    // Grab the specific object
                    tempSubItem = currItem.optJSONObject("CREATEDFORUMTHREAD");
                    itemTitle = context.getString(R.string.info_p_forumthread)
                            .replace(

                                    "{thread}", tempSubItem.getString("threadTitle")

                            );
                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle,
                            tempSubItem.getString("threadBody"),
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("WROTEFORUMPOST")) {

                    // Grab the specific object
                    tempSubItem = currItem.optJSONObject("WROTEFORUMPOST");
                    itemTitle = context.getString(R.string.info_p_forumpost)
                            .replace(

                                    "{thread}", tempSubItem.getString("threadTitle")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle,
                            tempSubItem.getString("postBody"),
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("GAMEREPORT")) {

                    // Grab the specific object
                    JSONArray tempStatsArray = currItem.optJSONObject(
                            "GAMEREPORT").optJSONArray("statItems");

                    if (tempStatsArray.length() > 1) {

                        itemTitle = context
                                .getString(R.string.info_p_newunlocks);

                    } else {

                        itemTitle = context
                                .getString(R.string.info_p_newunlock);

                    }

                    for (int statsCounter = 0, maxCounter = tempStatsArray
                            .length(); statsCounter < maxCounter; statsCounter++) {

                        // Let's get the item
                        String tempKey;
                        tempSubItem = tempStatsArray
                                .optJSONObject(statsCounter);
                        if (itemContent.equals("")) {
                            itemContent = "<b>";
                        }

                        // Do we need to append anything?
                        if (statsCounter > 0) {

                            if (statsCounter == (maxCounter - 1)) {

                                itemContent += " </b>and<b> ";

                            } else {

                                itemContent += ", ";

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

                                itemContent += tempKey
                                        + " "
                                        + DataBank
                                                .getAttachmentTitle(tempSubItem
                                                        .getString("langKeyTitle"));

                            } else {

                                // Grab a vehicle title then
                                tempKey = DataBank.getVehicleTitle(parentKey);

                                // Validate
                                if (!parentKey.equals(tempKey)) {

                                    itemContent += tempKey
                                            + " "
                                            + DataBank
                                                    .getVehicleUpgradeTitle(tempSubItem
                                                            .getString("langKeyTitle"));

                                } else {

                                    itemContent += tempKey;

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

                                            Log.d(Constants.DEBUG_TAG, tempKey
                                                    + " => " + tempKey);
                                            itemContent += tempKey;

                                        } else {

                                            itemContent += tempKey;

                                        }

                                    } else {

                                        itemContent += tempKey;

                                    }

                                } else {

                                    itemContent += tempKey;

                                }

                            } else {

                                itemContent += tempKey;

                            }

                        }

                    }

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle.replace("{item}",
                                    itemContent + "</b>"), "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("STATUSMESSAGE")) {

                    // Get the JSON-Object
                    tempSubItem = currItem.optJSONObject("STATUSMESSAGE");

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, "<b>{username}</b> "
                                    + tempSubItem.getString("statusMessage"),
                            "", currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("ADDEDFAVSERVER")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("ADDEDFAVSERVER");

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, context.getString(
                                    R.string.info_p_favserver).replace(

                                    "{server}", tempSubItem.getString("serverName")

                                    ), "", currItem.getString("event"),
                            new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

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

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

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

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle,
                            tempSubItem.getString("gameReportComment"),
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("COMMENTEDBLOG")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("COMMENTEDBLOG");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_blog_comment)
                            .replace(

                                    "{post name}", tempSubItem.getString("blogTitle")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle,
                            tempSubItem.getString("blogCommentBody"),
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("JOINEDPLATOON")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("JOINEDPLATOON")
                            .getJSONObject("platoon");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_platoon_join)
                            .replace(

                                    "{platoon}", tempSubItem.getString("name")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

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

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

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

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

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

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

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

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

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

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle,
                            tempSubItem.getString("wallBody"),
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

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

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ),
                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            friendObject.getString("username"),
                                            new PersonaData[] {},
                                            friendObject.getString("gravatarMd5")

                                    )

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("RECEIVEDAWARD")) {

                    // Get it!
                    JSONArray tempStatsArray = currItem.optJSONObject(
                            "RECEIVEDAWARD").optJSONArray("statItems");

                    // Set it!
                    if (tempStatsArray.length() > 1) {

                        itemTitle = context.getString(R.string.info_p_awards);

                    } else {

                        itemTitle = context.getString(R.string.info_p_award);

                    }

                    for (int statsCounter = 0, maxCounter = tempStatsArray
                            .length(); statsCounter < maxCounter; statsCounter++) {

                        // Let's get the item
                        tempSubItem = tempStatsArray
                                .optJSONObject(statsCounter);
                        String tempKey = tempSubItem.getString("langKeyTitle");
                        if (itemContent.equals("")) {
                            itemContent = "<b>";
                        }

                        // Do we need to append anything?
                        if (statsCounter > 0) {

                            if (statsCounter == (maxCounter - 1)) {

                                itemContent += " </b>and<b> ";

                            } else {

                                itemContent += ", ";

                            }

                        }

                        // Weapon? Attachment?
                        itemContent += DataBank.getAwardTitle(tempKey);

                    }

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle.replace("{award}",
                                    itemContent + "</b>"), "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

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

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            otherUserObject.getString("username"),
                                            new PersonaData[] {},
                                            otherUserObject.getString("gravatarMd5")

                                    ),
                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    )

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("GAMEACCESS")) {

                    // Get it!
                    tempSubItem = currItem.optJSONObject("GAMEACCESS");

                    // Set it!
                    itemTitle = "<b>{username} now has access to <b>{title}</b> for <b>Battlefield 3</b>. "
                            .replace(

                                    "{title}", DataBank.getExpansionTitle(tempSubItem
                                            .getString("expansion"))

                            );

                    // Let's get it!
                    tempGravatarHash = ownerObject.getString("gravatarMd5");

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else {

                    Log.d(Constants.DEBUG_TAG,
                            "event => " + currItem.getString("event"));
                    tempFeedItem = null;

                }

                // Append it to the array
                if (tempFeedItem != null) {
                    feedItemArray.add(tempFeedItem);
                }

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

    public static ArrayList<FeedItem> getFeed(Context context, int type, long id, int num,
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
                    url = Constants.URL_FRIEND_FEED;
                    break;

                case FeedItem.TYPE_PROFILE:
                    url = Constants.URL_PROFILE_FEED.replace("{PID}", id + "");
                    break;

                case FeedItem.TYPE_PLATOON:
                    url = Constants.URL_PLATOON_FEED.replace("{PLATOON_ID}", id + "");
                    break;

                default:
                    url = Constants.URL_FRIEND_FEED;
                    break;

            }

            // Let's see
            for (int i = 0, max = Math.round(num / 10); i < max; i++) {

                // Get the content, and create a JSONArray
                httpContent = rh.get(
                        url.replace("{NUMSTART}",
                                String.valueOf(i * 10)), 1);
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


    public static boolean doHooahInFeed(long postId, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.post(

                    Constants.URL_HOOAH.replace("{POST_ID}", postId + ""),
                    new PostData[] {

                        new PostData(

                                Constants.FIELD_NAMES_CHECKSUM[0], checksum

                        )
                    }, 1

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                return true;

            } else {

                throw new WebsiteHandlerException(
                        "Could not hooah the message.");

            }

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean unHooahInFeed(long postId, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.post(

                    Constants.URL_UNHOOAH.replace("{POST_ID}", postId + ""),
                    new PostData[] {

                        new PostData(

                                Constants.FIELD_NAMES_CHECKSUM[0], checksum

                        )
                    }, 1

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                return true;

            } else {

                throw new WebsiteHandlerException(
                        "Could not un-hooah the message.");

            }

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }
}
