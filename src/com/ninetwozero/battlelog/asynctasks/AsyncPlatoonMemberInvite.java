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
import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class AsyncPlatoonMemberInvite extends AsyncTask<String, Void, Integer> {
	
	//Attributes
	private Context context;
	private long platoonId;
	private long[] userId;
	
	public AsyncPlatoonMemberInvite(Context c, long[] uId, long pId) {

		context = c;
		userId = uId;
		platoonId = pId;
		
	}
	
	@Override
	protected Integer doInBackground(String... arg0) {

		try {
				
			return WebsiteHandler.sendPlatoonInvite( userId, platoonId, arg0[0] );
			
		} catch( Exception ex ) {
		
			ex.printStackTrace();
			return -1;
			
		}		
		
	}
	
	@Override
	protected void onPostExecute( Integer result ) { 
	
		if( context instanceof Activity ) { 

			
			switch( result  ) { 

				case -1:
					Toast.makeText( context, "An error occured.", Toast.LENGTH_SHORT).show();
					((Activity) context).finish();
					break;

				case 0:
					Toast.makeText( context, "Invites have been sent!", Toast.LENGTH_SHORT).show();
					((Activity) context).finish();
					break;

				case 1:
					Toast.makeText( context, "Invites could not be sent!", Toast.LENGTH_SHORT).show();
					break;
					
				case 2:
					Toast.makeText( context, "Some invites have been sent, some not!", Toast.LENGTH_SHORT).show();
					break;
					
				default:
					((Activity) context).finish();
					break;
					
			}
			
		}	
		
	}
	
}
