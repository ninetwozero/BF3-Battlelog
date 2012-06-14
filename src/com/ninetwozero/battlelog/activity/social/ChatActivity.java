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

package com.ninetwozero.battlelog.activity.social;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
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

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapter.ChatListAdapter;
import com.ninetwozero.battlelog.asynctask.AsyncChatClose;
import com.ninetwozero.battlelog.asynctask.AsyncChatRefresh;
import com.ninetwozero.battlelog.asynctask.AsyncChatSend;
import com.ninetwozero.battlelog.datatype.ChatMessage;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.COMClient;
import com.ninetwozero.battlelog.http.RequestHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class ChatActivity extends ListActivity {

    // Attributes
    private long chatId;
    private ProfileData activeUser;
    private ProfileData otherUser;
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
        PublicUtils.restoreCookies(this, icicle);

        // Did we get someone to chat with?
        if (!getIntent().hasExtra("activeUser") || !getIntent().hasExtra("otherUser") ) {
            finish();
        }

        // Setup important stuff
        PublicUtils.setupFullscreen(this, sharedPreferences);
        PublicUtils.setupLocale(this, sharedPreferences);

        // Set the content view
        setContentView(R.layout.chat_view);

        // Prepare to tango
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Let's get the other chat participant
        activeUser = (ProfileData) getIntent().getParcelableExtra("activeUser");
        otherUser = (ProfileData) getIntent().getParcelableExtra("otherUser");

        // Get the ListView
        listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        listView.setAdapter(new ChatListAdapter(this, null, activeUser.getUsername(), layoutInflater));
        
        // Setup the title
        setTitle(getTitle().toString().replace("...", otherUser.getUsername()));

        // Get the elements
        buttonSend = (Button) findViewById(R.id.button_send);
        fieldMessage = (EditText) findViewById(R.id.field_message);

        // Try to get the chatid
        new AsyncGetChatId(activeUser.getId()).execute(sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);

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

                chatId = new COMClient(arg0[0]).getChatId(profileId);
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
        new AsyncChatClose(chatId).execute();

    }

    public void reload() {

        new AsyncChatRefresh(this, listView,
                layoutInflater).execute(otherUser.getId());

    }

    public void onClick(View v) {

        // Send?
        if (v.getId() == R.id.button_send) {

            // Send it!
            new AsyncChatSend(

                    this, activeUser.getId(), chatId, buttonSend, false,
                    new AsyncChatRefresh(this, listView, layoutInflater)

            ).execute(

                    sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""),
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

    public void notifyNewPost(List<ChatMessage> cm) {

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
            if (m.getSender().equals(activeUser.getUsername())) {

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

}
