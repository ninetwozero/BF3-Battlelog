/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.battlelog.misc;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.AssignmentData;
import com.ninetwozero.battlelog.datatypes.ChatMessage;
import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.ForumData;
import com.ninetwozero.battlelog.datatypes.ForumPostData;
import com.ninetwozero.battlelog.datatypes.ForumSearchResult;
import com.ninetwozero.battlelog.datatypes.ForumThreadData;
import com.ninetwozero.battlelog.datatypes.FriendListDataWrapper;
import com.ninetwozero.battlelog.datatypes.GeneralSearchResult;
import com.ninetwozero.battlelog.datatypes.NewsData;
import com.ninetwozero.battlelog.datatypes.NotificationData;
import com.ninetwozero.battlelog.datatypes.PersonaData;
import com.ninetwozero.battlelog.datatypes.PersonaStats;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.PlatoonInformation;
import com.ninetwozero.battlelog.datatypes.PlatoonMemberData;
import com.ninetwozero.battlelog.datatypes.PlatoonStats;
import com.ninetwozero.battlelog.datatypes.PlatoonStatsItem;
import com.ninetwozero.battlelog.datatypes.PlatoonTopStatsItem;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileComparator;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ProfileInformation;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.TopStatsComparator;
import com.ninetwozero.battlelog.datatypes.UnlockComparator;
import com.ninetwozero.battlelog.datatypes.UnlockData;
import com.ninetwozero.battlelog.datatypes.UnlockDataWrapper;
import com.ninetwozero.battlelog.datatypes.WeaponStats;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;

/* 
 * Methods of this class should be loaded in AsyncTasks, as they would probably lock up the GUI
 */

public class WebsiteHandler {

    public static ProfileData getProfileIdFromSearch(final String keyword,
            final String checksum) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;
            ProfileData profile = null;

            // Get the content
            httpContent = wh.post(

                    Constants.URL_PROFILE_SEARCH, new PostData[] {

                            new PostData(Constants.FIELD_NAMES_PROFILE_SEARCH[0],
                                    keyword),
                            new PostData(Constants.FIELD_NAMES_PROFILE_SEARCH[1],
                                    checksum)

                    }, 0

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Generate an object
                JSONArray searchResults = new JSONObject(httpContent)
                        .getJSONObject("data").getJSONArray("matches");

                // Did we get any results?
                if (searchResults.length() > 0) {

                    // init cost counters
                    int costOld = 999, costCurrent = 0;

                    // Iterate baby!
                    for (int i = 0, max = searchResults.length(); i < max; i++) {

                        // Get the JSONObject
                        JSONObject tempObj = searchResults.optJSONObject(i);

                        // A perfect match?
                        if (tempObj.getString("username").equals(keyword)) {

                            profile = new ProfileData(
                                    Long.parseLong(tempObj.getString("userId")),
                                    tempObj.getString("username"),
                                    new PersonaData[] {},
                                    tempObj.optString("gravatarMd5", "")

                                    );

                            break;

                        }

                        // Grab the "cost"
                        costCurrent = PublicUtils.getLevenshteinDistance(
                                keyword, tempObj.getString("username"));

                        // Somewhat of a match? Get the "best" one!
                        if (costOld > costCurrent) {

                            profile = new ProfileData(
                                    Long.parseLong(tempObj.getString("userId")),
                                    tempObj.getString("username"),
                                    new PersonaData[] {},
                                    tempObj.optString("gravatarMd5", "")

                                    );
                        }

                        // Shuffle!
                        costOld = costCurrent;

                    }

                    return WebsiteHandler.getPersonaIdFromProfile(profile);

                }

                return null;

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

    public static int applyForPlatoonMembership(final long platoonId,
            final String checksum) throws WebsiteHandlerException {

        try {

            // Let's set it up!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Do the actual request
            httpContent = wh.post(

                    Constants.URL_PLATOON_APPLY, new PostData[] {

                            new PostData(Constants.FIELD_NAMES_PLATOON_APPLY[0],
                                    platoonId + ""),
                            new PostData(Constants.FIELD_NAMES_PLATOON_APPLY[1],
                                    checksum)

                    }, 3

                    );

            // What up?
            if (httpContent == null || httpContent.equals("")) {

                return -1; // Invalid request

            } else {

                if (httpContent.equals("success")) { // OK!

                    return 0;

                } else if (httpContent.equals("wrongplatform")) { // Wrong
                    // platform

                    return 1;

                } else if (httpContent.equals("maxmembersreached")) { // Full
                    // platoon

                    return 2;

                } else { // unknown

                    return 3;

                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean closePlatoonMembership(final long platoonId,
            final long userId, final String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's set it up!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Do the actual request
            httpContent = wh.post(

                    Constants.URL_PLATOON_LEAVE, new PostData[] {

                            new PostData(Constants.FIELD_NAMES_PLATOON_LEAVE[0],
                                    platoonId + ""),
                            new PostData(Constants.FIELD_NAMES_PLATOON_LEAVE[1], userId
                                    + ""),
                            new PostData(Constants.FIELD_NAMES_PLATOON_LEAVE[2],
                                    checksum)

                    }, 3

                    );

            // What up?
            if (httpContent == null || httpContent.equals("")) {

                return false;

            } else {

                return true;

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static PlatoonData getPlatoonIdFromSearch(final String keyword,
            final String checksum) throws WebsiteHandlerException {

        try {

            // Let's do this!
            RequestHandler wh = new RequestHandler();
            String httpContent;
            PlatoonData platoon = null;

            // Get the content
            httpContent = wh.post(

                    Constants.URL_PLATOON_SEARCH, new PostData[] {

                            new PostData(Constants.FIELD_NAMES_PLATOON_SEARCH[0],
                                    keyword),
                            new PostData(Constants.FIELD_NAMES_PLATOON_SEARCH[1],
                                    checksum)

                    }, 3

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Generate an object
                JSONArray searchResults = new JSONArray(httpContent);

                // Did we get any results?
                if (searchResults.length() > 0) {

                    // init cost counters
                    int costOld = 999, costCurrent = 0;

                    // Iterate baby!
                    for (int i = 0, max = searchResults.length(); i < max; i++) {

                        // Get the JSONObject
                        JSONObject tempObj = searchResults.optJSONObject(i);

                        // Is it visible?
                        if (tempObj.getBoolean("hidden")) {
                            continue;
                        }

                        // A perfect match?
                        if (tempObj.getString("name").equals(keyword)) {

                            platoon = new PlatoonData(Long.parseLong(tempObj
                                    .getString("id")),
                                    tempObj.getInt("fanCounter"),
                                    tempObj.getInt("memberCounter"),
                                    tempObj.getInt("platform"),
                                    tempObj.getString("name"),
                                    tempObj.getString("tag"), null, true);

                            break;

                        }

                        // Grab the "cost"
                        costCurrent = PublicUtils.getLevenshteinDistance(
                                keyword, tempObj.getString("name"));

                        // Somewhat of a match? Get the "best" one!
                        if (costOld > costCurrent) {

                            platoon = new PlatoonData(

                                    Long.parseLong(tempObj.getString("id")),
                                    tempObj.getInt("fanCounter"),
                                    tempObj.getInt("memberCounter"),
                                    tempObj.getInt("platform"),
                                    tempObj.getString("name"),
                                    tempObj.getString("tag"), null, true

                                    );

                        }

                        // Shuffle!
                        costOld = costCurrent;

                    }

                    return platoon;

                }

                throw new WebsiteHandlerException("No platoons found.");

            } else {

                throw new WebsiteHandlerException(
                        "Could not retreive the ProfileIDs.");

            }

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static ProfileData getProfileIdFromPersona(final long personaId)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.get(
                    Constants.URL_STATS_OVERVIEW.replace("{PID}",
                            personaId + "").replace("{PLATFORM_ID}", "0"), 0);

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Grab the array
                JSONObject user = new JSONObject(httpContent).getJSONObject(
                        "data").getJSONObject("user");

                return new ProfileData(
                        Long.parseLong(user.getString("userId")),
                        user.getString("username"),
                        new PersonaData[] {},
                        user.optString("gravatarMd5", "")

                );

            } else {

                throw new WebsiteHandlerException(
                        "Could not retrieve the Profile.");

            }

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static ProfileData getPersonaIdFromProfile(final long profileId)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.get(
                    Constants.URL_PROFILE.replace("{UID}", profileId + ""), 0);

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Grab the array
                JSONArray soldierBox = new JSONObject(httpContent)
                        .getJSONObject("data").getJSONArray("soldiersBox");

                // Get the number of soldiers
                final int numPersonas = soldierBox.length();

                // Init arrays
                PersonaData[] personaArray = new PersonaData[numPersonas];

                // Loop
                for (int i = 0; i < numPersonas; i++) {

                    // Current soldier
                    JSONObject personaObject = soldierBox.optJSONObject(i)
                            .getJSONObject("persona");

                    // Grab the variable data
                    personaArray[i] = new PersonaData(

                            personaObject.getLong("personaId"),
                            personaObject.getString("personaName"),
                            DataBank.getPlatformIdFromName(personaObject.getString("namespace")),
                            personaObject.getString("picture")
                            );
                }

                return new ProfileData(

                        profileId, personaArray[0].getName(), personaArray, null

                );

            } else {

                throw new WebsiteHandlerException(
                        "Could not retrieve the PersonaID.");

            }

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static ProfileData getPersonaIdFromProfile(final ProfileData p)
            throws WebsiteHandlerException {

        try {

            ProfileData profile = WebsiteHandler.getPersonaIdFromProfile(p
                    .getId());
            return new ProfileData(

                    p.getId(), p.getUsername(), profile.getPersonaArray(),
                    profile.getGravatarHash()

            );

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException("No profile found");

        }
    }

    public static PersonaStats getStatsForPersona(ProfileData pd)
            throws WebsiteHandlerException {

        try {

            // Do we have a personaId?
            if (pd.getNumPersonas() == 0) {

                pd = getPersonaIdFromProfile(pd.getId());

            }

            // Let's see...
            RequestHandler wh = new RequestHandler();

            // Get the data
            String content = wh.get(

                    Constants.URL_STATS_OVERVIEW.replace(

                            "{PID}", pd.getPersona(0).getId() + ""

                            ).replace(

                                    "{PLATFORM_ID}", pd.getPersona(0).getPlatformId() + ""), 0

                    );

            // JSON Objects
            JSONObject dataObject = new JSONObject(content)
                    .getJSONObject("data");

            // Is overviewStats NULL? If so, no data.
            if (dataObject.isNull("overviewStats")) {
                return null;
            }

            // Keep it up!
            JSONObject statsOverview = dataObject
                    .getJSONObject("overviewStats");
            JSONObject kitScores = statsOverview.getJSONObject("kitScores");
            JSONObject nextRankInfo = dataObject.getJSONObject("rankNeeded");
            JSONObject currRankInfo = dataObject
                    .getJSONObject("currentRankNeeded");

            // Yay
            return new PersonaStats(

                    pd.getUsername(), pd.getPersona(0).getName(),
                    currRankInfo.getString("name"),
                    statsOverview.getLong("rank"), pd.getPersona(0).getId(),
                    pd.getId(), pd.getPersona(0).getPlatformId(),
                    statsOverview.getLong("timePlayed"),
                    currRankInfo.getLong("pointsNeeded"),
                    nextRankInfo.getLong("pointsNeeded"),
                    statsOverview.getInt("kills"),
                    statsOverview.getInt("killAssists"),
                    statsOverview.getInt("vehiclesDestroyed"),
                    statsOverview.getInt("vehiclesDestroyedAssists"),
                    statsOverview.getInt("heals"),
                    statsOverview.getInt("revives"),
                    statsOverview.getInt("repairs"),
                    statsOverview.getInt("resupplies"),
                    statsOverview.getInt("deaths"),
                    statsOverview.getInt("numWins"),
                    statsOverview.getInt("numLosses"),
                    statsOverview.getDouble("kdRatio"),
                    statsOverview.getDouble("accuracy"),
                    statsOverview.getDouble("longestHeadshot"),
                    statsOverview.getDouble("killStreakBonus"),
                    statsOverview.getDouble("elo"),
                    statsOverview.getDouble("scorePerMinute"),
                    kitScores.getLong("1"), kitScores.getLong("2"),
                    kitScores.getLong("32"), kitScores.getLong("8"),
                    statsOverview.getLong("sc_vehicle"),
                    statsOverview.getLong("combatScore"),
                    statsOverview.getLong("sc_award"),
                    statsOverview.getLong("sc_unlock"),
                    statsOverview.getLong("totalScore")

            );

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static HashMap<Long, PersonaStats> getStatsForUser(
            final Context context, final ProfileData pd)
            throws WebsiteHandlerException {

        try {

            // Init
            HashMap<Long, PersonaStats> stats = new HashMap<Long, PersonaStats>();
            ProfileData profileData = pd;

            // Do we have a personaId?
            if (profileData.getNumPersonas() == 0) {

                profileData = getPersonaIdFromProfile(pd.getId());

            }

            // Let's see...
            RequestHandler wh = new RequestHandler();
            for (int i = 0, max = profileData.getNumPersonas(); i < max; i++) {

                // Get the data
                String httpContent = wh.get(

                        Constants.URL_STATS_OVERVIEW.replace(

                                "{PID}", profileData.getPersona(i).getId() + ""

                                ).replace(

                                        "{PLATFORM_ID}",
                                        profileData.getPersona(i).getPlatformId() + ""), 0

                        );

                // JSON Objects
                JSONObject dataObject = new JSONObject(httpContent)
                        .getJSONObject("data");

                // Is overviewStats NULL? If so, no data.
                if (!dataObject.isNull("overviewStats")) {

                    // Keep it up!
                    JSONObject statsOverview = dataObject
                            .getJSONObject("overviewStats");
                    JSONObject kitScores = statsOverview
                            .getJSONObject("kitScores");
                    JSONObject nextRankInfo = dataObject
                            .getJSONObject("rankNeeded");
                    JSONObject currRankInfo = dataObject
                            .getJSONObject("currentRankNeeded");

                    // Yay
                    stats.put(

                            profileData.getPersona(i).getId(),
                            new PersonaStats(

                                    profileData.getUsername(), profileData
                                            .getPersona(i).getName(), currRankInfo
                                            .getString("name"), statsOverview
                                            .getLong("rank"), profileData
                                            .getPersona(i).getId(), profileData
                                            .getId(), profileData
                                            .getPersona(i).getPlatformId(), statsOverview
                                            .getLong("timePlayed"), currRankInfo
                                            .getLong("pointsNeeded"), nextRankInfo
                                            .getLong("pointsNeeded"), statsOverview
                                            .getInt("kills"), statsOverview
                                            .getInt("killAssists"), statsOverview
                                            .getInt("vehiclesDestroyed"), statsOverview
                                            .getInt("vehiclesDestroyedAssists"),
                                    statsOverview.getInt("heals"),
                                    statsOverview.getInt("revives"),
                                    statsOverview.getInt("repairs"),
                                    statsOverview.getInt("resupplies"),
                                    statsOverview.getInt("deaths"),
                                    statsOverview.getInt("numWins"),
                                    statsOverview.getInt("numLosses"),
                                    statsOverview.getDouble("kdRatio"),
                                    statsOverview.getDouble("accuracy"),
                                    statsOverview.getDouble("longestHeadshot"),
                                    statsOverview.getDouble("killStreakBonus"),
                                    statsOverview.getDouble("elo"),
                                    statsOverview.getDouble("scorePerMinute"),
                                    kitScores.getLong("1"), kitScores
                                            .getLong("2"), kitScores
                                            .getLong("32"), kitScores
                                            .getLong("8"), statsOverview
                                            .getLong("sc_vehicle"),
                                    statsOverview.getLong("combatScore"),
                                    statsOverview.getLong("sc_award"),
                                    statsOverview.getLong("sc_unlock"),
                                    statsOverview.getLong("totalScore")

                            )

                            );

                } else {

                    stats.put(

                            profileData.getPersona(i).getId(),
                            new PersonaStats(

                                    profileData.getUsername(), profileData
                                            .getPersona(i).getName(), "Recruit", 0,
                                    profileData.getPersona(i).getId(), profileData
                                            .getId(), profileData
                                            .getPersona(i).getPlatformId(), 0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0,
                                    0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0

                            )

                            );

                }

            }

            if (CacheHandler.Persona.insert(context, stats) == 0) {

                CacheHandler.Persona.update(context, stats);

            }

            // Return!
            return stats;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static HashMap<Long, UnlockDataWrapper> getUnlocksForUser(
            final ProfileData pd) throws WebsiteHandlerException {

        try {

            // Init the RequestHandler
            RequestHandler wh = new RequestHandler();

            // Init the ArrayLists
            HashMap<Long, UnlockDataWrapper> unlockDataMap = new HashMap<Long, UnlockDataWrapper>();
            List<UnlockData> weaponArray;
            List<UnlockData> attachmentArray;
            List<UnlockData> kitUnlockArray;
            List<UnlockData> vehicleUpgradeArray;
            List<UnlockData> skillArray;
            List<UnlockData> unlockArray;

            for (int count = 0, maxCount = pd.getNumPersonas(); count < maxCount; count++) {

                weaponArray = new ArrayList<UnlockData>();
                attachmentArray = new ArrayList<UnlockData>();
                kitUnlockArray = new ArrayList<UnlockData>();
                vehicleUpgradeArray = new ArrayList<UnlockData>();
                skillArray = new ArrayList<UnlockData>();
                unlockArray = new ArrayList<UnlockData>();

                // Get the data
                String content = "";

                content = wh
                        .get(

                                Constants.URL_STATS_UNLOCKS.replace(

                                        "{PID}", pd.getPersona(count).getId() + ""

                                        ).replace(

                                                "{PLATFORM_ID}",
                                                pd.getPersona(count).getPlatformId() + ""), 0

                        );

                // JSON Objects
                JSONObject dataObject = new JSONObject(content)
                        .getJSONObject("data");
                JSONArray unlockResults = dataObject.optJSONArray("unlocks");
                int unlockKit;

                if (dataObject.isNull("unlocks") || unlockResults.length() == 0) {

                    unlockDataMap.put(pd.getPersona(count).getId(),
                            new UnlockDataWrapper(null, null, null, null, null,
                                    null));
                    continue;

                }

                // Iterate over the unlocksArray
                for (int i = 0, max = unlockResults.length(); i < max; i++) {

                    // Get the temporary object
                    JSONObject unlockRow = unlockResults.optJSONObject(i);

                    try {

                        unlockKit = unlockRow.getInt("kit");

                    } catch (Exception ex) {

                        unlockKit = 0;

                    }

                    // What did we get?
                    if (!unlockRow.isNull("weaponAddonUnlock")) {

                        // Get the object
                        JSONObject detailObject = unlockRow
                                .getJSONObject("weaponAddonUnlock");
                        JSONObject unlockDetails = detailObject
                                .getJSONObject("unlockedBy");

                        // Bigger than 0.0 (ie 0.1+)?
                        if (unlockDetails.getDouble("completion") == 0.0) {
                            continue;
                        }

                        // Add them to the array
                        attachmentArray.add(

                                new UnlockData(

                                        unlockKit, unlockDetails.getDouble("completion"),
                                        unlockDetails.getLong("valueNeeded"),
                                        unlockDetails.getLong("actualValue"),
                                        detailObject.getString("weaponCode"),
                                        detailObject.getString("unlockId"),
                                        unlockDetails.getString("codeNeeded"),
                                        "weapon+"

                                )

                                );

                    } else if (!unlockRow.isNull("kitItemUnlock")) {

                        // Get the object
                        JSONObject detailObject = unlockRow
                                .getJSONObject("kitItemUnlock");
                        JSONObject unlockDetails = detailObject
                                .getJSONObject("unlockedBy");

                        // Less than 1.0?
                        if (unlockDetails.getDouble("completion") < 1.0) {
                            continue;
                        }

                        // Add them to the array
                        kitUnlockArray.add(

                                new UnlockData(

                                        unlockKit, unlockDetails.getDouble("completion"),
                                        unlockDetails.getLong("valueNeeded"),
                                        unlockDetails.getLong("actualValue"), unlockRow
                                                .getString("parentId"), detailObject
                                                .getString("unlockId"), unlockDetails
                                                .getString("codeNeeded"), "kit+"

                                )

                                );

                    } else if (!unlockRow.isNull("vehicleAddonUnlock")) {

                        // Get the object
                        JSONObject detailObject = unlockRow
                                .getJSONObject("vehicleAddonUnlock");
                        JSONObject unlockDetails = detailObject
                                .getJSONObject("unlockedBy");

                        // Less than 1.0?
                        if (unlockDetails.getDouble("completion") < 1.0) {
                            continue;
                        }

                        // Add them to the array
                        vehicleUpgradeArray.add(

                                new UnlockData(

                                        unlockKit, unlockDetails.getDouble("completion"),
                                        unlockDetails.getLong("valueNeeded"),
                                        unlockDetails.getLong("actualValue"), unlockRow
                                                .getString("parentId"), detailObject
                                                .getString("unlockId"), unlockDetails
                                                .getString("codeNeeded"), "vehicle+"

                                )

                                );

                    } else if (!unlockRow.isNull("weaponUnlock")) {

                        // Get the object
                        JSONObject detailObject = unlockRow
                                .getJSONObject("weaponUnlock");
                        JSONObject unlockDetails = detailObject
                                .getJSONObject("unlockedBy");

                        // Less than 1.0?
                        if (unlockDetails.getDouble("completion") < 1.0) {
                            continue;
                        }

                        // Add them to the array
                        weaponArray.add(

                                new UnlockData(

                                        unlockKit, unlockDetails.getDouble("completion"),
                                        unlockDetails.getLong("valueNeeded"),
                                        unlockDetails.getLong("actualValue"), unlockRow
                                                .getString("parentId"), detailObject
                                                .getString("unlockId"), unlockDetails
                                                .getString("codeNeeded"), "weapon"

                                )

                                );

                    } else if (!unlockRow.isNull("soldierSpecializationUnlock")) {

                        // Get the object
                        JSONObject detailObject = unlockRow
                                .getJSONObject("soldierSpecializationUnlock");
                        JSONObject unlockDetails = detailObject
                                .getJSONObject("unlockedBy");

                        // Less than 1.0?
                        if (unlockDetails.getDouble("completion") < 1.0) {
                            continue;
                        }

                        // Add them to the array
                        skillArray.add(

                                new UnlockData(

                                        unlockKit, unlockDetails.getDouble("completion"),
                                        unlockDetails.getLong("valueNeeded"),
                                        unlockDetails.getLong("actualValue"), unlockRow
                                                .getString("parentId"), detailObject
                                                .getString("unlockId"), unlockDetails
                                                .getString("codeNeeded"), "skill"

                                )

                                );

                    } else {
                    }
                }

                // Let's put them together
                unlockArray.addAll(weaponArray);
                unlockArray.addAll(attachmentArray);
                unlockArray.addAll(kitUnlockArray);
                unlockArray.addAll(vehicleUpgradeArray);
                unlockArray.addAll(skillArray);

                // Yay
                Collections.sort(weaponArray, new UnlockComparator());
                Collections.sort(attachmentArray, new UnlockComparator());
                Collections.sort(kitUnlockArray, new UnlockComparator());
                Collections.sort(vehicleUpgradeArray, new UnlockComparator());
                Collections.sort(skillArray, new UnlockComparator());
                Collections.sort(unlockArray, new UnlockComparator());

                unlockDataMap.put(pd.getPersona(count).getId(),
                        new UnlockDataWrapper(weaponArray, attachmentArray,
                                kitUnlockArray, vehicleUpgradeArray,
                                skillArray, unlockArray));

            }

            return unlockDataMap;

        } catch (Exception ex) {

            ex.printStackTrace();
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

    public static boolean answerPlatoonRequest(long plId, long pId,
            Boolean accepting, String checksum) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Modifier
            int modifier = (accepting) ? 0 : 1;

            httpContent = wh
                    .post(

                            Constants.URL_PLATOON_RESPOND.replace(

                                    "{PLATOON_ID}", plId + ""),
                            new PostData[] {

                                    new PostData(

                                            Constants.FIELD_NAMES_PLATOON_RESPOND[0],
                                            ""

                                    ),
                                    new PostData(

                                            Constants.FIELD_NAMES_PLATOON_RESPOND[1],
                                            pId + ""

                                    ),
                                    new PostData(

                                            Constants.FIELD_NAMES_PLATOON_RESPOND[2],
                                            checksum

                                    ),
                                    new PostData(

                                            Constants.FIELD_NAMES_PLATOON_RESPOND[3 + modifier],
                                            Constants.FIELD_VALUES_PLATOON_RESPOND[3 + modifier]

                                    )

                            }, 0

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                return true;

            } else {

                throw new WebsiteHandlerException(
                        "Could not respond to the platoon request.");

            }

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

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

    public static ProfileInformation getProfileInformationForUser(
            Context context, ProfileData profileData,
            long activeProfileId) throws WebsiteHandlerException {

        try {

            // Let's go!
            RequestHandler rh = new RequestHandler();
            List<PlatoonData> platoonDataArray = new ArrayList<PlatoonData>();
            String httpContent;

            // Get the content
            httpContent = rh.get(
                    Constants.URL_PROFILE_INFO.replace("{UNAME}",
                            profileData.getUsername()), 1);

            // Did we manage?
            if (!"".equals(httpContent)) {

                // JSON Objects
                JSONObject contextObject = new JSONObject(httpContent)
                        .optJSONObject("context");
                JSONObject profileCommonObject = contextObject
                        .optJSONObject("profileCommon");
                JSONObject userInfo = profileCommonObject
                        .optJSONObject("userinfo");
                JSONObject presenceObject = profileCommonObject.getJSONObject(
                        "user").getJSONObject("presence");
                // JSONArray gameReports = contextObject.getJSONArray(
                // "gameReportPreviewGroups" ); //Max 16 items
                JSONArray soldierArray = contextObject
                        .getJSONArray("soldiersBox");
                // JSONArray feedItems = contextObject.getJSONArray("feed");
                JSONArray platoonArray = profileCommonObject
                        .getJSONArray("platoons");
                JSONObject statusMessage = profileCommonObject
                        .optJSONObject("userStatusMessage");
                JSONObject currItem;
                String playingOn;

                // Persona related
                int numSoldiers = soldierArray.length();
                int numPlatoons = platoonArray.length();

                // Init the arrays
                PersonaData[] personaArray = new PersonaData[numSoldiers];

                // Get the username
                String username = profileCommonObject.getJSONObject("user")
                        .getString("username");

                // Is status messages null?
                if (statusMessage == null) {
                    statusMessage = new JSONObject(
                            "{'statusMessage':'', 'statusMessageChanged':0}");
                }

                // What's up with the user?
                if (presenceObject.isNull("serverName")
                        && presenceObject.getBoolean("isPlaying")) {

                    playingOn = (presenceObject.getInt("platform") == 2) ? "Xbox Live"
                            : "Playstation Network";

                } else {

                    playingOn = presenceObject.optString("serverName", "");

                }

                for (int i = 0, max = numSoldiers; i < max; i++) {

                    // Each loop is an object
                    currItem = soldierArray.getJSONObject(i);
                    JSONObject personaObject = currItem
                            .getJSONObject("persona");

                    // Store them
                    personaArray[i] = new PersonaData(
                            Long.parseLong(personaObject
                                    .getString("personaId")),
                            (personaObject.isNull("personaName") ? username
                                    : personaObject.getString("personaName")) + " " + DataBank
                                    .resolvePlatformId(DataBank
                                            .getPlatformIdFromName(personaObject
                                                    .getString("namespace"))),
                            DataBank
                                    .getPlatformIdFromName(personaObject
                                            .getString("namespace")),
                            null
                            );

                }

                // Iterate over the platoons
                for (int i = 0; i < numPlatoons; i++) {

                    // Each loop is an object
                    currItem = platoonArray.getJSONObject(i);

                    // Let's cache the gravatar
                    String title = currItem.getString("id") + ".jpeg";

                    // Is it cached?
                    if (!CacheHandler.isCached(context, title)) {

                        WebsiteHandler.cacheBadge(

                                context, currItem.getString("badgePath"), title,
                                Constants.DEFAULT_BADGE_SIZE

                                );

                    }

                    // Store the data
                    platoonDataArray.add(

                            new PlatoonData(

                                    Long.parseLong(currItem.getString("id")), currItem
                                            .getInt("fanCounter"), currItem
                                            .getInt("memberCounter"), currItem
                                            .getInt("platform"), currItem.getString("name"),
                                    currItem.getString("tag"), title, !currItem
                                            .getBoolean("hidden")

                            )

                            );

                }

                ProfileInformation tempProfile = new ProfileInformation(

                        userInfo.optInt("age", 0), profileData.getId(),
                        userInfo.optLong("birthdate", 0), userInfo.optLong(
                                "lastLogin", 0), statusMessage.optLong(
                                "statusMessageChanged", 0), personaArray, userInfo.optString(
                                "name", "N/A"), username,
                        userInfo.isNull("presentation") ? null : userInfo
                                .getString("presentation"), userInfo.optString(
                                "location", "us"), statusMessage.optString(
                                "statusMessage", ""), playingOn, userInfo.optBoolean(
                                "allowFriendRequests", true),
                        presenceObject.getBoolean("isOnline"),
                        presenceObject.getBoolean("isPlaying"),
                        profileCommonObject.getString("friendStatus").equals(
                                "ACCEPTED"), platoonDataArray);

                // Let's log it
                if (CacheHandler.Profile.insert(context, tempProfile) == 0) {

                    CacheHandler.Profile.update(context, tempProfile);

                }

                // RETURN
                return tempProfile;

            } else {

                throw new WebsiteHandlerException("Could not get the profile.");

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static ArrayList<PlatoonMemberData> getFansForPlatoon(
            final long platoonId) throws WebsiteHandlerException {

        try {

            // Let's go!
            RequestHandler rh = new RequestHandler();
            List<PlatoonMemberData> fans = new ArrayList<PlatoonMemberData>();
            String httpContent;

            // Do the request
            httpContent = rh.get(

                    Constants.URL_PLATOON_FANS.replace("{PLATOON_ID}", platoonId + ""),
                    1

                    );

            // Let's start with the JSON shall we?
            JSONObject fanArray = new JSONObject(httpContent).getJSONObject(
                    "context").getJSONObject("fans");
            JSONArray fanIdArray = fanArray.names();
            JSONObject tempObject = null;

            // Iterate over the fans
            if (fanIdArray != null) {

                for (int i = 0, max = fanIdArray.length(); i < max; i++) {

                    // Grab the fan
                    tempObject = fanArray
                            .getJSONObject(fanIdArray.getString(i));

                    // Store him in the ArrayList
                    fans.add(

                            new PlatoonMemberData(

                                    Long.parseLong(tempObject.getString("userId")),
                                    tempObject.getString("username"),
                                    new PersonaData[] {},
                                    tempObject.optString("gravatarMd5", ""), 0)

                            );

                }

            }

            // Did we get more than 0?
            if (fans.size() > 0) {

                // Add a header just 'cause we can
                fans.add(

                        new PlatoonMemberData(

                                0, "Loyal fans", new PersonaData[] {}, null, 0

                        )

                        );

                // a-z please!
                Collections.sort(fans, new ProfileComparator());

            }
            // Return
            return (ArrayList<PlatoonMemberData>) fans;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }
    }

    public static PlatoonInformation getProfileInformationForPlatoon(

            final Context c, final PlatoonData pData, final int num, final long aPId

            ) throws WebsiteHandlerException {

        try {

            // Let's go!
            RequestHandler rh = new RequestHandler();
            List<PlatoonMemberData> fans = new ArrayList<PlatoonMemberData>();
            List<PlatoonMemberData> members = new ArrayList<PlatoonMemberData>();
            List<ProfileData> friends = new ArrayList<ProfileData>();
            PlatoonStats stats = null;

            // Arrays to divide the users in
            List<PlatoonMemberData> founderMembers = new ArrayList<PlatoonMemberData>();
            List<PlatoonMemberData> adminMembers = new ArrayList<PlatoonMemberData>();
            List<PlatoonMemberData> regularMembers = new ArrayList<PlatoonMemberData>();
            List<PlatoonMemberData> invitedMembers = new ArrayList<PlatoonMemberData>();
            List<PlatoonMemberData> requestMembers = new ArrayList<PlatoonMemberData>();

            // Get the content
            String httpContent = rh.get(
                    Constants.URL_PLATOON.replace("{PLATOON_ID}", pData.getId()
                            + ""), 1);
            boolean isAdmin = false;
            boolean isMember = false;

            // Did we manage?
            if (!"".equals(httpContent)) {

                // JSON Objects
                JSONObject contextObject = new JSONObject(httpContent)
                        .optJSONObject("context");

                // Does the platoon exist?
                if (contextObject.isNull("platoon")) {
                    return null;
                }

                // Moar JSON
                JSONObject profileCommonObject = contextObject
                        .optJSONObject("platoon");
                JSONObject memberArray = profileCommonObject
                        .optJSONObject("members");
                // JSONArray feedItems = contextObject.getJSONArray( "feed" );
                JSONObject currItem;

                // Get the user's friends
                friends = WebsiteHandler.getFriends(

                        PreferenceManager.getDefaultSharedPreferences(c)
                                .getString(Constants.SP_BL_PROFILE_CHECKSUM, ""), false

                        );

                // Let's iterate over the members
                JSONArray idArray = memberArray.names();
                for (int counter = 0, max = idArray.length(); counter < max; counter++) {

                    // Temporary var
                    PlatoonMemberData tempProfile;

                    // Get the current item
                    currItem = memberArray.optJSONObject(idArray
                            .getString(counter));

                    // Check the *rights* of the user
                    if (idArray.getString(counter).equals("" + aPId)) {

                        isMember = true;
                        if (currItem.getInt("membershipLevel") >= 128) {
                            isAdmin = true;
                        }
                    }

                    // If we have a persona >> add it to the members
                    if (!currItem.isNull("persona")) {

                        tempProfile = new PlatoonMemberData(

                                Long.parseLong(currItem.getString("userId")),
                                currItem.getJSONObject("user").getString("username"),
                                new PersonaData(
                                        Long.parseLong(currItem
                                                .getString("personaId")),
                                        currItem
                                                .getJSONObject("persona").getString(
                                                        "personaName"),
                                        profileCommonObject.getInt("platform"),
                                        null

                                ),
                                currItem.optString("gravatarMd5", ""),
                                currItem.getJSONObject("user").getJSONObject("presence")
                                        .getBoolean("isOnline"), currItem
                                        .getJSONObject("user")
                                        .getJSONObject("presence")
                                        .getBoolean("isPlaying"),
                                currItem.getInt("membershipLevel")

                                );

                    } else {

                        continue;

                    }

                    // Add the user to the correct *part* of the (future) merged
                    // list
                    switch (currItem.getInt("membershipLevel")) {

                        case 1:
                            requestMembers.add(tempProfile);
                            break;

                        case 2:
                            invitedMembers.add(tempProfile);
                            break;

                        case 4:
                            regularMembers.add(tempProfile);
                            break;

                        case 128:
                            adminMembers.add(tempProfile);
                            break;

                        case 256:
                            founderMembers.add(tempProfile);
                            break;

                        default:
                            regularMembers.add(tempProfile);
                            break;
                    }

                }

                // Let's sort the members...
                Collections.sort(requestMembers, new ProfileComparator());
                Collections.sort(invitedMembers, new ProfileComparator());
                Collections.sort(regularMembers, new ProfileComparator());
                Collections.sort(adminMembers, new ProfileComparator());
                Collections.sort(founderMembers, new ProfileComparator());

                // ...and then merge them with their *labels*
                if (founderMembers.size() > 0) {

                    // Plural?
                    if (founderMembers.size() > 1) {

                        members.add(new PlatoonMemberData(0, c
                                .getString(R.string.info_platoon_member_founder_p),
                                new PersonaData[] {}, null, 0));

                    } else {

                        members.add(new PlatoonMemberData(0, c
                                .getString(R.string.info_platoon_member_founder),
                                new PersonaData[] {}, null, 0));

                    }

                    // Add them to the members
                    members.addAll(founderMembers);

                }

                if (adminMembers.size() > 0) {

                    // Plural?
                    if (adminMembers.size() > 1) {

                        members.add(new PlatoonMemberData(0, c
                                .getString(R.string.info_platoon_member_admin_p),
                                new PersonaData[] {}, null, 0));

                    } else {

                        members.add(new PlatoonMemberData(0, c
                                .getString(R.string.info_platoon_member_admin),
                                new PersonaData[] {}, null, 0));

                    }

                    // Add them to the members
                    members.addAll(adminMembers);

                }

                if (regularMembers.size() > 0) {

                    // Plural?
                    if (regularMembers.size() > 1) {

                        members.add(new PlatoonMemberData(0, c
                                .getString(R.string.info_platoon_member_regular_p),
                                new PersonaData[] {}, null, 0));

                    } else {

                        members.add(new PlatoonMemberData(0, c
                                .getString(R.string.info_platoon_member_regular),
                                new PersonaData[] {}, null, 0));

                    }

                    // Add them to the members
                    members.addAll(regularMembers);

                }

                // Is the user *admin* or higher?
                if (isAdmin) {

                    if (invitedMembers.size() > 0) {

                        // Just add them
                        members.add(new PlatoonMemberData(0, c
                                .getString(R.string.info_platoon_member_invited_label),
                                new PersonaData[] {}, null, 0));
                        members.addAll(invitedMembers);

                    }

                    if (requestMembers.size() > 0) {

                        // Just add them
                        members.add(new PlatoonMemberData(0, c
                                .getString(R.string.info_platoon_member_requested_label),
                                new PersonaData[] {}, null, 0));
                        members.addAll(requestMembers);

                    }

                }

                // Let's get 'em fans too
                fans = WebsiteHandler.getFansForPlatoon(pData.getId());

                // Oh man, don't forget the stats!!!
                stats = WebsiteHandler.getStatsForPlatoon(c, pData);

                // Required
                long platoonId = Long.parseLong(profileCommonObject
                        .getString("id"));
                String filename = platoonId + ".jpeg";

                // Is the image already cached?
                Log.d(Constants.DEBUG_TAG, "filename => " + filename
                        + " (cached: " + CacheHandler.isCached(c, filename)
                        + ")");
                if (!CacheHandler.isCached(c, filename)) {

                    WebsiteHandler.cacheBadge(c,
                            profileCommonObject.getString("badgePath"),
                            filename, Constants.DEFAULT_BADGE_SIZE);

                }

                // Return it!
                PlatoonInformation platoonInformation = new PlatoonInformation(

                        platoonId, profileCommonObject.getLong("creationDate"),
                        profileCommonObject.getInt("platform"),
                        profileCommonObject.getInt("game"),
                        profileCommonObject.getInt("fanCounter"),
                        profileCommonObject.getInt("memberCounter"),
                        profileCommonObject.getInt("blazeClubId"),
                        profileCommonObject.getString("name"),
                        profileCommonObject.getString("tag"),
                        profileCommonObject.getString("presentation"),
                        PublicUtils.normalizeUrl(profileCommonObject.optString(
                                "website", "")),
                        !profileCommonObject.getBoolean("hidden"), isMember,
                        isAdmin,
                        profileCommonObject.getBoolean("allowNewMembers"), members, fans, friends,
                        stats

                        );

                // Let's log it
                if (CacheHandler.Platoon.insert(c, platoonInformation) == 0) {

                    CacheHandler.Platoon.update(c, platoonInformation);

                }

                // return
                return platoonInformation;

            } else {

                throw new WebsiteHandlerException("Could not get the platoon.");

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static HashMap<Long, List<AssignmentData>> getAssignments(
            Context c, ProfileData profile) throws WebsiteHandlerException {

        try {

            // Attributes
            RequestHandler rh = new RequestHandler();
            HashMap<Long, List<AssignmentData>> assignmentMap = new HashMap<Long, List<AssignmentData>>();
            List<AssignmentData> items;

            for (int count = 0, maxCount = profile.getNumPersonas(); count < maxCount; count++) {

                // Init
                items = new ArrayList<AssignmentData>();

                // Get the JSON!
                String httpContent = rh.get(

                        Constants.URL_STATS_ASSIGNMENTS
                                .replace(

                                        "{PNAME}", profile.getPersona(count).getName()

                                )
                                .replace(

                                        "{PID}", profile.getPersona(count).getId() + ""

                                )
                                .replace(

                                        "{UID}", profile.getId() + ""

                                )
                                .replace(

                                        "{PLATFORM_ID}",
                                        profile.getPersona(count).getPlatformId() + ""), 1

                        );

                // Parse the JSON!
                JSONObject topLevel = new JSONObject(httpContent)
                        .getJSONObject("data");
                if (topLevel.isNull("missionTrees")) {

                    assignmentMap.put(profile.getPersona(count).getId(), items);
                    continue;

                }

                JSONObject missionTrees = topLevel
                        .getJSONObject("missionTrees");
                JSONArray missionLines = missionTrees.getJSONObject("512")
                        .getJSONArray("missionLines");
                int numCurrentAssignment = 0;
                for (int i = 0, max = missionLines.length(); i < max; i++) {

                    // Let's see if we need to tell the dev
                    if (max > Constants.ASSIGNMENT_RESOURCES_SCHEMATICS.length) {

                        Toast.makeText(

                                c, c.getString(R.string.info_assignments_new_unknown),
                                Toast.LENGTH_SHORT

                                ).show();

                        return null;

                    }

                    // Get the JSONObject per loop
                    JSONArray missions = missionLines.getJSONObject(i)
                            .getJSONArray("missions");
                    for (int missionCounter = 0, missionCount = missions
                            .length(); missionCounter < missionCount; missionCounter++) {

                        JSONObject assignment = missions
                                .getJSONObject(missionCounter);
                        JSONArray criteriasJSON = assignment
                                .getJSONArray("criterias");
                        JSONArray dependenciesJSON = assignment
                                .getJSONArray("dependencies");
                        JSONArray unlocksJSON = assignment
                                .getJSONArray("unlocks");

                        // Init
                        List<AssignmentData.Objective> criterias = new ArrayList<AssignmentData.Objective>();
                        List<AssignmentData.Dependency> dependencies = new ArrayList<AssignmentData.Dependency>();
                        List<AssignmentData.Unlock> unlocks = new ArrayList<AssignmentData.Unlock>();

                        // Alright, let's do this
                        for (int assignmentCounter = 0, assignmentCount = criteriasJSON
                                .length(); assignmentCounter < assignmentCount; assignmentCounter++) {

                            // Get the current item
                            JSONObject currentItem = criteriasJSON
                                    .getJSONObject(assignmentCounter);

                            // New object!
                            criterias.add(

                                    new AssignmentData.Objective(

                                            currentItem.getDouble("actualValue"), currentItem
                                                    .getDouble("completionValue"), currentItem
                                                    .getString("statCode"), currentItem
                                                    .getString("paramX"), currentItem
                                                    .getString("paramY"), currentItem
                                                    .getString("descriptionID"), currentItem
                                                    .getString("unit")

                                    )

                                    );

                        }

                        // Alright, let's do this
                        for (int counter = 0, maxCounter = dependenciesJSON
                                .length(); counter < maxCounter; counter++) {

                            // Get the current item
                            JSONObject currentItem = dependenciesJSON
                                    .getJSONObject(counter);

                            // New object!
                            dependencies.add(

                                    new AssignmentData.Dependency(

                                            currentItem.getInt("count"), currentItem
                                                    .getString("code")

                                    )

                                    );

                        }

                        // Alright, let's do this
                        for (int counter = 0, maxCounter = unlocksJSON.length(); counter < maxCounter; counter++) {

                            // Get the current item
                            JSONObject currentItem = unlocksJSON
                                    .getJSONObject(counter);

                            // New object!
                            unlocks.add(

                                    new AssignmentData.Unlock(

                                            currentItem.getString("unlockId"), currentItem
                                                    .getString("unlockType"), currentItem
                                                    .getBoolean("visible")

                                    )

                                    );

                        }

                        // Add the assignment
                        items.add(

                                new AssignmentData(

                                        Constants.ASSIGNMENT_RESOURCES_SCHEMATICS[numCurrentAssignment],
                                        Constants.ASSIGNMENT_RESOURCES_UNLOCKS[numCurrentAssignment],
                                        assignment.getString("stringID"), assignment
                                                .getString("descriptionID"), assignment
                                                .getString("license"), criterias,
                                        dependencies, unlocks

                                )

                                );

                        // Update the digit
                        numCurrentAssignment++;

                    }

                    // Add the items
                    assignmentMap.put(profile.getPersona(count).getId(), items);

                }

            }

            return assignmentMap;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static ArrayList<FeedItem> getFeed(Context context, int type, long id, int num,
            long profileId) throws WebsiteHandlerException {

        try {

            // Attributes
            RequestHandler rh = new RequestHandler();
            List<FeedItem> feedItems = new ArrayList<FeedItem>();
            JSONArray jsonArray;
            String url = "";
            String httpContent = null;

            // What's the url?
            switch (type) {

                case FeedItem.TYPE_GLOBAL:
                    url = Constants.URL_FRIEND_FEED;
                    break;

                case FeedItem.TYPE_PROFILE:
                    url = Constants.URL_PROFILE_FEED.replace("{PID}", id + "");
                    break;

                case FeedItem.TYPE_PLATOON:
                    url = Constants.URL_PLATOON_FEED.replace("{PLATOON_ID}", id + "");
                    break;

                default:
                    url = Constants.URL_FRIEND_FEED;
                    break;

            }

            // Let's see
            for (int i = 0, max = Math.round(num / 10); i < max; i++) {

                // Get the content, and create a JSONArray
                httpContent = rh.get(
                        url.replace("{NUMSTART}",
                                String.valueOf(i * 10)), 1);
                jsonArray = new JSONObject(httpContent).getJSONObject("data")
                        .getJSONArray("feedEvents");

                // Gather them
                feedItems.addAll(WebsiteHandler.getFeedItemsFromJSON(context,
                        jsonArray, profileId));

            }

            // Return it
            return (ArrayList<FeedItem>) feedItems;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean setActive() throws WebsiteHandlerException {

        try {

            // Let's see
            String httpContent = new RequestHandler().get(
                    Constants.URL_CHAT_SETACTIVE, 1);
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

    public static int getNewNotificationsCount(String checksum)
            throws WebsiteHandlerException {

        try {

            // Init
            RequestHandler rh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = rh.post(

                    Constants.URL_NOTIFICATIONS_TOP5, new PostData[] {

                        new PostData(Constants.FIELD_NAMES_CHECKSUM[0], checksum)

                    }, 3

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

    public static ArrayList<NotificationData> getNotifications(String checksum)
            throws WebsiteHandlerException {

        try {

            // Init
            RequestHandler rh = new RequestHandler();
            List<NotificationData> notifications = new ArrayList<NotificationData>();
            String httpContent;

            // Get the content
            httpContent = rh.get(

                    Constants.URL_NOTIFICATIONS_ALL, 1

                    );

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

    public static PlatoonStats getStatsForPlatoon(final Context context,
            final PlatoonData platoonData) throws WebsiteHandlerException {

        try {

            // Let's go!
            RequestHandler rh = new RequestHandler();

            // Get the content
            String httpContent = rh.get(

                    Constants.URL_PLATOON_STATS.replace(

                            "{PLATOON_ID}", platoonData.getId() + ""

                            ).replace(

                                    "{PLATFORM_ID}", platoonData.getPlatformId() + ""

                            ), 1

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // JSON-base!!
                JSONObject baseObject = new JSONObject(httpContent)
                        .getJSONObject("data");

                // Wait, did I just see what I think I saw?
                if (baseObject.isNull("platoonPersonas")) {

                    // Do we have a platformId?
                    if (platoonData.getPlatformId() == 0) {

                        // Get the content
                        String tempHttpContent = rh.get(

                                Constants.URL_PLATOON.replace(

                                        "{PLATOON_ID}", platoonData.getId() + ""

                                        ), 1

                                );

                        // Build an object
                        JSONObject tempPlatoonData = new JSONObject(
                                tempHttpContent).getJSONObject("context")
                                .getJSONObject("platoon");

                        // Return and reloop
                        return getStatsForPlatoon(

                                context,
                                new PlatoonData(

                                        platoonData.getId(), tempPlatoonData
                                                .getInt("fanCounter"), tempPlatoonData
                                                .getInt("memberCounter"),
                                        tempPlatoonData.getInt("platform"),
                                        tempPlatoonData.getString("name"),
                                        tempPlatoonData.getString("tag"),
                                        platoonData.getId() + ".jpeg",
                                        tempPlatoonData.getBoolean("hidden")

                                )

                        );

                    } else {

                        return null;

                    }

                }

                // Hold on...
                JSONObject memberStats = baseObject
                        .getJSONObject("memberStats");
                JSONObject personaList = baseObject
                        .optJSONObject("platoonPersonas");

                // JSON data-branches
                JSONObject objectGeneral = memberStats.getJSONObject("general");
                JSONObject objectKit = memberStats
                        .getJSONObject("kitsVehicles");
                JSONObject objectTop = memberStats.getJSONObject("topPlayers");
                JSONObject objectScore = objectKit.getJSONObject("score");
                JSONObject objectSPM = objectKit
                        .getJSONObject("scorePerMinute");
                JSONObject objectTime = objectKit.getJSONObject("time");
                JSONObject currObj = null;
                JSONObject currUser = null;
                JSONArray currObjNames = null;

                // Let's iterate and create the containers
                List<PlatoonStatsItem> arrayGeneral = new ArrayList<PlatoonStatsItem>();
                List<PlatoonStatsItem> arrayScore = new ArrayList<PlatoonStatsItem>();
                List<PlatoonStatsItem> arraySPM = new ArrayList<PlatoonStatsItem>();
                List<PlatoonStatsItem> arrayTime = new ArrayList<PlatoonStatsItem>();
                List<PlatoonTopStatsItem> arrayTop = new ArrayList<PlatoonTopStatsItem>();

                // More temp
                List<Integer> tempMid = new ArrayList<Integer>();
                String tempGravatarHash = null;

                // Get the *general* stats
                currObjNames = objectGeneral.names();
                int maxNames = currObjNames.length();

                // Got any?
                if (maxNames == 0) {
                    return null;
                }

                for (int i = 0; i < maxNames; i++) {

                    // Grab the current object
                    currObj = objectGeneral.getJSONObject(currObjNames
                            .getString(i));

                    // Is it the KD? The KD == DOUBLE
                    if (currObjNames.getString(i).equals("kd")) {

                        // Create a new "stats item"
                        arrayGeneral.add(

                                new PlatoonStatsItem(

                                        currObjNames.getString(i), currObj.getDouble("min"),
                                        currObj.getDouble("median"), currObj
                                                .getDouble("best"), currObj
                                                .getDouble("average"), null

                                )

                                );

                    } else {

                        // Create a new "stats item"
                        arrayGeneral.add(

                                new PlatoonStatsItem(

                                        currObjNames.getString(i), currObj.getInt("min"),
                                        currObj.getInt("median"), currObj
                                                .getInt("best"), currObj
                                                .getInt("average"), null

                                )

                                );

                    }

                }

                // Create a new "overall item"
                arrayScore.add(new PlatoonStatsItem(context
                        .getString(R.string.info_platoon_stats_overall), 0, 0,
                        0, 0, null));

                // Get the *kit* scores
                currObjNames = objectScore.names();
                for (int i = 0, max = currObjNames.length(); i < max; i++) {

                    // Grab the current object
                    currObj = objectScore.getJSONObject(currObjNames
                            .getString(i));

                    // Hmm, where is the userInfo?
                    if (currObj.has("bestPersonaId")) {
                        currUser = personaList.getJSONObject(currObj
                                .getString("bestPersonaId"));
                    } else {
                        currUser = personaList.getJSONObject(currObj
                                .getString("personaId"));
                    }

                    // Store the gravatar
                    tempGravatarHash = currUser.optString("gravatarMd5", "");

                    // Create a new "stats item"
                    arrayScore.add(

                            new PlatoonStatsItem(

                                    currObjNames.getString(i), currObj.getInt("min"), currObj
                                            .getInt("median"), currObj.getInt("best"), currObj
                                            .getInt("average"), new ProfileData(
                                            Long
                                                    .parseLong(currUser
                                                            .optString("userId", "0")),
                                            currUser.optString("username", ""),
                                            new PersonaData(
                                                    Long.parseLong(currObj
                                                            .optString(
                                                                    "bestPersonaId", "")), currUser
                                                            .optString(
                                                                    "username", ""), platoonData
                                                            .getPlatformId(), null),
                                            tempGravatarHash

                                    )

                            )

                            );

                    // Is it ! the first?
                    if (i > 0) {
                        tempMid.add(arrayScore.get(i + 1).getMid());
                    }

                    // Update the "overall"
                    arrayScore.get(0).add(arrayScore.get(i + 1));

                }
                Collections.sort(tempMid);
                arrayScore.get(0).setMid(
                        tempMid.get((int) Math.ceil(tempMid.size() / 2)));
                tempMid.clear();

                // Create a new "overall item"
                arraySPM.add(new PlatoonStatsItem(context
                        .getString(R.string.info_platoon_stats_overall), 0, 0,
                        0, 0, null));

                // Get the *kit* score/min
                currObjNames = objectSPM.names();
                for (int i = 0, max = currObjNames.length(); i < max; i++) {

                    // Grab the current object
                    currObj = objectSPM
                            .getJSONObject(currObjNames.getString(i));

                    // Hmm, where is the userInfo?
                    if (currObj.has("bestPersonaId")) {
                        currUser = personaList.getJSONObject(currObj
                                .getString("bestPersonaId"));
                    } else {
                        currUser = personaList.getJSONObject(currObj
                                .getString("personaId"));
                    }

                    // Store the gravatar
                    tempGravatarHash = currUser.optString("gravatarMd5", "");

                    // Create a new "stats item"
                    arraySPM.add(

                            new PlatoonStatsItem(

                                    currObjNames.getString(i), currObj.getInt("min"), currObj
                                            .getInt("median"), currObj.getInt("best"), currObj
                                            .getInt("average"), new ProfileData(
                                            Long
                                                    .parseLong(currUser
                                                            .optString("userId", "0")),
                                            currUser.optString("username", ""),
                                            new PersonaData(
                                                    Long.parseLong(currObj
                                                            .optString(
                                                                    "bestPersonaId", "")), currUser
                                                            .optString(
                                                                    "username", ""), platoonData
                                                            .getPlatformId(), null),
                                            tempGravatarHash

                                    )

                            )

                            );

                    // Is it ! the first?
                    if (i > 0) {
                        tempMid.add(arraySPM.get(0).getMid());
                    }

                    // Update the "overall"
                    arraySPM.get(0).add(arraySPM.get(i + 1));

                }
                Collections.sort(tempMid);
                arraySPM.get(0).setMid(
                        tempMid.get((int) Math.ceil(tempMid.size() / 2)));
                tempMid.clear();

                // Create a new "overall item"
                arrayTime.add(new PlatoonStatsItem(context
                        .getString(R.string.info_platoon_stats_overall), 0, 0,
                        0, 0, null));

                // Get the *kit* times
                currObjNames = objectTime.names();
                for (int i = 0, max = currObjNames.length(); i < max; i++) {

                    // Grab the current object
                    currObj = objectTime.getJSONObject(currObjNames
                            .getString(i));

                    // Hmm, where is the userInfo?
                    if (currObj.has("bestPersonaId")) {
                        currUser = personaList.getJSONObject(currObj
                                .getString("bestPersonaId"));
                    } else {
                        currUser = personaList.getJSONObject(currObj
                                .getString("personaId"));
                    }

                    // Store the gravatar
                    tempGravatarHash = currUser.optString("gravatarMd5", "");

                    // Create a new "stats item"
                    arrayTime.add(

                            new PlatoonStatsItem(

                                    currObjNames.getString(i), currObj.getInt("min"), currObj
                                            .getInt("median"), currObj.getInt("best"), currObj
                                            .getInt("average"), new ProfileData(
                                            Long
                                                    .parseLong(currUser
                                                            .optString("userId", "0")),
                                            currUser.optString("username", ""),
                                            new PersonaData(
                                                    Long.parseLong(currObj
                                                            .optString(
                                                                    "bestPersonaId", "")), currUser
                                                            .optString(
                                                                    "username", ""), platoonData
                                                            .getPlatformId(), null),
                                            tempGravatarHash

                                    )

                            )

                            );

                    // Is it ! the first?
                    if (i > 0) {
                        tempMid.add(arrayTime.get(0).getMid());
                    }

                    // Update the "overall"
                    arrayTime.get(0).add(arrayTime.get(i + 1));

                }
                Collections.sort(tempMid);
                arrayTime.get(0).setMid(
                        tempMid.get((int) Math.ceil(tempMid.size() / 2)));
                tempMid.clear();

                // Get the *top player* Tops
                PlatoonTopStatsItem highestSPM = null;
                currObjNames = objectTop.names();

                for (int i = 0, max = currObjNames.length(); i < max; i++) {

                    // Grab the current object
                    currObj = objectTop
                            .getJSONObject(currObjNames.getString(i));

                    // Hmm, where is the userInfo?
                    if (currObj.has("bestPersonaId")) {
                        currUser = personaList.getJSONObject(currObj
                                .getString("bestPersonaId"));
                    } else if (!currObj.getString("personaId").equals("0")) {

                        currUser = personaList.getJSONObject(currObj
                                .getString("personaId"));

                    } else {

                        // Create a new "stats item"
                        arrayTop.add(

                                new PlatoonTopStatsItem(

                                        "N/A", 0, null

                                )

                                );

                        // Continue
                        continue;

                    }

                    // Store the gravatar
                    tempGravatarHash = currUser.optString("gravatarMd5", "");
                    String filename = tempGravatarHash + ".png";

                    // Do we need to download a new image?
                    if (!CacheHandler.isCached(context, filename)) {

                        WebsiteHandler.cacheGravatar(context, filename,
                                Constants.DEFAULT_AVATAR_SIZE);

                    }

                    // Create a new "stats item"
                    arrayTop.add(

                            new PlatoonTopStatsItem(

                                    currObjNames.getString(i), currObj.getInt("spm"),
                                    new ProfileData(
                                            Long
                                                    .parseLong(currUser
                                                            .optString("userId", "0")),
                                            currUser.optString("username", ""),
                                            new PersonaData(
                                                    Long.parseLong(currObj
                                                            .optString(
                                                                    "bestPersonaId", "0")),
                                                    currUser.optString(
                                                            "username", ""), platoonData
                                                            .getPlatformId(), null),
                                            tempGravatarHash

                                    )

                            )

                            );

                    // Store it if it's the highest
                    if (highestSPM == null
                            || highestSPM.getSPM() < arrayTop.get(i).getSPM()) {

                        highestSPM = arrayTop.get(i);

                    }

                }

                // Set the best & sort
                arrayTop.add(

                        new PlatoonTopStatsItem(

                                "TOP", highestSPM.getSPM(), highestSPM.getProfile()

                        )

                        );
                Collections.sort(arrayTop, new TopStatsComparator());

                // Return it now!!
                return new PlatoonStats(

                        platoonData.getName(), platoonData.getId(), arrayGeneral,
                        arrayTop, arrayScore, arraySPM, arrayTime

                );

            } else {

                return null;

            }

        } catch (JSONException ex) {

            ex.printStackTrace();
            return null;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    private static ArrayList<FeedItem> getFeedItemsFromJSON(Context context,
            JSONArray jsonArray, long activeProfileId)
            throws WebsiteHandlerException {

        try {

            // Variables that we need
            JSONObject currItem = null;
            JSONObject tempSubItem = null;
            JSONObject ownerObject = null;
            JSONObject otherUserObject = null;
            FeedItem tempFeedItem = null;
            List<FeedItem> feedItemArray = new ArrayList<FeedItem>();

            // Iterate over the feed
            for (int i = 0, max = jsonArray.length(); i < max; i++) {

                // Each loop is an object
                currItem = jsonArray.getJSONObject(i);

                // If we get a null, we do it my way!
                ownerObject = currItem.optJSONObject("owner");
                if (ownerObject == null) {
                    continue;
                }

                /*
                 * //Once per loop comments = new ArrayList<CommentData>();
                 * //Let's get the comments if( currItem.getInt("numComments") >
                 * 2 ) { comments = WebsiteHandler.getCommentsForPost(
                 * Long.parseLong(currItem.getString("id")) ); } else if(
                 * currItem.getInt( "numComments" ) == 0 ) { //For now, we do
                 * nothing here } else { //Iterate, as there's only comment1 &
                 * comment2 for( int cCounter = 1; cCounter < 3; cCounter++ ) {
                 * //No comment? if( currItem.isNull( "comment"+cCounter ) )
                 * break; //Grab a temporary comment and add it to our ArrayList
                 * tempCommentItem = currItem.getJSONObject( "comment"+cCounter
                 * ); JSONObject tempOwnerItem =
                 * currItem.getJSONObject("owner"); comments.add( new
                 * CommentData( Long.parseLong(
                 * tempCommentItem.getString("id")), 0, tempCommentItem.getLong(
                 * "creationDate" ), tempCommentItem.getLong( "ownerId" ),
                 * tempOwnerItem.getString("username"),
                 * tempCommentItem.getString( "body" ),
                 * tempOwnerItem.getString("gravatarMd5") ) ); } }
                 */

                // Variables if *modification* is needed
                String itemTitle = "";
                String itemContent = "";
                String tempGravatarHash = ownerObject.getString("gravatarMd5");

                // Process the likes
                JSONArray likeUsers = currItem.getJSONArray("likeUserIds");
                int numLikes = likeUsers.length();
                int numComments = currItem.getInt("numComments");
                boolean liked = false;
                boolean censored = currItem.getBoolean("hidden");

                // Iterate and see if the user has *liked* it already
                for (int likeCount = 0; likeCount < numLikes; likeCount++) {

                    if (Long.parseLong(likeUsers.getString(likeCount)) == activeProfileId) {

                        liked = true;
                        break;

                    }

                }

                // What do we have *here*?
                if (!currItem.isNull("BECAMEFRIENDS")) {

                    // Grab the specific object
                    tempSubItem = currItem.optJSONObject("BECAMEFRIENDS");
                    JSONObject friendUser = tempSubItem
                            .getJSONObject("friendUser");

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments,
                            context.getString(R.string.info_p_friendship), "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ),
                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            friendUser.getString("username"),
                                            new PersonaData[] {},
                                            friendUser.getString("gravatarMd5")

                                    )

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("ASSIGNMENTCOMPLETE")) {

                    // Grab the specific object
                    tempSubItem = currItem.optJSONObject("ASSIGNMENTCOMPLETE");
                    JSONObject statsItem = tempSubItem
                            .getJSONArray("statItems").getJSONObject(0);
                    String[] tempInfo = DataBank.getAssignmentTitle(statsItem
                            .getString("langKeyTitle"));

                    // Set the title
                    itemTitle = context
                            .getString(R.string.info_txt_assignment_ok)
                            .replace(

                                    "{assignment}", tempInfo[0]

                            ).replace(

                                    "{unlock}", tempInfo[1]

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("CREATEDFORUMTHREAD")) {

                    // Grab the specific object
                    tempSubItem = currItem.optJSONObject("CREATEDFORUMTHREAD");
                    itemTitle = context.getString(R.string.info_p_forumthread)
                            .replace(

                                    "{thread}", tempSubItem.getString("threadTitle")

                            );
                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle,
                            tempSubItem.getString("threadBody"),
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("WROTEFORUMPOST")) {

                    // Grab the specific object
                    tempSubItem = currItem.optJSONObject("WROTEFORUMPOST");
                    itemTitle = context.getString(R.string.info_p_forumpost)
                            .replace(

                                    "{thread}", tempSubItem.getString("threadTitle")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle,
                            tempSubItem.getString("postBody"),
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("GAMEREPORT")) {

                    // Grab the specific object
                    JSONArray tempStatsArray = currItem.optJSONObject(
                            "GAMEREPORT").optJSONArray("statItems");

                    if (tempStatsArray.length() > 1) {

                        itemTitle = context
                                .getString(R.string.info_p_newunlocks);

                    } else {

                        itemTitle = context
                                .getString(R.string.info_p_newunlock);

                    }

                    for (int statsCounter = 0, maxCounter = tempStatsArray
                            .length(); statsCounter < maxCounter; statsCounter++) {

                        // Let's get the item
                        String tempKey;
                        tempSubItem = tempStatsArray
                                .optJSONObject(statsCounter);
                        if (itemContent.equals("")) {
                            itemContent = "<b>";
                        }

                        // Do we need to append anything?
                        if (statsCounter > 0) {

                            if (statsCounter == (maxCounter - 1)) {

                                itemContent += " </b>and<b> ";

                            } else {

                                itemContent += ", ";

                            }

                        }

                        // Weapon? Attachment?
                        if (!tempSubItem.isNull("parentLangKeyTitle")) {

                            // Let's see
                            String parentKey = tempSubItem
                                    .getString("parentLangKeyTitle");
                            tempKey = DataBank.getWeaponTitleShort(parentKey);

                            // Is it empty?
                            if (!parentKey.equals(tempKey)) {

                                itemContent += tempKey
                                        + " "
                                        + DataBank
                                                .getAttachmentTitle(tempSubItem
                                                        .getString("langKeyTitle"));

                            } else {

                                // Grab a vehicle title then
                                tempKey = DataBank.getVehicleTitle(parentKey);

                                // Validate
                                if (!parentKey.equals(tempKey)) {

                                    itemContent += tempKey
                                            + " "
                                            + DataBank
                                                    .getVehicleAddon(tempSubItem
                                                            .getString("langKeyTitle"));

                                } else {

                                    itemContent += tempKey;

                                }

                            }

                        } else {

                            // Let's see
                            String key = tempSubItem.getString("langKeyTitle");
                            tempKey = DataBank.getWeaponTitleShort(key);

                            if (key.equals(tempKey)) {

                                tempKey = DataBank.getVehicleAddon(key);

                                if (key.equals(tempKey)) {

                                    tempKey = DataBank.getKitUnlockTitle(key);

                                    if (key.equals(tempKey)) {

                                        tempKey = DataBank.getSkillTitle(key);

                                        if (key.equals(tempKey)) {

                                            Log.d(Constants.DEBUG_TAG, tempKey
                                                    + " => " + tempKey);
                                            itemContent += tempKey;

                                        } else {

                                            itemContent += tempKey;

                                        }

                                    } else {

                                        itemContent += tempKey;

                                    }

                                } else {

                                    itemContent += tempKey;

                                }

                            } else {

                                itemContent += tempKey;

                            }

                        }

                    }

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle.replace("{item}",
                                    itemContent + "</b>"), "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("STATUSMESSAGE")) {

                    // Get the JSON-Object
                    tempSubItem = currItem.optJSONObject("STATUSMESSAGE");

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, "<b>{username}</b> "
                                    + tempSubItem.getString("statusMessage"),
                            "", currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("ADDEDFAVSERVER")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("ADDEDFAVSERVER");

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, context.getString(
                                    R.string.info_p_favserver).replace(

                                    "{server}", tempSubItem.getString("serverName")

                                    ), "", currItem.getString("event"),
                            new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("RANKEDUP")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("RANKEDUP");

                    // Set it!
                    itemTitle = context
                            .getString(R.string.info_p_promotion)
                            .replace(

                                    "{rank title}",
                                    DataBank.getRankTitle(tempSubItem
                                            .getString("langKeyTitle"))

                            ).replace(

                                    "{rank}", tempSubItem.getString("rank")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("COMMENTEDGAMEREPORT")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("COMMENTEDGAMEREPORT");

                    // Set it!
                    itemTitle = context
                            .getString(R.string.info_p_greport_comment)
                            .replace(

                                    "{server name}",
                                    tempSubItem.getString("serverName")

                            )
                            .replace(

                                    "{map}",
                                    DataBank.getMapTitle(tempSubItem
                                            .getString("map"))

                            )
                            .replace(

                                    "{game mode}",
                                    DataBank.getGameModeFromId(tempSubItem
                                            .getInt("gameMode"))

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle,
                            tempSubItem.getString("gameReportComment"),
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("COMMENTEDBLOG")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("COMMENTEDBLOG");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_blog_comment)
                            .replace(

                                    "{post name}", tempSubItem.getString("blogTitle")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle,
                            tempSubItem.getString("blogCommentBody"),
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("JOINEDPLATOON")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("JOINEDPLATOON")
                            .getJSONObject("platoon");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_platoon_join)
                            .replace(

                                    "{platoon}", tempSubItem.getString("name")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("KICKEDPLATOON")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("KICKEDPLATOON")
                            .getJSONObject("platoon");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_platoon_kick)
                            .replace(

                                    "{platoon}", tempSubItem.getString("name")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("CREATEDPLATOON")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("CREATEDPLATOON")
                            .getJSONObject("platoon");

                    // Set it!
                    itemTitle = context.getString(
                            R.string.info_p_platoon_create).replace(

                            "{platoon}", tempSubItem.getString("name")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("PLATOONBADGESAVED")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("PLATOONBADGESAVED")
                            .getJSONObject("platoon");

                    // Set it!
                    itemTitle = context
                            .getString(R.string.info_p_platoon_badge).replace(

                                    "{platoon}", tempSubItem.getString("name")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("LEFTPLATOON")) {

                    // Get it!
                    tempSubItem = currItem.getJSONObject("LEFTPLATOON")
                            .getJSONObject("platoon");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_platoon_left)
                            .replace(

                                    "{platoon}", tempSubItem.getString("name")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("RECEIVEDPLATOONWALLPOST")) {

                    // Get it!
                    tempSubItem = currItem
                            .getJSONObject("RECEIVEDPLATOONWALLPOST");

                    // Set it!
                    itemTitle = context.getString(R.string.info_p_platoon_feed)
                            .replace(

                                    "{platoon}",
                                    tempSubItem.getJSONObject("platoon")
                                            .getString("name")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle,
                            tempSubItem.getString("wallBody"),
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("LEVELCOMPLETE")) {

                    // Get em!
                    tempSubItem = currItem.getJSONObject("LEVELCOMPLETE");
                    JSONObject friendObject = tempSubItem
                            .getJSONObject("friend");

                    // Set it!
                    itemTitle = context
                            .getString(R.string.info_p_coop_level_comp)
                            .replace(

                                    "{level}",
                                    DataBank.getCoopLevelTitle(tempSubItem
                                            .getString("level"))

                            ).replace(

                                    "{difficulty}", tempSubItem.getString("difficulty")

                            );

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ),
                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            friendObject.getString("username"),
                                            new PersonaData[] {},
                                            friendObject.getString("gravatarMd5")

                                    )

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("RECEIVEDAWARD")) {

                    // Get it!
                    JSONArray tempStatsArray = currItem.optJSONObject(
                            "RECEIVEDAWARD").optJSONArray("statItems");

                    // Set it!
                    if (tempStatsArray.length() > 1) {

                        itemTitle = context.getString(R.string.info_p_awards);

                    } else {

                        itemTitle = context.getString(R.string.info_p_award);

                    }

                    for (int statsCounter = 0, maxCounter = tempStatsArray
                            .length(); statsCounter < maxCounter; statsCounter++) {

                        // Let's get the item
                        tempSubItem = tempStatsArray
                                .optJSONObject(statsCounter);
                        String tempKey = tempSubItem.getString("langKeyTitle");
                        if (itemContent.equals("")) {
                            itemContent = "<b>";
                        }

                        // Do we need to append anything?
                        if (statsCounter > 0) {

                            if (statsCounter == (maxCounter - 1)) {

                                itemContent += " </b>and<b> ";

                            } else {

                                itemContent += ", ";

                            }

                        }

                        // Weapon? Attachment?
                        itemContent += DataBank.getAwardTitle(tempKey);

                    }

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle.replace("{award}",
                                    itemContent + "</b>"), "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("RECEIVEDWALLPOST")) {

                    // Get it!
                    tempSubItem = currItem.optJSONObject("RECEIVEDWALLPOST");

                    // Set it!
                    itemTitle = "<b>{username1} » {username2}:</b> {message}"
                            .replace(

                                    "{message}", tempSubItem.getString("wallBody")

                            );

                    // Let's get it!
                    otherUserObject = tempSubItem.getJSONObject("writerUser");
                    tempGravatarHash = otherUserObject.getString("gravatarMd5");

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            otherUserObject.getString("username"),
                                            new PersonaData[] {},
                                            otherUserObject.getString("gravatarMd5")

                                    ),
                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    )

                            }, liked, censored, tempGravatarHash

                            );

                } else if (!currItem.isNull("GAMEACCESS")) {

                    // Get it!
                    tempSubItem = currItem.optJSONObject("GAMEACCESS");

                    // Set it!
                    itemTitle = "<b>{username} now has access to <b>{title}</b> for <b>Battlefield 3</b>. "
                            .replace(

                                    "{title}", DataBank.getExpansionTitle(tempSubItem
                                            .getString("expansion"))

                            );

                    // Let's get it!
                    tempGravatarHash = ownerObject.getString("gravatarMd5");

                    // Temporary storage
                    tempFeedItem = new FeedItem(

                            Long.parseLong(currItem.getString("id")),
                            Long.parseLong(currItem.getString("itemId")),
                            currItem.getLong("creationDate"), numLikes,
                            numComments, itemTitle, "",
                            currItem.getString("event"), new ProfileData[] {

                                    new ProfileData(
                                            Long.parseLong(currItem.getString("ownerId")),
                                            ownerObject.getString("username"),
                                            new PersonaData[] {},
                                            ownerObject.getString("gravatarMd5")

                                    ), null

                            }, liked, censored, tempGravatarHash

                            );

                } else {

                    Log.d(Constants.DEBUG_TAG,
                            "event => " + currItem.getString("event"));
                    tempFeedItem = null;

                }

                // Append it to the array
                if (tempFeedItem != null) {
                    feedItemArray.add(tempFeedItem);
                }

                // Fix a filename
                String filename = tempGravatarHash + ".png";

                // Before I forget - let's download the gravatar too!
                if (!CacheHandler.isCached(context, filename)) {

                    WebsiteHandler.cacheGravatar(context, filename,
                            Constants.DEFAULT_AVATAR_SIZE);

                }

            }

            return (ArrayList<FeedItem>) feedItemArray;

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

    public static boolean doHooahInFeed(long postId, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.post(

                    Constants.URL_HOOAH.replace("{POST_ID}", postId + ""),
                    new PostData[] {

                        new PostData(

                                Constants.FIELD_NAMES_CHECKSUM[0], checksum

                        )
                    }, 1

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                return true;

            } else {

                throw new WebsiteHandlerException(
                        "Could not hooah the message.");

            }

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean unHooahInFeed(long postId, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent;

            // Get the content
            httpContent = wh.post(

                    Constants.URL_UNHOOAH.replace("{POST_ID}", postId + ""),
                    new PostData[] {

                        new PostData(

                                Constants.FIELD_NAMES_CHECKSUM[0], checksum

                        )
                    }, 1

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                return true;

            } else {

                throw new WebsiteHandlerException(
                        "Could not un-hooah the message.");

            }

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

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

    public static boolean sendFriendRequest(long profileId, String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

                    Constants.URL_FRIEND_REQUESTS.replace(

                            "{UID}", profileId + ""

                            ), new PostData[] {

                        new PostData(

                                Constants.FIELD_NAMES_CHECKSUM[0], checksum

                        )
                    }, 1

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

    public static boolean removeFriend(long profileId)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.get(

                    Constants.URL_FRIEND_DELETE.replace("{UID}", profileId + ""), 1

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

    public static boolean updateStatus(String content, String checksum) {

        try {

            RequestHandler rh = new RequestHandler();
            String httpContent = rh.post(

                    Constants.URL_STATUS_SEND,
                    new PostData[] {

                            new PostData(

                                    Constants.FIELD_NAMES_STATUS[0],
                                    content
                            ),
                            new PostData(

                                    Constants.FIELD_NAMES_STATUS[1],
                                    checksum
                            )

                    },
                    0

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

    public static boolean postToWall(long profileId, String checksum,
            String content, boolean isPlatoon) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

                    Constants.URL_FEED_POST, new PostData[] {

                            new PostData(

                                    Constants.FIELD_NAMES_FEED_POST[0], content

                            ),
                            new PostData(

                                    Constants.FIELD_NAMES_FEED_POST[1], checksum

                            ),
                            new PostData(

                                    Constants.FIELD_NAMES_FEED_POST[(!isPlatoon ? 2 : 3)],
                                    profileId + ""

                            )
                    }, 2

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Check the JSON
                String status = new JSONObject(httpContent).optString(
                        "message", "");
                if (status.equals("WALL_POST_CREATED")
                        || status.equals("PLATOONWALL_POST_CREATED")) {

                    return true;

                } else {

                    return false;

                }

            } else {

                return false;

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean alterPlatoonMembership(final long userId,
            final long platoonId, final int filter)
            throws WebsiteHandlerException {

        try {

            // Init!
            String url = "";

            // Let's see filter we have here
            if (filter == 0) {
                url = Constants.URL_PLATOON_PROMOTE.replace("{UID}",
                        userId + "").replace("{PLATOON_ID}", platoonId + "");
            } else if (filter == 1) {
                url = Constants.URL_PLATOON_DEMOTE
                        .replace("{UID}", userId + "").replace("{PLATOON_ID}",
                                platoonId + "");
            } else if (filter == 2) {
                url = Constants.URL_PLATOON_KICK.replace("{UID}", userId + "")
                        .replace("{PLATOON_ID}", platoonId + "");
            }

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.get(

                    url, 2

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Check the JSON
                String status = new JSONObject(httpContent).optString(
                        "message", "");
                if (status.equals("USER_PROMOTED")
                        || status.equals("USER_DEMOTED")
                        || status.equals("MEMBER_KICKED")) {

                    return true;

                } else {

                    return false;

                }

            } else {

                return false;

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static int sendPlatoonInvite(final long[] userId,
            final long platoonId, final String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler rh = new RequestHandler();
            int numUsers = userId.length;
            List<PostData> postData = new ArrayList<PostData>();

            // Set the first two fields
            postData.add(new PostData(Constants.FIELD_NAMES_PLATOON_INVITE[0],
                    platoonId + ""));
            postData.add(new PostData(Constants.FIELD_NAMES_PLATOON_INVITE[1],
                    checksum));

            // We need to construct the array
            for (int i = 2, max = (numUsers + 2); i < max; i++) {

                if (userId[i - 2] == 0) {
                    continue;
                }
                postData.add(new PostData(
                        Constants.FIELD_NAMES_PLATOON_INVITE[2], userId[i - 2]
                                + ""));

            }

            // Get the content
            String httpContent = rh.post(Constants.URL_PLATOON_INVITE,
                    postData, 2);

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Check the JSON
                JSONObject baseObject = new JSONObject(httpContent);
                JSONArray errors = (baseObject.isNull("errors")) ? null
                        : baseObject.optJSONArray("errors");
                int numErrors = (errors == null) ? 0 : errors.length();

                // Let's see what we got
                if (numErrors == 0) {

                    return 0;

                } else if (numErrors == numUsers) {

                    return 1;

                } else if (numErrors < numUsers) {

                    return 2;

                } else {

                    return -1;

                }

            } else {

                return -1;

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static String downloadZip(Context context, String url, String title)
            throws WebsiteHandlerException {

        try {

            // Attributes
            RequestHandler rh = new RequestHandler();

            // Get the *content*
            if (!rh.saveFileFromURI(Constants.URL_IMAGE_PACK, "",
                    "imagepack-001.zip")) {

                return "<this is supposed to be the path to the zip>";

            } else {

                throw new WebsiteHandlerException("No zip found.");

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static Bitmap downloadGravatarToBitmap(String hash, int size)
            throws WebsiteHandlerException {

        try {

            // Any size requirements? Otherwise we just pick the standard number
            return new RequestHandler().getImageFromStream(

                    Constants.URL_GRAVATAR.replace(

                            "{hash}", hash

                            ).replace(

                                    "{size}", ((size > 0) ? size : Constants.DEFAULT_AVATAR_SIZE

                                            ) + ""

                            ).replace(

                                    "{default}", Constants.DEFAULT_AVATAR_SIZE + ""

                            ), true

                    );

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean cacheGravatar(Context c, String h, int s) {

        try {

            // Let's set it up
            RequestHandler rh = new RequestHandler();

            // Get the external cache dir
            String cacheDir = PublicUtils.getCachePath(c);

            // How does it end?
            if (!cacheDir.endsWith("/")) {
                cacheDir += "/";
            }

            // Get the actual stream
            HttpEntity httpEntity = rh.getHttpEntity(

                    Constants.URL_GRAVATAR.replace("{hash}", h)
                            .replace("{size}", s + "")
                            .replace("{default}", s + ""), false

                    );

            // Init
            int bytesRead = 0;
            int offset = 0;
            int contentLength = (int) httpEntity.getContentLength();
            byte[] data = new byte[contentLength];

            // Build a path
            String filepath = cacheDir + h;

            // Handle the streams
            InputStream imageStream = httpEntity.getContent();
            BufferedInputStream in = new BufferedInputStream(imageStream);

            // Iterate
            while (offset < contentLength) {

                bytesRead = in.read(data, offset, data.length - offset);
                if (bytesRead == -1) {
                    break;
                }
                offset += bytesRead;

            }

            // Alright?
            if (offset != contentLength) {

                throw new IOException("Only read " + offset
                        + " bytes; Expected " + contentLength + " bytes");

            }

            // Close the stream
            in.close();
            FileOutputStream out = new FileOutputStream(filepath);
            out.write(data);
            out.flush();
            out.close();

            return true;

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    public static boolean cacheBadge(Context c, String h, String fName, int s) {

        try {

            // Let's set it up
            RequestHandler rh = new RequestHandler();

            // Get the external cache dir
            String cacheDir = PublicUtils.getCachePath(c);

            // How does it end?
            if (!cacheDir.endsWith("/")) {
                cacheDir += "/";
            }

            // Get the actual stream
            HttpEntity httpEntity = rh.getHttpEntity(

                    Constants.URL_PLATOON_IMAGE.replace("{BADGE_PATH}", h), true

                    );

            // Init
            int bytesRead = 0;
            int offset = 0;
            int contentLength = (int) httpEntity.getContentLength();
            byte[] data = new byte[contentLength];

            // Handle the streams
            InputStream imageStream = httpEntity.getContent();
            BufferedInputStream in = new BufferedInputStream(imageStream);

            // Build a path
            String filepath = cacheDir + fName;

            // Iterate
            while (offset < contentLength) {

                bytesRead = in.read(data, offset, data.length - offset);
                if (bytesRead == -1) {
                    break;
                }
                offset += bytesRead;

            }

            // Alright?
            if (offset != contentLength) {

                throw new IOException("Only read " + offset
                        + " bytes; Expected " + contentLength + " bytes");

            }

            // Close the in-stream, start the outbound
            in.close();
            FileOutputStream out = new FileOutputStream(filepath);
            out.write(data);
            out.flush();
            out.close();

            return true;

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    public static Object[] getAllForums(String locale)
            throws WebsiteHandlerException {

        try {

            // Init to win it
            List<ForumData> forums = new ArrayList<ForumData>();
            String title = "";

            // Setup a RequestHandler
            RequestHandler rh = new RequestHandler();
            final String httpContent = rh.get(

                    Constants.URL_FORUM_LIST_LOCALIZED.replace("{LOCALE}", locale), 1

                    );

            // Let's parse it!
            JSONArray categoryArray = new JSONObject(httpContent)
                    .getJSONObject("context").getJSONArray("categories");
            JSONArray forumArray = categoryArray.getJSONObject(0).optJSONArray(
                    "forums");

            // Get the title
            title = categoryArray.getJSONObject(0).getString("title");

            // Loop
            for (int i = 0, max = forumArray.length(); i < max; i++) {

                // Get the current object
                JSONObject currObject = forumArray.getJSONObject(i);
                JSONObject lastThread = currObject.optJSONObject("lastThread");

                // Let's store them
                if (lastThread != null) {

                    JSONObject userInfo = lastThread
                            .optJSONObject("lastPoster");

                    if (userInfo != null) {

                        forums.add(

                                new ForumData(

                                        Long.parseLong(currObject.getString("id")), Long
                                                .parseLong(currObject.getString("categoryId")),
                                        lastThread.getLong("lastPostDate"), Long
                                                .parseLong(lastThread.getString("id")),
                                        Long.parseLong(lastThread
                                                .getString("lastPostId")), currObject
                                                .getLong("numberOfPosts"), currObject
                                                .getLong("numberOfThreads"), 0,
                                        currObject.getString("title"), currObject
                                                .getString("description"), lastThread
                                                .getString("title"), userInfo
                                                .getString("username")

                                )

                                );

                    }

                } else {

                    forums.add(

                            new ForumData(

                                    Long.parseLong(currObject.getString("id")), Long
                                            .parseLong(currObject.getString("categoryId")), 0,
                                    0, 0, currObject.getLong("numberOfPosts"),
                                    currObject.getLong("numberOfThreads"), 0,
                                    currObject.getString("title"), currObject
                                            .getString("description"), null, null

                            )

                            );

                }

            }

            // Return
            return new Object[] {
                    title, forums
            };

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException("No forums found.");

        }

    }

    public static ForumData getThreadsForForum(String locale, long forumId)
            throws WebsiteHandlerException {

        try {

            // Init to winit
            List<ForumThreadData> threads = new ArrayList<ForumThreadData>();

            // Setup a RequestHandler
            RequestHandler rh = new RequestHandler();
            final String httpContent = rh.get(

                    Constants.URL_FORUM_FORUM.replace("{LOCALE}", locale)
                            .replace("{FORUM_ID}", forumId + "")
                            .replace("{PAGE}", 1 + ""), 1

                    );

            // Let's parse it!
            JSONObject contextObject = new JSONObject(httpContent)
                    .getJSONObject("context");
            JSONObject forumObject = contextObject.getJSONObject("forum");
            JSONArray stickiesArray = contextObject.getJSONArray("stickies");
            JSONArray threadArray = contextObject.getJSONArray("threads");

            // Loop the stickies
            int numStickies = stickiesArray.length();
            for (int i = 0, max = numStickies; i < max; i++) {

                // Yay, we found at least one sticky
                if (i == 0) {
                    threads.add(new ForumThreadData("Stickies"));
                }

                // Get the current object
                JSONObject currObject = stickiesArray.getJSONObject(i);
                JSONObject ownerObject = currObject.getJSONObject("owner");
                JSONObject lastPosterObject = currObject
                        .getJSONObject("lastPoster");

                // Let's store them
                threads.add(

                        new ForumThreadData(

                                Long.parseLong(currObject.getString("id")),
                                forumId,
                                currObject
                                        .getLong("creationDate"), currObject
                                        .getLong("lastPostDate"), currObject
                                        .getInt("numberOfOfficialPosts"), currObject
                                        .getInt("numberOfPosts"),
                                currObject.getString("title"), new ProfileData(
                                        Long
                                                .parseLong(ownerObject.getString("userId")),
                                        ownerObject.getString("username"),
                                        new PersonaData[] {},
                                        ownerObject.getString("gravatarMd5")

                                ), new ProfileData(
                                        Long
                                                .parseLong(lastPosterObject.getString("userId")),
                                        lastPosterObject.getString("username"),
                                        new PersonaData[] {},
                                        lastPosterObject.getString("gravatarMd5")

                                ), currObject.getBoolean("isSticky"), currObject
                                        .getBoolean("isLocked")

                        )

                        );

            }

            // Loop the regular
            for (int i = numStickies, max = threadArray.length(); i < max; i++) {

                if (i == numStickies) {
                    threads.add(new ForumThreadData("Threads"));
                }

                // Get the current object
                JSONObject currObject = threadArray.getJSONObject(i);
                JSONObject ownerObject = currObject.getJSONObject("owner");
                JSONObject lastPosterObject = currObject
                        .getJSONObject("lastPoster");

                // Let's store them
                threads.add(

                        new ForumThreadData(

                                Long.parseLong(currObject.getString("id")),
                                forumId, currObject
                                        .getLong("creationDate"), currObject
                                        .getLong("lastPostDate"), currObject
                                        .getInt("numberOfOfficialPosts"), currObject
                                        .getInt("numberOfPosts"),
                                currObject.getString("title"), new ProfileData(
                                        Long.parseLong(ownerObject.getString("userId")),
                                        ownerObject.getString("username"),
                                        new PersonaData[] {},
                                        ownerObject.getString("gravatarMd5")

                                ), new ProfileData(
                                        Long
                                                .parseLong(lastPosterObject.getString("userId")),
                                        lastPosterObject.getString("username"),
                                        new PersonaData[] {},
                                        lastPosterObject.getString("gravatarMd5")

                                ), currObject.getBoolean("isSticky"), currObject
                                        .getBoolean("isLocked")

                        )

                        );

            }

            return new ForumData(

                    forumObject.getString("title"),
                    forumObject.getString("description"),
                    forumObject.getLong("numberOfPosts"),
                    forumObject.getLong("numberOfThreads"),
                    contextObject.getLong("numPages"), threads

            );

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException("No threads found.");

        }

    }

    public static ForumThreadData getPostsForThread(String locale,
            long threadId) throws WebsiteHandlerException {

        try {

            // Init to winit
            List<ForumPostData> posts = new ArrayList<ForumPostData>();

            // Setup a RequestHandler
            RequestHandler rh = new RequestHandler();
            final String httpContent = rh.get(

                    Constants.URL_FORUM_THREAD.replace("{LOCALE}", locale)
                            .replace("{THREAD_ID}", threadId + "")
                            .replace("{PAGE}", "1"), 1

                    );

            // Let's parse it!
            JSONObject contextObject = new JSONObject(httpContent)
                    .getJSONObject("context");
            JSONArray postArray = contextObject.getJSONArray("posts");
            JSONObject threadObject = contextObject.getJSONObject("thread");
            JSONObject lastPosterObject = threadObject
                    .getJSONObject("lastPoster");
            JSONObject threadOwnerObject = threadObject.getJSONObject("owner");

            // Loop the stickies
            for (int i = 0, max = postArray.length(); i < max; i++) {

                // Get the current object
                JSONObject currObject = postArray.getJSONObject(i);
                JSONObject ownerObject = currObject.getJSONObject("owner");

                // Let's store them
                posts.add(

                        new ForumPostData(

                                Long.parseLong(currObject.getString("id")), Long
                                        .parseLong(currObject.getString("creationDate")), Long
                                        .parseLong(currObject.getString("threadId")),
                                new ProfileData(
                                        Long.parseLong(ownerObject.getString("userId")),
                                        ownerObject.getString("username"),
                                        new PersonaData[] {},
                                        ownerObject.getString("gravatarMd5")

                                ), currObject.getString("formattedBody"), currObject
                                        .getInt("abuseCount"), currObject
                                        .getBoolean("isCensored"), currObject
                                        .getBoolean("isOfficial")

                        )

                        );

            }

            return new ForumThreadData(

                    Long.parseLong(threadObject.getString("id")),
                    Long.parseLong(threadObject.getString("forumId")),
                    threadObject.getLong("creationDate"),
                    threadObject.getLong("lastPostDate"),
                    threadObject.getInt("numberOfOfficialPosts"),
                    threadObject.getInt("numberOfPosts"),
                    contextObject.getInt("currentPage"),
                    contextObject.getInt("numPages"),
                    threadObject.getString("title"), new ProfileData(
                            Long
                                    .parseLong(threadOwnerObject.getString("userId")),
                            threadOwnerObject.getString("username"),
                            new PersonaData[] {},
                            threadOwnerObject.getString("gravatarMd5")

                    ),
                    new ProfileData(
                            Long
                                    .parseLong(lastPosterObject.getString("userId")),
                            lastPosterObject.getString("username"),
                            new PersonaData[] {},
                            lastPosterObject.getString("gravatarMd5")

                    ), threadObject.getBoolean("isSticky"),
                    threadObject.getBoolean("isLocked"),
                    contextObject.getBoolean("canEditPosts"),
                    contextObject.getBoolean("canCensorPosts"),
                    contextObject.getBoolean("canDeletePosts"),
                    contextObject.getBoolean("canPostOfficial"),
                    contextObject.getBoolean("canViewLatestPosts"),
                    contextObject.getBoolean("canViewPostHistory"),
                    contextObject.getBoolean("isAdmin"), posts

            );

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException("No thread data found.");

        }

    }

    public static boolean postReplyInThread(final Context c, final String body,
            final String chksm, final ForumThreadData threadData, final boolean cache,
            final long uid) {

        try {

            // Setup a RequestHandler
            RequestHandler rh = new RequestHandler();

            // POST!
            String httpContent = rh.post(

                    Constants.URL_FORUM_POST.replace("{THREAD_ID}", threadData.getId() + ""),
                    new PostData[] {

                            new PostData(Constants.FIELD_NAMES_FORUM_POST[0],
                                    body),
                            new PostData(Constants.FIELD_NAMES_FORUM_POST[1],
                                    chksm)

                    }, 1

                    );

            // How'd it go?
            if (!"".equals(httpContent)) {

                // Are we to cache it?
                if (cache) {

                    return CacheHandler.Forum.insert(c, threadData, uid) > -1;

                }

                // Return
                return true;

            } else {

                return false;

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    public static boolean createNewThreadInForum(final Context c,
            final String topic, final String body, final String chksm,
            final long fId) {

        try {

            // Setup a RequestHandler
            RequestHandler rh = new RequestHandler();

            // POST!
            String httpContent = rh.post(

                    Constants.URL_FORUM_NEW.replace("{FORUM_ID}", fId + ""),
                    new PostData[] {

                            new PostData(Constants.FIELD_NAMES_FORUM_NEW[0],
                                    topic),
                            new PostData(Constants.FIELD_NAMES_FORUM_NEW[1],
                                    body),
                            new PostData(Constants.FIELD_NAMES_FORUM_NEW[2],
                                    chksm)

                    }, 1

                    );

            // Let's do it
            return (!"".equals(httpContent));

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    public static ArrayList<ForumSearchResult> searchInForums(

            final Context c, final String keyword

            ) throws WebsiteHandlerException {

        // Init
        RequestHandler rh = new RequestHandler();
        List<ForumSearchResult> threads = new ArrayList<ForumSearchResult>();

        try {

            // Let's do the actual search
            String httpContent = rh.get(

                    Constants.URL_FORUM_SEARCH.replace("{SEARCH_STRING}", keyword), 1

                    );
            if (!"".equals(httpContent)) {

                // Let's parse it as JSON
                JSONArray threadArray = new JSONObject(httpContent)
                        .getJSONObject("data").getJSONArray("results");
                int numResults = threadArray.length();

                // Let's see...
                if (numResults > 0) {

                    // Iterate over the results!
                    for (int i = 0; i < numResults; i++) {

                        // Let's get the current result-set
                        JSONObject currentItem = threadArray.getJSONObject(i);

                        threads.add(

                                new ForumSearchResult(

                                        Long.parseLong(currentItem.getString("docid")
                                                .substring(2)),
                                        currentItem
                                                .getLong("timestamp"),
                                        currentItem
                                                .getString("title"),
                                        new ProfileData(
                                                Long
                                                        .parseLong(currentItem.getString("ownerId")),
                                                currentItem.getString("ownerUsername"),
                                                new PersonaData[] {},
                                                null),
                                        currentItem.getBoolean("isSticky"), currentItem
                                                .getBoolean("isOfficial")

                                )

                                );

                    }

                }
            }

            return (ArrayList<ForumSearchResult>) threads;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    //
    public static ArrayList<GeneralSearchResult> searchBattlelog(

            Context context, String keyword, String checksum

            ) throws WebsiteHandlerException {

        // Init
        List<GeneralSearchResult> results = new ArrayList<GeneralSearchResult>();
        RequestHandler rh = new RequestHandler();

        try {

            // Get the content
            String httpContent = rh.post(

                    Constants.URL_PROFILE_SEARCH, new PostData[] {

                            new PostData(Constants.FIELD_NAMES_PROFILE_SEARCH[0],
                                    keyword),
                            new PostData(Constants.FIELD_NAMES_PROFILE_SEARCH[1],
                                    checksum)

                    }, 0

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Generate an object
                JSONArray searchResultsProfile = new JSONObject(httpContent)
                        .getJSONObject("data").getJSONArray("matches");

                // Did we get any results?
                if (searchResultsProfile.length() > 0) {

                    // Iterate over the results
                    for (int i = 0, max = searchResultsProfile.length(); i < max; i++) {

                        // Get the JSONObject
                        JSONObject tempObj = searchResultsProfile
                                .optJSONObject(i);
                        String gravatarHash = tempObj.optString("gravatarMd5",
                                "");

                        // Save it into an array
                        results.add(

                                new GeneralSearchResult(

                                        new ProfileData(
                                                Long.parseLong(tempObj.getString("userId")),
                                                tempObj.getString("username"),
                                                new PersonaData[] {},
                                                tempObj.optString("gravatarMd5")

                                        )

                                )

                                );

                        // Construct the avatar
                        /*
                         * final String filename = gravatar+ ".png";
                         * Log.d(Constants.DEBUG_TAG, filename + " is cached: "
                         * + CacheHandler.isCached( context, filename ) ); //Do
                         * we need to download it? if( !CacheHandler.isCached(
                         * context, filename ) ) { WebsiteHandler.cacheGravatar(
                         * context, filename, Constants.DEFAULT_AVATAR_SIZE ); }
                         */
                    }

                }

            }

            // Get the content
            httpContent = rh.post(

                    Constants.URL_PLATOON_SEARCH, new PostData[] {

                            new PostData(Constants.FIELD_NAMES_PLATOON_SEARCH[0],
                                    keyword),
                            new PostData(Constants.FIELD_NAMES_PLATOON_SEARCH[1],
                                    checksum)

                    }, 3

                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Generate an object
                JSONArray searchResultsPlatoon = new JSONArray(httpContent);

                // Did we get any results?
                if (searchResultsPlatoon.length() > 0) {

                    // Iterate baby!
                    for (int i = 0, max = searchResultsPlatoon.length(); i < max; i++) {

                        // Get the JSONObject
                        JSONObject tempObj = searchResultsPlatoon
                                .optJSONObject(i);
                        final String filename = tempObj.getString("id")
                                + ".jpeg";

                        // Add it to the ArrayList
                        results.add(

                                new GeneralSearchResult(

                                        new PlatoonData(

                                                Long.parseLong(tempObj.getString("id")), tempObj
                                                        .getInt("fanCounter"), tempObj
                                                        .getInt("memberCounter"), tempObj
                                                        .getInt("platform"), tempObj
                                                        .getString("name"),
                                                tempObj.getString("tag"), filename, true

                                        )

                                )

                                );

                        /*
                         * Log.d(Constants.DEBUG_TAG, filename + " is cached: "
                         * + CacheHandler.isCached( context, filename ) ); //Do
                         * we need to download it? if( !CacheHandler.isCached(
                         * context, filename ) ) { WebsiteHandler.cacheBadge(
                         * context, tempObj.getString( "badgePath" ), filename,
                         * Constants.DEFAULT_BADGE_SIZE ); }
                         */

                    }

                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

        // Return the results
        return (ArrayList<GeneralSearchResult>) results;

    }

    public static ArrayList<PlatoonData> getPlatoonsForUser(
            final Context context, final String username)
            throws WebsiteHandlerException {

        // Inir
        RequestHandler rh = new RequestHandler();
        List<PlatoonData> platoons = new ArrayList<PlatoonData>();

        try {

            // Get the content
            String httpContent = rh.get(
                    Constants.URL_PROFILE_INFO.replace("{UNAME}", username), 1);

            // Is it ok?
            if (!"".equals(httpContent)) {

                // JSON
                JSONArray platoonArray = new JSONObject(httpContent)
                        .getJSONObject("context")
                        .getJSONObject("profileCommon")
                        .getJSONArray("platoons");

                // Validate the platoons
                if (platoonArray != null && platoonArray.length() > 0) {

                    // Iterate!!
                    for (int i = 0, max = platoonArray.length(); i < max; i++) {

                        // Get the current item
                        JSONObject currItem = platoonArray.getJSONObject(i);

                        // Let's cache the gravatar
                        String title = currItem.getString("id") + ".jpeg";

                        // Is it cached?
                        if (!CacheHandler.isCached(context, title)) {

                            WebsiteHandler.cacheBadge(

                                    context, currItem.getString("badgePath"), title,
                                    Constants.DEFAULT_BADGE_SIZE

                                    );

                        }

                        // Add to the ArrayList
                        platoons.add(

                                new PlatoonData(

                                        Long.parseLong(currItem.getString("id")), currItem
                                                .getInt("fanCounter"), currItem
                                                .getInt("memberCounter"), currItem
                                                .getInt("platform"),
                                        currItem.getString("name"), currItem
                                                .getString("tag"), title, !currItem
                                                .getBoolean("hidden")

                                )

                                );
                    }

                }

                return (ArrayList<PlatoonData>) platoons;

            } else {

                return null;

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static FeedItem getPostForNotification(NotificationData n) {

        // Init
        RequestHandler rh = new RequestHandler();

        try {

            // Get the data
            String httpContent = rh.get(
                    Constants.URL_FEED_SINGLE.replace("{POST_ID}",
                            n.getItemId() + ""), 1);

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

                    Log.d(Constants.DEBUG_TAG, "matcherTitle.group() => "
                            + matcherTitle.group());
                    username[1] = matcherTitle.group(1);
                    title = "<b>" + matcherTitle.group(2) + "</b>";
                    title += matcherTitle.group(3);
                    Log.d(Constants.DEBUG_TAG, "title => " + title);
                    break;
                }
                while (matcherContent.find()) {
                    content = matcherContent.group(1);
                }
                while (matcherDate.find()) {
                    date = Long.parseLong(matcherDate.group(1));
                }

                return new FeedItem(

                        id, id, date, 0, // numLikes
                        0, // numComments
                        title, content, "n/a", // type
                        new ProfileData[] {

                                new ProfileData(

                                        uid, username[0], new PersonaData[] {}, gravatar),
                                new ProfileData(

                                        0, username[1], new PersonaData[] {}, null)

                        }, false, // liked
                        false, // censored
                        gravatar // gravatar

                );

            }

            return null;

        } catch (Exception ex) {

            ex.printStackTrace();
            return null;

        }

    }

    public static List<ForumThreadData> getThreadsForForum(String locale, long forumId,
            int page) throws WebsiteHandlerException {

        try {

            // Init to winit
            List<ForumThreadData> threads = new ArrayList<ForumThreadData>();

            // Setup a RequestHandler
            RequestHandler rh = new RequestHandler();
            final String httpContent = rh.get(

                    Constants.URL_FORUM_FORUM.replace("{LOCALE}", locale)
                            .replace("{FORUM_ID}", forumId + "")
                            .replace("{PAGE}", page + ""), 1

                    );

            // Let's parse it!
            JSONObject contextObject = new JSONObject(httpContent)
                    .getJSONObject("context");
            JSONArray stickiesArray = contextObject.getJSONArray("stickies");
            JSONArray threadArray = contextObject.getJSONArray("threads");

            // Loop the stickies
            int numStickies = stickiesArray.length();
            for (int i = 0, max = numStickies; i < max; i++) {

                // Yay, we found at least one sticky
                if (i == 0 && page == 1) {
                    threads.add(new ForumThreadData("Stickies"));
                }

                // Get the current object
                JSONObject currObject = stickiesArray.getJSONObject(i);
                JSONObject ownerObject = currObject.getJSONObject("owner");
                JSONObject lastPosterObject = currObject
                        .getJSONObject("lastPoster");

                // Let's store them
                threads.add(

                        new ForumThreadData(

                                Long.parseLong(currObject.getString("id")),
                                forumId, currObject
                                        .getLong("creationDate"), currObject
                                        .getLong("lastPostDate"), currObject
                                        .getInt("numberOfOfficialPosts"), currObject
                                        .getInt("numberOfPosts"),
                                currObject.getString("title"), new ProfileData(
                                        Long.parseLong(ownerObject.getString("userId")),
                                        ownerObject.getString("username"),
                                        new PersonaData[] {},
                                        ownerObject.getString("gravatarMd5")

                                ), new ProfileData(
                                        Long
                                                .parseLong(lastPosterObject.getString("userId")),
                                        lastPosterObject.getString("username"),
                                        new PersonaData[] {},
                                        lastPosterObject.getString("gravatarMd5")

                                ), currObject.getBoolean("isSticky"), currObject
                                        .getBoolean("isLocked")

                        )

                        );

            }

            // Loop the regular
            for (int i = numStickies, max = threadArray.length(); i < max; i++) {

                if (i == numStickies && page == 1) {
                    threads.add(new ForumThreadData("Threads"));
                }

                // Get the current object
                JSONObject currObject = threadArray.getJSONObject(i);
                JSONObject ownerObject = currObject.getJSONObject("owner");
                JSONObject lastPosterObject = currObject
                        .getJSONObject("lastPoster");

                // Let's store them
                threads.add(

                        new ForumThreadData(

                                Long.parseLong(currObject.getString("id")),
                                forumId, currObject
                                        .getLong("creationDate"), currObject
                                        .getLong("lastPostDate"), currObject
                                        .getInt("numberOfOfficialPosts"), currObject
                                        .getInt("numberOfPosts"),
                                currObject.getString("title"), new ProfileData(
                                        Long.parseLong(ownerObject.getString("userId")),
                                        ownerObject.getString("username"),
                                        new PersonaData[] {},
                                        ownerObject.getString("gravatarMd5")

                                ), new ProfileData(
                                        Long
                                                .parseLong(lastPosterObject.getString("userId")),
                                        lastPosterObject.getString("username"),
                                        new PersonaData[] {},
                                        lastPosterObject.getString("gravatarMd5")

                                ), currObject.getBoolean("isSticky"), currObject
                                        .getBoolean("isLocked")

                        )

                        );

            }

            return threads;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException("No threads found.");

        }

    }

    public static List<ForumPostData> getPostsForThread(long threadId,
            int page, String locale) throws WebsiteHandlerException {

        try {

            // Init to winit
            List<ForumPostData> posts = new ArrayList<ForumPostData>();

            // Setup a RequestHandler
            RequestHandler rh = new RequestHandler();
            final String httpContent = rh.get(

                    Constants.URL_FORUM_THREAD.replace("{LOCALE}", locale)
                            .replace("{THREAD_ID}", threadId + "")
                            .replace("{PAGE}", page + ""), 1

                    );
            Log.d(Constants.DEBUG_TAG,
                    "The link => " + Constants.URL_FORUM_THREAD.replace("{LOCALE}", locale)
                            .replace("{THREAD_ID}", threadId + "")
                            .replace("{PAGE}", page + ""));
            // Let's parse it!
            JSONArray postArray = new JSONObject(httpContent).getJSONObject(
                    "context").getJSONArray("posts");

            // Loop the stickies
            for (int i = 0, max = postArray.length(); i < max; i++) {

                // Get the current object
                JSONObject currObject = postArray.getJSONObject(i);
                JSONObject ownerObject = currObject.getJSONObject("owner");

                // Let's store them
                posts.add(

                        new ForumPostData(

                                Long.parseLong(currObject.getString("id")), Long
                                        .parseLong(currObject.getString("creationDate")), Long
                                        .parseLong(currObject.getString("threadId")),
                                new ProfileData(
                                        Long.parseLong(ownerObject.getString("userId")),
                                        ownerObject.getString("username"),
                                        new PersonaData[] {},
                                        ownerObject.getString("gravatarMd5")

                                ), currObject.getString("formattedBody"), currObject
                                        .getInt("abuseCount"), currObject
                                        .getBoolean("isCensored"), currObject
                                        .getBoolean("isOfficial")

                        )

                        );

            }

            return (ArrayList<ForumPostData>) posts;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException("No posts found for the thread.");

        }

    }

    public static boolean reportPostInThread(Context c, long pId, String r)
            throws WebsiteHandlerException {

        try {
            // Setup a RequestHandler
            RequestHandler rh = new RequestHandler();
            final String httpContent = rh.post(

                    Constants.URL_FORUM_REPORT.replace("{POST_ID}", pId + ""),
                    new PostData[] {

                            new PostData(

                                    Constants.FIELD_NAMES_FORUM_REPORT[0], r

                            ), new PostData(

                                    Constants.FIELD_NAMES_FORUM_REPORT[1], ""

                            )

                    }, 1

                    );

            // Let's parse it!
            int status = new JSONObject(httpContent).getJSONObject("data")
                    .getInt("success");
            return (status == 1);

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException("Could not report the post.");

        }

    }

    public static List<WeaponStats> getWeaponStatisticsForPersona(long profileId,
            int platformId) { /* TODO: WORK IN PROGRESS */

        try {

            // Init
            RequestHandler rh = new RequestHandler();
            List<WeaponStats> weaponStatsArray = new ArrayList<WeaponStats>();

            // Get the data
            String httpContent = rh.get(

                    Constants.URL_STATS_WEAPONS.replace("{PID}", profileId + "").replace(
                            "{PLATFORM_ID}", platformId + ""),
                    0

                    );

            // So... how'd it go?
            if (httpContent != null && !httpContent.equals("")) {

                // Woo, we got results
                JSONObject baseObject = new JSONObject(httpContent).getJSONObject("data");
                JSONArray weaponStats = baseObject.getJSONArray("mainWeaponStats");

                // Let's iterate over the JSONArray
                for (int count = 0, maxCount = weaponStats.length(); count < maxCount; count++) {

                    // Get the current item
                    JSONObject currentItem = weaponStats.getJSONObject(count);

                    // Store it
                    weaponStatsArray.add(

                            new WeaponStats(

                                    currentItem.getString("name"),
                                    currentItem.getString("guid"),
                                    currentItem.getString("slug"),
                                    currentItem.getInt("kills"),
                                    currentItem.getInt("headshots"),
                                    currentItem.getInt("kitId"),
                                    currentItem.getLong("shotsFired"),
                                    currentItem.getLong("shotsHit"),
                                    currentItem.getLong("timeEquipped"),
                                    currentItem.getDouble("accuracy"),
                                    currentItem.getDouble("serviceStars"),
                                    currentItem.getDouble("serviceStarProgress")

                            )

                            );

                }

            }

            // Return it!
            return weaponStatsArray;

        } catch (RequestHandlerException ex) {

            ex.printStackTrace();
            return null;

        } catch (Exception ex) {

            ex.printStackTrace();
            return null;

        }

    }

    public static List<NewsData> getNewsForPage(int p) throws WebsiteHandlerException {

        try {

            // Init!
            RequestHandler rh = new RequestHandler();
            List<NewsData> news = new ArrayList<NewsData>();

            // Iterate!
            for (int i = 0, max = 2; i < max; i++) {

                // Let's see
                int num = (10 * p);
                if (i == 1) {

                    num += 5;

                }

                // Get the data
                String httpContent = rh.get(Constants.URL_NEWS.replace("{COUNT}", num + ""), 1);

                // Did we get something?
                if (httpContent != null && !httpContent.equals("")) {

                    // JSON!
                    JSONArray baseArray = new JSONObject(httpContent).getJSONObject("context")
                            .getJSONArray("blogPosts");

                    // Iterate
                    for (int count = 0, maxCount = baseArray.length(); count < maxCount; count++) {

                        // Get the current item
                        JSONObject item = baseArray.getJSONObject(count);
                        JSONObject user = item.getJSONObject("user");

                        // Handle the data
                        news.add(

                                new NewsData(

                                        Long.parseLong(item.getString("id")),
                                        item.getLong("creationDate"),
                                        item.getInt("devblogCommentCount"),
                                        item.getString("title"),
                                        item.getString("body"),
                                        new ProfileData(

                                                Long.parseLong(user.getString("userId")),
                                                user.getString("username"),
                                                new PersonaData[] {},
                                                user.getString("gravatarMd5")

                                        )

                                )

                                );

                    }

                }

            }

            return news;

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());
        }

    }

    public static boolean createNewPlatoon(String... params) {

        try {

            // Get the # of params
            int max = params.length;
            PostData[] postDataArray = new PostData[max];

            // Let's iterate over the params and create postdata
            for (int count = 0; count < max; count++) {

                // Set the value
                postDataArray[count] = new PostData(Constants.FIELD_NAMES_PLATOON_NEW[count],
                        params[count]);

            }

            // Let's do the actual request
            RequestHandler rh = new RequestHandler();
            String httpContent = rh.post(Constants.URL_PLATOON_NEW, postDataArray, 1);

            // Is the httpContent !null?
            if (httpContent != null && !httpContent.equals("")) {

                // Let's validate further...
                return !(new JSONObject(httpContent).optJSONObject("data").isNull("platoon"));

            }

            return false;

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

}
