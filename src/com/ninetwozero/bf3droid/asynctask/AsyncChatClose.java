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

import android.os.AsyncTask;
import com.ninetwozero.bf3droid.http.COMClient;

public class AsyncChatClose extends AsyncTask<Void, Integer, Boolean> {

    // Attribute
    private long chatId;

    // Constructor
    public AsyncChatClose(long cId) {

        chatId = cId;

    }

    @Override
    protected Boolean doInBackground(Void... arg0) {

        try {

            // Did we manage?
            return COMClient.closeChat(chatId);

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

}
