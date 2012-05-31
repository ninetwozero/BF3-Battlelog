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
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;

import com.ninetwozero.battlelog.datatypes.GeneralSearchResult;
import com.ninetwozero.battlelog.datatypes.NewsData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.datatypes.SearchComparator;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;

/* 
 * Methods of this class should be loaded in AsyncTasks, as they would probably lock up the GUI
 */

public class MODWebsiteHandler extends DefaultHandler {
    
    public MODWebsiteHandler() {
        
        requestHandler = new RequestHandler();
        
    }
    
    public String downloadZip(Context context, String url, String title)
            throws WebsiteHandlerException {

        try {


            // Get the *content*
            if (!requestHandler.saveFileFromURI(Constants.URL_IMAGE_PACK, "",
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

    public Bitmap downloadGravatarToBitmap(String hash, int size)
            throws WebsiteHandlerException {

        try {

            // Any size requirements? Otherwise we just pick the standard number
            return new RequestHandler().getImageFromStream(

                    RequestHandler.generateUrl(
                            Constants.URL_GRAVATAR,
                            hash,
                            ((size > 0) ? size : Constants.DEFAULT_AVATAR_SIZE),
                            Constants.DEFAULT_AVATAR_SIZE

                            ),
                    true

                    );

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    public boolean cacheGravatar(Context c, String h, int s) {

        try {

            // Get the external cache dir
            String cacheDir = PublicUtils.getCachePath(c);

            // How does it end?
            if (!cacheDir.endsWith("/")) {
                cacheDir += "/";
            }

            // Get the actual stream
            HttpEntity httpEntity = requestHandler.getHttpEntity(

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

    public boolean cacheBadge(Context c, String h, String fName, int s) {

        try {

            // Get the external cache dir
            String cacheDir = PublicUtils.getCachePath(c);

            // How does it end?
            if (!cacheDir.endsWith("/")) {
                cacheDir += "/";
            }

            // Get the actual stream
            HttpEntity httpEntity = requestHandler.getHttpEntity(

                    RequestHandler.generateUrl(PlatoonHandler.URL_IMAGE, h), true

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

    public List<GeneralSearchResult> search(Context c, String k, String ch)
            throws WebsiteHandlerException {

        try {

            // Get the results
            List<GeneralSearchResult> results = ProfileHandler.search(c, k, ch);
            results.addAll(PlatoonHandler.search(c, k, ch));

            // Sort & return
            Collections.sort(results, new SearchComparator());
            return results;

        } catch (WebsiteHandlerException ex) {

            throw ex;

        }
    }

    public List<NewsData> getNewsForPage(int p) throws WebsiteHandlerException {

        try {

            // Init!
            List<NewsData> news = new ArrayList<NewsData>();

            // Iterate!
            for (int i = 0, max = 2; i < max; i++) {

                // Let's see
                int num = (10 * p);
                if (i == 1) {

                    num += 5;

                }

                // Get the data
                String httpContent = requestHandler.get(

                        RequestHandler.generateUrl(Constants.URL_NEWS, num),
                        RequestHandler.HEADER_AJAX

                        );

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
                                        new ProfileData.Builder(

                                                Long.parseLong(user.getString("userId")),
                                                user.getString("username")

                                        ).gravatarHash(

                                                user.getString("gravatarMd5")

                                                ).build()

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
