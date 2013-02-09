/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ninetwozero.bf3droid.datatype.PostData;
import com.ninetwozero.bf3droid.datatype.RequestHandlerException;
import com.ninetwozero.bf3droid.datatype.ShareableCookie;
import com.ninetwozero.bf3droid.misc.HttpHeaders;
import com.ninetwozero.bf3droid.server.HttpClientFactory;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

public class RequestHandler {

    public static DefaultHttpClient httpClient = HttpClientFactory.getThreadSafeClient();

    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    public static final int HEADER_NORMAL = 0;
    public static final int HEADER_AJAX = 1;
    public static final int HEADER_JSON = 2;
    public static final int HEADER_GZIP = 3;

    public String get(String link, int extraHeaders) throws RequestHandlerException {
        if ("".equals(link)) {
            throw new RequestHandlerException("No link found.");
        }

        try {
            HttpGet httpGet = new HttpGet(link.replace(" ", "%20"));
            httpGet.setHeaders(HttpHeaders.GET_HEADERS.get(extraHeaders));
            httpGet.setHeader("Referer", link);
            HttpResponse httpResponse = RequestHandler.httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();

            if (httpEntity != null) {
                return EntityUtils.toString(httpEntity);
            }

        } catch (ClientProtocolException e) {
            Log.w("requestHandler", e.toString());
        } catch (IOException e) {
            Log.w("requestHandler", e.toString());
        } catch (Exception ex) {
            throw new RequestHandlerException(ex.getMessage());
        }
        return "";
    }

    public HttpEntity getHttpEntity(String link, boolean extraHeaders) throws RequestHandlerException {
        if ("".equals(link)) {
            throw new RequestHandlerException("No link found.");
        }
        
        try {
            HttpGet httpGet = new HttpGet(link.replace(" ", "%20"));

            if (extraHeaders) {
                httpGet.setHeaders(HttpHeaders.GET_HEADERS.get(0));
                httpGet.setHeader("Referer", link);
            }

            HttpResponse httpResponse = RequestHandler.httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();

            return httpEntity;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public Bitmap getImageFromStream(String link, boolean extraHeaders) throws RequestHandlerException {
        if ("".equals(link)) {
            throw new RequestHandlerException("No link found.");
        }
        Bitmap image = null;
        try {
            HttpGet httpGet = new HttpGet(link);

            if (extraHeaders) {
                httpGet.setHeaders(HttpHeaders.GET_HEADERS.get(0));
                httpGet.setHeader("Referer", link);
            }
            HttpResponse httpResponse = RequestHandler.httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();

            if (httpEntity != null) {
                image = BitmapFactory.decodeStream(httpEntity.getContent());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return image;

    }

    public boolean saveFileFromURI(String link, String directory, String filename) throws RequestHandlerException {
        if ("".equals(link)) {
            throw new RequestHandlerException("No link found.");
        }

        byte[] httpContent = null;
        FileOutputStream fileStream = null;
        String path = "";

        if ("".equals(filename)) {
            path = directory + System.currentTimeMillis();
        } else {
            path = directory + filename;
        }

        try {
            HttpGet httpGet = new HttpGet(link);
            HttpResponse httpResponse = RequestHandler.httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();

            if (httpEntity != null) {
                if (httpResponse.containsHeader("Encoding-Type") && 
                	httpResponse.getFirstHeader("Encoding-Type").getValue().equalsIgnoreCase("gzip")
                ) {
                    httpEntity = new InflatingEntity(httpEntity);
                    httpContent = getStringFromGzipStream(new GZIPInputStream(httpEntity.getContent())).getBytes();
                } else {
                    httpContent = EntityUtils.toString(httpEntity).getBytes();
                }
            }

            if (httpContent == null || httpContent.length <= 0) {
                return false;
            } else {
                fileStream = new FileOutputStream(path);
                for (byte b : httpContent) {
                    fileStream.write(b);
                }

                fileStream.flush();
                fileStream.close();
                return true;
            }
            
        } catch (UnknownHostException ex) {
            throw new RequestHandlerException(
                    "Host unreachable - please restart your 3G connection and try again.");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RequestHandlerException("No file found");
        }
    }

    public String post(String link, PostData[] postDataArray, int extraHeaders) throws RequestHandlerException {
        if ("".equals(link)) {
            throw new RequestHandlerException("No link found.");
        }

        HttpPost httpPost = new HttpPost(link);

        if (extraHeaders > 0) {
            httpPost.setHeaders(HttpHeaders.POST_HEADERS.get(extraHeaders));
        }

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        HttpEntity httpEntity;
        HttpResponse httpResponse;

        for (PostData data : postDataArray) {
            nameValuePairs.add(
            	new BasicNameValuePair(data.getField(), (data.isHash()) ? this.hash(data.getValue()) : data.getValue())
            );
        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            httpResponse = httpClient.execute(httpPost);
            httpEntity = httpResponse.getEntity();

            if (httpEntity != null) {
                return EntityUtils.toString(httpEntity);
            }

        } catch (UnknownHostException ex) {
            throw new RequestHandlerException(
                    "Host unreachable - please restart your 3G connection and try again.");

        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }

    /**
     * @author http://androidgenuine.com/?p=402
     */
    public String hash(String str) {

        try {
            // Create SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getStringFromGzipStream(GZIPInputStream in) {

        // Initialize variables
        BufferedInputStream buff_in = new BufferedInputStream(in);
        int byte_read = 0;
        int buff_size = 512;
        byte[] byte_buffer = new byte[buff_size];
        ByteArrayBuffer buff_array = new ByteArrayBuffer(50);

        try {
        	// Read the buffer until -1 (EOL)
        	while (true) {
                byte_read = buff_in.read(byte_buffer);
                if (byte_read == -1) {
                    break;
                }

                buff_array.append(byte_buffer, 0, byte_read);
            }
            return new String(buff_array.toByteArray());

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void close() {
        if (httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().closeExpiredConnections();
        }
    }

    public static ArrayList<ShareableCookie> getCookies() {
        List<Cookie> cookies = RequestHandler.httpClient.getCookieStore().getCookies();
        List<ShareableCookie> sharableCookies = new ArrayList<ShareableCookie>();

        if (!cookies.isEmpty()) {
            for (Cookie c : cookies) {
                sharableCookies.add(new ShareableCookie(c));
            }
        }
        return (ArrayList<ShareableCookie>) sharableCookies;
    }

    @Deprecated
    public static void setCookies(ShareableCookie sc) {
/*
        // Init
        CookieStore cookieStore = RequestHandler.httpClient.getCookieStore();

        // Did we have an icicle?
        if (cookieStore.getCookies().isEmpty()) {

            BasicClientCookie tempCookie = new BasicClientCookie(sc.getName(),
                    sc.getValue());
            tempCookie.setDomain(sc.getDomain());
            cookieStore.addCookie(tempCookie);
            RequestHandler.httpClient.setCookieStore(cookieStore);

        }*/

    }
    @Deprecated
    public static void setCookies(List<ShareableCookie> sc) {

        /*// Init
        CookieStore cookieStore = RequestHandler.httpClient.getCookieStore();
        if (cookieStore.getCookies().isEmpty()) {

            // Set it up
            List<ShareableCookie> serializedCookies = sc;

            // Loop & add
            for (ShareableCookie sCookie : serializedCookies) {
                BasicClientCookie tempCookie = new BasicClientCookie(
                        sCookie.getName(), sCookie.getValue());
                tempCookie.setDomain(sCookie.getDomain());
                cookieStore.addCookie(tempCookie);
            }
            RequestHandler.httpClient.setCookieStore(cookieStore);
        }*/
    }

    static {
        httpClient.addRequestInterceptor(
            new HttpRequestInterceptor() {
                public void process(HttpRequest request, HttpContext context) {
                    if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                        request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                    }
                }
            }
        );

        httpClient.addResponseInterceptor(
                new HttpResponseInterceptor() {
                    public void process(HttpResponse response, HttpContext context) {
                        final HttpEntity entity = response.getEntity();
                        final Header encoding = entity.getContentEncoding();

                        if (encoding != null) {
                            for (HeaderElement element : encoding.getElements()) {
                                if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                                    response.setEntity(new InflatingEntity(response
                                            .getEntity()));
                                    break;
                                }
                            }
                        }
                    }
                }
        );
    }

    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }

    public static String generateUrl(String base, Object... data) {
        for (Object d : data) {
            base = base.replaceFirst("\\{[^\\}]+\\}", String.valueOf(d));
        }
        return base;
    }

    public static PostData[] generatePostData(String[] fields, Object... data) {
        PostData[] postData = new PostData[fields.length];
        for (int i = 0, max = postData.length; i < max; i++) {
            if (data[i] == null) {
                continue;
            }
            postData[i] = new PostData(fields[i], String.valueOf(data[i]));
        }
        return postData;
    }
}
