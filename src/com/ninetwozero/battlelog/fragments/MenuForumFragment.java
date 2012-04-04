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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.ninetwozero.battlelog.ForumActivity;
import com.ninetwozero.battlelog.ForumSavedActivity;
import com.ninetwozero.battlelog.ForumSearchActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.ForumData;
import com.ninetwozero.battlelog.datatypes.PersonaData;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;

public class MenuForumFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private Map<Integer, Intent> MENU_INTENTS;
    private SharedPreferences sharedPreferences;

    // Elements
    private RelativeLayout wrapLanguage;
    private TextView textLanguage;
    private ImageView imageLanguage;

    // Let's store the position & persona
    private List<ForumData> forums;
    private PersonaData[] persona;
    private String selectedLocale;
    private String selectedLanguage;
    private int selectedPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get the unlocks
        selectedLocale = sharedPreferences.getString(Constants.SP_BL_FORUM_LOCALE, "en");
        selectedPosition = sharedPreferences.getInt(Constants.SP_BL_FORUM_LOCALE_POSITION, 0);
        selectedLanguage = DataBank.getLanguage(selectedPosition);

        // Let's get that data
        forums = new ArrayList<ForumData>();

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_dashboard_forum,
                container, false);

        initFragment(view);

        return view;

    }

    public void initFragment(View view) {

        // Set up the Persona box
        wrapLanguage = (RelativeLayout) view.findViewById(R.id.wrap_language);
        wrapLanguage.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        generateDialogLanguageList(context, DataBank.getLanguages(),
                                DataBank.getLocales()).show();

                    }

                }

                );
        imageLanguage = (ImageView) wrapLanguage.findViewById(R.id.image_language);
        textLanguage = (TextView) wrapLanguage.findViewById(R.id.text_language);
        textLanguage.setSelected(true);

        // Setup the "persona box"
        setupLanguageBox();

        // Set up the intents
        MENU_INTENTS = new HashMap<Integer, Intent>();
        MENU_INTENTS.put(R.id.button_view, new Intent(context, ForumActivity.class));
        MENU_INTENTS.put(R.id.button_search, new Intent(context, ForumSearchActivity.class));
        MENU_INTENTS.put(R.id.button_saved, new Intent(context, ForumSavedActivity.class));

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

    public Dialog generateDialogLanguageList(final Context context,
            final String[] languages, final String[] locales) {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and the view
        builder.setTitle(R.string.info_forum_lang);

        builder.setSingleChoiceItems(

                languages, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        selectedPosition = item;
                        selectedLocale = locales[selectedPosition];
                        selectedLanguage = languages[selectedPosition];

                        SharedPreferences.Editor spEdit = sharedPreferences.edit();
                        spEdit.putString(Constants.SP_BL_FORUM_LOCALE, selectedLocale);
                        spEdit.putInt(Constants.SP_BL_FORUM_LOCALE_POSITION, selectedPosition);
                        spEdit.commit();

                        setupLanguageBox();

                        dialog.dismiss();

                    }

                }

                );

        // CREATE
        return builder.create();

    }

    public void setupLanguageBox() {

        // Let's see...
        textLanguage.setText(selectedLanguage);
        // imageLanguage.setImageResource(DataBank.getImageForPersona(persona[selectedPosition].getLogo()));

    }

}
