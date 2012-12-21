package com.ninetwozero.bf3droid.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.Statistics;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.PersonaInfo;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.PersonaStatsOverview;
import com.ninetwozero.bf3droid.provider.table.PersonaStatistics;
import com.ninetwozero.bf3droid.misc.NumberFormatter;

import java.util.ArrayList;
import java.util.List;

import static com.ninetwozero.bf3droid.misc.NumberFormatter.format;
import static com.ninetwozero.bf3droid.misc.PublicUtils.timeToLiteral;

public class PersonaStatisticsDAO {

    public static List<Statistics> personaStaticsFromCursor(Cursor cursor) {
        List<Statistics> list = new ArrayList<Statistics>();
        list.add(new Statistics(R.string.info_xml_kills, valueFromCursor(cursor, PersonaStatistics.Columns.KILLS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_assists, valueFromCursor(cursor, PersonaStatistics.Columns.KILL_ASSISTS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_vehicles_destroyed, valueFromCursor(cursor, PersonaStatistics.Columns.VEHICLE_DESTROYED), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_vehicles_assisted_with, valueFromCursor(cursor, PersonaStatistics.Columns.VEHICLE_ASSISTS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_heals, valueFromCursor(cursor, PersonaStatistics.Columns.HEALS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_revives, valueFromCursor(cursor, PersonaStatistics.Columns.REVIVES), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_repairs, valueFromCursor(cursor, PersonaStatistics.Columns.REPAIRS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_resupplies, valueFromCursor(cursor, PersonaStatistics.Columns.RESUPPLIES), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_deaths, valueFromCursor(cursor, PersonaStatistics.Columns.DEATHS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_kd_ratio, valueFromCursor(cursor, PersonaStatistics.Columns.KD_RATIO), R.style.InfoSubHeading));
        list.add(new Statistics(R.string.info_xml_wins, valueFromCursor(cursor, PersonaStatistics.Columns.WINS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_losses, valueFromCursor(cursor, PersonaStatistics.Columns.LOSSES), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_wl_ratio, valueFromCursor(cursor, PersonaStatistics.Columns.WL_RATIO), R.style.InfoSubHeading));
        list.add(new Statistics(R.string.info_xml_accuracy, valueFromCursor(cursor, PersonaStatistics.Columns.ACCURACY), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_longest_headshot, valueFromCursor(cursor, PersonaStatistics.Columns.LONGEST_HEADSHOT), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_longest_killstreak, valueFromCursor(cursor, PersonaStatistics.Columns.LONGEST_KILLSTREAK), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_skill_rating, valueFromCursor(cursor, PersonaStatistics.Columns.SKILLRATING), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_time_played, valueFromCursor(cursor, PersonaStatistics.Columns.TIME_PLAYED), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_score_minute, valueFromCursor(cursor, PersonaStatistics.Columns.SCORE_PER_MINUTE), R.style.InfoSubHeading));
        return list;
    }

    public static List<Statistics> personaStatisticsFromJSON(PersonaInfo pi) {
        PersonaStatsOverview pso = pi.getStatsOverview();
        List<Statistics> list = new ArrayList<Statistics>();
        list.add(new Statistics(R.string.info_xml_kills, NumberFormatter.format(pso.getKills()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_assists, NumberFormatter.format(pso.getKillAssists()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_vehicles_destroyed, NumberFormatter.format(pso.getVehiclesDestroyed()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_vehicles_assisted_with, NumberFormatter.format(pso.getVehiclesDestroyedAssists()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_heals, NumberFormatter.format(pso.getHeals()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_revives, NumberFormatter.format(pso.getRevives()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_repairs, NumberFormatter.format(pso.getRepairs()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_resupplies, NumberFormatter.format(pso.getResupplies()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_deaths, NumberFormatter.format(pso.getDeaths()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_kd_ratio, NumberFormatter.format(pso.getKdRatio()), R.style.InfoSubHeading));
        list.add(new Statistics(R.string.info_xml_wins, NumberFormatter.format(pso.getGameWon()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_losses, NumberFormatter.format(pso.getGameLost()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_wl_ratio, NumberFormatter.format(pso.getWlRatio()), R.style.InfoSubHeading));
        list.add(new Statistics(R.string.info_xml_accuracy, NumberFormatter.format(pso.getAccuracy()) + "%", R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_longest_headshot, NumberFormatter.format(pso.getLongestHeadshot()) + "m", R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_longest_killstreak, NumberFormatter.format(pso.getLongestKillStreak()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_skill_rating, NumberFormatter.format(pso.getSkill()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_time_played, timeToLiteral(pso.getTimePlayed()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_score_minute, NumberFormatter.format(pso.getScoreMin()), R.style.InfoSubHeading));
        return list;
    }

    public static ContentValues personaStatisticsForDB(PersonaInfo pi, long personaId) {
        PersonaStatsOverview pso = pi.getStatsOverview();
        ContentValues values = new ContentValues();
        values.put(PersonaStatistics.Columns.PERSONA_ID, personaId);
        values.put(PersonaStatistics.Columns.KILLS, NumberFormatter.format(pso.getKills()));
        values.put(PersonaStatistics.Columns.KILL_ASSISTS, NumberFormatter.format(pso.getKillAssists()));
        values.put(PersonaStatistics.Columns.VEHICLE_DESTROYED, NumberFormatter.format(pso.getVehiclesDestroyed()));
        values.put(PersonaStatistics.Columns.VEHICLE_ASSISTS, NumberFormatter.format(pso.getVehiclesDestroyedAssists()));
        values.put(PersonaStatistics.Columns.HEALS, NumberFormatter.format(pso.getHeals()));
        values.put(PersonaStatistics.Columns.REVIVES, NumberFormatter.format(pso.getRevives()));
        values.put(PersonaStatistics.Columns.REPAIRS, NumberFormatter.format(pso.getRepairs()));
        values.put(PersonaStatistics.Columns.RESUPPLIES, NumberFormatter.format(pso.getResupplies()));
        values.put(PersonaStatistics.Columns.DEATHS, NumberFormatter.format(pso.getDeaths()));
        values.put(PersonaStatistics.Columns.KD_RATIO, NumberFormatter.format(pso.getKdRatio()));
        values.put(PersonaStatistics.Columns.WINS, NumberFormatter.format(pso.getGameWon()));
        values.put(PersonaStatistics.Columns.LOSSES, NumberFormatter.format(pso.getGameLost()));
        values.put(PersonaStatistics.Columns.WL_RATIO, NumberFormatter.format(pso.getWlRatio()));
        values.put(PersonaStatistics.Columns.ACCURACY, NumberFormatter.format(pso.getAccuracy()) + "%");
        values.put(PersonaStatistics.Columns.LONGEST_HEADSHOT, NumberFormatter.format(pso.getLongestHeadshot()) + "m");
        values.put(PersonaStatistics.Columns.LONGEST_KILLSTREAK, NumberFormatter.format(pso.getLongestKillStreak()));
        values.put(PersonaStatistics.Columns.SKILLRATING, NumberFormatter.format(pso.getSkill()));
        values.put(PersonaStatistics.Columns.TIME_PLAYED, String.valueOf(timeToLiteral(pso.getTimePlayed())));
        values.put(PersonaStatistics.Columns.SCORE_PER_MINUTE, NumberFormatter.format(pso.getScoreMin()));
        return values;
    }

    private static String valueFromCursor(Cursor cursor, String name) {
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
    }
}
