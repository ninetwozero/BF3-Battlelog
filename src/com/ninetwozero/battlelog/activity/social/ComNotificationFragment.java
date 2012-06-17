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

package com.ninetwozero.battlelog.activity.social;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapter.NotificationListAdapter;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.NotificationData;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.NotificationClient;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class ComNotificationFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;

    // Misc
    private List<NotificationData> notifications;
    private NotificationListAdapter notificationListAdapter;

    // Elements
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_com_notifications,
                container, false);

        // Let's try this
        initFragment(view);

        // Return
        return view;

    }

    @Override
    public void initFragment(View view) {

        // Get the listview
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(notificationListAdapter = new NotificationListAdapter(context, null,
                layoutInflater, SessionKeeper.getProfileData().getId()));
        registerForContextMenu(listView);

    }

    @Override
    public void reload() {

        new AsyncRefresh().execute(sharedPreferences
                .getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));

    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return menu;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {

        Toast.makeText(context, R.string.msg_unimplemented, Toast.LENGTH_SHORT).show();

    }

    private class AsyncRefresh extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {

                // Let's get this!
                notifications = new NotificationClient().get(arg0[0]);
                return true;

            } catch (WebsiteHandlerException e) {

                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (results) {

                // Display the friend list
                display(notifications);

            }

            // R-turn

        }

    }

    public void display(List<NotificationData> items) {

        // Do we have it already? If no, we init
        notificationListAdapter.setItemArray(items);

    }

    @Override
    public void onResume() {

        super.onResume();
        reload();
    }

}
