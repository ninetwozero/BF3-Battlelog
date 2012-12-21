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

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.adapter.PlatoonInviteListAdapter;
import com.ninetwozero.bf3droid.asynctask.AsyncPlatoonMemberInvite;
import com.ninetwozero.bf3droid.datatype.PlatoonData;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;

import java.util.List;

public class PlatoonInviteActivity extends ListActivity {

    // Attributes
    private SharedPreferences mSharedPreferences;
    private LayoutInflater mLayoutInflater;
    private PlatoonData mPlatoonData;
    private List<ProfileData> mFriends;
    private Object[] mSelectedIds;

    // Elements
    private ListView mListView;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PublicUtils.restoreCookies(this, icicle);

        // Prepare to tango
        this.mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the intent
        if (getIntent().hasExtra("platoon")) {
            mPlatoonData = getIntent().getParcelableExtra("platoon");
        }
        if (getIntent().hasExtra("friends")) {
            mFriends = getIntent().getParcelableArrayListExtra("friends");
        }

        // Is the profileData null?!
        if (mPlatoonData == null || mPlatoonData.getId() == 0) {
            finish();

        }

        // Fix it
        mSelectedIds = new Object[mFriends.size()];

        // Setup the locale
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Set the content view
        setContentView(R.layout.activity_platoon_invite);

        // Let's see
        initLayout();

    }

    public void initLayout() {

        if (mListView == null) {

            mListView = getListView();
            mListView.setAdapter(new PlatoonInviteListAdapter(mFriends, mLayoutInflater));

        }

    }

    public void onListItemClick(ListView lv, View v, int pos, long id) {

        if (mSelectedIds[pos].equals(0)) {

            mSelectedIds[pos] = id;
            ((CheckBox) v.findViewById(R.id.checkbox)).setChecked(true);

        } else {

            mSelectedIds[pos] = 0;
            ((CheckBox) v.findViewById(R.id.checkbox)).setChecked(false);

        }

    }

    public void onClick(View v) {

        if (v.getId() == R.id.button_ok) {

            new AsyncPlatoonMemberInvite(this, mSelectedIds, mPlatoonData)
                    .execute(mSharedPreferences
                            .getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));

        } else if (v.getId() == R.id.button_cancel) {

            this.finish();

        }

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, mSharedPreferences);
    }

}
