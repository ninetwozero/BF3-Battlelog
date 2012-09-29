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
    private SharedPreferences mSharedPreferences;

    // Elements
    private EditText mTextName;
    private EditText mTextTag;
    private CheckedTextView mCheckboxActive;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);
        PublicUtils.restoreCookies(this, icicle);

        // Set sharedPreferences & LayoutInflater
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Setup the locale
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Set the content view
        setContentView(R.layout.platoon_create_view);

        // Init the fields
        initActivity();

    }

    public void initActivity() {

        mTextName = (EditText) findViewById(R.id.text_name);
        mTextTag = (EditText) findViewById(R.id.text_tag);
        mCheckboxActive = (CheckedTextView) findViewById(R.id.checkbox_active);
        mCheckboxActive.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, mSharedPreferences);

    }

    public void onClick(View v) {

        if (v.getId() == R.id.button_create) {

            // Let's get the values
            String stringName = mTextName.getText().toString();
            String stringTag = mTextTag.getText().toString();
            String stringActive = mCheckboxActive.isChecked() ? "1" : "0";

            // Validate
            if ("".equals(stringName)) {

                Toast.makeText(this, R.string.info_platoon_new_empty_name,
                        Toast.LENGTH_SHORT).show();

            } else if ("".equals(stringTag)) {

                Toast.makeText(this, R.string.info_platoon_new_empty_tag, Toast.LENGTH_SHORT)
                        .show();

            }

            // Actually fire off the AsyncTask to create the platoon
            new AsyncCreate(this).execute(stringName, stringTag, stringActive,
                    mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));

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
