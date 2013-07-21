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

import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.ninetwozero.bf3droid.adapter.NotificationListAdapter;
import com.ninetwozero.bf3droid.datatype.NotificationData;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.http.NotificationClient;

import java.util.ArrayList;
import java.util.List;

public class AsyncNotificationsRefresh extends
        AsyncTask<String, Integer, Boolean> {

    // Attribute
    private List<NotificationData> notifications = new ArrayList<NotificationData>();
    private ListView listNotifications;
    private TextView status;

    // Constructor
    public AsyncNotificationsRefresh(ListView f, TextView s) {

        listNotifications = f;
        status = s;

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            // Let's get this!!
            notifications = new NotificationClient().get(arg0[0]);
            return true;

        } catch (WebsiteHandlerException e) {

            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        // Fill the listviews!!
        if (listNotifications != null) {

            if (notifications.isEmpty()) {

                // Do wee need to think about the listview?
                if (status.getVisibility() == View.GONE) {
                    status.setVisibility(View.VISIBLE);
                }
                if (listNotifications.getVisibility() != View.GONE) {
                    listNotifications.setVisibility(View.GONE);
                }

            } else {

                // Set the adapter
                if (status.getVisibility() != View.GONE) {
                    status.setVisibility(View.GONE);
                }
                if (listNotifications.getVisibility() != View.VISIBLE) {
                    listNotifications.setVisibility(View.VISIBLE);
                }
                ((NotificationListAdapter) listNotifications.getAdapter())
                        .setItemArray(notifications);
                ((NotificationListAdapter) listNotifications.getAdapter())
                        .notifyDataSetChanged();

            }

        }

    }

}
