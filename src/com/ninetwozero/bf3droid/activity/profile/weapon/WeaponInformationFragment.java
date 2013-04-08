/*
    This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.activity.profile.weapon;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.*;
import com.ninetwozero.bf3droid.http.ProfileClient;
import com.ninetwozero.bf3droid.misc.DrawableResourceList;
import com.ninetwozero.bf3droid.misc.StringResourceList;

import java.util.Map;

public class WeaponInformationFragment extends Fragment implements DefaultFragment {

    private Context context;
    private LayoutInflater layoutInflater;

    // Elements
    private ImageView weaponImage;
    private TextView title;
    private TextView description;
    private TextView autoFire;
    private TextView burstFire;
    private TextView singleShotFire;
    private TextView ammunition;
    private TextView range;
    private TextView rateOfFire;

    private WeaponInfo weaponInfo;
    private WeaponStats weaponStats;
    private Map<Long, WeaponDataWrapper> mWeaponDataWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        layoutInflater = inflater;

        View view = layoutInflater.inflate(R.layout.tab_content_weapon_info, container, false);

        initFragment(view);
        return view;
    }

    public void initFragment(View v) {
        weaponImage = (ImageView) v.findViewById(R.id.image_item);
        title = (TextView) v.findViewById(R.id.text_title);
        description = (TextView) v.findViewById(R.id.text_desc);
        autoFire = (TextView) v.findViewById(R.id.text_rate_full);
        burstFire = (TextView) v.findViewById(R.id.text_rate_burst);
        singleShotFire = (TextView) v.findViewById(R.id.text_rate_single);
        ammunition = (TextView) v.findViewById(R.id.text_ammo);
        range = (TextView) v.findViewById(R.id.text_range);
        rateOfFire = (TextView) v.findViewById(R.id.text_rate_num);
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
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
                mWeaponDataWrapper = new ProfileClient().getWeapon(weaponInfo, weaponStats, getArguments().getString("user"));
                return true;
            } catch (Exception ex) {
                Log.d("WeaponInformationFragment", ex.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (context != null) {
                if (result) {
                    show(mWeaponDataWrapper.get(selectedPersonaId()));
                } else {
                    Toast.makeText(context, R.string.general_no_data, Toast.LENGTH_SHORT).show();
                }
            }
        }

        private long selectedPersonaId() {
            return BF3Droid.getUser().selectedPersona().getPersonaId();
        }
    }

    private void show(WeaponDataWrapper w) {
        if (w == null || w.getData() == null) {
            return;
        }

        weaponImage.setImageResource(DrawableResourceList.getWeapon(w.getData().getIdentifier()));
        title.setText(w.getData().getName());
        description.setText(StringResourceList.getWeaponDescription(w.getData().getIdentifier()));

        autoFire.setText(w.getData().isAuto() ? R.string.general_yes : R.string.general_no);
        burstFire.setText(w.getData().isBurst() ? R.string.general_yes : R.string.general_no);
        singleShotFire.setText(w.getData().isSingle() ? R.string.general_yes : R.string.general_no);
        ammunition.setText(w.getData().getAmmo());
        range.setText(w.getData().getRangeTitle());
        rateOfFire.setText(String.valueOf(w.getData().getRateOfFire()));

        ((SingleWeaponActivity) context).showData(w);
    }
}
