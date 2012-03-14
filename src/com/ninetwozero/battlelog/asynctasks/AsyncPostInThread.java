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

import com.ninetwozero.battlelog.Backup_ForumThreadView;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncPostInThread extends AsyncTask<String, Void, Boolean> {

    // Attributes
    private Context context;
    private long threadId;

    // Construct
    public AsyncPostInThread(Context c, long tId) {

        this.context = c;
        this.threadId = tId;

    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            // How'd it go?
            return WebsiteHandler.postReplyInThread(

                    this.context, arg0[0], arg0[1], this.threadId

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

            Toast.makeText(this.context, R.string.info_forum_newpost_true,
                    Toast.LENGTH_SHORT).show();
            if (context instanceof Backup_ForumThreadView) {

                ((Backup_ForumThreadView) this.context).reload();
                ((Backup_ForumThreadView) this.context).resetPostFields();

            }

        } else {

            Toast.makeText(this.context, R.string.info_forum_newpost_false,
                    Toast.LENGTH_SHORT).show();

        }

    }

}
