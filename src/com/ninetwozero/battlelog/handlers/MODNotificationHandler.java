
package com.ninetwozero.battlelog.handlers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ninetwozero.battlelog.datatypes.NotificationData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class MODNotificationHandler extends DefaultHandler {
    
    public MODNotificationHandler() {
        
        requestHandler = new RequestHandler();
        
    }

    public int getNewNotificationsCount(String checksum)
            throws WebsiteHandlerException {

        try {

            // Init
            String httpContent = requestHandler.post(

                    Constants.URL_NOTIFICATIONS_TOP5,
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_GZIP

                    );

            // Got httpContent
            if (!"".equals(httpContent)) {

                // Grab the notifications
                return new JSONObject(httpContent).getJSONObject("data")
                        .getInt("numUnread");

            } else {

                throw new WebsiteHandlerException("No notifications found.");

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public ArrayList<NotificationData> get(String checksum)
            throws WebsiteHandlerException {

        try {

            // Init
            List<NotificationData> notifications = new ArrayList<NotificationData>();
            String httpContent = requestHandler.get(Constants.URL_NOTIFICATIONS_ALL,
                    RequestHandler.HEADER_AJAX);

            // Got httpContent
            if (!"".equals(httpContent)) {

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

            } else {

                throw new WebsiteHandlerException("No notifications found.");

            }

            // Return!!
            return (ArrayList<NotificationData>) notifications;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }
}
