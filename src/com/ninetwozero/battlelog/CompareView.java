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

import com.ninetwozero.battlelog.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.datatypes.PersonaStats;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
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
	
	//These are the different fields
	private final int fieldPersona[] = new int[] { R.id.string_persona_0, R.id.string_persona_1 };
	private final int fieldRank[] = new int[] { R.id.string_rank_short_0, R.id.string_rank_short_1 };
	private final int fieldProgressCurr[] = new int[] { R.id.string_progress_curr_0, R.id.string_progress_curr_1 };
	private final int fieldProgressMax[] = new int[] { R.id.string_progress_max_0, R.id.string_progress_max_1 };
	
	private final int fieldScoreAssault[] = new int[] { R.id.string_score_assault_0, R.id.string_score_assault_1 };
	private final int fieldScoreEngineer[] = new int[] { R.id.string_score_engineer_0, R.id.string_score_engineer_1 };
	private final int fieldScoreSupport[] = new int[] { R.id.string_score_support_0, R.id.string_score_support_1 };
	private final int fieldScoreRecon[] = new int[] { R.id.string_score_recon_0, R.id.string_score_recon_1 };
	private final int fieldScoreVehicles[] = new int[] { R.id.string_score_vehicles_0, R.id.string_score_vehicles_1 };
	private final int fieldScoreCombat[] = new int[] { R.id.string_score_combat_0, R.id.string_score_combat_1 };
	private final int fieldScoreAward[] = new int[] { R.id.string_score_award_0, R.id.string_score_award_1 };
	private final int fieldScoreUnlocks[] = new int[] { R.id.string_score_unlock_0, R.id.string_score_unlock_1 };
	private final int fieldScoreTotal[] = new int[] { R.id.string_score_total_0, R.id.string_score_total_1 };
    
	private final int fieldStatsKills[] = new int[] { R.id.string_stats_kills_0, R.id.string_stats_kills_1 };
	private final int fieldStatsAssists[] = new int[] { R.id.string_stats_assists_0, R.id.string_stats_assists_1 };
	private final int fieldStatsVKills[] = new int[] { R.id.string_stats_vkills_0, R.id.string_stats_vkills_1 };
	private final int fieldStatsVAssists[] = new int[] { R.id.string_stats_vassists_0, R.id.string_stats_vassists_1 };
	private final int fieldStatsHeals[] = new int[] { R.id.string_stats_heals_0, R.id.string_stats_heals_1 };
	private final int fieldStatsRevives[] = new int[] { R.id.string_stats_revives_0, R.id.string_stats_revives_1 };
	private final int fieldStatsRepairs[] = new int[] { R.id.string_stats_repairs_0, R.id.string_stats_repairs_1 };
	private final int fieldStatsResupplies[] = new int[] { R.id.string_stats_resupplies_0, R.id.string_stats_resupplies_1 };
	private final int fieldStatsDeath[] = new int[] { R.id.string_stats_deaths_0, R.id.string_stats_deaths_1 };
	private final int fieldStatsKDR[] = new int[] { R.id.string_stats_kdr_0, R.id.string_stats_kdr_1 };
	private final int fieldStatsWins[] = new int[] { R.id.string_stats_wins_0, R.id.string_stats_wins_1 };
	private final int fieldStatsLosses[] = new int[] { R.id.string_stats_losses_0, R.id.string_stats_losses_1 };
	private final int fieldStatsWLR[] = new int[] { R.id.string_stats_wlr_0, R.id.string_stats_wlr_1 };
	private final int fieldStatsAccuracy[] = new int[] { R.id.string_stats_accuracy_0, R.id.string_stats_accuracy_1 };
	private final int fieldStatsTime[] = new int[] { R.id.string_stats_time_0, R.id.string_stats_time_1 };
	private final int fieldStatsSkill[] = new int[] { R.id.string_stats_skill_0, R.id.string_stats_skill_1 };
	private final int fieldStatsSPM[] = new int[] { R.id.string_stats_spm_0, R.id.string_stats_spm_1 };
	private final int fieldStatsLKS[] = new int[] { R.id.string_stats_lks_0, R.id.string_stats_lks_1 };
	private final int fieldStatsLHS[] = new int[] { R.id.string_stats_lhs_0, R.id.string_stats_lhs_1 };
	
	//These are the different elements
	private TextView tvPersona[] = new TextView[2];
	private TextView tvRank[] = new TextView[2];
	private TextView tvProgressCurr[] = new TextView[2];
	private TextView tvProgressMax[] = new TextView[2];
	
	private TextView tvScoreAssault[] = new TextView[2];
	private TextView tvScoreEngineer[] = new TextView[2];
	private TextView tvScoreSupport[] = new TextView[2];
	private TextView tvScoreRecon[] = new TextView[2];
	private TextView tvScoreVehicles[] = new TextView[2];
	private TextView tvScoreCombat[] = new TextView[2];
	private TextView tvScoreAward[] = new TextView[2];
	private TextView tvScoreUnlocks[] = new TextView[2];
	private TextView tvScoreTotal[] = new TextView[2];
    
	private TextView tvStatsKills[] = new TextView[2];
	private TextView tvStatsAssists[] = new TextView[2];
	private TextView tvStatsVKills[] = new TextView[2];
	private TextView tvStatsVAssists[] = new TextView[2];
	private TextView tvStatsHeals[] = new TextView[2];
	private TextView tvStatsRevives[] = new TextView[2];
	private TextView tvStatsRepairs[] = new TextView[2];
	private TextView tvStatsResupplies[] = new TextView[2];
	private TextView tvStatsDeath[] = new TextView[2];
	private TextView tvStatsKDR[] = new TextView[2];
	private TextView tvStatsWins[] = new TextView[2];
	private TextView tvStatsLosses[] = new TextView[2];
	private TextView tvStatsWLR[] = new TextView[2];
	private TextView tvStatsAccuracy[] = new TextView[2];
	private TextView tvStatsTime[] = new TextView[2];
	private TextView tvStatsSkill[] = new TextView[2];
	private TextView tvStatsSPM[] = new TextView[2];
	private TextView tvStatsLKS[] = new TextView[2];
	private TextView tvStatsLHS[] = new TextView[2];
	
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		RequestHandler.setCookies( (ArrayList<ShareableCookie> ) icicle.getParcelable(Constants.SUPER_COOKIES) );
    	
    	}
    	
    	//Set the content view
        setContentView(R.layout.compare_view);

        //Prepare to tango
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    
        //Let's set them straight
        playerOne = new ProfileData(

    		this.sharedPreferences.getString( Constants.SP_BL_USERNAME, "" ),
    		this.sharedPreferences.getString( Constants.SP_BL_PERSONA, "" ),
			this.sharedPreferences.getLong( Constants.SP_BL_PERSONA_ID, 0 ),
			this.sharedPreferences.getLong( Constants.SP_BL_PERSONA_ID, 0 ),
			this.sharedPreferences.getLong( Constants.SP_BL_PLATFORM_ID, 1),
			this.sharedPreferences.getString( Constants.SP_BL_GRAVATAR, "" )
		
		);
        playerTwo = (ProfileData) getIntent().getParcelableExtra( "profile" );
        
        if( tvPersona[0] == null ) { this.grabAllViews(); }
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
    	PersonaStats[] playerData;
    	
    	public GetDataSelfAsync(Context c) {
    		
    		this.context = c;
    		this.progressDialog = null;
    		this.playerData = new PersonaStats[2];
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		
    		//Let's see
			this.progressDialog = new ProgressDialog(this.context);
			this.progressDialog.setTitle(context.getString( R.string.general_wait ));
			this.progressDialog.setMessage( context.getString( R.string.general_downloading ) );
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
				Toast.makeText( this.context, R.string.general_no_data, Toast.LENGTH_SHORT).show(); 
				((Activity) this.context).finish();
				return; 
			
			}
			
			//Player One
			populateStats(playerData[0], 0);

			//Player Two	
			populateStats(playerData[1], 1);
			
	        //Remove the dialog?	        
			if( this.progressDialog != null ) { this.progressDialog.dismiss(); }
			return;
		}
    	
    }
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_basic, menu );
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
		outState.putParcelableArrayList(Constants.SUPER_COOKIES, RequestHandler.getCookies());
	
	}
	
	public void grabAllViews() {
		
		//Loop over 'em
		for( int i = 0, max = tvPersona.length; i < max; i++ ) {

			//General
			this.tvPersona[i] = (TextView) findViewById(fieldPersona[i]);
			this.tvRank[i] = (TextView) findViewById(fieldRank[i]);
	        
	        //Progress
			this.tvProgressCurr[i] = (TextView) findViewById(fieldProgressCurr[i]);
			this.tvProgressMax[i] = (TextView) findViewById(fieldProgressMax[i]);
	        
	        //Score
			this.tvScoreAssault[i] = (TextView) findViewById(fieldScoreAssault[i]);
			this.tvScoreEngineer[i] = (TextView) findViewById(fieldScoreEngineer[i]);
			this.tvScoreSupport[i] = (TextView) findViewById(fieldScoreSupport[i]);
			this.tvScoreRecon[i] = (TextView) findViewById(fieldScoreRecon[i]);
			this.tvScoreVehicles[i] = (TextView) findViewById(fieldScoreVehicles[i]);
			this.tvScoreCombat[i] = (TextView) findViewById(fieldScoreCombat[i]);
			this.tvScoreAward[i] = (TextView) findViewById(fieldScoreAward[i]);
			this.tvScoreUnlocks[i] = (TextView) findViewById(fieldScoreUnlocks[i]);
			this.tvScoreTotal[i] = (TextView) findViewById(fieldScoreTotal[i]);
	        
	        //Stats
			this.tvStatsKills[i] = (TextView) findViewById(fieldStatsKills[i]);
			this.tvStatsAssists[i] = (TextView) findViewById(fieldStatsAssists[i]);
			this.tvStatsVKills[i] = (TextView) findViewById(fieldStatsVKills[i]);
			this.tvStatsVAssists[i] = (TextView) findViewById(fieldStatsVAssists[i]);
			this.tvStatsHeals[i] = (TextView) findViewById(fieldStatsHeals[i]);
			this.tvStatsRevives[i] = (TextView) findViewById(fieldStatsRevives[i]);
			this.tvStatsRepairs[i] = (TextView) findViewById(fieldStatsRepairs[i]);
			this.tvStatsResupplies[i] = (TextView) findViewById(fieldStatsResupplies[i]);
			this.tvStatsDeath[i] = (TextView) findViewById(fieldStatsDeath[i]);
			this.tvStatsKDR[i] = (TextView) findViewById(fieldStatsKDR[i]);
			this.tvStatsWins[i] = (TextView) findViewById(fieldStatsWins[i]);
			this.tvStatsLosses[i] = (TextView) findViewById(fieldStatsLosses[i]);
			this.tvStatsWLR[i] = (TextView) findViewById(fieldStatsWLR[i]);
			this.tvStatsAccuracy[i] = (TextView) findViewById(fieldStatsAccuracy[i]);
			this.tvStatsTime[i] = (TextView) findViewById(fieldStatsTime[i]);
			this.tvStatsSkill[i] = (TextView) findViewById(fieldStatsSkill[i]);
			this.tvStatsSPM[i] = (TextView) findViewById(fieldStatsSPM[i]);
			this.tvStatsLKS[i] = (TextView) findViewById(fieldStatsLKS[i]);
			this.tvStatsLHS[i] = (TextView) findViewById(fieldStatsLHS[i]);
		
		}
	}
	
	public void populateStats(PersonaStats ps, int pos) {
	
		//Persona & rank
		tvPersona[pos].setText( ps.getPersonaName() );
        tvRank[pos].setText( ps.getRankId() + "" );
        
        //Progress
        tvProgressCurr[pos].setText( ps.getPointsProgressLvl() + "" );
        tvProgressMax[pos].setText( ps.getPointsNeededToLvlUp() + "" );
        
        //Score
        tvScoreAssault[pos].setText( ps.getScoreAssault() + "" );
        tvScoreEngineer[pos].setText( ps.getScoreEngineer() + "" );
        tvScoreSupport[pos].setText( ps.getScoreSupport() + "" );
        tvScoreRecon[pos].setText( ps.getScoreRecon() + "" );
        tvScoreVehicles[pos].setText( ps.getScoreVehicle() + "" );
        tvScoreCombat[pos].setText( ps.getScoreCombat() + "" );
        tvScoreAward[pos].setText( ps.getScoreAwards() + "" );
        tvScoreUnlocks[pos].setText( ps.getScoreUnlocks() + "" );
        tvScoreTotal[pos].setText( ps.getScoreTotal() + "" );
        
        //Stats
        tvStatsKills[pos].setText( ps.getNumKills() + "" );
        tvStatsAssists[pos].setText( ps.getNumAssists() + "" );
        tvStatsVKills[pos].setText( ps.getNumVehicles() + "" );
        tvStatsVAssists[pos].setText( ps.getNumVehicleAssists() + "" );
        tvStatsHeals[pos].setText( ps.getNumHeals() + "" );
        tvStatsRevives[pos].setText( ps.getNumRevives() + "" );
        tvStatsRepairs[pos].setText( ps.getNumRepairs() + "" );
        tvStatsResupplies[pos].setText( ps.getNumResupplies() + "" );
        tvStatsDeath[pos].setText( ps.getNumDeaths() + "" );
        tvStatsKDR[pos].setText( ps.getKDRatio() + "" );
        tvStatsWins[pos].setText( ps.getNumWins() + "" );
        tvStatsLosses[pos].setText( ps.getNumLosses() + "" );
        tvStatsWLR[pos].setText( ps.getWLRatio() + "" );
        tvStatsAccuracy[pos].setText( ps.getAccuracy() + "%" );
        tvStatsTime[pos].setText( ps.getTimePlayedString() + "" );
        tvStatsSkill[pos].setText( ps.getSkill() + "" );
        tvStatsSPM[pos].setText( ps.getScorePerMinute() + "" );
        tvStatsLKS[pos].setText( ps.getLongestKS() + "" );
        tvStatsLHS[pos].setText( ps.getLongestHS() + " m");		
	
	}
}