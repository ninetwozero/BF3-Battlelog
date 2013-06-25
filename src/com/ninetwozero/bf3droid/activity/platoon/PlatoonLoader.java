package com.ninetwozero.bf3droid.activity.platoon;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ninetwozero.bf3droid.jsonmodel.platoon.PlatoonDossier;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.misc.HttpHeaders;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;

public class PlatoonLoader implements LoaderManager.LoaderCallbacks<CompletedTask> {

    private static final int LOADER_PLATOON = 31;
    private final Callback callback;
    private final Context context;
    private final long platoonId;
    private final LoaderManager loaderManager;

    public PlatoonLoader(Callback callback, Context context, long platoonId, LoaderManager loaderManager) {
        this.callback = callback;
        this.context = context;
        this.platoonId = platoonId;
        this.loaderManager = loaderManager;
    }

    public interface Callback {
        void onLoadFinished(PlatoonDossier platoonDossier);
    }

    public void restart() {
        loaderManager.restartLoader(LOADER_PLATOON, new Bundle(), this);
    }

    @Override
    public Loader<CompletedTask> onCreateLoader(int i, Bundle bundle) {
        return new Bf3Loader(context, httpDataStats());
    }

    private Bf3ServerCall.HttpData httpDataStats() {
        Header[] requestHeaders = HttpHeaders.GET_HEADERS.get(HttpHeaders.AJAX_GET_HEADER);
        return new Bf3ServerCall.HttpData(UriFactory.platoonDossier(platoonId), HttpGet.METHOD_NAME, true, requestHeaders );
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> completedTaskLoader, CompletedTask completedTask) {
        if(isTaskSuccess(completedTask.result)){
            PlatoonDossier dossier = processLoaderResult(completedTask.jsonObject);
            callback.onLoadFinished(dossier);
        }else{
            Log.e(PlatoonLoader.class.getSimpleName(), "Platoon data extraction failed for " + platoonId);
        }
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {

    }

    private boolean isTaskSuccess(CompletedTask.Result result) {
        return result == CompletedTask.Result.SUCCESS;
    }

    private PlatoonDossier processLoaderResult(JsonObject jsonObject){
        Gson gson = new Gson();
        PlatoonDossier dossier = gson.fromJson(jsonObject.getAsJsonObject("context"), PlatoonDossier.class);
        return dossier;
    }
}
