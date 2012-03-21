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

package com.ninetwozero.battlelog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class PlatoonCreateActivity extends Activity {

    // SharedPreferences for shizzle
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;

    // Elements
    private EditText textName, textTag;
    private CheckedTextView checkboxActive;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);
        PublicUtils.restoreCookies(this, icicle);

        // Set sharedPreferences & LayoutInflater
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Set the content view
        setContentView(R.layout.platoon_create_view);

        // Init the fields
        initActivity();

    }

    public void initActivity() {

        textName = (EditText) findViewById(R.id.text_name);
        textTag = (EditText) findViewById(R.id.text_tag);
        checkboxActive = (CheckedTextView) findViewById(R.id.checkbox_active);
   

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);

    }

    public void onClick(View v) {

        if (v.getId() == R.id.button_create) {

            // Let's get the values
            String stringName = textName.getText().toString();
            String stringTag = textTag.getText().toString();
            String stringActive = checkboxActive.isChecked() ? "1" : "0";

            // Validate
            if (stringName.equals("")) {

                Toast.makeText(this, R.string.info_platoon_new_empty_name,
                        Toast.LENGTH_SHORT).show();
                return;

            } else if (stringTag.equals("")) {

                Toast.makeText(this, R.string.info_platoon_new_empty_tag, Toast.LENGTH_SHORT)
                        .show();
                return;

            }

            // Actually fire off the AsyncTask to create the platoon
            new AsyncCreate().execute(stringName, stringTag, stringActive,
                    sharedPreferences.getString(Constants.SP_BL_CHECKSUM, ""));

        }

    }

    private class AsyncCreate extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... arg0) {

            return WebsiteHandler.createNewPlatoon(arg0[0], arg0[1], arg0[2], arg0[3]);

        }

    }

}
