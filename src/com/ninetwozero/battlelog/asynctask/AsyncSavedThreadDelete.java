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
import com.ninetwozero.battlelog.activity.forum.ForumSavedActivity;
import com.ninetwozero.battlelog.datatype.SavedForumThreadData;
import com.ninetwozero.battlelog.misc.CacheHandler;

public class AsyncSavedThreadDelete extends AsyncTask<SavedForumThreadData, Void, Boolean> {

    // Attributes
    private Context mContext;

    public AsyncSavedThreadDelete(Context c) {

        mContext = c;

    }

    @Override
    protected Boolean doInBackground(SavedForumThreadData... t) {

        try {

            // Delete the item
            return CacheHandler.Forum.delete(mContext, new long[]{
                    t[0].getId()
            });

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean result) {

        if (result) {

            Toast.makeText(mContext, "The thread has been removed from the list.",
                    Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(mContext, "The thread could not be removed from the list.",
                    Toast.LENGTH_SHORT).show();

        }

        // Update the ListView
        ((ForumSavedActivity) mContext).reload();

    }

}
