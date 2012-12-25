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

package com.ninetwozero.bf3droid.activity.platoon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.profile.settings.ProfileSettingsActivity;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.dialog.ListDialogFragment;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.SessionKeeper;
import com.ninetwozero.bf3droid.model.SelectedPersona;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class MenuPlatoonFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context mContext;

    // Elements
    private RelativeLayout mWrapPlatoon;
    private TextView mTextPlatoon;
    private ImageView mImagePlatoon;

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

        View view = inflater.inflate(R.layout.tab_content_dashboard_platoon, container, false);

        initFragment(view);

        return view;

    }

    public void initFragment(View view) {
        // Set up the Platoon box
        mWrapPlatoon = (RelativeLayout) view.findViewById(R.id.wrap_platoon);
        mWrapPlatoon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                ListDialogFragment dialog = ListDialogFragment.newInstance(platoonsToMap());
                dialog.show(manager, DIALOG);
            }
        }

        );
        mImagePlatoon = (ImageView) mWrapPlatoon.findViewById(R.id.image_platoon);
        mTextPlatoon = (TextView) mWrapPlatoon.findViewById(R.id.text_platoon);
        mTextPlatoon.setSelected(true);

        // Setup the "platoon box"
        setupPlatoonBox();

        view.findViewById(R.id.button_new).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, PlatoonCreateActivity.class));
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
                if (BF3Droid.getUserPlatoons().size() > 0) {
                    startActivity(new Intent(mContext, PlatoonActivity.class));
                }
            }
        });
        view.findViewById(R.id.button_settings).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BF3Droid.getUserPlatoons().size() > 0) {
                    startActivity(new Intent(mContext, ProfileSettingsActivity.class));
                }
            }
        });

        // Let's reload!
        reload();
    }

    @Override
    public void reload() {
        /*TODO if AsyncRefresh is not finished (rotation and async call restart issue) before user access other part of application
        * this can cause application crash. Example after login, went into assignments, loaded all assignments
        * and browsed them. After it I pressed return button and app crashed.*/
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

    private Map<Long, String> platoonsToMap() {
        Map<Long, String> map = new HashMap<Long, String>();
        for (SimplePlatoon platoon : BF3Droid.getUserPlatoons()) {
            map.put(platoon.getPlatoonId(), platoon.getName()+ " ["+platoon.getPlatform()+"]");
        }
        return map;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void personaChanged(SelectedPersona selectedPersona) {
        updateSharedPreference(selectedPersona.getPersonaId());
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

    private int indexOfPlatoon(long platoonId) {
        for (int i = 0; i < BF3Droid.getUserPlatoons().size(); i++) {
            if (BF3Droid.getUserPlatoons().get(i).getPlatoonId() == platoonId) {
                return i;
            }
        }
        Log.w(MenuPlatoonFragment.class.getSimpleName(), "Failed to find index of the platoon!");
        return 0;
    }

    public void setupPlatoonBox() {
        if (BF3Droid.getUserPlatoons().size() > 0
                && mTextPlatoon != null) {

            mTextPlatoon.setText(BF3Droid.selectedUserPlatoon().getName()+ " ["+ BF3Droid.selectedUserPlatoon().getPlatform() + "]");
           /* mImagePlatoon.setImageBitmap(BitmapFactory.decodeFile(PublicUtils
                    .getCachePath(mContext)
                    + mPlatoonData.get(mSelectedPosition).getImage()));*/
        }
    }

    /*public void setPlatoonData(List<PlatoonData> p) {
        mPlatoonData = p;
        setupPlatoonBox();

    }*/

    public class AsyncRefresh extends AsyncTask<ProfileData, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ProfileData... arg0) {
            /*try {
                mPlatoonData = new ProfileClient(arg0[0]).getPlatoons(mContext);
                return (mPlatoonData != null);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }*/
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                setupPlatoonBox();
            }
        }
    }
}
