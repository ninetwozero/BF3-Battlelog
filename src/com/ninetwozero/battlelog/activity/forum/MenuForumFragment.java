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

package com.ninetwozero.battlelog.activity.forum;

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

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;

public class MenuForumFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private Map<Integer, Intent> MENU_INTENTS;
    private SharedPreferences mSharedPreferences;

    // Elements
    private RelativeLayout mWrapLanguage;
    private TextView mTextLanguage;
    private ImageView mImageLanguage;

    // Let's store the position & persona
    private String mSelectedLocale;
    private String mSelectedLanguage;
    private int mSelectedPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // Get the unlocks
        mSelectedLocale = mSharedPreferences.getString(Constants.SP_BL_FORUM_LOCALE, "en");
        mSelectedPosition = mSharedPreferences.getInt(Constants.SP_BL_FORUM_LOCALE_POSITION, 0);
        mSelectedLanguage = DataBank.getLanguage(mSelectedPosition);

        // Let's inflate & return the view
        View view = inflater.inflate(R.layout.tab_content_dashboard_forum,
                container, false);

        initFragment(view);

        return view;

    }

    public void initFragment(View view) {

        // Set up the Persona box
        mWrapLanguage = (RelativeLayout) view.findViewById(R.id.wrap_language);
        mWrapLanguage.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        generateDialogLanguageList(mContext, DataBank.getLanguages(),
                                DataBank.getLocales()).show();

                    }

                }

                );
        mImageLanguage = (ImageView) mWrapLanguage.findViewById(R.id.image_language);
        mTextLanguage = (TextView) mWrapLanguage.findViewById(R.id.text_language);
        mTextLanguage.setSelected(true);

        // Setup the "persona box"
        setupLanguageBox();

        // Set up the intents
        MENU_INTENTS = new HashMap<Integer, Intent>();
        MENU_INTENTS.put(R.id.button_view, new Intent(mContext, ForumActivity.class));
        MENU_INTENTS.put(R.id.button_search, new Intent(mContext, ForumSearchActivity.class));
        MENU_INTENTS.put(R.id.button_saved, new Intent(mContext, ForumSavedActivity.class));

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

                        mSelectedPosition = item;
                        mSelectedLocale = locales[mSelectedPosition];
                        mSelectedLanguage = languages[mSelectedPosition];

                        SharedPreferences.Editor spEdit = mSharedPreferences.edit();
                        spEdit.putString(Constants.SP_BL_FORUM_LOCALE, mSelectedLocale);
                        spEdit.putInt(Constants.SP_BL_FORUM_LOCALE_POSITION, mSelectedPosition);
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
        mTextLanguage.setText(mSelectedLanguage);
        mImageLanguage.setImageResource(R.drawable.locale_us);

    }

}
