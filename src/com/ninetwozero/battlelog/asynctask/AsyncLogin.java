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
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.DashboardActivity;
import com.ninetwozero.battlelog.datatype.*;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.http.RequestHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.service.BattlelogService;

import java.util.List;

public class AsyncLogin extends AsyncTask<PostData, Integer, Boolean> {

    // Attribute
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private AsyncLogin mOrigin;
    private SharedPreferences mSharedPreferences;
    private SessionKeeperPackage mSessionKeeperPackage;
    private String mLocale;
    private String mErrorMessage;
    private PostData[] mPostData;

    // Constructor
    public AsyncLogin(Context c) {
        mOrigin = this;
        mContext = c;
    }

    @Override
    protected void onPreExecute() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(mContext.getString(R.string.general_wait));
        mProgressDialog.setMessage(mContext.getString(R.string.msg_logging_in));
        mProgressDialog.setOnCancelListener(
            new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mOrigin.cancel(true);
                    dialog.dismiss();
                }
            }
        );
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values[0].equals(1)) {
            mProgressDialog.setMessage("Fetching information (todo)...");
        }
    }

    @Override
    protected Boolean doInBackground(PostData... arg0) {
        try {
            ProfileInformation profileInformation = doLogin(arg0);
            ProfileData profileData = new ProfileData.Builder(profileInformation.getUserId(), profileInformation.getUsername())
                    .persona(profileInformation.getAllPersonas())
                    .build();
            mSessionKeeperPackage = new SessionKeeperPackage(profileData, profileInformation.getPlatoons());

            // Preloading...
            /* TODO: Pre-download the profile + active platoon? */
            publishProgress(1);
            SystemClock.sleep(2000);

            return (mSessionKeeperPackage != null);

        } catch (WebsiteHandlerException ex) {
            mErrorMessage = ex.getMessage();
            return false;
        } catch (Exception ex) {
            mErrorMessage = mContext.getString(R.string.general_no_data);
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        if (results) {
            // Do we want to start a service?
            int serviceInterval = mSharedPreferences.getInt(
                    Constants.SP_BL_INTERVAL_SERVICE,
                    (Constants.HOUR_IN_SECONDS / 2)) * 1000;
            startAlarmManager(serviceInterval);

            // Start new
            mContext.startActivity(

                    new Intent(mContext,
                            DashboardActivity.class)
                            .putExtra("myProfile", mSessionKeeperPackage.getProfileData())
                            .putExtra("myLocale", mLocale)
                            .putExtra("myPlatoon", mSessionKeeperPackage.getPlatoons())
            );
            ((Activity) mContext).finish();
        } else {
            Toast.makeText(mContext, mErrorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public SessionKeeperPackage renewSession(PostData[] postData) throws WebsiteHandlerException,
            RequestHandlerException {
        ProfileInformation information = doLogin(postData);
        ProfileData profileData = new ProfileData.Builder(information.getUserId(), information.getUsername())
                .persona(information.getAllPersonas())
                .build();
        return new SessionKeeperPackage(profileData, information.getPlatoons());
    }

    // Let's have this one ready
    private ProfileInformation doLogin(PostData[] postData) throws WebsiteHandlerException {
        this.mPostData = postData.clone();
        try {
            RequestHandler wh = new RequestHandler();
            String httpContent = wh.post(Constants.URL_LOGIN, this.mPostData, 0);
            return httpContent.length() > 0 ? fetchProfileInformation(httpContent) : null;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebsiteHandlerException(ex.getMessage());
        }
    }

    private ProfileInformation fetchProfileInformation(String httpContent) throws Exception {
        // Set the int
        int startPosition = httpContent.indexOf(Constants.ELEMENT_UID_LINK);
        return startPosition == -1 ? elementUidLinkError(httpContent)
                : processHttpContent(httpContent);
    }

    private ProfileInformation processHttpContent(String httpContent) throws Exception {
        // Get the checksum & soldier name from the HTML
        String postCheckSum = substringFrom(httpContent, Constants.ELEMENT_STATUS_CHECKSUM, "\" />");
        String soldierName = substringFrom(httpContent, Constants.ELEMENT_USERNAME_LINK, "</div>").trim();
        
        // Fetch some profile information & store it in SharedPreferences
        ProfileInformation profileInformation = new ProfileClient(new ProfileData(soldierName)).getInformation(mContext, 0);
        addToSharedPreferences(profileInformation, postCheckSum);
        return profileInformation;
    }

    private SharedPreferences addToSharedPreferences(ProfileInformation profileInfo, String checkSum) throws Exception {
        // Init
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor spEdit = sharedPreferences.edit();
        
        // Further more, we would actually like to store the userid and name
        spEdit.putString(Constants.SP_BL_PROFILE_EMAIL,mPostData[0].getValue());
        spEdit.putString(Constants.SP_BL_PROFILE_PASSWORD, "");
        spEdit.putBoolean(Constants.SP_BL_PROFILE_REMEMBER, false);

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
            throw new WebsiteHandlerException(mContext.getString(R.string.info_login_lostcookie));
        } else {
            ShareableCookie sc = sca.get(0);
            spEdit.putString(Constants.SP_BL_COOKIE_NAME, sc.getName());
            spEdit.putString(Constants.SP_BL_COOKIE_VALUE, sc.getValue());
        }
        spEdit.commit();
        return sharedPreferences;
    }

    private void startAlarmManager(int serviceInterval) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(
    		AlarmManager.ELAPSED_REALTIME, 
    		0, 
    		serviceInterval, 
    		PendingIntent.getService(mContext, 0, new Intent(mContext, BattlelogService.class), 0)
        );
    }

    private ProfileInformation elementUidLinkError(String httpContent)
            throws WebsiteHandlerException {
        int startPosition;// Update the position
        startPosition = httpContent.indexOf(Constants.ELEMENT_ERROR_MESSAGE);

        // Is it -1 again?
        if (startPosition == -1) {
            throw new WebsiteHandlerException(
                    "The website won't let us in. Please try again later.");
        } else {
        	int endPosition = httpContent.indexOf("</div>", startPosition);
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
