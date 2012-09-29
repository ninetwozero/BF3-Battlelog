package com.ninetwozero.battlelog.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.google.gson.JsonObject;
import com.ninetwozero.battlelog.server.Bf3ServerCall;

import java.net.URI;

import static com.ninetwozero.battlelog.loader.CompletedTask.Result.FAILURE;
import static com.ninetwozero.battlelog.loader.CompletedTask.Result.SUCCESS;

public class Bf3Loader extends AsyncTaskLoader<CompletedTask> implements
        Bf3ServerCall.Bf3ServerCallCallback {

    private final Bf3ServerCall serverCall;
    private CompletedTask completedTask;
    private boolean loading;

    public Bf3Loader(Context context, URI uri) {
        super(context);
        serverCall = new Bf3ServerCall(uri, this);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public CompletedTask loadInBackground() {
        loading = true;
        serverCall.execute();
        loading = false;
        return completedTask;
    }

    @Override
    public void onBf3CallSuccess(JsonObject jsonObject) {
        completedTask = new CompletedTask(SUCCESS, jsonObject);
    }

    @Override
    public void onBf3CallFailure() {
        completedTask = new CompletedTask(FAILURE);
    }

    @Override
    public void onBf3CallError() {

    }
}
