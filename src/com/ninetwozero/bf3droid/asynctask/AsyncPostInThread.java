/*
	This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.forum.ForumActivity;
import com.ninetwozero.bf3droid.datatype.ForumThreadData;
import com.ninetwozero.bf3droid.http.ForumClient;
import com.ninetwozero.bf3droid.misc.SessionKeeper;

public class AsyncPostInThread extends AsyncTask<String, Void, Boolean> {

    // Attributes
    private Context context;
    private ForumThreadData threadData;
    private boolean cache;

    // Construct
    public AsyncPostInThread(Context c, ForumThreadData t, boolean ca) {

        context = c;
        threadData = t;
        cache = ca;

    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            // How'd it go?
            threadData.setNumPosts(threadData.getNumPosts() + 1);
            return new ForumClient().reply(

                    context, arg0[0], arg0[1], threadData, cache, SessionKeeper
                    .getProfileData().getId()

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

            Toast.makeText(context, R.string.info_forum_newpost_true,
                    Toast.LENGTH_SHORT).show();
            if (context instanceof ForumActivity) {

                ((ForumActivity) context).reload();
                ((ForumActivity) context).resetPostFields();

            }

        } else {

            Toast.makeText(context, R.string.info_forum_newpost_false,
                    Toast.LENGTH_SHORT).show();

        }

    }

}
