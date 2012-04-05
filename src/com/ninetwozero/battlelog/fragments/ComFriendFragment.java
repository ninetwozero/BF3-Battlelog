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

package com.ninetwozero.battlelog.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapters.FriendListAdapter;
import com.ninetwozero.battlelog.adapters.RequestListAdapter;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.FriendListDataWrapper;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class ComFriendFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private FriendListDataWrapper friendListData;
    private FriendListAdapter friendListAdapter;

    // Elements
    private ListView listView;
    private View wrapFriendRequests;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_com_friends,
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
        listView.setAdapter(friendListAdapter = new FriendListAdapter(context, null, layoutInflater));
        registerForContextMenu(listView);

    }

    @Override
    public void reload() {

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

        // Get the url
        String url = String.valueOf(v.getTag());
        Log.d(Constants.DEBUG_TAG, "url => " + url);

        // Is it empty?
        if (url.equals("")) {
            Toast.makeText(context, R.string.info_credits_nolink, Toast.LENGTH_SHORT).show();

        } else {
            // Let's send it somewhere
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));

        }
        return;

    }
    
    public class AsyncRefresh extends AsyncTask<String, Integer, Boolean> {

        // Constructor
        public AsyncRefresh() {
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {

                // Let's get this!
                friendListData = WebsiteHandler.getFriendsCOM(context, arg0[0]);
                return true;

            } catch (WebsiteHandlerException e) {

                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if( results ) {
                
                //Display the friend list
                display(friendListData);

            }
            
            // R-turn
            return;

        }

    }

    public void display(FriendListDataWrapper items) {

        // If empty --> show other
        if (items != null) {

            // If we don't have it defined, then we need to set it
            friendListAdapter.setItemArray(items.getFriends());
            
        }

    }
    
}
