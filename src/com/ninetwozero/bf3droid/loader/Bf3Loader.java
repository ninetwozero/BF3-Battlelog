package com.ninetwozero.bf3droid.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.google.gson.JsonObject;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;

import static com.ninetwozero.bf3droid.loader.CompletedTask.Result.FAILURE;
import static com.ninetwozero.bf3droid.loader.CompletedTask.Result.SUCCESS;

public class Bf3Loader extends AsyncTaskLoader<CompletedTask> implements Bf3ServerCall.Bf3ServerCallCallback {

    private final Bf3ServerCall serverCall;
    private CompletedTask completedTask;

    public Bf3Loader(Context context, Bf3ServerCall.HttpData httpData){
        super(context);
        serverCall = new Bf3ServerCall(httpData, this);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public CompletedTask loadInBackground() {
        serverCall.execute();
        return completedTask;
    }

    @Override
    public void onBf3CallSuccess(JsonObject jsonObject) {
        completedTask = new CompletedTask(SUCCESS, jsonObject);
    }

    @Override
    public void onBf3CallSuccess(String response){
        completedTask = new CompletedTask(SUCCESS, response);
    }

    @Override
    public void onBf3CallFailure() {
        completedTask = new CompletedTask(FAILURE);
    }

    @Override
    public void onBf3CallError() {

    }
}
