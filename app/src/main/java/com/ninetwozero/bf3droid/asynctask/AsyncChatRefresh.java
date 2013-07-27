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

    private Context context;
    private ChatSession chatSession;
    private ListView listView;

    public AsyncChatRefresh(Context c, COMClient cc) {
    	context = c;
        listView = ((ListActivity) context).getListView();
    }
    public AsyncChatRefresh(Context c, COMClient cc, ListView lv) {
        context = c;
        listView = lv;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(COMClient... chat) {
        try {
            chatSession = chat[0].getChat();
            return chatSession != null;
        } catch (WebsiteHandlerException e) {
        	e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean results) {
        if (results) {
            ((ChatListAdapter) listView.getAdapter()).setMessages(chatSession.getMessages());
            if (context instanceof ChatActivity) {
                ((ChatActivity) context).notifyNewPost(chatSession.getMessages());
            }
        } else {
            Toast.makeText(context, R.string.msg_chat_norefresh, Toast.LENGTH_SHORT).show();
        }
    }
}
