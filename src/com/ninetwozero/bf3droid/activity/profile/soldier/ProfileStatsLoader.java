package com.ninetwozero.bf3droid.activity.profile.soldier;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.gson.Gson;
import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.activity.profile.soldier.restorer.PersonaStatisticsRestorer;
import com.ninetwozero.bf3droid.dao.PersonaStatisticsDAO;
import com.ninetwozero.bf3droid.dao.RankProgressDAO;
import com.ninetwozero.bf3droid.dao.ScoreStatisticsDAO;
import com.ninetwozero.bf3droid.datatype.PersonaOverviewStatistics;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.PersonaInfo;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;
import com.ninetwozero.bf3droid.util.Platform;

import org.apache.http.client.methods.HttpGet;

public class ProfileStatsLoader implements LoaderManager.LoaderCallbacks<CompletedTask> {

    private static final int LOADER_STATISTICS = 23;
    private final Callback callback;
    private final Context context;
    private final String user;
    private final LoaderManager loaderManager;

    public interface Callback {
        void onLoadFinished(PersonaOverviewStatistics pso);
    }

    public ProfileStatsLoader(Callback callback, Context context, String user, LoaderManager loaderManager) {
        this.callback = callback;
        this.context = context;
        this.user = user;
        this.loaderManager = loaderManager;
    }

    public void restart() {
        loaderManager.restartLoader(LOADER_STATISTICS, new Bundle(), this);
    }

    @Override
    public Loader<CompletedTask> onCreateLoader(int id, Bundle bundle) {
        return new Bf3Loader(context, httpDataStats());
    }

    private Bf3ServerCall.HttpData httpDataStats() {
        return new Bf3ServerCall.HttpData(UriFactory.getPersonaOverviewUri(selectedPersonaId(), platformId()), HttpGet.METHOD_NAME);
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> loader, CompletedTask completedTask) {
        if (isTaskSuccess(completedTask.result)) {
             PersonaOverviewStatistics stats = processStatsLoaderResult(completedTask);
            stats.setUserType(user);
            callback.onLoadFinished(stats);
        } else {
            Log.e("ProfileOverviewFragment", "User data extraction failed for " + user(user).getName());
        }
    }

    private PersonaOverviewStatistics processStatsLoaderResult(CompletedTask completedTask) {
        PersonaOverviewStatistics stats = personaStatsFrom(completedTask);
        new PersonaStatisticsRestorer(context, user).save(stats);
        return stats;
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }

    private boolean isTaskSuccess(CompletedTask.Result result) {
        return result == CompletedTask.Result.SUCCESS;
    }

    private PersonaOverviewStatistics personaStatsFrom(CompletedTask task) {
        Gson gson = new Gson();
        PersonaInfo data = gson.fromJson(task.jsonObject, PersonaInfo.class);
        return new PersonaOverviewStatistics(RankProgressDAO.rankProgressFromJSON(data)
                , PersonaStatisticsDAO.personaStatisticsFromJSON(data)
                , ScoreStatisticsDAO.scoreStatisticsFromJSON(data));
    }

    private User user(String user) {
        return BF3Droid.getUserBy(user);
    }

    private long selectedPersonaId(){
        return user(user).selectedPersona().getPersonaId();
    }

    private int platformId(){
        return Platform.resolveIdFromPlatformName(user(user).selectedPersona().getPlatform());
    }
}
