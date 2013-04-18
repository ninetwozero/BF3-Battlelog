package com.ninetwozero.bf3droid.activity.profile.soldier.restorer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ninetwozero.bf3droid.dao.PersonaStatisticsDAO;
import com.ninetwozero.bf3droid.dao.RankProgressDAO;
import com.ninetwozero.bf3droid.dao.ScoreStatisticsDAO;
import com.ninetwozero.bf3droid.datatype.PersonaOverviewStatistics;
import com.ninetwozero.bf3droid.datatype.Statistics;
import com.ninetwozero.bf3droid.provider.table.PersonaStatistics;
import com.ninetwozero.bf3droid.provider.table.RankProgress;
import com.ninetwozero.bf3droid.provider.table.ScoreStatistics;
import com.ninetwozero.bf3droid.service.Restorer;

import java.util.HashMap;
import java.util.Map;

public class PersonaStatisticsRestorer extends Restorer<PersonaOverviewStatistics> {

    private final Context context;
    private final String userType;

    public PersonaStatisticsRestorer(Context context, String userType){
        this.context = context;
        this.userType = userType;
    }

    @Override
    public PersonaOverviewStatistics fetch() {
        RankProgress rankProgress = getRankProgress();
        Map<String, Statistics> personaStats = getPersonaStatistics();
        Map<String, Statistics> scoreStats = getScoreStatistics();
        return new PersonaOverviewStatistics(rankProgress, personaStats, scoreStats);
    }

    @Override
    public void save(PersonaOverviewStatistics pso) {
        updateRankProgressDB(pso);
        updatePersonaStats(pso);
        updateScoreStatistics(pso);
    }

    private RankProgress getRankProgress() {
        RankProgress rankProgress;
        Cursor cursor = context.getContentResolver().query(
                RankProgress.URI,
                RankProgress.RANK_PROGRESS_PROJECTION,
                RankProgress.Columns.PERSONA_ID + "=?",
                new String[]{String.valueOf(selectedPersonaId())},
                null
        );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            rankProgress = RankProgressDAO.rankProgressFromCursor(cursor);
            cursor.close();
            return rankProgress;
        }
        cursor.close();
        return null;
    }

    private Map<String, Statistics> getPersonaStatistics() {
        Cursor cursor = context.getContentResolver().query(
                PersonaStatistics.URI,
                PersonaStatistics.PERSONA_STATS_PROJECTION,
                PersonaStatistics.Columns.PERSONA_ID + "=?",
                new String[]{String.valueOf(selectedPersonaId())},
                null
        );
        Map<String, Statistics> listPersonaStatistics;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            listPersonaStatistics = PersonaStatisticsDAO.personaStaticsFromCursor(cursor);
            cursor.close();
            return listPersonaStatistics;
        }
        cursor.close();
        return new HashMap<String, Statistics>();
    }

    private Map<String, Statistics> getScoreStatistics() {
        Map<String, Statistics> listScoreStatistics;
        Cursor cursor = context.getContentResolver().query(
                ScoreStatistics.URI,
                ScoreStatistics.SCORE_STATISTICS_PROJECTION,
                ScoreStatistics.Columns.PERSONA_ID + "=?",
                new String[]{String.valueOf(selectedPersonaId())},
                null
        );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            listScoreStatistics = ScoreStatisticsDAO.scoreStatisticsFromCursor(cursor);
            cursor.close();
            return listScoreStatistics;
        }
        cursor.close();
        return new HashMap<String, Statistics>();
    }

    private void updateRankProgressDB(PersonaOverviewStatistics pso) {
        ContentValues contentValues = RankProgressDAO.rankProgressForDB(pso.getRankProgress());
        context.getContentResolver().insert(RankProgress.URI, contentValues);
    }

    private void updatePersonaStats(PersonaOverviewStatistics pso) {
        ContentValues contentValues = PersonaStatisticsDAO.personaStatisticsForDB(pso.getPersonaStats(), pso.getRankProgress().getPersonaId());
        context.getContentResolver().insert(PersonaStatistics.URI, contentValues);
    }

    private void updateScoreStatistics(PersonaOverviewStatistics pso) {
        ContentValues contentValues = ScoreStatisticsDAO.scoreStatisticsForDB(pso.getScoreStats(), pso.getRankProgress().getPersonaId());
        context.getContentResolver().insert(ScoreStatistics.URI, contentValues);
    }

    private long selectedPersonaId(){
        return userBy(userType).selectedPersona().getPersonaId();
    }
}
