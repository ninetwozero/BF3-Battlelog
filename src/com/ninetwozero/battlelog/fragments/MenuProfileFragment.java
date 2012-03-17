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

import com.ninetwozero.battlelog.AssignmentView;
import com.ninetwozero.battlelog.ForumView;
import com.ninetwozero.battlelog.PlatoonView;
import com.ninetwozero.battlelog.ProfileView;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.SearchView;
import com.ninetwozero.battlelog.UnlockView;
import com.ninetwozero.battlelog.adapters.DashboardPopupPlatoonListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncFetchDataToCompare;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class MenuProfileFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private Map<Integer, Intent> MENU_INTENTS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_dashboard_profile,
                container, false);

        initFragment(view);
        
        return view;

    }
    
    public void initFragment(View view) {
        
        //Set up the intents
        MENU_INTENTS = new HashMap<Integer, Intent>();
        MENU_INTENTS.put(R.id.button_unlocks,
                        new Intent(context, UnlockView.class).putExtra("profile",
                                SessionKeeper.getProfileData()));
        MENU_INTENTS.put(R.id.button_assignments,
                        new Intent(context, AssignmentView.class).putExtra("profile",
                                SessionKeeper.getProfileData()));
        MENU_INTENTS.put(R.id.button_search, new Intent(context, SearchView.class));
        MENU_INTENTS.put(R.id.button_self,
                new Intent(context, ProfileView.class).putExtra("profile",
                        SessionKeeper.getProfileData()));        
        
        MENU_INTENTS.put(R.id.button_settings,
                new Intent(context, ProfileView.class).putExtra("profile",
                        SessionKeeper.getProfileData()));

        //Add the OnClickListeners
        for( int key : MENU_INTENTS.keySet() ) {
            
            view.findViewById(key).setOnClickListener( new OnClickListener() {

                @Override
                public void onClick(View v) {

                    startActivity(MENU_INTENTS.get(v.getId()));
                    
                }} );
        
        }
        
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
