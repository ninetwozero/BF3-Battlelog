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

package com.ninetwozero.battlelog.activity.platoon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.http.PlatoonClient;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class PlatoonCreateActivity extends Activity {

    // SharedPreferences for shizzles
    private SharedPreferences sharedPreferences;

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
        checkboxActive.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });

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
            new AsyncCreate(this).execute(stringName, stringTag, stringActive,
                    sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));

        }

    }

    private class AsyncCreate extends AsyncTask<String, Void, Boolean> {

        // Attributes
        private Context context;
        private ProgressDialog progressDialog;

        public AsyncCreate(Context c) {

            context = c;

        }

        @Override
        protected void onPreExecute() {

            if (context != null) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle(R.string.general_wait);
                progressDialog.setMessage(context.getString(R.string.info_platoon_new_creating));
                progressDialog.show();

            }

        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            return PlatoonClient.create(arg0[0], arg0[1], arg0[2], arg0[3]);

        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (context != null) {

                // So... what's up?
                if (result) {

                    Toast.makeText(context, R.string.info_platoon_new_true, Toast.LENGTH_SHORT)
                            .show();
                    ((Activity) context).finish();

                } else {

                    Toast.makeText(context, R.string.info_platoon_new_false, Toast.LENGTH_SHORT)
                            .show();

                }

                progressDialog.dismiss();

            }

        }

    }

}
