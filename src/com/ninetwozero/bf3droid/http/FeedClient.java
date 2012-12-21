package com.ninetwozero.bf3droid.http;

import java.util.ArrayList;
import java.util.List;

import com.ninetwozero.bf3droid.datatype.FeedItem;
import com.ninetwozero.bf3droid.datatype.ParsedFeedItemData;
import com.ninetwozero.bf3droid.datatype.RequestHandlerException;
import com.ninetwozero.bf3droid.misc.CacheHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ninetwozero.bf3droid.datatype.CommentData;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.factory.FeedItemDataFactory;
import com.ninetwozero.bf3droid.misc.Constants;

public class FeedClient extends DefaultClient {

    // Attributes
    private long mId;
    private int mType;

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
    public static final String URL_HOOAH_LIST = Constants.URL_MAIN
    		+ "feed/showlikes/{POST_ID}/";
    
    // Constants
    public static final String[] FIELD_NAMES_POST = new String[]{
            "wall-message", "post-check-sum", "wall-ownerId", "wall-platoonId"
    };

    public final static String[] EVENT_LABELS = new String[]{
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
        mId = i;
        mType = t;
    }

    public boolean post(String checksum, String content) throws WebsiteHandlerException {
        try {
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(
                URL_POST,
                RequestHandler.generatePostData(
                    FIELD_NAMES_POST,
                    content,
                    checksum,
                    mType == TYPE_PLATOON ? null : mId,
                    mType == TYPE_PLATOON ? mId : null
                ),
                RequestHandler.HEADER_AJAX
            );
            if ("".equals(httpContent)) {
                throw new WebsiteHandlerException("Post could not be saved.");
            } else {
                String status = new JSONObject(httpContent).optString("message", "");
                return (status.matches("_POST_CREATED"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public ArrayList<FeedItem> get(Context context, int num, long profileId) throws WebsiteHandlerException {
        try {
            List<FeedItem> feedItems = new ArrayList<FeedItem>();
            JSONArray jsonArray;
            String url;
            
            switch (mType) {
                case TYPE_GLOBAL:
                    url = URL_FRIEND_FEED;
                    break;
                case TYPE_PROFILE:
                    url = RequestHandler.generateUrl(URL_PROFILE, mId);
                    break;
                case TYPE_PLATOON:
                    url = RequestHandler.generateUrl(URL_PLATOON, mId);
                    break;
                default:
                    url = URL_FRIEND_FEED;
                    break;
            }

            for (int i = 0, max = Math.round(num / 10); i < max; i++) {
                String httpContent = mRequestHandler.get(
                    url.replace(
                        "{NUMSTART}",
                        String.valueOf(i * 10)
                    ),
                    RequestHandler.HEADER_AJAX
                );

                jsonArray = new JSONObject(httpContent).getJSONObject("data").getJSONArray("feedEvents");
                feedItems.addAll(getFeedItemsFromJSON(context, jsonArray, profileId));
            }
            return (ArrayList<FeedItem>) feedItems;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    private ArrayList<FeedItem> getFeedItemsFromJSON(Context context, JSONArray jsonArray, long activeProfileId) {
        List<FeedItem> feedItemArray = new ArrayList<FeedItem>();
        try {
            for (int i = 0, max = jsonArray.length(); i < max; i++) {
                feedItemArray.add(getFeedItemFromJSON(context, jsonArray.getJSONObject(i), activeProfileId));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (ArrayList<FeedItem>) feedItemArray;
    }
 
    private FeedItem getFeedItemFromJSON(Context context, JSONObject currItem, long activeProfileId) throws WebsiteHandlerException {
        try {
        	List<CommentData> comments = new ArrayList<CommentData>();
            JSONObject ownerObject = currItem.optJSONObject("owner");
            final String event = currItem.getString("event");

            // Process the likes & comments
            JSONArray likeUsers = currItem.getJSONArray("likeUserIds");
            int numLikes = likeUsers.length();
            int numComments = currItem.getInt("numComments");
            boolean liked = false;
            boolean censored = currItem.getBoolean("hidden");

            for (int likeCount = 0; likeCount < numLikes; likeCount++) {
                if (Long.parseLong(likeUsers.getString(likeCount)) == activeProfileId) {
                    liked = true;
                    break;
                }
            }
            
            final String[] jsonCommentLabels = new String[] {"comment1", "comment2"};
            for( String label : jsonCommentLabels ) {
	            if( !currItem.isNull(label) ) {
	            	JSONObject comment = currItem.getJSONObject(label);
	            	JSONObject commenter = comment.getJSONObject("owner");
	            	String gravatarHash = commenter.getString("gravatarMd5");
	            	comments.add( 
	        			new CommentData(
	    					Long.parseLong(comment.getString("id")), 
	    					0, 
	    					comment.getLong("creationDate"), 
	    					comment.getString("body"), 
	    					new ProfileData.Builder(
	    						Long.parseLong(commenter.getString("userId")),
	    						commenter.getString("username")
							).gravatarHash(gravatarHash).build()
						)
	    			);
	            	
	            	String filename = gravatarHash + ".png";
	                if (!CacheHandler.isCached(context, filename)) {
	                    ProfileClient.cacheGravatar(context, filename, Constants.DEFAULT_AVATAR_SIZE);
	                }
	            }
            }
	            
            ProfileData mainProfile = new ProfileData.Builder(
                    Long.parseLong(currItem.getString("ownerId")),
                    ownerObject.getString("username")
            ).gravatarHash(ownerObject.getString("gravatarMd5")).build();

            int feedItemTypeId = getTypeIdFromEvent(event);
            ParsedFeedItemData feedItemData = FeedItemDataFactory.feedItemDataFrom(context, feedItemTypeId, mainProfile, currItem);

            String filename = feedItemData.getGravatarHash() + ".png";
            if (!CacheHandler.isCached(context, filename)) {
                ProfileClient.cacheGravatar(context, filename, Constants.DEFAULT_AVATAR_SIZE);
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
                feedItemData.getGravatarHash(),
                comments
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
        Log.d(Constants.DEBUG_TAG, "Event '" + event + "' unknown.");
        return -1;
    }


    public static boolean hooah(long postId, String checksum) throws WebsiteHandlerException {
        try {
            String httpContent = new RequestHandler().post(
                    RequestHandler.generateUrl(URL_HOOAH, postId),
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_AJAX
            );
            return (!"".equals(httpContent));
        } catch (RequestHandlerException ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public static boolean unhooah(long postId, String checksum) throws WebsiteHandlerException {
        try {
            String httpContent = new RequestHandler().post(
                    RequestHandler.generateUrl(URL_UNHOOAH, postId),
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_AJAX
            );
            return (!"".equals(httpContent));
        } catch (RequestHandlerException ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }
    
    public List<ProfileData> getHooahs() throws WebsiteHandlerException {
    	try {
    		List<ProfileData> profiles = new ArrayList<ProfileData>();
    		String httpContent = mRequestHandler.get(
    			RequestHandler.generateUrl(URL_HOOAH_LIST, mId),
    			RequestHandler.HEADER_GZIP
			);

    		// Get the JSON and subsequently add the profiles
    		JSONArray jsonProfiles = new JSONObject(httpContent).getJSONObject("data").getJSONArray("likeUsers");
    		for(int i = 0, max = jsonProfiles.length(); i < max; i++) {
    			JSONObject current = jsonProfiles.getJSONObject(i);
    			profiles.add(
    				new ProfileData.Builder(current.getLong("userId"), current.getString("username"))
    				.gravatarHash(current.getString("gravatarMd5"))
    				.build()
    			);    			
    		}
    		return profiles;
    	} catch(Exception ex) {
    		throw new WebsiteHandlerException(ex.getMessage());
    	}
    }
}
