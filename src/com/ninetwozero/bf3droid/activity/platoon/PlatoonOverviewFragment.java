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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.ninetwozero.bf3droid.asynctask.AsyncPlatoonRequest;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.PlatoonData;
import com.ninetwozero.bf3droid.datatype.PlatoonInformation;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;
import com.ninetwozero.bf3droid.misc.SessionKeeper;

public class PlatoonOverviewFragment extends Fragment implements DefaultFragment {

    private Context context;
    private LayoutInflater inflater;
    private SharedPreferences mSharedPreferences;
    private ImageView platoonBadge;
    private RelativeLayout mWrapWeb;
    private PlatoonData mPlatoonData;
    private PlatoonInformation mPlatoonInformation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.inflater = inflater;

        View view = this.inflater.inflate(R.layout.tab_content_platoon_overview, container, false);
        initFragment(view);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void initFragment(View v) {
        mWrapWeb = (RelativeLayout) v.findViewById(R.id.wrap_web);
        mWrapWeb.setOnClickListener(
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

    public final void show(PlatoonInformation data) {
        if (data == null || context == null) {
            return;
        }
        Activity activity = (Activity) context;

        ((TextView) activity.findViewById(R.id.text_name_platoon)).setText(data.getName() + " [" + data.getTag() + "]");
        if( data.getDateCreated() == 0 ) {
        	((TextView) activity.findViewById(R.id.text_date)).setText(R.string.msg_unknown_time);
        } else {
        	((TextView) activity.findViewById(R.id.text_date)).setText(
	            getString(R.string.info_platoon_created).replace(
	            	"{DATE}", 
	            	PublicUtils.getDate(data.getDateCreated())
	            ).replace(
	            	"{RELATIVE DATE}", 
	            	PublicUtils.getRelativeDate(context, data.getDateCreated())
	            )
			);
        }

        switch (data.getPlatformId()) {
            case 1:
                ((ImageView) activity.findViewById(R.id.image_platform)).setImageResource(R.drawable.logo_pc);
                break;
            case 2:
                ((ImageView) activity.findViewById(R.id.image_platform)).setImageResource(R.drawable.logo_xbox);
                break;
            case 4:
                ((ImageView) activity.findViewById(R.id.image_platform)).setImageResource(R.drawable.logo_ps3);
                break;
            default:
                ((ImageView) activity.findViewById(R.id.image_platform)).setImageResource(R.drawable.logo_pc);
                break;
        }

        ((ImageView) activity.findViewById(R.id.image_badge)).setImageBitmap(
        	BitmapFactory.decodeFile(PublicUtils.getCachePath(context) + data.getId() + ".jpeg")
        );

        if ("".equals(data.getWebsite())) {
            ((View) activity.findViewById(R.id.wrap_web)).setVisibility(View.GONE);
        } else {
            ((TextView) activity.findViewById(R.id.text_web)).setText(data.getWebsite());
            ((View) activity.findViewById(R.id.wrap_web)).setTag(data.getWebsite());
        }

        if (data.getPresentation().equals("")) {
        	((TextView) activity.findViewById(R.id.text_presentation)).setText(R.string.info_profile_empty_pres);
        } else {
            ((TextView) activity.findViewById(R.id.text_presentation)).setText(data.getPresentation());
        }
    }

    public void reload() {
    }

    public void setPlatoonData(PlatoonData p) {
        mPlatoonData = p;
    }

    public Menu prepareOptionsMenu(Menu menu) {
        if (mPlatoonInformation == null) {
            return menu;
        }
        if (mPlatoonInformation.isOpenForNewMembers()) {
            if (mPlatoonInformation.isMember()) {
                menu.findItem(R.id.option_join).setVisible(false);
                menu.findItem(R.id.option_leave).setVisible(true);
                menu.findItem(R.id.option_fans).setVisible(false);
                menu.findItem(R.id.option_invite).setVisible(false);
                menu.findItem(R.id.option_members).setVisible(false);
            } else if (mPlatoonInformation.isOpenForNewMembers()) {
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

	private void modifyMembership(boolean isJoining) {
		new AsyncPlatoonRequest(
                context,
        	mPlatoonData, 
        	SessionKeeper.getProfileData().getId(),
        	mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")
        ).execute(isJoining);		
	}
}
