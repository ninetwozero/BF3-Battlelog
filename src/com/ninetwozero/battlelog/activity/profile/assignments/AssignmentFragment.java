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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.AssignmentData;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.misc.DataBank;

import java.util.List;

public class AssignmentFragment extends Fragment implements DefaultFragment {

    // Constants
    public final static int TYPE_PAIRS = 0;
    public final static int TYPE_STACK = 1;

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mViewPagerPosition;
    private int mType;

    // Elements
    private TableLayout mTableAssignments;

    // Misc
    private List<AssignmentData> mAssignments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.tab_content_assignments, container, false);

        // Get the unlocks
        if (mContext instanceof AssignmentActivity) {
            mAssignments = ((AssignmentActivity) mContext).getItemsForFragment(mViewPagerPosition);

        }

        // Init views
        initFragment(view);
        show(mAssignments);

        // Return the view
        return view;

    }

    public void initFragment(View v) {

        // Prepare to tango
        mTableAssignments = (TableLayout) v.findViewById(R.id.table_assignments);

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    public int getViewPagerPosition() {

        return mViewPagerPosition;

    }

    public void setViewPagerPosition(int p) {

        mViewPagerPosition = p;

    }

    // Loop & create
    public void displayPairsInTable(TableLayout table, List<AssignmentData> assignments) {

        for (int i = 0, max = assignments.size(); i < max; i += 2) {

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
            AssignmentData ass1 = assignments.get(i);
            AssignmentData ass2 = assignments.get(i + 1);

            // Set the images
            imageLeft.setImageResource(ass1.getResourceId());
            if (ass1.isCompleted()) {

                imageRight.setImageResource(ass2.getResourceId());

            } else {

                imageRight.setImageResource(R.drawable.assignment_locked);

            }

            // Set the tags
            imageLeft.setTag(ass1); // i
            imageRight.setTag(ass2); // i+1

            imageLeft.setOnClickListener(

                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AssignmentData data = (AssignmentData) v.getTag();
                            createDialog(data);
                        }

                    }
            );
            imageRight.setOnClickListener(

                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AssignmentData data = (AssignmentData) v.getTag();
                            createDialog(data);
                        }

                    }
            );

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

    public void displayStackedInTable(TableLayout table, List<AssignmentData> assignments) {

        boolean completedAll = false;
        for (int i = 0, max = assignments.size(); i < max; i++) {

            // Get the data
            AssignmentData assignment = assignments.get(i);
            int progressValue = assignment.getProgress();

            // Init the elements
            TableRow tableRow = (TableRow) mLayoutInflater.inflate(
                    R.layout.list_item_assignment_stacked, null);
            ProgressBar progress = (ProgressBar) tableRow.findViewById(R.id.progress);
            ImageView image = (ImageView) tableRow.findViewById(R.id.image_assignment);

            // Act
            if (i == (max - 1)) {
                image.setImageResource(completedAll
                        ? assignment.getResourceId()
                        : R.drawable.assignment_locked);
            } else {
                completedAll = assignment.isCompleted();
                image.setImageResource(assignment.getResourceId());
            }
            image.setOnClickListener(

                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AssignmentData data = (AssignmentData) v.getTag();
                            createDialog(data);
                        }

                    }
            );

            // ...and set the progress bars
            progress.setProgress(progressValue);
            progress.setMax(100);

            // Set the tags
            image.setTag(assignment);

            // Add the table row
            mTableAssignments.addView(tableRow);

        }

    }

    public void show(List<AssignmentData> data) {

        // Setup the view
        mTableAssignments.removeAllViews();
        if (mType == TYPE_PAIRS) {

            displayPairsInTable(mTableAssignments, data);

        } else if (mType == TYPE_STACK) {

            displayStackedInTable(mTableAssignments, data);

        }
    }

    @Override
    public void reload() {

    }

    private void createDialog(AssignmentData mCurrentPopupData) {

        // Init
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialog = mLayoutInflater.inflate(R.layout.popup_dialog_view, null);
        LinearLayout wrapObjectives = (LinearLayout) dialog.findViewById(R.id.wrap_objectives);

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
        String[] assignmentTitleData = DataBank.getAssignmentTitle(mCurrentPopupData.getId());

        // Set the actual fields too
        ImageView imageAssignment = ((ImageView) dialog.findViewById(R.id.image_assignment));
        imageAssignment.setImageResource(mCurrentPopupData.getResourceId());

        // turn off clickable in assignment dialog (image_assignment needs it in
        // the assignment list window)
        imageAssignment.setClickable(false);
        ((TextView) dialog.findViewById(R.id.text_title))
                .setText(assignmentTitleData[0]);

        // Loop over the criterias
        for (AssignmentData.Objective objective : mCurrentPopupData.getObjectives()) {

            // Inflate a layout...
            View v = mLayoutInflater.inflate(
                    R.layout.list_item_assignment_popup, null);

            // ...and set the fields
            ((TextView) v.findViewById(R.id.text_obj_title)).setText(DataBank
                    .getAssignmentCriteria(objective.getDescription()));
            ((TextView) v.findViewById(R.id.text_obj_values)).setText(

                    (int) objective.getCurrentValue() + "/" + (int) objective.getGoalValue()

            );

            wrapObjectives.addView(v);

        }

        ((ImageView) dialog.findViewById(R.id.image_reward))
                .setImageResource(mCurrentPopupData.getUnlockResourceId());
        ((TextView) dialog.findViewById(R.id.text_rew_name))
                .setText(assignmentTitleData[1]);

        AlertDialog theDialog = builder.create();
        theDialog.setView(dialog, 0, 0, 0, 0);
        theDialog.show();
    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return null;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }

    public void setType(int i) {
        mType = i;

    }
}
