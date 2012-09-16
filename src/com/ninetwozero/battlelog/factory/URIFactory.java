
package com.ninetwozero.battlelog.factory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import android.content.UriMatcher;
import com.ninetwozero.battlelog.Battlelog;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;

public class UriFactory {

    private static final String SCHEME = "http";
    private static final String HOST = "battlelog.battlefield.com/bf3";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final int DEFAULT_PORT = -1;

    public interface URI_CODES{
        public static final int RANK_PROGRESS = 10;
        public static final int PERSONA_STATISTICS = 20;
    }

    public interface URI_PATH{
        public static final String RANK_PROGRESS = "rankProgress";
        public static final String PERSONA_STATISTICS = "personaStatistics";
    }

    public static final UriMatcher URI_MATCHER;
    static{
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(Battlelog.AUTHORITY, URI_PATH.RANK_PROGRESS+"/", UriFactory.URI_CODES.RANK_PROGRESS);
        URI_MATCHER.addURI(Battlelog.AUTHORITY, URI_PATH.PERSONA_STATISTICS+"/", URI_CODES.PERSONA_STATISTICS);
    }

    private static URI createUri(String path) {
        return prepareURI(path, null);
    }

    private static URI createUri(String path, List<NameValuePair> params) {
        String query = URLEncodedUtils.format(params, DEFAULT_ENCODING);
        return prepareURI(path, query);
    }

    private static URI prepareURI(String path, String query) {
        try {
            return URIUtils.createURI(SCHEME, HOST, DEFAULT_PORT, path, query, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static URI personaOverview(long personaId, int platformId) {
        String path = new StringBuilder("/overviewPopulateStats/").
                append(personaId).append("/None/").append(platformId).toString();
        return createUri(path);
    }
}
