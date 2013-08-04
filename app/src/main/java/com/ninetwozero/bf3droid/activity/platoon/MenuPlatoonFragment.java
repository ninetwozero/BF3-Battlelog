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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.profile.settings.ProfileSettingsActivity;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.dialog.ListDialogFragment;
import com.ninetwozero.bf3droid.model.SelectedOption;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.ninetwozero.bf3droid.util.ImageLoader;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class MenuPlatoonFragment extends Fragment implements DefaultFragment {

    private Context context;
    private RelativeLayout wrapPlatoon;
    private TextView platoonText;
    private ImageView platoonImage;
    private final String DIALOG = "dialog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.tab_content_dashboard_platoon, container, false);
        initFragment(view);
        return view;
    }

    public void initFragment(View view) {
        wrapPlatoon = (RelativeLayout) view.findViewById(R.id.wrap_platoon);
        wrapPlatoon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                ListDialogFragment dialog = ListDialogFragment.newInstance(platoonsToMap(), SelectedOption.PLATOON);
                dialog.show(manager, DIALOG);
            }
        }

        );
        platoonImage = (ImageView) wrapPlatoon.findViewById(R.id.image_platoon);
        platoonText = (TextView) wrapPlatoon.findViewById(R.id.text_platoon);
        platoonText.setSelected(true);

        setupPlatoonBox();

        view.findViewById(R.id.button_new).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, PlatoonCreateActivity.class));
            }
        });
        view.findViewById(R.id.button_self).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user().getPlatoons().size() > 0) {
                    startActivity(new Intent(context, PlatoonActivity.class).putExtra("userType", User.USER));
                }
            }
        });
        view.findViewById(R.id.button_invites).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ProfileSettingsActivity.class));
            }
        });
        view.findViewById(R.id.button_settings).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user().getPlatoons().size() > 0) {
                    startActivity(new Intent(context, ProfileSettingsActivity.class));
                }
            }
        });
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

    private Map<Long, String> platoonsToMap() {
        Map<Long, String> map = new HashMap<Long, String>();
        for (SimplePlatoon platoon : user().getPlatoons()) {
            map.put(platoon.getPlatoonId(), platoon.getName() + " [" + platoon.getPlatform() + "]");
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
    public void selectionChanged(SelectedOption selectedOption) {
        if (selectedOption.getChangedGroup().equals(SelectedOption.PLATOON)) {
            user().selectPlatoon(selectedOption.getSelectedId());
            setupPlatoonBox();
        }
    }

    public void setupPlatoonBox() {
        if (user().getPlatoons().size() > 0 && platoonText != null) {
            platoonText.setText(selectedPlatoon().getName() + " [" + selectedPlatoon().getPlatform() + "]");
            ImageLoader imageLoader = provideImageLoader();
            imageLoader.setImageDimensions(48, 48);
            imageLoader.setDefaultImages(R.drawable.default_platoon_100);
            imageLoader.loadImage(platoonImage, selectedPlatoon().getPlatoonBadge());
        }
    }

    private SimplePlatoon selectedPlatoon() {
        return user().selectedPlatoon();
    }

    protected ImageLoader provideImageLoader() {
        Context appContext = context;
        LoaderSettings settings = new LoaderSettings.SettingsBuilder()
                .withDisconnectOnEveryCall(true)
                .withCacheManager(new LruBitmapCache(appContext))
                .build(appContext);
        return new ImageLoader(new ImageManager(appContext, settings));
    }

    private User user() {
        return BF3Droid.getUser();
    }
}
