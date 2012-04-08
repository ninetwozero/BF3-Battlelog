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

import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.RemoteViews;

import com.ninetwozero.battlelog.MainActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.PersonaStats;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class SocialWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_WIDGET_RECEIVER = "SocialWidgetReciever";
    public static final String ACTION_WIDGET_OPENAPP = "Main";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {

        // Attributes
        Intent active = new Intent(context, SocialWidgetProvider.class)
                .setAction(ACTION_WIDGET_RECEIVER);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context,
                0, active, 0);
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0,
                appIntent, 0);
        appIntent.setAction(ACTION_WIDGET_OPENAPP);

        RemoteViews remoteView = null;
        SharedPreferences sharedPreferences = null;
        ComponentName BattlelogListWidget;

        // Set the values
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        remoteView = new RemoteViews(context.getPackageName(),
                R.layout.widget_social);
        final Resources res = context.getResources();

        // Set the session if needed
        SessionKeeper.setProfileData(SessionKeeper
                .generateProfileDataFromSharedPreferences(sharedPreferences));

        // if service == active
        if (SessionKeeper.getProfileData() == null) {

            remoteView.setTextViewText(R.id.label,
                    Html.fromHtml("<b>Error</b>"));
            remoteView.setTextViewText(R.id.title,
                    res.getString(R.string.general_no_data));
            remoteView.setTextViewText(R.id.stats,
                    res.getString(R.string.info_connect_bl));
            remoteView.setTextColor(R.id.friends, Color.RED);
            remoteView.setTextViewText(R.id.friends, "0");

        } else {

            // Let's update it
            RequestHandler.setCookies(

                    new ShareableCookie(

                            sharedPreferences.getString(Constants.SP_BL_COOKIE_NAME, ""),
                            sharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE, ""),
                            Constants.COOKIE_DOMAIN

                    )

                    );

            try {

                playerData = WebsiteHandler.getStatsForPersona(SessionKeeper
                        .getProfileData());
                remoteView.setTextViewText(

                        R.id.label, playerData.getPersonaName()

                        );
                remoteView.setTextViewText(

                        R.id.title,
                        res.getString(R.string.info_xml_rank)
                                + playerData.getRankId());
                remoteView
                        .setTextViewText(

                                R.id.stats,
                                ("W/L: " + Math.floor(playerData.getWLRatio() * 100) / 100
                                        + "  K/D: " + Math.floor(playerData.getKDRatio() * 100) / 100));
                profileDataArray = WebsiteHandler.getFriends(

                        sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""), true

                        );
                numFriendsOnline = profileDataArray.size();

            } catch (WebsiteHandlerException e) {

                e.printStackTrace();

            }

            if (numFriendsOnline > 0) {

                remoteView.setTextColor(R.id.friends, Color.BLACK);
                remoteView.setTextViewText(R.id.friends, "" + numFriendsOnline);

            } else {

                remoteView.setTextColor(R.id.friends, Color.RED);
                remoteView.setTextViewText(R.id.friends, "0");

            }

        }
        remoteView.setOnClickPendingIntent(R.id.widget_button,
                actionPendingIntent);
        remoteView.setOnClickPendingIntent(R.id.widget_button2,
                appPendingIntent);
        BattlelogListWidget = new ComponentName(context,
                SocialWidgetProvider.class);
        appWidgetManager.updateAppWidget(BattlelogListWidget, remoteView);

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

}
