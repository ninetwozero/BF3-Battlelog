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

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.social.ChatActivity;
import com.ninetwozero.bf3droid.http.COMClient;

public class AsyncChatSend extends AsyncTask<String, Integer, Boolean> {

    // Attribute
    private Context mContext;
    private COMClient mComClient;
    
    // Constructor
    public AsyncChatSend(Context c, COMClient cc) {
        mContext = c;
        mComClient = cc;
    }

    @Override
    protected void onPreExecute() {
    	if( mContext instanceof ChatActivity) {
    		((ChatActivity) mContext).prePostMode();
    	}
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        try {
            return mComClient.sendMessage(arg0[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean results) {
    	
    	if( mContext instanceof ChatActivity ) {
    		((ChatActivity) mContext).postPostMode();
    	}

        Toast.makeText(
    		mContext, 
    		results ?
				R.string.msg_chat_ok :
				R.string.msg_chat_fail
			,
			Toast.LENGTH_SHORT
		).show();
    }
}
