package com.ninetwozero.battlelog.handlers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.datatypes.NewsData;
import com.ninetwozero.battlelog.datatypes.PersonaData;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class CommentHandler {
    public static boolean commentOnFeedPost(long postId, String checksum,
            String comment) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.post(

                    Constants.URL_COMMENT.replace("{POST_ID}", postId + ""),
                    new PostData[] {

                            new PostData(

                                    Constants.FIELD_NAMES_FEED_COMMENT[0], comment

                            ), new PostData(

                                    Constants.FIELD_NAMES_FEED_COMMENT[1], checksum

                            )
                    }, 2 // Noticed the 2?

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Hopefully this goes as planned
                if (!httpContent.equals("Internal server error")) {

                    return true;

                } else {

                    return false;

                }

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

                    Constants.URL_FEED_COMMENTS.replace(

                            "{POST_ID}", postId + ""

                            ), 0

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

                    Constants.URL_NEWS_COMMENTS_NEW.replace(

                            "{ARTICLE_ID}", n.getId() + ""

                            ), new PostData[] {

                            new PostData(

                                    Constants.FIELD_NAMES_COMMENTS_NEW[0], comment

                            ),
                            new PostData(

                                    Constants.FIELD_NAMES_COMMENTS_NEW[1], checksum

                            )
                    }, 2

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                return true;

            } else {

                return false;

            }

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
                    Constants.URL_NEWS_COMMENTS.replace("{ARTICLE_ID}", n.getId() + "").replace(
                            "{PAGE}", pageId + ""), 1);

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
