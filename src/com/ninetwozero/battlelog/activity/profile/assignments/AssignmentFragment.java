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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.dialog.AssignmentDialog;
import com.ninetwozero.battlelog.jsonmodel.assignments.Mission;
import com.ninetwozero.battlelog.jsonmodel.assignments.MissionPack;
import com.ninetwozero.battlelog.provider.BusProvider;
import com.ninetwozero.battlelog.util.AssignmentsMap;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

        if (expansionId == 1024) {
            premiumPackage();
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
        image.setImageResource(assignmentImage(mission.getCode()));
        /*if (ass1.isCompleted()) {
            image.setImageResource(ass2.getResourceId());
        } else {
            image.setImageResource(R.drawable.assignment_locked);
        }*/

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
        Log.e("AssignmentFragment", expansionId + "- " + mission.getAddonUnlocks().get(0).getUnlockId());
        progress.setProgress(mission.getCompletion());
        progress.setMax(100);
    }

    private boolean hasDependency(Mission a, Mission b) {
        return b.hasDependencies() && b.isDependentOn(a.getCode());
    }

    private int assignmentImage(String code) {
        return AssignmentsMap.assignmentDrawable(code);
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
