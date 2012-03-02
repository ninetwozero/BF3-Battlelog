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

import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncChatClose extends AsyncTask<Void, Integer, Boolean> {

    // Attribute
    Context context;
    long chatId;
    String httpContent;

    // Constructor
    public AsyncChatClose(Context c, long cId) {

        this.context = c;
        this.chatId = cId;

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {

        try {

            // Did we manage?
            if (WebsiteHandler.closeChatWindow(chatId)) {
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
    }

}
