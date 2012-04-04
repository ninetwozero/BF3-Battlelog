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
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ninetwozero.battlelog.AssignmentActivity;
import com.ninetwozero.battlelog.ProfileActivity;
import com.ninetwozero.battlelog.ProfileSettingsActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.SearchActivity;
import com.ninetwozero.battlelog.UnlockActivity;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.PersonaData;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class MenuProfileFragment extends Fragment implements DefaultFragment {

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
    private long[] personaId;
    private String[] personaName;
    private long selectedPersona;
    private int selectedPosition;

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
        persona = SessionKeeper.getProfileData().getPersonaArray();
        selectedPersona = sharedPreferences.getLong(Constants.SP_BL_PERSONA_CURRENT_ID, 0);
        selectedPosition = sharedPreferences.getInt(Constants.SP_BL_PERSONA_CURRENT_POS, 0);

        // Set up the Persona box
        wrapPersona = (RelativeLayout) view.findViewById(R.id.wrap_persona);
        wrapPersona.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        generateDialogPersonaList().show();

                    }

                }

                );
        imagePersona = (ImageView) wrapPersona.findViewById(R.id.image_persona);
        textPersona = (TextView) wrapPersona.findViewById(R.id.text_persona);
        textPersona.setSelected(true);

        // Setup the "persona box"
        setupPersonaBox();

        // Set up the intents
        MENU_INTENTS = new HashMap<Integer, Intent>();
        MENU_INTENTS.put(R.id.button_unlocks,
                new Intent(context, UnlockActivity.class).putExtra("profile",
                        SessionKeeper.getProfileData()));
        MENU_INTENTS.put(R.id.button_assignments,
                new Intent(context, AssignmentActivity.class).putExtra("profile",
                        SessionKeeper.getProfileData()));
        MENU_INTENTS.put(R.id.button_search, new Intent(context, SearchActivity.class));
        MENU_INTENTS.put(R.id.button_self,
                new Intent(context, ProfileActivity.class).putExtra("profile",
                        SessionKeeper.getProfileData()));
        MENU_INTENTS.put(R.id.button_settings,
                new Intent(context, ProfileSettingsActivity.class));

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

    public Dialog generateDialogPersonaList() {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and the view
        builder.setTitle(R.string.info_dialog_soldierselect);

        // Do we have items to show?
        if (personaId == null) {

            // Init
            personaId = new long[persona.length];
            personaName = new String[persona.length];

            // Iterate
            for (int count = 0, max = persona.length; count < max; count++) {

                personaId[count] = persona[count].getId();
                personaName[count] = persona[count].getName() + "["
                        + DataBank.resolvePlatformId(persona[count].getPlatformId()) + "]";

            }

        }

        // Set it up
        builder.setSingleChoiceItems(

                personaName, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        if (personaId[item] != selectedPersona) {

                            // Update it
                            selectedPersona = personaId[item];

                            // Store selectedPersonaPos
                            selectedPosition = item;

                            // Load the new!
                            setupPersonaBox();

                            // Save it
                            SharedPreferences.Editor spEdit = sharedPreferences.edit();
                            spEdit.putLong(Constants.SP_BL_PERSONA_CURRENT_ID, selectedPersona);
                            spEdit.putInt(Constants.SP_BL_PERSONA_CURRENT_POS, selectedPosition);
                            spEdit.commit();
                        }

                        dialog.dismiss();

                    }

                }

                );

        // CREATE
        return builder.create();

    }

    public void setupPersonaBox() {

        // Let's see...
        textPersona.setText(persona[selectedPosition].getName());
        imagePersona.setImageResource(DataBank.getImageForPersona(persona[selectedPosition]
                .getLogo()));

    }

}
