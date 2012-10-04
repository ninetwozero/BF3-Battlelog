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

package com.ninetwozero.battlelog.http;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.AssignmentComparator;
import com.ninetwozero.battlelog.datatype.AssignmentData;
import com.ninetwozero.battlelog.datatype.AssignmentDataWrapper;
import com.ninetwozero.battlelog.datatype.GeneralSearchResult;
import com.ninetwozero.battlelog.datatype.PersonaData;
import com.ninetwozero.battlelog.datatype.PersonaStats;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.ProfileInformation;
import com.ninetwozero.battlelog.datatype.RequestHandlerException;
import com.ninetwozero.battlelog.datatype.UnlockComparator;
import com.ninetwozero.battlelog.datatype.UnlockData;
import com.ninetwozero.battlelog.datatype.UnlockDataWrapper;
import com.ninetwozero.battlelog.datatype.WeaponDataWrapper;
import com.ninetwozero.battlelog.datatype.WeaponDataWrapperComparator;
import com.ninetwozero.battlelog.datatype.WeaponInfo;
import com.ninetwozero.battlelog.datatype.WeaponStats;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class ProfileClient extends DefaultClient {

    // Attributes
    private ProfileData mProfileData;

    // URLS
    public static final String URL_INFO = Constants.URL_MAIN + "user/{UNAME}/";
    public static final String URL_SETTINGS = Constants.URL_MAIN + "profile/edit/";
    public static final String URL_SETTINGS_EDIT = Constants.URL_MAIN + "profile/update/";
    public static final String URL_OVERVIEW = Constants.URL_MAIN
            + "overviewPopulateStats/{PID}/None/{PLATFORM_ID}/";
    public static final String URL_WEAPONS = Constants.URL_MAIN
            + "weaponsPopulateStats/{PID}/{PLATFORM_ID}/";
    public static final String URL_WEAPONS_INFO = Constants.URL_MAIN
            + "statsitemPopulateStats/iteminfo/{PNAME}/{PID}/{SLUG}/{PLATFORM_ID}/";
    public static final String URL_VEHICLES = Constants.URL_MAIN
            + "vehiclesPopulateStats/{PID}/{PLATFORM_ID}/";
    public static final String URL_AWARDS = Constants.URL_MAIN
            + "awardsPopulateStats/{PID}/{PLATFORM_ID}/";
    public static final String URL_UNLOCKS = Constants.URL_MAIN
            + "upcomingUnlocksPopulateStats/{PID}/{PLATFORM_ID}/";
    public static final String URL_DOGTAGS = Constants.URL_MAIN
            + "soldier/dogtagsPopulateStats/{PID}/{UID}/{PLATFORM_ID}/";
    public static final String URL_ASSIGNMENTS = Constants.URL_MAIN
            + "soldier/missionsPopulateStats/{PNAME}/{PID}/{UID}/{PLATFORM_ID}/";
    public static final String URL_ALL = Constants.URL_MAIN
            + "indexstats/{PID}/{PLATFORM_NAME}/";
    public static final String URL_SEARCH = Constants.URL_MAIN
            + "search/getMatches/";
    public static final String URL_PROFILE = Constants.URL_MAIN
            + "user/overviewBoxStats/{UID}/";

    // Constants
    public static final String[] FIELD_NAMES_SETTINGS = new String[] {

            "profile-edit-gravatar", "profile-edit-personaId[]", "profile-edit-picture[]",
            "profile-edit-clantag[]", "profile-edit-clantag-games[]", "profile-edit-name",
            "profile-edit-presentation", "profile-edit-birthyear", "profile-edit-birthmonth",
            "profile-edit-birthday", "profile-edit-location", "profile-edit-dateformat",
            "profile-edit-ampm", "profile-edit-utcoffset", "profile-edit-hidedetails",
            "profile-edit-inactivatefeed", "profile-edit-allowfriendrequests", "post-check-sum"

    };

    public static final String[] FIELD_NAMES_SEARCH = new String[] {
            "username", "post-check-sum"
    };

    public ProfileClient(ProfileData pd) {

        mRequestHandler = new RequestHandler();
        mProfileData = pd;

    }

    public static ProfileData getProfileIdFromName(final String keyword, final String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            ProfileData profile = null;
            String httpContent = new RequestHandler().post(

                    URL_SEARCH,
                    RequestHandler.generatePostData(

                            FIELD_NAMES_SEARCH,
                            keyword,
                            checksum

                            ),
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            if ("".equals(httpContent)) {

                throw new WebsiteHandlerException(
                        "Could not retrieve the ProfileIDs.");

            } else {

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

                            profile = new ProfileData.Builder(
                                    Long.parseLong(tempObj.getString("userId")),
                                    tempObj.getString("username")
                                    ).gravatarHash(tempObj.optString("gravatarMd5", "")).build();

                            break;

                        }

                        // Grab the "cost"
                        costCurrent = PublicUtils.getLevenshteinDistance(
                                keyword, tempObj.getString("username"));

                        // Somewhat of a match? Get the "best" one!
                        if (costOld > costCurrent) {

                            profile = new ProfileData.Builder(
                                    Long.parseLong(tempObj.getString("userId")),
                                    tempObj.getString("username")
                                    ).gravatarHash(tempObj.optString("gravatarMd5", "")).build();

                        }

                        // Shuffle!
                        costOld = costCurrent;

                    }

                    return resolveFullProfileDataFromProfileData(profile);

                }

                return null; /* TODO: <--- remove */

            }

        } catch (JSONException e) {

            throw new WebsiteHandlerException(e.getMessage());

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static ProfileData resolveFullProfileFromPersonaId(final long personaId)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            String httpContent = new RequestHandler().get(
                    RequestHandler.generateUrl(URL_OVERVIEW, personaId, 0),
                    RequestHandler.HEADER_NORMAL);

            // Did we manage?
            if ("".equals(httpContent)) {

                throw new WebsiteHandlerException(
                        "Could not retrieve the Profile.");

            } else {

                // Grab the array
                JSONObject user = new JSONObject(httpContent).getJSONObject(
                        "data").getJSONObject("user");

                return new ProfileData.Builder(
                        Long.parseLong(user.getString("userId")),
                        user.getString("username")).gravatarHash(user.optString("gravatarMd5", ""))
                        .build();

            }

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static ProfileData resolveFullProfileDataFromProfileId(final long profileId)
            throws WebsiteHandlerException {

        try {

            // Let's get the profile everybody
            String httpContent = new RequestHandler().get(

                    RequestHandler.generateUrl(URL_PROFILE, profileId),
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            if ("".equals(httpContent)) {

                throw new WebsiteHandlerException("Could not retrieve the PersonaID.");

            } else {

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

                return new ProfileData.Builder(profileId, personaArray[0].getName()).persona(
                        personaArray).build();

            }

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static ProfileData resolveFullProfileDataFromProfileData(final ProfileData p)
            throws WebsiteHandlerException {

        try {
        	Log.d(Constants.DEBUG_TAG, "profileDAta => " + p.toString());
            ProfileData profile = resolveFullProfileDataFromProfileId(p.getId());
            return new ProfileData.Builder(p.getId(), p.getUsername()).persona(

                    profile.getPersonaArray()

                    ).gravatarHash(profile.getGravatarHash()).build();

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException("No profile found");

        }
    }

    public PersonaStats getStats(String personaName, long personaId, int platformId)
            throws WebsiteHandlerException {

        try {

            // Get the data
            String content = mRequestHandler.get(

                    RequestHandler.generateUrl(
                            URL_OVERVIEW,
                            personaId,
                            platformId
                            ),
                    RequestHandler.HEADER_NORMAL

                    );

            // JSON Objects
            JSONObject dataObject = new JSONObject(content).getJSONObject("data");

            // Is overviewStats NULL? If so, no data.
            if (dataObject.isNull("overviewStats")) {

                return new PersonaStats(mProfileData.getUsername(), mProfileData.getId(),
                        personaName, personaId, platformId);

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

                    mProfileData.getUsername(), mProfileData.getPersona(0).getName(),
                    currRankInfo.getString("name"),
                    statsOverview.getLong("rank"), mProfileData.getPersona(0).getId(),
                    mProfileData.getId(), mProfileData.getPersona(0).getPlatformId(),
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

    public HashMap<Long, PersonaStats> getStats(final Context context)
            throws WebsiteHandlerException {

        try {

            // Init
            HashMap<Long, PersonaStats> stats = new HashMap<Long, PersonaStats>();

            // Do we have a personaId?
            if (mProfileData.getNumPersonas() == 0) {

                mProfileData = resolveFullProfileDataFromProfileId(mProfileData.getId());

            }

            // Let's see...
            for (int i = 0, max = mProfileData.getNumPersonas(); i < max; i++) {

                PersonaData current = mProfileData.getPersona(i);
                stats.put(current.getId(),
                        getStats(current.getName(), current.getId(), current.getPlatformId()));

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

    public HashMap<Long, UnlockDataWrapper> getUnlocks(final int minCompletion)
            throws WebsiteHandlerException {

        try {

            // Init the ArrayLists
            HashMap<Long, UnlockDataWrapper> unlockDataMap = new HashMap<Long, UnlockDataWrapper>();
            List<UnlockData> weaponArray;
            List<UnlockData> attachmentArray;
            List<UnlockData> kitUnlockArray;
            List<UnlockData> vehicleUpgradeArray;
            List<UnlockData> skillArray;
            List<UnlockData> unlockArray;

            for (int count = 0, maxCount = mProfileData.getNumPersonas(); count < maxCount; count++) {

                weaponArray = new ArrayList<UnlockData>();
                attachmentArray = new ArrayList<UnlockData>();
                kitUnlockArray = new ArrayList<UnlockData>();
                vehicleUpgradeArray = new ArrayList<UnlockData>();
                skillArray = new ArrayList<UnlockData>();
                unlockArray = new ArrayList<UnlockData>();

                // Get the data
                String content = mRequestHandler.get(

                        RequestHandler.generateUrl(
                                URL_UNLOCKS,
                                mProfileData.getPersona(count).getId(),
                                mProfileData.getPersona(count).getPlatformId()
                                ),
                        RequestHandler.HEADER_NORMAL

                        );

                // JSON Objects
                JSONObject dataObject = new JSONObject(content)
                        .getJSONObject("data");
                JSONArray unlockResults = dataObject.optJSONArray("unlocks");

                if (dataObject.isNull("unlocks") || unlockResults.length() == 0) {

                    unlockDataMap.put(mProfileData.getPersona(count).getId(),
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

                unlockDataMap.put(mProfileData.getPersona(count).getId(),
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

    public UnlockData getUnlockDataFromJSON(JSONObject row, double minCompletion)
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

    public ProfileInformation getInformation(Context context, long activeProfileId)
            throws WebsiteHandlerException {

        try {

            // Let's go!

            List<PlatoonData> platoonDataArray = new ArrayList<PlatoonData>();
            Log.d(Constants.DEBUG_TAG, "Information from => " + RequestHandler.generateUrl(
                    URL_INFO,
                    mProfileData.getUsername()
                    ));
            String httpContent = mRequestHandler.get(
                    RequestHandler.generateUrl(
                            URL_INFO,
                            mProfileData.getUsername()
                            ),
                    RequestHandler.HEADER_AJAX
                    );

            // Did we manage?
            if ("".equals(httpContent)) {

                throw new WebsiteHandlerException("Could not get the profile.");

            } else {

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

                        PlatoonClient.cacheBadge(

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

                        userInfo.optInt("age", 0), mProfileData.getId(),
                        userInfo.optLong("birthdate", 0), userInfo.optLong(
                                "lastLogin", 0), statusMessage.optLong(
                                "statusMessageChanged", 0), personaArray, userInfo.optString(
                                "name", "N/A"), username,
                        userInfo.isNull("presentation") ? "" : userInfo
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

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    //
    public static List<GeneralSearchResult> search(Context context, String keyword,
            String checksum)
            throws WebsiteHandlerException {

        // Init
        List<GeneralSearchResult> results = new ArrayList<GeneralSearchResult>();

        try {

            // Get the content
            String httpContent = new RequestHandler().post(

                    URL_SEARCH,
                    RequestHandler.generatePostData(

                            FIELD_NAMES_SEARCH,
                            keyword,
                            checksum

                            ),
                    RequestHandler.HEADER_NORMAL

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
                        JSONObject tempObj = searchResultsProfile.optJSONObject(i);

                        // Save it into an array
                        results.add(

                                new GeneralSearchResult(

                                        new ProfileData.Builder(Long.parseLong(tempObj
                                                .getString("userId")), tempObj
                                                .getString("username")).gravatarHash(
                                                tempObj.optString("gravatarMd5")).build()

                                )

                                );
                    }

                }

            }

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }
        // Return the results
        return (ArrayList<GeneralSearchResult>) results;

    }

    public Map<Long, List<WeaponDataWrapper>> getWeapons() {

        try {

            // Init
            Map<Long, List<WeaponDataWrapper>> weaponDataMap = new HashMap<Long, List<WeaponDataWrapper>>();

            for (int i = 0, max = mProfileData.getNumPersonas(); i < max; i++) {

                // Init the array
                List<WeaponDataWrapper> weaponDataArray = new ArrayList<WeaponDataWrapper>();

                // Get the data
                String httpContent = mRequestHandler.get(

                        RequestHandler.generateUrl(

                                URL_WEAPONS,
                                mProfileData.getPersona(i).getId(),
                                mProfileData.getPersona(i).getPlatformId()
                                ),
                        RequestHandler.HEADER_NORMAL

                        );

                // So... how'd it go?
                if (httpContent != null && !httpContent.equals("")) {

                    // Woo, we got results
                    JSONObject baseObject = new JSONObject(httpContent).getJSONObject("data");
                    JSONObject weaponInfoObject = baseObject.getJSONObject("gadgetsLocale")
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
                weaponDataMap.put(mProfileData.getPersona(i).getId(), weaponDataArray);

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

    public HashMap<Long, WeaponDataWrapper> getWeapon(WeaponInfo weaponInfo, WeaponStats weaponStats) {

        try {

            // Init

            List<UnlockData> unlockArray = new ArrayList<UnlockData>();
            HashMap<Long, WeaponDataWrapper> weaponDataArray = new HashMap<Long, WeaponDataWrapper>();

            // Iterate per persona
            for (int i = 0, max = mProfileData.getNumPersonas(); i < max; i++) {

                // Get the data
                String httpContent = mRequestHandler.get(

                        RequestHandler.generateUrl(

                                URL_WEAPONS_INFO,
                                mProfileData.getPersona(i).getName(),
                                mProfileData.getPersona(i).getId(),
                                weaponStats.getSlug(),
                                mProfileData.getPersona(i).getPlatformId()

                                ),
                        RequestHandler.HEADER_NORMAL

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

                            mProfileData.getPersona(i).getId(),
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

    public HashMap<Long, AssignmentDataWrapper> getAssignments(Context c)
            throws WebsiteHandlerException {

        try {

            // Attributes
            HashMap<Long, AssignmentDataWrapper> assignmentMap = new HashMap<Long, AssignmentDataWrapper>();
            List<AssignmentData> b2kAssignments = new ArrayList<AssignmentData>();
            List<AssignmentData> cqAssignments = new ArrayList<AssignmentData>();
            List<AssignmentData> premiumAssignments = new ArrayList<AssignmentData>();

            // Loop over the personas
            for (int count = 0, maxCount = mProfileData.getNumPersonas(); count < maxCount; count++) {

                // Init
                b2kAssignments = new ArrayList<AssignmentData>();
                cqAssignments = new ArrayList<AssignmentData>();
                premiumAssignments = new ArrayList<AssignmentData>();

                // Get the JSON!
                String httpContent = mRequestHandler.get(

                        RequestHandler.generateUrl(

                                URL_ASSIGNMENTS,
                                mProfileData.getPersona(count).getName(),
                                mProfileData.getPersona(count).getId(),
                                mProfileData.getId(),
                                mProfileData.getPersona(count).getPlatformId()

                                ),
                        RequestHandler.HEADER_AJAX

                        );

                // Parse the JSON!
                JSONObject topLevel = new JSONObject(httpContent).getJSONObject("data");
                if (topLevel.isNull("missionTrees")) {

                    assignmentMap.put(mProfileData.getPersona(count).getId(),
                            new AssignmentDataWrapper());
                    continue;

                }

                // Create the missions
                JSONObject missionTrees = topLevel.getJSONObject("missionTrees");
                JSONArray missionNames = missionTrees.names();

                for (int missionTreeCount = 0, maxMissionTrees = missionNames.length(); missionTreeCount < maxMissionTrees; missionTreeCount++) {

                    int mId = Integer.parseInt(String.valueOf(missionNames.get(missionTreeCount)));
                    JSONObject missionTop = missionTrees.getJSONObject(String.valueOf(mId));
                    JSONObject missions = missionTop.getJSONObject("missions");
                    List<AssignmentData> currentList = getAssignmentsFromJSON(missions);

                    // Init
                    switch (mId) {

                        case 512:
                            b2kAssignments.addAll(currentList);
                            break;

                        case 1024:
                            premiumAssignments.addAll(currentList);
                            break;

                        case 2048:
                            cqAssignments.addAll(currentList);
                            break;

                        default:
                            Toast.makeText(c, R.string.info_assignments_new_unknown,
                                    Toast.LENGTH_SHORT).show();
                            break;

                    }

                }

                // Add the items
                assignmentMap.put(mProfileData.getPersona(count).getId(),
                        new AssignmentDataWrapper(b2kAssignments, premiumAssignments,
                                cqAssignments));

            }

            Log.d(Constants.DEBUG_TAG, "numB2K => " + b2kAssignments.size());
            Log.d(Constants.DEBUG_TAG, "numPremium => " + premiumAssignments.size());
            Log.d(Constants.DEBUG_TAG, "numCQ => " + cqAssignments.size());
            return assignmentMap;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public List<AssignmentData> getAssignmentsFromJSON(JSONObject missions) {

        // Init
        List<AssignmentData> assignments = new ArrayList<AssignmentData>();
        try {

            // Get the JSONObject per loop
            JSONArray jsonArrayKeys = missions.names();
            List<String> stringArrayKeys = new ArrayList<String>();
            for (int i = 0, max = jsonArrayKeys.length(); i < max; i++) {

                stringArrayKeys.add(String.valueOf(jsonArrayKeys.get(i)));

            }

            // Iterate over the label from above
            int assignmentCounter = 0;
            for (String key : stringArrayKeys) {

                JSONObject assignment = missions.getJSONObject(key);
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
                for (int criteriaCounter = 0, criteriaCount = criteriasJSON
                        .length(); criteriaCounter < criteriaCount; criteriaCounter++) {

                    // Get the current item
                    JSONObject currentItem = criteriasJSON
                            .getJSONObject(criteriaCounter);

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
                int[] resources = DataBank.getResourcesForAssignment(key);
                assignments.add(

                        new AssignmentData(

                                resources[0],
                                resources[1],
                                assignment.getString("stringID"),
                                assignment.getString("descriptionID"),
                                assignment.getString("license"),
                                criterias,
                                dependencies,
                                unlocks

                        )

                        );

                assignmentCounter++;

            }

        } catch (JSONException ex) {

            ex.printStackTrace();

        }

        Collections.sort(assignments, new AssignmentComparator());
        return assignments;
    }

    public ArrayList<PlatoonData> getPlatoons(final Context context)
            throws WebsiteHandlerException {

        // Inir
        List<PlatoonData> platoons = new ArrayList<PlatoonData>();

        try {

            // Get the content
            String httpContent = mRequestHandler.get(

                    RequestHandler.generateUrl(ProfileClient.URL_INFO, mProfileData.getUsername()),
                    RequestHandler.HEADER_AJAX

                    );

            // Is it ok?
            if ("".equals(httpContent)) {

                return null;

            } else {

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

                            PlatoonClient.cacheBadge(

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

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean cacheGravatar(Context c, String h, int s) {

        try {

            // Get the external cache dir
            String cacheDir = PublicUtils.getCachePath(c);

            // How does it end?
            if (!cacheDir.endsWith("/")) {
                cacheDir += "/";
            }

            // Get the actual stream
            HttpEntity httpEntity = new RequestHandler().getHttpEntity(

                    RequestHandler.generateUrl(Constants.URL_GRAVATAR, h, s, s), false

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

}
