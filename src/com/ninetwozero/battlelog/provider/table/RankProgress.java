package com.ninetwozero.battlelog.provider.table;

import android.net.Uri;
import android.provider.BaseColumns;
import com.ninetwozero.battlelog.Battlelog;

public class RankProgress {

    public static final Uri URI = Uri.parse("content://" + Battlelog.AUTHORITY + "/rankProgress");

    public interface Columns extends BaseColumns {
        public static final String ID = "_id";
        public static final String PERSONA_ID = "personaId";
        public static final String RANK_TITLE = "rankTitle";
        public static final String CURRENT_RANK_SCORE = "currentRankScore";
        public static final String NEXT_RANK_SCORE = "nextRankScore";
        public static final String SCORE = "score";
    }

    public static final String[] RANK_PROGRESS_PROJECTION = new String[]{Columns.ID, Columns.PERSONA_ID,
            Columns.RANK_TITLE, Columns.CURRENT_RANK_SCORE, Columns.NEXT_RANK_SCORE, Columns.SCORE};
}
