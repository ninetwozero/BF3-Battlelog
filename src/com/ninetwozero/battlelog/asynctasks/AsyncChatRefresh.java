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

package com.ninetwozero.battlelog.asynctasks;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.ChatActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapters.ChatListAdapter;
import com.ninetwozero.battlelog.datatypes.ChatMessage;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncChatRefresh extends AsyncTask<Long, Integer, Boolean> {

    // Attribute
    private Context context;
    private SharedPreferences sharedPreferences;
    private List<ChatMessage> messageArray = new ArrayList<ChatMessage>();
    private ListView listView;
    private LayoutInflater layoutInflater;
    private String username; // The user that's using the chat on "this" end

    // Constructor
    public AsyncChatRefresh(Context c, ListView lv, String u, LayoutInflater l) {

        this.context = c;
        this.listView = lv;
        this.layoutInflater = l;
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        this.username = u;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(Long... chatId) {

        try {

            // Let's get this!!
            messageArray = WebsiteHandler.getChatMessages(chatId[0],
                    sharedPreferences.getString(Constants.SP_BL_CHECKSUM, ""));
            return true;

        } catch (WebsiteHandlerException e) {

            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        // How did go?
        if (results) {

            // Set the almighty adapter
            ((ChatListAdapter) listView.getAdapter())
                    .setMessageArray(messageArray);

            // Do we need to ploop?
            if (context instanceof ChatActivity) {
                ((ChatActivity) context).notifyNewPost(messageArray);
            }

        } else {

            Toast.makeText(context, R.string.msg_chat_norefresh,
                    Toast.LENGTH_SHORT).show();

        }
        return;

    }

}
