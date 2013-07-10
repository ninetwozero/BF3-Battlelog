package com.ninetwozero.bf3droid.loader;

import com.google.gson.JsonObject;

public class CompletedTask {

    public enum Result {
        SUCCESS, ERROR, FAILURE, OFFLINE, SERVER_FAILURE
    }

    public final Result result;
    public final String response;
    public final Throwable exception;
    public final JsonObject jsonObject;
    public int loaderID;

    public CompletedTask(Result result) {
        this.result = result;
        this.response = null;
        this.exception = null;
        this.jsonObject = null;
    }

    public CompletedTask(Result result, String response) {
        this.result = result;
        this.response = response;
        this.exception = null;
        this.jsonObject = null;
    }

    public CompletedTask(Result result, Throwable exception) {
        this.result = result;
        this.exception = exception;
        this.response = null;
        this.jsonObject = null;
    }

    public CompletedTask(Result result, JsonObject jsonObject) {
        this.result = result;
        this.exception = null;
        this.response = null;
        this.jsonObject = jsonObject;
    }

    public int getLoaderID() {
        return loaderID;
    }

    public void setLoaderID(int loaderID) {
        this.loaderID = loaderID;
    }
}
