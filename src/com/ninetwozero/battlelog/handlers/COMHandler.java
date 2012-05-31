
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
import com.ninetwozero.battlelog.datatypes.ProfileComparator;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class COMHandler extends DefaultHandler {

    // Attributes
    private String checksum;

    // URLS
    public static final String URL_STATUS = Constants.URL_MAIN + "user/setStatusmessage/";
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

    public static final String[] FIELD_NAMES_STATUS = new String[] {
            "message",
            "post-check-sum",
            "urls[]"
    };

    public COMHandler(String c) {

        requestHandler = new RequestHandler();
        checksum = c;
    }

    public Long getChatId(long profileId)
            throws WebsiteHandlerException {

        try {

            // Let's do this!
            String httpContent = requestHandler.post(

                    RequestHandler.generateUrl(URL_CONTENTS, profileId),
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Get the messages
                return new JSONObject(httpContent).getJSONObject("data").getLong("chatId");

            } else {

                throw new WebsiteHandlerException("Could not get the chatId");

            }

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public List<ChatMessage> getMessages(long profileId) throws WebsiteHandlerException {

        try {

            // Let's do this!
            List<ChatMessage> messageArray = new ArrayList<ChatMessage>();
            String httpContent = requestHandler.post(

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

    public boolean sendMessage(long chatId, String message) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            String httpContent = requestHandler.post(

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
            return (!"".equals(httpContent));

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean closeChat(long chatId)
            throws WebsiteHandlerException {

        try {

            // Get the content
            new RequestHandler().get(
                    RequestHandler.generateUrl(URL_CLOSE, chatId),
                    RequestHandler.HEADER_AJAX
                    );

            // Did we manage?
            return true;

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public final FriendListDataWrapper getFriendsForCOM(final Context c)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            String httpContent = requestHandler.post(

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

                                tempProfileData = new ProfileData.Builder(

                                        Long.parseLong(tempObj.getString("userId")),
                                        tempObj.getString("username")

                                        ).gravatarHash(

                                                tempObj.optString("gravatarMd5", "")

                                                ).build()

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
                        ProfileData tempProfile = new ProfileData.Builder(Long.parseLong(tempObj
                                .getString("userId")), tempObj.getString("username"))
                                .gravatarHash(tempObj.optString("gravatarMd5", ""))
                                .isOnline(presenceObj.getBoolean("isOnline"))
                                .isPlaying(presenceObj.getBoolean("isPlaying")).build();

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

    public final List<ProfileData> getFriends(long profileId,
            boolean noOffline) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            List<ProfileData> profileArray = new ArrayList<ProfileData>();
            String httpContent = requestHandler.post(

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

                            new ProfileData.Builder(

                                    Long.parseLong(tempObj.getString("userId")), tempObj
                                            .getString("username")

                            ).gravatarHash(tempObj.optString("gravatarMd5", "")).build()

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

    public boolean answerFriendRequest(long profileId, Boolean accepting)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            String url = accepting ? Constants.URL_FRIEND_ACCEPT : Constants.URL_FRIEND_DECLINE;
            String httpContent = requestHandler.post(

                    RequestHandler.generateUrl(url, profileId),
                    RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, checksum),
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            return !"".equals(httpContent);

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public boolean sendFriendRequest(long profileId)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            String httpContent = requestHandler.post(

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

    public boolean removeFriend(long profileId)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            String httpContent = requestHandler.get(

                    RequestHandler.generateUrl(URL_FRIEND_DELETE, profileId),
                    RequestHandler.HEADER_AJAX

                    );

            // Did we manage?
            return (httpContent != null);

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public boolean updateStatus(String content) {

        try {

            String httpContent = requestHandler.post(

                    URL_STATUS,
                    RequestHandler.generatePostData(

                            FIELD_NAMES_STATUS,
                            content,
                            checksum

                            ),
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            if (httpContent != null && !httpContent.equals("")) {

                // Set the int
                int startPosition = httpContent
                        .indexOf(Constants.ELEMENT_STATUS_OK);

                // Did we find it?
                return (startPosition > -1);

            }

            return false;

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    /* TODO */
    public static boolean setActive() throws WebsiteHandlerException {

        try {

            // Let's see
            String httpContent = new RequestHandler()
                    .get(URL_SETACTIVE, RequestHandler.HEADER_AJAX);
            JSONObject httpResponse = new JSONObject(httpContent);

            // Is it ok?
            return (httpResponse.optString("message", "FAIL").equals("OK"));

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }
}
