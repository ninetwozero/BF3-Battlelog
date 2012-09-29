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
import com.ninetwozero.battlelog.activity.forum.ForumActivity;
import com.ninetwozero.battlelog.http.ForumClient;

public class AsyncCreateNewThread extends AsyncTask<String, Void, Boolean> {

    // Attributes
    private Context mContext;
    private long mForumId;

    // Construct
    public AsyncCreateNewThread(Context c, long fId) {

        mContext = c;
        mForumId = fId;

    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            // How'd it go?
            ForumClient forumHandler = new ForumClient();
            forumHandler.setForumId(mForumId);
            boolean created = forumHandler.create(

                    mContext, arg0[0], arg0[1], arg0[2]

            );

            // If we managed, we need to fetch the new information
            if (created) {
            }
            return created;

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        // Well, how'd it go?
        if (results) {

            Toast.makeText(this.mContext, R.string.info_forum_newthread_true,
                    Toast.LENGTH_SHORT).show();
            if (mContext instanceof ForumActivity) {

                ((ForumActivity) this.mContext).reload();
                ((ForumActivity) this.mContext).resetPostFields();
            }

        } else {

            Toast.makeText(this.mContext, R.string.info_forum_newthread_false,
                    Toast.LENGTH_SHORT).show();

        }

    }

}
