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

package com.ninetwozero.battlelog.activity.profile.soldier;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.platoon.PlatoonActivity;
import com.ninetwozero.battlelog.asynctask.AsyncFriendRemove;
import com.ninetwozero.battlelog.asynctask.AsyncFriendRequest;
import com.ninetwozero.battlelog.datatype.*;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class ProfileOverviewFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SharedPreferences mSharedPreferences;

    // Misc
    private ProfileData mProfileData;
    private ProfileInformation mProfileInformation;
    private boolean mPostingRights;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.tab_content_profile_overview,
                container, false);

        // Init the fragment
        initFragment(view);

        // Let's return the view
        return view;

    }

    @Override
    public void onResume() {

        super.onResume();
        new AsyncCache().execute();

    }

    public void initFragment(View v) {

        // Let's see if we're allowed to post (before we've gotten the data
        // atleast)
        mPostingRights = false;

    }

    public final void showProfile(ProfileInformation data) {

        // Do we have valid data?
        if (data == null) {
            return;

        }

        // Get activity
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        // Let's start drawing the... layout
        ((TextView) activity.findViewById(R.id.text_username)).setText(data.getUsername());

        // When did was the users last login?
        if (data.isPlaying() && data.isOnline()) {

            ((TextView) activity.findViewById(R.id.text_online)).setText(

                    getString(R.string.info_profile_playing).replace(

                            "{server name}", data.getCurrentServer()

                    )

            );

        } else if (data.isOnline()) {

            ((TextView) activity.findViewById(R.id.text_online))
                    .setText(R.string.info_profile_online);

        } else {

            ((TextView) activity.findViewById(R.id.text_online)).setText(data
                    .getLastLogin(mContext));

        }

        // Is the status ""?
        if ("".equals(data.getStatusMessage())) {

            // Hide the view
            ((RelativeLayout) activity.findViewById(R.id.wrap_status))
                    .setVisibility(View.GONE);

        } else {

            // Set the status
            ((TextView) activity.findViewById(R.id.text_status)).setText(data
                    .getStatusMessage());
            ((TextView) activity.findViewById(R.id.text_status_date))
                    .setText(PublicUtils.getRelativeDate(mContext,
                            data.getStatusMessageChanged(),
                            R.string.info_lastupdate));
        }

        // Do we have a presentation?
        if ("".equals(data.getPresentation())) {

            ((TextView) activity.findViewById(R.id.text_presentation))
                    .setText(R.string.info_profile_empty_pres);
        } else {

            ((TextView) activity.findViewById(R.id.text_presentation)).setText(data
                    .getPresentation());

        }

        // Any platoons?
        if (data.getNumPlatoons() > 0) {

            // Init
            View convertView;
            LinearLayout platoonWrapper = (LinearLayout) activity.findViewById(
                    R.id.list_platoons);

            // Clear the platoonWrapper
            ((TextView) activity.findViewById(R.id.text_platoon))
                    .setVisibility(View.GONE);
            platoonWrapper.removeAllViews();

            final OnClickListener onClickListener = new OnClickListener() {

                @Override
                public void onClick(View v) {

                    // On-click
                    startActivity(

                            new Intent(mContext, PlatoonActivity.class).putExtra(

                                    "platoon", (PlatoonData) v.getTag()

                            )

                    );

                }

            };

            // Iterate over the platoons
            for (PlatoonData currentPlatoon : data.getPlatoons()) {

                // Does it already exist?
                if (platoonWrapper.findViewWithTag(currentPlatoon) != null) {
                    continue;
                }

                // Recycle
                convertView = mLayoutInflater.inflate(
                        R.layout.list_item_platoon, platoonWrapper, false);

                // Set the TextViews
                ((TextView) convertView.findViewById(R.id.text_name))
                        .setText(currentPlatoon.getName());
                ((TextView) convertView.findViewById(R.id.text_tag))
                        .setText("[" + currentPlatoon.getTag() + "]");
                ((TextView) convertView.findViewById(R.id.text_members))
                        .setText(String.valueOf(currentPlatoon.getCountMembers()));
                ((TextView) convertView.findViewById(R.id.text_fans))
                        .setText(String.valueOf(currentPlatoon.getCountFans()));

                // Almost forgot - we got a Bitmap too!
                ((ImageView) convertView.findViewById(R.id.image_badge))
                        .setImageBitmap(

                                BitmapFactory.decodeFile(PublicUtils.getCachePath(mContext)
                                        + currentPlatoon.getImage())

                        );

                // Store it in the tag
                convertView.setTag(currentPlatoon);
                convertView.setOnClickListener(onClickListener);

                // Add it!
                platoonWrapper.addView(convertView);

            }

        } else {

            ((LinearLayout) activity.findViewById(R.id.list_platoons)).removeAllViews();
            ((TextView) activity.findViewById(R.id.text_platoon))
                    .setVisibility(View.VISIBLE);

        }

        // Set the username
        ((TextView) activity.findViewById(R.id.text_username)).setText(data
                .getUsername());
    }

    public class AsyncCache extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            // Do we?
            this.progressDialog = new ProgressDialog(mContext);
            this.progressDialog.setTitle(mContext
                    .getString(R.string.general_wait));
            this.progressDialog.setMessage(mContext
                    .getString(R.string.general_downloading));
            this.progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {

                // Get...
                mProfileInformation = CacheHandler.Profile.select(mContext,
                        mProfileData.getId());

                // We got one?!
                return (mProfileInformation != null);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {

                // Siiiiiiiiilent refresh
                //new AsyncRefresh(SessionKeeper.getProfileData().getId()).execute();
                if (this.progressDialog != null) {
                    this.progressDialog.dismiss();
                }

                // Set the data for the profile
                showProfile(mProfileInformation);

                // Set the profileData IF we're down by atleast 1
                if (mProfileData.getNumPersonas() < mProfileInformation.getNumPersonas()) {

                    /* Setting reference, or *fragment variable*? */
                    mProfileData.setPersona(mProfileInformation.getAllPersonas());

                }

                // ...and then send it to the stats
                sendToStats(mProfileData);

            } else {
                //TODO disabled refresh, it was crashing application on persona changes in ProfileStatsFragment
                //new AsyncRefresh(SessionKeeper.getProfileData().getId(), progressDialog).execute();

            }

            // Get back here!

        }

    }

    public class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private long activeProfileId;
        private ProgressDialog progressDialog;

        public AsyncRefresh(long pId) {

            this.activeProfileId = pId;

        }

        public AsyncRefresh(long pId, ProgressDialog pDialog) {

            this.activeProfileId = pId;
            this.progressDialog = pDialog;

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                if (mProfileData.getNumPersonas() == 0) {
                    mProfileData = ProfileClient
                            .resolveFullProfileDataFromProfileData(mProfileData);
                }

                // Let's get the personas!
                mProfileInformation = new ProfileClient(mProfileData).getInformation(
                        mContext,activeProfileId);

                // ...and then send it to the stats
                sendToStats(mProfileData);
                return (mProfileInformation != null);

            } catch (WebsiteHandlerException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Fail?
            if (!result) {
                Toast.makeText(mContext, R.string.general_no_data,
                        Toast.LENGTH_SHORT).show();
            }

            // Do we have a dialog?
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            // Set the data
            showProfile(mProfileInformation);
            setFeedPermission(mProfileInformation.isFriend() || mPostingRights);
        }

    }

    public void reload() {
        new AsyncRefresh(SessionKeeper.getProfileData()
                .getId()).execute();
    }

    public void sendToStats(ProfileData p) {
        ((ProfileActivity) mContext).openStats(p);
    }

    public void setFeedPermission(boolean c) {
        ((ProfileActivity) mContext).setFeedPermission(c);
    }

    public void setProfileData(ProfileData p) {
        mProfileData = p;
    }

    public Menu prepareOptionsMenu(Menu menu) {

        // Is it null?
        if (mProfileInformation == null) {
            return menu;
        }
        if (mProfileInformation.isAllowingFriendRequests()) {
            if (mProfileInformation.isFriend()) {

                ((MenuItem) menu.findItem(R.id.option_friendadd))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_frienddel))
                        .setVisible(true);

            } else {

                ((MenuItem) menu.findItem(R.id.option_friendadd))
                        .setVisible(true);
                ((MenuItem) menu.findItem(R.id.option_frienddel))
                        .setVisible(false);
            }
        } else {

            ((MenuItem) menu.findItem(R.id.option_friendadd))
                    .setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_frienddel))
                    .setVisible(false);

        }
        ((MenuItem) menu.findItem(R.id.option_compare))
                .setVisible(false);
        return menu;

    }

    public boolean handleSelectedOption(MenuItem item) {
        if (item.getItemId() == R.id.option_friendadd) {
            new AsyncFriendRequest(mContext, mProfileData.getId()).execute(

                    mSharedPreferences.getString(

                            Constants.SP_BL_PROFILE_CHECKSUM, "")

            );
        } else if (item.getItemId() == R.id.option_frienddel) {
            new AsyncFriendRemove(mContext, mProfileData.getId()).execute(

                    mSharedPreferences.getString(

                            Constants.SP_BL_PROFILE_CHECKSUM, "")

            );
        }
        return true;
    }
}