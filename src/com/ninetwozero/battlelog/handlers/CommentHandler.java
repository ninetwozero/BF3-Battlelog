
package com.ninetwozero.battlelog.handlers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.datatypes.NewsData;
import com.ninetwozero.battlelog.datatypes.PersonaData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class CommentHandler {

    // URLS
    public static final String URL_LIST = Constants.URL_MAIN
            + "feed/getComments/{POST_ID}/";
    public static final String URL_COMMENT = Constants.URL_MAIN
            + "comment/postcomment/{POST_ID}/feed-item-comment/";
   
    // Attributes
    public static final String[] FIELD_NAMES_COMMENT = new String[] {
        "comment", "post-check-sum"
    };

    public static boolean commentOnFeedPost(long postId, String checksum,
            String comment) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.post(

                    RequestHandler.generateUrl(URL_COMMENT, postId),
                    RequestHandler.generatePostData(

                            FIELD_NAMES_COMMENT,
                            comment,
                            checksum
                 
                    ),
                    RequestHandler.HEADER_JSON

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

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

    public static ArrayList<CommentData> getCommentsForPost(long postId)
            throws WebsiteHandlerException {

        try {

            // Let's do this!
            RequestHandler wh = new RequestHandler();
            List<CommentData> comments = new ArrayList<CommentData>();
            String httpContent;

            // Get the content
            httpContent = wh.get(

                    RequestHandler.generateUrl(URL_LIST, postId), 
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Get the messages
                JSONArray commentArray = new JSONObject(httpContent)
                        .getJSONObject("data").getJSONArray("comments");
                JSONObject tempObject;

                // Iterate
                for (int i = 0, max = commentArray.length(); i < max; i++) {

                    tempObject = commentArray.optJSONObject(i);
                    JSONObject tempOwnerItem = tempObject
                            .getJSONObject("owner");
                    comments.add(

                            new CommentData(

                                    postId,
                                    Long.parseLong(tempObject.getString("itemId")),
                                    Long.parseLong(tempObject.getString("creationDate")),
                                    tempObject.getString("body"), new ProfileData(
                                            Long.parseLong(tempOwnerItem.getString("ownerId")),
                                            tempOwnerItem.getString("username"),
                                            new PersonaData[] {},
                                            tempOwnerItem.getString("gravatarMd5")

                                    )

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

    public static boolean commentOnNews(String comment, NewsData n, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

                    RequestHandler.generateUrl(Constants.URL_NEWS_COMMENTS_NEW, n.getId()),
                    RequestHandler.generatePostData(
                            
                            FIELD_NAMES_COMMENT, 
                            comment,
                            checksum

                    ),
                    RequestHandler.HEADER_JSON

                    );

            // Did we manage?
            return (!"".equals(httpContent));

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static List<CommentData> getCommentsForNews(NewsData n, int pageId)
            throws WebsiteHandlerException {

        try {

            // Init!
            RequestHandler rh = new RequestHandler();
            List<CommentData> comments = new ArrayList<CommentData>();

            // Get the data
            String httpContent = rh.get(

                    RequestHandler.generateUrl(Constants.URL_NEWS_COMMENTS, n.getId(), pageId),
                    RequestHandler.HEADER_AJAX
                    
            );

            // Did we get something?
            if (httpContent != null && !httpContent.equals("")) {

                // JSON!
                JSONArray commentArray = new JSONObject(httpContent).getJSONObject("context")
                        .getJSONObject("blogPost").getJSONArray("comments");

                // Iterate
                for (int count = 0, maxCount = commentArray.length(); count < maxCount; count++) {

                    // Get the current item
                    JSONObject item = commentArray.getJSONObject(count);
                    JSONObject user = item.getJSONObject("user");

                    // Handle the data
                    comments.add(

                            new CommentData(

                                    Long.parseLong(item.getString("id")),
                                    Long.parseLong(item.getString("itemId")),
                                    item.getLong("creationDate"),
                                    item.getString("body"),
                                    new ProfileData(
                                            Long.parseLong(user.getString("userId")),
                                            user.getString("username"),
                                            user.getString("gravatarMd5")
                                    )
                            )

                            );

                }

            }

            return comments;

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());
        }

    }
}
