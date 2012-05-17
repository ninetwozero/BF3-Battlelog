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
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.handlers.PlatoonHandler;

public class AsyncPlatoonMemberInvite extends AsyncTask<String, Void, Integer> {

    // Attributes
    private Context context;
    private long platoonId;
    private Object[] userId;

    public AsyncPlatoonMemberInvite(Context c, Object[] uId, long pId) {

        context = c;
        userId = uId.clone();
        platoonId = pId;

    }

    @Override
    protected Integer doInBackground(String... arg0) {

        try {

            return PlatoonHandler.sendInvite(userId, platoonId, arg0[0]);

        } catch (Exception ex) {

            ex.printStackTrace();
            return -1;

        }

    }

    @Override
    protected void onPostExecute(Integer result) {

        if (context instanceof Activity) {

            switch (result) {

                case PlatoonHandler.STATE_OK:
                    Toast.makeText(context, R.string.info_platoon_invite_ok,
                            Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                    break;

                case PlatoonHandler.STATE_ERROR:
                    Toast.makeText(context, R.string.info_platoon_invite_passed,
                            Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                    break;

                case PlatoonHandler.STATE_FAIL:
                    Toast.makeText(context, R.string.info_platoon_invite_fail,
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(context, R.string.msg_error, Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                    break;

            }

        }

    }

}
