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

package com.ninetwozero.bf3droid.activity.platoon;

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
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.http.PlatoonClient;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;

public class PlatoonCreateActivity extends Activity {
    private SharedPreferences mSharedPreferences;

    private EditText mTextName;
    private EditText mTextTag;
    private CheckedTextView mCheckboxActive;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
       
        PublicUtils.restoreCookies(this, icicle);
        PublicUtils.setupLocale(this, mSharedPreferences);

        setContentView(R.layout.activity_platoon_create);
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
        PublicUtils.setupLocale(this, mSharedPreferences);
        PublicUtils.setupSession(this, mSharedPreferences);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.button_create) {
            String stringName = mTextName.getText().toString();
            String stringTag = mTextTag.getText().toString();
            String stringActive = mCheckboxActive.isChecked() ? "1" : "0";

            // Validate
            if ("".equals(stringName)) {
            	Toast.makeText(this, R.string.info_platoon_new_empty_name, Toast.LENGTH_SHORT).show();
            } else if ("".equals(stringTag)) {
                Toast.makeText(this, R.string.info_platoon_new_empty_tag, Toast.LENGTH_SHORT).show();
            }

            new AsyncCreate(this).execute(
        		stringName, 
        		stringTag, 
        		stringActive,
                mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")
            );
        }
    }

    private class AsyncCreate extends AsyncTask<String, Void, Boolean> {
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
        protected void onPostExecute(Boolean platoonCreated) {
            if (context != null) {
                progressDialog.dismiss();
                if (platoonCreated) {
                    Toast.makeText(context, R.string.info_platoon_new_true, Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                } else {
                    Toast.makeText(context, R.string.info_platoon_new_false, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
