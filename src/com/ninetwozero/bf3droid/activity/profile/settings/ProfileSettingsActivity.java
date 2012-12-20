package com.ninetwozero.bf3droid.activity.profile.settings;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.http.RequestHandler;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;

public class ProfileSettingsActivity extends PreferenceActivity {

    // Attributes
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);
        // TODO query battlelog for current set of settings

        // Set sharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PublicUtils.restoreCookies(this, icicle);

        // Setup the locale
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Let's put 'em there
        addPreferencesFromResource(R.xml.profile_settings_view);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // TODO: Here's where we have to update versus battlelog?
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
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, mSharedPreferences);

    }

}
