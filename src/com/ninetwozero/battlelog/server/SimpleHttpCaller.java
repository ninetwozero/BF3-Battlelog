package com.ninetwozero.battlelog.server;

import com.ninetwozero.battlelog.server.Bf3ServerCall._403Exception;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

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
