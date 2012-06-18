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
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mViewPagerPosition;

    // Elements
    private ImageView mImageItem;
    private TextView mTextTitle;
    private TextView mTextDesc;
    private TextView mTextAuto;
    private TextView mTextBurst;
    private TextView mTextSingle;
    private TextView mTextAmmo;
    private TextView mTextRange;
    private TextView mTextRateOfFire;

    // Misc
    private ProfileData mProfileData;
    private WeaponInfo mWeaponInfo;
    private WeaponStats mWeaponStats;
    private long mSelectedPersona;
    private Map<Long, WeaponDataWrapper> mWeaponDataWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.tab_content_weapon_info,
                container, false);

        // Init views
        initFragment(view);

        // Return the view
        return view;

    }

    public void initFragment(View v) {

        // Let's setup the fields
        mImageItem = (ImageView) v.findViewById(R.id.image_item);
        mTextTitle = (TextView) v.findViewById(R.id.text_title);
        mTextDesc = (TextView) v.findViewById(R.id.text_desc);
        mTextAuto = (TextView) v.findViewById(R.id.text_rate_full);
        mTextBurst = (TextView) v.findViewById(R.id.text_rate_burst);
        mTextSingle = (TextView) v.findViewById(R.id.text_rate_single);
        mTextAmmo = (TextView) v.findViewById(R.id.text_ammo);
        mTextRange = (TextView) v.findViewById(R.id.text_range);
        mTextRateOfFire = (TextView) v.findViewById(R.id.text_rate_num);

        // Let's see
        mSelectedPersona = (mSelectedPersona == 0) ? mProfileData.getPersona(0).getId()
                : mSelectedPersona;

    }

    @Override
    public void onResume() {

        super.onResume();
        if (mProfileData != null) {

            reload();

        }

    }

    public int getViewPagerPosition() {

        return mViewPagerPosition;

    }

    public void setSelectedPersona(long p) {

        mSelectedPersona = p;
    }

    public void setProfileData(ProfileData p) {

        mProfileData = p;

    }

    public void setWeaponInfo(WeaponInfo w) {

        mWeaponInfo = w;

    }

    public void setWeaponStats(WeaponStats w) {

        mWeaponStats = w;
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

                mWeaponDataWrapper = new ProfileClient(mProfileData).getWeapon(mWeaponInfo,
                        mWeaponStats);
                return true;

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (mContext != null) {

                if (result) {

                    show(mWeaponDataWrapper.get(mSelectedPersona));

                } else {

                    Toast.makeText(mContext, R.string.general_no_data, Toast.LENGTH_SHORT).show();

                }

            }
        }

    }

    private void show(WeaponDataWrapper w) {

        // No need to pass null
        if (w == null || w.getData() == null) {
            return;
        }

        mImageItem.setImageResource(DrawableResourceList.getWeapon(w.getData().getIdentifier()));
        mTextTitle.setText(w.getData().getName());
        mTextDesc.setText(StringResourceList.getWeaponDescription(w.getData().getIdentifier()));

        // Add fields for the text, and set the data
        mTextAuto.setText(w.getData().isAuto() ? R.string.general_yes : R.string.general_no);
        mTextBurst.setText(w.getData().isBurst() ? R.string.general_yes : R.string.general_no);
        mTextSingle.setText(w.getData().isSingle() ? R.string.general_yes : R.string.general_no);
        mTextAmmo.setText(w.getData().getAmmo());
        mTextRange.setText(w.getData().getRangeTitle());
        mTextRateOfFire.setText(String.valueOf(w.getData().getRateOfFire()));

        // Update the previous
        ((SingleWeaponActivity) mContext).showData(w);
    }

}
