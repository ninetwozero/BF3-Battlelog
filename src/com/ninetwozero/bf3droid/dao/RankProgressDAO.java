package com.ninetwozero.bf3droid.dao;

import static com.ninetwozero.bf3droid.misc.ResolvePlatform.platformName;
import android.content.ContentValues;
import android.database.Cursor;

import com.ninetwozero.bf3droid.jsonmodel.soldierstats.PersonaInfo;
import com.ninetwozero.bf3droid.provider.table.RankProgress;

public class RankProgressDAO {
    public static RankProgress rankProgressFromCursor(Cursor cursor) {
        RankProgress rp = new RankProgress();
        rp.setPersonaId(cursor.getLong(cursor.getColumnIndexOrThrow(RankProgress.Columns.PERSONA_ID)));
        rp.setPersonaName(cursor.getString(cursor.getColumnIndexOrThrow(RankProgress.Columns.PERSONA_NAME)));
        rp.setPlatform(cursor.getString(cursor.getColumnIndexOrThrow(RankProgress.Columns.PLATFORM)));
        rp.setRank(cursor.getInt(cursor.getColumnIndexOrThrow(RankProgress.Columns.RANK)));
        rp.setCurrentRankScore(cursor.getLong(cursor.getColumnIndexOrThrow(RankProgress.Columns.CURRENT_RANK_SCORE)));
        rp.setNextRankScore(cursor.getLong(cursor.getColumnIndexOrThrow(RankProgress.Columns.NEXT_RANK_SCORE)));
        rp.setScore(cursor.getLong(cursor.getColumnIndexOrThrow(RankProgress.Columns.SCORE)));
        return rp;
    }

    public static RankProgress rankProgressFromJSON(PersonaInfo pi) {
        RankProgress rp = new RankProgress();
        rp.setPersonaId(pi.getPersonaId());
        rp.setPersonaName(pi.getUser().getUserName());
        rp.setPlatform(platformName(pi.getPlatform()));
        rp.setRank(pi.getCurrentRank().getLevel());
        rp.setCurrentRankScore(pi.getCurrentRank().getRankPoints());
        rp.setNextRankScore(pi.getNextRank().getRankPoints());
        rp.setScore(pi.getStatsOverview().getScore());
        return rp;
    }

    public static ContentValues rankProgressForDB(PersonaInfo pi, long personaId) {
        ContentValues values = new ContentValues();
        values.put(RankProgress.Columns.PERSONA_ID, personaId);
        values.put(RankProgress.Columns.PERSONA_NAME, pi.getUser().getUserName());
        values.put(RankProgress.Columns.PLATFORM,platformName(pi.getPlatform()));
        values.put(RankProgress.Columns.RANK, pi.getCurrentRank().getLevel());
        values.put(RankProgress.Columns.CURRENT_RANK_SCORE, pi.getCurrentRank().getRankPoints());
        values.put(RankProgress.Columns.NEXT_RANK_SCORE, pi.getNextRank().getRankPoints());
        values.put(RankProgress.Columns.SCORE, pi.getStatsOverview().getScore());
        return values;
    }
}
