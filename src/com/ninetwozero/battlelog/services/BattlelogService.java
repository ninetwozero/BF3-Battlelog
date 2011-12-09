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
	private static Timer serviceTimer = null;
	
	@Override
	public IBinder onBind(Intent intent) { return null; }
	
	@Override
	public void onCreate() {

		//SET THE SHARED PREFERENCES
		if( BattlelogService.sharedPreferences == null ) {
			
			BattlelogService.sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this ); 
			
		}
    	
	}

	public void start() {
		
		if( serviceTimer == null ) { 
			
			serviceTimer = new Timer();
			serviceTimer.scheduleAtFixedRate(
	    			
	    		new TimerTask() {
	    			
	    			@Override
	    			public void run() {
	
	    				new AsyncServiceTask(CONTEXT).execute(
	
	    					BattlelogService.sharedPreferences.getString( "battlelog_post_checksum", "" ),
	    					BattlelogService.sharedPreferences.getString( "origin_email", "" ),
	    					BattlelogService.sharedPreferences.getString( "origin_password", "" )
	    					
	    				);
	
	    				
	    			}
	    			
	    		}, 
	    		0, 
	    		BattlelogService.sharedPreferences.getInt( "battlelog_service_interval", Constants.MINUTE_IN_SECONDS )*1000
			);
		
		}
		
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	
		Log.d(Constants.DEBUG_TAG, "Service has reached onStartCommand()");
		start();
		return Service.START_STICKY;
		
	}
	
	@Override
	public void onDestroy() { BattlelogService.stop(); }
	
	@Override
	public void onStart(Intent intent, int startid) { Log.d(Constants.DEBUG_TAG, "Service started."); }
	
	/* SELF */
	public static Timer getServiceTimer() { return BattlelogService.serviceTimer; }
	
	public static void stop() { 
		
		Log.d(Constants.DEBUG_TAG, "Service stopped");
		if( BattlelogService.getServiceTimer() != null ){ 
			
			BattlelogService.getServiceTimer().cancel(); 
			serviceTimer = null;
					
		} 
		
	}

}