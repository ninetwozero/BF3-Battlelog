package com.ninetwozero.battlelog.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import com.ninetwozero.battlelog.server.SimpleHttpCaller.SimpleHttpCallerCallback;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

public class Bf3ServerCall implements SimpleHttpCallerCallback {


    public interface Bf3ServerCallCallback {
        void onBf3CallSuccess(/*JsonNode node*/);

        void onBf3CallFailure();

        void onBf3CallError();
    }

    protected final Bf3ServerCallCallback callback;
    protected final URI call;
    private final HttpClient httpClient;

    public Bf3ServerCall(URI call, Bf3ServerCallCallback callback){
        this.callback = callback;
        this.call = call;
        this.httpClient = Bf3HttpClient.newInstance();
    }

    public void execute(){
        try{
            buildHttpCaller().execute();
        } catch (final Exception e){
            callback.onBf3CallError();
        }
    }

    protected SimpleHttpCaller buildHttpCaller() throws Exception{
        HttpGet request = new HttpGet(call);
        return new SimpleHttpCaller(httpClient, request, this);
    }

    @Override
    public void onSimpleHttpCallSuccess(HttpResponse response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onSimpleHttpCallFailure(HttpResponse response) throws _403Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Reader getJSONReader(HttpResponse response){
        InputStream data = null;
        Reader reader = null;
        try {
            data=  response.getEntity().getContent();
            reader = new InputStreamReader(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

    @SuppressWarnings("serial")
    public static class _403Exception extends IOException{}
}
