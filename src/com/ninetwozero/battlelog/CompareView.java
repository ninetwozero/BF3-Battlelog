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
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
	private SharedPreferences sharedPreferences;
	private ProgressBar progressBar;
	private GetDataSelfAsync getDataAsync;
	private ProfileData playerOne, playerTwo;
	private int selectedPosition;
	private long[] selectedPersona;
	private HashMap<Long, PersonaStats> playerData1, playerData2; 
	
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
    		
    		ArrayList<ShareableCookie> shareableCookies = icicle.getParcelableArrayList(Constants.SUPER_COOKIES);
			
    		if( shareableCookies != null ) { 
    			
    			RequestHandler.setCookies( shareableCookies );
    		
    		} else {
    			
    			finish();
    			
    		}
    		
    	}
    	
    	//Set the content view
        setContentView(R.layout.compare_view);

        //Prepare to tango
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    
        //Let's set them straight
        playerOne = (ProfileData) getIntent().getParcelableExtra( "profile1" );
        playerTwo = (ProfileData) getIntent().getParcelableExtra( "profile2" );
        selectedPersona = new long[2];
        selectedPosition = getIntent().getIntExtra( "selectedPosition", 0 );
        if( tvPersona[0] == null ) { grabAllViews(); }
        initLayout();
        
	}        

    public void initLayout() {
    	
    	//ASYNC!!!
    	new GetDataSelfAsync(this, selectedPosition).execute(
    		
    		playerOne,
    		playerTwo
		
		);
    	
    	
    }

    public void reloadLayout() {
    	
    	//ASYNC!!!
    	new GetDataSelfAsync(this, selectedPosition, selectedPersona ).execute(
    		
    		playerOne,
    		playerTwo
		
		);
    	
    	
    }

    
    public void doFinish() {}
    
    private class GetDataSelfAsync extends AsyncTask<ProfileData, Void, Boolean> {
    
    	//Attributes
    	private Context context;
    	private ProgressDialog progressDialog;/*TODO: WHY LOCAL? */
    	private ProfileData[] profileData;
    	private long[] profileIdArray;
    	private int arrayPosition;
    	
    	public GetDataSelfAsync(Context c, int ap) {
    		
    		context = c;
    		progressDialog = null;
    		if( playerData1 == null ) { playerData1 = new HashMap<Long, PersonaStats>(); }
    		if( playerData2 == null ) { playerData2 = new HashMap<Long, PersonaStats>(); }
        	profileIdArray = null;
        	arrayPosition = ap;
    	
    	}
    	
    	public GetDataSelfAsync(Context c, int ap, long[] pIds ) {
    		
    		this(c, ap);
        	profileIdArray = pIds;
        
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		
    		//Let's see
			progressDialog = new ProgressDialog(context);
			progressDialog.setTitle(context.getString( R.string.general_wait ));
			progressDialog.setMessage( context.getString( R.string.general_downloading ) );
			progressDialog.show();
    		
    	}
    	

		@Override
		protected Boolean doInBackground( ProfileData... arg0 ) {
			
			try {
				//Grab the profiles
				profileData = arg0;
				
				//Let's store them
				if( profileIdArray == null ) {
					
					profileIdArray = new long[] { 
							
						profileData[0].getPersonaId(), 
						profileData[1].getPersonaId(arrayPosition) 
						
					};
					
				} else if( profileIdArray[0] == 0 ) {
					
					profileIdArray[0] = profileData[0].getPersonaId(); 
					
				} else if( profileIdArray[1] == 0 ) { 
					
					profileIdArray[1] = profileData[1].getPersonaId(arrayPosition); 
					
				}
				
				//Grab the stats
				playerData1 = WebsiteHandler.getStatsForUser( context, profileData[0] );
				playerData2 = WebsiteHandler.getStatsForUser( context, profileData[1] );
				
				if( playerData1 != null && playerData2 != null ) { return true; }
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
				
				if( progressDialog != null ) { progressDialog.dismiss(); }
				Toast.makeText( context, R.string.general_no_data, Toast.LENGTH_SHORT).show(); 
				((Activity) context).finish();
				return; 
			
			}
			
			//Player One
			populateStats(playerData1.get( profileIdArray[0] ), 0);

			//Player Two	
			populateStats(playerData2.get( profileIdArray[1] ), 1);
			
	        //Remove the dialog?	        
			if( progressDialog != null ) { progressDialog.dismiss(); }
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
	
			reloadLayout();
			
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
			tvPersona[i] = (TextView) findViewById(fieldPersona[i]);
			tvRank[i] = (TextView) findViewById(fieldRank[i]);
	        
	        //Progress
			tvProgressCurr[i] = (TextView) findViewById(fieldProgressCurr[i]);
			tvProgressMax[i] = (TextView) findViewById(fieldProgressMax[i]);
	        
	        //Score
			tvScoreAssault[i] = (TextView) findViewById(fieldScoreAssault[i]);
			tvScoreEngineer[i] = (TextView) findViewById(fieldScoreEngineer[i]);
			tvScoreSupport[i] = (TextView) findViewById(fieldScoreSupport[i]);
			tvScoreRecon[i] = (TextView) findViewById(fieldScoreRecon[i]);
			tvScoreVehicles[i] = (TextView) findViewById(fieldScoreVehicles[i]);
			tvScoreCombat[i] = (TextView) findViewById(fieldScoreCombat[i]);
			tvScoreAward[i] = (TextView) findViewById(fieldScoreAward[i]);
			tvScoreUnlocks[i] = (TextView) findViewById(fieldScoreUnlocks[i]);
			tvScoreTotal[i] = (TextView) findViewById(fieldScoreTotal[i]);
	        
	        //Stats
			tvStatsKills[i] = (TextView) findViewById(fieldStatsKills[i]);
			tvStatsAssists[i] = (TextView) findViewById(fieldStatsAssists[i]);
			tvStatsVKills[i] = (TextView) findViewById(fieldStatsVKills[i]);
			tvStatsVAssists[i] = (TextView) findViewById(fieldStatsVAssists[i]);
			tvStatsHeals[i] = (TextView) findViewById(fieldStatsHeals[i]);
			tvStatsRevives[i] = (TextView) findViewById(fieldStatsRevives[i]);
			tvStatsRepairs[i] = (TextView) findViewById(fieldStatsRepairs[i]);
			tvStatsResupplies[i] = (TextView) findViewById(fieldStatsResupplies[i]);
			tvStatsDeath[i] = (TextView) findViewById(fieldStatsDeath[i]);
			tvStatsKDR[i] = (TextView) findViewById(fieldStatsKDR[i]);
			tvStatsWins[i] = (TextView) findViewById(fieldStatsWins[i]);
			tvStatsLosses[i] = (TextView) findViewById(fieldStatsLosses[i]);
			tvStatsWLR[i] = (TextView) findViewById(fieldStatsWLR[i]);
			tvStatsAccuracy[i] = (TextView) findViewById(fieldStatsAccuracy[i]);
			tvStatsTime[i] = (TextView) findViewById(fieldStatsTime[i]);
			tvStatsSkill[i] = (TextView) findViewById(fieldStatsSkill[i]);
			tvStatsSPM[i] = (TextView) findViewById(fieldStatsSPM[i]);
			tvStatsLKS[i] = (TextView) findViewById(fieldStatsLKS[i]);
			tvStatsLHS[i] = (TextView) findViewById(fieldStatsLHS[i]);
		
		}
	}
	
	public void populateStats(PersonaStats ps, int pos) {
	
		//If ps == null 
		if( ps == null ) { return; }
		
		//Persona & rank
		tvPersona[pos].setText( ps.getPersonaName().replaceAll( "(\\[^\\]]+)", "") );
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
	
	public Dialog generateDialogPersonaList( final Context context, final long[] personaId, final String[] persona, final int pos ) {
		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
	    //Set the title and the view
		builder.setTitle( R.string.info_dialog_soldierselect );
		
		builder.setSingleChoiceItems(
				
			persona, -1, new DialogInterface.OnClickListener() {
		  
				public void onClick(DialogInterface dialog, int item) {
			    	
					if( personaId[item] != selectedPersona[pos] ) {
						
						//Update it
						selectedPersona[pos] = personaId[item];
					
						//Load the new!
						if( pos == 0 ) {
							
							populateStats( playerData1.get( selectedPersona[pos] ), pos );
						
						} else {
							
							populateStats( playerData2.get( selectedPersona[pos] ), pos );
							
						}
						
						//Store selectedPersonaPos
						selectedPosition = item;
						
					}
					
					dialog.dismiss();
		
				}
				
			}
		
		);
		
		//CREATE
		return builder.create();
		
	}
	
	public void onClick(View v) {
	
		 if( v.getId() == R.id.string_persona_0 ) {
				
			//Let's get it on!
			generateDialogPersonaList(this, playerOne.getPersonaIdArray(), playerOne.getPersonaNameArray(), 0).show();
			
		} else if( v.getId() == R.id.string_persona_1 ) {
			
			generateDialogPersonaList(this, playerTwo.getPersonaIdArray(), playerTwo.getPersonaNameArray(), 1).show();
			
		}
		
	}
}