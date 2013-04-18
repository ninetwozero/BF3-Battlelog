package com.ninetwozero.bf3droid.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.Statistics;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.KitScores;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.PersonaInfo;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.PersonaStatsOverview;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.VehicleScores;
import com.ninetwozero.bf3droid.provider.table.ScoreStatistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.ninetwozero.bf3droid.misc.NumberFormatter.format;

public class ScoreStatisticsDAO {

    public static Map<String, Statistics> scoreStatisticsFromCursor(final Cursor cursor) {
        return new HashMap<String, Statistics>(){{
        put(ScoreStatistics.Columns.ASSAULT, new Statistics(R.string.info_xml_assault, valueFromCursor(cursor, ScoreStatistics.Columns.ASSAULT), R.style.Wrap));
        put(ScoreStatistics.Columns.ENGINEER, new Statistics(R.string.info_xml_engineer, valueFromCursor(cursor, ScoreStatistics.Columns.ENGINEER), R.style.Wrap));
        put(ScoreStatistics.Columns.SUPPORT, new Statistics(R.string.info_xml_support, valueFromCursor(cursor, ScoreStatistics.Columns.SUPPORT), R.style.Wrap));
        put(ScoreStatistics.Columns.RECON, new Statistics(R.string.info_xml_recon, valueFromCursor(cursor, ScoreStatistics.Columns.RECON), R.style.Wrap));
        put(ScoreStatistics.Columns.JET, new Statistics(R.string.info_xml_jet, valueFromCursor(cursor, ScoreStatistics.Columns.JET), R.style.Wrap));
        put(ScoreStatistics.Columns.TANK, new Statistics(R.string.info_xml_tank, valueFromCursor(cursor, ScoreStatistics.Columns.TANK), R.style.Wrap));
        put(ScoreStatistics.Columns.IFV, new Statistics(R.string.info_xml_ifv, valueFromCursor(cursor, ScoreStatistics.Columns.IFV), R.style.Wrap));
        put(ScoreStatistics.Columns.ANTI_AIR, new Statistics(R.string.info_xml_anti_air, valueFromCursor(cursor, ScoreStatistics.Columns.ANTI_AIR), R.style.Wrap));
        put(ScoreStatistics.Columns.ATTACK_HELI, new Statistics(R.string.info_xml_attack_heli, valueFromCursor(cursor, ScoreStatistics.Columns.ATTACK_HELI), R.style.Wrap));
        put(ScoreStatistics.Columns.SCOUT_HELI, new Statistics(R.string.info_xml_scout_heli, valueFromCursor(cursor, ScoreStatistics.Columns.SCOUT_HELI), R.style.Wrap));
        put(ScoreStatistics.Columns.COMBAT, new Statistics(R.string.info_xml_total_in_combat, valueFromCursor(cursor, ScoreStatistics.Columns.COMBAT), R.style.Wrap));
        put(ScoreStatistics.Columns.AWARD, new Statistics(R.string.info_xml_award, valueFromCursor(cursor, ScoreStatistics.Columns.AWARD), R.style.Wrap));
        put(ScoreStatistics.Columns.UNLOCKS, new Statistics(R.string.info_xml_unlocks, valueFromCursor(cursor, ScoreStatistics.Columns.UNLOCKS), R.style.Wrap));
        put(ScoreStatistics.Columns.TOTAL_SCORE, new Statistics(R.string.info_xml_total_score, valueFromCursor(cursor, ScoreStatistics.Columns.TOTAL_SCORE), R.style.InfoSubHeading));
        }};
    }

    public static Map<String, Statistics> scoreStatisticsFromJSON(PersonaInfo pi) {
        final PersonaStatsOverview pso = pi.getStatsOverview();
        final KitScores ks = pi.getStatsOverview().getKitScores();
        final VehicleScores vs = pi.getStatsOverview().getVehicleScores();
        Map<String, Statistics> map = new HashMap<String, Statistics>();
        map.put(ScoreStatistics.Columns.ASSAULT, new Statistics(R.string.info_xml_assault, format(ks.getAssaultScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.ENGINEER, new Statistics(R.string.info_xml_engineer, format(ks.getEngineerScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.SUPPORT, new Statistics(R.string.info_xml_support, format(ks.getSupportScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.RECON, new Statistics(R.string.info_xml_recon, format(ks.getReconScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.JET, new Statistics(R.string.info_xml_jet, format(vs.getJetScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.TANK, new Statistics(R.string.info_xml_tank, format(vs.getTankScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.IFV, new Statistics(R.string.info_xml_ifv, format(vs.getIfvScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.ANTI_AIR, new Statistics(R.string.info_xml_anti_air, format(vs.getAntiAirScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.ATTACK_HELI, new Statistics(R.string.info_xml_attack_heli, format(vs.getAttackHeliScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.SCOUT_HELI, new Statistics(R.string.info_xml_scout_heli, format(vs.getScoutHeliScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.COMBAT, new Statistics(R.string.info_xml_total_in_combat, format(pso.getCombatScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.AWARD, new Statistics(R.string.info_xml_award, format(pso.getAwardScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.UNLOCKS, new Statistics(R.string.info_xml_unlocks, format(pso.getUnlockScore()), R.style.Wrap));
        map.put(ScoreStatistics.Columns.TOTAL_SCORE, new Statistics(R.string.info_xml_total_score, format(pso.getTotalScore()), R.style.InfoSubHeading));
        return map;
    }

    public static ContentValues scoreStatisticsForDB(Map<String, Statistics> map, long personaId) {
        Set<String> keys = map.keySet();
        ContentValues values = new ContentValues();
        values.put(ScoreStatistics.Columns.PERSONA_ID, personaId);
        for(String key : keys){
            values.put(key, map.get(key).getValue());
        }
        return values;
    }

    private static String valueFromCursor(Cursor cursor, String name) {
        return format(cursor.getLong(cursor.getColumnIndexOrThrow(name)));
    }
}
