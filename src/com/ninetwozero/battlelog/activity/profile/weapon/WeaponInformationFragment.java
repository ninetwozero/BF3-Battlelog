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

package com.ninetwozero.battlelog.activity.profile.weapon;

import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.WeaponDataWrapper;
import com.ninetwozero.battlelog.datatype.WeaponInfo;
import com.ninetwozero.battlelog.datatype.WeaponStats;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.misc.DrawableResourceList;
import com.ninetwozero.battlelog.misc.StringResourceList;

public class WeaponInformationFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private int viewPagerPosition;

    // Elements
    private ImageView imageItem;
    private TextView textTitle, textDesc, textAuto, textBurst, textSingle, textAmmo, textRange,
            textRateOfFire;

    // Misc
    private ProfileData profileData;
    private WeaponInfo weaponInfo;
    private WeaponStats weaponStats;
    private long selectedPersona;
    private Map<Long, WeaponDataWrapper> weaponDataWrapper;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_weapon_info,
                container, false);

        // Init views
        initFragment(view);

        // Return the view
        return view;

    }

    public void initFragment(View v) {

        // Let's setup the fields
        imageItem = (ImageView) v.findViewById(R.id.image_item);
        textTitle = (TextView) v.findViewById(R.id.text_title);
        textDesc = (TextView) v.findViewById(R.id.text_desc);
        textAuto = (TextView) v.findViewById(R.id.text_rate_full);
        textBurst = (TextView) v.findViewById(R.id.text_rate_burst);
        textSingle = (TextView) v.findViewById(R.id.text_rate_single);
        textAmmo = (TextView) v.findViewById(R.id.text_ammo);
        textRange = (TextView) v.findViewById(R.id.text_range);
        textRateOfFire = (TextView) v.findViewById(R.id.text_rate_num);

        // Let's see
        selectedPersona = (selectedPersona == 0) ? profileData.getPersona(0).getId()
                : selectedPersona;

    }

    @Override
    public void onResume() {

        super.onResume();
        if (profileData != null) {

            reload();

        }

    }

    public int getViewPagerPosition() {

        return viewPagerPosition;

    }

    public void setSelectedPersona(long p) {

        selectedPersona = p;
    }

    public void setProfileData(ProfileData p) {

        profileData = p;

    }

    public void setWeaponInfo(WeaponInfo w) {

        weaponInfo = w;

    }

    public void setWeaponStats(WeaponStats w) {

        weaponStats = w;
    }

    @Override
    public void reload() {

        new AsyncRefresh().execute();

    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return null;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }

    private class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... arg) {

            try {

                weaponDataWrapper = new ProfileClient(profileData).getWeapon(weaponInfo,
                        weaponStats);
                return true;

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (context != null) {

                if (result) {

                    show(weaponDataWrapper.get(selectedPersona));

                } else {

                    Toast.makeText(context, R.string.general_no_data, Toast.LENGTH_SHORT).show();

                }

            }
        }

    }

    private void show(WeaponDataWrapper w) {

        // No need to pass null
        if (w == null || w.getData() == null) {
            return;
        }

        imageItem.setImageResource(DrawableResourceList.getWeapon(w.getData().getIdentifier()));
        textTitle.setText(w.getData().getName());
        textDesc.setText(StringResourceList.getWeaponDescription(w.getData().getIdentifier()));

        // Add fields for the text, and set the data
        textAuto.setText(w.getData().isAuto() ? R.string.general_yes : R.string.general_no);
        textBurst.setText(w.getData().isBurst() ? R.string.general_yes : R.string.general_no);
        textSingle.setText(w.getData().isSingle() ? R.string.general_yes : R.string.general_no);
        textAmmo.setText(w.getData().getAmmo());
        textRange.setText(w.getData().getRangeTitle());
        textRateOfFire.setText(String.valueOf(w.getData().getRateOfFire()));

        // Update the previous
        ((SingleWeaponActivity) context).showData(w);
    }

}
