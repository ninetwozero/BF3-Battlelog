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

package com.ninetwozero.battlelog.asynctask;

import java.util.List;

import net.sf.andhsli.hotspotlogin.SimpleCrypto;
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
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.DashboardActivity;
import com.ninetwozero.battlelog.datatype.PersonaData;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.PostData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.ProfileInformation;
import com.ninetwozero.battlelog.datatype.RequestHandlerException;
import com.ninetwozero.battlelog.datatype.SessionKeeperPackage;
import com.ninetwozero.battlelog.datatype.ShareableCookie;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.http.RequestHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.service.BattlelogService;

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

    }

    public SessionKeeperPackage renewSession(PostData[] postData) throws WebsiteHandlerException,
            RequestHandlerException {
        return doLogin(postData);
    }

    // Let's have this one ready
    private SessionKeeperPackage doLogin(PostData[] postData) throws WebsiteHandlerException,
            RequestHandlerException {
        this.postData = postData.clone();

        try {

            // Let's login everybody!
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(Constants.URL_LOGIN, this.postData, 0);

            // Did we manage?
            return httpContent.length() > 0 ? profileData(httpContent) : null;

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());

        }

    }

    private SessionKeeperPackage profileData(String httpContent) throws Exception {
        // Set the int
        int startPosition = httpContent.indexOf(Constants.ELEMENT_UID_LINK);

        // Did we find it?
        return startPosition == -1 ? elementUidLinkError(httpContent)
                : processHttpContent(httpContent);
    }

    private SessionKeeperPackage processHttpContent(String httpContent) throws Exception {
    	
        // Get the checksum & soldier name from the HTML
        String postCheckSum = substringFrom(httpContent, Constants.ELEMENT_STATUS_CHECKSUM, "\" />");
        String soldierName = substringFrom(httpContent, Constants.ELEMENT_USERNAME_LINK, "</div>").trim(); 
        
        // Fetch some profile information
        ProfileInformation profileInformation = new ProfileClient(new ProfileData(soldierName)).getInformation(context, 0);
        ProfileData profileData = new ProfileData.Builder(profileInformation.getUserId(), profileInformation.getUsername())
        .persona(profileInformation.getAllPersonas())
        .build();
        
        // Store the information in SharedPreferences
        SharedPreferences sharedPreferences = addToSharedPreferences(profileInformation, postCheckSum);

        // Do we want to start a service?
        int serviceInterval = sharedPreferences.getInt(
                Constants.SP_BL_INTERVAL_SERVICE,
                (Constants.HOUR_IN_SECONDS / 2)) * 1000;

        startAlarmManager(serviceInterval);

        // Return it!!
        return new SessionKeeperPackage(profileData, profileInformation.getPlatoons());
    }

    private SharedPreferences addToSharedPreferences(ProfileInformation profileInfo, String checkSum) throws Exception {
        // Init
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spEdit = sharedPreferences.edit();
        // Further more, we would actually like to store the userid and
        // name
        spEdit.putString(Constants.SP_BL_PROFILE_EMAIL,
                postData[0].getValue());

        // Should we remember the password?
        if (savePassword) {
            spEdit.putString(Constants.SP_BL_PROFILE_PASSWORD, SimpleCrypto
                    .encrypt(postData[0].getValue(),
                            postData[1].getValue()));
            spEdit.putBoolean(Constants.SP_BL_PROFILE_REMEMBER, true);

        } else {

            spEdit.putString(Constants.SP_BL_PROFILE_PASSWORD, "");
            spEdit.putBoolean(Constants.SP_BL_PROFILE_REMEMBER, false);
        }

        // Init the strings
        StringBuilder personaNames = new StringBuilder();
        StringBuilder personaIds = new StringBuilder();
        StringBuilder platformIds = new StringBuilder();
        StringBuilder personaLogos = new StringBuilder();

        // Do it for the platoons too
        StringBuilder platoonIds = new StringBuilder();
        StringBuilder platoonNames = new StringBuilder();
        StringBuilder platoonTags = new StringBuilder();
        StringBuilder platoonPlatformIds = new StringBuilder();
        StringBuilder platoonImages = new StringBuilder();

        // We need to append the different parts to the ^ strings
        for (int i = 0, max = profileInfo.getNumPersonas(); i < max; i++) {

        	PersonaData persona = profileInfo.getPersona(i);
            personaNames.append(persona.getName() + ":");
            personaIds.append(persona.getId() + ":");
            platformIds.append(persona.getPlatformId() + ":");
            personaLogos.append(persona.getLogo() + ":");

        }

        // The platoons need to be "cacheable" too
        for (int i = 0, max = profileInfo.getNumPlatoons(); i < max; i++) {

        	PlatoonData platoon = profileInfo.getPlatoon(i);
            platoonIds.append(platoon.getId() + ":");
            platoonNames.append(platoon.getName() + ":");
            platoonTags.append(platoon.getTag() + ":");
            platoonPlatformIds.append(platoon.getPlatformId() + ":");
            platoonImages.append(platoon.getImage() + ":");

        }

        // This we keep!!!
        spEdit.putString(Constants.SP_BL_PROFILE_NAME, profileInfo.getUsername());
        spEdit.putString(Constants.SP_BL_PERSONA_NAME, personaNames.toString());
        spEdit.putLong(Constants.SP_BL_PROFILE_ID, profileInfo.getUserId());
        spEdit.putString(Constants.SP_BL_PERSONA_ID, personaIds.toString());
        spEdit.putString(Constants.SP_BL_PLATFORM_ID, platformIds.toString());
        spEdit.putString(Constants.SP_BL_PERSONA_LOGO, personaLogos.toString());
        spEdit.putString(Constants.SP_BL_PROFILE_CHECKSUM, checkSum);

        // Platoons too!
        spEdit.putString(Constants.SP_BL_PLATOON_ID, platoonIds.toString());
        spEdit.putString(Constants.SP_BL_PLATOON, platoonNames.toString());
        spEdit.putString(Constants.SP_BL_PLATOON_TAG, platoonTags.toString());
        spEdit.putString(Constants.SP_BL_PLATOON_PLATFORM_ID, platoonPlatformIds.toString());
        spEdit.putString(Constants.SP_BL_PLATOON_IMAGE, platoonImages.toString());

        // Cookie-related
        List<ShareableCookie> sca = RequestHandler.getCookies();
        if (sca == null) {

            throw new WebsiteHandlerException(
                    context.getString(R.string.info_login_lostcookie));

        } else {

            ShareableCookie sc = sca.get(0);
            spEdit.putString(Constants.SP_BL_COOKIE_NAME, sc.getName());
            spEdit.putString(Constants.SP_BL_COOKIE_VALUE, sc.getValue());

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

    private SessionKeeperPackage elementUidLinkError(String httpContent)
            throws WebsiteHandlerException {
        int startPosition;// Update the position
        startPosition = httpContent.indexOf(Constants.ELEMENT_ERROR_MESSAGE);

        // Is it -1 again?
        if (startPosition == -1) {

            throw new WebsiteHandlerException(
                    "The website won't let us in. Please try again later.");

        } else {

        	int endPosition = httpContent.indexOf("</div>");
            String errorMsg = httpContent.substring(startPosition, endPosition)
                    .replace("</div>", "")
                    .replace("\n", "")
                    .replace(Constants.ELEMENT_ERROR_MESSAGE, "");

            throw new WebsiteHandlerException(errorMsg);

        }
    }

    private String substringFrom(String content, String expression, String endsWith) {
        String result = content.substring(content
                .indexOf(expression));
        return result.substring(0,
                result.indexOf(endsWith)).replace(expression, "");
    }
}
