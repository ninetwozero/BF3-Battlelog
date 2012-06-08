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
import android.widget.Toast;

import com.ninetwozero.battlelog.activity.forum.ForumActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.handlers.ForumHandler;

public class AsyncCreateNewThread extends AsyncTask<String, Void, Boolean> {

    // Attributes
    private Context context;
    private long forumId;

    // Construct
    public AsyncCreateNewThread(Context c, long fId) {

        this.context = c;
        this.forumId = fId;

    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            // How'd it go?
            ForumHandler forumHandler = new ForumHandler();
            forumHandler.setForumId(forumId);
            return forumHandler.create(

                    this.context, arg0[0], arg0[1], arg0[2]

                    );

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        // Well, how'd it go?
        if (results) {

            Toast.makeText(this.context, R.string.info_forum_newthread_true,
                    Toast.LENGTH_SHORT).show();
            if (context instanceof ForumActivity) {

                ((ForumActivity) this.context).reload();
                ((ForumActivity) this.context).resetPostFields();
            }

        } else {

            Toast.makeText(this.context, R.string.info_forum_newthread_false,
                    Toast.LENGTH_SHORT).show();

        }

    }

}
