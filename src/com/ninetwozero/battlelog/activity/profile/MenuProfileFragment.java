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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.ninetwozero.battlelog.datatype.PersonaData;
import com.ninetwozero.battlelog.dialog.ListDialogFragment;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DataBank;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.model.SelectedPersona;
import com.ninetwozero.battlelog.provider.BusProvider;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_ID;
import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_POS;

public class MenuProfileFragment extends Fragment {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SharedPreferences mSharedPreferences;

    private RelativeLayout mWrapPersona;
    private TextView mTextPersona;
    private ImageView mImagePersona;

    private PersonaData[] mPersona;
    private int mSelectedPosition;
    private final String DIALOG = "dialog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        mContext = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mLayoutInflater = inflater;

        View view = mLayoutInflater.inflate(R.layout.tab_content_dashboard_profile, container, false);

        initFragment(view);
        return view;
    }

    public void initFragment(View view) {

        dataFromSharedPreferences();

        mWrapPersona = (RelativeLayout) view.findViewById(R.id.wrap_persona);
        mWrapPersona.setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    ListDialogFragment dialog = ListDialogFragment.newInstance(personasToMap());
                    dialog.show(manager, DIALOG);
                }
            }
        );
        mImagePersona = (ImageView) mWrapPersona.findViewById(R.id.image_persona);
        mTextPersona = (TextView) mWrapPersona.findViewById(R.id.text_persona);
        mTextPersona.setSelected(true);

        setupActiveSoldierContent();

        view.findViewById(R.id.button_unlocks).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,UnlockActivity.class)
                        .putExtra("profile", SessionKeeper.getProfileData()));
            }
        });
        view.findViewById(R.id.button_weapon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, WeaponListActivity.class)
                        .putExtra("profile", SessionKeeper.getProfileData()));
            }
        });
        view.findViewById(R.id.button_assignments).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AssignmentActivity.class)
                        .putExtra("profile", SessionKeeper.getProfileData()));
            }
        });
        view.findViewById(R.id.button_self).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, ProfileActivity.class)
                        .putExtra("profile", SessionKeeper.getProfileData()));
            }
        });
        view.findViewById(R.id.button_settings).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, ProfileSettingsActivity.class));
            }
        });
    }

    private void dataFromSharedPreferences() {
        mPersona = SessionKeeper.getProfileData().getPersonaArray();
        mSelectedPosition = mSharedPreferences.getInt(
                Constants.SP_BL_PERSONA_CURRENT_POS, 0);
    }

    private Map<Long, String> personasToMap() {
        Map<Long, String> map = new HashMap<Long, String>();
        for (PersonaData pd : mPersona) {
            map.put(pd.getId(), pd.getName() + " " + pd.resolvePlatformId());
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
        dataFromSharedPreferences();
        setupActiveSoldierContent();
    }

    public void setupActiveSoldierContent() {
        mTextPersona.setText(getPersonaNameAndPlatform());
        mImagePersona.setImageResource(DataBank
                .getImageForPersona(mPersona[mSelectedPosition].getLogo()));
    }

    private String getPersonaNameAndPlatform() {
        return mPersona[mSelectedPosition].getName()
                + mPersona[mSelectedPosition].resolvePlatformId();
    }

    private void updateSharedPreference(long personaId) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity()
                        .getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(SP_BL_PERSONA_CURRENT_ID, personaId);
        editor.putInt(SP_BL_PERSONA_CURRENT_POS, indexOfPersona(personaId));
        editor.commit();
    }

    private int indexOfPersona(long platoonId){
        for(int i = 0; i <  mPersona.length; i++){
            if(mPersona[i].getId() == platoonId){
                return i;
            }
        }
        Log.w(MenuProfileFragment.class.getSimpleName(), "Failed to find index of the platoon!");
        return 0;
    }
}
