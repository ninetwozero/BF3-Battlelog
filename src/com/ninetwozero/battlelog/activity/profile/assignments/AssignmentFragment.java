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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.AssignmentData;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.misc.DataBank;

public class AssignmentFragment extends Fragment implements DefaultFragment {

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

        View view = mLayoutInflater.inflate(R.layout.tab_content_assignments, container, false);

        if (mContext instanceof AssignmentActivity) {
            mAssignments = ((AssignmentActivity) mContext).getItemsForFragment(mViewPagerPosition);
        }
        initFragment(view);
        show(mAssignments);
        return view;

    }

    public void initFragment(View v) {
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
    public void displayPairsInTable(List<AssignmentData> assignments) {

        for (int i = 0, max = assignments.size(); i < max; i += 2) {
            TableRow tableRow = (TableRow) mLayoutInflater.inflate(
                    R.layout.list_item_assignment, null);
            RelativeLayout leftLayout = (RelativeLayout) tableRow.findViewById(R.id.assignment_left);
            ImageView imageLeft = (ImageView) leftLayout.findViewById(R.id.assignment_image);
            ProgressBar progressLeft = (ProgressBar) leftLayout.findViewById(R.id.assignment_progress);

            RelativeLayout rightLayout = (RelativeLayout)tableRow.findViewById(R.id.assignment_right);
            ImageView imageRight = (ImageView) rightLayout.findViewById(R.id.assignment_progress);
            ProgressBar progressRight = (ProgressBar) rightLayout.findViewById(R.id.assignment_progress);

            mTableAssignments.addView(tableRow);

            AssignmentData ass1 = assignments.get(i);
            AssignmentData ass2 = assignments.get(i + 1);

            imageLeft.setImageResource(ass1.getResourceId());
            if (ass1.isCompleted()) {
                imageRight.setImageResource(ass2.getResourceId());
            } else {
                imageRight.setImageResource(R.drawable.assignment_locked);
            }

            imageLeft.setTag(ass1);
            imageRight.setTag(ass2);

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

            int progressValueLeft = ass1.getProgress();
            int progressValueRight = ass2.getProgress();

            progressLeft.setProgress(progressValueLeft);
            progressLeft.setMax(100);
            progressRight.setProgress(progressValueRight);
            progressRight.setMax(100);
        }
    }

    public void displayStackedInTable(List<AssignmentData> assignments) {

        boolean completedAll = false;
        for (int i = 0, max = assignments.size(); i < max; i++) {

            AssignmentData assignment = assignments.get(i);
            int progressValue = assignment.getProgress();

            TableRow tableRow = (TableRow) mLayoutInflater.inflate(R.layout.list_item_assignment_stacked, null);
            ImageView image = (ImageView) tableRow.findViewById(R.id.assignment_image);
            ProgressBar progress = (ProgressBar) tableRow.findViewById(R.id.assignment_progress);

            if (i == (max - 1)) {
                image.setImageResource(completedAll? assignment.getResourceId(): R.drawable.assignment_locked);
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

            progress.setProgress(progressValue);
            progress.setMax(100);

            image.setTag(assignment);

            mTableAssignments.addView(tableRow);
        }
    }

    public void show(List<AssignmentData> data) {
        mTableAssignments.removeAllViews();
        if (mType == TYPE_PAIRS) {
            displayPairsInTable(data);
        } else if (mType == TYPE_STACK) {
            displayStackedInTable(data);
        }
    }

    @Override
    public void reload() {}

    private void createDialog(AssignmentData mCurrentPopupData) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialog = mLayoutInflater.inflate(R.layout.popup_dialog_view, null);
        LinearLayout wrapObjectives = (LinearLayout) dialog.findViewById(R.id.wrap_objectives);

        builder.setCancelable(false).setPositiveButton("OK",new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });

        builder.setView(dialog);
        builder.setCancelable(true);

        String[] assignmentTitleData = DataBank.getAssignmentTitle(mCurrentPopupData.getId());

        ImageView imageAssignment = ((ImageView) dialog.findViewById(R.id.image_assignment));
        imageAssignment.setImageResource(mCurrentPopupData.getResourceId());

        imageAssignment.setClickable(false);
        ((TextView) dialog.findViewById(R.id.text_title)).setText(assignmentTitleData[0]);

        for (AssignmentData.Objective objective : mCurrentPopupData.getObjectives()) {

            View v = mLayoutInflater.inflate(R.layout.list_item_assignment_popup, null);

            ((TextView) v.findViewById(R.id.text_obj_title)).setText(DataBank
                    .getAssignmentCriteria(objective.getDescription()));
            ((TextView) v.findViewById(R.id.text_obj_values)).setText(
                    (int) objective.getCurrentValue() + "/" + (int) objective.getGoalValue());
            wrapObjectives.addView(v);
        }

        ((ImageView) dialog.findViewById(R.id.image_reward)).setImageResource(mCurrentPopupData.getUnlockResourceId());
        ((TextView) dialog.findViewById(R.id.text_rew_name)).setText(assignmentTitleData[1]);

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
