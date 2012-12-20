package com.ninetwozero.bf3droid.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.ChatMessage;
import com.ninetwozero.bf3droid.datatype.ChatSession;
import com.ninetwozero.bf3droid.datatype.FriendListDataWrapper;
import com.ninetwozero.bf3droid.datatype.ProfileComparator;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.datatype.RequestHandlerException;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.misc.Constants;

public class COMClient extends DefaultClient {

	// Attributes
	private long mProfileId;
	private long mChatId;
	private String mChecksum;

	// URLS
	public static final String URL_STATUS = Constants.URL_MAIN
			+ "user/setStatusmessage/";
	public static final String URL_FRIENDS = Constants.URL_MAIN
			+ "comcenter/sync/";
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
	public static final String[] FIELD_NAMES_CHAT = new String[] { "message",
			"chatId", "post-check-sum" };

	public static final String[] FIELD_NAMES_STATUS = new String[] { "message",
			"post-check-sum", "urls[]" };

	public COMClient(long p, String c) {
		mRequestHandler = new RequestHandler();
		mProfileId = p;
		mChecksum = c;
	}

	public long getProfileId() {
		return mProfileId;
	}
	public long getChatId() {
		return mChatId;
	}

	public ChatSession getChat() throws WebsiteHandlerException {
		try {
			List<ChatMessage> messageArray = new ArrayList<ChatMessage>();
			String httpContent = mRequestHandler.post(
				RequestHandler.generateUrl(URL_CONTENTS, mProfileId),
				RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, mChecksum),
				RequestHandler.HEADER_NORMAL
			);
			
			if ("".equals(httpContent)) {
				throw new WebsiteHandlerException("Could not get the chat.");
			} else {
				JSONObject tempObject;
				JSONObject chatObject = new JSONObject(httpContent).getJSONObject("data").getJSONObject("chat");
				JSONArray messages = chatObject.getJSONArray("messages");

				mChatId = Long.parseLong(chatObject.getString("chatId"));
				
				for (int i = 0, max = messages.length(); i < max; i++) {
					tempObject = messages.optJSONObject(i);
					messageArray.add(
						new ChatMessage(
							tempObject.getLong("timestamp"), 
							tempObject.getString("fromUsername"), 
							tempObject.getString("message")
						)
					);
				}
				return new ChatSession(mChatId, messageArray);
			}
		} catch (Exception ex) {
			throw new WebsiteHandlerException(ex.getMessage());
		}
	}

	public boolean sendMessage(String message) throws WebsiteHandlerException {
		try {
			String httpContent = mRequestHandler.post(
				URL_SEND, 
				RequestHandler.generatePostData(FIELD_NAMES_CHAT, message, mChatId, mChecksum),
				RequestHandler.HEADER_AJAX
			);
			Log.d(Constants.DEBUG_TAG, "httpContent => " + httpContent);
			return (!"".equals(httpContent));
		} catch (RequestHandlerException ex) {
			throw new WebsiteHandlerException(ex.getMessage());
		}
	}

	public static boolean closeChat(long chatId) throws WebsiteHandlerException {
		try {
			new RequestHandler().get(
				RequestHandler.generateUrl(URL_CLOSE, chatId),
				RequestHandler.HEADER_AJAX);
			return true;
		} catch (RequestHandlerException ex) {
			throw new WebsiteHandlerException(ex.getMessage());
		}
	}

	public final FriendListDataWrapper getFriendsForCOM(final Context c) throws WebsiteHandlerException {
		try {
			String httpContent = mRequestHandler.post(
				URL_FRIENDS,
				RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, mChecksum),
				RequestHandler.HEADER_NORMAL
			);

			if ("".equals(httpContent)) {
				throw new WebsiteHandlerException("Could not retrieve the ProfileIDs.");
			} else {
				JSONObject comData = new JSONObject(httpContent).getJSONObject("data");
				JSONArray friendsObject = comData.getJSONArray("friendscomcenter");
				JSONArray requestsObject = comData.getJSONArray("friendrequests");
				JSONObject tempObj;
				JSONObject presenceObj;

				List<ProfileData> friends = new ArrayList<ProfileData>();
				List<ProfileData> profileRowRequests = new ArrayList<ProfileData>();
				List<ProfileData> profileRowPlaying = new ArrayList<ProfileData>();
				List<ProfileData> profileRowOnline = new ArrayList<ProfileData>();
				List<ProfileData> profileRowOffline = new ArrayList<ProfileData>();

				int numRequests = requestsObject.length();
				int numFriends = friendsObject.length();
				int numPlaying = 0;
				int numOnline = 0;
				int numOffline = 0;

				if (numRequests > 0) {
					ProfileData tempProfileData;
					for (int i = 0, max = requestsObject.length(); i < max; i++) {
						tempObj = requestsObject.optJSONObject(i);
						profileRowRequests.add(
							tempProfileData = new ProfileData.Builder(
								Long.parseLong(tempObj.getString("userId")), tempObj.getString("username")
							).gravatarHash(tempObj.optString("gravatarMd5", "")).build()
						);
						tempProfileData.setFriend(false);
					}

					Collections.sort(profileRowRequests, new ProfileComparator());
					friends.add(new ProfileData(c.getString(R.string.info_xml_friend_requests)));
					friends.addAll(profileRowRequests);
				}

				if (numFriends > 0) {
					for (int i = 0; i < numFriends; i++) {
						tempObj = friendsObject.optJSONObject(i);
						presenceObj = tempObj.getJSONObject("presence");

						ProfileData tempProfile = new ProfileData.Builder(
							Long.parseLong(tempObj.getString("userId")),
							tempObj.getString("username")
						).gravatarHash(tempObj.optString("gravatarMd5", ""))
						 .isOnline(presenceObj.getBoolean("isOnline"))
						 .isPlaying(presenceObj.getBoolean("isPlaying"))
						 .isFriend(true)
						 .build();

						if (tempProfile.isPlaying()) {
							profileRowPlaying.add(tempProfile);
						} else if (tempProfile.isOnline()) {
							profileRowOnline.add(tempProfile);
						} else {
							profileRowOffline.add(tempProfile);
						}
					}

					numPlaying = profileRowPlaying.size();
					numOnline = profileRowOnline.size();
					numOffline = profileRowOffline.size();

					if (numPlaying > 0) {
						Collections.sort(profileRowPlaying, new ProfileComparator());
						friends.add(new ProfileData(c.getString(R.string.info_txt_friends_playing)));
						friends.addAll(profileRowPlaying);
					}

					if (numOnline > 0) {
						Collections.sort(profileRowOnline, new ProfileComparator());
						friends.add(new ProfileData(c.getString(R.string.info_txt_friends_online)));
						friends.addAll(profileRowOnline);
					}

					if (numOffline > 0) {
						Collections.sort(profileRowOffline,	new ProfileComparator());
						friends.add(new ProfileData(c.getString(R.string.info_txt_friends_offline)));
						friends.addAll(profileRowOffline);
					}
				}
				return new FriendListDataWrapper(friends, numRequests, numPlaying, numOnline, numOffline);
			}
		} catch (JSONException e) {
			throw new WebsiteHandlerException(e.getMessage());
		} catch (RequestHandlerException ex) {
			throw new WebsiteHandlerException(ex.getMessage());
		}
	}

	public final List<ProfileData> getFriends(long profileId, boolean noOffline) throws WebsiteHandlerException {
		try {
			List<ProfileData> profileArray = new ArrayList<ProfileData>();
			String httpContent = mRequestHandler.post(
				URL_FRIENDS, 
				RequestHandler.generatePostData(
					Constants.FIELD_NAMES_CHECKSUM, 
					mChecksum
				),
				RequestHandler.HEADER_NORMAL
			);

			if ("".equals(httpContent)) {
				throw new WebsiteHandlerException("Could not retrieve the ProfileIDs.");
			} else {
				JSONArray profileObject = new JSONObject(httpContent).getJSONObject("data").getJSONArray("friendscomcenter");
				JSONObject tempObj;

				for (int i = 0, max = profileObject.length(); i < max; i++) {
					tempObj = profileObject.optJSONObject(i);

					if (noOffline && !tempObj.getJSONObject("presence").getBoolean("isOnline")) {
						continue;
					}

					profileArray.add(
						new ProfileData.Builder(
							Long.parseLong(tempObj.getString("userId")), 
							tempObj.getString("username")
						).gravatarHash(tempObj.optString("gravatarMd5", "")).build()
					);
				}
				return profileArray;
			}
		} catch (JSONException e) {
			throw new WebsiteHandlerException(e.getMessage());
		} catch (RequestHandlerException ex) {
			throw new WebsiteHandlerException(ex.getMessage());
		}
	}

	public boolean answerFriendRequest(long profileId, Boolean accepting) throws WebsiteHandlerException {
		try {
			String url = accepting ? Constants.URL_FRIEND_ACCEPT : Constants.URL_FRIEND_DECLINE;
			String httpContent = mRequestHandler.post(
				RequestHandler.generateUrl(url, profileId),
				RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, mChecksum),
				RequestHandler.HEADER_NORMAL
			);
			return !"".equals(httpContent);
		} catch (RequestHandlerException ex) {
			throw new WebsiteHandlerException(ex.getMessage());
		}
	}

	public boolean sendFriendRequest(long profileId) throws WebsiteHandlerException {
		try {
			String httpContent = mRequestHandler.post(
				RequestHandler.generateUrl(URL_FRIEND_REQUESTS, profileId),
				RequestHandler.generatePostData(Constants.FIELD_NAMES_CHECKSUM, mChecksum),
				RequestHandler.HEADER_AJAX
			);
			return (httpContent != null);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
		}
	}

	public boolean removeFriend(long profileId) throws WebsiteHandlerException {
		try {
			String httpContent = mRequestHandler.get(
				RequestHandler.generateUrl(URL_FRIEND_DELETE, profileId),
				RequestHandler.HEADER_AJAX
			);
			return (httpContent != null);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WebsiteHandlerException(ex.getMessage());
		}
	}

	public boolean updateStatus(String content) {
		try {
			String httpContent = mRequestHandler.post(
				URL_STATUS, 
				RequestHandler.generatePostData(
					FIELD_NAMES_STATUS, content, mChecksum, ""
				), 
				RequestHandler.HEADER_NORMAL
			);

			if (httpContent != null && !httpContent.equals("")) {
				int startPosition = httpContent.indexOf(Constants.ELEMENT_STATUS_OK);
				return (startPosition > -1);
			}
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/* TODO FIXME */
	public static boolean setActive() throws WebsiteHandlerException {
		try {
			String httpContent = new RequestHandler().get(URL_SETACTIVE, RequestHandler.HEADER_AJAX);
			JSONObject httpResponse = new JSONObject(httpContent);
			return (httpResponse.optString("message", "FAIL").equals("OK"));

		} catch (RequestHandlerException ex) {
			throw new WebsiteHandlerException(ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
