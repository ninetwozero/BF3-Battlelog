package com.ninetwozero.battlelog.provider.table;

import android.net.Uri;
import android.provider.BaseColumns;
import com.ninetwozero.battlelog.Battlelog;

public class RankProgress {

    public static final Uri URI = Uri.parse("content://" + Battlelog.AUTHORITY
            + "/rankProgress/");

    public interface Columns extends BaseColumns {
        public static final String ID = "_id";
        public static final String PERSONA_ID = "personaId";
        public static final String PERSONA_NAME = "personaName";
        public static final String PLATFORM = "platform";
        public static final String RANK = "rank";
        public static final String CURRENT_RANK_SCORE = "currentRankScore";
        public static final String NEXT_RANK_SCORE = "nextRankScore";
        public static final String SCORE = "score";
    }

    public static final String[] RANK_PROGRESS_PROJECTION = new String[]{
            Columns.ID, Columns.PERSONA_ID, Columns.PERSONA_NAME,
            Columns.PLATFORM, Columns.RANK, Columns.CURRENT_RANK_SCORE,
            Columns.NEXT_RANK_SCORE, Columns.SCORE};

    private long personaId;
    private String personaName;
    private String platform;
    private int rank;
    private long currentRankScore;
    private long nextRankScore;
    private long score;

    public long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(long personaId) {
        this.personaId = personaId;
    }

    public String getPersonaName() {
        return personaName;
    }

    public void setPersonaName(String personaName) {
        this.personaName = personaName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getCurrentRankScore() {
        return currentRankScore;
    }

    public void setCurrentRankScore(long currentRankScore) {
        this.currentRankScore = currentRankScore;
    }

    public long getNextRankScore() {
        return nextRankScore;
    }

    public void setNextRankScore(long nextRankScore) {
        this.nextRankScore = nextRankScore;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}