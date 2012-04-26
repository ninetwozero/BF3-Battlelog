package com.ninetwozero.battlelog.misc;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;

public final class HttpHeaders {

    private static final Header[] ajaxGetHeader = new BasicHeader[]{
            new BasicHeader("X-Requested-With", "XMLHttpRequest"),
            new BasicHeader("X-AjaxNavigation", "1"),
            new BasicHeader("Accept", "application/json, text/javascript, */*"),
            new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
    };

    private static final Header[] jsonGetHeader = new BasicHeader[]{
            new BasicHeader("Accept", "application/json, text/javascript, */*"),
            new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
            new BasicHeader("X-JSON", "1"),
    };

    private static final Header[] ajaxPostHeader = new BasicHeader[]{
            new BasicHeader("Host", "battlelog.battlefield.com"),
            new BasicHeader("X-Requested-With", "XMLHttpRequest"),
            new BasicHeader("Accept-Encoding", "gzip, deflate"),
            new BasicHeader("Referer", Constants.URL_MAIN),
            new BasicHeader("Accept", "application/json, text/javascript, */*"),
            new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
            new BasicHeader("X-AjaxNavigation", "1")
    };
    private static final Header[] jsonPostHeader = new BasicHeader[]{
            new BasicHeader("Host", "battlelog.battlefield.com"),
            new BasicHeader("X-Requested-With", "XMLHttpRequest"),
            new BasicHeader("Accept-Encoding", "gzip, deflate"),
            new BasicHeader("Referer", Constants.URL_MAIN),
            new BasicHeader("Accept", "application/json, text/javascript, */*"),
            new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
    };

    private static final Header[] jsonPostHeaderWithCharset = new BasicHeader[]{
            new BasicHeader("Host", "battlelog.battlefield.com"),
            new BasicHeader("X-Requested-With", "XMLHttpRequest"),
            new BasicHeader("Accept-Encoding", "gzip, deflate"),
            new BasicHeader("Referer", Constants.URL_MAIN),
            new BasicHeader("Accept", "application/json, text/javascript, */*"),
            new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
            new BasicHeader("Accept-Charset", "utf-8,ISO-8859-1;")
    };

    public static final List<Header[]> GET_HEADERS = new ArrayList<Header[]>() {{
        add(ajaxGetHeader);
        add(jsonGetHeader);
    }};

    public static final List<Header[]> POST_HEADERS = new ArrayList<Header[]>() {{
        add(ajaxPostHeader);
        add(jsonPostHeader);
        add(jsonPostHeaderWithCharset);
    }};

}
