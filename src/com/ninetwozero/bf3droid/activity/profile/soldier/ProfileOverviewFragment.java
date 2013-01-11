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

package com.ninetwozero.bf3droid.activity.profile.soldier;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3Fragment;
import com.ninetwozero.bf3droid.activity.platoon.PlatoonActivity;
import com.ninetwozero.bf3droid.asynctask.AsyncFriendRemove;
import com.ninetwozero.bf3droid.asynctask.AsyncFriendRequest;
import com.ninetwozero.bf3droid.dao.PlatoonInformationDAO;
import com.ninetwozero.bf3droid.dao.ProfileInformationDAO;
import com.ninetwozero.bf3droid.dao.UserProfileDataDAO;
import com.ninetwozero.bf3droid.datatype.PlatoonData;
import com.ninetwozero.bf3droid.datatype.ProfileInformation;
import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.http.COMClient;
import com.ninetwozero.bf3droid.provider.table.UserProfileData;

import java.util.List;

public class ProfileOverviewFragment extends Bf3Fragment {
    private Context context;
    private LayoutInflater layoutInflater;
    private COMClient comClient;
    private SharedPreferences sharedPreferences;
    private boolean postingRights;
    private List<SimplePlatoon> platoons;
    private UserProfileData profileData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        layoutInflater = inflater;

        View view = layoutInflater.inflate(R.layout.tab_content_profile_overview, container, false);
        initFragment(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        context = getActivity();
        //new AsyncCache().execute();
        profileData = userProfileDataFromDB();
        platoons = BF3Droid.getUserPlatoons();
        showProfile();
    }

    public void initFragment(View v) {
    	comClient = new COMClient(BF3Droid.getUserId(), BF3Droid.getCheckSum());
        postingRights = false;
    }

    public final void showProfile() {
        Activity activity = (Activity) context;
        
        ((TextView) activity.findViewById(R.id.text_username)).setText(BF3Droid.getUser());

        /*if (data.isPlaying() && data.isOnline()) {
            ((TextView) activity.findViewById(R.id.text_online)).setText(
                getString(R.string.info_profile_playing).replace("{server name}", data.getCurrentServer())
            );
        } else if (data.isOnline()) {
            ((TextView) activity.findViewById(R.id.text_online)).setText(R.string.info_profile_online);
        } else {
            ((TextView) activity.findViewById(R.id.text_online)).setText(data.getLastLoginString(context));
        }*/

        if ("".equals(profileData.getStatusMessage())) {
            (activity.findViewById(R.id.wrap_status)).setVisibility(View.GONE);
        } else {
            ((TextView) activity.findViewById(R.id.text_status)).setText(profileData.getStatusMessage());
            ((TextView) activity.findViewById(R.id.text_status_date)).setText(profileData.getStatusMessageDate());
        }

        if ("".equals(profileData.getPresentation())) {
            ((TextView) activity.findViewById(R.id.text_presentation)).setText(R.string.info_profile_empty_pres);
        } else {
            ((TextView) activity.findViewById(R.id.text_presentation)).setText(profileData.getPresentation());
        }

        showPlatoons(activity);
    }

    private void showPlatoons(Activity activity) {
        if (platoons.size() > 0) {
            View convertView;
            LinearLayout platoonWrapper = (LinearLayout) activity.findViewById(R.id.list_platoons);

            activity.findViewById(R.id.text_platoon).setVisibility(View.GONE);
            platoonWrapper.removeAllViews();

            final OnClickListener onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(
                    	new Intent(context, PlatoonActivity.class).putExtra("platoon", (PlatoonData) v.getTag())
                    );
                }
            };

            for (SimplePlatoon platoon : platoons) {
                if (platoonWrapper.findViewWithTag(platoon) != null) {
                    continue;
                }
                convertView = layoutInflater.inflate(R.layout.list_item_platoon, platoonWrapper, false);

                ((TextView) convertView.findViewById(R.id.text_name)).setText(platoon.getName());
                ((TextView) convertView.findViewById(R.id.text_members)).setText(String.valueOf(platoon.getMembersCount()));

                /*String image = PublicUtils.getCachePath(context)+ currentPlatoon.getImage();
                ((ImageView) convertView.findViewById(R.id.image_badge)).setImageBitmap(BitmapFactory.decodeFile(image));*/

                convertView.setTag(platoon);
                convertView.setOnClickListener(onClickListener);

                platoonWrapper.addView(convertView);
            }
        } else {
            ((LinearLayout) activity.findViewById(R.id.list_platoons)).removeAllViews();
            (activity.findViewById(R.id.text_platoon)).setVisibility(View.VISIBLE);
        }
    }

    private UserProfileData userProfileDataFromDB(){
        Cursor cursor = getContext().getContentResolver().query(
                UserProfileDataDAO.URI,
                UserProfileDataDAO.PROJECTION,
                UserProfileDataDAO.Columns.USER_ID + "=?",
                new String[]{String.valueOf(BF3Droid.getUserId())},
                null
        );
        return UserProfileDataDAO.userProfileDataFrom(cursor);
    }


    public void reload() {
        //new AsyncRefresh(SessionKeeper.getProfileData().getId()).execute();
        Log.e("ProfileOverviewFragment", "Reload pressed");
    }

    public void updateProfileInDB(ProfileInformation p) {
    	ContentValues contentValues = ProfileInformationDAO.convertProfileInformationForDB(p, System.currentTimeMillis());
    	try {
    		 context.getContentResolver().insert(ProfileInformationDAO.URI, contentValues);
    	 } catch(SQLiteConstraintException ex) {
    		 context.getContentResolver().update(
				 ProfileInformationDAO.URI, 
				 contentValues,
				 ProfileInformationDAO.Columns.USER_ID + "=?", 
				 new String[] { String.valueOf(p.getUserId()) }
			 );
    	 }
	}
    
    public void updatePlatoonInDB(PlatoonData p) {
    	ContentValues contentValues = PlatoonInformationDAO.platoonDataForDB(p);
    	context.getContentResolver().insert(PlatoonInformationDAO.URI, contentValues);
	}

    public void setFeedPermission(boolean c) {
        ((ProfileActivity) context).setFeedPermission(c);
    }

    public Menu prepareOptionsMenu(Menu menu) {
        /* Disabled for time being
        if (profileInformation == null) {
            return menu;
        }
        if (profileInformation.isAllowingFriendRequests()) {
            if (profileInformation.isFriend()) {
                (menu.findItem(R.id.option_friendadd)).setVisible(false);
                (menu.findItem(R.id.option_frienddel)).setVisible(true);
            } else {
                (menu.findItem(R.id.option_friendadd)).setVisible(true);
                (menu.findItem(R.id.option_frienddel)).setVisible(false);
            }
        } else {
            ( menu.findItem(R.id.option_friendadd)).setVisible(false);
            ( menu.findItem(R.id.option_frienddel)).setVisible(false);
        }*/
        (menu.findItem(R.id.option_compare)).setVisible(false);
        return menu;
    }

    public boolean handleSelectedOption(MenuItem item) {
        if (item.getItemId() == R.id.option_friendadd) {
            new AsyncFriendRequest(context, BF3Droid.getUserId()).execute(comClient);
        } else if (item.getItemId() == R.id.option_frienddel) {
            new AsyncFriendRemove(context, BF3Droid.getUserId()).execute(comClient);
        }
        return true;
    }

    private Context getContext() {
        return getActivity().getApplicationContext();
    }


    private SimplePersona selectedPersona(){
        return BF3Droid.selectedUserPersona();
    }
}