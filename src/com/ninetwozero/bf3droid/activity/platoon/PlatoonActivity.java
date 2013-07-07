/*
	This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.activity.platoon;

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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.activity.feed.FeedFragment;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.http.FeedClient;
import com.ninetwozero.bf3droid.jsonmodel.platoon.PlatoonDossier;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.ninetwozero.bf3droid.provider.UriFactory;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

import java.net.URI;
import java.util.ArrayList;

public class PlatoonActivity extends CustomFragmentActivity implements PlatoonLoader.Callback{

    private PlatoonOverviewFragment platoonOverview;
    private PlatoonStatsFragment platoonStats;
    private PlatoonMemberFragment platoonMembers;
    private FeedFragment platoonFeeds;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.viewpager_default);
        setup();
        init();
    }

    public void init() {}

    @Override
    public void onResume() {
        super.onResume();
        startLoadingDialog(PlatoonActivity.class.getSimpleName());
        new PlatoonLoader(this, getApplicationContext(), platoonDossierUri(), getSupportLoaderManager()).restart();
    }

    private URI platoonDossierUri() {
        return UriFactory.platoonDossier(platoon().getPlatoonId());
    }

    public void reload() {
    }

    private void setup() {
        if (mListFragments == null) {
            mListFragments = new ArrayList<Fragment>();
            mListFragments.add(platoonOverview = (PlatoonOverviewFragment) Fragment.instantiate(this, PlatoonOverviewFragment.class.getName()));
            mListFragments.add(platoonMembers = (PlatoonMemberFragment) Fragment.instantiate(this, PlatoonMemberFragment.class.getName()));
            mListFragments.add(platoonStats = (PlatoonStatsFragment) Fragment.instantiate(this, PlatoonStatsFragment.class.getName()));
            mListFragments.add(platoonFeeds = (FeedFragment) Fragment.instantiate(this, FeedFragment.class.getName()));

            //remove all these set data types, each fragment set its own view with provided data
            platoonFeeds.setTitle(platoon().getName());
            platoonFeeds.setType(FeedClient.TYPE_PLATOON);
            platoonFeeds.setId(platoon().getPlatoonId());
            platoonFeeds.setCanWrite(false);

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            mPagerAdapter = new SwipeyTabsPagerAdapter(
                    mFragmentManager,
                    tabTitles(R.array.platoon_tab),
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_platoonview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*public void updatePlatoonInDB(PlatoonInformation p) {
		ContentValues contentValues = PlatoonInformationDAO.platoonInformationForDB(p, System.currentTimeMillis());
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
	}*/

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mViewPager.getCurrentItem() == 0) {
            return super.onPrepareOptionsMenu(platoonOverview.prepareOptionsMenu(menu));
        } else if (mViewPager.getCurrentItem() == 1) {
            return super.onPrepareOptionsMenu(platoonStats.prepareOptionsMenu(menu));
        } else if (mViewPager.getCurrentItem() == 2) {
            return super.onPrepareOptionsMenu(platoonMembers.prepareOptionsMenu(menu));
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
            this.finish();
        } else {
            if (mViewPager.getCurrentItem() == 0) {
                return platoonOverview.handleSelectedOption(item);
            } else if (mViewPager.getCurrentItem() == 1) {
                return platoonStats.handleSelectedOption(item);
            } else if (mViewPager.getCurrentItem() == 2) {
                return platoonMembers.handleSelectedOption(item);
            }
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                platoonMembers.createContextMenu(menu, view, menuInfo);
                break;
            case 3:
                platoonFeeds.createContextMenu(menu, view, menuInfo);
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
                return platoonMembers.handleSelectedContextItem(info, item);
            case 3:
                return platoonFeeds.handleSelectedContextItem(info, item);
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() > 0) {
            mViewPager.setCurrentItem(0, true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private SimplePlatoon platoon() {
        return BF3Droid.getUserBy(User.USER).selectedPlatoon();
    }

    @Override
    public void onLoadFinished(JsonObject jsonObject) {
        BusProvider.getInstance().post(processLoaderResult(jsonObject));
        closeLoadingDialog(PlatoonActivity.class.getSimpleName());
    }

    private PlatoonDossier processLoaderResult(JsonObject jsonObject){
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.getAsJsonObject("context"), PlatoonDossier.class);
    }
}
