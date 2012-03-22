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

import com.ninetwozero.battlelog.DashboardActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.*;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;
import com.ninetwozero.battlelog.services.BattlelogService;
import net.sf.andhsli.hotspotlogin.SimpleCrypto;

import java.util.List;

public class AsyncLogin extends AsyncTask<PostData, Integer, Boolean> {

    // Attribute
    private ProgressDialog progressDialog;
    private Context context;
    private AsyncLogin origin;
    private boolean savePassword;
    private SessionKeeperPackage sessionKeeperPackage;
    private String locale;
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

            sessionKeeperPackage = doLogin(arg0);

            // Did it go ok?
            return (sessionKeeperPackage != null);

        } catch (WebsiteHandlerException ex) {

            errorMessage = ex.getMessage();
            return false;

        } catch (Exception ex) {

            errorMessage = context.getString(R.string.general_no_data);
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

                    new Intent(context, DashboardActivity.class).putExtra(

                            "myProfile", sessionKeeperPackage.getProfileData()

                            ).putExtra(

                                    "myLocale", locale

                            ).putExtra("myPlatoon", sessionKeeperPackage.getPlatoons())

                    );

            // Kill the main
            ((Activity) context).finish();

        } else {

            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                    .show();

        }
        return;

    }
    
    public SessionKeeperPackage renewSession(PostData[] postData) throws WebsiteHandlerException, RequestHandlerException {
        return doLogin(postData);
    }

    // Let's have this one ready
    private SessionKeeperPackage doLogin(PostData[] postData) throws WebsiteHandlerException, RequestHandlerException {
        this.postData = postData.clone();

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(Constants.URL_LOGIN, this.postData, 0);

            // Did we manage?
            return httpContent.length() >0 ? profileData(httpContent) : null;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    private SessionKeeperPackage profileData(String httpContent) throws Exception {
        // Set the int
        int startPosition = httpContent.indexOf(Constants.ELEMENT_UID_LINK);

        // Did we find it?
        return startPosition == -1 ? elementUidLinkError(httpContent) : processHttpContent(httpContent);
    }

    private SessionKeeperPackage processHttpContent(String httpContent) throws Exception {

        // Get the checksum
        String postCheckSum = substringFrom(httpContent, Constants.ELEMENT_STATUS_CHECKSUM, "\" />");

        // Let's work on getting the "username", not persona name -->
        // profileId
        String soldierName = substringFrom(httpContent, Constants.ELEMENT_USERNAME_LINK, "/\">");

        ProfileData profile = WebsiteHandler.getProfileIdFromSearch(soldierName,postCheckSum);
        profile = WebsiteHandler.getPersonaIdFromProfile(profile);
        List<PlatoonData> platoons = WebsiteHandler.getPlatoonsForUser(context, profile.getUsername());
        SharedPreferences sharedPreferences = addToSharedPreferences(profile, platoons, postCheckSum, soldierName);


        // Do we want to start a service?
        int serviceInterval = sharedPreferences.getInt(
                Constants.SP_BL_INTERVAL_SERVICE,
                (Constants.HOUR_IN_SECONDS / 2)) * 1000;

        startAlarmManager(serviceInterval);

        Log.d(Constants.DEBUG_TAG,
                "Setting the service to update every "
                        + serviceInterval / 60000 + " minutes");

        // Return it!!
        return new SessionKeeperPackage(profile, platoons);
    }

    private SharedPreferences addToSharedPreferences(ProfileData profile, List<PlatoonData> platoons, String postCheckSum, String soldierName) throws Exception {
        // Init
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spEdit = sharedPreferences.edit();
        // Further more, we would actually like to store the userid and
        // name
        spEdit.putString(Constants.SP_BL_EMAIL,
                postData[0].getValue());

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
        String personaLogos = "";

        // Do it for the platoons too
        String platoonIds = "";
        String platoonNames = "";
        String platoonTags = "";
        String platoonPlatformIds = "";
        String platoonImages = "";

        // We need to append the different parts to the ^ strings
        for (int i = 0, max = profile.getNumPersonas(); i < max; i++) {

            personaNames += profile.getPersona(i).getName() + ":";
            personaIds += String.valueOf(profile.getPersona(i).getId()) + ":";
            platformIds += String.valueOf(profile.getPersona(i).getPlatformId())
                    + ":";
            personaLogos += profile.getPersona(i).getLogo() + ":";

        }

        // The platoons need to be "cacheable" too
        for (int i = 0, max = platoons.size(); i < max; i++) {

            platoonIds += platoons.get(i).getId() + ":";
            platoonNames += platoons.get(i).getName() + ":";
            platoonTags += platoons.get(i).getTag() + ":";
            platoonPlatformIds += platoons.get(i).getPlatformId() + ":";
            platoonImages += platoons.get(i).getImage() + ":";

        }

        // This we keep!!!
        spEdit.putString(Constants.SP_BL_USERNAME, soldierName);
        spEdit.putString(Constants.SP_BL_PERSONA, personaNames);
        spEdit.putLong(Constants.SP_BL_PROFILE_ID,
                profile.getId());
        spEdit.putString(Constants.SP_BL_PERSONA_ID, personaIds);
        spEdit.putString(Constants.SP_BL_PLATFORM_ID, platformIds);
        spEdit.putString(Constants.SP_BL_PERSONA_LOGO, personaLogos);
        spEdit.putString(Constants.SP_BL_CHECKSUM, postCheckSum);

        // Platoons too!
        spEdit.putString(Constants.SP_BL_PLATOON_ID, platoonIds);
        spEdit.putString(Constants.SP_BL_PLATOON, platoonNames);
        spEdit.putString(Constants.SP_BL_PLATOON_TAG, platoonTags);
        spEdit.putString(Constants.SP_BL_PLATOON_PLATFORM_ID, platoonPlatformIds);
        spEdit.putString(Constants.SP_BL_PLATOON_IMAGE, platoonImages);

        // Cookie-related
        List<ShareableCookie> sca = RequestHandler.getCookies();
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

    private SessionKeeperPackage elementUidLinkError(String httpContent) throws WebsiteHandlerException {
        int startPosition;// Update the position
        startPosition = httpContent.indexOf(Constants.ELEMENT_ERROR_MESSAGE);

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
