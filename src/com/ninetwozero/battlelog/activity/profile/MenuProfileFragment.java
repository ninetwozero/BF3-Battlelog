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

package com.ninetwozero.battlelog.activity.profile;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.profile.assignments.AssignmentActivity;
import com.ninetwozero.battlelog.activity.profile.settings.ProfileSettingsActivity;
import com.ninetwozero.battlelog.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.battlelog.activity.profile.unlocks.UnlockActivity;
import com.ninetwozero.battlelog.activity.profile.weapon.WeaponListActivity;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.PersonaData;
import com.ninetwozero.battlelog.dialog.OnCloseListDialogListener;
import com.ninetwozero.battlelog.dialog.ListDialogFragment;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class MenuProfileFragment extends Fragment implements DefaultFragment, OnCloseListDialogListener {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private Map<Integer, Intent> MENU_INTENTS;
    private SharedPreferences sharedPreferences;

    // Elements
    private RelativeLayout wrapPersona;
    private TextView textPersona;
    private ImageView imagePersona;

    // Let's store the position & persona
    private PersonaData[] persona;
    private int selectedPosition;
    private final String DIALOG = "dialog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_dashboard_profile,
                container, false);

        initFragment(view);

        return view;

    }

    public void initFragment(View view) {

        // Let's set the vars
        dataFromSharedPreferences();

        // Set up the Persona box
        wrapPersona = (RelativeLayout) view.findViewById(R.id.wrap_persona);
        wrapPersona.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentManager manager = getFragmentManager();
                        ListDialogFragment dialog = ListDialogFragment.newInstance(persona, getTag());
                        dialog.show(manager, DIALOG);
                    }

                }

                );
        imagePersona = (ImageView) wrapPersona.findViewById(R.id.image_persona);
        textPersona = (TextView) wrapPersona.findViewById(R.id.text_persona);
        textPersona.setSelected(true);

        // Setup the "persona box"
        setupActiveSoldierContent();

        // Set up the intents
        MENU_INTENTS = menuOptions();

        // Add the OnClickListeners
        for (int key : MENU_INTENTS.keySet()) {
            view.findViewById(key).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(MENU_INTENTS.get(v.getId()));

                }
            });
        }
    }

    private void dataFromSharedPreferences() {
        persona = SessionKeeper.getProfileData().getPersonaArray();
        selectedPosition = sharedPreferences.getInt(Constants.SP_BL_PERSONA_CURRENT_POS, 0);
    }

    private Map<Integer, Intent> menuOptions(){
        return new HashMap<Integer, Intent>(){{
            put(R.id.button_unlocks,
                    new Intent(context, UnlockActivity.class).putExtra("profile",
                            SessionKeeper.getProfileData()));
            put(R.id.button_weapon,
                    new Intent(context, WeaponListActivity.class).putExtra("profile",
                            SessionKeeper.getProfileData()));
            put(R.id.button_assignments,
                    new Intent(context, AssignmentActivity.class).putExtra("profile",
                            SessionKeeper.getProfileData()));
            put(R.id.button_self,
                    new Intent(context, ProfileActivity.class).putExtra("profile",
                            SessionKeeper.getProfileData()));
            put(R.id.button_settings,
                    new Intent(context, ProfileSettingsActivity.class));
        }};

    }

    @Override
    public void onDialogListSelection() {
        dataFromSharedPreferences();
        setupActiveSoldierContent();
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

    public void setupActiveSoldierContent() {
        textPersona.setText(getPersonaNameAndPlatform());
        imagePersona.setImageResource(DataBank.getImageForPersona(persona[selectedPosition].getLogo()));
    }

    private String getPersonaNameAndPlatform() {
        return persona[selectedPosition].getName()+persona[selectedPosition].resolvePlatformId();
    }
}
