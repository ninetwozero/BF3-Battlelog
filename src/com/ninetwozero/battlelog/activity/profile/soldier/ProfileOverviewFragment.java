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

package com.ninetwozero.battlelog.activity.profile.soldier;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.Bf3Fragment;
import com.ninetwozero.battlelog.activity.platoon.PlatoonActivity;
import com.ninetwozero.battlelog.asynctask.AsyncFriendRemove;
import com.ninetwozero.battlelog.asynctask.AsyncFriendRequest;
import com.ninetwozero.battlelog.dao.PlatoonInformationDAO;
import com.ninetwozero.battlelog.dao.ProfileInformationDAO;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.ProfileInformation;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.COMClient;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class ProfileOverviewFragment extends Bf3Fragment {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private COMClient mComClient;
    private SharedPreferences mSharedPreferences;

    private ProfileData mProfileData;
    private ProfileInformation mProfileInformation;
    private boolean mPostingRights;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mLayoutInflater = inflater;

        View view = mLayoutInflater.inflate(R.layout.tab_content_profile_overview, container, false);
        initFragment(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();
        new AsyncCache().execute();
    }

    public void initFragment(View v) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    	mComClient = new COMClient(
			mProfileData.getId(),
			mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")
    	);
        mPostingRights = false;
    }

    public final void showProfile(ProfileInformation data) {
        if (data == null || mContext == null) {
            return;
        }
        Activity activity = (Activity) mContext;
        
        ((TextView) activity.findViewById(R.id.text_username)).setText(data.getUsername());
        if (data.isPlaying() && data.isOnline()) {
            ((TextView) activity.findViewById(R.id.text_online)).setText(
                getString(R.string.info_profile_playing).replace("{server name}", data.getCurrentServer())
            );
        } else if (data.isOnline()) {
            ((TextView) activity.findViewById(R.id.text_online)).setText(R.string.info_profile_online);
        } else {
            ((TextView) activity.findViewById(R.id.text_online)).setText(data.getLastLoginString(mContext));
        }

        if ("".equals(data.getStatusMessage())) {
            (activity.findViewById(R.id.wrap_status)).setVisibility(View.GONE);
        } else {
            ((TextView) activity.findViewById(R.id.text_status)).setText(data.getStatusMessage());
            ((TextView) activity.findViewById(R.id.text_status_date)).setText(PublicUtils.getRelativeDate(mContext,
                    data.getStatusMessageChanged(), R.string.info_lastupdate));
        }

        if ("".equals(data.getPresentation())) {
            ((TextView) activity.findViewById(R.id.text_presentation)).setText(R.string.info_profile_empty_pres);
        } else {
            ((TextView) activity.findViewById(R.id.text_presentation)).setText(data.getPresentation());
        }

        if (data.getNumPlatoons() > 0) {
            View convertView;
            LinearLayout platoonWrapper = (LinearLayout) activity.findViewById(R.id.list_platoons);

            activity.findViewById(R.id.text_platoon).setVisibility(View.GONE);
            platoonWrapper.removeAllViews();

            final OnClickListener onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(
                    	new Intent(mContext, PlatoonActivity.class).putExtra("platoon", (PlatoonData) v.getTag())
                    );
                }
            };

            for (PlatoonData currentPlatoon : data.getPlatoons()) {
                if (platoonWrapper.findViewWithTag(currentPlatoon) != null) {
                    continue;
                }
                convertView = mLayoutInflater.inflate(R.layout.list_item_platoon, platoonWrapper, false);

                ((TextView) convertView.findViewById(R.id.text_name)).setText(currentPlatoon.getName());
                ((TextView) convertView.findViewById(R.id.text_members)).setText(String.valueOf(currentPlatoon.getCountMembers()));
                ((TextView) convertView.findViewById(R.id.text_fans)).setText(String.valueOf(currentPlatoon.getCountFans()));

                String image = PublicUtils.getCachePath(mContext)+ currentPlatoon.getImage();
                ((ImageView) convertView.findViewById(R.id.image_badge)).setImageBitmap(BitmapFactory.decodeFile(image));
                
                convertView.setTag(currentPlatoon);
                convertView.setOnClickListener(onClickListener);

                platoonWrapper.addView(convertView);
            }
        } else {
            ((LinearLayout) activity.findViewById(R.id.list_platoons)).removeAllViews();
            (activity.findViewById(R.id.text_platoon)).setVisibility(View.VISIBLE);
        }
        ((TextView) activity.findViewById(R.id.text_username)).setText(data.getUsername());
    }

    public class AsyncCache extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            this.progressDialog = new ProgressDialog(mContext);
            this.progressDialog.setTitle(mContext.getString(R.string.general_wait));
            this.progressDialog.setMessage(mContext.getString(R.string.general_downloading));
            this.progressDialog.show();
        }
        
        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                mProfileInformation = ProfileInformationDAO.getProfileInformationFromCursor(
            		mContext.getContentResolver().query(
	            		ProfileInformationDAO.URI, 
	            		null,
	            		ProfileInformationDAO.Columns.USER_ID + "=?",
	            		new String[] {String.valueOf(mProfileData.getId())},
	            		null
	            	)
				);

                if( mProfileInformation != null ) {
	                List<PlatoonData> platoons = new ArrayList<PlatoonData>();
	                for(String platoonId : mProfileInformation.getSerializedPlatoonIds().split(":")) {
	                	platoons.add(
	            			PlatoonInformationDAO.getPlatoonDataFromCursor(
	            				mContext.getContentResolver().query(
	            					PlatoonInformationDAO.URI,
	            					PlatoonInformationDAO.getSmallerProjection(),
	            					PlatoonInformationDAO.Columns.PLATOON_ID + "=?",
	            					new String[] { platoonId },
	            					null
	    						)
	    					)
	                	);
	                }
	                mProfileInformation.setPlatoons(platoons);
	                return true;
                }
                return false;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean foundCachedVersion) {
            if (foundCachedVersion) {
            	long cacheExpiration = System.currentTimeMillis()-((Constants.MINUTE_IN_SECONDS*15)*1000);
            	if( mProfileInformation.getTimestamp() < cacheExpiration) {
            		new AsyncRefresh(SessionKeeper.getProfileData().getId()).execute();
            	}
                if (this.progressDialog != null) {
                    this.progressDialog.dismiss();
                }

                showProfile(mProfileInformation);

                if (mProfileData.getNumPersonas() < mProfileInformation.getNumPersonas()) {
                    mProfileData.setPersona(mProfileInformation.getAllPersonas());
                }

                sendToStats(mProfileData);

            } else {
                new AsyncRefresh(SessionKeeper.getProfileData().getId(), progressDialog).execute();
            }
        }
    }

    public class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {
        private long activeProfileId;
        private ProgressDialog progressDialog;

        public AsyncRefresh(long pId) {
            this.activeProfileId = pId;
        }

        public AsyncRefresh(long pId, ProgressDialog pDialog) {
            this.activeProfileId = pId;
            this.progressDialog = pDialog;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                if (mProfileData.getNumPersonas() == 0) {
                    mProfileData = ProfileClient.resolveFullProfileDataFromProfileData(mProfileData);
                }
                
                mProfileInformation = new ProfileClient(mProfileData).getInformation(mContext);
                updateProfileInDB(mProfileInformation);
               
                for(PlatoonData p : mProfileInformation.getPlatoons()) {
                	updatePlatoonInDB(p);
                }
                return (mProfileInformation != null);
            } catch (WebsiteHandlerException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(mContext, R.string.general_no_data,Toast.LENGTH_SHORT).show();
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            showProfile(mProfileInformation);
            sendToStats(mProfileData);
            setFeedPermission(mProfileInformation.isFriend() || mPostingRights);
        }
    }

    public void reload() {
        new AsyncRefresh(SessionKeeper.getProfileData().getId()).execute();
    }

    public void updateProfileInDB(ProfileInformation p) {
    	ContentValues contentValues = ProfileInformationDAO.convertProfileInformationForDB(p, System.currentTimeMillis());
    	try {
    		 mContext.getContentResolver().insert(ProfileInformationDAO.URI, contentValues);		
    	 } catch(SQLiteConstraintException ex) {
    		 mContext.getContentResolver().update(
				 ProfileInformationDAO.URI, 
				 contentValues,
				 ProfileInformationDAO.Columns.USER_ID + "=?", 
				 new String[] { String.valueOf(p.getUserId()) }
			 );
    	 }
	}
    
    public void updatePlatoonInDB(PlatoonData p) {
    	ContentValues contentValues = PlatoonInformationDAO.convertPlatoonDataForDB(p);
    	try {
    		 mContext.getContentResolver().insert(PlatoonInformationDAO.URI, contentValues);		
    	 } catch(SQLiteConstraintException ex) {
    		 mContext.getContentResolver().update(
				 PlatoonInformationDAO.URI, 
				 contentValues,
				 PlatoonInformationDAO.Columns.PLATOON_ID + "=?", 
				 new String[] { String.valueOf(p.getId()) }
			 );
    	 }
	}

	public void sendToStats(ProfileData p) {
        ((ProfileActivity) mContext).openStats(p);
    }

    public void setFeedPermission(boolean c) {
        ((ProfileActivity) mContext).setFeedPermission(c);
    }

    public void setProfileData(ProfileData p) {
        mProfileData = p;
    }

    public Menu prepareOptionsMenu(Menu menu) {
        if (mProfileInformation == null) {
            return menu;
        }
        if (mProfileInformation.isAllowingFriendRequests()) {
            if (mProfileInformation.isFriend()) {
                ((MenuItem) menu.findItem(R.id.option_friendadd)).setVisible(false);
                ((MenuItem) menu.findItem(R.id.option_frienddel)).setVisible(true);
            } else {
                ((MenuItem) menu.findItem(R.id.option_friendadd)).setVisible(true);
                ((MenuItem) menu.findItem(R.id.option_frienddel)).setVisible(false);
            }
        } else {
            ((MenuItem) menu.findItem(R.id.option_friendadd)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.option_frienddel)).setVisible(false);
        }
        ((MenuItem) menu.findItem(R.id.option_compare)).setVisible(false);
        return menu;
    }

    public boolean handleSelectedOption(MenuItem item) {
        if (item.getItemId() == R.id.option_friendadd) {
            new AsyncFriendRequest(mContext, mProfileData.getId()).execute(mComClient);
        } else if (item.getItemId() == R.id.option_frienddel) {
            new AsyncFriendRemove(mContext, mProfileData.getId()).execute(mComClient);
        }
        return true;
    }
}