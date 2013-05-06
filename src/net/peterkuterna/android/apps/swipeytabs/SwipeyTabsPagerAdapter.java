/*
 * Copyright 2011 Peter Kuterna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Modfied by Karl Lindmark for BF3 BF3Droid */

package net.peterkuterna.android.apps.swipeytabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ninetwozero.bf3droid.R;

import java.util.List;

public class SwipeyTabsPagerAdapter extends FragmentPagerAdapter implements SwipeyTabsAdapter {

    private String[] titles;
    private List<Fragment> fragments;
    private LayoutInflater layoutInflater;
    private ViewPager viewPager;

    public SwipeyTabsPagerAdapter(FragmentManager fm, String[] t, List<Fragment> f, ViewPager v, LayoutInflater l) {
        super(fm);
        titles = t.clone();
        fragments = f;
        viewPager = v;
        layoutInflater = l;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public TextView getTab(final int position, SwipeyTabs root) {
        TextView view = (TextView) layoutInflater.inflate(R.layout.swipey_tab_indicator, root, false);
        view.setText(titles[position]);
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                viewPager.setCurrentItem(position);
            }
        });
        view.setTextColor(R.color.alpha_white);
        return view;
    }

}
