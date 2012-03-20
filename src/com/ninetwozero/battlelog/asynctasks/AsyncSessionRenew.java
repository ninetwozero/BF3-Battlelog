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

import com.ninetwozero.battlelog.Main;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;

public class AsyncSessionRenew extends AsyncTask<PostData, Integer, Boolean> {

    // Attribute
    private ProgressDialog progressDialog;
    private Context context;
    private AsyncSessionRenew origin;
    private ProfileData profile;

    // Constructor
    public AsyncSessionRenew(Context c) {

        origin = this;
        context = c;

    }

    @Override
    protected void onPreExecute() {

        // Let's see
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.general_wait));
        progressDialog.setMessage(context
                .getString(R.string.info_session_expired));
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

            // Let's try
            AsyncLogin login = new AsyncLogin(context, true);
            profile = login.sessionRenew(arg0);

            // Did it go ok?
            return (profile != null);

        } catch (WebsiteHandlerException e) {

            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        // Got a dialog?
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        // Did it fail? Let's tell the user
        if (results) {

            Toast.makeText(context, R.string.info_session_renew_ok,
                    Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(context, R.string.info_session_renew_fail,
                    Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, Main.class));
            ((Activity) context).finish();

        }
        return;

    }

}
