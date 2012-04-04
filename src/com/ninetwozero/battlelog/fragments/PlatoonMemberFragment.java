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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.PlatoonInviteActivity;
import com.ninetwozero.battlelog.ProfileActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapters.PlatoonUserListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncPlatoonMemberManagement;
import com.ninetwozero.battlelog.asynctasks.AsyncPlatoonRespond;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.PlatoonInformation;
import com.ninetwozero.battlelog.datatypes.PlatoonMemberData;
import com.ninetwozero.battlelog.misc.Constants;

public class PlatoonMemberFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;

    // Elements
    private TextView textTitle;
    private ListView listView;
    private PlatoonUserListAdapter platoonUserListAdapter;

    // Misc
    private PlatoonData platoonData;
    private PlatoonInformation platoonInformation;
    private boolean isViewingMembers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_platoon_users,
                container, false);

        // Init the fragment
        initFragment(view);

        // Let's return the view
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    public void initFragment(View v) {

        // Setup the listView
        listView = (ListView) v.findViewById(android.R.id.list);
        platoonUserListAdapter = new PlatoonUserListAdapter(context, null, layoutInflater);
        listView.setAdapter(platoonUserListAdapter);

        // Default
        isViewingMembers = true;

        // Get the TextView
        textTitle = (TextView) v.findViewById(R.id.text_title_users);

    }

    public final void showMembers(PlatoonInformation data) {

        // Do we have valid data?
        if (data == null) {
            return;
        }

        // Get the activity
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        // Store the PlatoonInformation
        if (platoonInformation != data) {
            platoonInformation = data;
        }

        Log.d(Constants.DEBUG_TAG, "lets update it!");
        // Get the appropriate data for the listview
        if (isViewingMembers) {

            textTitle.setText(R.string.label_own_soldiermbers);
            platoonUserListAdapter.setProfileArray(platoonInformation.getMembers());

        } else {

            textTitle.setText(R.string.label_fans);
            platoonUserListAdapter.setProfileArray(platoonInformation.getFans());

        }

    }

    public void reload() {

    }

    public void setPlatoonData(PlatoonData p) {

        platoonData = p;
    }

    public Menu prepareOptionsMenu(Menu menu) {

        // Is it null?
        if (isViewingMembers) {

            ((MenuItem) menu.findItem(R.id.option_join)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_leave)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_fans)).setVisible(true);
            ((MenuItem) menu.findItem(R.id.option_invite)).setVisible(true);
            ((MenuItem) menu.findItem(R.id.option_members))
                    .setVisible(false);

        } else {

            ((MenuItem) menu.findItem(R.id.option_join)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_leave)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_fans)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_invite)).setVisible(true);
            ((MenuItem) menu.findItem(R.id.option_members))
                    .setVisible(true);

        }
        return menu;

    }

    public boolean handleSelectedOption(MenuItem item) {

        if (item.getItemId() == R.id.option_members
                || item.getItemId() == R.id.option_fans) {

            isViewingMembers = !isViewingMembers;
            showMembers(platoonInformation);

        } else if (item.getItemId() == R.id.option_invite) {

            startActivity(

            new Intent(

                    context, PlatoonInviteActivity.class

            ).putExtra(

                    "platoon", platoonData

                    ).putExtra(

                            "friends", platoonInformation.getInvitableFriends()

                    )

            );

        }

        return true;

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        getActivity().openContextMenu(v);

    }

    public void createContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        // Get the actual menu item and tag
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

        // Get the data
        PlatoonMemberData data = (PlatoonMemberData) info.targetView
                .getTag();

        // General
        menu.add(2, 0, 0, R.string.info_profile_view);

        // Let's see... founder? No "admin" options on that user!!
        if (data.getMembershipLevel() == 256) {

            return;

        }
        if (data.getMembershipLevel() >= 4) { // ^Other actual member

            // Are we on an admin level and able to modify?
            if (platoonInformation.isAdmin() && isViewingMembers) {

                // 128 == Admin, which renders our action to demote
                if (data.getMembershipLevel() == 128) {

                    menu.add(2, 1, 0, R.string.info_platoon_member_demote);

                } else {

                    menu.add(2, 1, 0, R.string.info_platoon_member_promote);

                }

                menu.add(2, 2, 0, R.string.info_platoon_member_kick);

            }

        } else if (data.getMembershipLevel() == 1) { // Players that want to
            // join

            menu.add(2, 3, 0, R.string.info_platoon_member_new_accept);
            menu.add(2, 4, 0, R.string.info_platoon_member_new_deny);

        }
        return;

    }

    public boolean handleSelectedContextItem(AdapterView.AdapterContextMenuInfo info, MenuItem item) {

        try {

            // Divide & conquer
            if (item.getGroupId() == 2) {

                // Get the data
                PlatoonMemberData data = (PlatoonMemberData) info.targetView
                        .getTag();

                // ...
                if (item.getItemId() == 0) {

                    startActivity(

                    new Intent(context, ProfileActivity.class).putExtra(

                            "profile", data

                            )

                    );

                } else if (item.getItemId() == 1) {

                    if (!data.isAdmin()) {

                        Toast.makeText(context,
                                R.string.info_platoon_member_promoting,
                                Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(context,
                                R.string.info_platoon_member_demoting,
                                Toast.LENGTH_SHORT).show();

                    }
                    new AsyncPlatoonMemberManagement(context, data.getId(),
                            platoonData.getId(), 1).execute(!data.isAdmin());

                } else if (item.getItemId() == 2) {

                    Toast.makeText(context, R.string.info_platoon_member_kicking,
                            Toast.LENGTH_SHORT).show();
                    new AsyncPlatoonMemberManagement(context, data.getId(),
                            platoonData.getId(), 2).execute();

                } else if (item.getItemId() == 3) {

                    Toast.makeText(context, R.string.info_platoon_member_new_ok,
                            Toast.LENGTH_SHORT).show();
                    new AsyncPlatoonRespond(

                            context, platoonData.getId(), data.getId(), true

                    ).execute(sharedPreferences.getString(
                            Constants.SP_BL_PROFILE_CHECKSUM, ""));

                } else if (item.getItemId() == 4) {

                    Toast.makeText(context,
                            R.string.info_platoon_member_new_false,
                            Toast.LENGTH_SHORT).show();
                    new AsyncPlatoonRespond(

                            context, platoonData.getId(), data.getId(), false

                    ).execute(sharedPreferences.getString(
                            Constants.SP_BL_PROFILE_CHECKSUM, ""));

                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

        return true;

    }

}
