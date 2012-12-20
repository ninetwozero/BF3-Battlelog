package com.ninetwozero.bf3droid.server;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import com.ninetwozero.bf3droid.server.Bf3ServerCall._403Exception;

public class SimpleHttpCaller {

    public interface SimpleHttpCallerCallback {
        void onSimpleHttpCallSuccess(HttpResponse response);

        void onSimpleHttpCallFailure(HttpResponse response)
                throws _403Exception;
    }

    private final SimpleHttpCallerCallback callback;
    private final HttpClient httpClient;
    private final HttpUriRequest request;

    public SimpleHttpCaller(HttpClient httpClient, HttpUriRequest request,
                            SimpleHttpCallerCallback callback) {
        this.callback = callback;
        this.request = request;
        this.httpClient = httpClient;
    }

    public void execute() throws IOException {
        HttpResponse response = httpClient.execute(request);
        handleResponse(response);
        request.abort();
    }

    private void handleResponse(HttpResponse response) throws _403Exception {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            callback.onSimpleHttpCallSuccess(response);
        } else {
            callback.onSimpleHttpCallFailure(response);
        }
    }
}
