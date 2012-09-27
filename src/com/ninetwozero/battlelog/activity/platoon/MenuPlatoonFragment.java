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

package com.ninetwozero.battlelog.activity.platoon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.profile.settings.ProfileSettingsActivity;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class MenuPlatoonFragment extends Fragment implements DefaultFragment {

	// Attributes
	private Context mContext;
	private Map<Integer, Intent> MENU_INTENTS;
	private SharedPreferences mSharedPreferences;

	// Elements
	private RelativeLayout mWrapPlatoon;
	private TextView mTextPlatoon;
	private ImageView mImagePlatoon;

	// Let's store the position & platoon
	private List<PlatoonData> mPlatoonData;
	private long[] mPlatoonId;
	private String[] mPlatoonName;
	private long mSelectedPlatoon;
	private int mSelectedPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Set our attributes
		mContext = getActivity();
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);

		// Let's inflate & return the view
		View view = inflater.inflate(R.layout.tab_content_dashboard_platoon,
				container, false);

		initFragment(view);

		return view;

	}

	public void initFragment(View view) {

		// Let's set the vars
		mSelectedPosition = mSharedPreferences.getInt(
				Constants.SP_BL_PLATOON_CURRENT_POS, 0);
		mSelectedPlatoon = mSharedPreferences.getLong(
				Constants.SP_BL_PLATOON_CURRENT_ID, 0);

		// Set up the Platoon box
		mWrapPlatoon = (RelativeLayout) view.findViewById(R.id.wrap_platoon);
		mWrapPlatoon.setOnClickListener(

		new OnClickListener() {

			@Override
			public void onClick(View v) {

				generateDialogPlatoonList().show();

			}

		}

		);
		mImagePlatoon = (ImageView) mWrapPlatoon
				.findViewById(R.id.image_platoon);
		mTextPlatoon = (TextView) mWrapPlatoon.findViewById(R.id.text_platoon);
		mTextPlatoon.setSelected(true);

		// Setup the "platoon box"
		setupPlatoonBox();

		// Set up the intents
		MENU_INTENTS = new HashMap<Integer, Intent>();
		MENU_INTENTS.put(R.id.button_new, new Intent(mContext,
				PlatoonCreateActivity.class));
		MENU_INTENTS.put(R.id.button_invites, new Intent(mContext,
				ProfileSettingsActivity.class));

		if (mPlatoonData != null && !mPlatoonData.isEmpty()) {

			MENU_INTENTS.put(R.id.button_self, new Intent(mContext,
					PlatoonActivity.class).putExtra("platoon",
					mPlatoonData.get(mSelectedPosition)));
			MENU_INTENTS.put(R.id.button_settings, new Intent(mContext,
					ProfileSettingsActivity.class).putExtra("platoon",
					mPlatoonData.get(mSelectedPosition)));
		}

		// Add the OnClickListeners
		final OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(MENU_INTENTS.get(v.getId()));

			}
		};

		for (int key : MENU_INTENTS.keySet()) {

			view.findViewById(key).setOnClickListener(onClickListener);

		}

		// Let's reload!
		reload();

	}

	@Override
	public void reload() {

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

	public Dialog generateDialogPlatoonList() {

		// Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		// Set the title and the view
		builder.setTitle(R.string.info_xml_platoon_select);

		// Do we have items to show?
		if (mPlatoonId == null) {

			// Init
			mPlatoonId = new long[mPlatoonData.size()];
			mPlatoonName = new String[mPlatoonData.size()];

			// Iterate
			for (int count = 0, max = mPlatoonData.size(); count < max; count++) {

				mPlatoonId[count] = mPlatoonData.get(count).getId();
				mPlatoonName[count] = mPlatoonData.get(count).getName();

			}

		}

		// Set it up
		builder.setSingleChoiceItems(

		mPlatoonName, -1, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) {

				if (mPlatoonId[item] != mSelectedPlatoon) {

					// Update it
					mSelectedPlatoon = mPlatoonId[item];

					// Store selectedPlatoonPos
					mSelectedPosition = item;

					// Load the new!
					setupPlatoonBox();

					// Save it
					SharedPreferences.Editor spEdit = mSharedPreferences.edit();
					spEdit.putLong(Constants.SP_BL_PLATOON_CURRENT_ID,
							mSelectedPlatoon);
					spEdit.putInt(Constants.SP_BL_PLATOON_CURRENT_POS,
							mSelectedPosition);
					spEdit.commit();

					// Reset these
					MENU_INTENTS.put(R.id.button_self, new Intent(mContext,
							PlatoonActivity.class).putExtra("platoon",
							mPlatoonData.get(mSelectedPosition)));
					MENU_INTENTS.put(R.id.button_settings, new Intent(mContext,
							ProfileSettingsActivity.class).putExtra("platoon",
							mPlatoonData.get(mSelectedPosition)));

				}

				dialog.dismiss();

			}

		}

		);

		// CREATE
		return builder.create();

	}

	public void setupPlatoonBox() {

		// Let's see...
		if (mPlatoonData != null && !mPlatoonData.isEmpty()
				&& mTextPlatoon != null) {

			// Let's validate our digits
			if ((mPlatoonData.size() - 1) < mSelectedPosition) {

				mSelectedPosition = mPlatoonData.size() - 1;
				mSelectedPlatoon = mPlatoonData.get(mSelectedPosition).getId();

				// Reset these
				MENU_INTENTS.put(R.id.button_self, new Intent(mContext,
						PlatoonActivity.class).putExtra("platoon",
						mPlatoonData.get(mSelectedPosition)));
				MENU_INTENTS.put(R.id.button_settings, new Intent(mContext,
						ProfileSettingsActivity.class).putExtra("platoon",
						mPlatoonData.get(mSelectedPosition)));

			}

			// Set the text
			mTextPlatoon.setText(mPlatoonData.get(mSelectedPosition).getName()
					+ "[" + mPlatoonData.get(mSelectedPosition).getTag() + "]");
			mImagePlatoon.setImageBitmap(BitmapFactory.decodeFile(PublicUtils
					.getCachePath(mContext)
					+ mPlatoonData.get(mSelectedPosition).getImage()));

		}

	}

	public void setPlatoonData(List<PlatoonData> p) {

		mPlatoonData = p;
		setupPlatoonBox();

	}

	public class AsyncRefresh extends AsyncTask<ProfileData, Void, Boolean> {
		@Override
		protected Boolean doInBackground(ProfileData... arg0) {
			try {
				mPlatoonData = new ProfileClient(arg0[0]).getPlatoons(mContext);
				return (mPlatoonData != null);
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				setupPlatoonBox();
			}
		}
	}

}
