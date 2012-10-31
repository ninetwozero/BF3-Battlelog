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

import android.content.Context;
import android.preference.PreferenceManager;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.*;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlatoonClient extends DefaultClient {

    // Attributes
    private PlatoonData mPlatoonData;

    // URLS
    public static final String URL_INFO = Constants.URL_MAIN + "platoon/{PLATOON_ID}/";
    public static final String URL_FANS = Constants.URL_MAIN + "platoon/{PLATOON_ID}/listfans/";
    public static final String URL_MEMBERS = Constants.URL_MAIN + "platoon/{PLATOON_ID}/listmembers/";
    public static final String URL_STATS = Constants.URL_MAIN + "platoon/platoonMemberStats/{PLATOON_ID}/2/{PLATFORM_ID}/";
    public static final String URL_PROMOTE = Constants.URL_MAIN + "platoon/promotemember/{PLATOON_ID}/{UID}/";
    public static final String URL_DEMOTE = Constants.URL_MAIN + "platoon/demotemember/{PLATOON_ID}/{UID}/";
    public static final String URL_APPLY = Constants.URL_MAIN + "platoon/applyformembership/";
    public static final String URL_RESPOND = Constants.URL_MAIN + "platoon/applyingactions/{PLATOON_ID}/";
    public static final String URL_LEAVE = Constants.URL_MAIN + "platoon/leave/";
    public static final String URL_KICK = Constants.URL_MAIN + "platoon/kickmember/{PLATOON_ID}/{UID}/";
    public static final String URL_INVITE = Constants.URL_MAIN + "platoon/invitemember/";
    public static final String URL_NEW = Constants.URL_MAIN + "platoon/newplatoon/";
    public static final String URL_EDIT = Constants.URL_MAIN + "platoon/edit/{PLATOON_ID}/";
    public static final String URL_SEARCH = Constants.URL_MAIN + "platoon/search/";
    public static final String URL_IMAGE = Constants.URL_STATIC_CONTENT + "prod/emblems/320/{BADGE_PATH}";
    public static final String URL_THUMBS = Constants.URL_STATIC_CONTENT + "prod/emblems/60/{BADGE_PATH}";

    // Constants
    public static final String[] FIELD_NAMES_SETTINGS = new String[]{
    	"name", "tag", "presentation", "website", "allow_members_apply", "post-check-sum"
    };

    public static final String[] FIELD_NAMES_SEARCH = new String[]{
        "searchplat", "post-check-sum"
    };

    public static final String[] FIELD_NAMES_APPLY = new String[]{
        "platoonId", "post-check-sum"
    };

    public static final String[] FIELD_NAMES_RESPOND = new String[]{
        "apply-action", "userIds[]", "post-check-sum", "accept", "deny"
    };
    public static final String[] FIELD_VALUES_RESPOND = new String[]{
        "", null, null, "accept", "deny"
    };

    public static final String[] FIELD_NAMES_INVITE = new String[]{
        "platoonId", "post-check-sum", "userIds[]"
    };

    public static final String[] FIELD_NAMES_LEAVE = new String[]{
        "platoonId", "userId", "post-check-sum"
    };

    public static final String[] FIELD_NAMES_NEW = new String[]{
        "name", "tag", "active", "post-check-sum"
    };

    public static final int FILTER_PROMOTE = 0;
    public static final int FILTER_DEMOTE = 1;
    public static final int FILTER_KICK = 2;

    public static final int STATE_OK = -1;
    public static final int STATE_FAIL = 0;
    public static final int STATE_ERROR = 1;

    public PlatoonClient(PlatoonData p) {
        mRequestHandler = new RequestHandler();
        mPlatoonData = p;
    }

    public boolean answerPlatoonRequest(long pId, Boolean accepting, String checksum) throws WebsiteHandlerException {
        try {
            String httpContent = mRequestHandler.post(
                RequestHandler.generateUrl(URL_RESPOND, mPlatoonData.getId()),
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
            return (!"".equals(httpContent));
        } catch (RequestHandlerException ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public boolean applyForPlatoonMembership(final String checksum) throws WebsiteHandlerException {
        try {
            String httpContent = mRequestHandler.post(
                URL_APPLY,
                RequestHandler.generatePostData(
                    FIELD_NAMES_APPLY,
                    mPlatoonData.getId(),
                    checksum
                ),
                RequestHandler.HEADER_GZIP
            );

            // What up?
            if ("".equals(httpContent)) {
                throw new WebsiteHandlerException("Invalid request");
            } else {
                if ("success".equals(httpContent)) {
                    return true;
                } else if ("wrongplatform".equals(httpContent)) {
                    throw new WebsiteHandlerException("Wrong platform.");
                } else if ("maxmembersreached".equals(httpContent)) {
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

    public boolean leave(final long userId, final String checksum) throws WebsiteHandlerException {
        try {
            String httpContent = mRequestHandler.post(
                URL_LEAVE,
                RequestHandler.generatePostData(
                    FIELD_NAMES_LEAVE,
                    mPlatoonData.getId(),
                    userId,
                    checksum
                ),
                RequestHandler.HEADER_GZIP
            );
            return !"".equals(httpContent);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public static PlatoonData getPlatoonId(final String keyword, final String checksum) throws WebsiteHandlerException {
        try {
            PlatoonData platoon = null;
            String httpContent = new RequestHandler().post(
                URL_SEARCH,
                RequestHandler.generatePostData(
                    FIELD_NAMES_SEARCH,
                    keyword,
                    checksum
                ),
                RequestHandler.HEADER_GZIP
            );
            if (!"".equals(httpContent)) {
                JSONArray searchResults = new JSONArray(httpContent);
                if (searchResults.length() > 0) {

                    // init cost counters
                    int costOld = 999, costCurrent = 0;
                    for (int i = 0, max = searchResults.length(); i < max; i++) {
                        JSONObject tempObj = searchResults.optJSONObject(i);
                        if (tempObj.getBoolean("hidden")) {
                            continue;
                        }
                        if (tempObj.getString("name").equals(keyword)) {
                            platoon = new PlatoonData(
                            	Long.parseLong(tempObj.getString("id")),
                                tempObj.getInt("fanCounter"),
                                tempObj.getInt("memberCounter"),
                                tempObj.getInt("platform"),
                                tempObj.getString("name"),
                                tempObj.getString("tag"), null, true
                            );
                            break;
                        }

                        // Grab the "cost"
                        costCurrent = PublicUtils.getLevenshteinDistance(keyword, tempObj.getString("name"));

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
                        costOld = costCurrent;
                    }
                    return platoon;
                }
                throw new WebsiteHandlerException("No platoons found.");
            } else {
                throw new WebsiteHandlerException("Could not retreive the ProfileIDs.");
            }
        } catch (Exception ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public static boolean create(Object... params) {
        try {
            String httpContent = new RequestHandler().post(
                URL_NEW,
                RequestHandler.generatePostData(
                    FIELD_NAMES_NEW,
                    params
                ),
                RequestHandler.HEADER_AJAX
            );
            if ("".equals(httpContent)) {
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
            String cacheDir = PublicUtils.getCachePath(c);
            if (!cacheDir.endsWith("/")) {
                cacheDir += "/";
            }

            HttpEntity httpEntity = new RequestHandler().getHttpEntity(
        		RequestHandler.generateUrl(URL_IMAGE, h),
                true
            );

            int bytesRead = 0;
            int offset = 0;
            int contentLength = (int) httpEntity.getContentLength();
            byte[] data = new byte[contentLength];

            InputStream imageStream = httpEntity.getContent();
            BufferedInputStream in = new BufferedInputStream(imageStream);

            String filepath = cacheDir + fName;

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

    public int sendInvite(final Object[] userId, final String checksum) throws WebsiteHandlerException {
        try {
            int numUsers = userId.length;
            String httpContent = mRequestHandler.post(
                URL_INVITE,
                RequestHandler.generatePostData(
                    FIELD_NAMES_INVITE,
                    mPlatoonData.getId(),
                    checksum,
                    userId
                ),
                RequestHandler.HEADER_JSON
            );

            if (!"".equals(httpContent)) {
                JSONObject baseObject = new JSONObject(httpContent);
                JSONArray errors = (baseObject.isNull("errors")) ? null : baseObject.optJSONArray("errors");
                int numErrors = (errors == null) ? 0 : errors.length();

                if (numErrors == 0) {
                    return STATE_OK;
                } else if (numErrors == numUsers) {
                    return STATE_FAIL;
                } else if (numErrors < numUsers) {
                    return STATE_ERROR;
                }
            }
            return STATE_FAIL;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public boolean editMember(final long userId, final int filter) throws WebsiteHandlerException {
        try {
            String url = "";
            if (filter == PlatoonClient.FILTER_PROMOTE) {
                url = RequestHandler.generateUrl(
                    URL_PROMOTE,
                    userId,
                    mPlatoonData.getId()
                );
            } else if (filter == PlatoonClient.FILTER_DEMOTE) {
                url = RequestHandler.generateUrl(
                    URL_DEMOTE,
                    userId,
                    mPlatoonData.getId()
                );
            } else if (filter == PlatoonClient.FILTER_KICK) {
                url = RequestHandler.generateUrl(
                    URL_KICK,
                    userId,
                    mPlatoonData.getId()
                );
            }

            String httpContent = mRequestHandler.get(url, RequestHandler.HEADER_JSON);
            if ("".equals(httpContent)) {
                return false;
            } else {
                String status = new JSONObject(httpContent).optString("message", "");
                return (
                	"USER_PROMOTED".equals(status) || 
                    "USER_DEMOTED".equals(status) || 
                    "MEMBER_KICKED".equals(status)
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public PlatoonStats getStats(final Context context) throws WebsiteHandlerException {
        try {
            String httpContent = mRequestHandler.get(
                RequestHandler.generateUrl(
                    URL_STATS,
                    mPlatoonData.getId(),
                    mPlatoonData.getPlatformId()
                ),
                RequestHandler.HEADER_AJAX
            );

            if (!"".equals(httpContent)) {
                JSONObject baseObject = new JSONObject(httpContent).getJSONObject("data");
                if (baseObject.isNull("platoonPersonas")) {
                    if (mPlatoonData.getPlatformId() == 0) {
                        String tempHttpContent = mRequestHandler.get(
                            RequestHandler.generateUrl(URL_INFO, mPlatoonData.getId()),
                            RequestHandler.HEADER_AJAX
                        );

                        JSONObject tempPlatoonData = new JSONObject(tempHttpContent).getJSONObject("context").getJSONObject("platoon");
                        mPlatoonData = new PlatoonData(
                            mPlatoonData.getId(), 
                            tempPlatoonData.getInt("fanCounter"), 
                            tempPlatoonData.getInt("memberCounter"),
                            tempPlatoonData.getInt("platform"),
                            tempPlatoonData.getString("name"),
                            tempPlatoonData.getString("tag"),
                            mPlatoonData.getId() + ".jpeg",
                            tempPlatoonData.getBoolean("hidden")
                        );
                        return getStats(context);
                    } else {
                        return null;
                    }
                }

                JSONObject memberStats = baseObject.getJSONObject("memberStats");
                JSONObject personaList = baseObject.optJSONObject("platoonPersonas");
                JSONObject objectGeneral = memberStats.getJSONObject("general");
                JSONObject objectKit = memberStats.getJSONObject("kitsVehicles");
                JSONObject objectTop = memberStats.getJSONObject("topPlayers");
                JSONObject objectScore = objectKit.getJSONObject("score");
                JSONObject objectSPM = objectKit.getJSONObject("scorePerMinute");
                JSONObject objectTime = objectKit.getJSONObject("time");
                JSONObject currObj = null;
                JSONObject currUser = null;
                JSONArray currObjNames = null;

                List<PlatoonStatsItem> arrayGeneral = new ArrayList<PlatoonStatsItem>();
                List<PlatoonStatsItem> arrayScore = new ArrayList<PlatoonStatsItem>();
                List<PlatoonStatsItem> arraySPM = new ArrayList<PlatoonStatsItem>();
                List<PlatoonStatsItem> arrayTime = new ArrayList<PlatoonStatsItem>();
                List<PlatoonTopStatsItem> arrayTop = new ArrayList<PlatoonTopStatsItem>();

                List<Integer> tempMid = new ArrayList<Integer>();
                String tempGravatarHash = null;

                currObjNames = objectGeneral.names();
                int maxNames = currObjNames.length();
                if (maxNames == 0) {
                    return null;
                }

                for (int i = 0; i < maxNames; i++) {
                    currObj = objectGeneral.getJSONObject(currObjNames.getString(i));
                    if (currObjNames.getString(i).equals("kd")) {
                        arrayGeneral.add(
                            new PlatoonStatsItem(
                                currObjNames.getString(i), 
                                currObj.getDouble("min"),
                                currObj.getDouble("median"), 
                                currObj.getDouble("best"), 
                                currObj.getDouble("average"), 
                                null
                            )
                        );
                    } else {
                        arrayGeneral.add(
                            new PlatoonStatsItem(
                                currObjNames.getString(i), 
                                currObj.getInt("min"),
                                currObj.getInt("median"), 
                                currObj.getInt("best"), 
                                currObj.getInt("average"), 
                                null
                            )
                        );
                    }
                }

                // Create a new "overall item"
                arrayScore.add(new PlatoonStatsItem(context.getString(R.string.info_platoon_stats_overall), 0, 0, 0, 0, null));

                // Persona Scores
                currObjNames = objectScore.names();
                for (int i = 0, max = currObjNames.length(); i < max; i++) {
                    currObj = objectScore.getJSONObject(currObjNames.getString(i));
                    if (currObj.has("bestPersonaId")) {
                        currUser = personaList.getJSONObject(currObj.getString("bestPersonaId"));
                    } else {
                        currUser = personaList.getJSONObject(currObj.getString("personaId"));
                    }

                    tempGravatarHash = currUser.optString("gravatarMd5", "");

                    arrayScore.add(
                        new PlatoonStatsItem(
                            currObjNames.getString(i), 
                            currObj.getInt("min"), 
                            currObj.getInt("median"), 
                            currObj.getInt("best"), 
                            currObj.getInt("average"),
                            new ProfileData.Builder(
                            	Long.parseLong(currUser.optString("userId", "0")),
                                currUser.optString("username", "")
                            ).persona(
                        		new PersonaData(
                    				Long.parseLong(currObj.optString("bestPersonaId", "")), 
                    				currUser.optString("username", ""), 
                    				mPlatoonData.getPlatformId(), 
                    				null
                				)
                            ).gravatarHash(tempGravatarHash).build()
                        )
                    );

                    // Is it ! the first?
                    if (i > 0) {
                        tempMid.add(arrayScore.get(i + 1).getMid());
                    }
                    arrayScore.get(0).add(arrayScore.get(i + 1));
                }
                Collections.sort(tempMid);
                arrayScore.get(0).setMid(tempMid.get((int) Math.ceil(tempMid.size() / 2)));
                tempMid.clear();

                // Create a new "overall item"
                arraySPM.add(new PlatoonStatsItem(context.getString(R.string.info_platoon_stats_overall), 0, 0,0, 0, null));

                // Get the *kit* score/min
                currObjNames = objectSPM.names();
                for (int i = 0, max = currObjNames.length(); i < max; i++) {
                    currObj = objectSPM.getJSONObject(currObjNames.getString(i));

                    if (currObj.has("bestPersonaId")) {
                        currUser = personaList.getJSONObject(currObj.getString("bestPersonaId"));
                    } else {
                        currUser = personaList.getJSONObject(currObj.getString("personaId"));
                    }

                    tempGravatarHash = currUser.optString("gravatarMd5", "");

                    arraySPM.add(
                		new PlatoonStatsItem(
                                currObjNames.getString(i), 
                                currObj.getInt("min"), 
                                currObj.getInt("median"), 
                                currObj.getInt("best"), 
                                currObj.getInt("average"),
                                new ProfileData.Builder(
                                	Long.parseLong(currUser.optString("userId", "0")),
                                    currUser.optString("username", "")
                                ).persona(
                            		new PersonaData(
                        				Long.parseLong(currObj.optString("bestPersonaId", "")), 
                        				currUser.optString("username", ""), 
                        				mPlatoonData.getPlatformId(), 
                        				null
                    				)
                                ).gravatarHash(tempGravatarHash).build()
                            )
                    );

                    // Is it ! the first?
                    if (i > 0) {
                        tempMid.add(arraySPM.get(0).getMid());
                    }
                    arraySPM.get(0).add(arraySPM.get(i + 1));

                }
                Collections.sort(tempMid);
                arraySPM.get(0).setMid(tempMid.get((int) Math.ceil(tempMid.size() / 2)));
                tempMid.clear();

                // Create a new "overall item"
                arrayTime.add(new PlatoonStatsItem(context.getString(R.string.info_platoon_stats_overall), 0, 0,0, 0, null));

                // Get the *kit* times
                currObjNames = objectTime.names();
                for (int i = 0, max = currObjNames.length(); i < max; i++) {
                    currObj = objectTime.getJSONObject(currObjNames.getString(i));

                    if (currObj.has("bestPersonaId")) {
                        currUser = personaList.getJSONObject(currObj.getString("bestPersonaId"));
                    } else {
                        currUser = personaList.getJSONObject(currObj.getString("personaId"));
                    }

                    tempGravatarHash = currUser.optString("gravatarMd5", "");

                    arrayTime.add(
                		new PlatoonStatsItem(
                                currObjNames.getString(i), 
                                currObj.getInt("min"), 
                                currObj.getInt("median"), 
                                currObj.getInt("best"), 
                                currObj.getInt("average"),
                                new ProfileData.Builder(
                                	Long.parseLong(currUser.optString("userId", "0")),
                                    currUser.optString("username", "")
                                ).persona(
                            		new PersonaData(
                        				Long.parseLong(currObj.optString("bestPersonaId", "")), 
                        				currUser.optString("username", ""), 
                        				mPlatoonData.getPlatformId(), 
                        				null
                    				)
                                ).gravatarHash(tempGravatarHash).build()
                            )
                    );

                    // Is it ! the first?
                    if (i > 0) {
                        tempMid.add(arrayTime.get(0).getMid());
                    }
                    arrayTime.get(0).add(arrayTime.get(i + 1));
                }
                Collections.sort(tempMid);
                arrayTime.get(0).setMid(tempMid.get((int) Math.ceil(tempMid.size() / 2)));
                tempMid.clear();

                // Get the *top player* Tops
                PlatoonTopStatsItem highestSPM = null;
                currObjNames = objectTop.names();

                for (int i = 0, max = currObjNames.length(); i < max; i++) {
                    currObj = objectTop.getJSONObject(currObjNames.getString(i));
                    if (currObj.has("bestPersonaId")) {
                        currUser = personaList.getJSONObject(currObj.getString("bestPersonaId"));
                    } else if (!currObj.getString("personaId").equals("0")) {
                        currUser = personaList.getJSONObject(currObj.getString("personaId"));
                    } else {
                        arrayTop.add(new PlatoonTopStatsItem("N/A", 0, null));
                        continue;
                    }

                    tempGravatarHash = currUser.optString("gravatarMd5", "");
                    String filename = tempGravatarHash + ".png";

                    if (!CacheHandler.isCached(context, filename)) {
                        ProfileClient.cacheGravatar(context, filename, Constants.DEFAULT_AVATAR_SIZE);
                    }

                    // Create a new "stats item"
                    arrayTop.add(
                        new PlatoonTopStatsItem(
                            currObjNames.getString(i), 
                            currObj.getInt("spm"),
                            new ProfileData.Builder(
                                Long.parseLong(currUser.optString("userId", "0")),
                                currUser.optString("username", "")
                            ).gravatarHash(tempGravatarHash).build()
                        )
                    );

                    // Store it if it's the highest
                    if (highestSPM == null || highestSPM.getSPM() < arrayTop.get(i).getSPM()) {
                        highestSPM = arrayTop.get(i);
                    }
                }

                // Set the best & sort
                arrayTop.add(
                    new PlatoonTopStatsItem(
                        "TOP", 
                        highestSPM.getSPM(), 
                        highestSPM.getProfile()
                    )
                );
                Collections.sort(arrayTop, new TopStatsComparator());
                return new PlatoonStats(
                    mPlatoonData.getName(), 
                    mPlatoonData.getId(), 
                    arrayGeneral,
                    arrayTop, 
                    arrayScore, 
                    arraySPM, 
                    arrayTime
                );
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public ArrayList<ProfileData> getFans() throws WebsiteHandlerException {
        try {
            List<ProfileData> fans = new ArrayList<ProfileData>();
            String httpContent = mRequestHandler.get(
                RequestHandler.generateUrl(URL_FANS, mPlatoonData.getId()),
                RequestHandler.HEADER_AJAX
            );

            JSONObject fanArray = new JSONObject(httpContent).getJSONObject("context").getJSONObject("fans");
            JSONArray fanIdArray = fanArray.names();
            JSONObject tempObject = null;

            if (fanIdArray != null) {
                for (int i = 0, max = fanIdArray.length(); i < max; i++) {
                    tempObject = fanArray.getJSONObject(fanIdArray.getString(i));
                    fans.add(
                        new ProfileData.Builder(
                            Long.parseLong(tempObject.optString("userId", "0")),
                            tempObject.optString("username", "")
                        ).gravatarHash(tempObject.optString("gravatarMd5")).build()
                    );
                }
            }

            if (!fans.isEmpty()) {
                fans.add(new ProfileData("Loyal fans"));
                Collections.sort(fans, new ProfileComparator());
            }
            return (ArrayList<ProfileData>) fans;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public PlatoonInformation getInformation(final Context c, final int num, final long aPId) throws WebsiteHandlerException {
        try {
            List<ProfileData> fans = new ArrayList<ProfileData>();
            List<ProfileData> members = new ArrayList<ProfileData>();
            List<ProfileData> friends = new ArrayList<ProfileData>();
            PlatoonStats stats = null;

            List<ProfileData> founderMembers = new ArrayList<ProfileData>();
            List<ProfileData> adminMembers = new ArrayList<ProfileData>();
            List<ProfileData> regularMembers = new ArrayList<ProfileData>();
            List<ProfileData> invitedMembers = new ArrayList<ProfileData>();
            List<ProfileData> requestMembers = new ArrayList<ProfileData>();

            String httpContent = mRequestHandler.get(
                RequestHandler.generateUrl(URL_INFO, mPlatoonData.getId()),
                RequestHandler.HEADER_AJAX
            );
            boolean isAdmin = false;
            boolean isMember = false;

            if (!"".equals(httpContent)) {
                JSONObject contextObject = new JSONObject(httpContent).optJSONObject("context");

                if (contextObject.isNull("platoon")) {
                    return null;
                }

                JSONObject profileCommonObject = contextObject.optJSONObject("platoon");
                JSONObject memberArray = profileCommonObject.optJSONObject("members");
                // JSONArray feedItems = contextObject.getJSONArray( "feed" );
                JSONObject currItem;

                friends = new COMClient(
            		aPId, PreferenceManager.getDefaultSharedPreferences(c).getString(Constants.SP_BL_PROFILE_CHECKSUM, "")
        		).getFriends(aPId,false);

                JSONArray idArray = memberArray.names();
                for (int counter = 0, max = idArray.length(); counter < max; counter++) {
                    ProfileData tempProfile;
                    currItem = memberArray.optJSONObject(idArray.getString(counter));
                    if (idArray.getString(counter).equals(aPId)) {
                        isMember = true;
                        if (currItem.getInt("membershipLevel") >= 128) {
                            isAdmin = true;
                        }
                    }

                    if (!currItem.isNull("persona")) {
                        tempProfile = new ProfileData.Builder(
                            Long.parseLong(currItem.getString("userId")),
                            currItem.getJSONObject("user").getString("username")
                        ).persona(
                            new PersonaData(
                                Long.parseLong(currItem.getString("personaId")),
                                currItem.getJSONObject("persona").getString("personaName"),
                                profileCommonObject.getInt("platform"),
                                null
                            )
                        ).gravatarHash(currItem.optString("gravatarMd5", "")).isOnline(
                    		currItem.getJSONObject("user").getJSONObject("presence").getBoolean("isOnline")
                        ).isPlaying(
                    		currItem.getJSONObject("user").getJSONObject("presence").getBoolean("isPlaying")
                        ).membershipLevel(currItem.getInt("membershipLevel")).build();
                    } else {
                        continue;
                    }

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

                Collections.sort(requestMembers, new ProfileComparator());
                Collections.sort(invitedMembers, new ProfileComparator());
                Collections.sort(regularMembers, new ProfileComparator());
                Collections.sort(adminMembers, new ProfileComparator());
                Collections.sort(founderMembers, new ProfileComparator());

                if (!founderMembers.isEmpty()) {
                    if (founderMembers.size() > 1) {
                        members.add(new ProfileData(c.getString(R.string.info_platoon_member_founder_p)));
                    } else {
                        members.add(new ProfileData(c.getString(R.string.info_platoon_member_founder)));
                    }
                    members.addAll(founderMembers);
                }

                if (!adminMembers.isEmpty()) {
                    if (adminMembers.size() > 1) {
                        members.add(new ProfileData(c.getString(R.string.info_platoon_member_admin_p)));
                    } else {
                        members.add(new ProfileData(c.getString(R.string.info_platoon_member_admin)));
                    }
                    members.addAll(adminMembers);
                }

                if (!regularMembers.isEmpty()) {
                    if (regularMembers.size() > 1) {
                        members.add(new ProfileData(c.getString(R.string.info_platoon_member_regular_p)));
                    } else {
                        members.add(new ProfileData(c.getString(R.string.info_platoon_member_regular)));
                    }
                    members.addAll(regularMembers);
                }

                if (isAdmin) {
                    if (!invitedMembers.isEmpty()) {
                        members.add(new ProfileData(c.getString(R.string.info_platoon_member_invited_label)));
                        members.addAll(invitedMembers);
                    }

                    if (!requestMembers.isEmpty()) {
                        members.add(new ProfileData(c.getString(R.string.info_platoon_member_requested_label)));
                        members.addAll(requestMembers);
                    }
                }
                fans = getFans();
                stats = getStats(c);

                long platoonId = Long.parseLong(profileCommonObject.getString("id"));
                String filename = platoonId + ".jpeg";

                if (!CacheHandler.isCached(c, filename)) {
                    PlatoonClient.cacheBadge(c, profileCommonObject.getString("badgePath"), filename, Constants.DEFAULT_BADGE_SIZE);
                }

                PlatoonInformation platoonInformation = new PlatoonInformation(
                    platoonId, 
                    profileCommonObject.getLong("creationDate"),
                    profileCommonObject.getInt("platform"),
                    profileCommonObject.getInt("game"),
                    profileCommonObject.getInt("fanCounter"),
                    profileCommonObject.getInt("memberCounter"),
                    profileCommonObject.getInt("blazeClubId"),
                    profileCommonObject.getString("name"),
                    profileCommonObject.getString("tag"),
                    profileCommonObject.getString("presentation"),
                    PublicUtils.normalizeUrl(profileCommonObject.optString("website", "")),
                    !profileCommonObject.getBoolean("hidden"), 
                    isMember,
                    isAdmin,
                    profileCommonObject.getBoolean("allowNewMembers"), 
                    members, 
                    fans, 
                    friends,
                    stats
                );
                if (CacheHandler.Platoon.insert(c, platoonInformation) == 0) {
                    CacheHandler.Platoon.update(c, platoonInformation);
                }
                return platoonInformation;
            } else {
                throw new WebsiteHandlerException("Could not get the platoon.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    public static ArrayList<GeneralSearchResult> search(Context context, String keyword, String checksum) throws WebsiteHandlerException {
        List<GeneralSearchResult> results = new ArrayList<GeneralSearchResult>();
        try {
            String httpContent = new RequestHandler().post(
                URL_SEARCH,
                RequestHandler.generatePostData(
                    FIELD_NAMES_SEARCH,
                    keyword,
                    checksum
                ),
                RequestHandler.HEADER_JSON
            );
            if (!"".equals(httpContent)) {
                JSONArray searchResultsPlatoon = new JSONArray(httpContent);
                if (searchResultsPlatoon.length() > 0) {
                    for (int i = 0, max = searchResultsPlatoon.length(); i < max; i++) {
                        JSONObject tempObj = searchResultsPlatoon.optJSONObject(i);
                        final String filename = tempObj.getString("id") + ".jpeg";
                        results.add(
                            new GeneralSearchResult(
                                new PlatoonData(
                                    Long.parseLong(tempObj.getString("id")), 
                                    tempObj.getInt("fanCounter"), 
                                    tempObj.getInt("memberCounter"), 
                                    tempObj.getInt("platform"), 
                                    tempObj.getString("name"),
                                    tempObj.getString("tag"), 
                                    filename, 
                                    true
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
        return (ArrayList<GeneralSearchResult>) results;
    }
}
