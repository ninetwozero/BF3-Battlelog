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

package com.ninetwozero.bf3droid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.coveragemapper.android.Map.ExternalCacheDirectory;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.activity.DashboardActivity;
import com.ninetwozero.bf3droid.activity.LoginActivity;
import com.ninetwozero.bf3droid.activity.aboutapp.AboutCreditsFragment;
import com.ninetwozero.bf3droid.activity.aboutapp.AboutFAQFragment;
import com.ninetwozero.bf3droid.activity.aboutapp.AboutLicenseFragment;
import com.ninetwozero.bf3droid.activity.aboutapp.AboutMainFragment;
import com.ninetwozero.bf3droid.datatype.DefaultFragmentActivity;
import com.ninetwozero.bf3droid.datatype.PostData;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;
import com.ninetwozero.bf3droid.misc.SessionKeeper;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

	private String[] valueFields;
	private PostData[] postData;

	private CheckBox agree;
	private EditText emailField;
	private EditText passwordField;
	private SlidingDrawer slidingDrawer;
	private OnDrawerOpenListener onDrawerOpenListener;
	private OnDrawerCloseListener onDrawerCloseListener;
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
		setContentView(R.layout.activity_main);

		cacheDirCheck();
		defaultFileCheck();
		createSession();

		postData = new PostData[Constants.FIELD_NAMES_LOGIN.length];
		valueFields = new String[2];
		init();
		setupDrawer();
	}

	public void init() {
		loginForm = (RelativeLayout) findViewById(R.id.login_form);
		loginNotice = (RelativeLayout) findViewById(R.id.login_notice);
		agree = (CheckBox) findViewById(R.id.agree_checkbox);
		agree.setOnCheckedChangeListener(agreeChanged);
		emailField = (EditText) findViewById(R.id.field_email);
		passwordField = (EditText) findViewById(R.id.field_password);
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
		setEmail();
	}

	private void setEmail() {
		if (hasEmail()) {
			emailField.setText(mSharedPreferences.getString(Constants.SP_BL_PROFILE_EMAIL, ""));
		}
	}

	private boolean hasEmail() {
		return mSharedPreferences.contains(Constants.SP_BL_PROFILE_EMAIL);
	}

	private void createSession() {
		if (SessionKeeper.getProfileData() != null) {
			startActivity(new Intent(this, DashboardActivity.class));
			finish();
		} else if (!mSharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE,"").equals("")) {
			startActivity(new Intent(this, DashboardActivity.class));
			finish();
		}
	}

	private void defaultFileCheck() {
		if (mSharedPreferences.getInt(Constants.SP_V_FILE, 0) != Constants.CHANGELOG_VERSION) {
			SharedPreferences.Editor spEdit = mSharedPreferences.edit();
			String username = mSharedPreferences.getString(Constants.SP_BL_PROFILE_EMAIL, "");
			String password = mSharedPreferences.getString(Constants.SP_BL_PROFILE_PASSWORD, "");
			spEdit.clear();

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
			if (!ExternalCacheDirectory.getInstance(this).getExternalCacheDirectory().exists()) {
				Toast.makeText(this, R.string.info_general_nocache, Toast.LENGTH_SHORT).show();
			} else {
				File nomediaFile = new File(ExternalCacheDirectory.getInstance(this).getExternalCacheDirectory()
						.toString(), ".nomedia");
				if (!nomediaFile.exists()) {
					nomediaFile.createNewFile();
				}
			}
		} catch (Exception ex) {
            Log.w("MainActivity", ex.toString());
			Toast.makeText(this, R.string.info_general_nocache,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void setupDrawer() {
		if (slidingDrawer == null) {
			slidingDrawer = (SlidingDrawer) findViewById(R.id.about_slider);
			onDrawerCloseListener = new OnDrawerCloseListener() {
				@Override
				public void onDrawerClosed() {
					slidingDrawer.setClickable(false);
				}
			};
			onDrawerOpenListener = new OnDrawerOpenListener() {
				@Override
				public void onDrawerOpened() {
					slidingDrawer.setClickable(true);
				}
			};

			slidingDrawer.setOnDrawerOpenListener(onDrawerOpenListener);
			slidingDrawer.setOnDrawerCloseListener(onDrawerCloseListener);
			setup();
		}
	}

	// TODO refactor this method and use more reliable check such as
	// http://stackoverflow.com/questions/6119722/how-to-check-edittexts-text-is-email-address-or-not
	public void onClick(View v) {
		if (v.getId() == R.id.button_login) {
			valueFields[0] = emailField.getText().toString();
			valueFields[1] = passwordField.getText().toString();
			if (validateEmailAndPassword(valueFields[0], valueFields[1])) {
				for (int i = 0, max = Constants.FIELD_NAMES_LOGIN.length; i < max; i++) {
					postData[i] = new PostData(
							Constants.FIELD_NAMES_LOGIN[i],
							(Constants.FIELD_VALUES_LOGIN[i] == null) ? valueFields[i]
									: Constants.FIELD_VALUES_LOGIN[i]);
				}
			} else {
				return;
			}

			if (PublicUtils.isNetworkAvailable(this)) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra(LoginActivity.EMAIL, emailField.getText().toString());
                intent.putExtra(LoginActivity.PASSWORD, passwordField.getText().toString());
                startActivity(intent);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && slidingDrawer.isOpened()) {
			slidingDrawer.animateClose();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void setup() {
		if (mListFragments == null) {
			mListFragments = new ArrayList<Fragment>();
			mListFragments.add(Fragment.instantiate(this, AboutLicenseFragment.class.getName()));
			mListFragments.add(Fragment.instantiate(this, AboutMainFragment.class.getName()));
			mListFragments.add(Fragment.instantiate(this, AboutFAQFragment.class.getName()));
			mListFragments.add(Fragment.instantiate(this, AboutCreditsFragment.class.getName()));

			mViewPager = (ViewPager) findViewById(R.id.viewpager_sub);
			mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs_sub);

			mPagerAdapter = new SwipeyTabsPagerAdapter(

			mFragmentManager,tabTitles(R.array.about_tab), mListFragments,
					mViewPager, mLayoutInflater);
			mViewPager.setAdapter(mPagerAdapter);
			mTabs.setAdapter(mPagerAdapter);

			mViewPager.setOnPageChangeListener(mTabs);
			mViewPager.setCurrentItem(1);
			mViewPager.setOffscreenPageLimit(2);
		}
	}

	@Override
	public void reload() {
	}
}