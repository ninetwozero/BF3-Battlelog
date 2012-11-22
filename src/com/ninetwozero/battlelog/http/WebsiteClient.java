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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;

import com.ninetwozero.battlelog.datatype.GeneralSearchResult;
import com.ninetwozero.battlelog.datatype.NewsData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.SearchComparator;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;

/* 
 * Methods of this class should be loaded in AsyncTasks, as they would probably lock up the GUI
 */

public class WebsiteClient extends DefaultClient {

    public WebsiteClient() {
        mRequestHandler = new RequestHandler();
    }

    public Bitmap downloadGravatarToBitmap(String hash, int size) throws WebsiteHandlerException {
        try {
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

    public static List<GeneralSearchResult> search(Context c, String k, String ch) throws WebsiteHandlerException {
        try {
            List<GeneralSearchResult> results = ProfileClient.search(k, ch);
            results.addAll(PlatoonClient.search(c, k, ch));
            
            Collections.sort(results, new SearchComparator());
            return results;
        } catch (WebsiteHandlerException ex) {
            throw ex;
        }
    }

    public List<NewsData> getNewsForPage(int p) throws WebsiteHandlerException {
        try {
            List<NewsData> news = new ArrayList<NewsData>();
            for (int i = 0, max = 2; i < max; i++) {
                int num = (10 * p);
                if (i == 1) {
                    num += 5;
                }

                String httpContent = mRequestHandler.get(
                    RequestHandler.generateUrl(Constants.URL_NEWS, num),
                    RequestHandler.HEADER_AJAX
                );
                
                if (httpContent != null && !httpContent.equals("")) {
                    JSONArray baseArray = new JSONObject(httpContent).getJSONObject("context").getJSONArray("blogPosts");
                    for (int count = 0, maxCount = baseArray.length(); count < maxCount; count++) {
                        JSONObject item = baseArray.getJSONObject(count);
                        JSONObject user = item.getJSONObject("user");
                        news.add(
                            new NewsData(
                                Long.parseLong(item.getString("id")),
                                item.getLong("creationDate"),
                                item.getInt("devblogCommentCount"),
                                item.getString("title"),
                                new JSONObject(item.getString("json")).getString("excerpt"),
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
        } catch (Exception ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

	public NewsData getNewsFromId(long id) throws WebsiteHandlerException {
        try {
            String httpContent = mRequestHandler.get(
                RequestHandler.generateUrl(Constants.URL_NEWS_SINGLE, id),
                RequestHandler.HEADER_AJAX
            );
                
            if (httpContent != null && !httpContent.equals("")) {
                JSONObject item = new JSONObject(httpContent).getJSONObject("context").getJSONObject("blogPost");
                JSONObject user = item.getJSONObject("user");
                return new NewsData(
                    Long.parseLong(item.getString("id")),
                    item.getLong("creationDate"),
                    item.getInt("devblogCommentCount"),
                    item.getString("title"),
                    new JSONObject(item.getString("json")).getString("content"),
                    new ProfileData.Builder(
                            Long.parseLong(user.getString("userId")),
                            user.getString("username")
                    ).gravatarHash(
                            user.getString("gravatarMd5")
                    ).build()
                );
            }
            return null;
        } catch (Exception ex) {
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }
}
