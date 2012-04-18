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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.SingleWeaponActivity;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WeaponDataWrapper;
import com.ninetwozero.battlelog.datatypes.WeaponStats;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.DrawableResourceList;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class WeaponInformationFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private int viewPagerPosition;

    // Elements
    private ImageView imageItem;
    private TextView textTitle, textDesc, textSpecs;

    // Misc
    private ProfileData profileData;
    private WeaponStats weaponStats;
    private long selectedPersona;
    private SharedPreferences sharedPreferences;
    private HashMap<Long, WeaponDataWrapper> weaponDataWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_weapon_info,
                container, false);

        // Init views
        initFragment(view);

        // Return the view
        return view;

    }

    public void initFragment(View v) {
        
        //Let's setup the fields
        imageItem = (ImageView) v.findViewById(R.id.image_item);
        textTitle = (TextView) v.findViewById(R.id.text_title);
        textDesc = (TextView) v.findViewById(R.id.text_desc);
        textSpecs = (TextView) v.findViewById(R.id.text_specs);
        
        //Let's see
        selectedPersona = ( selectedPersona == 0 ) ? profileData.getPersona(0).getId() : selectedPersona;

    }

    @Override
    public void onResume() {

        super.onResume();
        if( profileData != null ) {
            
            reload();
        
        }

    }

    public int getViewPagerPosition() {

        return viewPagerPosition;

    }

    public void setSelectedPersona(long p) {
        
        selectedPersona = p;
    }
    
    public void setProfileData(ProfileData p){
        
        profileData = p;
        
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
                
                weaponDataWrapper = WebsiteHandler.getWeapon(profileData, weaponStats);
                return true;   

            } catch( Exception ex ) {
                
                ex.printStackTrace();
                return false;
            }
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            
            if( context != null ) {
                
                if( result ) {
                    Log.d(Constants.DEBUG_TAG, "selectedPersona => " + selectedPersona);
                    show(weaponDataWrapper.get(selectedPersona));
                    
                } else {
                    
                    Toast.makeText(context, R.string.general_no_data, Toast.LENGTH_SHORT).show();
                    
                }
                
            }
        }
        
    }
    
    private void show(WeaponDataWrapper w) {
        
        Log.d(Constants.DEBUG_TAG, "w => " + w);
        //No need to pass null
        if( w == null ) { return; }
        
        imageItem.setImageResource( DrawableResourceList.getWeapon(w.getGuid()) );
        textTitle.setText(w.getName());
        textDesc.setText(w.getDescription());
        textSpecs.setText(w.getSpecifications());
        
        //Update the previous
        ((SingleWeaponActivity) context).showData(w);
    }
    
}
