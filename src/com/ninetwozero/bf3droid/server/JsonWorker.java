package com.ninetwozero.bf3droid.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonWorker {

    public static JsonObject jsonFrom(HttpResponse response) {
        Reader reader = getJSONReader(response);
        JsonParser parser = new JsonParser();
        return parser.parse(reader).getAsJsonObject();
    }

    private static Reader getJSONReader(HttpResponse response) {
        Reader reader = null;
        try {
            InputStream data = response.getEntity().getContent();
            reader = new InputStreamReader(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }
}
