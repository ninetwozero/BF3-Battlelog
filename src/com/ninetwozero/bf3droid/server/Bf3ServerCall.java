package com.ninetwozero.bf3droid.server;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.server.SimpleHttpCaller.SimpleHttpCallerCallback;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HTTP;

public class Bf3ServerCall implements SimpleHttpCallerCallback {

    private final String FIND_COOKIE_HEADER = "Set-Cookie";

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
        this.httpClient = httpClient();
    }

    private DefaultHttpClient httpClient(){
        DefaultHttpClient client = new DefaultHttpClient();
        if (BF3Droid.hasCookieStore()) {
            client.setCookieStore(BF3Droid.getCookieStore());
        }
        return client;
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
        //request.setHeader("Referer", "http://battlelog.battlefield.com/bf3/");
        //request.setHeader("Accept", "application/json, text/javascript, */*");
        //request.setHeader("Accept-Language", "en");
        //request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        //request.setHeader("X-Requested-With", "XMLHttpRequest");
        return new SimpleHttpCaller(httpClient, request, this);
    }

    protected SimpleHttpCaller buildHttpPostCaller() throws Exception {
        HttpPost request = new HttpPost(httpData.getCall());
        request.setEntity(new UrlEncodedFormEntity(httpData.getNameValuePairs(), HTTP.UTF_8));
        return new SimpleHttpCaller(httpClient, request, this);
    }

    @Override
    public void onSimpleHttpCallSuccess(HttpResponse response) {
        if (isProfileURI()) {
            printHeaders(response.getAllHeaders());
        }
        if (httpData.isGetMethod() && httpData.doesReturnsJson()) {
            doJsonCallback(response);
        } else {
            doStringCallback(response);
        }
    }

    private boolean isProfileURI() {
        return BF3Droid.hasUser() && httpData.getCall().equals(UriFactory.getProfileInformationUri(BF3Droid.getUser()));
    }

    //Set-Cookie => beaker.session.id=d3a331e6151c7bd440f5006ca3d7b0ea; Path=/;HttpOnly
    private void printHeaders(Header[] headers) {
        for (Header header : headers) {
            if (header.getName().equals(FIND_COOKIE_HEADER)) {
                storeCookie(header);
            }
        }
        Log.e("Bf3ServerCall", "Invalid cookie: missing name and value");
    }

    private void storeCookie(Header header) {
        String[] rawCookieParams = header.getValue().split(";");
        String[] rawCookieNameAndValue = rawCookieParams[0].split("=");
        BasicClientCookie cookie = new BasicClientCookie(rawCookieNameAndValue[0], rawCookieNameAndValue[1]);
        cookie.setDomain("battlelog.battlefield.com");
        for (int i = 1; i < rawCookieParams.length; i++) {
            String[] attributes = rawCookieParams[i].split("=");
            if (attributes.length == 2) {
                cookie.setAttribute(attributes[0], attributes[1]);
            } else {
                cookie.setAttribute(attributes[0], null);
            }
        }
        cookie.setPath("/");
        BF3Droid.setCookie(cookie);
    }

    private void doJsonCallback(HttpResponse response) {
        Reader reader = getJSONReader(response);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
        callback.onBf3CallSuccess(overviewStatsObject(jsonObject));
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

    public static class HttpData {
        private static URI call;
        private static List<NameValuePair> nameValuePairs;
        private static String method;
        private static boolean asJson;

        public HttpData(URI call, String method) {
            this(call, new ArrayList<NameValuePair>(), method, true);
        }

        public HttpData(URI call, String method, boolean json) {
            this(call, new ArrayList<NameValuePair>(), method, json);
        }

        public HttpData(URI uri, List<NameValuePair> list, String string, boolean json) {
            call = uri;
            nameValuePairs = list;
            method = string;
            asJson = json;
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

        public boolean doesReturnsJson() {
            return asJson;
        }
    }
}
