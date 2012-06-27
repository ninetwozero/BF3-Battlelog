
package com.ninetwozero.battlelog.loader;

import com.google.gson.JsonObject;

public class CompletedTask {

    public enum Result {
        SUCCESS, ERROR, FAILURE, OFFLINE, SERVER_FAILURE;
    }

    public final Result result;
    public final String message;
    public final Throwable exception;
    public final JsonObject jsonObject;

    public CompletedTask(Result result) {
        this.result = result;
        this.message = null;
        this.exception = null;
        this.jsonObject = null;
    }

    public CompletedTask(Result result, String message) {
        this.result = result;
        this.message = message;
        this.exception = null;
        this.jsonObject = null;
    }

    public CompletedTask(Result result, Throwable exception) {
        this.result = result;
        this.exception = exception;
        this.message = null;
        this.jsonObject = null;
    }

    public CompletedTask(Result result, JsonObject jsonObject) {
        this.result = result;
        this.exception = null;
        this.message = null;
        this.jsonObject = jsonObject;
    }

}
