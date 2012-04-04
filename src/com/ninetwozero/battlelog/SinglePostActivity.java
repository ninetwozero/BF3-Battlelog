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
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.asynctasks.AsyncCommentSend;
import com.ninetwozero.battlelog.asynctasks.AsyncCommentsRefresh;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.NotificationData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class SinglePostActivity extends ListActivity {

    // Attributes
    private Intent intent;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    private FeedItem item;
    private NotificationData notification;

    // Elements
    private ListView listView;
    private TextView textHeading, textTitle, textContent, textDate;
    private ImageView imageAvatar;
    private EditText fieldMessage;
    private Button buttonSend;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PublicUtils.restoreCookies(this, icicle);

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Set the content view
        setContentView(R.layout.single_post_view);

        // Prepare to tango
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the ListView
        listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        // Let's grab the intent
        intent = getIntent();
        if (intent.hasExtra("feedItem")) {
            item = intent.getParcelableExtra("feedItem");
        }
        if (intent.hasExtra("notification")) {
            notification = intent.getParcelableExtra("notification");
        }

        // Do we have what we need?
        if (item == null && notification == null) {

            Toast.makeText(this, R.string.general_no_data, Toast.LENGTH_SHORT)
                    .show();
            return;

        }

        // Get data if we need it via the id
        if (notification != null) {

            // Get data here async
            new AsyncGetDataForPost(this).execute(notification);

        } else {

            setupLayout(item);

        }

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

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);

        // We need to reload
        if (item != null) {
            reload();
        }

    }

    public void reload() {

        if (item != null) {
            new AsyncCommentsRefresh(this, listView, layoutInflater)
                    .execute(item.getId());
        }

    }

    public void onClick(View v) {

        // Send?
        if (v.getId() == R.id.button_send) {

            // Send it!
            new AsyncCommentSend(

                    this, item.getId(), buttonSend, false, new AsyncCommentsRefresh(
                            this, listView, layoutInflater)

            ).execute(

                    sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""),
                    fieldMessage.getText().toString()

                    );

            // Clear the field
            fieldMessage.setText("");

        } else if (v.getId() == R.id.image_avatar) {

            startActivity(new Intent(this, ProfileActivity.class).putExtra(
                    "profile", (ProfileData) v.getTag()));

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
        if (item.getItemId() == R.id.option_reload) {

            this.reload();

        } else if (item.getItemId() == R.id.option_back) {

            ((Activity) this).finish();

        }

        // Return true yo
        return true;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        // Show the menu
        // menu.add(0, 0, 0, "Report comment");

        return;

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // Declare...
        AdapterView.AdapterContextMenuInfo info;

        // Let's try to get some menu information via a try/catch
        try {

            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        } catch (ClassCastException e) {

            e.printStackTrace();
            return false;

        }

        try {

            // Divide & conquer
            if (item.getGroupId() == 0) {

                // REQUESTS
                if (item.getItemId() == 0) {

                    /*
                     * new AsyncCommentReport(this, info.id, false, new
                     * AsyncProfileRefresh(this, true, profileData)).execute(
                     * sharedPreferences.getString( Constants.SP_BL_CHECKSUM, ""
                     * ) );
                     */
                    Toast.makeText(this,
                            R.string.info_comment_report + item.getItemId(),
                            Toast.LENGTH_SHORT).show();

                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

        return true;

    }

    public void checkCommentRights() {

        // Is the user allowed to post?
        if (!intent.getBooleanExtra("canComment", false)) {

            if (SessionKeeper.getProfileData().getId() != intent
                    .getLongExtra("profileId", 0)) {

                buttonSend.setVisibility(View.GONE);
                fieldMessage.setVisibility(View.GONE);

                return;

            }

        }

        if (item == null || item.getType().equals("wroteforumpost")
                || item.getType().equals("createdforumthread")) {

            buttonSend.setVisibility(View.GONE);
            fieldMessage.setVisibility(View.GONE);

        } else {

            buttonSend.setVisibility(View.VISIBLE);
            fieldMessage.setVisibility(View.VISIBLE);

        }

    }

    public class AsyncGetDataForPost extends
            AsyncTask<NotificationData, Void, Boolean> {

        // Attributes
        private Context context;

        // Constructs
        public AsyncGetDataForPost(Context c) {

            this.context = c;

        }

        @Override
        protected Boolean doInBackground(NotificationData... arg0) {

            try {

                item = WebsiteHandler.getPostForNotification(arg0[0]);
                return true;

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            // Let's see...
            if (results) {

                setupLayout(item);
                reload();

            }

        }

    }

    public void setupLayout(FeedItem feedItem) {

        // Let's do this
        if (feedItem == null) {

            Toast.makeText(this, R.string.general_no_data, Toast.LENGTH_SHORT)
                    .show();
            return;

        }

        // Get the fields
        if (textHeading == null) {
            textHeading = (TextView) findViewById(R.id.text_heading);
        }
        if (textTitle == null) {
            textTitle = (TextView) findViewById(R.id.text_title);
        }
        if (textContent == null) {
            textContent = (TextView) findViewById(R.id.text_content);
        }
        if (textDate == null) {
            textDate = (TextView) findViewById(R.id.text_date);
        }
        if (fieldMessage == null) {
            fieldMessage = (EditText) findViewById(R.id.field_message);
        }
        if (buttonSend == null) {
            buttonSend = (Button) findViewById(R.id.button_send);
        }
        if (imageAvatar == null) {

            imageAvatar = (ImageView) findViewById(R.id.image_avatar);
        }

        // Set up the top-place
        String username = "";
        if (feedItem.getProfile(1) == null) {

            username = feedItem.getProfile(0).getUsername();

        } else {

            username = feedItem.getProfile(1).getUsername();

        }

        if (!username.endsWith("s")) {

            textHeading.setText(getString(R.string.info_spost_heading).replace(
                    "{username}", username));

        } else {

            textHeading.setText(getString(R.string.info_spost_heading_s)
                    .replace("{username}", username));

        }

        textTitle.setText(Html.fromHtml(feedItem.getTitle()));
        textDate.setText(PublicUtils.getRelativeDate(this, feedItem.getDate()));

        // Handle the content
        if (feedItem.getContent() != null && feedItem.getContent().length() > 0) {

            textContent.setText(Html.fromHtml(feedItem.getContent()));
            textContent.setVisibility(View.VISIBLE);
            textContent.setMovementMethod(LinkMovementMethod.getInstance());

        } else {

            textContent.setVisibility(View.GONE);

        }

        imageAvatar.setImageBitmap(BitmapFactory.decodeFile(PublicUtils.getCachePath(this)
                + feedItem.getAvatarForPost() + ".png"));

        // Validate comment rights
        checkCommentRights();

    }

}
