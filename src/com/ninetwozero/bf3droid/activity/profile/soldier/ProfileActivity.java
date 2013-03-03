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

package com.ninetwozero.bf3droid.activity.profile.soldier;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3Fragment;
import com.ninetwozero.bf3droid.activity.CustomFragmentActivity;
import com.ninetwozero.bf3droid.activity.feed.FeedFragment;
import com.ninetwozero.bf3droid.dao.PlatoonInformationDAO;
import com.ninetwozero.bf3droid.dao.UserProfileDataDAO;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.datatype.UserInfo;
import com.ninetwozero.bf3droid.http.FeedClient;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.PersonaInfo;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.provider.table.UserProfileData;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;
import com.ninetwozero.bf3droid.util.HtmlParsing;
import com.ninetwozero.bf3droid.util.Platform;

import java.util.ArrayList;
import java.util.List;

import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabsPagerAdapter;

import org.apache.http.client.methods.HttpGet;

import static com.ninetwozero.bf3droid.dao.PlatoonInformationDAO.simplePlatoonFrom;

public class ProfileActivity extends CustomFragmentActivity implements LoaderManager.LoaderCallbacks<CompletedTask> {

    private final int LOADER_OVERVIEW = 22;
    private static final int LOADER_STATS = 23;
    private static final String TAG = "ProfileActivity";
    private ProfileOverviewFragment fragmentOverview;
    private ProfileStatsFragment fragmentStats;
    private FeedFragment fragmentFeed;
    private Bundle bundle;
    private UserProfileData userProfileData;
    private List<SimplePlatoon> platoons;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.viewpager_default);

        if (getIntent().hasExtra("user")) {
            bundle = getIntent().getExtras();
        } else {
            finish();
        }

        setup();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
        getData();
    }

    public void init() {
    }

    public void reload() {
    }



    private void getData() {
        startLoadingDialog(TAG);
        fetchOverview();
        fetchStats();
        closeLoadingDialog(TAG);
    }

    private void fetchOverview() {
        if (userProfileDataFromDB() && platoonsFromDB()) {
            BusProvider.getInstance().post(new UserInfo(platoons, userProfileData));
        } else {
            restartLoader(LOADER_OVERVIEW);
        }
    }

    private void fetchStats() {
        Log.e("ProfileActivity", "Starting stats loading");
        restartLoader(LOADER_STATS);
    }

    private boolean userProfileDataFromDB() {
        Cursor cursor = context().getContentResolver().query(
                UserProfileDataDAO.URI,
                UserProfileDataDAO.PROJECTION,
                UserProfileDataDAO.Columns.USER_ID + "=?",
                new String[]{String.valueOf(user().getId())},
                null
        );
        if (cursor.getCount() > 0) {
            userProfileData = UserProfileDataDAO.userProfileDataFrom(cursor);
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    private boolean platoonsFromDB() {
        Cursor cursor = context().getContentResolver().query(
                PlatoonInformationDAO.URI,
                PlatoonInformationDAO.SIMPLE_PLATOON_PROJECTION,
                PlatoonInformationDAO.Columns.USER_ID + "=?",
                new String[]{String.valueOf(user().getId())},
                null
        );
        if (cursor.getCount() > 0) {
            platoons = new ArrayList<SimplePlatoon>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                platoons.add(simplePlatoonFrom(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return true;
    }

    private void restartLoader(int callId) {
        if (callId == LOADER_OVERVIEW) {
            getSupportLoaderManager().restartLoader(LOADER_OVERVIEW, bundle, this);
        } else {
            getSupportLoaderManager().restartLoader(LOADER_STATS, bundle, this);
        }
    }

    @Override
    public Loader<CompletedTask> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_OVERVIEW) { Log.e("ProfileActivity", "Overview loader started");
            return new Bf3Loader(context(), httpDataOverview());
        } else {
            Log.e("ProfileActivity", "Stats loader started");
            return new Bf3Loader(context(), httpDataStats());
        }
    }

    private Bf3ServerCall.HttpData httpDataStats() {
        return new Bf3ServerCall.HttpData(UriFactory.getPersonaOverviewUri(selectedPersonaId(), platformId()), HttpGet.METHOD_NAME);
    }

    private Bf3ServerCall.HttpData httpDataOverview() {
        return new Bf3ServerCall.HttpData(UriFactory.getProfileInformationUri(user().getName()), HttpGet.METHOD_NAME, false);
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> loader, CompletedTask completedTask) {
        Log.e("ProfileActivity", "Loader fetched some data");
        if (loader.getId() == LOADER_OVERVIEW && isTaskSuccess(completedTask.result)) {
            processOverviewLoaderResult(completedTask);
        } else if (loader.getId() == LOADER_STATS && isTaskSuccess(completedTask.result)) {
            processStatsLoaderResult(completedTask);
        } else {
            Log.e("ProfileOverviewFragment", "User data extraction failed for " + user().getName());
        }
    }

    private void processOverviewLoaderResult(CompletedTask completedTask) {
        UserInfo userInfo = processUserDataResult(completedTask.response);
        savePersonaToApp(userInfo);
        BusProvider.getInstance().post(userInfo);
        Log.e("ProfileActivity", "Overview loaded, starting stats loading");
        restartLoader(LOADER_STATS);
    }

    private void processStatsLoaderResult(CompletedTask completedTask) {
        PersonaInfo personaInfo = personaStatsFrom(completedTask);
        BusProvider.getInstance().post(personaInfo);
        Log.e("ProfileActivity", "Stats loaded");
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }

    private boolean isTaskSuccess(CompletedTask.Result result) {
        return result == CompletedTask.Result.SUCCESS;
    }

    private UserInfo processUserDataResult(String response) {
        HtmlParsing parser = new HtmlParsing();
        return parser.extractUserInfo(response);
    }

    private PersonaInfo personaStatsFrom(CompletedTask task) {
        Gson gson = new Gson();
        PersonaInfo data = gson.fromJson(task.jsonObject, PersonaInfo.class);
        return data;
    }

    public void setup() {
        if (mListFragments == null) {
            mListFragments = new ArrayList<Fragment>();

            fragmentOverview = (ProfileOverviewFragment) Fragment.instantiate(this, ProfileOverviewFragment.class.getName(), bundle);
            fragmentStats = (ProfileStatsFragment) Fragment.instantiate(this, ProfileStatsFragment.class.getName(), bundle);
            fragmentFeed = (FeedFragment) Fragment.instantiate(this, FeedFragment.class.getName(), bundle);

            mListFragments.add(fragmentOverview);
            mListFragments.add(fragmentStats);
            mListFragments.add(fragmentFeed);

            fragmentFeed.setTitle(BF3Droid.getUser().getName());
            fragmentFeed.setType(FeedClient.TYPE_PROFILE);
            fragmentFeed.setId(BF3Droid.getUser().getId());
            fragmentFeed.setCanWrite(false);

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

            mPagerAdapter = new SwipeyTabsPagerAdapter(
                    mFragmentManager,
                    new String[]{"OVERVIEW", "STATS", "FEED"},
                    mListFragments,
                    mViewPager,
                    mLayoutInflater
            );
            mViewPager.setAdapter(mPagerAdapter);
            mTabs.setAdapter(mPagerAdapter);

            mViewPager.setOnPageChangeListener(mTabs);
            mViewPager.setCurrentItem(0);
            mViewPager.setOffscreenPageLimit(2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_profileview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //TODO a bundle boolean required to decide when user or visitor persona used and based on that show appropriate menu options
        /*if (profileData.getId() == SessionKeeper.getProfileData().getId()) {
            menu.removeItem(R.id.option_friendadd);
			menu.removeItem(R.id.option_frienddel);
			menu.removeItem(R.id.option_compare);
			menu.removeItem(R.id.option_unlocks);
		} else {*/
        if (mViewPager.getCurrentItem() == 0) {
            return super.onPrepareOptionsMenu(fragmentOverview.prepareOptionsMenu(menu));
        } else if (mViewPager.getCurrentItem() == 1) {
            return super.onPrepareOptionsMenu(fragmentStats.prepareOptionsMenu(menu));
        } else if (mViewPager.getCurrentItem() == 2) {
            menu.findItem(R.id.option_friendadd).setVisible(false);
            menu.findItem(R.id.option_frienddel).setVisible(false);
            menu.findItem(R.id.option_compare).setVisible(false);
            menu.findItem(R.id.option_unlocks).setVisible(false);
        } else {
            menu.removeItem(R.id.option_friendadd);
            menu.removeItem(R.id.option_frienddel);
            menu.removeItem(R.id.option_compare);
            menu.removeItem(R.id.option_unlocks);
        }
        //}
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_reload) {
            Fragment fragment = mPagerAdapter.getItem(mViewPager.getCurrentItem());
            if (fragment instanceof Bf3Fragment) {
                ((Bf3Fragment) fragment).reload();
            }
        } else if (item.getItemId() == R.id.option_back) {
            finish();
        } else {
            if (mViewPager.getCurrentItem() == 0) {
                return fragmentOverview.handleSelectedOption(item);
            } else if (mViewPager.getCurrentItem() == 1) {
                return fragmentStats.handleSelectedOption(item);
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
                fragmentFeed.createContextMenu(menu, view, menuInfo);
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
                return fragmentFeed.handleSelectedContextItem(info, item);
            default:
                break;
        }
        return true;
    }

    public void setFeedPermission(boolean c) {
        fragmentFeed.setCanWrite(c);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() > 0) {
            mViewPager.setCurrentItem(0, true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void savePersonaToApp(UserInfo userInfo) {
        user().setPersonas(userInfo.getPersonas());
        user().setPlatoons(userInfo.getPlatoons());
    }

    private Context context() {
        return getApplicationContext();
    }

    private User user() {
        if (getIntent().getStringExtra("user").equals(User.USER)) {
            return BF3Droid.getUser();
        } else {
            return BF3Droid.getGuest();
        }
    }

    private long selectedPersonaId(){
        return user().selectedPersona().getPersonaId();
    }

    private int platformId() {
        return Platform.resolveIdFromPlatformName(user().selectedPersona().getPlatform());
    }
}