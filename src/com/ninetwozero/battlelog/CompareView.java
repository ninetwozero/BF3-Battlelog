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

import com.ninetwozero.battlelog.datatypes.PlayerData;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.SerializedCookie;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class CompareView extends Activity {

	//SharedPreferences for shizzle
	SharedPreferences sharedPreferences;
	ProgressBar progressBar;
	GetDataSelfAsync getDataAsync;
	ProfileData playerOne, playerTwo;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( "serializedCookies" ) ) {
    		
    		RequestHandler.setSerializedCookies( (ArrayList<SerializedCookie> ) icicle.getSerializable("serializedCookies") );
    	
    	}
    	
    	//Set the content view
        setContentView(R.layout.compare_view);

        //Prepare to tango
        this.sharedPreferences = this.getSharedPreferences( Constants.fileSharedPrefs, 0);
    
        //Let's set them straight
        playerOne = new ProfileData(

    		this.sharedPreferences.getString( "battlelog_username", "" ),
    		this.sharedPreferences.getString( "battlelog_persona", "" ),
			this.sharedPreferences.getLong( "battlelog_persona_id", 0 ),
			this.sharedPreferences.getLong( "battlelog_persona_id", 0 ),
			this.sharedPreferences.getLong( "battlelog_platform_id", 1)
		
		);
        playerTwo = (ProfileData) getIntent().getSerializableExtra( "profile" );
        
        this.reloadLayout();
        
	}        

    public void reloadLayout() {
    	
    	//ASYNC!!!
    	new GetDataSelfAsync(this).execute(
    		
    		playerOne,
    		playerTwo
		
		);
    	
    	
    }
    
    public void doFinish() {}
    
    private class GetDataSelfAsync extends AsyncTask<ProfileData, Void, Boolean> {
    
    	//Attributes
    	Context context;
    	ProgressDialog progressDialog;
    	PlayerData[] playerData;
    	
    	public GetDataSelfAsync(Context c) {
    		
    		this.context = c;
    		this.progressDialog = null;
    		this.playerData = new PlayerData[2];
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
				
				this.playerData[0] = WebsiteHandler.getStatsForUser( arg0[0] );
				this.playerData[1] = WebsiteHandler.getStatsForUser( arg0[1] );
				
				if( this.playerData[0] != null && this.playerData[1] != null ) { return true; }
				else { return false; }
				
			} catch ( WebsiteHandlerException ex ) {
				
				ex.printStackTrace();
				return false;
				
			}

		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		
			//Fail?
			if( !result ) { 
				
				if( this.progressDialog != null ) { this.progressDialog.dismiss(); }
				Toast.makeText( this.context, "No data found.", Toast.LENGTH_SHORT).show(); 
				((Activity) this.context).finish();
				return; 
			
			}
			
			/* Disclaimer: I feel bad about this copy&paste /K */
			
			//Player One
			
			//Persona & rank
	        ((TextView) findViewById(R.id.string_persona_0)).setText( playerData[0].getPersonaName() );
	        //((TextView) findViewById(R.id.string_rank_title)).setText( playerData[0].getRankTitle() );
	        ((TextView) findViewById(R.id.string_rank_short_0)).setText( playerData[0].getRankId() + "" );
	        
	        //Progress
	        ((TextView) findViewById(R.id.string_progress_curr_0)).setText( playerData[0].getPointsProgressLvl() + "" );
	        ((TextView) findViewById(R.id.string_progress_max_0)).setText( playerData[0].getPointsNeededToLvlUp() + "" );
	        
	        //Score
	        ((TextView) findViewById(R.id.string_score_assault_0)).setText( playerData[0].getScoreAssault() + "" );
	        ((TextView) findViewById(R.id.string_score_engineer_0)).setText( playerData[0].getScoreEngineer() + "" );
	        ((TextView) findViewById(R.id.string_score_support_0)).setText( playerData[0].getScoreSupport() + "" );
	        ((TextView) findViewById(R.id.string_score_recon_0)).setText( playerData[0].getScoreRecon() + "" );
	        ((TextView) findViewById(R.id.string_score_vehicles_0)).setText( playerData[0].getScoreVehicle() + "" );
	        ((TextView) findViewById(R.id.string_score_combat_0)).setText( playerData[0].getScoreCombat() + "" );
	        ((TextView) findViewById(R.id.string_score_award_0)).setText( playerData[0].getScoreAwards() + "" );
	        ((TextView) findViewById(R.id.string_score_unlock_0)).setText( playerData[0].getScoreUnlocks() + "" );
	        ((TextView) findViewById(R.id.string_score_total_0)).setText( playerData[0].getScoreTotal() + "" );
	        
	        //Stats
	        ((TextView) findViewById(R.id.string_stats_kills_0)).setText( playerData[0].getNumKills() + "" );
	        ((TextView) findViewById(R.id.string_stats_assists_0)).setText( playerData[0].getNumAssists() + "" );
	        ((TextView) findViewById(R.id.string_stats_vkills_0)).setText( playerData[0].getNumVehicles() + "" );
	        ((TextView) findViewById(R.id.string_stats_vassists_0)).setText( playerData[0].getNumVehicleAssists() + "" );
	        ((TextView) findViewById(R.id.string_stats_heals_0)).setText( playerData[0].getNumHeals() + "" );
	        ((TextView) findViewById(R.id.string_stats_revives_0)).setText( playerData[0].getNumRevives() + "" );
	        ((TextView) findViewById(R.id.string_stats_repairs_0)).setText( playerData[0].getNumRepairs() + "" );
	        ((TextView) findViewById(R.id.string_stats_resupplies_0)).setText( playerData[0].getNumResupplies() + "" );
	        ((TextView) findViewById(R.id.string_stats_deaths_0)).setText( playerData[0].getNumDeaths() + "" );
	        ((TextView) findViewById(R.id.string_stats_kdr_0)).setText( playerData[0].getKDRatio() + "" );
	        ((TextView) findViewById(R.id.string_stats_wins_0)).setText( playerData[0].getNumWins() + "" );
	        ((TextView) findViewById(R.id.string_stats_losses_0)).setText( playerData[0].getNumLosses() + "" );
	        ((TextView) findViewById(R.id.string_stats_wlr_0)).setText( playerData[0].getWLRatio() + "" );
	        ((TextView) findViewById(R.id.string_stats_accuracy_0)).setText( playerData[0].getAccuracy() + "%" );
	        ((TextView) findViewById(R.id.string_stats_time_0)).setText( playerData[0].getTimePlayedString() + "" );
	        ((TextView) findViewById(R.id.string_stats_skill_0)).setText( playerData[0].getSkill() + "" );
	        ((TextView) findViewById(R.id.string_stats_spm_0)).setText( playerData[0].getScorePerMinute() + "" );
	        ((TextView) findViewById(R.id.string_stats_lks_0)).setText( playerData[0].getLongestKS() + "" );
	        ((TextView) findViewById(R.id.string_stats_lhs_0)).setText( playerData[0].getLongestHS() + " m");		
		
	        //Player Two
	        
	        //Persona & rank
	        ((TextView) findViewById(R.id.string_persona_1)).setText( playerData[1].getPersonaName() );
	        //((TextView) findViewById(R.id.string_rank_title)).setText( playerData[1].getRankTitle() );
	        ((TextView) findViewById(R.id.string_rank_short_1)).setText( playerData[1].getRankId() + "" );
	        
	        //Progress
	        ((TextView) findViewById(R.id.string_progress_curr_1)).setText( playerData[1].getPointsProgressLvl() + "" );
	        ((TextView) findViewById(R.id.string_progress_max_1)).setText( playerData[1].getPointsNeededToLvlUp() + "" );
	        
	        //Score
	        ((TextView) findViewById(R.id.string_score_assault_1)).setText( playerData[1].getScoreAssault() + "" );
	        ((TextView) findViewById(R.id.string_score_engineer_1)).setText( playerData[1].getScoreEngineer() + "" );
	        ((TextView) findViewById(R.id.string_score_support_1)).setText( playerData[1].getScoreSupport() + "" );
	        ((TextView) findViewById(R.id.string_score_recon_1)).setText( playerData[1].getScoreRecon() + "" );
	        ((TextView) findViewById(R.id.string_score_vehicles_1)).setText( playerData[1].getScoreVehicle() + "" );
	        ((TextView) findViewById(R.id.string_score_combat_1)).setText( playerData[1].getScoreCombat() + "" );
	        ((TextView) findViewById(R.id.string_score_award_1)).setText( playerData[1].getScoreAwards() + "" );
	        ((TextView) findViewById(R.id.string_score_unlock_1)).setText( playerData[1].getScoreUnlocks() + "" );
	        ((TextView) findViewById(R.id.string_score_total_1)).setText( playerData[1].getScoreTotal() + "" );
	        
	        //Stats
	        ((TextView) findViewById(R.id.string_stats_kills_1)).setText( playerData[1].getNumKills() + "" );
	        ((TextView) findViewById(R.id.string_stats_assists_1)).setText( playerData[1].getNumAssists() + "" );
	        ((TextView) findViewById(R.id.string_stats_vkills_1)).setText( playerData[1].getNumVehicles() + "" );
	        ((TextView) findViewById(R.id.string_stats_vassists_1)).setText( playerData[1].getNumVehicleAssists() + "" );
	        ((TextView) findViewById(R.id.string_stats_heals_1)).setText( playerData[1].getNumHeals() + "" );
	        ((TextView) findViewById(R.id.string_stats_repairs_1)).setText( playerData[1].getNumRepairs() + "" );
	        ((TextView) findViewById(R.id.string_stats_resupplies_1)).setText( playerData[1].getNumResupplies() + "" );
	        ((TextView) findViewById(R.id.string_stats_revives_1)).setText( playerData[1].getNumRevives() + "" );
	        ((TextView) findViewById(R.id.string_stats_deaths_1)).setText( playerData[1].getNumDeaths() + "" );
	        ((TextView) findViewById(R.id.string_stats_kdr_1)).setText( playerData[1].getKDRatio() + "" );
	        ((TextView) findViewById(R.id.string_stats_wins_1)).setText( playerData[1].getNumWins() + "" );
	        ((TextView) findViewById(R.id.string_stats_losses_1)).setText( playerData[1].getNumLosses() + "" );
	        ((TextView) findViewById(R.id.string_stats_wlr_1)).setText( playerData[1].getWLRatio() + "" );
	        ((TextView) findViewById(R.id.string_stats_accuracy_1)).setText( playerData[1].getAccuracy() + "%" );
	        ((TextView) findViewById(R.id.string_stats_time_1)).setText( playerData[1].getTimePlayedString() + "" );
	        ((TextView) findViewById(R.id.string_stats_skill_1)).setText( playerData[1].getSkill() + "" );
	        ((TextView) findViewById(R.id.string_stats_spm_1)).setText( playerData[1].getScorePerMinute() + "" );
	        ((TextView) findViewById(R.id.string_stats_lks_1)).setText( playerData[1].getLongestKS() + "" );
	        ((TextView) findViewById(R.id.string_stats_lhs_1)).setText( playerData[1].getLongestHS() + " m");		
		
	        //Remove the dialog?	        
			if( this.progressDialog != null ) { this.progressDialog.dismiss(); }
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