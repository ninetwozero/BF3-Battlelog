package com.ninetwozero.battlelog.http;

import android.content.Context;
import com.ninetwozero.battlelog.datatype.*;
import com.ninetwozero.battlelog.factory.FeedItemDataFactory;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
            "gameaccess",
            "sharedgameevent"
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
            String url;
            String httpContent;

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

            int feedItemTypeId = getTypeIdFromEvent(event);
            ParsedFeedItemData feedItemData = FeedItemDataFactory.feedItemDataFrom(context, feedItemTypeId, mainProfile, currItem);


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
                    feedItemTypeId,
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
