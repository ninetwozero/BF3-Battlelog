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

import android.content.Context;
import android.content.Intent;
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
import com.ninetwozero.bf3droid.activity.profile.soldier.restorer.UserInfoRestorer;
import com.ninetwozero.bf3droid.asynctask.AsyncFriendRemove;
import com.ninetwozero.bf3droid.asynctask.AsyncFriendRequest;
import com.ninetwozero.bf3droid.datatype.PlatoonData;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.datatype.UserInfo;
import com.ninetwozero.bf3droid.http.COMClient;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.table.UserProfileData;

import java.util.List;

public class ProfileOverviewFragment extends Bf3Fragment {

    private Context context;
    private LayoutInflater layoutInflater;
    private COMClient comClient;
    private boolean postingRights;
    private List<SimplePlatoon> platoons;
    private Bundle bundle;
    private UserProfileData userProfileData;
    private UserInfoRestorer restorer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater = inflater;
        bundle = savedInstanceState;

        View view = layoutInflater.inflate(R.layout.tab_content_profile_overview, container, false);
        initFragment(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        context = getActivity();
        getData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void getData() {
        if (hasUserInfo()) {
            showProfile();
            showPlatoons();
        }
    }

    public void initFragment(View v) {
        comClient = new COMClient(getUserId(), BF3Droid.getCheckSum());
        postingRights = false;
    }

    public final void showProfile() {

        ((TextView) getView().findViewById(R.id.text_username)).setText(user().getName());

        /*if (data.isPlaying() && data.isOnline()) {
            ((TextView) activity.findViewById(R.id.text_online)).setText(
                getString(R.string.info_profile_playing).replace("{server name}", data.getCurrentServer())
            );
        } else if (data.isOnline()) {
            ((TextView) activity.findViewById(R.id.text_online)).setText(R.string.info_profile_online);
        } else {
            ((TextView) activity.findViewById(R.id.text_online)).setText(data.getLastLoginString(context));
        }*/

        if ("".equals(userProfileData.getStatusMessage())) {
            (getView().findViewById(R.id.wrap_status)).setVisibility(View.GONE);
        } else {
            ((TextView) getView().findViewById(R.id.text_status)).setText(userProfileData.getStatusMessage());
            ((TextView) getView().findViewById(R.id.text_status_date)).setText(userProfileData.getStatusMessageDate());
        }

        if ("".equals(userProfileData.getPresentation())) {
            ((TextView) getView().findViewById(R.id.text_presentation)).setText(R.string.info_profile_empty_pres);
        } else {
            ((TextView) getView().findViewById(R.id.text_presentation)).setText(userProfileData.getPresentation());
        }
    }

    private void showPlatoons() {
        platoons = user().getPlatoons();
        if (platoons.size() > 0) {
            View convertView;
            LinearLayout platoonWrapper = (LinearLayout) getView().findViewById(R.id.list_platoons);

            getView().findViewById(R.id.text_platoon).setVisibility(View.GONE);
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
            ((LinearLayout) getView().findViewById(R.id.list_platoons)).removeAllViews();
            (getView().findViewById(R.id.text_platoon)).setVisibility(View.VISIBLE);
        }
    }

    private boolean hasUserInfo(){
        this.restorer = new UserInfoRestorer(context, getArguments().getString("user"));
        UserInfo userInfo = restorer.fetch();
        if(userInfo.isEmpty()){
            return false;
        } else{
            user().setPersonas(userInfo.getPersonas());
            user().setPlatoons(userInfo.getPlatoons());
            userProfileData = userInfo.getUserProfileData();
            return  true;
        }
    }

    public void reload() {
        //new AsyncRefresh(SessionKeeper.getProfileData().getId()).execute();
        Log.e("ProfileOverviewFragment", "Reload pressed");
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
            new AsyncFriendRequest(context, getUserId()).execute(comClient);
        } else if (item.getItemId() == R.id.option_frienddel) {
            new AsyncFriendRemove(context, getUserId()).execute(comClient);
        }
        return true;
    }

    private Context getContext() {
        return getActivity().getApplicationContext();
    }

    private long getUserId() {
        return user().getId();
    }

    private User user() {
        if (getArguments().getString("user").equals(User.USER)) {
            return BF3Droid.getUser();
        } else {
            return BF3Droid.getGuest();
        }
    }
}