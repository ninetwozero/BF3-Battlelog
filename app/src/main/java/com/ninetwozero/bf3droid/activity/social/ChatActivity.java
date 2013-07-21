/*
	This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.activity.social;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.adapter.ChatListAdapter;
import com.ninetwozero.bf3droid.asynctask.AsyncChatClose;
import com.ninetwozero.bf3droid.asynctask.AsyncChatRefresh;
import com.ninetwozero.bf3droid.asynctask.AsyncChatSend;
import com.ninetwozero.bf3droid.datatype.ChatMessage;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.http.COMClient;
import com.ninetwozero.bf3droid.http.RequestHandler;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends ListActivity {

	private COMClient comClient;
    private ProfileData activeUser;
    private ProfileData otherUser;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;

    private ListView listView;
    private EditText messageField;
    private Button sendButton;

    private long latestChatResponseTimestamp;

    // Misc
    private Timer timerReload;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PublicUtils.restoreCookies(this, icicle);
        PublicUtils.setupFullscreen(this, sharedPreferences);
        PublicUtils.setupLocale(this, sharedPreferences);
        setContentView(R.layout.activity_chat);

        if (!getIntent().hasExtra("activeUser") || !getIntent().hasExtra("otherUser")) {
            finish();
        }
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        activeUser = (ProfileData) getIntent().getParcelableExtra("activeUser");
        otherUser = (ProfileData) getIntent().getParcelableExtra("otherUser");

        listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        listView.setAdapter(new ChatListAdapter(this, null, BF3Droid.getUser().getName(), layoutInflater));

        setTitle(getTitle().toString().replace("...", otherUser.getUsername()));

        sendButton = (Button) findViewById(R.id.button_send);
        messageField = (EditText) findViewById(R.id.field_message);

        comClient = new COMClient(otherUser.getId(), BF3Droid.getCheckSum());
    }

    @Override
    public void onResume() {
        super.onResume();
        PublicUtils.setupLocale(this, sharedPreferences);
        PublicUtils.setupSession(this, sharedPreferences);
        setupTimer();
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
        new AsyncChatClose(comClient.getChatId()).execute();
    }

    public void reload() {
        new AsyncChatRefresh(this, comClient).execute(comClient);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.button_send) {
        	new AsyncChatSend(this, comClient).execute(
                    sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""),
                    messageField.getText().toString()
            );
            messageField.setText("");
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
        if (item.getItemId() == R.id.option_reload) {
            this.reload();
        } else if (item.getItemId() == R.id.option_back) {
            ((Activity) this).finish();
        }
        return true;
    }

    public void notifyNewPost(List<ChatMessage> cm) {
        boolean hasNewResponse = false;
        boolean isFirstRun = true;
        boolean playSound = sharedPreferences.getBoolean("battlelog_chat_sound", true);

        for (int curr = cm.size() - 1, min = ((curr > 5) ? curr - 5 : 0); curr > min; curr--) {
            ChatMessage m = cm.get(curr);
            if (m.getSender().equals(otherUser.getUsername()) && m.getTimestamp() > latestChatResponseTimestamp) {
                hasNewResponse = true;
                isFirstRun = (latestChatResponseTimestamp == 0);
                latestChatResponseTimestamp = m.getTimestamp();
                break;
            }
        }

        if (hasNewResponse && !isFirstRun && playSound) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.notification);
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
            listView.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            listView.setSelection(listView.getAdapter().getCount() - 1);
                        }
                    }
            );
        }
    }
    
    public void prePostMode() {
       	sendButton.setEnabled(false);
        sendButton.setText(R.string.label_sending);
    }
    
    public void postPostMode() {
       	sendButton.setEnabled(true);
        sendButton.setText(R.string.label_send);
        reload();
    }

    public void setupTimer() {
        if (PublicUtils.isNetworkAvailable(this) && timerReload == null) {
            timerReload = new Timer();
            timerReload.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        reload();
                    }
                }, 
                0, 
                sharedPreferences.getInt(Constants.SP_BL_INTERVAL_CHAT, 25) * 1000
            );
        }
    }
}
