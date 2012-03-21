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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.battlelog.DashboardActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.SessionKeeperPackage;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncLogin extends AsyncTask<PostData, Integer, Boolean> {

    // Attribute
    private ProgressDialog progressDialog;
    private Context context;
    private AsyncLogin origin;
    private boolean savePassword;
    private SessionKeeperPackage sessionKeeperPackage;
    private String locale;
    private String errorMessage;

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

            sessionKeeperPackage = WebsiteHandler.doLogin(context, arg0, savePassword);

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

                            ).putExtra("myPlatoons", sessionKeeperPackage.getPlatoons())

                    );

            // Kill the main
            ((Activity) context).finish();

        } else {

            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                    .show();

        }
        return;

    }

}
