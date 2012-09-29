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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.social.ChatActivity;
import com.ninetwozero.battlelog.adapter.ChatListAdapter;
import com.ninetwozero.battlelog.datatype.ChatMessage;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.COMClient;
import com.ninetwozero.battlelog.misc.Constants;

public class AsyncChatRefresh extends AsyncTask<Long, Integer, Boolean> {

	// Attribute
	private Context context;
	private SharedPreferences sharedPreferences;
	private List<ChatMessage> messageArray = new ArrayList<ChatMessage>();
	private ListView listView;

	// Constructor
	public AsyncChatRefresh(Context c, ListView lv) {

		context = c;
		listView = lv;
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected Boolean doInBackground(Long... profileId) {

		try {

			// Let's get this!!
			messageArray = new COMClient(sharedPreferences.getString(
					Constants.SP_BL_PROFILE_CHECKSUM, ""))
					.getMessages(profileId[0]);
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
			((ChatListAdapter) listView.getAdapter())
					.setMessageArray(messageArray);

			// Do we need to ploop?
			if (context instanceof ChatActivity) {
				((ChatActivity) context).notifyNewPost(messageArray);
			}

		} else {

			Toast.makeText(context, R.string.msg_chat_norefresh,
					Toast.LENGTH_SHORT).show();

		}

	}

}
