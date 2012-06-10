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

import java.io.File;
import java.util.Vector;

import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.activity.DashboardActivity;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import net.sf.andhsli.hotspotlogin.SimpleCrypto;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.coveragemapper.android.Map.ExternalCacheDirectory;
import com.ninetwozero.battlelog.asynctask.AsyncLogin;
import com.ninetwozero.battlelog.datatype.DefaultFragmentActivity;
import com.ninetwozero.battlelog.datatype.PostData;
import com.ninetwozero.battlelog.datatype.ShareableCookie;
import com.ninetwozero.battlelog.activity.aboutapp.AboutCreditsFragment;
import com.ninetwozero.battlelog.activity.aboutapp.AboutFAQFragment;
import com.ninetwozero.battlelog.activity.aboutapp.AboutMainFragment;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.http.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class MainActivity extends CustomFragmentActivity implements DefaultFragmentActivity {

    // Attributes
    private String[] valueFields;
    private PostData[] postDataArray;

    // Elements
    private EditText fieldEmail, fieldPassword;
    private CheckBox checkboxSave;
    private SlidingDrawer slidingDrawer;
    private OnDrawerOpenListener onDrawerOpenListener;
    private OnDrawerCloseListener onDrawerCloseListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // onCreate - save the instance state
        super.onCreate(savedInstanceState);

        // Set the content view
        setContentView(R.layout.main);

        // Does the cache-dir exist?
        cacheDirCheck();

        // Check if the default-file is ok
        defaultFileCheck();

        // Are we active?
        createSession();

        // Initialize the attributes
        postDataArray = new PostData[Constants.FIELD_NAMES_LOGIN.length];
        valueFields = new String[2];

        // Do we need to show the cool changelog-dialog?
        changeLogDialog();

        // Let's populate... or shall we not?
        init();

        // Setup the drawer
        setupDrawer();

    }

    private void changeLogDialog() {

        if (sharedPreferences.getInt(Constants.SP_V_CHANGELOG,
                Constants.CHANGELOG_VERSION - 1) < Constants.CHANGELOG_VERSION) {
            createChangelogDialog().show();
        }

    }

    public void init() {

        // Get the fields
        fieldEmail = (EditText) findViewById(R.id.field_email);
        fieldPassword = (EditText) findViewById(R.id.field_password);
        checkboxSave = (CheckBox) findViewById(R.id.checkbox_save);
        emailPasswordValues();

    }

    private void emailPasswordValues() {

        setEmail();
        setCheckbox();
        setPassword();

    }

    private void setEmail() {

        if (hasEmail()) {
            fieldEmail.setText(sharedPreferences.getString(
                    Constants.SP_BL_PROFILE_EMAIL, ""));
        }

    }

    private boolean hasEmail() {

        return sharedPreferences.contains(Constants.SP_BL_PROFILE_EMAIL);

    }

    private void setCheckbox() {

        if (hasEmail()) {
            checkboxSave.setChecked(isPasswordRemembered());
        }

    }

    private boolean isPasswordRemembered() {

        return sharedPreferences.getBoolean(Constants.SP_BL_PROFILE_REMEMBER, false);

    }

    private void setPassword() {

        if (hasEmail() && isPasswordRemembered()) {

            // Do we have a password stored?
            if (hasPassword()) {

                try {

                    // Set the password (decrypted version)
                    fieldPassword.setText(SimpleCrypto.decrypt(
                            sharedPreferences.getString(Constants.SP_BL_PROFILE_EMAIL,
                                    ""), sharedPreferences.getString(
                                    Constants.SP_BL_PROFILE_PASSWORD, "")));

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

        }

    }

    private boolean hasPassword() {

        return !sharedPreferences.getString(Constants.SP_BL_PROFILE_PASSWORD, "")
                .equals("");
    }

    private void createSession() {

        if (SessionKeeper.getProfileData() != null) {

            startActivity(new Intent(this, DashboardActivity.class));
            finish();

        } else if (!sharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE,
                "").equals("")) {

            RequestHandler.setCookies(

                    new ShareableCookie(

                            sharedPreferences.getString(Constants.SP_BL_COOKIE_NAME, ""),
                            sharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE,
                                    ""), Constants.COOKIE_DOMAIN

                    )

                    );
            startActivity(

            new Intent(this, DashboardActivity.class)
                    .putExtra(

                            "myProfile",
                            SessionKeeper
                                    .generateProfileDataFromSharedPreferences(sharedPreferences)
                    ).putExtra(
                            "myPlatoon",
                            SessionKeeper
                                    .generatePlatoonDataFromSharedPreferences(sharedPreferences))

            );
            finish();

        }

    }

    private void defaultFileCheck() {

        if (sharedPreferences.getInt(Constants.SP_V_FILE, 0) != Constants.CHANGELOG_VERSION) {

            // Get the sharedPreferences
            SharedPreferences.Editor spEdit = sharedPreferences.edit();
            String username = sharedPreferences.getString(Constants.SP_BL_PROFILE_EMAIL, "");
            String password = sharedPreferences.getString(Constants.SP_BL_PROFILE_PASSWORD, "");

            // Let's clear it out
            spEdit.clear();

            // Re-fill
            spEdit.putString(Constants.SP_BL_PROFILE_EMAIL, username);
            spEdit.putString(Constants.SP_BL_PROFILE_PASSWORD, password);
            spEdit.putBoolean(Constants.SP_BL_PROFILE_REMEMBER, !password.equals(""));
            spEdit.putInt(Constants.SP_V_FILE, Constants.CHANGELOG_VERSION);

            // Commit!!
            spEdit.commit();

        }

    }

    private void cacheDirCheck() {

        try {

            if (!ExternalCacheDirectory.getInstance(this)
                    .getExternalCacheDirectory().exists()) {

                Toast.makeText(this, R.string.info_general_nocache,
                        Toast.LENGTH_SHORT).show();

            } else {

                // Is .nomedia created?
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
        if (slidingDrawer == null) {

            slidingDrawer = (SlidingDrawer) findViewById(R.id.about_slider);

            // Set the drawer listeners
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

            // Attach the listeners
            slidingDrawer.setOnDrawerOpenListener(onDrawerOpenListener);
            slidingDrawer.setOnDrawerCloseListener(onDrawerCloseListener);

            setup();
        }
    }

    // TODO refactor this method and use more reliable check such as
    // http://stackoverflow.com/questions/6119722/how-to-check-edittexts-text-is-email-address-or-not
    public void onClick(View v) {

        if (v.getId() == R.id.button_login) {

            // Let's set 'em values
            valueFields[0] = fieldEmail.getText().toString();
            valueFields[1] = fieldPassword.getText().toString();
            if (!validateEmailAndPassword(valueFields[0], valueFields[1]))
                return;

            // Iterate and conquer
            for (int i = 0, max = Constants.FIELD_NAMES_LOGIN.length; i < max; i++) {

                postDataArray[i] = new PostData(
                        Constants.FIELD_NAMES_LOGIN[i],
                        (Constants.FIELD_VALUES_LOGIN[i] == null) ? valueFields[i]
                                : Constants.FIELD_VALUES_LOGIN[i]);

            }

            // Clear the pwd-field
            if (!checkboxSave.isChecked())
                fieldPassword.setText("");

            // Do the async
            if (PublicUtils.isNetworkAvailable(this)) {

                AsyncLogin al = new AsyncLogin(this, checkboxSave.isChecked());
                al.execute(postDataArray);

            } else {

                Toast.makeText(this, R.string.general_nonetwork,
                        Toast.LENGTH_SHORT).show();

            }
            return;
        }

    }

    private boolean validateEmailAndPassword(String email, String password) {

        if (email.equals("") || !email.contains("@")) {

            Toast.makeText(this, R.string.general_invalid_email,
                    Toast.LENGTH_SHORT).show();
            return false;

        } else if (password.equals("")) {

            Toast.makeText(this, R.string.general_invalid_password,
                    Toast.LENGTH_SHORT).show();
            return false;

        }
        return true;
    }

    public final Dialog createChangelogDialog() {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.changelog_dialog,
                (ViewGroup) findViewById(R.id.dialog_root));

        // Set the title and the view
        builder.setTitle(getString(R.string.general_changelog_version).replace("{version}",
                Constants.CHANGELOG_VERSION + ""));

        // Grab the fields
        final TextView textView = (TextView) layout
                .findViewById(R.id.text_changelog);
        textView.setText(Html.fromHtml(getString(R.string.changelog)));

        // Set the button
        builder.setPositiveButton(

                android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        sharedPreferences
                                .edit()
                                .putInt(Constants.SP_V_CHANGELOG,
                                        Constants.CHANGELOG_VERSION).commit();

                    }

                }

                );

        // CREATE
        AlertDialog theDialog = builder.create();
        theDialog.setView(layout, 0, 0, 0, 0);
        return theDialog;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (slidingDrawer.isOpened()) {

                slidingDrawer.animateClose();
                return true;

            }

        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void setup() {

        // Do we need to setup the fragments?
        if (listFragments == null) {

            // Add them to the list
            listFragments = new Vector<Fragment>();
            listFragments.add(Fragment.instantiate(this,
                    AboutMainFragment.class.getName()));
            listFragments.add(Fragment.instantiate(this,
                    AboutFAQFragment.class.getName()));
            listFragments.add(Fragment.instantiate(this,
                    AboutCreditsFragment.class.getName()));

            // Get the ViewPager
            viewPager = (ViewPager) findViewById(R.id.viewpager_sub);
            tabs = (SwipeyTabs) findViewById(R.id.swipeytabs_sub);

            Log.d(Constants.DEBUG_TAG, "viewPager => " + viewPager);
            Log.d(Constants.DEBUG_TAG, "tabs => " + tabs);

            // Fill the PagerAdapter & set it to the viewpager
            pagerAdapter = new SwipeyTabsPagerAdapter(

                    fragmentManager,
                    new String[] {
                            getString(R.string.label_about), getString(R.string.label_faq),
                            getString(R.string.label_credits)
                    },
                    listFragments,
                    viewPager,
                    layoutInflater
                    );
            Log.d(Constants.DEBUG_TAG, "pagerAdapter => " + pagerAdapter);
            viewPager.setAdapter(pagerAdapter);
            tabs.setAdapter(pagerAdapter);

            // Make sure the tabs follow
            viewPager.setOnPageChangeListener(tabs);
            viewPager.setCurrentItem(0);
            viewPager.setOffscreenPageLimit(2);

        }

    }

    @Override
    public void reload() {
    }

}
