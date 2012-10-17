package com.ninetwozero.battlelog.dao;

import static com.ninetwozero.battlelog.misc.NumberFormatter.format;
import static com.ninetwozero.battlelog.misc.PublicUtils.timeToLiteral;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.Statistics;
import com.ninetwozero.battlelog.jsonmodel.soldierstats.PersonaInfo;
import com.ninetwozero.battlelog.jsonmodel.soldierstats.PersonaStatsOverview;
import com.ninetwozero.battlelog.provider.table.PersonaStatistics;
import com.ninetwozero.battlelog.provider.table.PersonaStatistics.Columns;

public class PersonaStatisticsDAO {

    public static List<Statistics> personaStaticsFromCursor(Cursor cursor) {
        List<Statistics> list = new ArrayList<Statistics>();
        list.add(new Statistics(R.string.info_xml_kills, valueFromCursor(cursor, Columns.KILLS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_assists, valueFromCursor(cursor, Columns.KILL_ASSISTS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_vehicles_destroyed, valueFromCursor(cursor, Columns.VEHICLE_DESTROYED), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_vehicles_assisted_with, valueFromCursor(cursor, Columns.VEHICLE_ASSISTS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_heals, valueFromCursor(cursor, Columns.HEALS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_revives, valueFromCursor(cursor, Columns.REVIVES), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_repairs, valueFromCursor(cursor, Columns.REPAIRS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_resupplies, valueFromCursor(cursor, Columns.RESUPPLIES), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_deaths, valueFromCursor(cursor, Columns.DEATHS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_kd_ratio, valueFromCursor(cursor, Columns.KD_RATIO), R.style.InfoSubHeading));
        list.add(new Statistics(R.string.info_xml_wins, valueFromCursor(cursor, Columns.WINS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_losses, valueFromCursor(cursor, Columns.LOSSES), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_wl_ratio, valueFromCursor(cursor, Columns.WL_RATIO), R.style.InfoSubHeading));
        list.add(new Statistics(R.string.info_xml_accuracy, valueFromCursor(cursor, Columns.ACCURACY), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_longest_headshot, valueFromCursor(cursor, Columns.LONGEST_HEADSHOT), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_longest_killstreak, valueFromCursor(cursor, Columns.LONGEST_KILLSTREAK), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_skill_rating, valueFromCursor(cursor, Columns.SKILLRATING), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_time_played, valueFromCursor(cursor, Columns.TIME_PLAYED), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_score_minute, valueFromCursor(cursor, Columns.SCORE_PER_MINUTE), R.style.InfoSubHeading));
        return list;
    }

    public static List<Statistics> personaStatisticsFromJSON(PersonaInfo pi) {
        PersonaStatsOverview pso = pi.getStatsOverview();
        List<Statistics> list = new ArrayList<Statistics>();
        list.add(new Statistics(R.string.info_xml_kills, format(pso.getKills()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_assists, format(pso.getKillAssists()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_vehicles_destroyed, format(pso.getVehiclesDestroyed()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_vehicles_assisted_with, format(pso.getVehiclesDestroyedAssists()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_heals, format(pso.getHeals()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_revives, format(pso.getRevives()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_repairs, format(pso.getRepairs()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_resupplies, format(pso.getResupplies()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_deaths, format(pso.getDeaths()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_kd_ratio, format(pso.getKdRatio()), R.style.InfoSubHeading));
        list.add(new Statistics(R.string.info_xml_wins, format(pso.getGameWon()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_losses, format(pso.getGameLost()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_wl_ratio, format(pso.getWlRatio()), R.style.InfoSubHeading));
        list.add(new Statistics(R.string.info_xml_accuracy, format(pso.getAccuracy()) + "%", R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_longest_headshot, format(pso.getLongestHeadshot()) + "m", R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_longest_killstreak, format(pso.getLongestKillStreak()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_skill_rating, format(pso.getSkill()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_time_played, timeToLiteral(pso.getTimePlayed()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_score_minute, format(pso.getScoreMin()), R.style.InfoSubHeading));
        return list;
    }

    public static ContentValues personaStatisticsForDB(PersonaInfo pi, long personaId) {
        PersonaStatsOverview pso = pi.getStatsOverview();
        ContentValues values = new ContentValues();
        values.put(PersonaStatistics.Columns.PERSONA_ID, personaId);
        values.put(PersonaStatistics.Columns.KILLS, format(pso.getKills()));
        values.put(PersonaStatistics.Columns.KILL_ASSISTS, format(pso.getKillAssists()));
        values.put(PersonaStatistics.Columns.VEHICLE_DESTROYED, format(pso.getVehiclesDestroyed()));
        values.put(PersonaStatistics.Columns.VEHICLE_ASSISTS, format(pso.getVehiclesDestroyedAssists()));
        values.put(PersonaStatistics.Columns.HEALS, format(pso.getHeals()));
        values.put(PersonaStatistics.Columns.REVIVES, format(pso.getRevives()));
        values.put(PersonaStatistics.Columns.REPAIRS, format(pso.getRepairs()));
        values.put(PersonaStatistics.Columns.RESUPPLIES, format(pso.getResupplies()));
        values.put(PersonaStatistics.Columns.DEATHS, format(pso.getDeaths()));
        values.put(PersonaStatistics.Columns.KD_RATIO, format(pso.getKdRatio()));
        values.put(PersonaStatistics.Columns.WINS, format(pso.getGameWon()));
        values.put(PersonaStatistics.Columns.LOSSES, format(pso.getGameLost()));
        values.put(PersonaStatistics.Columns.WL_RATIO, format(pso.getWlRatio()));
        values.put(PersonaStatistics.Columns.ACCURACY, format(pso.getAccuracy()) + "%");
        values.put(PersonaStatistics.Columns.LONGEST_HEADSHOT, format(pso.getLongestHeadshot()) + "m");
        values.put(PersonaStatistics.Columns.LONGEST_KILLSTREAK, format(pso.getLongestKillStreak()));
        values.put(PersonaStatistics.Columns.SKILLRATING, format(pso.getSkill()));
        values.put(PersonaStatistics.Columns.TIME_PLAYED, String.valueOf(timeToLiteral(pso.getTimePlayed())));
        values.put(PersonaStatistics.Columns.SCORE_PER_MINUTE, format(pso.getScoreMin()));
        return values;
    }

    private static String valueFromCursor(Cursor cursor, String name) {
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
    }
}
