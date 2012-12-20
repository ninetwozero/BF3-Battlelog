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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.adapter.CommentListAdapter;
import com.ninetwozero.bf3droid.datatype.CommentData;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.http.CommentClient;

public class AsyncCommentsRefresh extends AsyncTask<Integer, Integer, Boolean> {

    // Attribute
    private Context context;
    private long postId;
    private List<CommentData> comments = new ArrayList<CommentData>();
    private ListView listView;
    private LayoutInflater layoutInflater;

    // Constructor
    public AsyncCommentsRefresh(Context c, long i, ListView lv, LayoutInflater l) {

        context = c;
        postId = i;
        listView = lv;
        layoutInflater = l;

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(Integer... page) {

        try {

            // Let's get this!!
            /* TODO: SOLUTION */
            comments = new CommentClient(postId, CommentData.TYPE_FEED)
                    .get(page[0]);
            return true;
        } catch (WebsiteHandlerException ex) {

            ex.printStackTrace();
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

    }

}
