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

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import net.sf.andhsli.hotspotlogin.SimpleCrypto;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.coveragemapper.android.Map.ExternalCacheDirectory;
import com.ninetwozero.battlelog.asynctasks.AsyncLogin;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.services.BattlelogService;

public class Main extends Activity {

	//Fields
	private EditText fieldEmail, fieldPassword;
	private CheckBox checkboxSave;
	private SlidingDrawer slidingDrawer;
	private TabHost cTabHost;
	private LayoutInflater layoutInflater;
	private OnDrawerOpenListener onDrawerOpenListener;
	private OnDrawerCloseListener onDrawerCloseListener;
	private TextView slidingDrawerHandle;
	
	//Values
	private String[] valueFields;
	private PostData[] postDataArray;
	
	//SP
	private SharedPreferences sharedPreferences;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
    	//onCreate - save the instance state
    	super.onCreate(savedInstanceState);
    	
    	//Set the content view
        setContentView(R.layout.main);
        
        //Does the cache-dir exist?
        try { 
        	
        	if( !ExternalCacheDirectory.getInstance( this ).getExternalCacheDirectory().exists() ) {
	        	
	        	Toast.makeText( this, R.string.info_general_nocache, Toast.LENGTH_SHORT).show();
	        
	        }
 
        } catch( Exception ex ) {
        	
        	ex.printStackTrace();
        	Toast.makeText( this, R.string.info_general_nocache, Toast.LENGTH_SHORT).show();
        	
        }
        
        //Are we active?
        if( PublicUtils.isMyServiceRunning( this ) && BattlelogService.isRunning() ) { 
        	
        	startActivity( 
        			
        		new Intent(this, Dashboard.class)
        		
        	);
        	
        	finish();
        	
        }
        
        //Check if the default-file is ok
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        if( sharedPreferences.getInt( Constants.SP_V_FILE, 0) == 0 ) {
        	
	        //Get the sharedPreferences
        	SharedPreferences sharedPreferencesOld = getSharedPreferences( Constants.FILE_SHPREF, 0 );
        	SharedPreferences.Editor spEdit = sharedPreferences.edit();
        	
        	//Set it up
        	spEdit.putInt( Constants.SP_V_FILE, sharedPreferencesOld.getInt( Constants.SP_V_FILE, 0 )+1 );
        	spEdit.putInt( Constants.SP_V_CHANGELOG, Integer.valueOf( String.valueOf( sharedPreferencesOld.getLong(Constants.SP_V_CHANGELOG, 0 ) ) ) );
        	spEdit.putLong( Constants.SP_BL_PLATFORM_ID, sharedPreferencesOld.getLong(Constants.SP_BL_PLATFORM_ID, 0 ) );
        	spEdit.putLong( Constants.SP_BL_PROFILE_ID, sharedPreferencesOld.getLong(Constants.SP_BL_PROFILE_ID, 0 ) );
        	spEdit.putLong( Constants.SP_BL_PERSONA_ID, sharedPreferencesOld.getLong(Constants.SP_BL_PERSONA_ID, 0 ) );
        	spEdit.putString( Constants.SP_BL_EMAIL, sharedPreferencesOld.getString(Constants.SP_BL_EMAIL, "" ) );
        	spEdit.putString( Constants.SP_BL_PASSWORD, sharedPreferencesOld.getString(Constants.SP_BL_PASSWORD, "" ) );
        	spEdit.putString( Constants.SP_BL_USERNAME, sharedPreferencesOld.getString(Constants.SP_BL_USERNAME, "" ) );
        	spEdit.putString( Constants.SP_BL_CHECKSUM, sharedPreferencesOld.getString(Constants.SP_BL_CHECKSUM, "" ) );
        	spEdit.putBoolean( Constants.SP_BL_REMEMBER, sharedPreferencesOld.getBoolean( Constants.SP_BL_REMEMBER, false ));
        	
        	//Commit!!
        	spEdit.commit();
        	
        }
        
        //Initialize the attributes
        postDataArray = new PostData[Constants.FIELD_NAMES_LOGIN.length];
        valueFields = new String[2];
        
        //Get the fields
        fieldEmail = (EditText) findViewById(R.id.field_email);
        fieldPassword = (EditText) findViewById(R.id.field_password);
        checkboxSave = (CheckBox) findViewById(R.id.checkbox_save);
        
        //Do we need to show the cool changelog-dialog?
        if( sharedPreferences.getInt( Constants.SP_V_CHANGELOG, Constants.CHANGELOG_VERSION-1) < Constants.CHANGELOG_VERSION ) {
        	
        	createChangelogDialog().show();
        	
        }

        //Let's populate... or shall we not?
        if( sharedPreferences.contains( Constants.SP_BL_EMAIL ) ) {
        	
        	//Set the e-mail field
        	fieldEmail.setText( sharedPreferences.getString( Constants.SP_BL_EMAIL, "") );

        	//Did the user want us to remember the password?
        	if( sharedPreferences.getBoolean( Constants.SP_BL_REMEMBER, false) ) {
        		
        		//Do we have a password stored?
        		if( !sharedPreferences.getString( Constants.SP_BL_PASSWORD, "").equals( "" ) ) {
	        		
	        		try {
	        		
	        			//Set the password (decrypted version)
						fieldPassword.setText( 
							SimpleCrypto.decrypt( 
								sharedPreferences.getString( 
									Constants.SP_BL_EMAIL, 
									"" 
								), 
								sharedPreferences.getString( 
									Constants.SP_BL_PASSWORD, 
									""
								)
							) 
						);
						
						checkboxSave.setChecked( true );
					
	        		} catch ( Exception e ) {
						
	        			e.printStackTrace();
					
	        		}
        		
        		}	
        	
        	}
        }
        
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cTabHost = (TabHost) findViewById(R.id.com_tabhost);
    	cTabHost.setup();

    	setupTabsSecondary(
    			
    		new String[] { getString(R.string.label_about), getString(R.string.label_faq), getString(R.string.label_credits) }, 
    		new int[] { R.layout.tab_content_main_about, R.layout.tab_content_main_faq, R.layout.tab_content_main_credits }
    		
    	);
    	
        /* TODO: SPLIT UP CODE ABOVE ^*/
        setupDrawer();
        
	}
	
	private void setupDrawer() {
		
        //Define the SlidingDrawer
		if( slidingDrawer == null ) {

			slidingDrawer = (SlidingDrawer) findViewById( R.id.about_slider);
			slidingDrawerHandle = (TextView) findViewById( R.id.about_slide_handle_text );
			
			//Set the drawer listeners
			onDrawerCloseListener = new OnDrawerCloseListener() {
	
				@Override
				public void onDrawerClosed() { slidingDrawer.setClickable( false ); }
			
			};
			onDrawerOpenListener = new OnDrawerOpenListener() { 
			
				@Override 
				public void onDrawerOpened() { slidingDrawer.setClickable( true ); } 
				
			};
			
			//Attach the listeners
			slidingDrawer.setOnDrawerOpenListener( onDrawerOpenListener );
			slidingDrawer.setOnDrawerCloseListener( onDrawerCloseListener );
			
		}
		
	}


	private void setupTabsSecondary( final String[] titleArray, final int[] layoutArray ) {

		//Init
    	TabHost.TabSpec spec;
    	
    	//Iterate them tabs
    	for(int i = 0, max = titleArray.length; i < max; i++) {

    		//Num
    		final int num = i;
			View tabview = createTabView(cTabHost.getContext(), titleArray[num]);
			
			//Let's set the content
			spec = cTabHost.newTabSpec(titleArray[num]).setIndicator(tabview).setContent(
	        		
	    		new TabContentFactory() {
	    			
	            	public View createTabContent(String tag) {
	            		
	            		return layoutInflater.inflate( layoutArray[num], null );
	    
	            	}
	            
	            }
	    		
	        );
			
			//Add the tab
			cTabHost.addTab( 
				
				spec 
				
			); 
    	
    	}
    	
    }
    
    public void onClick(View v) {
    	
    	if( v.getId() == R.id.button_login ) {
    		
    		//Let's set 'em values
    		valueFields[0] = fieldEmail.getText().toString();
    		valueFields[1] = fieldPassword.getText().toString();
    		
    		//Validate
    		if( valueFields[0].equals( "" ) || !valueFields[0].contains( "@" ) ) {
    			
    			Toast.makeText( this, R.string.general_invalid_email, Toast.LENGTH_SHORT).show();
    			return;
    			
    		} else if( valueFields[1].equals( "" ) ) {
    			
    			Toast.makeText( this, R.string.general_invalid_password, Toast.LENGTH_SHORT).show();
    			return;
    			
    		}
    		
    		//Iterate and conquer
    		for( int i = 0, max = Constants.FIELD_NAMES_LOGIN.length; i < max; i++ ) {

    			postDataArray[i] =	new PostData(
	    			
    				Constants.FIELD_NAMES_LOGIN[i],
	    			(Constants.FIELD_VALUES_LOGIN[i] == null) ? valueFields[i] : Constants.FIELD_VALUES_LOGIN[i] 
	    		
				);
    		
    		}
    		//Clear the pwd-field
    		if( !checkboxSave.isChecked() ) fieldPassword.setText( "" );
    		
    		//Do the async
    		if( PublicUtils.isNetworkAvailable( this ) ) { 
    			
    			AsyncLogin al = new AsyncLogin(this, checkboxSave.isChecked());
    			al.execute( postDataArray );
    		
    		} else {
    			
    			
    			Toast.makeText( this, "No network connection available!", Toast.LENGTH_SHORT ).show();
    			
    		}
    		
    		return;
    		
    	}
    	
    }
    
    public void onContactClick(View v) {
    	
    	if( v.getId() == R.id.wrap_web ) {
    		
    		startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ninetwozero.com") ) );
    		
    	} else if( v.getId() == R.id.wrap_twitter ) {
    		
    		startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/karllindmark") ) );
    		
    	} else if( v.getId() == R.id.wrap_email ) {
    		
    		startActivity( 
    				
    			Intent.createChooser(
    					
    				new Intent(Intent.ACTION_SENDTO).setData( 
    					
	    				Uri.parse( 
	    						
	    					"mailto:support@ninetwozero.com"
	    						
	    				) 
	    					
	    			),
	    			getString( R.string.info_txt_email_send )
	    			
	    		)
    			
    		);
		    
    	} else if( v.getId() == R.id.wrap_forum ) {

    		startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ninetwozero.com/forum") ) );
    		
    	} else if( v.getId() == R.id.wrap_xbox ) {
    		
    		startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse("http://live.xbox.com/en-US/Profile?gamertag=NINETWOZERO") ) );
    		
    	} else if( v.getId() == R.id.wrap_paypal ) {
    	
    		startActivity( 
    				
    			new Intent(
    					
    				Intent.ACTION_VIEW, Uri.parse( 
    					
	    				"https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=Y8GLB993JKTCL" 
	    					
	    			) 
    			) 
    		
    		);
    	
    	}
    	
    }
    
    public final Dialog createChangelogDialog() {
    		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	    final View layout = inflater.inflate(R.layout.changelog_dialog, (ViewGroup) findViewById(R.id.dialog_root));
		
	    //Set the title and the view
		builder.setTitle( getString( R.string.general_changelog_version ) + " 1.0." + Constants.CHANGELOG_VERSION);

		//Grab the fields
		final TextView textView = (TextView) layout.findViewById(R.id.text_changelog);
		textView.setText( Html.fromHtml( getString(R.string.changelog ) ) );
		
		//Set the button
		builder.setPositiveButton(
				
			android.R.string.ok, 
			new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
			      
					sharedPreferences.edit().putInt( Constants.SP_V_CHANGELOG, Constants.CHANGELOG_VERSION).commit();
			   
				}
				
			}
			
		);
    		
		//CREATE
		AlertDialog theDialog = builder.create();
		theDialog.setView( layout, 0, 0, 0, 0 );
		return theDialog;
    	
    }
    
    private final View createTabView(final Context context, final String text) {
    	
    	View view = LayoutInflater.from(context).inflate(R.layout.profile_tab_layout, null);
    	TextView tv = (TextView) view.findViewById(R.id.tabsText);
    	tv.setText(text);
    	return view;
    
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) { super.onConfigurationChanged(newConfig); }
    
    @Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {

		// Hotkeys
		if ( keyCode == KeyEvent.KEYCODE_BACK ) {
		
			if( slidingDrawer.isOpened() ) {
			
				slidingDrawer.animateClose();
				return true;
					
			}
			
		}
		return super.onKeyDown( keyCode, event );
	}
	
}