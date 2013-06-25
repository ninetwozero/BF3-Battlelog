package com.ninetwozero.bf3droid.misc;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;

public final class HttpHeaders {

    public static final int EMPTY_HEADER = 0;
    public static final int AJAX_GET_HEADER = 1;
    public static final int AJAX_MOD_GET_HEADER = 2;
    public static final int JSON_GET_HEADER = 3;

    private static final Header[] emptyHeader = new BasicHeader[]{};

    private static final Header[] ajaxGetHeader = new BasicHeader[]{
        new BasicHeader("X-Requested-With", "XMLHttpRequest"),
        new BasicHeader("X-AjaxNavigation", "1"),
        new BasicHeader("Accept", "application/json, text/javascript, */*"),
        new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
    };    
    private static final Header[] ajaxModGetHeader = new BasicHeader[]{
	    new BasicHeader("X-Requested-With", "XMLHttpRequest"),
	    new BasicHeader("Accept", "application/json, text/javascript, */*"),
    };
    private static final Header[] jsonGetHeader = new BasicHeader[]{
            new BasicHeader("Accept", "application/json, text/javascript, */*"),
            new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
            new BasicHeader("X-JSON", "1"),
    };

    public static final List<Header[]> GET_HEADERS = new ArrayList<Header[]>() {

        private static final long serialVersionUID = 8337984543829725911L;
        {
            add(emptyHeader);
            add(ajaxGetHeader);
            add(jsonGetHeader);
            add(ajaxModGetHeader);
        }
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

    public static final List<Header[]> POST_HEADERS = new ArrayList<Header[]>() {

        private static final long serialVersionUID = -2164345116720011770L;
        {
            add(emptyHeader);
            add(ajaxPostHeader);
            add(jsonPostHeader);
            add(jsonPostHeaderWithCharset);
        }
    };

}
