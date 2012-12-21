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

package com.ninetwozero.bf3droid.asynctask;

import android.app.AlarmManager;
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

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.DashboardActivity;
import com.ninetwozero.bf3droid.http.COMClient;
import com.ninetwozero.bf3droid.http.NotificationClient;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.service.BattlelogService;

public class AsyncServiceTask extends AsyncTask<String, Integer, Boolean> {

    // Attributes
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private int mNumNotifications;

    public AsyncServiceTask(Context c, SharedPreferences sp) {
        mContext = c;
        mSharedPreferences = sp;
        mNumNotifications = 0;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        try {
            if (COMClient.setActive()) {
                mNumNotifications = new NotificationClient().getNewNotificationsCount(
                    mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")
                );
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
        	if( mNumNotifications > 0 ) {
        		// Create the upcoming notification
        		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification battlelogNotification = new Notification();
                battlelogNotification.icon = R.drawable.app_logo;
                battlelogNotification.when = System.currentTimeMillis();

                // Create a new intent despite the result
                Intent notificationIntent = new Intent(mContext, DashboardActivity.class).putExtra("openCOMCenter", true).putExtra("openTabId", 1);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

                // So... let's fix the singular/plural<insert something here>
                String text;
                if (mNumNotifications == 1) {
                    text = mContext.getString(R.string.info_txt_notification_new);
                } else {
                    text = mContext.getString(
                            R.string.info_txt_notification_new_p).replace(
                            "{num}", String.valueOf(mNumNotifications));
                }

                // Set the ticker (text scrolling up above when first notified)
                battlelogNotification.tickerText = mContext.getString(R.string.msg_notification);
                battlelogNotification.setLatestEventInfo(
                    mContext, text,
                    mContext.getString(R.string.info_tap_notification),
                    contentIntent
                );
                battlelogNotification.flags |= Notification.FLAG_AUTO_CANCEL;

                // Setup LED, sound & vibration
                if (mSharedPreferences.getBoolean("notification_light", true)) {
                    battlelogNotification.defaults |= Notification.DEFAULT_LIGHTS;
                }

                if (mSharedPreferences.getBoolean("notification_sound", true)) {
                    if (mSharedPreferences.getBoolean("notification_sound_special", true)) {
                        battlelogNotification.sound = Uri.parse(
                        	"android.resource://com.ninetwozero.battlelog/" +
                            R.raw.notification
                        );
                    } else {
                        battlelogNotification.defaults |= Notification.DEFAULT_SOUND;
                    }
                }

                if (mSharedPreferences.getBoolean("notification_vibrate", true)) {
                    battlelogNotification.vibrate = new long[]{100, 100};
                }
                notificationManager.notify(0, battlelogNotification);
            } else {
                Log.i(Constants.DEBUG_TAG, "No unread notifications");
            }
        } else {
            Log.i(Constants.DEBUG_TAG, "No valid session found.");
        	SharedPreferences.Editor spEditor = mSharedPreferences.edit();
        	spEditor.putString(Constants.SP_BL_COOKIE_NAME, "");
        	spEditor.putString(Constants.SP_BL_COOKIE_VALUE, "");
        	spEditor.commit();

        	// Cancel the same PendingIntent that we start in AsyncLogin.java
        	AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        	alarmManager.cancel(PendingIntent.getService(mContext, 0, new Intent(mContext, BattlelogService.class), 0));
        }

        // Stop the service!!
        ((Service) mContext).stopSelf();
    }
}
