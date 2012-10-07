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
import android.widget.EditText;
import android.widget.Toast;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.feed.FeedFragment;
import com.ninetwozero.battlelog.http.COMClient;

public class AsyncStatusUpdate extends AsyncTask<String, Integer, Boolean> {

    // Attribute
    private Context context;
    private FeedFragment fragmentFeed;
    private Button buttonSend;

    // Constructor
    public AsyncStatusUpdate(Context c, FeedFragment f) {

        context = c;
        fragmentFeed = f;

    }

    @Override
    protected void onPreExecute() {

        if (context != null) {

            Toast.makeText(context, R.string.msg_status, Toast.LENGTH_SHORT)
                    .show();

            buttonSend = (Button) fragmentFeed.getView().findViewById(
                    R.id.button_send);
            buttonSend.setEnabled(false);

        }

    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {


            return new COMClient(arg0[1]).updateStatus(arg0[0]);

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        if (context != null) {

            if (results) {

                // Yay
                Toast.makeText(context, R.string.msg_status_ok,
                        Toast.LENGTH_SHORT).show();
                ((EditText) fragmentFeed.getView().findViewById(
                        R.id.field_message)).setText("");
                buttonSend.setEnabled(true);

            } else {

                Toast.makeText(context, R.string.msg_status_fail,
                        Toast.LENGTH_SHORT).show();

            }

            // Reload
            fragmentFeed.reload();

        }

    }

}
