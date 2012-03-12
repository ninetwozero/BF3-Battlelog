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

package com.ninetwozero.battlelog;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;

public class AboutView extends Activity {

    // Fields
    private TabHost cTabHost;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Set the content view
        setContentView(R.layout.about_view);

        if (icicle != null && icicle.containsKey(Constants.SUPER_COOKIES)) {

            ArrayList<ShareableCookie> shareableCookies = icicle
                    .getParcelableArrayList(Constants.SUPER_COOKIES);
            RequestHandler.setCookies(shareableCookies);

        }

        // Set 'em up
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Tab
        cTabHost = (TabHost) findViewById(R.id.com_tabhost);
        cTabHost.setup();

        setupTabsSecondary(

                new String[] {
                        getString(R.string.label_about),
                        getString(R.string.label_faq),
                        getString(R.string.label_credits)
                }, new int[] {
                        R.layout.tab_content_main_about, R.layout.tab_content_main_faq,
                        R.layout.tab_content_main_credits
                }

        );

    }

    private void setupTabsSecondary(final String[] titleArray,
            final int[] layoutArray) {

        // Init
        TabHost.TabSpec spec;

        // Iterate them tabs
        for (int i = 0, max = titleArray.length; i < max; i++) {

            // Num
            final int num = i;
            View tabview = createTabView(cTabHost.getContext(), titleArray[num]);

            // Let's set the content
            spec = cTabHost.newTabSpec(titleArray[num]).setIndicator(tabview)
                    .setContent(

                            new TabContentFactory() {

                                public View createTabContent(String tag) {

                                    return layoutInflater.inflate(layoutArray[num],
                                            null);

                                }

                            }

                    );

            // Add the tab
            cTabHost.addTab(

                    spec

                    );

        }

    }

    public void onContactClick(View v) {

        if (v.getId() == R.id.wrap_web) {

            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.ninetwozero.com")));

        } else if (v.getId() == R.id.wrap_twitter) {

            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.ninetwozero.com")));

        } else if (v.getId() == R.id.wrap_email) {

            startActivity(

            Intent.createChooser(

                    new Intent(Intent.ACTION_SENDTO).setData(

                            Uri.parse(

                                    "mailto:support@ninetwozero.com"

                                    )

                            ), getString(R.string.info_txt_email_send)

                    )

            );

        } else if (v.getId() == R.id.wrap_forum) {

            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.ninetwozero.com/forum")));

        } else if (v.getId() == R.id.wrap_xbox) {

            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://live.xbox.com/en-US/Profile?gamertag=NINETWOZERO")));

        } else if (v.getId() == R.id.wrap_paypal) {

            startActivity(

            new Intent(

                    Intent.ACTION_VIEW,
                    Uri.parse(

                            "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=Y8GLB993JKTCL"

                            ))

            );

        }

    }

    private final View createTabView(final Context context, final String text) {

        View view = LayoutInflater.from(context).inflate(
                R.layout.profile_tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SUPER_COOKIES,
                RequestHandler.getCookies());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);
    }

}
