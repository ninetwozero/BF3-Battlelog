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

package com.ninetwozero.bf3droid.widgets;

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

import com.ninetwozero.bf3droid.MainActivity;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.FriendListDataWrapper;
import com.ninetwozero.bf3droid.datatype.PersonaData;
import com.ninetwozero.bf3droid.datatype.PersonaStats;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.http.COMClient;
import com.ninetwozero.bf3droid.http.ProfileClient;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;
import com.ninetwozero.bf3droid.misc.SessionKeeper;

public class BattlelogAppWidgetProvider extends AppWidgetProvider {

    public static final String DEBUG_TAG = "WidgetProvider";
    public static final String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
    public static final String ACTION_WIDGET_OPENAPP = "Main";
    private SharedPreferences mSharedPreferences;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // Set the values
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        // Set the session if needed
        SessionKeeper.setProfileData(SessionKeeper
                .generateProfileDataFromSharedPreferences(mSharedPreferences));

        // if service == active
        PublicUtils.setupSession(context, mSharedPreferences);

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
        private Context mContext;
        private AppWidgetManager mAppWidgetManager;
        private int mNumFriendsOnline;
        private PersonaStats mPlayerData;
        private FriendListDataWrapper mFriends;

        public AsyncRefresh(Context c, AppWidgetManager a) {

            mContext = c;
            mAppWidgetManager = a;

        }

        @Override
        protected Boolean doInBackground(Void... arg) {

            try {

                PersonaData firstPersona = SessionKeeper.getProfileData()
                        .getPersona(0);
                mPlayerData = new ProfileClient(SessionKeeper.getProfileData())
                        .getStats(

                                firstPersona.getName(), firstPersona.getId(),
                                firstPersona.getPlatformId()

                        );

                mFriends = new COMClient(0, mSharedPreferences.getString(
                        Constants.SP_BL_PROFILE_CHECKSUM, ""))
                        .getFriendsForCOM(mContext);
                mNumFriendsOnline = mFriends.getNumTotalOnline();
                return true;

            } catch (WebsiteHandlerException ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (mContext != null) {

                // Let's init a RemoteViews
                RemoteViews remoteView = new RemoteViews(
                        mContext.getPackageName(), R.layout.widget_dogtag);

                // Set the views
                if (results) {

                    remoteView.setTextViewText(

                            R.id.label, mPlayerData.getPersonaName()

                    );
                    remoteView.setTextViewText(

                            R.id.title, mContext.getString(R.string.info_xml_rank)
                            + mPlayerData.getRankId());
                    remoteView
                            .setTextViewText(

                                    R.id.stats,
                                    ("W/L: "
                                            + Math.floor(mPlayerData
                                            .getWLRatio() * 100) / 100
                                            + "  K/D: " + Math
                                            .floor(mPlayerData.getKDRatio() * 100) / 100));

                    if (mNumFriendsOnline > 0) {

                        remoteView.setTextColor(R.id.friends, Color.BLACK);
                        remoteView.setTextViewText(R.id.friends,
                                String.valueOf(mNumFriendsOnline));

                    } else {

                        remoteView.setTextColor(R.id.friends, Color.RED);
                        remoteView.setTextViewText(R.id.friends, "0");

                    }

                } else {

                    remoteView.setTextViewText(R.id.label,
                            Html.fromHtml("<b>Error</b>"));
                    remoteView.setTextViewText(R.id.title,
                            mContext.getString(R.string.general_no_data));
                    remoteView.setTextViewText(R.id.stats,
                            mContext.getString(R.string.info_connect_bl));
                    remoteView.setTextColor(R.id.friends, Color.RED);
                    remoteView.setTextViewText(R.id.friends, "0");

                }

                remoteView.setOnClickPendingIntent(R.id.widget_button,
                        PendingIntent.getBroadcast(mContext, 0, new Intent(
                                mContext, BattlelogAppWidgetProvider.class)
                                .setAction(ACTION_WIDGET_RECEIVER), 0));
                remoteView.setOnClickPendingIntent(R.id.widget_button2,
                        PendingIntent.getActivity(mContext, 0, new Intent(
                                mContext, MainActivity.class)
                                .setAction(ACTION_WIDGET_OPENAPP), 0));
                mAppWidgetManager.updateAppWidget(new ComponentName(mContext,
                        BattlelogAppWidgetProvider.class), remoteView);

            }

        }
    }

}
