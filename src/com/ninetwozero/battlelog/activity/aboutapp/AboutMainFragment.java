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

package com.ninetwozero.battlelog.activity.aboutapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.misc.DataBank;

public class AboutMainFragment extends Fragment implements DefaultFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes

        // Let's inflate & return the view
        View view = inflater.inflate(R.layout.tab_content_main_about,
                container, false);

        // Let's try this
        initFragment(view);

        // Return
        return view;

    }

    @Override
    public void initFragment(View view) {

        // Prevent bug
        setUserVisibleHint(true);

        final OnClickListener onClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(DataBank.getContactIntent(v.getId()));

            }
        };

        for (int key : DataBank.getContactIntents().keySet()) {

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

}
