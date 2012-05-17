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

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.handlers.COMHandler;

public class AsyncChatSend extends AsyncTask<String, Integer, Boolean> {

    // Attribute
    Context context;
    Button buttonSend;
    long chatId, profileId;
    boolean fromWidget;
    String httpContent;
    AsyncChatRefresh asyncChatRefresh;

    // Constructor
    public AsyncChatSend(Context c, long cId, Button b, boolean w,
            AsyncChatRefresh acr) {

        this.context = c;
        this.chatId = cId;
        this.profileId = 0;
        this.fromWidget = w;
        this.buttonSend = b;
        this.asyncChatRefresh = acr;

    }

    public AsyncChatSend(Context c, long pId, long cId, Button b, boolean w,
            AsyncChatRefresh acr) {

        this(c, cId, b, w, acr);
        this.profileId = pId;

    }

    @Override
    protected void onPreExecute() {

        // Let's see
        if (!fromWidget) {

            buttonSend.setEnabled(false);
            buttonSend.setText(R.string.label_sending);

        }

    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            // Did we manage?
            if (COMHandler.sendMessage(chatId, arg0[0], arg0[1])) {
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        if (!fromWidget) {

            buttonSend.setEnabled(true);
            buttonSend.setText(R.string.label_send);

            // Reload
            asyncChatRefresh.execute(profileId);

            if (results)
                Toast.makeText(context, R.string.msg_chat_ok,
                        Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, R.string.msg_chat_fail,
                        Toast.LENGTH_SHORT).show();

        }
        return;

    }

}
