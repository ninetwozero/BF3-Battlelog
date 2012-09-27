package com.ninetwozero.battlelog.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.Statistics;
import com.ninetwozero.battlelog.jsonmodel.PersonaInfo;
import com.ninetwozero.battlelog.jsonmodel.PersonaStatsOverview;
import com.ninetwozero.battlelog.provider.table.PersonaStatistics;
import com.ninetwozero.battlelog.provider.table.PersonaStatistics.Columns;

import java.util.ArrayList;
import java.util.List;

import static com.ninetwozero.battlelog.misc.PublicUtils.timeToLiteral;

public class PersonaStatisticsDAO {

	public static List<Statistics> personaStaticsFromCursor(Cursor cursor) {
		List<Statistics> list = new ArrayList<Statistics>();
		list.add(new Statistics(R.string.info_xml_kills, valueFromCursor(
				cursor, Columns.KILLS), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_assists, valueFromCursor(
				cursor, Columns.KILL_ASSISTS), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_vehicles_destroyed,
				valueFromCursor(cursor, Columns.VEHICLE_DESTROYED),
				R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_vehicles_assisted_with,
				valueFromCursor(cursor, Columns.VEHICLE_ASSISTS), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_heals, valueFromCursor(
				cursor, Columns.HEALS), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_revives, valueFromCursor(
				cursor, Columns.REVIVES), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_repairs, valueFromCursor(
				cursor, Columns.REPAIRS), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_resupplies, valueFromCursor(
				cursor, Columns.RESUPPLIES), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_deaths, valueFromCursor(
				cursor, Columns.DEATHS), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_kd_ratio, valueFromCursor(
				cursor, Columns.KD_RATIO), R.style.InfoSubHeading));
		list.add(new Statistics(R.string.info_xml_wins, valueFromCursor(cursor,
				Columns.WINS), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_losses, valueFromCursor(
				cursor, Columns.LOSSES), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_wl_ratio, valueFromCursor(
				cursor, Columns.WL_RATIO), R.style.InfoSubHeading));
		list.add(new Statistics(R.string.info_xml_accuracy, valueFromCursor(
				cursor, Columns.ACCURACY), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_longest_headshot,
				valueFromCursor(cursor, Columns.LONGEST_HEADSHOT), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_longest_killstreak,
				valueFromCursor(cursor, Columns.LONGEST_KILLSTREAK),
				R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_skill_rating,
				valueFromCursor(cursor, Columns.SKILLRATING), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_time_played, valueFromCursor(
				cursor, Columns.TIME_PLAYED), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_score_minute,
				valueFromCursor(cursor, Columns.SCORE_PER_MINUTE),
				R.style.InfoSubHeading));
		return list;
	}

	public static List<Statistics> personaStatisticsFromJSON(PersonaInfo pi) {
		PersonaStatsOverview pso = pi.getStatsOverview();
		List<Statistics> list = new ArrayList<Statistics>();
		list.add(new Statistics(R.string.info_xml_kills, String.valueOf(pso
				.getKills()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_assists, String.valueOf(pso
				.getKillAssists()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_vehicles_destroyed, String
				.valueOf(pso.getVehiclesDestroyed()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_vehicles_assisted_with,
				String.valueOf(pso.getVehiclesDestroyedAssists()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_heals, String.valueOf(pso
				.getHeals()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_revives, String.valueOf(pso
				.getRevives()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_repairs, String.valueOf(pso
				.getRepairs()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_resupplies, String
				.valueOf(pso.getResupplies()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_deaths, String.valueOf(pso
				.getDeaths()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_kd_ratio, String.valueOf(pso
				.getKdRatio()) + "%", R.style.InfoSubHeading));
		list.add(new Statistics(R.string.info_xml_wins, String.valueOf(pso
				.getGameWon()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_losses, String.valueOf(pso
				.getGameLost()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_wl_ratio, String.valueOf(pso
				.getWlRatio()) + "%", R.style.InfoSubHeading));
		list.add(new Statistics(R.string.info_xml_accuracy, String.valueOf(pso
				.getAccuracy()) + "%", R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_longest_headshot, String
				.valueOf(pso.getLongestHeadshot()) + "m", R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_longest_killstreak, String
				.valueOf(pso.getLongestKillStreak()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_skill_rating, String
				.valueOf(pso.getSkill()), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_time_played, String
				.valueOf(timeToLiteral(pso.getTimePlayed())), R.style.Wrap));
		list.add(new Statistics(R.string.info_xml_score_minute, String
				.valueOf(pso.getScoreMin()), R.style.InfoSubHeading));
		return list;
	}

	public static ContentValues personaStatisticsForDB(PersonaInfo pi,
			long personaId) {
		PersonaStatsOverview pso = pi.getStatsOverview();
		ContentValues values = new ContentValues();
		values.put(PersonaStatistics.Columns.PERSONA_ID, personaId);
		values.put(PersonaStatistics.Columns.KILLS,
				String.valueOf(pso.getKills()));
		values.put(PersonaStatistics.Columns.KILL_ASSISTS,
				String.valueOf(pso.getKillAssists()));
		values.put(PersonaStatistics.Columns.VEHICLE_DESTROYED,
				String.valueOf(pso.getVehiclesDestroyed()));
		values.put(PersonaStatistics.Columns.VEHICLE_ASSISTS,
				String.valueOf(pso.getVehiclesDestroyedAssists()));
		values.put(PersonaStatistics.Columns.HEALS,
				String.valueOf(pso.getHeals()));
		values.put(PersonaStatistics.Columns.REVIVES,
				String.valueOf(pso.getRevives()));
		values.put(PersonaStatistics.Columns.REPAIRS,
				String.valueOf(pso.getRepairs()));
		values.put(PersonaStatistics.Columns.RESUPPLIES,
				String.valueOf(pso.getResupplies()));
		values.put(PersonaStatistics.Columns.DEATHS,
				String.valueOf(pso.getDeaths()));
		values.put(PersonaStatistics.Columns.KD_RATIO,
				String.valueOf(pso.getKdRatio()) + "%");
		values.put(PersonaStatistics.Columns.WINS,
				String.valueOf(pso.getGameWon()));
		values.put(PersonaStatistics.Columns.LOSSES,
				String.valueOf(pso.getGameLost()));
		values.put(PersonaStatistics.Columns.WL_RATIO,
				String.valueOf(pso.getWlRatio()) + "%");
		values.put(PersonaStatistics.Columns.ACCURACY,
				String.valueOf(pso.getAccuracy()) + "%");
		values.put(PersonaStatistics.Columns.LONGEST_HEADSHOT,
				String.valueOf(pso.getLongestHeadshot()) + "m");
		values.put(PersonaStatistics.Columns.LONGEST_KILLSTREAK,
				String.valueOf(pso.getLongestKillStreak()));
		values.put(PersonaStatistics.Columns.SKILLRATING,
				String.valueOf(pso.getSkill()));
		values.put(PersonaStatistics.Columns.TIME_PLAYED,
				String.valueOf(timeToLiteral(pso.getTimePlayed())));
		values.put(PersonaStatistics.Columns.SCORE_PER_MINUTE,
				String.valueOf(pso.getScoreMin()));
		return values;
	}

	private static String valueFromCursor(Cursor cursor, String name) {
		return cursor.getString(cursor.getColumnIndexOrThrow(name));
	}
}
