package com.ninetwozero.battlelog.factory;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class URIFactory {

    private static final String SCHEME = "http";
    private static final String HOST = "battlelog.battlefield.com/bf3";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final int DEFAULT_PORT = -1;

    private static URI createUri(String path){
        return prepareURI(path, null);
    }

    private static URI createUri(String path, List<NameValuePair> params){
        String query = URLEncodedUtils.format(params, DEFAULT_ENCODING);
        return prepareURI(path, query);
    }

    private static URI prepareURI(String path, String query){
        try{
            return URIUtils.createURI(SCHEME, HOST, DEFAULT_PORT, path, query, null);
        } catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    private static URI personaOverview(String path){
        return URI.create(path);
    }
}