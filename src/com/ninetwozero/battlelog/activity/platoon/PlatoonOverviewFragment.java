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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.asynctask.AsyncPlatoonRequest;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.PlatoonInformation;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.PlatoonClient;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class PlatoonOverviewFragment extends Fragment implements
        DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SharedPreferences mSharedPreferences;

    // Elements
    private ImageView mImageViewBadge;
    private RelativeLayout mWrapWeb;

    // Misc
    private PlatoonData mPlatoonData;
    private PlatoonInformation mPlatoonInformation;
    private boolean mPostingRights;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(
                R.layout.tab_content_platoon_overview, container, false);

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

        // Let's see if we're allowed to post (before we've gotten the data)
        mPostingRights = false;

        // Add a click listener
        mWrapWeb = (RelativeLayout) v.findViewById(R.id.wrap_web);
        mWrapWeb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(

                        new Intent(Intent.ACTION_VIEW).setData(

                                Uri.parse(

                                        String.valueOf(v.getTag())

                                )

                        )

                );
            }
        });

    }

    public final void showProfile(PlatoonInformation data) {

        // Do we have valid data?
        if (data == null) {
            return;
        }

        // Get the activity
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        // Let's start by getting an ImageView
        if (mImageViewBadge == null) {
            mImageViewBadge = (ImageView) activity
                    .findViewById(R.id.image_badge);
        }

        // Set some TextViews
        ((TextView) activity.findViewById(R.id.text_name_platoon)).setText(data
                .getName() + " [" + data.getTag() + "]");
        ((TextView) activity.findViewById(R.id.text_date)).setText(

                getString(R.string.info_platoon_created).replace(

                        "{DATE}", PublicUtils.getDate(data.getDate())

                ).replace(

                        "{RELATIVE DATE}",
                        PublicUtils.getRelativeDate(mContext, data.getDate())

                ));

        // Platform!!
        switch (data.getPlatformId()) {

            case 1:
                ((ImageView) activity.findViewById(R.id.image_platform))
                        .setImageResource(R.drawable.logo_pc);
                break;

            case 2:
                ((ImageView) activity.findViewById(R.id.image_platform))
                        .setImageResource(R.drawable.logo_xbox);
                break;

            case 4:
                ((ImageView) activity.findViewById(R.id.image_platform))
                        .setImageResource(R.drawable.logo_ps3);
                break;

            default:
                ((ImageView) activity.findViewById(R.id.image_platform))
                        .setImageResource(R.drawable.logo_pc);
                break;

        }

        // Set the properties
        mImageViewBadge.setImageBitmap(

                BitmapFactory.decodeFile(

                        PublicUtils.getCachePath(mContext) + data.getId() + ".jpeg"

                )

        );

        // Do we have a link?!
        if ("".equals(data.getWebsite())) {

            ((View) activity.findViewById(R.id.wrap_web))
                    .setVisibility(View.GONE);

        } else {

            ((TextView) activity.findViewById(R.id.text_web)).setText(data
                    .getWebsite());
            ((View) activity.findViewById(R.id.wrap_web)).setTag(data
                    .getWebsite());

        }

        // Do we have a presentation?
        if (data.getPresentation().equals("")) {

            ((TextView) activity.findViewById(R.id.text_presentation))
                    .setText(R.string.info_profile_empty_pres);

        } else {

            ((TextView) activity.findViewById(R.id.text_presentation))
                    .setText(data.getPresentation());

        }

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
                mPlatoonInformation = CacheHandler.Platoon.select(mContext,
                        mPlatoonData.getId());

                // We got one?!
                return (mPlatoonInformation != null);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {

                // Siiiiiiiiilent refresh
                new AsyncRefresh(mContext, mPlatoonData, SessionKeeper
                        .getProfileData().getId()).execute();
                if (this.progressDialog != null) {
                    this.progressDialog.dismiss();
                }

                // Set the data
                showProfile(mPlatoonInformation);
                sendToStats(mPlatoonInformation);

            } else {

                new AsyncRefresh(mContext, mPlatoonData, SessionKeeper
                        .getProfileData().getId(), progressDialog).execute();

            }

        }

    }

    public class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context context;
        private ProgressDialog progressDialog;
        private PlatoonData platoonData;
        private long activeProfileId;

        public AsyncRefresh(Context c, PlatoonData pd, long pId) {

            this.context = c;
            this.platoonData = pd;
            this.activeProfileId = pId;

        }

        public AsyncRefresh(Context c, PlatoonData pd, long pId,
                            ProgressDialog p) {

            this.context = c;
            this.platoonData = pd;
            this.activeProfileId = pId;
            this.progressDialog = p;

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {

                // Get...
                mPlatoonInformation = new PlatoonClient(this.platoonData)
                        .getInformation(

                                context, mSharedPreferences.getInt(
                                Constants.SP_BL_NUM_FEED,
                                Constants.DEFAULT_NUM_FEED),
                                this.activeProfileId

                        );

                return (mPlatoonInformation != null);

            } catch (WebsiteHandlerException ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Fail?
            if (!result) {

                Toast.makeText(context, R.string.general_no_data,
                        Toast.LENGTH_SHORT).show();

            }

            // Do we have a dialog?
            if (progressDialog != null && progressDialog.isShowing()) {

                progressDialog.dismiss();

            }

            // Set the data
            showProfile(mPlatoonInformation);
            sendToStats(mPlatoonInformation);
            sendToUsers(mPlatoonInformation);
            setFeedPermission(mPlatoonInformation.isMember() || mPostingRights);

        }

    }

    public void reload() {

        // ASYNC!!!
        new AsyncRefresh(mContext, mPlatoonData, SessionKeeper.getProfileData()
                .getId()).execute();

    }

    public void sendToStats(PlatoonInformation p) {

        ((PlatoonActivity) mContext).openStats(p);

    }

    public void sendToUsers(PlatoonInformation p) {

        ((PlatoonActivity) mContext).openMembers(p);

    }

    public void setFeedPermission(boolean c) {

        ((PlatoonActivity) mContext).setFeedPermission(c);

    }

    public void setPlatoonData(PlatoonData p) {

        mPlatoonData = p;
    }

    public Menu prepareOptionsMenu(Menu menu) {

        // Is it null?
        if (mPlatoonInformation == null) {
            return menu;
        }
        if (mPlatoonInformation.isOpenForNewMembers()) {

            if (mPlatoonInformation.isMember()) {

                ((MenuItem) menu.findItem(R.id.option_join)).setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_leave)).setVisible(true);
                ((MenuItem) menu.findItem(R.id.option_fans)).setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_invite))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_members))
                        .setVisible(false);

            } else if (mPlatoonInformation.isOpenForNewMembers()) {

                ((MenuItem) menu.findItem(R.id.option_join)).setVisible(true);
                ((MenuItem) menu.findItem(R.id.option_leave)).setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_fans)).setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_invite))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_members))
                        .setVisible(false);

            } else {

                ((MenuItem) menu.findItem(R.id.option_join)).setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_leave)).setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_fans)).setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_invite))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_members))
                        .setVisible(false);
            }

        } else {

            ((MenuItem) menu.findItem(R.id.option_join)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_leave)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_fans)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_invite)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_members)).setVisible(false);

        }
        return menu;

    }

    public boolean handleSelectedOption(MenuItem item) {

        if (item.getItemId() == R.id.option_join) {

            new AsyncPlatoonRequest(

                    mContext, mPlatoonData, SessionKeeper.getProfileData().getId(),
                    mSharedPreferences.getString(
                            Constants.SP_BL_PROFILE_CHECKSUM, "")

            ).execute(true);

        } else if (item.getItemId() == R.id.option_leave) {

            new AsyncPlatoonRequest(

                    mContext, mPlatoonData, SessionKeeper.getProfileData().getId(),
                    mSharedPreferences.getString(
                            Constants.SP_BL_PROFILE_CHECKSUM, "")

            ).execute(false);

        }

        return true;

    }

}
