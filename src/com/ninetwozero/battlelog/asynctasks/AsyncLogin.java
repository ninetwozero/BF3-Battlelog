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

package com.ninetwozero.battlelog.asynctasks;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ninetwozero.battlelog.Dashboard;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;
import com.ninetwozero.battlelog.services.BattlelogService;
import net.sf.andhsli.hotspotlogin.SimpleCrypto;

import java.util.ArrayList;

public class AsyncLogin extends AsyncTask<PostData, Integer, Boolean> {

    // Attribute
    private ProgressDialog progressDialog;
    private Context context;
    private AsyncLogin origin;
    private boolean savePassword;
    private ProfileData profile;
    private String locale;     //TODO investigate if we really need this as only NULL is passed with Intent
    private String errorMessage;
    private PostData[] postData;

    // Constructor
    public AsyncLogin(Context c) {

        origin = this;
        context = c;

    }

    // Constructor
    public AsyncLogin(Context c, boolean s) {

        this(c);
        savePassword = s;
    }

    @Override
    protected void onPreExecute() {

        // Let's see
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.general_wait));
        progressDialog.setMessage(context.getString(R.string.msg_logging_in));
        progressDialog.setOnCancelListener(

                new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        origin.cancel(true);
                        dialog.dismiss();
                    }
                }
        );
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(PostData... arg0) {

        try {
            profile = doLogin(arg0);

            // Did it go ok?
            return (profile != null);

        } catch (WebsiteHandlerException ex) {

            errorMessage = ex.getMessage();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean results) {

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        if (results) {
            // Start new
            context.startActivity(

                    new Intent(context, Dashboard.class)
                            .putExtra("myProfile", profile)
                            .putExtra("myLocale", locale)
            );

            // Kill the main
            ((Activity) context).finish();

        } else {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
        return;
    }



    public ProfileData sessionRenew(PostData[] arg0) throws WebsiteHandlerException{
        return doLogin(arg0);
    }

    private ProfileData doLogin(PostData[] postDataArray) throws WebsiteHandlerException {
        this.postData = postDataArray.clone();
        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(Constants.URL_LOGIN, postData, 0);

            // Did we manage?
            return httpContent.length() != 0 ? profileData(httpContent) : null;

        } catch (Exception ex) {

            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    private ProfileData profileData(String httpContent) throws Exception {
        // Set the int
        int startPosition = httpContent.indexOf(Constants.ELEMENT_UID_LINK);

        // Did we find it?
        return startPosition == -1 ? elementUidLinkError(httpContent) : processHttpContent(httpContent);
    }

    private ProfileData processHttpContent(String httpContent) throws Exception {
        // Get the checksum
        String postCheckSum = substringFrom(httpContent, Constants.ELEMENT_STATUS_CHECKSUM, "\" />");

        // Let's work on getting the "username", not persona name -->
        // profileId
        String soldierName = substringFrom(httpContent, Constants.ELEMENT_USERNAME_LINK, "/\">");

        ProfileData profileData = WebsiteHandler.getProfileIdFromSearch(soldierName,
                postCheckSum);
        profileData = WebsiteHandler.getPersonaIdFromProfile(profileData);
        
        SharedPreferences sharedPreferences = soldierToSharedPreferences(postCheckSum, soldierName, profileData);

        // Do we want to start a service?
        int serviceInterval = sharedPreferences.getInt(
                Constants.SP_BL_INTERVAL_SERVICE,
                (Constants.HOUR_IN_SECONDS / 2)) * 1000;
        startAlarmManager(serviceInterval);

        profileToLog(profileData, serviceInterval);
        return profileData;
    }

    private SharedPreferences soldierToSharedPreferences(String postCheckSum, String soldierName, ProfileData profileData) throws Exception {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spEdit = sharedPreferences.edit();
        // Further more, we would actually like to store the userid and
        // name
        spEdit.putString(Constants.SP_BL_EMAIL, postData[0].getValue());

        // Should we remember the password?
        if (savePassword) {

            spEdit.putString(Constants.SP_BL_PASSWORD, SimpleCrypto
                    .encrypt(postData[0].getValue(),
                            postData[1].getValue()));
            spEdit.putBoolean(Constants.SP_BL_REMEMBER, true);

        } else {

            spEdit.putString(Constants.SP_BL_PASSWORD, "");
            spEdit.putBoolean(Constants.SP_BL_REMEMBER, false);
        }

        // Init the strings
        String personaNames = "";
        String personaIds = "";
        String platformIds = "";

        // We need to append the different parts to the ^ strings
        for (int i = 0, max = profileData.getNumPersonas(); i < max; i++) {

            personaNames += profileData.getPersona(i).getName() + ":";
            personaIds += String.valueOf(profileData.getPersona(i).getId()) + ":";
            platformIds += String.valueOf(profileData.getPersona(i).getPlatformId())
                    + ":";

        }

        // This we keep!!!
        spEdit.putString(Constants.SP_BL_USERNAME, soldierName);
        spEdit.putString(Constants.SP_BL_PERSONA, personaNames);
        spEdit.putLong(Constants.SP_BL_PROFILE_ID,
                profileData.getId());
        spEdit.putString(Constants.SP_BL_PERSONA_ID, personaIds);
        spEdit.putString(Constants.SP_BL_PLATFORM_ID, platformIds);
        spEdit.putString(Constants.SP_BL_CHECKSUM, postCheckSum);

        // Cookie-related
        ArrayList<ShareableCookie> sca = RequestHandler.getCookies();
        if (sca != null) {

            ShareableCookie sc = sca.get(0);
            spEdit.putString(Constants.SP_BL_COOKIE_NAME, sc.getName());
            spEdit.putString(Constants.SP_BL_COOKIE_VALUE,
                    sc.getValue());

        } else {

            throw new WebsiteHandlerException(
                    context.getString(R.string.info_login_lostcookie));

        }

        // Co-co-co-commit
        spEdit.commit();
        return sharedPreferences;
    }

    private void startAlarmManager(int serviceInterval) {
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(

                AlarmManager.ELAPSED_REALTIME, 0, serviceInterval,
                PendingIntent.getService(context, 0, new Intent(
                        context, BattlelogService.class), 0)

        );
    }

    private void profileToLog(ProfileData profile, int serviceInterval) {
        Log.d(Constants.DEBUG_TAG,
                "Setting the service to update every "
                        + serviceInterval / 60000 + " minutes");

        // Return it!!
        Log.d(Constants.DEBUG_TAG, "profile => " + profile);
        Log.d(Constants.DEBUG_TAG, "-------------------------");
        Log.d(Constants.DEBUG_TAG, "profile::accountName => " + profile.getUsername());
        Log.d(Constants.DEBUG_TAG, "profile::personaId => " + profile.getPersona(0).getId());
        Log.d(Constants.DEBUG_TAG, "profile::personaName => " + profile.getPersona(0).getName());
        Log.d(Constants.DEBUG_TAG, "profile::platformId => " + profile.getPersona(0).getPlatformId());
        Log.d(Constants.DEBUG_TAG, "profile::gravarhash => " + profile.getGravatarHash());
        Log.d(Constants.DEBUG_TAG, "-------------------------");
    }

    private ProfileData elementUidLinkError(String httpContent) throws WebsiteHandlerException {
        int startPosition  = httpContent.indexOf(Constants.ELEMENT_ERROR_MESSAGE);

        // Is it -1 again?
        if (startPosition == -1) {

            throw new WebsiteHandlerException(
                    "The website won't let us in. Please try again later.");

        } else {

            String errorMsg = httpContent.substring(startPosition)
                    .replace("</div>", "")
                    .replace("\n", "")
                    .replace(Constants.ELEMENT_ERROR_MESSAGE, "");
            errorMsg = errorMsg.substring(0, errorMsg.indexOf("<div"));

            throw new WebsiteHandlerException(errorMsg);

        }
    }

    private String substringFrom(String content, String expression, String endsWith){
        String result = content.substring(content
                .indexOf(expression));
        return result.substring(0,
                result.indexOf(endsWith)).replace(expression, "");
    }
}
