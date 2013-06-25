package com.ninetwozero.bf3droid.server;

import android.util.Log;

import com.google.gson.JsonObject;
import com.ninetwozero.bf3droid.server.SimpleHttpCaller.SimpleHttpCallerCallback;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Bf3ServerCall implements SimpleHttpCallerCallback {

    public interface Bf3ServerCallCallback {
        void onBf3CallSuccess(JsonObject jsonObject);

        void onBf3CallSuccess(String response);

        void onBf3CallFailure();

        void onBf3CallError();
    }

    protected final Bf3ServerCallCallback callback;
    private final DefaultHttpClient httpClient;
    private HttpData httpData;

    public Bf3ServerCall(HttpData httpData, Bf3ServerCallCallback callback) {
        this.httpData = httpData;
        this.callback = callback;
        this.httpClient = HttpClientFactory.getThreadSafeClient();
    }

    public void execute() {
        try {
            if (httpData.isGetMethod()) {
                buildHttpGetCaller().execute();
            } else {
                buildHttpPostCaller().execute();
            }
        } catch (final Exception e) {
            Log.e("Bf3ServerCall", "HttpClient exception " + e.toString());
            callback.onBf3CallError();
        }
    }

    protected SimpleHttpCaller buildHttpGetCaller() throws Exception {
        HttpGet request = new HttpGet(httpData.getCall());
        if(httpData.hasHeaders()){
            request.setHeaders(httpData.getRequestHeaders());
        }
        return new SimpleHttpCaller(httpClient, request, this);
    }

    protected SimpleHttpCaller buildHttpPostCaller() throws Exception {
        HttpPost request = new HttpPost(httpData.getCall());
        request.setEntity(new UrlEncodedFormEntity(httpData.getNameValuePairs(), HTTP.UTF_8));
        return new SimpleHttpCaller(httpClient, request, this);
    }

    @Override
    public void onSimpleHttpCallSuccess(HttpResponse response) {
        if (httpData.isGetMethod() && httpData.doesReturnsJson()) {
            JsonObject jsonObject = JsonWorker.jsonFrom(response);
            callback.onBf3CallSuccess(jsonObject);
        } else {
            doStringCallback(response);
        }
    }

    private void doStringCallback(HttpResponse response) {
        try {
            InputStream is = response.getEntity().getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                result.append(line);
            }
            callback.onBf3CallSuccess(result.toString());
        } catch (IOException io) {
            Log.e("Bf3ServerCall", io.toString());
            callback.onBf3CallError();
        }
    }

    @Override
    public void onSimpleHttpCallFailure(HttpResponse response) throws _403Exception {
        callback.onBf3CallFailure();
    }

    @SuppressWarnings("serial")
    public static class _403Exception extends IOException {
    }

    public static class HttpData {
        private static URI call;
        private static List<NameValuePair> nameValuePairs;
        private static String method;
        private static boolean asJson;
        private static Header[] requestHeaders;

        public HttpData(URI call, String method) {
            this(call, new ArrayList<NameValuePair>(), method, true, new Header[]{});
        }

        public HttpData(URI call, String method, boolean json) {
            this(call, new ArrayList<NameValuePair>(), method, json, new Header[]{});
        }
        public HttpData(URI call, String method, boolean json, Header[] requestHeaders) {
            this(call, new ArrayList<NameValuePair>(), method, json, requestHeaders);
        }

        public HttpData(URI uri, List<NameValuePair> list, String string, boolean json, Header[] requestHeaders) {
            this.call = uri;
            this.nameValuePairs = list;
            this.method = string;
            this.asJson = json;
            this.requestHeaders = requestHeaders.clone();
        }

        public URI getCall() {
            return call;
        }

        public List<NameValuePair> getNameValuePairs() {
            return nameValuePairs;
        }

        public boolean isGetMethod() {
            return method.equals(HttpGet.METHOD_NAME);
        }

        public Header[] getRequestHeaders() {
            return requestHeaders;
        }

        public boolean hasHeaders(){
            return requestHeaders.length > 0;
        }

        public boolean doesReturnsJson() {
            return asJson;
        }
    }
}
