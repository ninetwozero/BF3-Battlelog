package com.ninetwozero.battlelog.services;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ninetwozero.battlelog.asynctasks.AsyncServiceTask;
import com.ninetwozero.battlelog.misc.Constants;

public class BattlelogService extends Service {
	
	//Attributes
	private final Context CONTEXT = this;
	private static SharedPreferences sharedPreferences;
	private static Timer serviceTimer = new Timer();
	
	@Override
	public IBinder onBind(Intent intent) { return null; }
	
	@Override
	public void onCreate() {

		//SET THE SHARED PREFERENCES
		if( BattlelogService.sharedPreferences == null ) {
			
			BattlelogService.sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this ); 
			
		}

		//TimerTask
    	start();
    	
	}

	public void start() {
		
		serviceTimer.scheduleAtFixedRate(
    			
    		new TimerTask() {
    			
    			@Override
    			public void run() {

    				new AsyncServiceTask(CONTEXT).execute(
    						
    					BattlelogService.sharedPreferences.getString( "battlelog_post_checksum", "" )
    					
    				);

    				
    			}
    			
    		}, 
    		0, 
    		BattlelogService.sharedPreferences.getInt( "battlelog_service_timer", Constants.HOUR_IN_SECONDS )*1000
		);
		
	}
		
	@Override
	public void onDestroy() { BattlelogService.stop(); }
	
	@Override
	public void onStart(Intent intent, int startid) { Log.d(Constants.DEBUG_TAG, "Service started."); }
	
	/* SELF */
	public static Timer getServiceTimer() { return BattlelogService.serviceTimer; }
	
	public static void stop() { 
		
		Log.d(Constants.DEBUG_TAG, "Service stopped");
		if( BattlelogService.getServiceTimer() != null ){ BattlelogService.getServiceTimer().cancel(); } 
	}

}