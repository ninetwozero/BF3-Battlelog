
package com.ninetwozero.battlelog.server;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class Bf3HttpClient implements HttpClient {

    public static Bf3HttpClient newInstance() {
        return new Bf3HttpClient();
    }

    // TODO implement methods
    @Override
    public HttpParams getParams() {
        return null;
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return null;
    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest) throws IOException,
            ClientProtocolException {
        return null;
    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext)
            throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws IOException,
            ClientProtocolException {
        return null;
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext)
            throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler)
            throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest,
            ResponseHandler<? extends T> responseHandler, HttpContext httpContext)
            throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest,
            ResponseHandler<? extends T> responseHandler) throws IOException,
            ClientProtocolException {
        return null;
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest,
            ResponseHandler<? extends T> responseHandler, HttpContext httpContext)
            throws IOException, ClientProtocolException {
        return null;
    }
}
