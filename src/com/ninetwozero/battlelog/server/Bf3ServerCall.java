package com.ninetwozero.battlelog.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ninetwozero.battlelog.server.SimpleHttpCaller.SimpleHttpCallerCallback;

public class Bf3ServerCall implements SimpleHttpCallerCallback {

    public interface Bf3ServerCallCallback {
        void onBf3CallSuccess(JsonObject jsonObject);

        void onBf3CallFailure();

        void onBf3CallError();
    }

    protected final Bf3ServerCallCallback callback;
    protected final URI call;
    private final HttpClient httpClient;

    public Bf3ServerCall(URI call, Bf3ServerCallCallback callback) {
        this.callback = callback;
        this.call = call;
        this.httpClient = new DefaultHttpClient();
    }

    public void execute() {
        try {
            buildHttpCaller().execute();
        } catch (final Exception e) {
            callback.onBf3CallError();
        }
    }

    protected SimpleHttpCaller buildHttpCaller() throws Exception {
        HttpGet request = new HttpGet(call);
        return new SimpleHttpCaller(httpClient, request, this);
    }

    @Override
    public void onSimpleHttpCallSuccess(HttpResponse response) {
        Reader reader = getJSONReader(response);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
        callback.onBf3CallSuccess(overviewStatsObject(jsonObject));
    }

    @Override
    public void onSimpleHttpCallFailure(HttpResponse response) throws _403Exception {
        callback.onBf3CallFailure();
    }

    private Reader getJSONReader(HttpResponse response) {
        Reader reader = null;
        try {
            InputStream data = response.getEntity().getContent();
            reader = new InputStreamReader(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

    private JsonObject overviewStatsObject(JsonObject jsonObject) {
        return jsonObject.getAsJsonObject("data");
    }

    @SuppressWarnings("serial")
    public static class _403Exception extends IOException {
    }
}
