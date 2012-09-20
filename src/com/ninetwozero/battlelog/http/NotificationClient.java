
package com.ninetwozero.battlelog.http;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ninetwozero.battlelog.datatype.FeedItem;
import com.ninetwozero.battlelog.datatype.NotificationData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;

public class NotificationClient extends DefaultClient {

    public static final String URL_SINGLE = Constants.URL_MAIN
            + "feed/show/{POST_ID}/";

    public NotificationClient() {

        mRequestHandler = new RequestHandler();

    }

    public int getNewNotificationsCount(String checksum)
            throws WebsiteHandlerException {

        try {

            // Init
            String httpContent = mRequestHandler.post(

                    Constants.URL_NOTIFICATIONS_TOP5,
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_GZIP

                    );

            // Got httpContent
            if ("".equals(httpContent)) {

                throw new WebsiteHandlerException("No notifications found.");

            } else {

                // Grab the notifications
                return new JSONObject(httpContent).getJSONObject("data").getInt("numUnread");

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public List<NotificationData> get(String checksum)
            throws WebsiteHandlerException {

        try {

            // Init
            List<NotificationData> notifications = new ArrayList<NotificationData>();
            String httpContent = mRequestHandler.get(Constants.URL_NOTIFICATIONS_ALL,
                    RequestHandler.HEADER_AJAX);

            // Got httpContent
            if ("".equals(httpContent)) {

                throw new WebsiteHandlerException("No notifications found.");

            } else {

                // Grab the notifications
                JSONObject contextObject = new JSONObject(httpContent)
                        .getJSONObject("context");
                JSONObject platoonObject = contextObject
                        .optJSONObject("platoons");
                JSONArray notificationArray = contextObject
                        .getJSONArray("notifications");

                // Now we store the information - easy peasy
                for (int i = 0, max = notificationArray.length(); i < max; i++) {

                    // Grab the current item
                    JSONObject currItem = notificationArray.getJSONObject(i);

                    // Let's see
                    String extra = null;
                    if (currItem.has("platoonId")) {

                        try {

                            extra = (

                                    "["
                                            + platoonObject.getJSONObject(
                                                    currItem.getString("platoonId"))
                                                    .getString("tag") + "] " + platoonObject
                                            .getJSONObject(
                                                    currItem.getString("platoonId"))
                                            .getString("name")

                                    );

                        } catch (JSONException ex) {

                            extra = "*removed platoon*";

                        }

                    }

                    // Add!!
                    notifications.add(

                            new NotificationData(

                                    Long.parseLong(currItem.optString("feedItemId", "0")), Long
                                            .parseLong(currItem.optString("itemOwnerId", "0")),
                                    Long.parseLong(currItem.getString("userId")),
                                    Long.parseLong(currItem.getString("timestamp")),
                                    currItem.optInt("feedItemType", 0), currItem
                                            .optString("itemOwnerUsername", ""),
                                    currItem.getString("username"), currItem
                                            .getString("type"), extra

                            )

                            );

                }

            }

            // Return!!
            return (ArrayList<NotificationData>) notifications;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public FeedItem getPostForNotification(NotificationData n) {

        try {

            // Get the data
            String httpContent = mRequestHandler.get(

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
                        0, 0, title, content,
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

}
