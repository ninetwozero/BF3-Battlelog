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
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.http.COMClient;

public class AsyncFriendRequest extends AsyncTask<String, Integer, Boolean> {

    // Attribute
    private Context context;
    private long profileId;

    // Constructor
    public AsyncFriendRequest(Context c, long pId) {

        this.context = c;
        this.profileId = pId;

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            return new COMClient(arg0[0]).sendFriendRequest(profileId);

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        if (results) {

            Toast.makeText(this.context, R.string.msg_frequest_ok,
                    Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this.context, R.string.msg_frequest_fail,
                    Toast.LENGTH_SHORT).show();

        }

    }

}
