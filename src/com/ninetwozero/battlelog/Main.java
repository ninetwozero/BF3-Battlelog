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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
import android.util.Log;
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
import com.ninetwozero.battlelog.asynctasks.AsyncSessionSetActive;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;

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
        cacheDirCheck();

        //Check if the default-file is ok
        defaultFileCheck();

        //Are we active?
        createSession();

        //Initialize the attributes
        postDataArray = new PostData[Constants.FIELD_NAMES_LOGIN.length];
        valueFields = new String[2];

        //Do we need to show the cool changelog-dialog?
        changeLogDialog();

        //Let's populate... or shall we not?
        populateFields();

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cTabHost = (TabHost) findViewById(R.id.com_tabhost);
        cTabHost.setup();

        setupTabsSecondary(
                new String[]{getString(R.string.label_about), getString(R.string.label_faq), getString(R.string.label_credits)},
                new int[]{R.layout.tab_content_main_about, R.layout.tab_content_main_faq, R.layout.tab_content_main_credits}
        );

        setupDrawer();

    }

    private void changeLogDialog() {
    	
        if (sharedPreferences.getInt(Constants.SP_V_CHANGELOG, Constants.CHANGELOG_VERSION - 1) < Constants.CHANGELOG_VERSION) {
            createChangelogDialog().show();
        }
        
    }

    private void populateFields() {
    	
        //Get the fields
        fieldEmail = (EditText) findViewById(R.id.field_email);
        fieldPassword = (EditText) findViewById(R.id.field_password);
        checkboxSave = (CheckBox) findViewById(R.id.checkbox_save);
        emailPasswordValues();
        
    }

    private void emailPasswordValues() {
    	
        setEmail();
        setCheckbox();
        setPassword();
        
    }

    private void setEmail() {
    	
        if ( hasEmail() ) {
            fieldEmail.setText(sharedPreferences.getString(Constants.SP_BL_EMAIL, ""));
        }
        
    }

    private boolean hasEmail() {
    	
        return sharedPreferences.contains(Constants.SP_BL_EMAIL);
   
    }
    
    private void setCheckbox(){
    	
        if(hasEmail()){
            checkboxSave.setChecked(isPasswordRemembered());
        }
        
    }

    private boolean isPasswordRemembered() {
    	
        return sharedPreferences.getBoolean(Constants.SP_BL_REMEMBER, false);
   
    }

    private void setPassword() {
    	
        if (hasEmail() && isPasswordRemembered()) {
        	
            //Do we have a password stored?
            if (hasPassword()) {
            	
                try {
                	
                    //Set the password (decrypted version)
                    fieldPassword.setText(
                        SimpleCrypto.decrypt(
                            sharedPreferences.getString(Constants.SP_BL_EMAIL, ""),
                            sharedPreferences.getString(Constants.SP_BL_PASSWORD, "")
                        )
                    );
                    
                } catch (Exception e) {
                	
                    e.printStackTrace();
                    
                }
                
            }
            
        }
        
    }

    private boolean hasPassword() { return !sharedPreferences.getString(Constants.SP_BL_PASSWORD, "").equals(""); }

    private void createSession() {
    	
        if (SessionKeeper.getProfileData() != null) {
           
        	startActivity(new Intent(this, Dashboard.class));
            finish();

        } else if (!sharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE, "").equals("")) {

        	RequestHandler.setCookies(
        			
                new ShareableCookie(
                		
                    sharedPreferences.getString(Constants.SP_BL_COOKIE_NAME, ""),
                    sharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE, ""),
                    Constants.COOKIE_DOMAIN
                    
                )
                
            );
            startActivity(
            		
            	new Intent(this, Dashboard.class).putExtra(
                		
                    "myProfile",
                    SessionKeeper.generateProfileDataFromSharedPreferences(sharedPreferences)
                )
                
            );
            finish();
            
        }
        
    }

    private void defaultFileCheck() {
    	
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getInt(Constants.SP_V_FILE, 0) < Constants.CHANGELOG_VERSION) {
        	
            //Get the sharedPreferences
            SharedPreferences.Editor spEdit = sharedPreferences.edit();
            
            //Set it up
            spEdit.putInt(Constants.SP_V_FILE, Constants.CHANGELOG_VERSION);
            spEdit.remove(Constants.SP_BL_PERSONA_ID);
            spEdit.remove(Constants.SP_BL_PLATFORM_ID);
            
            //Commit!!
            spEdit.commit();
        }
        
    }

    private void cacheDirCheck() {
    	
        try {
        	
            if (!ExternalCacheDirectory.getInstance(this).getExternalCacheDirectory().exists()) {
               
            	Toast.makeText(this, R.string.info_general_nocache, Toast.LENGTH_SHORT).show();
            
            }
            
        } catch (Exception ex) {
        	
            ex.printStackTrace();
            Toast.makeText(this, R.string.info_general_nocache, Toast.LENGTH_SHORT).show();
        
        }
        
    }

    private void setupDrawer() {
    	
        //Define the SlidingDrawer
        if (slidingDrawer == null) {
        	
            slidingDrawer = (SlidingDrawer) findViewById(R.id.about_slider);
            slidingDrawerHandle = (TextView) findViewById(R.id.about_slide_handle_text);
            
            //Set the drawer listeners
            onDrawerCloseListener = new OnDrawerCloseListener() {
            	
                @Override
                public void onDrawerClosed() { slidingDrawer.setClickable(false); }
                
            };
            onDrawerOpenListener = new OnDrawerOpenListener() {
            	
                @Override
                public void onDrawerOpened() { slidingDrawer.setClickable(true); }
                
            };

            //Attach the listeners
            slidingDrawer.setOnDrawerOpenListener(onDrawerOpenListener);
            slidingDrawer.setOnDrawerCloseListener(onDrawerCloseListener);
        }
    }


    private void setupTabsSecondary(final String[] titleArray, final int[] layoutArray) {
        //Init
        TabHost.TabSpec spec;

        //Iterate them tabs
        for (int i = 0, max = titleArray.length; i < max; i++) {
        	
            //Num
            final int num = i;
            View tabview = createTabView(cTabHost.getContext(), titleArray[num]);

            //Let's set the content
            spec = cTabHost.newTabSpec(titleArray[num]).setIndicator(tabview).setContent(
            		
                new TabContentFactory() {
                    
                	public View createTabContent(String tag) { return layoutInflater.inflate(layoutArray[num], null); }
                
                }
                
            );

            //Add the tab
            cTabHost.addTab(spec);
        }
    }

    //TODO refactor this method and use more reliable check such as http://stackoverflow.com/questions/6119722/how-to-check-edittexts-text-is-email-address-or-not
    public void onClick(View v) {
    	
        if (v.getId() == R.id.button_login) {
        	
            //Let's set 'em values
            valueFields[0] = fieldEmail.getText().toString();
            valueFields[1] = fieldPassword.getText().toString();
            if (!validateEmailAndPassword(valueFields[0], valueFields[1])) return;

            //Iterate and conquer
            for (int i = 0, max = Constants.FIELD_NAMES_LOGIN.length; i < max; i++) {
            	
                postDataArray[i] = new PostData(
                        Constants.FIELD_NAMES_LOGIN[i],
                        (Constants.FIELD_VALUES_LOGIN[i] == null) ? valueFields[i] : Constants.FIELD_VALUES_LOGIN[i]
                );
                
            }
            
            //Clear the pwd-field
            if (!checkboxSave.isChecked()) fieldPassword.setText("");

            //Do the async
            if (PublicUtils.isNetworkAvailable(this)) {
            	
                AsyncLogin al = new AsyncLogin(this, checkboxSave.isChecked());
                al.execute(postDataArray);

            } else {
            	
                Toast.makeText(this, R.string.general_nonetwork, Toast.LENGTH_SHORT).show();
           
            }
            return;
        }
        
    }

    private boolean validateEmailAndPassword(String email, String password) {
    	
        if (email.equals("") || !email.contains("@")) {
        	
            Toast.makeText(this, R.string.general_invalid_email, Toast.LENGTH_SHORT).show();
            return false;
            
        } else if (password.equals("")) {
        	
            Toast.makeText(this, R.string.general_invalid_password, Toast.LENGTH_SHORT).show();
            return false;
            
        }
        return true;
    }

    public void onContactClick(View v) {
    	
        final Map<Integer, Intent> SOCIAL_INTENTS = new HashMap<Integer, Intent>() {
        	
        	{
        	
	            put(R.id.wrap_web, new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ninetwozero.com")));
	            put(R.id.wrap_twitter, new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/karllindmark")));
	            put(R.id.wrap_email, Intent.createChooser(
                    new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@ninetwozero.com")), getString(R.string.info_txt_email_send)
	            ));
	
	            put(R.id.wrap_forum, new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ninetwozero.com/forum")));
	            put(R.id.wrap_xbox, new Intent(Intent.ACTION_VIEW, Uri.parse("http://live.xbox.com/en-US/Profile?gamertag=NINETWOZERO")));
	            put(R.id.wrap_paypal,
                    new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=Y8GLB993JKTCL"))
        		);
        	}
        };
        
        //Is it in the HashMap?
        if (SOCIAL_INTENTS.containsKey(v.getId())) { startActivity(SOCIAL_INTENTS.get(v.getId())); }
    }

    public final Dialog createChangelogDialog() {
    	
        //Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.changelog_dialog, (ViewGroup) findViewById(R.id.dialog_root));

        //Set the title and the view
        builder.setTitle(getString(R.string.general_changelog_version) + " 1.0." + Constants.CHANGELOG_VERSION);

        //Grab the fields
        final TextView textView = (TextView) layout.findViewById(R.id.text_changelog);
        textView.setText(Html.fromHtml(getString(R.string.changelog)));

        //Set the button
        builder.setPositiveButton(
        		
            android.R.string.ok,
            new DialogInterface.OnClickListener() {
                
            	public void onClick(DialogInterface dialog, int which) {
                  
                	sharedPreferences.edit().putInt(Constants.SP_V_CHANGELOG, Constants.CHANGELOG_VERSION).commit();
                
                }
                
            }
            
        );

        //CREATE
        AlertDialog theDialog = builder.create();
        theDialog.setView(layout, 0, 0, 0, 0);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	
            if (slidingDrawer.isOpened()) {
            	
                slidingDrawer.animateClose();
                return true;
                
            }
            
        }
        return super.onKeyDown(keyCode, event);
        
    }

    @Override
    public void onResume() {

    	super.onResume();

    	//Setup the locale
    	if( !sharedPreferences.getString( Constants.SP_BL_LANG, "" ).equals( "" ) ) {

    		Locale locale = new Locale( sharedPreferences.getString( Constants.SP_BL_LANG, "en" ) );
	    	Locale.setDefault(locale);
	    	Configuration config = new Configuration();
	    	config.locale = locale;
	    	getResources().updateConfiguration(config, getResources().getDisplayMetrics() );

    	}

     	new AsyncSessionSetActive().execute();

    }

}