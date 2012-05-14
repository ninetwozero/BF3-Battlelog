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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.ninetwozero.battlelog.misc.CacheHandler.Forum;
import com.ninetwozero.battlelog.misc.CacheHandler.Persona;
import com.ninetwozero.battlelog.misc.CacheHandler.Platoon;
import com.ninetwozero.battlelog.misc.CacheHandler.Profile;

/* 
 * Methods of this class should be loaded in AsyncTasks, as they would probably lock up the GUI
 */

public class WebsiteHandler {

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
