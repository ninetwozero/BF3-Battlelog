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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapters.CommentListAdapter;
import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.handlers.CommentHandler;

public class AsyncCommentsRefresh extends AsyncTask<Long, Integer, Boolean> {

    // Attribute
    private Context context;
    private SharedPreferences sharedPreferences;
    private List<CommentData> comments = new ArrayList<CommentData>();
    private ListView listView;
    private LayoutInflater layoutInflater;

    // Constructor
    public AsyncCommentsRefresh(Context c, ListView lv, LayoutInflater l) {

        this.context = c;
        this.listView = lv;
        this.layoutInflater = l;
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(Long... commentId) {

        try {

            // Let's get this!!
            comments = CommentHandler.getCommentsForPost(commentId[0]);
            return true;

        } catch (WebsiteHandlerException e) {

            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        // How did go?
        if (results) {

            // Set the almighty adapter
            listView.setAdapter(

                    new CommentListAdapter(context, comments, layoutInflater)

                    );

        } else {

            Toast.makeText(context, R.string.msg_comment_norefresh,
                    Toast.LENGTH_SHORT).show();

        }
        return;

    }

}
