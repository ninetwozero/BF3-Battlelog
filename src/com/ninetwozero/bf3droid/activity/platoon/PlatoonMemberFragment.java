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

package com.ninetwozero.bf3droid.activity.platoon;

import java.util.ArrayList;
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
import com.ninetwozero.bf3droid.misc.Constants;

public class PlatoonMemberFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SharedPreferences mSharedPreferences;

    // Elements
    private TextView mTextTitle;
    private ListView mListView;
    private PlatoonUserListAdapter mPlatoonUserListAdapter;

    // Misc
    private PlatoonData mPlatoonData;
    private List<ProfileData> mMembers;
    private List<ProfileData> mFans;
    private List<ProfileData> mFriends;
    private boolean mViewingMembers;
    private boolean isAdmin = false;
    
    // Constants
    private final int MEMBER_FOUNDER = 256;
    private final int MEMBER_APPLIED = 1;
    private final int MEMBER_NORMAL = 4;
    private final int MEMBER_ADMIN = 128;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mLayoutInflater = inflater;

        View view = mLayoutInflater.inflate(R.layout.tab_content_platoon_users, container, false);
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
    }

    public void initFragment(View v) {
        mListView = (ListView) v.findViewById(android.R.id.list);
        mPlatoonUserListAdapter = new PlatoonUserListAdapter(null, mLayoutInflater);
        mListView.setAdapter(mPlatoonUserListAdapter);
        
        mViewingMembers = true;

        mTextTitle = (TextView) v.findViewById(R.id.text_title_users);
    }
    public final void show() {
    	if( getActivity() == null ) {
    		return;
    	}
    	
    	if (mViewingMembers) {
            mTextTitle.setText(R.string.label_members);
            mPlatoonUserListAdapter.setProfileArray(mMembers);
        } else {
            mTextTitle.setText(R.string.label_fans);
            mPlatoonUserListAdapter.setProfileArray(mFans);
        }
    }
    
    public void setMembers(List<ProfileData> members) {
        mMembers = members;
    }

    public void setFans(List<ProfileData> fans) {
        mFans = fans;
    }
    
    public void setFriends(List<ProfileData> friends) {
    	mFriends = friends;
    }

    public void reload() {

    }

    public void setPlatoonData(PlatoonData p) {
        mPlatoonData = p;
    }

    public void setAdmin(boolean b) {
    	isAdmin = b;
    }
    public Menu prepareOptionsMenu(Menu menu) {
        if (mViewingMembers) {
            ((MenuItem) menu.findItem(R.id.option_join)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_leave)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_fans)).setVisible(true);
            ((MenuItem) menu.findItem(R.id.option_invite)).setVisible(true);
            ((MenuItem) menu.findItem(R.id.option_members)).setVisible(false);
        } else {
            ((MenuItem) menu.findItem(R.id.option_join)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_leave)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_fans)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_invite)).setVisible(true);
            ((MenuItem) menu.findItem(R.id.option_members)).setVisible(true);
        }
        return menu;
    }

    public boolean handleSelectedOption(MenuItem item) {
        if (item.getItemId() == R.id.option_members || item.getItemId() == R.id.option_fans) {
            mViewingMembers = !mViewingMembers;
            show();
        } else if (item.getItemId() == R.id.option_invite) {
            startActivity(
                new Intent(
                    mContext, PlatoonInviteActivity.class
                ).putExtra(
                    "platoon", mPlatoonData
                ).putExtra(
                    "friends", (ArrayList<ProfileData>) mFriends
                )
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
        
        ProfileData user = (ProfileData) info.targetView.getTag();
        if (user.getMembershipLevel() == MEMBER_FOUNDER) {
            return;
        } else if (user.getMembershipLevel() >= MEMBER_NORMAL) {
            if (isAdmin && mViewingMembers) {
                if (user.getMembershipLevel() == MEMBER_ADMIN) {
                    menu.add(2, 1, 0, R.string.info_platoon_member_demote);
                } else {
                    menu.add(2, 1, 0, R.string.info_platoon_member_promote);
                }
                menu.add(2, 2, 0, R.string.info_platoon_member_kick);
            }
        } else if (user.getMembershipLevel() == MEMBER_APPLIED) {
            menu.add(2, 3, 0, R.string.info_platoon_member_new_accept);
            menu.add(2, 4, 0, R.string.info_platoon_member_new_deny);
        }
    }

    public boolean handleSelectedContextItem(AdapterView.AdapterContextMenuInfo info, MenuItem item) {
        if (item.getGroupId() == 2) {
            ProfileData user = (ProfileData) info.targetView.getTag();
            switch(item.getItemId()) {
            	case 0:
                    startActivity(new Intent(mContext, ProfileActivity.class).putExtra("profile", user));
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
		int string = isAccepting? R.string.info_platoon_member_new_ok : R.string.info_platoon_member_new_false;
		Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
        new AsyncPlatoonRespond(mContext, mPlatoonData, user.getId(), isAccepting).execute(
			mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")
		);
	}

	private void kickUser(ProfileData data) {
		Toast.makeText(mContext, R.string.info_platoon_member_kicking, Toast.LENGTH_SHORT).show();
        new AsyncPlatoonMemberManagement(mContext, data.getId(), mPlatoonData).execute();
	}

	private void modifyMembership(ProfileData user) {
        int string = user.isAdmin() ? R.string.info_platoon_member_demoting : R.string.info_platoon_member_promoting;
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
        new AsyncPlatoonMemberManagement(mContext, user.getId(), mPlatoonData).execute(!user.isAdmin());
	}
}
