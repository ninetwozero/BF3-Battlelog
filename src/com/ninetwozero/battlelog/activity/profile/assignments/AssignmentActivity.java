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

package com.ninetwozero.battlelog.activity.profile.assignments;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.AssignmentData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.http.RequestHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class AssignmentActivity extends Activity {

    // SharedPreferences for shizzle
    private SharedPreferences mSharedPreferences;
    private LayoutInflater mLayoutInflater;
    private ProfileData mProfileData;
    private Map<Long, List<AssignmentData>> mAssignments;
    private long mSelectedPersona;
    private int mSelectedPosition;
    private long[] mPersonaId;
    private String[] mPersonaName;

    // Elements
    private TableLayout mTableAssignments;
    private TextView mTextEmpty;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Setup the locale
        PublicUtils.setupFullscreen(this, mSharedPreferences);
        PublicUtils.setupSession(this, mSharedPreferences);
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Set the content view
        setContentView(R.layout.assignment_view);

        // Get the intent
        if (getIntent().hasExtra("profile")) {

            mProfileData = (ProfileData) getIntent().getParcelableExtra(
                    "profile");

        } else {

            Toast.makeText(this, R.string.info_general_noprofile,
                    Toast.LENGTH_SHORT).show();
            finish();

        }

        // Let's call initActivity()
        initActivity();

    }

    public void initActivity() {

        // Prepare to tango
        mTextEmpty = (TextView) findViewById(R.id.text_empty);

        // Let's try something out
        if (mProfileData.getId() == SessionKeeper.getProfileData().getId()) {

            mSelectedPersona = mSharedPreferences.getLong(Constants.SP_BL_PERSONA_CURRENT_ID, 0);
            mSelectedPosition = mSharedPreferences.getInt(Constants.SP_BL_PERSONA_CURRENT_POS, 0);

        } else {

            mSelectedPersona = mProfileData.getPersona(0).getId();

        }

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, mSharedPreferences);

        // Reload the layout
        reload();

    }

    public void setupList(List<AssignmentData> data) {

        // Do we have the TableLayout?
        if (mTableAssignments == null) {

            mTableAssignments = (TableLayout) findViewById(R.id.table_assignments);

        }

        // Is it empty?
        if (data == null || data.isEmpty()) {

            mTextEmpty.setVisibility(View.VISIBLE);
            mTableAssignments.removeAllViews();

        } else {

            mTextEmpty.setVisibility(View.GONE);

        }

        // Let's clear the table
        mTableAssignments.removeAllViews();

        // Loop & create
        for (int i = 0, max = data.size(); i < max; i += 2) {

            // Init the elements
            TableRow tableRow = (TableRow) mLayoutInflater.inflate(
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
            mTableAssignments.addView(tableRow);

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
        new AsyncReload(this).execute(mProfileData);

    }

    public void doFinish() {
    }

    private class AsyncReload extends
            AsyncTask<ProfileData, Void, Boolean> {

        // Attributes
        private Context context;
        private ProgressDialog progressDialog;

        public AsyncReload(Context c) {

            context = c;

        }

        @Override
        protected void onPreExecute() {

            // Let's see if we got data already
            if (mAssignments == null) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle(context
                        .getString(R.string.general_wait));
                progressDialog
                        .setMessage(getString(R.string.general_downloading));
                progressDialog.show();

            }

        }

        @Override
        protected Boolean doInBackground(ProfileData... arg0) {

            try {

                ProfileClient profileHandler = new ProfileClient(arg0[0]);
                mAssignments = profileHandler.getAssignments(context);
                return (mAssignments != null);

            } catch (WebsiteHandlerException ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Fail?
            if (!result) {

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(context, R.string.general_no_data,
                        Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();

            }

            // Do actual stuff
            setupList(mAssignments.get(mSelectedPersona));

            // Go go go
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

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

            reload();

        } else if (item.getItemId() == R.id.option_change) {

            generateDialogPersonaList().show();

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
        AssignmentData assignment = mAssignments.get(mSelectedPersona).get(id);

        View dialog = mLayoutInflater.inflate(R.layout.popup_dialog_view, null);
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

        // turn off clickable in assignment dialog (image_assignment needs it in
        // the assignment list window)
        imageAssignment.setClickable(false);
        ((TextView) dialog.findViewById(R.id.text_title))
                .setText(assignmentTitleData[0]);

        // Loop over the criterias
        for (AssignmentData.Objective objective : assignment.getObjectives()) {

            // Inflate a layout...
            View v = mLayoutInflater.inflate(
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

    }

    public Dialog generateDialogPersonaList() {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title and the view
        builder.setTitle(R.string.info_dialog_soldierselect);

        // Do we have items to show?
        if (mPersonaId == null) {

            // Init
            mPersonaId = new long[mProfileData.getNumPersonas()];
            mPersonaName = new String[mProfileData.getNumPersonas()];

            // Iterate
            for (int count = 0, max = mPersonaId.length; count < max; count++) {

                mPersonaId[count] = mProfileData.getPersona(count).getId();
                mPersonaName[count] = mProfileData.getPersona(count).getName();

            }

        }

        // Set it up
        builder.setSingleChoiceItems(

                mPersonaName, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        if (mPersonaId[item] != mSelectedPersona) {

                            // Update it
                            mSelectedPersona = mPersonaId[item];

                            // Store selectedPersonaPos
                            mSelectedPosition = item;

                            // Update the layout
                            setupList(mAssignments.get(mSelectedPersona));

                            // Save it
                            if (mProfileData.getId() == SessionKeeper.getProfileData().getId()) {
                                SharedPreferences.Editor spEdit = mSharedPreferences.edit();
                                spEdit.putLong(Constants.SP_BL_PERSONA_CURRENT_ID, mSelectedPersona);
                                spEdit.putInt(Constants.SP_BL_PERSONA_CURRENT_POS,
                                        mSelectedPosition);
                                spEdit.commit();
                            }

                        }

                        dialog.dismiss();

                    }

                }

                );

        // CREATE
        return builder.create();

    }
}
