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

package com.ninetwozero.battlelog.asynctask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.DashboardActivity;
import com.ninetwozero.battlelog.datatype.PostData;
import com.ninetwozero.battlelog.datatype.SessionKeeperPackage;
import com.ninetwozero.battlelog.http.COMClient;
import com.ninetwozero.battlelog.http.NotificationClient;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import net.sf.andhsli.hotspotlogin.SimpleCrypto;

public class AsyncServiceTask extends AsyncTask<String, Integer, Integer> {

    // Attribute
    private Context context;
    private SharedPreferences sharedPreferences;
    private String exception;

    // Constructor
    public AsyncServiceTask(Context c, SharedPreferences sp) {

        context = c;
        sharedPreferences = sp;

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Integer doInBackground(String... arg0) {

        try {
            // Let's try to setActive
            if (COMClient.setActive()) {

                // The user is active, so how many notifications does he have?
                int numNotifications = new NotificationClient().getNewNotificationsCount(

                        sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")

                );
                return numNotifications;

            } else {

                // Attributes
                String email = sharedPreferences.getString(
                        Constants.SP_BL_PROFILE_EMAIL, "");
                String password = SimpleCrypto.decrypt(email, sharedPreferences
                        .getString(Constants.SP_BL_PROFILE_PASSWORD, ""));

                // Do the login
                AsyncLogin login = new AsyncLogin(context);
                SessionKeeperPackage sessionKeeperPackage = login.renewSession(postData(email,
                        password));

                // Did it work?
                if (sessionKeeperPackage != null) {
                    SessionKeeper.setProfileData(sessionKeeperPackage.getProfileData());
                    SessionKeeper.setPlatoonData(sessionKeeperPackage.getPlatoons());
                }

                return -1;

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            exception = ex.getMessage();
            return -2;

        }

    }

    private PostData[] postData(String email, String password) {
        return new PostData[]{

                new PostData(Constants.FIELD_NAMES_LOGIN[0], email),
                new PostData(Constants.FIELD_NAMES_LOGIN[1], password),
                new PostData(Constants.FIELD_NAMES_LOGIN[2],
                        Constants.FIELD_VALUES_LOGIN[2]),
                new PostData(Constants.FIELD_NAMES_LOGIN[3],
                        Constants.FIELD_VALUES_LOGIN[3]),

        };
    }

    @Override
    protected void onPostExecute(Integer results) {

        // Is the result >= 0
        if (results >= 0) {

            // We had a "positive" outcome
            if (results > 0) {

                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Notification battlelogNotification = new Notification();
                battlelogNotification.icon = R.drawable.app_logo;
                battlelogNotification.when = System.currentTimeMillis();

                // Create a new intent
                Intent notificationIntent = new Intent(context, DashboardActivity.class)
                        .putExtra(

                                "openCOMCenter", true

                        ).putExtra(

                                "openTabId", 1

                        );
                // Convert it to a "PendingIntent" as it won't be activated
                // right here, right now (later via notification)
                PendingIntent contentIntent = PendingIntent.getActivity(
                        context, 0, notificationIntent, 0);

                // So... let's fix the singular/plural<insert something here>
                String text;
                if (results == 1) {

                    text = context
                            .getString(R.string.info_txt_notification_new);

                } else {

                    text = context.getString(
                            R.string.info_txt_notification_new_p).replace(
                            "{num}", String.valueOf(results));

                }

                // Set the ticker (text scrolling up above when first notified)
                battlelogNotification.tickerText = context
                        .getString(R.string.msg_notification);
                battlelogNotification.setLatestEventInfo(

                        context, text,
                        context.getString(R.string.info_tap_notification),
                        contentIntent

                );
                battlelogNotification.flags |= Notification.FLAG_AUTO_CANCEL;

                // Setup LED, sound & vibration
                if (sharedPreferences.getBoolean("notification_light", true)) {

                    battlelogNotification.defaults |= Notification.DEFAULT_LIGHTS;

                }

                if (sharedPreferences.getBoolean("notification_sound", true)) {

                    if (sharedPreferences.getBoolean(
                            "notification_sound_special", true)) {

                        battlelogNotification.sound = Uri
                                .parse("android.resource://com.ninetwozero.battlelog/"
                                        + R.raw.notification);

                    } else {

                        battlelogNotification.defaults |= Notification.DEFAULT_SOUND;

                    }
                }

                if (sharedPreferences.getBoolean("notification_vibrate", true)) {

                    battlelogNotification.vibrate = new long[]{
                            100, 100
                    };

                }

                // Do the actual notification
                notificationManager.notify(0, battlelogNotification);

            } else {

                Log.d(Constants.DEBUG_TAG, "No unread notifications");

            }

        } else if (results == -1) {

            Log.d(Constants.DEBUG_TAG, "Trying to relogin...");

        } else {

            // Error in previous method
            Toast.makeText(context, exception, Toast.LENGTH_SHORT).show();

        }

        // Stop the service!!
        ((Service) context).stopSelf();

    }

}
