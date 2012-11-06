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

package com.ninetwozero.battlelog.activity.platoon;

import java.util.ArrayList;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.CustomFragmentActivity;
import com.ninetwozero.battlelog.activity.feed.FeedFragment;
import com.ninetwozero.battlelog.dao.PlatoonInformationDAO;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.datatype.PlatoonInformation;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.FeedClient;
import com.ninetwozero.battlelog.http.PlatoonClient;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class PlatoonActivity extends CustomFragmentActivity {

    // Fragment related
    private PlatoonOverviewFragment mFragmentOverview;
    private PlatoonStatsFragment mFragmentStats;
    private PlatoonMemberFragment mFragmentMember;
    private FeedFragment mFragmentFeed;

    // Misc
    private PlatoonData mPlatoonData;
    private PlatoonInformation mPlatoonInformation;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        if (!getIntent().hasExtra("platoon")) {
            finish();
        }
        mPlatoonData = (PlatoonData) getIntent().getParcelableExtra("platoon");

        setContentView(R.layout.viewpager_default);
        setup();
        init();
    }

    public void init() {

    }

    public void reload() {
        new AsyncRefresh(this, mPlatoonData, SessionKeeper.getProfileData().getId()).execute();
    }

    public class AsyncCache extends AsyncTask<Void, Void, Boolean> {
    	private Context context;
    	private ProgressDialog progressDialog;

        public AsyncCache(Context c) {
        	context = c;
        }
        
        @Override
        protected void onPreExecute() {
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setTitle(context.getString(R.string.general_wait));
            this.progressDialog.setMessage(context.getString(R.string.general_downloading));
            this.progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
            	mPlatoonInformation = PlatoonInformationDAO.getPlatoonInformationFromCursor(
        			context.getContentResolver().query(
	            		PlatoonInformationDAO.URI,
	            		null, 
	            		PlatoonInformationDAO.Columns.PLATOON_ID + "=?", 
	            		new String[] { String.valueOf(mPlatoonData.getId())},
	            		null
	            	)
        		);
                return (mPlatoonInformation != null);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        /* TODO: 
         * Future work flow: 
         * Each fragment fixes its own data, as they are only dependent on PlatoonData. 
         * Each caches at different table in Provider etc
         * Each fragment has its own "cache expiration" date. */
        protected void onPostExecute(Boolean cacheExists) {
            if (cacheExists) {
            	//long cacheExpiration = System.currentTimeMillis()-((Constants.MINUTE_IN_SECONDS*30)*1000);
                //if(( mPlatoonInformation.getTimestamp() < cacheExpiration)) {
                	new AsyncRefresh(context, mPlatoonData, SessionKeeper.getProfileData().getId()).execute();
            	//}

                mFragmentOverview.show(mPlatoonInformation);
                mFragmentStats.show(mPlatoonInformation.getStats());
                
                mFragmentMember.setMembers(mPlatoonInformation.getMembers());
                mFragmentMember.setFans(mPlatoonInformation.getFans());
                mFragmentMember.setAdmin(mPlatoonInformation.isAdmin());
                mFragmentMember.show(); 
                
            	if (this.progressDialog != null) {
                    this.progressDialog.dismiss();
                }
            } else {
                new AsyncRefresh(context, mPlatoonData, SessionKeeper.getProfileData().getId(), progressDialog).execute();
            }
        }
    }

    public class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private ProgressDialog progressDialog;
        private PlatoonData platoonData;
        private long activeProfileId;

        public AsyncRefresh(Context c, PlatoonData pd, long pId) {
            this.context = c;
            this.platoonData = pd;
            this.activeProfileId = pId;
        }

        public AsyncRefresh(Context c, PlatoonData pd, long pId, ProgressDialog p) {
            this.context = c;
            this.platoonData = pd;
            this.activeProfileId = pId;
            this.progressDialog = p;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                mPlatoonInformation = new PlatoonClient(this.platoonData).getInformation(
                	context, 
                	mSharedPreferences.getInt(Constants.SP_BL_NUM_FEED,Constants.DEFAULT_NUM_FEED),
                	this.activeProfileId
            	);
                if( mPlatoonInformation != null ) {
                	updatePlatoonInDB(mPlatoonInformation);
                	return true;
                }
                return false;
            } catch (WebsiteHandlerException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        	if (!result) {
                Toast.makeText(context, R.string.general_no_data,Toast.LENGTH_SHORT).show();
                return;
            }
        	
            mFragmentOverview.show(mPlatoonInformation);
            mFragmentStats.show(mPlatoonInformation.getStats());
            mFragmentFeed.setCanWrite(mPlatoonInformation.isMember());
            
            mFragmentMember.setMembers(mPlatoonInformation.getMembers());
            mFragmentMember.setFans(mPlatoonInformation.getFans());
            mFragmentMember.setAdmin(mPlatoonInformation.isAdmin());
            mFragmentMember.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_platoonview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void updatePlatoonInDB(PlatoonInformation p) {
		ContentValues contentValues = PlatoonInformationDAO.convertPlatoonInformationForDB(p, System.currentTimeMillis());
    	try {
	    	getContentResolver().insert(PlatoonInformationDAO.URI, contentValues); 
    	} catch(SQLiteConstraintException ex) {
    		getContentResolver().update(
	    		PlatoonInformationDAO.URI,
	    		contentValues,	    		
	    		PlatoonInformationDAO.Columns.PLATOON_ID + "=?",
	    		new String[] { String.valueOf(p.getId()) }
			);
    	}
	}

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mViewPager.getCurrentItem() == 0) {
            return super.onPrepareOptionsMenu(mFragmentOverview.prepareOptionsMenu(menu));
        } else if (mViewPager.getCurrentItem() == 1) {
            return super.onPrepareOptionsMenu(mFragmentStats.prepareOptionsMenu(menu));
        } else if (mViewPager.getCurrentItem() == 2) {
            return super.onPrepareOptionsMenu(mFragmentMember.prepareOptionsMenu(menu));
        } else {
            menu.removeItem(R.id.option_friendadd);
            menu.removeItem(R.id.option_frienddel);
            menu.removeItem(R.id.option_compare);
            menu.removeItem(R.id.option_unlocks);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_reload) {
            this.reload();
        } else if (item.getItemId() == R.id.option_back) {
            ((Activity) this).finish();
        } else {
            if (mViewPager.getCurrentItem() == 0) {
                return mFragmentOverview.handleSelectedOption(item);
            } else if (mViewPager.getCurrentItem() == 1) {
                return mFragmentStats.handleSelectedOption(item);
            } else if (mViewPager.getCurrentItem() == 2) {
                return mFragmentMember.handleSelectedOption(item);
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        new AsyncCache(this).execute();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                mFragmentMember.createContextMenu(menu, view, menuInfo);
                break;
            case 3:
                mFragmentFeed.createContextMenu(menu, view, menuInfo);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            e.printStackTrace();
            return false;
        }
        
        switch (mViewPager.getCurrentItem()) {
            case 2:
                return mFragmentMember.handleSelectedContextItem(info, item);
            case 3:
                return mFragmentFeed.handleSelectedContextItem(info, item);
            default:
                break;
        }
        return true;
    }

    public void setup() {
        if (mListFragments == null) {
            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(mFragmentOverview = (PlatoonOverviewFragment) Fragment.instantiate(this, PlatoonOverviewFragment.class.getName()));
            mListFragments.add(mFragmentStats = (PlatoonStatsFragment) Fragment.instantiate(this, PlatoonStatsFragment.class.getName()));
            mListFragments.add(mFragmentMember = (PlatoonMemberFragment) Fragment.instantiate(this, PlatoonMemberFragment.class.getName()));
            mListFragments.add(mFragmentFeed = (FeedFragment) Fragment.instantiate(this, FeedFragment.class.getName()));

            mFragmentOverview.setPlatoonData(mPlatoonData);
            mFragmentMember.setPlatoonData(mPlatoonData);

            mFragmentFeed.setTitle(mPlatoonData.getName());
            mFragmentFeed.setType(FeedClient.TYPE_PLATOON);
            mFragmentFeed.setId(mPlatoonData.getId());
            mFragmentFeed.setCanWrite(false);

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            mPagerAdapter = new SwipeyTabsPagerAdapter(
                mFragmentManager, 
                new String[]{"OVERVIEW", "STATS", "USERS", "FEED"}, 
                mListFragments, 
                mViewPager, 
                mLayoutInflater
            );
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setCurrentItem(0);
            mViewPager.setOffscreenPageLimit(3);
        }
    }
    
    public void setFeedPermission(boolean c) {
        mFragmentFeed.setCanWrite(c);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() > 0) {
            mViewPager.setCurrentItem(0, true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
