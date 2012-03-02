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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.ChatListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncChatClose;
import com.ninetwozero.battlelog.asynctasks.AsyncChatRefresh;
import com.ninetwozero.battlelog.asynctasks.AsyncChatSend;
import com.ninetwozero.battlelog.asynctasks.AsyncSessionSetActive;
import com.ninetwozero.battlelog.asynctasks.AsyncSessionValidate;
import com.ninetwozero.battlelog.datatypes.ChatMessage;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class ChatView extends ListActivity {

    // Attributes
    private long chatId;
    private ProfileData profileData;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;

    // Elements
    private ListView listView;
    private EditText fieldMessage;
    private Button buttonSend;

    // Current "last" message
    private long latestChatResponseTimestamp;

    // Misc
    private Timer timerReload;

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

        // Did we get someone to chat with?
        if (!getIntent().hasExtra("profile")) {
            finish();
        }

        // Setup the locale
        setupLocale();

        // Set the content view
        setContentView(R.layout.chat_view);

        // Prepare to tango
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the ListView
        listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        listView.setAdapter(new ChatListAdapter(this, null, SessionKeeper
                .getProfileData().getAccountName(), layoutInflater));

        // Let's get the other chat participant
        profileData = (ProfileData) getIntent().getParcelableExtra("profile");

        // Setup the title
        setTitle(getTitle().toString().replace("...",
                profileData.getAccountName()));

        // Get the elements
        buttonSend = (Button) findViewById(R.id.button_send);
        fieldMessage = (EditText) findViewById(R.id.field_message);

        // Try to get the chatid
        new AsyncGetChatId(profileData.getProfileId())
                .execute(sharedPreferences.getString(Constants.SP_BL_CHECKSUM,
                        ""));

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        setupLocale();

        // Setup the session
        setupSession();

        // We need to setup the timer
        setupTimer();

    }

    public class AsyncGetChatId extends AsyncTask<String, Void, Boolean> {

        // Attributes
        private long chatId;
        private long profileId;

        // Construct
        public AsyncGetChatId(long pId) {
            this.profileId = pId;
        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {

                this.chatId = WebsiteHandler.getChatId(profileId, arg0[0]);
                return true;

            } catch (WebsiteHandlerException ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            // If we succeeded, we're ok with this
            if (results) {

                setChatId(chatId);
                buttonSend.setEnabled(true);

            }

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
    public void onPause() {

        super.onPause();
        if (timerReload != null) {
            timerReload.cancel();
        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        new AsyncChatClose(this, chatId).execute();

    }

    public void reload() {

        new AsyncChatRefresh(this, listView, profileData.getAccountName(),
                layoutInflater).execute(profileData.getProfileId());

    }

    public void onClick(View v) {

        // Send?
        if (v.getId() == R.id.button_send) {

            // Send it!
            new AsyncChatSend(

                    this, profileData.getProfileId(), chatId, buttonSend, false,
                    new AsyncChatRefresh(this, listView,
                            profileData.getAccountName(), layoutInflater)

            ).execute(

                    sharedPreferences.getString(Constants.SP_BL_CHECKSUM, ""),
                    fieldMessage.getText().toString()

                    );

            // Clear the field
            fieldMessage.setText("");

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

    public void setChatId(long cId) {
        this.chatId = cId;
    }

    public void notifyNewPost(ArrayList<ChatMessage> cm) {

        // Let's see...
        if (!sharedPreferences.getBoolean("battlelog_chat_sound", true)) {
            return;
        }

        // Init!
        boolean hasNewResponse = false;
        boolean isFirstRun = true;

        // Iterate
        for (int curr = cm.size() - 1, min = ((curr > 5) ? curr - 5 : 0); curr > min; curr--) {

            // Let's see what happens.
            ChatMessage m = cm.get(curr);
            if (m.getSender().equals(profileData.getAccountName())) {

                // Ooh, ooh, is it fresh?
                if (m.getTimestamp() > latestChatResponseTimestamp) {

                    hasNewResponse = true;
                    isFirstRun = (latestChatResponseTimestamp == 0);
                    latestChatResponseTimestamp = m.getTimestamp();
                    break;

                }

            }

        }

        // So, did we have a new response?
        if (hasNewResponse && !isFirstRun) {

            MediaPlayer mediaPlayer = MediaPlayer.create(this,
                    R.raw.notification);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(

                    new OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer arg0) {
                            arg0.release();
                        }

                    }

                    );

        } else if (isFirstRun) {

            // Scroll down to the bottom
            listView.post(

                    new Runnable() {

                        @Override
                        public void run() {
                            listView.setSelection(listView.getAdapter().getCount() - 1);

                        }

                    }

                    );

        }

        return;

    }

    public void setupTimer() {

        // Do we have a connection?
        if (PublicUtils.isNetworkAvailable(this)) {

            if (timerReload == null) {

                // Let's reload the chat will we?
                timerReload = new Timer();
                timerReload
                        .schedule(

                                new TimerTask() {

                                    @Override
                                    public void run() {

                                        reload();

                                    }
                                }, 0, sharedPreferences.getInt(Constants.SP_BL_INTERVAL_CHAT,
                                        25) * 1000

                        );

            }

        }

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
