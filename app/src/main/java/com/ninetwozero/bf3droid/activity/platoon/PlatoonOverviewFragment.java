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

package com.ninetwozero.bf3droid.activity.platoon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.PlatoonInformation;
import com.ninetwozero.bf3droid.jsonmodel.platoon.Platoon;
import com.ninetwozero.bf3droid.jsonmodel.platoon.PlatoonDossier;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.ninetwozero.bf3droid.util.DateTimeFormatter;
import com.ninetwozero.bf3droid.util.ImageInputStream;
import com.ninetwozero.bf3droid.util.ImageLoader;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.io.InputStream;

public class PlatoonOverviewFragment extends Fragment implements DefaultFragment {

    private static final String EMBLEMS_URL = "http://static.cdn.ea.com/battlelog/prod/emblems/320/";
    private static final String DEFAULT_BADGE = "http://battlelog-cdn.battlefield.com/cdnprefix/dab70dc9082526/public/platoon/default-emblem-320.png";

    private LayoutInflater inflater;
    private RelativeLayout layoutWrapper;
    /*TODO replace platoon information with appropriate Platoon object equivalents*/
    private PlatoonInformation platoonInformation;
    private TextView platoonLabel;
    private TextView dateLabel;
    private ImageView platformBadge;
    private ImageView platoonBadge;
    private RelativeLayout websiteContainer;
    private TextView platoonPresentation;
    private Platoon platoon;
    private TextView membersCount;
    private TextView fansCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;

        View view = this.inflater.inflate(R.layout.tab_content_platoon_overview, container, false);
        initFragment(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showUI();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void platoonDossier(PlatoonDossier platoonDossier) {
        this.platoon = platoonDossier.getPlatoon();
        updateUI();
    }

    public void initFragment(View v) {
        layoutWrapper = (RelativeLayout) v.findViewById(R.id.wrap_web);
        layoutWrapper.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(
                                new Intent(Intent.ACTION_VIEW).setData(Uri.parse(String.valueOf(v.getTag())))
                        );
                    }
                }
        );
    }

    private void showUI() {
        platoonLabel = (TextView) getView().findViewById(R.id.text_name_platoon);
        dateLabel = (TextView) getView().findViewById(R.id.text_date);
        platformBadge = (ImageView) getView().findViewById(R.id.image_platform);
        platoonBadge = (ImageView) getView().findViewById(R.id.image_badge);
        websiteContainer = (RelativeLayout) getView().findViewById(R.id.wrap_web);
        platoonPresentation = (TextView) getView().findViewById(R.id.text_presentation);
        membersCount = (TextView) getView().findViewById(R.id.text_members_count);
        fansCount = (TextView) getView().findViewById(R.id.text_fans_count);
    }

    public final void updateUI() {
        platoonLabel.setText(platoon.getName() + " [" + platoon.getTag() + "]");
        dateLabel.setText(DateTimeFormatter.dateString(platoon.getCreationDate()));
        platformBadge.setImageResource(platformImage(platoon.getPlatform()));

        ImageLoader imageLoader = provideImageLoader();
        imageLoader.setImageDimensions(320, 320);
        imageLoader.setDefaultImages(R.drawable.default_platoon_100);
        imageLoader.loadImage(platoonBadge, badgeUrl(platoon.getBadgePath()));


        if ("".equals(platoon.getWebsite())) {
            websiteContainer.setVisibility(View.GONE);
        } else {
            ((TextView) getView().findViewById(R.id.text_web)).setText(platoon.getWebsite());
            websiteContainer.setTag(platoon.getWebsite());
        }
        membersCount.setText(String.valueOf(platoon.getMemberCounter()));
        fansCount.setText(String.valueOf(platoon.getFanCounter()));

        if (platoon.getPresentation().equals("")) {
            platoonPresentation.setText(R.string.info_profile_empty_pres);
        } else {
            platoonPresentation.setText(platoon.getPresentation());
        }
    }

    private String badgeUrl(String badgePath) {
        return badgePath.length() == 0 ? DEFAULT_BADGE : buildBadgeUrl(badgePath);
    }

    private String buildBadgeUrl(String badgePath) {
        return EMBLEMS_URL + badgePath;
    }

    private int platformImage(int platformId) {
        switch (platformId) {
            case 1:
                return R.drawable.logo_pc;
            case 2:
                return R.drawable.logo_xbox;
            case 4:
                return R.drawable.logo_ps3;
            default:
                return R.drawable.logo_pc;
        }
    }

    public void reload() {
    }

    public Menu prepareOptionsMenu(Menu menu) {
        if (platoonInformation == null) {
            return menu;
        }
        if (platoonInformation.isOpenForNewMembers()) {
            if (platoonInformation.isMember()) {
                menu.findItem(R.id.option_join).setVisible(false);
                menu.findItem(R.id.option_leave).setVisible(true);
                menu.findItem(R.id.option_fans).setVisible(false);
                menu.findItem(R.id.option_invite).setVisible(false);
                menu.findItem(R.id.option_members).setVisible(false);
            } else if (platoonInformation.isOpenForNewMembers()) {
                menu.findItem(R.id.option_join).setVisible(true);
                menu.findItem(R.id.option_leave).setVisible(false);
                menu.findItem(R.id.option_fans).setVisible(false);
                menu.findItem(R.id.option_invite).setVisible(false);
                menu.findItem(R.id.option_members).setVisible(false);
            } else {
                menu.findItem(R.id.option_join).setVisible(false);
                menu.findItem(R.id.option_leave).setVisible(false);
                menu.findItem(R.id.option_fans).setVisible(false);
                menu.findItem(R.id.option_invite).setVisible(false);
                menu.findItem(R.id.option_members).setVisible(false);
            }
        } else {
            menu.findItem(R.id.option_join).setVisible(false);
            menu.findItem(R.id.option_leave).setVisible(false);
            menu.findItem(R.id.option_fans).setVisible(false);
            menu.findItem(R.id.option_invite).setVisible(false);
            menu.findItem(R.id.option_members).setVisible(false);
        }
        return menu;
    }

    public boolean handleSelectedOption(MenuItem item) {
        if (item.getItemId() == R.id.option_join) {
            modifyMembership(true);
        } else if (item.getItemId() == R.id.option_leave) {
            modifyMembership(false);
        }
        return true;
    }

    /*TODO fix membership*/
    private void modifyMembership(boolean isJoining) {
        /*new AsyncPlatoonRequest(
                context,
                mPlatoonData,
                SessionKeeper.getProfileData().getId(),
                sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")
        ).execute(isJoining);*/
    }

    protected ImageLoader provideImageLoader() {
        Context appContext = getGontext();
        LoaderSettings settings = new LoaderSettings.SettingsBuilder()
                .withDisconnectOnEveryCall(true)
                .withCacheManager(new LruBitmapCache(appContext))
                .build(appContext);
        return new ImageLoader(new ImageManager(appContext, settings));
    }

    private Context getGontext() {
        return getActivity().getApplicationContext();
    }
}
