package com.ninetwozero.battlelog.broadcastreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.service.BattlelogService;

public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // We'll have to get the AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get("networkInfo");

        // Cancel it first of all
        alarmManager.cancel(PendingIntent.getService(context, 0, new Intent(context,
                BattlelogService.class), 0));

        // Do we have a connection? Yes? Alright! Let's start the service again
        if (networkInfo.isConnected()) {

            // Default SP
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);

            // Let's see if we have a password to use if needing to login
            if (!sharedPreferences.getString(Constants.SP_BL_PROFILE_PASSWORD, "").equals("")) {

                // Set the alarm
                alarmManager.setInexactRepeating(

                        0,
                        0,
                        sharedPreferences.getInt(Constants.SP_BL_INTERVAL_SERVICE, 0),
                        PendingIntent.getService(context, 0, new Intent(context,
                                BattlelogService.class), 0)

                );

            }

        }

    }

}
