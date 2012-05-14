package com.ninetwozero.battlelog.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.ChatMessage;
import com.ninetwozero.battlelog.datatypes.FriendListDataWrapper;
import com.ninetwozero.battlelog.datatypes.PersonaData;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileComparator;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class COMHandler {
    
    public static Long getChatId(long profileId, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's do this!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.post(

                    Constants.URL_CHAT_CONTENTS.replace(

                            "{UID}", profileId + ""), new PostData[] {

                        new PostData(

                                Constants.FIELD_NAMES_CHECKSUM[0], checksum

                        )

                    }, 0

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

                    Constants.URL_CHAT_CONTENTS.replace(

                            "{UID}", profileId + ""), new PostData[] {

                        new PostData(

                                Constants.FIELD_NAMES_CHECKSUM[0], checksum

                        )

                    }, 0

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

                    Constants.URL_CHAT_SEND, new PostData[] {
                            new PostData(

                                    Constants.FIELD_NAMES_CHAT[2], checksum

                            ), new PostData(

                                    Constants.FIELD_NAMES_CHAT[1], chatId

                            ), new PostData(

                                    Constants.FIELD_NAMES_CHAT[0], message

                            )

                    }, 1

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

                    Constants.URL_CHAT_CLOSE.replace(

                            "{CID}", chatId + ""

                            ), 1

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

                    Constants.URL_FRIENDS, new PostData[] {

                        new PostData(

                                Constants.FIELD_NAMES_CHECKSUM[0], checksum

                        )

                    }, 0

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
                        Log.d(Constants.DEBUG_TAG, "friendRequest => " + tempObj.toString(2));
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
                        if (presenceObj.getBoolean("isOnline")) {

                            if (presenceObj.getBoolean("isPlaying")) {

                                profileRowPlaying.add(

                                        new ProfileData(
                                                Long.parseLong(tempObj.getString("userId")),
                                                tempObj.getString("username"),
                                                new PersonaData[] {},
                                                tempObj.optString("gravatarMd5", ""),
                                                true,
                                                true

                                        )

                                        );

                            } else {

                                profileRowOnline.add(

                                        new ProfileData(
                                                Long.parseLong(tempObj.getString("userId")),
                                                tempObj.getString("username"),
                                                new PersonaData[] {},
                                                tempObj.optString("gravatarMd5", ""),
                                                true,
                                                false

                                        )

                                        );

                            }

                        } else {

                            profileRowOffline.add(

                                    new ProfileData(
                                            Long.parseLong(tempObj.getString("userId")),
                                            tempObj.getString("username"),
                                            new PersonaData[] {},
                                            tempObj.optString("gravatarMd5", "")

                                    )

                                    );

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
            httpContent = wh.post(Constants.URL_FRIENDS,
                    new PostData[] {
                        new PostData(
                                Constants.FIELD_NAMES_CHECKSUM[0], checksum)
                    }, 0);

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
                                    new PersonaData[] {}, tempObj.optString(
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

            // Get the content
            if (accepting) {

                httpContent = wh.post(

                        Constants.URL_FRIEND_ACCEPT.replace(

                                "{UID}", pId + ""), new PostData[] {

                            new PostData(

                                    Constants.FIELD_NAMES_CHECKSUM[0], checksum

                            )

                        }, 0

                        );

            } else {

                httpContent = wh.post(

                        Constants.URL_FRIEND_DECLINE.replace(

                                "{UID}", pId + ""), new PostData[] {

                            new PostData(

                                    Constants.FIELD_NAMES_CHECKSUM[0], checksum

                            )

                        }, 0

                        );

            }

            // Did we manage?
            if (!"".equals(httpContent)) {

                return true;

            } else {

                throw new WebsiteHandlerException(
                        "Could not respond to the friend request.");

            }

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }
}
