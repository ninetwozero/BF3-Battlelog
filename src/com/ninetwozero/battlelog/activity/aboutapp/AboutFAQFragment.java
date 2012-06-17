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
import android.view.ViewGroup;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.DefaultFragment;

public class AboutFAQFragment extends Fragment implements DefaultFragment {

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_main_faq,
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
