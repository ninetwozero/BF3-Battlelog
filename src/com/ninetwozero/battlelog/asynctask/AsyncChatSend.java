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

package com.ninetwozero.battlelog.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.http.COMClient;

public class AsyncChatSend extends AsyncTask<String, Integer, Boolean> {

    // Attribute
    private Context context;
    private Button buttonSend;
    private long chatId, profileId;
    private boolean fromWidget;
    private AsyncChatRefresh asyncChatRefresh;

    // Constructor
    public AsyncChatSend(Context c, long cId, Button b, boolean w,
            AsyncChatRefresh acr) {

        context = c;
        chatId = cId;
        profileId = 0;
        fromWidget = w;
        buttonSend = b;
        asyncChatRefresh = acr;

    }

    public AsyncChatSend(Context c, long pId, long cId, Button b, boolean w,
            AsyncChatRefresh acr) {

        this(c, cId, b, w, acr);
        profileId = pId;

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
            return new COMClient(arg0[0]).sendMessage(chatId, arg0[1]);

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

            if (results) {
                Toast.makeText(context, R.string.msg_chat_ok,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.msg_chat_fail,
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

}
