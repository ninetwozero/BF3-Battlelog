package com.ninetwozero.battlelog.provider;

import android.content.UriMatcher;
import android.net.Uri;
import com.ninetwozero.battlelog.Battlelog;

public class BattlelogUriMatcher extends UriMatcher{

    public static final String CONTENT = "content://";

    public static final String RANK_PROGRESS = "rankProgress";

    public static final int PERSONA_RANK_PROGRESS = 10;
    public static final int PERSONAS_RANK_PROGRESS = 20;

    public BattlelogUriMatcher(int code) {
        super(code);
        setUp();
    }

    private void setUp(){
        addURI(Battlelog.AUTHORITY, RANK_PROGRESS+"/", PERSONA_RANK_PROGRESS);
    }
}
