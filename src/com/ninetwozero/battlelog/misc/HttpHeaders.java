package com.ninetwozero.battlelog.misc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public final class HttpHeaders {

    private static final Header[] mEmptyHeader = new BasicHeader[]{};

    private static final Header[] mAjaxGetHeader = new BasicHeader[]{
        new BasicHeader("X-Requested-With", "XMLHttpRequest"),
        new BasicHeader("X-AjaxNavigation", "1"),
        new BasicHeader("Accept", "application/json, text/javascript, */*"),
        new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
    };    
    private static final Header[] mAjaxModGetHeader = new BasicHeader[]{
	    new BasicHeader("X-Requested-With", "XMLHttpRequest"),
	    new BasicHeader("Accept", "application/json, text/javascript, */*"),
    };
    private static final Header[] mJsonGetHeader = new BasicHeader[]{
            new BasicHeader("Accept", "application/json, text/javascript, */*"),
            new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
            new BasicHeader("X-JSON", "1"),
    };

    private static final Header[] mAjaxPostHeader = new BasicHeader[]{
            new BasicHeader("Host", "battlelog.battlefield.com"),
            new BasicHeader("X-Requested-With", "XMLHttpRequest"),
            new BasicHeader("Accept-Encoding", "gzip, deflate"),
            new BasicHeader("Referer", Constants.URL_MAIN),
            new BasicHeader("Accept", "application/json, text/javascript, */*"),
            new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
            new BasicHeader("X-AjaxNavigation", "1")
    };
    private static final Header[] mJsonPostHeader = new BasicHeader[]{
            new BasicHeader("Host", "battlelog.battlefield.com"),
            new BasicHeader("X-Requested-With", "XMLHttpRequest"),
            new BasicHeader("Accept-Encoding", "gzip, deflate"),
            new BasicHeader("Referer", Constants.URL_MAIN),
            new BasicHeader("Accept", "application/json, text/javascript, */*"),
            new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
    };

    private static final Header[] mJsonPostHeaderWithCharset = new BasicHeader[]{
            new BasicHeader("Host", "battlelog.battlefield.com"),
            new BasicHeader("X-Requested-With", "XMLHttpRequest"),
            new BasicHeader("Accept-Encoding", "gzip, deflate"),
            new BasicHeader("Referer", Constants.URL_MAIN),
            new BasicHeader("Accept", "application/json, text/javascript, */*"),
            new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
            new BasicHeader("Accept-Charset", "utf-8,ISO-8859-1;")
    };

    public static final List<Header[]> GET_HEADERS = new ArrayList<Header[]>() {

        private static final long serialVersionUID = 8337984543829725911L;
        {
            add(mEmptyHeader);
            add(mAjaxGetHeader);
            add(mJsonGetHeader);
            add(mAjaxModGetHeader);
        }
    };

    public static final List<Header[]> POST_HEADERS = new ArrayList<Header[]>() {

        private static final long serialVersionUID = -2164345116720011770L;
        {
            add(mEmptyHeader);
            add(mAjaxPostHeader);
            add(mJsonPostHeader);
            add(mJsonPostHeaderWithCharset);
        }
    };

}
