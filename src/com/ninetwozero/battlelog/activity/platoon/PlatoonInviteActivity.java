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

package com.ninetwozero.battlelog.activity.platoon;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapters.PlatoonInviteListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncPlatoonMemberInvite;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class PlatoonInviteActivity extends ListActivity {

    // Attributes
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    private PlatoonData platoonData;
    private List<ProfileData> friends;
    private Object[] selectedIds;

    // Elements
    private ListView listView;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PublicUtils.restoreCookies(this, icicle);

        // Prepare to tango
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the intent
        if (getIntent().hasExtra("platoon")) {
            platoonData = getIntent().getParcelableExtra("platoon");
        }
        if (getIntent().hasExtra("friends")) {
            friends = getIntent().getParcelableArrayListExtra("friends");
        }

        // Is the profileData null?!
        if (platoonData == null || platoonData.getId() == 0) {
            finish();
            return;
        }

        // Fix it
        selectedIds = new Object[friends.size()];

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Set the content view
        setContentView(R.layout.platoon_invite_view);

        // Let's see
        initLayout();

    }

    public void initLayout() {

        if (listView == null) {

            listView = getListView();
            listView.setAdapter(new PlatoonInviteListAdapter(friends, layoutInflater));

        }

    }

    public void onListItemClick(ListView lv, View v, int pos, long id) {

        if (selectedIds[pos].equals(0)) {

            selectedIds[pos] = id;
            ((CheckBox) v.findViewById(R.id.checkbox)).setChecked(true);

        } else {

            selectedIds[pos] = 0;
            ((CheckBox) v.findViewById(R.id.checkbox)).setChecked(false);

        }

    }

    public void onClick(View v) {

        if (v.getId() == R.id.button_ok) {

            new AsyncPlatoonMemberInvite(this, selectedIds, platoonData).execute(sharedPreferences
                    .getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));

        } else if (v.getId() == R.id.button_cancel) {

            this.finish();

        }

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);
    }

}
