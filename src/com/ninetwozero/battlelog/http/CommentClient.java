
package com.ninetwozero.battlelog.http;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.ninetwozero.battlelog.datatype.CommentData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;

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
    public static final String[] FIELD_NAMES_COMMENT = new String[] {
            "comment", "post-check-sum"
    };

    public CommentClient(long i, int t) {

        mRequestHandler = new RequestHandler();
        mId = i;
        mType = t;

    }

    public boolean post(String checksum, String comment) throws WebsiteHandlerException {

        try {

            // Let's post!
            boolean isFeed = (mType == CommentData.TYPE_FEED);
            String url = RequestHandler
                    .generateUrl(isFeed ? URL_COMMENT : URL_NEWS_COMMENT, mId, 1);

            // Get the httpContent
            String httpContent = mRequestHandler.post(

                    url,
                    RequestHandler.generatePostData(

                            FIELD_NAMES_COMMENT,
                            comment,
                            checksum

                            ),
                    RequestHandler.HEADER_JSON

                    );

            // Did we manage?
            if (httpContent.length() > 0) {

                // Hopefully this goes as planned
                return (!httpContent.equals("Internal server error"));

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

    public List<CommentData> get(int pId)
            throws WebsiteHandlerException {

        try {

            // Let's do this!
            List<CommentData> comments = new ArrayList<CommentData>();
            boolean isFeed = (mType == CommentData.TYPE_FEED);

            // Get the content depending on the pagee
            String httpContent = mRequestHandler.get(

                    RequestHandler.generateUrl(

                            isFeed ? URL_LIST : URL_NEWS_LIST,
                            mId,
                            pId
                            ),
                    RequestHandler.HEADER_AJAX

                    );

            // Did we manage?
            if (httpContent.length() > 0) {

                // Init
                JSONObject dataObject = null;
                JSONObject tempObject = null;

                // Is it a feed?
                if (isFeed) {

                    dataObject = new JSONObject(httpContent).getJSONObject("data");

                } else {

                    dataObject = new JSONObject(httpContent).getJSONObject("context")
                            .getJSONObject("blogPost");

                }
                
                // Get the comment array
                JSONArray commentArray = dataObject.getJSONArray("comments");

                // Iterate
                for (int i = 0, max = commentArray.length(); i < max; i++) {

                    tempObject = commentArray.optJSONObject(i);
                    JSONObject tempOwnerItem = tempObject.getJSONObject("owner");
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
