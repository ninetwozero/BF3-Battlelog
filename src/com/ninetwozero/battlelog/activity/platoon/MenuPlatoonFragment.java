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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.profile.settings.ProfileSettingsActivity;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.dialog.ListDialogFragment;
import com.ninetwozero.battlelog.dialog.OnCloseListDialogListener;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.SessionKeeper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuPlatoonFragment extends Fragment implements DefaultFragment, OnCloseListDialogListener {

    // Attributes
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    // Elements
    private RelativeLayout mWrapPlatoon;
    private TextView mTextPlatoon;
    private ImageView mImagePlatoon;

    // Let's store the position & platoon
    private List<PlatoonData> mPlatoonData;
    private long[] mPlatoonId;
    private String[] mPlatoonName;
    private long mSelectedPlatoon;
    private int mSelectedPosition;
    private final String DIALOG = "dialog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        View view = inflater.inflate(R.layout.tab_content_dashboard_platoon,
                container, false);

        initFragment(view);

        return view;

    }

    public void initFragment(View view) {
        getPlatoonPreferences();

        // Set up the Platoon box
        mWrapPlatoon = (RelativeLayout) view.findViewById(R.id.wrap_platoon);
        mWrapPlatoon.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentManager manager = getFragmentManager();
                        ListDialogFragment dialog = ListDialogFragment.newInstance(
                                platoonsToMap(), getTag());
                        dialog.show(manager, DIALOG);
                    }

                }

        );
        mImagePlatoon = (ImageView) mWrapPlatoon
                .findViewById(R.id.image_platoon);
        mTextPlatoon = (TextView) mWrapPlatoon.findViewById(R.id.text_platoon);
        mTextPlatoon.setSelected(true);

        // Setup the "platoon box"
        setupPlatoonBox();

        view.findViewById(R.id.button_new).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,PlatoonCreateActivity.class));
            }
        });
        view.findViewById(R.id.button_invites).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, ProfileSettingsActivity.class));
            }
        });
        view.findViewById(R.id.button_self).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlatoonData != null && !mPlatoonData.isEmpty()){
                    startActivity(new Intent(mContext,
                            PlatoonActivity.class).putExtra("platoon",
                            mPlatoonData.get(mSelectedPosition)));
                }
            }
        });
        view.findViewById(R.id.button_settings).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlatoonData != null && !mPlatoonData.isEmpty()){
                    startActivity(new Intent(mContext,
                            ProfileSettingsActivity.class).putExtra("platoon",
                            mPlatoonData.get(mSelectedPosition)));
                }
            }
        });

        // Let's reload!
        reload();
    }

    private void getPlatoonPreferences() {
        mSelectedPosition = mSharedPreferences.getInt(
                Constants.SP_BL_PLATOON_CURRENT_POS, 0);
        mSelectedPlatoon = mSharedPreferences.getLong(
                Constants.SP_BL_PLATOON_CURRENT_ID, 0);
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

    private Map<Long, String> platoonsToMap(){
        Map<Long, String> map = new HashMap<Long, String>();
        for(PlatoonData pd : mPlatoonData){
            map.put(pd.getId(), pd.getName());
        }
        return map;
    }

    @Override
    public void onDialogListSelection(long id) {
        updateSharedPreference(id);
        getPlatoonPreferences();
        setupPlatoonBox();
    }

    private void updateSharedPreference(long platoonId) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity()
                        .getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(Constants.SP_BL_PLATOON_CURRENT_ID, platoonId);
        editor.putInt(Constants.SP_BL_PLATOON_CURRENT_POS, indexOfPlatoon(platoonId));
        editor.commit();
    }

    private int indexOfPlatoon(long platoonId){
        for(int i = 0; i <  mPlatoonData.size(); i++){
            if(mPlatoonData.get(i).getId() == platoonId){
                return i;
            }
        }
        Log.w(MenuPlatoonFragment.class.getSimpleName(), "Failed to find index of the platoon!");
        return 0;
    }

    public void setupPlatoonBox() {
        if (mPlatoonData != null && !mPlatoonData.isEmpty()
                && mTextPlatoon != null) {

            getPlatoonPreferences();

            mTextPlatoon.setText(mPlatoonData.get(mSelectedPosition).getName()
                    + "[" + mPlatoonData.get(mSelectedPosition).getTag() + "]");
            mImagePlatoon.setImageBitmap(BitmapFactory.decodeFile(PublicUtils
                    .getCachePath(mContext)
                    + mPlatoonData.get(mSelectedPosition).getImage()));
        }
    }

    public void setPlatoonData(List<PlatoonData> p) {
        mPlatoonData = p;
        setupPlatoonBox();

    }

    public class AsyncRefresh extends AsyncTask<ProfileData, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ProfileData... arg0) {
            try {
                mPlatoonData = new ProfileClient(arg0[0]).getPlatoons(mContext);
                return (mPlatoonData != null);
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
