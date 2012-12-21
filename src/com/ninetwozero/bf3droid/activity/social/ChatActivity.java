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

package com.ninetwozero.bf3droid.activity.social;

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

public class ChatActivity extends ListActivity {

    // Attributes
	private COMClient mComClient;
    private ProfileData mActiveUser;
    private ProfileData mOtherUser;
    private SharedPreferences mSharedPreferences;
    private LayoutInflater mLayoutInflater;

    // Elements
    private ListView mListView;
    private EditText mFieldMessage;
    private Button mButtonSend;

    // Current "last" message
    private long mLatestChatResponseTimestamp;

    // Misc
    private Timer mTimerReload;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        
        // Needs to be run prior to setting the content view
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PublicUtils.restoreCookies(this, icicle);
        PublicUtils.setupFullscreen(this, mSharedPreferences);
        PublicUtils.setupLocale(this, mSharedPreferences);
        
        // Finally set the view
        setContentView(R.layout.activity_chat);
        
        // Did we get someone to chat with?
        if (!getIntent().hasExtra("activeUser") || !getIntent().hasExtra("otherUser")) {
            finish();
        }
        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Let's get the other chat participant
        mActiveUser = (ProfileData) getIntent().getParcelableExtra("activeUser");
        mOtherUser = (ProfileData) getIntent().getParcelableExtra("otherUser");

        // Get the ListView
        mListView = getListView();
        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        mListView.setAdapter(new ChatListAdapter(this, null, mActiveUser.getUsername(), mLayoutInflater));

        // Setup the title
        setTitle(getTitle().toString().replace("...", mOtherUser.getUsername()));

        // Get the elements
        mButtonSend = (Button) findViewById(R.id.button_send);
        mFieldMessage = (EditText) findViewById(R.id.field_message);

        // Try to get the chatid
        mComClient = new COMClient(
    		mOtherUser.getId(), 
    		mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")
		);
    }

    @Override
    public void onResume() {
        super.onResume();
        PublicUtils.setupLocale(this, mSharedPreferences);
        PublicUtils.setupSession(this, mSharedPreferences);
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
        if (mTimerReload != null) {
            mTimerReload.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new AsyncChatClose(mComClient.getChatId()).execute();
    }

    public void reload() {
        new AsyncChatRefresh(this, mComClient).execute(mComClient);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.button_send) {
        	new AsyncChatSend(this, mComClient).execute(
                mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""),
                mFieldMessage.getText().toString()
            );
            mFieldMessage.setText("");
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
        boolean playSound = mSharedPreferences.getBoolean("battlelog_chat_sound", true);

        for (int curr = cm.size() - 1, min = ((curr > 5) ? curr - 5 : 0); curr > min; curr--) {
            ChatMessage m = cm.get(curr);
            if (m.getSender().equals(mOtherUser.getUsername()) && m.getTimestamp() > mLatestChatResponseTimestamp) {
                hasNewResponse = true;
                isFirstRun = (mLatestChatResponseTimestamp == 0);
                mLatestChatResponseTimestamp = m.getTimestamp();
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
            mListView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        mListView.setSelection(mListView.getAdapter().getCount() - 1);
                    }
                }
            );
        }
    }
    
    public void prePostMode() {
       	mButtonSend.setEnabled(false);
        mButtonSend.setText(R.string.label_sending);
    }
    
    public void postPostMode() {
       	mButtonSend.setEnabled(true);
        mButtonSend.setText(R.string.label_send);
        reload();
    }

    public void setupTimer() {
        if (PublicUtils.isNetworkAvailable(this) && mTimerReload == null) {
            mTimerReload = new Timer();
            mTimerReload.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        reload();
                    }
                }, 
                0, 
                mSharedPreferences.getInt(Constants.SP_BL_INTERVAL_CHAT, 25) * 1000
            );
        }
    }
}
