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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapters.FriendListAdapter;
import com.ninetwozero.battlelog.adapters.RequestListAdapter;
import com.ninetwozero.battlelog.datatypes.FriendListDataWrapper;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncComRefresh extends AsyncTask<Void, Integer, Boolean> {

    // Attribute
    private Context context;
    private SharedPreferences sharedPreferences;
    private FriendListDataWrapper profileArray;
    private ListView listRequests, listFriends;
    private LayoutInflater layoutInflater;
    private Button buttonRefresh;
    private TextView drawerHandle;

    // Constructor
    public AsyncComRefresh(Context c, ListView r, ListView f, LayoutInflater l,
            Button b, TextView t) {

        this.context = c;
        this.listRequests = r;
        this.listFriends = f;
        this.layoutInflater = l;
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        this.buttonRefresh = b;
        this.drawerHandle = t;

    }

    @Override
    protected void onPreExecute() {

        this.buttonRefresh.setEnabled(false);
        this.buttonRefresh.setText(R.string.label_wait);

    }

    @Override
    protected Boolean doInBackground(Void... arg0) {

        try {

            // Let's get this!!
            profileArray = WebsiteHandler.getFriendsCOM(context,
                    sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));
            return true;

        } catch (WebsiteHandlerException e) {

            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        // Boolean
        int emptyLists = 0;
        int numOnlineFriends = 0;

        // Fill the listviews!!
        if (profileArray != null) {

            if (profileArray.getRequests() == null
                    || profileArray.getRequests().size() == 0) {

                ((Activity) context).findViewById(R.id.wrap_friends_requests)
                        .setVisibility(View.GONE);

            } else {

                // VISIBILITY!!!
                ((Activity) context).findViewById(R.id.wrap_friends_requests)
                        .setVisibility(View.VISIBLE);

                // Set the adapter
                listRequests.setAdapter(new RequestListAdapter(context,
                        profileArray.getRequests(), layoutInflater));

            }

            if (profileArray.getOnlineFriends().size() > 0
                    || profileArray.getOfflineFriends().size() > 0) {

                // Set the visibility (could've been hidden)
                ((Activity) context).findViewById(R.id.list_friends)
                        .setVisibility(View.VISIBLE);

                // Create a copy so that we can merge later on
                List<ProfileData> mergedArray = profileArray
                        .getOnlineFriends();

                // ...but first we want the lenght, oorah!
                numOnlineFriends = mergedArray.size() - 1;

                // ...and now we can merge 'em!
                mergedArray.addAll(profileArray.getOfflineFriends());

                // Set the adapter
                listFriends.setAdapter(new FriendListAdapter(context,
                        mergedArray, layoutInflater));

            } else {

                // No friends found :-(
                ((Activity) context).findViewById(R.id.list_friends)
                        .setVisibility(View.GONE);

            }

            // Update the sliding drawer handle
            drawerHandle.setText(

                    context.getString(R.string.label_com_handle).replace(

                            "{num}", numOnlineFriends + ""

                            )

                    ); // -1 to compensate for the header

        } else {

            results = false;

        }

        // Update the button y'all
        this.buttonRefresh.setEnabled(true);
        this.buttonRefresh.setText(R.string.lable_refresh);

        // How did go?
        if (results)
            Toast.makeText(context, R.string.msg_com_ok, Toast.LENGTH_SHORT)
                    .show();
        else
            Toast.makeText(context, R.string.msg_com_fail, Toast.LENGTH_SHORT)
                    .show();
        return;

    }

}
