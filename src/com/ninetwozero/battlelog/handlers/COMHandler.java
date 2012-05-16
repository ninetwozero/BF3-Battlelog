
package com.ninetwozero.battlelog.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.ChatMessage;
import com.ninetwozero.battlelog.datatypes.FriendListDataWrapper;
import com.ninetwozero.battlelog.datatypes.PersonaData;
import com.ninetwozero.battlelog.datatypes.ProfileComparator;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class COMHandler {

    // URLS
    public static final String URL_FRIENDS = Constants.URL_MAIN + "comcenter/sync/";
    public static final String URL_FRIEND_REQUESTS = Constants.URL_MAIN
            + "friend/requestFriendship/{UID}/";
    public static final String URL_FRIEND_DELETE = Constants.URL_MAIN
            + "friend/removeFriend/{UID}/";
    public static final String URL_CONTENTS = Constants.URL_MAIN
            + "comcenter/getChatId/{UID}/";
    public static final String URL_SEND = Constants.URL_MAIN
            + "comcenter/sendChatMessage/";
    public static final String URL_CLOSE = Constants.URL_MAIN
            + "comcenter/hideChat/{CID}/";
    public static final String URL_SETACTIVE = Constants.URL_MAIN
            + "comcenter/setActive/";
    
    // Constants
    public static final String[] FIELD_NAMES_CHAT = new String[] {
            "message",
            "chatId", "post-check-sum"
    };

    public static Long getChatId(long profileId, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's do this!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.post(

                    RequestHandler.generateUrl(URL_CONTENTS, profileId),
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Get the messages
                return new JSONObject(httpContent).getJSONObject("data")
                        .getLong("chatId");

            } else {

                throw new WebsiteHandlerException("Could not get the chatId");

            }

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static ArrayList<ChatMessage> getChatMessages(long profileId,
            String checksum) throws WebsiteHandlerException {

        try {

            // Let's do this!
            RequestHandler wh = new RequestHandler();
            List<ChatMessage> messageArray = new ArrayList<ChatMessage>();
            String httpContent;

            // Get the content
            httpContent = wh.post(

                    RequestHandler.generateUrl(URL_CONTENTS, profileId),
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Get the messages
                JSONArray messages = new JSONObject(httpContent)
                        .getJSONObject("data").getJSONObject("chat")
                        .getJSONArray("messages");
                JSONObject tempObject;

                // Iterate
                for (int i = 0, max = messages.length(); i < max; i++) {

                    tempObject = messages.optJSONObject(i);
                    messageArray.add(

                            new ChatMessage(

                                    profileId, tempObject.getLong("timestamp"), tempObject
                                            .getString("fromUsername"), tempObject
                                            .getString("message")

                            )

                            );

                }
                return (ArrayList<ChatMessage>) messageArray;

            } else {

                throw new WebsiteHandlerException(
                        "Could not get the chat messages.");

            }

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean sendChatMessages(long chatId, String checksum,
            String message) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.post(

                    URL_SEND,
                    RequestHandler.generatePostData(

                            FIELD_NAMES_CHAT,
                            message,
                            chatId,
                            checksum
                            ),
                    RequestHandler.HEADER_AJAX

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                return true;

            } else {

                throw new WebsiteHandlerException(
                        "Could not send the chat message.");

            }

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean closeChatWindow(long chatId)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler rh = new RequestHandler();

            // Get the content
            rh.get(
                    RequestHandler.generateUrl(URL_CLOSE, chatId),
                    RequestHandler.HEADER_AJAX
                    );

            // Did we manage?
            return true;

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static final FriendListDataWrapper getFriendsCOM(final Context c,
            final String checksum) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.post(

                    URL_FRIENDS,
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Generate an object
                JSONObject comData = new JSONObject(httpContent)
                        .getJSONObject("data");
                JSONArray friendsObject = comData
                        .getJSONArray("friendscomcenter");
                JSONArray requestsObject = comData
                        .getJSONArray("friendrequests");
                JSONObject tempObj, presenceObj;

                // Arraylists!
                List<ProfileData> friends = new ArrayList<ProfileData>();
                List<ProfileData> profileRowRequests = new ArrayList<ProfileData>();
                List<ProfileData> profileRowPlaying = new ArrayList<ProfileData>();
                List<ProfileData> profileRowOnline = new ArrayList<ProfileData>();
                List<ProfileData> profileRowOffline = new ArrayList<ProfileData>();

                // Grab the lengths
                int numRequests = requestsObject.length();
                int numFriends = friendsObject.length();
                int numPlaying = 0;
                int numOnline = 0;
                int numOffline = 0;

                // Got requests?
                if (numRequests > 0) {

                    // Temp
                    ProfileData tempProfileData;

                    // Iterate baby!
                    for (int i = 0, max = requestsObject.length(); i < max; i++) {

                        // Grab the object
                        tempObj = requestsObject.optJSONObject(i);

                        // Save it
                        profileRowRequests.add(

                                tempProfileData = new ProfileData(
                                        Long.parseLong(tempObj.getString("userId")),
                                        tempObj.getString("username"),
                                        new PersonaData[] {},
                                        tempObj.optString("gravatarMd5", "")

                                        )

                                );

                        tempProfileData.setFriend(false);

                    }

                    // Sort it out
                    Collections.sort(profileRowRequests,
                            new ProfileComparator());
                    friends.add(new ProfileData(c.getString(R.string.info_xml_friend_requests)));
                    friends.addAll(profileRowRequests);

                }

                // Do we have more than... well, at least one friend?
                if (numFriends > 0) {

                    // Iterate baby!
                    for (int i = 0; i < numFriends; i++) {

                        // Grab the object
                        tempObj = friendsObject.optJSONObject(i);
                        presenceObj = tempObj.getJSONObject("presence");

                        // Save it
                        ProfileData tempProfile = new ProfileData(

                                Long.parseLong(tempObj.getString("userId")),
                                tempObj.getString("username"),
                                new PersonaData[] {},
                                tempObj.optString("gravatarMd5", ""),
                                presenceObj.getBoolean("isOnline"),
                                presenceObj.getBoolean("isPlaying")

                                );

                        if (tempProfile.isPlaying()) {

                            profileRowPlaying.add(tempProfile);

                        } else if (tempProfile.isOnline()) {

                            profileRowOnline.add(tempProfile);

                        } else {

                            profileRowOffline.add(tempProfile);

                        }

                    }

                    // How many "online" friends do we have? Playing + idle
                    numPlaying = profileRowPlaying.size();
                    numOnline = profileRowOnline.size();
                    numOffline = profileRowOffline.size();

                    // First add the separators)...
                    if (numPlaying > 0) {

                        Collections.sort(profileRowPlaying, new ProfileComparator());
                        friends.add(new ProfileData(c.getString(R.string.info_txt_friends_playing)));
                        friends.addAll(profileRowPlaying);
                    }

                    if (numOnline > 0) {

                        // ...then we sort it out...
                        Collections.sort(profileRowOnline, new ProfileComparator());
                        friends.add(new ProfileData(c.getString(R.string.info_txt_friends_online)));
                        friends.addAll(profileRowOnline);
                    }

                    if (numOffline > 0) {

                        Collections.sort(profileRowOffline, new ProfileComparator());
                        friends.add(new ProfileData(c.getString(R.string.info_txt_friends_offline)));
                        friends.addAll(profileRowOffline);
                    }
                }

                return new FriendListDataWrapper(friends, numRequests, numPlaying, numOnline,
                        numOffline);

            } else {

                throw new WebsiteHandlerException(
                        "Could not retrieve the ProfileIDs.");

            }

        } catch (JSONException e) {

            throw new WebsiteHandlerException(e.getMessage());

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static final List<ProfileData> getFriends(String checksum,
            boolean noOffline) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;
            List<ProfileData> profileArray = new ArrayList<ProfileData>();

            // Get the content
            httpContent = wh.post(

                    URL_FRIENDS,
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Generate an object
                JSONArray profileObject = new JSONObject(httpContent)
                        .getJSONObject("data").getJSONArray("friendscomcenter");
                JSONObject tempObj;

                // Iterate baby!
                for (int i = 0, max = profileObject.length(); i < max; i++) {

                    // Grab the object
                    tempObj = profileObject.optJSONObject(i);

                    // Only online friends?
                    if (noOffline
                            && !tempObj.getJSONObject("presence").getBoolean(
                                    "isOnline")) {
                        continue;
                    }

                    // Save it
                    profileArray.add(

                            new ProfileData(

                                    Long.parseLong(tempObj
                                            .getString("userId")), tempObj.getString("username"),
                                    tempObj.optString(
                                            "gravatarMd5", "")

                            )

                            );
                }

                return profileArray;

            } else {

                throw new WebsiteHandlerException(
                        "Could not retrieve the ProfileIDs.");

            }

        } catch (JSONException e) {

            throw new WebsiteHandlerException(e.getMessage());

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean answerFriendRequest(long pId, Boolean accepting,
            String checksum) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;
            String url = accepting ? Constants.URL_FRIEND_ACCEPT : Constants.URL_FRIEND_DECLINE;

            // Get the content
            httpContent = wh.post(

                    RequestHandler.generateUrl(url, pId),
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            return !"".equals(httpContent);

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }
    
    public static boolean sendFriendRequest(long profileId, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(
                    
                    RequestHandler.generateUrl(URL_FRIEND_REQUESTS, profileId),
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_AJAX

                    );

            // Did we manage?
            return (httpContent != null);
            
        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean removeFriend(long profileId)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.get(

                    RequestHandler.generateUrl(URL_FRIEND_DELETE, profileId),
                    RequestHandler.HEADER_AJAX

            );

            // Did we manage?
            if (httpContent != null) {

                return true;

            } else {

                return false;

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }
    
    /* TODO */
    public static boolean setActive() throws WebsiteHandlerException {

        try {

            // Let's see
            String httpContent = new RequestHandler().get(
                    URL_SETACTIVE, RequestHandler.HEADER_AJAX);
            JSONObject httpResponse = new JSONObject(httpContent);

            // Is it ok?
            if (httpResponse.optString("message", "FAIL").equals("OK")) {

                return true;

            } else {

                return false;

            }

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }
}
