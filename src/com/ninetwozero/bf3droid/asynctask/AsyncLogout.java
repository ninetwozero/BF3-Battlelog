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

package com.ninetwozero.bf3droid.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.RequestHandlerException;
import com.ninetwozero.bf3droid.http.RequestHandler;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.SessionKeeper;

public class AsyncLogout extends AsyncTask<Void, Integer, Integer> {

    // Attribute
    private Context context;
    private String httpContent;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;
    private ProgressDialog progressDialog;

    // Constructor
    public AsyncLogout(Context c) {

        context = c;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        spEdit = sharedPreferences.edit();

    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.general_wait));
        progressDialog.setMessage(context.getString(R.string.msg_logout));
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    @Override
    protected Integer doInBackground(Void... arg0) {

        try {


            RequestHandler wh = new RequestHandler();
            httpContent = wh.get(Constants.URL_LOGOUT, 0);

            // Did we manage?
            if (httpContent != null && !httpContent.equals("")) {

                // Further more, we would actually like to store the userid and
                // name
                spEdit.putString(Constants.SP_BL_PERSONA_NAME, "");
                spEdit.putString(Constants.SP_BL_PERSONA_ID, "");
                spEdit.putString(Constants.SP_BL_PLATFORM_ID, "");
                spEdit.putString(Constants.SP_BL_PERSONA_LOGO, "");
                spEdit.putLong(Constants.SP_BL_PERSONA_CURRENT_ID, 0);
                spEdit.putInt(Constants.SP_BL_PERSONA_CURRENT_POS, 0);
                spEdit.putString(Constants.SP_BL_COOKIE_NAME, "");
                spEdit.putString(Constants.SP_BL_COOKIE_VALUE, "");

                // Co-co-co-commit
                spEdit.commit();
                SessionKeeper.setProfileData(null);

            }
            wh.close();

        } catch (RequestHandlerException ex) {

            ex.printStackTrace();
            return 1;
        }

        return 0;

    }

    @Override
    protected void onPostExecute(Integer results) {

        if (progressDialog != null && progressDialog.isShowing()) {

            progressDialog.dismiss();
            ((Activity) context).finish();

        }

    }

}
