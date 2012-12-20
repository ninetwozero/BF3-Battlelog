/*
	This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.widgets;

import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.FeedItem;
import com.ninetwozero.bf3droid.datatype.FriendListDataWrapper;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.http.COMClient;
import com.ninetwozero.bf3droid.http.FeedClient;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;
import com.ninetwozero.bf3droid.misc.SessionKeeper;

public class SocialWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_WIDGET_RECEIVER = "SocialWidgetReciever";
    public static final String ACTION_WIDGET_OPENAPP = "Main";
    public static int mFeedPageId = 0;
    private static FriendListDataWrapper mFriends;
    public static List<FeedItem> mFeedItems;

    public static final int FEED_WRAP_IDS[] = new int[]{
            R.id.wrap_feed_0, R.id.wrap_feed_1, R.id.wrap_feed_2
    };
    public static final int FEED_CONTENT_IDS[] = new int[]{
            R.id.text_content_0, R.id.text_content_1, R.id.text_content_2
    };
    public static final int FEED_DATE_IDS[] = new int[]{
            R.id.text_date_0, R.id.text_date_1, R.id.text_date_2
    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // Get the SP
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        // Set the session if needed
        SessionKeeper.setProfileData(SessionKeeper
                .generateProfileDataFromSharedPreferences(sharedPreferences));

        // Let's setup the session
        PublicUtils.setupSession(context, sharedPreferences);

        // if service == active
        if (SessionKeeper.getProfileData() == null) {
    		Log.i(Constants.DEBUG_TAG, "No session to use in the widget");
        } else {
            new AsyncRefresh(context, appWidgetManager).execute();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context,
                SocialWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

        // UPDATE IT !!!!
        onUpdate(context, appWidgetManager, appWidgetIds);

    }

    private class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context context;
        private String message;
        private AppWidgetManager appWidgetManager;

        public AsyncRefresh(Context c, AppWidgetManager a) {
            context = c;
            appWidgetManager = a;
        }

        @Override
        protected Boolean doInBackground(Void... arg) {
            try {
                mFriends = new COMClient(0,PreferenceManager.getDefaultSharedPreferences(context)
                        .getString(
                                Constants.SP_BL_PROFILE_CHECKSUM, "")).getFriendsForCOM(context);
                mFeedItems = new FeedClient(SessionKeeper.getProfileData().getId(),
                        FeedClient.TYPE_GLOBAL).get(context, 0, Constants.DEFAULT_NUM_FEED);
                return (mFeedItems != null && mFriends != null);
            } catch (WebsiteHandlerException ex) {
                ex.printStackTrace();
                message = ex.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean results) {
            if (context != null) {
                drawLayout(context, appWidgetManager);
                if (!results) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void drawLayout(Context c, AppWidgetManager a) {
        // Draw the GUI
        RemoteViews remoteView = new RemoteViews(c.getPackageName(), R.layout.widget_social);
        remoteView.setTextViewText(R.id.text_title, SessionKeeper.getProfileData().getUsername());

        // Let's see what's up!
        if (mFriends != null) {
            remoteView.setTextViewText(R.id.text_online, mFriends.getNumTotalOnline() + "");
            remoteView.setTextViewText(R.id.text_playing, mFriends.getNumPlaying() + "");
        } else {
            remoteView.setTextViewText(R.id.text_online, "TBA");
            remoteView.setTextViewText(R.id.text_playing, "TBA");
        }

        // If the feed items are gone...
        if (mFeedItems != null && !mFeedItems.isEmpty()) {
            for (int count = 0, max = FEED_DATE_IDS.length; count < max; count++) {
                remoteView.setTextViewText(FEED_CONTENT_IDS[count],
                        Html.fromHtml(mFeedItems.get(mFeedPageId + count).getTitle()));
                remoteView
                        .setTextViewText(FEED_DATE_IDS[count], PublicUtils.getRelativeDate(c,
                                mFeedItems.get(mFeedPageId + count).getDate()));
                remoteView.setOnClickPendingIntent(FEED_WRAP_IDS[count], PendingIntent.getActivity(
                        c, 0, mFeedItems.get(mFeedPageId + count).getIntent(c), 0));
            }
            remoteView.setTextViewText(R.id.text_latest, "Latest updates");
        } else {
            remoteView.removeAllViews(R.id.wrap_feed);
            remoteView.setTextViewText(R.id.text_latest, "No connection to Battlelog");
        }

        // Set the click listeners
        ComponentName widgetComponent = new ComponentName(c, SocialWidgetProvider.class);
        a.updateAppWidget(widgetComponent, remoteView);
    }
}
