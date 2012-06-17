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

package com.ninetwozero.battlelog.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.forum.ForumActivity;
import com.ninetwozero.battlelog.activity.platoon.PlatoonActivity;
import com.ninetwozero.battlelog.activity.profile.assignments.AssignmentActivity;
import com.ninetwozero.battlelog.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.battlelog.activity.profile.unlocks.UnlockActivity;
import com.ninetwozero.battlelog.adapter.DashboardPopupPlatoonListAdapter;
import com.ninetwozero.battlelog.asynctask.AsyncFetchDataToCompare;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class MenuFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<PlatoonData> mPlatoonArray; /* TODO */
    private Map<Integer, Intent> MENU_INTENTS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.tab_content_dashboard_menu,
                container, false);

        initFragment(view);

        return view;

    }

    public void initFragment(View view) {

        // Set up the intents
        MENU_INTENTS = new HashMap<Integer, Intent>();
        MENU_INTENTS.put(R.id.button_unlocks,
                new Intent(mContext, UnlockActivity.class).putExtra("profile",
                        SessionKeeper.getProfileData()));
        MENU_INTENTS.put(R.id.button_assignments,
                new Intent(mContext, AssignmentActivity.class).putExtra("profile",
                        SessionKeeper.getProfileData()));
        MENU_INTENTS.put(R.id.button_search, new Intent(mContext, SearchActivity.class));
        MENU_INTENTS.put(R.id.button_self,
                new Intent(mContext, ProfileActivity.class).putExtra("profile",
                        SessionKeeper.getProfileData()));
        MENU_INTENTS.put(R.id.button_forum, new Intent(mContext, ForumActivity.class));

        // Add the OnClickListeners
        final OnClickListener onClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(MENU_INTENTS.get(v.getId()));

            }
        };
        
        for (int key : MENU_INTENTS.keySet()) {

            view.findViewById(key).setOnClickListener(onClickListener);

        }

    }

    public final void onMenuClick(View v) {

        if (v.getId() == R.id.button_platoons) {

            generatePopupPlatoonList(mContext, getView()).show();

        } else if (v.getId() == R.id.button_compare) {

            generateDialogCompare(mContext, getView()).show();

        } else {

            Toast.makeText(mContext, R.string.msg_unimplemented,
                    Toast.LENGTH_SHORT).show();

        }

    }

    public Dialog generateDialogCompare(final Context context, final View view) {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_compare,
                (ViewGroup) view.findViewById(R.id.dialog_root));

        // Set the title and the view
        builder.setTitle(R.string.label_compare_bs);
        builder.setView(layout);

        // Grab the fields
        final EditText fieldUsername = (EditText) layout
                .findViewById(R.id.field_username);

        // Dialog options
        builder.setNegativeButton(

                android.R.string.cancel,

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.dismiss();

                    }

                }

                );

        builder.setPositiveButton(

                android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        String username = fieldUsername.getText().toString();
                        if ("".equals(username)) {

                            Toast.makeText(context, R.string.general_empty_user,
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            new AsyncFetchDataToCompare(context, SessionKeeper
                                    .getProfileData()).execute(username);

                        }

                    }

                }

                );

        // Padding fix
        AlertDialog theDialog = builder.create();
        theDialog.setView(layout, 0, 0, 0, 0);
        return theDialog;

    }

    public Dialog generatePopupPlatoonList(final Context context,
            final View view) {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_dashboard_platoon,
                (ViewGroup) view.findViewById(R.id.dialog_root));

        // Set the title
        builder.setTitle(R.string.info_xml_platoon_select);

        // Grab the fields
        ListView listView = (ListView) layout.findViewById(R.id.list_platoons);
        listView.setAdapter(new DashboardPopupPlatoonListAdapter(context,
                mPlatoonArray, mLayoutInflater));
        listView.setOnItemClickListener(

                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                            long arg3) {

                        startActivity(new Intent(context, PlatoonActivity.class).putExtra(
                                "platoon", ((PlatoonData) arg1.getTag())));

                    }

                }

                );

        // Dialog options
        builder.setPositiveButton(

                android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }

                }

                );

        // Padding fix
        AlertDialog theDialog = builder.create();
        theDialog.setView(layout, 0, 0, 0, 0);

        return theDialog;

    }

    @Override
    public void reload() {
    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return menu;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }

}
