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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ninetwozero.battlelog.Dashboard;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.RequestHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;


public class AsyncStatusUpdate extends AsyncTask<PostData, Integer, Integer> {

	//Attribute
	Context context;
	String httpContent;
	AsyncTask<Void, Void, Boolean> asyncTask;
	
	//Constructor
	public AsyncStatusUpdate( Context c, AsyncTask<Void, Void, Boolean> a ) { 
		
		this.context = c; 
		this.asyncTask = a;
	
	}	
	
	@Override
	protected void onPreExecute() {

		if( context instanceof Dashboard ) { 

			Toast.makeText( context, R.string.msg_status, Toast.LENGTH_SHORT).show();
			((Button) ((Activity)context).findViewById(R.id.button_status)).setEnabled(false);

		}
		
	}
	
	@Override
	protected Integer doInBackground( PostData... arg0 ) {
		
		try {
		
			//Let's login everybody!
			RequestHandler wh = new RequestHandler();
    		httpContent = wh.post( Constants.URL_STATUS_SEND, arg0, 0);
    		
    		//Did we manage?
    		if( httpContent != null && !httpContent.equals( "" ) ) {
    			
    			//Set the int
    			int startPosition = httpContent.indexOf( Constants.ELEMENT_STATUS_OK );
    			
    			//Did we find it?
    			if( startPosition == -1 ) {
    				
    				return 1;

    			}
				
    		}
    		
		} catch ( RequestHandlerException ex ) {
			
			ex.printStackTrace();
			return 1;
		
		} catch( Exception ex ) {
			
			ex.printStackTrace();
			return 1;
			
		}

		return 0;
		
	}
	
	@Override
	protected void onPostExecute(Integer results) {

		if( results == 0 ) { 
				
			//Yay
			Toast.makeText(this.context, R.string.msg_status_ok, Toast.LENGTH_SHORT).show(); 
			((EditText) ((Activity)context).findViewById(R.id.field_status)).setText("");
			((Button) ((Activity)context).findViewById(R.id.button_status)).setEnabled(true);
							
		} else { 
			
			Toast.makeText( this.context, R.string.msg_status_fail, Toast.LENGTH_SHORT).show(); 
			
		}
		
		//Do we need to do our AsyncTask?
		if( asyncTask != null ) { asyncTask.execute(); }

		return;
		
	}

	
	
}
