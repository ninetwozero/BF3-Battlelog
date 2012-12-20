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

package com.ninetwozero.bf3droid.activity.social;

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

import com.ninetwozero.bf3droid.Battlelog;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.DashboardActivity;
import com.ninetwozero.bf3droid.activity.profile.assignments.AssignmentActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.CompareActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.bf3droid.activity.profile.unlocks.UnlockActivity;
import com.ninetwozero.bf3droid.adapter.FriendListAdapter;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.FriendListDataWrapper;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.http.COMClient;
import com.ninetwozero.bf3droid.http.ProfileClient;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.SessionKeeper;

public class ComFriendFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SharedPreferences mSharedPreferences;
    private COMClient mComClient;

    // Misc
    private FriendListDataWrapper mFriendListData;
    private FriendListAdapter mFriendListAdapter;

    // Elements
    private ListView mListView;

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
        mContext = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.tab_content_com_friends,
                container, false);

        // Let's try this
        initFragment(view);

        // Return
        return view;

    }

    @Override
    public void initFragment(View view) {

        // Get the listview
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mFriendListAdapter = new FriendListAdapter(mContext, null,
                mLayoutInflater));
        ((Activity) mContext).registerForContextMenu(mListView);
        
        // Setup the COM
        mComClient = new COMClient(
    		/*SessionKeeper.getProfileData().getId()*/Battlelog.getUserId(),
    		/*mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")*/Battlelog.getCheckSum()
		);
    }

    @Override
    public void reload() {
        new AsyncRefresh().execute(mSharedPreferences
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
    }

    public boolean handleSelectedContextItem(AdapterView.AdapterContextMenuInfo info, MenuItem item) {
        try {
            ProfileData profileData = (ProfileData) info.targetView.getTag();
            if (item.getItemId() == MENU_POS_CHAT) {
                startActivity(
                    new Intent(
                            mContext, ChatActivity.class
                    ).putExtra(
                            "activeUser", SessionKeeper.getProfileData()
                    ).putExtra(
                            "otherUser", profileData
                    )
                );
            } else if (item.getItemId() == MENU_POS_REQUEST_Y
                    || item.getItemId() == MENU_POS_REQUEST_N) {
                new AsyncRequest(profileData.getId(), (item.getItemId() == MENU_POS_REQUEST_Y))
                        .execute(mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));
            } else if (item.getItemId() == MENU_POS_PROFILE) {
                startActivity(
                    new Intent(
                        mContext, ProfileActivity.class
                    ).putExtra(
                        "profile", profileData
                    )
                );
            } else if (item.getItemId() == MENU_POS_UNLOCKS) {
                startActivity(
                    new Intent(
                            mContext, UnlockActivity.class
                    ).putExtra(
                            "profile", (ProfileData) info.targetView.getTag()
                    )
                );
            } else if (item.getItemId() == MENU_POS_COMPARE) {

            	startActivity(
                    new Intent(
                            mContext, CompareActivity.class
                    ).putExtra(
                            "profile1", SessionKeeper.getProfileData()
                    ).putExtra(
                            "profile2", 
                            ProfileClient.resolveFullProfileDataFromProfileId(profileData.getId())
                    )
                );
                /* TODO: Move profile2 population into ASYNCTASK */
            } else if (item.getItemId() == MENU_POS_ASSIGNMENTS) {
                startActivity(
                    new Intent(
                        mContext, AssignmentActivity.class
                    ).putExtra(
                        "profile", 
                        ProfileClient.resolveFullProfileDataFromProfileId(
                    		profileData.getId()
                        )
                    )
                );
            }
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
            ((DashboardActivity) mContext).setComLabel(mContext.getString(R.string.label_wait));
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                mFriendListData = mComClient.getFriendsForCOM(mContext);
                return mFriendListData != null;
            } catch (WebsiteHandlerException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (results) {

                // Display the friend list
                ((DashboardActivity) mContext).setComLabel(mContext.getString(
                        R.string.label_com_handle).replace("{num}",
                        String.valueOf(mFriendListData.getNumTotalOnline())));
                display(mFriendListData);

            }

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
                return mComClient.answerFriendRequest(profileId, response);

            } catch (WebsiteHandlerException e) {

                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            // Let the user know and then refresh!
            Toast.makeText(mContext, R.string.info_friendreq_resp_ok,
                    Toast.LENGTH_SHORT).show();
            reload();

        }

    }

    public void display(FriendListDataWrapper items) {

        // If empty --> show other
        if (items != null) {

            // If we don't have it defined, then we need to set it
            mFriendListAdapter.setItemArray(items.getFriends());

        }

    }

    @Override
    public void onResume() {

        super.onResume();
        reload();
    }

}
