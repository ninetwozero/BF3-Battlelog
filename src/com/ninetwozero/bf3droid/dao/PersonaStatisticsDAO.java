package com.ninetwozero.bf3droid.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.Statistics;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.PersonaInfo;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.PersonaStatsOverview;
import com.ninetwozero.bf3droid.misc.NumberFormatter;
import com.ninetwozero.bf3droid.provider.table.PersonaStatistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.ninetwozero.bf3droid.misc.PublicUtils.timeToLiteral;

public class PersonaStatisticsDAO {

    public static Map<String, Statistics> personaStaticsFromCursor(final Cursor cursor) {
        return new HashMap<String, Statistics >(){{
        put(PersonaStatistics.Columns.KILLS, new Statistics(R.string.info_xml_kills, valueFromCursor(cursor, PersonaStatistics.Columns.KILLS), R.style.Wrap));
        put(PersonaStatistics.Columns.KILL_ASSISTS, new Statistics(R.string.info_xml_assists, valueFromCursor(cursor, PersonaStatistics.Columns.KILL_ASSISTS), R.style.Wrap));
        put(PersonaStatistics.Columns.VEHICLE_DESTROYED, new Statistics(R.string.info_xml_vehicles_destroyed, valueFromCursor(cursor, PersonaStatistics.Columns.VEHICLE_DESTROYED), R.style.Wrap));
        put(PersonaStatistics.Columns.VEHICLE_ASSISTS, new Statistics(R.string.info_xml_vehicles_assisted_with, valueFromCursor(cursor, PersonaStatistics.Columns.VEHICLE_ASSISTS), R.style.Wrap));
        put(PersonaStatistics.Columns.HEALS, new Statistics(R.string.info_xml_heals, valueFromCursor(cursor, PersonaStatistics.Columns.HEALS), R.style.Wrap));
        put(PersonaStatistics.Columns.REVIVES, new Statistics(R.string.info_xml_revives, valueFromCursor(cursor, PersonaStatistics.Columns.REVIVES), R.style.Wrap));
        put(PersonaStatistics.Columns.REPAIRS, new Statistics(R.string.info_xml_repairs, valueFromCursor(cursor, PersonaStatistics.Columns.REPAIRS), R.style.Wrap));
        put(PersonaStatistics.Columns.RESUPPLIES, new Statistics(R.string.info_xml_resupplies, valueFromCursor(cursor, PersonaStatistics.Columns.RESUPPLIES), R.style.Wrap));
        put(PersonaStatistics.Columns.DEATHS, new Statistics(R.string.info_xml_deaths, valueFromCursor(cursor, PersonaStatistics.Columns.DEATHS), R.style.Wrap));
        put(PersonaStatistics.Columns.KD_RATIO, new Statistics(R.string.info_xml_kd_ratio, valueFromCursor(cursor, PersonaStatistics.Columns.KD_RATIO), R.style.InfoSubHeading));
        put(PersonaStatistics.Columns.WINS, new Statistics(R.string.info_xml_wins, valueFromCursor(cursor, PersonaStatistics.Columns.WINS), R.style.Wrap));
        put(PersonaStatistics.Columns.LOSSES, new Statistics(R.string.info_xml_losses, valueFromCursor(cursor, PersonaStatistics.Columns.LOSSES), R.style.Wrap));
        put(PersonaStatistics.Columns.WL_RATIO, new Statistics(R.string.info_xml_wl_ratio, valueFromCursor(cursor, PersonaStatistics.Columns.WL_RATIO), R.style.InfoSubHeading));
        put(PersonaStatistics.Columns.ACCURACY, new Statistics(R.string.info_xml_accuracy, valueFromCursor(cursor, PersonaStatistics.Columns.ACCURACY), R.style.Wrap));
        put(PersonaStatistics.Columns.LONGEST_HEADSHOT, new Statistics(R.string.info_xml_longest_headshot, valueFromCursor(cursor, PersonaStatistics.Columns.LONGEST_HEADSHOT), R.style.Wrap));
        put(PersonaStatistics.Columns.LONGEST_KILLSTREAK, new Statistics(R.string.info_xml_longest_killstreak, valueFromCursor(cursor, PersonaStatistics.Columns.LONGEST_KILLSTREAK), R.style.Wrap));
        put(PersonaStatistics.Columns.SKILLRATING, new Statistics(R.string.info_xml_skill_rating, valueFromCursor(cursor, PersonaStatistics.Columns.SKILLRATING), R.style.Wrap));
        put(PersonaStatistics.Columns.TIME_PLAYED, new Statistics(R.string.info_xml_time_played, valueFromCursor(cursor, PersonaStatistics.Columns.TIME_PLAYED), R.style.Wrap));
        put(PersonaStatistics.Columns.SCORE_PER_MINUTE, new Statistics(R.string.info_xml_score_minute, valueFromCursor(cursor, PersonaStatistics.Columns.SCORE_PER_MINUTE), R.style.InfoSubHeading));
        }};
    }

    public static Map<String, Statistics> personaStatisticsFromJSON(PersonaInfo pi) {
        final PersonaStatsOverview pso = pi.getStatsOverview();
        Map<String, Statistics> map = new HashMap<String, Statistics >();
        map.put(PersonaStatistics.Columns.KILLS, new Statistics(R.string.info_xml_kills, NumberFormatter.format(pso.getKills()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.KILL_ASSISTS, new Statistics(R.string.info_xml_assists, NumberFormatter.format(pso.getKillAssists()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.VEHICLE_DESTROYED, new Statistics(R.string.info_xml_vehicles_destroyed, NumberFormatter.format(pso.getVehiclesDestroyed()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.VEHICLE_ASSISTS, new Statistics(R.string.info_xml_vehicles_assisted_with, NumberFormatter.format(pso.getVehiclesDestroyedAssists()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.HEALS, new Statistics(R.string.info_xml_heals, NumberFormatter.format(pso.getHeals()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.REVIVES, new Statistics(R.string.info_xml_revives, NumberFormatter.format(pso.getRevives()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.REPAIRS, new Statistics(R.string.info_xml_repairs, NumberFormatter.format(pso.getRepairs()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.RESUPPLIES, new Statistics(R.string.info_xml_resupplies, NumberFormatter.format(pso.getResupplies()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.DEATHS, new Statistics(R.string.info_xml_deaths, NumberFormatter.format(pso.getDeaths()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.KD_RATIO, new Statistics(R.string.info_xml_kd_ratio, NumberFormatter.format(pso.getKdRatio()), R.style.InfoSubHeading));
        map.put(PersonaStatistics.Columns.WINS, new Statistics(R.string.info_xml_wins, NumberFormatter.format(pso.getGameWon()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.LOSSES, new Statistics(R.string.info_xml_losses, NumberFormatter.format(pso.getGameLost()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.WL_RATIO, new Statistics(R.string.info_xml_wl_ratio, NumberFormatter.format(pso.getWlRatio()), R.style.InfoSubHeading));
        map.put(PersonaStatistics.Columns.ACCURACY, new Statistics(R.string.info_xml_accuracy, NumberFormatter.format(pso.getAccuracy()) + "%", R.style.Wrap));
        map.put(PersonaStatistics.Columns.LONGEST_HEADSHOT, new Statistics(R.string.info_xml_longest_headshot, NumberFormatter.format(pso.getLongestHeadshot()) + "m", R.style.Wrap));
        map.put(PersonaStatistics.Columns.LONGEST_KILLSTREAK, new Statistics(R.string.info_xml_longest_killstreak, NumberFormatter.format(pso.getLongestKillStreak()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.SKILLRATING, new Statistics(R.string.info_xml_skill_rating, NumberFormatter.format(pso.getSkill()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.TIME_PLAYED, new Statistics(R.string.info_xml_time_played, timeToLiteral(pso.getTimePlayed()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.SCORE_PER_MINUTE, new Statistics(R.string.info_xml_score_minute, NumberFormatter.format(pso.getScoreMin()), R.style.InfoSubHeading));
        return map;
    }

    public static ContentValues personaStatisticsForDB(Map<String, Statistics> map, long personaId) {
        ContentValues values = new ContentValues();
        values.put(PersonaStatistics.Columns.PERSONA_ID, personaId);
        Set<String> keys = map.keySet();
        for(String key : keys) {
            values.put(key, map.get(key).getValue());
        }
        return values;
    }

    private static String valueFromCursor(Cursor cursor, String name) {
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
    }
}
