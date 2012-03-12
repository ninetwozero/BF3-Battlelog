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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.AssignmentView;
import com.ninetwozero.battlelog.ForumView;
import com.ninetwozero.battlelog.PlatoonView;
import com.ninetwozero.battlelog.ProfileView;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.SearchView;
import com.ninetwozero.battlelog.UnlockView;
import com.ninetwozero.battlelog.adapters.DashboardPopupPlatoonListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncFetchDataToCompare;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class MenuFragment extends Fragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private List<PlatoonData> platoonArray; /* TODO */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_dashboard_menu,
                container, false);

        return view;

    }

    public final void onMenuClick(View v) {

        // Get it
        final Map<Integer, Intent> intents = new HashMap<Integer, Intent>() {

            {
                put(R.id.button_unlocks,
                        new Intent(context, UnlockView.class).putExtra("profile",
                                SessionKeeper.getProfileData()));
                put(R.id.button_assignments,
                        new Intent(context, AssignmentView.class).putExtra("profile",
                                SessionKeeper.getProfileData()));
                put(R.id.button_search, new Intent(context, SearchView.class));
                put(R.id.button_self,
                        new Intent(context, ProfileView.class).putExtra("profile",
                                SessionKeeper.getProfileData()));
                put(R.id.button_forum, new Intent(context, ForumView.class));
            }
        };

        // Is it in the HashMap?
        if (intents.containsKey(v.getId())) {
            startActivity(intents.get(v.getId()));
        } else if (v.getId() == R.id.button_platoons) {

            generatePopupPlatoonList(context, getView()).show();
            return;

        } else if (v.getId() == R.id.button_compare) {

            generateDialogCompare(context, getView()).show();
            return;

        } else {

            Toast.makeText(context, R.string.msg_unimplemented,
                    Toast.LENGTH_SHORT).show();
            return;

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

                        if (!fieldUsername.getText().toString().equals("")) {

                            new AsyncFetchDataToCompare(context, SessionKeeper
                                    .getProfileData()).execute(fieldUsername.getText()
                                    .toString());

                        } else {

                            Toast.makeText(context, R.string.general_empty_user,
                                    Toast.LENGTH_SHORT).show();

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
                platoonArray, layoutInflater));
        listView.setOnItemClickListener(

                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                            long arg3) {

                        startActivity(new Intent(context, PlatoonView.class).putExtra(
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

}
