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
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.feed.FeedFragment;
import com.ninetwozero.bf3droid.http.COMClient;

public class AsyncStatusUpdate extends AsyncTask<String, Integer, Boolean> {

    // Attribute
    private Context mContext;
    private FeedFragment mFragmentFeed;

    // Constructor
    public AsyncStatusUpdate(Context c, FeedFragment f) {
        mContext = c;
        mFragmentFeed = f;
    }

    @Override
    protected void onPreExecute() {
        if (mContext != null) {
        	mFragmentFeed.prePostMode();
        	Toast.makeText(mContext, R.string.msg_status, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        try {
           return new COMClient(0, arg0[1]).updateStatus(arg0[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean results) {
        if (mContext != null) {
            if (results) {
                Toast.makeText(mContext, R.string.msg_status_ok,
                        Toast.LENGTH_SHORT).show();
                mFragmentFeed.postPostMode();
            } else {
                Toast.makeText(mContext, R.string.msg_status_fail,
                        Toast.LENGTH_SHORT).show();
            }
            mFragmentFeed.reload();
        }
    }
}
