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

import com.ninetwozero.battlelog.PlatoonActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.asynctasks.AsyncPlatoonRequest;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.datatypes.PlatoonInformation;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.handlers.PlatoonHandler;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class PlatoonOverviewFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;

    // Elements
    private ImageView imageViewBadge;
    private RelativeLayout wrapWeb;

    // Misc
    private PlatoonData platoonData;
    private PlatoonInformation platoonInformation;
    private boolean hasPostingRights;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_platoon_overview,
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

        // Let's see if we're allowed to post (before we've gotten the data)
        hasPostingRights = false;

        // Add a click listener
        wrapWeb = (RelativeLayout) v.findViewById(R.id.wrap_web);
        wrapWeb.setOnClickListener(new OnClickListener() {

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
        if (imageViewBadge == null) {
            imageViewBadge = (ImageView) activity.findViewById(R.id.image_badge);
        }

        // Set some TextViews
        ((TextView) activity.findViewById(R.id.text_name_platoon)).setText(data
                .getName() + " [" + data.getTag() + "]");
        ((TextView) activity.findViewById(R.id.text_date)).setText(

                getString(R.string.info_platoon_created).replace(

                        "{DATE}", PublicUtils.getDate(data.getDate())

                        ).replace(

                                "{RELATIVE DATE}",
                                PublicUtils.getRelativeDate(context, data.getDate())

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
        imageViewBadge.setImageBitmap(

                BitmapFactory.decodeFile(

                        PublicUtils.getCachePath(context) + data.getId() + ".jpeg"

                        )

                );

        // Do we have a link?!
        if (data.getWebsite() != null && !data.getWebsite().equals("")) {

            ((TextView) activity.findViewById(R.id.text_web)).setText(data.getWebsite());
            ((View) activity.findViewById(R.id.wrap_web)).setTag(data.getWebsite());

        } else {

            ((View) activity.findViewById(R.id.wrap_web)).setVisibility(View.GONE);

        }
        // Do we have a presentation?
        if (data.getPresentation() != null
                && !data.getPresentation().equals("")) {

            ((TextView) activity.findViewById(R.id.text_presentation)).setText(data
                    .getPresentation());

        } else {

            ((TextView) activity.findViewById(R.id.text_presentation))
                    .setText(R.string.info_profile_empty_pres);

        }

    }

    public class AsyncCache extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            // Do we?
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setTitle(context
                    .getString(R.string.general_wait));
            this.progressDialog.setMessage(context
                    .getString(R.string.general_downloading));
            this.progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {

                // Get...
                platoonInformation = CacheHandler.Platoon.select(context, platoonData.getId());

                // We got one?!
                return (platoonInformation != null);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {

                // Siiiiiiiiilent refresh
                new AsyncRefresh(context, platoonData, SessionKeeper
                        .getProfileData().getId()).execute();
                if (this.progressDialog != null) {
                    this.progressDialog.dismiss();
                }

                // Set the data
                showProfile(platoonInformation);
                sendToStats(platoonInformation);

            } else {

                new AsyncRefresh(context, platoonData, SessionKeeper
                        .getProfileData().getId()).execute();

            }

            // Get back here!
            return;

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
            this.progressDialog = null;

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
                platoonInformation = new PlatoonHandler(this.platoonData).getInformation(

                        context, sharedPreferences.getInt(
                                Constants.SP_BL_NUM_FEED,
                                Constants.DEFAULT_NUM_FEED),
                        this.activeProfileId

                        );

                return (platoonInformation != null);

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
            if (progressDialog != null) {

                if (progressDialog.isShowing()) {

                    progressDialog.dismiss();
                    progressDialog = null;

                }

            }

            // Set the data
            showProfile(platoonInformation);
            sendToStats(platoonInformation);
            sendToUsers(platoonInformation);
            setFeedPermission(platoonInformation.isMember() || hasPostingRights);

            // Get back here!
            return;

        }

    }

    public void reload() {

        // ASYNC!!!
        new AsyncRefresh(context, platoonData, SessionKeeper
                .getProfileData().getId()).execute();

    }

    public void sendToStats(PlatoonInformation p) {

        ((PlatoonActivity) context).openStats(p);

    }

    public void sendToUsers(PlatoonInformation p) {

        ((PlatoonActivity) context).openMembers(p);

    }

    public void setFeedPermission(boolean c) {

        ((PlatoonActivity) context).setFeedPermission(c);

    }

    public void setPlatoonData(PlatoonData p) {

        platoonData = p;
    }

    public Menu prepareOptionsMenu(Menu menu) {

        // Is it null?
        if (platoonInformation == null) {
            return menu;
        }
        if (platoonInformation.isOpenForNewMembers()) {

            if (platoonInformation.isMember()) {

                ((MenuItem) menu.findItem(R.id.option_join))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_leave))
                        .setVisible(true);
                ((MenuItem) menu.findItem(R.id.option_fans))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_invite))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_members))
                        .setVisible(false);

            } else if (platoonInformation.isOpenForNewMembers()) {

                ((MenuItem) menu.findItem(R.id.option_join))
                        .setVisible(true);
                ((MenuItem) menu.findItem(R.id.option_leave))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_fans))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_invite))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_members))
                        .setVisible(false);

            } else {

                ((MenuItem) menu.findItem(R.id.option_join))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_leave))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_fans))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_invite))
                        .setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_members))
                        .setVisible(false);
            }

        } else {

            ((MenuItem) menu.findItem(R.id.option_join)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_leave)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_fans)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_invite))
                    .setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_members))
                    .setVisible(false);

        }
        return menu;

    }

    public boolean handleSelectedOption(MenuItem item) {

        if (item.getItemId() == R.id.option_join) {

            new AsyncPlatoonRequest(

                    context, platoonData, SessionKeeper.getProfileData().getId(), sharedPreferences.getString(
                            Constants.SP_BL_PROFILE_CHECKSUM, "")

            ).execute(true);

        } else if (item.getItemId() == R.id.option_leave) {

            new AsyncPlatoonRequest(

                    context, platoonData, SessionKeeper.getProfileData()
                            .getId(), sharedPreferences.getString(
                            Constants.SP_BL_PROFILE_CHECKSUM, "")

            ).execute(false);

        }

        return true;

    }

}
