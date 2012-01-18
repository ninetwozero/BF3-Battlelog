package com.ninetwozero.battlelog.services;

import java.util.Timer;
import java.util.TimerTask;

import net.sf.andhsli.hotspotlogin.SimpleCrypto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.asynctasks.AsyncServiceTask;
import com.ninetwozero.battlelog.misc.Constants;

public class BattlelogService extends Service {
	
	//Attributes
	private static Context CONTEXT = null;
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
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	
		Log.d(Constants.DEBUG_TAG, "Service has reached onStartCommand()");
		start(this);
		return Service.START_STICKY;
		
	}
	
	@Override
	public void onDestroy() { BattlelogService.stop(); }
	
	@Override
	public void onStart(Intent intent, int startid) { Log.d(Constants.DEBUG_TAG, "Service started."); }
	
	/* SELF */
	public static Timer getServiceTimer() { return BattlelogService.serviceTimer; }

	public static void start(final Context context) {
		
		//Is the context null?
		if( CONTEXT == null ) { CONTEXT = context; }
		
		//Is the timre null?
		if( serviceTimer == null ) { 
		
			//SET THE SHARED PREFERENCES IF THEY'RE NOT SET
			if( BattlelogService.sharedPreferences == null ) {
				
				BattlelogService.sharedPreferences = PreferenceManager.getDefaultSharedPreferences( CONTEXT ); 
				
			}
			
			//Init a new Timer 
			serviceTimer = new Timer();
			serviceTimer.scheduleAtFixedRate(
	    			
	    		new TimerTask() {
	    			
	    			@Override
	    			public void run() {
	    				
	    				final String email = BattlelogService.sharedPreferences.getString( Constants.SP_BL_EMAIL, "" );
	    				try {
	    					
	    					new AsyncServiceTask(CONTEXT).execute(
		
		    					BattlelogService.sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ),
		    					email,
		    					SimpleCrypto.decrypt( email, BattlelogService.sharedPreferences.getString( Constants.SP_BL_PASSWORD, "" ) )
		    					
		    				);
	
	    				} catch( Exception ex ) {
	    					
	    					ex.printStackTrace();
	    					
	    				}
	    				
	    			}
	    			
	    		}, 
	    		0, 
	    		BattlelogService.sharedPreferences.getInt( Constants.SP_BL_INTERVAL_SERVICE, (Constants.HOUR_IN_SECONDS/2) )*1000
			);
		
		}
		
	}

	public static void restart() {
		
		BattlelogService.stop();
		BattlelogService.start( CONTEXT );
		
	}
	
	public static void stop() { 
		
		Log.d(Constants.DEBUG_TAG, "Service stopped");
		if( BattlelogService.getServiceTimer() != null ){ 
			
			BattlelogService.getServiceTimer().cancel(); 
			BattlelogService.serviceTimer = null;
					
		} 
		
	}
	
	public static boolean isRunning() { return (BattlelogService.getServiceTimer() != null ); }

}