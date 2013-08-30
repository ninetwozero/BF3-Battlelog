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
        return new HashMap<String, Statistics>() {{
            put(PersonaStatistics.Columns.KILLS, new Statistics(R.string.info_xml_kills, valueFromCursor(cursor, PersonaStatistics.Columns.KILLS), R.style.Wrap));
            put(PersonaStatistics.Columns.DEATHS, new Statistics(R.string.info_xml_deaths, valueFromCursor(cursor, PersonaStatistics.Columns.DEATHS), R.style.Wrap));
            put(PersonaStatistics.Columns.KD_RATIO, new Statistics(R.string.info_xml_kd_ratio, valueFromCursor(cursor, PersonaStatistics.Columns.KD_RATIO), R.style.InfoSubHeading));
            put(PersonaStatistics.Columns.KILL_ASSISTS, new Statistics(R.string.info_xml_assists, valueFromCursor(cursor, PersonaStatistics.Columns.KILL_ASSISTS), R.style.Wrap));
            put(PersonaStatistics.Columns.SCORE_PER_MINUTE, new Statistics(R.string.info_xml_score_minute, valueFromCursor(cursor, PersonaStatistics.Columns.SCORE_PER_MINUTE), R.style.InfoSubHeading));
            put(PersonaStatistics.Columns.QUITS, new Statistics(R.string.info_xml_quits, valueFromCursor(cursor, PersonaStatistics.Columns.QUITS), R.style.Wrap));
            put(PersonaStatistics.Columns.MCOM_DEFENCE_KILLS, new Statistics(R.string.info_xml_mcom_defence_kills, valueFromCursor(cursor, PersonaStatistics.Columns.MCOM_DEFENCE_KILLS), R.style.Wrap));
            put(PersonaStatistics.Columns.MCOM_DESTROYED, new Statistics(R.string.info_xml_mcom_destroyed, valueFromCursor(cursor, PersonaStatistics.Columns.MCOM_DESTROYED), R.style.Wrap));
            put(PersonaStatistics.Columns.CONQ_FLAG_CAPTURED, new Statistics(R.string.info_xml_conq_flag_captured, valueFromCursor(cursor, PersonaStatistics.Columns.CONQ_FLAG_CAPTURED), R.style.Wrap));
            put(PersonaStatistics.Columns.CONQ_FLAG_DEFENDED, new Statistics(R.string.info_xml_conq_flag_defended, valueFromCursor(cursor, PersonaStatistics.Columns.CONQ_FLAG_DEFENDED), R.style.Wrap));
            put(PersonaStatistics.Columns.VEHICLE_DESTROYED, new Statistics(R.string.info_xml_vehicles_destroyed, valueFromCursor(cursor, PersonaStatistics.Columns.VEHICLE_DESTROYED), R.style.Wrap));
            put(PersonaStatistics.Columns.VEHICLE_ASSISTS, new Statistics(R.string.info_xml_vehicles_assisted_with, valueFromCursor(cursor, PersonaStatistics.Columns.VEHICLE_ASSISTS), R.style.Wrap));
            put(PersonaStatistics.Columns.ACCURACY, new Statistics(R.string.info_xml_accuracy, valueFromCursor(cursor, PersonaStatistics.Columns.ACCURACY), R.style.Wrap));
            put(PersonaStatistics.Columns.LONGEST_HEADSHOT, new Statistics(R.string.info_xml_longest_headshot, valueFromCursor(cursor, PersonaStatistics.Columns.LONGEST_HEADSHOT), R.style.Wrap));
            put(PersonaStatistics.Columns.LONGEST_KILLSTREAK, new Statistics(R.string.info_xml_longest_killstreak, valueFromCursor(cursor, PersonaStatistics.Columns.LONGEST_KILLSTREAK), R.style.Wrap));
            put(PersonaStatistics.Columns.SKILLRATING, new Statistics(R.string.info_xml_skill_rating, valueFromCursor(cursor, PersonaStatistics.Columns.SKILLRATING), R.style.Wrap));
            put(PersonaStatistics.Columns.AVENGER_KILLS, new Statistics(R.string.info_xml_avenger_kills, valueFromCursor(cursor, PersonaStatistics.Columns.AVENGER_KILLS), R.style.Wrap));
            put(PersonaStatistics.Columns.SAVIOR_KILLS, new Statistics(R.string.info_xml_savior_kills, valueFromCursor(cursor, PersonaStatistics.Columns.SAVIOR_KILLS), R.style.Wrap));
            put(PersonaStatistics.Columns.DOGTAG_TAKEN, new Statistics(R.string.info_xml_dogtag_taken, valueFromCursor(cursor, PersonaStatistics.Columns.DOGTAG_TAKEN), R.style.Wrap));
            put(PersonaStatistics.Columns.CTF_CAPTURE_FLAG, new Statistics(R.string.info_xml_ctf_capture_flag, valueFromCursor(cursor, PersonaStatistics.Columns.CTF_CAPTURE_FLAG), R.style.Wrap));
            put(PersonaStatistics.Columns.SQUAD_SCORE_BONUS, new Statistics(R.string.info_xml_squad_score_bonus, valueFromCursor(cursor, PersonaStatistics.Columns.SQUAD_SCORE_BONUS), R.style.Wrap));
            put(PersonaStatistics.Columns.REVIVES, new Statistics(R.string.info_xml_revives, valueFromCursor(cursor, PersonaStatistics.Columns.REVIVES), R.style.Wrap));
            put(PersonaStatistics.Columns.REPAIRS, new Statistics(R.string.info_xml_repairs, valueFromCursor(cursor, PersonaStatistics.Columns.REPAIRS), R.style.Wrap));
            put(PersonaStatistics.Columns.HEALS, new Statistics(R.string.info_xml_heals, valueFromCursor(cursor, PersonaStatistics.Columns.HEALS), R.style.Wrap));
            put(PersonaStatistics.Columns.RESUPPLIES, new Statistics(R.string.info_xml_resupplies, valueFromCursor(cursor, PersonaStatistics.Columns.RESUPPLIES), R.style.Wrap));
            put(PersonaStatistics.Columns.SHOT_FIRED, new Statistics(R.string.info_xml_shot_fired, valueFromCursor(cursor, PersonaStatistics.Columns.SHOT_FIRED), R.style.Wrap));
            put(PersonaStatistics.Columns.HIG_NEMESIS_STREAK, new Statistics(R.string.info_xml_highest_nemesis_streak, valueFromCursor(cursor, PersonaStatistics.Columns.HIG_NEMESIS_STREAK), R.style.Wrap));
            put(PersonaStatistics.Columns.NEMESIS_KILLS, new Statistics(R.string.info_xml_nemesis_kills, valueFromCursor(cursor, PersonaStatistics.Columns.NEMESIS_KILLS), R.style.Wrap));
            put(PersonaStatistics.Columns.SUPPRESSION_ASSISTS, new Statistics(R.string.info_xml_suppression_assists, valueFromCursor(cursor, PersonaStatistics.Columns.SUPPRESSION_ASSISTS), R.style.Wrap));
            put(PersonaStatistics.Columns.WINS, new Statistics(R.string.info_xml_wins, valueFromCursor(cursor, PersonaStatistics.Columns.WINS), R.style.Wrap));
            put(PersonaStatistics.Columns.LOSSES, new Statistics(R.string.info_xml_losses, valueFromCursor(cursor, PersonaStatistics.Columns.LOSSES), R.style.Wrap));
            put(PersonaStatistics.Columns.WL_RATIO, new Statistics(R.string.info_xml_wl_ratio, valueFromCursor(cursor, PersonaStatistics.Columns.WL_RATIO), R.style.InfoSubHeading));
            put(PersonaStatistics.Columns.TIME_PLAYED, new Statistics(R.string.info_xml_time_played, valueFromCursor(cursor, PersonaStatistics.Columns.TIME_PLAYED), R.style.Wrap));
        }};
    }

    public static Map<String, Statistics> personaStatisticsFromJSON(PersonaInfo pi) {
        final PersonaStatsOverview pso = pi.getStatsOverview();
        Map<String, Statistics> map = new HashMap<String, Statistics>();
        map.put(PersonaStatistics.Columns.KILLS, new Statistics(R.string.info_xml_kills, NumberFormatter.format(pso.getKills()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.DEATHS, new Statistics(R.string.info_xml_deaths, NumberFormatter.format(pso.getDeaths()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.KD_RATIO, new Statistics(R.string.info_xml_kd_ratio, kdRatio(pso.getKills(), pso.getDeaths()), R.style.InfoSubHeading));
        map.put(PersonaStatistics.Columns.KILL_ASSISTS, new Statistics(R.string.info_xml_assists, NumberFormatter.format(pso.getKillAssists()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.SCORE_PER_MINUTE, new Statistics(R.string.info_xml_score_minute, NumberFormatter.format(pso.getScoreMin()), R.style.InfoSubHeading));
        map.put(PersonaStatistics.Columns.QUITS, new Statistics(R.string.info_xml_quits, NumberFormatter.format(pso.getQuitPercentage()) + "%", R.style.Wrap));
        map.put(PersonaStatistics.Columns.MCOM_DEFENCE_KILLS, new Statistics(R.string.info_xml_mcom_defence_kills, NumberFormatter.format(pso.getMcomDefendKills()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.MCOM_DESTROYED, new Statistics(R.string.info_xml_mcom_destroyed, NumberFormatter.format(pso.getMcomDestroyed()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.CONQ_FLAG_CAPTURED, new Statistics(R.string.info_xml_conq_flag_captured, NumberFormatter.format(pso.getFlagCaptures()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.CONQ_FLAG_DEFENDED, new Statistics(R.string.info_xml_conq_flag_defended, NumberFormatter.format(pso.getFlagsDefended()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.VEHICLE_DESTROYED, new Statistics(R.string.info_xml_vehicles_destroyed, NumberFormatter.format(pso.getVehiclesDestroyed()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.VEHICLE_ASSISTS, new Statistics(R.string.info_xml_vehicles_assisted_with, NumberFormatter.format(pso.getVehiclesDestroyedAssists()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.ACCURACY, new Statistics(R.string.info_xml_accuracy, NumberFormatter.format(pso.getAccuracy()) + "%", R.style.Wrap));
        map.put(PersonaStatistics.Columns.LONGEST_HEADSHOT, new Statistics(R.string.info_xml_longest_headshot, NumberFormatter.format(pso.getLongestHeadshot()) + "m", R.style.Wrap));
        map.put(PersonaStatistics.Columns.LONGEST_KILLSTREAK, new Statistics(R.string.info_xml_longest_killstreak, NumberFormatter.format(pso.getLongestKillStreak()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.SKILLRATING, new Statistics(R.string.info_xml_skill_rating, NumberFormatter.format(pso.getSkill()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.AVENGER_KILLS, new Statistics(R.string.info_xml_avenger_kills, NumberFormatter.format(pso.getAvengerKills()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.SAVIOR_KILLS, new Statistics(R.string.info_xml_savior_kills, NumberFormatter.format(pso.getSaviorKills()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.DOGTAG_TAKEN, new Statistics(R.string.info_xml_dogtag_taken, NumberFormatter.format(pso.getDogtagsTaken()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.CTF_CAPTURE_FLAG, new Statistics(R.string.info_xml_ctf_capture_flag, NumberFormatter.format(pso.getFlagRunner()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.SQUAD_SCORE_BONUS, new Statistics(R.string.info_xml_squad_score_bonus, NumberFormatter.format(pso.getSquadScoreBonus()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.REPAIRS, new Statistics(R.string.info_xml_repairs, NumberFormatter.format(pso.getRepairs()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.REVIVES, new Statistics(R.string.info_xml_revives, NumberFormatter.format(pso.getRevives()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.HEALS, new Statistics(R.string.info_xml_heals, NumberFormatter.format(pso.getHeals()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.RESUPPLIES, new Statistics(R.string.info_xml_resupplies, NumberFormatter.format(pso.getResupplies()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.SHOT_FIRED, new Statistics(R.string.info_xml_shot_fired, NumberFormatter.format(pso.getShotsFired()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.HIG_NEMESIS_STREAK, new Statistics(R.string.info_xml_highest_nemesis_streak, NumberFormatter.format(pso.getHighestNemesisStreak()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.NEMESIS_KILLS, new Statistics(R.string.info_xml_nemesis_kills, NumberFormatter.format(pso.getNemesisKills()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.SUPPRESSION_ASSISTS, new Statistics(R.string.info_xml_suppression_assists, NumberFormatter.format(pso.getSuppresionsAssists()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.WINS, new Statistics(R.string.info_xml_wins, NumberFormatter.format(pso.getGameWon()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.LOSSES, new Statistics(R.string.info_xml_losses, NumberFormatter.format(pso.getGameLost()), R.style.Wrap));
        map.put(PersonaStatistics.Columns.WL_RATIO, new Statistics(R.string.info_xml_wl_ratio, NumberFormatter.format(pso.getWlRatio()), R.style.InfoSubHeading));
        map.put(PersonaStatistics.Columns.TIME_PLAYED, new Statistics(R.string.info_xml_time_played, timeToLiteral(pso.getTimePlayed()), R.style.Wrap));
        return map;
    }

    public static ContentValues personaStatisticsForDB(Map<String, Statistics> map, long personaId) {
        ContentValues values = new ContentValues();
        values.put(PersonaStatistics.Columns.PERSONA_ID, personaId);
        Set<String> keys = map.keySet();
        for (String key : keys) {
            values.put(key, map.get(key).getValue());
        }
        return values;
    }

    private static String valueFromCursor(Cursor cursor, String name) {
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
    }

    private static String kdRatio(long kills, long deaths) {
        return NumberFormatter.format((double)kills / deaths);
    }
}
