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

package com.ninetwozero.bf3droid.http;

import android.content.Context;

import com.ninetwozero.bf3droid.datatype.*;
import com.ninetwozero.bf3droid.misc.CacheHandler;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.DataBank;
import com.ninetwozero.bf3droid.misc.PublicUtils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileClient extends DefaultClient {

    // Attributes
    private ProfileData mProfileData;

    // URLS
    public static final String URL_INFO = Constants.URL_MAIN + "user/{UNAME}/";
    public static final String URL_SETTINGS = Constants.URL_MAIN + "profile/edit/";
    public static final String URL_SETTINGS_EDIT = Constants.URL_MAIN + "profile/update/";
    public static final String URL_OVERVIEW = Constants.URL_MAIN + "overviewPopulateStats/{PID}/None/{PLATFORM_ID}/";
    public static final String URL_WEAPONS = Constants.URL_MAIN + "weaponsPopulateStats/{PID}/{PLATFORM_ID}/";
    public static final String URL_WEAPONS_INFO = Constants.URL_MAIN + "statsitemPopulateStats/iteminfo/{PNAME}/{PID}/{SLUG}/{PLATFORM_ID}/";
    public static final String URL_VEHICLES = Constants.URL_MAIN + "vehiclesPopulateStats/{PID}/{PLATFORM_ID}/";
    public static final String URL_AWARDS = Constants.URL_MAIN + "awardsPopulateStats/{PID}/{PLATFORM_ID}/";
    public static final String URL_UNLOCKS = Constants.URL_MAIN + "upcomingUnlocksPopulateStats/{PID}/{PLATFORM_ID}/";
    public static final String URL_DOGTAGS = Constants.URL_MAIN + "soldier/dogtagsPopulateStats/{PID}/{UID}/{PLATFORM_ID}/";
    public static final String URL_ASSIGNMENTS = Constants.URL_MAIN + "soldier/missionsPopulateStats/{PNAME}/{PID}/{UID}/{PLATFORM_ID}/";
    public static final String URL_ALL = Constants.URL_MAIN + "indexstats/{PID}/{PLATFORM_NAME}/";
    public static final String URL_SEARCH = Constants.URL_MAIN + "search/getMatches/";
    public static final String URL_PROFILE = Constants.URL_MAIN + "user/overviewBoxStats/{UID}/";

    // Constants
    public static final String[] FIELD_NAMES_SETTINGS = new String[]{
        "profile-edit-gravatar", "profile-edit-personaId[]",
        "profile-edit-picture[]", "profile-edit-clantag[]",
        "profile-edit-clantag-games[]", "profile-edit-name",
        "profile-edit-presentation", "profile-edit-birthyear",
        "profile-edit-birthmonth", "profile-edit-birthday",
        "profile-edit-location", "profile-edit-dateformat",
        "profile-edit-ampm", "profile-edit-utcoffset",
        "profile-edit-hidedetails", "profile-edit-inactivatefeed",
        "profile-edit-allowfriendrequests", "post-check-sum"
    };

    public static final String[] FIELD_NAMES_SEARCH = new String[]{"name", "post-check-sum"};

    public ProfileClient(ProfileData pd) {
        mRequestHandler = new RequestHandler();
        mProfileData = pd;
    }

    public static ProfileData getProfileIdFromName(final String keyword, final String checksum) throws WebsiteHandlerException {
        try {
            ProfileData profile = null;
            String httpContent = new RequestHandler().post(
                URL_SEARCH, 
                RequestHandler.generatePostData(FIELD_NAMES_SEARCH, keyword, checksum),
                RequestHandler.HEADER_NORMAL
            );
            
            if ("".equals(httpContent)) {
                throw new WebsiteHandlerException("Could not retrieve the ProfileIDs.");
            } else {
                JSONArray searchResults = new JSONObject(httpContent).getJSONObject("data").getJSONArray("matches");
                if (searchResults.length() > 0) {
                    int costOld = 999, costCurrent;
                    for (int i = 0, max = searchResults.length(); i < max; i++) {
                        JSONObject tempObj = searchResults.optJSONObject(i);
                        if (tempObj.getString("username").equals(keyword)) {
                            profile = new ProfileData.Builder(
                        		Long.parseLong(tempObj.getString("userId")),
                                tempObj.getString("username")
                            ).gravatarHash(tempObj.optString("gravatarMd5", "")).build();
                            break;
                        }
                        costCurrent = PublicUtils.getLevenshteinDistance(keyword, tempObj.getString("username"));

                        // Somewhat of a match? Get the "best" one!
                        if (costOld > costCurrent) {
                            profile = new ProfileData.Builder(
                                Long.parseLong(tempObj.getString("userId")),
                                tempObj.getString("username")
                            ).gravatarHash(tempObj.optString("gravatarMd5", "")).build();
                        }
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

    public static ProfileData resolveFullProfileDataFromProfileId(final long profileId) throws WebsiteHandlerException {
        try {
        	String httpContent = new RequestHandler().get(
                RequestHandler.generateUrl(URL_PROFILE, profileId),
                RequestHandler.HEADER_NORMAL
            );

        	if ("".equals(httpContent)) {
                throw new WebsiteHandlerException("Could not retrieve the PersonaID.");
            } else {
                JSONArray soldierBox = new JSONObject(httpContent).getJSONObject("data").getJSONArray("soldiersBox");
                final int numPersonas = soldierBox.length();
                PersonaData[] personaArray = new PersonaData[numPersonas];

                for (int i = 0; i < numPersonas; i++) {
                    JSONObject personaObject = soldierBox.optJSONObject(i).getJSONObject("persona");
                    personaArray[i] = new PersonaData(
                        personaObject.getLong("personaId"),
                        personaObject.getString("personaName"),
                        DataBank.getPlatformIdFromName(personaObject.getString("namespace")),
                        personaObject.getString("picture")
                    );
                }
                return new ProfileData.Builder(
            		profileId, 
            		personaArray[0].getName()
        		).persona(personaArray).build();
            }
        } catch (Exception ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public static ProfileData resolveFullProfileDataFromProfileData(final ProfileData p) throws WebsiteHandlerException {
        try {
            ProfileData profile = resolveFullProfileDataFromProfileId(p.getId());
            return new ProfileData.Builder(
        		p.getId(), 
        		p.getUsername()
    		).persona(profile.getPersonaArray()
            ).gravatarHash(profile.getGravatarHash()).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebsiteHandlerException("No profile found");
        }
    }

    public PersonaStats getStats(String personaName, long personaId, int platformId) throws WebsiteHandlerException {
        try {
            String content = mRequestHandler.get(
                RequestHandler.generateUrl(URL_OVERVIEW, personaId, platformId),
                RequestHandler.HEADER_NORMAL
            );

            JSONObject dataObject = new JSONObject(content).getJSONObject("data");
            if (dataObject.isNull("overviewStats")) {
                return new PersonaStats(
            		mProfileData.getUsername(),
            		mProfileData.getId(), 
            		personaName, 
            		personaId,
                    platformId
                );
            }

            JSONObject statsOverview = dataObject.getJSONObject("overviewStats");
            JSONObject kitScores = statsOverview.getJSONObject("kitScores");
            JSONObject nextRankInfo = dataObject.getJSONObject("rankNeeded");
            JSONObject currRankInfo = dataObject.getJSONObject("currentRankNeeded");

            return new PersonaStats(
                mProfileData.getUsername(), 
                mProfileData.getPersona(0).getName(),
                currRankInfo.getString("name"),
                statsOverview.getLong("rank"), 
                mProfileData.getPersona(0).getId(), 
                mProfileData.getId(), 
                mProfileData.getPersona(0).getPlatformId(),
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

    public HashMap<Long, UnlockDataWrapper> getUnlocks(final int minCompletion) throws WebsiteHandlerException {
        try {
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

                String content = mRequestHandler.get(
                    RequestHandler.generateUrl(
                		URL_UNLOCKS, 
                		mProfileData.getPersona(count).getId(),
                        mProfileData.getPersona(count).getPlatformId()
                    ),
                    RequestHandler.HEADER_NORMAL
                );

                JSONObject dataObject = new JSONObject(content).getJSONObject("data");
                JSONArray unlockResults = dataObject.optJSONArray("unlocks");
                if (dataObject.isNull("unlocks") || unlockResults.length() == 0) {
                    unlockDataMap.put(
                		mProfileData.getPersona(count).getId(),
                        new UnlockDataWrapper(null, null, null, null, null, null)
            		);
                    continue;
                }

                for (int i = 0, max = unlockResults.length(); i < max; i++) {
                    JSONObject unlockRow = unlockResults.optJSONObject(i);
                    UnlockData unlockData = getUnlockDataFromJSON(unlockRow, minCompletion);

                    if (unlockData == null) {
                        continue;
                    }

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

                unlockArray.addAll(weaponArray);
                unlockArray.addAll(attachmentArray);
                unlockArray.addAll(kitUnlockArray);
                unlockArray.addAll(vehicleUpgradeArray);
                unlockArray.addAll(skillArray);

                Collections.sort(weaponArray, new UnlockComparator());
                Collections.sort(attachmentArray, new UnlockComparator());
                Collections.sort(kitUnlockArray, new UnlockComparator());
                Collections.sort(vehicleUpgradeArray, new UnlockComparator());
                Collections.sort(skillArray, new UnlockComparator());
                Collections.sort(unlockArray, new UnlockComparator());

                unlockDataMap.put(
            		mProfileData.getPersona(count).getId(),
                    new UnlockDataWrapper(
                		weaponArray, 
                		attachmentArray,
                        kitUnlockArray, 
                        vehicleUpgradeArray,
                        skillArray, 
                        unlockArray
                    )
        		);
            }
            return unlockDataMap;
        } catch (Exception ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public UnlockData getUnlockDataFromJSON(JSONObject row, double minCompletion) throws WebsiteHandlerException {
        try {
            final String[] unlockCategories = {
        		UnlockData.CATEGORY_WEAPON,
                UnlockData.CATEGORY_ATTACHMENT,
                UnlockData.CATEGORY_VEHICLE, 
                UnlockData.CATEGORY_KIT,
                UnlockData.CATEGORY_SKILL
            };

            for (String category : unlockCategories) {
                if (row.isNull(category)) {
                    continue;
                }

                JSONObject detailObject = row.getJSONObject(category);
                JSONObject unlockDetails = detailObject.getJSONObject("unlockedBy");

                if (unlockDetails.getDouble("completion") < minCompletion) {
                    continue;
                }

                return new UnlockData(
                    row.optInt("kit", 0), 
                    unlockDetails.getDouble("completion"),
                    unlockDetails.optLong("valueNeeded", 0),
                    unlockDetails.optLong("actualValue", 0),
                    row.getString("parentId"),
                    detailObject.getString("unlockId"),
                    unlockDetails.getString("codeNeeded"), 
                    category
                );
            }
            return null;
        } catch (Exception ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }
    @Deprecated
    public ProfileInformation getInformation(Context context) throws WebsiteHandlerException {
        try {
            List<PlatoonData> platoonDataArray = new ArrayList<PlatoonData>();
            String httpContent = mRequestHandler.get(
                RequestHandler.generateUrl(URL_INFO, mProfileData.getUsername()),
                RequestHandler.HEADER_AJAX
            );

            if ("".equals(httpContent)) {
                throw new WebsiteHandlerException("Could not get the profile.");
            } else {
                JSONObject contextObject = new JSONObject(httpContent).optJSONObject("context");
                JSONObject profileCommonObject = contextObject.optJSONObject("profileCommon");
                JSONObject userInfo = profileCommonObject.optJSONObject("userinfo");
                JSONObject presenceObject = profileCommonObject.getJSONObject("user").getJSONObject("presence");
                // JSONArray gameReports = contextObject.getJSONArray(
                // "gameReportPreviewGroups" ); //Max 16 items
                JSONArray soldierArray = contextObject.getJSONArray("soldiersBox");
                // JSONArray feedItems = contextObject.getJSONArray("feed");
                JSONArray platoonArray = profileCommonObject.getJSONArray("platoons");
                JSONObject statusMessage = profileCommonObject.optJSONObject("userStatusMessage");
                JSONObject currItem;
                String playingOn;

                int numSoldiers = soldierArray.length();
                int numPlatoons = platoonArray.length();

                PersonaData[] personaArray = new PersonaData[numSoldiers];
                String username = profileCommonObject.getJSONObject("user").getString("username");

                if (statusMessage == null) {
                    statusMessage = new JSONObject("{'statusMessage':'', 'statusMessageChanged':0}");
                }

                if (presenceObject.isNull("serverName") && presenceObject.getBoolean("isPlaying")) {
                    playingOn = (presenceObject.getInt("platform") == 2) ? "Xbox Live" : "Playstation Network";
                } else {
                    playingOn = presenceObject.optString("serverName", "");
                }

                for (int i = 0; i < numSoldiers; i++) {
                    currItem = soldierArray.getJSONObject(i);
                    JSONObject personaObject = currItem.getJSONObject("persona");

                    personaArray[i] = new PersonaData(
                        Long.parseLong(personaObject.getString("personaId")),
                        (personaObject.isNull("personaName") ? username : personaObject.getString("personaName")),
                        DataBank.getPlatformIdFromName(personaObject.getString("namespace")),
                        personaObject.getString("picture")
                    );
                }

                if( numPlatoons > 0 ) {
                	for (int i = 0; i < numPlatoons; i++) {
	                    currItem = platoonArray.getJSONObject(i);
	                    String title = currItem.getString("id") + ".jpeg";
	                    if (!CacheHandler.isCached(context, title)) {
	                        PlatoonClient.cacheBadge(
	                            context, 
	                            currItem.getString("badgePath"), 
	                            title,
	                            Constants.DEFAULT_BADGE_SIZE
	                        );
	                    }
	
	                    platoonDataArray.add(
	                        new PlatoonData(
	                    		Long.parseLong(currItem.getString("id")), 
	                    		currItem.getString("name"), 
	                    		currItem.getString("tag"), 
	                    		currItem.getInt("platform"), 
	                    		currItem.getInt("fanCounter"),
	                    		currItem.getInt("memberCounter"), 
	                    		!currItem.getBoolean("hidden")
	                        )
	                    );
	                }	
                }
                	
                ProfileInformation.Builder tempProfile = new ProfileInformation.Builder(
            		Long.parseLong(userInfo.getString("userId")),
            		username
        		);
                ProfileInformation profile = tempProfile
            		.name(userInfo.optString("name", "N/A"))
                	.age(userInfo.optInt("age", 0))
                	.birthday(userInfo.optLong("birthdate", 0))
                	.loginDate(userInfo.optLong("lastLogin", 0))
                	.statusMessage(statusMessage.optString("statusMessage", ""))
                	.statusDate(statusMessage.optLong("statusMessageChanged", 0))
                	.persona(personaArray)
                	.presentation(userInfo.isNull("presentation") ? "" : userInfo.getString("presentation"))
                	.location(userInfo.optString("location", "us"))
                	.server(playingOn)
                	.allowFriendRequests(userInfo.optBoolean("allowFriendRequests", true))
                	.isOnline(presenceObject.getBoolean("isOnline"))
                	.isPlaying(presenceObject.getBoolean("isPlaying"))
                	.isFriend(profileCommonObject.getString("friendStatus").equals("ACCEPTED")) 
                    .platoons(platoonDataArray)
                    .build();
                profile.generateSerializedState();
                return profile;
            }
        } catch (Exception ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public static List<GeneralSearchResult> search(String keyword, String checksum) throws WebsiteHandlerException {
        List<GeneralSearchResult> results = new ArrayList<GeneralSearchResult>();
        try {
            String httpContent = new RequestHandler().post(
                URL_SEARCH, 
                RequestHandler.generatePostData(FIELD_NAMES_SEARCH, keyword, checksum), 
                RequestHandler.HEADER_NORMAL
            );
            
            if (!"".equals(httpContent)) {
                JSONArray searchResultsProfile = new JSONObject(httpContent).getJSONObject("data").getJSONArray("matches");
                if (searchResultsProfile.length() > 0) {
                    for (int i = 0, max = searchResultsProfile.length(); i < max; i++) {
                        JSONObject tempPersonaObj = searchResultsProfile.optJSONObject(i);
                        JSONObject tempUserObj = tempPersonaObj.getJSONObject("user");
                        results.add(
                            new GeneralSearchResult(
                                new ProfileData.Builder(
                            		Long.parseLong(tempUserObj.getString("userId")),
                            		tempUserObj.getString("username")
                        		).gravatarHash(tempUserObj.optString("gravatarMd5")).persona(
                        			new PersonaData(
                    					Long.parseLong(tempPersonaObj.getString("personaId")),
                    					tempPersonaObj.getString("personaName"),
                    					DataBank.getPlatformIdFromName(tempPersonaObj.getString("namespace")),
                    					""
                					)
                				).build()
                            )
                        );
                    }
                }
            }
        } catch (Exception ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
        return results;
    }

    public Map<Long, List<WeaponDataWrapper>> getWeapons() {
        try {
            Map<Long, List<WeaponDataWrapper>> weaponDataMap = new HashMap<Long, List<WeaponDataWrapper>>();
            for (int i = 0, max = mProfileData.getNumPersonas(); i < max; i++) {
            	List<WeaponDataWrapper> weaponDataArray = new ArrayList<WeaponDataWrapper>();
            	String httpContent = mRequestHandler.get(
                    RequestHandler.generateUrl(
                		URL_WEAPONS, 
                		mProfileData.getPersona(i).getId(), 
                		mProfileData.getPersona(i).getPlatformId()
            		),
                    RequestHandler.HEADER_NORMAL
                );

                if (httpContent != null && !httpContent.equals("")) {
                    JSONObject baseObject = new JSONObject(httpContent)                            .getJSONObject("data");
                    JSONObject weaponInfoObject = baseObject.getJSONObject("gadgetsLocale").getJSONObject("weapons");
                    JSONArray weaponStats = baseObject.getJSONArray("mainWeaponStats");
                    JSONObject unlockMap = baseObject.getJSONObject("unlocksAdded");

                    for (int count = 0, maxCount = weaponStats.length(); count < maxCount; count++) {
                        int numUnlocked = 0;
                        List<UnlockData> unlocks = new ArrayList<UnlockData>();
                        JSONObject currentItem = weaponStats.getJSONObject(count);

                        String guid = currentItem.isNull("duplicateOf") ? "guid" : "duplicateOf";
                        JSONObject currentWeapon = weaponInfoObject.getJSONObject(currentItem.getString("guid"));

                        if (!unlockMap.isNull(currentItem.getString(guid))) {
                            JSONArray currentUnlocks = unlockMap.getJSONArray(currentItem.getString(guid));

                            for (int uCount = 0, maxUCount = currentUnlocks.length(); uCount < maxUCount; uCount++) {
                                UnlockData unlockData = getUnlockDataFromJSON(currentUnlocks.getJSONObject(uCount), 0);
                                if (unlockData == null) {
                                    continue;
                                }

                                if (unlockData.getScoreCurrent() >= unlockData.getScoreNeeded()) {
                                    numUnlocked++;
                                }
                                unlocks.add(unlockData);
                            }
                        }
                        /* TODO: VALIDATE BURST MODES */
                        weaponDataArray.add(
                            new WeaponDataWrapper(
                                numUnlocked, 
                                new WeaponInfo(
                            		currentItem.getString("guid"), 
                            		currentItem.getString("name"), 
                            		currentItem.getString("slug"), 
                            		currentWeapon.optInt("rateOfFire", 1), 
                            		WeaponInfo.RANGE_VLONG,
                            		"Marshmallows", currentWeapon.getBoolean("fireModeAuto"),
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
            List<UnlockData> unlockArray;
            HashMap<Long, WeaponDataWrapper> weaponDataArray = new HashMap<Long, WeaponDataWrapper>();

            for (int i = 0, max = mProfileData.getNumPersonas(); i < max; i++) {
                String httpContent = mRequestHandler.get(
                    RequestHandler.generateUrl(
                        URL_WEAPONS_INFO,
                        mProfileData.getPersona(i).getName(),
                        mProfileData.getPersona(i).getId(), 
                        weaponStats.getSlug(), 
                        mProfileData.getPersona(i).getPlatformId()
                    ), RequestHandler.HEADER_NORMAL
                );

                if (httpContent != null && !httpContent.equals("")) {
                    unlockArray = new ArrayList<UnlockData>();
                    JSONObject baseObject = new JSONObject(httpContent).getJSONObject("data");
                    JSONArray unlockObjectArray = baseObject.getJSONObject("statsItem").getJSONObject("itemWeapon").getJSONArray("unlocks");

                    for (int count = 0, maxCount = unlockObjectArray.length(); count < maxCount; count++) {
                        JSONObject unlockObject = unlockObjectArray.getJSONObject(count);
                        JSONObject unlockObjectBy = unlockObject.getJSONObject("unlockedBy");

                        unlockArray.add(
                            new UnlockData(
                                weaponStats.getKitId(), 
                                unlockObjectBy.optDouble("completion", 0), 
                                unlockObjectBy.optLong("valueNeeded", 0), 
                                unlockObjectBy.optLong("actualValue", 0), weaponStats.getName(),
                                unlockObject.getString("unlockId"),
                                unlockObjectBy.getString("codeNeeded"),
                                UnlockData.CATEGORY_ATTACHMENT
                            )
                        );
                    }
                    Collections.sort(unlockArray, new UnlockComparator());
                    weaponDataArray.put(
                        mProfileData.getPersona(i).getId(), 
                        new WeaponDataWrapper(0, weaponInfo, weaponStats, unlockArray)
                    );
                }
            }
            return weaponDataArray;
        } catch (RequestHandlerException ex) {
            ex.printStackTrace();
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean cacheGravatar(Context c, String h, int s) {
        try {
            String cacheDir = PublicUtils.getCachePath(c);
            if (!cacheDir.endsWith("/")) {
                cacheDir += "/";
            }

            // Get the actual stream
            HttpEntity httpEntity = new RequestHandler().getHttpEntity(
                RequestHandler.generateUrl(Constants.URL_GRAVATAR, h, s, s), false
            );

            int bytesRead;
            int offset = 0;
            int contentLength = (int) httpEntity.getContentLength();
            byte[] data = new byte[contentLength];

            String filepath = cacheDir + h;
            InputStream imageStream = httpEntity.getContent();
            BufferedInputStream in = new BufferedInputStream(imageStream);

            while (offset < contentLength) {
                bytesRead = in.read(data, offset, data.length - offset);
                if (bytesRead == -1) {
                    break;
                }
                offset += bytesRead;
            }

            if (offset != contentLength) {
                throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
            }
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
