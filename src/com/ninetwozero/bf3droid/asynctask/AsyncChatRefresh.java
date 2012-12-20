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

package com.ninetwozero.bf3droid.asynctask;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.social.ChatActivity;
import com.ninetwozero.bf3droid.adapter.ChatListAdapter;
import com.ninetwozero.bf3droid.datatype.ChatSession;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.http.COMClient;

public class AsyncChatRefresh extends AsyncTask<COMClient, Integer, Boolean> {

    // Attribute
    private Context mContext;
    private ChatSession mChat;
    private ListView mListView;

    // Constructor
    public AsyncChatRefresh(Context c, COMClient cc) {
    	mContext = c;
        mListView = ((ListActivity) mContext).getListView();
    }
    public AsyncChatRefresh(Context c, COMClient cc, ListView lv) {
        mContext = c;
        mListView = lv;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(COMClient... chat) {
        try {
            mChat = chat[0].getChat();
            return mChat != null;
        } catch (WebsiteHandlerException e) {
        	e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean results) {
        if (results) {
            ((ChatListAdapter) mListView.getAdapter()).setMessages(mChat.getMessages());
            if (mContext instanceof ChatActivity) {
                ((ChatActivity) mContext).notifyNewPost(mChat.getMessages());
            }
        } else {
            Toast.makeText(mContext, R.string.msg_chat_norefresh, Toast.LENGTH_SHORT).show();
        }
    }
}
