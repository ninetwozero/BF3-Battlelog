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
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.DefaultActivity;
import com.ninetwozero.battlelog.http.CommentClient;
import com.ninetwozero.battlelog.misc.Constants;

public class AsyncCommentSend extends AsyncTask<String, Integer, Boolean> {

    // Attribute
    private Context context;
    private Button buttonSend;
    private long postId;
    private int type;

    // Constructor
    public AsyncCommentSend(Context c, long pId, int t, Button b) {

        context = c;
        postId = pId;
        type = t;
        buttonSend = b;

    }

    @Override
    protected void onPreExecute() {

        // Set the button
        buttonSend.setEnabled(false);
        buttonSend.setText(R.string.label_wait);

    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            // Did we manage?
            return (new CommentClient(postId, type).post(arg0[0], arg0[1]));

        } catch (Exception ex) {

            Log.e(Constants.DEBUG_TAG, "", ex);
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        // Reload
        if (context instanceof DefaultActivity) {

            ((DefaultActivity) context).reload();

        }

        // Set the button
        buttonSend.setEnabled(true);
        buttonSend.setText(R.string.label_send);

        if (results) {
            Toast.makeText(context, R.string.msg_comment_ok,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.msg_comment_fail,
                    Toast.LENGTH_SHORT).show();
        }
        return;

    }

}
