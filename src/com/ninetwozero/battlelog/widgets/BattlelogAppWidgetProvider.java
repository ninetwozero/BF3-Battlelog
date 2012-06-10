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

package com.ninetwozero.battlelog.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.RemoteViews;

import com.ninetwozero.battlelog.MainActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.FriendListDataWrapper;
import com.ninetwozero.battlelog.datatype.PersonaStats;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.COMClient;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class BattlelogAppWidgetProvider extends AppWidgetProvider {

    public static final String DEBUG_TAG = "WidgetProvider";
    public static final String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
    public static final String ACTION_WIDGET_OPENAPP = "Main";
    private SharedPreferences sharedPreferences;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {

        // Set the values
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        // Set the session if needed
        SessionKeeper.setProfileData(SessionKeeper
                .generateProfileDataFromSharedPreferences(sharedPreferences));

        // if service == active
        PublicUtils.setupSession(context, sharedPreferences);

        // Let's call the AsyncTask
        new AsyncRefresh(context, appWidgetManager).execute();

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context,
                BattlelogAppWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

        // UPDATE IT !!!!
        onUpdate(context, appWidgetManager, appWidgetIds);

    }

    private class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context context;
        private AppWidgetManager appWidgetManager;
        private int numFriendsOnline;
        private PersonaStats playerData;
        private FriendListDataWrapper friends;

        public AsyncRefresh(Context c, AppWidgetManager a) {

            context = c;
            appWidgetManager = a;

        }

        @Override
        protected Boolean doInBackground(Void... arg) {

            try {

                playerData = new ProfileClient(SessionKeeper.getProfileData()).getStats();

                friends = new COMClient(sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")).getFriendsForCOM(context);
                numFriendsOnline = friends.getNumTotalOnline();
                return true;

            } catch (WebsiteHandlerException ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (context != null) {

                // Let's init a RemoteViews
                RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                        R.layout.widget_dogtag);

                // Set the views
                if (results) {

                    remoteView.setTextViewText(

                            R.id.label, playerData.getPersonaName()

                            );
                    remoteView.setTextViewText(

                            R.id.title,
                            context.getString(R.string.info_xml_rank)
                                    + playerData.getRankId());
                    remoteView
                            .setTextViewText(

                                    R.id.stats,
                                    ("W/L: " + Math.floor(playerData.getWLRatio() * 100) / 100
                                            + "  K/D: " + Math.floor(playerData.getKDRatio() * 100) / 100));

                    if (numFriendsOnline > 0) {

                        remoteView.setTextColor(R.id.friends, Color.BLACK);
                        remoteView.setTextViewText(R.id.friends, "" + numFriendsOnline);

                    } else {

                        remoteView.setTextColor(R.id.friends, Color.RED);
                        remoteView.setTextViewText(R.id.friends, "0");

                    }

                } else {

                    remoteView.setTextViewText(R.id.label,
                            Html.fromHtml("<b>Error</b>"));
                    remoteView.setTextViewText(R.id.title,
                            context.getString(R.string.general_no_data));
                    remoteView.setTextViewText(R.id.stats,
                            context.getString(R.string.info_connect_bl));
                    remoteView.setTextColor(R.id.friends, Color.RED);
                    remoteView.setTextViewText(R.id.friends, "0");

                }

                remoteView.setOnClickPendingIntent(R.id.widget_button,
                        PendingIntent.getBroadcast(context,
                                0, new Intent(context, BattlelogAppWidgetProvider.class)
                                        .setAction(ACTION_WIDGET_RECEIVER), 0));
                remoteView.setOnClickPendingIntent(
                        R.id.widget_button2,
                        PendingIntent.getActivity(
                                context, 0, new Intent(context, MainActivity.class).setAction(
                                        ACTION_WIDGET_OPENAPP), 0));
                appWidgetManager.updateAppWidget(new ComponentName(context,
                        BattlelogAppWidgetProvider.class), remoteView);

            }

        }
    }

}
