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
import com.ninetwozero.battlelog.activity.feed.FeedFragment;
import com.ninetwozero.battlelog.http.FeedClient;

public class AsyncFeedHooah extends AsyncTask<String, Integer, Boolean> {

	// Attribute
	private Context context;
	private long postId;
	private boolean fromWidget, liked;
	private FeedFragment feedFragment;

	// Constructor
	public AsyncFeedHooah(Context c, long pId, boolean w, boolean l,
			FeedFragment f) {

		context = c;
		postId = pId;
		fromWidget = w;
		liked = l;
		feedFragment = f;

	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected Boolean doInBackground(String... arg0) {

		try {

			// Did we manage?
			if (liked) {

				return FeedClient.unhooah(postId, arg0[0]);

			} else {

				return FeedClient.hooah(postId, arg0[0]);

			}

		} catch (Exception ex) {

			ex.printStackTrace();
			return false;

		}

	}

	@Override
	protected void onPostExecute(Boolean results) {

		if (!fromWidget) {

			if (!results) {
				Toast.makeText(context, R.string.msg_hooah_fail,
						Toast.LENGTH_SHORT).show();
			}

			feedFragment.reload();

		}

	}

}
