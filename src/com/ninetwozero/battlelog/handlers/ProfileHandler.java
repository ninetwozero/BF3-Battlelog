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

package com.ninetwozero.battlelog.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ninetwozero.battlelog.datatypes.GeneralSearchResult;
import com.ninetwozero.battlelog.datatypes.NewsData;
import com.ninetwozero.battlelog.datatypes.PersonaData;
import com.ninetwozero.battlelog.datatypes.PersonaStats;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ProfileInformation;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.UnlockComparator;
import com.ninetwozero.battlelog.datatypes.UnlockData;
import com.ninetwozero.battlelog.datatypes.UnlockDataWrapper;
import com.ninetwozero.battlelog.datatypes.WeaponDataWrapper;
import com.ninetwozero.battlelog.datatypes.WeaponDataWrapperComparator;
import com.ninetwozero.battlelog.datatypes.WeaponInfo;
import com.ninetwozero.battlelog.datatypes.WeaponStats;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class ProfileHandler {

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

                    return ProfileHandler.getPersonaIdFromProfile(profile);

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

            ProfileData profile = ProfileHandler.getPersonaIdFromProfile(p
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
            final ProfileData pd, final int minCompletion) throws WebsiteHandlerException {

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
                    UnlockData unlockData = getUnlockDataFromJSON(unlockRow, minCompletion);

                    if (unlockData == null) {
                        continue;

                    }

                    // Send it to the appropriate
                    if (unlockData.getType().equals(UnlockData.CATEGORY_WEAPON)) {

                        weaponArray.add(unlockData);

                    } else if (unlockData.getType().equals(UnlockData.CATEGORY_ATTACHMENT)) {

                        attachmentArray.add(unlockData);

                    } else if (unlockData.getType().equals(UnlockData.CATEGORY_KIT)) {

                        kitUnlockArray.add(unlockData);

                    } else if (unlockData.getType().equals(UnlockData.CATEGORY_VEHICLE)) {

                        vehicleUpgradeArray.add(unlockData);

                    } else if (unlockData.getType().equals(UnlockData.CATEGORY_SKILL)) {

                        skillArray.add(unlockData);

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

    public static UnlockData getUnlockDataFromJSON(JSONObject row, double minCompletion)
            throws WebsiteHandlerException {

        try {

            // Attributes
            final String[] unlockCategories = {
                    UnlockData.CATEGORY_WEAPON,
                    UnlockData.CATEGORY_ATTACHMENT,
                    UnlockData.CATEGORY_VEHICLE,
                    UnlockData.CATEGORY_KIT,
                    UnlockData.CATEGORY_SKILL
            };

            for (String category : unlockCategories) {

                // If the category is null, then we need to loop another one!
                if (row.isNull(category)) {
                    continue;
                }

                // Get the object
                JSONObject detailObject = row.getJSONObject(category);
                JSONObject unlockDetails = detailObject
                        .getJSONObject("unlockedBy");

                // Less than 1.0?
                if (unlockDetails.getDouble("completion") < minCompletion) {
                    continue;
                }

                // Add them to the array
                return new UnlockData(

                        row.optInt("kit", 0),
                        unlockDetails.getDouble("completion"),
                        unlockDetails.optLong("valueNeeded", 0),
                        unlockDetails.optLong("actualValue", 0),
                        row.getString("parentId"),
                        detailObject.getString("unlockId"),
                        unlockDetails.getString("codeNeeded"), category

                );

            }

            return null;

        } catch (Exception ex) {

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
                                    : personaObject.getString("personaName")),
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
/* TODO */
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

    public static Map<Long, List<WeaponDataWrapper>> getWeapons(ProfileData p) {

        try {

            // Init
            RequestHandler rh = new RequestHandler();
            Map<Long, List<WeaponDataWrapper>> weaponDataMap = new HashMap<Long, List<WeaponDataWrapper>>();

            for (int i = 0, max = p.getNumPersonas(); i < max; i++) {

                // Init the array
                List<WeaponDataWrapper> weaponDataArray = new ArrayList<WeaponDataWrapper>();

                // Get the data
                String httpContent = rh.get(

                        Constants.URL_STATS_WEAPONS.replace("{PID}", p.getPersona(i).getId() + "")
                                .replace(
                                        "{PLATFORM_ID}", p.getPersona(i).getPlatformId() + ""),
                        0

                        );

                // So... how'd it go?
                if (httpContent != null && !httpContent.equals("")) {

                    // Woo, we got results
                    JSONObject baseObject = new JSONObject(httpContent).getJSONObject("data");
                    JSONObject weaponInfoObject = baseObject.getJSONObject("bf3GadgetsLocale")
                            .getJSONObject("weapons");
                    JSONArray weaponStats = baseObject.getJSONArray("mainWeaponStats");
                    JSONObject unlockMap = baseObject.getJSONObject("unlocksAdded");

                    // Let's iterate over the JSONArray
                    for (int count = 0, maxCount = weaponStats.length(); count < maxCount; count++) {

                        // Get the current item
                        int numUnlocked = 0;
                        List<UnlockData> unlocks = new ArrayList<UnlockData>();
                        JSONObject currentItem = weaponStats.getJSONObject(count);

                        // Determine the GUID
                        String guid = currentItem.isNull("duplicateOf") ? "guid" : "duplicateOf";

                        // Get the "current weapon information"
                        JSONObject currentWeapon = weaponInfoObject.getJSONObject(currentItem
                                .getString("guid"));

                        // Do we have unlocks for this item?
                        if (!unlockMap.isNull(currentItem.getString(guid))) {

                            // Unlocks
                            JSONArray currentUnlocks = unlockMap.getJSONArray(currentItem
                                    .getString(guid));

                            // Iterate over the unlocks
                            for (int uCount = 0, maxUCount = currentUnlocks.length(); uCount < maxUCount; uCount++) {

                                // Grab the unlocks
                                UnlockData unlockData = getUnlockDataFromJSON(
                                        currentUnlocks.getJSONObject(uCount), 0);
                                if (unlockData == null) {
                                    continue;
                                }

                                // Increment
                                if (unlockData.getScoreCurrent() >= unlockData.getScoreNeeded()) {

                                    numUnlocked++;

                                }

                                unlocks.add(unlockData);

                            }

                        }
                        /* TODO: VALIDATE BURST MODES */
                        // Store it
                        weaponDataArray.add(

                                new WeaponDataWrapper(

                                        numUnlocked,
                                        new WeaponInfo(
                                                currentItem.getString("guid"),
                                                currentItem.getString("name"),
                                                currentItem.getString("slug"),
                                                currentWeapon.optInt("rateOfFire", 1),
                                                WeaponInfo.RANGE_VLONG,
                                                "Marshmallows",
                                                currentWeapon.getBoolean("fireModeAuto"),
                                                currentWeapon.getBoolean("fireModeBurst"),
                                                currentWeapon.getBoolean("fireModeSingle")

                                        ),
                                        new WeaponStats(

                                                currentItem.getString("name"),
                                                currentItem.getString("guid"),
                                                currentItem.getString("slug"),
                                                currentItem.getInt("kills"),
                                                currentItem.getInt("headshots"),
                                                currentItem.optInt("kitId", 999),
                                                currentItem.getLong("shotsFired"),
                                                currentItem.getLong("shotsHit"),
                                                currentItem.getLong("timeEquipped"),
                                                currentItem.getDouble("accuracy"),
                                                currentItem.getDouble("serviceStars"),
                                                currentItem.optDouble("serviceStarProgress", 0.0)

                                        ),
                                        unlocks

                                )

                                );

                    }

                }

                Collections.sort(weaponDataArray, new WeaponDataWrapperComparator());
                weaponDataMap.put(p.getPersona(i).getId(), weaponDataArray);

            }

            // Return it!
            return weaponDataMap;

        } catch (RequestHandlerException ex) {

            ex.printStackTrace();
            return null;

        } catch (Exception ex) {

            ex.printStackTrace();
            return null;

        }

    }

    public static HashMap<Long, WeaponDataWrapper> getWeapon(ProfileData p, WeaponInfo weaponInfo,
            WeaponStats weaponStats) {

        try {

            // Init
            RequestHandler rh = new RequestHandler();
            List<UnlockData> unlockArray = new ArrayList<UnlockData>();
            HashMap<Long, WeaponDataWrapper> weaponDataArray = new HashMap<Long, WeaponDataWrapper>();

            // Iterate per persona
            for (int i = 0, max = p.getNumPersonas(); i < max; i++) {

                // Get the data
                String httpContent = rh.get(

                        Constants.URL_STATS_WEAPONS_INFO.replace(

                                "{PNAME}", p.getPersona(i).getName()

                                ).replace(

                                        "{PID}", p.getPersona(i).getId() + ""

                                ).replace(

                                        "{SLUG}", weaponStats.getSlug()

                                ).replace(

                                        "{PLATFORM_ID}", p.getPersona(i).getPlatformId() + ""

                                ), 0

                        );

                // So... how'd it go?
                if (httpContent != null && !httpContent.equals("")) {

                    // Clear the unlockArray
                    unlockArray = new ArrayList<UnlockData>();

                    // Woo, we got results
                    JSONObject baseObject = new JSONObject(httpContent).getJSONObject("data");
                    JSONArray unlockObjectArray = baseObject.getJSONObject("statsItem")
                            .getJSONObject("itemWeapon")
                            .getJSONArray("unlocks");

                    // Let's iterate over the unlocks
                    for (int count = 0, maxCount = unlockObjectArray.length(); count < maxCount; count++) {

                        // Get the current item
                        JSONObject unlockObject = unlockObjectArray.getJSONObject(count);
                        JSONObject unlockObjectBy = unlockObject.getJSONObject("unlockedBy");

                        // Populate the array
                        unlockArray.add(

                                new UnlockData(

                                        weaponStats.getKitId(),
                                        unlockObjectBy.optDouble("completion", 0),
                                        unlockObjectBy.optLong("valueNeeded", 0),
                                        unlockObjectBy.optLong("actualValue", 0),
                                        weaponStats.getName(),
                                        unlockObject.getString("unlockId"),
                                        unlockObjectBy.getString("codeNeeded"),
                                        UnlockData.CATEGORY_ATTACHMENT

                                )

                                );

                    }

                    // Sort the unlocks
                    Collections.sort(unlockArray, new UnlockComparator());

                    // Add the data array to the WeaponDataWrapper
                    weaponDataArray.put(

                            p.getPersona(i).getId(),
                            new WeaponDataWrapper(

                                    0,
                                    weaponInfo,
                                    weaponStats,
                                    unlockArray

                            )

                            );
                }

            }

            // Return it!
            return weaponDataArray;

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

}
