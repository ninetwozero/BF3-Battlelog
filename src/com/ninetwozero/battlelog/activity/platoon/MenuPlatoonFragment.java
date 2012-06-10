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

package com.ninetwozero.battlelog.activity.platoon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import com.ninetwozero.battlelog.activity.profile.settings.ProfileSettingsActivity;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class MenuPlatoonFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private Map<Integer, Intent> MENU_INTENTS;
    private SharedPreferences sharedPreferences;

    // Elements
    private RelativeLayout wrapPlatoon;
    private TextView textPlatoon;
    private ImageView imagePlatoon;

    // Let's store the position & platoon
    private List<PlatoonData> platoonData;
    private long[] platoonId;
    private String[] platoonName;
    private long selectedPlatoon;
    private int selectedPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_dashboard_platoon,
                container, false);

        initFragment(view);

        return view;

    }

    public void initFragment(View view) {

        // Let's set the vars
        selectedPosition = sharedPreferences.getInt(Constants.SP_BL_PLATOON_CURRENT_POS, 0);
        selectedPlatoon = sharedPreferences.getLong(Constants.SP_BL_PLATOON_CURRENT_ID, 0);

        // Set up the Platoon box
        wrapPlatoon = (RelativeLayout) view.findViewById(R.id.wrap_platoon);
        wrapPlatoon.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        generateDialogPlatoonList().show();

                    }

                }

                );
        imagePlatoon = (ImageView) wrapPlatoon.findViewById(R.id.image_platoon);
        textPlatoon = (TextView) wrapPlatoon.findViewById(R.id.text_platoon);
        textPlatoon.setSelected(true);

        // Setup the "platoon box"
        setupPlatoonBox();

        // Set up the intents
        MENU_INTENTS = new HashMap<Integer, Intent>();
        MENU_INTENTS.put(R.id.button_new, new Intent(context, PlatoonCreateActivity.class));
        MENU_INTENTS.put(R.id.button_invites, new Intent(context, ProfileSettingsActivity.class));

        if (platoonData != null && platoonData.size() > 0) {

            MENU_INTENTS.put(
                    R.id.button_self,
                    new Intent(context, PlatoonActivity.class).putExtra("platoon",
                            platoonData.get(selectedPosition)));
            MENU_INTENTS.put(
                    R.id.button_settings,
                    new Intent(context, ProfileSettingsActivity.class).putExtra("platoon",
                            platoonData.get(selectedPosition)));
        }

        // Add the OnClickListeners
        for (int key : MENU_INTENTS.keySet()) {

            view.findViewById(key).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    startActivity(MENU_INTENTS.get(v.getId()));

                }
            });

        }

        // Let's reload!
        reload();

    }

    @Override
    public void reload() {

        new AsyncRefresh().execute(SessionKeeper.getProfileData());

    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return menu;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }

    public Dialog generateDialogPlatoonList() {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and the view
        builder.setTitle(R.string.info_xml_platoon_select);

        // Do we have items to show?
        if (platoonId == null) {

            // Init
            platoonId = new long[platoonData.size()];
            platoonName = new String[platoonData.size()];

            // Iterate
            for (int count = 0, max = platoonData.size(); count < max; count++) {

                platoonId[count] = platoonData.get(count).getId();
                platoonName[count] = platoonData.get(count).getName();

            }

        }

        // Set it up
        builder.setSingleChoiceItems(

                platoonName, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        if (platoonId[item] != selectedPlatoon) {

                            // Update it
                            selectedPlatoon = platoonId[item];

                            // Store selectedPlatoonPos
                            selectedPosition = item;

                            // Load the new!
                            setupPlatoonBox();

                            // Save it
                            SharedPreferences.Editor spEdit = sharedPreferences.edit();
                            spEdit.putLong(Constants.SP_BL_PLATOON_CURRENT_ID, selectedPlatoon);
                            spEdit.putInt(Constants.SP_BL_PLATOON_CURRENT_POS, selectedPosition);
                            spEdit.commit();

                            // Reset these
                            MENU_INTENTS.put(
                                    R.id.button_self,
                                    new Intent(context, PlatoonActivity.class).putExtra("platoon",
                                            platoonData.get(selectedPosition)));
                            MENU_INTENTS.put(
                                    R.id.button_settings,
                                    new Intent(context, ProfileSettingsActivity.class).putExtra(
                                            "platoon",
                                            platoonData.get(selectedPosition)));

                        }

                        dialog.dismiss();

                    }

                }

                );

        // CREATE
        return builder.create();

    }

    public void setupPlatoonBox() {

        // Let's see...
        if (platoonData != null && platoonData.size() > 0 && textPlatoon != null) {

            // Let's validate our digits
            if ((platoonData.size() - 1) < selectedPosition) {

                selectedPosition = platoonData.size() - 1;
                selectedPlatoon = platoonData.get(selectedPosition).getId();

                // Reset these
                MENU_INTENTS.put(
                        R.id.button_self,
                        new Intent(context, PlatoonActivity.class).putExtra("platoon",
                                platoonData.get(selectedPosition)));
                MENU_INTENTS.put(
                        R.id.button_settings,
                        new Intent(context, ProfileSettingsActivity.class).putExtra(
                                "platoon",
                                platoonData.get(selectedPosition)));

            }

            // Set the text
            textPlatoon.setText(platoonData.get(selectedPosition).getName() + "["
                    + platoonData.get(selectedPosition).getTag() + "]");
            imagePlatoon.setImageBitmap(BitmapFactory.decodeFile(PublicUtils.getCachePath(context)
                    + platoonData.get(selectedPosition).getImage()));

        }

    }

    public void setPlatoonData(List<PlatoonData> p) {

        platoonData = p;
        setupPlatoonBox();

    }

    public class AsyncRefresh extends AsyncTask<ProfileData, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ProfileData... arg0) {
            try {
                platoonData = new ProfileClient(arg0[0]).getPlatoons(context);
                return (platoonData != null);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                setupPlatoonBox();
            }
        }
    }

}
