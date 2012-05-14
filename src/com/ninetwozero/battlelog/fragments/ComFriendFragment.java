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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.AssignmentActivity;
import com.ninetwozero.battlelog.ChatActivity;
import com.ninetwozero.battlelog.CompareActivity;
import com.ninetwozero.battlelog.DashboardActivity;
import com.ninetwozero.battlelog.ProfileActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.UnlockActivity;
import com.ninetwozero.battlelog.adapters.FriendListAdapter;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.FriendListDataWrapper;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.handlers.WebsiteHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class ComFriendFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;

    // Misc
    private FriendListDataWrapper friendListData;
    private FriendListAdapter friendListAdapter;

    // Elements
    private ListView listView;

    // Constants
    private final int MENU_POS_CHAT = 0;
    private final int MENU_POS_REQUEST_Y = 1;
    private final int MENU_POS_REQUEST_N = 2;
    private final int MENU_POS_PROFILE = 3;
    private final int MENU_POS_UNLOCKS = 4;
    private final int MENU_POS_COMPARE = 5;
    private final int MENU_POS_ASSIGNMENTS = 6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

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
        ((Activity) context).registerForContextMenu(listView);

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

    public void createContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        // Get the actual menu item and tag
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

        // Get the "object"
        ProfileData selectedUser = (ProfileData) info.targetView.getTag();

        // Wait, is the position 0? If so, it's the heading...
        if (selectedUser.getId() != 0) {

            if (selectedUser.isFriend()) {

                menu.add(0, MENU_POS_CHAT, 0, R.string.label_chat_open);
                menu.add(0, MENU_POS_PROFILE, 0, R.string.label_soldier_view);
                menu.add(0, MENU_POS_UNLOCKS, 0, R.string.label_soldier_unlocks);
                menu.add(0, MENU_POS_COMPARE, 0, R.string.label_compare_bs);
                menu.add(0, MENU_POS_ASSIGNMENTS, 0, R.string.label_assignments_view);

            } else {

                menu.add(0, MENU_POS_REQUEST_Y, 0, R.string.label_com_accept);
                menu.add(0, MENU_POS_REQUEST_N, 0, R.string.label_com_reject);
                menu.add(0, MENU_POS_PROFILE, 0, R.string.label_soldier_view);
                menu.add(0, MENU_POS_UNLOCKS, 0, R.string.label_soldier_unlocks);
                menu.add(0, MENU_POS_COMPARE, 0, R.string.label_compare_bs);
                menu.add(0, MENU_POS_ASSIGNMENTS, 0, R.string.label_assignments_view);

            }
        }
        return;

    }

    public boolean handleSelectedContextItem(AdapterView.AdapterContextMenuInfo info, MenuItem item) {

        try {

            ProfileData profileData = (ProfileData) info.targetView.getTag();

            if (item.getItemId() == MENU_POS_CHAT) {

                startActivity(

                new Intent(

                        context, ChatActivity.class

                ).putExtra(

                        "profile", profileData

                        )

                );

            } else if (item.getItemId() == MENU_POS_REQUEST_Y
                    || item.getItemId() == MENU_POS_REQUEST_N) {

                new AsyncRequest(profileData.getId(), (item.getItemId() == MENU_POS_REQUEST_Y))
                        .execute(sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));

            } else if (item.getItemId() == MENU_POS_PROFILE) {

                startActivity(

                new Intent(

                        context, ProfileActivity.class

                ).putExtra(

                        "profile", profileData

                        )

                );

            } else if (item.getItemId() == MENU_POS_UNLOCKS) {

                startActivity(

                new Intent(

                        context, UnlockActivity.class

                ).putExtra(

                        "profile", (ProfileData) info.targetView.getTag()

                        )

                );

            } else if (item.getItemId() == MENU_POS_COMPARE) {

                startActivity(

                new Intent(

                        context, CompareActivity.class

                ).putExtra(

                        "profile1", SessionKeeper.getProfileData()

                        ).putExtra(

                                "profile2", WebsiteHandler.getPersonaIdFromProfile(

                                        profileData.getId()

                                        )

                        )

                );

            } else if (item.getItemId() == MENU_POS_ASSIGNMENTS) {

                startActivity(

                new Intent(

                        context, AssignmentActivity.class

                ).putExtra(

                        "profile", WebsiteHandler.getPersonaIdFromProfile(

                                profileData.getId()

                                )

                        )

                );

            }

        } catch (WebsiteHandlerException ex) {

            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
            return false;

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;
        }

        return true;

    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {

        getActivity().openContextMenu(v);

    }

    private class AsyncRefresh extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            ((DashboardActivity) context).setComLabel(context.getString(R.string.label_wait));

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

            if (results) {

                // Display the friend list
                ((DashboardActivity) context).setComLabel(context.getString(
                        R.string.label_com_handle).replace("{num}",
                        friendListData.getNumTotalOnline() + ""));
                display(friendListData);

            }

            // R-turn
            return;

        }

    }

    private class AsyncRequest extends AsyncTask<String, Integer, Boolean> {

        // Attribute
        private long profileId;
        private boolean response;

        // Constructor
        public AsyncRequest(long p, boolean r) {

            this.profileId = p;
            this.response = r;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {

                // Let's get this!!
                return WebsiteHandler.answerFriendRequest(profileId, response,
                        arg0[0]);

            } catch (WebsiteHandlerException e) {

                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            // Let the user know and then refresh!
            Toast.makeText(context, R.string.info_friendreq_resp_ok,
                    Toast.LENGTH_SHORT).show();
            reload();

        }

    }

    public void display(FriendListDataWrapper items) {

        // If empty --> show other
        if (items != null) {

            // If we don't have it defined, then we need to set it
            friendListAdapter.setItemArray(items.getFriends());

        }

    }

    @Override
    public void onResume() {

        super.onResume();
        reload();
    }

}
