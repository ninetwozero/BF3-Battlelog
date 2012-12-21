package com.ninetwozero.bf3droid.http;

import java.util.ArrayList;
import java.util.List;

import com.ninetwozero.bf3droid.datatype.CommentData;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.misc.Constants;

public class CommentClient extends DefaultClient {

    // Attributes
    private long mId;
    private int mType;

    // URLS
    public static final String URL_LIST = Constants.URL_MAIN
            + "feed/getComments/{POST_ID}/";
    public static final String URL_COMMENT = Constants.URL_MAIN
            + "comment/postcomment/{POST_ID}/feed-item-comment/";
    public static final String URL_NEWS_LIST = Constants.URL_MAIN
            + "news/view/{ARTICLE_ID}/{PAGE}/";
    public static final String URL_NEWS_COMMENT = Constants.URL_MAIN
            + "news/view/{ARTICLE_ID}/{PAGE}/True";
    // Attributes
    public static final String[] FIELD_NAMES_COMMENT = new String[]{
            "comment", "post-check-sum"};

    public CommentClient(long i, int t) {
        mRequestHandler = new RequestHandler();
        mId = i;
        mType = t;
    }

    public boolean post(String checksum, String comment) throws WebsiteHandlerException {
        try {
            boolean isFeed = (mType == CommentData.TYPE_FEED);
            String url = RequestHandler.generateUrl(isFeed ? URL_COMMENT : URL_NEWS_COMMENT, mId, 1);

            String httpContent = mRequestHandler.post(
                url, 
                RequestHandler.generatePostData(FIELD_NAMES_COMMENT, comment, checksum), 
	            RequestHandler.HEADER_JSON
            );

            if (httpContent.length() > 0) {
            	JSONObject dataObject = new JSONObject(httpContent).getJSONObject("data");
                return dataObject.isNull("error");
            } else {
                throw new WebsiteHandlerException("Could not post the comment.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public List<CommentData> get() throws WebsiteHandlerException {
        return get(1);
    }

    public List<CommentData> get(int pId) throws WebsiteHandlerException {
        try {
            List<CommentData> comments = new ArrayList<CommentData>();
            boolean isFeed = (mType == CommentData.TYPE_FEED);

            String httpContent = mRequestHandler.get(
                RequestHandler.generateUrl(
            		isFeed ? URL_LIST : URL_NEWS_LIST, 
    				mId, 
    				pId
    			),
                RequestHandler.HEADER_AJAX
            );

            if (httpContent.length() > 0) {

                // Init
                JSONObject dataObject = null;
                JSONObject tempObject = null;
                String keyOwner = "";
                
                // Is it a feed?
                if (isFeed) {
                    dataObject = new JSONObject(httpContent).getJSONObject("data");
                    keyOwner = "owner";
                } else {
                    dataObject = new JSONObject(httpContent).getJSONObject("context").getJSONObject("blogPost");
                    keyOwner = "user";
                }

                JSONArray commentArray = dataObject.getJSONArray("comments");
                for (int i = 0, max = commentArray.length(); i < max; i++) {
                    tempObject = commentArray.optJSONObject(i);
                    JSONObject tempOwnerItem = tempObject.getJSONObject(keyOwner);
                    comments.add(
                        new CommentData(
                            mId,
                            Long.parseLong(tempObject.getString("itemId")), 
                            Long.parseLong(tempObject.getString("creationDate")),
                            tempObject.getString("body"),
                            new ProfileData.Builder(
                                Long.parseLong(tempOwnerItem.getString("userId")),
                                tempOwnerItem.getString("username")
                            ).gravatarHash(tempOwnerItem.getString("gravatarMd5")).build()
                        )
                    );
                }
                return (ArrayList<CommentData>) comments;
            } else {
                throw new WebsiteHandlerException("Could not get the comments.");
            }
        } catch (Exception ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }
}
