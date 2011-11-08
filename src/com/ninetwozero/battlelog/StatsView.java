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
package com.ninetwozero.battlelog;

import java.util.ArrayList;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.Config;
import com.ninetwozero.battlelog.datatypes.PlayerData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.SerializedCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class StatsView extends Activity {

	//SharedPreferences for shizzle
	SharedPreferences sharedPreferences;
	ProgressBar progressBar;
	GetDataSelfAsync getDataAsync;
	private static int instances = 0;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( "serializedCookies" ) ) {
    		
    		RequestHandler.setSerializedCookies( (ArrayList<SerializedCookie> ) icicle.getSerializable("serializedCookies") );
    	
    	}
    	
    	//Instances += 1
    	instances = 1;
    	
    	//Set the content view
        setContentView(R.layout.stats_view);

        //Prepare to tango
        this.sharedPreferences = this.getSharedPreferences( Config.fileSharedPrefs, 0);
    	if( instances == 1 ) { this.reloadLayout(); }
	}        

    public void reloadLayout() {
    	
    	//ASYNC!!!
    	new GetDataSelfAsync(this).execute(
    		
    		new ProfileData(
				this.sharedPreferences.getString( "battlelog_persona", "" ),
				this.sharedPreferences.getLong( "battlelog_persona_id", 0 ),
				this.sharedPreferences.getLong( "battlelog_persona_id", 0 ),
				this.sharedPreferences.getLong( "battlelog_platform_id", 1)
			)
		
		);
    	
    	
    }
    
    public void doFinish() {}
    
    private class GetDataSelfAsync extends AsyncTask<ProfileData, Void, Boolean> {
    
    	//Attributes
    	Context context;
    	ProgressDialog progressDialog;
    	PlayerData playerData;
    	
    	public GetDataSelfAsync(Context c) {
    		
    		this.context = c;
    		this.progressDialog = null;
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		
    		//Let's see
			this.progressDialog = new ProgressDialog(this.context);
			this.progressDialog.setTitle("Please wait");
			this.progressDialog.setMessage( "Downloading the data..." );
			this.progressDialog.show();
    		
    	}
    	

		@Override
		protected Boolean doInBackground( ProfileData... arg0 ) {
			
			try {
				
				this.playerData = WebsiteHandler.getStatsForUser( arg0[0] );
				return true;
				
			} catch ( WebsiteHandlerException ex ) {
				
				Log.d("com.ninetwozero.battlelog", ex.getMessage() );
				return false;
				
			}

		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		
			//Fail?
			if( !result ) { 
				
				if( this.progressDialog != null ) this.progressDialog.dismiss();
				Toast.makeText( this.context, "No data found.", Toast.LENGTH_SHORT).show(); 
				((Activity) this.context).finish();
				return; 
			
			}

			//Persona & rank
	        ((TextView) findViewById(R.id.string_persona)).setText( playerData.getPersonaName() );
	        ((TextView) findViewById(R.id.string_rank_title)).setText( playerData.getRankTitle() );
	        ((TextView) findViewById(R.id.string_rank_short)).setText( playerData.getRankId() + "" );
	        
	        //Progress
	        progressBar = ( (ProgressBar) findViewById(R.id.progress_level));
	        progressBar.setMax( (int) playerData.getPointsNeededToLvlUp()  );
	        progressBar.setProgress( (int) playerData.getPointsProgressLvl() );
	        ((TextView) findViewById(R.id.string_progress_curr)).setText( playerData.getPointsProgressLvl() + "" );
	        ((TextView) findViewById(R.id.string_progress_max)).setText( playerData.getPointsNeededToLvlUp() + "" );
	        ((TextView) findViewById(R.id.string_progress_left)).setText( playerData.getPointsLeft() + "" );
	        
	        //Score
	        ((TextView) findViewById(R.id.string_score_assault)).setText( playerData.getScoreAssault() + "" );
	        ((TextView) findViewById(R.id.string_score_engineer)).setText( playerData.getScoreEngineer() + "" );
	        ((TextView) findViewById(R.id.string_score_support)).setText( playerData.getScoreSupport() + "" );
	        ((TextView) findViewById(R.id.string_score_recon)).setText( playerData.getScoreRecon() + "" );
	        ((TextView) findViewById(R.id.string_score_vehicles)).setText( playerData.getScoreVehicle() + "" );
	        ((TextView) findViewById(R.id.string_score_combat)).setText( playerData.getScoreCombat() + "" );
	        ((TextView) findViewById(R.id.string_score_award)).setText( playerData.getScoreAwards() + "" );
	        ((TextView) findViewById(R.id.string_score_unlock)).setText( playerData.getScoreUnlocks() + "" );
	        ((TextView) findViewById(R.id.string_score_total)).setText( playerData.getScoreTotal() + "" );
	        
	        //Stats
	        ((TextView) findViewById(R.id.string_stats_kills)).setText( playerData.getNumKills() + "" );
	        ((TextView) findViewById(R.id.string_stats_assists)).setText( playerData.getNumAssists() + "" );
	        ((TextView) findViewById(R.id.string_stats_heals)).setText( playerData.getNumHeals() + "" );
	        ((TextView) findViewById(R.id.string_stats_revives)).setText( playerData.getNumRevives() + "" );
	        ((TextView) findViewById(R.id.string_stats_repairs)).setText( playerData.getNumRepairs() + "" );
	        ((TextView) findViewById(R.id.string_stats_resupplies)).setText( playerData.getNumResupplies() + "" );
	        ((TextView) findViewById(R.id.string_stats_deaths)).setText( playerData.getNumDeaths() + "" );
	        ((TextView) findViewById(R.id.string_stats_kdr)).setText( playerData.getKDRatio() + "" );
	        ((TextView) findViewById(R.id.string_stats_wins)).setText( playerData.getNumWins() + "" );
	        ((TextView) findViewById(R.id.string_stats_losses)).setText( playerData.getNumLosses() + "" );
	        ((TextView) findViewById(R.id.string_stats_wlr)).setText( playerData.getWLRatio() + "" );
	        ((TextView) findViewById(R.id.string_stats_accuracy)).setText( playerData.getAccuracy() + "%" );
	        ((TextView) findViewById(R.id.string_stats_time)).setText( playerData.getTimePlayedString() + "" );
	        ((TextView) findViewById(R.id.string_stats_spm)).setText( playerData.getScorePerMinute() + "" );
	        ((TextView) findViewById(R.id.string_stats_lks)).setText( playerData.getLongestKS() + "" );
	        ((TextView) findViewById(R.id.string_stats_lhs)).setText( playerData.getLongestHS() + " m");		
		
	        if( this.progressDialog != null ) this.progressDialog.dismiss();
			return;
		}
    	
    }
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_profileview, menu );
		return super.onCreateOptionsMenu( menu );
	
    }
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		//Let's act!
		if( item.getItemId() == R.id.option_reload ) {
	
			this.reloadLayout();
			
		} else if( item.getItemId() == R.id.option_back ) {
			
			((Activity) this).finish();
			
		}
		
		// Return true yo
		return true;

	}  
    
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
    }  
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putSerializable("serializedCookies", RequestHandler.getSerializedCookies());
	
	}
}