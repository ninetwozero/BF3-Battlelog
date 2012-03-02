
package com.ninetwozero.battlelog;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
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
import android.widget.Toast;

import com.ninetwozero.battlelog.asynctasks.AsyncSessionSetActive;
import com.ninetwozero.battlelog.asynctasks.AsyncSessionValidate;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.services.BattlelogService;

public class SettingsView extends PreferenceActivity {

    // Attributes
    private int originalInterval;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Did it get passed on?
        if (icicle != null && icicle.containsKey(Constants.SUPER_COOKIES)) {

            ArrayList<ShareableCookie> shareableCookies = icicle
                    .getParcelableArrayList(Constants.SUPER_COOKIES);

            if (shareableCookies != null) {

                RequestHandler.setCookies(shareableCookies);

            } else {

                finish();

            }

        }

        // Setup the locale
        setupLocale();

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
            startActivity(new Intent(this, Dashboard.class));
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
        setupLocale();

        // Setup the session
        setupSession();

    }

    public void setupSession() {

        // Let's set "active" against the website
        new AsyncSessionSetActive().execute();

        // If we don't have a profile...
        if (SessionKeeper.getProfileData() == null) {

            // ...but we do indeed have a cookie...
            if (!sharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE, "")
                    .equals("")) {

                // ...we set the SessionKeeper, but also reload the cookies!
                // Easy peasy!
                SessionKeeper
                        .setProfileData(SessionKeeper
                                .generateProfileDataFromSharedPreferences(sharedPreferences));
                RequestHandler.setCookies(

                        new ShareableCookie(

                                sharedPreferences.getString(Constants.SP_BL_COOKIE_NAME, ""),
                                sharedPreferences.getString(
                                        Constants.SP_BL_COOKIE_VALUE, ""),
                                Constants.COOKIE_DOMAIN

                        )

                        );

                // ...but just to be sure, we try to verify our session
                // "behind the scenes"
                new AsyncSessionValidate(this, sharedPreferences).execute();

            } else {

                // Aw man, that backfired.
                Toast.makeText(this, R.string.info_txt_session_lost,
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Main.class));
                finish();

            }

        }

    }

    public void setupLocale() {

        if (!sharedPreferences.getString(Constants.SP_BL_LANG, "").equals("")) {

            Locale locale = new Locale(sharedPreferences.getString(
                    Constants.SP_BL_LANG, "en"));
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config,
                    getResources().getDisplayMetrics());

        }

    }

}
