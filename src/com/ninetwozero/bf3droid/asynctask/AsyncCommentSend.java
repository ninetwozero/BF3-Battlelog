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

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.Toast;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.DefaultFragmentActivity;
import com.ninetwozero.bf3droid.http.CommentClient;

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
        buttonSend.setEnabled(false);
        buttonSend.setText(R.string.label_wait);
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        try {
            return (new CommentClient(postId, type).post(arg0[0], arg0[1]));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean results) {
        if (context instanceof DefaultFragmentActivity) {
            ((DefaultFragmentActivity) context).reload();
        }

        buttonSend.setEnabled(true);
        buttonSend.setText(R.string.label_send);

        if (results) {
            Toast.makeText(context, R.string.msg_comment_ok, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.msg_comment_fail, Toast.LENGTH_SHORT).show();
        }
    }
}
