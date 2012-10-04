package com.ninetwozero.battlelog.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.Statistics;
import com.ninetwozero.battlelog.jsonmodel.KitScores;
import com.ninetwozero.battlelog.jsonmodel.PersonaInfo;
import com.ninetwozero.battlelog.jsonmodel.PersonaStatsOverview;
import com.ninetwozero.battlelog.jsonmodel.VehicleScores;

import java.util.ArrayList;
import java.util.List;

import static com.ninetwozero.battlelog.misc.NumberFormatter.format;
import static com.ninetwozero.battlelog.provider.table.ScoreStatistics.Columns;

public class ScoreStatisticsDAO {

    public static List<Statistics> scoreStatisticsFromCursor(Cursor cursor) {
        List<Statistics> list = new ArrayList<Statistics>();
        list.add(new Statistics(R.string.info_xml_assault, valueFromCursor(cursor, Columns.ASSAULT), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_engineer, valueFromCursor(cursor, Columns.ENGINEER), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_support, valueFromCursor(cursor, Columns.SUPPORT), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_recon, valueFromCursor(cursor, Columns.RECON), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_jet, valueFromCursor(cursor, Columns.JET), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_tank, valueFromCursor(cursor, Columns.TANK), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_ifv, valueFromCursor(cursor, Columns.IFV), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_anti_air, valueFromCursor(cursor, Columns.ANTI_AIR), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_attack_heli, valueFromCursor(cursor, Columns.ATTACK_HELI), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_scout_heli, valueFromCursor(cursor, Columns.SCOUT_HELI), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_total_in_combat, valueFromCursor(cursor, Columns.COMBAT), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_award, valueFromCursor(cursor, Columns.AWARD), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_unlocks, valueFromCursor(cursor, Columns.UNLOCKS), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_total_score, valueFromCursor(cursor, Columns.TOTAL_SCORE), R.style.InfoSubHeading));
        return list;
    }

    public static List<Statistics> scoreStatisticsFromJSON(PersonaInfo pi) {
        PersonaStatsOverview pso = pi.getStatsOverview();
        KitScores ks = pi.getStatsOverview().getKitScores();
        VehicleScores vs = pi.getStatsOverview().getVehicleScores();
        List<Statistics> list = new ArrayList<Statistics>();
        list.add(new Statistics(R.string.info_xml_assault, format(ks.getAssaultScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_engineer, format(ks.getEngineerScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_support, format(ks.getSupportScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_recon, format(ks.getReconScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_jet, format(vs.getJetScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_tank, format(vs.getTankScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_ifv, format(vs.getIfvScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_anti_air, format(vs.getAntiAirScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_attack_heli, format(vs.getAttackHeliScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_scout_heli, format(vs.getScoutHeliScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_total_in_combat, format(pso.getCombatScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_award, format(pso.getAwardScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_unlocks, format(pso.getUnlockScore()), R.style.Wrap));
        list.add(new Statistics(R.string.info_xml_total_score, format(pso.getTotalScore()), R.style.InfoSubHeading));
        return list;
    }

    public static ContentValues scoreStatisticsForDB(PersonaInfo pi, long personaId) {
        PersonaStatsOverview pso = pi.getStatsOverview();
        KitScores ks = pi.getStatsOverview().getKitScores();
        VehicleScores vs = pi.getStatsOverview().getVehicleScores();
        ContentValues values = new ContentValues();
        values.put(Columns.PERSONA_ID, personaId);
        values.put(Columns.ASSAULT, ks.getAssaultScore());
        values.put(Columns.ENGINEER, ks.getEngineerScore());
        values.put(Columns.SUPPORT, ks.getSupportScore());
        values.put(Columns.RECON, ks.getReconScore());
        values.put(Columns.JET, vs.getJetScore());
        values.put(Columns.TANK, vs.getTankScore());
        values.put(Columns.IFV, vs.getIfvScore());
        values.put(Columns.ANTI_AIR, vs.getAntiAirScore());
        values.put(Columns.ATTACK_HELI, vs.getAttackHeliScore());
        values.put(Columns.SCOUT_HELI, vs.getScoutHeliScore());
        values.put(Columns.COMBAT, pso.getCombatScore());
        values.put(Columns.AWARD, pso.getAwardScore());
        values.put(Columns.UNLOCKS, pso.getUnlockScore());
        values.put(Columns.TOTAL_SCORE, pso.getTotalScore());
        return values;
    }

    private static String valueFromCursor(Cursor cursor, String name) {
        return format(cursor.getLong(cursor.getColumnIndexOrThrow(name)));
    }
}
