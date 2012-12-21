package com.ninetwozero.bf3droid.provider;

import android.content.UriMatcher;
import com.ninetwozero.bf3droid.Battlelog;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class UriFactory {
    private static final String SCHEME = "http";
    private static final String HOST = "battlelog.battlefield.com/bf3";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final int DEFAULT_PORT = -1;

    public interface URI_CODES {
    	public static final int PROFILE_INFO = 0;
        public static final int RANK_PROGRESS = 10;
        public static final int PERSONA_STATISTICS = 20;
        public static final int SCORE_STATISTICS = 30;
        public static final int PLATOON_INFO = 40;
    }

    public interface URI_PATH {
    	public static final String PROFILE_INFO = "profile";
        public static final String RANK_PROGRESS = "rankProgress";
        public static final String PERSONA_STATISTICS = "personaStatistics";
        public static final String SCORE_STATISTICS = "scoreStatistics";
        public static final String PLATOON_INFO = "platoon";
    }

    public static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(Battlelog.AUTHORITY, URI_PATH.PROFILE_INFO + "/",URI_CODES.PROFILE_INFO);
        URI_MATCHER.addURI(Battlelog.AUTHORITY, URI_PATH.RANK_PROGRESS + "/",URI_CODES.RANK_PROGRESS);
        URI_MATCHER.addURI(Battlelog.AUTHORITY, URI_PATH.PERSONA_STATISTICS + "/", URI_CODES.PERSONA_STATISTICS);
        URI_MATCHER.addURI(Battlelog.AUTHORITY, URI_PATH.SCORE_STATISTICS + "/", URI_CODES.SCORE_STATISTICS);
        URI_MATCHER.addURI(Battlelog.AUTHORITY, URI_PATH.PLATOON_INFO + "/", URI_CODES.PLATOON_INFO);
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

    public static URI getLogginUri(){
        return createUri("/gate/login/");
    }

    public static URI getProfileInformationUri(String username) {
    	String path = new StringBuilder("/user/").append(username).toString();
    	return createUri(path);
    }

    public static URI getProfilePersonasUri(long userId) {
    	String path = new StringBuilder("user/overviewBoxStats/").append(userId).toString();
    	return createUri(path);
    }

    public static URI getPersonaOverviewUri(long personaId, int platformId) {
        String path = new StringBuilder("/overviewPopulateStats/").append(personaId).append("/None/").append(platformId).toString();
        return createUri(path);
    }

    public static URI assignments(String personaName, long personaId, long userId, int platformId){
        String path = new StringBuilder("soldier/missionsPopulateStats/")
                .append(personaName).append("/").append(personaId).append("/")
                .append(userId).append("/").append(platformId).toString();
        return createUri(path);
    }
}