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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3ListFragment;
import com.ninetwozero.bf3droid.activity.DashboardActivity;
import com.ninetwozero.bf3droid.activity.profile.assignments.AssignmentActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.CompareActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.ProfileOverviewFragment;
import com.ninetwozero.bf3droid.activity.profile.unlocks.UnlockActivity;
import com.ninetwozero.bf3droid.adapter.FriendListAdapter;
import com.ninetwozero.bf3droid.dao.PlatoonInformationDAO;
import com.ninetwozero.bf3droid.dao.UserProfileDataDAO;
import com.ninetwozero.bf3droid.datatype.*;
import com.ninetwozero.bf3droid.http.COMClient;
import com.ninetwozero.bf3droid.http.ProfileClient;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.SessionKeeper;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.provider.table.Personas;
import com.ninetwozero.bf3droid.provider.table.UserProfileData;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;
import com.ninetwozero.bf3droid.util.HtmlParsing;

import java.util.List;

import org.apache.http.client.methods.HttpGet;

import static com.ninetwozero.bf3droid.dao.PersonasDAO.simplePersonaToDB;
import static com.ninetwozero.bf3droid.dao.PlatoonInformationDAO.simplePlatoonToDatabase;
import static com.ninetwozero.bf3droid.dao.UserProfileDataDAO.userProfileDataToDB;

public class ComFriendFragment extends Bf3ListFragment {

    private Context context;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;
    private COMClient comClient;

    private FriendListDataWrapper friendListData;
    private FriendListAdapter friendListAdapter;

    private ListView listView;
    private Bundle bundle;

    private final int MENU_OPEN_CHAT = 0;
    private final int MENU_ACCEPT_REQUEST = 1;
    private final int MENU_REJECT_REQUEST = 2;
    private final int MENU_VIEW_SOLDIER = 3;
    private final int MENU_VIEW_UNLOCKS = 4;
    private final int MENU_COMPARE_BATTLESCARS = 5;
    private final int MENU_VIEW_ASSIGNMENTS = 6;

    private final int LOADER_OVERVIEW = 22;
    private AdapterView.AdapterContextMenuInfo selectedInfo;
    private MenuItem selectedItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        layoutInflater = inflater;
        bundle = savedInstanceState;

        View view = layoutInflater.inflate(R.layout.tab_content_com_friends, container, false);

        initFragment(view);
        return view;
    }

    public void initFragment(View view) {
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(friendListAdapter = new FriendListAdapter(context, null, layoutInflater));
        ((Activity) context).registerForContextMenu(listView);

        comClient = new COMClient(BF3Droid.getUser().getId(), BF3Droid.getCheckSum());
    }

    public void reload() {
        new AsyncRefresh().execute(sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));
    }

    public Menu prepareOptionsMenu(Menu menu) {
        return menu;
    }

    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }

    public void createContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        ProfileData selectedUser = (ProfileData) info.targetView.getTag();
        if (selectedUser.getId() != 0) {
            if (selectedUser.isFriend()) {
                menu.add(0, MENU_OPEN_CHAT, 0, R.string.label_chat_open);
            } else {
                menu.add(0, MENU_ACCEPT_REQUEST, 0, R.string.label_com_accept);
                menu.add(0, MENU_REJECT_REQUEST, 0, R.string.label_com_reject);
            }
            menu.add(0, MENU_VIEW_SOLDIER, 0, R.string.label_soldier_view);
            menu.add(0, MENU_VIEW_UNLOCKS, 0, R.string.label_soldier_unlocks);
            menu.add(0, MENU_COMPARE_BATTLESCARS, 0, R.string.label_compare_bs);
            menu.add(0, MENU_VIEW_ASSIGNMENTS, 0, R.string.label_assignments_view);
        }
    }

    public boolean handleSelectedContextItem(AdapterView.AdapterContextMenuInfo info, MenuItem item) {
        selectedInfo = info;
        selectedItem = item;
        ProfileData profileData = (ProfileData) selectedInfo.targetView.getTag();
        BF3Droid.setGuest(new User(profileData.getUsername(), profileData.getId()));
        startLoadingDialog(ComFriendFragment.class.getSimpleName());
        restartLoader();

        return true;
    }

    private void menuActionAfterLoad(){
        try {
            ProfileData profileData = (ProfileData) selectedInfo.targetView.getTag();

            if (selectedItem.getItemId() == MENU_OPEN_CHAT) {
                startActivity(
                        new Intent(context, ChatActivity.class)
                                .putExtra("activeUser", SessionKeeper.getProfileData())
                                .putExtra("otherUser", profileData)
                );
            } else if (selectedItem.getItemId() == MENU_ACCEPT_REQUEST || selectedItem.getItemId() == MENU_REJECT_REQUEST) {
                new AsyncRequest(profileData.getId(), (selectedItem.getItemId() == MENU_ACCEPT_REQUEST))
                        .execute(sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));
            } else if (selectedItem.getItemId() == MENU_VIEW_SOLDIER) {
                viewSoldier(profileData);
            } else if (selectedItem.getItemId() == MENU_VIEW_UNLOCKS) {
                startActivity(
                        new Intent(context, UnlockActivity.class).putExtra("profile", (ProfileData) selectedInfo.targetView.getTag())
                );
            } else if (selectedItem.getItemId() == MENU_COMPARE_BATTLESCARS) {
                startActivity(
                        new Intent(context, CompareActivity.class)
                                .putExtra("profile1", SessionKeeper.getProfileData())
                                .putExtra("profile2", ProfileClient.resolveFullProfileDataFromProfileId(profileData.getId()))
                );
                /* TODO: Move profile2 population into ASYNCTASK */
            } else if (selectedItem.getItemId() == MENU_VIEW_ASSIGNMENTS) {
                startActivity(
                        new Intent(context, AssignmentActivity.class)
                                .putExtra("profile", ProfileClient.resolveFullProfileDataFromProfileId(profileData.getId()))
                );
            }
        } catch (Exception ex) {
            Log.e(ComFriendFragment.class.getSimpleName(), ex.toString());
        }
    }

    private void viewSoldier(ProfileData profileData) {
        startActivity(new Intent(context, ProfileActivity.class).putExtra("user", User.GUEST));
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_OVERVIEW, bundle, this);
    }

    @Override
    public Loader<CompletedTask> onCreateLoader(int id, Bundle bundle) {
        return new Bf3Loader(context, httpDataOverview());
    }

    private Bf3ServerCall.HttpData httpDataOverview() {
        return new Bf3ServerCall.HttpData(UriFactory.getProfileInformationUri(user().getName()), HttpGet.METHOD_NAME, false);
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> loader, CompletedTask completedTask) {
        if (isTaskSuccess(completedTask.result)) {
            processOverviewLoaderResult(completedTask);
        } else {
            Log.e(ProfileOverviewFragment.class.getSimpleName(), "User data extraction failed for " + user().getName());
        }
        closeLoadingDialog(ComFriendFragment.class.getSimpleName());
        menuActionAfterLoad();
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }

    private void processOverviewLoaderResult(CompletedTask completedTask) {
        UserInfo userInfo = processUserDataResult(completedTask.response);
        savePersonaToApp(userInfo);
    }

    private boolean isTaskSuccess(CompletedTask.Result result) {
        return result == CompletedTask.Result.SUCCESS;
    }

    private UserInfo processUserDataResult(String response) {
        HtmlParsing parser = new HtmlParsing();
        return parser.extractUserInfo(response);
    }

    private void savePersonaToApp(UserInfo userInfo) {
        user().setPersonas(userInfo.getPersonas());
        user().setPlatoons(userInfo.getPlatoons());
        personasToDatabase(userInfo.getPersonas());
        platoonsToDatabase(userInfo.getPlatoons());
        userProfileDataToDatabase(userInfo.getUserProfileData());
    }

    private void personasToDatabase(List<SimplePersona> personas) {
        for (SimplePersona persona : personas) {
            ContentValues contentValues = simplePersonaToDB(persona, getUserId());
            context.getContentResolver().insert(Personas.URI, contentValues);
        }
    }

    private void platoonsToDatabase(List<SimplePlatoon> platoons) {
        for (SimplePlatoon platoon : platoons) {
            ContentValues contentValues = simplePlatoonToDatabase(platoon, getUserId());
            context.getContentResolver().insert(PlatoonInformationDAO.URI, contentValues);
        }
    }

    private void userProfileDataToDatabase(UserProfileData profileData) {
        ContentValues contentValues = userProfileDataToDB(profileData);
        context.getContentResolver().insert(UserProfileDataDAO.URI, contentValues);
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
                friendListData = comClient.getFriendsForCOM(context);
                return friendListData != null;
            } catch (WebsiteHandlerException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean results) {
            if (results) {
                ((DashboardActivity) context)
                        .setComLabel(context.getString(R.string.label_com_handle)
                                .replace("{num}", String.valueOf(friendListData.getNumTotalOnline())));
                display(friendListData);
            }
        }
    }

    private class AsyncRequest extends AsyncTask<String, Integer, Boolean> {
        private long profileId;
        private boolean response;

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
                return comClient.answerFriendRequest(profileId, response);
            } catch (WebsiteHandlerException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean results) {
            Toast.makeText(context, R.string.info_friendreq_resp_ok, Toast.LENGTH_SHORT).show();
            reload();
        }
    }

    public void display(FriendListDataWrapper items) {
        if (items != null) {
            friendListAdapter.setItemArray(items.getFriends());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private long getUserId() {
        return user().getId();
    }

    private User user() {
        return BF3Droid.getGuest();
    }
}
