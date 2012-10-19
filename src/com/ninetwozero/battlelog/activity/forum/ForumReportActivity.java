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

package com.ninetwozero.battlelog.activity.forum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.http.ForumClient;
import com.ninetwozero.battlelog.http.RequestHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class ForumReportActivity extends Activity {

    // Attributes
    private SharedPreferences mSharedPreferences;

    // Elements
    private EditText mFieldReport;
    private Button mButtonReport;

    // Misc
    private long mPostId;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PublicUtils.restoreCookies(this, icicle);

        // Do we have a postId?
        if (!getIntent().hasExtra("postId")) {

            finish();

        }
        mPostId = getIntent().getLongExtra("postId", 0);

        // Setup the locale
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Set the content view
        setContentView(R.layout.activity_forum_report);

        // Get the elements
        mButtonReport = (Button) findViewById(R.id.button_report);
        mFieldReport = (EditText) findViewById(R.id.field_reason);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SUPER_COOKIES,
                RequestHandler.getCookies());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onClick(View v) {

        // Send?
        if (v.getId() == R.id.button_report) {

            // Get & validate
            String reason = mFieldReport.getText().toString();
            if ("".equals(reason)) {

                Toast.makeText(this, R.string.info_forum_report_error,
                        Toast.LENGTH_SHORT).show();

            } else {

                new AsyncReportPost(this, mPostId).execute(reason);

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_basic, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Let's act!
        if (item.getItemId() == R.id.option_back) {

            ((Activity) this).finish();

        }

        // Return true yo
        return true;

    }

    public class AsyncReportPost extends AsyncTask<String, Void, Boolean> {

        // Attributes
        private final Context context;
        private final long postId;

        // Constructs
        public AsyncReportPost(Context c, long p) {

            this.context = c;
            this.postId = p;

        }

        @Override
        protected void onPreExecute() {

            if (context instanceof ForumReportActivity) {

                mButtonReport.setText(R.string.info_report_active);
                mButtonReport.setEnabled(false);

            }

        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {

                return ForumClient.reportPost(context, postId, arg0[0]);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (context instanceof ForumReportActivity) {

                if (results) {

                    Toast.makeText(context, R.string.info_forum_report_true,
                            Toast.LENGTH_SHORT).show();
                    ((ForumReportActivity) context).finish();

                } else {

                    Toast.makeText(context, R.string.info_forum_report_false,
                            Toast.LENGTH_SHORT).show();
                    mButtonReport.setText(R.string.label_report);
                    mButtonReport.setEnabled(true);
                }

            }

        }

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
