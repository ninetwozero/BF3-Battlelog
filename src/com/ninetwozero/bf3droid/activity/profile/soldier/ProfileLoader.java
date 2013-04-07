package com.ninetwozero.bf3droid.activity.profile.soldier;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.activity.profile.soldier.restorer.UserInfoRestorer;
import com.ninetwozero.bf3droid.datatype.UserInfo;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;
import com.ninetwozero.bf3droid.util.HtmlParsing;

import org.apache.http.client.methods.HttpGet;

public class ProfileLoader implements LoaderManager.LoaderCallbacks<CompletedTask>{

    private final Callback callback;
    private final Context context;
    private final String user;
    private final LoaderManager loaderManager;
    private final int LOADER_OVERVIEW = 22;

    public interface Callback {
        void onLoadFinished(UserInfo userInfo);
    }

    public ProfileLoader(Callback callback, Context context, String user, LoaderManager loaderManager){
        this.callback = callback;
        this.context = context;
        this.user = user;
        this.loaderManager = loaderManager;
    }

    public void restart(){
        loaderManager.restartLoader(LOADER_OVERVIEW, new Bundle(), this);
    }

    @Override
    public Loader<CompletedTask> onCreateLoader(int i, Bundle bundle) {
        return new Bf3Loader(context, httpDataOverview());
    }

    private Bf3ServerCall.HttpData httpDataOverview() {
        return new Bf3ServerCall.HttpData(UriFactory.getProfileInformationUri(user().getName()), HttpGet.METHOD_NAME, false);
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask>loader, CompletedTask completedTask) {
        loaderManager.destroyLoader(LOADER_OVERVIEW);
        if (isTaskSuccess(completedTask.result)) {
            UserInfo userInfo = processOverviewLoaderResult(completedTask);
            callback.onLoadFinished(userInfo);
        } else {
            Log.e(ProfileOverviewFragment.class.getSimpleName(), "User data extraction failed for " + user().getName());
        }
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }

    private UserInfo processOverviewLoaderResult(CompletedTask completedTask) {
        UserInfo userInfo = processUserDataResult(completedTask.response);
        new UserInfoRestorer(context, user).save(userInfo);

        return userInfo;
    }

    private UserInfo processUserDataResult(String response) {
        HtmlParsing parser = new HtmlParsing();
        return parser.extractUserInfo(response);
    }

    private boolean isTaskSuccess(CompletedTask.Result result) {
        return result == CompletedTask.Result.SUCCESS;
    }

    private User user() {
        if (user.equals(User.USER)) {
            return BF3Droid.getUser();
        } else {
            return BF3Droid.getGuest();
        }
    }
}
