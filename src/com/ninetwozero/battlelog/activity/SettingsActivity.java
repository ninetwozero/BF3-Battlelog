
package com.ninetwozero.battlelog.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.handlers.RequestHandler;
import com.ninetwozero.battlelog.services.BattlelogService;

public class SettingsActivity extends PreferenceActivity {

    // Attributes
    private int originalInterval;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PublicUtils.restoreCookies(this, icicle);

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Let's put 'em there
        addPreferencesFromResource(R.xml.settings_view);

        // Set the originalInterval
        originalInterval = sharedPreferences.getInt(
                Constants.SP_BL_INTERVAL_SERVICE, 0);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // If the new interval != the old interval, we got to restart the
            // "alarm"
            if (originalInterval != sharedPreferences.getInt(
                    Constants.SP_BL_INTERVAL_SERVICE, 0)) {

                // Get the interval
                int serviceInterval = sharedPreferences.getInt(
                        Constants.SP_BL_INTERVAL_SERVICE,
                        (Constants.HOUR_IN_SECONDS / 2)) * 1000;

                // Restart the AlarmManager
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                        new Intent(this, BattlelogService.class), 0);
                alarmManager.cancel(pendingIntent);
                alarmManager.setInexactRepeating(

                        AlarmManager.ELAPSED_REALTIME, 0, serviceInterval,
                        pendingIntent

                        );

                Log.d(Constants.DEBUG_TAG,
                        "Setting the service to update every "
                                + serviceInterval / 60000 + " minutes");
            }
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return true;

        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SUPER_COOKIES,
                RequestHandler.getCookies());

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);

    }

}
