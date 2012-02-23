package com.ninetwozero.battlelog.services;

import java.util.Timer;

import net.sf.andhsli.hotspotlogin.SimpleCrypto;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ninetwozero.battlelog.Dashboard;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.asynctasks.AsyncServiceTask;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class BattlelogService extends Service {
	
	//Attributes
	private SharedPreferences sharedPreferences;
	
	@Override
	public IBinder onBind(Intent intent) { return null; }
	
	@Override
	public void onCreate() {

		//SET THE SHARED PREFERENCES
		if( sharedPreferences == null ) {
			
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this ); 
			
		}   
	
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	
		Log.d(Constants.DEBUG_TAG, "Service has reached onStartCommand()");
		new AsyncServiceTask(this, sharedPreferences).execute();
		return Service.START_STICKY;
		
	}
	
	@Override
	public void onStart(Intent intent, int startid) { Log.d(Constants.DEBUG_TAG, "Service started."); }
	
}