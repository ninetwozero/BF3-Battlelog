/*
    This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.activity.profile.assignments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.dialog.AssignmentDialog;
import com.ninetwozero.bf3droid.jsonmodel.assignments.Mission;
import com.ninetwozero.bf3droid.jsonmodel.assignments.MissionPack;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.ninetwozero.bf3droid.util.AssignmentsMap;
import com.squareup.otto.Subscribe;

public class AssignmentFragment extends Fragment implements DefaultFragment {

    private LayoutInflater mLayoutInflater;
    private TableLayout table;
    private MissionPack missionPack;
    private int expansionId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        mLayoutInflater = inflater;
        expansionId = getArguments().getInt(AssignmentActivity.EXPANSION_ID);
        View view = mLayoutInflater.inflate(R.layout.tab_content_assignments, container, false);
        table = (TableLayout) view.findViewById(R.id.table_assignments);
        table.setId(getArguments().getInt(AssignmentActivity.EXPANSION_ID));
        return view;
    }

    public void initFragment(View v) {

    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        missionPack = ((AssignmentActivity) getActivity()).getMissionPack(expansionId);
        if (missionPack != null) {
            showData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    private void showData() {
        table.removeAllViews();
        if (expansionId == 1024) {
            premiumPackage();
        } else if(expansionId == 8192){
            aftermath(missionPack.getMissions());
        } else {
            Map<String, Mission> missions = missionPack.getMissions();
            List<String> keys = Arrays.asList(missions.keySet().toArray(new String[]{}));
            Collections.sort(keys);
            for (int i = 0; i < keys.size(); i++) {
                if (i + 1 < keys.size() && hasDependency(missions.get(keys.get(i)), missions.get(keys.get(i + 1)))) {
                    twoInRow(missions.get(keys.get(i)), missions.get(keys.get(i + 1)), View.VISIBLE);
                    i++;
                } else {
                    oneInRow(missions.get(keys.get(i)));
                }
            }
        }
    }

    private void premiumPackage() {
        Map<String, Mission> missions = missionPack.getMissions();
        if(missions.get("xp2prema01").isActive()){
            premiumMember(missions);
        } else {
            nonPremiumMember(missions);
        }
    }

    private void premiumMember(Map<String, Mission> missions){
        twoInRow(missions.get("xp2prema01"), missions.get("xp2prema06"), View.VISIBLE);
        twoInRow(missions.get("xp2prema02"), missions.get("xp2prema07"), View.VISIBLE);
        twoInRow(missions.get("xp2prema03"), missions.get("xp2prema08"), View.VISIBLE);
        twoInRow(missions.get("xp2prema04"), missions.get("xp2prema09"), View.VISIBLE);
        twoInRow(missions.get("xp2prema05"), missions.get("xp2prema10"), View.VISIBLE);
        twoInRow(missions.get("xp3prema01"), missions.get("xp3prema06"), View.INVISIBLE);
        twoInRow(missions.get("xp3prema02"), missions.get("xp3prema07"), View.INVISIBLE);
        twoInRow(missions.get("xp3prema03"), missions.get("xp3prema08"), View.INVISIBLE);
        twoInRow(missions.get("xp3prema04"), missions.get("xp3prema09"), View.INVISIBLE);
        twoInRow(missions.get("xp3prema05"), missions.get("xp3prema10"), View.INVISIBLE);
        oneInRow(missions.get("xp4prema01"));
        oneInRow(missions.get("xp4prema02"));
        oneInRow(missions.get("xp4prema03"));
        oneInRow(missions.get("xp4prema04"));
        oneInRow(missions.get("xp4prema05"));
    }

    private void nonPremiumMember(Map<String, Mission> missions){
        oneInRow(missions.get("xp2prema01"));
        oneInRow(missions.get("xp2prema02"));
        oneInRow(missions.get("xp2prema03"));
        oneInRow(missions.get("xp2prema04"));
        oneInRow(missions.get("xp2prema05"));
        oneInRow(missions.get("xp3prema01"));
        oneInRow(missions.get("xp3prema02"));
        oneInRow(missions.get("xp3prema03"));
        oneInRow(missions.get("xp3prema04"));
        oneInRow(missions.get("xp3prema05"));
        oneInRow(missions.get("xp4prema01"));
        oneInRow(missions.get("xp4prema02"));
        oneInRow(missions.get("xp4prema03"));
        oneInRow(missions.get("xp4prema04"));
        oneInRow(missions.get("xp4prema05"));
    }

    private void aftermath(Map<String, Mission> missions){
        twoInRow(missions.get("xp4ma02"), missions.get("xp4ma09"), View.VISIBLE);
        twoInRow(missions.get("xp4ma01"), missions.get("xp4ma05"), View.VISIBLE);
        twoInRow(missions.get("xp4ma03"), missions.get("xp4ma06"), View.VISIBLE);
        twoInRow(missions.get("xp4ma04"), missions.get("xp4ma07"), View.VISIBLE);
        twoInRow(missions.get("xp4ma08"), missions.get("xp4ma10"), View.VISIBLE);
    }

    private void oneInRow(Mission mission) {
        TableRow tableRow = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.list_item_assignment_stacked, table, false);
        RelativeLayout layout = (RelativeLayout) tableRow.findViewById(R.id.assignment);
        setMission(layout, mission);
        table.addView(tableRow);
    }

    private void twoInRow(Mission mission1, Mission mission2, int visibility) {
        TableRow tableRow = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.list_item_assignment, table, false);
        RelativeLayout leftLayout = (RelativeLayout) tableRow.findViewById(R.id.assignment_left);
        setMission(leftLayout, mission1);
        tableRow.findViewById(R.id.image_arrow).setVisibility(visibility);
        RelativeLayout rightLayout = (RelativeLayout) tableRow.findViewById(R.id.assignment_right);
        setMission(rightLayout, mission2);
        table.addView(tableRow);
    }

    private void setMission(View view, final Mission mission) {
        ImageView image = (ImageView) view.findViewById(R.id.assignment_image);
        ProgressBar progress = (ProgressBar) view.findViewById(R.id.assignment_progress);
        image.setImageResource(assignmentImage(mission.getCode(), mission.isActive()));

        image.setTag(mission.getCode());
        image.setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AssignmentDialog dialog = AssignmentDialog.newInstance(mission);
                        dialog.show(getFragmentManager(), "Assignment Dialog");
                    }

                }
        );
        progress.setProgress(mission.getCompletion());
        progress.setMax(100);
    }

    private boolean hasDependency(Mission a, Mission b) {
        return b.hasDependencies() && b.isDependentOn(a.getCode());
    }

    private int assignmentImage(String code, boolean isActive) {
        return isActive ? AssignmentsMap.assignmentDrawable(code) : R.drawable.assignment_locked;
    }

    @Override
    public void reload() {
    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return null;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }

    @Subscribe
    public void assignmentChange(String message) {
        if (message.equals(AssignmentActivity.ASSIGNMENTS)) {
            missionPack = ((AssignmentActivity) getActivity()).getMissionPack(expansionId);
            showData();
        }
    }
}
