/*
 * This file is part of BF3 Battlelog
 * 
 * BF3 Battlelog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * BF3 Battlelog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package com.ninetwozero.battlelog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import com.coveragemapper.android.Map.ExternalCacheDirectory;
import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.activity.DashboardActivity;
import com.ninetwozero.battlelog.activity.aboutapp.AboutCreditsFragment;
import com.ninetwozero.battlelog.activity.aboutapp.AboutFAQFragment;
import com.ninetwozero.battlelog.activity.aboutapp.AboutLicenseFragment;
import com.ninetwozero.battlelog.activity.aboutapp.AboutMainFragment;
import com.ninetwozero.battlelog.asynctask.AsyncLogin;
import com.ninetwozero.battlelog.datatype.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.PostData;
import com.ninetwozero.battlelog.datatype.ShareableCookie;
import com.ninetwozero.battlelog.http.RequestHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import net.sf.andhsli.hotspotlogin.SimpleCrypto;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends CustomFragmentActivity implements
		DefaultFragmentActivity {

	private String[] mValueFields;
	private PostData[] mPostDataArray;

	private CheckBox agree;
	private EditText mFieldEmail;
	private EditText mFieldPassword;
	private SlidingDrawer mSlidingDrawer;
	private OnDrawerOpenListener mOnDrawerOpenListener;
	private OnDrawerCloseListener mOnDrawerCloseListener;
	private static final String USER_AGREED = "user_agreed";
	private RelativeLayout loginNotice;
	private RelativeLayout loginForm;
	private CompoundButton.OnCheckedChangeListener agreeChanged = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton compoundButton,
				boolean isChecked) {
			if (isChecked) {
				loginForm.setVisibility(View.VISIBLE);
				loginNotice.setVisibility(View.GONE);
			} else {
				loginForm.setVisibility(View.GONE);
				loginNotice.setVisibility(View.VISIBLE);
			}
			changeAgreeStatus(isChecked);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		cacheDirCheck();
		defaultFileCheck();
		createSession();

		// Initialize the attributes
		mPostDataArray = new PostData[Constants.FIELD_NAMES_LOGIN.length];
		mValueFields = new String[2];

		// Do we need to show the cool changelog-dialog?
		showChangeLogDialog();

		init();
		setupDrawer();
	}

	private void showChangeLogDialog() {
		if (mSharedPreferences.getInt(Constants.SP_V_CHANGELOG,
				Constants.CHANGELOG_VERSION - 1) < Constants.CHANGELOG_VERSION) {
			createChangelogDialog().show();
		}
	}

	public void init() {
		loginForm = (RelativeLayout) findViewById(R.id.login_form);
		loginNotice = (RelativeLayout) findViewById(R.id.login_notice);
		agree = (CheckBox) findViewById(R.id.agree_checkbox);
		agree.setOnCheckedChangeListener(agreeChanged);
		mFieldEmail = (EditText) findViewById(R.id.field_email);
		mFieldPassword = (EditText) findViewById(R.id.field_password);
		TextView notice = (TextView) findViewById(R.id.notice_text);
		notice.setText(Html.fromHtml(getString(R.string.notice_text)));

		if (mSharedPreferences.getBoolean(USER_AGREED, false)) {
			loginForm.setVisibility(View.VISIBLE);
			loginNotice.setVisibility(View.GONE);
			agree.setChecked(true);
		} else {
			loginForm.setVisibility(View.GONE);
			loginNotice.setVisibility(View.VISIBLE);
		}
		emailPasswordValues();
	}

	private void emailPasswordValues() {
		setEmail();
		setPassword();
	}

	private void setEmail() {
		if (hasEmail()) {
			mFieldEmail.setText(mSharedPreferences.getString(
					Constants.SP_BL_PROFILE_EMAIL, ""));
		}
	}

	private boolean hasEmail() {
		return mSharedPreferences.contains(Constants.SP_BL_PROFILE_EMAIL);
	}

	private boolean isPasswordRemembered() {
		return mSharedPreferences.getBoolean(Constants.SP_BL_PROFILE_REMEMBER,
				false);
	}

	private void setPassword() {
		if (hasEmail() && isPasswordRemembered() && hasPassword()) {
			try {
				mFieldPassword.setText(SimpleCrypto.decrypt(mSharedPreferences
						.getString(Constants.SP_BL_PROFILE_EMAIL, ""),
						mSharedPreferences.getString(
								Constants.SP_BL_PROFILE_PASSWORD, "")));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean hasPassword() {
		return !mSharedPreferences.getString(Constants.SP_BL_PROFILE_PASSWORD,
				"").equals("");
	}

	private void createSession() {
		if (SessionKeeper.getProfileData() != null) {
			startActivity(new Intent(this, DashboardActivity.class));
			finish();
		} else if (!mSharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE,
				"").equals("")) {

			RequestHandler.setCookies(new ShareableCookie(mSharedPreferences
					.getString(Constants.SP_BL_COOKIE_NAME, ""),
					mSharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE,
							""), Constants.COOKIE_DOMAIN));
			startActivity(new Intent(this, DashboardActivity.class)
					.putExtra(
							"myProfile",
							SessionKeeper
									.generateProfileDataFromSharedPreferences(mSharedPreferences))
					.putExtra(
							"myPlatoon",
							(ArrayList<PlatoonData>) SessionKeeper
									.generatePlatoonDataFromSharedPreferences(mSharedPreferences)));
			finish();
		}
	}

	private void defaultFileCheck() {
		if (mSharedPreferences.getInt(Constants.SP_V_FILE, 0) != Constants.CHANGELOG_VERSION) {
			SharedPreferences.Editor spEdit = mSharedPreferences.edit();
			String username = mSharedPreferences.getString(
					Constants.SP_BL_PROFILE_EMAIL, "");
			String password = mSharedPreferences.getString(
					Constants.SP_BL_PROFILE_PASSWORD, "");
			spEdit.clear();

			// Re-fill
			spEdit.putString(Constants.SP_BL_PROFILE_EMAIL, username);
			spEdit.putString(Constants.SP_BL_PROFILE_PASSWORD, password);
			spEdit.putBoolean(Constants.SP_BL_PROFILE_REMEMBER,
					!password.equals(""));
			spEdit.putInt(Constants.SP_V_FILE, Constants.CHANGELOG_VERSION);
			spEdit.commit();
		}
	}

	private void changeAgreeStatus(boolean isChecked) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putBoolean(USER_AGREED, isChecked);
		editor.commit();
	}

	private void cacheDirCheck() {
		try {
			if (!ExternalCacheDirectory.getInstance(this)
					.getExternalCacheDirectory().exists()) {

				Toast.makeText(this, R.string.info_general_nocache,
						Toast.LENGTH_SHORT).show();
			} else {
				File nomediaFile = new File(ExternalCacheDirectory
						.getInstance(this).getExternalCacheDirectory()
						.toString(), ".nomedia");
				if (!nomediaFile.exists()) {
					nomediaFile.createNewFile();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, R.string.info_general_nocache,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void setupDrawer() {
		// Define the SlidingDrawer
		if (mSlidingDrawer == null) {
			mSlidingDrawer = (SlidingDrawer) findViewById(R.id.about_slider);
			mOnDrawerCloseListener = new OnDrawerCloseListener() {
				@Override
				public void onDrawerClosed() {
					mSlidingDrawer.setClickable(false);
				}
			};
			mOnDrawerOpenListener = new OnDrawerOpenListener() {
				@Override
				public void onDrawerOpened() {
					mSlidingDrawer.setClickable(true);
				}
			};

			mSlidingDrawer.setOnDrawerOpenListener(mOnDrawerOpenListener);
			mSlidingDrawer.setOnDrawerCloseListener(mOnDrawerCloseListener);
			setup();
		}
	}

	// TODO refactor this method and use more reliable check such as
	// http://stackoverflow.com/questions/6119722/how-to-check-edittexts-text-is-email-address-or-not
	public void onClick(View v) {
		if (v.getId() == R.id.button_login) {
			mValueFields[0] = mFieldEmail.getText().toString();
			mValueFields[1] = mFieldPassword.getText().toString();
			if (validateEmailAndPassword(mValueFields[0], mValueFields[1])) {
				for (int i = 0, max = Constants.FIELD_NAMES_LOGIN.length; i < max; i++) {
					mPostDataArray[i] = new PostData(
							Constants.FIELD_NAMES_LOGIN[i],
							(Constants.FIELD_VALUES_LOGIN[i] == null) ? mValueFields[i]
									: Constants.FIELD_VALUES_LOGIN[i]);
				}
			} else {
				return;
			}

			// Do the async
			if (PublicUtils.isNetworkAvailable(this)) {
				AsyncLogin al = new AsyncLogin(this);
				al.execute(mPostDataArray);
			} else {
				Toast.makeText(this, R.string.general_nonetwork,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private boolean validateEmailAndPassword(String email, String password) {
		if ("".equals(email) || !email.contains("@")) {
			Toast.makeText(this, R.string.general_invalid_email,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if ("".equals(password)) {
			Toast.makeText(this, R.string.general_invalid_password,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	public final Dialog createChangelogDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.changelog_dialog,
				(ViewGroup) findViewById(R.id.dialog_root));

		builder.setTitle(getString(R.string.general_changelog_version).replace(
				"{version}", Constants.CHANGELOG_VERSION + ""));

		// Grab the fields
		final TextView textView = (TextView) layout
				.findViewById(R.id.text_changelog);
		textView.setText(Html.fromHtml(getString(R.string.changelog)));

		// Set the button
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mSharedPreferences
								.edit()
								.putInt(Constants.SP_V_CHANGELOG,
										Constants.CHANGELOG_VERSION).commit();
					}
				});
		AlertDialog theDialog = builder.create();
		theDialog.setView(layout, 0, 0, 0, 0);
		return theDialog;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mSlidingDrawer.isOpened()) {
			mSlidingDrawer.animateClose();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void setup() {
		if (mListFragments == null) {
			mListFragments = new ArrayList<Fragment>();
			mListFragments.add(Fragment.instantiate(this,
					AboutLicenseFragment.class.getName()));
			mListFragments.add(Fragment.instantiate(this,
					AboutMainFragment.class.getName()));
			mListFragments.add(Fragment.instantiate(this,
					AboutFAQFragment.class.getName()));
			mListFragments.add(Fragment.instantiate(this,
					AboutCreditsFragment.class.getName()));

			// Get the ViewPager
			mViewPager = (ViewPager) findViewById(R.id.viewpager_sub);
			mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs_sub);

			// Fill the PagerAdapter & set it to the viewpager
			mPagerAdapter = new SwipeyTabsPagerAdapter(

			mFragmentManager, new String[] { getString(R.string.label_license),
					getString(R.string.label_about),
					getString(R.string.label_faq),
					getString(R.string.label_credits) }, mListFragments,
					mViewPager, mLayoutInflater);
			mViewPager.setAdapter(mPagerAdapter);
			mTabs.setAdapter(mPagerAdapter);

			// Make sure the tabs follow
			mViewPager.setOnPageChangeListener(mTabs);
			mViewPager.setCurrentItem(1);
			mViewPager.setOffscreenPageLimit(2);
		}
	}

	@Override
	public void reload() {
	}
}
