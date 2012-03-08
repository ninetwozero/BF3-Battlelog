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

package com.ninetwozero.battlelog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.asynctasks.AsyncSessionSetActive;
import com.ninetwozero.battlelog.asynctasks.AsyncSessionValidate;
import com.ninetwozero.battlelog.datatypes.AssignmentData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AssignmentView extends Activity {

    // SharedPreferences for shizzle
    private final Context CONTEXT = this;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    private ProfileData profileData;
    private HashMap<Long, ArrayList<AssignmentData>> assignments;
    private long selectedPersona;
    private int selectedPosition;

    // Elements
    private TableLayout tableAssignments;
    private TextView textEmpty;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Did it get passed on?
        if (icicle != null && icicle.containsKey(Constants.SUPER_COOKIES)) {

            ArrayList<ShareableCookie> shareableCookies = icicle
                    .getParcelableArrayList(Constants.SUPER_COOKIES);
            RequestHandler.setCookies(shareableCookies);

        }

        // Setup the locale
        setupLocale();

        // Set the content view
        setContentView(R.layout.assignment_view);

        // Get the intent
        if (getIntent().hasExtra("profile")) {

            profileData = (ProfileData) getIntent().getParcelableExtra(
                    "profile");

        } else {

            Toast.makeText(this, R.string.info_general_noprofile,
                    Toast.LENGTH_SHORT).show();
            return;

        }

        // Is the profileData null?!
        if (profileData == null || profileData.getProfileId() == 0) {
            finish();
            return;
        }

        // Get the pos
        selectedPersona = getIntent().getLongExtra("selectedPersona",
                profileData.getPersonaId());
        selectedPosition = 0;

        // Prepare to tango
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.textEmpty = (TextView) findViewById(R.id.text_empty);

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        setupLocale();

        // Setup the session
        setupSession();

        // Reload the layout
        this.reload();

    }

    public void setupList(ArrayList<AssignmentData> data) {

        // Do we have the TableLayout?
        if (tableAssignments == null) {

            tableAssignments = (TableLayout) findViewById(R.id.table_assignments);

        }

        // Is it empty?
        if (data == null || data.size() == 0) {

            textEmpty.setVisibility(View.VISIBLE);
            tableAssignments.removeAllViews();
            return;

        } else {

            textEmpty.setVisibility(View.GONE);

        }

        // Let's clear the table
        tableAssignments.removeAllViews();

        // Loop & create
        for (int i = 0, max = data.size(); i < max; i += 2) {

            // Init the elements
            TableRow tableRow = (TableRow) layoutInflater.inflate(
                    R.layout.list_item_assignment, null);
            ProgressBar progressLeft = (ProgressBar) tableRow
                    .findViewById(R.id.progress_left);
            ProgressBar progressRight = (ProgressBar) tableRow
                    .findViewById(R.id.progress_right);
            ImageView imageLeft = (ImageView) tableRow
                    .findViewById(R.id.image_leftassignment);
            ImageView imageRight = (ImageView) tableRow
                    .findViewById(R.id.image_rightassignment);

            // Add the table row
            tableAssignments.addView(tableRow);

            // Get the values
            AssignmentData ass1 = data.get(i);
            AssignmentData ass2 = data.get(i + 1);

            // Set the images
            imageLeft.setImageResource(ass1.getResourceId());
            if (ass1.isCompleted()) {

                imageRight.setImageResource(ass2.getResourceId());

            } else {

                imageRight.setImageResource(R.drawable.assignment_locked);

            }

            // Set the tags
            imageLeft.setTag(i);
            imageRight.setTag(i + 1);

            // Get the progress...
            int progressValueLeft = ass1.getProgress();
            int progressValueRight = ass2.getProgress();

            // ...and set the progress bars
            progressLeft.setProgress(progressValueLeft);
            progressLeft.setMax(100);
            progressRight.setProgress(progressValueRight);
            progressRight.setMax(100);

        }

    }

    public void reload() {

        // ASYNC!!!
        new GetDataSelfAsync(this).execute(profileData);

    }

    public void doFinish() {
    }

    private class GetDataSelfAsync extends
            AsyncTask<ProfileData, Void, Boolean> {

        // Attributes
        Context context;
        ProgressDialog progressDialog;

        public GetDataSelfAsync(Context c) {

            this.context = c;
            this.progressDialog = null;

        }

        @Override
        protected void onPreExecute() {

            // Let's see if we got data already
            if (assignments == null) {

                this.progressDialog = new ProgressDialog(this.context);
                this.progressDialog.setTitle(context
                        .getString(R.string.general_wait));
                this.progressDialog
                        .setMessage(getString(R.string.general_downloading));
                this.progressDialog.show();

            }

        }

        @Override
        protected Boolean doInBackground(ProfileData... arg0) {

            try {

                assignments = WebsiteHandler.getAssignments(context, arg0[0]);
                return (assignments != null);

            } catch (WebsiteHandlerException ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Fail?
            if (!result) {

                if (this.progressDialog != null)
                    this.progressDialog.dismiss();
                Toast.makeText(this.context, R.string.general_no_data,
                        Toast.LENGTH_SHORT).show();
                ((Activity) this.context).finish();
                return;

            }

            // Do actual stuff
            setupList(assignments.get(selectedPersona));

            // Go go go
            if (this.progressDialog != null)
                this.progressDialog.dismiss();
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_unlock, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Let's act!
        if (item.getItemId() == R.id.option_reload) {

            this.reload();

        } else if (item.getItemId() == R.id.option_change) {

            generateDialogPersonaList(

                    this, profileData.getPersonaIdArray(),
                    profileData.getPersonaNameArray(),
                    profileData.getPlatformIdArray()

            ).show();

        } else if (item.getItemId() == R.id.option_back) {

            ((Activity) this).finish();

        }

        // Return true yo
        return true;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SUPER_COOKIES,
                RequestHandler.getCookies());

    }

    @Override
    protected Dialog onCreateDialog(int id) {

        // Init
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AssignmentData assignment = assignments.get(selectedPersona).get(id);
        AssignmentData.Unlock unlocks = assignment.getUnlocks().get(0);

        View dialog = layoutInflater.inflate(R.layout.popup_dialog_view, null);
        LinearLayout wrapObjectives = (LinearLayout) dialog
                .findViewById(R.id.wrap_objectives);

        // Set the title
        builder.setCancelable(false).setPositiveButton("OK",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                    }

                });

        // Create the dialog and set the contentView
        builder.setView(dialog);
        builder.setCancelable(true);

        // Grab the data
        String[] assignmentTitleData = DataBank.getAssignmentTitle(assignment
                .getId());

        // Set the actual fields too
        ImageView imageAssignment = ((ImageView) dialog.findViewById(R.id.image_assignment));
        imageAssignment.setImageResource(assignment.getResourceId());
        
        // turn off clickable in assignment dialog (image_assignment is clickable in other dialogs)
        imageAssignment.setClickable(false);
        
        ((TextView) dialog.findViewById(R.id.text_title))
                .setText(assignmentTitleData[0]);
        

        // Loop over the criterias
        for (AssignmentData.Objective objective : assignment.getObjectives()) {

            // Inflate a layout...
            View v = layoutInflater.inflate(
                    R.layout.list_item_assignment_popup, null);

            // ...and set the fields
            ((TextView) v.findViewById(R.id.text_obj_title)).setText(DataBank
                    .getAssignmentCriteria(objective.getDescription()));
            ((TextView) v.findViewById(R.id.text_obj_values)).setText(

                    objective.getCurrentValue() + "/" + objective.getGoalValue()

                    );

            wrapObjectives.addView(v);

        }

        ((ImageView) dialog.findViewById(R.id.image_reward))
                .setImageResource(assignment.getUnlockResourceId());
        ((TextView) dialog.findViewById(R.id.text_rew_name))
                .setText(assignmentTitleData[1]);

        AlertDialog theDialog = builder.create();
        theDialog.setView(dialog, 0, 0, 0, 0);

        return theDialog;
    }

    public void onPopupClick(View v) {

        showDialog(Integer.parseInt(v.getTag().toString()));
        return;

    }

    public Dialog generateDialogPersonaList(final Context context,
            final long[] personaId, final String[] persona, final long[] ls) {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and the view
        builder.setTitle(R.string.info_dialog_soldierselect);
        String[] listNames = new String[personaId.length];

        for (int i = 0, max = personaId.length; i < max; i++) {

            listNames[i] = persona[i] + " "
                    + DataBank.resolvePlatformId((int) ls[i]);

        }
        builder.setSingleChoiceItems(

                listNames, selectedPosition, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        if (personaId[item] != selectedPersona) {

                            // Update it
                            selectedPersona = profileData.getPersonaId(item);

                            // Update the layout
                            setupList(assignments.get(selectedPersona));

                            // Store selectedPersonaPos
                            selectedPosition = item;

                        }

                        dialog.dismiss();

                    }

                }

                );

        // CREATE
        return builder.create();

    }

    public void setupSession() {

        // Let's set "active" against the website
        new AsyncSessionSetActive().execute();

        // If we don't have a profile...
        if (SessionKeeper.getProfileData() == null) {

            // ...but we do indeed have a cookie...
            if (!sharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE, "")
                    .equals("")) {

                // ...we set the SessionKeeper, but also reload the cookies!
                // Easy peasy!
                SessionKeeper
                        .setProfileData(SessionKeeper
                                .generateProfileDataFromSharedPreferences(sharedPreferences));
                RequestHandler.setCookies(

                        new ShareableCookie(

                                sharedPreferences.getString(Constants.SP_BL_COOKIE_NAME, ""),
                                sharedPreferences.getString(
                                        Constants.SP_BL_COOKIE_VALUE, ""),
                                Constants.COOKIE_DOMAIN

                        )

                        );

                // ...but just to be sure, we try to verify our session
                // "behind the scenes"
                new AsyncSessionValidate(this, sharedPreferences).execute();

            } else {

                // Aw man, that backfired.
                Toast.makeText(this, R.string.info_txt_session_lost,
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Main.class));
                finish();

            }

        }

    }

    public void setupLocale() {

        if (!sharedPreferences.getString(Constants.SP_BL_LANG, "").equals("")) {

            Locale locale = new Locale(sharedPreferences.getString(
                    Constants.SP_BL_LANG, "en"));
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config,
                    getResources().getDisplayMetrics());

        }

    }

}
