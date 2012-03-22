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

import net.sf.andhsli.hotspotlogin.SimpleCrypto;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncSessionValidate extends AsyncTask<PostData, Integer, Boolean> {

    // Attribute
    private Context origin;
    private SharedPreferences sharedPreferences;

    // Constructor
    public AsyncSessionValidate(Context c, SharedPreferences sp) {

        origin = c;
        sharedPreferences = sp;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(PostData... arg0) {

        try {

            return WebsiteHandler.setActive();

        } catch (WebsiteHandlerException e) {

            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        if (!results) {

            // Get the e-mail
            String email = sharedPreferences.getString(Constants.SP_BL_EMAIL,
                    "");

            // Let's renew it
            try {

                new AsyncSessionRenew(origin).execute(

                        new PostData(Constants.FIELD_NAMES_LOGIN[0], email),
                        new PostData(

                                Constants.FIELD_NAMES_LOGIN[1], SimpleCrypto.decrypt(
                                        email, sharedPreferences.getString(
                                                Constants.SP_BL_PASSWORD, ""))

                        ), new PostData(Constants.FIELD_NAMES_LOGIN[2], ""),
                        new PostData(Constants.FIELD_NAMES_LOGIN[3],
                                Constants.FIELD_VALUES_LOGIN[3])

                        );

            } catch (Exception ex) {

                Toast.makeText(origin, ex.getMessage(), Toast.LENGTH_SHORT)
                        .show();

            }

        }

    }

}
