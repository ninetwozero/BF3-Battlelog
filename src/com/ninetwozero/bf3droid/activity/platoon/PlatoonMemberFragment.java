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

package com.ninetwozero.bf3droid.activity.platoon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.bf3droid.adapter.PlatoonUserListAdapter;
import com.ninetwozero.bf3droid.asynctask.AsyncPlatoonMemberManagement;
import com.ninetwozero.bf3droid.asynctask.AsyncPlatoonRespond;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.PlatoonData;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.jsonmodel.platoon.PlatoonDossier;
import com.ninetwozero.bf3droid.jsonmodel.platoon.PlatoonMember;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.squareup.otto.Subscribe;

public class PlatoonMemberFragment extends ListFragment implements DefaultFragment {

    private Context context;
    private LayoutInflater inflater;
    private SharedPreferences sharedPreferences;
    private TextView titleText;
    private ListView listView;
    private PlatoonUserListAdapter platoonUserListAdapter;
    private PlatoonData platoonData;
    private List<PlatoonMember> membersList;
    private List<ProfileData> fansList;
    private List<ProfileData> friendsList;
    private boolean mViewingMembers;
    private boolean isAdmin = false;

    private final int MEMBER_FOUNDER = 256;
    private final int MEMBER_ADMIN = 128;
    private final int MEMBER_NORMAL = 4;
    private final int MEMBER_APPLIED = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.inflater = inflater;

        View view = this.inflater.inflate(R.layout.tab_content_platoon_users, container, false);
        initFragment(view);
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
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void platoonDossier(PlatoonDossier platoonDossier) {
        Collection<PlatoonMember> collection = platoonDossier.getPlatoon().getMembers().values();
        membersList = Arrays.asList(collection.toArray(new PlatoonMember[]{}));
        show();
    }

    public void initFragment(View v) {
        listView = (ListView) v.findViewById(android.R.id.list);
        platoonUserListAdapter = new PlatoonUserListAdapter(null, inflater);
        listView.setAdapter(platoonUserListAdapter);

        mViewingMembers = true;

        titleText = (TextView) v.findViewById(R.id.text_title_users);
    }

    public final void show() {
        if (getActivity() == null) {
            return;
        }

        if (mViewingMembers) {
            titleText.setText(R.string.label_members);
            platoonUserListAdapter.setPlatoonMembers(membersList);
        } /*else {
            titleText.setText(R.string.label_fans);
            platoonUserListAdapter.setPlatoonMembers(fansList);
        }*/
    }

    public void reload() {
    }

    public void setPlatoonData(PlatoonData data) {
        platoonData = data;
    }

    public void setAdmin(boolean isAdminValue) {
        isAdmin = isAdminValue;
    }

    public Menu prepareOptionsMenu(Menu menu) {
        if (mViewingMembers) {
            menu.findItem(R.id.option_join).setVisible(false);
            menu.findItem(R.id.option_leave).setVisible(false);
            menu.findItem(R.id.option_fans).setVisible(true);
            menu.findItem(R.id.option_invite).setVisible(true);
            menu.findItem(R.id.option_members).setVisible(false);
        } else {
            menu.findItem(R.id.option_join).setVisible(false);
            menu.findItem(R.id.option_leave).setVisible(false);
            menu.findItem(R.id.option_fans).setVisible(false);
            menu.findItem(R.id.option_invite).setVisible(true);
            menu.findItem(R.id.option_members).setVisible(true);
        }
        return menu;
    }

    public boolean handleSelectedOption(MenuItem item) {
        if (item.getItemId() == R.id.option_members || item.getItemId() == R.id.option_fans) {
            mViewingMembers = !mViewingMembers;
            show();
        } else if (item.getItemId() == R.id.option_invite) {
            startActivity(
                    new Intent(context, PlatoonInviteActivity.class)
                            .putExtra("platoon", platoonData)
                            .putExtra("friends", (ArrayList<ProfileData>) friendsList)
            );
        }
        return true;
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        getActivity().openContextMenu(v);
    }

    public void createContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        menu.add(2, 0, 0, R.string.info_profile_view);

        PlatoonMember platoonMember = (PlatoonMember) info.targetView.getTag();
        if (platoonMember.getMembershipLevel() == MEMBER_FOUNDER) {
            return;
        } else if (platoonMember.getMembershipLevel() >= MEMBER_NORMAL) {
            if (isAdmin && mViewingMembers) {
                if (platoonMember.getMembershipLevel() == MEMBER_ADMIN) {
                    menu.add(2, 1, 0, R.string.info_platoon_member_demote);
                } else {
                    menu.add(2, 1, 0, R.string.info_platoon_member_promote);
                }
                menu.add(2, 2, 0, R.string.info_platoon_member_kick);
            }
        } else if (platoonMember.getMembershipLevel() == MEMBER_APPLIED) {
            menu.add(2, 3, 0, R.string.info_platoon_member_new_accept);
            menu.add(2, 4, 0, R.string.info_platoon_member_new_deny);
        }
    }

    public boolean handleSelectedContextItem(AdapterView.AdapterContextMenuInfo info, MenuItem item) {
        if (item.getGroupId() == 2) {
            ProfileData user = (ProfileData) info.targetView.getTag();
            switch (item.getItemId()) {
                case 0:
                    startActivity(new Intent(context, ProfileActivity.class).putExtra("profile", user));
                    break;
                case 1:
                    modifyMembership(user);
                    break;
                case 2:
                    kickUser(user);
                    break;
                case 3:
                    answerUserApplication(user, true);
                    break;
                case 4:
                    answerUserApplication(user, false);
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    private void answerUserApplication(ProfileData user, boolean isAccepting) {
        int string = isAccepting ? R.string.info_platoon_member_new_ok : R.string.info_platoon_member_new_false;
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
        new AsyncPlatoonRespond(context, platoonData, user.getId(), isAccepting).execute(
                sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")
        );
    }

    private void kickUser(ProfileData data) {
        Toast.makeText(context, R.string.info_platoon_member_kicking, Toast.LENGTH_SHORT).show();
        new AsyncPlatoonMemberManagement(context, data.getId(), platoonData).execute();
    }

    private void modifyMembership(ProfileData user) {
        int string = user.isAdmin() ? R.string.info_platoon_member_demoting : R.string.info_platoon_member_promoting;
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
        new AsyncPlatoonMemberManagement(context, user.getId(), platoonData).execute(!user.isAdmin());
    }
}
