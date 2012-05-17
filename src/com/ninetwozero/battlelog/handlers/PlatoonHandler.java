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

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.preference.PreferenceManager;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.GeneralSearchResult;
import com.ninetwozero.battlelog.datatypes.PersonaData;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.PlatoonInformation;
import com.ninetwozero.battlelog.datatypes.PlatoonStats;
import com.ninetwozero.battlelog.datatypes.PlatoonStatsItem;
import com.ninetwozero.battlelog.datatypes.PlatoonTopStatsItem;
import com.ninetwozero.battlelog.datatypes.ProfileComparator;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.TopStatsComparator;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class PlatoonHandler {

    // URLS
    public static final String URL_INFO = Constants.URL_MAIN + "platoon/{PLATOON_ID}/";
    public static final String URL_FANS = Constants.URL_MAIN
            + "platoon/{PLATOON_ID}/listfans/";
    public static final String URL_MEMBERS = Constants.URL_MAIN
            + "platoon/{PLATOON_ID}/listmembers/";
    public static final String URL_STATS = Constants.URL_MAIN
            + "platoon/platoonMemberStats/{PLATOON_ID}/2/{PLATFORM_ID}/";
    public static final String URL_PROMOTE = Constants.URL_MAIN
            + "platoon/promotemember/{PLATOON_ID}/{UID}/";
    public static final String URL_DEMOTE = Constants.URL_MAIN
            + "platoon/demotemember/{PLATOON_ID}/{UID}/";
    public static final String URL_APPLY = Constants.URL_MAIN
            + "platoon/applyformembership/";
    public static final String URL_RESPOND = Constants.URL_MAIN
            + "platoon/applyingactions/{PLATOON_ID}/";
    public static final String URL_LEAVE = Constants.URL_MAIN + "platoon/leave/";
    public static final String URL_KICK = Constants.URL_MAIN
            + "platoon/kickmember/{PLATOON_ID}/{UID}/";
    public static final String URL_INVITE = Constants.URL_MAIN
            + "platoon/invitemember/";
    public static final String URL_NEW = Constants.URL_MAIN + "platoon/newplatoon/";
    public static final String URL_EDIT = Constants.URL_MAIN + "platoon/edit/{PLATOON_ID}/";
    public static final String URL_SEARCH = Constants.URL_MAIN + "platoon/search/";
    public static final String URL_IMAGE = Constants.URL_STATIC_CONTENT
            + "prod/emblems/320/{BADGE_PATH}";
    public static final String URL_THUMBS = Constants.URL_STATIC_CONTENT
            + "prod/emblems/60/{BADGE_PATH}";

    // Constants
    public static final String[] FIELD_NAMES_SETTINGS = new String[] {

            "name", "tag", "presentation", "website", "allow_members_apply", "post-check-sum"

    };

    public static final String[] FIELD_NAMES_SEARCH = new String[] {
            "searchplat", "post-check-sum"
    };

    public static final String[] FIELD_NAMES_APPLY = new String[] {
            "platoonId", "post-check-sum"
    };

    public static final String[] FIELD_NAMES_RESPOND = new String[] {
            "apply-action", "userIds[]", "post-check-sum", "accept", "deny"
    };
    public static final String[] FIELD_VALUES_RESPOND = new String[] {
            "", null, null, "accept", "deny"
    };

    public static final String[] FIELD_NAMES_INVITE = new String[] {
            "platoonId", "post-check-sum", "userIds[]"
    };

    public static final String[] FIELD_NAMES_LEAVE = new String[] {
            "platoonId", "userId", "post-check-sum"
    };

    public static final String[] FIELD_NAMES_NEW = new String[] {
            "name", "tag", "active", "post-check-sum"
    };

    // Constants
    public static final int FILTER_PROMOTE = 0;
    public static final int FILTER_DEMOTE = 1;
    public static final int FILTER_KICK = 2;

    public static final int STATE_OK = -1;
    public static final int STATE_FAIL = 0;
    public static final int STATE_ERROR = 1;

    public static boolean answerPlatoonRequest(long plId, long pId,
            Boolean accepting, String checksum) throws WebsiteHandlerException {

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

                    RequestHandler.generateUrl(URL_RESPOND, plId),
                    RequestHandler.generatePostData(

                            FIELD_NAMES_RESPOND,
                            "",
                            pId,
                            checksum,
                            accepting ? FIELD_VALUES_RESPOND[3] : null,
                            !accepting ? FIELD_VALUES_RESPOND[4] : null

                            ),
                    RequestHandler.HEADER_NORMAL

                    );

            // Did we manage?
            return (!"".equals(httpContent));

        } catch (RequestHandlerException ex) {

            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean applyForPlatoonMembership(final long platoonId,
            final String checksum) throws WebsiteHandlerException {

        try {

            // Let's set it up!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

                    URL_APPLY,
                    RequestHandler.generatePostData(
                            FIELD_NAMES_APPLY,
                            platoonId,
                            checksum

                            ),
                    RequestHandler.HEADER_GZIP

                    );

            // What up?
            if (httpContent == null || httpContent.equals("")) {

                throw new WebsiteHandlerException("Invalid request");

            } else {

                if (httpContent.equals("success")) {

                    return true;

                } else if (httpContent.equals("wrongplatform")) {

                    throw new WebsiteHandlerException("Wrong platform.");

                } else if (httpContent.equals("maxmembersreached")) {

                    throw new WebsiteHandlerException("The platoon has reached its level cap.");

                } else {

                    throw new WebsiteHandlerException("Unknown request");

                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean leave(final long platoonId,
            final long userId, final String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's set it up!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(

                    URL_LEAVE,
                    RequestHandler.generatePostData(

                            FIELD_NAMES_LEAVE,
                            platoonId,
                            userId,
                            checksum

                            ),
                    RequestHandler.HEADER_GZIP

                    );

            // What up?
            return (httpContent != null & !httpContent.equals(""));

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static PlatoonData getPlatoonId(final String keyword,
            final String checksum) throws WebsiteHandlerException {

        try {

            // Let's do this!
            RequestHandler wh = new RequestHandler();
            PlatoonData platoon = null;
            String httpContent = wh.post(

                    URL_SEARCH,
                    RequestHandler.generatePostData(

                            FIELD_NAMES_SEARCH,
                            keyword,
                            checksum

                            ),
                    RequestHandler.HEADER_GZIP

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

    public static boolean create(Object... params) {

        try {

            // Let's do the actual request
            RequestHandler rh = new RequestHandler();
            String httpContent = rh.post(

                    URL_NEW,
                    RequestHandler.generatePostData(

                            FIELD_NAMES_NEW,
                            params

                            ),
                    RequestHandler.HEADER_AJAX

                    );

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

                    RequestHandler.generateUrl(URL_IMAGE, h),
                    true

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

    public static int sendInvite(final Object[] userId,
            final long platoonId, final String checksum)
            throws WebsiteHandlerException {

        try {

            // Let's login everybody
            RequestHandler rh = new RequestHandler();
            int numUsers = userId.length;
            String httpContent = rh.post(

                    URL_INVITE,
                    RequestHandler.generatePostData(

                            FIELD_NAMES_INVITE,
                            platoonId,
                            checksum,
                            userId

                            ),
                    RequestHandler.HEADER_JSON
                    );

            // Did we manage?
            if (!"".equals(httpContent)) {

                // Check the JSON
                JSONObject baseObject = new JSONObject(httpContent);
                JSONArray errors = (baseObject.isNull("errors")) ? null
                        : baseObject.optJSONArray("errors");
                int numErrors = (errors == null) ? 0 : errors.length();

                // Let's see what we got
                if (numErrors == 0) {

                    return STATE_OK;

                } else if (numErrors == numUsers) {

                    return STATE_FAIL;

                } else if (numErrors < numUsers) {

                    return STATE_ERROR;

                }

            }

            // Fallback
            return STATE_FAIL;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public static boolean editMember(final long userId,
            final long platoonId, final int filter)
            throws WebsiteHandlerException {

        try {

            // Init!
            String url = "";

            // Let's see filter we have here
            if (filter == PlatoonHandler.FILTER_PROMOTE) {

                url = RequestHandler.generateUrl(

                        URL_PROMOTE,
                        userId,
                        platoonId

                        );

            } else if (filter == PlatoonHandler.FILTER_DEMOTE) {

                url = RequestHandler.generateUrl(

                        URL_DEMOTE,
                        userId,
                        platoonId

                        );

            } else if (filter == PlatoonHandler.FILTER_KICK) {

                url = RequestHandler.generateUrl(

                        URL_KICK,
                        userId,
                        platoonId

                        );

            }

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.get(url, RequestHandler.HEADER_JSON);

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

    public static PlatoonStats getStats(final Context context,
            final PlatoonData platoonData) throws WebsiteHandlerException {

        try {

            // Let's go!
            RequestHandler rh = new RequestHandler();

            // Get the content
            String httpContent = rh.get(

                    RequestHandler.generateUrl(

                            URL_STATS,
                            platoonData.getId(),
                            platoonData.getPlatformId()

                            ),
                    RequestHandler.HEADER_AJAX

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

                                RequestHandler.generateUrl(URL_INFO, platoonData.getId()),
                                RequestHandler.HEADER_AJAX

                                );

                        // Build an object
                        JSONObject tempPlatoonData = new JSONObject(
                                tempHttpContent).getJSONObject("context")
                                .getJSONObject("platoon");

                        // Return and reloop
                        return getStats(

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
                                            .getInt("average"),
                                    new ProfileData.Builder(
                                            Long.parseLong(currUser.optString("userId", "0")),
                                            currUser.optString("username", "")
                                    ).persona(
                                            new PersonaData(
                                                    Long.parseLong(currObj
                                                            .optString(
                                                                    "bestPersonaId", "")), currUser
                                                            .optString(
                                                                    "username", ""), platoonData
                                                            .getPlatformId(), null)
                                            ).gravatarHash(tempGravatarHash).build()

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
                                            .getInt("average"),
                                    new ProfileData.Builder(
                                            Long.parseLong(currUser.optString("userId", "0")),
                                            currUser.optString("username", "")
                                    ).persona(
                                            new PersonaData(
                                                    Long.parseLong(currObj
                                                            .optString(
                                                                    "bestPersonaId", "")), currUser
                                                            .optString(
                                                                    "username", ""), platoonData
                                                            .getPlatformId(), null)
                                            ).gravatarHash(tempGravatarHash).build()

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
                                            .getInt("average"),
                                    new ProfileData.Builder(
                                            Long.parseLong(currUser.optString("userId", "0")),
                                            currUser.optString("username", "")
                                    ).persona(
                                            new PersonaData(
                                                    Long.parseLong(currObj
                                                            .optString(
                                                                    "bestPersonaId", "")), currUser
                                                            .optString(
                                                                    "username", ""), platoonData
                                                            .getPlatformId(), null)
                                            ).gravatarHash(tempGravatarHash).build()

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
                                    new ProfileData.Builder(
                                            Long.parseLong(currUser.optString("userId", "0")),
                                            currUser.optString("username", "")
                                    ).gravatarHash(tempGravatarHash).build()

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

    public static ArrayList<ProfileData> getFans(
            final long platoonId) throws WebsiteHandlerException {

        try {

            // Let's go!
            RequestHandler rh = new RequestHandler();
            List<ProfileData> fans = new ArrayList<ProfileData>();
            String httpContent;

            // Do the request
            httpContent = rh.get(

                    RequestHandler.generateUrl(URL_FANS, platoonId),
                    RequestHandler.HEADER_AJAX

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

                            new ProfileData.Builder(
                                    Long.parseLong(tempObject.optString("userId", "0")),
                                    tempObject.optString("username", "")
                            ).gravatarHash(tempObject.optString("gravatarMd5")).build()
                            );

                }

            }

            // Did we get more than 0?
            if (fans.size() > 0) {

                // Add a header just 'cause we can
                fans.add(new ProfileData("Loyal fans"));

                // a-z please!
                Collections.sort(fans, new ProfileComparator());

            }
            // Return
            return (ArrayList<ProfileData>) fans;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }
    }

    public static PlatoonInformation getInformation(

            final Context c, final PlatoonData pData, final int num, final long aPId

            ) throws WebsiteHandlerException {

        try {

            // Let's go!
            RequestHandler rh = new RequestHandler();
            List<ProfileData> fans = new ArrayList<ProfileData>();
            List<ProfileData> members = new ArrayList<ProfileData>();
            List<ProfileData> friends = new ArrayList<ProfileData>();
            PlatoonStats stats = null;

            // Arrays to divide the users in
            List<ProfileData> founderMembers = new ArrayList<ProfileData>();
            List<ProfileData> adminMembers = new ArrayList<ProfileData>();
            List<ProfileData> regularMembers = new ArrayList<ProfileData>();
            List<ProfileData> invitedMembers = new ArrayList<ProfileData>();
            List<ProfileData> requestMembers = new ArrayList<ProfileData>();

            // Get the content
            String httpContent = rh.get(

                    RequestHandler.generateUrl(URL_INFO, pData.getId()),
                    RequestHandler.HEADER_AJAX

                    );
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
                friends = COMHandler.getFriends(

                        PreferenceManager.getDefaultSharedPreferences(c)
                                .getString(Constants.SP_BL_PROFILE_CHECKSUM, ""), false

                        );

                // Let's iterate over the members
                JSONArray idArray = memberArray.names();
                for (int counter = 0, max = idArray.length(); counter < max; counter++) {

                    // Temporary var
                    ProfileData tempProfile;

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

                        tempProfile = new ProfileData.Builder(

                                Long.parseLong(currItem.getString("userId")),
                                currItem.getJSONObject("user").getString("username")
                                ).persona(
                                        new PersonaData(
                                                Long.parseLong(currItem
                                                        .getString("personaId")),
                                                currItem
                                                        .getJSONObject("persona").getString(
                                                                "personaName"),
                                                profileCommonObject.getInt("platform"),
                                                null

                                        )
                                        )
                                        .gravatarHash(currItem.optString("gravatarMd5", ""))
                                        .isOnline(
                                                currItem.getJSONObject("user")
                                                        .getJSONObject("presence")
                                                        .getBoolean("isOnline")
                                        )
                                        .isPlaying(
                                                currItem.getJSONObject("user")
                                                        .getJSONObject("presence")
                                                        .getBoolean("isPlaying")
                                        ).membershipLevel(currItem.getInt("membershipLevel"))
                                        .build();

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

                        members.add(new ProfileData(c
                                .getString(R.string.info_platoon_member_founder_p)));

                    } else {

                        members.add(new ProfileData(c
                                .getString(R.string.info_platoon_member_founder)));

                    }

                    // Add them to the members
                    members.addAll(founderMembers);

                }

                if (adminMembers.size() > 0) {

                    // Plural?
                    if (adminMembers.size() > 1) {

                        members.add(new ProfileData(c
                                .getString(R.string.info_platoon_member_admin_p)));

                    } else {

                        members.add(new ProfileData(c.getString(R.string.info_platoon_member_admin)));

                    }

                    // Add them to the members
                    members.addAll(adminMembers);

                }

                if (regularMembers.size() > 0) {

                    // Plural?
                    if (regularMembers.size() > 1) {

                        members.add(new ProfileData(c
                                .getString(R.string.info_platoon_member_regular_p)));

                    } else {

                        members.add(new ProfileData(c
                                .getString(R.string.info_platoon_member_regular)));

                    }

                    // Add them to the members
                    members.addAll(regularMembers);

                }

                // Is the user *admin* or higher?
                if (isAdmin) {

                    if (invitedMembers.size() > 0) {

                        // Just add them
                        members.add(new ProfileData(c
                                .getString(R.string.info_platoon_member_invited_label)));
                        members.addAll(invitedMembers);

                    }

                    if (requestMembers.size() > 0) {

                        // Just add them
                        members.add(new ProfileData(c
                                .getString(R.string.info_platoon_member_requested_label)));
                        members.addAll(requestMembers);

                    }

                }

                // Let's get 'em fans too
                fans = PlatoonHandler.getFans(pData.getId());

                // Oh man, don't forget the stats!!!
                stats = PlatoonHandler.getStats(c, pData);

                // Required
                long platoonId = Long.parseLong(profileCommonObject
                        .getString("id"));
                String filename = platoonId + ".jpeg";

                // Is the image already cached?
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

    public static ArrayList<GeneralSearchResult> search(

            Context context, String keyword, String checksum

            ) throws WebsiteHandlerException {

        // Init
        List<GeneralSearchResult> results = new ArrayList<GeneralSearchResult>();
        RequestHandler rh = new RequestHandler();

        try {

            // Get the content
            String httpContent = rh.post(

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

}
