package com.ninetwozero.battlelog.factory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.FeedItem;
import com.ninetwozero.battlelog.datatype.ParsedFeedItemData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class FeedItemDataFactory {

	public static final String PLATOON = "platoon";
	public static final String NAME = "name";
	public static final String NAME_SID = "nameSID";
	public static final String THREAD_TITLE = "threadTitle";
	public static final String THREAD_BODY = "threadBody";
	public static final String POST_BODY = "postBody";
	public static final String EXPANSION = "expansion";
	public static final String BLOG_TITLE = "blogTitle";
	public static final String BLOG_COMMENT_BODY = "blogCommentBody";
	public static final String SERVER_NAME = "serverName";
	public static final String MAP = "map";
	public static final String GAME_MODE = "gameMode";
	public static final String GAME_REPORT_COMMENT = "gameReportComment";
	public static final String STAT_ITEMS = "statItems";
	public static final String PARENT_NAME_SID = "parentNameSID";
	public static final String FRIEND = "friend";
	public static final String USER_ID = "userId";
	public static final String USERNAME = "username";
	public static final String GRAVATAR_MD_5 = "gravatarMd5";
	public static final String LEVEL = "level";
	public static final String DIFFICULTY = "difficulty";
	public static final String RANK = "rank";
	public static final String WALL_BODY = "wallBody";
	public static final String WRITER_USER = "writerUser";
	public static final String FRIEND_USER = "friendUser";

	public static ParsedFeedItemData feedItemDataFrom(Context context,
			int feedItemTypeId, ProfileData profileData, JSONObject currItem) {

		ParsedFeedItemData feedItemData = null;
		try {
			switch (feedItemTypeId) {
			case FeedItem.TYPE_NEW_STATUS:
				feedItemData = generateFromNewStatus(
						currItem.getJSONObject("STATUSMESSAGE"), profileData);
				break;
			case FeedItem.TYPE_NEW_FRIEND:
				feedItemData = generateFromNewFriend(context,
						currItem.getJSONObject("BECAMEFRIENDS"), profileData);
				break;
			case FeedItem.TYPE_NEW_FORUM_THREAD:
				feedItemData = generateFromNewThread(context,
						currItem.getJSONObject("CREATEDFORUMTHREAD"),
						profileData);
				break;
			case FeedItem.TYPE_NEW_FORUM_POST:
				feedItemData = generateFromNewForumPost(context,
						currItem.getJSONObject("WROTEFORUMPOST"), profileData);
				break;
			case FeedItem.TYPE_GOT_WALL_POST:
				feedItemData = generateFromWallPost(context,
						currItem.getJSONObject("RECEIVEDWALLPOST"), profileData);
				break;
			case FeedItem.TYPE_GOT_PLATOON_POST:
				feedItemData = generateFromPlatoonPost(context,
						currItem.getJSONObject("RECEIVEDPLATOONWALLPOST"),
						profileData);
				break;
			case FeedItem.TYPE_NEW_FAVSERVER:
				feedItemData = generateFromFavoritingServer(context,
						currItem.getJSONObject("ADDEDFAVSERVER"), profileData);
				break;
			case FeedItem.TYPE_NEW_RANK:
				feedItemData = generateFromReachingNewRank(context,
						currItem.getJSONObject("RANKEDUP"), profileData);
				break;
			case FeedItem.TYPE_COMPLETED_LEVEL:
				feedItemData = generateFromCompletingLevel(context,
						currItem.getJSONObject("LEVELCOMPLETE"), profileData);
				break;
			case FeedItem.TYPE_NEW_PLATOON:
				feedItemData = generateFromCreatingNewPlatoon(context,
						currItem.getJSONObject("CREATEDPLATOON"), profileData);
				break;
			case FeedItem.TYPE_NEW_EMBLEM:
				feedItemData = generateFromNewPlatoonEmblem(context,
						currItem.getJSONObject("PLATOONBADGESAVED"),
						profileData);
				break;
			case FeedItem.TYPE_JOINED_PLATOON:
				feedItemData = generateFromJoiningPlatoon(context,
						currItem.getJSONObject("JOINEDPLATOON"), profileData);
				break;
			case FeedItem.TYPE_KICKED_PLATOON:
				feedItemData = generateFromGettingKickedFromPlatoon(context,
						currItem.getJSONObject("KICKEDPLATOON"), profileData);
				break;
			case FeedItem.TYPE_LEFT_PLATOON:
				feedItemData = generateFromLeavingPlatoon(context,
						currItem.getJSONObject("LEFTPLATOON"), profileData);
				break;
			case FeedItem.TYPE_COMPLETED_GAME:
				feedItemData = generateFromGameReport(context,
						currItem.getJSONObject("GAMEREPORT"), profileData);
				break;
			case FeedItem.TYPE_GOT_AWARD:
				feedItemData = generateFromAward(context,
						currItem.getJSONObject("RECEIVEDAWARD"), profileData);
				break;
			case FeedItem.TYPE_COMPLETED_ASSIGNMENT:
				feedItemData = generateFromCompletingAssignment(context,
						currItem.getJSONObject("COMPLETEDASSIGNMENT"),
						profileData);
				break;
			case FeedItem.TYPE_NEW_COMMENT_GAME:
				feedItemData = generateFromCommentOnGameReport(context,
						currItem.getJSONObject("COMMENTEDGAMEREPORT"),
						profileData);
				break;
			case FeedItem.TYPE_NEW_COMMENT_BLOG:
				feedItemData = generateFromCommentOnBlog(context,
						currItem.getJSONObject("COMMENTEDBLOG"), profileData);
				break;
			case FeedItem.TYPE_NEW_EXPANSION:
				feedItemData = generateFromUnlockedExpansion(context,
						currItem.getJSONObject("GAMEACCESS"), profileData);
				break;

			case FeedItem.TYPE_NEW_SHARED_GAMEEVENT:
				feedItemData = generateFromSharedGameEvent(context,
						currItem.getJSONObject("SHAREDGAMEEVENT"), profileData);
				break;

			default:
				throw new WebsiteHandlerException("Unknown event: "
						+ currItem.getString("event"));
			}

		} catch (WebsiteHandlerException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return feedItemData;
	}

	/*
	 * FIXME: Needs to be able to handle three types; awards, br's and
	 * assignments
	 */
	private static ParsedFeedItemData generateFromSharedGameEvent(
			Context context, JSONObject jsonObject, ProfileData profile)
			throws JSONException {

		// Init
		String generatedTitle;
		String shareType = jsonObject.getString("eventName");
		JSONObject eventData = jsonObject.getJSONObject(shareType);
		JSONArray stats = eventData.optJSONArray(STAT_ITEMS);
		StringBuilder title = new StringBuilder();

		/* TODO: EXPORT TO SMALLER METHODS */
		if (stats == null) {
			generatedTitle = PublicUtils.createStringWithData(context,
					R.string.info_p_shared_rank, profile.getUsername(),
					DataBank.getRankTitle(eventData.getString("nameSID")),
					eventData.getString("rank"));

		} else {

			for (int counter = 0, maxCounter = stats.length(); counter < maxCounter; counter++) {

				// Let's get the item
				JSONObject tempSubItem = stats.optJSONObject(counter);

				// Append
				if (title.length() == 0) {
					title.append("<b>");
				}

				// Do we need to append anything?
				if (counter > 0) {
					if (counter == (maxCounter - 1)) {
						title.append(" </b>and<b> ");

					} else {
						title.append(", ");

					}

				}

				// Let's see
				String key = tempSubItem.getString(NAME_SID);
				if (shareType.equals("BF3AWARDS")) {
					title.append(DataBank.getAwardTitle(key));
				} else if (shareType.equals("BF3GAMEREPORT")) {
					title.append(getTitleFromUnlock(context, tempSubItem));
				} else {
					title.append(DataBank.getAttachmentTitle(key));
				}

			}

			// Set the things straight
			int stringResource = R.string.info_p_shared_awards;
			boolean isSingleUnlock = (stats.length() == 1);
			if (isSingleUnlock) {
				if (shareType.equals("BF3AWARDS")) {
					stringResource = R.string.info_p_shared_award;
				} else if (shareType.equals("BF3GAMEREPORT")) {
					stringResource = R.string.info_p_shared_unlock;
				} else {
					stringResource = R.string.info_p_shared_assignment;
				}
			} else {
				if (shareType.equals("BF3AWARDS")) {
					stringResource = R.string.info_p_shared_awards;
				} else if (shareType.equals("BF3GAMEREPORT")) {
					stringResource = R.string.info_p_shared_unlocks;
				} else {
					stringResource = R.string.info_p_shared_assignments;
				}
			}

			generatedTitle = PublicUtils
					.createStringWithData(context, stringResource,
							profile.getUsername(), title.append("</b>"));
		}

		return new ParsedFeedItemData(generatedTitle, "", new ProfileData[] {
				profile, null });
	}

	private static ParsedFeedItemData generateFromNewForumPost(Context context,
			JSONObject currItem, ProfileData profile) throws JSONException {

		return new ParsedFeedItemData(PublicUtils.createStringWithData(context,
				R.string.info_p_forumthread, profile.getUsername(),
				currItem.getString(THREAD_TITLE)),
				currItem.getString(POST_BODY), new ProfileData[] {

				profile, null

				});
	}

	private static ParsedFeedItemData generateFromUnlockedExpansion(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {

		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context, R.string.info_p_gameaccess,
				profile.getUsername(),
				DataBank.getExpansionTitle(currItem.getString(EXPANSION))), "",
				new ProfileData[] {

				profile, null

				});
	}

	private static ParsedFeedItemData generateFromCommentOnBlog(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {
		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context, R.string.info_p_blog_comment,
				profile.getUsername(), currItem.getString(BLOG_TITLE)),
				currItem.getString(BLOG_COMMENT_BODY), new ProfileData[] {

				profile, null

				});

	}

	private static ParsedFeedItemData generateFromCommentOnGameReport(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {
		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context,
				R.string.info_p_greport_comment, profile.getUsername(),
				currItem.getString(SERVER_NAME),
				DataBank.getMapTitle(currItem.getString(MAP)),
				DataBank.getGameModeFromId(currItem.getInt(GAME_MODE))),
				currItem.getString(GAME_REPORT_COMMENT), new ProfileData[] {

				profile, null

				});

	}

	private static ParsedFeedItemData generateFromCompletingAssignment(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {

		JSONObject statsItem = currItem.getJSONArray(STAT_ITEMS).getJSONObject(
				0);
		Object[] tempInfo = DataBank.getAssignmentTitle(statsItem
				.getString(NAME_SID));

		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context,
				R.string.info_txt_assignment_ok, profile.getUsername(),
				tempInfo), "", new ProfileData[] {

		profile, null

		});

	}

	private static ParsedFeedItemData generateFromAward(Context context,
			JSONObject currItem, ProfileData profile) throws JSONException {
		JSONArray stats = currItem.optJSONArray(STAT_ITEMS);
		StringBuilder title = new StringBuilder();

		for (int counter = 0, maxCounter = stats.length(); counter < maxCounter; counter++) {

			// Let's get the item
			JSONObject subItem = stats.optJSONObject(counter);
			String tempKey = subItem.getString(NAME_SID);
			if (title.length() == 0) {
				title.append("<b>");
			}

			// Do we need to append anything?
			if (counter > 0) {

				if (counter == (maxCounter - 1)) {

					title.append("</b> and <b>");

				} else {

					title.append(", ");

				}

			}

			// Weapon? Attachment?
			title.append(DataBank.getAwardTitle(tempKey));

		}

		// Set the title
		String generatedTitle;
		if (stats.length() > 1) {

			generatedTitle = PublicUtils.createStringWithData(context,
					R.string.info_p_awards, profile.getUsername(),
					title.append("</b>"));

		} else {

			generatedTitle = PublicUtils.createStringWithData(context,
					R.string.info_p_award, profile.getUsername(),
					title.append("</b>"));

		}
		return new ParsedFeedItemData(

		generatedTitle, "", new ProfileData[] {

		profile, null

		});

	}

	private static ParsedFeedItemData generateFromGameReport(Context context,
			JSONObject currItem, ProfileData profile) throws JSONException {

		// Init
		JSONArray stats = currItem.optJSONArray(STAT_ITEMS);
		StringBuilder title = new StringBuilder();

		/* TODO: EXPORT TO SMALLER METHODS */
		for (int counter = 0, maxCounter = stats.length(); counter < maxCounter; counter++) {

			// Let's get the item
			String tempKey;
			JSONObject tempSubItem = stats.optJSONObject(counter);

			// Append
			if (title.length() == 0) {
				title.append("<b>");
			}

			// Do we need to append anything?
			if (counter > 0) {

				if (counter == (maxCounter - 1)) {

					title.append(" </b>and<b> ");

				} else {

					title.append(", ");

				}

			}

			title.append(getTitleFromUnlock(context, tempSubItem));

		}

		// Set the things straight
		String generatedTitle;
		if (stats.length() > 1) {

			generatedTitle = PublicUtils.createStringWithData(context,
					R.string.info_p_newunlocks, profile.getUsername(),
					title.append("</b>"));

		} else {

			generatedTitle = PublicUtils.createStringWithData(context,
					R.string.info_p_newunlock, profile.getUsername(),
					title.append("</b>"));

		}
		return new ParsedFeedItemData(

		generatedTitle, "", new ProfileData[] {

		profile, null

		});

	}

	private static String getTitleFromUnlock(Context context, JSONObject unlock)
			throws JSONException {

		StringBuffer title = new StringBuffer();

		// Weapon? Attachment?
		if (!unlock.isNull(PARENT_NAME_SID)) {

			// Let's see
			String parentKey = unlock.getString(PARENT_NAME_SID);
			String tempKey = DataBank.getWeaponTitle(context, parentKey);

			// Is it empty?
			if (!parentKey.equals(tempKey)) {

				title.append(tempKey)
						.append(" ")
						.append(DataBank.getAttachmentTitle(unlock
								.getString(NAME_SID)));

			} else {

				// Grab a vehicle title then
				tempKey = DataBank.getVehicleTitle(parentKey);

				// Validate
				if (!parentKey.equals(tempKey)) {

					title.append(tempKey)
							.append(" ")
							.append(DataBank.getVehicleUpgradeTitle(unlock
									.getString(NAME_SID)));

				} else {

					title.append(tempKey);

				}

			}

		} else {

			// Let's see
			String key = unlock.getString(NAME_SID);
			String guid = unlock.getString("guid");

			if (key.startsWith("ID_P_ANAME_")) {

				title.append(DataBank.getAttachmentTitle(key));

			} else if (key.startsWith("ID_P_WNAME_")) {

				title.append(DataBank.getWeaponTitle(context, guid));

			} else if (key.startsWith("ID_P_VUNAME_")) {

				title.append(DataBank.getVehicleUpgradeTitle(key));

			} else if (key.startsWith("ID_P_SNAME")) {

				title.append(DataBank.getSkillTitle(key));

			} else {

				title.append(DataBank.getKitTitle(key));

			}

		}
		return title.toString();
	}

	private static ParsedFeedItemData generateFromLeavingPlatoon(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {
		JSONObject platoonObject = currItem.getJSONObject(PLATOON);
		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context, R.string.info_p_platoon_left,
				profile.getUsername(), platoonObject.getString(NAME)), "",
				new ProfileData[] {

				profile, null

				});

	}

	private static ParsedFeedItemData generateFromGettingKickedFromPlatoon(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {

		JSONObject platoonObject = currItem.getJSONObject(PLATOON);
		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context, R.string.info_p_platoon_kick,
				profile.getUsername(), platoonObject.getString(NAME)), "",
				new ProfileData[] {

				profile, null

				});

	}

	private static ParsedFeedItemData generateFromJoiningPlatoon(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {
		JSONObject platoonObject = currItem.getJSONObject(PLATOON);
		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context, R.string.info_p_platoon_join,
				profile.getUsername(), platoonObject.getString(NAME)), "",
				new ProfileData[] {

				profile, null

				});

	}

	private static ParsedFeedItemData generateFromNewPlatoonEmblem(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {
		JSONObject platoonObject = currItem.getJSONObject(PLATOON);
		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context,
				R.string.info_p_platoon_badge, profile.getUsername(),
				platoonObject.getString(NAME)), "", new ProfileData[] {

		profile, null

		});

	}

	private static ParsedFeedItemData generateFromCreatingNewPlatoon(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {

		JSONObject platoonObject = currItem.getJSONObject(PLATOON);
		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context,
				R.string.info_p_platoon_create, profile.getUsername(),
				platoonObject.getString(NAME)), "", new ProfileData[] {

		profile, null

		});

	}

	private static ParsedFeedItemData generateFromCompletingLevel(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {
		JSONObject friendObject = currItem.getJSONObject(FRIEND);
		ProfileData profile2 = new ProfileData.Builder(
				Long.parseLong(friendObject.getString(USER_ID)),
				friendObject.getString(USERNAME)).gravatarHash(
				friendObject.getString(GRAVATAR_MD_5)).build();

		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context,
				R.string.info_p_coop_level_comp, profile.getUsername(),
				profile2.getUsername(),
				DataBank.getCoopLevelTitle(currItem.getString(LEVEL)),
				currItem.getString(DIFFICULTY)), "", new ProfileData[] {

		profile, profile2

		});

	}

	private static ParsedFeedItemData generateFromReachingNewRank(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {
		return new ParsedFeedItemData(PublicUtils.createStringWithData(context,
				R.string.info_p_promotion, profile.getUsername(),
				DataBank.getRankTitle(currItem.getString(NAME_SID)),
				currItem.getString(RANK)), "", new ProfileData[] {

		profile, null

		});

	}

	private static ParsedFeedItemData generateFromFavoritingServer(
			Context context, JSONObject currItem, ProfileData profile)
			throws JSONException {
		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context, R.string.info_p_favserver,
				profile.getUsername(), currItem.getString(SERVER_NAME)), "",
				new ProfileData[] {

				profile, null

				});

	}

	private static ParsedFeedItemData generateFromPlatoonPost(Context context,
			JSONObject currItem, ProfileData profile) throws JSONException {
		JSONObject platoonObject = currItem.getJSONObject(PLATOON);
		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context, R.string.info_p_platoon_feed,
				profile.getUsername(), platoonObject.getString(NAME)),
				currItem.getString(WALL_BODY), new ProfileData[] {

				profile, null

				});

	}

	private static ParsedFeedItemData generateFromWallPost(Context context,
			JSONObject currItem, ProfileData profile2) throws JSONException {

		// Let's get it!
		JSONObject otherUserObject = currItem.getJSONObject(WRITER_USER);

		// Set the other profile
		ProfileData profile1 = new ProfileData.Builder(
				Long.parseLong(otherUserObject.getString(USER_ID)),
				otherUserObject.getString(USERNAME)).gravatarHash(
				otherUserObject.getString(GRAVATAR_MD_5)).build();

		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context, R.string.info_p_got_wallpost,
				profile1.getUsername(), profile2.getUsername(),
				currItem.getString(WALL_BODY)), "", new ProfileData[] {

		profile1, profile2 });

	}

	private static ParsedFeedItemData generateFromNewThread(Context context,
			JSONObject currItem, ProfileData profile) throws JSONException {

		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context, R.string.info_p_forumthread,
				profile.getUsername(), currItem.getString(THREAD_TITLE)),
				currItem.getString(THREAD_BODY), new ProfileData[] {

				profile, null

				});

	}

	private static ParsedFeedItemData generateFromNewFriend(Context context,
			JSONObject currItem, ProfileData profile) throws JSONException {

		JSONObject friendUser = currItem.getJSONObject(FRIEND_USER);

		// Set the first profile
		ProfileData profile2 = new ProfileData.Builder(
				Long.parseLong(friendUser.getString(USER_ID)),
				friendUser.getString(USERNAME)).gravatarHash(
				friendUser.getString(GRAVATAR_MD_5)).build();

		// Get the title
		return new ParsedFeedItemData(

		PublicUtils.createStringWithData(context, R.string.info_p_friendship,
				profile.getUsername(), profile2.getUsername()), "",
				new ProfileData[] {

				profile, profile2

				});

	}

	private static ParsedFeedItemData generateFromNewStatus(
			JSONObject currItem, ProfileData profile) throws JSONException {
		return new ParsedFeedItemData(

		"<b>" + profile.getUsername() + "</b> "
				+ currItem.getString("statusMessage"), "", new ProfileData[] {

		profile, null

		});

	}

}
